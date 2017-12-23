package com.iskyshop.foundation.events.publisher;

import com.iskyshop.foundation.domain.OrderSyn;

/**
 * Created by 亚翔 on 2015/11/16.
 */
public interface SynchronizeOrderPublisher {

	/**
	 * 触发同步订单事件
	 * 
	 * @param orderId
	 *            主订单ID值
	 * @return 返回true表示事件发送成功，false表示事件发送失败
	 */
	boolean synchronizeOrder(long orderId);

	/**
	 * 推送订单至指定服务器
	 * 
	 * @param orderSyn
	 *            需要同步的待同步订单对象
	 * @return 返回true表示推送成功，false则表示成功
	 */
	boolean pushOrder(OrderSyn orderSyn);
}
