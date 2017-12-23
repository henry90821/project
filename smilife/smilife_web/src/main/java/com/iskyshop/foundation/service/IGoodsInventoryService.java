package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsInventory;


public interface IGoodsInventoryService {
	/**
	 * 保存一个GoodsInventory，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsInventory instance);
	
	/**
	 * 根据一个ID得到GoodsInventory
	 * 
	 * @param id
	 * @return
	 */
	GoodsInventory getObjById(Long id);
	
	/**
	 * 删除一个GoodsInventory
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsInventory
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsInventory
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	/**
	 * 更新一个GoodsInventory
	 * 
	 * @param id
	 *            需要更新的GoldRecord的id
	 * @param dir
	 *            需要更新的GoldRecord
	 */
	boolean update(GoodsInventory instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsInventory> query(String query, Map params, int begin, int max);
	
	public int  executeNativeSQL(String nnq);
	
	public List executeNativeQuery(String nnq, Object[] params, int begin, int max);
	
	public void batchSave(List instances);
}
