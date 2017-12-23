package com.smi.mc.service.busi.bill.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.billing.SmsInfoMapper;
import com.smi.mc.service.busi.bill.ISmsInfoService;

@Service
public class SmsInfoServiceImpl implements ISmsInfoService{

	@Autowired
	private SmsInfoMapper smsInfoMapper;


	@Override
	public int smsInsert(java.util.Map<String, Object> param) throws Exception {
		return smsInfoMapper.smsSendInsert(param);
	}
	

	

	
	
}
