package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券一些属性vo")
public class CouponsSomeVo {

	@ApiModelProperty(value = "优惠券ID")
	private String cCouponsid;

	@ApiModelProperty(value = "优惠券类别名称")
	private String cCouponsTypeName;

	@ApiModelProperty(value = "优惠信息")
	private String cCouponsmsg;

	public String getcCouponsid() {
		return cCouponsid;
	}

	public void setcCouponsid(String cCouponsid) {
		this.cCouponsid = cCouponsid;
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

}
