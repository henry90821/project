package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.SystemParaAtomicService;

public class SystemParamTest extends BaseWebIntegrationTests {

	@Autowired
	private SystemParaAtomicService systemParaAtomicService;

	@Test
	public void qryPara() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("PARA_TYPE", "OrderStatusFeeFlag");
			param.put("PARA_CODE", "100");
			Map<String, Object> maps = systemParaAtomicService.qryPara(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryCodeList() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("TAB_NAME", "INFO_ORDER");
			param.put("STATUS_CD", "1000");
			Map<String, Object> maps = systemParaAtomicService.qryCodeList(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
