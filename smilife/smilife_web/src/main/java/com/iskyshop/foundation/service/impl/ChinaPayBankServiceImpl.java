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
import com.iskyshop.foundation.domain.ChinaPayBank;

import com.iskyshop.foundation.service.IChinaPayBankService;

@Service
@Transactional
public class ChinaPayBankServiceImpl implements IChinaPayBankService {

	@Resource(name = "chinaPayBankDAO")
	private IGenericDAO<ChinaPayBank> chinaPayBankDAO;
	@Transactional(readOnly = false)
	public boolean save(ChinaPayBank chinaPayBank) {
		/**
		 * init other field here
		 */
		try {
			this.chinaPayBankDAO.save(chinaPayBank);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<ChinaPayBank> query(String query, Map params, int begin, int max) {
		return this.chinaPayBankDAO.query(query, params, begin, max);

	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(ChinaPayBank.class, construct,
				query, params, this.chinaPayBankDAO);
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
	public ChinaPayBank getObjById(Long id) {
		ChinaPayBank chinaPayBank = this.chinaPayBankDAO.get(id);
		if (chinaPayBank != null) {
			return chinaPayBank;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public boolean update(ChinaPayBank chinaPayBank) {
		try {
			this.chinaPayBankDAO.update(chinaPayBank);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.chinaPayBankDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
