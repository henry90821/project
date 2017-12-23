package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.dao.cust.CodeCardMapper;
import com.smi.mc.dao.cust.CodeListMapper;
import com.smi.mc.dao.cust.CustBusiInfoMapper;

public class CodeCardTest extends BaseWebIntegrationTests {

	@Autowired
	private CodeCardMapper codeCardMapper;
	
	@Autowired
	private CodeListMapper codeListMapper;
	
	@Autowired
	private CustBusiInfoMapper custBusiInfoMapper;
	

	@Test
	public void getOfferId() {
		try {
			Map<String, Object> contMap = new HashMap<String, Object>();
			contMap.put("CARD_NAME","星美生活会员");
	        Map<String,Object> maps = codeCardMapper.getOfferId(contMap);
	        System.out.println(maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getOfferList() {
		try {
			Map<String, Object> contMap = new HashMap<String, Object>();
			contMap.put("CHANNEL_CODE","009006");
	        String oo = codeListMapper.qryCodeList(contMap);
	        System.out.println("oo :" + oo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryCustBusiInfo() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CUST_ID", "31000000000269");
			param.put("STATUS_CD", "1000");
			param.put("OFFER_ID", "1002");
			List<Map<String, Object>> qryCustBusiInfo = custBusiInfoMapper.qryCustBusiInfo(param);
			System.out.println("qryCustBusiInfo :"+ qryCustBusiInfo.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
}
