package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.OrderFeeItemAtomicService;

public class OrderFeeItemAtomicTest extends BaseWebIntegrationTests {

	@Autowired
	private OrderFeeItemAtomicService orderFeeItemAtomicService;

	@Test
	public void qryOrderFeeItem() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("PAGE_INDEX", "1");
			param.put("PAGE_SIZE", "10");
			Map<String, Object> maps = orderFeeItemAtomicService.qryOrderFeeItem(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
