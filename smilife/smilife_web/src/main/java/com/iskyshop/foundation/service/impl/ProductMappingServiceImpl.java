package com.iskyshop.foundation.service.impl;

import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.service.IProductMappingService;

@Service
@Transactional
public class ProductMappingServiceImpl implements IProductMappingService {

	@Resource(name = "productMappingDAO")
	private IGenericDAO<ProductMapping> productMappingDAO;
	@Transactional(readOnly = false)
	public boolean save(ProductMapping productMapping) {
		/**
		 * init other field here
		 */
		try {
			this.productMappingDAO.save(productMapping);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<ProductMapping> query(String query, Map params, int begin, int max) {
		return this.productMappingDAO.query(query, params, begin, max);

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
				query, params, this.productMappingDAO);
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
	public ProductMapping getObjById(Long id) {
		ProductMapping productMapping = this.productMappingDAO.get(id);
		if (productMapping != null) {
			return productMapping;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public boolean update(ProductMapping productMapping) {
		try {
			this.productMappingDAO.update(productMapping);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.productMappingDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List executeNativeQuery(String nnq, Object[] params, int begin, int max) {
		List ret = this.productMappingDAO.executeNativeQuery(nnq, params, begin, max);
		return  ret == null ? new ArrayList() : ret;
	}

}
