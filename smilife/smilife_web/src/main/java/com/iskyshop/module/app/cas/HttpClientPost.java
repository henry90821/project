package com.iskyshop.module.app.cas;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.iskyshop.core.tools.StringUtils;

public class HttpClientPost {
	/**
	 * 
	 * @param url
	 * @param ticket
	 * @param paramter
	 * @return String数组 第1个为jsessionid,第0位为原接口该返回的报文
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String[] postTest(String url, String ticket, Map<String, String> paramter)
			throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		postMethod.addRequestHeader("app", "true");
		// 增加ticket变量
		if (!StringUtils.isNullOrEmpty(ticket)) {
			postMethod.addParameter("ticket", ticket);
		}
		// 如果参数不为空，将参数加入到请求中
		if (paramter != null) {
			for (Map.Entry<String, String> entry : paramter.entrySet()) {
				postMethod.addParameter(entry.getKey(), entry.getValue());
			}
		}
		// 结果返回数组
		String[] result = new String[2];
		// Header header=new Header("app","true");
		// postMethod.addRequestHeader(header);
		int code = client.executeMethod(postMethod);
		// 如果做302跳转，重发post请求至跳转地址
		if (code == 302) {
			Header locationHeader = postMethod.getResponseHeader("location");
			String location = null;
			if (locationHeader != null) {
				// 获取跳转地址，截取jsessionid
				location = locationHeader.getValue();
				int idx = location.indexOf("jsessionid");
				String jid = location.substring(idx);
				String jid2 = location.substring(idx + "jsessionid=".length());
				result[1] = jid2;
				// 重发post请求
				PostMethod redirect = new PostMethod(location);
				redirect.addRequestHeader("app", "true");
				// 添加参数
				if (paramter != null) {
					for (Map.Entry<String, String> entry : paramter.entrySet()) {
						redirect.addParameter(entry.getKey(), entry.getValue());
					}
				}
				client = new HttpClient();
				client.executeMethod(redirect);
				result[0] = redirect.getResponseBodyAsString();
				redirect.releaseConnection();
			}
		} else {
			result[0] = new String(postMethod.getResponseBodyAsString().getBytes("utf-8"));
			postMethod.releaseConnection();
		}
		return result;
	}
}
