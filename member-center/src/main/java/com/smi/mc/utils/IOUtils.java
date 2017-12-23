package com.smi.mc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class IOUtils {

	/**
	 * 获取报文字符串
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestParam(HttpServletRequest request) throws IOException {
		StringBuffer ioString = new StringBuffer();
		InputStream is = request.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		while ((s = br.readLine()) != null) {
			ioString.append(s);
		}
		String paramStr = ioString.toString();
		return paramStr;
	}

}
