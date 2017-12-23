package com.smi.mc.api.valueobject.enums;

/**
 * 密码类型枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum PwdTypeEnum {

	/**
	 * 登录密码
	 */
	LOGIN_PWD("1", "登录密码"),

	/**
	 * 服务密码（支付密码）
	 */
	SERVICE_PWD("2", "服务密码（支付密码）");

	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	PwdTypeEnum(String code, String name) {
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
