package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;

@Service
@Transactional
public class ActivityGoodsServiceImpl implements IActivityGoodsService {
	private Logger logger = Logger.getLogger(this.getClass());
	@Resource(name = "activityGoodsDAO")
	private IGenericDAO<ActivityGoods> activityGoodsDao;

	@Transactional(readOnly = false)
	public boolean save(ActivityGoods activityGoods) {
		/**
		 * init other field here
		 */
		try {
			this.activityGoodsDao.save(activityGoods);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public ActivityGoods getObjById(Long id) {
		ActivityGoods activityGoods = this.activityGoodsDao.get(id);
		if (activityGoods != null) {
			return activityGoods;
		}
		return null;
	}

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.activityGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> activityGoodsIds) {
		for (Serializable id : activityGoodsIds) {
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
		GenericPageList pList = new GenericPageList(ActivityGoods.class, construct, query, params, this.activityGoodsDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	@Transactional(readOnly = false)
	public boolean update(ActivityGoods activityGoods) {
		try {
			this.activityGoodsDao.update(activityGoods);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<ActivityGoods> query(String query, Map params, int begin, int max) {
		return this.activityGoodsDao.query(query, params, begin, max);

	}
}
