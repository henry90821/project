package com.smilife.bcp.dto.common;

/**
 * 密码类型枚举
 * 
 * @author liz 2015-8-28
 */
public enum EPwdType {

	/**
	 * 登录密码
	 */
	LOGIN_PWD("1"),

	/**
	 * 服务密码（支付密码）
	 */
	PAY_PWD("2");

	private String state;

	private EPwdType(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
