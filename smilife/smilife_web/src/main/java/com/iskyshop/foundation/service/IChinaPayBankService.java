package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.ChinaPayBank;



public interface IChinaPayBankService {
	/**
	 * 保存一个ChinaPayBank，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ChinaPayBank instance);
	
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
	List<ChinaPayBank> query(String query, Map params, int begin, int max);
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public ChinaPayBank getObjById(Long id); 
	
	/***
	 * 
	 * @param chinaPayBank
	 * @return
	 */
	public boolean update(ChinaPayBank chinaPayBank);
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public boolean delete(Long id);
}
