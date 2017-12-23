package com.smi.mc.api.valueobject.enums;

/**
 * 会员等级枚举<br/>
 * Created by Andriy on 16/9/19.
 */
public enum CustVipLevelEnum {

	/**
	 * 星美生活会员
	 */
	SMILIFE_MEMBER("1000", "星美生活会员"),

	/**
	 * 黄金会员
	 */
	GOLD_LEVEL_MEMBER("1100", "黄金会员"),

	/**
	 * 白金会员
	 */
	PLATINUM_LEVEL_MEMBER("1200", "白金会员"),

	/**
	 * 钻石会员
	 */
	DIAMOND_LEVEL_MEMBER("1300", "钻石会员");

	/**
	 * 会员等级编码
	 */
	private String code;

	/**
	 * 会员等级名称
	 */
	private String name;

	/**
	 * 实例化会员等级枚举
	 *
	 * @param code
	 *            会员等级编码
	 * @param name
	 *            会员等级名称
	 */
	CustVipLevelEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 会员等级编码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 会员等级名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 根据等级编码获取相应枚举对象
	 *
	 * @param levelCode
	 *            等级编码值
	 * @return
	 */
	public static CustVipLevelEnum getEnumByCode(String levelCode) {
		CustVipLevelEnum result = null;
		// 判断结果
		if (SMILIFE_MEMBER.getCode().equals(levelCode)) { // 星美生活会员
			result = SMILIFE_MEMBER;
		} else if (GOLD_LEVEL_MEMBER.getCode().equals(levelCode)) { // 黄金会员
			result = GOLD_LEVEL_MEMBER;
		} else if (PLATINUM_LEVEL_MEMBER.getCode().equals(levelCode)) { // 白金会员
			result = PLATINUM_LEVEL_MEMBER;
		} else if (DIAMOND_LEVEL_MEMBER.getCode().equals(levelCode)) { // 钻石会员
			result = DIAMOND_LEVEL_MEMBER;
		}
		return result;
	}
}
