package com.smi.pay.model;

import java.util.Date;

public class Order {

	public Integer id;

	public String appcode;

	public String reqno;

	public String sign;

	public String channel;

	public String payType;

	public String payPwd;

	public String custId;

	public Integer totalFee;

	public String billNo;

	public String billType;

	public String title;

	public String returnUrl;

	public String openId;

	public String commodity;

	public Date createTime;

	public String expDate;

	/**
	 * 星美钱包支付分期数
	 */
	public Integer instNumber;

	/**
	 * 当前订单状态，0:支付成功 1:支付失败
	 */
	public String status;

	/**
	 * 支付请求提交后接收到第三方回调状态 0:成功 1:失败
	 */
	public String callBackStatus;
	/**
	 * 支付请求提交后接收到第三方产生的支付流水号
	 */
	public String paySn;

	/**
	 * 接收到回调后返回给第三方的内容
	 */
	public String callBackMemo;

	/**
	 * 支付请求提交后接收到第三方回调时间
	 */
	public Date callBackTime;

	/**
	 * 通知业务系统状态 0:成功 1:失败
	 */
	public String noticeStatus;

	/**
	 * 通知业务系统后返回
	 */
	public String noticeReturn;
	/**
	 * 最后通知业务系统时间
	 */
	public Date lastNoticeTime;
	/**
	 * 通知业务系统次数
	 */
	public Integer noticeTimes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppcode() {
		return appcode;
	}

	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}

	public String getReqno() {
		return reqno;
	}

	public void setReqno(String reqno) {
		this.reqno = reqno;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayPwd() {
		return payPwd;
	}

	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCallBackStatus() {
		return callBackStatus;
	}

	public void setCallBackStatus(String callBackStatus) {
		this.callBackStatus = callBackStatus;
	}

	public String getPaySn() {
		return paySn;
	}

	public void setPaySn(String paySn) {
		this.paySn = paySn;
	}

	public String getCallBackMemo() {
		return callBackMemo;
	}

	public void setCallBackMemo(String callBackMemo) {
		this.callBackMemo = callBackMemo;
	}

	public Date getCallBackTime() {
		return callBackTime;
	}

	public void setCallBackTime(Date callBackTime) {
		this.callBackTime = callBackTime;
	}

	public String getNoticeStatus() {
		return noticeStatus;
	}

	public void setNoticeStatus(String noticeStatus) {
		this.noticeStatus = noticeStatus;
	}

	public String getNoticeReturn() {
		return noticeReturn;
	}

	public void setNoticeReturn(String noticeReturn) {
		this.noticeReturn = noticeReturn;
	}

	public Date getLastNoticeTime() {
		return lastNoticeTime;
	}

	public void setLastNoticeTime(Date lastNoticeTime) {
		this.lastNoticeTime = lastNoticeTime;
	}

	public Integer getNoticeTimes() {
		return noticeTimes;
	}

	public void setNoticeTimes(Integer noticeTimes) {
		this.noticeTimes = noticeTimes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	/**
	 * 星美钱包支付分期数
	 */
	public Integer getInstNumber() {
		return instNumber;
	}

	/**
	 * 星美钱包支付分期数
	 */
	public void setInstNumber(Integer instNumber) {
		this.instNumber = instNumber;
	}
}
