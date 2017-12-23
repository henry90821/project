package com.smi.mc.utils;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smi
 *
 */
public class HttpClientUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static BasicCookieStore cookieStore;

	private static CloseableHttpClient httpClient = null;

	/**
	 * create a httpclient
	 * 
	 * @param supportCookies
	 * @param supportHttps
	 * @return
	 */
	public static CloseableHttpClient create(boolean supportCookies, boolean supportHttps) {

		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		if (supportCookies) {
			cookieStore = new BasicCookieStore();
			httpClientBuilder.setDefaultCookieStore(cookieStore).build();
		}

		if (supportHttps) {
			try {
				SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
					// 信任所有
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();

				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

				httpClientBuilder.setSSLSocketFactory(sslsf);
			} catch (Exception e) {
				logger.error("create https client failed.", e);
			}
		}

		httpClient = httpClientBuilder.build();

		return httpClient;
	}

	/**
	 * create a default httpClient whitout cookies and https support.
	 * 
	 * @return
	 */
	public static CloseableHttpClient create() {
		return create(true, false);
	}

	public static String httpGet(String uri) {

		try (CloseableHttpClient httpClient = create()) {

			return httpGet(httpClient, uri);
		} catch (Exception e) {
			throw new RuntimeException("httpGet error:", e);
		}
	}

	public static String httpGet(CloseableHttpClient httpClient, String uri) {
		try {
			return httpGet(httpClient, new URI(uri), null);
		} catch (Exception e) {
			throw new IllegalArgumentException("uri error!", e);
		}

	}

	public static String httpGet(CloseableHttpClient httpClient, String uri, Map<String, String> header) {
		try {
			return httpGet(httpClient, new URI(uri), header);
		} catch (Exception e) {
			throw new IllegalArgumentException("uri error!", e);
		}

	}

	public static String httpGet(CloseableHttpClient httpClient, URI uri, Map<String, String> header) {

		HttpGet httpGet = new HttpGet(uri);

		setDefaultConfig(httpGet);
		setDefaultHeader(httpGet);
		if (!isMapEmpty(header))
			setHeader(httpGet, header);

		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			// response status
			int responseStatus = response.getStatusLine().getStatusCode();
			if (responseStatus == HttpStatus.SC_OK) {

				return EntityUtils.toString(entity, "UTF-8");

			} else if ((responseStatus == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (responseStatus == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (responseStatus == HttpStatus.SC_SEE_OTHER)
					|| (responseStatus == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header lastHeader = response.getLastHeader("location");
				String movedUrl = lastHeader.getValue();

				return httpGet(httpClient, movedUrl);
			} else {
				String message = EntityUtils.toString(entity, "UTF-8");
				logger.error("response status=" + responseStatus + ",message=" + message);

				return message;
			}

		} catch (Exception e) {
			throw new RuntimeException("httpGet error!", e);
		}
	}

	public static String httpPost(String uri, Object params) {
		try (CloseableHttpClient httpClient = create()) {

			return httpPost(httpClient, uri, null, params);
		} catch (Exception e) {
			throw new RuntimeException("post error:", e);
		}
	}

	public static String httpPost(String uri, Map<String, String> header, Map<String, String> params) {
		try (CloseableHttpClient httpClient = create()) {

			return httpPost(httpClient, uri, header, params);
		} catch (Exception e) {
			throw new RuntimeException("post error:", e);
		}
	}

	public static String httpPost(CloseableHttpClient httpClient, String uri, Map<String, String> header,
			Object params) {

		try {
			return httpPost(httpClient, new URI(uri), header, params);
		} catch (Exception e) {
			throw new IllegalArgumentException("uri error:", e);
		}
	}

	public static String httpPost(CloseableHttpClient httpClient, URI uri, Map<String, String> header, Object params) {

		HttpPost httpPost = new HttpPost(uri);

		setDefaultConfig(httpPost);
		setDefaultHeader(httpPost);

		if (!isMapEmpty(header)) {
			setHeader(httpPost, header);
		}

		if (params != null) {
			setParams(httpPost, params);
		}

		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			// response status
			int responseStatus = response.getStatusLine().getStatusCode();
			if (responseStatus == HttpStatus.SC_OK) {

				return EntityUtils.toString(entity, "UTF-8");

			} else if ((responseStatus == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (responseStatus == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (responseStatus == HttpStatus.SC_SEE_OTHER)
					|| (responseStatus == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header lastHeader = response.getLastHeader("location");
				String movedUrl = lastHeader.getValue();

				return httpPost(httpClient, movedUrl, header, params);
			} else {
				String message = EntityUtils.toString(entity, "UTF-8");
				logger.error("response status=" + responseStatus + ",message=" + message);

				return message;
			}

		} catch (Exception e) {
			throw new RuntimeException("httpPost error!", e);
		}

	}

	// ==================================== common private method
	// =====================================
	private static void setDefaultHeader(AbstractHttpMessage httpMsg) {
		httpMsg.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		httpMsg.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpMsg.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,es;q=0.4,zh-TW;q=0.2");
		// httpMsg.setHeader("Connection","keep-alive");
	}

	private static void setHeader(AbstractHttpMessage httpMsg, Map<String, String> header) {
		Header[] heads = createHeader(header);
		if (heads != null) {
			httpMsg.setHeaders(heads);
		}
	}

	private static Header[] createHeader(Map<String, String> header) {

		if (header == null || header.isEmpty()) {
			return null;
		}

		Header[] headers = new Header[header.size()];

		int n = 0;
		for (Map.Entry<String, String> s : header.entrySet()) {
			headers[n] = new BasicHeader(s.getKey(), s.getValue());
			n++;
		}

		return headers;
	}

	private static boolean isMapEmpty(Map<String, ?> map) {
		return (map == null || map.isEmpty());
	}

	private static void setDefaultConfig(HttpRequestBase httpRequest) {
		RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();// 设置请求和传输超时时间
		httpRequest.setConfig(config);
	}

	private static void setParams(HttpPost requestBase, Object params) {
		try {
			StringEntity entity = new StringEntity(params.toString());
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			requestBase.setEntity(entity);
		} catch (Exception e) {
			logger.error("setParams error:", e);
			throw new RuntimeException("setParams error:", e);
		}
		// }

	}

	public enum HttpMethod {
		GET, POST, PUT, DELETE;
	}

	public static void main(String[] args) {
		// System.out.println(httpPost("http://search.youth.cn/cse/search?q=&s=6006151520371175686&stp=1&sti=10080&nsid=0",
		// null, null));
		// if(!cookieStore.getCookies().isEmpty()){
		// System.out.println("........."+cookieStore.getCookies().toString());
		// }
	}

}