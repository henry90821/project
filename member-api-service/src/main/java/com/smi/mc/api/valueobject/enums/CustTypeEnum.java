package com.smi.mc.api.valueobject.enums;

/**
 * 会员类型编码枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum CustTypeEnum {

	/**
	 * 公众
	 */
	PUBLIC_PEOPLE("1000", "公众"),

	/**
	 * 政企
	 */
	ENTERPRISE("1100", "政企");


	/**
	 * 实例化枚举对象
	 *
	 * @param code
	 *            枚举值编码
	 * @param name
	 *            枚举值名称
	 */
	CustTypeEnum(String code, String name) {
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
	 *            会员类型编码值
	 * @return
	 */
	public static CustTypeEnum getEnumByCode(String custTypeCode) {
		CustTypeEnum result = null;
		// 判断结果
		if (PUBLIC_PEOPLE.getCode().equals(custTypeCode)) { 
			result = PUBLIC_PEOPLE;
		} else if (ENTERPRISE.getCode().equals(custTypeCode)) {
			result = ENTERPRISE;
		}
		return result;
	}
}
