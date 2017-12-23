package com.smi.mc.service.atomic.cust;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;

public class LogInterfaceTest extends BaseWebIntegrationTests {

	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;

	@Test
	public void addLogInterface() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("LOG_ID", "OrderStatusFeeFlag");
			param.put("REQUEST", "100");
			param.put("REQ_DATE", new Date());
			param.put("RESPONSE", "100");
			param.put("RES_DATE", new Date());
			param.put("REQ_SYSTEM", "100");
			param.put("RES_SYSTEM", "100");
			param.put("INTERFACE_CODE", "100");
			String re = logInterfaceAtomicService.addLogInterface(param);
			System.out.println(re);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
