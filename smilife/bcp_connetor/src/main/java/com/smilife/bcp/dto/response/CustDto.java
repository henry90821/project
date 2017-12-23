package com.smilife.bcp.dto.response;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 会员资料查询dto
 * 
 * @author xzr
 * @date 2016/03/11
 */
public class CustDto implements Serializable {

	private static final long serialVersionUID = 177678505263694970L;

	// 会员ID
	@JSONField(name = "CUST_ID")
	private String custId;

	
	// 会员名称
	@JSONField(name = "CUST_NAME")
	private String custName;
		
	@JSONField(name = "LOGON_NBR_TYPE")
	private String logonNbrType;
	
	// 会员等级 1000：星美生活会员，1100：黄金会员，1200：白金会员，1300：钻石会员，1998：随影卡会员(系统标识是鼎新时返回)，1999：百度联名会员（系统标识是鼎新时返回，为了鼎新系统给百度联名会员打4折）
	@JSONField(name = "CUST_VIP_LEVEL")
	private String custVipLevel;

	/**
	 * @return the custId
	 */
	public String getCustId() {
		return custId;
	}

	/**
	 * @param custId the custId to set
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}

	/**
	 * @return the custName
	 */
	public String getCustName() {
		return custName;
	}

	/**
	 * @param custName the custName to set
	 */
	public void setCustName(String custName) {
		this.custName = custName;
	}


	/**
	 * @return the logonNbrType
	 */
	public String getLogonNbrType() {
		return logonNbrType;
	}

	/**
	 * @param logonNbrType the logonNbrType to set
	 */
	public void setLogonNbrType(String logonNbrType) {
		this.logonNbrType = logonNbrType;
	}

	/**
	 * @return the custVipLevel
	 */
	public String getCustVipLevel() {
		return custVipLevel;
	}

	/**
	 * @param custVipLevel the custVipLevel to set
	 */
	public void setCustVipLevel(String custVipLevel) {
		this.custVipLevel = custVipLevel;
	}
	

	

}
