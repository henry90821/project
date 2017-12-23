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
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.service.ICombinPlanService;

@Service
@Transactional
public class CombinPlanServiceImpl implements ICombinPlanService {
	@Resource(name = "combinPlanDAO")
	private IGenericDAO<CombinPlan> combinPlanDao;
	@Transactional(readOnly = false)
	public boolean save(CombinPlan combinPlan) {
		/**
		 * init other field here
		 */
		try {
			this.combinPlanDao.save(combinPlan);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public CombinPlan getObjById(Long id) {
		CombinPlan combinPlan = this.combinPlanDao.get(id);
		if (combinPlan != null) {
			return combinPlan;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.combinPlanDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> combinPlanIds) {
		// TODO Auto-generated method stub
		for (Serializable id : combinPlanIds) {
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
		GenericPageList pList = new GenericPageList(CombinPlan.class,
				construct, query, params, this.combinPlanDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(CombinPlan combinPlan) {
		try {
			this.combinPlanDao.update(combinPlan);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<CombinPlan> query(String query, Map params, int begin, int max) {
		return this.combinPlanDao.query(query, params, begin, max);

	}
}
