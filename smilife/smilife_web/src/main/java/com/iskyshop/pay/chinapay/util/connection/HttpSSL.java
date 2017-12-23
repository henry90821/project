package com.iskyshop.pay.chinapay.util.connection;

import java.security.GeneralSecurityException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * 以https的方式发送报文
 * 
 */
public class HttpSSL extends CPHttpConnection {

	private static final Logger LOGGER = Logger.getLogger(HttpSSL.class);

	/**
	 * 构造方法，传入接收地址和http参数
	 * 
	 * @param HttpsUrl
	 *            接收请求的https地址,以https开头
	 * @param parameters
	 *            需要发送的http参数,组成格式为key=value&key=value
	 */
	public HttpSSL(String httpsURL, String timeOut) {
		URL = httpsURL;
		this.timeOut = timeOut;
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = new X509TrustManager[] { new CPX509TrustManager() };
			sslContext.init(null, xtmArray, new java.security.SecureRandom());
		} catch (GeneralSecurityException gse) {
			LOGGER.error("gse=[" + gse.toString() + "]");
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(new CPHostnameVerifier());
	}

	public byte[] getReceiveData() {
		return receiveData;
	}
}
