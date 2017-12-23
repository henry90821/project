package com.smi.pay.sdk.wx;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.smi.pay.sdk.ali.AlipayConfig;
@Component
@ConfigurationProperties(locations = { "classpath:sdk_wx.yml" },prefix="wxsdk")
public class WXConfig {

	public static final String	FILE_NAME	= "sdk_wx.properties";
	/** 公众号商户ID */
	@Value(value = "partner")
	private String				partner;
	/** 公众号商户密钥 */
	@Value(value = "partnerkey")
	private String				partnerkey;
	/** 公众号appid */
	@Value(value = "h5appid")
	private String				h5appid;
	/** 公众号appsecret */
	@Value(value = "h5secret")
	private String				h5secret;
	/** 公众号对应商户证书 */
	@Value(value = "certpath")
	private String				certpath;

	/** APP商户ID */
	@Value(value = "apppartner")
	private String				apppartner;
	/** APP商户密钥 */
	@Value(value = "apppartnerkey")
	private String				apppartnerkey;
	/** APPID */
	@Value(value = "appid")
	private String				appid;
	/** APPsecret. */
	@Value(value = "secret")
	private String				secret;
	/** APP(开放平台)对应商户证书 */
	@Value(value = "appcertpath")
	private String				appcertpath;
	@Value(value = "notify_url")
	private String				notify_url;

	/** 操作对象. */
	private static WXConfig		config;
	/** 属性文件对象. */
	//private Properties			properties;

	/**
	 * 获取config对象.
	 * 
	 * @return
	 */
	public static void init(WXConfig wXConfig) {
    	if(null==config){
		 config=wXConfig;
    	}
	}
	public static WXConfig getConfig() {
//		if (null == config) {
//			config = new WXConfig();
//			//config.loadProperties();
//		}
		return config;
	}

//	/**
//	 * 从classpath路径下加载配置参数
//	 */
//	public void loadProperties() {
//		InputStream in = null;
//		try {
//			LogUtil.writeLog("从classpath: " + WXConfig.class.getClassLoader().getResource("").getPath() + " 获取属性文件" + FILE_NAME);
//			in = WXConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
//			if (null != in) {
//				BufferedReader bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
//				properties = new Properties();
//				try {
//					properties.load(bf);
//				}
//				catch (IOException e) {
//					throw e;
//				}
//			}
//			else {
//				LogUtil.writeErrorLog(FILE_NAME + "属性文件未能在classpath指定的目录下 " + WXConfig.class.getClassLoader().getResource("").getPath() + " 找到!");
//				return;
//			}
//			loadProperties(properties);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		finally {
//			if (null != in) {
//				try {
//					in.close();
//				}
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	/**
	 * 根据传入的 {@link #load(java.util.Properties)}对象设置配置参数
	 * 
	 * @param pro
	 */
//	public void loadProperties(Properties pro) {
//		LogUtil.writeLog("开始从属性文件中加载配置项");
//		String value = null;
//		value = pro.getProperty("wxsdk.partner");
//		if (StringUtils.isNotBlank(value)) {
//			this.partner = value;
//		}
//		value = pro.getProperty("wxsdk.partnerkey");
//		if (StringUtils.isNotBlank(value)) {
//			this.partnerkey = value;
//		}
//		value = pro.getProperty("wxsdk.h5appid");
//		if (StringUtils.isNotBlank(value)) {
//			this.h5appid = value;
//		}
//		value = pro.getProperty("wxsdk.h5secret");
//		if (StringUtils.isNotBlank(value)) {
//			this.h5secret = value;
//		}
//		value = pro.getProperty("wxsdk.certpath");
//		if (StringUtils.isNotBlank(value)) {
//			this.certpath = value;
//		}
//		value = pro.getProperty("wxsdk.apppartner");
//		if (StringUtils.isNotBlank(value)) {
//			this.apppartner = value;
//		}
//		value = pro.getProperty("wxsdk.apppartnerkey");
//		if (StringUtils.isNotBlank(value)) {
//			this.apppartnerkey = value;
//		}
//		value = pro.getProperty("wxsdk.appid");
//		if (StringUtils.isNotBlank(value)) {
//			this.appid = value;
//		}
//		value = pro.getProperty("wxsdk.secret");
//		if (StringUtils.isNotBlank(value)) {
//			this.secret = value;
//		}
//		value = pro.getProperty("wxsdk.appcertpath");
//		if (StringUtils.isNotBlank(value)) {
//			this.appcertpath = value;
//		}
//		value = pro.getProperty("wxsdk.notify_url");
//		if (StringUtils.isNotBlank(value)) {
//			this.notify_url = value;
//		}
//
//	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getPartnerkey() {
		return partnerkey;
	}

	public void setPartnerkey(String partnerkey) {
		this.partnerkey = partnerkey;
	}

	public String getH5appid() {
		return h5appid;
	}

	public void setH5appid(String h5appid) {
		this.h5appid = h5appid;
	}

	public String getH5secret() {
		return h5secret;
	}

	public void setH5secret(String h5secret) {
		this.h5secret = h5secret;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

//	public Properties getProperties() {
//		return properties;
//	}
//
//	public void setProperties(Properties properties) {
//		this.properties = properties;
//	}

//	public static String getFileName() {
//		return FILE_NAME;
//	}

//	public static void setConfig(WXConfig config) {
//		WXConfig.config = config;
//	}

	public String getCertpath() {
		return certpath;
	}

	public void setCertpath(String certpath) {
		this.certpath = certpath;
	}

	public String getApppartner() {
		return apppartner;
	}

	public void setApppartner(String apppartner) {
		this.apppartner = apppartner;
	}

	public String getApppartnerkey() {
		return apppartnerkey;
	}

	public void setApppartnerkey(String apppartnerkey) {
		this.apppartnerkey = apppartnerkey;
	}

	public String getAppcertpath() {
		return appcertpath;
	}

	public void setAppcertpath(String appcertpath) {
		this.appcertpath = appcertpath;
	}

}
