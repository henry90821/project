package com.iskyshop.module.weixin.view.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.service.IGoodsBrandCategoryService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * 
 * <p>
 * Title:WapBrandViewAction.java
 * </p>
 * 
 * <p>
 * Description: 移动端品牌页
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jinxinzhe
 * 
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinBrandViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private GoodsViewTools goodsViewTools;

	/**
	 * 移动端品牌页请求
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/brand.htm")
	public ModelAndView brand(HttpServletRequest request, HttpServletResponse response, String gc_id) {
		ModelAndView mv = new JModelAndView("wap/brand.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("audit", 1);
		List<GoodsBrand> brands = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc", params, -1, -1);
		List all_list = new ArrayList();
		Set set = new HashSet();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String words[] = list_word.split(",");
		for (String word : words) {
			Map brand_map = new HashMap();
			List brand_list = new ArrayList();
			for (GoodsBrand gb : brands) {
				if (!StringUtils.isNullOrEmpty(gb.getFirst_word()) && word.equals(gb.getFirst_word().toUpperCase())) {
					Map map = new HashMap();
					map.put("name", gb.getName());
					map.put("id", gb.getId());
					map.put("photo", gb.getBrandLogo().getPath() + "/" + gb.getBrandLogo().getName());
					brand_list.add(map);
					set.add(gb.getFirst_word());
				}
			}
			if (brand_list.size() > 0) {
				brand_map.put("brand_list", brand_list);
				brand_map.put("word", word);
				all_list.add(brand_map);
			}
		}
		Arrays.sort(set.toArray());
		mv.addObject("words", set);
		mv.addObject("all_list", all_list);
		return mv;
	}

	/**
	 * 根据商城品牌列表商品
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/wap/brand_goods.htm")
	public ModelAndView brand_goods(HttpServletRequest request, HttpServletResponse response, String gb_id, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("wap/brand_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (StringUtils.isNullOrEmpty(orderBy)) {
			orderBy = "goods_salenum";
		}
		if (StringUtils.isNullOrEmpty(orderType)) {
			orderType = "desc";
		}
		List<Goods> goods_list = null;
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("gb_id", CommUtil.null2Long(gb_id));
		if ("goods_collect".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.goods_collect desc",
					params, 0, 12);
		}
		if ("goods_salenum".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.goods_salenum desc",
					params, 0, 12);
		}
		if ("store_price".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.store_price "
							+ orderType, params, 0, 12);
		}
		GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil.null2Long(gb_id));
		mv.addObject("objs", goods_list);
		mv.addObject("gb_id", gb_id);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("gb", gb);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param queryType
	 * @return
	 */
	@RequestMapping("/wap/brand_goods_ajax.htm")
	public ModelAndView brand_goods_ajax(HttpServletRequest request, HttpServletResponse response, String begin_count,
			String orderBy, String orderType, String gb_id) {
		ModelAndView mv = new JModelAndView("wap/brand_goods_data.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("gb_id", CommUtil.null2Long(gb_id));
		List<Goods> goods_list = null;
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if ("goods_collect".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.goods_collect desc",
					params, CommUtil.null2Int(begin_count), 6);
		}
		if ("goods_salenum".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.goods_salenum desc",
					params, CommUtil.null2Int(begin_count), 6);
		}
		if ("store_price".equalsIgnoreCase(orderBy)) {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id =:gb_id order by obj.store_price "
							+ orderType, params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

}
