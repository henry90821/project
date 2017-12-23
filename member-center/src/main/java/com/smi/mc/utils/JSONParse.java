package com.smi.mc.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class JSONParse {

	public static String getString(String text,String node) {
		
		if(StringUtils.isEmpty(text) || StringUtils.isEmpty(node)){
			return StringUtils.EMPTY;
		}
		String regx = "\""+node+"\"\\s*"+":"+"\\s*\"\\s*"+"(.*?)"+"\\s*\"";
		Pattern p= Pattern.compile(regx);
		Matcher m = p.matcher(text);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	/**
	 * 取JSON数组
	 * @param text
	 * @param node
	 * @return
	 */
	public static String getJsonArray(String text,String node) {
		
		if(StringUtils.isEmpty(text) || StringUtils.isEmpty(node)){
			return StringUtils.EMPTY;
		}
		String regx = "\""+node+"\"\\s*"+":\\s*"+"(\\[\\s*\\{.*?\\}\\s*\\])";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(text);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		if("".equals(value)){
			regx = "\""+node+"\"\\s*"+":\\s*"+"(\\[.*?\\])";
			p= Pattern.compile(regx,Pattern.DOTALL);
			m = p.matcher(text);
			if(m.find()){
				value = m.group(1);
			}
		}
		return value;
	}
	
	public static int getInt(String text,String node) {
		return Integer.parseInt(getString(text,node));
	}
	/**
	 * 取JSON对象
	 * @param reqJson
	 * @param node
	 * @return
	 */
	public static String getJsonObject(String reqJson, String node) {
		if(StringUtils.isEmpty(reqJson) || StringUtils.isEmpty(node)){
			return StringUtils.EMPTY;
		}
		String regx = "\""+node+"\"\\s*"+":\\s*"+"(\\{.*?\\})";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(reqJson);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	
	public static String getOutsideJsonObject(String reqJson, String node) {
		if(StringUtils.isEmpty(reqJson) || StringUtils.isEmpty(node)){
			return StringUtils.EMPTY;
		}
		String regx = "\""+node+"\"\\s*"+":\\s*"+"(\\{.*?\\}\\s*})";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(reqJson);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	public static String getSaleJsonArray(String text) {
		String regx = "\""+"SALE_.*?"+"\"\\s*"+":\\s*"+"(\\[\\s*\\{.*?\\}\\s*\\])";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(text);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		if("".equals(value)){
			regx = "\""+"SALE_.*?"+"\"\\s*"+":\\s*"+"(\\[.*?\\])";
			p= Pattern.compile(regx,Pattern.DOTALL);
			m = p.matcher(text);
			if(m.find()){
				value = m.group(1);
			}
		}
		return value;
	}
	public static String getSaleJsonObject(String reqJson) {
		
		String regx = "\""+"SALE_.*?"+"\"\\s*"+":\\s*"+"(\\{.*?\\})";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(reqJson);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	public static String getSooJson(String reqJson) {
		String regx="\"SOO\"\\s*:\\s*(\\[.*\\])";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(reqJson);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	public static String getJsonArrayWithHead(String reqJson,String node) {
		if(StringUtils.isEmpty(reqJson) || StringUtils.isEmpty(node)){
			return StringUtils.EMPTY;
		}
		String regx = ".*(,\\s*\\{\\s*\"PUB_REQ.*?\""+node+"\"\\s*"+":\\s*"+"\\[\\s*\\{.*?\\}\\s*\\]\\s*})";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(reqJson);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		if("".equals(value)){
			regx = ".*(,\\s*\\{\\s*\"PUB_REQ.*?\""+node+"\"\\s*"+":\\s*"+"\\[.*?\\]\\s*})";
			p= Pattern.compile(regx,Pattern.DOTALL);
			m = p.matcher(reqJson);
			if(m.find()){
				value = m.group(1);
			}
		}
		return value;
		
	}
	public static String getCodeValue(String custId) {
		if(StringUtils.isEmpty(custId)){
			return StringUtils.EMPTY;
		}
		String regx = "\\("+"(.*?),";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(custId);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	
	public static String getCodeType(String custId) {
		if(StringUtils.isEmpty(custId)){
			return StringUtils.EMPTY;
		}
		String regx = "\\(.*?,"+"(.*?),";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(custId);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}
	public static String getCodeLatnId(String custId) {
		if(StringUtils.isEmpty(custId)){
			return StringUtils.EMPTY;
		}
		String regx = ".*,(.*?)\\)";
		Pattern p= Pattern.compile(regx,Pattern.DOTALL);
		Matcher m = p.matcher(custId);
		String value = "";
		if(m.find()){
			value = m.group(1);
		}
		return value;
	}

	
	
	public static String getSvcContJson(String reqJson) throws Exception {
		String value = "";
		int indexSOO = isHaveMaohaoInAfter(reqJson,"\"SvcCont\"");
		int indexFirst = reqJson.indexOf("{", indexSOO);
		int indexLast = 0;
		int leftCount = 0;
		int rightCount = 0;
		
		List<String> maohaoDuiList = getJsonMaoHaoDui(reqJson) ;
		
		for(int i=indexFirst;i<reqJson.length();i++){
			if(reqJson.charAt(i)=='{'&&!isInMaohao(maohaoDuiList,i)){
				
				leftCount++;
			}
			else if(reqJson.charAt(i)=='}'&&!isInMaohao(maohaoDuiList,i)){
				rightCount++;
				if(leftCount==rightCount){
					indexLast= i;
					break;
				}
			}
		}
		
		value = reqJson.substring(indexFirst, indexLast+1);
		
		return value;
	}
	
	/**
	 * 判断key（包括两边的冒号） 字符串在 reqJson中的位置,确保key 后面离他最近的非空字符是 ":"买
	 * @param reqJson
	 * @param key
	 * @return
	 */
	private static int isHaveMaohaoInAfter(String reqJson,String key){
		int index = -1;
		index = reqJson.indexOf(key);
		int fromindex = -1;
		boolean isFind = false;
		while(index!=-1&&isFind==false) {
			if(fromindex!=-1){
				index = reqJson.indexOf(key,fromindex+1);
			}else{
				index = reqJson.indexOf(key);
			}
			
			//判断其后是否为“:”
			for(int i = index+key.length();i<reqJson.length();i++){
				if(reqJson.charAt(i)!='\r'&&reqJson.charAt(i)!='\n'
						&&reqJson.charAt(i)!='\b'&&reqJson.charAt(i)!='\t'
						&&reqJson.charAt(i)!=' '){
					if(reqJson.charAt(i)==':'){
						isFind = true;
						break;
					}else{
						fromindex = index;
						break;
					}
				}
			}
			//
		}
		return index;
	}
	
	/**
	 * 判断 reqJson 中的第i个符号，是否在一对冒号中
	 * @param reqJson
	 * @param i
	 * @return
	 */
	private static boolean isInMaohao(List<String> maohaoList,int i){
		int firstIndex = -1;
		int secondIndex = -1;
		boolean b = false;
		for (String maohaoDui : maohaoList) {
			firstIndex = Integer.parseInt(maohaoDui.split(",")[0]);
			secondIndex = Integer.parseInt(maohaoDui.split(",")[1]);
			if(firstIndex<i&&i<secondIndex){
				b = true;
				break;
			}
		}
		return b;
	}
	
	/**
	 * 得到json中的冒号对
	 * @param reqJson
	 * @return
	 * @throws Exception
	 */
	private static List<String> getJsonMaoHaoDui(String reqJson) throws Exception{
		List<String> maoHaoDuiList = new ArrayList<String>();
		
		int firstMaohaoindex = -1;  
		int secondMaohaoindex = -1;
		
		int preSecondMaohaoIndex = -1;  //上一组冒号的后面一个冒号号的位置
		
		StringBuffer tempSb = null;
		do{
			tempSb = new StringBuffer();
			if(preSecondMaohaoIndex==-1){
				firstMaohaoindex = reqJson.indexOf("\"");
			}else{
				firstMaohaoindex = reqJson.indexOf("\"",preSecondMaohaoIndex+1);
			}
			
			if(firstMaohaoindex==-1) {
				break;
			}
			
			secondMaohaoindex = reqJson.indexOf("\"",firstMaohaoindex+1);
			
			if(secondMaohaoindex==-1){
				Exception exception = new Exception("json格式不正确!");
				throw exception;
			}
			
			tempSb = new StringBuffer(firstMaohaoindex+","+secondMaohaoindex);
			maoHaoDuiList.add(tempSb.toString());
			
			preSecondMaohaoIndex = secondMaohaoindex;
			
		}while(firstMaohaoindex != -1);
		return maoHaoDuiList;
		
	}  

}
