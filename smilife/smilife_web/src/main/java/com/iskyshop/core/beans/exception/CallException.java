package com.iskyshop.core.beans.exception;

public class CallException extends RuntimeException {

	private ExceptionCode code;

	public CallException() {
		super();
	}

	public CallException(String msg) {
		super(msg);
	}

	public CallException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CallException(Throwable cause) {
		super(cause);
	}

	public CallException(String msg, Throwable cause, ExceptionCode code) {
		super(msg, cause);
		this.code = code;
	}

	public CallException(Throwable cause, ExceptionCode code) {
		super(code.message, cause);
		this.code = code;
	}

	public ExceptionCode getCode() {
		return code;
	}

	public void setCode(ExceptionCode code) {
		this.code = code;
	}

	enum ExceptionCode {

		/**
		 * 请求服务超时
		 */
		TIME_OUT(408, "请求服务超时！"),

		/**
		 * 请求服务超时
		 */
		REQUEST_FAIL(500, "请求服务超时！");

		private int code;
		private String message;

		ExceptionCode(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}
