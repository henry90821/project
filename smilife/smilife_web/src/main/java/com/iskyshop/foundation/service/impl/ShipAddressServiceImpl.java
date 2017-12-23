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
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.service.IShipAddressService;

@Service
@Transactional
public class ShipAddressServiceImpl implements IShipAddressService{
	@Resource(name = "shipAddressDAO")
	private IGenericDAO<ShipAddress> shipAddressDao;
	@Transactional(readOnly = false)
	public boolean save(ShipAddress shipAddress) {
		/**
		 * init other field here
		 */
		try {
			this.shipAddressDao.save(shipAddress);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ShipAddress getObjById(Long id) {
		ShipAddress shipAddress = this.shipAddressDao.get(id);
		if (shipAddress != null) {
			return shipAddress;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.shipAddressDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> shipAddressIds) {
		// TODO Auto-generated method stub
		for (Serializable id : shipAddressIds) {
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
		GenericPageList pList = new GenericPageList(ShipAddress.class,construct, query,
				params, this.shipAddressDao);
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
	public boolean update(ShipAddress shipAddress) {
		try {
			this.shipAddressDao.update( shipAddress);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<ShipAddress> query(String query, Map params, int begin, int max){
		return this.shipAddressDao.query(query, params, begin, max);
		
	}
    @Transactional(readOnly = true)
    public ShipAddress getObjByProperty(String construct, String propertyName,
                                        Object value) {
        return this.shipAddressDao.getBy(construct, propertyName, value);
    }
}
