package com.smilife.core.quartz;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;

/**
 * Created by 亚翔 on 2015/9/22.
 */
public class SmiSchedulerFactoryBean extends SchedulerFactoryBean {

	public void setConfigLocation(String configLocation) throws IOException {
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources(configLocation);
		super.setConfigLocation(resources[0]);
	}
}
