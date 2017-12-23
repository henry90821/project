package com.iskyshop.module.circle.service.impl;
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
import com.iskyshop.module.circle.domain.Circle;
import com.iskyshop.module.circle.service.ICircleService;

@Service
@Transactional
public class CircleServiceImpl implements ICircleService{
	@Resource(name = "circleDAO")
	private IGenericDAO<Circle> circleDao;
	@Transactional(readOnly = false)
	public boolean save(Circle circle) {
		/**
		 * init other field here
		 */
		try {
			this.circleDao.save(circle);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Circle getObjById(Long id) {
		Circle circle = this.circleDao.get(id);
		if (circle != null) {
			return circle;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.circleDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> circleIds) {
		// TODO Auto-generated method stub
		for (Serializable id : circleIds) {
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
		GenericPageList pList = new GenericPageList(Circle.class, construct,query,
				params, this.circleDao);
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
	public boolean update(Circle circle) {
		try {
			this.circleDao.update( circle);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<Circle> query(String query, Map params, int begin, int max){
		return this.circleDao.query(query, params, begin, max);
		
	}
}
