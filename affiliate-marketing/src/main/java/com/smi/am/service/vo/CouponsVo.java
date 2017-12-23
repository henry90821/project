package com.smi.am.service.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息值对象")
public class CouponsVo extends CouponsParentVo{

	@ApiModelProperty(value="优惠券ID")
	private String cCouponsid;
	
	@ApiModelProperty(value="实发数量")
	private Integer cRealNum;
	
	@ApiModelProperty(value="已用数量")
	private Integer cUsedNum;
	
	@ApiModelProperty(value="预发数量详情")
	private String pretest;
	
	@ApiModelProperty(value="优惠券详情")
	private String cdetailInfo;
	
	@ApiModelProperty(value="活动区域名称")
	private String cActivityareaName;
	
	@ApiModelProperty(value="优惠券类别名称")
	private String cCouponsTypeName;
	
	
	
	public String getcActivityareaName() {
		return cActivityareaName;
	}

	public void setcActivityareaName(String cActivityareaName) {
		this.cActivityareaName = cActivityareaName;
	}

	public String getcCouponsTypeName() {
		return cCouponsTypeName;
	}

	public void setcCouponsTypeName(String cCouponsTypeName) {
		this.cCouponsTypeName = cCouponsTypeName;
	}

	public String getCdetailInfo() {
		return cdetailInfo;
	}

	public void setCdetailInfo(String cdetailInfo) {
		this.cdetailInfo = cdetailInfo;
	}

	public String getPretest() {
		return pretest;
	}

	public void setPretest(String pretest) {
		this.pretest = pretest;
	}

	public String getcCouponsid() {
		return cCouponsid;
	}

	public void setcCouponsid(String cCouponsid) {
		this.cCouponsid = cCouponsid;
	}

	public Integer getcRealNum() {
		return cRealNum;
	}

	public void setcRealNum(Integer cRealNum) {
		this.cRealNum = cRealNum;
	}

	public Integer getcUsedNum() {
		return cUsedNum;
	}

	public void setcUsedNum(Integer cUsedNum) {
		this.cUsedNum = cUsedNum;
	}

	

}