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
import com.iskyshop.foundation.domain.GoodsInventory;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.service.IGoodsInventoryService;
import com.iskyshop.foundation.service.IProductMappingService;

@Service
@Transactional
public class GoodsInventoryServiceImpl implements IGoodsInventoryService {

	@Resource(name = "goodsInventoryDAO")
	private IGenericDAO<GoodsInventory> goodsInventoryDAO;
	
	@Transactional(readOnly = false)
	public boolean save(GoodsInventory goodsInventory) {
		/**
		 * init other field here
		 */
		try {
			this.goodsInventoryDAO.save(goodsInventory);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<GoodsInventory> query(String query, Map params, int begin, int max) {
		return this.goodsInventoryDAO.query(query, params, begin, max);

	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(ProductMapping.class, construct,
				query, params, this.goodsInventoryDAO);
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
	public GoodsInventory getObjById(Long id) {
		GoodsInventory goodsInventory = this.goodsInventoryDAO.get(id);
		if (goodsInventory != null) {
			return goodsInventory;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public boolean update(GoodsInventory goodsInventory) {
		try {
			this.goodsInventoryDAO.update(goodsInventory);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsInventoryDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean batchDelete(List<Serializable> ids) {
		for (Serializable id : ids) {
			delete((Long) id);
		}
		return true;
	}
	
	public int  executeNativeSQL(String nnq){
		return this.goodsInventoryDAO.executeNativeSQL(nnq);
	}

	@Override
	public List executeNativeQuery(String nnq, Object[] params, int begin,
			int max) {
		// TODO Auto-generated method stub
		return this.goodsInventoryDAO.executeNativeQuery(nnq, params, begin, max);
	}

	
	@Override
	public void batchSave(List instances) {
		this.goodsInventoryDAO.batchInsert(instances);		
	}


	

}
