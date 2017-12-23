package com.smi.mc.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.smi.mc.po.SOO;
import com.smi.mc.po.SvcCont;

import net.sf.json.JSONObject;

public class SOOUtils {


	@SuppressWarnings("unchecked")
	public static SOO createResponseSOO(String type) {
		SOO soo = new SOO(type, "PUB_RES");

		if (soo.getPageIndex() == -1 || soo.getPageSize() == -1) {
			Map<String, Object> head = (Map<String, Object>) soo.get("PUB_RES");
			head.remove("PAGE_SIZE");
			head.remove("PAGE_INDEX");
			head.remove("COUNT_TOTAL");
		}
		return soo;
	}
	

	public static SOO createSOO(String type) {
		SOO soo = new SOO();
		Map<Object, Object> head = new HashMap<Object, Object>();
		head.put("TYPE", type);
		soo.put("PUB_RES", head);
		return soo;
	}
	
	public static SOO createResponseSOO(String type,String id) {
		SOO soo = new SOO();
		Map<Object, Object> head = new HashMap<Object, Object>();
		Map<Object, Object> head1 = new HashMap<Object, Object>();
		head1.put("SOO_ID", id);
		head.put("TYPE", type);
		soo.put("PUB_RES", head);
		return soo;
	}
	
	public static JSONObject toResJsonObject(SOO soo) {
		SvcCont svcCont = new SvcCont();
		svcCont.getSoos().add(soo);
		String rtnJson = JSON.toJSONString(svcCont,
				SerializerFeature.DisableCircularReferenceDetect);
		return JSONObject.fromObject(rtnJson);
	}

}
