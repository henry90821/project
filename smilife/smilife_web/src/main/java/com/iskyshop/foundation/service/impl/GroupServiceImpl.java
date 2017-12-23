package com.iskyshop.foundation.service.impl;
import java.io.File;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.lucene.LuceneUtil;

@Service
@Transactional
public class GroupServiceImpl implements IGroupService{
	@Resource(name = "groupDAO")
	private IGenericDAO<Group> groupDao;
	@Resource
	private IGroupGoodsService groupGoodsService;
	@Resource
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Resource
	private IGoodsService goodsService;
	
	@Transactional(readOnly = false)
	public boolean save(Group group) {
		/**
		 * init other field here
		 */
		try {
			this.groupDao.save(group);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Group getObjById(Long id) {
		Group group = this.groupDao.get(id);
		if (group != null) {
			return group;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.groupDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> groupIds) {
		// TODO Auto-generated method stub
		for (Serializable id : groupIds) {
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
		GenericPageList pList = new GenericPageList(Group.class,construct, query,
				params, this.groupDao);
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
	public boolean update(Group group) {
		try {
			this.groupDao.update( group);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<Group> query(String query, Map params, int begin, int max){
		return this.groupDao.query(query, params, begin, max);
		
	}
	
	@Transactional(readOnly = false)
	public void closeGroup(Group group) {
		this.update(group);
		if (group.getGroup_type() == 0) {
			for (GroupGoods gg : group.getGg_list()) {
				gg.setGg_status(-2);
				this.groupGoodsService.update(gg);
			}
			for (Goods goods : group.getGoods_list()) {
				goods.setGroup(null);
				goods.setGroup_buy(0);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.update(goods);
			}
		}
		if (group.getGroup_type() == 1) {
			for (GroupLifeGoods glg : group.getGl_list()) {
				glg.setGroup_status(-2);
				groupLifeGoodsService.update(glg);
			}
		}
	}
}
