package com.smi.am.service.vo;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class CustVo {

	@ApiModelProperty(value = "会员id")
	private String custId;
	@ApiModelProperty(value = "会员名称")
	private String custName;
	@ApiModelProperty(value = "会员卡号")
	private String custNbr;
	@ApiModelProperty(value = "创建渠道")
	private String orgId;
	@ApiModelProperty(value = "会员等级")
	private String custVipLevel;
	@ApiModelProperty(value = "手机号")
	private String contactMobile;
	@ApiModelProperty(value = "会员性别")
	private String sex;
	@ApiModelProperty(value = "会员生日")
	private String birthdate;
	@ApiModelProperty(value = "办卡日期")
	private String crtDate;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustNbr() {
		return custNbr;
	}

	public void setCustNbr(String custNbr) {
		this.custNbr = custNbr;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCustVipLevel() {
		return custVipLevel;
	}

	public void setCustVipLevel(String custVipLevel) {
		this.custVipLevel = custVipLevel;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getCrtDate() {
		return crtDate;
	}

	public void setCrtDate(String crtDate) {
		this.crtDate = crtDate;
	}

}
