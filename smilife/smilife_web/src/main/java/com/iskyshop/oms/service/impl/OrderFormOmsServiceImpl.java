package com.iskyshop.oms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaitSynOrder;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaitSynOrderService;
import com.iskyshop.oms.service.AbstractOmsManagerService;

/**
 * 具体策略类-订单
 * @author dengyuqi
 *
 */
@Component
public class OrderFormOmsServiceImpl extends AbstractOmsManagerService<OrderForm> {

	private static Logger logger = Logger.getLogger(OrderFormOmsServiceImpl.class);
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IWaitSynOrderService waitSynOrderService;
	
	@Override
	public String getInsertData(OrderForm mainOrderForm) {
		Map jsonData = new HashMap<>();
		jsonData.put("moMainId", mainOrderForm.getMainOrderId()); //主订单号
		
		User user = userService.getObjById(CommUtil.null2Long(mainOrderForm.getUser_id()));
		jsonData.put("moCustId", user.getCustId());
		
		String jpql = "SELECT o FROM OrderForm o WHERE o.mainOrderId = :mainOrderId";
		Map params = new HashMap<>();
		params.put("mainOrderId", mainOrderForm.getMainOrderId());
		List<OrderForm> orderForms = orderFormService.query(jpql, params, -1, -1);
		double goodssPrice = 0.00; //所有订单的商品总价格
		double discountPrice =0.00; //所有订单的优惠总价格
		double totalPrice = 0.00; //所有订单优惠后的总价格
		int goodsTotalNo = 0; //所有订单的商品总数量
		for(OrderForm o : orderForms){
			goodssPrice += CommUtil.null2Double(o.getGoods_amount());
			
			String couponInfo = o.getCoupon_info();//订单使用的优惠券信息
			if(StringUtils.hasText(couponInfo)){
				Map map = Json.fromJson(Map.class, couponInfo);
				discountPrice += CommUtil.null2Double(map.get("coupon_amount"));//优惠券金额
			}
			discountPrice += CommUtil.null2Double(o.getEnough_reduce_amount());//满减金额
			
			totalPrice += CommUtil.null2Double(o.getTotalPrice());
			
			String goodsInfo = o.getGoods_info();
			List<Map> list = CommUtil.json2List(goodsInfo);
			for(Map map :list){
				goodsTotalNo += CommUtil.null2Int(map.get("goods_count"));
			}
		}
		jsonData.put("moGoodsAmount", goodssPrice);
		jsonData.put("moDiscountAmount", discountPrice);
		jsonData.put("moTotalPrice", totalPrice);
		jsonData.put("moPayableAmount", totalPrice); //所有订单应付总金额
		jsonData.put("moActualAmount", 0.00); //所有订单实付总金额
		jsonData.put("moGoodsTotalNo", goodsTotalNo);
		jsonData.put("moPayTradeNo", "");//支付流水号 
		jsonData.put("moPaySource", "");//订单支付渠道 
		jsonData.put("moPayTime", "");//订单支付时间
		jsonData.put("opreatType", "save");

		setReceiveInfo(mainOrderForm, jsonData);
		
		List<Map> listOrder = new ArrayList<Map>();
		for(OrderForm o : orderForms){
			Map order = new HashMap<>();
			order.put("mainOrderMainId", o.getMainOrderId()); //主订单号
			order.put("mainOrderSource", "S"); //主订单来源
			order.put("orderId", o.getOrder_id()); //订单号
			order.put("orderCustId", user.getCustId());
			order.put("orderBizStatus", o.getOrder_status()); //订单状态
			order.put("orderTotalPrice", o.getTotalPrice()); //订单优惠后总价
			order.put("orderPayableAmount", o.getTotalPrice()); //订单应付金额
			order.put("orderActualAmount", 0.00); //订单实付金额
			
			double discountAmount = 0.00; //订单优惠金额
			String couponInfo = o.getCoupon_info();
			if(StringUtils.hasText(couponInfo)){
				Map map = Json.fromJson(Map.class, couponInfo);
				discountPrice += CommUtil.null2Double(map.get("coupon_amount"));
			}
			discountPrice += CommUtil.null2Double(o.getEnough_reduce_amount());
			order.put("orderDiscountAmount", discountAmount);
			
			order.put("orderGoodsAmount", o.getGoods_amount()); //订单商品总金额
			order.put("orderStroeId", o.getStore_id() == null ? "self" : o.getStore_id());
			order.put("orderStoreName", o.getStore_name());
			order.put("createTime", o.getAddTime());
			order.put("updateTime", new Date());
//			order.put("dataStatus", );
			order.put("orderDetail", "");
			order.put("orderMark", o.getMsg());
			
			setExpressInfo(o, order);
			
			int orderGoodsTotalNo = 0;
			List<Map> listProductInfo = new ArrayList<>();
			if(o.getOrder_cat() == 0){
				List<Map> goodss = CommUtil.json2List(o.getGoods_info());
				for(Map goods :goodss){
					Goods g = goodsService.getObjById(CommUtil.null2Long(goods.get("goods_id")));
					Map map = new HashMap<>();
					map.put("createTime", g.getAddTime());
					map.put("orderId", o.getOrder_id());
					map.put("productCount", goods.get("goods_count"));
					map.put("productDetail", g.getSeo_description());
					map.put("productId", goods.get("goods_id"));
					map.put("productName", goods.get("goods_name"));
					map.put("productPicUrl", goods.get("goods_mainphoto_path"));
					map.put("productPrice", goods.get("goods_price"));
					map.put("productWapLink", goods.get("goods_domainPath"));
					map.put("productWebLink", goods.get("goods_domainPath"));
//					map.put("updateTime", );
					listProductInfo.add(map);
					orderGoodsTotalNo += CommUtil.null2Int(goods.get("goods_count"));
				}
				order.put("listProductInfo", listProductInfo);
				order.put("orderGoodsTotalNo", orderGoodsTotalNo);
				String domainPath = (String)goodss.get(0).get("goods_domainPath");
				domainPath = domainPath.replace("//", "~");
				domainPath = domainPath.substring(0, domainPath.indexOf("/")).replace("~", "//");
				order.put("orderCallbackWebUrl", domainPath+"/buyer/order_view.htm?id="+o.getId()+"&type=all");
				order.put("orderCallbackWapUrl", domainPath+"/buyer/order_view.htm?id="+o.getId()+"&type=all");
			}else if(o.getOrder_cat() == 2){
				Map group = (Map) Json.fromJson(o.getGroup_info());
					Map map = new HashMap<>();
					map.put("createTime", " ");
					map.put("orderId", o.getOrder_id());
					map.put("productCount", group.get("goods_count"));
					map.put("productDetail", " ");
					map.put("productId", group.get("goods_id"));
					map.put("productName", group.get("goods_name"));
					map.put("productPicUrl", group.get("goods_mainphoto_path"));
					map.put("productPrice", group.get("goods_price"));
					map.put("productWapLink", " ");
					map.put("productWebLink", " ");
					listProductInfo.add(map);
					orderGoodsTotalNo += CommUtil.null2Int(group.get("goods_count"));
				order.put("listProductInfo", listProductInfo);
				order.put("orderGoodsTotalNo", orderGoodsTotalNo);
				
				order.put("orderCallbackWebUrl"," ");
				order.put("orderCallbackWapUrl"," ");
			}
			listOrder.add(order);
		}
		jsonData.put("listOrder", listOrder);
		
		return Json.toJson(jsonData);
	}

