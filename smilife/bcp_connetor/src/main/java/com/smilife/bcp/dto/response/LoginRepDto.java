package com.smilife.bcp.dto.response;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户登录响应dto
 * 
 * @author xzr
 * @date 2016/03/11
 */
public class LoginRepDto implements Serializable {

	private static final long serialVersionUID = 7219655608033916830L;

	// 用户信息对象
	@JSONField(name = "CUST")
	private CustDto cust;

	// 用户会话TOKEN
	@JSONField(name = "TOKEN")
	private String token;
		
	// 返回结果对象
	@JSONField(name = "RESP")
	private ResultDTO resp;

	public CustDto getCust() {
		return cust;
	}

	public void setCust(CustDto cust) {
		this.cust = cust;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ResultDTO getResp() {
		return resp;
	}

	public void setResp(ResultDTO resp) {
		this.resp = resp;
	}

	

}
