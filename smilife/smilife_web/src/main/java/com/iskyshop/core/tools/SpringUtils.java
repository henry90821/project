package com.iskyshop.core.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by Andriy on 15/12/1.
 */
@Component
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	/**
	 * 获取Spring上下文
	 * 
	 * @return
	 */
	public static ApplicationContext getContext() {
		return context;
	}
}
