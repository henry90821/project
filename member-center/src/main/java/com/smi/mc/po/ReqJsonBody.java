package com.smi.mc.po;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "用于接口请求的VO对象")
public class ReqJsonBody implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "渠道编码")
	private String appcode;
	 
	@ApiModelProperty(value = "序列")
	private String reqno;
	 
	@ApiModelProperty(value = "签名")
	private String sign;
	 
	@ApiModelProperty(value = "接口报文数据")
	private String data;

	public String getAppcode() {
		return appcode;
	}

	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}

	public String getReqno() {
		return reqno;
	}

	public void setReqno(String reqno) {
		this.reqno = reqno;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
