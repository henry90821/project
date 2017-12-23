package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.ConsultSatis;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.virtual.GoodsCompareView;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IConsultSatisService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.ConsultViewTools;
import com.iskyshop.view.web.tools.EvaluateViewTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: GoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品前台控制器,用来显示商品列表、商品详情、商品其他信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private ConsultViewTools consultViewTools;
	@Autowired
	private EvaluateViewTools evaluateViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IConsultSatisService consultsatisService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@RequestMapping("/getPartsPrice.htm")
	public void genericCombinPartsPrice(HttpServletRequest request, HttpServletResponse response, String parts_ids,
			String gid) {
		double all_price = 0.00;
		if (!StringUtils.isNullOrEmpty(gid)) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(gid));
			all_price = CommUtil.null2Double(obj.getGoods_current_price());
		}
		if (!StringUtils.isNullOrEmpty(parts_ids)) {
			String[] ids = parts_ids.split(",");
			for (String id : ids) {
				if (!"".equals(id)) {
					Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
					all_price = CommUtil.add(all_price, obj.getGoods_current_price());
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(CommUtil.formatMoney(all_price));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 查看店铺商品详细信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/goods.htm")
	public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = null;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (this.configService.getSysConfig().isSecond_domain_open()) { // 如果系统开启了二级域名，则判断该商品是不是对应的二级域名下的，如果不是则返回错误页面
				String serverName = request.getServerName().toLowerCase();
				String secondDomain = CommUtil.null2String(serverName.substring(0, serverName.indexOf(".")));
				if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
					secondDomain = "www";
				}
				if (!"".equals(secondDomain)) {
					if (obj.getGoods_type() == 0) { // 自营商品禁止使用二级域名访问
						if (!"www".equals(secondDomain)) {
							mv = new JModelAndView("error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", "参数错误，商品查看失败");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
							return mv;
						}
					} else {
						if (!obj.getGoods_store().getStore_second_domain().equals(secondDomain)) {
							mv = new JModelAndView("error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", "参数错误，商品查看失败");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
							return mv;
						}
					}
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "参数错误，商品查看失败");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
				}
			}
			// 利用cookie添加浏览过的商品
			Cookie[] cookies = request.getCookies();
			Cookie goodscookie = null;
			int k = 0;
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("goodscookie".equals(cookie.getName())) {
						String goods_ids = cookie.getValue();
						int m = 6;
						int n = goods_ids.split(",").length;
						if (m > n) {
							m = n + 1;
						}
						String[] new_goods_ids = goods_ids.split(",", m);
						for (int i = 0; i < new_goods_ids.length; i++) {
							if ("".equals(new_goods_ids[i])) {
								for (int j = i + 1; j < new_goods_ids.length; j++) {
									new_goods_ids[i] = new_goods_ids[j];
								}
							}
						}
						String[] new_ids = new String[6];
						for (int i = 0; i < m - 1; i++) {
							if (id.equals(new_goods_ids[i])) {
								k++;
							}
						}
						if (k == 0) {
							new_ids[0] = id;
							for (int j = 1; j < m; j++) {
								new_ids[j] = new_goods_ids[j - 1];
							}
							goods_ids = id + ",";
							if (m == 2) {
								for (int i = 1; i <= m - 1; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							} else {
								for (int i = 1; i < m; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							}
							goodscookie = new Cookie("goodscookie", goods_ids);
						} else {
							new_ids = new_goods_ids;
							goods_ids = "";
							for (int i = 0; i < m - 1; i++) {
								goods_ids += new_ids[i] + ",";
							}
							goodscookie = new Cookie("goodscookie", goods_ids);
						}
						goodscookie.setMaxAge(60 * 60 * 24 * 7);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
						break;
					} else {
						goodscookie = new Cookie("goodscookie", id + ",");
						goodscookie.setMaxAge(60 * 60 * 24 * 7);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
					}
				}
			} else {
				goodscookie = new Cookie("goodscookie", id + ",");
				goodscookie.setMaxAge(60 * 60 * 24 * 7);
				goodscookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(goodscookie);
			}
			User current_user = SecurityUserHolder.getCurrentUser();
			boolean admin_view = false; // 超级管理员可以查看未审核得到商品信息
			if (current_user != null) {
				// 登录用户记录浏览足迹信息
				Map params = new HashMap();
				params.put("fp_date", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
				params.put("fp_user_id", current_user.getId());
				List<FootPoint> fps = this.footPointService.query(
						"select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id", params,
						-1, -1);
				if (fps.size() == 0) {
					FootPoint fp = new FootPoint();
					fp.setAddTime(new Date());
					fp.setFp_date(new Date());
					fp.setFp_user_id(current_user.getId());
					fp.setFp_user_name(current_user.getUsername());
					fp.setFp_goods_count(1);
					Map map = new HashMap();
					map.put("goods_id", obj.getId());
					map.put("goods_name", obj.getGoods_name());
					map.put("goods_sale", obj.getGoods_salenum());
					map.put("goods_time", CommUtil.formatLongDate(new Date()));
					//解决问题：624 用户中心-我的足迹页面很多图片加载失败，原因是在给图赋值路径是加入了本地地址，即是 CommUtil.getURL(request)
					map.put("goods_img_path",obj.getGoods_main_photo() != null ? (obj.getGoods_main_photo().getPath() + "/" 
									+ obj.getGoods_main_photo().getName()) : (this.configService.getSysConfig()
									.getGoodsImage().getPath() + "/"
									+ this.configService.getSysConfig().getGoodsImage().getName()));
					map.put("goods_price", obj.getGoods_current_price());
					map.put("goods_class_id", CommUtil.null2Long(obj.getGc().getId()));
					map.put("goods_class_name", CommUtil.null2String(obj.getGc().getClassName()));
					List<Map> list = new ArrayList<Map>();
					list.add(map);
					fp.setFp_goods_content(Json.toJson(list, JsonFormat.compact()));
					this.footPointService.save(fp);
				} else {
					FootPoint fp = fps.get(0);
					List<Map> list = Json.fromJson(List.class, fp.getFp_goods_content());
					boolean add = true;
					for (Map map : list) { // 排除重复的商品足迹
						if (CommUtil.null2Long(map.get("goods_id")).equals(obj.getId())) {
							add = false;
						}
					}
					if (add) {
						Map map = new HashMap();
						map.put("goods_id", obj.getId());
						map.put("goods_name", obj.getGoods_name());
						map.put("goods_sale", obj.getGoods_salenum());
						map.put("goods_time", CommUtil.formatLongDate(new Date()));
						map.put("goods_img_path",obj.getGoods_main_photo() != null ? (obj.getGoods_main_photo().getPath() + "/" 
								+ obj.getGoods_main_photo().getName()) : (this.configService.getSysConfig()
								.getGoodsImage().getPath() + "/"
								+ this.configService.getSysConfig().getGoodsImage().getName()));
						map.put("goods_price", obj.getGoods_current_price());
						map.put("goods_class_id", CommUtil.null2Long(obj.getGc().getId()));
						map.put("goods_class_name", CommUtil.null2String(obj.getGc().getClassName()));
						list.add(0, map); // 后浏览的总是插入最前面
						fp.setFp_goods_count(list.size());
						fp.setFp_goods_content(Json.toJson(list, JsonFormat.compact()));
						this.footPointService.update(fp);
					}
				}
				current_user = this.userService.getObjById(current_user.getId());
				if ("ADMIN".equals(current_user.getUserRole())) {
					admin_view = true;
				}
			}

			// 记录商品点击日志
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj.getId());
			todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
			String click_from_str = todayGoodsLog.getGoods_click_from();
			Map<String, Integer> clickmap = (!StringUtils.isNullOrEmpty(click_from_str)) ? (Map<String, Integer>) Json
					.fromJson(click_from_str) : new HashMap<String, Integer>();
			String from = clickfrom_to_chinese(CommUtil.null2String(request.getParameter("from")));
			if (from != null && !"".equals(from)) {
				if (clickmap.containsKey(from)) {
					clickmap.put(from, clickmap.get(from) + 1);
				} else {
					clickmap.put(from, 1);
				}
			} else {
				if (clickmap.containsKey("unknow")) {
					clickmap.put("unknow", clickmap.get("unknow") + 1);
				} else {
					clickmap.put("unknow", 1);
				}
			}
			todayGoodsLog.setGoods_click_from(Json.toJson(clickmap, JsonFormat.compact()));
			this.goodsLogService.update(todayGoodsLog);
			
			boolean isPrivate = false;//指示当前商品是否为当前用户对应的店铺中的商品
			if(current_user!= null && current_user.getUserRole().contains("SELLER")) {
				current_user = current_user.getParent() == null? current_user: current_user.getParent();
				if(obj.getGoods_store() != null && obj.getGoods_store().getId().equals(current_user.getStore().getId())) {
					isPrivate = true;
				}
			}
			
			if (obj.getGoods_status() == 0 || admin_view || isPrivate) {
				mv = new JModelAndView("default/store_goods.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status() && obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				// 检测商品所有活动状态
				if (obj.getActivity_status() == 1 || obj.getActivity_status() == 2) { // 检查商城促销商品是否过期
					if (!"".equals(CommUtil.null2String(obj.getActivity_goods_id()))) {
						ActivityGoods ag = this.actgoodsService.getObjById(obj.getActivity_goods_id());
						if (ag.getAct().getAc_end_time().before(new Date())) {
							ag.setAg_status(-2);
							this.actgoodsService.update(ag);
							obj.setActivity_status(0);
							obj.setActivity_goods_id(null);
						}
					}
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) { // 检查团购是否过期
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				if (obj.getSeckill_buy() == 4) { // 检查秒杀是否开始					
					String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
					Map params = new HashMap();
					params.put("goods_id", obj.getId());
					params.put("gg_status", 1);
					List<SeckillGoods> seckillGoodses = seckillGoodsService.query(jpql, params, 0, 1);
					SeckillGoods seckillGoods = null;
					if(seckillGoodses.size() > 0) {
						seckillGoods = seckillGoodses.get(0);
						if (seckillGoods.getBeginTime().before(new Date())) {
							seckillGoods.setGg_status(2);
							seckillGoodsService.update(seckillGoods);
							
							obj.setSeckill_buy(2);
							obj.setGoods_current_price(seckillGoods.getGg_price());
							
							// 更新lucene索引
							String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
									+ "goods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneUtil lucene = LuceneUtil.instance();
							LuceneUtil.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(obj.getId()), luceneVoTools.updateGoodsIndex(obj));
						}
					}
					
					mv.addObject("seckillGoods",seckillGoods);
				}else if (obj.getSeckill_buy() == 2) { // 检查秒杀是否过期
					String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
					Map params = new HashMap();
					params.put("goods_id", obj.getId());
					params.put("gg_status", 2);
					List<SeckillGoods> seckillGoodses = seckillGoodsService.query(jpql, params, 0, 1);
					
					SeckillGoods seckillGoods = null;
					if(seckillGoodses.size() > 0) {
						seckillGoods = seckillGoodses.get(0);
						if (seckillGoods.getEndTime().before(new Date())) {
							seckillGoods.setGg_status(3);
							seckillGoodsService.update(seckillGoods);
							
							obj.setSeckill_buy(0);
							obj.setGoods_inventory(obj.getGoods_inventory()+seckillGoods.getGg_count());
							obj.setGoods_current_price(obj.getStore_price());
							
							// 更新lucene索引
							String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
									+ "goods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneUtil lucene = LuceneUtil.instance();
							LuceneUtil.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(obj.getId()), luceneVoTools.updateGoodsIndex(obj));
						}
					}					
					
					mv.addObject("seckillGoods",seckillGoods);
				}
				if (obj.getCombin_status() == 1) { // 检查组合是否过期
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("endTime", new Date());
					params.put("main_goods_id", obj.getId());
					List<CombinPlan> combins = this.combinplanService
							.query("select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
									params, -1, -1);
					if (combins.size() > 0) {
						for (CombinPlan com : combins) {
							if (com.getCombin_type() == 0) {
								if (obj.getCombin_suit_id().equals(com.getId())) {
									obj.setCombin_suit_id(null);
								}
							} else {
								if (obj.getCombin_parts_id().equals(com.getId())) {
									obj.setCombin_parts_id(null);
								}
							}
							obj.setCombin_status(0);
						}
					}
				}
				if (obj.getOrder_enough_give_status() == 1) { // 检查满就送是否过期
					BuyGift bg = this.buyGiftService.getObjById(obj.getBuyGift_id());
					if (bg != null && bg.getEndTime().before(new Date())) {
						bg.setGift_status(20);
						List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
						maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
						for (Map map : maps) {
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
							if (goods != null) {
								goods.setOrder_enough_give_status(0);
								goods.setOrder_enough_if_give(0);
								goods.setBuyGift_id(null);
							}
						}
						this.buyGiftService.update(bg);
					}
					if (bg != null && bg.getGift_status() == 10 && bg.getBeginTime().before(new Date())) {
						mv.addObject("isGift", true);
					}
				}
				if (obj.getOrder_enough_if_give() == 1) { // 检查满就送赠品是否过期
					BuyGift bg = this.buyGiftService.getObjById(obj.getBuyGift_id());
					if (bg != null && bg.getGift_status() == 10 && bg.getBeginTime().before(new Date())) {
						mv.addObject("isGive", true);
					}
				}
				if (obj.getEnough_reduce() == 1) { // 如果是满就减商品，未到活动时间不作处理，活动时间显示满减信息，已过期则删除满减信息
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(obj.getOrder_enough_reduce_id()));
					if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())
							&& er.getErend_time().after(new Date())) { // 正在进行
						mv.addObject("enoughreduce", er);
					} else if (er.getErend_time().before(new Date())) { // 已过期
						er.setErstatus(20);
						this.enoughReduceService.update(er);
						String goods_json = er.getErgoods_ids_json();
						List<String> goods_id_list = (List) Json.fromJson(goods_json);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id("");
							this.goodsService.update(ergood);
						}
					}
				}
				this.goodsService.update(obj);
				
				List<Map> gsps = (List<Map>)CommUtil.json2List(obj.getGoods_specs_info());			
				List<GoodsSpecProperty> gspsBase = obj.getGoods_specs();
				List<GoodsSpecProperty> gspsBaseCopy = new ArrayList<GoodsSpecProperty>();
				for(GoodsSpecProperty gsp: gspsBase) {
					GoodsSpecProperty s = new GoodsSpecProperty();
					s.setId(gsp.getId());
					s.setSpecImage(gsp.getSpecImage());
					s.setValue(gsp.getValue());	
					s.setSpec(gsp.getSpec());
					for(Map m: gsps) {
						if(s.getId().toString().equals(m.get("id"))) {
							s.setValue(CommUtil.null2String(m.get("name")));
						}
					}
					gspsBaseCopy.add(s);
				}				
				mv.addObject("gspsCopy", gspsBaseCopy);
				
				mv.addObject("obj", obj);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("transportTools", transportTools);
				// 计算当期访问用户的IP地址，并计算对应的运费信息
				String current_ip = CommUtil.getIpAddr(request); // 获得本机IP
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					String current_city = ip.getIPLocation(current_ip).getCountry();
					mv.addObject("current_city", current_city);
				} else {
					mv.addObject("current_city", "未知地区");
				}
				// 查询运费地区
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
				mv.addObject("areas", areas);
				// 相关分类
				Map params = new HashMap();
				params.put("parent_id", obj.getGc().getParent().getId());
				params.put("display", true);
				List<GoodsClass> about_gcs = this.goodsClassService
						.query("select new GoodsClass(id,className) from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
								params, -1, -1);
				mv.addObject("about_gcs", about_gcs);
				mv.addObject("userTools", userTools);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("activityViewTools", activityViewTools);
				if (obj.getGoods_type() == 0) { // 平台自营商品
				} else { // 商家商品
					this.generic_evaluate(obj.getGoods_store(), mv);
					mv.addObject("store", obj.getGoods_store());
				}
				// 查詢评价第一页信息
				EvaluateQueryObject qo = new EvaluateQueryObject("1", mv, "addTime", "desc");
				qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
				qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
				qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0), "=");
				qo.setPageSize(10);
				IPageList eva_pList = this.evaluateService.list(qo);
				String url = CommUtil.getURL(request) + "/goods_evaluation.htm";
				mv.addObject("eva_objs", eva_pList.getResult());
				mv.addObject("eva_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "", eva_pList.getCurrentPage(), eva_pList.getPages()));
				mv.addObject("evaluateViewTools", this.evaluateViewTools);
				mv.addObject("orderFormTools", this.orderFormTools);				

				mv.addObject("evaluates_count", obj.getEvaluate_count());				
				mv.addObject("well_count", obj.getWell_evaluate());
				mv.addObject("mid_count", obj.getMiddle_evaluate());
				mv.addObject("bad_count", obj.getBad_evaluate());
				
				// 查询成交记录第一页
				qo = new EvaluateQueryObject("1", mv, "addTime", "desc");
				qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
				qo.setPageSize(10);
				IPageList order_eva_pList = this.evaluateService.list(qo);
				url = CommUtil.getURL(request) + "/goods_order.htm";
				mv.addObject("order_objs", order_eva_pList.getResult());
				mv.addObject("order_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "", order_eva_pList.getCurrentPage(), order_eva_pList.getPages()));
				// 查询商品咨询第一页
				ConsultQueryObject cqo = new ConsultQueryObject("1", mv, "addTime", "desc");
				cqo.addQuery("obj.goods_id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
				cqo.setPageSize(10);
				IPageList pList = this.consultService.list(cqo);
				url = CommUtil.getURL(request) + "/goods_consult.htm";
				mv.addObject("consult_objs", pList.getResult());
				mv.addObject("consult_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "", pList.getCurrentPage(), pList.getPages()));
				mv.addObject("consultViewTools", this.consultViewTools);
				// 处理系统商品对比信息
				List<Goods> goods_compare_list = (List<Goods>) request.getSession(true).getAttribute("goods_compare_cart");
				int compare = 0; // 当前商品是否存在对比商品session中
				if (goods_compare_list != null) {
					for (Goods goods : goods_compare_list) {
						if (goods.getId().equals(obj.getId())) {
							compare = 1;
						}
					}
				} else {
					goods_compare_list = new ArrayList<Goods>();
				}
				// 计算商品对比中第一间商品的分类，只允许对比同一个分类的商品
				int compare_goods_flag = 0; // 默认不允许对比商品，如果商品分类不一致则不允许对比
				
				//begin dengyuqi 2015-9-14 add 
				long compareGoodsGcId = 0; //对比栏中的商品所属分类
				long currentGoodsGcId = 0; //当前商品所属分类
				//end
				
				for (Goods compare_goods : goods_compare_list) {
					if (compare_goods != null) {
						compare_goods = this.goodsService.getObjById(compare_goods.getId());
						
						//begin dengyuqi 2015-9-15 处理空指针异常，当对比栏中存在商品时，对比栏中的商品以及当前详情页面的商品均可能存在无商品分类或只存在二级分类
						if(null != compare_goods.getGc() && null != obj.getGc()){
							compareGoodsGcId = compare_goods.getGc().getParent().getParent()==null?
									compare_goods.getGc().getParent().getId():compare_goods.getGc().getParent().getParent().getId();
							
							currentGoodsGcId = obj.getGc().getParent().getParent()==null?
									obj.getGc().getParent().getId():obj.getGc().getParent().getParent().getId();
						}
						if(compareGoodsGcId != 0 && compareGoodsGcId == currentGoodsGcId){
							compare_goods_flag = 1;
						}
//						if (!compare_goods.getGc().getParent().getParent().getId()
//								.equals(obj.getGc().getParent().getParent().getId())) {
//							compare_goods_flag = 1;
//						}
						//end
					}
				}
				mv.addObject("compare_goods_flag", compare_goods_flag);
				mv.addObject("goods_compare_list", goods_compare_list);
				mv.addObject("compare", compare);
				// 相关品牌
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);				

				String message = "商品已下架，商品查看失败";
				if(obj.getGoods_status() == -5){
					message = "商品正在审核中，暂时无法查看";
				}
				mv.addObject("op_title", message);

				
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，商品查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		
		mv.addObject("nowTime", CommUtil.formatLongDate(new Date()));
		
		/***
		 * start
		 * group seckill 
		 * 由于前端把后台与js耦合的部分移到了页面的后面，在生成html的 时候 可能 拿不到 值 来判断，所以在后台先给值
		 * 按照页面velocity代码转换成java代码
		 */
		int group=0;
		int seckill=0;
		if(obj.getGroup_buy()==2){
			for(GroupGoods groupGoods:obj.getGroup_goods_list()){
				if(obj.getGroup().getId()==groupGoods.getGroup().getId()){
					if(groupGoods.getGg_status()==1){
						group=1;
						break;
					}
				}
			}
		}else if(obj.getSeckill_buy()==2 || obj.getSeckill_buy()==4){
			seckill=1;
		}
		mv.addObject("group", group);
		mv.addObject("seckill", seckill);
		/**
		 * end
		 * group seckill 
		 */
		
		return mv;
	}

	public String clickfrom_to_chinese(String key) {
		String str = "其它";
		if ("search".equals(key)) {
			str = "搜索";
		}
		if ("floor".equals(key)) {
			str = "首页楼层";
		}
		if ("gcase".equals(key)) {
			str = "橱窗";
		}
		return str;
	}

	/**
	 * 根据商城分类查看商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/store_goods_list.htm")
	public ModelAndView store_goods_list(HttpServletRequest request, HttpServletResponse response, String gc_id,
			String currentPage, String orderBy, String orderType, String brand_ids, String gs_ids, String properties,
			String all_property_status, String detail_property_status, String goods_type, String goods_inventory,
			String goods_transfee, String goods_cod) {
		ModelAndView mv = new JModelAndView("store_goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
		mv.addObject("gc", gc);
		Set gc_list = new TreeSet();
		if (gc != null) {
			if (gc.getLevel() == 0) {
				gc_list = gc.getChilds();
			} else if (gc.getLevel() == 1) {
				gc_list = gc.getParent().getChilds();
			} else if (gc.getLevel() == 2) {
				gc_list = gc.getParent().getParent().getChilds();
			}
		}
		mv.addObject("gc_list", gc_list);
		if (StringUtils.isNullOrEmpty(orderBy)) {
			orderBy = "addTime";
		}
		if (StringUtils.isNullOrEmpty(orderType)) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(null, currentPage, mv, orderBy, orderType);
		Set<Long> ids = null;
		if (gc != null) {
			ids = this.gcViewTools.genericIds(gc.getId(), false, false);
			ids.add(gc.getId());
		}
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}
		if (goods_cod != null) {
			gqo.addQuery("obj.goods_cod", new SysMap("goods_cod", 0), "=");
			mv.addObject("goods_cod", goods_cod);
		}
		if (goods_transfee != null) {
			gqo.addQuery("obj.goods_transfee", new SysMap("goods_transfee", 1), "=");
			mv.addObject("goods_transfee", goods_transfee);
		}
		gqo.setPageSize(24); // 设定分页查询，每页24件商品
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		List<Map> goods_property = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(brand_ids)) {
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id", CommUtil.null2Long(brand_id)), "=", "and");
				Map map = new HashMap();
				GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_id));
				if (brand != null) {
					map.put("name", "品牌");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				for (int i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					if (i == 0) {
						gqo.addQuery("and (obj.goods_brand.id=" + CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_id) + ")", null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					}
				}
			}
			mv.addObject("brand_ids", brand_ids);
		}
		if (!StringUtils.isNullOrEmpty(gs_ids)) {
			List<List<GoodsSpecProperty>> gsp_lists = this.generic_gsp(gs_ids);
			for (int j = 0; j < gsp_lists.size(); j++) {
				List<GoodsSpecProperty> gsp_list = gsp_lists.get(j);
				if (gsp_list.size() == 1) {
					GoodsSpecProperty gsp = gsp_list.get(0);
					gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
					Map map = new HashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i = 0; i < gsp_list.size(); i++) {
						if (i == 0) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						}
					}
				}
			}
			mv.addObject("gs_ids", gs_ids);
		}
		if (!StringUtils.isNullOrEmpty(properties)) {
			String[] properties_list = properties.substring(1).split("\\|");
			for (int i = 0; i < properties_list.length; i++) {
				String property_info = CommUtil.null2String(properties_list[i]);
				String[] property_info_list = property_info.split(",");
				GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil.null2Long(property_info_list[0]));
				Map p_map = new HashMap();
				p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
				p_map.put("gtp_value" + i, "%" + property_info_list[1].trim() + "%");
				gqo.addQuery("and (obj.goods_property like :gtp_name" + i + " and obj.goods_property like :gtp_value" + i
						+ ")", p_map);
				Map map = new HashMap();
				map.put("name", gtp.getName());
				map.put("value", property_info_list[1]);
				map.put("type", "properties");
				map.put("id", gtp.getId());
				goods_property.add(map);
			}
			mv.addObject("properties", properties);
			// 处理筛选类型互斥,|1,超短裙（小于75cm）|2,纯色
			List<GoodsTypeProperty> filter_properties = new ArrayList<GoodsTypeProperty>();
			List<String> hc_property_list = new ArrayList<String>(); // 已经互斥处理过的属性值，在循环中不再处理
			if (gc.getGoodsType() != null) {
				for (GoodsTypeProperty gtp : gc.getGoodsType().getProperties()) {
					boolean flag = true;
					GoodsTypeProperty gtp1 = new GoodsTypeProperty();
					gtp1.setDisplay(gtp.isDisplay());
					gtp1.setGoodsType(gtp.getGoodsType());
					gtp1.setHc_value(gtp.getHc_value());
					gtp1.setId(gtp.getId());
					gtp1.setName(gtp.getName());
					gtp1.setSequence(gtp.getSequence());
					gtp1.setValue(gtp.getValue());
					for (String hc_property : hc_property_list) {
						String[] hc_list = hc_property.split(":");
						if (hc_list[0].equals(gtp.getName())) {
							String[] hc_temp_list = hc_list[1].split(",");
							String[] defalut_list_value = gtp1.getValue().split(",");
							ArrayList<String> defalut_list = new ArrayList<String>(Arrays.asList(defalut_list_value));
							for (String hc_temp : hc_temp_list) {
								defalut_list.remove(hc_temp);
							}
							String value = "";
							for (int i = defalut_list.size() - 1; i >= 0; i--) {
								value = defalut_list.get(i) + "," + value;
							}
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}

					}
					if (flag) {
						if (!StringUtils.isNullOrEmpty(gtp.getHc_value())) { // 取消互斥类型
							String[] list1 = gtp.getHc_value().split("#");
							for (int i = 0; i < properties_list.length; i++) {
								String property_info = CommUtil.null2String(properties_list[i]);
								String[] property_info_list = property_info.split(",");
								if (property_info_list[1].equals(list1[0])) { // 存在该互斥，则需要进行处理
									hc_property_list.add(list1[1]);
								}
							}

						}
						filter_properties.add(gtp);
					} else {
						filter_properties.add(gtp1);
					}
				}
				mv.addObject("filter_properties", filter_properties);
			}
		} else {
			// 处理筛选类型互斥
			mv.addObject("filter_properties", gc.getGoodsType() != null ? gc.getGoodsType().getProperties() : "");
		}
		
		if (CommUtil.null2Int(goods_inventory) == 0) { // 查询库存大于0
			gqo.addQuery("obj.goods_inventory", new SysMap("goods_inventory", 0), ">");
		}
		if (!StringUtils.isNullOrEmpty(goods_type) && CommUtil.null2Int(goods_type) != -1) { // 查询自营或者第三方经销商商品
			gqo.addQuery("obj.goods_type", new SysMap("goods_type", CommUtil.null2Int(goods_type)), "=");
		}
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", pList.getRowCount());
		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧10条数据，从第3位开始查询
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
				List all_left_ztc_goods = this.goodsService.query(
						"select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc", ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc", ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
				List all_left_ztc_goods = this.goodsService
						.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc", ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc", ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// 页面顶部,直通车前3个商品
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				top_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc", ztc_map2, 0, 3);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				top_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc", ztc_map2, 0, 3);
			}
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if (detail_property_status != null && !"".equals(detail_property_status)) {
			mv.addObject("detail_property_status", detail_property_status);
			String temp_str[] = detail_property_status.split(",");
			Map pro_map = new HashMap();
			List pro_list = new ArrayList();
			for (String property_status : temp_str) {
				if (property_status != null && !"".equals(property_status)) {
					String mark[] = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		// 计算当期访问用户的IP地址，并计算对应的运费信息  ????前台没有用到current_city这个变量啊？？？？？？
		String current_ip = CommUtil.getIpAddr(request); // 获得本机IP
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			mv.addObject("current_city", current_city);
		} else {
			mv.addObject("current_city", "未知地区");
		}
		mv.addObject("goods_inventory", CommUtil.null2Int(goods_inventory));
		mv.addObject("goods_type", "".equals(CommUtil.null2String(goods_type)) ? -1 : CommUtil.null2Int(goods_type));
		mv.addObject("userTools", userTools);
		// 处理系统商品对比信息
		List<Goods> goods_compare_list = null;
		if(request.getSession() != null) {
			goods_compare_list = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		}
		// 计算商品对比中第一间商品的分类，只允许对比同一个分类的商品
		if (goods_compare_list == null) {
			goods_compare_list = new ArrayList<Goods>();
		}
		int compare_goods_flag = 0; // 默认允许对比商品，如果商品分类不一致曾不允许对比
		
		//begin dengyuqi 2015-9-17 add 
		long compareGoodsGcId = 0; //对比栏中的商品所属分类
		long currentGoodsGcId = 0; //当前商品所属分类
		//end
		
		for (Goods compare_goods : goods_compare_list) {
			if (compare_goods != null) {
				compare_goods = this.goodsService.getObjById(compare_goods.getId());
				
				//begin dengyuqi 2015-9-17 处理空指针异常，当对比栏中存在商品时，对比栏中的商品以及当前详情页面的商品均可能存在无商品分类或只存在二级分类
				if(null != compare_goods.getGc()){
					compareGoodsGcId = compare_goods.getGc().getParent().getParent()==null?
							compare_goods.getGc().getParent().getId():compare_goods.getGc().getParent().getParent().getId();
					
					currentGoodsGcId = CommUtil.null2Long(gc_id);
				}
				if(compareGoodsGcId != 0 && compareGoodsGcId == currentGoodsGcId){
					compare_goods_flag = 1;
				}
//				if (!compare_goods.getGc().getParent().getParent().getId().equals(CommUtil.null2Long(gc_id))) {
//					compare_goods_flag = 1;
//				}
				//end
			}
		}
		mv.addObject("compare_goods_flag", compare_goods_flag);
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 底部根据流程猜你喜欢商品列表， 使用自定义标签$!httpInclude.include("/goods_list_bottom.htm") 完成页面引用,默认查询20条数据
	 */
	@RequestMapping("/goods_list_bottom.htm")
	public ModelAndView goods_list_bottom(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_list_bottom.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("goodscookie".equals(cookie.getName())) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					your_like_goods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not "
									+ goods.getId()
									+ " order by obj.goods_salenum desc", null, 0, 20);
					int gcs_size = your_like_goods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService
								.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.id is not "
										+ goods.getId() + " order by obj.goods_salenum desc", null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < your_like_goods.size(); j++) {
								if (like_goods.get(i).getId().equals(your_like_goods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								your_like_goods.add(like_goods.get(i));
							}
						}
					}
					break;
				} else {
					your_like_goods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);
		List<Goods> goods_last = new ArrayList<Goods>();
		Cookie[] cookies_last = request.getCookies();
		Map params = new HashMap();
		Set<Long> ids = new HashSet<Long>();
		if (cookies_last != null) {
			for (Cookie co : cookies_last) {
				if ("goodscookie".equals(co.getName())) {
					String[] goods_id = co.getValue().split(",");
					int j = 4;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						ids.add(CommUtil.null2Long(goods_id[i]));
					}
				}
			}
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			goods_last = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id in(:ids)",
							params, -1, -1);
		}
		mv.addObject("goods_last", goods_last);
		return mv;
	}

	
	/**
	 * 对gs_ids中的所有规格值按其所属规格进行分类。返回的List列表中的每一个元素是一个List，此List元素中包含的属于同一规格下的所有规格值对象
	 * @param gs_ids
	 * @return
	 */
	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		for (String gd_id_info : gs_id_list) {
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId().equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_evaluation.htm")
	public ModelAndView goods_evaluation(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String currentPage, String goods_eva) {
		ModelAndView mv = new JModelAndView("default/goods_evaluation.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0), "=");
		qo.setPageSize(10);
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!"".equals(CommUtil.null2String(goods_eva))) {
			if ("100".equals(goods_eva)) {
				qo.addQuery("obj.evaluate_photos", new SysMap("evaluate_photos", ""), "!=");
			} else {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap("evaluate_buyer_val", CommUtil.null2Int(goods_eva)), "=");
			}
		}
		IPageList pList = this.evaluateService.list(qo);
		String url = CommUtil.getURL(request) + "/goods_evaluation.htm";
		mv.addObject("eva_objs", pList.getResult());
		mv.addObject("eva_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}

	@RequestMapping("/goods_detail.htm")
	public ModelAndView goods_detail(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("default/goods_detail.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	@RequestMapping("/goods_order.htm")
	public ModelAndView goods_order(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");

		qo.setPageSize(10);
		IPageList order_eva_pList = this.evaluateService.list(qo);
		String url = CommUtil.getURL(request) + "/goods_order.htm";
		mv.addObject("order_objs", order_eva_pList.getResult());
		mv.addObject("order_gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url, "", order_eva_pList.getCurrentPage(), order_eva_pList.getPages()));

		return mv;
	}

	@RequestMapping("/goods_consult.htm")
	public ModelAndView goods_consult(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String consult_type, String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_consult.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.goods_id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		if (!"".equals(CommUtil.null2String(consult_type))) {
			qo.addQuery("obj.consult_type", new SysMap("consult_type", consult_type), "=");
		}
		qo.setPageSize(10);
		IPageList pList = this.consultService.list(qo);
		String url2 = CommUtil.getURL(request) + "/goods_consult.htm";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url2, "", pList.getCurrentPage(), pList.getPages()));

		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}

	@RequestMapping("/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request, HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
		
		Map result = this.goodsTools.getGoodsPriceAndInventory(goods, gsp, null);
		
		map.put("count", result.get("count"));
		map.put("price", result.get("price"));
		if (result.get("act_price") != null) {
			map.put("act_price", CommUtil.formatMoney(result.get("act_price")));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@RequestMapping("/trans_fee.htm")
	public void trans_fee(HttpServletRequest request, HttpServletResponse response, String city_name, String goods_id) {
		Map map = new HashMap();
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		float mail_fee = 0;
		float express_fee = 0;
		float ems_fee = 0;
		if (goods != null && goods.getTransport() != null) {
			mail_fee = this.transportTools.cal_goods_trans_fee(CommUtil.null2String(goods.getTransport().getId()), "mail", 1,
					CommUtil.null2String(goods.getGoods_weight()), CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(CommUtil.null2String(goods.getTransport().getId()),
					"express", 1, CommUtil.null2String(goods.getGoods_weight()), CommUtil.null2String(goods.getGoods_volume()),
					city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(CommUtil.null2String(goods.getTransport().getId()), "ems", 1,
					CommUtil.null2String(goods.getGoods_weight()), CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("mail_fee", mail_fee);
		map.put("express_fee", express_fee);
		map.put("ems_fee", ems_fee);
		map.put("current_city_info", CommUtil.substring(city_name, 5));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@RequestMapping("/goods_share.htm")
	public ModelAndView goods_share(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_share.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	
	@RequestMapping("/goods_consult_win.htm")
	public ModelAndView goods_consult_win(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("default/goods_consult_win.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("goods_id", goods_id);
		mv.addObject("time", CommUtil.formatLongDate(new Date()));
		return mv;
	}

	@RequestMapping("/goods_consult_save.htm")
	public ModelAndView goods_consult_save(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String consult_content, String consult_type, String consult_code) {
		String verify_code = CommUtil.null2String(request.getSession(true).getAttribute("verify_code"));
		boolean visit_consult = true;
		if (!this.configService.getSysConfig().isVisitorConsult()) {
			if (SecurityUserHolder.getCurrentUser() == null) {
				visit_consult = false;
			}
		}
		boolean save_ret = true;
		if (visit_consult) {
			if (CommUtil.null2String(consult_code).equals(verify_code)) {
				Consult obj = new Consult();
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
				obj.setAddTime(new Date());
				obj.setConsult_type(consult_type);
				obj.setConsult_content(consult_content);
				User user = SecurityUserHolder.getCurrentUser();
				if (user != null) {
					obj.setConsult_user_id(user.getId());
					obj.setConsult_user_name(user.getUserName());
					obj.setConsult_email(user.getEmail());
				} else {
					obj.setConsult_user_name("游客");
				}
				List<Map> maps = new ArrayList<Map>();
				Map map = new HashMap();
				map.put("goods_id", goods.getId());
				map.put("goods_name", goods.getGoods_name());
				map.put("goods_main_photo", goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt());
				map.put("goods_price", goods.getGoods_current_price());
				String goods_domainPath = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
				if (this.configService.getSysConfig().isSecond_domain_open() && goods.getGoods_store() != null
						&& goods.getGoods_store().getStore_second_domain() != "" && goods.getGoods_type() == 1) {
					String store_second_domain = "http://" + goods.getGoods_store().getStore_second_domain() + "."
							+ CommUtil.generic_domain(request);
					goods_domainPath = store_second_domain + "/goods_" + goods.getId() + ".htm";
				}
				map.put("goods_domainPath", goods_domainPath); // 商品二级域名路径
				maps.add(map);
				obj.setGoods_info(Json.toJson(maps, JsonFormat.compact()));
				obj.setGoods_id(goods.getId());
				if (goods.getGoods_store() != null) {
					obj.setStore_id(goods.getGoods_store().getId());
					obj.setStore_name(goods.getGoods_store().getStore_name());
				} else {
					obj.setWhether_self(1);
				}
				save_ret = this.consultService.save(obj);
				request.getSession(true).removeAttribute("consult_code");
			}
		}
		ModelAndView mv = new JModelAndView("default/goods_consult.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject("1", mv, "addTime", "desc");
		qo.addQuery("obj.goods_id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		if (!"".equals(CommUtil.null2String(consult_type))) {
			qo.addQuery("obj.consult_type", new SysMap("consult_type", consult_type), "=");
		}

		qo.setPageSize(10);
		IPageList pList = this.consultService.list(qo);
		String url2 = CommUtil.getURL(request) + "/goods_consult.htm";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url2, "", pList.getCurrentPage(), pList.getPages()));

		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;

	}

	/**
	 * 用户对某条咨询点击满意
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping("/goods_consult_satisfy.htm")
	public void goods_consult_satisfy(HttpServletRequest request, HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(consult_id));
		if (user != null) { // 如果用户不为空，则更加用户id检测是否已经进行满意度点评
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService.query(
					"select obj from ConsultSatis obj where obj.cs_user_id=:user_id and obj.cs_consult_id=:cs_consult_id",
					params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.save(cs);
				//
				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.update(obj);
			}
		} else { // 用户为空则根据ip检测，如果该ip已经进行点评则不可以进行操作
			Map params = new HashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService.query(
					"select obj from ConsultSatis obj where obj.cs_ip=:cs_ip and obj.cs_consult_id=:cs_consult_id", params,
					-1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				this.consultsatisService.save(cs);
				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.update(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.getSatisfy());
		} catch (IOException e) {
			logger.error(e);
		}

	}

	/**
	 * 用户对某条评论点不满意
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping("/goods_consult_unsatisfy.htm")
	public void goods_consult_unsatisfy(HttpServletRequest request, HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		Consult obj = this.consultService.getObjById(CommUtil.null2Long(consult_id));
		if (user != null) { // 如果用户不为空，则更加用户id检测是否已经进行满意度点评
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService.query(
					"select obj from ConsultSatis obj where obj.cs_user_id=:user_id and obj.cs_consult_id=:cs_consult_id",
					params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.save(cs);
				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.update(obj);
			}
		} else { // 用户为空则根据ip检测，如果该ip已经进行点评则不可以进行操作
			Map params = new HashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService.query(
					"select obj from ConsultSatis obj where obj.cs_ip=:cs_ip and obj.cs_consult_id=:cs_consult_id", params,
					-1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				this.consultsatisService.save(cs);
				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.update(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.getUnsatisfy());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint().getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint().getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate - description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate - service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate, ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100 ? 100 : CommUtil.mul(
							description_result, 100)) + "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "value_light");
			mv.addObject("description_result", CommUtil.null2String(CommUtil.mul(-description_result, 100)) + "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "value_strong");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100) > 100 ? 100 : CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "value_light");
			mv.addObject("service_result", CommUtil.null2String(CommUtil.mul(-service_result, 100)) + "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100) > 100 ? 100 : CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "value_light");
			mv.addObject("ship_result", CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}

	/**
	 * 打开商品对比页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_compare.htm")
	public ModelAndView goods_compare(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_compare.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		List<GoodsCompareView> goods_compare_list = new ArrayList<GoodsCompareView>();
		if (goods_compare_cart != null && goods_compare_cart.size() > 0) {
			Goods goods = goods_compare_cart.get(goods_compare_cart.size() - 1);
			goods = this.goodsService.getObjById(goods.getId());
			GoodsClass gc = goods.getGc();
			if (goods.getGc().getParent() != null) {
				gc = goods.getGc().getParent();
			}
			if (goods.getGc().getParent() != null && goods.getGc().getParent().getParent() != null) {
				gc = goods.getGc().getParent().getParent();
			}
			mv.addObject("gc", gc);
			// 20150227add 当商品类型没有添加时屏蔽此处
			List<GoodsTypeProperty> gtps = null;
			if (gc.getGoodsType() != null) {
				gtps = gc.getGoodsType().getProperties();
			}
			mv.addObject("gtps", gtps);
			for (Goods obj : goods_compare_cart) {
				obj = this.goodsService.getObjById(obj.getId());
				GoodsCompareView gcv = new GoodsCompareView();
				gcv.setBad_evaluate(CommUtil.mul(CommUtil.div(new Integer(obj.getBad_evaluate()),  new Integer(obj.getEvaluate_count())), 100) + "%");
				gcv.setGoods_brand(obj.getGoods_brand() != null ? obj.getGoods_brand().getName() : "-----");
				gcv.setGoods_cod(obj.getGoods_cod() == 0 ? "支持" : "不支持");
				gcv.setGoods_id(obj.getId());
				gcv.setGoods_img(this.generic_goods_img(request, obj));
				gcv.setGoods_name(obj.getGoods_name());
				gcv.setGoods_price(obj.getGoods_current_price());
				gcv.setGoods_url(this.generic_goods_url(request, obj));
				gcv.setGoods_weight(obj.getGoods_weight());
				gcv.setMiddle_evaluate(CommUtil.mul(CommUtil.div(new Integer(obj.getMiddle_evaluate()),  new Integer(obj.getEvaluate_count())), 100) + "%");
				gcv.setTax_invoice(obj.getTax_invoice() == 0 ? "不支持" : "支持");
				gcv.setWell_evaluate(CommUtil.mul(CommUtil.div(new Integer(obj.getWell_evaluate()),  new Integer(obj.getEvaluate_count())), 100) + "%");
				if (gtps != null) {
					List<Map> list = Json.fromJson(List.class, obj.getGoods_property());
					Map gcv_props = new HashMap();
					for (GoodsTypeProperty gtp : gtps) {
						for (Map map : list) {
							if (CommUtil.null2Long(map.get("id")).equals(gtp.getId())) {
								if (!"".equals(CommUtil.null2String(map.get("val")))) {
									gcv_props.put(gtp.getName(), map.get("val"));
								} else {
									gcv_props.put(gtp.getName(), "-----");
								}
							}
						}
					}
					gcv.setProps(gcv_props);
				}
				goods_compare_list.add(gcv);
			}

		}
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}

	/**
	 * 生成商品主图片路径
	 * 
	 * @param obj
	 * @return
	 */
	private String generic_goods_img(HttpServletRequest request, Goods obj) {
		String img = "";
		if (obj.getGoods_main_photo() != null) {
			img = obj.getGoods_main_photo().getPath() + "/" + obj.getGoods_main_photo().getName() + "_middle."
					+ obj.getGoods_main_photo().getExt();
		} else {
			img = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName();
		}
		return img;
	}

	/**
	 * 生成商品url
	 * 
	 * @param request
	 * @param obj
	 * @return
	 */
	private String generic_goods_url(HttpServletRequest request, Goods obj) {
		String url = CommUtil.getURL(request) + "/goods_" + obj.getId() + ".htm";
		if (this.configService.getSysConfig().isSecond_domain_open() && obj.getGoods_type() == 1
				&& !"".equals(CommUtil.null2String(obj.getGoods_store().getStore_second_domain()))) {
			url = "http://" + obj.getGoods_store().getStore_second_domain() + "." + CommUtil.generic_domain(request) + "/"
					+ "/goods_" + obj.getId() + ".htm";
		}
		return url;
	}

	/**
	 * 添加商品到对比栏
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/add_goods_compare_cart.htm")
	public ModelAndView add_goods_compare_cart(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		if (goods_compare_cart == null) {
			goods_compare_cart = new ArrayList<Goods>();
		}
		if (goods_compare_cart.size() < 4) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			boolean add = true;
			for (Goods goods : goods_compare_cart) {
				if (goods.getId().equals(obj.getId())) {
					add = false;
				}
			}
			if (add) {
				goods_compare_cart.add(0, obj);
			}
		}
		request.getSession(true).setAttribute("goods_compare_cart", goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * 从商品对比栏移除商品
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_goods_compare_cart.htm")
	public ModelAndView remove_goods_compare_cart(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		request.getSession(true).setAttribute("goods_compare_cart", goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * 清空商品对比栏数据
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_all_goods_compare_cart.htm")
	public ModelAndView remove_all_goods_compare_cart(HttpServletRequest request, HttpServletResponse response) {
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		if(goods_compare_cart != null && !goods_compare_cart.isEmpty()){
			goods_compare_cart.clear();
			request.getSession(true).removeAttribute("goods_compare_cart");
		}
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * 在对比页删除某件商品
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_goods_compart.htm")
	public String remove_goods_compart(HttpServletRequest request, HttpServletResponse response, String goods_id) {
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession().getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		request.getSession(true).setAttribute("goods_compare_cart", goods_compare_cart);
		return "redirect:goods_compare.htm";
	}
}
