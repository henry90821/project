package com.smi.am.service.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.utils.ValidatorUtil;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "礼包vo")
public class GiftPackageVo extends BaseVo {

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
	@Pattern(regexp = "[1,2]", message = "输入参数不正确")
	@NotEmpty
	@ApiModelProperty(value = "礼包状态")
	private String status;

	@ApiModelProperty(value = "发放方式")
	private Integer gpDeliveringWay;

	@ApiModelProperty(value = "预发数量")
	private String preSendNum;

	@ApiModelProperty(value = "实发数量")
	private Integer sendNum;

	private String[] activityAreaID;

	// @ApiModelProperty(value = "优惠券详情列表")
	private List<AmCounponsDetails> amcList;

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
		this.channel = channel == null ? "" : channel;
	}

	public String getActivityArea() {
		return activityArea;
	}

	public void setActivityArea(String activityArea) {
		this.activityArea = activityArea;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public List<AmCounponsDetails> getAmcList() {
		return amcList;
	}

	public void setAmcList(List<AmCounponsDetails> amcList) {
		this.amcList = amcList;
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

	public String getActivityShop() {
		return activityShop;
	}

	public void setActivityShop(String activityShop) {
		this.activityShop = activityShop;
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

	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	public String[] getActivityAreaID() {
		return activityAreaID;
	}

	public void setActivityAreaID(String[] activityAreaID) {
		this.activityAreaID = activityAreaID;
	}

	public  boolean validatorParam(String... args) {
		boolean flag = true;
		for (String s : args) {
			if (!ValidatorUtil.isEmptyIgnoreBlank(s)) {
				continue;
			} else {
				return false;
			}
		}
		return flag;
	}

}
