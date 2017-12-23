package com.smilife.bcp.dto.request;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.framework.util.PropertyUtil;

@SuppressWarnings("serial")
public class CustUpdateReq implements Serializable {
	@JSONField(name = "CUST_ID")
	private String custId; // 会员唯一标识

	@JSONField(name = "EMAIL")
	private String custEmail; // 电子邮箱

	@JSONField(name = "BIRTHDATE")
	private String custBirthdate; // 生日

	@JSONField(name = "MOBILE")
	private String custMobile; // 会员的联系电话

	@JSONField(name = "NICK_NAME")
	private String custNickName; // 会员昵称

	@JSONField(name = "SEX")
	private String custSex; // 性别

	@JSONField(name = "CONTACT_ADDR")
	private String custContactAddr; // 联系地址

	@JSONField(name = "CERTI_ADDR")
	private String custCertiAddr; // 证件地址

	@JSONField(name = "CUST_NAME")
	private String custName; // 会员名称

	@JSONField(name = "CERTI_NBR")
	private String custCertNbr; // 身份证

	@JSONField(name = "CHANNEL_CODE")
	private String channelcode;

	@JSONField(name = "EXT_SYSTEM")
	private String extsystem;

	public CustUpdateReq() {
		channelcode = PropertyUtil.getProperty("CHANNEL_CODE");
		extsystem = PropertyUtil.getProperty("EXT_SYSTEM");
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustBirthdate() {
		return custBirthdate;
	}

	public void setCustBirthdate(String custBirthdate) {
		this.custBirthdate = custBirthdate;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getCustNickName() {
		return custNickName;
	}

	public void setCustNickName(String custNickName) {
		this.custNickName = custNickName;
	}

	public String getCustSex() {
		return custSex;
	}

	public void setCustSex(String custSex) {
		this.custSex = custSex;
	}

	public String getCustContactAddr() {
		return custContactAddr;
	}

	public void setCustContactAddr(String custContactAddr) {
		this.custContactAddr = custContactAddr;
	}

	public String getCustCertiAddr() {
		return custCertiAddr;
	}

	public void setCustCertiAddr(String custCertiAddr) {
		this.custCertiAddr = custCertiAddr;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustCertNbr() {
		return custCertNbr;
	}

	public void setCustCertNbr(String custCertNbr) {
		this.custCertNbr = custCertNbr;
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
