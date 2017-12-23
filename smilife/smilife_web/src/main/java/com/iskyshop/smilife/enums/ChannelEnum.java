package com.iskyshop.smilife.enums;


/**
 * 订单支付渠道（不是订单的下单渠道）
 * 
 * @author Ryan Wu
 * 
 */
public enum ChannelEnum {
	/**
	 * 手机移动端
	 */
	APP("手机移动端"),

	/**
	 * 微信端
	 */
	WX("微信"),

	/**
	 * PC网页
	 */
	WEB("pc网页"),

	/**
	 * 手机网页WAP
	 */
	WAP("手机网页");

	private String descr;

	private ChannelEnum(String descr) {
		this.descr = descr;
	}

	/**
	 * 获取中文名称.
	 * 
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * 根据枚举名获取对应的枚举值。若失败，则返回null
	 * 
	 */
	public static final ChannelEnum parse(String value) {
		try {
			return ChannelEnum.valueOf(value);
		} catch (Throwable t) {
			return null;
		}
	}
}
