package com.smi.mc.api.valueobject.enums;

/**
 * 会员资料查询类型枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum QryTypeEnum {

	/**
	 * 手机号码
	 */
	MOBILE("1", "手机号码"),

	/**
	 * 会员卡号
	 */
	CARD_NBR("2", "会员卡号"),
	
	/**
	 * 线上登录账号
	 */
	LOGIN_ACCOUNT("3", "线上登录账号"),
	
	/**
	 * 会员编码
	 */
	CUST_CODE("4", "会员编码"),
	
	/**
	 * 身份证号码
	 */
	IDCARD("5", "身份证号码"),
	
	/**
	 * 会员卡号+业务号码+线上登录账号
	 */
	ALL("6", "会员卡号+业务号码+线上登录账号");


	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	QryTypeEnum(String code, String name) {
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
