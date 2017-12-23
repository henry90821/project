package com.iskyshop.module.cms.service.impl;
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
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.service.IInformationClassService;

@Service
@Transactional
public class InformationClassServiceImpl implements IInformationClassService{
	@Resource(name = "informationClassDAO")
	private IGenericDAO<InformationClass> informationClassDao;
	@Transactional(readOnly = false)
	public boolean save(InformationClass informationClass) {
		/**
		 * init other field here
		 */
		try {
			this.informationClassDao.save(informationClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public InformationClass getObjById(Long id) {
		InformationClass informationClass = this.informationClassDao.get(id);
		if (informationClass != null) {
			return informationClass;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.informationClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> informationClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : informationClassIds) {
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
		GenericPageList pList = new GenericPageList(InformationClass.class,construct, query,
				params, this.informationClassDao);
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
	public boolean update(InformationClass informationClass) {
		try {
			this.informationClassDao.update( informationClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<InformationClass> query(String query, Map params, int begin, int max){
		return this.informationClassDao.query(query, params, begin, max);
		
	}
}
