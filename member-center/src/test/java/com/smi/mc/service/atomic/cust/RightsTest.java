package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.RightsAtomicService;

public class RightsTest extends BaseWebIntegrationTests {

	@Autowired
	private RightsAtomicService rightsAtomicService;

	@Test
	public void qryPara() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RIGHT_TYPE_ID", "1100");
			Map<String, Object> maps = rightsAtomicService.qryRights(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
