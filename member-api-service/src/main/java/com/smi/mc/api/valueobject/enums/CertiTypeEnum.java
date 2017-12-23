package com.smi.mc.api.valueobject.enums;

/**
 * 证件类型枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum CertiTypeEnum {

	/**
	 * 身份证
	 */
	IDCARD("10", "身份证"),

	/**
	 * 其他
	 */
	OTHER("20", "其他"),
	
	/**
	 * 组织机构代码证 
	 */
	ORGCODE("30", "组织机构代码证");


	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	CertiTypeEnum(String code, String name) {
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
	
	/**
	 * 根据编码获取相应枚举对象
	 *
	 * @param certiCode
	 *            证件类型编码值
	 * @return
	 */
	public static CertiTypeEnum getEnumByCode(String certiCode) {
		CertiTypeEnum result = null;
		// 判断结果
		if (IDCARD.getCode().equals(certiCode)) { 
			result = IDCARD;
		} else if (OTHER.getCode().equals(certiCode)) {
			result = OTHER;
		} else if (ORGCODE.getCode().equals(certiCode)){
			result = ORGCODE;
		}else {
			result = OTHER;
		}
		return result;
	}
}
