package com.smi.pay.dao;

import com.smi.pay.model.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {

	int deleteByPrimaryKey(Integer id);

	int insert(Order order);

	int update(Order order);

	Order load(Integer id);

	Order getByNO(String billNo);

	Order getLikeNO(String billNo);

	List getAll(Map filter);

	/**
	 * 根据序列号查询订单详情
	 * 
	 * @param reqNo
	 *            请求序列号
	 * @return
	 */
	Order getByReqNo(String reqNo);
}