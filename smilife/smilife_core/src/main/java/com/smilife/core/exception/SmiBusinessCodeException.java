package com.smilife.core.exception;

import com.smi.tools.kits.StrKit;
import com.smilife.core.utils.MessageSourceUtil;

public class SmiBusinessCodeException extends SmiBusinessException {

	private static final long serialVersionUID = 1L;

	private String msgkey;
	private Object[] params;

	private String msgValue;

	public SmiBusinessCodeException(Throwable e) {
		super(e.getMessage(), e);
	}

	public SmiBusinessCodeException(String msgkey) {
		super(msgkey);
		this.msgkey = msgkey;
	}

	public SmiBusinessCodeException(String msgkey, Object[] params) {
		super(StrKit.format(msgkey, params));
		this.msgkey = msgkey;
		this.params = params;
	}

	public SmiBusinessCodeException(String msgkey, Throwable throwable) {
		super(msgkey, throwable);
		this.msgkey = msgkey;
	}

	public SmiBusinessCodeException(Throwable throwable, String msgkey, Object... params) {
		super(StrKit.format(msgkey, params), throwable);
		this.msgkey = msgkey;
		this.params = params;
	}

	@Override
	public String getMessage() {
		return getMessage4Client();
	}

	public String getMessageSuper() {// 原始的getMessage处理
		return super.getMessage();
	}

	private String getMessage4Client() {
		if (msgValue == null) {
			if (StrKit.isEmpty(msgkey)) {// Default msg
				msgValue = MessageSourceUtil.getMessage(DEFAULT_CODE);
			} else {
				msgValue = MessageSourceUtil.getMessage(msgkey, params);
				if (StrKit.isEmpty(msgValue)) {
					msgValue = msgkey;// 未配置则取原始key
					// msgValue = MessageSourceUtil.getMessage(DEFAULT_CODE);//未配置则取默认错误信息
					// msgValue = super.getMessage();
				}
			}
		}
		return msgValue;
	}

	public static final String DEFAULT_CODE = "comm.default";

}
