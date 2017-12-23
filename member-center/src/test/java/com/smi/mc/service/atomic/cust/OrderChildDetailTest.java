package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.OrderChildDetailAtomicService;

public class OrderChildDetailTest extends BaseWebIntegrationTests {

	@Autowired
	private OrderChildDetailAtomicService orderChildDetailAtomicService;

	@Test
	public void qryOrderChildDetail() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_CHILD_ID", "12");
			Map<String, Object> maps = orderChildDetailAtomicService.qryOrderChildDetail(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addOrderChildDetail() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_CHILD_DETAIL_ID", "99999");
			param.put("ORDER_CHILD_ID", "1000");
			param.put("OBJ_TYPE", "1000");
			param.put("OBJ_CODE", "1000");
			param.put("ENTITY_CODE", "1000");
			param.put("BEFORE_VALUE", "1000");
			param.put("AFTER_VALUE", "1000");
			
			Map<String, Object> maps = orderChildDetailAtomicService.addOrderChildDetail(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
