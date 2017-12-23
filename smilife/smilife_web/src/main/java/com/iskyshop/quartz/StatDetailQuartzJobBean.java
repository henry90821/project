package com.iskyshop.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: JobDetailQuartzJobBean.java
 * </p>
 * 
 * <p>
 * Description: 每半个小时执行的定时器
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
 * @date 2014年3月1日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class StatDetailQuartzJobBean extends QuartzJobBean {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;

	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			logger.info("半小时定时器执行start..........."+CommUtil.formatLongDate(new Date()));
			this.configService.runTimerByHalfhour();
			logger.info("半小时定时器执行end..........."+CommUtil.formatLongDate(new Date()));
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}
