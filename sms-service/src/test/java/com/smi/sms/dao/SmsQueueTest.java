package com.smi.sms.dao;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.smi.sms.common.FinalValue;
import com.smi.sms.common.SmsQueue;
import com.smi.sms.model.SmsHistory;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.MD5Kit;
import com.smi.tools.kits.XmlKit;

public class SmsQueueTest {

	@Test
	public void testQueue() {

		for (int i = 1; i < 5; i++) {
			SmsHistory his = new SmsHistory();
			his.setId(i);
			SmsQueue.offerElement(his);
		}
		SmsQueue.printQueue();
		List<SmsHistory> list = new ArrayList<SmsHistory>();
		list = SmsQueue.getQueueAsList();
		for (SmsHistory h : list) {
			System.out.println(JsonKit.toJsonString(h));
		}

	}
	
	
	@Test
	public void testSendMsgTwo() throws UnsupportedEncodingException{
		/*对接协议：HTTP
		企业ID：100001
		客户账号：100001
		客户密码：PVWNF7PXJ9
		发送速率：100条/秒
		字数：64字（已扣除签名）
		长短信：67字（含签名在内）
		对接IP：121.201.29.177
		端口：8860
		接入号：1069065177364
		【星美生活】*/

		Map<String, Object> params = new HashMap<String, Object>();
		String rtn = "";
		String cust_pwd="PVWNF7PXJ9";//生产用户密码
		//String cust_pwd="2GPFH37M7X";//客服用户密码
		
		params.put("cust_code", "100001");//生产用户账户
		//params.put("cust_code", "100003");//客服用户账户
		
		params.put("sp_code", "1069065177364");//生产长号码,选填
		//params.put("sp_code", "1069035155282");//客服长号码,选填
		
		params.put("destMobiles", "15974206479,1700018317");//接收号码,每秒上限（100条/秒）
		String content="你的短信验证码为：5690";
		params.put("content", URLEncoder.encode(content,"utf-8"));//短信内容
		params.put("sign", MD5Kit.getMD5String(URLEncoder.encode(content,"utf-8")+cust_pwd));//签名
		
		try {
			rtn = HttpKit.post("http://121.201.29.177:8860",HttpKit.toParams(params));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(rtn);
		System.out.println(URLDecoder.decode(rtn));
		
		decodeMsg(URLDecoder.decode(rtn));
		
	}
	
	public void decodeMsg(String msg){
		//String msg="SUCCESS:提交成功！\r\n13537812145:0904d5302b4845000023:15\r\n13554860437:0904d5302b4845000024:15\r\n15815593920:0904d5302b4845000025:15\r\n";
		//String msg="ERROR:签名验证不通过！";
		String[] strs = msg.split("\n");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String codeMsg = strs[0];
		String result=codeMsg.substring(0,codeMsg.indexOf(":"));
		String message=codeMsg.substring(codeMsg.lastIndexOf(":")+1,codeMsg.length());
		if("SUCCESS".equals(result)){
			returnMap.put("result", true);
		}else{
			returnMap.put("result", false);
		}
		returnMap.put("message", message);
		Map<String, Object> detailMap = new HashMap<String, Object>();
		for (int i = 1; i < strs.length; i++) {
			String tmp = strs[i];
			String phone =tmp.substring(0,tmp.indexOf(":"));
			String code = tmp.substring(tmp.lastIndexOf(":")+1,tmp.length());
			detailMap.put(phone, code);
			//getErrorMsgByCode(code)
		}
		returnMap.put("details", detailMap);
		System.out.println(JsonKit.toJsonString(returnMap));
	}


	
	@Test
	public void send(){
		String phoneId="15974206479";
		String content="你的短信验证码为：5690"+"【星美生活】";
		String cust_pwd="PVWNF7PXJ9";
		try { 
			String urlencContent = URLEncoder.encode(content,"utf-8");
			String sign = MD5Kit.getMD5String((urlencContent + cust_pwd));
			
			String postData = "content=" + urlencContent + "&destMobiles="
					+ phoneId + "&sign=" + sign + "&cust_code=" +"100001"
					+ "&sp_code=" + "1069065177364" + "&task_id=1";
			System.out.println(postData);
			URL myurl = new URL("http://121.201.29.177:8860");
			URLConnection urlc = myurl.openConnection();
			urlc.setReadTimeout(1000 * 30);
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setAllowUserInteraction(false);
			
			DataOutputStream server = new DataOutputStream(urlc.getOutputStream());
			server.write(postData.getBytes());
			server.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlc.getInputStream(), "utf-8"));
			String resXml = "", s = "";
			while ((s = in.readLine()) != null)
				resXml = resXml + s + "\r\n";
			in.close();
			String returnData = URLDecoder.decode(resXml,"utf-8");
			System.out.println(returnData);
			//Map<String,Object> result =decodeMsg(returnData);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSendMsg() {
		Map<String, Object> params = new HashMap<String, Object>();
		String rtn = "";
		params.put("userId", "J22712");
		params.put("password", "206851");
		params.put("pszMobis", "15974206479");
		params.put("pszMsg", "你的短信验证码为：5690");
		params.put("iMobiCount", 1);
		params.put("pszSubPort", "*");
		try {
			rtn = HttpKit.post("http://61.145.229.29:7903/MWGate/wmgw.asmx/MongateCsSpSendSmsNew", params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(rtn);
		
	
		 String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">4311942658541542114</string>";
		
		 System.out.println(XmlKit.getRootNode(XmlKit.parserXml(xml)).getTextTrim());
		 
		 System.out.println(XmlKit.getElementValue(XmlKit.parserXml(xml).getRootElement()));
		 
		/*try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			
			System.out.println("Root: " + root.getName());
			// 获取名字为指定名称的第一个子元素
	        System.out.println("String: " + root.getTextTrim());
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}  
		*/
	}

}
