package com.iskyshop.foundation.service;

import com.iskyshop.foundation.domain.OrderSynLog;

/**
 * 订单同步日志Service<br/>
 * Created by Andriy on 15/12/1.
 */
public interface IOrderSynLogService {

	/**
	 * 保存一个OrderSynLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 *            OrderSynLog
	 * @return 是否保存成功
	 */
	boolean save(OrderSynLog instance);
}
