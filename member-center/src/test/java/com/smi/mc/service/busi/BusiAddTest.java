package com.smi.mc.service.busi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.SystemParaAtomicService;
import com.smi.mc.service.busi.cust.BusiAddService;

public class BusiAddTest extends BaseWebIntegrationTests {

	@Autowired
	private BusiAddService busiAddService;

	@Test
	public void busiAddService() {
		try {
			//封装报文
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("CHANNEL_CODE", "10002");
			param.put("SYSTEM_USER_ID", "102");
			param.put("EXT_SYSTEM", "120");
			param.put("CUST_ID", "31010000000094");
			param.put("SERV_CODE", "107");
			param.put("EXT_ORDER_ID", "");
			
			Map<String,Object> busiParam = new HashMap<String,Object>();
			busiParam.put("OFFER_ID", "1004");
			busiParam.put("EFF_DATE", "20160601");
			busiParam.put("EXP_DATE", "20160630");
			busiParam.put("BUSI_NBR", "13113608995");
			
			List<Map<String,Object>> busiList = new ArrayList<Map<String,Object>>();
			busiList.add(busiParam);
			param.put("BUSI", busiList);
			
			Map<String, Object> maps = busiAddService.addBusi(param);
			System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
