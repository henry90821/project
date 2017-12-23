package com.smi.pay.service.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 请求众安支付接口使用的值对象<br/>
 * Created by Andriy on 16/7/26.
 */
@JsonSerialize
public class PayRequest {

	/**
	 * 订单名称
	 */
	@JSONField
	private String orderName;

	/**
	 * 商户订单号（购物车下单编号）
	 */
	@JSONField
	private String orderNo;

	/**
	 * 支付流水号
	 */
	@JSONField
	private String payNo;

	/**
	 * 交易时间
	 */
	@JSONField
	private String tradeDate;

	/**
	 * 交易金额
	 */
	@JSONField
	private BigDecimal tradeAmount;

	/**
	 * 分期期数,不分期为 0期
	 */
	@JSONField
	private Integer periodNum;

	/**
	 * 订单类型
	 */
	@JSONField
	private OrderType orderType;

	/**
	 * 还款方式
	 */
	@JSONField
	private RepaymentType repayMode;

	/**
	 * 商户后台回调地址
	 */
	@JSONField
	private String notifyUrl;

	// /**
	// * 其他信息
	// */
	// @JSONField
	// private String otherInfo;

	public PayRequest() {
	}

	/**
	 * 订单名称
	 */
	public String getOrderName() {
		return orderName;
	}

	/**
	 * 订单名称
	 */
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	/**
	 * 商户订单号（购物车下单编号）
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * 商户订单号（购物车下单编号）
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 支付流水号
	 */
	public String getPayNo() {
		return payNo;
	}

	/**
	 * 支付流水号
	 */
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	/**
	 * 交易时间
	 */
	public String getTradeDate() {
		return tradeDate;
	}

	/**
	 * 交易时间
	 */
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	/**
	 * 交易金额
	 */
	public String getTradeAmount() {
		DecimalFormat format = new DecimalFormat("#.00");
		return format.format(tradeAmount.doubleValue());
	}

	/**
	 * 交易金额
	 */
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	/**
	 * 分期期数,不分期为 0期
	 */
	public Integer getPeriodNum() {
		return periodNum;
	}

	/**
	 * 分期期数,不分期为 0期
	 */
	public void setPeriodNum(Integer periodNum) {
		this.periodNum = periodNum;
	}

	/**
	 * 订单类型
	 */
	public String getOrderType() {
		return orderType.name();
	}

	/**
	 * 订单类型
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	/**
	 * 还款方式
	 */
	public String getRepayMode() {
		return repayMode.name();
	}

	/**
	 * 还款方式
	 */
	public void setRepayMode(RepaymentType repayMode) {
		this.repayMode = repayMode;
	}

	/**
	 * 商户后台回调地址
	 */
	public String getNotifyUrl() {
		return notifyUrl;
	}

	/**
	 * 商户后台回调地址
	 */
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	// /**
	// * 其他信息
	// */
	// public String getOtherInfo() {
	// return otherInfo;
	// }
	//
	// /**
	// * 其他信息
	// */
	// public void setOtherInfo(String otherInfo) {
	// this.otherInfo = otherInfo;
	// }
}
