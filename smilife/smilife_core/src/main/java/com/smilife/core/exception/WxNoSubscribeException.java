package com.smilife.core.exception;

import com.smilife.core.exception.SmiBusinessException;

/**
 * 未关注公证号异常
 * 
 * @author xzr
 * @date 2016/06/14
 */
public class WxNoSubscribeException extends SmiBusinessException{

	private static final long serialVersionUID = 1L;

	public WxNoSubscribeException(Throwable e) {
		super(e.getMessage(), e);
	}

	public WxNoSubscribeException(String message) {
		super(message);
	}
	

}
