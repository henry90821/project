package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
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
import com.iskyshop.foundation.domain.RoleGroup;
import com.iskyshop.foundation.service.IRoleGroupService;

@Service
@Transactional
public class RoleGroupServiceImpl implements IRoleGroupService {
	@Resource(name = "roleGroupDAO")
	private IGenericDAO<RoleGroup> roleGroupDao;
	@Transactional(readOnly = false)
	public boolean save(RoleGroup roleGroup) {
		/**
		 * init other field here
		 */
		try {
			this.roleGroupDao.save(roleGroup);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public RoleGroup getObjById(Long id) {
		RoleGroup roleGroup = this.roleGroupDao.get(id);
		if (roleGroup != null) {
			return roleGroup;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.roleGroupDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> roleGroupIds) {
		// TODO Auto-generated method stub
		for (Serializable id : roleGroupIds) {
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
		GenericPageList pList = new GenericPageList(RoleGroup.class,construct, query,
				params, this.roleGroupDao);
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
	public boolean update(RoleGroup roleGroup) {
		try {
			this.roleGroupDao.update(roleGroup);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<RoleGroup> query(String query, Map params, int begin, int max) {
		return this.roleGroupDao.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public RoleGroup getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		return this.roleGroupDao.getBy(construct, propertyName, value);
	}
}
