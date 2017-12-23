package com.smi.mc.service.atomic.cust;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.InfoWXUserAtomicService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

public class InfoWXUserAtomicServiceTest extends BaseWebIntegrationTests{
	private final Logger LOGGER=LoggerUtils.getLogger(getClass());
	@Autowired
	private InfoWXUserAtomicService infoWXUserAtomicService;

	
	@Test
	public void addInfoWXUser() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("WX_USER_ID", "99999999");
			param.put("STATUS_CD", "10");
			param.put("CUST_ID", "31010000000001");
			param.put("OPEN_ID", "9999777888");
			param.put("WX_CODE", "0000");
			param.put("USER_MOBILE", "13510567863");
			param.put("CRT_DATE", new Date());
			param.put("ON_CARE_DATE", new Date());
			int rep = infoWXUserAtomicService.addInfoWXUser(param);
			LOGGER.info("inovoke addInfoWXUser([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void queryInfoWXUser() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CUST_ID", "31010000000099");
			Map<String,Object> rep = infoWXUserAtomicService.queryInfoWXUser(param);
			LOGGER.info("inovoke queryInfoWXUser([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateStateByWXUserId() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("WX_USER_ID", "99999999");
			param.put("STATUS_CD", "20");
			int rep = infoWXUserAtomicService.updateStateByWXUserId(param);
			LOGGER.info("inovoke updateStateByWXUserId([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateCustIdByWXUserId() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("WX_USER_ID", "99999999");
			param.put("USER_MOBILE", "13410956897");
			int rep = infoWXUserAtomicService.updateCustIdByWXUserId(param);
			LOGGER.info("inovoke updateCustIdByWXUserId([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void getInfoWXUserId() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			Map<String,Object> rep = infoWXUserAtomicService.getInfoWXUserId(param);
			LOGGER.info("inovoke getInfoWXUserId([{}]) result: {}",param,rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
