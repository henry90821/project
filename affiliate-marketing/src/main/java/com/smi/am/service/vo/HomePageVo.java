package com.smi.am.service.vo;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "首页信息值对象")
public class HomePageVo {

	@ApiModelProperty(value="优惠券数量")
	private int couponsNum;
	
	@ApiModelProperty(value="礼包数量")
	private int gitPackageNum;

	@ApiModelProperty(value="最新动态")
	private List<SystemLogVo> sysLogList;
	
	@ApiModelProperty(value="优惠券详情")
	private List<HomeCouponsInfo> couponsList;
	
	@ApiModelProperty(value="礼包详情")
	private List<HomeGitPackageInfo> gitpackageList;

	public int getCouponsNum() {
		return couponsNum;
	}

	public void setCouponsNum(int couponsNum) {
		this.couponsNum = couponsNum;
	}

	public int getGitPackageNum() {
		return gitPackageNum;
	}

	public void setGitPackageNum(int gitPackageNum) {
		this.gitPackageNum = gitPackageNum;
	}

	public List<SystemLogVo> getSysLogList() {
		return sysLogList;
	}

	public void setSysLogList(List<SystemLogVo> sysLogList) {
		this.sysLogList = sysLogList;
	}

	public List<HomeCouponsInfo> getCouponsList() {
		return couponsList;
	}

	public void setCouponsList(List<HomeCouponsInfo> couponsList) {
		this.couponsList = couponsList;
	}

	public List<HomeGitPackageInfo> getGitpackageList() {
		return gitpackageList;
	}

	public void setGitpackageList(List<HomeGitPackageInfo> gitpackageList) {
		this.gitpackageList = gitpackageList;
	}

	
}
