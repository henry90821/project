package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.InfoRandomCodeAtomicService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

public class InfoRandomCodeAtomicTest extends BaseWebIntegrationTests {

	private final Logger LOGGER=LoggerUtils.getLogger(getClass());
	@Autowired
	private InfoRandomCodeAtomicService infoRandomCodeAtomicService;

	
	@Test
	public void qryRandomCode() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RANDOM_CODE", "797291");
			param.put("PAGE_INDEX", "1");
			param.put("PAGE_SIZE", "5");
			Map<String, Object> rep = infoRandomCodeAtomicService.qryRandomCode(param);
			LOGGER.info("inovoke qryRandomCode([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addRandomCode() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RANDOM_CODE_ID", "9991");
			param.put("CODE_TYPE", "20");
			param.put("RANDOM_CODE", "199999");
			param.put("APPLICANT_TYPE", "2000");
			param.put("APPLICANT_ID", "15842365932");
			param.put("STATUS_CD", "1100");
			param.put("DEV_NUM", "731");
			Map<String,Object> rep = infoRandomCodeAtomicService.addRandomCode(param);
			LOGGER.info("inovoke addRandomCode([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updRandomCode() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RANDOM_CODE_ID", "9991");
			param.put("APPLICANT_ID", "13410927632");
			Map<String,Object> rep = infoRandomCodeAtomicService.updRandomCode(param);
			LOGGER.info("inovoke updRandomCode([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updRandomCode1() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("APPLICANT_ID", "15842365932");
			param.put("STATUS_CD", "1000");
			Map<String,Object> rep = infoRandomCodeAtomicService.updRandomCode1(param);
			LOGGER.info("inovoke updRandomCode([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
