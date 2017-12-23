package com.iskyshop.core.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;



public class AddressUtil {
	private static Logger logger = Logger.getLogger(AddressUtil.class);
	
	private static final String AK = "PfRYBEmGcY9EDiIG4oKwc2Qo";
	private static final String SK = "ZPHlq7lUU1Gf29ic6sigE49kndhWf7Z2";
	private static final String GEOURL = "http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=" + AK + "&sn=";
	private static double DEF_PI = 3.14159265359; // PI
	private static double DEF_2PI = 6.28318530712; // 2*PI
	private static double DEF_PI180 = 0.01745329252; // PI/180.0
	private static double DEF_R = 6370693.5; // radius of earth

	/**
	 * 获取指定收货地址对应的经纬度
	 * 
	 * @param addr
	 * @return 若函数出错，则返回false, 否则返回true并将计算结果保存到addr的经纬度属性中去(但未保存数据库)
	 */
	public static boolean getLngAndLat(Address addr) {
		Area currentArea = addr.getArea();		
		String address = null;
		String an = currentArea.getAreaName();
		String pan = currentArea.getParent().getAreaName();
		if(addr.getArea_info().indexOf(an) >= 0 && addr.getArea_info().indexOf(pan) >= 0) {//为了尽量避免地址太长导致百度地图返回“Request Parameter Error:address length too long”错误的现象出现
			address = addr.getArea_info();
		} else {
			address = pan + an + addr.getArea_info();
		}
			
		Map<String, Double> result = getLngAndLat(address);
		if(result != null) {
			addr.setLongitude(result.get("lng"));
			addr.setLatitude(result.get("lat"));
			return true;
		}
		return false;
	}
	
	/**
	 * 获取指定物理地址对应的经纬度
	 * @param fullAddrInfo  完整的地址，如：广东省深圳市福田区福田体育公园
	 * @return 若获取失败则返回null，否则返回Map<String, Double>:key=lng（经度）， key=lat(纬度)
	 */
	public static Map getLngAndLat(String fullAddrInfo) {
		Map result = new HashMap<String, Double>();//保存经纬度
		String url = "";
		logger.info("向百度地图请求地址“" + fullAddrInfo + "”的经纬度......");
		try {
			url = String.format(GEOURL + calSN(fullAddrInfo), URLEncoder.encode(fullAddrInfo, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("向百度地图请求地址“" + fullAddrInfo + "”的经纬度前，计算SN时出错。", e);
			return null;
		}		
		logger.info("向百度地图发起的url：" + url);
		JSONObject obj = HttpUtils.getJsonFromUrl(url);
		logger.info("百度地图的返回值：" + obj.toString());
		
		if ("0".equals(obj.get("status").toString())) {
			JSONObject location = obj.getJSONObject("result").getJSONObject("location");
			result.put("lng", location.getDouble("lng"));
			result.put("lat", location.getDouble("lat"));
			logger.info("获取经纬度成功：lng=" + location.getDouble("lng") + "  lat=" + location.getDouble("lat"));
		} else {
			logger.error("向百度地图请求地址“" + fullAddrInfo + "”的经纬度失败：请求的返回状态为：" + obj.get("status").toString() + "，错误：" + obj.get("msg").toString());
			return null;
		}

		return result;
	}

	/**
	 * 获取两个经纬度之间的距离（适用于短距离的计算），返回单位：米
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI) {
			dew = DEF_2PI - dew;
		} else if (dew < -DEF_PI) {
			dew = DEF_2PI + dew;
		}
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}
	

	/**
	 * 获取两个经纬度之间的距离（适用于长距离的计算），返回单位：米
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
				* Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0) {
			distance = 1.0;
		} else if (distance < -1.0) {
			distance = -1.0;
		}
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}

	
	private static String calSN(String addrStr) throws UnsupportedEncodingException {
		Map paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("address", addrStr);
		paramsMap.put("output", "json");
		paramsMap.put("ak", AK);

		String wholeStr = new String("/geocoder/v2/?" + toQueryString(paramsMap) + SK);

		return MD5(URLEncoder.encode(wholeStr, "UTF-8"));
	}
	
	/**
	 * 对Map内所有value作utf8编码，拼接返回结果
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
    private static String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException {
		StringBuffer queryString = new StringBuffer();
		for (Entry<?, ?> pair : data.entrySet()) {
			queryString.append(pair.getKey() + "=");
			queryString.append(URLEncoder.encode((String) pair.getValue(),"UTF-8") + "&");
		}
		if (queryString.length() > 0) {
			queryString.deleteCharAt(queryString.length() - 1);
		}
		return queryString.toString();
    }

    
    /**
     * MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
     * @param md5
     * @return
     */
    private static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
    }
}
