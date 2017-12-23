package com.smi.tools.kits;

import java.math.BigDecimal;

/**
 * 数字相关工具类
 * 
 * @author Looly
 * 
 */
public class MathKit {
	/**
	 * 保留小数位
	 * 
	 * @param number
	 *            被保留小数的数字
	 * @param digit
	 *            保留的小数位数
	 * @return 保留小数后的字符串
	 */
	public static String roundStr(double number, int digit) {
		return String.format("%." + digit + 'f', number);
	}

	/**
	 * 保留小数位
	 * 
	 * @param number
	 *            被保留小数的数字
	 * @param digit
	 *            保留的小数位数
	 * @return 保留小数后的字符串
	 */
	public static double round(double number, int digit) {
		final BigDecimal bg = new BigDecimal(number);
		return bg.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的加法运算保留两位小数。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static String add2(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		double db = b1.add(b2).doubleValue();
		BigDecimal b = new BigDecimal(db);
		return b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
}
