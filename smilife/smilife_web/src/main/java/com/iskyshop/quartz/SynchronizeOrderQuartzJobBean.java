package com.iskyshop.quartz;

import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.tools.SpringUtils;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import com.iskyshop.foundation.service.IOrderDistributeService;
import com.iskyshop.foundation.service.IOrderSynService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.tydic.framework.util.PropertyUtil;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时处理待同步订单<br/>
 * Created by Andriy on 15/11/24.
 */
@Component
public class SynchronizeOrderQuartzJobBean extends QuartzJobBean {
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	/**
	 * 订单第一次同步失败后需要重复同步的次数，即订单最大同步次数
	 */
	private final String REPEAT_SYNC_COUNT = "repeat-sync-count";

	@Autowired
	private IOrderSynService orderSynService;
	@Autowired
	private IShipAddressService shipAddressService;

	@Autowired
	private SynchronizeOrderPublisher synchronizeOrderPublisher;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LOGGER.info("===================================开始——进行订单同步任务调度===================================");
		// 首先从OrderSyn中查询需要同步的数据，同步逻辑：synStatus为0（未同步）或2（同步失败），同步失败的订单最多同步3次
		LOGGER.info("开始从数据库中查询出需要同步的订单数据集合！");
		Map<String, Object> param = null;
		String aliasName = null;
		int totalCount = 0;
		List<OrderSyn> orderSynList = null;

