package com.smilife.bcp.service.common;

import com.tydic.framework.util.PropertyUtil;
import org.apache.log4j.Logger;

/**
 * Created by 亚翔 on 2015/8/24.
 */
public class BaseServiceImpl {
	private Logger logger;

    /**
	 * 获取日志对象
	 * 
	 * @param clazz
	 *            需要写日志的CLASS对象
	 * @return
	 */
	protected Logger getLogger(Class clazz) {
		if (null == this.logger && null != clazz) {
			this.logger = Logger.getLogger(clazz);
		}
		return this.logger;
	}

	/**
	 * 写调试级别信息日志
	 * 
	 * @param message
	 */
	protected void debug(String message) {
		this.getLogger(this.getClass()).debug(message);
	}

	/**
	 * 写入带异常信息的调试日志
	 * 
	 * @param message
	 * @param throwable
	 */
	protected void debug(String message, Throwable throwable) {
		this.getLogger(this.getClass()).debug(message, throwable);
	}

	/**
	 * 写警告级别信息日志
	 * 
	 * @param message
	 */
	protected void warn(String message) {
		this.getLogger(this.getClass()).warn(message);
	}

	/**
	 * 写入带异常信息的警告日志
	 * 
	 * @param message
	 * @param throwable
	 */
	protected void warn(String message, Throwable throwable) {
		this.getLogger(this.getClass()).warn(message, throwable);
	}

	/**
	 * 写消息级别信息日志
	 * 
	 * @param message
	 */
	protected void info(String message) {
		this.getLogger(this.getClass()).info(message);
	}

	/**
	 * 写入带异常信息的消息日志
	 * 
	 * @param message
	 * @param throwable
	 */
	protected void info(String message, Throwable throwable) {
		this.getLogger(this.getClass()).info(message, throwable);
	}

	/**
	 * 写跟踪级别信息日志
	 * 
	 * @param message
	 */
	protected void trace(String message) {
		this.getLogger(this.getClass()).trace(message);
	}

	/**
	 * 写入带异常信息的跟踪日志
	 * 
	 * @param message
	 * @param throwable
	 */
	protected void trace(String message, Throwable throwable) {
		this.getLogger(this.getClass()).trace(message, throwable);
	}

	/**
	 * 写错误级别信息日志
	 * 
	 * @param message
	 */
	protected void error(String message) {
		this.getLogger(this.getClass()).error(message);
	}

	/**
	 * 写入带异常信息的错误日志
	 * 
	 * @param message
	 * @param throwable
	 */
	protected void error(String message, Throwable throwable) {
		this.getLogger(this.getClass()).error(message, throwable);
	}
}
