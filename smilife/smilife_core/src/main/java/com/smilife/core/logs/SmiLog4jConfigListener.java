package com.smilife.core.logs;

import javax.servlet.ServletContextEvent;

import org.springframework.web.util.Log4jConfigListener;

/**
 * Created by Andriy on 15/7/8.
 */
@SuppressWarnings("deprecation")
public class SmiLog4jConfigListener extends Log4jConfigListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		SmiLog4jWebConfigurer.initLogging(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}
}
