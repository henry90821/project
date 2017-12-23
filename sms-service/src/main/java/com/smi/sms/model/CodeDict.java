package com.smi.sms.model;

public class CodeDict {
    private Integer id;

    private String code;
    
    /**
     * 字典类型：1 短信发送 梦网的返回代码  	2 使用短信服务的渠道编码 	3 短信发送 网景的返回代码 	4短信通道编码
     */
    private int type;

    private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}