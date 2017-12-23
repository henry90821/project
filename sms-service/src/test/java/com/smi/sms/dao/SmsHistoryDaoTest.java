package com.smi.sms.dao;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.sms.model.SmsHistory;

public class SmsHistoryDaoTest extends BaseMapperTest {

	@Autowired
	SmsHistoryMapper smsHistoryMapper;
	
	@Test
	public void testSave() {
		SmsHistory sms = new SmsHistory();
		
		sms.setAccountType(1);
		sms.setContent("你的応兴扬州吗为12233");
		sms.setCreateTime(new Date());
		sms.setPhoneNo("15012552356");
		sms.setReqNo("2016032573455238IQWE");
		sms.setStatusCode("0");
		
		smsHistoryMapper.save(sms);
	}
	
}
