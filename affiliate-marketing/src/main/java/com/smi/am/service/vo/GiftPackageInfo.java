package com.smi.am.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class GiftPackageInfo {
	@ApiModelProperty(value = "礼包id")
	private Integer gpId;

	@ApiModelProperty(value = "礼包名称")
	private String gpName;

	@ApiModelProperty(value = "投放渠道")
	private String gpChannel;

	@ApiModelProperty(value = "实发数量	")
	private int gpSendnum;

	@ApiModelProperty(value = "活动区域")
	private String gpActivityarea;

	@ApiModelProperty(value = "活动门店")
	private JSONArray gpActivityShop;

	@ApiModelProperty(value = "活动开始时间")
	private Date gpStarttime;

	@ApiModelProperty(value = "活动结束时间")
	private Date gpEndtime;

	@ApiModelProperty(value = "当前状态")
	private String gpStatus;

	@ApiModelProperty(value = "发放对象")
	private String gpProvideuser;

	@ApiModelProperty(value = "预发数量")
	private String preSendNum;
	
	@ApiModelProperty(value = "发放方式")
	private Integer gpDeliveringWay;

	public Integer getGpId() {
		return gpId;
	}

	public void setGpId(Integer gpId) {
		this.gpId = gpId;
	}

	public String getGpName() {
		return gpName;
	}

	public void setGpName(String gpName) {
		this.gpName = gpName;
	}

	public String getGpChannel() {
		return gpChannel;
	}

	public void setGpChannel(String gpChannel) {
		this.gpChannel=gpChannel==null?"":gpChannel;
	}

	public int getGpSendnum() {
		return gpSendnum;
	}

	public void setGpSendnum(int gpSendnum) {
		this.gpSendnum = gpSendnum;
	}

	public String getGpActivityarea() {
		return gpActivityarea;
	}

	public void setGpActivityarea(String gpActivityarea) {
		this.gpActivityarea = gpActivityarea;
	}


	public JSONArray getGpActivityShop() {
		return gpActivityShop;
	}

	public void setGpActivityShop(JSONArray gpActivityShop) {
		this.gpActivityShop = gpActivityShop;
	}

	public Date getGpStarttime() {
		return gpStarttime;
	}

	public void setGpStarttime(Date gpStarttime) {
		this.gpStarttime = gpStarttime;
	}

	public Date getGpEndtime() {
		return gpEndtime;
	}

	public void setGpEndtime(Date gpEndtime) {
		this.gpEndtime = gpEndtime;
	}

	public String getGpStatus() {
		return gpStatus;
	}

	public void setGpStatus(String gpStatus) {
		this.gpStatus = gpStatus;
	}

	public String getGpProvideuser() {
		return gpProvideuser;
	}

	public void setGpProvideuser(String gpProvideuser) {
		this.gpProvideuser = gpProvideuser;
	}

	public String getPreSendNum() {
		return preSendNum;
	}

	public void setPreSendNum(String preSendNum) {
		this.preSendNum = preSendNum;
	}

	public Integer getGpDeliveringWay() {
		return gpDeliveringWay;
	}

	public void setGpDeliveringWay(Integer gpDeliveringWay) {
		this.gpDeliveringWay = gpDeliveringWay;
	}


}
