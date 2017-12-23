package com.smilife.core.utils.logger;

import org.slf4j.LoggerFactory;

/**
 * 日志工具类,通过这个类获取到日志对象并进行日志打印<br/>
 * Created by Andriy on 16/4/2.
 */
public class LoggerUtils {

	/**
	 * 根据对象类型创建日志打印对象
	 * 
	 * @param clazz
	 *            需要打印日志的对象类型
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {
		return new Logger(LoggerFactory.getLogger(clazz));
	}
}
