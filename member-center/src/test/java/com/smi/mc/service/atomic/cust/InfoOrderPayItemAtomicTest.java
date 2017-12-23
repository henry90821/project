package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.InfoOrderPayItemAtomicService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

public class InfoOrderPayItemAtomicTest extends BaseWebIntegrationTests {

	private final Logger LOGGER=LoggerUtils.getLogger(getClass());
	@Autowired
	private InfoOrderPayItemAtomicService infoOrderPayItemAtomicService;

	
	@Test
	public void addInfo() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_PAY_ITEM_ID", "888");
			param.put("ORDER_ID", "888");
			param.put("PAY_SERIAL", "888");
			param.put("PAY_TYPE", "1");
			param.put("PAY_AMOUNT", "10");
			param.put("DEDU_AMOUNT", "10");
			param.put("EXT_PAY_SERIAL", "12");
			boolean flag = infoOrderPayItemAtomicService.addInfo(param);
			LOGGER.info("inovoke addInfo([{}]) result: {}",param,flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void qryInfo() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_ID", "731");
			Map<String,Object> rep = infoOrderPayItemAtomicService.qryInfo(param);
			LOGGER.info("inovoke qryInfo([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
