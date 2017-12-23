package com.smi.am.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.am.common.BaseWebIntegrationTests;

public class CustQryControllerTest extends BaseWebIntegrationTests{
	
	@Autowired
	CustListQryController custListQryController;
	
	@Test
	public void test(){
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("custName", "");
//		paramMap.put("custId", "20150000675692");
//		paramMap.put("orgId", "");
//		paramMap.put("contactMobile", "10002023926");
		String custName="";
		String custId="20150000675692";
		String orgId="";
		String contactMobile="10002023926";
//		try {
////			custListQryController.custQryList(custName,custId,orgId,contactMobile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
