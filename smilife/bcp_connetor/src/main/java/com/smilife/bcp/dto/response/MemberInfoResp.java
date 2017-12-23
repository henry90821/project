package com.smilife.bcp.dto.response;


import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 会员资料DTO
 * @author liz
 * 2015-8-26
 */
public class MemberInfoResp implements Serializable {
	
	private static final long serialVersionUID = -7346108143723084188L;

	@JSONField(name = "CUST_NBR")
	private String custNbr; // 会员卡号
	
	@JSONField(name = "CUST_NAME")
	private String custName; // 会员名称
	
	@JSONField(name = "CUST_ID")
	private String custId; // 会员唯一标识
	
/*	@JSONField(name = "CUST_TYPE")
	private String custType; // 会员类型
*/	
	@JSONField(name = "CONTACT_ADDR")
	private String contactAddr; // 联系地址
	
	@JSONField(name = "SEX")
	private String sex; // 性别
	
	@JSONField(name = "NICK_NAME")
	private String nickName; // 会员昵称
	
	@JSONField(name = "MOBILE")
	private String mobile; // 会员联系电话，会员当前绑定的手机号码
	
/*	@JSONField(name = "CUST_VIP_LEVEL")
	private String custVipLevel; // 会员等级 1000：普通会员 1100：银牌会员 1200：金牌会员 1300：铂金会员
*/
	@JSONField(name = "MOBILE_170")
	private String mobile170; // 170号码
	
	@JSONField(name = "CERTI_TYPE")
	private String certiType; // 身份证类型—主数据编码
	
	@JSONField(name = "CERTI_NBR")
	private String certiNbr; // 身份证
	
/*	@JSONField(name = "AUTH_FLAG")
	private String authFlag; // 身份证认证标识，1：已认证；0：未认证
*/	
	@JSONField(name = "EMAIL")
	private String email; // 电子邮箱
	
	@JSONField(name = "BIRTHDATE")
	private String birthDate; // 生日 日期格式：YYYY-MM-DD
	
	@JSONField(name = "JOIN_DATE")
	private String joinDate; // 入会时间：格式YYYY-MM-DD 
	
/*	@JSONField(name = "BLACKLIST")
	private String blackList; // 是否黑名单用户  1100：否  1000：是

	
	@JSONField(name = "CHANNEL_CODE")
	private String channelCode; // 入会渠道：组织机构的渠道编号
	
	@JSONField(name = "CHANNEL_NAME")
	private String channelName; // 入会渠道名称
	
	@JSONField(name = "ACTIVE_MOVIE")
	private String activeMovie; // 观影活跃状态  1000：活跃  1100：沉寂  1200：睡眠  1300：流失
	
	@JSONField(name = "ACTIVE_SALE")
	private String activeSale; // 零售活跃状态1000：活跃  1100：沉寂  1200：睡眠  1300：流失
*/
	@JSONField(name = "PAY_PWD")
	private String payPwd; // 会员支付密码，1：存在；0：不存在；
	
/*	@JSONField(name = "PROD_INST")
	private List<MemberInfoProdInst> prodInst; // 会员下的170号码，包含主卡、副卡
*/	
/*	@JSONField(name = "PROD_OFFER_INST")
	private List<MemberInfoProdOfferInst> prodOfferInst; // 会员订购的销售品、加油包
*/
	public String getCustNbr() {
		return custNbr;
	}

	public void setCustNbr(String custNbr) {
		this.custNbr = custNbr;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	/*public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}*/

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/*public String getCustVipLevel() {
		return custVipLevel;
	}

	public void setCustVipLevel(String custVipLevel) {
		this.custVipLevel = custVipLevel;
	}
*/
	public String getMobile170() {
		return mobile170;
	}

	public void setMobile170(String mobile170) {
		this.mobile170 = mobile170;
	}

	public String getCertiType() {
		return certiType;
	}

	public void setCertiType(String certiType) {
		this.certiType = certiType;
	}

	public String getCertiNbr() {
		return certiNbr;
	}

	public void setCertiNbr(String certiNbr) {
		this.certiNbr = certiNbr;
	}

	/*public String getAuthFlag() {
		return authFlag;
	}

	public void setAuthFlag(String authFlag) {
		this.authFlag = authFlag;
	}*/

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	/*public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}


	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getActiveMovie() {
		return activeMovie;
	}

	public void setActiveMovie(String activeMovie) {
		this.activeMovie = activeMovie;
	}

	public String getActiveSale() {
		return activeSale;
	}

	public void setActiveSale(String activeSale) {
		this.activeSale = activeSale;
	}*/

	public String getPayPwd() {
		return payPwd;
	}

	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}

	/*public List<MemberInfoProdInst> getProdInst() {
		return prodInst;
	}

	public void setProdInst(List<MemberInfoProdInst> prodInst) {
		this.prodInst = prodInst;
	}

	public List<MemberInfoProdOfferInst> getProdOfferInst() {
		return prodOfferInst;
	}

	public void setProdOfferInst(List<MemberInfoProdOfferInst> prodOfferInst) {
		this.prodOfferInst = prodOfferInst;
	}
*/
	public String getContactAddr() {
		return contactAddr;
	}

	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	
}
