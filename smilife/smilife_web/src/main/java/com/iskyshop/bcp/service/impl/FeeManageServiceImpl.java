package com.iskyshop.bcp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.smilife.bcp.dto.request.Commodity;
import com.smilife.bcp.dto.request.CommodityCancle;
import com.smilife.bcp.dto.request.OrderCancleReq;
import com.smilife.bcp.dto.request.PaymentInfo;
import com.smilife.bcp.dto.request.PaymentReq;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;

@Service
public class FeeManageServiceImpl implements IFeeManageservice {

	@Autowired
	private FeeManageConnector feeManageConnector;

	@Autowired
	private OrderFormTools orderFormTools;

	@Autowired
	private IOrderFormService orderFormService;

	public ResultDTO payment(User user, OrderForm order, double order_total_price) {
		PaymentReq req = new PaymentReq();
//		req.setOrderId(order.getCrmOrderId());
		req.setOrderId(order.getOrder_id());
		req.setMemberId(user.getCustId());
		List<Commodity> commodities = new ArrayList<Commodity>();
		// 主订单商品信息
		commodities.addAll(getOrderCommodities(order));
		// 子订单商品信息
		if (!StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
				commodities.addAll(getOrderCommodities(child_order));
			}
		}
		req.setCommodity(commodities);
		List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setAmount(String.valueOf(order_total_price));
		paymentInfo.setPayCode("0");// 0现金账本
		paymentInfos.add(paymentInfo);
		req.setPaymethodInfo(paymentInfos);
		req.setSumCharge(String.valueOf(order_total_price));
		req.setDiscounCharge(String.valueOf(order_total_price));

