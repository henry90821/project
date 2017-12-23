package com.smi.mc.service.busi.cust;

import java.util.Map;


import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface CustAddService {

	/**
	 * 会员资料新增(满天星接口)
	 * @param param
	 * @return
	 */
	public Map<String,Object> addCustByMTX(Map<String,Object> param) throws Exception;
	
	
}
