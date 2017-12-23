package com.smi.pay.sdk.wx;

import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class MySSLSocketFactory extends SSLSocketFactory {

	/**
	 * H5对应商户SSL证书
	 */
	private static MySSLSocketFactory	h5SSLSocketFactory	= null;

	/**
	 * APP对应商户SSL证书
	 */
	private static MySSLSocketFactory	appSSLSocketFactory	= null;

	private static SSLContext createSContext() {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");

		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			sslcontext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, null);
		}
		catch (KeyManagementException e) {
			e.printStackTrace();
			return null;
		}
		return sslcontext;
	}

	private MySSLSocketFactory(SSLContext sslContext) {
		super(sslContext);
		this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
	}

	public static MySSLSocketFactory getH5Instance() {
		if (h5SSLSocketFactory != null) {
			return h5SSLSocketFactory;
		}
		else {
			return h5SSLSocketFactory = new MySSLSocketFactory(createSSLcontext("H5"));
		}
	}

	public static MySSLSocketFactory getAppInstance() {
		if (appSSLSocketFactory != null) {
			return appSSLSocketFactory;
		}
		else {
			return appSSLSocketFactory = new MySSLSocketFactory(createSSLcontext("APP"));
		}
	}

	public static SSLContext createSSLcontext(String type) {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");
			String keyStorePath = "";
			String password = "";
			// 证书文件(微信商户平台-账户设置-API安全-API证书-下载证书)
			if (type.equals("H5")) {
				keyStorePath = WXConfig.getConfig().getCertpath();
				// 证书密码（默认为商户ID）
				password = WXConfig.getConfig().getPartner();
			}
			else if (type.equals("APP")) {
				keyStorePath = WXConfig.getConfig().getAppcertpath();
				password = WXConfig.getConfig().getApppartner();
			}

			// 实例化密钥库
			KeyStore ks = KeyStore.getInstance("PKCS12");
			// 获得密钥库文件流
			FileInputStream fis = new FileInputStream(keyStorePath);
			// 加载密钥库
			ks.load(fis, password.toCharArray());
			// 关闭密钥库文件流
			fis.close();
			// 实例化密钥库
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			// 初始化密钥工厂
			kmf.init(ks, password.toCharArray());

			// 创建SSLContext
			sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
			sslcontext.init(kmf.getKeyManagers(), null, new SecureRandom());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sslcontext;
	}

}