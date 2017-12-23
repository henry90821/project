package com.iskyshop.pay.bill.util;

public class BillCore {
	// 功能函数。将变量值不为空的参数组成字符串
	public static String appendParam(String returnStr, String paramId, String paramValue) {
		if (!"".equals(returnStr)) {
			if (paramValue != null && !"".equals(paramValue)) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (paramValue != null && !"".equals(paramValue)) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}
}
