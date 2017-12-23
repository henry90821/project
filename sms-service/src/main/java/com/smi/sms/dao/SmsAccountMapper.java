package com.smi.sms.dao;

import java.util.List;

import com.smi.sms.model.SmsAccount;

public interface SmsAccountMapper {

	/**
	 * 查询所有的记录
	 * 
	 * @return 记录列表
	 */
	List<SmsAccount> listAll();

}