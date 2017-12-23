package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.InfoOrderAtomicService;

public class InfoOrderAtomicServiceTest extends BaseWebIntegrationTests {

	@Autowired
	private InfoOrderAtomicService infoOrderAtomicService;

	@Test
	public void addInfoOrder() {
		try {
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("ORDER_ID", "11");
			requestMap.put("CUST_ID", "11");
			requestMap.put("ORDER_TYPE", "11");
			requestMap.put("SYSTEM_USER_ID", "11");
			requestMap.put("ORG_ID", "11");
			requestMap.put("STATUS_CD", "110");
			requestMap.put("SYSTEM_ID", "11");
			requestMap.put("EXT_ORDER_ID", "11");
			requestMap.put("BUSINESS_TYPE", "11");
			requestMap.put("SERV_CODE", "11");
			requestMap.put("DEVICE_NUMBER", "11");
			requestMap.put("REGION_ID", "11");
			requestMap.put("PAY_STATUS", "11");
			requestMap.put("ORDER_DESC", "11");
			Map<String, Object> maps = infoOrderAtomicService.addInfoOrder(requestMap);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
