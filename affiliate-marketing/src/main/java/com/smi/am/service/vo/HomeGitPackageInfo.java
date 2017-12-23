package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "首页优惠券详细值对象")
public class HomeGitPackageInfo {

	@ApiModelProperty(value="礼包ID")
	private String couponsPackageId;
	
	@ApiModelProperty(value="礼包名称")
	private String couponsPackageName;
	
	@ApiModelProperty(value="实发数量")
	private int realSendNum;

	@ApiModelProperty(value="活动区")
	private String areaName;
	
	@ApiModelProperty(value="活动区IDS")
	private String areaIds;

	
	
	public String getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(String areaIds) {
		this.areaIds = areaIds;
	}

	public String getCouponsPackageId() {
		return couponsPackageId;
	}

	public void setCouponsPackageId(String couponsPackageId) {
		this.couponsPackageId = couponsPackageId;
	}


	public String getCouponsPackageName() {
		return couponsPackageName;
	}

	public void setCouponsPackageName(String couponsPackageName) {
		this.couponsPackageName = couponsPackageName;
	}

	public int getRealSendNum() {
		return realSendNum;
	}

	public void setRealSendNum(int realSendNum) {
		this.realSendNum = realSendNum;
	}


	
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	
}
