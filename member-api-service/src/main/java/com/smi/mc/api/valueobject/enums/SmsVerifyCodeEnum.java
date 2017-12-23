package com.smi.mc.api.valueobject.enums;

public enum SmsVerifyCodeEnum {

	/**
	 * 登录功能验证码
	 */
	LOGIN_CODE("1", "登录功能验证码"),

	/**
	 * 注册功能验证码
	 */
	REGISTER_CODE("2", "注册功能验证码");
	
	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	SmsVerifyCodeEnum(String code, String name) {
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
