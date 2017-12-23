package com.smi.mc.utils;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 公共请求参数-请求类型
 * 
 * @author xzr
 * @date 2016/03/11
 */
public class PubReq implements Serializable {

	private static final long serialVersionUID = 3776275914108877692L;
	
	@JSONField(name = "TYPE")
	private String type; // 类型

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
