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
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.service.ISysLogService;

@Service
@Transactional
public class SysLogServiceImpl implements ISysLogService {
	@Resource(name = "sysLogDAO")
	private IGenericDAO<SysLog> sysLogDao;
	@Transactional(readOnly = false)
	public boolean save(SysLog sysLog) {
		/**
		 * init other field here
		 */
		try {
			this.sysLogDao.save(sysLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public SysLog getObjById(Long id) {
		SysLog sysLog = this.sysLogDao.get(id);
		if (sysLog != null) {
			return sysLog;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.sysLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> sysLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : sysLogIds) {
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
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(SysLog.class,construct, query,
				params, this.sysLogDao);
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
	public boolean update(SysLog sysLog) {
		try {
			this.sysLogDao.update(sysLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<SysLog> query(String query, Map params, int begin, int max) {
		return this.sysLogDao.query(query, params, begin, max);

	}
}
