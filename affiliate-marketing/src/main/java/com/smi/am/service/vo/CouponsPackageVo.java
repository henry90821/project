package com.smi.am.service.vo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息响应vo")
public class CouponsPackageVo {

	@ApiModelProperty(value = "优惠券ID")
	private String cCouponsid;

	@ApiModelProperty(value = "优惠券名称")
	private String cCouponsname;

	@ApiModelProperty(value = "优惠券信息")
	private String cCouponsmsg;

	@ApiModelProperty(value = "可用订单金额")
	private Double cAvaliabledorderamt;

	@ApiModelProperty(value = "使用期限开始时间")
	private Date cLimitstart;

	@ApiModelProperty(value = "使用期限结束时间")
	private Date cLimitend;

	@ApiModelProperty(value = "剩余数量")
	private Integer cRemainnum;

	@ApiModelProperty(value = "优惠券类型名称")
	private String ctCouponsName;

	@ApiModelProperty(value = "总数量")
	private Integer cPresendcount;
	
	@ApiModelProperty(value = "是否永久有效1是,2否")
	private Integer cPermanent;

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

	public Integer getcRemainnum() {
		return cRemainnum;
	}

	public void setcRemainnum(Integer cRemainnum) {
		this.cRemainnum = cRemainnum;
	}

	public String getCtCouponsName() {
		return ctCouponsName;
	}

	public void setCtCouponsName(String ctCouponsName) {
		this.ctCouponsName = ctCouponsName;
	}

	public Integer getcPresendcount() {
		return cPresendcount;
	}

	public void setcPresendcount(Integer cPresendcount) {
		this.cPresendcount = cPresendcount;
	}

	public Integer getcPermanent() {
		return cPermanent;
	}

	public void setcPermanent(Integer cPermanent) {
		this.cPermanent = cPermanent;
	}

}
