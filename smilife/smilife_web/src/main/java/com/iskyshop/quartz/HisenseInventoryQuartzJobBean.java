package com.iskyshop.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * @ClassName: HisenseInventoryQuartzJobBean
 * @Description: 更新海信商品库存
 * @author wangkang
 * @date 2015-10-14
 * 
 */
public class HisenseInventoryQuartzJobBean extends QuartzJobBean {
	private Logger				logger	= Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService	configService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("海信商品库存更新定时器执行......start");
		try {
			configService.hisenseInventoryQuartzService();
		}
		catch (Exception e) {
			logger.error("海信商品库存更新:", e);
		}
		logger.info("海信商品库存更新定时器执行......end");
	}
}
