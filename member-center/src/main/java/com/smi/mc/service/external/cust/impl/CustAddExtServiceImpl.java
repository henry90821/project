package com.smi.mc.service.external.cust.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.po.RESP;
import com.smi.mc.service.busi.cust.CustAddService;
import com.smi.mc.service.external.cust.CustAddExtServive;
import com.smi.mc.utils.JsonUtils;
import com.smi.mc.utils.SooHandler;
import com.smi.tools.kits.JsonKit;

@Service("custAddExtService")
public class CustAddExtServiceImpl implements CustAddExtServive{

	@Autowired
	private CustAddService custAddService; 
	
	/**
	 * 报文json格式
	 * { 'SOO': [ { 'PUB_REQ': { 'TYPE': 'CUST_ADD' }, 'CUST': { 'CHANNEL_CODE': '10002', 'EXT_SYSTEM': '120', 'CONTACT_MOBILE': '13113608991', 'PAY_PWD': '123456', 'CUST_NAME': 'cdyy', 'SEX': '1000', 'CERTI_TYPE': '10', 'CERTI_NBR': '440882199008217636', 'CERTI_ADDR': '纽约苹果大厦', 'CARD_NAME': '随影卡会员', 'EFF_DATE': '2016-06-25', 'EXP_DATE': '2019-06-25', 'CARD_NBR': '4444444' } } ] }
	 */
	@Override
	public Map<String, Object> addCustByMTXExt(String paramJson) throws Exception {
		Map<String,Object> resMap = new HashMap<String, Object>();
		//解析请求报文
		String paramSOO = SooHandler.parseJson(paramJson);
		Map<String,Object> msgMap = JsonUtils.parserToMap(paramSOO);
		String pub_req = JsonKit.toJsonString(msgMap.get("PUB_REQ"));
		if(pub_req == null){
			resMap.put("RESP", RESP.createFailResp("1", "报文格式错误，报文中未找到pub_req"));
			return resMap;
		}
		
		Map<String,Object> pubReqMap = JsonUtils.parserToMap(pub_req);
		
		if(pubReqMap.get("TYPE") == null ){
			resMap.put("RESP", RESP.createFailResp("1", "报文格式错误，报文中未找到TYPE：CUST_ADD"));
			return resMap;
		}
		
		if(msgMap.get("CUST") == null ){
			resMap.put("RESP", RESP.createFailResp("1", "报文格式错误，报文中未找到CUST"));
			return resMap;
		}
		
		String custJson = JsonKit.toJsonString(msgMap.get("CUST"));
		Map<String,Object> custMap = JsonUtils.parserToMap(custJson);
		if(custMap != null){
			resMap = custAddService.addCustByMTX(custMap);
		}else{
			resMap.put("RESP", RESP.createFailResp("1", "报文格式错误，报文中未找到CUST"));
		}
		
		return resMap;
	}

	
	
}
