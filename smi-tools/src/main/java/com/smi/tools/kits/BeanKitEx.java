package com.smi.tools.kits;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import com.smi.tools.lang.DateConvert;

/**
 * @ClassName: BeanUtilsEx
 * @Description: BeanUtilEx对BeanUtil工具类的扩展
 * @author wangyun
 * @date 2016-3-4
 * 
 */
public class BeanKitEx extends BeanUtils {
	static {
		// 注入日期转器
		ConvertUtils.register(new DateConvert(), java.util.Date.class);
		ConvertUtils.register(new DateConvert(), java.sql.Date.class);
	}

	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}
}