		try {
			param = new HashMap<String, Object>();
			// 从配置文件中读取订单同步最大次数
			totalCount = Integer.valueOf(PropertyUtil.getProperty(this.REPEAT_SYNC_COUNT));
			param.put("synCount", totalCount); // 订单最大同步次数
			param.put("deleteStatus", 0); // 数据状态为0表示有效数据，为1则表示逻辑删除
			param.put("synStatus", 1); // 数据同步状态不能为1，即表示未成功的数据
			param.put("synUser", OrderSyn.SYN_USER_DEFAULT_VALUE); // 设置条件查询出手动同步的数据
			param.put("synIng", 3); // 数据同步状态为3，即表示为手动同步的数据
			// String hql =
			// "FROM OrderSyn WHERE (synCount<:synCount AND synUser=:synUser AND synStatus<>:synStatus AND deleteStatus=:deleteStatus) OR (synUser<>:synUser AND synStatus<>:synStatus AND deleteStatus=:deleteStatus)";
			String hql = "FROM OrderSyn WHERE (synCount<:synCount AND synStatus<>:synStatus AND deleteStatus=:deleteStatus) OR (synUser<>:synUser AND synStatus=:synIng AND deleteStatus=:deleteStatus)";
			orderSynList = this.orderSynService.query(hql, param, -1, -1);
			LOGGER.info("需要同步的订单数量为：" + (StringUtils.isNullOrEmpty(orderSynList) ? 0 : orderSynList.size()));
			// 检查是否有需要同步的订单
			if (!StringUtils.isNullOrEmpty(orderSynList)) {
				// 开始遍历待同步订单数据集合
				for (int i = 0; i < orderSynList.size(); i++) {
					LOGGER.info("开始解析待同步订单数据集合中的第" + (i + 1) + "个订单。");
					// 是否允许推送当前订单，默认为TRUE，如果待同步订单的数据准备过程中出现错误则置为FALSE，即表示不符合推送条件
					boolean allowSync = true;
					// 获取一个需要同步的订单数据对象
					OrderSyn orderSyn = orderSynList.get(i);
					// 在执行同步订单数据准备工作前先判断是否是订单APP同步数据，如果是则需先查看对应的海信同步数据是否已经成功
					if (GoodsConfig.CODE_DDAPP.equals(orderSyn.getGoodsConfig().getConfigCode())) {
						OrderSyn hisenseOrderSyn = this.orderSynService.queryHisenseByApp(orderSyn.getOrderForm()); // 如果返回值不为Null则表示对应的海信商品已经推送成功
						// 判断如果没有查询到推送成功的海信商品，则直接中断当前同步操作
						if (StringUtils.isNullOrEmpty(hisenseOrderSyn)) {
							LOGGER.warn("订单主键ID值为" + orderSyn.getOrderForm().getId() + "，订单号为"
									+ orderSyn.getOrderForm().getOrder_id() + "的海信待同步订单未推送成功，故暂时不对订单APP后台进行数据推送！");
							continue;
						}
					}
					// 从配置文件中获取需要实例化的接口实现类别名
					String gconfigCode = orderSyn.getGoodsConfig().getConfigCode();
					aliasName = PropertyUtil.getProperty(gconfigCode);
					if(StringUtils.isNullOrEmpty(aliasName)) {
						LOGGER.debug("未找到处理" + gconfigCode + "商品来源的Handler实现，故忽略！！！");
						continue;
					}
					// 根据每个OrderSyn中对应的GoodsConfig进行动态实例化接口来调用
					IOrderDistributeService orderDistributeService = (IOrderDistributeService) SpringUtils.getContext()
							.getBean(aliasName);
					// 获取订单同步对象对应的订单数据对象
					OrderForm orderForm = orderSyn.getOrderForm();
					// 如果同步报文信息为空则重新获取同步报文信息
					if (StringUtils.isNullOrEmpty(orderSyn.getOrderInfo())) {
						try {
							// 查询出ShipAddress对象
							ShipAddress shipAddress = this.shipAddressService.getObjById(orderForm.getShip_addr_id());
							if (StringUtils.isNullOrEmpty(shipAddress)) {
								throw new SmiBusinessException("主键值为" + orderForm.getId() + "，订单号为"
										+ orderForm.getOrder_id() + "的订单数据不完整，没有正确的分配配送影院字段（即ship_addr_id字段的值不合法）！");
							}
							// 调用接口获取报文信息
							String message = orderDistributeService.createPushMessage(orderForm, shipAddress.getSa_code());
							// 将报文信息设置到数据对象中
							orderSyn.setOrderInfo(message);
							// 保存数据对象
							boolean updateResult = this.orderSynService.update(orderSyn);
							if (updateResult) {
								LOGGER.info("更新主键值为" + orderSyn.getId() + "的待同步订单报文成功！");
							} else {
								LOGGER.error("更新主键值为" + orderSyn.getId() + "的待同步订单报文失败！");
							}
						} catch (Exception e) {
							LOGGER.error("调用" + IOrderDistributeService.class.toString() + "接口的实现类" + aliasName
									+ "创建分发报文时出错！", e);
							allowSync = false;
						}
					}
					// 订单数据准备全部无误的情况下允许推送当前订单
					if (allowSync) {
						LOGGER.info("==================开始进行订单同步事件分发==================");
						// 发布推送订单同步至服务器的事件消息
						boolean syncEventResult = this.synchronizeOrderPublisher.pushOrder(orderSyn);
						// 判断事件发布返回值结果
						if (syncEventResult) { // 返回true表示事件发布成功
							LOGGER.info("发送同步订单ID主键值为" + orderSyn.getId() + "的事件成功！");
						} else {
							LOGGER.error("发送同步订单ID主键值为" + orderSyn.getId() + "的事件失败！");
						}
						LOGGER.info("==================订单同步事件分发结束==================");
					} else {
						LOGGER.error(orderSyn.getId() + "的待同步订单数据准备不完整，无法推送该订单！");
					}
				}
			} else {
				LOGGER.warn("没有需要同步的订单！");
			}
		} catch (NumberFormatException e) {
			LOGGER.error("从配置文件中读取待同步订单可重复发送最大次数出错！", e);
		} catch (BeansException e) {
			LOGGER.error("创建" + IOrderDistributeService.class.toString() + "接口的实现类出错！从配置中读取出来需要实现的子类别名为：" + aliasName, e);
		} catch (Exception e) {
			LOGGER.error("读取OrderSyn待同步订单数据并准备发送同步事件时发生未知异常！", e);
		}
		LOGGER.info("===================================结束——进行订单同步任务调度===================================");
	}
}
