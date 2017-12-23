package com.smilife.bcp.dto.common;

/**
 * bcp接口返回结果枚举类
 * 
 * @author liz 2015-8-28
 */
public enum EInterfaceState {

	/**
	 * 成功
	 */
	SUCCESS("0"),

	/**
	 * 失败
	 */
	FAIL("1");

	private String state;

	private EInterfaceState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
