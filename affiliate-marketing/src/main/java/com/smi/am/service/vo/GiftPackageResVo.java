package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "礼包响应vo")
public class GiftPackageResVo {

	@ApiModelProperty(value = "礼包id")
	private Integer pgId;

	@ApiModelProperty(value = "礼包名称")
	private String giftPackageName;

	@ApiModelProperty(value = "预发数量")
	private int preSendNum;

	@ApiModelProperty(value = "实发数量")
	private int sendNum;

	@ApiModelProperty(value = "投放渠道")
	private String putChannel;

	@ApiModelProperty(value = "活动开始时间")
	private String startTime;

	@ApiModelProperty(value = "活动结束时间")
	private String endTime;

	@ApiModelProperty(value = "活动区")
	private String gpActivityarea;

	@ApiModelProperty(value = "状态")
	private String status;

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

	public int getPreSendNum() {
		return preSendNum;
	}

	public void setPreSendNum(int preSendNum) {
		this.preSendNum = preSendNum;
	}

	public int getSendNum() {
		return sendNum;
	}

	public void setSendNum(int sendNum) {
		this.sendNum = sendNum;
	}

	public String getPutChannel() {
		return putChannel;
	}

	public void setPutChannel(String putChannel) {
		this.putChannel = putChannel == null ? "" : putChannel;
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

	public String getGpActivityarea() {
		return gpActivityarea;
	}

	public void setGpActivityarea(String gpActivityarea) {
		this.gpActivityarea = gpActivityarea;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
