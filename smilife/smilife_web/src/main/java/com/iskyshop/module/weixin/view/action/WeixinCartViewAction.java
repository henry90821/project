package com.iskyshop.module.weixin.view.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.buyer.tools.CartTools;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;
import com.iskyshop.manage.seller.tools.CombinTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.module.weixin.manage.coupon.activity.CouponActivityComm;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;


/**
 * 
 * 
 * <p>
 * Title:WapCartViewAction.java
 * </p>
 * 
 * <p>
 * Description:移动端用户中心购物车控制器
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
 * @date 2014年8月20日
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinCartViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
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
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityTools;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private DeliveryAddressTools DeliveryAddressTools;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private UserManageConnector userManageConnector;	
	@Autowired
	private IFeeManageservice feeManageservice;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private SynchronizeOrderPublisher synchronizeOrderPublisher;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IpayCenterService payCenterService;

	/**
	 * 添加商品到购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            添加到购物车的商品id
	 * @param count
	 *            添加到购物车的商品数量
	 * @param price
	 *            添加到购物车的商品的价格,该逻辑会更加gsp再次计算实际价格，避免用户在前端篡改
	 * @param gsp
	 *            商品的属性值，这里传递id值，如12,1,21
	 * @param suit_gsp 组合套装中各商品的规格值
	 * @param buy_type
	 *            购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids
	 *            组合搭配中配件id
	 */
	@RequestMapping("/wap/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, Long id, Integer count,
			String gsp, String suit_gsp, String buy_type, String combin_ids, String combin_version) {
		
		Map json_map = this.goodsCartService.add_goodsCart(request, response, id, count, gsp, suit_gsp, buy_type, combin_ids, CommUtil.null2Long(combin_version));
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	
	/**
	 * 移动端用户查看购物车
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_cart1.htm")
	public ModelAndView goods_cart1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/goods_cart1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);
		Date date = new Date();
		if (carts.size() > 0) {
			Set<Long> set = new HashSet<Long>();//保存达到满赠条件的购物车对应的满赠活动的id
			List<GoodsCart> native_goods = new ArrayList<GoodsCart>();
			Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
			Map<Long, String> erString = new HashMap<Long, String>();
			for (GoodsCart cart : carts) {
				if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
					BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
					if (bg.getBeginTime().before(date)) {
						set.add(cart.getGoods().getBuyGift_id());
					} else {
						native_goods.add(cart);
					}
				} else if (cart.getGoods().getEnough_reduce() == 1) { // 满就减
					String er_id = cart.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
					if (er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er.getId())) {
							ermap.get(er.getId()).add(cart);
						} else {
							List<GoodsCart> list = new ArrayList<GoodsCart>();
							list.add(cart);
							ermap.put(er.getId(), list);
							Map map = (Map) Json.fromJson(er.getEr_json());
							double k = 0;
							String str = "";
							for (Object key : map.keySet()) {
								if (k == 0) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
								}
								if (Double.parseDouble(key.toString()) < k) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
								}
							}
							erString.put(er.getId(), str);
						}
					} else {
						native_goods.add(cart);
					}
				} else {
					native_goods.add(cart);
				}
			}
			mv.addObject("erString", erString);
			mv.addObject("er_goods", ermap); // 满就减
			Map<String, List<GoodsCart>> separate_carts = this.separateCombin(native_goods); // 传入没有分离组合活动商品的购物车
			mv.addObject("cart", (List<GoodsCart>) separate_carts.get("normal")); // 无活动的商品购物车
			mv.addObject("combin_carts", (List<GoodsCart>) separate_carts.get("combin")); // 组合套装商品购物车
			// 将有活动的商品分组(满就送)
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
				mv.addObject("ac_goods", map);
			}
		} else {
			mv = new JModelAndView("wap/goods_cart1_none.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("combinTools", combinTools);
		return mv;
	}

	@RequestMapping("/wap/goods_cart_del.htm")
	public String goods_cart_del(HttpServletRequest request, HttpServletResponse response, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
				gc.getGsps().clear();
				this.goodsCartService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:wap/goods_cart0.htm";
	}

	/**
	 * 购物车列表相关调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/wap/goods_count_adjust.htm")
	public void goods_count_adjust(HttpServletRequest request, HttpServletResponse response, String gc_id, String count,
			String gcs) {
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);
		String code = "100"; // 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00; // 单位GoodsCart总价钱
		double total_price = 0.00; // 购物车总价钱
		Goods goods = null;
		if (!StringUtils.isNullOrEmpty(gc_id)) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
			if (gc.getId().toString().equals(gc_id)) {
				goods = gc.getGoods();
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
					}
				} else {
					if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
						if (gc.getId().toString().equals(gc_id)) {
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gc.getPrice(), count);
						}
					} else {
						code = "200";
					}
				}
			}
		}
		total_price = this.calCartPrice(carts, gcs);
		Map map = new HashMap();
		map.put("count", count);
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param orderType 值为s+秒杀商品id，如s51
	 * @return
	 */
	@SecurityMapping(title = "确认秒杀商品购物车", value = "/wap/seckill_goods_cart2.htm*", rtype = "buyer", rname = "移动端秒杀购物流程1", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/seckill_goods_cart2.htm")
	public ModelAndView seckill_goods_cart2(HttpServletRequest request, HttpServletResponse response, String orderType) {
		ModelAndView mv = new JModelAndView("wap/goods_cart2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		//未登录状态下，购买秒杀商品遭单点登录拦截，参数丢失，需求cookie中获得参数
//		if (null == orderType || !orderType.matches("s\\d+")) {
//			Cookie[] cookies = request.getCookies();
//			if(cookies != null){
//				for(Cookie cookie: cookies){
//					if ("orderType".equals(cookie.getName())) {
//						orderType = CommUtil.null2String(cookie.getValue());
//						cookie.setValue("");
//						break;
//					}
//				}
//			}
//		}
		
		if (null == orderType || !orderType.matches("s\\d+")) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误，请重新秒杀");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			return mv;
		}
		
		// 未登录状态下，购买秒杀商品，购物车中userid为空，查出结果集为空，需将cookie购物车转换为user购物车
		this.goodsCartService.cart_calc(request, response);
		
		//查询秒杀商品所在的购物车
		Goods goods = goodsService.getObjById(Long.parseLong(orderType.substring(1)));//秒杀商品
		Map cart_map = new HashMap();
		cart_map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		cart_map.put("cart_status", 0);
		cart_map.put("goods_id", goods.getId());
		List<GoodsCart> carts = goodsCartService
				.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.goods.id=:goods_id",
						cart_map, 0, 1);
		String gcs = carts.get(0).getId().toString();
		
		return goods_cart2( request,  response,  gcs,  null, null);
	}

	/**
	 * 移动端用户对选定购物车进行结算
	 * @param request
	 * @param response
	 * @param gcs
	 * @param giftids
	 * @param addr_id
	 * @return
	 */
	@SecurityMapping(title = "确认购物车", value = "/wap/goods_cart2.htm*", rtype = "buyer", rname = "移动端购物流程1", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/goods_cart2.htm")
	public ModelAndView goods_cart2(HttpServletRequest request, HttpServletResponse response, String gcs, String giftids, Long addr_id) {
		ModelAndView mv = new JModelAndView("wap/goods_cart2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if(StringUtils.isNullOrEmpty(gcs)){
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误，请重新进入购物车");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		
		try {
			Map map = this.orderFormService.goodsCart2(request, response, gcs, giftids, addr_id, true);
			mv.addObject("order_goods_price", map.get("all"));
			mv.addObject("order_er_price", map.get("reduce"));
			int selfPickupEnabled = CommUtil.null2Int(map.get("selfPickupEnabled"));
			mv.addObject("selfPickupEnabled", selfPickupEnabled);
			if (selfPickupEnabled == 1) {
				mv.addObject("areas",
						this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1));
			}
			List<Address> addrs = (List<Address>) map.get("addrs");
			mv.addObject("addrs", addrs);
			if(addrs != null && addrs.size() > 0) {
				mv.addObject("addr_id", addrs.get(0).getId());//默认选中的收货地址
			} else {
				String returnUrl = URLEncoder.encode(CommUtil.getWebPath(request, configService.getSysConfig()) + "/wap/goods_cart2.htm?gcs=" + gcs + "&giftids=" + giftids);
				mv.addObject("returnUrl", returnUrl);
			}
			String cart_session = CommUtil.randomString(32);
			mv.addObject("cart_session", cart_session);
			// 比较当日时间段
			Calendar cal = Calendar.getInstance();
			mv.addObject(
					"before_time1",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 15:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			mv.addObject(
					"before_time2",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 19:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			mv.addObject(
					"before_time3",
					cal.getTime().before(
							CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 22:00:00", new Date()),
									"yyyy-MM-dd HH:mm:ss")));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			mv.addObject("user", user);
			mv.addObject("days", map.get("days"));
			mv.addObject("day_list", map.get("day_list"));
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("cartTools", cartTools);
			mv.addObject("transportTools", transportTools);
			mv.addObject("userTools", userTools);
			mv.addObject("map_list", map.get("map_list"));
			mv.addObject("gcs", gcs);
			mv.addObject("goods_cod", map.get("goods_cod"));
			mv.addObject("tax_invoice", map.get("tax_invoice"));
			mv.addObject("giftids", map.get("validGiftIds"));
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("needId", map.get("needId"));

			HttpSession session = request.getSession(true);
			session.setAttribute("hxgoods", map.get("hxgoods"));
			session.setAttribute("cart_session", cart_session);
			session.setAttribute("selfPickupEnabled", selfPickupEnabled);
			// 保存计算出来的用户已选择的购物车、相关的店铺、赠品，用于在goods_cart3中使用，以避免重复计算和校验
			session.setAttribute("cartIds", gcs);
			session.setAttribute("storeIds", map.get("store_list"));
			session.setAttribute("giftIds", map.get("validGiftIds"));
			session.setAttribute("giftCartMapping", map.get("giftCart"));
			session.setAttribute("storeCartMapping", map.get("storeCart"));
			session.setAttribute("needId", map.get("needId"));
			session.setAttribute("storeTransportsMapping", map.get("storeTransports"));// 每个店铺当前计算出来的运费结果
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", e.getMessage());
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "订单地址管理", value = "/wap/choose_address.htm*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/choose_address.htm")
	public ModelAndView choose_address(HttpServletRequest request, HttpServletResponse response, String addr_id, Long needId) {
		ModelAndView mv = new JModelAndView("wap/goods_cart2_address.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addrs = this.addressService.query(
				"select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc", params, -1, -1);
		mv.addObject("addrs", addrs);
		mv.addObject("areaViewTools", areaViewTools);
		String cart_session = (String) request.getSession(true).getAttribute("cart_session");
		mv.addObject("cart_session", cart_session);
		mv.addObject("addr_id", addr_id);		
		mv.addObject("needId", needId);
		
		String gcs = (String)request.getParameter("gcs");
		mv.addObject("gcs", gcs);
		String giftids = (String)request.getParameter("giftids");
		mv.addObject("giftids", giftids);
		String orderType = (String)request.getParameter("orderType");
		mv.addObject("orderType", orderType);
		
		String returnUrl = CommUtil.getWebPath(request, configService.getSysConfig()) + "/wap/choose_address.htm"
				+ "?addr_id=" + addr_id + "&needId=" + needId + "&gcs=" + gcs + "&giftids=" + giftids + "&orderType=" + orderType;
		mv.addObject("returnUrl", URLEncoder.encode(returnUrl));

		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/goods_cart0.htm")
	public ModelAndView goods_cart0(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/goods_cart0.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 提交订单
	 * @param request
	 * @param response
	 * @param cart_session
	 * @param store_id 店铺id，逗号隔开
	 * @param addr_id 收货地址id
	 * @param gcs 购物车id，逗号隔开
	 * @param delivery_time 配送时间
	 * @param delivery_type 配送方式，值为0（快递），1（自提点），2（到店自提）
	 * @param delivery_id 自提点id
	 * @param payType 付款方式，分为online（在线支付）,payafter（货到付款）
	 * @param gifts 赠品id，逗号隔开
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "确认购物车第3步", value = "/wap/goods_cart3.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/goods_cart3.htm")
	public ModelAndView goods_cart3(HttpServletRequest request, HttpServletResponse response, String cart_session,
			String store_id, Long addr_id, String gcs, String delivery_time, int delivery_type, String delivery_id,
			String payType, String gifts, String xmStoreId) {
		ModelAndView mv = new JModelAndView("wap/goods_cart3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
	
		String cart_session1 = (String) request.getSession(true).getAttribute("cart_session");
		if (!CommUtil.null2String(cart_session1).equals(cart_session) || StringUtils.isNullOrEmpty(gcs)) {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "订单已经失效");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/goods_cart1.htm");
		}
		
		try {
			User buyer = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			String[] gcIds = CommUtil.null2String(request.getSession(true).getAttribute("cartIds")).split(",");// 直接从上一步中取得用户已选择的购物车
			int needId = CommUtil.null2Int(request.getSession(true).getAttribute("needId"));
			Address addr = null;// 用户选择的收货地址。isSelfPickup为false且addr为null，则表示用户选择的是代收点
			DeliveryAddress deliveryAddr = null;// 自提点地址
			ShipAddress xmStore = null; // 发货的星美门店。到店自提时使用
			if ("2".equals(delivery_type)) {// 到店自提
				if (CommUtil.null2Int(request.getSession(true).getAttribute("selfPickupEnabled")) == 0) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "该笔订单不支持到店自提，请重新选择配送方式。");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/goods_cart1.htm");
					return mv;
 				}
				xmStore = this.shipAddressService.getObjById(CommUtil.null2Long(xmStoreId));
				if (xmStore == null) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "您选择了到店自提，请您选择要上门提货的门店。");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
					return mv;
				}
			} else {// 快递或快递自提点
				addr = this.addressService.getObjById(addr_id);
				if (addr == null) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "收货地址错误，请重新填写收货地址");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/goods_cart2.htm");
					return mv;
				}

				if (needId == 1 && StringUtils.isNullOrEmpty(addr.getCard())) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "收货地址错误，请填写身份证信息。");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/goods_cart2.htm");
					return mv;
				}

				if (CommUtil.null2Int(delivery_type) == 1) {// 自提点服务
					deliveryAddr = this.deliveryaddrService.getObjById(CommUtil.null2Long(delivery_id));
					if (deliveryAddr == null) {
						mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "请选择自提点。");
						mv.addObject("url", CommUtil.getURL(request) + "/wap/goods_cart2.htm");
						return mv;
					}
				}
			}
			String[] gift_ids = CommUtil.null2String(request.getSession(true).getAttribute("giftIds")).split(",");
			Map<Object, Map<Long, Set<Long>>> giftCartMapping = (Map<Object, Map<Long, Set<Long>>>) request
					.getSession(true).getAttribute("giftCartMapping");
			Map<Object, Map> hxGoods = (Map<Object, Map>) request.getSession(true).getAttribute("hxgoods");// 可能为null
			Map<Object, Map> transports = (Map<Object, Map>) request.getSession(true)
					.getAttribute("storeTransportsMapping");// 可能为null
			Set<Object> storeIds = (Set<Object>) request.getSession(true).getAttribute("storeIds");// 可能是Long类型的店铺id，也可能是String类型的"self"
			Map<Object, Set<Long>> storeCart = (Map<Object, Set<Long>>) request.getSession(true).getAttribute("storeCartMapping");
			
			Map map = this.orderFormService.goodsCart3(request,buyer,gift_ids, addr, gcIds, delivery_time, deliveryAddr, payType,  xmStore, true,
					giftCartMapping,hxGoods,transports,storeIds,storeCart);
			double all_of_price = (double) map.get("all_of_price");
			OrderForm main_order = (OrderForm) map.get("main_order");
			
			if ("payafter".equals(payType)) { // 使用货到付款
				mv = new JModelAndView("wap/payafter_pay.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(true).setAttribute("pay_session", pay_session);
				mv.addObject("pay_session", pay_session);
			}else{
				//不是货到付款，走支付中心在线支付
				String mark = PropertyUtil.getProperty("wap_pay_center_mark");
				if("1".equals(mark)){
					String url = PropertyUtil.getProperty("wap_return_url_order");
					String returnUrl = CommUtil.getURL(request) + url; //回调业务系统地址
					mv = (ModelAndView) this.orderFormService.orderPay(request, response, buyer, main_order, ChannelEnum.WAP.toString(), OrderTypeEnum.SHOPPING.getIndex(), returnUrl);
				}
			}
			mv.addObject("user", buyer);
			mv.addObject("all_of_price", all_of_price);
			mv.addObject("paymentTools", paymentTools);
			mv.addObject("order", main_order); // 将主订单信息封装到前台视图中
			
			List<Payment> payments = new ArrayList<Payment>();
			Map params = new HashMap();
			params.put("mark", "wx_pay");
			payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
			Payment payment = null;
			if (payments.size() > 0) {
				payment = payments.get(0);
				mv.addObject("appid", payment.getWx_appid());
			}
			
			try {
				// 在循环外，给买家只发送一次短信邮件
				if (main_order.getOrder_form() == 0) {
					this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
							buyer.getEmail(), null, CommUtil.null2String(main_order.getId()), main_order.getStore_id());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
							buyer.getMobile(), null, CommUtil.null2String(main_order.getId()), main_order.getStore_id());
				} else {
					this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
							buyer.getEmail(), null, CommUtil.null2String(main_order.getId()));
					this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
							buyer.getMobile(), null, CommUtil.null2String(main_order.getId()));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", e.getMessage());
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			return mv;
		} finally {
			HttpSession session = request.getSession(true);
			session.removeAttribute("cart_session"); // 删除订单提交唯一标示，用户不能进行第二次订单提交
			session.removeAttribute("selfPickupEnabled");
			session.removeAttribute("hxgoods");
			session.removeAttribute("cartIds");
			session.removeAttribute("storeIds");
			session.removeAttribute("giftIds");
			session.removeAttribute("giftCartMapping");
			session.removeAttribute("storeCartMapping");
			session.removeAttribute("needId");
			session.removeAttribute("storeTransportsMapping");
		}
		
		return mv;
	}

	@SecurityMapping(title = "订单加载自提点", value = "/wap/cart_delivery.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/cart_delivery.htm")
	public ModelAndView cart_delivery(HttpServletRequest request, HttpServletResponse response, String addr_id,
			String currentPage, String deliver_area_id) {
		ModelAndView mv = new JModelAndView("wap/cart_delivery.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map paras = new HashMap();
		StringBuffer query = new StringBuffer("select obj from DeliveryAddress obj where 1=1 ");
		if (!StringUtils.isNullOrEmpty(deliver_area_id)) {
			Area deliver_area = this.areaService.getObjById(CommUtil.null2Long(deliver_area_id));
			Set<Long> ids = this.genericIds(deliver_area);
			paras.put("ids", ids);
			query.append("and obj.da_area.id in(:ids) ");
			mv.addObject("deliver_area_id", deliver_area_id);
		} else {
			if (!StringUtils.isNullOrEmpty(addr_id)) {
				Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
				paras.put("da_area_id", addr.getArea().getParent().getId());
				query.append("and obj.da_area.parent.id=:da_area_id ");
				mv.addObject("area", addr.getArea().getParent());
			}
		}
		paras.put("da_status", 10);
		query.append("and obj.da_status=:da_status order by addTime desc");
		List<DeliveryAddress> objs = this.deliveryaddrService.query(query.toString(), paras, -1, -1);
		mv.addObject("objs", objs);
		return mv;
	}

	private String generic_day(int day) {
		String[] list = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return list[day - 1];
	}


	private double getGoodscartCommission(GoodsCart gc) {
		double commission_price = CommUtil.mul(gc.getGoods().getGc().getCommission_rate(),
				CommUtil.mul(gc.getPrice(), gc.getCount()));
		return commission_price;
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getOrderCommission(List<GoodsCart> gcs) {
		double commission_price = 0.00;
		for (GoodsCart gc : gcs) {
			commission_price = commission_price + this.getGoodscartCommission(gc);
		}
		return commission_price;
	}

	@SecurityMapping(title = "订单支付详情", value = "/wap/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/order_pay_view.htm")
	public ModelAndView order_pay_view(HttpServletRequest request, HttpServletResponse response, String id) {
		// TODO 我的订单点击付款功能
		ModelAndView mv = new JModelAndView("wap/order_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		
		if (of != null && of.getUser_id().equals(user.getId().toString())) {
			if (of.getOrder_status() == 10) {
				boolean exist = this.orderFormTools.order_goods_exist(id);
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", "wx_pay");
				payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
				Payment payment = null;
				if (payments.size() > 0) {
					payment = payments.get(0);
					mv.addObject("appid", payment.getWx_appid());
				}
				if (!exist) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "订单中商品已被删除，请重新下单");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
					return mv;
				}
				// 验证订单中商品库存是否充足
//				boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(id, of);
//				if (inventory_very) {
					mv.addObject("of", of);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("orderFormTools", this.orderFormTools);
					mv.addObject("url", CommUtil.getURL(request));
//				} else {
//					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
//							this.userConfigService.getUserConfig(), 1, request, response);
//					mv.addObject("op_title", "订单中商品库存不足，请重新下单");
//					mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
//				}
				mv.addObject("user",user);
				
				String mark = PropertyUtil.getProperty("wap_pay_center_mark");
				if("1".equals(mark)){
					String url = PropertyUtil.getProperty("wap_return_url_order");
					String returnUrl = CommUtil.getURL(request) + url; //回调业务系统地址
					mv = (ModelAndView) this.orderFormService.orderPay(request, response, user, of, ChannelEnum.WAP.toString(), OrderTypeEnum.SHOPPING.getIndex(), returnUrl);
				}
			} else if (of.getOrder_status() < 10) {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "该订单已经取消");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "该订单已付款");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list");
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "该订单已失效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		
		return mv;
	}

	@SecurityMapping(title = "移动端订单支付详情", value = "/wap/order_pay.htm*", rtype = "buyer", rname = "移动端订单支付详情", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay.htm")
	public ModelAndView order_pay(HttpServletRequest request, HttpServletResponse response, String payType, String order_id,
			String pay_pwd) {
		ModelAndView mv = null;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if ("wx_pay".equals(payType)) {
			String type = "goods";
			if (order.getOrder_cat() == 2) {
				type = "group"; // 订单属性为生活类团购
			}
			try {
				response.sendRedirect(request.getContextPath() + "/weixin/pay/wx_pay.htm?id=" + order_id + "&showwxpaytitle=1&type=" + type);
			} catch (IOException e) {
				logger.error(e);
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
			}
		}
		if (order != null && order.getOrder_status() == 10) {
			if ("".equals(CommUtil.null2String(payType))) {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
			} else {
				boolean exist = this.orderFormTools.order_goods_exist(order_id);
				if (!exist) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "订单中商品已被删除，请重新下单");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
					return mv;
				}
				// 验证订单中商品库存是否充足
//				boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(order_id, order);
//				if (inventory_very) {
					// 给订单添加支付方式 ,
					List<Payment> payments = new ArrayList<Payment>();
					Map params = new HashMap();
					params.put("mark", payType);
					payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
					order.setPayment(payments.get(0));
					order.setPayType("online");
					this.orderFormService.update(order);
					List<Map> cmaps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
					for (Map child_map : cmaps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
								.get("order_id")));
						if (child_order.getOrder_status() == 10) {
							child_order.setPayment(payments.get(0));
							child_order.setPayType("online");
							this.orderFormService.update(child_order);
						}
					}
					if ("balance".equals(payType)) { // 使用预存款支付
						// 校验支付密码
						User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
						ResultDTO resultDTO = userManageConnector.payPwdCheck(user.getCustId(), Md5Encrypt.md5(pay_pwd)
								.toLowerCase());
						if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
							mv = new JModelAndView("wap/balance_pay.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							double order_total_price = CommUtil.null2Double(order.getTotalPrice());
							if (!"".equals(CommUtil.null2String(order.getChild_order_detail()))) {
								List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
								for (Map map : maps) {
									OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map
											.get("order_id")));
									order_total_price = order_total_price
											+ CommUtil.null2Double(child_order.getTotalPrice());
								}
							}
							mv.addObject("order_total_price", order_total_price);
						} else {
							mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", resultDTO.getMsg());
							mv.addObject("url", CommUtil.getURL(request) + "/wap/order_pay_view.htm?id="+order.getId());
						}
					} else { // 使用在线支付
						mv = new JModelAndView("wap/line_pay.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("payType", payType);
						mv.addObject("url", CommUtil.getURL(request));
						mv.addObject("payTools", payTools);
						String type = "goods";
						if (order.getOrder_cat() == 2) {
							type = "group"; // 订单属性为生活类团购
						}
						mv.addObject("type", type);
						mv.addObject("payment_id", order.getPayment().getId());
					}
					mv.addObject("order", order);
					mv.addObject("order_id", order.getId());
//				} else {
//					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
//							this.userConfigService.getUserConfig(), 1, request, response);
//					mv.addObject("op_title", "订单中商品库存不足，请重新下单");
//					mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
//				}
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "参数错误，付款失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "移动端订单预付款支付", value = "/wap/order_pay_balance.htm*", rtype = "buyer", rname = "移动端预存款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay_balance.htm")
	public ModelAndView order_pay_balance(HttpServletRequest request, HttpServletResponse response, String order_id,
			String pay_msg) throws Exception {
		String retPage="wap/success.html";
		int typePage=1;
		if(CouponActivityComm.isCouponActivityTime()){
			typePage=2;
			retPage="redirect:/wap/jump_coupon_share.htm";
		}
		ModelAndView mv = new JModelAndView(retPage, configService.getSysConfig(),
				this.userConfigService.getUserConfig(), typePage, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null && order.getOrder_status() < 20) { // 订单不为空且订单状态为未付款才可以正常使用预存款付款
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
				return mv;
			}
			
			double order_total_price = CommUtil.null2Double(order.getTotalPrice());
			if (!"".equals(CommUtil.null2String(order.getChild_order_detail())) && order.getOrder_cat() != 2) {
				order_total_price = this.orderFormTools.query_order_price(CommUtil.null2String(order.getId()));
			}
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {// 验证用户余额是否大于订单总金额
				// 调用预存款支付进行会员消费支付start
				ResultDTO resultDTO = feeManageservice.payment(user,order,order_total_price);
				if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
					// 预存款日志
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_op_type("消费");
					log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(order.getTotalPrice())));
					log.setPd_log_info(order.getOrder_id() + "订单购物减少可用预存款");
					log.setPd_type("可用预存款");
					this.predepositLogService.save(log);
					
					//更新账户余额
					//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), order_total_price)));//余额都在CRM端保存和增减，故注释掉
					//this.userService.update(user);
					
					//更新主订单状态
//					order.setOut_order_id(order.getCrmOrderId()); //预存款支付成功后将crm订单号设置到外部订单号字段中
					order.setOut_order_id(order.getOrder_id()); //预存款支付成功后将crm订单号设置到外部订单号字段中
					order.setPay_msg(pay_msg);
					order.setOrder_status(20);
					Map params = new HashMap();
					params.put("mark", "balance");
					List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params,
							-1, -1);
					if (payments.size() > 0) {
						order.setPayment(payments.get(0));
						order.setPayTime(new Date());
					}
					this.orderFormService.update(order);
			
					// 记录主订单日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("预付款支付成功");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					
					//非生活类团购订单，更新子订单状态及发送提醒信息
					if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))
							&& order.getOrder_cat() != 2) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							//更新子订单状态
							OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
							child_order.setOrder_status(20);
							child_order.setOut_order_id(order.getOut_order_id());
							if (payments.size() > 0) {
								child_order.setPayment(payments.get(0));
								child_order.setPayTime(new Date());
							}
							this.orderFormService.update(child_order);
							
							// 记录子订单日志
