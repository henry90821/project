package com.smi.mc.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CheckParamUtils {

	/**
	 * 校验参数非空
	 * @param param
	 * @param args
	 * @return
	 */
	public static Map<String, Object> checkParams(Map<String, Object> param, String[] args) {
		Map<String, Object> reMap = new HashMap<String, Object>();
		String mesg = "";
		String status = "0";
		for (int i = 0; i < args.length; i++) {
			if (StringUtils.isBlank((String) param.get(args[i]))) {
				mesg = "必传参数" + args[i] + "为空";
				status = "1";
				reMap.put("MESSAGE", mesg);
				reMap.put("STATUS", status);
				return reMap;
			}
		}
		return reMap;
	}
	
}
