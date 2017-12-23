package com.smilife.bcp.dto.response;


import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class ResultDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5768090216026431024L;
	
	@JSONField(name = "RESULT")
	private String result; // 资源转换比例(0:成功；1：失败)
	
	@JSONField(name = "CODE")
	private String code; // 资源转换类型
	
	@JSONField(name = "MSG")
	private String msg; // 资源转换名称

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
}