//							OrderFormLog child_ofl = new OrderFormLog();
//							child_ofl.setAddTime(new Date());
//							child_ofl.setLog_info("预付款支付");
//							child_ofl.setLog_user(SecurityUserHolder.getCurrentUser());
//							child_ofl.setOf(child_order);
//							this.orderFormLogService.save(child_ofl);
							
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
							if (child_order.getOrder_form() == 0) {
								this.msgTools.sendEmailCharge(CommUtil.getURL(request),
										"email_toseller_balance_pay_ok_notify", store.getUser().getEmail(), null,
										CommUtil.null2String(child_order.getId()), child_order.getStore_id());
								this.msgTools.sendSmsCharge(CommUtil.getURL(request),
										"sms_toseller_balance_pay_ok_notify", store.getUser().getMobile(), null,
										CommUtil.null2String(child_order.getId()), child_order.getStore_id());
							}
						}
					}
					
					// 如果是生活类团购订单
					if (order.getOrder_cat() == 2) {
						//买家退货发货截止时间
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
						SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						order.setReturn_shipTime(CommUtil.formatDate(latertime));
						orderFormService.update(order);
						
						Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
						int count = CommUtil.null2Int(map.get("goods_count").toString());
						String goods_id = map.get("goods_id").toString();
						GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(goods_id));
						goods.setSelled_count(goods.getSelled_count() + CommUtil.null2Int(count));
						this.groupLifeGoodsService.update(goods);

						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
								+ File.separator + "grouplifegoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateLifeGoodsIndex(goods));
						
						List<String> code_list = new ArrayList(); // 存放团购消费码
						String codes = "";
						for (int i = 0;i < count;i++) {
							GroupInfo info = new GroupInfo();
							info.setAddTime(new Date());
							info.setLifeGoods(goods);
							info.setPayment(payments.get(0));
							info.setUser_id(user.getId());
							info.setUser_name(user.getUserName());
							info.setOrder_id(order.getId());
							info.setGroup_sn(user.getId() + CommUtil.formatTime("yyyyMMddHHmmss" + i, new Date()));
							Calendar ca2 = Calendar.getInstance();
							ca2.add(ca2.DATE, this.configService.getSysConfig().getGrouplife_order_return());
							SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String latertime2 = bartDateFormat2.format(ca2.getTime());
							info.setRefund_Time(CommUtil.formatDate(latertime2));
							this.groupInfoService.save(info);
							codes += info.getGroup_sn() + " ";
							code_list.add(info.getGroup_sn());
						}
						
						if (order.getOrder_form() == 0) { //商家商品订单
							Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
							store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
									store.getStore_sale_amount()))); // 店铺本次结算总销售金额
							// 团购消费码，没有佣金，店铺总佣金不变
							store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
									store.getStore_payoff_amount()))); // 店铺本次结算总佣金
							this.storeService.update(store);
							
							PayoffLog plog = new PayoffLog();
							plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
									+ store.getUser().getId());
							plog.setPl_info("团购码生成成功");
							plog.setAddTime(new Date());
							plog.setSeller(store.getUser());
							plog.setO_id(CommUtil.null2String(order.getId()));
							plog.setOrder_id(order.getOrder_id().toString());
							plog.setCommission_amount(BigDecimal.valueOf(CommUtil.null2Double("0.00"))); // 该订单总佣金费用
							// 将订单中group_info（{}）转换为List<Map>([{}])
							List<Map> Map_list = new ArrayList<Map>();
							Map group_map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
							Map_list.add(group_map);
							plog.setGoods_info(Json.toJson(Map_list, JsonFormat.compact()));
							plog.setOrder_total_price(order.getTotalPrice().subtract(order.getShip_price())); // 该订单总商品金额
							plog.setTotal_amount(this.orderFormService.getPayoffAmount(order)); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
							plog.setShip_price(order.getShip_price());//订单运费
							this.payoffLogService.save(plog);
						}
						
						// 增加系统总销售金额、消费码没有佣金，系统总佣金不变
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
								sc.getPayoff_all_sale())));
						this.configService.update(sc);
						
						// 发送系统站内信给买家
						String msg_content = "恭喜您成功购买团购" + map.get("goods_name") + ",团购消费码分别为：" + codes
								+ "您可以到用户中心-我的生活购中查看消费码的使用情况";
						Message tobuyer_msg = new Message();
						tobuyer_msg.setAddTime(new Date());
						tobuyer_msg.setStatus(0);
						tobuyer_msg.setType(0);
						tobuyer_msg.setContent(msg_content);
						tobuyer_msg.setFromUser(this.userService.getObjByProperty(null, "userName", "admin"));
						tobuyer_msg.setToUser(user);
						this.messageService.save(tobuyer_msg);
						
						// 付款成功，发送短信团购消费码
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_groupInfo_sms(request, order, user.getMobile(),
									"sms_tobuyer_online_ok_send_groupinfo", code_list, user.getId().toString(), goods
											.getUser().getId().toString());
						}
					}else {
						List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
						// 更新订单中组合套装商品信息
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
						for (Map map_combin : maps) {
							if (map_combin.get("combin_suit_info") != null) {
								Map suit_info = Json.fromJson(Map.class,
										CommUtil.null2String(map_combin.get("combin_suit_info")));
								int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
								List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
								for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
									for (Goods temp : goods_list) {
										if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
											Goods goods = this.goodsService.getObjById(CommUtil.null2Long(temp_goods
													.get("id")));
											goods.setGoods_salenum(goods.getGoods_salenum() + combin_count);
											this.goodsService.update(goods);
										}
									}
								}
							}
						}
						for (Goods goods : goods_list) {
							//商品购买数量
							int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
									CommUtil.null2String(goods.getId()));

							//如果该商品为团购商品则同步更新团购商品库存
							if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
								for (GroupGoods gg : goods.getGroup_goods_list()) {
									if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
										gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
										this.groupGoodsService.update(gg);
										
										String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp()
												+ "luence" + File.separator + "groupgoods";
										File file = new File(goods_lucene_path);
										if (!file.exists()) {
											CommUtil.createFolder(goods_lucene_path);
										}
										LuceneUtil lucene = LuceneUtil.instance();
										lucene.setIndex_path(goods_lucene_path);
										lucene.update(CommUtil.null2String(goods.getId()),
												luceneVoTools.updateGroupGoodsIndex(gg));
										break;
									}
								}
							}else if(!"".equals(CommUtil.null2String(order.getSeckill_info()))){ //增加秒杀商品销量
								Map map = Json.fromJson(Map.class, order.getSeckill_info());
								SeckillGoods seckillGoods = seckillGoodsService.getObjById(Long.valueOf(String.valueOf(map.get("seckill_goods_id"))));
								seckillGoods.setGg_selled_count(seckillGoods.getGg_selled_count()+goods_count);
								seckillGoodsService.update(seckillGoods);
							}
