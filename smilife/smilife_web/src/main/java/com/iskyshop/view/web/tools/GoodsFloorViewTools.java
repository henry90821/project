package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFloorService;
import com.iskyshop.foundation.service.IGoodsService;

/**
 * 
 * <p>
 * Title: GoodsFloorViewTools.java
 * </p>
 * 
 * <p>
 * Description: 楼层管理json转换工具
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-8-25
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class GoodsFloorViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IGoodsBrandService goodsBrandService;

		
	/**
	 * 专门提供给首页，以加速首页加载
	 * @param json
	 * @return
	 */
	public List<GoodsClass> getGoodsFloorGcs(String json) {
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		if (!StringUtils.isNullOrEmpty(json)) {
			try {
				List<Map> list = Json.fromJson(List.class, json);
				Set<Long> ids = new HashSet<Long>();				
				for (Map map : list) {					
					int count = CommUtil.null2Int(map.get("gc_count"));
					if(count == 0) {
						ids.add(CommUtil.null2Long(map.get("pid")));
					} else {
						for (int i = 1; i <= count; i++) {
							ids.add(CommUtil.null2Long(map.get("gc_id" + i)));
						}
					}					
				}
				
				if(ids.size() > 0) {
					Map params = new HashMap();
					params.put("ids", ids);
					gcs = this.goodsClassService.query("select new GoodsClass(id,className) from GoodsClass obj where obj.id in(:ids)", params, -1, -1);
				}								
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return gcs;
	}
	
	
 	public List<Goods> generic_goods(String json) {
		List<Goods> goods_list = new ArrayList<Goods>();
		if (json != null && !"".equals(json)) {
			try {
				Map map = Json.fromJson(Map.class, json);
				Set<Long> ids = new HashSet<Long>();
				Map params = new HashMap();
				for(Object gId : map.values()) {
					ids.add(CommUtil.null2Long(gId));
				}
				params.put("ids", ids);				
//				goods_list = this.goodsService.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id in (:ids)", params, -1, -1);
				//防止外层调用对返回值进行update操作，故查询所有字段
				goods_list = this.goodsService.query("select obj from Goods obj where obj.id in (:ids)", params, -1, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return goods_list;
	} 	
 	

	public Map generic_goods_list(String json) {
		Map map = new HashMap();
		map.put("list_title", "商品排行");
		if (json != null && !"".equals(json)) {
			try {
				Map list = Json.fromJson(Map.class, json);
				map.put("list_title", CommUtil.null2String(list.get("list_title")));				
				
				Set<Long> ids = new HashSet<Long>();				
				for (int i = 1; i <= 5; i++) {
					Object id = list.get("goods_id" + i);
					if(id != null) {
						ids.add(CommUtil.null2Long(id));
					}					
				}
				
				Map params = new HashMap();
				params.put("ids", ids);
//				List<Goods> temps = this.goodsService.query("select new Goods(id,goods_name,goods_current_price,goods_collect,goods_salenum,goods_main_photo) from Goods obj where obj.id in (:ids)", params, -1, -1);
				//防止外层调用对返回值进行update操作，故查询所有字段
				List<Goods> temps = this.goodsService.query("select obj from Goods obj where obj.id in (:ids)", params, -1, -1);
				
				for(int j = 0; j < temps.size();) {
					Goods t = temps.get(j);
					map.put("goods" + ++j, t);
				}				
			} catch (Exception e) {
				map.put("list_title", "");
				map.put("goods1", null);
				map.put("goods2", null);
				map.put("goods3", null);
				map.put("goods4", null);
				map.put("goods5", null);
				logger.error(e);
			}
		}
		return map;
	}
	
	

	public String generic_adv(String web_url, String json) {
		StringBuilder template = new StringBuilder();
		template.append("<div style='float:left;overflow:hidden;'>");
		if (json != null && !"".equals(json)) {
			try {
				Map map = Json.fromJson(Map.class, json);
				if ("".equals(CommUtil.null2String(map.get("adv_id")))) {
					Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(map.get("acc_id")));
					if (img != null) {
						String url = CommUtil.null2String(map.get("acc_url"));
						template.append("<a href='" + url + "' target='_blank'><img src='"+img.getPath() + "/" + img.getName() + "' /></a>");
					}
				} else {
					AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(map.get("adv_id")));
					AdvertPosition obj = new AdvertPosition();
					obj.setAp_type(ap.getAp_type());
					obj.setAp_status(ap.getAp_status());
					obj.setAp_show_type(ap.getAp_show_type());
					obj.setAp_width(ap.getAp_width());
					obj.setAp_height(ap.getAp_height());
					List<Advert> advs = new ArrayList<Advert>();
					for (Advert temp_adv : ap.getAdvs()) {
						if (temp_adv.getAd_status() == 1 && temp_adv.getAd_begin_time().before(new Date())
								&& temp_adv.getAd_end_time().after(new Date())) {
							advs.add(temp_adv);
						}
					}
					if (advs.size() > 0) {
						if ("img".equals(obj.getAp_type())) {
							if (obj.getAp_show_type() == 0) {// 固定广告
								obj.setAp_acc(advs.get(0).getAd_acc());
								obj.setAp_acc_url(advs.get(0).getAd_url());
								obj.setAdv_id(CommUtil.null2String(advs.get(0).getId()));
							}
							if (obj.getAp_show_type() == 1) {// 随机广告
								Random random = new Random();
								int i = random.nextInt(advs.size());
								obj.setAp_acc(advs.get(i).getAd_acc());
								obj.setAp_acc_url(advs.get(i).getAd_url());
								obj.setAdv_id(CommUtil.null2String(advs.get(i).getId()));
							}
						}
					} else {
						obj.setAp_acc(ap.getAp_acc());
						obj.setAp_text(ap.getAp_text());
						obj.setAp_acc_url(ap.getAp_acc_url());
						Advert adv = new Advert();
						adv.setAd_url(obj.getAp_acc_url());
						adv.setAd_acc(ap.getAp_acc());
						obj.getAdvs().add(adv);
					}
					template.append("<a href='" + obj.getAp_acc_url() + "' target='_blank'><img src='"+obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName() + "' /></a>");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		template.append("</div>");
		return template.toString();
	}
	
	

	public List<GoodsBrand> generic_brand(String json) {
		List<GoodsBrand> brands = new ArrayList<GoodsBrand>();
		if (json != null && !"".equals(json)) {
			try {
				Map map = Json.fromJson(Map.class, json);
				for (int i = 1; i <= 11; i++) {
					String key = "brand_id" + i;
					GoodsBrand brand = this.goodsBrandService.getObjById(CommUtil.null2Long(map.get(key)));
					if (brand != null) {
						brands.add(brand);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return brands;
	}

	
	
	/**
	 * 生成商城首页style2样式单个模块信息
	 * 
	 * @param json
	 * @param module_id
	 * @return
	 */
	public Map generic_style2_goods(List<Map> maps, String module_id) {
		if(maps != null) {
			for (Map map : maps) {
				if (map.get("module_id").equals(module_id)) {
					return map;
				}
			}
		}
		return null;
	}
}
