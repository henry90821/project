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
import com.iskyshop.foundation.domain.CouponRecord;
import com.iskyshop.foundation.service.ICouponRecordService;

@Service
@Transactional
public class CouponRecordServiceImpl implements ICouponRecordService {

	@Resource(name = "couponRecordDAO")
	private IGenericDAO<CouponRecord> couponRecordDao;
	@Transactional(readOnly = false)
	public boolean save(CouponRecord couponRecord) {
		/**
		 * init other field here
		 */
		try {
			this.couponRecordDao.save(couponRecord);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public CouponRecord getObjById(Long id) {
		CouponRecord couponRecord = this.couponRecordDao.get(id);
		if (couponRecord != null) {
			return couponRecord;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.couponRecordDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> couponRecordIds) {
		// TODO Auto-generated method stub
		for (Serializable id : couponRecordIds) {
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
		GenericPageList pList = new GenericPageList(CouponRecord.class, construct,
				query, params, this.couponRecordDao);
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
	public boolean update(CouponRecord couponRecord) {
		try {
			this.couponRecordDao.update(couponRecord);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<CouponRecord> query(String query, Map params, int begin, int max) {
		return this.couponRecordDao.query(query, params, begin, max);

	}

}
