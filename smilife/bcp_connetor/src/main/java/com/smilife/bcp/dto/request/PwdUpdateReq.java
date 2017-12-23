package com.smilife.bcp.dto.request;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.framework.util.PropertyUtil;

/**
 * CRM修改密码DTO
 * @author liz
 * 2015-8-27
 */
public class PwdUpdateReq implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JSONField(name = "CUST_ID")
	private String custId; //会员唯一标识
	
	@JSONField(name = "OLD_PWD")
	private String oldPwd;	//老密码
	
	@JSONField(name = "NEW_PWD")
	private String newPwd; //新密码
	
	@JSONField(name = "PWD_TYPE")
	private String pwdType; //1，登录密码	2，服务密码（支付密码）
	
	@JSONField(name = "RESET_TYPE")
	private String resetType; //1:修改  2:重置 3:找回密码
	
	
	@JSONField(name = "CHANNEL_CODE")
	private String channelcode;
	
	@JSONField(name = "EXT_SYSTEM")
	private String extsystem;

	public PwdUpdateReq() {
		channelcode = PropertyUtil.getProperty("CHANNEL_CODE");
		extsystem = PropertyUtil.getProperty("EXT_SYSTEM");
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getPwdType() {
		return pwdType;
	}

	public void setPwdType(String pwdType) {
		this.pwdType = pwdType;
	}

	public String getResetType() {
		return resetType;
	}

	public void setResetType(String resetType) {
		this.resetType = resetType;
	}

	public String getChannelcode() {
		return channelcode;
	}

	public void setChannelcode(String channelcode) {
		this.channelcode = channelcode;
	}

	public String getExtsystem() {
		return extsystem;
	}

	public void setExtsystem(String extsystem) {
		this.extsystem = extsystem;
	}
	
	

}
