package com.smilife.bcp.service.common;

import java.util.HashMap;
import java.util.Map;

import com.tydic.shop.dto.req.PubReq;

/**
 * 业务变更处理类
 * 
 * @author xzr 2015-10-08
 */
public class BusinessHandler2 {
	
	private static final String PUB_REQ = "PUB_REQ";

	
	public Map<String, Object> handParam(Object obj, String type, String attKeyName) {
		PubReq pubReq = new PubReq();
		pubReq.setType(type);
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(PUB_REQ, pubReq);
		reqMap.put(attKeyName, obj);
		return reqMap;
	}

}
