package com.iskyshop.smilife.buy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 提交的商鋪相關信息
 * @author herendian
 * @version 1.0
 * @date 2016年3月29日 上午11:37:17
 */
@JsonSerialize
@ApiModel
public class SubmitStoreInfoVo {
	
	@ApiModelProperty("店鋪ID")
	private String storeId;
	
	@ApiModelProperty("留言信息")
	private String remark;
	
	@ApiModelProperty("优惠券信息")
	private String transport;
	
	@ApiModelProperty("物流信息")
	private String couponId;

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	
	
	
	

}
