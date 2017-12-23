package com.smi.tools.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;

/**
 * @ClassName: DateConvert
 * @Description: 日期转换器
 * @author wangyun
 * @date 2016-3-4
 * 
 */
public class DateConvert implements Converter {

	private Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object convert(Class arg0, Object arg1) {
		String p = (String) arg1;
		if (p == null || p.trim().length() == 0) {
			return null;
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(p.trim());
		} catch (Exception e) {
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				return df.parse(p.trim());
			} catch (ParseException ex) {
				logger.error(ex);
				return null;
			}
		}
	}
}
