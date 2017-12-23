package com.smilife.core.utils;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring上下文管理
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
	private static final Logger LOGGER = LoggerUtils.getLogger(ApplicationContextUtil.class);

	private static ApplicationContext context;// 声明一个静态变量保存

	public ApplicationContextUtil() {
		LOGGER.info("初始化ApplicationContextUtil类。");
	}

	public void setApplicationContext(ApplicationContext contex) throws BeansException {
		ApplicationContextUtil.context = contex;
	}

	/**
	 * 根据指定类型获取Spring中管理的Bean对象实例
	 * 
	 * @param requiredType
	 *            需要获取Bean实例的对象类型
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}
}
