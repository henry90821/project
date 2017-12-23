package com.iskyshop.pay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class CheckURL {
	private static Logger logger = Logger.getLogger(CheckURL.class);

	/**
	 * 检查url是否合法
	 * 
	 * @param urlvalue
	 * @return
	 */
	public static String check(String urlvalue) {

		String inputLine = "";

		try {
			URL url = new URL(urlvalue);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			inputLine = in.readLine().toString();
		} catch (Exception e) {
			logger.error(e);
		}
		/*
		 * 输出对应的参数对应错误： 1.invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 2.true 返回正确信息 3.false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
		 */

		return inputLine;
	}
}