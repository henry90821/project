package com.smi.mc.vo.cust;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smi.mc.vo.eumus.CustVipLevelEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 登录结果值对象<br/>
 * Created by Andriy on 16/9/19.
 */
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "登录结果值对象")
public class LoginVo {

	/**
	 * 登录后令牌
	 */
	@ApiModelProperty(value = "登录后令牌")
	private String smiToken;
	
	/**
	 * 会员编号
	 */
	@ApiModelProperty(value = "会员编号")
	private String custId;

	/**
	 * 会员姓名
	 */
	@ApiModelProperty(value = "会员姓名")
	private String custName;

	/**
	 * 会员等级编码
	 */
	@ApiModelProperty(value = "会员等级编码")
	private String custVipLevelCode;

	/**
	 * 会员等级名称
	 */
	@ApiModelProperty(value = "会员等级名称")
	private String custVipLevelName;

	/**
	 * 登录账号类型
	 */
	@ApiModelProperty(value = "登录账号类型")
	private String custNbrType;

	/**
	 * 会员ID
	 */
	public String getCustId() {
		return custId;
	}

	/**
	 * 会员ID
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}

	/**
	 * 会员名称
	 */
	public String getCustName() {
		return custName;
	}

	/**
	 * 会员名称
	 */
	public void setCustName(String custName) {
		this.custName = custName;
	}

	/**
	 * 会员等级编码
	 */
	public String getCustVipLevelCode() {
		return custVipLevelCode;
	}

	/**
	 * 会员等级名称
	 */
	public String getCustVipLevelName() {
		return custVipLevelName;
	}

	public void setCustVipLevel(CustVipLevelEnum custVipLevel) {
		this.custVipLevelCode = custVipLevel.getCode();
		this.custVipLevelName = custVipLevel.getName();
	}

	/**
	 * 登录账号类型
	 */
	public String getCustNbrType() {
		return custNbrType;
	}

	/**
	 * 登录账号类型
	 */
	public void setCustNbrType(String custNbrType) {
		this.custNbrType = custNbrType;
	}
	
	/**
	 * 令牌
	 */
	public String getSmiToken() {
		return smiToken;
	}

	/**
	 * 令牌
	 */
	public void setSmiToken(String smiToken) {
		this.smiToken = smiToken;
	}
	
}
