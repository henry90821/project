package com.iskyshop.foundation.service;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.domain.OrderSynLog;

import java.util.List;
import java.util.Map;

/**
 * 订单同步Service
 * 
 * @author liz 2015-11-11
 */
public interface IOrderSynService {

	/**
	 * 保存一个OrderSyn，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 *            OrderSyn
	 * @return 是否保存成功
	 */
	boolean save(OrderSyn instance);

	/**
	 * 更新一个OrderSyn，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 *            OrderSyn
	 * @return 是否保存成功
	 */
	boolean update(OrderSyn instance);

	/**
	 * 根据一个ID得到OrderSyn
	 * 
	 * @param id
	 *            主键
	 * @return OrderSyn
	 */
	OrderSyn getObjById(Long id);

	/**
	 * 根据一个ID删除OrderSyn
	 * 
	 * @param id
	 *            主键
	 * @return 是否成功
	 */
	boolean delete(Long id);

	/**
	 * 通过一个查询对象得到OrderSynList
	 * 
	 * @param properties
	 *            p
	 * @return IPageList
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 通过一个查询对象得到OrderSynList
	 * 
	 * @param query
	 *            sql
	 * @param params
	 *            参数
	 * @param begin
	 *            开始
	 * @param max
	 *            最大数
	 * @return list
	 */
	List<OrderSyn> query(String query, Map params, int begin, int max);

	/**
	 * 批量增加
	 * 
	 * @param orderSynList
	 *            需要保存的{@link OrderSyn}对象集合
	 * @return 返回true表示批量添加成功，返回false则表示失败
	 */
	boolean batchSave(List<OrderSyn> orderSynList);

	/**
	 * 更新{@link OrderSyn}对象并同时添加{@link OrderSynLog}对象数据
	 * 
	 * @param instance
	 * @return
	 */
	boolean updateAndLog(OrderSyn instance);

	/**
	 * 根据订单{@link OrderForm}查询海信待同步订单
	 * 
	 * @param orderId
	 *            需要查询的订单对象数据
	 * @return 返回海信待同步订单对象
	 */
	OrderSyn queryHisenseByApp(OrderForm orderId);
}
