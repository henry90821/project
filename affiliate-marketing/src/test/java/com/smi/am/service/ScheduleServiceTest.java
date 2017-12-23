package com.smi.am.service;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.am.common.BaseWebIntegrationTests;

public class ScheduleServiceTest  extends BaseWebIntegrationTests{

	@Autowired
	private IScheduleService iScheduleService;
	
	@Test
	public void updateAllUnAuditCoupons(){
		iScheduleService.updateAllUnAuditCoupons();
	}
	
	@Test
	public void updateAllUnDeliveryCoupons(){
		iScheduleService.updateAllUnDeliveryCoupons();
	}
	
	
}
