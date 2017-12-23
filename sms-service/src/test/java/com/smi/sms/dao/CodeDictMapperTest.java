package com.smi.sms.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.sms.model.CodeDict;

public class CodeDictMapperTest extends BaseMapperTest {

	@Autowired
	CodeDictMapper codeDictMapper ;
	
	@Test
	public void testListAll() {
		
		List<CodeDict> list = codeDictMapper.listAll();
		
		this.printInfo(list);
	}
}
