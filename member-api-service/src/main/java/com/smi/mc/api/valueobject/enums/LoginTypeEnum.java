package com.smi.mc.api.valueobject.enums;

/**
 * 登录类型枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum LoginTypeEnum {

	/**
	 * 账号登录
	 */
	ACCOUNT_LOGIN("1", "账号登录"),

	/**
	 * 短信验证码登录
	 */
	SMS_VERIFYCODE_LOGIN("3", "短信验证码登录");

	/**
	 * 实例化枚举对象
	 * 
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	LoginTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 枚举值编码
	 */
	private String code;

	/**
	 * 枚举值名称
	 */
	private String name;

	/**
	 * 枚举值编码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 枚举值名称
	 */
	public String getName() {
		return name;
	}
}
