package com.iskyshop.quartz;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.WaitSynOrder;
import com.iskyshop.foundation.service.IWaitSynOrderService;
import com.iskyshop.oms.service.AbstractOmsManagerService;

/**
 * 
 * <p>
 * Title: JobDetailQuartzJobBean.java
 * </p>
 * 
 * <p>
 * Description: 每三分钟执行的定时器
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
 * @author wuzhipeng
 * 
 * @date 2014年3月1日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class WaitSynOrderQuartzJobBean extends QuartzJobBean {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IWaitSynOrderService waitSynOrderService;
	@Autowired
	private AbstractOmsManagerService waitSynOrderOmsServiceImpl;

	@Override
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		logger.info("订单中心待同步订单start..........."+CommUtil.formatLongDate(new Date()));
			boolean bool = true;
			List<WaitSynOrder> synorder = waitSynOrderService.query("select obj from WaitSynOrder obj", null, -1, -1);
			for (WaitSynOrder waitsynorder : synorder) {
				if(waitsynorder.getOms_interface() == 0){
					bool = waitSynOrderOmsServiceImpl.insertOrder(waitsynorder);
					if(bool){
						waitSynOrderService.delete(waitsynorder.getId());
					}
				}else if(waitsynorder.getOms_interface() == 1){
					bool = waitSynOrderOmsServiceImpl.updateOrder(waitsynorder);
					if(bool){
						waitSynOrderService.delete(waitsynorder.getId());
					}
				}
			}
		logger.info("订单中心待同步订单end..........."+CommUtil.formatLongDate(new Date()));
	}
}
