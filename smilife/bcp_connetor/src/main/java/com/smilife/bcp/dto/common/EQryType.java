package com.smilife.bcp.dto.common;

public enum EQryType {
	
	/**
	 * 查找类型为：用户手机号
	 */
	ByMobileNumber("1"),
	
	/**
	 * 查找类型为：用户会员卡号
	 */
	ByMemberCardNumber("2"), 
	
	/**
	 * 查找类型为：用户在线登录账号
	 */
	ByOnlineAccount("3"),
	
	/**
	 * 查找类型为：用户会员编码
	 */
	ByMemberNumber("4");
	
	
	private String qryType;
	
	private EQryType(String qryType) {
		this.qryType = qryType;
	}

	public String getQryType() {
		return qryType;
	}
}
