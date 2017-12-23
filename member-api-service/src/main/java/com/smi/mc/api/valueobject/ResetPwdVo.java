package com.smi.mc.api.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * 重置会员密码值对象<br/>
 * Created by Andriy on 16/9/19.
 */
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "重置会员密码值对象")
public class ResetPwdVo extends BaseValueObject {
}
