package com.smi.mc.service.busi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.busi.cust.CustAddService;
import com.smi.mc.service.busi.cust.CustQryService;

public class CustAddServiceTest extends BaseWebIntegrationTests {

	@Autowired
	private CustAddService custAddService;
	
	@Autowired
	private CustQryService custQryService;

	@Test
	public void custAddService() {
		try {
			//封装报文
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("CHANNEL_CODE", "10002");
			param.put("EXT_SYSTEM", "120");
			param.put("CONTACT_MOBILE", "13113608991");
			param.put("PAY_PWD", "123456");
			param.put("CUST_NAME", "cd99");
			param.put("SEX", "1000");
			param.put("CERTI_TYPE", "10");
			param.put("CERTI_NBR", "440882199008217636");
			param.put("CERTI_ADDR", "纽约苹果大厦");
			param.put("CARD_NAME", "随影卡会员");
			param.put("EFF_DATE", "2016-06-25");
			param.put("EXP_DATE", "2019-06-25");
			param.put("CARD_NBR", "4444444");
			
			Map<String, Object> maps = custAddService.addCustByMTX(param);
			System.out.println(maps.get("MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getCustIdByMobileId(){
		List<Map<String, Object>> custIdByloginMobile = custQryService.getCustIdByloginMobile("10000024089,17100000836,19087865631");
		System.out.println(custIdByloginMobile.size()+"-----------------------------------------------");
		for (Map<String, Object> map : custIdByloginMobile) {
			System.out.println(map.get("CUST_ID"));
			System.out.println(map.get("LOGIN_USER"));
		}
	}
	
}