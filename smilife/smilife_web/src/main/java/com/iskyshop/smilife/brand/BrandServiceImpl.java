package com.iskyshop.smilife.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsService;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private IGoodsBrandService goodsBrandService;

    @Autowired
    private IGoodsService goodsService;
    
    
    /**
     * 获取所有被推荐的商品品牌的信息，这些品牌按品牌所属的商品分类进行分组，每个Map对应一个分组
     * @return
     */
	public List<Map> getRecommendedBrands() {
		String hql = "select obj from GoodsBrand obj where obj.recommend = true";
		List<GoodsBrand> brands = this.goodsBrandService.query(hql, null, -1, -1);
		
		Map<String, List<Map>> tmpGC2GB = new HashMap<String, List<Map>>();
		
		for(GoodsBrand gb: brands) {
			GoodsClass gc = gb.getGc();
			List<Map> gbs = tmpGC2GB.get(gc.getClassName());
			if(gbs == null) {
				gbs = new ArrayList<Map>();
				tmpGC2GB.put(gc.getClassName(), gbs);
			}
			
			Map gbInfo = new HashMap(3);
			gbInfo.put("brandId", gb.getId());
			gbInfo.put("brandName", gb.getName());
			Accessory logo = gb.getBrandLogo();
			if(logo != null) {
				gbInfo.put("brandIconUrl", logo.getPath() + "/" + logo.getName());
			} else {
				gbInfo.put("brandIconUrl", "");
			}
			gbs.add(gbInfo);
		}
		
		List<Map> result = new ArrayList<Map>(tmpGC2GB.keySet().size());
		
		for(Map.Entry<String, List<Map>> entry: tmpGC2GB.entrySet()) {
			Map gc2gbInfo = new HashMap(2);
			gc2gbInfo.put("className", entry.getKey());
			gc2gbInfo.put("brands", entry.getValue());
			result.add(gc2gbInfo);
		}
		
		return result;
	}
	

	/**
     * 获取指定商品品牌下的商品，按指定字段进行排序
     * @param brandId
     * @param currentPage 要>=0
     * @param pageSize 要>0
     * @param orderBy 排序字段：1:商品价格, 2:商品销量, 3:商品人气。  不传则默认为3
     * @param String orderType
     * @return
     */
	public IPageList getGoodsOfBrand(Long brandId, Integer currentPage, Integer pageSize, String orderBy, String orderType) {
		if(brandId == null) {
			throw new SmiBusinessException("品牌id不能为null");
		}
		
		if(currentPage == null || currentPage < 0) {
			currentPage = 0;
		}
		
		if(pageSize == null || pageSize <= 0) {
			pageSize = 10;
		}
		
		if(orderBy == null) {
			orderBy = "goods_click";//默认：人气（不用goods_collect）
		} else if("1".equals(orderBy)){
			orderBy = "goods_current_price";
		} else if("2".equals(orderBy)){
			orderBy = "goods_salenum";
		} else {
			orderBy = "goods_click";
		} 
		
		if(orderType == null || !"asc".equals(orderType)) {
			orderType = "desc";
		} 
		
		GoodsQueryObject gqo = new GoodsQueryObject("new Goods(id, goods_name, goods_current_price, goods_salenum, goods_main_photo)",
				String.valueOf(currentPage + 1), null, orderBy, orderType);
		gqo.setPageSize(pageSize);
		
		gqo.addQuery("obj.goods_brand.id", new SysMap("brandId", brandId), "=");
		
		return this.goodsService.list(gqo);
	}
    
}
