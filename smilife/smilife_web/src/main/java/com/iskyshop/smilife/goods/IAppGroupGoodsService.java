package com.iskyshop.smilife.goods;

import java.util.List;
import java.util.Map;

public interface IAppGroupGoodsService {
	/**
	 * 获取指定团购中的商品列表(只返回未开始和正在进行的团购商品，不返回已过期的团购商品)
	 * @method getGroupGoodsList
	 * @return List<Map>
	 */
	public List<Map> getGroupGoodsList(Long group_id,Integer currentPage, Integer pageSize);

}
