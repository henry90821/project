
package com.smi.mc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;

public final class JsonUtils {

	private JsonUtils() {
	}

	public static String toJsonString(Object obj) {
		if (null == obj)
			return null;
		else
			return JSON.toJSONString(obj);
	}

	public static String toJsonStringWithDateFormat(Object obj, String dateFormat) {
		if (null == obj)
			return null;
		if (StrKit.isBlank(dateFormat))
			return JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
		else
			return JSON.toJSONStringWithDateFormat(obj, dateFormat, new SerializerFeature[0]);
	}

	public static String toJsonStringWithDateFormat(Object obj) {
		return toJsonStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss");
	}

	public static String toJsonWithChineseToUnicode(Object obj) {
		if (null == obj)
			return null;
		else
			return JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss",
					new SerializerFeature[] { SerializerFeature.BrowserCompatible });
	}

	public static String toJsonWithNoRefrenceDetect(Object obj) {
		if (null == obj)
			return null;
		else
			return JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss",
					new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect });
	}

	public static List parserToList(String jsonStr, Class clazz) {
		if (null == jsonStr)
			return null;
		else
			return JSON.parseArray(jsonStr, clazz);
	}

	public static Map parserToMap(String jsonStr) {
		if (null == jsonStr)
			return null;
		Map map = new HashMap();
		JSONObject json = JSON.parseObject(jsonStr);
		for (Iterator i$ = json.keySet().iterator(); i$.hasNext();) {
			Object k = i$.next();
			Object v = json.get(k);
			if (v instanceof JSONArray) {
				List list = new ArrayList();
				JSONObject json2;
				for (Iterator it = ((JSONArray) v).iterator(); it.hasNext(); list.add(parserToMap(json2.toString())))
					json2 = (JSONObject) it.next();

				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}

		return map;
	}

	public static Map parserToMap2(String jsonStr, Class clazz) {
		if (null == jsonStr)
			return null;
		Map map = new HashMap();
		JSONObject json = JSON.parseObject(jsonStr);
		for (Iterator i$ = json.keySet().iterator(); i$.hasNext();) {
			Object k = i$.next();
			Object v = json.get(k);
			String vStr = v.toString();
			if (v instanceof JSONArray) {
				List list = new ArrayList();
				list = parserToList(vStr, clazz);
				map.put(k.toString(), list);
			} else {
				Object obj = JSON.parseObject(vStr, clazz);
				map.put(k.toString(), obj);
			}
		}

		return map;
	}

	public static JSONArray parserToJsonArray(String jsonStr) {
		if (null == jsonStr)
			return null;
		else
			return JSON.parseArray(jsonStr);
	}

	public static Object parseObject(String jsonStr, Class clazz) {
		if (null == jsonStr)
			return null;
		else
			return JSON.parseObject(jsonStr, clazz);
	}

	public static JSONObject parseObject(String jsonStr) {
		if (null == jsonStr)
			return null;
		else
			return JSON.parseObject(jsonStr);
	}

	public static String formatJson(String json) {
		StringBuffer result = new StringBuffer();
		int length = json.length();
		int number = 0;
		char key = '\0';
		for (int i = 0; i < length; i++) {
			key = json.charAt(i);
			if (i == 0 && key == '{')
				result.append('\n');
			if (key == '[' || key == '{') {
				result.append(key);
				if (i + 1 >= length || json.charAt(i + 1) != ']' && json.charAt(i + 1) != '}') {
					result.append('\n');
					number++;
					result.append(indent(number));
				}
				continue;
			}
			if (key == ']' || key == '}') {
				if (i - 1 <= 0 || json.charAt(i - 1) != '[' && json.charAt(i - 1) != '{') {
					result.append('\n');
					number--;
					result.append(indent(number));
				}
				result.append(key);
				continue;
			}
			if (key == ',') {
				result.append(key);
				result.append('\n');
				result.append(indent(number));
				continue;
			}
			if (key == ':' && i + 1 < length && json.charAt(i + 1) == '"') {
				result.append(key);
				result.append(' ');
			} else {
				result.append(key);
			}
		}

		return result.toString();
	}

	private static String indent(int number) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < number; i++)
			result.append(SPACE);

		return result.toString();
	}

	private static String SPACE = "    ";
	
	public static void main(String[] args) {
		String json = "{'SOO': [{'PUB_REQ': { 'TYPE': 'CUST_ADD' },'"
				+ "CUST': {'CHANNEL_CODE': '','STAFF_ID': '10020','SYSTEM_ID':'120','MOBILE': '15297765280','BAIDU_UID': '1','BALANCE':'100000'}}]}";
		
		String resultSOO = SooHandler.parseJson(json);
		Map parserToMap = JsonUtils.parserToMap(resultSOO);
		String jsonString = JsonKit.toJsonString( parserToMap.get("CUST"));
		Map parserToMap2 = JsonUtils.parserToMap(jsonString);
		System.out.println(parserToMap2.get("BALANCE"));
		System.out.println(parserToMap.get("PUB_REQ"));
		
		
	}

}
