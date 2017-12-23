package com.smi.am.service.vo;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券分类值对象")
public class CounponsTypeVo {
	
	@ApiModelProperty(value="优惠券分类ID")
	private Integer ctId;
	
	@NotEmpty
	@Length(min = 2, max = 20, message = "优惠券分类名称长度必须位于2到20个字符之间")  
	@ApiModelProperty(value="优惠券分类名称")
    private String ctCounponsname;

	@Pattern(regexp="^-?\\d+$",message="请输入数字")
	@NotEmpty
	@ApiModelProperty(value="优惠券分类类别")
    private String ctCounponstype;

	@ApiModelProperty(value="备注")
	@Length(min = 0, max = 100, message = "备注长度必须位于0到100个字符之间")  
    private String ctRemark;
	

	

	public Integer getCtId() {
		return ctId;
	}

	public void setCtId(Integer ctId) {
		this.ctId = ctId;
	}

	public String getCtCounponsname() {
        return ctCounponsname;
    }

    public void setCtCounponsname(String ctCounponsname) {
        this.ctCounponsname = ctCounponsname == null ? null : ctCounponsname.trim();
    }

    public String getCtCounponstype() {
        return ctCounponstype;
    }

    public void setCtCounponstype(String ctCounponstype) {
        this.ctCounponstype = ctCounponstype == null ? null : ctCounponstype.trim();
    }

    public String getCtRemark() {
        return ctRemark;
    }

    public void setCtRemark(String ctRemark) {
        this.ctRemark = ctRemark == null ? null : ctRemark.trim();
    }

}