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
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGroupInfoService;

@Service
@Transactional
public class GroupInfoServiceImpl implements IGroupInfoService {
	@Resource(name = "groupInfoDAO")
	private IGenericDAO<GroupInfo> groupInfoDao;
	@Transactional(readOnly = false)
	public boolean save(GroupInfo groupInfo) {
		/**
		 * init other field here
		 */
		try {
			this.groupInfoDao.save(groupInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public GroupInfo getObjById(Long id) {
		GroupInfo groupInfo = this.groupInfoDao.get(id);
		if (groupInfo != null) {
			return groupInfo;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.groupInfoDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> groupInfoIds) {
		// TODO Auto-generated method stub
		for (Serializable id : groupInfoIds) {
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
		GenericPageList pList = new GenericPageList(GroupInfo.class,construct, query,
				params, this.groupInfoDao);
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
	public boolean update(GroupInfo groupInfo) {
		try {
			this.groupInfoDao.update(groupInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<GroupInfo> query(String query, Map params, int begin, int max) {
		return this.groupInfoDao.query(query, params, begin, max);

	}

	@Override
	public GroupInfo getObjByProperty(String construct, String propertyName,
			String value) {
		// TODO Auto-generated method stub
		return this.groupInfoDao.getBy(construct, propertyName, value);
	}
	
	@Override
	public GroupInfo getObjByProperty(String construct, String propertyName,
			Object value) {
		return this.groupInfoDao.getBy(construct, propertyName, value);
	}
}
