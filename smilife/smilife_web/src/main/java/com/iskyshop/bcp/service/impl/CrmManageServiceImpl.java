package com.iskyshop.bcp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.bcp.service.ICrmManageservice;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.eshop.model.common.constants.Constant;
import com.tydic.eshop.model.order.CrmOrderCommodity;
import com.tydic.eshop.model.order.CrmOrderFeeItem;
import com.tydic.eshop.model.order.CrmOrderInst;
import com.tydic.eshop.model.order.CrmOrderPayment;
import com.tydic.eshop.model.order.CrmOrderReceiveInfo;
import com.tydic.eshop.model.order.ReqCrmOrder;
import com.tydic.framework.util.PropertyUtil;

/**
 * CRM接口实现
 * 
 * @author xzr 2015-10-08
 */
@Service
public class CrmManageServiceImpl implements ICrmManageservice {

	@Autowired
	private IUserService		userService;

	@Autowired
	private OrderFormTools		orderFormTools;

	@Autowired
	private IOrderFormService	orderFormService;

	@Autowired
	private UserManageConnector	userManageConnector;

	/**
	 * CRM订单保存
	 * 
	 * @param id
	 * @param saveType
	 *            0：正式单，1：临时单
	 * @return
	 */
//	@SuppressWarnings("rawtypes")
//	public String saveCrmOrder(Long id, String saveType) {
//
//		// 主订单信息
//		OrderForm orderForm = this.orderFormTools.query_order(id.toString());
//		// 买家信息
//		User user = this.userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));
//
//		// crm订单对象
//		ReqCrmOrder reqCrmOrder = new ReqCrmOrder();
//
//		// 组装SALE_ORDER_INST节点<订单基础实例>
//		String payStatus = "0"; // 未支付
//		String businessType = "20"; // 商品类
//		String orderType = Constant.TEMP_ORDER_TYPE; // 临时订单
//		String saleOrderId = CrmOrderInst.CRM_Virtual_VALUE; // 虚拟订单号
//
//		// 临时单和正式单的判断
//		boolean newFlag = Constant.NEW_FLAG_ADD_N.equals(saveType);
//		if (newFlag) {
//			if (null != orderForm.getCrmOrderId() && !"".equals(orderForm.getCrmOrderId())) { // 有临时单
//				saleOrderId = orderForm.getCrmOrderId(); // crm订单号
//			}
//			orderType = Constant.ADD_ORDER_TYPE; // 正式单
//			payStatus = "1"; // 已支付
//		}
//		ArrayList<CrmOrderInst> orderInsts = new ArrayList<CrmOrderInst>();
//		CrmOrderInst orderInst = new CrmOrderInst(); // 订单基础实例
//		orderInst.setSaleOrderId(saleOrderId); // 订单号
//		orderInst.setCustId(user.getCustId()); // 用户ID
//		String extSystem = PropertyUtil.getProperty("EXT_SYSTEM");
//		String selInOrgId = PropertyUtil.getProperty("SEL_IN_ORG_ID");
//		orderInst.setExtSystem(extSystem); // 接入系统标识
//		orderInst.setSelInOrgId(selInOrgId); // 渠道ID
//		orderInst.setOrderType(orderType); // 订单类型
//		orderInst.setPayStatus(payStatus); // 支付状态
//		orderInst.setBusinessType(businessType); // 业务类型
//		orderInst.setExtOrderId(orderForm.getOrder_id()); // 外部订单号
//		orderInsts.add(orderInst);
//		reqCrmOrder.setOrderInst(orderInsts); // 组装节点1:SALE_ORDER_INST<订单基础实例>
//
//		// 订单总金额
//		double totalPrice = 0;
//		totalPrice = CommUtil.add(orderForm.getTotalPrice(), 0);
//
//		// 组装COMMODITY节点<商品信息实例>
//		List<CrmOrderCommodity> commodities = new ArrayList<CrmOrderCommodity>();
//		// 主订单商品信息
//		commodities.addAll(getOrderCommodities(orderForm));
//		// 子订单商品信息
//		if (!StringUtils.isNullOrEmpty(orderForm.getChild_order_detail())) {
//			List<Map> maps = this.orderFormTools.queryGoodsInfo(orderForm.getChild_order_detail());
//			for (Map map : maps) {
//				OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
//				commodities.addAll(getOrderCommodities(child_order));
//
//				totalPrice = CommUtil.add(child_order.getTotalPrice(), totalPrice);
//			}
//		}
//		reqCrmOrder.setCommodity(commodities); // 组装节点2:COMMODITY<商品信息实例>
//
//		// 组装COMM_ATTR 节点 <商品属性实例>
//		// 组装SALE_OFFER_INST 节点 <套餐信息实例>
//		// 组装SALE_PROD_INST 节点 <产品信息实例>
//		// 组装SALE_PROD_INST_ATTR节点 <产品属性实例>
//		// 组装SALE_PROD_INST_SUB 节点 <产品功能实例>
//		// 组装NUMBER_INFO 节点 <号码信息实例>
//		// 组装SALE_RES_INST 节点 <终端信息实例>
//		// 组装SALE_CUST 节点 <客户信息实例>
//
//		// 组装RECEIVE_INFO 节点 <收货信息实例>
//		if (null != orderForm.getReceiver_area_info() && !"".equals(orderForm.getReceiver_area_info().trim())) {
//			List<CrmOrderReceiveInfo> receiveInfos = new ArrayList<CrmOrderReceiveInfo>();
//			CrmOrderReceiveInfo receiveInfo = new CrmOrderReceiveInfo(); // 收货信息实例
//			receiveInfo.setSaleOrderId(saleOrderId); // 订单号
//			String mobile = "";
//			if (null != orderForm.getReceiver_mobile() && !"".equals(orderForm.getReceiver_mobile().trim())) {
//				mobile = orderForm.getReceiver_mobile(); // 手机号
//			}
//			else {
//				mobile = orderForm.getReceiver_telephone(); // 联系号码
//			}
//			receiveInfo.setMobile(mobile); // 联系电话
//			receiveInfo.setReceiveName(orderForm.getReceiver_Name()); // 收货人姓名
//			receiveInfo.setPostCode(orderForm.getReceiver_zip()); // 邮政编码
//			receiveInfo.setAddress(orderForm.getReceiver_area_info()); // 收货详细地址
//			receiveInfos.add(receiveInfo);
//			reqCrmOrder.setReceiveInfo(receiveInfos); // 组装节点3:RECEIVE_INFO<收货信息实例>
//		}
//
//		// 组装FEE_ITEM 节点 <费用信息实例>
//		if (null != orderForm.getTotalPrice() && !"".equals(orderForm.getTotalPrice())) {
//			List<CrmOrderFeeItem> feeItems = new ArrayList<CrmOrderFeeItem>();
//			CrmOrderFeeItem feeItem = new CrmOrderFeeItem();
//			feeItem.setSaleOrderId(saleOrderId); // 订单号
//			feeItem.setAcctItemType("7"); // 卖品费用
//			feeItem.setAmount(CommUtil.formatMoney(totalPrice)); // 订单总金额
//			feeItem.setObjInstId(CrmOrderInst.CRM_PROD_VIRTUAL_VALUE); // 费用对象ID
//			feeItem.setObjInstType(Constant.OBJ_INST_TYPE_PROD); // 费用对象类型2：商品
//			feeItems.add(feeItem);
//			reqCrmOrder.setFeeItem(feeItems); // 组装节点4:FEE_ITEM<费用信息实例>
//		}
//
//		// 组装PAYMENT 节点 <支付信息实例>
//		if (null != orderForm.getPayTime() && !"".equals(orderForm.getPayTime())) {
//			List<CrmOrderPayment> crmPayments = new ArrayList<CrmOrderPayment>();
//			CrmOrderPayment crmPayment = new CrmOrderPayment();
//			if (null != orderForm.getPayment()) {
//				crmPayment.setSaleOrderId(saleOrderId); // 订单号
//				crmPayment.setAmount(CommUtil.formatMoney(totalPrice)); // 支付数值
//				crmPayment.setDeduAmt(CommUtil.formatMoney(totalPrice)); // 抵扣金额
//
//				String payType = orderForm.getPayment().getMark();
//				if ("balance".equals(payType)) { // 预存款支付（星美生活余额支付）
//					crmPayment.setPaymentCode("0"); // 余额支付
//					crmPayment.setPaymentSerial(saleOrderId); // 支付流水号
//				}
//				/*
//				 * else if("payafter".equals(payType)){ //货到付款
//				 * crmPayment.setPaymentCode("14"); //银联支付
//				 * crmPayment.setPaymentSerial(saleOrderId); //支付流水号
//				 * 
//				 * }
//				 */
//				else if (payType.indexOf("alipay") > 0) { // 支付宝支付，包含alipay，alipay_wap，alipay_app
//					crmPayment.setPaymentCode("11"); // 支付宝支付
//					crmPayment.setPaymentSerial(orderForm.getOut_order_id()); // 支付流水号
//				}
//				else {
//					crmPayment.setPaymentCode("14"); // 银联等支付
//					crmPayment.setPaymentSerial(orderForm.getOut_order_id()); // 支付流水号
//				}
//				crmPayments.add(crmPayment);
//			}
//			else { // 货到付款
//				/*
//				 * crmPayment.setSaleOrderId(saleOrderId); //订单号
//				 * crmPayment.setAmount(CommUtil.formatMoney(totalPrice));
//				 * //支付数值
//				 * crmPayment.setDeduAmt(CommUtil.formatMoney(totalPrice));
//				 * //抵扣金额 crmPayment.setPaymentCode("14"); //银联等支付
//				 * crmPayment.setPaymentSerial(orderForm.getOut_order_id());
//				 * //支付流水号 crmPayments.add(crmPayment);
//				 */
//			}
//			reqCrmOrder.setPayment(crmPayments); // 组装节点5:PAYMENT<支付信息实例>
//		}
//
//		String crmOrderId = userManageConnector.saveCrmOrder(reqCrmOrder);
//		return crmOrderId;
//	}

