package com.smi.mc.api.valueobject.enums;

/**
 * 性别枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum SexEnum {

	/**
	 * 男
	 */
	MALE("1000", "男"),
	
	/**
	 * 女
	 */
	FEMALE("1098", "女"),
	
	/**
	 * 未知
	 */
	UNKNOWM("1198", "未知");
	

	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	SexEnum(String code, String name) {
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
	 * 根据性别编码获取相应枚举对象
	 *
	 * @param sexCode
	 *            性别编码值
	 * @return
	 */
	public static SexEnum getEnumByCode(String sexCode) {
		SexEnum result = null;
		// 判断结果
		if (MALE.getCode().equals(sexCode)) { //男
			result = MALE;
		} else if (FEMALE.getCode().equals(sexCode)) { // 女
			result = FEMALE;
		}else if (UNKNOWM.getCode().equals(sexCode)) {
			result = UNKNOWM;
		}else {
			result = UNKNOWM;
		}
		return result;
	}
}
