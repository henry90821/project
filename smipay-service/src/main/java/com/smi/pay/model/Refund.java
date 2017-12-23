package com.smi.pay.model;

import java.util.Date;

public class Refund {
	
	public Integer id;
	
	public String appcode;
	
	public String reqno;
	
	public String sign;
	
	public String custId;
	
	public Integer refundFee;
	
	public String refundNo;
	
	public String billNo;
	
	public String packageValue;
	
	public String memo;
	 
	public Date createTime;
	
	/**
	 * 当前退款单状态，0:退款成功  1:退款失败
	 */
	public String status;
	
	/**
	 * 退款请求提交后接收到第三方回调状态  0:成功 1:失败 
	 */
	public String callBackStatus;
	/**
	 * 退款请求提交后接收到第三方产生的支付流水号
	 */
	public String paySn;
	
	/**
	 * 接收到回调后返回给第三方的内容
	 */
	public String callBackMemo;
	
	/**
	 * 退款请求提交后接收到第三方回调时间
	 */
	public Date callBackTime;
	
	/**
	 * 通知业务系统状态 0:成功  1:失败 
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

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Integer getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(Integer refundFee) {
		this.refundFee = refundFee;
	}

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public String getPackageValue() {
		return packageValue;
	}

	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	 
	
	

}
