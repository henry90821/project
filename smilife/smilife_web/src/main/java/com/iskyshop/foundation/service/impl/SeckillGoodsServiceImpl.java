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
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;

/**
 * 
 * @author dengyuqi
 *
 */
@Service
@Transactional
public class SeckillGoodsServiceImpl implements ISeckillGoodsService{
	@Resource(name = "seckillGoodsDAO")
	private IGenericDAO<SeckillGoods> seckillGoodsDao;
	@Resource
	private IGoodsService goodsService;
	
	@Transactional(readOnly = false)
	public boolean save(SeckillGoods seckillGoods) {
		/**
		 * init other field here
		 */
		try {
			this.seckillGoodsDao.save(seckillGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public SeckillGoods getObjById(Long id) {
		SeckillGoods seckillGoods = this.seckillGoodsDao.get(id);
		if (seckillGoods != null) {
			return seckillGoods;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.seckillGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> seckillGoodsIds) {
		for (Serializable id : seckillGoodsIds) {
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
		GenericPageList pList = new GenericPageList(SeckillGoods.class,construct, query,
				params, this.seckillGoodsDao);
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
	public boolean update(SeckillGoods seckillGoods) {
		try {
			this.seckillGoodsDao.update(seckillGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<SeckillGoods> query(String query, Map params, int begin, int max){
		return seckillGoodsDao.query(query, params, begin, max);
	}
	
	@Transactional(readOnly = false)
	public void saveOrUpdateSeckill(String id, SeckillGoods gg,
			Goods beforeGoods, Goods goods) {
		if (StringUtils.isNullOrEmpty(id)) {
			boolean isOK = this.save(gg);
			if(isOK){
				goodsService.update(goods);
			}
		} else {
			boolean isOK = this.update(gg);
			if(isOK){
				if(null != beforeGoods){
					goodsService.update(beforeGoods);
				}
				goodsService.update(goods);
			}
		}
	}
}
