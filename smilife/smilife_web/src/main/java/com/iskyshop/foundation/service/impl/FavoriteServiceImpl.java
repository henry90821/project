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
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.service.IFavoriteService;

@Service
@Transactional
public class FavoriteServiceImpl implements IFavoriteService {
	@Resource(name = "favoriteDAO")
	private IGenericDAO<Favorite> favoriteDao;
	@Transactional(readOnly = false)
	public boolean save(Favorite favorite) {
		/**
		 * init other field here
		 */
		try {
			this.favoriteDao.save(favorite);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Favorite getObjById(Long id) {
		Favorite favorite = this.favoriteDao.get(id);
		if (favorite != null) {
			return favorite;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.favoriteDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> favoriteIds) {
		// TODO Auto-generated method stub
		for (Serializable id : favoriteIds) {
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
		GenericPageList pList = new GenericPageList(Favorite.class, construct,
				query, params, this.favoriteDao);
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
	public boolean update(Favorite favorite) {
		try {
			this.favoriteDao.update(favorite);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<Favorite> query(String query, Map params, int begin, int max) {
		return this.favoriteDao.query(query, params, begin, max);

	}
}
