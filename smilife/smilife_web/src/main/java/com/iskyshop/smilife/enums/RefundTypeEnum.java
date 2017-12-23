package com.iskyshop.smilife.enums;

import com.iskyshop.core.tools.StringUtils;

/**
 * 退款类型
 * @author Ryan Wu
 *
 */
public enum RefundTypeEnum {
	/**
	 * 订单中商品退货时的退款
	 */
	GoodsRefund("0"),
	
	/**
	 * 团购消费码的退款
	 */
	GroupSNRefund("1"),
	
	/**
	 * 整个订单的退款
	 */
	OrderRefund("2");
	
	
	private String code;
	
	private RefundTypeEnum(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	

	
	/**
	 * 根据提供的枚举的code获取对应的枚举值。若未获取不对劲对应的枚举值，则返回null
	 * @param code
	 * @return
	 */
	public static RefundTypeEnum parseCode(String code) {
		if(!StringUtils.isNullOrEmpty(code)) {
			for(RefundTypeEnum type: RefundTypeEnum.values()) {
				if(type.getCode().equals(code)) {
					return type;
				}
			}
		}
		
		return null;
	}

}
