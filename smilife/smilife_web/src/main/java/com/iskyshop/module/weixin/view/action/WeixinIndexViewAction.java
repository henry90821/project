package com.iskyshop.module.weixin.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lsolr.entity.SortWrapper;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.manage.admin.tools.FreeTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.SubjectTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: WapIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:wap以及微信商城使用的前台首页控制器
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
 * @author jinxinzhe
 * 
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class WeixinIndexViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IFreeGoodsService freeGoodsService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private ISubjectService subjectQueryObject;
	@Autowired
	private SubjectTools SubjectTools;
	
	/**
	 * 手机客户端商城首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/index.htm")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/index.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Date date = new Date();
		//秒杀
		Map params = new HashMap();
		params.put("weixin_shop_recommend", true);
		List<SeckillGoods> seckill_goods = seckillGoodsService.query(
				"select obj from SeckillGoods obj where obj.weixin_shop_recommend=:weixin_shop_recommend and obj.publish_time is not null order by weixin_shop_recommendTime desc",
				params, 0, 3);
		mv.addObject("seckill_goods", seckill_goods);
		
		//爆款商品查询
		params.clear();
		params.put("wx_moldbaby", 1);
		params.put("goods_status", 0);
		List<Goods> wx_moldbabys = this.goodsService.query(
				"select obj from Goods obj where obj.wx_moldbaby=:wx_moldbaby and obj.goods_status =:goods_status order by wx_squence asc",
				params, 0, 6);
		mv.addObject("wx_moldbabys", wx_moldbabys);
		
		params.clear();
		params.put("weixin_hot", 1);
		params.put("goods_status", 0);
		List<Goods> goods_hots = this.goodsService.query(
				"select obj from Goods obj where obj.weixin_hot=:weixin_hot and obj.goods_status =:goods_status order by weixin_hotTime desc",
				params, 0, 50);
		mv.addObject("goods_hots", goods_hots);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> top_recommends = this.goodsService.query(
				"select obj from Goods obj where obj.weixin_recommend=:weixin_recommend and obj.goods_status =:goods_status order by obj.weixin_recommendTime desc",
				params, 0, 6);
		mv.addObject("top_recommends", top_recommends);
		params.clear();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.display=:display and obj.level=0 order by obj.sequence asc", params, 0, 8);
		mv.addObject("gcs", gcs);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.brandService.query(
				"select obj from GoodsBrand obj where obj.audit=:audit and obj.weixin_recommend=:weixin_recommend order by weixin_recommendTime desc",
				params, 0, 4);
		mv.addObject("gbs", gbs);
		params.clear();
		params.put("ac_begin_time", date);
		params.put("ac_end_time", date);
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time", params, 0, 1);
		if (activitys.size() > 0) {
			params.clear();
			params.put("ag_status", 1);
			params.put("goods_status", 0);
			params.put("weixin_recommend", 1);
			params.put("act_id", activitys.get(0).getId());
			List<ActivityGoods> activitygoods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
							+ "and obj.weixin_recommend=:weixin_recommend and obj.act.id=:act_id "
							+ " order by weixin_recommendTime desc", params, 0, 3);
			mv.addObject("activitygoods", activitygoods);
		}
		params.clear();
		params.put("gg_status", 1);
		params.put("group_status", 0);
		params.put("weixin_recommend", 1);
		params.put("beginTime", date);
		params.put("endTime", date);
		params.put("group_beginTime", date);
		params.put("group_endTime", date);
		List<GroupGoods> groupgoods = this.groupgoodsService.query(
				"select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
						+ "and obj.weixin_recommend=:weixin_recommend and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by weixin_recommendTime desc",
				params, 0, 4);
		mv.addObject("groupgoods", groupgoods);
		params.clear();
		params.put("freeStatus", 5);
		params.put("weixin_recommend", 1);
		List<FreeGoods> freegoods = this.freeGoodsService
				.query("select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.weixin_recommend="
						+ ":weixin_recommend order by obj.weixin_recommendTime desc", params, 0, 1);
		
	    params.clear();
		params.put("ap_status", 1);
		params.put("ap_use_status", 1);
		List<AdvertPosition> advertPosition = this.advertPositionService
				.query("select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_use_status="
						+ ":ap_use_status", params, -1, -1);
		mv.addObject("advertPosition", advertPosition);
		
		
		mv.addObject("freegoods", freegoods);
		mv.addObject("freeTools", freeTools);
		mv.addObject("imageTools", imageTools);
		params.clear();
		params.put("ifctype", "H5");
		List<Subject> subject_list = this.subjectQueryObject
				.query("select obj from Subject obj where obj.ifcType=:ifctype order by obj.sequence asc", params, 0, 3);
		mv.addObject("subject_list", subject_list);
		return mv;
	}

	/**
	 * 手机端商品搜索
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/search.htm")
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type, String goods_inventory, String keyword,
			String goods_transfee, String goods_cod) throws UnsupportedEncodingException {
		ModelAndView mv = new JModelAndView("wap/search_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (!StringUtils.isNullOrEmpty(keyword)) {
			// 根据店铺SEO关键字，查出关键字命中的店铺
			if (!StringUtils.isNullOrEmpty(keyword) && keyword.length() > 1) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			List temp_list = this.goodsClassService.query("select obj.id from GoodsClass obj", null, -1, -1);
			lucene.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			SortWrapper sort = null;
			String query_gc = "";
			// 处理排序方式
			if ("goods_salenum".equals(CommUtil.null2String(orderBy))) {
				order_by = "goods_salenum";
				sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
			}
			if ("goods_collect".equals(CommUtil.null2String(orderBy))) {
				order_by = "goods_collect";
				sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
			}
			if ("well_evaluate".equals(CommUtil.null2String(orderBy))) {
				order_by = "well_evaluate";
				sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
			}
			if ("asc".equals(CommUtil.null2String(orderType))) {
				order_type = false;
			}
			if (StringUtils.isNullOrEmpty(orderType)) {
				orderType = "desc";
			}
			if ("store_price".equals(CommUtil.null2String(orderBy))) {
				order_by = "store_price";
				sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
			}
			if (!StringUtils.isNullOrEmpty(gc_id)) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
				query_gc = gc.getLevel() == 1 ? gc_id + "_*" : CommUtil.null2String(gc.getParent().getId()) + "_" + gc_id;
				mv.addObject("gc_id", gc_id);
			}
			LuceneResult pList = null;
			if (sort != null) {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage), 24, goods_inventory, goods_type, query_gc,
						goods_transfee, goods_cod, sort, null, null, null);
			} else {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage), 24, goods_inventory, goods_type, query_gc,
						goods_transfee, goods_cod, null, null, null);
			}

			CommUtil.saveLucene2ModelAndView(pList, mv);

			// 对关键字命中的商品进行分类提取
			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);
			// 对商品分类数据进行分析加载,只查询id和className
			List<GoodsClass> gcs = this.query_GC_second(list_gcs);

			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", pList.getRows());
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goods_transfee", goods_transfee);
		mv.addObject("goods_cod", goods_cod);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("userTools", userTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 手机端商品搜索
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/search_ajax.htm")
	public ModelAndView search_ajax(HttpServletRequest request, HttpServletResponse response, String gc_id,
			String currentPage, String orderBy, String orderType, String goods_type, String goods_inventory, String keyword,
			String goods_transfee, String goods_cod) {
		ModelAndView mv = new JModelAndView("wap/search_goods_data.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (!StringUtils.isNullOrEmpty(keyword)) {
			// 根据店铺SEO关键字，查出关键字命中的店铺
			if (!StringUtils.isNullOrEmpty(keyword) && keyword.length() > 1) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			List temp_list = this.goodsClassService.query("select obj.id from GoodsClass obj", null, -1, -1);
			lucene.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			SortWrapper sort = null;
			String query_gc = "";
			// 处理排序方式
			if ("goods_salenum".equals(CommUtil.null2String(orderBy))) {
				order_by = "goods_salenum";
				sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
			}
			if ("goods_collect".equals(CommUtil.null2String(orderBy))) {
				order_by = "goods_collect";
				sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
			}
			if ("well_evaluate".equals(CommUtil.null2String(orderBy))) {
				order_by = "well_evaluate";
				sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
			}
			if ("asc".equals(CommUtil.null2String(orderType))) {
				order_type = false;
			}
			if (StringUtils.isNullOrEmpty(orderType)) {
				orderType = "desc";
			}
			if ("store_price".equals(CommUtil.null2String(orderBy))) {
				order_by = "store_price";
				sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
			}
			if (!StringUtils.isNullOrEmpty(gc_id)) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
				query_gc = gc.getLevel() == 1 ? gc_id + "_*" : CommUtil.null2String(gc.getParent().getId()) + "_" + gc_id;
				mv.addObject("gc_id", gc_id);
			}
			LuceneResult pList = null;
			if (sort != null) {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage), 24, goods_inventory, goods_type, query_gc,
						goods_transfee, goods_cod, sort, null, null, null);
			} else {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage), 24, goods_inventory, goods_type, query_gc,
						goods_transfee, goods_cod, null, null, null);
			}

			CommUtil.saveLucene2ModelAndView(pList, mv);

			// 对关键字命中的商品进行分类提取
			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);
			// 对商品分类数据进行分析加载,只查询id和className
			List<GoodsClass> gcs = this.query_GC_second(list_gcs);

			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", pList.getRows());
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goods_transfee", goods_transfee);
		mv.addObject("goods_cod", goods_cod);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("userTools", userTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping("/wap/footer.htm")
	public ModelAndView footer(HttpServletRequest request, HttpServletResponse response, String op) {
		ModelAndView mv = new JModelAndView("wap/footer.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("name", request.getServerName());
		return mv;
	}

	@RequestMapping("/wap/layer.htm")
	public ModelAndView layer(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/layer.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 根据前端二维码扫描结果自动下载对应的手机客户端
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/wap/app/download.htm")
	public void app_download(HttpServletRequest request, HttpServletResponse response) {
		logger.info(request.getHeader("User-Agent").toLowerCase());
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		// String ios_reg =
		// ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_download();
		}
		try {
			response.sendRedirect(CommUtil.getURL(request) + "/" + url);
		} catch (IOException e) {
			logger.error(e);
		}
	}



	/**
	 * 对商品分类数据进行处理去重，返回页面用以显示的二级分类
	 * 
	 * @param lucenc商品分类数据
	 * @return
	 */
	public List<GoodsClass> query_GC_second(Set<String> list_gcs) {
		String sid = new String();
		Map params = new HashMap();
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Set<Long> ids = new HashSet<Long>();
		for (String str : list_gcs) {
			//begin dengyuqi 2015-9-17 新增数组大小判断，解决商品所属分类为空时数组下表越界的bug
			if(null != str.split("_") && str.split("_").length > 0){
				sid = str.split("_")[0];
				ids.add(CommUtil.null2Long(sid));
			}
			//end
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			gcs = this.goodsClassService
					.query("select new GoodsClass(id,className) from GoodsClass obj where obj.id in(:ids)", params, -1, -1);
		}
		return gcs;
	}

	/**
	 * 根据店铺SEO关键字，查出关键字命中的店铺
	 * 
	 * @param keyword
	 * @return
	 */
	public List<Store> search_stores_seo(String keyword) {
		Map params = new HashMap();
		params.put("keyword1", keyword);
		params.put("keyword2", keyword + ",%");
		params.put("keyword3", "%," + keyword + ",%");
		params.put("keyword4", "%," + keyword);
		List<Store> stores = this.storeService.query(
				"select obj from Store obj where obj.store_seo_keywords =:keyword1 or obj.store_seo_keywords like:keyword2 or obj.store_seo_keywords like:keyword3 or obj.store_seo_keywords like:keyword4",
				params, 0, 3);
		Collections.sort(stores, new Comparator() {
			public int compare(Object o1, Object o2) {
				Store store1 = (Store) o1;
				Store store2 = (Store) o2;
				int l1 = store1.getStore_seo_keywords().split(",").length;
				int l2 = store2.getStore_seo_keywords().split(",").length;
				if (l1 > l2) {
					return 1;
				}
				;
				if (l1 == l2) {
					if (store1.getPoint().getStore_evaluate().compareTo(store2.getPoint().getStore_evaluate()) == 1) {
						return -1;
					}
					;
					if (store1.getPoint().getStore_evaluate().compareTo(store2.getPoint().getStore_evaluate()) == -1) {
						return 1;
					}
					;
					return 0;
				}
				return -1;
			}
		});
		return stores;
	}

	@RequestMapping("/wap/doc.htm")
	public ModelAndView doc(HttpServletRequest request, HttpServletResponse response, String mark) {
		ModelAndView mv = new JModelAndView("wap/article.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", "doc");
		Document obj = this.documentService.getObjByProperty(null, "mark", mark);
		mv.addObject("obj", obj);
		return mv;
	}
	/**
	 *商城热卖点击加载更多
	 * wuzhipeng
	 * 
	 * @param keyword
	 * @return
	 */
	@SecurityMapping(title = "商城热卖Ajax更新", value = "/wap/goods_hots_ajax.htm*", rtype = "wap", rname = "商城热卖", rcode = "goods_hots", rgroup = "wap")
	@RequestMapping("/wap/goods_hots_ajax.htm")
	public void goods_hots_ajax(HttpServletRequest request, HttpServletResponse response, Integer count) throws ClassNotFoundException {	
		if(count == null) {
			count = 0;
		}
		Map params = new HashMap();
		params.clear();
		params.put("weixin_hot", 1);
		params.put("goods_status", 0);
		List<Goods> goods_hots = this.goodsService.query(
				"select obj from Goods obj where obj.weixin_hot=:weixin_hot and obj.goods_status =:goods_status order by weixin_hotTime desc",
				params, count, 50);
		
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods_hots) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getStore_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("img", obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
			map.put("count", count + 50);
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		String temp = Json.toJson(list, JsonFormat.compact());
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	/**
	 * 专题详情
	 * wuzhipeng
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/wap/view.htm")
	public ModelAndView subject_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/activity_page.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Subject obj = this.subjectQueryObject.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getSubject_detail() != null) {
			List<Map> objs = (List<Map>) Json.fromJson(obj.getSubject_detail());
			mv.addObject("objs", objs);
		}
		mv.addObject("obj", obj);
		mv.addObject("SubjectTools", SubjectTools);
		return mv;
	}
	
	/**
	 * 2016wap端年福袋
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/gift.htm")
	public ModelAndView gift(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/gift.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
}
