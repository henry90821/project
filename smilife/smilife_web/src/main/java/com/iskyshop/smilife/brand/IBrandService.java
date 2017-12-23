package com.iskyshop.smilife.brand;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;

public interface IBrandService {

	/**
	 * 获取所有被推荐的商品品牌的信息，这些品牌按品牌所属的商品分类进行分组，每个Map对应一个分组
	 * @return
	 */
	public List<Map> getRecommendedBrands();
	
	/**
	 * 获取指定商品品牌下的商品，按指定字段进行排序
	 * @param brandId
	 * @param currentPage 要>=0
	 * @param pageSize 要>0
	 * @param orderBy 排序字段：1:商品价格, 2:商品销量, 3:商品人气。  不传则默认为3
	 * @param String orderType
	 * @return
	 */
	public IPageList getGoodsOfBrand(Long brandId, Integer currentPage, Integer pageSize, String orderBy, String orderType);
}
