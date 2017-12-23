package com.smi.pay.service.request;

/**
 * 还款方式
 */
public enum RepaymentType {
	// 以下命名汉字拼音
	/**
	 * 先息后本(月付利息)
	 */
	XXHB_YFLX(1, "先息后本(月付利息)"),

	/**
	 * 利随本清
	 */
	LSBQ(2, "利随本清"),

	/**
	 * 等额本息
	 */
	DEBX(3, "等额本息"),

	/**
	 * 等额本金
	 */
	DEBJ(4, "等额本金"),

	/**
	 * 等本等息
	 */
	DBDX(5, "等本等息"),

	/**
	 * 先息后本(季付利息)
	 */
	XXHB_JFLX(6, "先息后本(季付利息)");

	private int code;
	private String name;

	private RepaymentType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
