package com.iskyshop.core.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

/**
 * Description:用HttpClient模拟提交，获取远程HTTP数据 Date:2015-05-11 Author:Along
 */

public class HttpUtils {
	private static Logger logger = Logger.getLogger(HttpUtils.class);
	public static final String DEFAULT_CHARTSET = "utf-8";

	public static final String HTTP_GET = "get";

	public static final String HTTP_POST = "post";

	/**
	 * 从特定的url中获取json
	 * 
	 * @param urlStr
	 *            请求的路径 默认用get方式获取
	 * @return json对象
	 */
	public static JSONObject getJsonFromUrl(String urlStr) {

		return getJsonFromUrl(urlStr, HTTP_GET);
	}

	/**
	 * 从特定的url中获取返回值
	 * 
	 * @param urlStr
	 *            请求的路径
	 * @param method
	 *            请求方式 get or post
	 * @return json对象
	 */
	public static String getStrFromUrl(String urlStr, String method) {
		JSONObject json = null;
		String returnStr = null;
		if (method.equals(HTTP_GET)) {
			returnStr = doGet(urlStr);
		} else if (method.equals(HTTP_POST)) {
			returnStr = doPost(urlStr);
		}
		return returnStr;
	}

	/**
	 * 从特定的url中获取json
	 * 
	 * @param urlStr
	 *            请求的路径
	 * @param method
	 *            请求方式 get or post
	 * @return json对象
	 */
	public static JSONObject getJsonFromUrl(String urlStr, String method) {
		JSONObject json = null;
		String returnStr = null;
		if (method.equals(HTTP_GET)) {
			returnStr = doGet(urlStr);
		} else if (method.equals(HTTP_POST)) {
			returnStr = doPost(urlStr);
		}
		if (StringUtils.isNotBlank(returnStr)) {
			json = JSONObject.fromObject(returnStr);
		}
		return json;
	}

	/**
	 * 从特定的url中通过POST获取json
	 * 
	 * @param urlStr
	 *            请求的路径
	 * @param method
	 *            请求方式 post
	 * @return json对象
	 */
	public static String getStrFromPost(String urlStr, Map<String, String> params) {
		JSONObject json = null;
		String returnStr = null;
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		Iterator it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String value = params.get(key).toString();
			BasicNameValuePair p = new BasicNameValuePair(key, value);
			ps.add(p);
		}
		returnStr = doPost(urlStr, ps);
		return returnStr;
	}

	/**
	 * 从特定url中获取key=value格式的返回值并转化为map(目前只有QQ获取token时的返回值为这种格式)
	 * 
	 * @param urlStr
	 *            访问路径
	 * @param method
	 *            请求方式 get or post
	 * @return map格式的key value对
	 */
	public static Map<String, String> getKeyValueFromUrl(String urlStr, String method) {
		String returnStr = null;
		Map resultMap = new HashMap();
		Map<String, String> accssMap = new HashMap<String, String>();
		if (method.equals(HTTP_GET)) {
			returnStr = doGet(urlStr);
		} else if (method.equals(HTTP_POST)) {
			returnStr = doPost(urlStr);
		}

		if (StringUtils.isNotBlank(returnStr)) {
			String returnValues[] = returnStr.split("&");

			for (int i = 0; i < returnValues.length; i++) {
				String temp = returnValues[i];
				String[] keyValue = temp.split("=");
				resultMap.put(keyValue[0], keyValue[1]);
			}
		}
		return resultMap;
	}

	public static String doGet(String urlStr) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httptget = new HttpGet(urlStr);
		String str = null;
		try {
			HttpResponse response = httpClient.execute(httptget);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			byte[] bytes = new byte[256];
			StringBuffer sb = new StringBuffer();
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes, DEFAULT_CHARTSET));
				bytes = new byte[256];
			}
			str = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String doPost(String urlStr) {
		HttpClient httpClient = wrapClient(new DefaultHttpClient());
		String str = null;
		try {
			HttpPost httpPost = new HttpPost(urlStr);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			byte[] bytes = new byte[256];
			StringBuffer sb = new StringBuffer();
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes, DEFAULT_CHARTSET));
				bytes = new byte[256];
			}
			str = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String doPost(String urlStr, List<NameValuePair> params) {
		HttpClient httpClient = wrapClient(new DefaultHttpClient());
		String str = null;
		try {
			HttpPost httpPost = new HttpPost(urlStr);
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); // 进行转码
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			byte[] bytes = new byte[256];
			StringBuffer sb = new StringBuffer();
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes, DEFAULT_CHARTSET));
				bytes = new byte[256];
			}
			str = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 封装HttpClient，防止出现peer not authenticated 的问题
	 * 
	 * @param base
	 *            原HttpClient
	 * @return 加上信任机制的HttpClient
	 */
	private static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String postUrl(String posturl, String content) {
		String body = "";
		try {
			// Configure and open a connection to the site you will send the request
			URL url = new URL(posturl);
			URLConnection urlConnection = url.openConnection();
			// 设置doOutput属性为true表示将使用此urlConnection写入数据
			urlConnection.setDoOutput(true);
			// 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型
			urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			// 得到请求的输出流对象
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(),"utf-8");
			
			// 把数据写入请求的Body
			out.write(content);
			out.flush();
			out.close();

			// 从服务器读取响应
			InputStream inputStream = urlConnection.getInputStream();
			String encoding = urlConnection.getContentEncoding();
			body = IOUtils.toString(inputStream, encoding);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			// 设置通用的请求属性
			conn.setRequestProperty("accept","/");
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
//			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8")), true);
			// 发送请求参数
//			logger.info("post请求【编码前参数】:"+param);
//			param=new String(param.getBytes(),"utf-8");
//			logger.info("post请求【编码后参数】:"+param);
			out.print(URLEncoder.encode(param, "utf-8"));
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
//			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("send post request error",e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
