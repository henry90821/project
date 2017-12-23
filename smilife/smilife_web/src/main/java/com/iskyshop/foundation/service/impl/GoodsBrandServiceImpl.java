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
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.service.IGoodsBrandService;

@Service
@Transactional
public class GoodsBrandServiceImpl implements IGoodsBrandService{
	@Resource(name = "goodsBrandDAO")
	private IGenericDAO<GoodsBrand> goodsBrandDao;
	@Transactional(readOnly = false)
	public boolean save(GoodsBrand goodsBrand) {
		/**
		 * init other field here
		 */
		try {
			this.goodsBrandDao.save(goodsBrand);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public GoodsBrand getObjById(Long id) {
		GoodsBrand goodsBrand = this.goodsBrandDao.get(id);
		if (goodsBrand != null) {
			return goodsBrand;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsBrandDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> goodsBrandIds) {
		for (Serializable id : goodsBrandIds) {
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
		GenericPageList pList = new GenericPageList(GoodsBrand.class, construct,query, params, this.goodsBrandDao);
		PageObject pageObj = properties.getPageObj();
		pList.doList(pageObj.getCurrentPage(), pageObj.getPageSize());
		
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(GoodsBrand goodsBrand) {
		try {
			this.goodsBrandDao.update( goodsBrand);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<GoodsBrand> query(String query, Map params, int begin, int max){
		return this.goodsBrandDao.query(query, params, begin, max);
		
	}
	
	@Override
	public String getBrandNamesStr(String ids) {
		StringBuffer namesStr=new StringBuffer();
		try{
			List brands=query("select obj.name from GoodsBrand obj where id in ("+ids+")", null, -1, -1);
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
