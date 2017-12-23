package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券分类类别信息值对象")
public class PreferentialtypeVo {
	
	@ApiModelProperty(value="优惠券分类类别ID")
    private Integer ptId;

	@ApiModelProperty(value="优惠券分类类别名称")
    private String ptTypename;

    public Integer getPtId() {
        return ptId;
    }

    public void setPtId(Integer ptId) {
        this.ptId = ptId;
    }

    public String getPtTypename() {
        return ptTypename;
    }

    public void setPtTypename(String ptTypename) {
        this.ptTypename = ptTypename == null ? null : ptTypename.trim();
    }
}