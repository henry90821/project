package com.smi.am.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.service.IScheduleService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 定时任务配置类
 * @author yanghailong
 *
 */
@RestController
@RequestMapping(value = "/cronjob")
@Api(value = "定时器接口")
@Configuration
@EnableScheduling
public class SchedulingConfig {
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private IScheduleService iScheduleService;
	
	@ApiOperation(value = "更新已到活动开始时间还未审核通过的优惠券和礼包为未通过")
	@RequestMapping(value = "/updateAllUnAuditCouponsAndGitpackage", method = { RequestMethod.POST })
	@Scheduled(cron = "0 0 */1 * * ?")
	public void updateAllUnAuditCouponsAndGitpackage(){
		LOGGER.info("定时器start:每隔一小时执行定时任务，update已到活动开始时间还未审核通过的优惠券和礼包。时间是：" +new Date() );
		iScheduleService.updateAllUnAuditCoupons();
		iScheduleService.updateAllUnAuditGitPackage();
		LOGGER.info("定时器end:每隔一小时执行定时任务，update已到活动开始时间还未审核通过的优惠券和礼包。时间是："+new Date());
	}
	
	@ApiOperation(value = "更新已到活动结束时间还未发放的优惠券和礼包为已过期")
	@RequestMapping(value = "/updateAllUnDeliveryCouponsAndGitpackage", method = { RequestMethod.POST })
	@Scheduled(cron = "0 0 */1 * * ?")
	public void updateAllUnDeliveryCouponsAndGitpackage(){
		LOGGER.info("定时器start:每隔一小时执行定时任务，update已到活动结束时间还未发放的优惠券和礼包。时间是：" +new Date());
		iScheduleService.updateAllUnDeliveryCoupons();
		iScheduleService.updateAllUnDeliveryGitPackage();
		LOGGER.info("定时器end:每隔一小时执行定时任务，update已到活动结束时间还未发放的优惠券和礼包。时间是："+new Date());
	}
	
	@ApiOperation(value = "更新活动时间已到并确认上线的优惠券和礼包的发放状态为已发放")
	@RequestMapping(value = "/deliveryCouponsAndGitPackageJob", method = { RequestMethod.POST })
	@Scheduled(cron = "0 0 */1 * * ?")
	public void deliveryCouponsAndGitPackageJob(){
		LOGGER.info("定时器start:每隔一小时执行定时任务，更新活动时间已到并确认上线的优惠券的发放状态。时间是：" +new Date());
		iScheduleService.deliveryConfirmOnlineCoupons();
		LOGGER.info("定时器end:每隔一小时执行定时任务，更新活动时间已到并确认上线的优惠券的发放状态。时间是："+new Date());
		
		LOGGER.info("定时器start:每隔一小时执行定时任务，更新活动时间已到并确认上线的礼包的发放状态。时间是：" +new Date());
		iScheduleService.deliveryConfirmOnlineGitPackage();
		LOGGER.info("定时器end:每隔一小时执行定时任务，更新活动时间已到并确认上线的礼包的发放状态。时间是："+new Date());
		
	}
	
}
