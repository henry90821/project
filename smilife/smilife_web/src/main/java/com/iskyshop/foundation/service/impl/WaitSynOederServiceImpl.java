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
import com.iskyshop.foundation.domain.WaitSynOrder;
import com.iskyshop.foundation.service.IWaitSynOrderService;

@Service
@Transactional
public class WaitSynOederServiceImpl implements IWaitSynOrderService {
	@Resource(name = "waitsynoederDAO")
	private IGenericDAO<WaitSynOrder> waitsynoederDAO;

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.waitsynoederDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public WaitSynOrder getObjById(Long id) {
		return this.waitsynoederDAO.get(id);
	}

	@Transactional(readOnly = false)
	public boolean save(WaitSynOrder wsorder) {
		try {
			this.waitsynoederDAO.save(wsorder);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean update(WaitSynOrder wsorder) {
		try {
			this.waitsynoederDAO.update(wsorder);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<WaitSynOrder> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.waitsynoederDAO.query(query, params, begin, max);

	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(WaitSynOrder.class, construct, query, params, this.waitsynoederDAO);
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
	public WaitSynOrder getObjByProperty(String construct, String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.waitsynoederDAO.getBy(construct, propertyName, value);
	}

	@Override
	public boolean saveOrUpdate(WaitSynOrder wsorder) {
		boolean bool = true;
		WaitSynOrder waitSynOrder = this.getObjByProperty(null, "order_id", wsorder.getOrder_id());
		if(null != waitSynOrder){
			waitSynOrder.setOrder_data(wsorder.getOrder_data());
			waitSynOrder.setOms_interface(wsorder.getOms_interface());
			waitSynOrder.setUpdateTime(wsorder.getUpdateTime());
			bool = this.update(waitSynOrder);
		}else{
			bool = this.save(wsorder);
		}
		return bool;
	}
}
