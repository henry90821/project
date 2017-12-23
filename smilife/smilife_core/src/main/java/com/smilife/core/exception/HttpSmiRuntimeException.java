package com.smilife.core.exception;

import java.util.Map;

public class HttpSmiRuntimeException extends SmiBusinessCodeException {

	private static final long serialVersionUID = 7867715947146745695L;

	private String reqUrl;
	private Map<String, Object> reqParam;
	private String responseStr;

	public HttpSmiRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public HttpSmiRuntimeException(String message, Throwable throwable, String reqUrl, Map<String, Object> reqParam,
			String responseStr) {
		super(message, throwable);
		this.reqUrl = reqUrl;
		this.reqParam = reqParam;
		this.responseStr = responseStr;
	}

	public HttpSmiRuntimeException(String message, String reqUrl, Map<String, Object> reqParam, String responseStr) {
		super(message);
		this.reqUrl = reqUrl;
		this.reqParam = reqParam;
		this.responseStr = responseStr;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public Map<String, Object> getReqParam() {
		return reqParam;
	}

	public void setReqParam(Map<String, Object> reqParam) {
		this.reqParam = reqParam;
	}

	public String getResponseStr() {
		return responseStr;
	}

	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}

}
