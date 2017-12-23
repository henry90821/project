package com.iskyshop.module.weixin.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.iskyshop.core.annotation.SecurityMapping;
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
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.module.weixin.manage.coupon.activity.CouponActivityComm;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.ConsultViewTools;
import com.iskyshop.view.web.tools.EvaluateViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * 
 * <p>
 * Title:WapGoodsViewAction
 * </p>
 * 
 * <p>
 * Description: 手机客户端商城前台商品请求控制器
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
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinGoodsViewAction {
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
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IConsultService consultService;
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
	private IFavoriteService favoriteService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponinfoService;
	
	/**
	 * 自营和商家店铺领取优惠券
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "用户领取优惠券", value = "/wap/goods_store_coupon.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/wap/goods_store_coupon.htm")
	public void autotrophy_store_coupon(HttpServletRequest request, HttpServletResponse response, String coupon_id,String goods_id,String flag) {
		if(flag != null) {
			try {
				response.sendRedirect(CommUtil.getURL(request) + "/wap/goods.htm?id=" + goods_id);
			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			String couponFlag="0";
			User user = SecurityUserHolder.getCurrentUser();
			Coupon coupon=this.couponService.getObjById(Long.parseLong(coupon_id));
			Map couponParams = new HashMap();
			couponParams.put("coupon_id", Long.parseLong(coupon_id));
			couponParams.put("user_id", user.getId());
			List<CouponInfo> listCouponInfo = this.couponinfoService.query("select obj from CouponInfo obj where obj.coupon.id=:coupon_id and obj.user.id=:user_id",couponParams, -1, -1);
			
			if(null!=listCouponInfo && listCouponInfo.size()>0){
				couponFlag="1";//此优惠券已领过
			}else{
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user);
				this.couponinfoService.save(info);
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().print(couponFlag);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 商品评论
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param id
	 * @return
	 */
	@RequestMapping("/wap/goods_eva.htm")
	public ModelAndView goods_evaluate_ajax(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id, String type) {
		ModelAndView mv = new JModelAndView("wap/goods_eva.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new JModelAndView("wap/goods_eva_data.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0), "=");
		if (!StringUtils.isNullOrEmpty(type)) {
			if ("well".equals(type)) {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap("evaluate_buyer_val", 1), "=");
			}
			if ("middle".equals(type)) {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap("evaluate_buyer_val", 0), "=");
			}
			if ("bad".equals(type)) {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap("evaluate_buyer_val", -1), "=");
			}
		}
		qo.setPageSize(10);
		IPageList eva_pList = this.evaluateService.list(qo);
		mv.addObject("eva_objs", eva_pList.getResult());
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 
	 * @param 成交记录
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping("/wap/order_record.htm")
	public ModelAndView order_record(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		ModelAndView mv = new JModelAndView("wap/order_record.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new JModelAndView("wap/order_record_data.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
		qo.setPageSize(10);
		IPageList order_eva_pList = this.evaluateService.list(qo);
		mv.addObject("order_objs", order_eva_pList.getResult());
		mv.addObject("totalPage", order_eva_pList.getPages());
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 
	 * @param 促销组合
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping("/wap/combin_goods.htm")
	public ModelAndView group_goods(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/combin_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", goods);
		mv.addObject("id", id);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 
	 * @param 产品咨询
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping("/wap/consult.htm")
	public ModelAndView consult(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView("wap/consult.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new JModelAndView("wap/consult_data.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		ConsultQueryObject cqo = new ConsultQueryObject(currentPage, mv, "addTime", "desc");
		cqo.addQuery("obj.goods_id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
		cqo.setPageSize(10);
		IPageList pList = this.consultService.list(cqo);
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("totalPage", pList.getPages());
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 手机客户端商城首页商品详情请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/wap/goods.htm")
	public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = null;
		String activity_id = PropertyUtil.getProperty("couponActivityId");;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		logger.info("未开启二级域名");
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
					"select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id", params, -1,
					-1);
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
				map.put("goods_img_path",
						obj.getGoods_main_photo() != null ? CommUtil.getURL(request) + "/"
								+ obj.getGoods_main_photo().getPath() + "/" + obj.getGoods_main_photo().getName()
								: CommUtil.getURL(request) + "/"
										+ this.configService.getSysConfig().getGoodsImage().getPath() + "/"
										+ this.configService.getSysConfig().getGoodsImage().getName());
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
					map.put("goods_img_path",
							obj.getGoods_main_photo() != null
									? obj.getGoods_main_photo().getPath() + "/" + obj.getGoods_main_photo().getName()
									: CommUtil.getURL(request) + "/"
											+ this.configService.getSysConfig().getGoodsImage().getPath() + "/"
											+ this.configService.getSysConfig().getGoodsImage().getName());
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
		if (obj != null) {
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj.getId());
			todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
			String click_from_str = todayGoodsLog.getGoods_click_from();
			Map<String, Integer> clickmap = (!StringUtils.isNullOrEmpty(click_from_str))
					? (Map<String, Integer>) Json.fromJson(click_from_str) : new HashMap<String, Integer>();
			String from = clickfrom_to_chinese(CommUtil.null2String(request.getParameter("from")));
			if (!StringUtils.isNullOrEmpty(from)) {
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
		}
		if (obj != null && obj.getGoods_status() == 0 || admin_view) {
			if (obj.getGoods_type() == 0) { // 平台自营商品
				mv = new JModelAndView("wap/goods_details.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status() && obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) { // 如果是团购商品，检查团购是否过期
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
					SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
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
					mv.addObject("seckillGoods", seckillGoods);
				} else if (obj.getSeckill_buy() == 2) { // 检查秒杀是否过期
					String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
					Map params = new HashMap();
					params.put("goods_id", obj.getId());
					params.put("gg_status", 2);
					SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
					if (seckillGoods.getEndTime().before(new Date())) {
						seckillGoods.setGg_status(3);
						seckillGoodsService.update(seckillGoods);

						obj.setSeckill_buy(0);
						obj.setGoods_inventory(
								obj.getGoods_inventory() + (seckillGoods.getGg_count() - seckillGoods.getGg_selled_count()));
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
					mv.addObject("seckillGoods", seckillGoods);
				}
				if (obj.getCombin_status() == 1) { // 如果是组合商品，检查组合是否过期
					Map params = new HashMap();
					params.put("endTime", new Date());
					params.put("main_goods_id", obj.getId());
					List<CombinPlan> combins = this.combinplanService.query(
							"select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
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
				if (obj.getOrder_enough_give_status() == 1) {
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
								this.goodsService.update(goods);
							}
						}
						this.buyGiftService.update(bg);
					}
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGift", true);
					}
				}
				if (obj.getOrder_enough_if_give() == 1) {
					BuyGift bg = this.buyGiftService.getObjById(obj.getBuyGift_id());
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGive", true);
					}
				}
				this.goodsService.update(obj);

				if (obj.getEnough_reduce() == 1) { // 如果是满就减商品，未到活动时间不作处理，活动时间显示满减信息，已过期则删除满减信息
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(obj.getOrder_enough_reduce_id()));
					if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())
							&& er.getErend_time().after(new Date())) { // 正在进行
						mv.addObject("enoughreduce", er);
					}
				}

				List<Map> gsps = (List<Map>) CommUtil.json2List(obj.getGoods_specs_info());
				List<GoodsSpecProperty> gspsBase = obj.getGoods_specs();
				List<GoodsSpecProperty> gspsBaseCopy = new ArrayList<GoodsSpecProperty>();
				for (GoodsSpecProperty gsp : gspsBase) {
					GoodsSpecProperty s = new GoodsSpecProperty();
					s.setId(gsp.getId());
					s.setSpecImage(gsp.getSpecImage());
					s.setValue(gsp.getValue());
					s.setSpec(gsp.getSpec());
					for (Map m : gsps) {
						if (s.getId().toString().equals(m.get("id"))) {
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
				mv.addObject("userTools", userTools);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("activityViewTools", activityViewTools);

				String type = CommUtil.null2String(request.getAttribute("type"));
				String cart_session_id = "";
				Cookie[] cookies1 = request.getCookies();
				if (cookies1 != null) {
					for (Cookie cookie : cookies1) {
						if ("cart_session_id".equals(cookie.getName())) {
							cart_session_id = CommUtil.null2String(cookie.getValue());
						}
					}
				}
				if (StringUtils.isNullOrEmpty(cart_session_id)) {
					cart_session_id = UUID.randomUUID().toString();
					Cookie cookie = new Cookie("cart_session_id", cart_session_id);
					cookie.setDomain(CommUtil.generic_domain(request));
				}
				List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
				List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>(); // 未提交的用户cookie购物车
				List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
				User user = SecurityUserHolder.getCurrentUser();
				Map cart_map = new HashMap();
				if (user != null) {
					user = userService.getObjById(user.getId());
					if (!StringUtils.isNullOrEmpty(cart_session_id)) {
						cart_map.clear();
						cart_map.put("cart_session_id", cart_session_id);
						cart_map.put("cart_status", 0);
						carts_cookie = this.goodsCartService.query(
								"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
						// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
						if (user.getStore() != null) {
							for (GoodsCart gc : carts_cookie) {
								if (gc.getGoods().getGoods_type() == 1) { // 该商品为商家商品
									if (gc.getGoods().getGoods_store().getId().equals(user.getStore().getId())) {
										this.goodsCartService.delete(gc.getId());
									}
								}
							}
						}
						cart_map.clear();
						cart_map.put("user_id", user.getId());
						cart_map.put("cart_status", 0);
						carts_user = this.goodsCartService.query(
								"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
					} else {
						cart_map.clear();
						cart_map.put("user_id", user.getId());
						cart_map.put("cart_status", 0);
						carts_user = this.goodsCartService.query(
								"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
					}
				} else {
					if (!StringUtils.isNullOrEmpty(cart_session_id)) {
						cart_map.clear();
						cart_map.put("cart_session_id", cart_session_id);
						cart_map.put("cart_status", 0);
						carts_cookie = this.goodsCartService.query(
								"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
					}
				}
				// 将cookie购物车与user购物车合并，并且去重
				if (user != null) {
					for (GoodsCart cookie : carts_cookie) {
						boolean add = true;
						for (GoodsCart gc2 : carts_user) {
							if (cookie.getGoods().getId().equals(gc2.getGoods().getId())) {
								if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
									add = false;
									this.goodsCartService.delete(cookie.getId());
								}
							}
						}
						if (add) { // 将cookie去重并添加到cart_list中
							cookie.setCart_session_id(null);
							cookie.setUser(user);
							this.goodsCartService.update(cookie);
							carts_list.add(cookie);
						}
					}
				} else {
					for (GoodsCart gc : carts_cookie) { // 将carts_cookie添加到cart_list中
						carts_list.add(gc);
					}
				}
				for (GoodsCart gc : carts_user) { // 将carts_user添加到cart_list中
					carts_list.add(gc);
				}
				// 组合套装处理，只显示套装主购物车,套装内其他购物车不显示
				List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
				for (GoodsCart gc : carts_list) {
					if (gc.getCart_type() != null && "combin".equals(gc.getCart_type())) {
						if (gc.getCombin_main() != 1) {
							combin_carts_list.add(gc);
						}
					}
				}
				if (combin_carts_list.size() > 0) {
					carts_list.removeAll(combin_carts_list);
				}
				mv.addObject("carts", carts_list);
				//自营优惠券活动
				Map allCouponParams = new HashMap();
				allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
				allCouponParams.put("acType", 1);// 活动类型，0为商品活动，1为优惠券活动
				allCouponParams.put("ac_status", 1);// 活动状态，0为关闭，1为启动
				allCouponParams.put("ag_status", 1);// 活动商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2为已经到期关闭
				allCouponParams.put("activity_id",CommUtil.null2Long(activity_id));				
				allCouponParams.put("coupon_type", 0);// 优惠券类型，0为平台优惠券，抵消自营商品订单金额，1为商家优惠券，抵消订单中该商家商品部分金额
				
				List<Coupon> allCouponList = new ArrayList<Coupon>();
				List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(
						"select obj from ActivityGoods obj where obj.act.ac_status=:ac_status "
						+ "and obj.act.id=:activity_id and obj.acType=:acType and obj.ag_status=:ag_status "
						+ "and obj.act.ac_begin_time<=:current_time "
						+ "and obj.act.ac_end_time>=:current_time "
						+ "and obj.coupon.coupon_begin_time<=:current_time "
						+ "and obj.coupon.coupon_end_time>=:current_time "
						+ "and obj.coupon.coupon_type=:coupon_type "
						+ "and obj.coupon.store is null "
						+ "order by obj.coupon.coupon_amount",
						allCouponParams, -1, -1);
				for (ActivityGoods allActivityGoods : allActivityGoodsList) {
					allCouponList.add(allActivityGoods.getCoupon());
				}

				
				List<Coupon> allReceiveCouponList = new ArrayList<Coupon>();
				for (Coupon allCoupons : allCouponList) {
					List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
					int allCouponInfoLen = 0;
					if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
						allCouponInfoLen = allCouponInfoList.size();
					}
					if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
						allReceiveCouponList.add(allCoupons);
					}
				}
				mv.addObject("goods_id", id);
				mv.addObject("allReceiveCouponList", allReceiveCouponList);
				if(SecurityUserHolder.getCurrentUser() != null) {
					mv.addObject("logined", 1);
				}		
			} else {
				mv = new JModelAndView("wap/goods_details.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status() && obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) { // 如果是团购商品，检查团购是否过期
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				if (obj.getCombin_status() == 1) { // 如果是组合商品，检查组合是否过期
					Map params = new HashMap();
					params.put("endTime", new Date());
					params.put("main_goods_id", obj.getId());
					List<CombinPlan> combins = this.combinplanService.query(
							"select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
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
				if (obj.getOrder_enough_give_status() == 1) {
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
								this.goodsService.update(goods);
							}
						}
						this.buyGiftService.update(bg);
					}
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGift", true);
					}
				}
				if (obj.getOrder_enough_if_give() == 1) {
					BuyGift bg = this.buyGiftService.getObjById(obj.getBuyGift_id());
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGive", true);
					}
				}
				this.goodsService.update(obj);

				if (obj.getEnough_reduce() == 1) { // 如果是满就减商品，未到活动时间不作处理，活动时间显示满减信息
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(obj.getOrder_enough_reduce_id()));
					if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())
							&& er.getErend_time().after(new Date())) { // 正在进行
						mv.addObject("enoughreduce", er);
					}
				}
				// if (obj.getGoods_store().getStore_status() == 15) { // 店铺为开通状态

				List<Map> gsps = (List<Map>) CommUtil.json2List(obj.getGoods_specs_info());
				List<GoodsSpecProperty> gspsBase = obj.getGoods_specs();
				List<GoodsSpecProperty> gspsBaseCopy = new ArrayList<GoodsSpecProperty>();
				for (GoodsSpecProperty gsp : gspsBase) {
					GoodsSpecProperty s = new GoodsSpecProperty();
					s.setId(gsp.getId());
					s.setSpecImage(gsp.getSpecImage());
					s.setValue(gsp.getValue());
					s.setSpec(gsp.getSpec());
					for (Map m : gsps) {
						if (s.getId().toString().equals(m.get("id"))) {
							s.setValue(CommUtil.null2String(m.get("name")));
						}
					}
					gspsBaseCopy.add(s);
				}
				mv.addObject("gspsCopy", gspsBaseCopy);

				mv.addObject("obj", obj);
				mv.addObject("store", obj.getGoods_store());
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
				this.generic_evaluate(obj.getGoods_store(), mv);

				mv.addObject("userTools", userTools);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("activityViewTools", activityViewTools);
				
				//商家优惠券活动
				Map allCouponParams = new HashMap();
				allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
				allCouponParams.put("acType", 1);
				allCouponParams.put("ac_status", 1);
				allCouponParams.put("ag_status", 1);
				allCouponParams.put("coupon_type", 1);
				allCouponParams.put("activity_id",CommUtil.null2Long(activity_id));
				allCouponParams.put("storeId", obj.getGoods_store().getId());

				List<Coupon> allCouponList = new ArrayList<Coupon>();
				List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(
						"select obj from ActivityGoods obj where obj.act.ac_status=:ac_status "
						+ "and obj.act.id=:activity_id and obj.acType=:acType and obj.ag_status=:ag_status "
						+ "and obj.act.ac_begin_time<=:current_time "
						+ "and obj.act.ac_end_time>=:current_time "
						+ "and obj.coupon.coupon_begin_time<=:current_time "
						+ "and obj.coupon.coupon_end_time>=:current_time "
						+ "and obj.coupon.coupon_type=:coupon_type "
						+ "and obj.coupon.store.id =:storeId "
						+ "order by obj.coupon.coupon_amount",
						allCouponParams, -1, -1);
				for (ActivityGoods allActivityGoods : allActivityGoodsList) {
					allCouponList.add(allActivityGoods.getCoupon());
				}

				List<Coupon> allReceiveCouponList = new ArrayList<Coupon>();
				for (Coupon allCoupons : allCouponList) {
					//获取优惠券详情信息数量
					List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
					int allCouponInfoLen = 0;
					if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
						allCouponInfoLen = allCouponInfoList.size();
					}
					if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
						allReceiveCouponList.add(allCoupons);
					}
				}
				mv.addObject("goods_id", id);
				mv.addObject("allReceiveCouponList", allReceiveCouponList);
				if(SecurityUserHolder.getCurrentUser() != null) {
					mv.addObject("logined", 1);
				}		
			}
			if (SecurityUserHolder.getCurrentUser() != null) {
				Map map = new HashMap();
				map.put("gid", obj.getId());
				map.put("uid", SecurityUserHolder.getCurrentUser().getId());
				List<Favorite> favorites = this.favoriteService
						.query("select obj from Favorite obj where obj.goods_id=:gid and obj.user_id=:uid", map, -1, -1);
				if (favorites.size() > 0) {
					mv.addObject("mark", 1);
				}
			}
			mv.addObject("evaluates_count", obj.getEvaluate_count());
			int consul_count = this.consultViewTools.queryByType(null, obj.getId().toString()).size();
			mv.addObject("consul_count", consul_count);
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，商品查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("nowTime", CommUtil.formatLongDate(new Date()));
		return mv;
	}

	/**
	 * 推荐和热卖的商品列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String type, String store_id,
			String begin_count, String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("wap/goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String sql = "";
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if ("goods_collect".equals(CommUtil.null2String(orderBy))) {
			sql = sql + " by obj.goods_collect desc";
		}
		if ("goods_salenum".equals(CommUtil.null2String(orderBy))) {
			sql = sql + " by obj.goods_salenum desc";
		}
		if ("store_price".equals(CommUtil.null2String(orderBy))) {
			sql = sql + " by obj.store_price " + orderType;
		}
		Map params = new HashMap();
		if (type != null && "h".equals(type)) {
			params.put("mobile_hot", 1);
			params.put("goods_status", 0);
			if (!StringUtils.isNullOrEmpty(CommUtil.null2String(orderBy))) {
				List<Goods> goods_hots = this.goodsService
						.query("select obj from Goods obj where obj.mobile_hot=:mobile_hot and obj.goods_status =:goods_status order"
								+ sql, params, 0, 6);
				mv.addObject("goods", goods_hots);
				mv.addObject("type", "mobile_hot");
			} else {
				List<Goods> goods_hots = this.goodsService.query(
						"select obj from Goods obj where obj.mobile_hot=:mobile_hot and obj.goods_status =:goods_status order by obj.mobile_hotTime desc",
						params, 0, 6);
				mv.addObject("goods", goods_hots);
				mv.addObject("type", "mobile_hot");
			}
		}
		if (type != null && "r".equals(type)) {
			params.put("mobile_recommend", 1);
			params.put("goods_status", 0);
			if (!StringUtils.isNullOrEmpty(CommUtil.null2String(orderBy))) {
				List<Goods> goods_recommends = this.goodsService
						.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order"
								+ sql, params, 0, 6);
				mv.addObject("goods", goods_recommends);
				mv.addObject("type", "mobile_recommend");
			} else {
				List<Goods> goods_recommends = this.goodsService.query(
						"select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by obj.mobile_recommendTime desc",
						params, 0, 6);
				mv.addObject("goods", goods_recommends);
				mv.addObject("type", "mobile_recommend");
			}
		}
		if (!StringUtils.isNullOrEmpty(CommUtil.null2String(store_id))) {
			params.clear();
			params.put("store_id", CommUtil.null2Long(store_id));
			params.put("goods_status", 0);
			if (!StringUtils.isNullOrEmpty(CommUtil.null2String(orderBy))) {
				List<Goods> store_goods = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status =:goods_status order"
								+ sql, params, 0, 6);
				mv.addObject("goods", store_goods);
				mv.addObject("store_id", store_id);
				mv.addObject("type", "store_id");
			} else {
				List<Goods> store_goods = this.goodsService.query(
						"select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status =:goods_status",
						params, 0, 6);
				mv.addObject("goods", store_goods);
				mv.addObject("store_id", store_id);
				mv.addObject("type", "store_id");
			}
		}
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;

	}

	/**
	 * 根据商品id和地区名称查询相应商品运费
	 * 
	 * @param request
	 * @param response
	 * @param current_city
	 * @param goods_id
	 */
	@RequestMapping("/wap/goods_trans_fee.htm")
	public void goods_trans_fee(HttpServletRequest request, HttpServletResponse response, String current_city,
			String goods_id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			// 设置运费信息
			String trans_information = "商家承担";
			if (obj.getGoods_transfee() == 0) {
				if (!StringUtils.isNullOrEmpty(obj.getTransport())) {
					String main_info = "平邮(¥" + this.transportTools.cal_goods_trans_fee(
							obj.getTransport().getId().toString(), "mail", 1, CommUtil.null2String(obj.getGoods_weight()),
							CommUtil.null2String(obj.getGoods_volume()), current_city);
					String express_info = "快递(¥" + this.transportTools.cal_goods_trans_fee(
							obj.getTransport().getId().toString(), "express", 1, CommUtil.null2String(obj.getGoods_weight()),
							CommUtil.null2String(obj.getGoods_volume()), current_city);
					String ems_info = "EMS(¥" + this.transportTools.cal_goods_trans_fee(
							obj.getTransport().getId().toString(), "ems", 1, CommUtil.null2String(obj.getGoods_weight()),
							CommUtil.null2String(obj.getGoods_volume()), current_city);
					trans_information = main_info + ") | " + express_info + ") | " + ems_info + ")";
				} else {
					trans_information = "平邮(¥" + CommUtil.null2Float(obj.getMail_trans_fee()) + ") | " + "快递(¥"
							+ CommUtil.null2Float(obj.getExpress_trans_fee()) + ") | " + "EMS(¥"
							+ CommUtil.null2Float(obj.getEms_trans_fee()) + ")";
				}
			}
			json_map.put("trans_information", trans_information);
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			logger.info(Json.toJson(json_map, JsonFormat.compact()));
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 点击商品规格加载商品规格信息
	 * 
	 * @param request
	 * @param response
	 * @param gsp
	 * @param id
	 */
	@RequestMapping("/wap/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request, HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
		int count = 0;
		double price = 0;
		double act_price = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) { // 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if ("spec".equals(goods.getInventory_type())) {
				List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						count = CommUtil.null2Int(temp.get("count"));
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2 && SecurityUserHolder.getCurrentUser() != null) { // 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools
					.query_user_level(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			act_price = CommUtil.mul(rebate, price);
		}
		map.put("count", count);
		map.put("price", CommUtil.formatMoney(price));
		if (act_price != 0) {
			map.put("act_price", CommUtil.formatMoney(act_price));
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

	/**
	 * 手机客户端商城商品规格
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/wap/goods_specs.htm")
	public void goods_specs(HttpServletRequest request, HttpServletResponse response, String id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map map = new HashMap();
		List list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			List<GoodsSpecification> specs = goodsViewTools.generic_spec(id);
			for (GoodsSpecification spec : specs) {
				Map spec_map = new HashMap();
				spec_map.put("spec_type", spec.getType());
				spec_map.put("spec_key", spec.getName());
				List spec_list = new ArrayList();
				for (GoodsSpecProperty spro : obj.getGoods_specs()) {
					if (spro.getSpec().getId().equals(spec.getId())) {
						Map map_par = new HashMap();
						map_par.put("id", spro.getId());
						map_par.put("val", spro.getValue());
						spec_list.add(map_par);
					}
				}
				spec_map.put("spec_values", spec_list);
				list.add(spec_map);
			}
			map.put("spec_list", list);
		}
		map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 手机客户端商品详细介绍
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/wap/goods_introduce.htm")
	public ModelAndView goods_introduce(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/goods_introduce.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * 手机客户端商城商品资讯信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_consult.htm")
	public ModelAndView goods_consult(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String consult_type, String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_consult.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.goods_id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		if (!StringUtils.isNullOrEmpty(consult_type)) {
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
	 * 手机客户端发布商品咨询信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_consult_save.htm")
	public ModelAndView goods_consult_save(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String consult_content, String consult_type, String consult_code) {
		String verify_code = CommUtil.null2String(request.getSession(true).getAttribute("consult_code"));
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
		if (!StringUtils.isNullOrEmpty(CommUtil.null2String(consult_type))) {
			qo.addQuery("obj.consult_type", new SysMap("consult_type", consult_type), "=");
		}
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/goods_consult.htm", "", "", pList, mv);
		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}

	/**
	 * 手机客户端收藏商品请求
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_favorite_save.htm")
	public void goods_favorite_save(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String user_id, String token) {
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("goods_id", CommUtil.null2Long(goods_id));
		List<Favorite> list = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id", params, -1, -1);
		int ret = 0;
		if (list.size() == 0) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			Favorite obj = new Favorite();
			obj.setAddTime(new Date());
			obj.setType(0);
			User user = SecurityUserHolder.getCurrentUser();
			obj.setUser_name(user.getUserName());
			obj.setUser_id(user.getId());
			obj.setGoods_id(goods.getId());
			obj.setGoods_name(goods.getGoods_name());
			obj.setGoods_photo(goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName());
			obj.setGoods_photo_ext(goods.getGoods_main_photo().getExt());
			obj.setGoods_store_id(goods.getGoods_store() == null ? null : goods.getGoods_store().getId());
			obj.setGoods_type(goods.getGoods_type());
			obj.setGoods_current_price(goods.getGoods_current_price());
			if (this.configService.getSysConfig().isSecond_domain_open()) {
				Store store = this.storeService.getObjById(obj.getStore_id());
				obj.setGoods_store_second_domain(store.getStore_second_domain());
			}
			this.favoriteService.save(obj);
			goods.setGoods_collect(goods.getGoods_collect() + 1);
			this.goodsService.update(goods);
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj.getId());
			todayGoodsLog.setGoods_collect(todayGoodsLog.getGoods_collect() + 1);
			this.goodsLogService.update(todayGoodsLog);
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		} else {
			ret = 1;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 手机客户端商城首页商品详情底部“你可能喜欢的商品”列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/like_goods_list.htm")
	public void like_goods_list(HttpServletRequest request, HttpServletResponse response, String id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map goods_map = new HashMap();
		List goods_list = new ArrayList();
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			Map params = new HashMap();
			params.put("gid", CommUtil.null2Long(id));
			params.put("gc_id", obj.getGc().getId());
			params.put("goods_status", 0);
			List<Goods> lists_goods = this.goodsService.query(
					"select obj from Goods obj where obj.id!=:gid and obj.gc.id=:gc_id and obj.goods_status=:goods_status",
					params, 0, 30);
			String url = CommUtil.getURL(request);
			for (Goods goods : lists_goods) {
				Map map = new HashMap();
				map.put("id", goods.getId());
				map.put("name", goods.getGoods_name());
				String goods_main_photo = url // 系统默认商品图片
						+ "/" + this.configService.getSysConfig().getGoodsImage().getPath() + "/"
						+ this.configService.getSysConfig().getGoodsImage().getName();
				if (goods.getGoods_main_photo() != null) { // 商品主图片
					goods_main_photo = url + "/" + goods.getGoods_main_photo().getPath() + "/"
							+ goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
				}
				map.put("goods_main_photo", goods_main_photo);
				goods_list.add(map);
			}
			goods_map.put("goods_list", goods_list);
		}
		goods_map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(goods_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			logger.error(e);
		}
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
			mv.addObject("description_result",
					CommUtil.null2String(
							CommUtil.mul(description_result, 100) > 100 ? 100 : CommUtil.mul(description_result, 100))
					+ "%");
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
			mv.addObject("service_result", CommUtil.null2String(CommUtil.mul(service_result, 100)) + "%");
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
			mv.addObject("ship_result", CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
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
}
