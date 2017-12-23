package com.smi.am.service;


import net.bull.javamelody.MonitoredWithSpring;

/**
 * 定时任务服务
 * @author yanghailong
 *
 */
@MonitoredWithSpring
public interface IScheduleService {

	
	/**
	 * 更新所有已到活动开始时间还未审核的优惠券状态为未通过
	 * @return
	 */
	public void updateAllUnAuditCoupons();
	
	/**
	 * 更新所有已到活动结束时间还未发放的优惠券状态为已过期
	 */
	public void updateAllUnDeliveryCoupons();
	
	/**
	 * 更新所有已到活动开始时间并确认上线的优惠券状态为已发放
	 */
	public void deliveryConfirmOnlineCoupons();
	
	/**
	 * 更新所有已到活动开始时间并确认上线的礼包状态为已发放
	 */
	public void deliveryConfirmOnlineGitPackage();
	
	/**
	 * 更新所有已到活动开始时间还未审核的礼包状态为未通过
	 */
	public void updateAllUnAuditGitPackage();
	
	/**
	 * 更新所有已到活动结束时间还未发放的礼包状态为已过期
	 */
	public void updateAllUnDeliveryGitPackage();
}
