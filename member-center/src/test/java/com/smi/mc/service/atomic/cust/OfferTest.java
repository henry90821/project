package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.OfferAtomicService;

public class OfferTest extends BaseWebIntegrationTests {

	@Autowired
	private OfferAtomicService offerAtomicService;

	@Test
	public void qryOffer() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("STATUS_CD", "1000");
			Map<String, Object> maps = offerAtomicService.qryOffer(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryCodeAttr() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("STATUS_CD", "1000");
			Map<String, Object> maps = offerAtomicService.qryCodeAttr(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryCodeAttrValue() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ATTR_ID", "1");
			Map<String, Object> maps = offerAtomicService.qryCodeAttrValue(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
