package com.iskyshop.foundation.events.handler;

import com.iskyshop.core.beans.exception.CallException;
import com.iskyshop.core.tools.SpringUtils;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.events.PublishOrderEvent;
import com.iskyshop.foundation.events.utils.ThreadUtil;
import com.iskyshop.foundation.service.IOrderDistributeService;
import com.iskyshop.foundation.service.IOrderSynService;
import com.tydic.framework.util.PropertyUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 订单同步到第三方系统事件处理类<br/>
 * Created by Andriy on 15/12/1.
 */
@Component
public class PublishOrderHandler implements ApplicationListener<PublishOrderEvent> {
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private IOrderSynService orderSynService;

	@Async
	@Override
	public void onApplicationEvent(PublishOrderEvent publishOrderEvent) {
		// 获取当前线程名
		String threadName = Thread.currentThread().getName();
		// 获取当前线程信息
		String threadStr = Thread.currentThread().toString();
		LOGGER.info("===================================接收到异步处理订单发送到第三方系统事件" + threadStr
				+ "===================================");
		OrderSyn orderSyn = null;
		IOrderDistributeService orderDistributeService = null;
		String interfaceName = null;
		Boolean publishResult = false;

		if (ThreadUtil.isSyncOrderExecutor(threadName)) {
			try {
				// 从时间中获取待同步订单数据对象
				orderSyn = publishOrderEvent.getOrderSyn();
				// 根据每个OrderSyn中对应的GoodsConfig进行动态实例化接口来调用
				orderDistributeService = (IOrderDistributeService) SpringUtils.getContext().getBean(
						PropertyUtil.getProperty(orderSyn.getGoodsConfig().getConfigCode()));
				// 获取接口实现类的完整类路径
				interfaceName = orderDistributeService.getClass().getSimpleName();
				LOGGER.info("开始调用" + interfaceName + "接口的distribte方法进行订单推送！");
				// 设置订单同步时间
				orderSyn.setSynTime(new Date());
				// 调用接口进行第三方系统订单数据同步业务
				LOGGER.info("分发到接口的数据:orderInfo:"+orderSyn.getOrderInfo()+",goodsConfig:"+orderSyn.getGoodsConfig());
				publishResult = orderDistributeService.distribte(orderSyn.getOrderInfo(), orderSyn.getGoodsConfig()
						.getSynIUrl());
				// 判断同步结果返回值
				if (publishResult) {
					// 设置同步状态为成功
					orderSyn = this.setSyncOrderStatus(orderSyn, 1);
					// 同步成功则清空失败描述信息
					orderSyn.setSynDesc("");
					LOGGER.info("调用同步接口(" + interfaceName + ")返回TRUE，主键ID为" + orderSyn.getId() + "的待同步订单数据同步成功！");
				} else {
					// 设置同步状态为失败
					orderSyn = this.setSyncOrderStatus(orderSyn, 2);
					// 设置同步失败原因描述
					orderSyn.setSynDesc("调用同步接口(" + interfaceName + ")返回FALSE，接口内部报错！");
					LOGGER.error("调用同步接口(" + interfaceName + ")返回FALSE，主键ID为" + orderSyn.getId() + "的待同步订单数据同步失败！");
				}
			} catch (CallException e) {
				orderSyn = this.setSyncOrderStatus(orderSyn, 2);
				// 设置同步失败原因描述
				orderSyn.setSynDesc("调用同步接口(" + interfaceName + ")抛出异常信息！");
				LOGGER.error("调用同步接口(" + interfaceName + ")抛出异常信息！同步订单失败，待同步订单数据主键ID值为：" + orderSyn.getId(), e);
			} catch (Exception e) {
				orderSyn = this.setSyncOrderStatus(orderSyn, 2);
				orderSyn.setSynDesc("同步订单时发生未知异常！");
				LOGGER.error("同步订单时发生未知异常！", e);
			} finally {
				// 保存待同步订单数据对象和日志信息对象
				this.orderSynService.updateAndLog(orderSyn);
			}
		} else {
			LOGGER.warn("当前线程不是合法的指定执行器线程，不允许进行业务逻辑处理！");
		}
		LOGGER.info("===================================异步发送订单到第三方系统事件处理结束" + threadStr
				+ "===================================");
	}

	/**
	 * 设置订单同步基本状态
	 * 
	 * @param orderSyn
	 * @param status
	 * @return
	 */
	private OrderSyn setSyncOrderStatus(OrderSyn orderSyn, int status) {
		// 设置同步状态为失败
		orderSyn.setSynStatus(status);
		// 计算已同步次数
		orderSyn.setSynCount(orderSyn.getSynCount() + 1);
		return orderSyn;
	}
}
