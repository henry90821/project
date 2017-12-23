package com.iskyshop.foundation.events;

import com.iskyshop.foundation.domain.OrderSyn;
import org.springframework.context.ApplicationEvent;

/**
 * 推送订单同步事件<br/>
 * Created by 亚翔 on 2015/12/1.
 */
public class PublishOrderEvent extends ApplicationEvent {

	private OrderSyn orderSyn;

	public PublishOrderEvent(Object source, OrderSyn orderSyn) {
		super(source);
		this.orderSyn = orderSyn;
	}

	/**
	 * 获取待同步订单数据对象
	 * 
	 * @return
	 */
	public OrderSyn getOrderSyn() {
		return orderSyn;
	}
}
