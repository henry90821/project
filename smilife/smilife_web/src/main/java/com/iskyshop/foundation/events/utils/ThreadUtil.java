package com.iskyshop.foundation.events.utils;

import com.tydic.framework.util.PropertyUtil;
import org.apache.log4j.Logger;

/**
 * Created by Andriy on 15/12/4.
 */
public class ThreadUtil {
	private static final Logger LOGGER = Logger.getLogger(ThreadUtil.class);

	private static final String ORDER_EXECUTOR = "order-executor";

	/**
	 * 判断当前线程是否是订单同步执行器线程
	 * 
	 * @param threadName
	 *            当前正在执行的线程名称
	 * @return 返回true表示是允许执行的线程，false则表示当前线程不是允许执行的执行器线程
	 */
	public static boolean isSyncOrderExecutor(String threadName) {
		LOGGER.info("================开始进行线程执行器校验================");
		boolean result = false;
		String executorName = PropertyUtil.getProperty(ORDER_EXECUTOR);
		LOGGER.info("当前的执行器线程名是：" + threadName + "，允许执行的线程名是：" + executorName);
		result = threadName.contains(executorName);
		LOGGER.info(result ? "线程执行器校验通过！" : "线程执行器验证失败！");
		LOGGER.info("================进行线程执行器校验结束================");
		return result;
	}
}
