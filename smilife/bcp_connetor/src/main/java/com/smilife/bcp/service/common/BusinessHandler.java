package com.smilife.bcp.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tydic.shop.dto.req.PubReq;

/**
 * 业务变更处理类
 * 
 * @author liz 2015-8-26
 */
public class BusinessHandler {

	private static final String PUB_REQ = "PUB_REQ";

	public static List<Map<String, Object>> handParam(Object obj, String type, String attKeyName) {
		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();

		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (null != type && !("").equals(type)) {
			PubReq pubReq = new PubReq();
			pubReq.setType(type);
			reqMap.put(PUB_REQ, pubReq);
		}
		reqMap.put(attKeyName, obj);

		params.add(reqMap);
		return params;
	}

}
