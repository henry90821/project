package com.smilife.core.exception;

public class NoLoginException extends SmiBusinessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoLoginException(Throwable e) {
		super(e.getMessage(), e);
	}

	public NoLoginException(String message) {
		super(message);
	}
	

}
