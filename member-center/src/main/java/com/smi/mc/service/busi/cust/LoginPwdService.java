package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface LoginPwdService {

	/**
	 * 密码验证
	 * 
	 * @param map
	 * @return
	 * @throws BusiServiceException
	 */
	public Map<String, Object> checkLoginPwd(Map<String, Object> map) throws BusiServiceException;

	/**
	 * 设置支付密码
	 * 
	 * @param param
	 * @return
	 * @throws BusiServiceException
	 */
	public Map<String, Object> setPayPwd(Map<String, Object> param) throws BusiServiceException;

	/**
	 * 是否设置了支付密码
	 * 
	 * @param param
	 * @return
	 * @throws BusiServiceException
	 */
	public Map<String, Object> isSetPayPwd(Map<String, Object> param) throws BusiServiceException;

	/**
	 * 重置/修改密码
	 * 
	 * @return
	 */
	public Map<String, Object> restOrUpdatePwd(Map<String, Object> param) throws BusiServiceException;
}