		return feeManageConnector.payment(req);

	}

	/**
	 * 解析订单商品及运费为commodity
	 * 
	 * @param order
	 * @return
	 */
	public List<Commodity> getOrderCommodities(OrderForm order) {
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		Map groupMap = this.orderFormTools.queryGroupInfo(order.getGroup_info());
		List<Commodity> commodities = new ArrayList<Commodity>();
		for (Map map : goodList) {
			Commodity commodity = new Commodity();
			commodity.setCommodityId(map.get("goods_id").toString());
			commodity.setCommodityName(map.get("goods_name").toString());
			commodity.setCommodityNum(map.get("goods_count").toString());
			commodity.setCommodityType("4");// 4普通商品
			commodity.setDiscountCharge(map.get("goods_price").toString());
			commodity.setPrice(map.get("goods_price").toString());
			commodities.add(commodity);
		}
		if (!StringUtils.isNullOrEmpty(groupMap.entrySet())) { //团购商品
			Commodity commodity = new Commodity();
			commodity.setCommodityId(groupMap.get("goods_id").toString());
			commodity.setCommodityName(groupMap.get("goods_name").toString());
			commodity.setCommodityNum(groupMap.get("goods_count").toString());
			commodity.setCommodityType("4");// 4普通商品
			commodity.setDiscountCharge(groupMap.get("goods_price").toString());
			commodity.setPrice(groupMap.get("goods_price").toString());
			commodities.add(commodity);
		}
		
		if (!StringUtils.isNullOrEmpty(order.getShip_price()) && order.getShip_price().compareTo(BigDecimal.ZERO) == 1) {// 运费大于0
			Commodity commodity = new Commodity();
			commodity.setCommodityName("运费");
			commodity.setCommodityNum("1");
			commodity.setCommodityType("4");// 4普通商品
			commodity.setDiscountCharge(order.getShip_price().toString());
			commodity.setPrice(order.getShip_price().toString());
			commodities.add(commodity);
		}
		return commodities;
	}

	public ResultDTO orderCancle(OrderForm order, ReturnGoodsLog rgl, String amount) {
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		OrderCancleReq cancelInfo = new OrderCancleReq();
		cancelInfo.setCancelType("102"); // 原方式
		if (order.getOrder_main() == 1) { // 主订单
//			cancelInfo.setOrderId(order.getCrmOrderId()); // 原支付订单的crm订单号
			cancelInfo.setOrderId(order.getOrder_id());
		}else{ 
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(rgl.getReturn_main_order_id()));
//			cancelInfo.setOrderId(obj.getCrmOrderId()); // 子主订单时，需要找退货表中的订单号
			cancelInfo.setOrderId(obj.getOrder_id());
		}
		/**
		 * 运费大于0；购买的商品种类大于0；子订单不为空；不为主订单时；
		 */
		if (order.getShip_price().compareTo(BigDecimal.ZERO) == 1 || goodList.size() > 1
				|| !StringUtils.isNullOrEmpty(order.getChild_order_detail())
				|| order.getOrder_main() != 1) {
			cancelInfo.setCancelClass("101"); // 部分退
		} else {
			cancelInfo.setCancelClass("102"); // 全部退
		}
		cancelInfo.setCancelCharge(amount);
		cancelInfo.setDesc("商品退款");
		List<CommodityCancle> commoditys = new ArrayList<CommodityCancle>();
		this.buildGoodsCommodity(commoditys,rgl,amount);
		cancelInfo.setCommodity(commoditys);
		return feeManageConnector.orderCancle(cancelInfo);
	}
	//构造商品CommodityCancle
	public void buildGoodsCommodity(List<CommodityCancle> commoditys,ReturnGoodsLog rgl,String amount){
		CommodityCancle commodityCancle = new CommodityCancle();
		commodityCancle.setCommodityId(rgl.getGoods_id().toString());
		commodityCancle.setCommodityName(rgl.getGoods_name());
		commodityCancle.setCommodityNum(rgl.getGoods_count());
		double return_amount = CommUtil.null2Double(amount);
		int count = CommUtil.null2Int(rgl.getGoods_count());
		commodityCancle.setDiscount(CommUtil.formatMoney(CommUtil.div(return_amount , count)));
		commodityCancle.setPrice(rgl.getGoods_price());
		commoditys.add(commodityCancle);
	}
	
	public ResultDTO orderCancle(OrderForm order,String amount) {
		OrderCancleReq cancelInfo = new OrderCancleReq();
		cancelInfo.setCancelType("102"); // 原方式
		if (order.getOrder_main() == 1) { // 主订单
//			cancelInfo.setOrderId(order.getCrmOrderId()); // 原支付订单的crm订单号
			cancelInfo.setOrderId(order.getOrder_id());
		}else{ 
			OrderForm obj = this.orderFormService.getObjByProperty(null, "order_id",order.getMainOrderId());
//			cancelInfo.setOrderId(obj.getCrmOrderId()); // 子主订单时，需要找退货表中的订单号
			cancelInfo.setOrderId(obj.getOrder_id());
		}
		/**
		 * 运费大于0；购买的商品种类大于0；子订单不为空；不为主订单时；
		 */
		/*if (order.getShip_price().compareTo(BigDecimal.ZERO) == 1 || goodList.size() > 1
				|| !StringUtils.isNullOrEmpty(order.getChild_order_detail())
				|| order.getOrder_main() != 1) {
			cancelInfo.setCancelClass("101"); // 部分退
		} else {
			
		}
		cancelInfo.setCancelClass("102"); // 全部退
		cancelInfo.setCancelCharge(amount);
		cancelInfo.setDesc("订单退款");
		List<CommodityCancle> commoditys = new ArrayList<CommodityCancle>();
			List<Map> list = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		for (Map map : goodList) {
			CommodityCancle commodityCancle = new CommodityCancle();
			commodityCancle.setCommodityId(map.get("goods_id").toString());
			commodityCancle.setCommodityName(map.get("goods_name").toString());
			commodityCancle.setCommodityNum(map.get("goods_count").toString());
			double return_amount = CommUtil.null2Double(amount);
			int count = CommUtil.null2Int(map.get("goods_count").toString());
			commodityCancle.setDiscount(CommUtil.formatMoney(CommUtil.div(return_amount , count*list.size())));
			commodityCancle.setPrice(map.get("goods_price").toString());
			commoditys.add(commodityCancle);
		}*/
		//cancelInfo.setCommodity(commoditys);
		
		List<CommodityCancle> commoditys = new ArrayList<CommodityCancle>();
		if (order.getOrder_main() == 1) { // 主订单
			if(StringUtils.isNullOrEmpty(order.getChild_order_detail())){//没有子单，全部退
				cancelInfo.setCancelClass("102"); // 全部退
				cancelInfo.setCancelCharge(amount);
			}else{//有子单，部分退款
				cancelInfo.setCancelClass("101"); // 部分退
				double reality_amount = assembleDatagramData(commoditys, order, amount);
				cancelInfo.setCancelCharge(CommUtil.null2String(reality_amount));
			}
		}else{//子订单，部分退
			cancelInfo.setCancelClass("101"); // 部分退
			double reality_amount = assembleDatagramData(commoditys, order, amount);
			cancelInfo.setCancelCharge(CommUtil.null2String(reality_amount));
		}
		cancelInfo.setDesc("订单退款");
		cancelInfo.setCommodity(commoditys);
		return feeManageConnector.orderCancle(cancelInfo);
	}
	
	/**
	 * 组装报文数据
	 */
	public double assembleDatagramData(List<CommodityCancle> commoditys, OrderForm order, String amount){
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		int index = 0;
		double return_amount = CommUtil.null2Double(amount);//总退款金额
		double reality_amount = 0.00;//实际退款金额
		double amount1 = 0.00;
		double amount2 = 0.00;
		for(Map map : goodList){
			CommodityCancle commodityCancle = new CommodityCancle();
			commodityCancle.setCommodityId(map.get("goods_id").toString());
			commodityCancle.setCommodityName(map.get("goods_name").toString());
			commodityCancle.setCommodityNum(map.get("goods_count").toString());
			commodityCancle.setPrice(map.get("goods_price").toString());
			if(index == 0){
				double d1 = CommUtil.cutDecimals(return_amount / CommUtil.null2Double(map.get("goods_count")),2);
				commodityCancle.setDiscount(CommUtil.null2String(d1));
				amount1 = d1 * CommUtil.null2Double(map.get("goods_count"));
			}else if(index == 1){
				double d2 = CommUtil.cutDecimals((return_amount - amount1)/CommUtil.null2Double(map.get("goods_count")), 2);
				commodityCancle.setDiscount(CommUtil.null2String(d2));
				amount2 = d2 * CommUtil.null2Double(map.get("goods_count"));
			}else{
				commodityCancle.setDiscount("0");
			}
			commoditys.add(commodityCancle);
			index ++ ;
		}
		reality_amount = CommUtil.cutDecimals((amount1 + amount2), 2);
		return reality_amount;
	}
	
	public void assembleData(List<CommodityCancle> commoditys, OrderForm order, String amount){
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		int index = 0;
		double addPrice = 0.00;
		for (Map map : goodList) {
			CommodityCancle commodityCancle = new CommodityCancle();
			commodityCancle.setCommodityId(map.get("goods_id").toString());
			commodityCancle.setCommodityName(map.get("goods_name").toString());
			commodityCancle.setCommodityNum(map.get("goods_count").toString());
			double return_amount = CommUtil.null2Double(amount);
			if(index == goodList.size() - 1){
				commodityCancle.setDiscount(CommUtil.null2String((return_amount - addPrice) / CommUtil.null2Double(map.get("goods_count"))));
			}else{
				commodityCancle.setDiscount(map.get("goods_price").toString());
			}
			commodityCancle.setPrice(map.get("goods_price").toString());
			commoditys.add(commodityCancle);
			index ++;
			addPrice = addPrice + CommUtil.null2Double(map.get("goods_price")) * CommUtil.null2Double(map.get("goods_count"));
		}
	}

	@Override
	public ResultDTO orderCancle(OrderForm order, GroupInfo gi, String amount) {
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		Map group=Json.fromJson(Map.class, order.getGroup_info());
		OrderCancleReq cancelInfo = new OrderCancleReq();
		cancelInfo.setCancelType("102"); // 原方式
//		cancelInfo.setOrderId(order.getCrmOrderId()); // 原支付订单的订单号
		cancelInfo.setOrderId(order.getOrder_id());
		/**
		 * 运费大于0；购买的商品种类大于0；子订单不为空；
		 */
		if (order.getShip_price().compareTo(BigDecimal.ZERO) == 1 || goodList.size() > 1
				|| !StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
			cancelInfo.setCancelClass("101"); // 部分退
		} else {
			cancelInfo.setCancelClass("102"); // 全部退
		}
		cancelInfo.setCancelCharge(amount);
		cancelInfo.setDesc("商品退款");
		List<CommodityCancle> commoditys = new ArrayList<CommodityCancle>();
		Map groupMap = null;//GroupInfo无Commody需要的详细信息，因此需要从订单的group_info中获取
		if(group.get("goods_id").equals(gi.getLifeGoods().getId().toString())){
			groupMap = group;
		}
		//构造商品commoditys
		this.buildGroupInfoCommodity(order, commoditys, gi, amount);
		cancelInfo.setCommodity(commoditys);
		return feeManageConnector.orderCancle(cancelInfo);
	}

	//构造商品commoditys
	public void buildGroupInfoCommodity(OrderForm order,List<CommodityCancle> commoditys,GroupInfo gi,String amount){
		Map group=Json.fromJson(Map.class, order.getGroup_info());
		Map groupMap = null;//GroupInfo无Commody需要的详细信息，因此需要从订单的group_info中获取
		if(group.get("goods_id").equals(gi.getLifeGoods().getId().toString())){
			groupMap = group;
		}
		CommodityCancle commodityCancle = new CommodityCancle();
		commodityCancle.setCommodityId(groupMap.get("goods_id").toString());
		commodityCancle.setCommodityName(groupMap.get("goods_name").toString());
		commodityCancle.setCommodityNum(groupMap.get("goods_count").toString());
		commodityCancle.setDiscount(groupMap.get("goods_price").toString());
		commodityCancle.setPrice(groupMap.get("goods_price").toString());
		commoditys.add(commodityCancle);
	}
	
	@Override
	public ResultDTO payment(User user, GoldRecord obj) {
		ResultDTO dt = new ResultDTO();
		try {
				PaymentReq prq = new PaymentReq();
				
				List<Commodity> list = new ArrayList<Commodity>();
				Commodity cmd = new Commodity();
				cmd.setCommodityName("预存款支付");
				cmd.setCommodityNum("1");
				cmd.setDiscountCharge(String.valueOf(obj.getGold_money()));
				cmd.setPrice(String.valueOf(obj.getGold_money()));
				cmd.setCommodityType("4");
				list.add(cmd);
				
				PaymentInfo pif = new PaymentInfo();
				List<PaymentInfo> list2 = new ArrayList<PaymentInfo>();
				pif.setPayCode("0"); 
				pif.setAmount(String.valueOf(obj.getGold_money()));
				list2.add(pif);
				
				prq.setCommodity(list);
			    prq.setPaymethodInfo(list2);
				prq.setMemberId(user.getCustId());
				prq.setOrderId(obj.getGold_sn());
				prq.setSumCharge(String.valueOf(obj.getGold_money()));
				prq.setDiscounCharge(String.valueOf(obj.getGold_money()));
				prq.setSystemId("102");
				
				dt = feeManageConnector.payment(prq);	
		} catch (Exception e) {
			dt.setResult("1");
			dt.setMsg("更新预存款异常");
			return dt;
		}
		return dt;
	}

}
