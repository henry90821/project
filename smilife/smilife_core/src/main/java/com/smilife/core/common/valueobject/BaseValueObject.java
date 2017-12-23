/**
 *
 */
package com.smilife.core.common.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 所有值对象的基类，定义一些公共属性。
 *
 * @author Andriy
 */
@JsonInclude(Include.NON_NULL)
@JsonSerialize
@ApiModel
public class BaseValueObject {

	/**
	 * 错误码，具体的错误类型(1:成功;2:服务器出错;3:用户未登录;4:用户资料不全;5:业务异常;6:未关注公众号)。<br>
	 * <span style='color:red;'>默认值为1即服务器内部错误。</span>
	 */
	@ApiModelProperty(value = "错误码，具体的错误类型(1:成功;2:服务器出错;3:用户未登录;4:用户资料不全;5:业务异常;6:未关注公众号)")
	protected Integer code;

	/**
	 * 错误消息，服务端响应的消息内容。
	 */
	@ApiModelProperty(value = "错误消息，服务端响应的消息内容")
	protected String msg = "";

	/**
	 * 接口标识，用来给客户端识别当前的服务器响应来自于哪个接口的请求。
	 */
	@ApiModelProperty(value = "接口标识，用来给客户端识别当前的服务器响应来自于哪个接口的请求")
	protected String interfaceIdentity;

	public BaseValueObject() {
	}

	public BaseValueObject(String interfaceIdentity) {
		this.interfaceIdentity = interfaceIdentity;
	}

	/**
	 * @param interfaceIdentity
	 *            the interfaceIdentity to set
	 */
	public void setInterfaceIdentity(String interfaceIdentity) {

		this.interfaceIdentity = interfaceIdentity;
	}

	/**
	 * 错误码，用来表示具体的错误类型。
	 *
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(CodeEnum code) {
		this.code = code.code();
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg.equals("") ? this.getErrorMsg(this.code) : msg;
	}

	/**
	 * 根据Code获得Message。
	 *
	 * @param code
	 * @return
	 */
	public String getErrorMsg(Integer code) {
		String result = null;
		for (int i = 0; i < CodeEnum.values().length; i++) {
			CodeEnum tmp = CodeEnum.values()[i];
			if (tmp != null && tmp.code() == code) {
				result = tmp.msg();
				break;
			}
		}
		return result;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the interfaceIdentity
	 */
	public String getInterfaceIdentity() {
		return interfaceIdentity;
	}

}
