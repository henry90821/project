package com.smi.mc.service.atomic.cust;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.OrgOpAtomicService;

public class OrgOpTest extends BaseWebIntegrationTests {

	@Autowired
	private OrgOpAtomicService orgOpAtomicService;

	@Test
	public void querySysUId() {
		try {
			String re = orgOpAtomicService.querySysUId("20172");
			System.out.println(re);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
