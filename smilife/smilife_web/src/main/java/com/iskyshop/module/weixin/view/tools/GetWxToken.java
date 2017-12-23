package com.iskyshop.module.weixin.view.tools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;

/**
 * 
 * <p>
 * Title: GetWxToken.java
 * </p>
 * 
 * <p>
 * Description:微信相关获取的accessToken
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jinxinzhe
 * 
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class GetWxToken {
	private Logger logger = Logger.getLogger(this.getClass());
	private static GetWxToken getAccessToken = new GetWxToken(); // 创建单例
	// 以下为微信商城获取的token信息。
	private String wxAccessToken = ""; // 存放token
	private Date wxDate = null; // 超时时间

	// 获取单例
	public static GetWxToken instance() {
		return getAccessToken;
	}

	// 获取微商城accessToken
	public String getWxToken(String appId, String appSecret) {
		/*
		 * String appId = CommUtil.null2String(this.configService.getSysConfig() .getWeixin_appId()); String appSecret =
		 * CommUtil.null2String(this.configService .getSysConfig().getWeixin_appSecret());
		 */
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret="
				+ appSecret;
		try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
			http.setRequestMethod("GET"); // 必须是get方式请求
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); // 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			Map map = Json.fromJson(HashMap.class, message);
			String token = CommUtil.null2String(map.get("access_token"));
			is.close();
			if (!StringUtils.isNullOrEmpty(token)) {
				this.wxAccessToken = token;
				// 设置20分钟后重新获取token
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 20);
				this.wxDate = cal.getTime();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return this.wxAccessToken;
	}
}
