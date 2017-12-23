package com.smi.am.service.vo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "关联优惠券信息值对象")
public class CouponRelationVo {

	@ApiModelProperty(value="优惠券ID")
	private String cCouponsid;
	
	@ApiModelProperty(value="优惠券名称")
	private String cCouponsname;
	
	@ApiModelProperty(value="优惠券类型")
	private String cCouponsTypeName;
	
	@ApiModelProperty(value="优惠信息")
	private String cCouponsmsg;
	
	@ApiModelProperty(value="可用订单金额")
	private Double cAvaliabledorderamt;
	
	@ApiModelProperty(value="使用期限开始日期")
	private Date cLimitstart;

	@ApiModelProperty(value="使用期限结束日期")
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

	public String getcCouponsTypeName() {
		return cCouponsTypeName;
	}

	public void setcCouponsTypeName(String cCouponsTypeName) {
		this.cCouponsTypeName = cCouponsTypeName;
	}

	public String getcCouponsmsg() {
		return cCouponsmsg;
	}

	public void setcCouponsmsg(String cCouponsmsg) {
		this.cCouponsmsg = cCouponsmsg;
	}

	public Double getcAvaliabledorderamt() {
		return cAvaliabledorderamt;
	}

	public void setcAvaliabledorderamt(Double cAvaliabledorderamt) {
		this.cAvaliabledorderamt = cAvaliabledorderamt;
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