//							if("".equals(CommUtil.null2String(order.getSeckill_info()))){
								goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
								this.goodsService.update(goods);
								
								// 更新lucene索引
								String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp()
										+ "luence" + File.separator + "goods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
								
								//商品日志
								GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
								todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum() + goods_count);
								Map<String, Integer> logordermap = (Map<String, Integer>) Json.fromJson(todayGoodsLog
										.getGoods_order_type());
								String ordertype = order.getOrder_type();
								if (logordermap.containsKey(ordertype)) {
									logordermap.put(ordertype, logordermap.get(ordertype) + goods_count);
								} else {
									logordermap.put(ordertype, goods_count);
								}
								todayGoodsLog.setGoods_order_type(Json.toJson(logordermap, JsonFormat.compact()));
								Map<String, Integer> logspecmap = (Map<String, Integer>) Json.fromJson(todayGoodsLog
										.getGoods_sale_info());
								List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(
										CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
								String spectype = "";
								for (GoodsSpecProperty gsp : temp_gsp_list) {
									spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
								}
								if (logspecmap.containsKey(spectype)) {
									logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
								} else {
									logspecmap.put(spectype, goods_count);
								}
								todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));
								this.goodsLogService.update(todayGoodsLog);
//							}
						}
					}
					
					//发送提醒消息
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					if (order.getOrder_form() == 0) { //商家商品订单
						Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
						this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_balance_pay_ok_notify",
								store.getUser().getEmail(), null, CommUtil.null2String(order.getId()), order.getStore_id());
						this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_tobuyer_balance_pay_ok_notify",
								user.getEmail(), null, CommUtil.null2String(order.getId()), order.getStore_id());
						this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_balance_pay_ok_notify", store
								.getUser().getMobile(), null, CommUtil.null2String(order.getId()), order.getStore_id());
						this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_balance_pay_ok_notify",
								buyer.getMobile(), null, CommUtil.null2String(order.getId()), order.getStore_id());
					} else {
						this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_balance_pay_ok_notify",
								user.getEmail(), null, CommUtil.null2String(order.getId()));
						this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_balance_pay_ok_notify",
								buyer.getMobile(), null, CommUtil.null2String(order.getId()));
					}

					mv.addObject("op_title", "预付款支付成功");
					if (order.getOrder_cat() == 2) {
						mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/group_list.htm");
					} else {
						mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
					}
					
					// 判断如果不是生活类团购订单则发送订单同步请求
					if (order.getOrder_cat() != 2) {
						// TODO 预存款支付成功后发送订单同步事件
						this.synchronizeOrderPublisher.synchronizeOrder(order.getId());
					}
				} else {
					// 记录支付失败日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("预付款支付失败_"+resultDTO.getMsg());
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", resultDTO.getMsg());
					mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
				}
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "可用余额不足，支付失败");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "参数错误，支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "手机订单货到付款", value = "/wap/order_pay_payafter.htm*", rtype = "buyer", rname = "移动端货到付款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping("/wap/order_pay_payafter.htm")
	public ModelAndView order_pay_payafter(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("wap/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(true).getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
				return mv;
			}
			// 验证订单中商品库存是否充足
			boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(order_id, order);
			if (!inventory_very) {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
				return mv;
			}
			order.setPay_msg(pay_msg);
			order.setPay_msg(pay_msg);
			order.setPayTime(new Date());
			order.setPayType("payafter");
			order.setOrder_status(16);
			this.orderFormService.update(order);
			if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					child_order.setOrder_status(16);
					child_order.setPayType("payafter");
					child_order.setPayTime(new Date());
					child_order.setPay_msg(pay_msg);
					this.orderFormService.update(child_order);
					// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
					Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
					if (this.configService.getSysConfig().isSmsEnbale() && child_order.getOrder_form() == 0) {
						this.send_sms(request, child_order, store.getUser().getMobile(),
								"sms_toseller_payafter_pay_ok_notify");
					}
					if (this.configService.getSysConfig().isEmailEnable() && child_order.getOrder_form() == 0) {
						this.send_email(request, child_order, store.getUser().getEmail(),
								"email_toseller_payafter_pay_ok_notify");
					}
				}
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);
			order.getGoods_info();

			request.getSession(true).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/order_list.htm");
		}
		return mv;
	}

	/**
	 * 获得购物车总体商品价格
	 * 
	 * @param request
	 * @param response
	 */
	private double getcartsPrice(List<GoodsCart> carts) {
		double all_price = 0.0;
		for (GoodsCart gc : carts) {
			all_price = CommUtil.add(all_price, CommUtil.mul(gc.getCount(), gc.getPrice()));
		}
		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return CommUtil.null2Double(bd2);
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
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp) {
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
				if (!StringUtils.isNullOrEmpty(gsp)) {
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
		if (goods.getActivity_status() == 2 && SecurityUserHolder.getCurrentUser() != null) { // 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil.null2String(SecurityUserHolder.getCurrentUser()
					.getId()));
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
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * 
	 * @param request
	 * @param response
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		if ("".equals(CommUtil.null2String(gcs))) {
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
						double last_price = ermap.get(er_id);
						ermap.put(er_id, CommUtil.add(last_price, CommUtil.mul(gc.getCount(), gc.getPrice())));
					} else {
						ermap.put(er_id, CommUtil.mul(gc.getCount(), gc.getPrice()));
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						if (gc.getCart_type() != null && "combin".equals(gc.getCart_type()) && gc.getCombin_main() == 1) {
							Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
							all_price = CommUtil.add(all_price, map.get("suit_all_price"));
						} else {
							all_price = CommUtil.add(all_price, CommUtil.mul(gc.getCount(), gc.getPrice()));
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
	 * 根据商品规格获取价格
	 * 
	 * @param request
	 * @param response
	 */
	private String generGspgoodsPrice(String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		User user = SecurityUserHolder.getCurrentUser();
		if (goods.getActivity_status() == 2) {
			if (user != null) {
				Map map = this.activityTools.getActivityGoodsInfo(CommUtil.null2String(goods.getId()), CommUtil.null2String(user.getId()), null);
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

	

	private void send_email(HttpServletRequest request, OrderForm order, String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = CommUtil.getServerRealPathFromRequest(request) + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false),
					"UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, CommUtil.getServerRealPathFromRequest(request) + "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
			context.put("buyer", buyer);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
				context.put("seller", store.getUser());
			}
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			String replace_content = writer.toString();
			this.msgTools.sendEmail(email, subject, replace_content);
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
				context.setVariable("seller", store.getUser());
			}
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
			String content = ex.getValue(context, String.class);
			try {
				this.msgTools.sendSMS(mobile, content);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void send_groupInfo_sms(HttpServletRequest request, OrderForm order, String mobile, String mark,
			List<String> codes, String buyer_id, String seller_id) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append(codes.get(i) + ",");
		}
		String code = sb.toString();
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("buyer", this.userService.getObjById(CommUtil.null2Long(buyer_id)));
			context.setVariable("seller", this.userService.getObjById(CommUtil.null2Long(seller_id)));
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			Map map = Json.fromJson(Map.class, order.getGroup_info());
			context.setVariable("group_info", map.get("goods_name"));
			context.setVariable("code", code);
			Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
			String content = ex.getValue(context, String.class);
			try {
				this.msgTools.sendSMS(mobile, content);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
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

	private Set<Long> genericIds(Area area) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(area.getId());
		for (Area child : area.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@RequestMapping("/wap/f_code_cart.htm")
	@SecurityMapping(title = "wap端F码购物第一步", value = "/wap/f_code_cart.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	public ModelAndView f_code_cart(HttpServletRequest request, HttpServletResponse response, String goods_id, String gsp) {
		ModelAndView mv = new JModelAndView("wap/f_code_cart.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getF_sale_type() == 0) {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "该商品不需要F码购买");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			} else {
				if ("".equals(CommUtil.null2String(gsp))) {
					gsp = this.generic_default_gsp(goods);
				}
				int goods_inventory = goods_inventory = CommUtil
						.null2Int(this.generic_default_info(goods, gsp).get("count")); // 计算该规格商品的库存量
				if (goods_inventory > 0) {
					String[] gsp_ids = CommUtil.null2String(gsp).split(",");
					String spec_info = "";
					List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
					for (String gsp_id : gsp_ids) {
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil
								.null2Long(gsp_id));
						boolean add = false;
						for (GoodsSpecProperty temp_gsp : goods.getGoods_specs()) { // 检查传入的规格信息是否是该商品对应的规格信息,杜绝用户私自修改传递参数
							if (temp_gsp.getId().equals(spec_property.getId())) {
								add = true;
							}
						}
						for (GoodsSpecProperty temp_gsp : specs) {
							if (temp_gsp.getSpec().getId().equals(spec_property.getSpec().getId())) {
								add = false;
							}
						}
						if (add)
							specs.add(spec_property);
					}
					if (this.goodsViewTools.generic_spec(goods_id).size() == specs.size()) { // 这里判断传入的规格数量和商品本身具有的是否一致,杜绝用户私自修改传递参数
						for (GoodsSpecProperty spec : specs) {
							spec_info = spec.getSpec().getName() + ":" + spec.getValue() + " " + spec_info;
						}
						String price = this.generGspgoodsPrice(gsp, goods_id);
						mv.addObject("spec_info", spec_info);
						mv.addObject("price", price);
						mv.addObject("obj", goods);
						mv.addObject("gsp", gsp);
						mv.addObject("goodsViewTools", this.goodsViewTools);
					} else {
						mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "规格参数错误");
						mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
					}
				} else {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "商品默认规格无库存，请选择其他规格购买");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/goods.htm?id=" + goods.getId());
				}

			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	/**
	 * 验证F码信息，并返回到页面中
	 * 
	 * @param request
	 * @param response
	 * @param f_code
	 * @param goods_id
	 */
	@SecurityMapping(title = "wap端F码验证", value = "/wap/f_code_validate.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/f_code_validate.htm")
	public void f_code_validate(HttpServletRequest request, HttpServletResponse response, String f_code, String goods_id) {
		boolean ret = false;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
		for (Map map : list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code) && CommUtil.null2Int(map.get("status")) == 0) { // 存在该F码且是未使用的则验证成功
				ret = true;
				break;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "wap端F码完成验证进入订单提交", value = "/wap/add_f_code_goods_cart.htm*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping("/wap/add_f_code_goods_cart.htm")
	public void add_f_code_goods_cart(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String f_code, String gsp) {
		boolean ret = false;
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List<Map> f_code_list = Json.fromJson(List.class, goods.getGoods_f_code());
		for (Map map : f_code_list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code) && CommUtil.null2Int(map.get("status")) == 0) { // 存在该F码且是未使用的则验证成功
				ret = true;
			}
		}
		if (ret) {
			List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户整体购物车
			List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>(); // 未提交的用户cookie购物车
			List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
			Map cart_map = new HashMap();
			User user = userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
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
				String price = this.generGspgoodsPrice(gsp, goods_id);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				String spec_info = "";
				for (String gsp_id : gsp_ids) {
					GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gsp_id));
					obj.getGsps().add(spec_property);
					if (spec_property != null) {
						spec_info = spec_property.getSpec().getName() + ":" + spec_property.getValue() + " " + spec_info;
					}
				}
				obj.setUser(user);
				obj.setSpec_info(spec_info);
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
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