	/**
	 * 解析订单商品及运费为commodity
	 * 
	 * @param order
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<CrmOrderCommodity> getOrderCommodities(OrderForm order) {
		List<Map> goodList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		Map groupMap = this.orderFormTools.queryGroupInfo(order.getGroup_info());
		List<CrmOrderCommodity> commodities = new ArrayList<CrmOrderCommodity>();
		String commId = "";
		for (Map map : goodList) {
			CrmOrderCommodity commodity = new CrmOrderCommodity();
			commodity.setCommInstId(map.get("goods_id").toString()); // 商品实例标识
			commodity.setCommId(map.get("goods_id").toString()); // 商品标识
			commodity.setCommName(map.get("goods_name").toString()); // 商品名称
			commodity.setUnitPrice(map.get("goods_price").toString()); // 商品原价
			commodity.setDiscount(map.get("goods_price").toString()); // 商品折后价
			commodity.setCommCnt(map.get("goods_count").toString()); // 商品数量
			commodity.setNewFlag("1"); // 新增标识
			commodities.add(commodity);
			commId += map.get("goods_id").toString() + ","; // 组装运费的商品标识，多个商品id相加
		}
		if (!StringUtils.isNullOrEmpty(groupMap.entrySet())) { // 团购商品
			CrmOrderCommodity commodity = new CrmOrderCommodity();
			commodity.setCommInstId(groupMap.get("goods_id").toString());
			commodity.setCommId(groupMap.get("goods_id").toString());
			commodity.setCommName(groupMap.get("goods_name").toString());
			commodity.setCommCnt(groupMap.get("goods_count").toString());
			commodity.setUnitPrice(groupMap.get("goods_price").toString());
			commodity.setDiscount(groupMap.get("goods_price").toString());
			commodity.setNewFlag("1"); // 新增标识
			commodities.add(commodity);
			commId += groupMap.get("goods_id").toString() + ","; // 组装运费的商品标识，多个商品id相加
		}
		commId.substring(0, commId.length() - 1);
		if (order.getShip_price().compareTo(BigDecimal.ZERO) == 1) { // 运费大于0
			CrmOrderCommodity commodity = new CrmOrderCommodity();
			commodity.setCommInstId(order.getOrder_id()); // 商品实例标识
			/** modify by liz 解决商品过多时commId字符超过限制 start */
			// 组装运费的商品标识
			/*
			 * if (commId.length() > 1) { commId = commId.substring(0,
			 * commId.length() - 1); } commodity.setCommId(commId); // 商品标识
			 */
			/** modify by liz end */
			commodity.setCommId(CommUtil.null2String(order.getId())); // 商品标识
			commodity.setCommName("运费");
			commodity.setCommCnt("1"); // 商品数量
			commodity.setUnitPrice(order.getShip_price().toString()); // 商品原价
			commodity.setDiscount(order.getShip_price().toString()); // 商品折后价
			commodity.setNewFlag("1"); // 新增标识
			commodities.add(commodity);
		}
		return commodities;
	}

}
