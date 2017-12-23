package com.smilife.core.utils;

import com.smi.tools.kits.CollectionKit;
import com.smi.tools.kits.StrKit;
import com.smilife.core.exception.SmiBusinessCodeException;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 参数校验公共工具类
 * 
 * @author xzr
 * @date 2016/04/22
 */
public class ParamCheckUtil {

	/**
	 * 校验必填项是否为空
	 * 
	 * 支持多参数校验，如("name",name,"mobile"mobile)
	 * 
	 * @param params
	 * 
	 * @return void
	 */
	public static void checkRequired(Object... params) {
		Object key, value;
		if (null != params && params.length > 0) {
			for (int i = 0; i < params.length - 1; i += 2) {
				key = params[i];
				value = params[i + 1];
				if (null == value || StrKit.isBlank(String.valueOf(value))) {
					throw new SmiBusinessCodeException("comm.param.empty", new Object[] { key });
				}
			}
		} else {
			throw new SmiBusinessCodeException("comm.param.err", new Object[] { "param" });
		}
	}

	/**
	 * 校验必填项是否为空
	 * 
	 * 传入的参数用map组装
	 * 
	 * @param paramsMap
	 * 
	 * @return void
	 */
	public static void checkRequstParams(Map<String, Object> paramsMap) {
		if (CollectionKit.isEmpty(paramsMap)) {
			throw new SmiBusinessCodeException("comm.param.err", new Object[] { "param" });
		}
		for (Entry<String, Object> map : paramsMap.entrySet()) {
			if (null == map.getValue() || StrKit.isBlank(String.valueOf(map.getValue()))) {
				throw new SmiBusinessCodeException("comm.param.empty", new Object[] { map.getKey() });
			}
		}
	}

}
