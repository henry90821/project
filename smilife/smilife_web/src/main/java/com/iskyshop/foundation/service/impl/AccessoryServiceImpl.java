package com.iskyshop.foundation.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.service.IAccessoryService;

@Service
@Transactional
public class AccessoryServiceImpl implements IAccessoryService {
	
	private Logger logger = Logger.getLogger(AccessoryServiceImpl.class);
	
	@Resource(name = "accessoryDAO")
	private IGenericDAO<Accessory> accessoryDAO;

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.accessoryDAO.remove(id);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public Accessory getObjById(Long id) {
		return this.accessoryDAO.get(id);
	}

	@Transactional(readOnly = false)
	public boolean save(Accessory acc) {
		try {
			this.accessoryDAO.save(acc);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean update(Accessory acc) {
		try {
			this.accessoryDAO.update(acc);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<Accessory> query(String query, Map params, int begin, int max) {
		return this.accessoryDAO.query(query, params, begin, max);

	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Accessory.class, construct, query, params, this.accessoryDAO);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
		} else {
			pList.doList(0, -1);
		}
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public Accessory getObjByProperty(String construct, String propertyName, String value) {
		return this.accessoryDAO.getBy(construct, propertyName, value);
	}

}
