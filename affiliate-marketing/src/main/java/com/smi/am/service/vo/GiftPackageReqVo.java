package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "礼包请求vo")
public class GiftPackageReqVo {

	@ApiModelProperty(value = "礼包id")
	private Integer pgId;

	@ApiModelProperty(value = "礼包名称")
	private String giftPackageName;

	@ApiModelProperty(value = "开始时间")
	private String startTime;

	@ApiModelProperty(value = "结束时间")
	private String endTime;

	@ApiModelProperty(value = "投放渠道")
	private String channel;

	@ApiModelProperty(value = "活动区域id")
	private String activityArea;

	@ApiModelProperty(value = "活动门店")
	private String activityShop;

	@ApiModelProperty(value = "礼包状态(1未提交,3未通过)")
	private String status;

	@ApiModelProperty(value = "发放方式")
	private Integer gpDeliveringWay;

	@ApiModelProperty(value = "预发数量")
	private String preSendNum;

	@ApiModelProperty(value = "优惠券id列表")
	private String couponsList;

	@ApiModelProperty(value = "会员卡号列表")
	private String custNbrList;

	public Integer getPgId() {
		return pgId;
	}

	public void setPgId(Integer pgId) {
		this.pgId = pgId;
	}

	public String getGiftPackageName() {
		return giftPackageName;
	}

	public void setGiftPackageName(String giftPackageName) {
		this.giftPackageName = giftPackageName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getActivityArea() {
		return activityArea;
	}

	public void setActivityArea(String activityArea) {
		this.activityArea = activityArea;
	}

	public String getActivityShop() {
		return activityShop;
	}

	public void setActivityShop(String activityShop) {
		this.activityShop = activityShop;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getGpDeliveringWay() {
		return gpDeliveringWay;
	}

	public void setGpDeliveringWay(Integer gpDeliveringWay) {
		this.gpDeliveringWay = gpDeliveringWay;
	}

	public String getPreSendNum() {
		return preSendNum;
	}

	public void setPreSendNum(String preSendNum) {
		this.preSendNum = preSendNum;
	}

	public String getCouponsList() {
		return couponsList;
	}

	public void setCouponsList(String couponsList) {
		this.couponsList = couponsList;
	}

	public String getCustNbrList() {
		return custNbrList;
	}

	public void setCustNbrList(String custNbrList) {
		this.custNbrList = custNbrList;
	}

}
