package com.smi.mc.api.valueobject.enums;

/**
 * 黑白名单枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum BlackListEnum {

	/**
	 * 黑名单
	 */
	BLACK("0", "黑名单"),

	/**
	 * 白名单
	 */
	WHITE("1", "白名单");


	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	BlackListEnum(String code, String name) {
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
	 * @param blackCode
	 *            是否黑名单编码值
	 * @return
	 */
	public static BlackListEnum getEnumByCode(String blackCode) {
		BlackListEnum result = null;
		// 判断结果
		if (BLACK.getCode().equals(blackCode)) { //黑名单
			result = BLACK;
		} else if (WHITE.getCode().equals(blackCode)) { // 白名单
			result = WHITE;
		}
		return result;
	}
}
