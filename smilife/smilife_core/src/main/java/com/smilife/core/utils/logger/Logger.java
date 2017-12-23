package com.smilife.core.utils.logger;

/**
 * 日志打印对象,项目中所有的日志打印都需要使用该类<br/>
 * Created by Andriy on 16/5/4.
 */
public class Logger {

	/**
	 * 日志记录器对象
	 */
	private org.slf4j.Logger logger;

	/**
	 * 创建日志记录器
	 * 
	 * @param logger
	 */
	public Logger(org.slf4j.Logger logger) {
		this.logger = logger;
	}

	/**
	 * 根据指定的格式和参数在<span style='color:green'>TRACE</span>级别上记录消息。
	 *
	 * @param format
	 *            格式化的字符串
	 * @param arguments
	 *            动态参数列表
	 */
	public void trace(String format, Object... arguments) {
		this.logger.trace(format, arguments);
	}

	/**
	 * 记录附带有异常堆栈信息的<span style='color:green'>TRACE</span>级别地址
	 * 
	 * @param msg
	 *            需要记录的异常描述信息
	 * @param t
	 *            异常堆栈信息
	 */
	public void trace(String msg, Throwable t) {
		this.logger.trace(msg, t);
	}

	/**
	 * 根据指定的格式和参数在<span style='color:green'>DEBUG</span>级别上记录消息。
	 *
	 * @param format
	 *            格式化的字符串
	 * @param arguments
	 *            动态参数列表
	 */
	public void debug(String format, Object... arguments) {
		this.logger.debug(format, arguments);
	}

	/**
	 * 记录附带有异常堆栈信息的<span style='color:green'>DEBUG</span>级别地址
	 *
	 * @param msg
	 *            需要记录的异常描述信息
	 * @param t
	 *            异常堆栈信息
	 */
	public void debug(String msg, Throwable t) {
		this.logger.debug(msg, t);
	}

	/**
	 * 根据指定的格式和参数在<span style='color:green'>INFO</span>级别上记录消息。
	 *
	 * @param format
	 *            格式化的字符串
	 * @param arguments
	 *            动态参数列表
	 */
	public void info(String format, Object... arguments) {
		this.logger.info(format, arguments);
	}

	/**
	 * 记录附带有异常堆栈信息的<span style='color:green'>INFO</span>级别地址
	 *
	 * @param msg
	 *            需要记录的异常描述信息
	 * @param t
	 *            异常堆栈信息
	 */
	public void info(String msg, Throwable t) {
		this.logger.info(msg, t);
	}

	/**
	 * 根据指定的格式和参数在<span style='color:yellow'>WARN</span>级别上记录消息。
	 *
	 * @param format
	 *            格式化的字符串
	 * @param arguments
	 *            动态参数列表
	 */
	public void warn(String format, Object... arguments) {
		this.logger.warn(format, arguments);
	}

	/**
	 * 记录附带有异常堆栈信息的<span style='color:yellow'>WARN</span>级别地址
	 *
	 * @param msg
	 *            需要记录的异常描述信息
	 * @param t
	 *            异常堆栈信息
	 */
	public void warn(String msg, Throwable t) {
		this.logger.warn(msg, t);
	}

	/**
	 * 根据指定的格式和参数在<span style='color:red'>ERROR</span>级别上记录消息。
	 *
	 * @param format
	 *            格式化的字符串
	 * @param arguments
	 *            动态参数列表
	 */
	public void error(String format, Object... arguments) {
		this.logger.error(format, arguments);
	}

	/**
	 * 记录附带有异常堆栈信息的<span style='color:red'>ERROR</span>级别地址
	 *
	 * @param msg
	 *            需要记录的异常描述信息
	 * @param t
	 *            异常堆栈信息
	 */
	public void error(String msg, Throwable t) {
		this.logger.error(msg, t);
	}

}
