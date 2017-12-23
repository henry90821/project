package com.smi.mc.service.busi.bill;


import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface ISmsInfoService {
	
	int smsInsert(Map<String,Object> param) throws Exception;
	
}
