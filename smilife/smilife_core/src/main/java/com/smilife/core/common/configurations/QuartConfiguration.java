package com.smilife.core.common.configurations;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时任务配置对象<br/>
 * Created by Andriy on 16/9/6.
 */
@Configuration
public class QuartConfiguration {
	private static final Logger LOGGER = LoggerUtils.getLogger(QuartConfiguration.class);

	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		LOGGER.info("============初始化QUARTZ及配置信息============");
		final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setExposeSchedulerInRepository(true);
		return schedulerFactoryBean;
	}
}
