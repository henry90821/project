package com.smi.am.service.vo;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class WraparoundResVo {

	@ApiModelProperty(value = "卷包id")
	private String id;

	@ApiModelProperty(value = "卷包名称")
	private String WraparoundName;

	@ApiModelProperty(value = "投放渠道")
	private String putChannel;

	@ApiModelProperty(value = "可用渠道")
	private String usableChannel;

	@ApiModelProperty(value = "预发数量")
	private String preSendNum;

	@ApiModelProperty(value = "活动开始时间")
	private Date activityStartTime;

	@ApiModelProperty(value = "活动结束时间")
	private Date activityEndTime;

	@ApiModelProperty(value = "活动区")
	private String activityArea;
	
	@ApiModelProperty(value = "审核状态")
	private String status;
	
	@ApiModelProperty(value = "原因")
	private String cRemark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWraparoundName() {
		return WraparoundName;
	}

	public void setWraparoundName(String wraparoundName) {
		WraparoundName = wraparoundName;
	}

	public String getPutChannel() {
		return putChannel;
	}

	public void setPutChannel(String putChannel) {
		this.putChannel = putChannel;
	}

	public String getUsableChannel() {
		return usableChannel;
	}

	public void setUsableChannel(String usableChannel) {
		this.usableChannel = usableChannel;
	}

	public String getPreSendNum() {
		return preSendNum;
	}

	public void setPreSendNum(String preSendNum) {
		this.preSendNum = preSendNum;
	}


	public Date getActivityStartTime() {
		return activityStartTime;
	}

	public void setActivityStartTime(Date activityStartTime) {
		this.activityStartTime = activityStartTime;
	}

	public Date getActivityEndTime() {
		return activityEndTime;
	}

	public void setActivityEndTime(Date activityEndTime) {
		this.activityEndTime = activityEndTime;
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

	public String getcRemark() {
		return cRemark;
	}

	public void setcRemark(String cRemark) {
		this.cRemark = cRemark;
	}

}
