package com.smi.mc.po;

import java.io.Serializable;

/**
 * 会员中心返回会员信息实体
 * @author smi
 *
 */
public class CustResInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String CUST_NAME;

	private String BIRTHDATE;

	private String CUST_ID;

	private String SEX;

	private String CUST_VIP_LEVEL;

	private String CRT_DATE;
	private String CUST_NBR;
	private String CONTACT_MOBILE;
	private String ORG_ID;
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getBIRTHDATE() {
		return BIRTHDATE;
	}
	public void setBIRTHDATE(String bIRTHDATE) {
		BIRTHDATE = bIRTHDATE;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getCUST_VIP_LEVEL() {
		return CUST_VIP_LEVEL;
	}
	public void setCUST_VIP_LEVEL(String cUST_VIP_LEVEL) {
		CUST_VIP_LEVEL = cUST_VIP_LEVEL;
	}
	public String getCRT_DATE() {
		return CRT_DATE;
	}
	public void setCRT_DATE(String cRT_DATE) {
		CRT_DATE = cRT_DATE;
	}
	public String getCUST_NBR() {
		return CUST_NBR;
	}
	public void setCUST_NBR(String cUST_NBR) {
		CUST_NBR = cUST_NBR;
	}
	public String getCONTACT_MOBILE() {
		return CONTACT_MOBILE;
	}
	public void setCONTACT_MOBILE(String cONTACT_MOBILE) {
		CONTACT_MOBILE = cONTACT_MOBILE;
	}
	public String getORG_ID() {
		return ORG_ID;
	}
	public void setORG_ID(String oRG_ID) {
		ORG_ID = oRG_ID;
	}
	@Override
	public String toString() {
		return "CustResInfo [CUST_NAME=" + CUST_NAME + ", BIRTHDATE=" + BIRTHDATE + ", CUST_ID=" + CUST_ID + ", SEX="
				+ SEX + ", CUST_VIP_LEVEL=" + CUST_VIP_LEVEL + ", CRT_DATE=" + CRT_DATE + ", CUST_NBR=" + CUST_NBR
				+ ", CONTACT_MOBILE=" + CONTACT_MOBILE + ", ORG_ID=" + ORG_ID + "]";
	}
	
	
	
}
