package com.smi.pay.model;

import java.util.Date;

public class AppInfo {
	
	public Integer id;
	/**
	 * 渠道编码，订单来源系统
	 */
	public String appCode;
	/**
	 * 子系统描述
	 */
	public String appDesc;
	/**
	 * 回调类型 0:支付 1:退款
	 */
	public String kind;
	/**
	 * 支付成功后回调路径
	 */
	public String callBackUrl;
	/**
	 * 创建时间
	 */
	public Date createDate;
	/**
	 * 创建人
	 */
	public String createUser;
	/**
	 * 最后更新时间
	 */
	public Date lastUpdateDate;
	/**
	 * 最后更新人
	 */
	public String lastUpdateUser;
	 
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getAppDesc() {
		return appDesc;
	}
	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}
	public String getCallBackUrl() {
		return callBackUrl;
	}
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	 
	public String getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	

}
