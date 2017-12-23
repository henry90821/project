package com.smi.pay.model;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class OrderLog {

	public Integer id;
	@ApiModelProperty("渠道编码(10002：电渠,10003：爱星美,10004：星美汇),必填")
	public String appcode;
	@ApiModelProperty("请求序列(渠道编码+yyyyMMddhhmmss+6位随机数), 必填")
	public String reqno;
	@ApiModelProperty("签名, 必填")
	public String sign;
	@ApiModelProperty("根据不同场景选择不同的下单方式(APP, WX,WEB,WAP), 必填")
	public String channel;
	@ApiModelProperty("支付方式(WX：微信,ALI：支付宝,YE：余额,YP：影票), 必填")
	public String payType;
	@ApiModelProperty("支付密码(MD5加密), 必填")
	public String payPwd;
	@ApiModelProperty("用户唯一标识, 必填")
	public String custId;
	@ApiModelProperty("订单支付金额(必须是正整数，单位为分), 必填")
	public Integer totalFee;
	@ApiModelProperty("原订单号(由各业务系统生产，保证订单号唯一), 必填")
	public String billNo;
	@ApiModelProperty("订单类(1：星美汇购物订单,2：星美汇充值订单,3：爱星美购票订单), 必填")
	public String billType;
	@ApiModelProperty("订单描述或商品名称(例如：xxx电影,xxx商品,xxx充值), 必填")
	public String title;
	@ApiModelProperty("支付渠道处理完请求后,当前页面自动跳转到网站里指定页面的http路径,当channel参数为WEB/WAP时为 必填")
	public String returnUrl;
	@ApiModelProperty("用户相对于微信公众号的唯一id,微信公众号支付时必填")
	public String openId;
	@ApiModelProperty("星美生活支付的商品支付详情必填(用于预存款支付)")
	public String commodity;
	@ApiModelProperty("订单有效期(时间戳)，超过该时间，不允许支付,")
	public Date createTime;

	public String code;

	public String msg;

	public String returnDetail;
    
	@ApiModelProperty("订单有效期(时间戳)，超过该时间，不允许支付,必填")
	public String expDate;

	/**
	 * 星美钱包支付分期数
	 */
	public Integer instNumber;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReturnDetail() {
		return returnDetail;
	}

	public void setReturnDetail(String returnDetail) {
		this.returnDetail = returnDetail;
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