	@Override
	public String getUpdateData(OrderForm orderForm) {
		Map jsonData = new HashMap<>();
		jsonData.put("moMainId", orderForm.getMainOrderId()); //主订单号
		User user = userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));
		jsonData.put("moCustId", user.getCustId());
		jsonData.put("moGoodsAmount", "");
		jsonData.put("moDiscountAmount", "");
		jsonData.put("moTotalPrice", "");
		jsonData.put("moPayableAmount", ""); //所有订单应付总金额
		jsonData.put("moActualAmount", ""); //所有订单实付总金额
		jsonData.put("moGoodsTotalNo", "");
		jsonData.put("opreatType", "update");
		jsonData.put("moPayTradeNo", orderForm.getOut_order_id());//支付流水号 
		if("payafter".equals(orderForm.getPayType())){//货到付款
			jsonData.put("moPaySource", orderForm.getPayType());//订单支付渠道 
			jsonData.put("moPayTime", orderForm.getConfirmTime());//订单支付时间=收货时间
		}else{//在线支付
			Payment payment = orderForm.getPayment();
			if(null != payment){
				jsonData.put("moPaySource", payment.getMark());//订单支付渠道 
			}else{
				jsonData.put("moPaySource", "");//订单支付渠道 
			}
			jsonData.put("moPayTime", orderForm.getPayTime());//订单支付时间
		}
		
		setReceiveInfo(orderForm, jsonData);
		
		List<Map> listOrder = new ArrayList<Map>();
		Map order = new HashMap<>();
		order.put("mainOrderMainId", orderForm.getMainOrderId()); //主订单号
		order.put("mainOrderSource", "S"); //主订单来源
		order.put("orderId", orderForm.getOrder_id()); //订单号
		order.put("orderCustId", user.getCustId());
		order.put("orderBizStatus", orderForm.getOrder_status()); //订单状态
		order.put("orderTotalPrice", orderForm.getTotalPrice()); //订单优惠后总价
		order.put("orderPayableAmount", orderForm.getTotalPrice()); //订单应付金额
		order.put("orderActualAmount", ""); //订单实付金额
		
		double discountAmount = 0.00; //订单优惠金额
		String couponInfo = orderForm.getCoupon_info();
		if(StringUtils.hasText(couponInfo)){
			Map map = Json.fromJson(Map.class, couponInfo);
			discountAmount += CommUtil.null2Double(map.get("coupon_amount"));
		}
		discountAmount += CommUtil.null2Double(orderForm.getEnough_reduce_amount());
		order.put("orderDiscountAmount", discountAmount);
		
		order.put("orderGoodsAmount", orderForm.getGoods_amount()); //订单商品总金额
		order.put("orderStroeId", orderForm.getStore_id() == null ? "self" : orderForm.getStore_id());
		order.put("orderStoreName", orderForm.getStore_name());
		order.put("createTime", orderForm.getAddTime());
		order.put("updateTime", new Date());
