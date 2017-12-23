package com.smilife.core.sms.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.sms.common.SmsConstant;

/**
 * 短信信息vo
 * 
 * @author xzr
 * @date 2016/03/28
 */
@JsonSerialize
public class SmsVo extends BaseValueObject {

	/**
	 * 错误码，来自于业务系统
	 */
	@JSONField(name = "code")
	private Integer sysCode;

	private String reqNo; // 序列号

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public Integer getSysCode() {
		return sysCode;
	}

	public void setSysCode(Integer sysCode) {
		this.sysCode = sysCode;

		/**
		 * 重写父类的值
		 */
		// 成功
		if (SmsConstant.SEND_SUCCESS_CODE == sysCode) {
			super.setCode(CodeEnum.SUCCESS);
		} else {// 失败
			super.setCode(CodeEnum.REQUEST_ERROR);
		}
	}

}
