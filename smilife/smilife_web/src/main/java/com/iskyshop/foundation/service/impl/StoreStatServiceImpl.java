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
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.service.IStoreStatService;

@Service
@Transactional
public class StoreStatServiceImpl implements IStoreStatService{
	@Resource(name = "storeStatDAO")
	private IGenericDAO<StoreStat> storeStatDao;
	@Transactional(readOnly = false)
	public boolean save(StoreStat storeStat) {
		/**
		 * init other field here
		 */
		try {
			this.storeStatDao.save(storeStat);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public StoreStat getObjById(Long id) {
		StoreStat storeStat = this.storeStatDao.get(id);
		if (storeStat != null) {
			return storeStat;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.storeStatDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> storeStatIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeStatIds) {
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
		GenericPageList pList = new GenericPageList(StoreStat.class, construct,query,
				params, this.storeStatDao);
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
	public boolean update(StoreStat storeStat) {
		try {
			this.storeStatDao.update( storeStat);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<StoreStat> query(String query, Map params, int begin, int max){
		return this.storeStatDao.query(query, params, begin, max);
		
	}
}
