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
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.service.ICouponService;

@Service
@Transactional
public class CouponServiceImpl implements ICouponService {
	@Resource(name = "couponDAO")
	private IGenericDAO<Coupon> couponDao;
	@Transactional(readOnly = false)
	public boolean save(Coupon coupon) {
		/**
		 * init other field here
		 */
		try {
			this.couponDao.save(coupon);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Coupon getObjById(Long id) {
		Coupon coupon = this.couponDao.get(id);
		if (coupon != null) {
			return coupon;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.couponDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> couponIds) {
		// TODO Auto-generated method stub
		for (Serializable id : couponIds) {
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
		GenericPageList pList = new GenericPageList(Coupon.class, construct,
				query, params, this.couponDao);
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
	public boolean update(Coupon coupon) {
		try {
			this.couponDao.update(coupon);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<Coupon> query(String query, Map params, int begin, int max) {
		return this.couponDao.query(query, params, begin, max);

	}
}
