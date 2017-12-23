package com.smi.sms.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.sms.model.SmsAccount;

public class SmsAccountTest extends BaseMapperTest {

	@Autowired
	SmsAccountMapper smsAccountMapper;
	
	@Test
	public void testListAll() {
		List<SmsAccount> list = smsAccountMapper.listAll();
		
		this.printInfo(list);
	}
}
