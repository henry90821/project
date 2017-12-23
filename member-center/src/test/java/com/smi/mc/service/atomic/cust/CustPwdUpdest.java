/*package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.po.RESP;
import com.smi.mc.service.busi.cust.CustPwdUpdService;

public class CustPwdUpdest extends BaseWebIntegrationTests {

	@Autowired
	private CustPwdUpdService custPwdUpdService;

	@Test
	public void modifyAndResetCustPwd() {
		try {
			Map<String, Object> contMap = new HashMap<String, Object>();
			
			contMap.put("EXT_SYSTEM","120");
	        contMap.put("CHANNEL_CODE","10020");
	        contMap.put("CUST_ID","31000000335304");
	        contMap.put("NEW_PWD","123");
	        contMap.put("OLD_PWD","123");
	        contMap.put("PWD_TYPE","2");
	        contMap.put("RESET_TYPE","1");
	        
	        Map<String, Object> contMap1 = new HashMap<String, Object>();
	        contMap1.put("CUST", contMap);
	        
			Map<String, Object> maps = custPwdUpdService.modifyAndResetCustPwd(contMap1);
			System.out.println(((RESP)maps.get("RESP")).getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
*/