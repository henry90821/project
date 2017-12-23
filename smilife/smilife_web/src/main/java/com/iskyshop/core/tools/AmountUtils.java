package com.iskyshop.core.tools;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 金额处理工具类
 * 
 * @author panmin
 * @version [版本号, 2014-4-16]
 * @since [产品/模块版本]
 */
public class AmountUtils {

	/**
	 * 将元为单位的转换为分 （乘100）
	 */
	public static String changeY2F(Double amount) {
		BigInteger value = changeY2FDecimal(amount);
		return value == null ? null : value.toString();
	}

	/**
	 * 将元为单位的转换为分 （乘100）
	 */
	public static BigInteger changeY2FDecimal(Double amount) {
		if (amount == null)
			return null;
		return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toBigInteger();
	}

	/**
	 * 将元为单位的转换为分 （乘100）
	 */
	public static Long changeY2FLong(Double amount) {
		BigInteger value = changeY2FDecimal(amount);
		return value == null ? null : value.longValue();
	}

	/**
	 * 将元为单位的转换为分 （乘100）
	 */
	public static String changeY2F(String amount) {
		return changeY2F(Double.valueOf(amount));
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static String changeF2Y(Long amount) {
		Double value = changeF2YDecimal(amount);
		return value.toString();
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static String changeF2Y2(Long amount) {
		Long value = changeF2YDecimal2(amount);
		return value.toString();
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static Double changeF2YDecimal(Long amount) {
		if (amount == null)
			return null;
		return BigDecimal.valueOf(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static Long changeF2YDecimal2(Long amount) {
		if (amount == null)
			return null;
		return BigDecimal.valueOf(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).longValue();
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static Double changeF2YDouble(Long amount) {
		Double value = changeF2YDecimal(amount);
		return value;
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 */
	public static String changeF2Y(String amount) {
		return changeF2Y(Long.valueOf(amount));
	}

	/**
	 * 将元转为12位数分
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeYT12F(Double amount) {
		Integer intHao = Integer.parseInt(changeY2F(amount));
		return StringUtils.rPadZero(intHao, 12);
	}

}
