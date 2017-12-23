package com.iskyshop.core.base;

import com.iskyshop.core.tools.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Log4jConfigurer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 单元测试父类 Created by Andriy on 15/12/2.
 */
public class BaseTester {
	protected Logger LOGGER = Logger.getLogger(this.getClass());

	/**
	 * 控制台打印信息的时间格式
	 */
	private final String DATE_FORMAT = "HH:mm:ss,sss";

	/**
	 * Log4j日志配置文件路径
	 */
	private final String LOG4J_CONFIG_LOCATION = "classpath:config/*/log4j.properties";

	/**
	 * Spring配置文件路径
	 */
	private final String SPRING_CONTEXT_CONFIG_LOCATION = "classpath:applicationContext*.xml";

	@Before
	public void loadConfigFile() {
		// 加载LOG4J配置文件
		Assert.assertTrue(this.loadLog4jConfig());
		// 加载SPRING配置文件
		Assert.assertTrue(this.loadSpringConfig());
	}

	/**
	 * 获取当前系统时间字符串
	 * 
	 * @return
	 */
	private String getDateStr() {
		return new SimpleDateFormat(this.DATE_FORMAT).format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 控制台打印普通日志
	 * 
	 * @param message
	 */
	private void log(String message) {
		System.out.println(this.getDateStr() + " " + message);
	}

	/**
	 * 控制台打印带异常信息的日志
	 * 
	 * @param message
	 * @param e
	 */
	private void log(String message, Throwable e) {
		if (StringUtils.isNullOrEmpty(e)) {
			this.log(message);
		} else {
			System.err.println(this.getDateStr() + " " + message + "\n" + e);
		}
	}

	/**
	 * 初始化Log4j日志配置
	 * 
	 * @return
	 */
	private boolean loadLog4jConfig() {
		boolean result = false;
		String location = null;

		try {
			PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = pathMatchingResourcePatternResolver.getResources(LOG4J_CONFIG_LOCATION);
			location = resources[0].getFile().getAbsolutePath();
			this.log("从路径[" + location + "]初始化加载Log4j配置！");
			Log4jConfigurer.initLogging(location);
			result = true;
			this.log("初始化日志成功！");
		} catch (IOException e) {
			result = false;
			this.log("初始化Log4j文件失败！", e);
		}
		return result;
	}

	/**
	 * 初始化Spring配置
	 * 
	 * @return
	 */
	private boolean loadSpringConfig() {
		boolean result = false;

		try {
			new FileSystemXmlApplicationContext(this.SPRING_CONTEXT_CONFIG_LOCATION);
			result = true;
		} catch (BeansException e) {
			result = false;
			log("初始化Spring配置出错！", e);
		}
		return result;
	}
}
