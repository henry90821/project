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
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.service.ICouponInfoService;

@Service
@Transactional
public class CouponInfoServiceImpl implements ICouponInfoService {
	@Resource(name = "couponInfoDAO")
	private IGenericDAO<CouponInfo> couponInfoDao;
	@Transactional(readOnly = false)
	public boolean save(CouponInfo couponInfo) {
		/**
		 * init other field here
		 */
		try {
			this.couponInfoDao.save(couponInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public CouponInfo getObjById(Long id) {
		CouponInfo couponInfo = this.couponInfoDao.get(id);
		if (couponInfo != null) {
			return couponInfo;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.couponInfoDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> couponInfoIds) {
		// TODO Auto-generated method stub
		for (Serializable id : couponInfoIds) {
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
		GenericPageList pList = new GenericPageList(CouponInfo.class,
				construct, query, params, this.couponInfoDao);
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
	public boolean update(CouponInfo couponInfo) {
		try {
			this.couponInfoDao.update(couponInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<CouponInfo> query(String query, Map params, int begin, int max) {
		return this.couponInfoDao.query(query, params, begin, max);

	}
	@Transactional(readOnly = false)
	public void batchInsert(List<CouponInfo> couponInfos) {
		this.couponInfoDao.batchInsert(couponInfos,5000);
	}
	@Override
	public List executeNativeQuery(String nnq, Object[] params, int begin,
			int max) {
		return couponInfoDao.executeNativeQuery(nnq, params, begin, max);
	}
}
