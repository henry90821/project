package com.iskyshop.inventory.concurrence;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iskyshop.core.tools.HttpUtils;

@SuppressWarnings("all")
public class LocationTest {

	private static final String SUGGESTION_URL = "http://api.map.baidu.com/place/v2/suggestion";
	private static final String GEOCODING_URL = "http://api.map.baidu.com/geocoder/v2/";
	private static final String AK = "GzyDVg1z2Nbja21byY0MXO6q";

	// http://api.map.baidu.com/place/v2/suggestion?query=天安门&region=131&output=json&ak=E4805d16520de693a3fe707cdc962045

	public static void main(String[] args) throws UnsupportedEncodingException {
		LocationTest.getGeocoding();
	}


	public void getGetSuggestion() throws UnsupportedEncodingException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("query", URLEncoder.encode("天安门", "UTF-8"));
		map.put("region", "131");
		map.put("output", "json");
		map.put("ak", AK);
		map.put("SK", "kVEGyQkXAZC2lvczvAX1h6T6xdtNmBvO");

		StringBuffer sb = new StringBuffer();
		// 拼接参数
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		String paramStr = sb.toString();
		paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
		System.out.println(SUGGESTION_URL + "?" + paramStr);
		String retStr = HttpUtils.doGet(SUGGESTION_URL + "?" + paramStr);
		System.out.print(retStr);
	}

	public static void getGeocoding() throws UnsupportedEncodingException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("query", URLEncoder.encode("百度大厦", "UTF-8"));
		map.put("output", "json");
		map.put("ak", AK);
		map.put("callback", "showLocation");
		map.put("sn", "7de5a22212ffaa9e326444c75a58f9a0");

		StringBuffer sb = new StringBuffer();
		// 拼接参数
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		String paramStr = sb.toString();
		paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
		System.out.println(GEOCODING_URL + "?" + paramStr);
		String retStr = HttpUtils.doGet(GEOCODING_URL + "?" + paramStr);
		System.out.print(retStr);
	}
}
