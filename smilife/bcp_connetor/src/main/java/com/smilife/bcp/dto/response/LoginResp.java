package com.smilife.bcp.dto.response;

import com.smilife.bcp.dto.common.BaseResponse;

/**
 * Created by 亚翔 on 2015/8/24.
 */
public class LoginResp extends BaseResponse {
	private String custName;
	private String custId;

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
}
