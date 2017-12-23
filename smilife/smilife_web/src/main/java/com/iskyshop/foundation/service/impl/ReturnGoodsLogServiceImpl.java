package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.service.IReturnGoodsLogService;

@Service
@Transactional
public class ReturnGoodsLogServiceImpl implements IReturnGoodsLogService{
	@Resource(name = "returnGoodsLogDAO")
	private IGenericDAO<ReturnGoodsLog> returnGoodsLogDao;
	@Transactional(readOnly = false)
	public boolean save(ReturnGoodsLog returnGoodsLog) {
		/**
		 * init other field here
		 */
		try {
			this.returnGoodsLogDao.save(returnGoodsLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ReturnGoodsLog getObjById(Long id) {
		ReturnGoodsLog returnGoodsLog = this.returnGoodsLogDao.get(id);
		if (returnGoodsLog != null) {
			return returnGoodsLog;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.returnGoodsLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> returnGoodsLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : returnGoodsLogIds) {
			delete((Long) id);
		}
		return true;
	}
	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		String construct = properties.getConstruct();
		GenericPageList pList = new GenericPageList(ReturnGoodsLog.class,construct, query,
				params, this.returnGoodsLogDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(ReturnGoodsLog returnGoodsLog) {
		try {
			this.returnGoodsLogDao.update( returnGoodsLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<ReturnGoodsLog> query(String query, Map params, int begin, int max){
		return this.returnGoodsLogDao.query(query, params, begin, max);
		
	}
	
	@Transactional(readOnly = true)
	public ReturnGoodsLog getObjByProperty(String construct, String propertyName, Object value) {
		return this.returnGoodsLogDao.getBy(construct, propertyName, value);
	}

	public int getCountByNativeSql(String strSql) {
		List list = this.returnGoodsLogDao.executeNativeQuery(strSql, null, -1, -1);
		if (!StringUtils.isNullOrEmpty(list)) {
			return Integer.parseInt(list.get(0).toString());
		} else {
			return 0;
		}
	}
}
