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
import com.iskyshop.foundation.domain.TransArea;
import com.iskyshop.foundation.service.ITransAreaService;

@Service
@Transactional
public class TransAreaServiceImpl implements ITransAreaService{
	@Resource(name = "transAreaDAO")
	private IGenericDAO<TransArea> transAreaDao;
	@Transactional(readOnly = false)
	public boolean save(TransArea transArea) {
		/**
		 * init other field here
		 */
		try {
			this.transAreaDao.save(transArea);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public TransArea getObjById(Long id) {
		TransArea transArea = this.transAreaDao.get(id);
		if (transArea != null) {
			return transArea;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.transAreaDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> transAreaIds) {
		// TODO Auto-generated method stub
		for (Serializable id : transAreaIds) {
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
		GenericPageList pList = new GenericPageList(TransArea.class, construct,query,
				params, this.transAreaDao);
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
	public boolean update(TransArea transArea) {
		try {
			this.transAreaDao.update( transArea);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<TransArea> query(String query, Map params, int begin, int max){
		return this.transAreaDao.query(query, params, begin, max);
		
	}
}
