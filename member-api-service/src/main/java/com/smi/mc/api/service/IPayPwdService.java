package com.smi.mc.api.service;

import java.util.Map;

import com.smi.mc.api.valueobject.CustVo;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.exception.SmiBusinessException;

public interface IPayPwdService {
 
	public DataVo<CustVo> verifyPayPwd(Map<String,Object> param) throws SmiBusinessException;

	public BaseValueObject judgeIsSetPayPwd(Map<String,Object> param) throws SmiBusinessException;
	
	public BaseValueObject settingPayPwd(Map<String,Object> param) throws SmiBusinessException;
	
	public BaseValueObject updateLoginPwd(Map<String,Object> param) throws SmiBusinessException;

	public BaseValueObject restLoginPwd(Map<String,Object> param)  throws SmiBusinessException;

}
