package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface CustRegService {

	/**
	 * 会员注册
	 * @param paramMap
	 * @return
	 * @throws BusiServiceException
	 */
	public Map<String, Object> custReg(Map<String, Object> paramMap) throws BusiServiceException;
}