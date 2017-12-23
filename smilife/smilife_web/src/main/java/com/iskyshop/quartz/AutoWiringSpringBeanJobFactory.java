package com.iskyshop.quartz;

import org.apache.log4j.Logger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 
 * <p>
 * Title: AutoWiringSpringBeanJobFactory.java
 * </p>
 * 
 * <p>
 * Description: 集群版QuartzJob可以@Autowired注入spring托管的实例
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jinxinzhe
 * 
 * @version iskyshop_b2b2c 2.0
 */
public class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

	private Logger logger = Logger.getLogger(this.getClass());
	private transient AutowireCapableBeanFactory beanFactory;

	@Override
	public void setApplicationContext(final ApplicationContext context) {
		try {
			beanFactory = context.getAutowireCapableBeanFactory();
		} catch (IllegalStateException e) {
			logger.error("AutoWiringSpringBeanJobFactory IllegalStateException -> " + e);
		}
	}

	@Override
	protected Object createJobInstance(final TriggerFiredBundle bundle) {
		try {
			final Object job = super.createJobInstance(bundle);
			beanFactory.autowireBean(job);
			return job;
		} catch (BeansException e) {
			logger.error("createJobInstance BeansException -> " + e);
		} catch (Exception e) {
			logger.error("createJobInstance Exception -> " + e);
		}
		return null;
	}

}
