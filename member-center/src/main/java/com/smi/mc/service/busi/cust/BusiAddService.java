package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface BusiAddService {

	/**
	 * 会员业务服务受理
	 * @param map
	 * @return
	 * @throws BusiServiceException
	 */
	public abstract Map<String, Object> addBusi(Map<String, Object> map) throws BusiServiceException;
}
