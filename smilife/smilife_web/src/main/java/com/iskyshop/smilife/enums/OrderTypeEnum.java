package com.iskyshop.smilife.enums;

/**
 * 支付订单的类型
 * 
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月7日 上午10:27:05
 */
public enum OrderTypeEnum {
	/**
	 * 购物订单
	 */
	SHOPPING("购物", "1"), 
	
	/**
	 * 充值订单
	 */
	CHARGE("充值", "2"), 
	
	/**
	 * 购票订单
	 */
	BUY_TICKET("购票", "3"), 
	
	/**
	 * 积分兑换订单
	 */
	INTEGRAL("积分兑换", "4");

	// 成员变量
	private String descr;
	private String index;

	// 构造方法
	private OrderTypeEnum(String descr, String index) {
		this.descr = descr;
		this.index = index;
	}

	/**
	 * 通过索引获取对应的枚举值描述。若获取失败，则返回null
	 * @param index
	 * @return
	 */
	public static String getDescr(String index) {
		for (OrderTypeEnum c : OrderTypeEnum.values()) {
			if (c.getIndex().equals(index)) {
				return c.descr;
			}
		}
		return null;
	}

	// 通过描述获取索引
	public static String getIndex(String descr) {
		if (descr == null)
			descr = "";
		for (OrderTypeEnum c : OrderTypeEnum.values()) {
			if (descr.equals(c.descr)) {
				return c.index;
			}
		}
		return "";
	}	

	// get set 方法
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

}
