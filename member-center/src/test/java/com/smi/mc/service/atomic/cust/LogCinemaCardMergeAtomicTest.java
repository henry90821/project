package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.LogCinemaCardMergeAtomicService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

public class LogCinemaCardMergeAtomicTest extends BaseWebIntegrationTests {

	private final Logger LOGGER=LoggerUtils.getLogger(getClass());
	@Autowired
	private LogCinemaCardMergeAtomicService logCinemaCardMergeAtomicService;

	
	@Test
	public void addLogCinemaCardMerge() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_ID", "888");
			param.put("CUST_ID", "888");
			param.put("OLD_CARD_NBR", "888");
			param.put("BALANCE", "1");
			param.put("SCORE", "10");
			param.put("OPER_ID", "10");
			param.put("EXT_SYS_ID", "12");
			param.put("MESSAGE", "12");
			param.put("RESULT_FALG", "12");
			param.put("DEPT_NO", "20238");
			Map<String,Object> flag= logCinemaCardMergeAtomicService.addLogCinemaCardMerge(param);
			LOGGER.info("inovoke addLogCinemaCardMerge([{}]) result: {}",param,flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
