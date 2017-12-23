package com.iskyshop.foundation.events;

import org.springframework.context.ApplicationEvent;

/**
 * 同步订单事件<br/>
 * Created by 亚翔 on 2015/11/13.
 */
public class SynchronizeOrderEvent extends ApplicationEvent {

	private long orderId;

	public SynchronizeOrderEvent(Object source, long orderId) {
		super(source);
		this.orderId = orderId;
	}

	/**
	 * 主订单ID值
	 * 
	 * @return
	 */
	public long getOrderId() {
		return orderId;
	}

	/**
	 * 主订单ID值
	 * 
	 * @param orderId
	 */
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
}
