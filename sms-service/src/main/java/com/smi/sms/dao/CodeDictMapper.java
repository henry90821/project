package com.smi.sms.dao;

import java.util.List;

import com.smi.sms.model.CodeDict;

public interface CodeDictMapper {

	/**
	 * 查询所有的字典编码
	 * 
	 * @return 字典编码列表
	 */
	List<CodeDict> listAll();

}