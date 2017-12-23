package com.smi.sms.model;

import java.util.Date;

public class SmsHistory {
	private Integer id;

	/**
	 * 渠道
	 */
	private String channel;
	
	/**
     * 公司类型( 1-梦网       2-网景互动)
     */
    private String companyType;
	

	/**
	 * 账户类型：( 1-生产账号       2-服务类账号)
	 */
	private Integer accountType; 

	/**
	 * 序列号
	 */
	private String reqNo;

	/**
	 * 手机号码
	 */
	private String phoneNo;

	/**
	 * 短信内容
	 */
	private String content;
	
	/**
	 * 手机号码个数
	 */
	private Integer mobiCount;

	/**
	 * 状态编码：READY：待发送  SUECCESS：成功  FAIL：失败 REJECT：拒绝
	 */
	private String statusCode;

	/**
	 * 接收时间
	 */
	private Date createTime;

	/**
	 * 发送时间
	 */
	private Date sendTime;

	/**
	 * 备注
	 */
	private String note;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getMobiCount() {
		return mobiCount;
	}

	public void setMobiCount(Integer mobiCount) {
		this.mobiCount = mobiCount;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	
}