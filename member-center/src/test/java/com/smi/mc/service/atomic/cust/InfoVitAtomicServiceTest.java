package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.InfoVitAtomicService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

public class InfoVitAtomicServiceTest extends BaseWebIntegrationTests {

	private final Logger LOGGER=LoggerUtils.getLogger(getClass());
	@Autowired
	private InfoVitAtomicService infoVitAtomicService;

	
	@Test
	public void qryInfoVit() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CUST_ID", "31010000000001");
			Map<String, Object> rep = infoVitAtomicService.qryInfoVit(param);
			LOGGER.info("inovoke qryInfoVit([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qrySumInfoVit() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CUST_ID", "31010000000001");
			Map<String,Object> rep = infoVitAtomicService.qrySumInfoVit(param);
			LOGGER.info("inovoke qrySumInfoVit([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addInfoVit() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("VIT_ID", "19991");
			param.put("CUST_ID", "31010000099991");
			param.put("ORG_ID", "20247");
			param.put("VIT_VALUE", "500");
			param.put("SERV_CODE", "109");
			param.put("STATUS_CD", "1000");
			Map<String,Object> rep = infoVitAtomicService.addInfoVit(param);
			LOGGER.info("inovoke addInfoVit([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateInfoVit() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("VIT_ID", "2028");
			param.put("STATUS_CD", "1100");
			Map<String,Object> rep = infoVitAtomicService.updateInfoVit(param);
			LOGGER.info("inovoke updateInfoVit([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void infoVitQry() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("VIT_VALUE", "1000");
			Map<String,Object> rep = infoVitAtomicService.infoVitQry(param);
			LOGGER.info("inovoke infoVitQry([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
