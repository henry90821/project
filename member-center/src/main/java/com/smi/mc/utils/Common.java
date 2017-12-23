package com.smi.mc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;


public class Common {

	public static String getUUID() {
		String uu = UUID.randomUUID().toString();
		return uu.substring(0, 8) + uu.substring(9, 13) + uu.substring(14, 18)
				+ uu.substring(19, 23) + uu.substring(24);
	}

	public static Boolean canUsedUnitType(int unit_type_list[], int unit_type) {
		for (int i = 0; i < unit_type_list.length; i++) {
			if (unit_type_list[i] == unit_type) {
				return true;
			}
		}
		return false;
	}

	public static String genRandomNum(int pwd_len, String type) {
		char[] str = null;
		int maxNum = 0;
		if ("NUM".equalsIgnoreCase(type)) {
			maxNum = 10;
			str = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		} else if ("CHAR".equalsIgnoreCase(type)) {
			maxNum = 26;
			str = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
					'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
					'v', 'w', 'x', 'y', 'z' };
		} else if ("ALL".equalsIgnoreCase(type)) {

			maxNum = 36;
			str = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
					'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
					'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
					'7', '8', '9' };
		} else {
			maxNum = 10;
			str = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		}
		int i;
		int count = 0;
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {

			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	public static Map<String, Object> checkParams(Map<String, Object> param,
			String... args) {
		Map<String, Object> reMap = new HashMap<String, Object>();
		String mesg = "";
		String status = "0";
		for (int i = 0; i < args.length; i++) {
			if (StringUtils.isBlank((String) param.get(args[i]))) {
				mesg = "必传参数" + args[i] + "为空";
				status = "1";
				reMap.put("MESSAGE", mesg);
				reMap.put("STATUS", status);
				return reMap;
			}
		}
		return reMap;
	}

	public static String getNowTimeStr2() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new Date());
	}

}
