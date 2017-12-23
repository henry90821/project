package com.smilife.core.exception;

/**
 * 公用参数异常类<br/>
 * Created by Andriy on 16/5/10.
 */
@SuppressWarnings("serial")
public class SmiParamException extends RuntimeException {

	/**
	 * 异常参数名称
	 */
	private String paramName;

	public SmiParamException() {
		super();
	}

	public SmiParamException(String paramName, String message) {
		super(message);
		this.paramName = paramName;
	}

	public SmiParamException(String paramName, String message, Throwable cause) {
		super(message, cause);
		this.paramName = paramName;
	}

	public SmiParamException(String paramName, Throwable cause) {
		super(cause);
		this.paramName = paramName;
	}

	protected SmiParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * 异常参数名称
	 */
	public String getParamName() {
		return paramName;
	}
}
