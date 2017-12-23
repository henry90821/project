package com.iskyshop.foundation.events.publisher.impl;

import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.events.PublishOrderEvent;
import com.iskyshop.foundation.events.SynchronizeOrderEvent;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 订单同步事件触发类 Created by 亚翔 on 2015/11/16.
 */
@Component
public class SynchronizeOrderPublisherImpl implements ApplicationEventPublisherAware, SynchronizeOrderPublisher {
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public boolean synchronizeOrder(long orderId) {
		LOGGER.info("===================================开始准备发送订单同步事件===================================");
		boolean result = false;

		try {
			LOGGER.info("需要发送同步事件的订单ID值为：" + orderId);
			SynchronizeOrderEvent event = new SynchronizeOrderEvent(this, orderId);
			this.applicationEventPublisher.publishEvent(event);
			result = true;
			LOGGER.info("===================================发送订单同步事件成功！===================================");
		} catch (Exception e) {
			LOGGER.error("===================================发送同步订单事件失败，需要同步的订单ID值为：" + orderId
					+ "===================================", e);
		}
		LOGGER.info("===================================结束订单同步发送事件===================================");
		return result;
	}

	@Override
	public boolean pushOrder(OrderSyn orderSyn) {
		LOGGER.info("===================================开始准备推送订单到指定服务器事件===================================");
		boolean result = false;
		String message = null;
		String url = null;

		try {
			// 获取同步报文
			message = orderSyn.getOrderInfo();
			// 获取同步目的地URL
			url = orderSyn.getGoodsConfig().getSynIUrl();
			LOGGER.info("===================================需要同步的报文内容===================================\n" + message
					+ "\n===================================报文内容结束===================================");
			LOGGER.info("同步服务器的URL地址：" + url);
			PublishOrderEvent event = new PublishOrderEvent(this, orderSyn);
			this.applicationEventPublisher.publishEvent(event);
			result = true;
		} catch (Exception e) {
			LOGGER.error("===================================推送订单到指定服务器事件失败===================================");
		}
		LOGGER.info("===================================结束推送订单到指定服务器事件===================================");
		return result;
	}
}
