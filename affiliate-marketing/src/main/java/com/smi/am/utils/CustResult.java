package com.smi.am.utils;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 会员响应信息
 * 
 * @author smi
 * @param <T>
 *
 */
public class CustResult<T> extends SmiResult<T> {

	@ApiModelProperty(value = "响应中的会员信息")
	private T cust;

	public T getCust() {
		return cust;
	}

	public void setCust(T cust) {
		this.cust = cust;
	}
	
	

}
