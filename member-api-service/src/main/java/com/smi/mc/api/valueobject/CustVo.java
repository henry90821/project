package com.smi.mc.api.valueobject;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 会员信息结果值对象<br/>
 * 
 */
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "会员信息结果值对象")
public class CustVo {
	
	@ApiModelProperty(value = "会员编码")
	private String custId;

	@ApiModelProperty(value = "会员名称")
	private String custName;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	

}
