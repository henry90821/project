package com.smi.pay.service;

import com.smi.pay.model.Order;
import com.smi.pay.model.OrderLog;
import com.smi.pay.model.OrderReturn;

import java.util.List;
import java.util.Map;

public interface PayService {
	/**
	 * 检查下单信息
	 * 
	 * @return
	 */
	public OrderReturn check(OrderLog order);

	/**
	 * 保存下单信息
	 * 
	 * @param order
	 */
	public void saveOrderLog(OrderLog order);

	/**
	 * 获取下单信息
	 * 
	 * @param id
	 * @return
	 */
	public OrderLog getOrderLog(Integer id);

	/**
	 * 更新下单信息
	 * 
	 * @param id
	 * @return
	 */
	public void updateOrderLog(OrderLog order);

	/**
	 * 生成正式订单
	 * 
	 * @param order
	 * @return
	 */
	public Order saveOrder(OrderLog order);

	/**
	 * 获取正式订单信息
	 * 
	 * @param id
	 * @return
	 */
	public Order getOrder(Integer id);

	/**
	 * 根据订单事情查询订单
	 * 
	 * @param billNo
	 * @return
	 */
	public Order getOrderByBillNo(String billNo);

	/**
	 * 根据订单请求序列号查询订单
	 * 
	 * @param reqNo
	 * @return
	 */
	Order getOrderByReqNo(String reqNo);

	/**
	 * 支付成功收到回调后，更新订单信息
	 * 
	 * @param order
	 */
	public void updateOrder(Order order);

	/**
	 * 通知业务系统支付成功
	 * 
	 * @param order
	 * @return
	 */
	public void payNotify(Order order);

	/**
	 * 查询下单日志
	 */
	public List queryOrderLog(Map params);

	/**
	 * 查询支付日志
	 */
	public List queryOrder(Map params);
}
