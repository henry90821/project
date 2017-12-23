package com.smi.mc.api.valueobject.enums;

/**
 * 重置密码类型枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum RestPwdTypeEnum {

	/**
	 * 登录密码
	 */
	MODIFY_PWD("1", "修改密码"),

	/**
	 * 重置密码
	 */
	RESET_PWD("2", "重置密码"),

	/**
	 * 找回密码
	 */
	FIND_PWD("3", "找回密码");

	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	RestPwdTypeEnum(String code, String name) {
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
