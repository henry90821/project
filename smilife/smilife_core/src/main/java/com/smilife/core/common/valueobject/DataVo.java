package com.smilife.core.common.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 新版数据值对象，常用于WEB接口的返回值中<br/>
 * Created by Andriy on 16/9/19.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonSerialize
@ApiModel
public class DataVo<T> extends BaseValueObject {

	/**
	 * 接口中业务数据结构载体，所有的业务数据结构都放在该属性中
	 */
	@ApiModelProperty(value = "接口中业务数据结构载体，所有的业务数据结构都放在该属性中")
	protected T data;

	/**
	 * 接口中业务数据结构载体，所有的业务数据结构都放在该属性中
	 */
	public T getData() {
		return data;
	}

	/**
	 * 接口中业务数据结构载体，所有的业务数据结构都放在该属性中
	 */
	public void setData(T data) {
		this.data = data;
	}
}
