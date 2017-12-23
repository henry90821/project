package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.CouponRecord;

public interface ICouponRecordService {
	/**
	 * 保存一个CouponRecord，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CouponRecord instance);
	
	/**
	 * 根据一个ID得到CouponRecord
	 * 
	 * @param id
	 * @return
	 */
	CouponRecord getObjById(Long id);
	
	/**
	 * 删除一个CouponRecord
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CouponRecord
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CouponRecord
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CouponRecord
	 * 
	 * @param id
	 *            需要更新的CouponRecord的id
	 * @param dir
	 *            需要更新的CouponRecord
	 */
	boolean update(CouponRecord instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CouponRecord> query(String query, Map params, int begin, int max);
}
