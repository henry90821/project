package com.smi.mc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.smi.tools.kits.CollectionKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;

/**
 * Soo组装处理器
 * 
 * @author xzr
 * @date 2016/03/11
 */
public class SooHandler {
	
	//请求类型 参数名称
	private static final String PUB_REQ = "PUB_REQ";
	
	/**
	 * 不需要组装参数节点的调用方法，仅组装参数
	 * 
	 * @param reqParam 报文参数
	 * @param reqName 参数名称
	 * @return
	 */
	public static Map<String, Object> handParam(Object reqParam,String reqName) {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(reqName, reqParam);
		return reqMap;
	}
	/**
	 * 组装SOO的报文格式
	 * @date 2016年5月18日 下午3:13:53
	 * @param soo
	 * @param listParams
	 * @return
	 */
	public static Map<String, Object> packSOO(Object soo,List<Object> listParams){
		
		if (CollectionKit.isEmpty(listParams)) {
			List<Object> sooList=new ArrayList<Object>();
			sooList.add(soo);
			return handParam(sooList, "SOO");
		}else{
			listParams.add(soo);
			return handParam(listParams, "SOO");
		}
	}
	
	/**
	 * 组装SOO的多个报文格式
	 * @author hanhua
	 * @method packSOO
	 * @param soos
	 * @return 
	 * @date 2016年5月19日 下午6:21:03
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> packMoreSOO(Object... soos){
	    List<Object> sooList= Arrays.asList(soos);
	    return handParam(sooList, "SOO");
    }
	
	/**
	 * 需要组装参数节点的调用方法，组装操作类型和参数
	 * 
	 * @param type 操作类型
	 * @param reqParam 报文参数
	 * @param reqName 参数名称
	 * @return
	 */
	public static Map<String, Object> handParam(String type, Object reqParam, String reqName) {
		PubReq pubReq = new PubReq();
		pubReq.setType(type);
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(PUB_REQ, pubReq);
		reqMap.put(reqName, reqParam);
		return reqMap;
	}
	
	/**
	 * 返回的结果获取第一个BODY的第一个SOO字符串
	 * @version 1.0
	 * @date 2016年5月19日 下午3:08:52
	 * @param resut
	 * @return
	 */
	public static String getResultSOO(String resut){
		String sooOne="";
		if (StrKit.isNotEmpty(resut)) {
			Map<String, Object> bodyMap=JsonKit.parserToMap( resut);
			List<Object> bodyList=JSON.parseArray(JsonKit.toJsonString(bodyMap.get("SOO")));
			Map<String, Object> sooMap= JsonKit.parserToMap(JsonKit.toJsonString(bodyList.get(0)));
			List<Object> sooList=JSON.parseArray(JsonKit.toJsonString(sooMap.get("SOO")));
			sooOne=String.valueOf(sooList.get(0));
		}
		return sooOne;
	}
	
	/**
	 * 根据传入的key和json对象获取对应的Value
	 * @version 1.0
	 * @date 2016年5月19日 下午3:10:41
	 * @return
	 */
	public static String getValueByKey(String jsonStr,String key){
		String value="";
		if (StrKit.isNotEmpty(jsonStr)&&StrKit.isNotEmpty(key)) {
			Map<String, Object> map=JsonKit.parserToMap(jsonStr);
			value=String.valueOf(map.get(key));
		}
		return value;
	}
	
	/**
	 * 解析json报文
	 */
	public static String parseJson(String jsonParam){
		String jsonRes="";
		if (StrKit.isNotEmpty(jsonParam)) {
			Map<String, Object> paramMap=JsonKit.parserToMap( jsonParam);
			//List<Object> sooList=JSON.parseArray(JsonKit.toJsonString(paramMap.get("SOO")));
			Map<String, Object> sooMap= JsonKit.parserToMap(JsonKit.toJsonString(paramMap.get("SOO")));
			jsonRes = JsonKit.toJsonString(sooMap);
		}
		return jsonRes;
	}
	
	public static String parseJsonForInTest(String jsonParam){
		String jsonRes="";
		if (StrKit.isNotEmpty(jsonParam)) {
			Map<String, Object> paramMap=JsonKit.parserToMap( jsonParam);
			List<Object> sooList=JSON.parseArray(JsonKit.toJsonString(paramMap.get("SOO")));
			Map<String, Object> sooMap= JsonKit.parserToMap(JsonKit.toJsonString(sooList.get(0)));
			jsonRes = JsonKit.toJsonString(sooMap);
		}
		return jsonRes;
	}

}
