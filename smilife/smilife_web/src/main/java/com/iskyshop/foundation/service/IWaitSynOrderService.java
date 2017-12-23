package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.WaitSynOrder;

public interface IWaitSynOrderService {
	public boolean save(WaitSynOrder wsorder);

	public boolean delete(Long id);

	public boolean update(WaitSynOrder wsorder);

	IPageList list(IQueryObject properties);

	public WaitSynOrder getObjById(Long id);

	public WaitSynOrder getObjByProperty(String construct,String propertyName,String value);

	public List<WaitSynOrder> query(String query, Map params, int begin, int max);
	
	public boolean saveOrUpdate(WaitSynOrder wsorder);
}
