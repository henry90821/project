package com.iskyshop.foundation.service.impl;


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

import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.service.IGoodsConfigService;

@Service
@Transactional
public class GoodsConfigServiceImpl implements IGoodsConfigService {

	@Resource(name = "goodsConfigDAO")
	private IGenericDAO<GoodsConfig> goodsConfigDAO;
	@Transactional(readOnly = false)
	public boolean save(GoodsConfig goodsConfig) {
		/**
		 * init other field here
		 */
		try {
			this.goodsConfigDAO.save(goodsConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<GoodsConfig> query(String query, Map params, int begin, int max) {
		return this.goodsConfigDAO.query(query, params, begin, max);

	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(GoodsConfig.class, construct,
				query, params, this.goodsConfigDAO);
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
	
	@Transactional(readOnly = true)
	public GoodsConfig getObjById(Long id) {
		GoodsConfig goodsConfig = this.goodsConfigDAO.get(id);
		if (goodsConfig != null) {
			return goodsConfig;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public boolean update(GoodsConfig goodsConfig) {
		try {
			this.goodsConfigDAO.update(goodsConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsConfigDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	

}
