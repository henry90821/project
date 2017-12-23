package com.iskyshop.pay.chinapay;

import chinapay.PrivateKey;
import chinapay.SecureLink;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.pay.chinapay.util.connection.CPHttpConnection;
import com.iskyshop.pay.chinapay.util.connection.Http;
import com.iskyshop.pay.chinapay.util.connection.HttpSSL;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ChinaPayCommon {

	private static final Logger LOGGER = Logger.getLogger(ChinaPayCommon.class);

	public static final String PAY_SEPARATOR = "~";// 分隔符

	/***
	 * chinapay.SecureLink
	 * 
	 * @param merId
	 * @return
	 */
	public static SecureLink getSecureLink(String merId) {
		PrivateKey privateKey = new chinapay.PrivateKey();
		SecureLink secureLink;
		boolean flag;
		flag = privateKey.buildKey(merId, 0, getFilePathName(merId));
		if (!flag) {
			LOGGER.error("build key error!");
			return null;
		}
		secureLink = new chinapay.SecureLink(privateKey);
		return secureLink;
	}

	/***
	 * 回调
	 * 
	 * @return
	 */
	public static SecureLink getRetSecureLink() {
		PrivateKey privateKey = new chinapay.PrivateKey();
		SecureLink secureLink;
		boolean flag;
		flag = privateKey.buildKey("999999999999999", 0, getRetFilePathName());
		if (!flag) {
			LOGGER.error("ret build key error!");
			return null;
		}
		secureLink = new chinapay.SecureLink(privateKey);
		return secureLink;
	}

	/***
	 * 退款返回数据验证
	 * 
	 * @param str
	 * @param checkValue
	 * @return
	 */
	public static boolean getVerifyAuthToken(String str, String checkValue) {
		boolean flag = false;
		SecureLink secureLink = getRetSecureLink();
		flag = secureLink.verifyAuthToken(str, checkValue);
		return flag;
	}

	/***
	 * 退款返回数据验证
	 * 
	 * @param str
	 * @param checkValue
	 * @return
	 */
	public static boolean getVerifyAuthToken(Map<String, String> map) {
		String str = map.get("MerID") + map.get("ProcessDate") + map.get("TransType") + map.get("OrderId")
				+ map.get("RefundAmout") + map.get("Status") + map.get("Priv1");
		String checkValue = map.get("CheckValue");
		boolean flag = false;
		SecureLink secureLink = getRetSecureLink();
		flag = secureLink.verifyAuthToken(str, checkValue);
		return flag;
	}

	/***
	 * 应答数据的签名验证
	 * 
	 * @param merId
	 * @param ordId
	 * @param transAmt
	 * @param curyId
	 * @param transDate
	 * @param transType
	 * @param orderStatus
	 * @param checkValue
	 * @return
	 */
	public static boolean getVerifyTransResponse(String merId, String ordId, String transAmt, String curyId,
			String transDate, String transType, String orderStatus, String checkValue) {
		boolean flag = false;
		SecureLink secureLink = getRetSecureLink();
		flag = secureLink.verifyTransResponse(merId, ordId, transAmt, curyId, transDate, transType, orderStatus, checkValue);
		return flag;
	}

	/***
	 * 一串数字签名
	 * 
	 * @param merId
	 * @param str
	 * @return
	 */
	public static String getSign(String merId, String str) {
		String signValue = "";
		SecureLink secureLink = getSecureLink(merId);
		if (secureLink == null) {
			return signValue;
		}
		// 对一段字符串的签名
		signValue = secureLink.Sign(str);
		return signValue;
	}

	/***
	 * 提交第三方数据的html
	 * 
	 * @param requestMap
	 * @return
	 */
	public static String createHtml(Map<String, String> requestMap, String url) {
		String requestUrl = url;
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + requestUrl + "\" method=\"post\">");
		if (!StringUtils.isNullOrEmpty(requestMap)) {
			Set<Map.Entry<String, String>> set = requestMap.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/***
	 * chinapay properties
	 * 
	 * @param key
	 * @return
	 */
	public static String getPropertiesByKey(String key) {
		InputStream is = null;
		Properties properties = null;
		String value = "";
		try {
			properties = new Properties();
			is = ChinaPayCommon.class.getResourceAsStream("/chinapay.properties");
			properties.load(is);
			value = properties.getProperty(key);
		} catch (Exception e) {
			LOGGER.error("获取chinapay.properties失败");
		}
		return value;
	}

	/***
	 * 12位金额 分
	 * 
	 * @param total_fee
	 * @return
	 */
	public static String changeFee(String total_fee) {
		String totalFee = "0";
		totalFee = AmountUtils.changeY2F(total_fee);
		int totalFeeLen = totalFee.length();
		if (totalFeeLen > 0) {
			for (int i = 0; i < 12 - totalFeeLen; i++) {
				totalFee = "0" + totalFee;
			}
		}
		return totalFee;
	}

	/***
	 * 解析退款返回的数据
	 * 
	 * @param str
	 * @return
	 */
	public static Map<String, String> getRetMap(String str) {
		Map<String, String> retMap = new HashMap<String, String>();
		if (str == null) {
			return retMap;
		}
		str = str.replaceAll("\\r", "").replaceAll("\\n", "");
		String[] retBody = str.split("<body>");
		String retInfo = retBody[1];
		String[] retBodyInfo = retInfo.split("</body>");
		retInfo = retBodyInfo[0];
		String[] retValue = retInfo.split("&");
		for (int i = 0; i < retValue.length; i++) {
			String[] retParaVal = retValue[i].split("=");
			retMap.put(retParaVal[0], retParaVal[1]);
		}
		return retMap;
	}

	/**
	 * 发送http post报文，并且接受响应信息
	 * 
	 * @param strMsg
	 *            需要发送的交易报文,格式遵循httppost参数格式
	 * @return String 服务器返回响应报文,如果处理失败，返回空字符串
	 */
	public static String sendHttpMsg(String URL, String strMsg, String httpType, String timeOut) {
		String returnMsg = "";
		CPHttpConnection httpSend = null;
		if ("SSL".equals(httpType)) {
			httpSend = new HttpSSL(URL, timeOut);
		} else {
			httpSend = new Http(URL, timeOut);
		}
		// 设置获得响应结果的限制
		httpSend.setLenType(0);
		// 设置字符编码
		httpSend.setMsgEncoding(Globals.CharSet.GBK);
		int returnCode = httpSend.sendMsg(strMsg);
		if (returnCode == 1) {
			try {
				returnMsg = new String(httpSend.getReceiveData(), Globals.CharSet.GBK).trim();
			} catch (Exception e) {
				LOGGER.error("银联[getReceiveData Error!]");
			}
		} else {
			LOGGER.error("银联 报文处理失败,失败代码=" + returnCode);
		}
		return returnMsg;
	}

	/***
	 * key文件路径
	 * 
	 * @return
	 */
	public static String getFilePathName(String merId) {
		return CommUtil.getServerRealPathFromSystemProp() + "WEB-INF" + File.separator + "chinapay" + File.separator
				+ "MerPrK_" + merId + ".key";
	}

	/***
	 * 回调验证文件
	 * 
	 * @return
	 */
	public static String getRetFilePathName() {
		return CommUtil.getServerRealPathFromSystemProp() + "WEB-INF" + File.separator + "chinapay" + File.separator
				+ "PgPubk.key";
	}

}
