package com.smilife.core.listeners;

import com.smilife.core.utils.WebContextUtils;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by Andriy on 16/4/4.
 */
@WebListener
public class WebContextListener implements ServletContextListener {
	private static final Logger LOGGER = LoggerUtils.getLogger(WebContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		// 初始化WEB项目的上下文路径
		String contextPath = sce.getServletContext().getContextPath();
		LOGGER.info("初始化Web项目上下文路径。上下文路径是:'{}'", contextPath);
		WebContextUtils.setWebContext(contextPath);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("WebContextListener已经被销毁。");
	}
}
