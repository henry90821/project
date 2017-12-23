package com.smi.mc.service.atomic.billing;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.dao.billing.SmsInfoMapper;

public class SmsInfoTest extends BaseWebIntegrationTests {

	@Autowired
	private SmsInfoMapper smsInfoMapper;

	@Test
	public void smsSendInsert() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("MSG_ID", "10086");
			param.put("MSISDN_SEND", "nimei");
			param.put("MSISDN_RECEIVE", "10023");
			param.put("PRIORITY", "0");
			param.put("MESSAGE_TEXT", "|aoc.dic.voiceafteruse|2015|6|30|22|33");
			param.put("RETRY_TIMES", "0");
			
			int re = smsInfoMapper.smsSendInsert(param);
			System.out.println(re);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
