package com.smi.mc.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 用于测试接口返回的VO对象<br/>
 * Created by Andriy on 16/5/20.
 */
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "用于测试接口返回的VO对象")
public class TestVO extends BaseValueObject {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 名称
     */
    public void setName(String name) {
        this.name = name;
    }
}
