package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;

public interface IGoodsService {
	/**
	 * 保存一个Goods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Goods instance);

	/**
	 * 根据一个ID得到Goods
	 * 
	 * @param id
	 * @return
	 */
	Goods getObjById(Long id);

	/**
	 * 删除一个Goods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 执行native代码
	 * @param sql
	 * @return
	 */
	public List executeNativeQuery(String sql);
	
	/**
	 * 批量删除Goods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Goods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Goods
	 * 
	 * @param id
	 *            需要更新的Goods的id
	 * @param dir
	 *            需要更新的Goods
	 */
	boolean update(Goods instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Goods> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Goods getObjByProperty(String construct, String propertyName, Object value);

	/**
	 * 修改订单状态
	 * @param orderForm
	 */
	public void order_cancel(OrderForm orderForm);
	
	/**
	 * 还原商品库存
	 * @param orderForm
	 */
	public void recover_goods_inventory(OrderForm orderForm, String type);
	
	/**
	 * 提交订单减库存
	 * @param of
	 */
	public String subtract_goods_inventory(OrderForm of);
	
	/**
	 * 执行sql
	 * @param sql
	 * @return
	 */
	public boolean executeSql(String sql);
	
	/**
	 *  创建商品二维码
	 * @param goods_id
	 * @param serverRealPath
	 * @return
	 */
	public boolean createGoodsQR(Long goods_id,String serverRealPath);
}
