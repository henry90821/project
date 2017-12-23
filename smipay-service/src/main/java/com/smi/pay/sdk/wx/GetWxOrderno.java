package com.smi.pay.sdk.wx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.smi.pay.common.CommonUtil;

public class GetWxOrderno {

	public static String getPayNo(String url, String xmlParam, String type) {
		// System.out.println("xml是:"+xmlParam);
		// DefaultHttpClient client = new DefaultHttpClient();
		// client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
		// true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		String returnvalue = "";
		// DefaultHttpClient httpclient =
		// (DefaultHttpClient)HttpClientConnectionManager.getSSLInstance(new
		// DefaultHttpClient());
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = CommonUtil.httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			System.out.println("getPayNojson是:" + jsonStr);

			if (jsonStr.indexOf("FAIL") != -1) {
				return returnvalue;
			}
			Map map = doXMLParse(jsonStr);
			String code_url = (String) map.get("code_url");

			String prepay_id = (String) map.get("prepay_id");
			if (type.equals("NATIVE")) // 扫码支付，返回 code_url
			{
				returnvalue = code_url;
			}
			else {
				returnvalue = prepay_id;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return returnvalue;
	}

	// 退款请求
	public static Map refund(String url, String xmlParam, String type) {
		// DefaultHttpClient client = new DefaultHttpClient();
		// client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
		// true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		DefaultHttpClient httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(new DefaultHttpClient(), type);
		Map map = new HashMap();
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			map = doXMLParse(jsonStr);
			String return_code = (String) map.get("return_code");
			if ("SUCCESS".equals(return_code)) {
				String result_code = (String) map.get("result_code");
				String err_code = (String) map.get("err_code");
				String err_code_des = (String) map.get("err_code_des");
				if ("SUCCESS".equals(result_code)) {
					map.put("status", "0");
					map.put("msg", "退款成功");
				}
				else {
					map.put("status", "1");
					map.put("msg", err_code_des);
				}
			}
			else {
				String return_msg = (String) map.get("return_msg");
				map.put("status", "1");
				map.put("msg", return_msg);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map doXMLParse(String strxml) throws Exception {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}

		Map m = new HashMap();
		InputStream in = String2Inputstream(strxml);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			}
			else {
				v = getChildrenText(children);
			}

			m.put(k, v);
		}
		// 关闭流
		in.close();

		return m;
	}

	/**
	 * 获取子结点的xml
	 * 
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}

	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}

}