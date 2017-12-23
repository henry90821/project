package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.service.IZTCGoldLogService;

@Service
@Transactional
public class ZTCGoldLogServiceImpl implements IZTCGoldLogService{
	@Resource(name = "zTCGlodLogDAO")
	private IGenericDAO<ZTCGoldLog> zTCGlodLogDao;
	@Transactional(readOnly = false)
	public boolean save(ZTCGoldLog zTCGlodLog) {
		/**
		 * init other field here
		 */
		try {
			this.zTCGlodLogDao.save(zTCGlodLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ZTCGoldLog getObjById(Long id) {
		ZTCGoldLog zTCGlodLog = this.zTCGlodLogDao.get(id);
		if (zTCGlodLog != null) {
			return zTCGlodLog;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.zTCGlodLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> zTCGlodLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : zTCGlodLogIds) {
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
		GenericPageList pList = new GenericPageList(ZTCGoldLog.class,construct, query,
				params, this.zTCGlodLogDao);
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
	public boolean update(ZTCGoldLog zTCGlodLog) {
		try {
			this.zTCGlodLogDao.update( zTCGlodLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<ZTCGoldLog> query(String query, Map params, int begin, int max){
		return this.zTCGlodLogDao.query(query, params, begin, max);
		
	}
}
