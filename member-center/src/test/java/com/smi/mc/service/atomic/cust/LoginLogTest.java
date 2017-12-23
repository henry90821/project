package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.LoginLogAtomicService;

public class LoginLogTest extends BaseWebIntegrationTests {

	@Autowired
	private LoginLogAtomicService loginLogAtomicService;

	@Test
	public void addLogInterface() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("LOGIN_LOG_ID", "9999");
			param.put("LOGIN_USER_ID", "100");
			param.put("ORG_ID", "100");
			param.put("MAC_ADDRESS", "100");
			Map<String,Object> map = loginLogAtomicService.addLoginLog(param);
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
