package com.smi.sms.model.query;

import com.smi.sms.model.SmsHistory;

public class QueryResult extends SmsHistory {

	/**
	 * 状态码描述
	 */
	private String statusDesc;
	
	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

}
