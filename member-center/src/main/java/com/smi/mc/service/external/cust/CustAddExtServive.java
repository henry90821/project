package com.smi.mc.service.external.cust;

import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;


@MonitoredWithSpring
public interface CustAddExtServive {

	/**
	 * 会员资料新增对外接口(满天星接口)
	 * @param param
	 * @return
	 */
	public Map<String,Object> addCustByMTXExt(String paramJson) throws Exception;
	
}
