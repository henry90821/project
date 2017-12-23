package com.smilife.bcp.dto.common;

/**
 * 密码重置类型枚举
 * 
 * @author liz 2015-8-28
 */
public enum EPwdResetType {

	/**
	 * 修改
	 */
	MODIFY("1"),

	/**
	 * 重置
	 */
	RESET("2"),
	
	/**
	 * 找回密码
	 */
	FIND("3");

	private String state;

	private EPwdResetType(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
