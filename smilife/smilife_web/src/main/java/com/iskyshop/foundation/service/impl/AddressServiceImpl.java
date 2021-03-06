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
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.service.IAddressService;

@Service
@Transactional
public class AddressServiceImpl implements IAddressService {
	private Logger logger = Logger.getLogger(this.getClass());
	@Resource(name = "addressDAO")
	private IGenericDAO<Address> addressDao;

	@Transactional(readOnly = false)
	public boolean save(Address address) {
		/**
		 * init other field here
		 */
		try {
			this.addressDao.save(address);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public Address getObjById(Long id) {
		Address address = this.addressDao.get(id);
		if (address != null) {
			return address;
		}
		return null;
	}

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.addressDao.remove(id);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> addressIds) {
		// TODO Auto-generated method stub
		for (Serializable id : addressIds) {
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
		GenericPageList pList = new GenericPageList(Address.class, construct, query, params, this.addressDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null) {
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
			}
		} else {
			pList.doList(0, -1);
		}
		return pList;
	}

	@Transactional(readOnly = false)
	public boolean update(Address address) {
		try {
			this.addressDao.update(address);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<Address> query(String query, Map params, int begin, int max) {
		return this.addressDao.query(query, params, begin, max);

	}
}
