package com.smi.mc.api.valueobject;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smi.mc.api.valueobject.enums.BlackListEnum;
import com.smi.mc.api.valueobject.enums.CertiTypeEnum;
import com.smi.mc.api.valueobject.enums.CustTypeEnum;
import com.smi.mc.api.valueobject.enums.SexEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "会员资料详细信息结果值对象")
public class MemberInfoVo extends LoginVo{

	@ApiModelProperty(value = "会员卡号")
	private String custNbr;
	
	@ApiModelProperty(value = "会员昵称")
	private String nickName;
	
	@ApiModelProperty(value = "性别编码")
	private String sexCode;
	
	@ApiModelProperty(value = "性别名称")
	private String sexName;
	
	@ApiModelProperty(value = "联系地址")
	private String contactAddr;
	
	@ApiModelProperty(value = "会员类型编码")
	private String custTypeCode;
	
	@ApiModelProperty(value = "会员类型名称")
	private String custTypeName;
	
	@ApiModelProperty(value = "会员联系电话")
	private String mobile;
	
	@ApiModelProperty(value = "证件类型编码")
	private String certiTypeCode;
	
	@ApiModelProperty(value = "证件类型名称")
	private String certiTypeName;
	
	@ApiModelProperty(value = "证件号码")
	private String certiNbr;
	
	@ApiModelProperty(value = "证件地址")
	private String certiAddr;
	
	@ApiModelProperty(value = "电子邮箱")
	private String email;
	
	@ApiModelProperty(value = "生日")
	private Date birthdate;
	
	@ApiModelProperty(value = "黑白名单编码")
	private String blackListCode;
	
	@ApiModelProperty(value = "黑白名单名称")
	private String blackListName;
	
	@ApiModelProperty(value = "入会时间")
	private Date joinDate;
	
	@ApiModelProperty(value = "入会渠道")
	private String channelCode;
	
	@ApiModelProperty(value = "最后更新资料日期")
	private Date modDate;

	@ApiModelProperty(value = "是否需要同步密码(1:完全同步成功;2:密码未同步;3:资料未同步)")
	private Short issync;

	public String getCustNbr() {
		return custNbr;
	}

	public void setCustNbr(String custNbr) {
		this.custNbr = custNbr;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(SexEnum sexEnum) {
		this.sexCode = sexEnum.getCode();
		this.sexName = sexEnum.getName();
	}

	public String getSexName() {
		return sexName;
	}

	public String getContactAddr() {
		return contactAddr;
	}

	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}

	public void setCustTypeCode(CustTypeEnum custTypeEnum) {
		this.custTypeCode = custTypeEnum.getCode();
		this.custTypeName = custTypeEnum.getName();
	}

	public String getCustTypeName() {
		return custTypeName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getCertiTypeCode() {
		return certiTypeCode;
	}

	public void setCertiTypeCode(CertiTypeEnum certiTypeEnum) {
		this.certiTypeCode = certiTypeEnum.getCode();
		this.certiTypeName = certiTypeEnum.getName();
	}

	public String getCertiTypeName() {
		return certiTypeName;
	}

	public String getCertiNbr() {
		return certiNbr;
	}

	public void setCertiNbr(String certiNbr) {
		this.certiNbr = certiNbr;
	}

	public String getCertiAddr() {
		return certiAddr;
	}

	public void setCertiAddr(String certiAddr) {
		this.certiAddr = certiAddr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getBlackListCode() {
		return blackListCode;
	}

	public void setBlackListCode(BlackListEnum blackListEnum) {
		this.blackListCode = blackListEnum.getCode();
		this.blackListName = blackListEnum.getName();
	}

	public String getBlackListName() {
		return blackListName;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public Short getIssync() {
		return issync;
	}

	public void setIssync(Short issync) {
		this.issync = issync;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	
	
}
