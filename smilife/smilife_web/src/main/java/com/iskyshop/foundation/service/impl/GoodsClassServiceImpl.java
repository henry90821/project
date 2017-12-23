package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
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
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IGoodsClassService;

@Service
@Transactional
public class GoodsClassServiceImpl implements IGoodsClassService {
	@Resource(name = "goodsClassDAO")
	private IGenericDAO<GoodsClass> goodsClassDao;
	@Transactional(readOnly = false)
	public boolean save(GoodsClass goodsClass) {
		/**
		 * init other field here
		 */
		try {
			this.goodsClassDao.save(goodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public GoodsClass getObjById(Long id) {
		GoodsClass goodsClass = this.goodsClassDao.get(id);
		if (goodsClass != null) {
			return goodsClass;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> goodsClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsClassIds) {
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
		GenericPageList pList = new GenericPageList(GoodsClass.class,construct, query,
				params, this.goodsClassDao);
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
	public boolean update(GoodsClass goodsClass) {
		try {
			this.goodsClassDao.update(goodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<GoodsClass> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.goodsClassDao.query(query, params, begin, max);
	}

	@Override
	@Transactional(readOnly = true)
	public GoodsClass getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		return this.goodsClassDao.getBy(construct, propertyName, value);
	}
	
	@Override
	public String getClassNamesStr(String ids) {
		StringBuffer namesStr=new StringBuffer();
		try{
			List brands=query("select className from GoodsClass obj where id in ("+ids+")", null, -1, -1);
			if(brands==null || brands.size()<=0)
				return "";
				
			for(int i=0;i<brands.size();i++){
				if(i>0)
					namesStr.append(",");
				namesStr.append(brands.get(i));
			}
				
		}catch(Exception e){
			return "";
		}
		
		return namesStr.toString();
	}
}
