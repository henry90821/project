package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.oms.service.AbstractOmsManagerService;

@Service
@Transactional
public class IntegralGoodsOrderServiceImpl implements IIntegralGoodsOrderService{
	private static Logger logger = Logger.getLogger(IntegralGoodsOrderServiceImpl.class);
	@Resource(name = "integralGoodsOrderDAO")
	private IGenericDAO<IntegralGoodsOrder> integralGoodsOrderDao;
	@Autowired
	private AbstractOmsManagerService integralGoodsOrderOmsServiceImpl;
	
	@Transactional(readOnly = false)
	public boolean save(IntegralGoodsOrder integralGoodsOrder) {
		/**
		 * init other field here
		 */
		try {
			this.integralGoodsOrderDao.save(integralGoodsOrder);
			integralGoodsOrderOmsServiceImpl.insertOrder(integralGoodsOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public IntegralGoodsOrder getObjById(Long id) {
		IntegralGoodsOrder integralGoodsOrder = this.integralGoodsOrderDao.get(id);
		if (integralGoodsOrder != null) {
			return integralGoodsOrder;
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public IntegralGoodsOrder getObjByProperty(String construct, String propertyName, Object value) {
		return this.integralGoodsOrderDao.getBy(construct, propertyName, value);
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.integralGoodsOrderDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> integralGoodsOrderIds) {
		// TODO Auto-generated method stub
		for (Serializable id : integralGoodsOrderIds) {
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
		GenericPageList pList = new GenericPageList(IntegralGoodsOrder.class,construct, query,
				params, this.integralGoodsOrderDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(IntegralGoodsOrder integralGoodsOrder) {
		try {
			this.integralGoodsOrderDao.update( integralGoodsOrder);
			integralGoodsOrderOmsServiceImpl.insertOrder(integralGoodsOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<IntegralGoodsOrder> query(String query, Map params, int begin, int max){
		return this.integralGoodsOrderDao.query(query, params, begin, max);
		
	}
}
