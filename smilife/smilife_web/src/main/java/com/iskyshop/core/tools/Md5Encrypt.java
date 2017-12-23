/**
 * Alipay.com Inc. Copyright (c) 2004-2005 All Rights Reserved.
 * 
 * <p>
 * Created on 2005-7-9
 * </p>
 */
package com.iskyshop.core.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <p>
 * Title: Md5Encrypt.java
 * </p>
 * 
 * <p>
 * Description: MD5加密类 功能：将支付宝提交的相关参数按照传入编码进行MD5加密 接口名称：标准实物双接口 版本：2.0 日期：2008-12-25 作者：支付宝公司销售部技术支持团队 联系：0571-26888888
 * 版权：支付宝公司
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
public final class Md5Encrypt {
	private Md5Encrypt() {
	}

	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param text
	 *            明文
	 * 
	 * @return 密文
	 */
	public static String md5(String text) {
		MessageDigest msgDigest = null;

		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("System doesn't support MD5 algorithm.");
		}

		try {
			msgDigest.update(text.getBytes("utf-8")); // 注意改接口是按照utf-8编码形式加密
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("System doesn't support your  EncodingException.");
		}

		byte[] bytes = msgDigest.digest();
		// String md5Str = new String(encodeHex(bytes));
		String md5Str = new String(bufferToHex(bytes, 0, bytes.length));
		return md5Str;
	}

	private static String bufferToHex(byte[] bytes, int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = DIGITS[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>>
											// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = DIGITS[bt & 0xf]; // 取字节中低 4 位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	/**
	 * 16进制编码，将给定的字节数组进行16进制编码返回
	 * @param data 字节数组
	 * @return 十六进制编码字符数组
	 */
	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}
}