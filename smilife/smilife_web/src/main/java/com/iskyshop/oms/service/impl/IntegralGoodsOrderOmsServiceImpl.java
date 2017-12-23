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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaitSynOrder;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaitSynOrderService;
import com.iskyshop.oms.service.AbstractOmsManagerService;

/**
 * 具体策略类-积分订单
 * @author Administrator
 *
 */
@Component
public class IntegralGoodsOrderOmsServiceImpl extends
		AbstractOmsManagerService<IntegralGoodsOrder> {

	private static Logger logger = Logger.getLogger(IntegralGoodsOrderOmsServiceImpl.class);
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IWaitSynOrderService waitSynOrderService;
	
	@Override
	public String getInsertData(IntegralGoodsOrder integralGoodsOrder) {
		Map jsonData = new HashMap<>();
		jsonData.put("moMainId", integralGoodsOrder.getIgo_order_sn()); //主订单号
		
		User user = integralGoodsOrder.getIgo_user();
		jsonData.put("moCustId", user.getCustId());
		
		int goodsTotalNo = 0; //所有订单的商品总数量
		String goodsInfo = integralGoodsOrder.getGoods_info();
		List<Map> list = CommUtil.json2List(goodsInfo);
		for(Map map :list){
			goodsTotalNo += CommUtil.null2Int(map.get("ig_goods_count"));
		}
		jsonData.put("moGoodsAmount", 0.00);
		jsonData.put("moDiscountAmount", 0.00);
		jsonData.put("moTotalPrice", integralGoodsOrder.getIgo_trans_fee());
		jsonData.put("moPayableAmount", integralGoodsOrder.getIgo_trans_fee()); //所有订单应付总金额
		jsonData.put("moActualAmount", 0.00); //所有订单实付总金额
		jsonData.put("moGoodsTotalNo", goodsTotalNo);
		jsonData.put("moPayTradeNo", "");//支付流水号 
		jsonData.put("moPaySource", "");//订单支付渠道 
		jsonData.put("moPayTime", "");//订单支付时间
		jsonData.put("opreatType", "save");

		setReceiveInfo(integralGoodsOrder, jsonData);
		
		List<Map> listOrder = new ArrayList<Map>();
		Map order = new HashMap<>();
		order.put("mainOrderMainId", integralGoodsOrder.getIgo_order_sn()); //主订单号
		order.put("mainOrderSource", "S"); //主订单来源
		order.put("orderId", integralGoodsOrder.getIgo_order_sn()); //订单号
		order.put("orderCustId", user.getCustId());
		order.put("orderBizStatus", integralGoodsOrder.getIgo_status()); //订单状态
		order.put("orderTotalPrice", integralGoodsOrder.getIgo_trans_fee()); //订单优惠后总价
		order.put("orderPayableAmount", integralGoodsOrder.getIgo_trans_fee()); //订单应付金额
		order.put("orderActualAmount", 0.00); //订单实付金额
		order.put("orderDiscountAmount", 0.00);
		order.put("orderGoodsAmount", 0.00); //订单商品总金额
		order.put("orderStroeId", "");
		order.put("orderStoreName", "");
		order.put("createTime", integralGoodsOrder.getAddTime());
		order.put("updateTime", new Date());
//			order.put("dataStatus", );
		order.put("orderDetail", "");
		order.put("orderMark", integralGoodsOrder.getIgo_msg());
		
		setExpressInfo(integralGoodsOrder, order);
		
		int orderGoodsTotalNo = 0;
		List<Map> listProductInfo = new ArrayList<>();
		List<Map> goodss = CommUtil.json2List(integralGoodsOrder.getGoods_info());
		for(Map goods :goodss){
			Goods g = goodsService.getObjById(CommUtil.null2Long(goods.get("id")));
			Map map = new HashMap<>();
			map.put("createTime", g.getAddTime());
			map.put("orderId", integralGoodsOrder.getIgo_order_sn());
			map.put("productCount", goods.get("ig_goods_count"));
			map.put("productDetail", g.getSeo_description());
			map.put("productId", goods.get("id"));
			map.put("productName", goods.get("ig_goods_name"));
			map.put("productPicUrl", goods.get("ig_goods_img"));
			map.put("productPrice", 0.00);
			map.put("productWapLink", "");
			map.put("productWebLink", "");
//				map.put("updateTime", );
			listProductInfo.add(map);
			
			orderGoodsTotalNo += CommUtil.null2Int(goods.get("ig_goods_count"));
		}
		order.put("listProductInfo", listProductInfo);
		order.put("orderGoodsTotalNo", orderGoodsTotalNo);
		
		
		order.put("orderCallbackWebUrl", "");
		order.put("orderCallbackWapUrl", "");
		
		listOrder.add(order);
		jsonData.put("listOrder", listOrder);
		
		return Json.toJson(jsonData);
	}

	@Override
	public String getUpdateData(IntegralGoodsOrder integralGoodsOrder) {
		Map jsonData = new HashMap<>();
		jsonData.put("moMainId", integralGoodsOrder.getIgo_order_sn()); //主订单号
		User user = integralGoodsOrder.getIgo_user();
		jsonData.put("moCustId", user.getCustId());
		jsonData.put("moGoodsAmount", "");
		jsonData.put("moDiscountAmount", "");
		jsonData.put("moTotalPrice", "");
		jsonData.put("moPayableAmount", ""); //所有订单应付总金额
		jsonData.put("moActualAmount", ""); //所有订单实付总金额
		jsonData.put("moGoodsTotalNo", "");
		jsonData.put("opreatType", "update");
		jsonData.put("moPayTradeNo", integralGoodsOrder.getTrade_order_id());//支付流水号 
		jsonData.put("moPaySource", integralGoodsOrder.getIgo_payment());//订单支付渠道 
		jsonData.put("moPayTime", integralGoodsOrder.getIgo_pay_time());//订单支付时间

		setReceiveInfo(integralGoodsOrder, jsonData);
		
		List<Map> listOrder = new ArrayList<Map>();
		Map order = new HashMap<>();
		order.put("mainOrderMainId", integralGoodsOrder.getIgo_order_sn()); //主订单号
		order.put("mainOrderSource", "S"); //主订单来源
		order.put("orderId", integralGoodsOrder.getIgo_order_sn()); //订单号
		order.put("orderCustId", user.getCustId());
		order.put("orderBizStatus", integralGoodsOrder.getIgo_status()); //订单状态
		order.put("orderTotalPrice", integralGoodsOrder.getIgo_trans_fee()); //订单优惠后总价
		order.put("orderPayableAmount", integralGoodsOrder.getIgo_trans_fee()); //订单应付金额
		order.put("orderActualAmount", ""); //订单实付金额
		order.put("orderDiscountAmount", 0.00);
		order.put("orderGoodsAmount", 0.00); //订单商品总金额
		order.put("orderStroeId", "");
		order.put("orderStoreName", "");
		order.put("createTime", integralGoodsOrder.getAddTime());
		order.put("updateTime", new Date());
//			order.put("dataStatus", );
		order.put("orderDetail", "");
		order.put("orderMark", integralGoodsOrder.getIgo_msg());
		
		setExpressInfo(integralGoodsOrder, order);
		
		int orderGoodsTotalNo = 0;
		List<Map> listProductInfo = new ArrayList<>();
		List<Map> goodss = CommUtil.json2List(integralGoodsOrder.getGoods_info());
		for(Map goods :goodss){
			Goods g = goodsService.getObjById(CommUtil.null2Long(goods.get("id")));
			Map map = new HashMap<>();
			map.put("createTime", g.getAddTime());
			map.put("orderId", integralGoodsOrder.getIgo_order_sn());
			map.put("productCount", goods.get("ig_goods_count"));
			map.put("productDetail", g.getSeo_description());
			map.put("productId", goods.get("id"));
			map.put("productName", goods.get("ig_goods_name"));
			map.put("productPicUrl", goods.get("ig_goods_img"));
			map.put("productPrice", 0.00);
			map.put("productWapLink", "");
			map.put("productWebLink", "");
//				map.put("updateTime", );
			listProductInfo.add(map);
			
			orderGoodsTotalNo += CommUtil.null2Int(goods.get("ig_goods_count"));
		}
		order.put("listProductInfo", listProductInfo);
		order.put("orderGoodsTotalNo", orderGoodsTotalNo);
		
		
		order.put("orderCallbackWebUrl", "");
		order.put("orderCallbackWapUrl", "");
		
		listOrder.add(order);
		jsonData.put("listOrder", listOrder);
		
		return Json.toJson(jsonData);
	}

	private void setExpressInfo(IntegralGoodsOrder integralGoodsOrder, Map jsonData) {
		Map express = new HashMap<>();
		String expressInfo = integralGoodsOrder.getIgo_express_info();
		if(StringUtils.hasText(expressInfo)){
			Map map = Json.fromJson(Map.class, expressInfo);
			express.put("orderId", integralGoodsOrder.getIgo_order_sn());
			express.put("expressName", map.get("express_company_name"));
			express.put("expressCode", map.get("express_company_mark"));//快递公司编码
			express.put("expressNumber", integralGoodsOrder.getIgo_ship_code());//物流单号
			express.put("createTime", integralGoodsOrder.getIgo_ship_time());
//			express.put("dataStatus", map.get(""));
		}
		jsonData.put("express", express);
	}

	private void setReceiveInfo(IntegralGoodsOrder integralGoodsOrder, Map jsonData) {
		Map receiveAddress = new HashMap<>();
		receiveAddress.put("orderId", integralGoodsOrder.getIgo_order_sn());//主订单号
		receiveAddress.put("saNation", "china");//国家
		receiveAddress.put("saProvince", "");//省份
		receiveAddress.put("saCity", "");//城市
		receiveAddress.put("saCounty", integralGoodsOrder.getReceiver_area());//区域
		receiveAddress.put("saDetailAddr", integralGoodsOrder.getReceiver_area_info());//详细地址
		receiveAddress.put("saZipCode", integralGoodsOrder.getReceiver_zip());//邮编
		receiveAddress.put("saConsigneeName", integralGoodsOrder.getReceiver_Name());//收货人
		receiveAddress.put("saConsigneeMobile", integralGoodsOrder.getReceiver_mobile());
		jsonData.put("receiveAddress", receiveAddress);
	}

	@Override
	public boolean validateInsertOrder(IntegralGoodsOrder integralGoodsOrder) {
		if(null == integralGoodsOrder){
			return false;
		}
		return true;
	}

	@Override
	public boolean validateUpdateOrder(IntegralGoodsOrder integralGoodsOrder) {
		if(null == integralGoodsOrder){
			return false;
		}
		return true;
	}

	@Override
	public void afterInsertOrder(IntegralGoodsOrder integralGoodsOrder) {
		//保存未同步的订单数据
		WaitSynOrder wsorder = new WaitSynOrder();
		wsorder.setAddTime(new Date());
		wsorder.setOrder_data(data);
		wsorder.setOrder_id(integralGoodsOrder.getIgo_order_sn());
		wsorder.setOms_interface(0);
		waitSynOrderService.saveOrUpdate(wsorder);
	}

	@Override
	public void afterUpdateOrder(IntegralGoodsOrder integralGoodsOrder) {
		//保存未同步的订单数据
		WaitSynOrder wsorder = new WaitSynOrder();
		wsorder.setUpdateTime(new Date());
		wsorder.setOrder_data(data);
		wsorder.setOrder_id(integralGoodsOrder.getIgo_order_sn());
		wsorder.setOms_interface(1);
		waitSynOrderService.saveOrUpdate(wsorder);
	}

}
