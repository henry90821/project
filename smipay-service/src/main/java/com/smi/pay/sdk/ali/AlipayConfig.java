package com.smi.pay.sdk.ali;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/* *
 * 类名：AlipayConfig功能：基础配置类详细：设置帐户有关信息及返回路径版本：3.4修改日期：2016-03-08说明：
 * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
@Component
@ConfigurationProperties(locations = { "classpath:sdk_ali.yml" },prefix="alisdk")
public class AlipayConfig {
	//public static final String	FILE_NAME	= "sdk_ali.properties";
	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	@Value(value = "partner")
	public String				partner;

	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	@Value(value = "seller_id")
	public String				seller_id;

	// MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	@Value(value = "key")
	public String				key;

	// 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	@Value(value = "notify_url")
	public String				notify_url;

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	@Value(value = "return_url")
	public String				return_url;

	// 签名方式
	@Value(value = "sign_type")
	public String				sign_type;

	// 字符编码格式 目前支持utf-8
	@Value(value = "input_charset")
	public String				input_charset;

	// 支付类型 ，无需修改
	@Value(value = "payment_type")
	public String				payment_type;

	// 手机调用的接口名
	@Value(value = "wapservice")
	public String				wapservice;

	// PC调用的接口名
	@Value(value = "service")
	public String				service;

	// 退款调用接口名
	@Value(value = "refundservice")
	public String				refundservice;

	// 签名公钥
	@Value(value = "ras_public")
	public String				ras_public;

	// 签名私钥
	@Value(value = "ras_private")
	public String				ras_private;

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getInput_charset() {
		return input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public String getWapservice() {
		return wapservice;
	}

	public void setWapservice(String wapservice) {
		this.wapservice = wapservice;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRefundservice() {
		return refundservice;
	}

	public void setRefundservice(String refundservice) {
		this.refundservice = refundservice;
	}

	public String getRas_public() {
		return ras_public;
	}

	public void setRas_public(String ras_public) {
		this.ras_public = ras_public;
	}

	public String getRas_private() {
		return ras_private;
	}

	public void setRas_private(String ras_private) {
		this.ras_private = ras_private;
	}
	
	/** 操作对象. */
	private static AlipayConfig	config;
	/** 属性文件对象. */
//	private Properties			properties;

	/**
	 * 获取config对象.
	 * 
	 * @return
	 */
    public static void init(AlipayConfig alipayConfig) {
    	if(null==config){
		 config=alipayConfig;
    	}
	}
	public static AlipayConfig getConfig() {
		
		return config;
	}

	/**
	 * 从classpath路径下加载配置参数
	 */
//	public void loadProperties() {
//		InputStream in = null;
//		try {
//			LogUtil.writeLog("从classpath: " + AlipayConfig.class.getClassLoader().getResource("").getPath() + " 获取属性文件" + FILE_NAME);
//			in = AlipayConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
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
//		value = pro.getProperty("alisdk.partner");
//		if (StringUtils.isNotBlank(value)) {
//			this.partner = value;
//		}
//		value = pro.getProperty("alisdk.seller_id");
//		if (StringUtils.isNotBlank(value)) {
//			this.seller_id = value;
//		}
//		value = pro.getProperty("alisdk.key");
//		if (StringUtils.isNotBlank(value)) {
//			this.key = value;
//		}
//		value = pro.getProperty("alisdk.notify_url");
//		if (StringUtils.isNotBlank(value)) {
//			this.notify_url = value;
//		}
//		value = pro.getProperty("alisdk.return_url");
//		if (StringUtils.isNotBlank(value)) {
//			this.return_url = value;
//		}
//		value = pro.getProperty("alisdk.sign_type");
//		if (StringUtils.isNotBlank(value)) {
//			this.sign_type = value;
//		}
//		value = pro.getProperty("alisdk.input_charset");
//		if (StringUtils.isNotBlank(value)) {
//			this.input_charset = value;
//		}
//		value = pro.getProperty("alisdk.payment_type");
//		if (StringUtils.isNotBlank(value)) {
//			this.payment_type = value;
//		}
//		value = pro.getProperty("alisdk.service");
//		if (StringUtils.isNotBlank(value)) {
//			this.service = value;
//		}
//		value = pro.getProperty("alisdk.wapservice");
//		if (StringUtils.isNotBlank(value)) {
//			this.wapservice = value;
//		}
//		value = pro.getProperty("alisdk.refundservice");
//		if (StringUtils.isNotBlank(value)) {
//			this.refundservice = value;
//		}
//		value = pro.getProperty("alisdk.ras_public");
//		if (StringUtils.isNotBlank(value)) {
//			this.ras_public = value;
//		}
//		value = pro.getProperty("alisdk.ras_private");
//		if (StringUtils.isNotBlank(value)) {
//			this.ras_private = value;
//		}
//
//	}
	

}
