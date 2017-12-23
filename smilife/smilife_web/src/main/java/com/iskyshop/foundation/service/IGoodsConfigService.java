package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsConfig;

public interface IGoodsConfigService {
	/**
	 * 保存一个GoodsConfig，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsConfig instance);
	
	/**
	 * 通过一个查询对象得到ChinaPayBank
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsConfig> query(String query, Map params, int begin, int max);
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public GoodsConfig getObjById(Long id); 
	
	/***
	 * 
	 * @param goodsConfig
	 * @return
	 */
	public boolean update(GoodsConfig goodsConfig);
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public boolean delete(Long id);
}
