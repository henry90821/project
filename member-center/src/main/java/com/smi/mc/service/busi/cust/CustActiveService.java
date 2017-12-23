package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface CustActiveService {

	/**
	 * 会员激活
	 * @param map
	 * @return
	 * @throws BusiServiceException
	 */
	public abstract Map<String,Object> activeCust(Map<String,Object> map) throws BusiServiceException;
}
