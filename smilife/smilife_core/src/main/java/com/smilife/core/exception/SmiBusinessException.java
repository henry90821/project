package com.smilife.core.exception;

import com.smi.tools.kits.StrKit;

/**
 * SMI 自定义业务异常
 */
public class SmiBusinessException extends RuntimeException {

	private static final long serialVersionUID = 3570458085891996707L;

	public SmiBusinessException(Throwable e) {
		super(e.getMessage(), e);
	}

	public SmiBusinessException(String message) {
		super(message);
	}

	public SmiBusinessException(String messageTemplate, Object... params) {
		super(StrKit.format(messageTemplate, params));
	}

	public SmiBusinessException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public SmiBusinessException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrKit.format(messageTemplate, params), throwable);
	}
}
