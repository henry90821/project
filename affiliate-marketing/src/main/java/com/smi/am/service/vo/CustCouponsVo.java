package com.smi.am.service.vo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "会员卡包返回优惠券信息vo")
public class CustCouponsVo {

	@ApiModelProperty(value = "优惠券ID")
	private String cCouponsid;

	@ApiModelProperty(value = "优惠券名称")
	private String cCouponsname;

	@ApiModelProperty(value = "投放渠道")
	private String cChannel;

	@ApiModelProperty(value = "活动期限开始时间")
	private Date cLimitstart;

	@ApiModelProperty(value = "活动期限结束时间")
	private Date cLimitend;

	public String getcCouponsid() {
		return cCouponsid;
	}

	public void setcCouponsid(String cCouponsid) {
		this.cCouponsid = cCouponsid;
	}

	public String getcCouponsname() {
		return cCouponsname;
	}

	public void setcCouponsname(String cCouponsname) {
		this.cCouponsname = cCouponsname;
	}

	public String getcChannel() {
		return cChannel;
	}

	public void setcChannel(String cChannel) {
		this.cChannel = cChannel;
	}

	public Date getcLimitstart() {
		return cLimitstart;
	}

	public void setcLimitstart(Date cLimitstart) {
		this.cLimitstart = cLimitstart;
	}

	public Date getcLimitend() {
		return cLimitend;
	}

	public void setcLimitend(Date cLimitend) {
		this.cLimitend = cLimitend;
	}
	
	
}