//		order.put("dataStatus", );
		order.put("orderDetail", "");
		order.put("orderMark", orderForm.getMsg());
		
		setExpressInfo(orderForm, order);
		
		int orderGoodsTotalNo = 0;
		List<Map> listProductInfo = new ArrayList<>();
		if(orderForm.getOrder_cat() == 0){
			List<Map> goodss = CommUtil.json2List(orderForm.getGoods_info());
			for(Map goods :goodss){
				Goods g = goodsService.getObjById(CommUtil.null2Long(goods.get("goods_id")));
				Map map = new HashMap<>();
				map.put("createTime", g.getAddTime());
				map.put("orderId", orderForm.getOrder_id());
				map.put("productCount", goods.get("goods_count"));
				map.put("productDetail", g.getSeo_description());
				map.put("productId", goods.get("goods_id"));
				map.put("productName", goods.get("goods_name"));
				map.put("productPicUrl", goods.get("goods_mainphoto_path"));
				map.put("productPrice", goods.get("goods_price"));
				map.put("productWapLink", goods.get("goods_domainPath"));
				map.put("productWebLink", goods.get("goods_domainPath"));
				listProductInfo.add(map);
				
				orderGoodsTotalNo += CommUtil.null2Int(goods.get("goods_count"));
			}
			order.put("listProductInfo", listProductInfo);
			order.put("orderGoodsTotalNo", orderGoodsTotalNo);
			
			String domainPath = (String)goodss.get(0).get("goods_domainPath");
			domainPath = domainPath.replace("//", "~");
			domainPath = domainPath.substring(0, domainPath.indexOf("/")).replace("~", "//");
			order.put("orderCallbackWebUrl", domainPath+"/buyer/order_view.htm?id="+orderForm.getId()+"&type=all");
			order.put("orderCallbackWapUrl", domainPath+"/buyer/order_view.htm?id="+orderForm.getId()+"&type=all");
		}else if(orderForm.getOrder_cat() == 2){
			Map group = (Map) Json.fromJson(orderForm.getGroup_info());
				Map map = new HashMap<>();
				map.put("createTime", " ");
				map.put("orderId", orderForm.getOrder_id());
				map.put("productCount", group.get("goods_count"));
				map.put("productDetail", " ");
				map.put("productId", group.get("goods_id"));
				map.put("productName", group.get("goods_name"));
				map.put("productPicUrl", group.get("goods_mainphoto_path"));
				map.put("productPrice", group.get("goods_price"));
				
				
				listProductInfo.add(map);
				
				orderGoodsTotalNo += CommUtil.null2Int(group.get("goods_count"));
			order.put("listProductInfo", listProductInfo);
			order.put("orderGoodsTotalNo", orderGoodsTotalNo);
			
			order.put("orderCallbackWebUrl"," ");
			order.put("orderCallbackWapUrl"," ");
		}
		listOrder.add(order);
		jsonData.put("listOrder", listOrder);
		return Json.toJson(jsonData);
	}

	private void setExpressInfo(OrderForm orderForm, Map jsonData) {
		Map express = new HashMap<>();
		String expressInfo = orderForm.getExpress_info();
		if(StringUtils.hasText(expressInfo)){
			Map map = Json.fromJson(Map.class, expressInfo);
			express.put("orderId", orderForm.getOrder_id());
			express.put("expressName", map.get("express_company_name"));
			express.put("expressCode", map.get("express_company_mark"));//快递公司编码
			express.put("expressNumber", orderForm.getShipCode());//物流单号
			express.put("createTime", orderForm.getShipTime());
//			express.put("dataStatus", map.get(""));
		}
		jsonData.put("express", express);
	}

	private void setReceiveInfo(OrderForm orderForm, Map jsonData) {
		Map receiveAddress = new HashMap<>();
		Area area = orderForm.getReceiverArea();//收货地址信息
		if(null != area){
			receiveAddress.put("orderId", orderForm.getMainOrderId());//主订单号
			receiveAddress.put("saNation", "china");//国家
			receiveAddress.put("saProvince", area.getParent().getParent().getAreaName());//省份
			receiveAddress.put("saCity", area.getParent().getAreaName());//城市
			receiveAddress.put("saCounty", area.getAreaName());//区域
			receiveAddress.put("saDetailAddr", orderForm.getReceiver_area_info());//详细地址
			receiveAddress.put("saZipCode", orderForm.getReceiver_zip());//邮编
			receiveAddress.put("saConsigneeName", orderForm.getReceiver_Name());//收货人
			receiveAddress.put("saConsigneeMobile", orderForm.getReceiver_mobile());
		}
		jsonData.put("receiveAddress", receiveAddress);
	}

	@Override
	public boolean validateInsertOrder(OrderForm orderForm) {
		if(null == orderForm || orderForm.getOrder_main() != 1){
			return false;
		}
		return true;
	}

	@Override
	public boolean validateUpdateOrder(OrderForm orderForm) {
		if(null == orderForm){
			return false;
		}
		return true;
	}

	@Override
	public void afterInsertOrder(OrderForm orderForm) {
		WaitSynOrder wsorder = new WaitSynOrder();
		wsorder.setAddTime(new Date());
		wsorder.setOrder_data(data);
		wsorder.setOrder_id(orderForm.getOrder_id());
		wsorder.setOms_interface(0);
		waitSynOrderService.saveOrUpdate(wsorder);
	}

	@Override
	public void afterUpdateOrder(OrderForm orderForm) {
		//保存未同步的订单数据
		WaitSynOrder wsorder = new WaitSynOrder();
		wsorder.setUpdateTime(new Date());
		wsorder.setOrder_data(data);
		wsorder.setOrder_id(orderForm.getOrder_id());
		wsorder.setOms_interface(1);
		waitSynOrderService.saveOrUpdate(wsorder);
	}

}
