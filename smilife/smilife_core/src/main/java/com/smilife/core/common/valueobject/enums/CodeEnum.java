package com.smilife.core.common.valueobject.enums;

import com.smilife.core.exception.SmiBusinessException;

/**
 * Created by Andriy on 15/12/28.
 */
public enum CodeEnum {

	/**
	 * 占位符,无具体业务使用意义.
	 */
	PLACEHOLDER(0, "占位符,无具体业务使用意义."),

	/**
	 * 成功！
	 */
	SUCCESS(1, "成功！"),

	/**
	 * 服务器内部错误！
	 */
	SERVER_INNER_ERROR(2, "服务器出错了，请稍后重试！"),

	/**
	 * 用户未登录！
	 */
	NOT_LOGGED_IN(3, "用户未登录或登录失效！"),

	/**
	 * 用户资料未补全！
	 */
	PROFILE_MISSING(4, "用户资料不完整！"),

	/**
	 * 请求错误！
	 */
	REQUEST_ERROR(5, "业务异常！"),
	
	/**
	 * 微信用户未关注公众号！
	 */
	NO_SUBSCRIBE_ERROR(6, "请关注公众号！");

	private Integer code;
	private String msg;

	CodeEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer code() {
		return code;
	}

	public String msg() {
		return msg;
	}

	/**
	 * 根据传入的数值获得相应的枚举值
	 * 
	 * @param sourceCode
	 *            需要传入的数值
	 * @return 返回数值对应的枚举值对象
	 * @throws SmiBusinessException
	 *             如果传入的数值为空或者无法匹配的合法的枚举值则抛出异常
	 */
	public static CodeEnum getCodeEnum(Integer sourceCode) throws SmiBusinessException {
		CodeEnum result = null;

		if (null == sourceCode)
			throw new SmiBusinessException("参数sourceCode不能为NULL!");

		for (CodeEnum enumObj : CodeEnum.values()) {
			if (enumObj.code().equals(sourceCode))
				result = enumObj;
		}

		if (null == result) {
			throw new SmiBusinessException("无法匹配到合法的枚举值!");
		} else if (PLACEHOLDER.equals(result)) {
			throw new SmiBusinessException("不能使用占位符枚举值!");
		}
		return result;
	}
}
