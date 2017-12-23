/*
 * 文 件 名:  NumberRes.java
 * 版    权:  Tydic Technologies Co., Ltd. Copyright 1993-2012,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  panmin
 * 修改时间:  2015-3-12
 * 跟踪单号:  <需求跟踪单号>
 * 修改单号:  <需求修改单号>
 * 修改内容:  <修改内容>
 */
package com.smilife.bcp.dto.request;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.framework.util.PropertyUtil;

/**
 * 会员支付请求对象
 * 
 * @author liz 2015-8-27
 */
public class PaymentReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4350479083546814237L;

	public static final String TYPE = "PAYMENT";

	public static final String ATT_KEY_NAME = "PAYMENT_REQ";

	@JSONField(name = "MEMBER_ID")
	private String memberId; // 会员唯一标识

	@JSONField(name = "COMMODITY")
	private List<Commodity> commodity; // 商品信息

	@JSONField(name = "ORDER_ID")
	private String orderId; // 订单编码，编码规则：系统标识+YYYYMMDD+8位序列

	@JSONField(name = "PAYMETHOD_INFO")
	private List<PaymentInfo> paymethodInfo;

	@JSONField(name = "SUM_CHARGE")
	private String sumCharge; // 订单总金额，单位：元

	@JSONField(name = "DISCOUNT_CHARGE")
	private String discounCharge; // 折后金额，单位：元

	@JSONField(name = "STAFF_ID")
	private String staffId; // 受理员工

	@JSONField(name = "SYSTEM_ID")
	private String systemId; // 接入系统标识

	// @JSONField(name = "RECEIVER_INFO")
	// private List receiverInfo; // 收货人信息

	public PaymentReq() {
		staffId = PropertyUtil.getProperty("STAFF_ID");
		systemId = PropertyUtil.getProperty("SYSTEM_ID");
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public List<Commodity> getCommodity() {
		return commodity;
	}

	public void setCommodity(List<Commodity> commodity) {
		this.commodity = commodity;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<PaymentInfo> getPaymethodInfo() {
		return paymethodInfo;
	}

	public void setPaymethodInfo(List<PaymentInfo> paymethodInfo) {
		this.paymethodInfo = paymethodInfo;
	}

	public String getSumCharge() {
		return sumCharge;
	}

	public void setSumCharge(String sumCharge) {
		this.sumCharge = sumCharge;
	}

	public String getDiscounCharge() {
		return discounCharge;
	}

	public void setDiscounCharge(String discounCharge) {
		this.discounCharge = discounCharge;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

}
