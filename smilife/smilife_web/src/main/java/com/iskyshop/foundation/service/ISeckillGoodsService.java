package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SeckillGoods;

/**
 * 
 * @author dengyuqi
 *
 */
public interface ISeckillGoodsService {
	/**
	 * 保存一个SeckillGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SeckillGoods instance);
	
	/**
	 * 根据一个ID得到SeckillGoods
	 * 
	 * @param id
	 * @return
	 */
	SeckillGoods getObjById(Long id);
	
	/**
	 * 删除一个SeckillGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SeckillGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SeckillGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SeckillGoods
	 * 
	 * @param id
	 *            需要更新的SeckillGoods的id
	 * @param dir
	 *            需要更新的SeckillGoods
	 */
	boolean update(SeckillGoods instance);
	
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SeckillGoods> query(String query, Map params, int begin, int max);

	/**
	 * 添加或更新秒杀活动
	 * @param id 主键id
	 * @param gg 秒杀商品
	 * @param beforeGoods 更换前的商品
	 * @param goods 当前选中的商品
	 */
	void saveOrUpdateSeckill(String id, SeckillGoods gg, Goods beforeGoods,
			Goods goods);
}
