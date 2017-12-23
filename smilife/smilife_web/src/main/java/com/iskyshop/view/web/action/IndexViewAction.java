package com.iskyshop.view.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsCase;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsFloor;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsCaseService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFloorService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPartnerService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.FreeTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.view.web.tools.GoodsCaseViewTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsFloorViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: Complaint.java
 * </p>
 * 
 * <p>
 * Description:商城首页控制器
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
 * @author erikzhang、jinxinzhe、hezeng
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class IndexViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsFloorViewTools gfTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCaseService goodsCaseService;
	@Autowired
	private GoodsCaseViewTools goodsCaseViewTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private ISubjectService subjectQueryObject;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IFreeGoodsService freeGoodsService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private ImageTools imageTools;


	private int index_recommend_count = 5;// 首页推荐商品及推荐用户喜欢的商品个数，所有在这个页面位置的商品都以该数量作为查询基准，定义为一个参数，便于修改

	/**
	 * 前台公用顶部页面，使用自定义标签httpInclude.include("/top.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/top.htm")
	public ModelAndView top(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("top.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}

	/**
	 * 前台公用导航主菜单页面，使用自定义标签httpInclude.include("/nav.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/nav.htm")
	public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		return mv;
	}

	/**
	 * 带有全部商品分类的导航菜单，使用自定义标签httpInclude.include("/nav1.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/nav1.htm")
	public ModelAndView nav1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		String id = CommUtil.null2String(request.getAttribute("id"));
		mv.addObject("op", "/" + op + "/index.htm");
		if ("activity".equals(op) && id != null && !"".equals(id)) {
			mv.addObject("op", "activity/index_" + id + ".htm");
		}
		if ("subject".equals(op) && id != null && !"".equals(id)) {
			mv.addObject("op", "subject/view_" + id + ".htm");
		}
		return mv;
	}

	/**
	 * 前台公用head页面，包含系统logo及全文搜索，使用自定义标签httpInclude.include("/head.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/head.htm")
	public ModelAndView head(HttpServletRequest request, HttpServletResponse response, String keyword) {
		ModelAndView mv = new JModelAndView("head.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("cart_session_id".equals(cookie.getName())) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if ("".equals(cart_session_id)) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!"".equals(cart_session_id)) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
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
			if (!"".equals(cart_session_id)) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
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
				if (add) {// 将cookie去重并添加到cart_list中
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {// 将carts_cookie添加到cart_list中
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {// 将carts_user添加到cart_list中
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
		mv.addObject("type", "".equals(type) ? "goods" : type);
		mv.addObject("keyword", keyword);
		return mv;
	}

	/**
	 * 用户登录页顶部，使用include加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_head.htm")
	public ModelAndView login_head(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("login_head.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 首页商品楼层数据，该数据纳入系统缓存页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping("/floor.htm")
	public ModelAndView floor(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
		ModelAndView mv = new JModelAndView("floor.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null",	params, -1, -1);
		
		Collections.sort(floors, new Comparator<GoodsFloor>(){
			@Override
			public int compare(GoodsFloor o1, GoodsFloor o2) {
				return o1.getGf_sequence() - o2.getGf_sequence();
			}
			
		});
		
		mv.addObject("floors", floors);
		mv.addObject("gf_tools", this.gfTools);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}
	
	/**
	 * 首页商品楼层数据，该数据纳入系统缓存页面
	 * @param request
	 * @param response
	 * @param fnum ： 要加载的楼层(大于0的整数)。若fnum为null，则表示加载所有楼层的数据
	 * @return 返回指定的楼层数据或所有楼层的数据。若指定的楼层不存在，则返回空数组
	 * @throws InterruptedException
	 */
	@RequestMapping("/floor_data.htm")
	public void floor_new(HttpServletRequest request, HttpServletResponse response, Integer fnum) throws InterruptedException {		
		List<Map> floorInfos = new ArrayList<Map>();

		List<GoodsFloor> floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=true and obj.parent.id is null", null, -1, -1);
		Collections.sort(floors, new Comparator<GoodsFloor>(){
			@Override
			public int compare(GoodsFloor o1, GoodsFloor o2) {
				return o1.getGf_sequence() - o2.getGf_sequence();
			}
			
		});
		
		if(fnum == null || (fnum <= floors.size() && fnum > 0)) {
			SysConfig config = configService.getSysConfig();
			
			String webPath = CommUtil.getWebPath(request, config);
			String left_nav_icon = webPath + "/resources/style/system/front/default/images/left_nav.png";
			String loader1_icon = webPath + "/resources/style/common/images/loader1.gif";
			String default_goods_img = webPath + config.getGoodsImage().getPath() + "/" + config.getGoodsImage().getName();			
			
			int start = 0, end = floors.size();
			if(fnum != null) {
				start = fnum - 1;
				end = fnum;
			}
			for(; start < end; start++) {
				GoodsFloor gf = floors.get(start);
				Map floorInfo = new HashMap();
				floorInfo.put("id", gf.getId());
				floorInfo.put("gf_css", gf.getGf_css());
				floorInfo.put("fnum", start + 1);
				floorInfo.put("gf_name", gf.getGf_name());
				if(gf.getIcon() != null) {
					floorInfo.put("icon", gf.getIcon().getPath() + "/" + gf.getIcon().getName());
				} else {
					floorInfo.put("icon", left_nav_icon);
				}
				floorInfo.put("gf_left_adv", this.gfTools.generic_adv(CommUtil.getURL(request), gf.getGf_left_adv()));
				floorInfo.put("loader1_url", loader1_icon);
				floorInfo.put("default_goods_img", default_goods_img);
				List<Map<String, String>> gcs = new ArrayList<Map<String, String>>();
				for(GoodsClass gc: this.gfTools.getGoodsFloorGcs(gf.getGf_gc_list())) {
					Map<String, String> tmp = new HashMap<String, String>();
					tmp.put("gcname", gc.getClassName());
					tmp.put("goodslist_url", webPath + "/store_goods_list_" + gc.getId() + ".htm");
					gcs.add(tmp);
				}
				floorInfo.put("gcs", gcs);
				
				//加载当前主楼层中的子楼层
				List<Map> subFloorsInfo = new ArrayList<Map>();
				for(GoodsFloor subGf: gf.getChilds()) {
					if(subGf.isGf_display()) {
						Map t = new HashMap();
						t.put("id", subGf.getId());
						t.put("gf_name", subGf.getGf_name());
						t.put("style", subGf.getGf_style());
						List<Map> subFloorGoodsInfo = new ArrayList<Map>();
						if("style1".equals(subGf.getGf_style())) {
							for(Goods g: this.gfTools.generic_goods(subGf.getGf_gc_goods())) {
								Map goodsInfo = new HashMap();
								goodsInfo.put("goods_name", g.getGoods_name());
								goodsInfo.put("goods_current_price", g.getGoods_current_price());
								goodsInfo.put("goods_price", g.getGoods_price());
								if(g.getGoods_main_photo() != null) {
									goodsInfo.put("img", g.getGoods_main_photo().getPath() + "/" + g.getGoods_main_photo().getName() + "_middle." + g.getGoods_main_photo().getExt());
								} else {
									goodsInfo.put("img", default_goods_img);
								}
								if(config.isSecond_domain_open() && g.getGoods_type() == 1 && !StringUtils.isNullOrEmpty(g.getGoods_store().getStore_second_domain())) {
									goodsInfo.put("goods_url", "http://" + g.getGoods_store().getStore_second_domain() + "." + CommUtil.generic_domain(request) + "/goods_" + g.getId() + "_floor.htm");
								} else {
									goodsInfo.put("goods_url", webPath + "/goods_" + g.getId() + "_floor.htm");
								}
								subFloorGoodsInfo.add(goodsInfo);
							}
						} else {
							if(subGf.getGf_style2_goods() != null) {
								List<Map> goodsInfos = Json.fromJson(List.class, subGf.getGf_style2_goods());
								if(goodsInfos != null) {
									for(Map gi: goodsInfos) {
										subFloorGoodsInfo.add(gi);
									} 
								}
							}							
						}
						t.put("goods", subFloorGoodsInfo);					
						subFloorsInfo.add(t);
					}					
				}
				floorInfo.put("children", subFloorsInfo);
				Map hotGoods = this.gfTools.generic_goods_list(gf.getGf_list_goods());
				Map hotInfo = new HashMap();
				hotInfo.put("title", hotGoods.get("list_title"));
				List<Map> goodsInfos = new ArrayList<Map>();
				for(int i = 1; i <= 5; i++) {
					Goods goods = (Goods)hotGoods.get("goods" + i);
					if(goods != null) {
						Map goodsInfo = new HashMap();
						goodsInfo.put("goods_name", goods.getGoods_name());
						goodsInfo.put("goods_current_price", goods.getGoods_current_price());
						goodsInfo.put("goods_salenum", goods.getGoods_salenum());
						
						if(goods.getGoods_main_photo() != null) {
							goodsInfo.put("img", goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_middle." + goods.getGoods_main_photo().getExt());
						} else {
							goodsInfo.put("img", default_goods_img);
						}
						if(config.isSecond_domain_open() && goods.getGoods_type() == 1 && !StringUtils.isNullOrEmpty(goods.getGoods_store().getStore_second_domain())) {
							goodsInfo.put("goods_url", "http://" + goods.getGoods_store().getStore_second_domain() + "." + CommUtil.generic_domain(request) + "/goods_" + goods.getId() + "_floor.htm");
						} else {
							goodsInfo.put("goods_url", webPath + "/goods_" + goods.getId() + "_floor.htm");
						}
						
						goodsInfos.add(goodsInfo);
					}
				}
				hotInfo.put("goods", goodsInfos);
				floorInfo.put("hot", hotInfo);
				floorInfos.add(floorInfo);
			}
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().print(Json.toJson(floorInfos, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	

	/**
	 * 前台公用顶部导航页面，使用自定义标签httpInclude.include("/footer.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/footer.htm")
	public ModelAndView footer(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("footer.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		Map params = new HashMap();
		Set marks = new TreeSet();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shanjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		marks.add("AppBroadcast");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("acs", acs);
		return mv;
	}

	/**
	 * 商城首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("index.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String user_agent =  request.getHeader("User-Agent");
		if(StringUtils.isNullOrEmpty(user_agent)) {
			user_agent = "";
		}
		user_agent = user_agent.toLowerCase();
		if (user_agent.indexOf("iphone") >= 0 || user_agent.indexOf("android") >= 0) {
			mv = new JModelAndView("wap/index.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			goto_wap_index(request, response, mv);
			return mv;
		}
		Map params = new HashMap();
		params.clear();
		List<Partner> img_partners = this.partnerService.query(
				"select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
		mv.addObject("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService.query(
				"select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
		mv.addObject("text_partners", text_partners);
		params.clear();
		Set marks = new TreeSet();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shanjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		marks.add("AppBroadcast");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("acs", acs);
		params.clear();
		String[] class_marks = { "gonggao", "guize", "anquan", "zhinan" }; // 首页右上角公告区分类的标识，通过后台添加
		Map articles = new LinkedHashMap();
		for (String m : class_marks) {
			params.put("class_mark", m);
			params.put("display", true);
			params.put("type", "user");
			List<Article> article = this.articleService
					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.type=:type and obj.display=:display order by obj.addTime desc",
							params, 0, 3);
			articles.put(m, article);
		}
		mv.addObject("articles", articles);
		mv.addObject("goodsCaseViewTools", goodsCaseViewTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		}
		params.clear();
		params.put("show_index", true);
		params.put("audit", 1);
		List<GoodsBrand> goodsBrands = this.goodsBrandService
				.query("select new GoodsBrand(id,name,brandLogo) from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
						params, 0, 4);
		mv.addObject("goodsBrands", goodsBrands);
		mv.addObject("navTools", navTools);
		// 查询团购6件商品在首页显示,更加团购成功数量倒叙，团购成功数量越多的越靠前
		params.clear();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("group_type", 0);
		List<Group> groups = this.groupService
				.query("select obj.id from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
						params, -1, -1);
		List<GroupGoods> group_goods_list = new ArrayList<GroupGoods>();
		if (groups.size() > 0) {
			params.clear();
			params.put("group_id", CommUtil.null2Long(groups.get(0)));
			params.put("gg_status", 1);
			group_goods_list = this.groupGoodsService
					.query("select new GroupGoods(id,gg_price,gg_img) from GroupGoods obj where obj.group.id=:group_id and obj.gg_status=:gg_status order by obj.gg_selled_count desc",
							params, 0, 6);
		}
		mv.addObject("group_goods_list", group_goods_list);
		return mv;
	}

	/**
	 * 商城关闭时候导向的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/close.htm")
	public ModelAndView close(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("close.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	@RequestMapping("/404.htm")
	public ModelAndView error404(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/404.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		if (userAgent == null || userAgent.indexOf("Mobile") == -1) {
			mv = new JModelAndView("404.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("navTools", navTools);
		return mv;
	}

	@RequestMapping("/500.htm")
	public ModelAndView error500(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/500.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		if (userAgent == null || userAgent.indexOf("Mobile") == -1) {
			mv = new JModelAndView("500.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("navTools", navTools);
		return mv;
	}

	/**
	 * 商城商品分类导航页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_class.htm")
	public ModelAndView goods_class(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_class.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		params.put("recommend", true);
		List<GoodsClass> recommend_gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.recommend=:recommend order by obj.sequence asc",
						params, 0, 7);
		mv.addObject("gcs", gcs);
		mv.addObject("recommend_gcs", recommend_gcs);
		return mv;
	}

	@RequestMapping("/switch_case_goods.htm")
	public ModelAndView switch_case_goods(HttpServletRequest request, HttpServletResponse response, String goods_random,
			String caseid) {
		ModelAndView mv = new JModelAndView("switch_case_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCase goodscase = this.goodsCaseService.getObjById(CommUtil.null2Long(caseid));
		List list = (List) Json.fromJson(goodscase.getCase_content());
		List<Goods> goods_list = new ArrayList<Goods>();
		int length = list.size();
		if (length > 5) {
			int begin = CommUtil.null2Int(goods_random) * 5;
			int i = 0;
			while (i < 5) {
				long id = CommUtil.null2Long(list.get((begin + i) % length));
				Map params = new HashMap();
				params.put("id", CommUtil.null2Long(id));
				List<Goods> objs = this.goodsService
						.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
								params, 0, 1);
				if (objs.size() > 0) {
					goods_list.add(objs.get(0));
				}
				i++;
			}
		} else {
			for (Object id : list) {
				Map params = new HashMap();
				params.put("id", CommUtil.null2Long(id));
				List<Goods> objs = this.goodsService
						.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
								params, 0, 1);
				if (objs.size() > 0) {
					goods_list.add(objs.get(0));
				}
			}
		}
		mv.addObject("goods", goods_list);
		return mv;
	}

	/**
	 * 系统只允许单用户登录，第二次登陆后提出先前用户的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/outline.htm")
	public ModelAndView outline(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}

	/**
	 * ajax加载head部分用户中心 并从cookie中查询浏览记录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/head_ajax_usercenter.htm")
	public ModelAndView head_ajax_usercenter(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head_ajax_usercenter.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			mv.addObject("user", user);
			int[] status = new int[] { 10, 30 }; // 已提交 已发货 已完成
			for (int i = 0; i < status.length; i++) {
				int size = this.orderFormService.query(
						"select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id=" + user.getId().toString()
								+ " and obj.order_status =" + status[i] + "", null, -1, -1).size();
				mv.addObject("order_size_" + status[i], size);
			}
			Map map = new HashMap();
			map.put("status", 0);
			map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Message> msgs = this.messageService
					.query("select count(obj.id) from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
							map, -1, -1);
			mv.addObject("msg_size", msgs.get(0));
		}
		List<Goods> objs = new ArrayList<Goods>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("goodscookie".equals(cookie.getName())) {
					String[] goods_id = cookie.getValue().split(",");
					int j = 5;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						Goods goods = new Goods();
						goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id[i]));
						if (goods != null)
							objs.add(goods);
					}
				}
			}
		}
		mv.addObject("objs", objs);
		return mv;
	}
	
	/***
	 * 银行列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chinapay.htm")
	public ModelAndView chinapay(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("chinapay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}
	
	/***
	 * 银行列表cash
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chinapay_cash.htm")
	public ModelAndView chinapay_cash(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("chinapay_cash.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}


	/**
	 * 使用手机方式商城pc端首页时加载方法，前端自动显示wap首页
	 * 
	 * @param request
	 * @param response
	 * @param mv
	 */
	private void goto_wap_index(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) {

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
		List<Goods> goods_hots = this.goodsService
				.query("select obj from Goods obj where obj.weixin_hot=:weixin_hot and obj.goods_status =:goods_status order by weixin_hotTime desc",
						params, 0, 50);
		mv.addObject("goods_hots", goods_hots);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> top_recommends = this.goodsService
				.query("select obj from Goods obj where obj.weixin_recommend=:weixin_recommend and obj.goods_status =:goods_status order by obj.weixin_recommendTime desc",
						params, 0, 6);
		mv.addObject("top_recommends", top_recommends);
		params.clear();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.display=:display and obj.level=0 order by obj.sequence asc", params, 0, 4);
		mv.addObject("gcs", gcs);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.brandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.weixin_recommend=:weixin_recommend order by weixin_recommendTime desc",
						params, 0, 4);
		mv.addObject("gbs", gbs);
		params.clear();
		params.put("ifctype", "H5");
		List<Subject> subject_list = this.subjectQueryObject
				.query("select obj from Subject obj where obj.ifcType=:ifctype order by obj.sequence asc", params, 0, 3);
		mv.addObject("subject_list", subject_list);
		params.clear();
		params.put("ac_begin_time", date);
		params.put("ac_end_time", date);
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService.query(
				"select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time", params, 0, 1);
		if (activitys.size() > 0) {
			params.clear();
			params.put("ag_status", 1);
			params.put("goods_status", 0);
			params.put("weixin_recommend", 1);
			params.put("act_id", activitys.get(0).getId());
			List<ActivityGoods> activitygoods = this.activityGoodsService.query(
					"select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
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
		List<GroupGoods> groupgoods = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
						+ "and obj.weixin_recommend=:weixin_recommend and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by weixin_recommendTime desc",
						params, 0, 4);
		mv.addObject("groupgoods", groupgoods);
		params.clear();
		params.put("freeStatus", 5);
		params.put("weixin_recommend", 1);
		List<FreeGoods> freegoods = this.freeGoodsService.query(
				"select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.weixin_recommend="
						+ ":weixin_recommend order by obj.weixin_recommendTime desc", params, 0, 1);
		
		//查询广告位信息
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
	}
	
	@RequestMapping("/about.htm")
	public ModelAndView about(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("about.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	@RequestMapping("/contact.htm")
	public ModelAndView contact(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("contact.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	@RequestMapping("/friendly_link.htm")
	public ModelAndView friendly_link(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("friendly_link.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 两公里商圈地图
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mapPage.htm")
	public ModelAndView mapPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("mapPage.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 2016年福袋
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/gift.htm")
	public ModelAndView gift(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("gift.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
}
