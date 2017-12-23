package com.iskyshop.smilife.enums;

import com.iskyshop.core.tools.StringUtils;

/**
 * 退款方式
 * @author Ryan Wu
 *
 */
public enum RefundModeEnum {
	/**
	 * 部分退
	 */
	Partial("1"),
	
	/**
	 * 全部退
	 */
	Full("2");
	
	private String code;
	
	private RefundModeEnum(String code) {
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
	public static RefundModeEnum parseCode(String code) {
		if(!StringUtils.isNullOrEmpty(code)) {
			for(RefundModeEnum mode: RefundModeEnum.values()) {
				if(mode.getCode().equals(code)) {
					return mode;
				}
			}
		}
		
		return null;
	}
}
