package com.smi.pay.service;

import com.smi.pay.model.Order;
import com.smi.pay.model.Refund;
import com.smilife.core.common.valueobject.BaseValueObject;

import java.util.Map;

public interface XMPayService {

	/**
	 * 获取用户余额信息
	 * 
	 * @throws Exception
	 * @since 2016-04-09
	 */
	public Map getBal(String custId);

	/**
	 * 核对用户名和密码
	 * 
	 * @throws Exception
	 * @since 2016-04-09
	 */
	public boolean checkPassword(String custId, String passWord);

	/**
	 * 发起星美生活支付
	 * 
	 * @throws Exception
	 * @since 2016-04-09
	 */
	public Map pay(Order order);

	/**
	 * 发起星美生活退款
	 * 
	 * @throws Exception
	 * @since 2016-04-09
	 */
	public Map refund(Refund refund, Order order);

	/**
	 * 随便花支付
	 * 
	 * @param order
	 *            支付订单详细参数
	 * @return
	 */
	BaseValueObject sbhPay(Order order);

}
