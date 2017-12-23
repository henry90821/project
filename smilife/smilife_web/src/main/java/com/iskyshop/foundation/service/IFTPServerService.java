package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.FTPServer;

public interface IFTPServerService {
	/**
	 * 保存一个FTPServer，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(FTPServer instance);
	
	/**
	 * 根据一个ID得到FTPServer
	 * 
	 * @param id
	 * @return
	 */
	FTPServer getObjById(Long id);
	
	/**
	 * 删除一个FTPServer
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除FTPServer
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到FTPServer
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个FTPServer
	 * 
	 * @param id
	 *            需要更新的FTPServer的id
	 * @param dir
	 *            需要更新的FTPServer
	 */
	boolean update(FTPServer instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<FTPServer> query(String query, Map params, int begin, int max);
}
