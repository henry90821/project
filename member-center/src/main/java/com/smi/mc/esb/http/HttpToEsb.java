package com.smi.mc.esb.http;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.smi.mc.constants.ConfigConstants;
import com.smi.mc.utils.HttpClientUtil;
import com.smi.mc.utils.JSONParse;
import com.smi.mc.utils.RSAEncrypt;

import net.sf.json.JSONObject;

public class HttpToEsb {
	/**
	 * http发送esb平台
	 * 
	 * @param reqJson
	 *            请求报文
	 * @return
	 */
	public static String httpToEsb(String reqJson, String sysId, String transactionID, String ServiceCode,String url) {
		String resultJson = null;
		try {
			RSAEncrypt RSA = new RSAEncrypt();
			SimpleDateFormat localDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String reqDate = localDate.format(new Date());
			String str1 = JSONParse.getSvcContJson(reqJson);
			String enStr = RSA.MD5(str1);
//			String enStr ="DC085F7F6AEE92DB1A5305D08911BE3F";
			String signStr = RSA.enc(enStr);///ServiceBus/custView/cust/Recharge001
			String str = "\"TcpCont\":{\"TransactionID\":\"" + transactionID + "\",\"ReqTime\":\"" + reqDate
					+ "\",\"SignatureInfo\":\"" + signStr + "\",\"SYS_ID\":\"" + sysId + "\",\"ServiceCode\":\""
					+ ServiceCode + "\"}";
			StringBuffer sb = new StringBuffer();
			sb.append(ConfigConstants.LEFTSIGN);
			sb.append(str);
			sb.append(ConfigConstants.COMMA);
			sb.append(ConfigConstants.SvcContElement);
			sb.append(str1);
			sb.append(ConfigConstants.RIGHTSIGN);
			JSONObject JSON = JSONObject.fromObject(sb.toString());
			resultJson = HttpClientUtil.httpPost(url, JSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}
	

}
