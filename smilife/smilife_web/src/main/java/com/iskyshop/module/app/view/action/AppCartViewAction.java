package com.iskyshop.module.app.view.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.OrderEnoughReduceInfo;
import com.iskyshop.foundation.domain.virtual.OrderFreightInfo;
import com.iskyshop.foundation.domain.virtual.OrderEnoughReduceInfo.EnoughReduceGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.CartTools;
import com.iskyshop.manage.seller.tools.CombinTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.BuyGiftViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.SnapshotTools;

/**
 * 
 * <p>
 * Title: MobileCartViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端购物控制器,包括购物车所有操作及订单相关操作
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
 * @author hezeng
 * 
 * @date 2014-7-28
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppCartViewAction {

	private static Logger logger = Logger.getLogger(AppCartViewAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private PayTools payTools;
	@Autowired
	private OrderFormTools orderTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IIntegralGoodsOrderService iorderService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityTools;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private SnapshotTools snapshotTools;

	/**
	 * 合并购物车信息
	 * 
	 * @return
	 */
	private List<GoodsCart> cart_calc(String user_id, String token, String cart_mobile_ids) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>(); // 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null && !user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
			}
		}
		if (StringUtils.isNullOrEmpty(cart_mobile_ids)) {
			cart_mobile_ids = "0";
		}
		String mobile_ids[] = cart_mobile_ids.split(",");
		for (String mobile_id : mobile_ids) {
			if (!"".equals(mobile_id)) {
				mark_ids.add(mobile_id);
			}
		}
		if (user != null) {
			cart_map.clear();
			cart_map.put("mark_ids", mark_ids);
			cart_map.put("cart_status", 0);
			carts_mobile = this.goodsCartService.query(
					"select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
					cart_map, -1, -1);
			// 如果用户拥有自己的店铺，删除carts_mobile购物车中自己店铺中的商品信息
			if (user.getStore() != null) {
				for (GoodsCart gc : carts_mobile) {
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
					"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ", cart_map,
					-1, -1);
		} else {
			cart_map.clear();
			cart_map.put("mark_ids", mark_ids);
			cart_map.put("cart_status", 0);
			carts_mobile = this.goodsCartService.query(
					"select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
					cart_map, -1, -1);
		}
		// 将mobile购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart mobile : carts_mobile) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (mobile.getGoods().getId().equals(gc2.getGoods().getId())) {
						if (mobile.getSpec_info().equals(gc2.getSpec_info())) {
							if (!"combin".equals(mobile.getCart_type()) || mobile.getCombin_main() != 1) {
								add = false;
								this.goodsCartService.delete(mobile.getId());
							}
						}
					}
				}
				if (add) { // 将carts_mobile转变为user_cart
					mobile.setCart_mobile_id(null);
					mobile.setUser(user);
					this.goodsCartService.update(mobile);
					carts_list.add(mobile);
				}
			}
		} else {
			for (GoodsCart mobile : carts_mobile) {
				carts_list.add(mobile);
			}
		}
		// 组合套装处理，只显示套装主购物车,套装内其他购物车不显示
		List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts_list) {
			if (gc.getCart_type() != null && "combin".equals(gc.getCart_type())) {
				if (gc.getCombin_main() != 1) { // 组合购物车中非主购物车
					combin_carts_list.add(gc);
				}
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		return carts_list;
	}

	/**
	 * 手机端添加f码商品
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param f_code
	 * @param gsp
	 */
	@RequestMapping("/app/add_f_code_goods_cart.htm")
	public void add_f_code_goods_cart(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String f_code, String gsp, String user_id, String token, String cart_mobile_ids, String price) {
		boolean ret = false;
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List<Map> f_code_list = Json.fromJson(List.class, goods.getGoods_f_code());
		for (Map map : f_code_list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code) && CommUtil.null2Int(map.get("status")) == 0) { // 存在该F码且是未使用的则验证成功
				ret = true;
				break;
			}
		}
		if (ret) {
			List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
			List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>(); // 未提交的用户cookie购物车
			List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
			Map cart_map = new HashMap();
			User user = null;
			if (!StringUtils.isNullOrEmpty(user_id)) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
				if (!user.getApp_login_token().equals(token.toLowerCase())) {
					user = null;
				}
			}
			cart_map.clear();
			cart_map.put("user_id", user.getId());
			cart_map.put("cart_status", 0);
			carts_user = this.goodsCartService.query(
					"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ", cart_map,
					-1, -1);
			// 将cookie购物车与用户user购物车合并，去重
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
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
				if (add) { // 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
			GoodsCart obj = new GoodsCart();
			boolean add = true;
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0 && gc.getGsps() != null) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id) && Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						add = false;
					}
				}
			}
			if (add) { // 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(1);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				this.goodsCartService.setGoodsCartSpec(goods, obj, gsp_ids);
				obj.setUser(user);
				ret = this.goodsCartService.save(obj);
				if (ret) { // 确定F码商品已经添加到购物车，作废F码
					for (Map map : f_code_list) {
						if (CommUtil.null2String(map.get("code")).equals(f_code)
								&& CommUtil.null2Int(map.get("status")) == 0) { // 存在该F码且是未使用的则验证成功
							map.put("status", 1); // 设置该F码已经被使用
							break;
						}
					}
					goods.setGoods_f_code(Json.toJson(f_code_list, JsonFormat.compact()));
					this.goodsService.update(goods);
				}
			}
		}
		Map json_map = new HashMap();
		json_map.put("ret", ret);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 手机端添加购物车
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param cart_mobile_ids
	 *            ：手机端用户购物车标志，用户未登录手机端时记录购物车与该手机用户的标识
	 * @param id
	 * @param count
	 * @param price
	 * @param gsp
	 * @param buy_type
	 */
	@RequestMapping("/app/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String cart_mobile_ids, String goods_id, String count, String price, String gsp, String buy_type,
			String combin_ids, String combin_version) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>(); // 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		Map json_map = new HashMap();
		int next = 0;
		String cart_mobile_id = null;
		int code = 100; // 100成功，-100用户信息错误，
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (!user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
				code = -100;
			}
		}
		if (code == 100) {
			if ("".equals(cart_mobile_ids)) {
				mark_ids.add("0");
			} else {
				String mobile_ids[] = cart_mobile_ids.split(",");
				for (String mobile_id : mobile_ids) {
					if (!"".equals(mobile_id)) {
						mark_ids.add(mobile_id);
					}
				}
			}

			if (user != null) {
				cart_map.clear();
				cart_map.put("mark_ids", mark_ids);
				cart_map.put("cart_status", 0);
				carts_mobile = this.goodsCartService.query(
						"select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
						cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_mobile购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_mobile) {
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
				cart_map.put("mark_ids", mark_ids);
				cart_map.put("cart_status", 0);
				carts_mobile = this.goodsCartService.query(
						"select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
						cart_map, -1, -1);
			}
			// 将mobile购物车与用户user购物车合并，去重
			if (user != null) {
				for (GoodsCart ugc : carts_user) {
					carts_list.add(ugc);
				}
				for (GoodsCart mobile : carts_mobile) {
					boolean add = true;
					for (GoodsCart gc2 : carts_user) {
						if (mobile.getGoods().getId().equals(gc2.getGoods().getId())) {
							if (mobile.getSpec_info().equals(gc2.getSpec_info())) {
								add = false;
								this.goodsCartService.delete(mobile.getId());
							}
						}
					}
					if (add) { // 将carts_mobile转变为user_cart
						mobile.setCart_mobile_id(null);
						mobile.setUser(user);
						this.goodsCartService.update(mobile);
						carts_list.add(mobile);
					}
				}
			} else {
				for (GoodsCart mobile : carts_mobile) {
					carts_list.add(mobile);
				}
			}
			// 新添加购物车,排除没有重复商品后添加到carts_list中
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			String temp_gsp = gsp;
			if ("parts".equals(buy_type)) {
				if (!StringUtils.isNullOrEmpty(combin_ids)) {
					next = 1;
				}
			}
			if ("suit".equals(buy_type)) {
				if (!StringUtils.isNullOrEmpty(combin_ids)) {
					next = 2;
				}
			}
			boolean add = true;
			boolean combin_add = true;
			if ("suit".equals(buy_type)) {
				combin_add = false;
			}
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0 && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id) && Arrays.equals(gsp_ids, gsp_ids1)) {
						if ("combin".equals(gc.getCart_type())) {
							if (!combin_add) {
								add = false;
								break;
							} else {
								add = true;
							}
						} else {
							add = false;
							break;
						}
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						if ("combin".equals(gc.getCart_type())) {
							if (!combin_add) {
								add = false;
								break;
							} else {
								add = true;
							}
						} else {
							add = false;
							break;
						}
					}
				}
			}
			cart_mobile_id = CommUtil.randomString(12) + "_mobile_" + CommUtil.formatLongDate(new Date());
			if (add && combin_add) { // 排除购物车中没有重复商品后添加该商品到购物车,并且非组合添加
				GoodsCart obj = new GoodsCart();
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				price = this.load_goods_gsp(temp_gsp, goods_id, user_id) + "";
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				this.goodsCartService.setGoodsCartSpec(goods, obj, gsp_ids);
				if (user == null) {
					obj.setCart_mobile_id(cart_mobile_id); // 设置手机端未登录用户会话Id
				} else {
					obj.setUser(user);
				}
				this.goodsCartService.save(obj);
				double cart_total_price = 0;
				for (GoodsCart gc : carts_list) {
					if ("".equals(CommUtil.null2String(gc.getCart_type()))) {
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc.getGoods().getGoods_current_price()) * gc.getCount();
					}
					if ("combin".equals(CommUtil.null2String(gc.getCart_type()))) { // 如果是组合销售购买，则设置组合价格
						cart_total_price = cart_total_price + this.getGoodsCombinPrice() * gc.getCount();
					}
				}
			}

			if (next == 1) { // 组合配件商品添加
				String part_ids[] = combin_ids.split(",");
				for (String part_id : part_ids) {
					if (!"".equals(part_id)) {
						Goods part_goods = this.goodsService.getObjById(CommUtil.null2Long(part_id));
						GoodsCart part_cart = new GoodsCart();
						boolean part_add = true;
						part_cart.setAddTime(new Date());
						String temp_gsp_parts = null;
						temp_gsp_parts = this.generic_default_gsp(part_goods);
						String[] part_gsp_ids = CommUtil.null2String(temp_gsp_parts).split(",");
						Arrays.sort(part_gsp_ids);
						for (GoodsCart gc : carts_list) {
							if (part_gsp_ids != null && part_gsp_ids.length > 0 && gc.getGsps() != null
									&& gc.getGsps().size() > 0) {
								String[] gsp_ids1 = new String[gc.getGsps().size()];
								for (int i = 0; i < gc.getGsps().size(); i++) {
									gsp_ids1[i] = gc.getGsps().get(i) != null ? gc.getGsps().get(i).getId().toString() : "";
								}
								Arrays.sort(gsp_ids1);
								if (gc.getGoods().getId().toString().equals(part_id)
										&& Arrays.equals(part_gsp_ids, gsp_ids1)) {
									part_add = false;
								}
							} else {
								if (gc.getGoods().getId().toString().equals(part_id)) {
									part_add = false;
								}
							}
						}
						if (part_add) { // 排除购物车中没有重复商品后添加该商品到购物车
							part_cart.setAddTime(new Date());
							part_cart.setCount(CommUtil.null2Int(1));
							String part_price = this.generGspgoodsPrice(temp_gsp_parts, part_id, user_id);
							part_cart.setPrice(BigDecimal.valueOf(CommUtil.null2Double(part_price)));
							part_cart.setGoods(part_goods);
							this.goodsCartService.setGoodsCartSpec(part_goods, part_cart, part_gsp_ids);
							if (user == null) {
								part_cart.setCart_mobile_id(cart_mobile_id); // 设置手机端未登录用户会话Id
							} else {
								part_cart.setUser(user);
							}
							this.goodsCartService.save(part_cart);
						}
					}
				}
			}
			if (next == 2) { // 组合套装商品添加
				boolean suit_add = true;
				Map params = new HashMap();
				params.put("combin_main", 1);
				params.put("cart_type", "combin");
				params.put("gid", goods.getId());
				String hql = "select obj from GoodsCart obj where obj.cart_type=:cart_type and obj.combin_main=:combin_main and obj.goods.id=:gid";
				if (user != null) {
					params.put("user_id", user.getId());
					hql += " and obj.user.id=:user_id";
				} else {
					params.put("cart_mobile_id", token);
					hql += " and obj.cart_mobile_id=:cart_mobile_id";
				}
				params.put("gid", goods.getId());
				List<GoodsCart> suit_carts = this.goodsCartService.query(hql, params, -1, -1);
				if (suit_carts.size() > 0) {
					if (suit_carts.get(0).getCombin_version().contains(CommUtil.null2String(combin_version))) {
						suit_add = false;
					}
				}
				if (suit_add) {
					Map suit_map = null;
					params.clear();
					params.put("main_goods_id", CommUtil.null2Long(goods_id));
					params.put("combin_type", 0); // 组合套装
					params.put("combin_status", 1);
					List<CombinPlan> suits = this.combinplanService.query(
							"select obj from CombinPlan obj where obj.main_goods_id=:main_goods_id and obj.combin_type=:combin_type and obj.combin_status=:combin_status",
							params, -1, -1);
					for (CombinPlan plan : suits) {
						List<Map> map_list = (List<Map>) Json.fromJson(plan.getCombin_plan_info());
						for (Map temp_map : map_list) {
							String ids = this.goodsViewTools.getCombinPlanGoodsIds(temp_map);
							if (ids.equals(combin_ids)) {
								suit_map = temp_map;
								break;
							}
						}
					}
					String combin_mark = "combin" + UUID.randomUUID();
					if (suit_map != null) {
						String suit_ids = ""; // 主套装购物车中其他套装购物车id，（不包含自己的id）
						List<Map> goods_list = (List<Map>) suit_map.get("goods_list");
						for (Map good_map : goods_list) {
							Goods suit_goods = this.goodsService.getObjById(CommUtil.null2Long(good_map.get("id")));
							GoodsCart cart = new GoodsCart();
							cart.setAddTime(new Date());
							cart.setGoods(suit_goods);
							String temp_gsp_ids[] = CommUtil.null2String(this.generic_default_gsp(goods)).split(",");
							this.goodsCartService.setGoodsCartSpec(suit_goods, cart, temp_gsp_ids);
							cart.setCombin_mark(combin_mark);
							cart.setCart_type("combin");
							cart.setPrice(BigDecimal.valueOf(CommUtil.null2Double(suit_goods.getGoods_current_price())));
							cart.setCount(1);
							if (user == null) {
								cart.setCart_mobile_id(cart_mobile_id); // 设置手机端未登录用户会话Id
							} else {
								cart.setUser(user);
							}
							this.goodsCartService.save(cart);
							suit_ids = suit_ids + "," + CommUtil.null2String(cart.getId());
						}
						GoodsCart obj = new GoodsCart(); // 套装主购物车
						String combin_main_default_gsp = this.generic_default_gsp(goods);
						obj.setCart_gsp(combin_main_default_gsp);
						obj.setAddTime(new Date());
						obj.setCount(CommUtil.null2Int(count));
						price = this.load_goods_gsp(temp_gsp, goods_id, user_id) + "";
						obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
						obj.setGoods(goods);
						if (user == null) {
							obj.setCart_mobile_id(cart_mobile_id);
						} else {
							obj.setUser(user);
						}
						obj.setCombin_suit_ids(suit_ids); // 更新套装主购物车id信息(包括自己的id)
						obj.setCombin_main(1);
						obj.setCombin_version("【套装" + combin_version + "】");
						obj.setCount(CommUtil.null2Int(count));
						obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(suit_map.get("plan_goods_price"))));
						obj.setCombin_mark(combin_mark);
						obj.setCart_type("combin");
						String temp_gsp_ids[] = CommUtil.null2String(this.generic_default_gsp(goods)).split(",");
						this.goodsCartService.setGoodsCartSpec(goods, obj, temp_gsp_ids);
						obj.setCart_gsp(this.generic_default_gsp(goods));
						suit_map.put("suit_count", CommUtil.null2Int(count));
						String suit_all_price = CommUtil.formatMoney(CommUtil.mul(CommUtil.null2Int(count),
								CommUtil.null2Double(suit_map.get("plan_goods_price"))));
						suit_map.put("suit_all_price", suit_all_price); // 套装整体价格=套装单价*数量
						obj.setCombin_suit_info(Json.toJson(suit_map, JsonFormat.compact()));
						this.goodsCartService.save(obj);
					}
				} else {
					GoodsCart update_cart = suit_carts.get(0);
					Map temp_map = Json.fromJson(Map.class, update_cart.getCombin_suit_info());
					temp_map.put("suit_count", update_cart.getCount() + 1);
					update_cart.setCombin_suit_info(Json.toJson(temp_map, JsonFormat.compact()));
					update_cart.setCount(update_cart.getCount() + 1);
					this.goodsCartService.update(update_cart);
				}
			}
			json_map.put("code", code);
			if (cart_mobile_id != null) {
				json_map.put("cart_mobile_id", cart_mobile_id);
			}
			String json = Json.toJson(json_map, JsonFormat.compact());
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
	}

	/**
	 * 手机端删除购物车
	 * 
	 * @param request
	 * @param response
	 * @param cart_id
	 * @param user_id
	 * @param token
	 */
	@RequestMapping("/app/remove_goods_cart.htm")
	public void remove_goods_cart(HttpServletRequest request, HttpServletResponse response, String cart_ids, String user_id,
			String token) {
		Double total_price = 0.00;
		String temp_mobile_id = "";
		String code = "100"; // 100表示删除成功，200表示删除失败
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		if (!StringUtils.isNullOrEmpty(cart_ids)) {
			String[] ids = cart_ids.split(",");
			for (String id : ids) {
				if (!"".equals(id)) {
					GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
					if ("combin".equals(gc.getCart_type())) {
						if (gc.getCombin_main() == 1) { // 购物车为组合主购物车
							Map params = new HashMap();
							params.put("combin_mark", gc.getCombin_mark());
							List<GoodsCart> suit_carts = this.goodsCartService.query(
									"select obj from GoodsCart obj where obj.combin_mark=:combin_mark", params, -1, -1);
							for (GoodsCart suit_gc : suit_carts) {
								suit_gc.getGsps().clear();
								this.goodsCartService.delete(suit_gc.getId());
							}
						}
					} else {
						gc.getGsps().clear();
						this.goodsCartService.delete(CommUtil.null2Long(id));
					}
					temp_mobile_id = gc.getCart_mobile_id() + "," + temp_mobile_id;
				}
			}
		}
		total_price = this.getcartsPrice(carts);
		Map map = new HashMap();
		map.put("total_price", BigDecimal.valueOf(total_price));
		map.put("code", code);
		map.put("count", carts.size());
		map.put("dele_cart_mobile_ids", temp_mobile_id);
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
	 * 手机端购物车计算选中购物车的总价钱
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/compute_cart.htm")
	public void compute_cart(HttpServletRequest request, HttpServletResponse response, String cart_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> gcs = new ArrayList<GoodsCart>();
		if (!StringUtils.isNullOrEmpty(cart_ids)) {
			String ids[] = cart_ids.split(",");
			for (String id : ids) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
				if (gc != null) {
					gcs.add(gc);
				}
			}
		}
		double select_cart_price = this.getcartsPrice(gcs);
		Map map = this.calCartPriceInfo(gcs);
		json_map.put("select_cart_price", select_cart_price);
		json_map.put("select_cart_number", gcs.size());
		json_map.put("select_cart_all_price", map.get("all"));
		json_map.put("select_cart_reduce", map.get("reduce"));

		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 购物车选择赠品确认
	 * 
	 * @param request
	 * @param response
	 * @param cart_ids
	 * @param gift_id
	 */
	@RequestMapping("/app/add_cart_gift.htm")
	public void add_cart_gift(HttpServletRequest request, HttpServletResponse response, String cart_ids, String gift_id) {
		if (!StringUtils.isNullOrEmpty(gift_id)) {
			String[] ids = cart_ids.split(",");
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gift_id));
			Map map = new HashMap();
			map.put("goods_id", goods.getId().toString());
			map.put("goods_name", goods.getGoods_name());
			String goods_main_photo = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName();
			if (goods.getGoods_main_photo() != null) { // 商品主图片
				goods_main_photo = goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName()
						+ "_small." + goods.getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			String json = Json.toJson(map, JsonFormat.compact());
			for (String id : ids) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
				gc.setWhether_choose_gift(1);
				gc.setGift_info(json);
				this.goodsCartService.update(gc);
			}
		}
		Map map = new HashMap();
		map.put("ret", 100);
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
	 * 购物车商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/app/cart_count_adjust.htm")
	public void cart_count_adjust(HttpServletRequest request, HttpServletResponse response, String cart_id, String count,
			String user_id, String token, String cart_mobile_ids) {
		List<GoodsCart> carts = this.cart_calc(user_id, token, cart_mobile_ids);
		String code = "100"; // 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00; // 单位GoodsCart总价钱
		double total_price = 0.00; // 购物车总价钱
		String cart_type = ""; // 判断是否为组合销售
		int max_inventory = 0; // 超出商品库存时的最大库存
		Goods goods = null;
		Map map = new HashMap();
		if (!StringUtils.isNullOrEmpty(cart_id)) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(cart_id));

			if (gc.getId().toString().equals(cart_id)) {
				cart_type = CommUtil.null2String(gc.getCart_type());
				goods = gc.getGoods();
				if (goods.getF_sale_type() == 1) {
					code = "-100"; // f码商品只能1件
				} else {
					if ("".equals(cart_type)) { // 普通商品的处理
						if (goods.getGroup_buy() == 2) {
							GroupGoods gg = new GroupGoods();
							for (GroupGoods gg1 : goods.getGroup_goods_list()) {
								if (gg1.getGg_goods().getId().equals(goods.getId())) {
									gg = gg1;
								}
							}
							if (gg.getGg_count() >= CommUtil.null2Int(count)) {
								gc.setCount(CommUtil.null2Int(count));
								gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(gg.getGg_price())));
								this.goodsCartService.update(gc);
								gc_price = CommUtil.mul(gg.getGg_price(), count);
							} else {
								code = "300";
								max_inventory = gg.getGg_count();
							}
						} else {
							if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
								if (gc.getId().toString().equals(cart_id)) {
									gc.setCount(CommUtil.null2Int(count));
									this.goodsCartService.update(gc);
									gc_price = CommUtil.mul(gc.getPrice(), count);
								}
							} else {
								max_inventory = goods.getGoods_inventory();
								code = "200";
							}
						}
					} else if ("combin".equals(cart_type)) { // 组合销售的处理
						if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(gc);
							String suit_all_price = "0.00";
							GoodsCart suit = gc;
							Map suit_map = (Map) Json.fromJson(suit.getCombin_suit_info());
							suit_map.put("suit_count", CommUtil.null2Int(count));
							suit_all_price = CommUtil.formatMoney(CommUtil.mul(CommUtil.null2Int(count),
									CommUtil.null2Double(suit_map.get("plan_goods_price"))));
							suit_map.put("suit_all_price", suit_all_price); // 套装整体价格=套装单价*数量
							String new_json = Json.toJson(suit_map, JsonFormat.compact());
							suit.setCombin_suit_info(new_json);
							suit.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(suit);
							gc_price = CommUtil.null2Double(suit_all_price);
						} else {
							code = "200";
						}
					}
				}
			}
		}
		if (max_inventory != 0) {
			map.put("max_inventory", max_inventory);
		}
		total_price = this.getcartsPrice(carts);
		map.put("gc_price", gc_price);
		map.put("total_price", total_price);
		map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		logger.debug(Json.toJson(map, JsonFormat.compact()));
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 手机端轻松购，直接提交商品到订单页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_cart0.htm")
	public void goods_cart0(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String goods_id, String count, String price, String gsp) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>(); // 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		Map json_map = new HashMap();
		String cart_id = null;
		int code = 100; // 100成功，-100用户信息错误，
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null && !user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
				code = -100;
			}
		}
		if (code == 100) {
			// 新添加购物车,排除没有重复商品后添加到carts_list中
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			boolean add = true;
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0 && gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id) && Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						add = false;
					}
				}
			}
			if (add) { // 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				this.goodsCartService.setGoodsCartSpec(goods, obj, gsp_ids);
				obj.setUser(user);
				this.goodsCartService.save(obj);
				cart_id = CommUtil.null2String(obj.getId());
				double cart_total_price = 0;
				for (GoodsCart gc : carts_list) {
					if ("".equals(CommUtil.null2String(gc.getCart_type()))) {
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc.getGoods().getGoods_current_price()) * gc.getCount();
					}
					if ("combin".equals(CommUtil.null2String(gc.getCart_type()))) { // 如果是组合销售购买，则设置组合价格
						cart_total_price = cart_total_price + this.getGoodsCombinPrice() * gc.getCount();
					}
				}
			}
		}
		if (code == 100 && cart_id != null) { // 添加购物车完成，
			int selfPickupEnabled = 1; // 购物车中是否全为自营商品
			List<GoodsCart> carts = this.getGoodscartByids(cart_id);
			List<Object> store_list = new ArrayList<Object>();
			for (GoodsCart gc : carts) {
				if (gc.getGoods().getGoods_type() == 1) {
					store_list.add(gc.getGoods().getGoods_store().getId());
					selfPickupEnabled = 0;
				} else {
					store_list.add("self");
				}
			}
			HashSet hs = new HashSet(store_list);
			store_list.removeAll(store_list);
			store_list.addAll(hs);
			String store_ids = "";
			for (Object sl : store_list) {
				if (store_ids.indexOf(CommUtil.null2String(sl)) <= 0) {
					store_ids = CommUtil.null2String(sl) + "," + store_ids;
				}
			}
			double order_goods_price = this.getcartsPrice(carts);
			json_map.put("order_goods_price", order_goods_price); // 订单中商品总体价格
			json_map.put("store_ids",
					StringUtils.isNullOrEmpty(store_ids) ? store_ids : store_ids.substring(0, store_ids.length() - 1));
			json_map.put("selfPickupEnabled", selfPickupEnabled); // 订单中商品总体价格
		}
		json_map.put("cart_ids", cart_id);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 手机端购物车数量
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_cart_count.htm")
	public void goods_cart_count(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String cart_mobile_ids) {
		Map json_map = new HashMap();
		List cart_list = new ArrayList();
		List<GoodsCart> carts = this.cart_calc(user_id, token, cart_mobile_ids);
		Map map = new HashMap();
		map.put("count", carts.size());
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
	 * 手机端购物车列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_cart1.htm")
	public void goods_cart1(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String cart_mobile_ids, String selected_ids) {
		Map json_map = new HashMap();
		List cart_list = new ArrayList();
		List<GoodsCart> carts = this.cart_calc(user_id, token, cart_mobile_ids);
		List<String> selected_list = new ArrayList<String>();
		if (!StringUtils.isNullOrEmpty(selected_ids)) {
			selected_list = Arrays.asList(selected_ids.split(","));
		}
		List discount_list = new ArrayList();
		Date date = new Date();

		Map<String, List<GoodsCart>> selected_cart = new HashMap<String, List<GoodsCart>>();
		Map<String, List<GoodsCart>> separate_carts = this.separateCombin(carts); // 传入没有分离组合活动商品的购物车
		carts = (List<GoodsCart>) separate_carts.get("normal"); // 无活动的商品购物车
		List<GoodsCart> combine = (List<GoodsCart>) separate_carts.get("combin"); // 组合套装商品购物车
		Map<String, BigDecimal> enough_give_price = this.enough_give_price(selected_ids);
		for (GoodsCart gc : combine) { // 循环组合主商品
			Map map = new HashMap();
			map.put("goods_name", gc.getGoods().getGoods_name());
			String status = "goods";
			map.put("goods_status", status); // 商品状态
			map.put("cart_id", gc.getId()); // 商品对应购物车id
			map.put("goods_id", gc.getGoods().getId()); // 商品id
			map.put("goods_price", gc.getPrice()); // 商品价格
			map.put("goods_count", gc.getCount()); // 商品数量
			map.put("goods_spec", StringUtils.isNullOrEmpty(gc.getSpec_info()) ? gc.getSpec_info()
					: gc.getSpec_info().replaceAll("<br>", "  ")); // 商品规格
			String goods_main_photo = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName();
			if (gc.getGoods().getGoods_main_photo() != null) { // 商品主图片
				goods_main_photo = gc.getGoods().getGoods_main_photo().getPath() + "/"
						+ gc.getGoods().getGoods_main_photo().getName() + "_small."
						+ gc.getGoods().getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			double cart_price = this.getcartsPrice(combine);
			map.put("cart_price", cart_price);
			// 处理组合套装其他商品信息
			Map suit_info = Json.fromJson(Map.class, gc.getCombin_suit_info());
			List<Map> temp_goods = (List<Map>) suit_info.get("goods_list");
			suit_info.put("goods_list", temp_goods);
			map.put("suit_info", suit_info);
			Map discount_map = new HashMap();
			if (selected_list.contains(gc.getId().toString())) {
				discount_map.put("cart_price", cart_price); // 购物车总商品价格
				discount_map.put("reduce", 0); // 满减
			} else {
				discount_map.put("cart_price", 0);
				discount_map.put("reduce", 0); // 满减
			}
			List list = new ArrayList();
			list.add(map);
			logger.debug("list:" + list);
			discount_map.put("goods_list", list);

			Map suit_map = goodsViewTools.getSuitInfo(gc.getId().toString());

			double cheap = CommUtil.null2Double(suit_map.get("all_goods_price"))
					- CommUtil.null2Double(suit_map.get("plan_goods_price"));
			discount_map.put("info",
					gc.getCombin_version() + "组合套装，立省￥" + CommUtil.formatMoney(CommUtil.null2Double(cheap)));

			String key = "combine_" + gc.getCombin_mark();
			json_map.put(key, discount_map);
			discount_list.add(key);

		}

		for (GoodsCart gc : carts) {
			Map map = new HashMap();
			map.put("goods_name", gc.getGoods().getGoods_name());
			String status = "goods";
			if (gc.getGoods().getGroup_buy() == 2) {
				status = "group";
			}
			if (gc.getGoods().getActivity_status() == 2) {
				status = "activity";
			}
			map.put("goods_status", status); // 商品状态
			map.put("cart_id", gc.getId()); // 商品对应购物车id
			map.put("goods_id", gc.getGoods().getId()); // 商品id
			map.put("goods_price", gc.getPrice()); // 商品价格
			map.put("goods_count", gc.getCount()); // 商品数量
			map.put("goods_spec", StringUtils.isNullOrEmpty(gc.getSpec_info()) ? gc.getSpec_info()
					: gc.getSpec_info().replaceAll("<br>", "  ")); // 商品规格
			String goods_main_photo = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName();
			if (gc.getGoods().getGoods_main_photo() != null) { // 商品主图片
				goods_main_photo = gc.getGoods().getGoods_main_photo().getPath() + "/"
						+ gc.getGoods().getGoods_main_photo().getName() + "_small."
						+ gc.getGoods().getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			double cart_price = this.getcartsPrice(carts);
			map.put("cart_price", cart_price); // 购物车总商品价格

			if (gc.getGoods().getOrder_enough_give_status() == 1 && gc.getGoods().getBuyGift_id() != null) {
				BuyGift bg = this.buyGiftService.getObjById(gc.getGoods().getBuyGift_id());
				if (bg.getBeginTime().before(date)) {
					Map discount_map;
					String key = "gift_" + bg.getId();
					if (discount_list.contains(key)) {
						discount_map = (Map) json_map.get(key);
						List list = (List) discount_map.get("goods_list");
						list.add(map);

					} else {
						discount_map = new HashMap();
						List list = new ArrayList();
						list.add(map);
						discount_map.put("goods_list", list);
						discount_map.put("info", "活动商品满" + CommUtil.formatMoney(bg.getCondition_amount()) + "元即有赠品相送。");
						discount_map.put("whether_enough", 0);
						discount_map.put("gift_list", Json.fromJson(bg.getGift_info()));
						discount_map.put("cart_price", 0); // 小计
						discount_map.put("reduce", 0); // 满送
						json_map.put(key, discount_map);
						discount_list.add(key);

					}
					if (selected_list.contains(gc.getId().toString())) {
						List<GoodsCart> selected_cart_list;
						if (selected_cart.containsKey(key)) {
							selected_cart_list = (List<GoodsCart>) selected_cart.get(key);
							selected_cart_list.add(gc);
							selected_cart.put(key, selected_cart_list);
						} else {
							selected_cart_list = new ArrayList();
							selected_cart.put(key, selected_cart_list);
							selected_cart_list.add(gc);
						}
						BigDecimal bd = enough_give_price.get(bg.getId().toString());
						int ret = 0; // 判断购物车是否已选择赠品
						for (GoodsCart arr : selected_cart_list) {
							ret = ret + arr.getWhether_choose_gift();
						}
						if (bd.compareTo(bg.getCondition_amount()) >= 0) {
							discount_map.put("info", "活动商品已满" + CommUtil.formatMoney(bg.getCondition_amount()) + "元，请选择赠品");
							discount_map.put("whether_enough", 1);
							discount_map.put("gift_list", goodsViewTools.getGiftList(bg.getGift_info()));
							if (ret > 0) {
								discount_map.put("whether_enough", 2);
								if (selected_cart_list.size() > 0) {
									GoodsCart gift_gc = selected_cart_list.get(0);
									String gift_info = gift_gc.getGift_info();
									discount_map.put("gift", Json.fromJson(gift_info));
									discount_map.put("info", "已选择赠品");
								}
							}
						} else {
							// 未满足条件购物车清空已选择赠品
							for (GoodsCart arr : selected_cart_list) {
								arr.setGift_info(null);
								arr.setWhether_choose_gift(0);
								this.goodsCartService.update(arr);
							}
						}
					} else {
						if (gc.getGoods().getBuyGift_id().equals(bg.getId())) {
							gc.setGift_info(null);
							gc.setWhether_choose_gift(0);
							this.goodsCartService.update(gc);
						}
					}
				} else {
					cart_list.add(map);
				}
			} else if (gc.getGoods().getEnough_reduce() == 1) { // 满就减
				String er_id = gc.getGoods().getOrder_enough_reduce_id();
				EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
				if (er.getErbegin_time().before(date)) {
					String key = "reduce_" + er.getId();
					if (discount_list.contains(key)) {
						Map discount_map = (Map) json_map.get(key);
						List list = (List) discount_map.get("goods_list");
						list.add(map);
					} else {
						Map discount_map = new HashMap();
						List list = new ArrayList();
						list.add(map);
						discount_map.put("goods_list", list);
						Map er_map = (Map) Json.fromJson(er.getEr_json());
						double k = 0;
						String str = "";
						for (Object key1 : er_map.keySet()) {
							if (k == 0) {
								k = Double.parseDouble(key1.toString());
								str = "活动商品购满" + CommUtil.formatMoney(k) + "元，即可享受满减";
							}
							if (Double.parseDouble(key1.toString()) < k) {
								k = Double.parseDouble(key1.toString());
								str = "活动商品购满" + CommUtil.formatMoney(k) + "元，即可享受满减";
							}
						}
						discount_map.put("info", str);
						discount_map.put("cart_price", 0); // 小计
						discount_map.put("reduce", 0); // 满减
						json_map.put(key, discount_map);
						discount_list.add(key);
					}
					if (selected_list.contains(gc.getId().toString())) {
						if (selected_cart.containsKey(key)) {
							List selected_cart_list = (List) selected_cart.get(key);
							selected_cart_list.add(gc);
							selected_cart.put(key, selected_cart_list);
						} else {
							List selected_cart_list = new ArrayList();
							selected_cart_list.add(gc);
							selected_cart.put(key, selected_cart_list);
						}
					}
				} else {
					cart_list.add(map);
				}
			} else {
				cart_list.add(map);
			}
		}
		for (String str : selected_cart.keySet()) {
			Map map = this.calCartPriceInfo(selected_cart.get(str));

			Map mmap = (Map) json_map.get(str);
			mmap.put("cart_price", map.get("all"));
			mmap.put("reduce", map.get("reduce"));

			if (map.get("info") != null && !"".equals(map.get("info"))) {
				mmap.put("info", map.get("info"));
			}

		}

		json_map.put("discount_list", discount_list);
		json_map.put("cart_list", cart_list);
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	// 分离组合销售活动购物车,只显示主体套装商品购物车
	private Map<String, List<GoodsCart>> separateCombin(List<GoodsCart> carts) {
		Map<String, List<GoodsCart>> map = new HashMap<String, List<GoodsCart>>();
		List<GoodsCart> normal_carts = new ArrayList<GoodsCart>();
		List<GoodsCart> combin_carts = new ArrayList<GoodsCart>();
		for (GoodsCart cart : carts) {
			if (cart.getCart_type() != null && "combin".equals(cart.getCart_type())) {
				if (cart.getCombin_main() == 1) {
					combin_carts.add(cart);
				}
			} else {
				normal_carts.add(cart);
			}
		}
		map.put("combin", combin_carts);
		map.put("normal", normal_carts);
		return map;
	}

	@RequestMapping("/app/seckill_goods_cart2.htm")
	public void seckill_goods_cart2(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String cart_ids, String orderType) {
		// 未登录状态下，购买秒杀商品遭单点登录拦截，参数丢失，需求cookie中获得参数
		goods_cart2(request, response, user_id, token, cart_ids, orderType);
	}

	/**
	 * 手机端提交购物车进入订单页面
	 * 
	 * @param request
	 * @param response
	 * @param cart_ids
	 *            :提交的购物车id
	 * @return
	 */
	@RequestMapping("/app/goods_cart2.htm")
	public void goods_cart2(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String cart_ids, String orderType) {
		Map json_map = new HashMap();
		if (!StringUtils.isNullOrEmpty(user_id) && !StringUtils.isNullOrEmpty(token)) {
			User user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					boolean isSeckill = false; // 是否秒杀进入
					if (null != orderType && orderType.matches("s\\d+")) {
						isSeckill = true;
					}
					List<GoodsCart> carts = null;
					if (isSeckill) {
						Goods goods = goodsService.getObjById(Long.parseLong(orderType.substring(1))); // 秒杀
						Map cart_map = new HashMap();
						cart_map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
						cart_map.put("cart_status", 0);
						cart_map.put("goods_id", goods.getId());
						carts = goodsCartService.query(
								"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.goods.id=:goods_id",
								cart_map, -1, -1);

						// 未登录状态下，购买秒杀商品，购物车中userid为空，查出结果集为空，需将cookie购物车转换为user购物车后重新查询
						carts.get(0).setCount(1);
						this.goodsCartService.update(carts.get(0));
						cart_ids = "" + carts.get(0).getId();
					} else {
						carts = this.getGoodscartByids(cart_ids);
					}

					int selfPickupEnabled = 1; // 购物车中是否全为自营商品

					List<Object> store_list = new ArrayList<Object>();
					for (GoodsCart gc : carts) {
						if (gc.getGoods().getGoods_type() == 1) {
							store_list.add(gc.getGoods().getGoods_store().getId());
							selfPickupEnabled = 0;
						} else {
							store_list.add("self");
						}
					}
					HashSet hs = new HashSet(store_list);
					store_list.removeAll(store_list);
					store_list.addAll(hs);
					String store_ids = "";
					for (Object sl : store_list) {
						if (store_ids.indexOf(CommUtil.null2String(sl)) <= 0) {
							store_ids = CommUtil.null2String(sl) + "," + store_ids;
						}
					}
					double order_goods_price = this.getcartsPrice(carts);
					Map info = this.calCartPriceInfo(carts);
					double reduce = CommUtil.null2Double(info.get("reduce"));
					if (reduce > 0) {
						json_map.put("before", CommUtil.null2Double(info.get("all")));
						json_map.put("reduce", reduce);
					}
					json_map.put("order_goods_price", order_goods_price); // 订单中商品总体价格
					json_map.put("store_ids", StringUtils.isNullOrEmpty(store_ids) ? store_ids
							: store_ids.substring(0, store_ids.length() - 1));
					json_map.put("selfPickupEnabled", selfPickupEnabled);
				}
			}
		}
		json_map.put("cart_ids", cart_ids);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 手机端提交订单页商品清单
	 * 
	 * @param request
	 * @param response
	 * @param cart_ids
	 *            :提交的购物车id
	 * @return
	 */
	@RequestMapping("/app/goods_cart2_goodsInfo.htm")
	public void goods_cart2_goodsInfo(HttpServletRequest request, HttpServletResponse response, String cart_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
		List goods_list = new ArrayList();
		String url = CommUtil.getURL(request);
		List<String> gift_ids = new ArrayList<String>();
		for (GoodsCart gc : carts) {
			Map map = new HashMap();
			map.put("goods_id", gc.getGoods().getId()); // 商品id
			map.put("goods_name", gc.getGoods().getGoods_name()); // 商品名称
			map.put("goods_price", gc.getPrice()); // 商品价格
			map.put("goods_count", gc.getCount()); // 商品数量
			map.put("goods_spec", StringUtils.isNullOrEmpty(gc.getSpec_info()) ? gc.getSpec_info()
					: gc.getSpec_info().replaceAll("<br>", "  ")); // 商品规格
			String goods_main_photo = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName();
			if (gc.getGoods().getGoods_main_photo() != null) { // 商品主图片
				goods_main_photo = gc.getGoods().getGoods_main_photo().getPath() + "/"
						+ gc.getGoods().getGoods_main_photo().getName() + "_small."
						+ gc.getGoods().getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);

			if ("combin".equals(gc.getCart_type()) && gc.getCombin_main() == 1) { // 如果是组合销售
				String suit_ids[] = gc.getCombin_suit_ids().split(",");
				List<Map> suit_list = new ArrayList<Map>();
				for (String temp_id : suit_ids) {
					if (!"".equals(temp_id)) {
						GoodsCart suit_cart = this.goodsCartService.getObjById(CommUtil.null2Long(temp_id));
						Map suit_map = new HashMap();
						suit_map.put("name", suit_cart.getGoods().getGoods_name());
						suit_map.put("img",
								suit_cart.getGoods().getGoods_main_photo().getPath() + "/"
										+ suit_cart.getGoods().getGoods_main_photo().getName() + "_small."
										+ suit_cart.getGoods().getGoods_main_photo().getExt());
						suit_list.add(suit_map);
					}
				}
				map.put("suit_list", suit_list);
				map.put("cart_status", "组合销售");
			}
			if (gc.getGoods().getOrder_enough_give_status() == 1 && gc.getWhether_choose_gift() == 1) { // 如果是满就送

				Map cart_gift = Json.fromJson(Map.class, gc.getGift_info());
				Goods gift_goods = this.goodsService.getObjById(CommUtil.null2Long(cart_gift.get("goods_id")));
				if (gift_goods != null && !gift_ids.contains(gift_goods.getId().toString())) {
					cart_gift.put("goods_main_photo",
							gift_goods.getGoods_main_photo().getPath() + "/" + gift_goods.getGoods_main_photo().getName()
									+ "_small." + gift_goods.getGoods_main_photo().getExt());
					cart_gift.put("goods_price", gift_goods.getGoods_current_price());

					cart_gift.put("goods_count", ""); // 商品数量
					cart_gift.put("goods_spec", ""); // 商品规格
					cart_gift.put("cart_status", "赠品");
				}
				if (!gift_ids.contains(gift_goods.getId().toString())) {
					goods_list.add(cart_gift);
					gift_ids.add(gift_goods.getId().toString());
				}

				map.put("cart_status", "满就送");
			}
			if (gc.getGoods().getEnough_reduce() == 1) { // 如果是满就送
				map.put("cart_status", "满就减");
			}

			goods_list.add(map);
		}
		json_map.put("goods_list", goods_list);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 根据购物车获得相应的物流运费模板
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/goods_cart2_cartsTrans.htm")
	public void goods_cart2_cartsTrans(HttpServletRequest request, HttpServletResponse response, String cart_ids,
			String addr_id, String store_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
		Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
		if (!StringUtils.isNullOrEmpty(store_ids)) {
			String ids[] = store_ids.split(",");
			List trans_list = new ArrayList();
			for (String id : ids) {
				int goods_count = 0;
				if (!"".equals(id)) {
					List<GoodsCart> temp_gc_list = new ArrayList();
					for (GoodsCart gc : carts) {
						if ("self".equals(id) && gc.getGoods().getGoods_type() == 0) { // 平台自营
							temp_gc_list.add(gc);
						} else {
							if (gc.getGoods().getGoods_type() == 1
									&& gc.getGoods().getGoods_store().getId().toString().equals(id)) {
								temp_gc_list.add(gc);
							}
						}
					}
					goods_count = temp_gc_list.size();
					List goods_list = new ArrayList();
					String url = CommUtil.getURL(request);
					for (GoodsCart gc : temp_gc_list) {
						String goods_main_photo = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
								+ this.configService.getSysConfig().getGoodsImage().getName();
						if (gc.getGoods().getGoods_main_photo() != null) { // 商品主图片
							goods_main_photo = gc.getGoods().getGoods_main_photo().getPath() + "/"
									+ gc.getGoods().getGoods_main_photo().getName() + "_small."
									+ gc.getGoods().getGoods_main_photo().getExt();
						}
						goods_list.add(goods_main_photo);
					}
					List<SysMap> sms = this.transportTools.calStoreCartsTransFee(temp_gc_list,
							addr.getArea().getId().toString(), new ArrayList<Long>(), new ArrayList<Integer>(),null);
					List transInfo_list = new ArrayList();
					Map transInfo_map = new HashMap();
					for (SysMap s : sms) {
						Map map = new HashMap();
						map.put("key", s.getKey());
						map.put("value", s.getValue());
						transInfo_list.add(map);
					}
					transInfo_map.put("transInfo_list", transInfo_list);
					String store_name = "平台运营商";
					if (!"self".equals(id)) {
						Store store = this.storeService.getObjById(CommUtil.null2Long(id));
						store_name = store.getStore_name();
					}
					transInfo_map.put("goods_list", goods_list);
					transInfo_map.put("goods_count", goods_count);
					transInfo_map.put("store_name", store_name);
					transInfo_map.put("store_id", id);
					trans_list.add(transInfo_map);
				}
			}
			json_map.put("trans_list", trans_list);
		}
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 根据购物车获得相应的物流运费模板
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/goods_cart2_coupon.htm")
	public void goods_cart2_coupon(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String store_ids, String order_goods_price) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		if (!StringUtils.isNullOrEmpty(user_id) && !StringUtils.isNullOrEmpty(token)) {
			User user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					List<CouponInfo> Coupon_list = new ArrayList<CouponInfo>();
					List json_list = new ArrayList();
					String ids[] = store_ids.split(",");
					for (String id : ids) {
						if (!"".equals(id)) {
							List<CouponInfo> list_temp = this.cartTools.mobile_query_coupon(id, order_goods_price, user_id);
							Coupon_list.addAll(list_temp);
						}
					}
					for (CouponInfo info : Coupon_list) {
						Map map = new HashMap();
						map.put("coupon_id", info.getId());
						map.put("coupon_name", info.getCoupon().getCoupon_name());
						map.put("coupon_amount", info.getCoupon().getCoupon_amount());
						map.put("coupon_info", "优惠" + info.getCoupon().getCoupon_amount() + "元");
						json_list.add(map);
					}
					json_map.put("verify", true);
					json_map.put("coupon_list", json_list);
				} else {
					json_map.put("verify", false);
				}
			} else {
				json_map.put("verify", false);
			}
		} else {
			json_map.put("verify", false);
		}
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 手机端购物车提交订单请求,提交订单如果有多个商家商品，发票信息全部一致、提交订单之前将本订单中所有可用优惠券查出来，提交订单时只能选择一张优惠券，
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param store_id
	 *            ：店铺id，以逗号间隔
	 * @param addr_id
	 *            收货地址id
	 * @param cart_ids
	 *            购物车id，逗号隔开
	 * @param order_type
	 *            ：为订单类型标识，android端生成的订单为android，ios端生成订单为ios，
	 * @param payType
	 *            ：支付方式，分为online（在线支付）,balance（预存款支付）,payafter（货到付款）
	 * @param invoiceType
	 *            ：发票类型
	 * @param invoice
	 * @param coupon_id
	 *            ：优惠券id，手机端每次下单只能选择一张优惠券使用
	 * @param delivery_type
	 *            ：配送方式，值为0（快递），1（自提点），2（到店自提）
	 * @param delivery_time
	 *            配送时间
	 * @throws Exception
	 */
	@RequestMapping("/app/goods_cart3.htm")
	public synchronized void goods_cart3(HttpServletRequest request, HttpServletResponse response, String user_id,
			String token, String store_id, String addr_id, String cart_ids, String order_type, String payType,
			String invoiceType, String invoice, String coupon_id, int delivery_type, String delivery_time) throws Exception {
		Map json_map1 = new HashMap();
		boolean verify = true;
		Long main_order_id = null;
		String order_num = null; // 订单编号
		int code = 100;
		int needId = 0;// 用户购买商品时是否需要填写身份证，只要用户购买的商品中有一个需要身份证，则就需要填写身份证购买(此值不能由前台传过来)
		if (!StringUtils.isNullOrEmpty(user_id) && !StringUtils.isNullOrEmpty(token)) {
			User user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					String seckill_info = null;
					List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
					if (carts.size() > 0) {
						// 验证购物车中是否存在库存为0的商品
						boolean inventory_very = true;
						for (GoodsCart gc : carts) {
							if (gc.getCount() <= 0) {
								inventory_very = false;
								break;
							}
							if (gc.getGoods().getSeckill_buy() != 0) { // 秒杀订单
								if (gc.getGoods().getSeckill_buy() != 2 || seckill_info != null) { // 秒杀已停止或用户购买了两种秒杀商品
									inventory_very = false;
									break;
								} else {
									String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
									Map params = new HashMap();
									params.put("goods_id", gc.getGoods().getId());
									params.put("gg_status", 2);
									SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
									boolean isOK = orderFormService.validateSeckillOrderForm(
											SecurityUserHolder.getCurrentUser().getId(), seckillGoods.getId());
									if (!isOK) { // 同一用户ID只能购买一次同一个秒杀商品
										inventory_very = false;
										break;
									}
									seckill_info = "{good_id:" + gc.getGoods().getId() + ",seckill_goods_id:"
											+ seckillGoods.getId() + "}";
									gc.setCount(1);
									this.goodsCartService.update(gc);
								}
							}
							// 增加对是否需要使用身份证购买的检查
							if (needId == 0 && gc.getGoods().getGoodsConfig().getNeedId() == 1) {
								needId = 1;
							}
						}
						if (seckill_info != null && carts.size() > 1) {
							verify = false;
						} else {
							if (inventory_very) {
								Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
								if (delivery_type != 2 && addr == null) {
									verify = false;
								} else {
									List<Map<String, Object>> orderForms = new ArrayList<Map<String, Object>>();
									OrderForm main_order = null;
									double all_of_price = 0;

									String store_ids[] = store_id.split(",");
									String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
									// 计算最后一个店铺id start
									String main_order_store_id = "0";
									String main_store_id = store_ids[store_ids.length - 1];
									if (!"self".equals(main_store_id)) {
										Store store = this.storeService.getObjById(CommUtil.null2Long(main_store_id));
										main_order_store_id = CommUtil.null2String(store.getId());
									}
									// 计算最后一个店铺id end
									for (int i = 0; i < store_ids.length; i++) { // 根据店铺id，保存多个子订单
										String sid = store_ids[i];
										Store store = null;
										if (!"self".equals(sid)) {
											store = this.storeService.getObjById(CommUtil.null2Long(sid));
										}
										List<GoodsCart> gc_list = new ArrayList<GoodsCart>(); // 购物车
										List<Map> map_list = new ArrayList<Map>(); // 商品
										List<Map> gift_maps = new ArrayList<Map>(); // 赠品
										List<String> gift_ids = new ArrayList<String>();
										for (GoodsCart gc : carts) {
											// 处理赠品信息
											if (gc.getWhether_choose_gift() == 1) {
												Map cart_gift = Json.fromJson(Map.class, gc.getGift_info());
												Goods gift_goods = this.goodsService
														.getObjById(CommUtil.null2Long(cart_gift.get("goods_id")));
												if (gift_goods != null
														&& !gift_ids.contains(gift_goods.getId().toString())) {
													cart_gift.put("goods_main_photo",
															gift_goods.getGoods_main_photo().getPath() + "/"
																	+ gift_goods.getGoods_main_photo().getName() + "_small."
																	+ gift_goods.getGoods_main_photo().getExt());
													cart_gift.put("goods_price", gift_goods.getGoods_current_price());
													String goods_domainPath = CommUtil.getURL(request) + "/goods_"
															+ gift_goods.getId() + ".htm";
													if (this.configService.getSysConfig().isSecond_domain_open()
															&& gift_goods.getGoods_store().getStore_second_domain() != ""
															&& gift_goods.getGoods_type() == 1) {
														String store_second_domain = "http://"
																+ gift_goods.getGoods_store().getStore_second_domain() + "."
																+ CommUtil.generic_domain(request);
														goods_domainPath = store_second_domain + "/goods_"
																+ gift_goods.getId() + ".htm";
													}
													cart_gift.put("goods_domainPath", goods_domainPath); // 商品二级域名路径
												}
												if (!gift_ids.contains(gift_goods.getId().toString())) {
													gift_maps.add(cart_gift);
													gift_ids.add(gift_goods.getId().toString());
												}
											}

											Goods goods = gc.getGoods();
											// 过滤掉非当前店铺的商品
											if (goods.getGoods_type() == 1) { // 商家商品
												if (!goods.getGoods_store().getId().equals(CommUtil.null2Long(sid))) {
													continue;
												}
											} else { // 自营商品
												if (!"self".equals(sid)) {
													continue;
												}
											}

											Map json_map = new HashMap();
											String goodsUrl = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
											String goods_snapshoot = this.snapshotTools.createGoodsSnapshot(goodsUrl,
													user.getId().toString());
											json_map.put("goods_snapshoot", goods_snapshoot);
											json_map.put("goods_id", goods.getId());
											json_map.put("goods_name", goods.getGoods_name());
											json_map.put("goods_choice_type", goods.getGoods_choice_type());
											String goods_type = "";
											if ("combin".equals(gc.getCart_type())) {
												goods_type = "combin";
											}
											if ("group".equals(gc.getCart_type())) {
												goods_type = "group";
											}
											json_map.put("goods_type", goods_type);
											json_map.put("goods_count", gc.getCount());
											json_map.put("goods_price", gc.getPrice()); // 商品单价
											json_map.put("goods_all_price", CommUtil.mul(gc.getPrice(), gc.getCount())); // 商品总价
											json_map.put("goods_gsp_val", gc.getSpec_info());
											json_map.put("goods_gsp_ids", gc.getCart_gsp());
											json_map.put("goods_mainphoto_path",
													goods.getGoods_main_photo().getPath() + "/"
															+ goods.getGoods_main_photo().getName() + "_small."
															+ goods.getGoods_main_photo().getExt());
											if (!"self".equals(sid)) { // 商家商品
												//begin dengyuqi 2016-3-4 
//												json_map.put("goods_commission_price", this.getGoodscartCommission(gc)); // 设置该商品总佣金
//												json_map.put("goods_commission_rate", goods.getGc().getCommission_rate()); // 设置该商品的佣金比例
//												json_map.put("goods_payoff_price",
//														CommUtil.subtract(CommUtil.mul(gc.getPrice(), gc.getCount()),
//																this.getGoodscartCommission(gc))); // 该商品结账价格=该商品总价格-商品总佣金
												//TODO 该处无法获得满减优惠信息及优惠券优惠信息，因暂时不用app，故先传了两个null，待用到时再解决
												double commission = this.getGoodscartCommission(gc,carts,null,null);
												json_map.put("goods_commission_price", commission); // 设置该商品总佣金
												json_map.put("goods_payoff_price",
														CommUtil.subtract(CommUtil.mul(gc.getPrice(), gc.getCount()), commission)); // 该商品结账价格=该商品总价格-商品总佣金
												json_map.put("goods_commission_rate", getCommissionRate(gc.getGoods())); // 设置该商品的佣金比例
												//end
												
												String goods_domainPath = CommUtil.getURL(request) + "/goods_"
														+ goods.getId() + ".htm";
												String store_domainPath = CommUtil.getURL(request) + "/store_"
														+ goods.getGoods_store().getId() + ".htm";
												if (this.configService.getSysConfig().isSecond_domain_open()
														&& goods.getGoods_store().getStore_second_domain() != ""
														&& goods.getGoods_type() == 1) {
													String store_second_domain = "http://"
															+ goods.getGoods_store().getStore_second_domain() + "."
															+ CommUtil.generic_domain(request);
													goods_domainPath = store_second_domain + "/goods_" + goods.getId()
															+ ".htm";
													store_domainPath = store_second_domain;
												}
												json_map.put("goods_domainPath", goods_domainPath); // 商品二级域名路径
												json_map.put("store_domainPath", store_domainPath); // 店铺二级域名路径
											} else {
												json_map.put("goods_domainPath",
														CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm"); // 商品二级域名路径
											}
											map_list.add(json_map);
											gc_list.add(gc);
										}
										
										//begin dengyuqi 2016-3-4
										Map<String,List<OrderFreightInfo>> freightInfos = new HashMap<String, List<OrderFreightInfo>>();
										String freightInfo = null;
										//end

										// 计算当前店铺的订单总价格
										double ship_price = CommUtil.null2Double(request.getParameter("ship_price_" + sid));
										String transport = request.getParameter("trans_" + sid);
										if (delivery_type != 2) {
											List<SysMap> sms = this.transportTools.calStoreCartsTransFee(gc_list,
													CommUtil.null2String(addr.getArea().getId()), new ArrayList<Long>(),
													new ArrayList<Integer>(),freightInfos);
											for (SysMap sm : sms) {
												if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
													ship_price = CommUtil.null2Double(sm.getValue()); // 订单物流运费
													break;
												}
											}
											
											for (Entry<String, List<OrderFreightInfo>> sm : freightInfos.entrySet()) {
												if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
													freightInfo = Json.toJson(sm.getValue()); // 订单运费信息
													break;
												}
											}
										}
										double goods_amount = this.getcartsPrice(gc_list); // 订单中商品价格
										double totalPrice = CommUtil.add(goods_amount, ship_price); // 订单总价

										// 满就减
										Map ermap = this.orderFormTools.calEnoughReducePrice(gc_list, cart_ids);
										String er_json = (String) ermap.get("er_json");
										double all_goods = Double.parseDouble(ermap.get("all").toString());
										double reduce = Double.parseDouble(ermap.get("reduce").toString());

										OrderForm of = new OrderForm();
										of.setSeckill_info(seckill_info);
										of.setAddTime(new Date());
										String order_store_id = "0";
										if (!"self".equals(sid)) {
											order_store_id = CommUtil.null2String(store.getId());
										}
										of.setMainOrderId(user.getId() + order_suffix + main_order_store_id);
										of.setOrder_id(user.getId() + order_suffix + order_store_id);
										// 设置收货地址信息
										if (delivery_type != 2) {
											of.setReceiver_Name(addr.getTrueName());
											of.setReceiver_area(addr.getArea().getParent().getParent().getAreaName()
													+ addr.getArea().getParent().getAreaName()
													+ addr.getArea().getAreaName());
											of.setReceiver_area_info(addr.getArea_info());
											of.setReceiver_mobile(addr.getMobile());
											of.setReceiver_telephone(addr.getTelephone());
											of.setReceiver_zip(addr.getZip());
											if (needId == 1) {
												of.setReceiver_card(addr.getCard());
											} else {
												of.setReceiver_card(null);
											}
										}
										of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
										of.setEnough_reduce_info(er_json);
										of.setTransport(transport);
										of.setOrder_status(10);
										of.setUser_id(user.getId().toString());
										of.setUser_name(user.getUserName());
										of.setGoods_info(Json.toJson(map_list, JsonFormat.compact())); // 设置商品信息json数据
										of.setInvoiceType(CommUtil.null2Int(invoiceType));
										of.setInvoice(invoice);
										of.setShip_price(BigDecimal.valueOf(ship_price));
										of.setGoods_amount(BigDecimal.valueOf(goods_amount));
										of.setTotalPrice(BigDecimal.valueOf(totalPrice));
										
										//begin dengyuqi 2016-3-4
										of.setFreight_info(freightInfo);
										//end

										// 优惠券
										if (!StringUtils.isNullOrEmpty(coupon_id)) {
											CouponInfo ci = this.couponInfoService.getObjById(CommUtil.null2Long(coupon_id));
											boolean coupon_verify = false;
											if (seckill_info == null) { // modify by dengyuqi 2015-12-8 秒杀订单不允许使用优惠券
												if (of.getOrder_form() == 1 && ci.getCoupon().getCoupon_type() == 0) {// 平台自营订单
													coupon_verify = true;
												} else if (of.getOrder_form() == 0 && ci.getCoupon().getCoupon_type() != 0) {// 商家订单
													if (ci.getCoupon().getStore().getId().toString()
															.equals(of.getStore_id())) {
														coupon_verify = true;
													}
												}
											}
											if (ci != null && coupon_verify) {
												if (user.getId().equals(ci.getUser().getId())) {
													ci.setStatus(1);
													this.couponInfoService.update(ci);
													Map coupon_map = new HashMap();
													coupon_map.put("couponinfo_id", ci.getId());
													coupon_map.put("couponinfo_sn", ci.getCoupon_sn());
													coupon_map.put("coupon_amount", ci.getCoupon().getCoupon_amount());
													coupon_map.put("coupon_order_amount",
															ci.getCoupon().getCoupon_order_amount()); // 优惠券限制金额
													coupon_map.put("coupon_return_amount", 0); // 优惠券已退金额
													double rate = CommUtil.div(ci.getCoupon().getCoupon_amount(),
															goods_amount);
													coupon_map.put("coupon_goods_rate", rate);
													of.setCoupon_info(Json.toJson(coupon_map, JsonFormat.compact()));
													of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(of.getTotalPrice(),
															ci.getCoupon().getCoupon_amount())));
												}
											}
										}
										all_of_price = CommUtil.add(all_of_price, totalPrice); // 总订单价格

										if ("self".equals(sid)) {
											of.setOrder_form(1); // 平台自营商品订单
										} else {
											double commission_amount = this.getOrderCommission(gc_list,of.getEnough_reduce_info(),of.getCoupon_info()); // 订单总体佣金
											of.setCommission_amount(BigDecimal.valueOf(commission_amount)); // 该订单总体佣金费用
											of.setOrder_form(0); // 商家商品订单
											of.setStore_id(store.getId().toString());
											of.setStore_name(store.getStore_name());
										}
										of.setOrder_type(order_type); // 设置订单类型，android端为"android",苹果端为"ios"
										if (gift_maps.size() > 0) {
											of.setGift_infos(Json.toJson(gift_maps, JsonFormat.compact()));
											of.setWhether_gift(1);
										}
										of.setDelivery_type(delivery_type);
										of.setDelivery_time(delivery_time);
										if (i == store_ids.length - 1) {
											of.setOrder_main(1); // 同时购买多个商家商品，最后一个订单为主订单，其他的作为子订单，以json信息保存，用在买家中心统一显示大订单，统一付款
											of.setMainOrderId(null);// 主订单不存此单号
											main_order = of;
										}

										Map<String, Object> map = new HashMap<String, Object>();
										map.put("of", of);
										map.put("gc_list", gc_list);
										orderForms.add(map);
									}

									try {
//										orderFormService.saveOrderForm(orderForms);

										main_order_id = main_order.getId(); // 主订单id
										order_num = main_order.getOrder_id();

										// 在循环外，给买家只发送一次短信邮件
										if (main_order.getOrder_form() == 0) {
											this.msgTools.sendEmailCharge(CommUtil.getURL(request),
													"email_tobuyer_order_submit_ok_notify", user.getEmail(), null,
													CommUtil.null2String(main_order.getId()), main_order.getStore_id());
											this.msgTools.sendSmsCharge(CommUtil.getURL(request),
													"sms_tobuyer_order_submit_ok_notify", user.getMobile(), null,
													CommUtil.null2String(main_order.getId()), main_order.getStore_id());
										} else {
											this.msgTools.sendEmailFree(CommUtil.getURL(request),
													"email_tobuyer_order_submit_ok_notify", user.getEmail(), null,
													CommUtil.null2String(main_order.getId()));
											this.msgTools.sendSmsFree(CommUtil.getURL(request),
													"sms_tobuyer_order_submit_ok_notify", user.getMobile(), null,
													CommUtil.null2String(main_order.getId()));
										}
									} catch (Exception e) {
										logger.error(e);
										verify = false;
									}
								}
							} else {
								verify = false;
								code = -300;
							}
						}
					}
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map1.put("order_id", main_order_id);
		json_map1.put("order_num", order_num);
		json_map1.put("payType", payType);
		json_map1.put("verify", verify);
		String json = Json.toJson(json_map1, JsonFormat.compact());
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
	 * 根据商品信息，计算该商品默认的规格信息，以各个规格值的第一个为默认值
	 * 
	 * @param goods
	 *            商品
	 * @return 默认规格id组合，如1,2
	 */
	private String generic_default_gsp(Goods goods) {
		String gsp = "";
		if (goods != null) {
			List<GoodsSpecification> specs = this.goodsViewTools.generic_spec(CommUtil.null2String(goods.getId()));
			for (GoodsSpecification spec : specs) {
				for (GoodsSpecProperty prop : goods.getGoods_specs()) {
					if (prop.getSpec().getId().equals(spec.getId())) {
						gsp = prop.getId() + "," + gsp;
						break;
					}
				}
			}
		}
		return gsp;
	}

	/**
	 * 根据商品规格获取价格
	 * 
	 * @param request
	 * @param response
	 */
	private String generGspgoodsPrice(String gsp, String id, String user_id) {
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		}
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		if (goods.getActivity_status() == 2) {
			if (user != null) {
				Map map = this.activityTools.getActivityGoodsInfo(CommUtil.null2String(goods.getId()),
						CommUtil.null2String(user.getId()), null);
				price = CommUtil.null2Double(map.get("rate_price"));
			}
		} else {
			if ("spec".equals(goods.getInventory_type())) {
				List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		return CommUtil.null2String(price);
	}

	/**
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp, String user_id) {
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		}
		double price = 0;
		Map map = new HashMap();
		int count = 0;
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
				if (gsp != null) {
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
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2 && user != null) { // 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil.null2String(user.getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			price = CommUtil.mul(rebate, price);
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}

	/**
	 * 获得购物车总体商品价格
	 * 
	 * @param request
	 * @param response
	 */
	private double getcartsPrice(List<GoodsCart> carts) {

		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		for (GoodsCart gc : carts) {
			if (StringUtils.isNullOrEmpty(gc.getCart_type())) { // 普通商品处理
				all_price = CommUtil.add(all_price, CommUtil.mul(gc.getCount(), gc.getPrice()));
			} else if ("combin".equals(gc.getCart_type())) { // 组合套装商品处理
				if (gc.getCombin_main() == 1) {
					Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
					all_price = CommUtil.add(all_price, map.get("suit_all_price"));
				}
			}
			if (gc.getGoods().getEnough_reduce() == 1) { // 是满就减商品，记录金额
				String er_id = gc.getGoods().getOrder_enough_reduce_id();
				if (ermap.containsKey(er_id)) {
					double last_price = (double) ermap.get(er_id);
					ermap.put(er_id, CommUtil.add(last_price, CommUtil.mul(gc.getCount(), gc.getPrice())));
				} else {
					ermap.put(er_id, CommUtil.mul(gc.getCount(), gc.getPrice()));
				}
			}
		}

		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
			if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())) { // 活动通过审核且正在进行
				String erjson = er.getEr_json();
				double er_money = ermap.get(er_id); // 购物车中的此类满减的金额
				Map fromJson = (Map) Json.fromJson(erjson);
				double reduce = 0;
				for (Object enough : fromJson.keySet()) {
					if (er_money >= CommUtil.null2Double(enough)) {
						reduce = CommUtil.null2Double(fromJson.get(enough));
					}
				}
				all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
			}
		}
		double d2 = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		return CommUtil.null2Double(CommUtil.formatMoney(d2));
	}

	/**
	 * 金额详细计算
	 * 
	 * @param carts
	 * @param gcs
	 * @return
	 */
	private Map calCartPriceInfo(List<GoodsCart> carts) {
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		Map erid_goodsids = new HashMap();
		Date date = new Date();
		String info = "";
		for (GoodsCart gc : carts) {
			all_price = CommUtil.add(all_price, CommUtil.mul(gc.getCount(), gc.getPrice()));
			if (gc.getGoods().getEnough_reduce() == 1) { // 是满就减商品，记录金额
				String er_id = gc.getGoods().getOrder_enough_reduce_id();
				EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
				if (er.getErstatus() == 10 && er.getErbegin_time().before(date)) {
					if (ermap.containsKey(er_id)) {
						double last_price = (double) ermap.get(er_id);
						ermap.put(er_id, CommUtil.add(last_price, CommUtil.mul(gc.getCount(), gc.getPrice())));
						((List) erid_goodsids.get(er_id)).add(gc.getGoods().getId());
					} else {
						ermap.put(er_id, CommUtil.mul(gc.getCount(), gc.getPrice()));
						List list = new ArrayList();
						list.add(gc.getGoods().getId());
						erid_goodsids.put(er_id, list);
					}
				}
			}
		}

		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
			String erjson = er.getEr_json();
			double er_money = ermap.get(er_id); // 购物车中的此类满减的金额
			Map fromJson = (Map) Json.fromJson(erjson);
			double reduce = 0;
			String erstr = "";
			for (Object enough : fromJson.keySet()) {
				if (er_money >= CommUtil.null2Double(enough)) {
					reduce = CommUtil.null2Double(fromJson.get(enough));
					erstr = "已购满" + enough + "元,已减" + reduce + "元";
				}
			}
			info = erstr;

			all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
		}
		Map prices = new HashMap();

		prices.put("info", info);

		double er = Math.round(all_enough_reduce * 100) / 100.0;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("reduce", CommUtil.null2Double(erbd2)); // 满减价格

		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("all", CommUtil.null2Double(bd2)); // 商品总价

		return prices;
	}
	
	/**
	 * 根据cart_ids获取购物车
	 * 
	 * @param request
	 * @param response
	 */
	private List<GoodsCart> getGoodscartByids(String cart_ids) {
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		if (cart_ids != null) {
			String ids[] = cart_ids.split(",");
			for (String cart_id : ids) {
				if (!"".equals(cart_id)) {
					GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(cart_id));
					if (gc != null) {
						carts.add(gc);
					}
				}
			}
		}
		return carts;
	}

	/**
	 * 获得商品组合商品价格
	 * 
	 * @param request
	 * @param response
	 */
	private double getGoodsCombinPrice() {
		double combin_price = 0.00;
		return combin_price;
	}

	/**
	 * 获得商品佣金比例
	 * @param goods
	 * @return
	 */
	private double getCommissionRate(Goods goods){
		GoodsClass goodsClass = goods.getGc();
		//共用的佣金比
		double defaultCommissionRate = goodsClass.getCommission_rate().doubleValue();
		//定制的佣金比
		String commissionInfo = goods.getGoods_store().getGc_commission_info();
		List<Map> list = CommUtil.json2List(commissionInfo);
		for(Map map : list){
			if(goodsClass.getId() == Long.parseLong((String)map.get("id"))){
				defaultCommissionRate =Double.parseDouble(((String)map.get("commission"))) ;
				break;
			}
		}
		return defaultCommissionRate;
	}

	/**
	 * 获得商品佣金
	 * @param gc
	 * @param gcs
	 * @param enoughReduceInfo
	 * @param couponInfo
	 * @return
	 */
	private double getGoodscartCommission(GoodsCart gc,List<GoodsCart> gcs,String enoughReduceInfo,String couponInfo) {
//		double commission_price = CommUtil.mul(gc.getGoods().getGc().getCommission_rate(),
//				CommUtil.mul(gc.getPrice(), gc.getCount()));
//		return commission_price;
		Goods goods = gc.getGoods();
		GoodsClass goodsClass = goods.getGc();
		//共用的佣金比
		double defaultCommissionRate = getCommissionRate(goods);
		
		//扣点结算价=单个商品原金额- sum（（单个商品参与优惠的金额/所有商品参与优惠总额）*优惠总金额）
		double beforePrice = gc.getCount() * gc.getPrice().doubleValue();//商品原金额
		double enoughReducePrice = 0.00;//满减优惠的金额
		double couponPrice = 0.00;//优惠券优惠的金额
		
		if(null != enoughReduceInfo){
			enoughReducePrice = getEnoughReducePrice(goods.getId(),enoughReduceInfo);
		}
		
		if(null != couponInfo){
			double allBeforePrice = 0.00;
			double allEnoughReducePrice = 0.00;
			for(GoodsCart goodsCart : gcs){
				allBeforePrice += gc.getCount() * gc.getPrice().doubleValue();
				allEnoughReducePrice += getEnoughReducePrice(goodsCart.getGoods().getId(),enoughReduceInfo);
			}
			
			double selfAmount = beforePrice - enoughReducePrice;//单个商品参与优惠的金额
			double allAmount = allBeforePrice - allEnoughReducePrice;//所有商品参与优惠总额
			
			Map<String,Object> couponInfoMap = (Map<String,Object>) Json.fromJson(couponInfo); //优惠券优惠信息
			double reducePrice =  (Integer)couponInfoMap.get("coupon_amount");
			
			couponPrice = selfAmount / allAmount * reducePrice;
		}
		
		return (beforePrice - enoughReducePrice - couponPrice) * defaultCommissionRate;
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getOrderCommission(List<GoodsCart> gcs,String enoughReduceInfo,String couponInfo) {
//		double commission_price = 0.00;
//		for (GoodsCart gc : gcs) {
//			commission_price = commission_price + this.getGoodscartCommission(gc);
//		}
//		return commission_price;
		double commission_price = 0.00;
		
		for (GoodsCart gc : gcs) {
			double commissionPrice = getGoodscartCommission(gc,gcs,enoughReduceInfo,couponInfo);
			
			commission_price += commissionPrice;
		}
		
		return commission_price;
	}

	private static String getHttpContent(String url, String charSet, String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");
			// 设置访问超时时间及读取网页流的超时时间,毫秒值
			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);
			// 得到访问页面的返回值
			int response_code = connection.getResponseCode();
			if (response_code == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content += line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}

	private Map<String, BigDecimal> enough_give_price(String ids) {
		Map<String, BigDecimal> enough_give_price = new HashMap<String, BigDecimal>();
		List<GoodsCart> carts = this.getGoodscartByids(ids);
		Date date = new Date();
		Set<Long> set = new HashSet<Long>();
		for (GoodsCart cart : carts) {
			if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
				BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
				if (bg.getBeginTime().before(date)) {
					set.add(cart.getGoods().getBuyGift_id());
				}
			}
		}
		if (set.size() > 0) {
			Map<Long, List<GoodsCart>> map = new HashMap<Long, List<GoodsCart>>();
			for (Long id : set) {
				map.put(id, new ArrayList<GoodsCart>());
			}
			for (GoodsCart cart : carts) {
				if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
					if (map.containsKey(cart.getGoods().getBuyGift_id())) {
						map.get(cart.getGoods().getBuyGift_id()).add(cart);
					}
				}
			}
			for (Long id : set) {
				enough_give_price.put(id.toString(), BigDecimal.valueOf(this.getcartsPrice(map.get(id))));
			}
		}
		return enough_give_price;
	}

	/**
	 * 根据规格id、用户id来获取商品单价
	 * 
	 * @param gsp
	 * @param id
	 * @param user_id
	 * @return
	 */
	public double load_goods_gsp(String gsp, String id, String user_id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		double price = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) { // 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			price = CommUtil.null2Double(goods.getGoods_current_price());
			if ("spec".equals(goods.getInventory_type())) {
				List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
				if (gsp != null) {
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
			BigDecimal ac_rebate = null;
			if (user_id != null) {
				if (goods.getActivity_status() == 2) { // 如果是促销商品，根据规格配置价格计算相应配置的促销价格
					ActivityGoods actGoods = this.activityGoodsService.getObjById(goods.getActivity_goods_id());
					// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
					BigDecimal rebate = BigDecimal.valueOf(0.00);
					int level = this.integralViewTools.query_user_level(user_id);
					if (level == 0) {
						rebate = actGoods.getAct().getAc_rebate();
					} else if (level == 1) {
						rebate = actGoods.getAct().getAc_rebate1();
					} else if (level == 2) {
						rebate = actGoods.getAct().getAc_rebate2();
					} else if (level == 3) {
						rebate = actGoods.getAct().getAc_rebate3();
					}
					price = CommUtil.mul(rebate, price);
				}
			}

		}
		return price;
	}
	
	/**
	 * 按比例获取商品满减优惠价格
	 * @param goodsId 商品ID
	 * @param enoughReduceInfoStr 满减信息  e.g. {"prices_2":[1.00],"2":[5487],"return_2":0.0,"reduce_2":1.0,"counts_2":[3],"all_2":3.0,"enouhg_2":"2.0"}
	 * @return
	 */
	private Double getEnoughReducePrice(Long goodsId,String enoughReduceInfoStr){
		//商品满减优惠价格
		double enoughReducePrice = 0.00;
		Map<String,Object> enoughReduceInfo = (Map<String,Object>) Json.fromJson(enoughReduceInfoStr); //满减优惠信息
		//保存当前商品所在的满减活动
		OrderEnoughReduceInfo orderEnoughReduceInfo = null;
		Set<String> keys = enoughReduceInfo.keySet();
		for(String key : keys){
			if(key.matches("^\\d+$")){ //满减活动id
				List<Integer> goodsIds = (List) enoughReduceInfo.get(key); //参加该满减活动的商品id列表
				if(goodsIds.contains(goodsId)){
					orderEnoughReduceInfo = new OrderEnoughReduceInfo(Long.parseLong(key));
					
					//减
					double reducePrice =  (Double)enoughReduceInfo.get("reduce_"+key);
					orderEnoughReduceInfo.setReducePrice(reducePrice);
					
					List<EnoughReduceGoods> enoughReduceGoodss = new ArrayList<EnoughReduceGoods>();
					for(int i = 0 ; i < goodsIds.size(); i++){
						double price = (Double)((List)enoughReduceInfo.get("prices_"+key)).get(i);
						int count = (Integer)((List)enoughReduceInfo.get("counts_"+key)).get(i);
						EnoughReduceGoods enoughReduceGoods = orderEnoughReduceInfo.new EnoughReduceGoods(goodsIds.get(i), price, count);
						enoughReduceGoodss.add(enoughReduceGoods);
					}
					orderEnoughReduceInfo.setEnoughReduceGoodss(enoughReduceGoodss);
					break;
				}
			}
		}
		
		if(null != orderEnoughReduceInfo){
			double selfAmount = 0.00;//单个商品参与优惠的金额
			double allAmount = 0.00;//所有商品参与优惠总额
			List<EnoughReduceGoods> enoughReduceGoodss = orderEnoughReduceInfo.getEnoughReduceGoodss();
			for(EnoughReduceGoods enoughReduceGoods : enoughReduceGoodss){
				if(enoughReduceGoods.getGoodsId() == goodsId){
					selfAmount = enoughReduceGoods.getCount() * enoughReduceGoods.getPrice();
				}
				allAmount += enoughReduceGoods.getCount() * enoughReduceGoods.getPrice();
			}
			enoughReducePrice = selfAmount / allAmount * orderEnoughReduceInfo.getReducePrice();
		}
		return enoughReducePrice;
	}
}
