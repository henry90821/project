package com.smi.pay.service.request;

/**
 * 订单类型
 */
public enum OrderType {

	/**
	 * 虚拟卡消费
	 */
	VIRTUALCARD(1, "虚拟卡消费");

	private int code;
	private String name;

	private OrderType(int code, String name) {
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
