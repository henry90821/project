package com.smi.mc.service.atomic.cust;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.PayAtomicService;

public class PayTest extends BaseWebIntegrationTests {

	@Autowired
	private PayAtomicService payAtomicService;

	@Test
	public void addInfoPay() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("pay_id", "9999");
			param.put("cust_id", "100");
			param.put("eff_date", new Date());
			param.put("exp_date", new Date());
			param.put("prepay_flag", "1");
			Map<String, Object> maps = payAtomicService.addInfoPay(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updInfoPay() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("pay_id", "9999");
			param.put("cust_id", "100");
			param.put("eff_date", new Date());
			param.put("exp_date", new Date());
			param.put("prepay_flag", "1");
			Map<String, Object> maps = payAtomicService.updInfoPay(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryInfoPay() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("pay_id", "9999");
			param.put("cust_id", "100");
			Map<String, Object> maps = payAtomicService.qryInfoPay(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
