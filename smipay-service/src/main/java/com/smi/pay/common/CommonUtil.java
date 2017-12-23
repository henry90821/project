package com.smi.pay.common;

import com.smi.pay.sdk.wx.WXConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommonUtil {

	public static final String FILE_NAME = "common.properties";

	// 默认配置的是UTF-8
	public static String encoding_UTF8 = "UTF-8";

	public static String encoding_GBK = "GBK";

	public static SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd");

	public static SimpleDateFormat tformat = new SimpleDateFormat("yyyyMMddHHmmss");

	public static SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static String ips;

	private static String ipsFlag;

	private static String xm_hy_host;

	private static String xm_jf_host;

	private static String head;

	private static String reserveTime;

	private static String expMin;

	private static String xm_sbh_host;

    /**
     * 星美钱包支付成功回调地址
     */
//	private static String smiWalletNotifyUrl;

	/** 操作对象. */
	private static CommonUtil config;
	/** 属性文件对象. */
	private Properties properties;

	/**
	 * 获取config对象.
	 * 
	 * @return
	 */
	public static CommonUtil getConfig() {
		if (null == config) {
			config = new CommonUtil();
			config.loadProperties();
		}
		return config;
	}

	/**
	 * 从classpath路径下加载配置参数
	 */
	public void loadProperties() {
		InputStream in = null;
		try {
			LogUtil.writeLog(
					"从classpath: " + WXConfig.class.getClassLoader().getResource("").getPath() + " 获取属性文件" + FILE_NAME);
			in = WXConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
			if (null != in) {
				BufferedReader bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
				properties = new Properties();
				try {
					properties.load(bf);
				} catch (IOException e) {
					throw e;
				}
			} else {
				LogUtil.writeErrorLog(FILE_NAME + "属性文件未能在classpath指定的目录下 "
						+ WXConfig.class.getClassLoader().getResource("").getPath() + " 找到!");
				return;
			}
			loadProperties(properties);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据传入的 {@link #load(java.util.Properties)}对象设置配置参数
	 * 
	 * @param pro
	 */
	public void loadProperties(Properties pro) {
		LogUtil.writeLog("开始从属性文件中加载配置项");
		String value = null;
		value = pro.getProperty("ips");
		if (StringUtils.isNotBlank(value)) {
			this.ips = value;
		}
		value = pro.getProperty("ips_flag");
		if (StringUtils.isNotBlank(value)) {
			this.ipsFlag = value;
		}
		value = pro.getProperty("xm_hy_host");
		if (StringUtils.isNotBlank(value)) {
			this.xm_hy_host = value;
		}
		value = pro.getProperty("xm_jf_host");
		if (StringUtils.isNotBlank(value)) {
			this.xm_jf_host = value;
		}
		value = pro.getProperty("xm_sbh_host");
		if (StringUtils.isNotBlank(value)) {
			this.xm_sbh_host = value;
		}
		value = pro.getProperty("head");
		if (StringUtils.isNotBlank(value)) {
			this.head = value;
		}
		value = pro.getProperty("reserve_time");
		if (StringUtils.isNotBlank(value)) {
			this.reserveTime = value;
		}
		value = pro.getProperty("exp_min");
		if (StringUtils.isNotBlank(value)) {
			this.expMin = value;
		}
//		value = pro.getProperty("smi_wallet_notify_url");
//		if (StringUtils.isNotBlank(value)) {
//			this.smiWalletNotifyUrl = value;
//		}
	}

	public static String getIps() {
		return ips;
	}

	public static void setIps(String ips) {
		CommonUtil.ips = ips;
	}

	public static String getIpsFlag() {
		return ipsFlag;
	}

	public static void setIpsFlag(String ipsFlag) {
		CommonUtil.ipsFlag = ipsFlag;
	}

	public static String getXm_hy_host() {
		return xm_hy_host;
	}

	public static void setXm_hy_host(String xm_hy_host) {
		CommonUtil.xm_hy_host = xm_hy_host;
	}

	public static String getXm_jf_host() {
		return xm_jf_host;
	}

	public static void setXm_jf_host(String xm_jf_host) {
		CommonUtil.xm_jf_host = xm_jf_host;
	}

	public static String getExpMin() {
		return expMin;
	}

	public static void setExpMin(String expMin) {
		CommonUtil.expMin = expMin;
	}

	public static String getHead() {
		return head;
	}

	public static void setHead(String head) {
		CommonUtil.head = head;
	}

	public static String getXm_sbh_host() {
		return xm_sbh_host;
	}

	public static void setXm_sbh_host(String xm_sbh_host) {
		CommonUtil.xm_sbh_host = xm_sbh_host;
	}

    /**
     * 星美钱包支付成功回调地址
     */
//	public static String getSmiWalletNotifyUrl() {
//		return smiWalletNotifyUrl;
//	}

	// 后台服务异步加调地址
	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */

	public static DefaultHttpClient httpclient = new DefaultHttpClient();

	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}

	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * 
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}

	/**
	 * 获取当前日期 yyyyMMdd
	 * 
	 * @param date
	 * @return String
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(date);
		return strDate;
	}

	public static String getContextPath(HttpServletRequest request) {
		String path = getConfig().getHead() + "://" + request.getServerName() + request.getContextPath();
		String hostName = request.getServerName();
		if (hostName.startsWith("192") || hostName.startsWith("localhost") || hostName.startsWith("127")) {
			path = getConfig().getHead() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
		}
		return path;
	}

	public static String getExceptionStr(Exception ex) {
		String sOut = "";
		StackTraceElement[] trace = ex.getStackTrace();
		for (StackTraceElement s : trace) {
			sOut += "\tat " + s + "\r\n";
			if (sOut.length() > 300) {
				break;
			}
		}
		if (sOut.length() > 300) {
			sOut = sOut.substring(0, 300);
		}
		return sOut;
	}

	/**
	 * 补位，形成固定位数的字符串
	 * 
	 * @param num
	 * @return
	 */
	public static String formatStr(String str, Integer num) {
		String returnStr = str;
		if (str.length() < num) {
			for (int i = 0; i < num - str.length(); i++) {
				returnStr = "0" + returnStr;
			}
		}
		return returnStr;
	}

	/**
	 * 获取客户端IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteHost(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				// System.out.println("ServletUtil类247行 temp数据的键=="+en+" 值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}

	public static boolean isExpire(String expDate, Date createTime) {
		boolean flag = false;
		String expTime = expDate;
		if (StringUtils.isBlank(expDate)) {
			expTime = getDefaultExpTime(createTime).toString();
		}
		try {
			Long et = Long.parseLong(expTime); // 超期时间
			Long ct = new Date().getTime();// 当前时间
			Long rt = Long.parseLong(getConfig().reserveTime);// 预留支付时间
			if (et - ct - rt * 1000 < 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static Long getDefaultExpTime(Date createTime) {
		return createTime.getTime() + 60 * 1000 * Long.parseLong(getConfig().expMin);
	}

}
