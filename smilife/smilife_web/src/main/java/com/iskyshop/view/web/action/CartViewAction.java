package com.iskyshop.view.web.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AddressUtil;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.XMLUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
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
import com.iskyshop.foundation.domain.query.DeliveryAddressQueryObject;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
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
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.buyer.tools.CartTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * <p>
 * Title: CartViewAction.java
 * </p>
 * 
 * <p>
 * Description:购物控制器,包括购物车所有操作及订单相关操作。主要包含：购物三个主要流程、F码购物、添加商品到购物车、从购物车移除商品、 购物地址处理、各种付款方式付款等等
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
 * @author erikzhang、hezeng
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class CartViewAction {
	private static Logger logger = Logger.getLogger(CartViewAction.class);
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
	private ICombinPlanService combinplanService;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private UserManageConnector userManageConnector;
	@Autowired
	private IFeeManageservice feeManageservice;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private GoodsTools goodsTools;
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

	@RequestMapping("/cart_menu_detail.htm")
	public ModelAndView cart_menu_detail(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("cart_menu_detail.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);
		if (carts.size() > 0) {
			mv.addObject("total_price", orderFormTools.calCartPrice(carts, ""));
			mv.addObject("carts", carts);
		}
		return mv;
	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            添加到购物车的商品id
	 * @param count
	 *            添加到购物车的商品数量
	 * @param gsp
	 *            商品的属性值，这里传递id值，如12,1,21
	 * @param buy_type
	 *            购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids
	 *            组合搭配中配件id
	 * @param combin_version
	 *            组合套装中套装版本
	 */
	@RequestMapping("/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, Long id, Integer count, String gsp,
			String buy_type, String combin_ids, Long combin_version) {
		Map json_map = this.goodsCartService.add_goodsCart(request, response, id, count, gsp, null, buy_type, combin_ids,
				combin_version);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 从购物车移除商品
	 * 
	 * @param request
	 * @param response
	 * @param ids
	 *            要删除的购物车的id集合
	 */
	@RequestMapping("/remove_goods_cart.htm")
	public void remove_goods_cart(HttpServletRequest request, HttpServletResponse response, String ids) {
		List<String> list_ids = new ArrayList<String>();
		if (!StringUtils.isNullOrEmpty(ids)) {
			String[] cart_ids = ids.split(",");
			for (String id : cart_ids) {
				if (!StringUtils.isNullOrEmpty(id)) {
					list_ids.add(id);
				}
			}
		}
		String code = this.goodsCartService.remove_carts(list_ids);

		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);
		Double total_price = 0.00;
		total_price = orderFormTools.calCartPrice(carts, "");
		Map map = new HashMap();
		map.put("total_price", BigDecimal.valueOf(total_price));
		map.put("code", code);
		map.put("count", carts.size());
		map.put("ids", list_ids);
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

	@RequestMapping("/goods_adjust.htm")
	public void goods_adjust(HttpServletRequest request, HttpServletResponse response, String gcs, String counts) {
		List codes = new ArrayList();
		List gc_ids = new ArrayList();
		Map map = new HashMap();
		String[] gcsArr = gcs.split(",");
		String[] countArr = counts.split(",");
		for (int i = 0; i < gcsArr.length; i++) {
			String gc_id = gcsArr[i];
			String count = countArr[i];
			List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);// 获取当前用户的所有购物车(包括无效的)
			String code = "100"; // 100表示成功，200表示库存不足
			String cart_type = ""; // 判断是否为组合销售
			Goods goods = null;
			int temp_count = CommUtil.null2Int(count);
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
			if (gc != null) {
				if (CommUtil.null2String(count).length() <= 9) {
					if (gc.getId().toString().equals(gc_id)) {
						cart_type = CommUtil.null2String(gc.getCart_type());
						goods = gc.getGoods();
						if (goods.getSeckill_buy() == 2 || goods.getSeckill_buy() == 4) {
							gc.setCount(1);
							this.goodsCartService.update(gc);
							continue;
						}
						if ("".equals(cart_type)) { // 普通商品的处理
							if (goods.getGroup_buy() == 2) { // 团购商品处理
								GroupGoods gg = new GroupGoods();
								for (GroupGoods gg1 : goods.getGroup_goods_list()) {
									if (gg1.getGg_goods().getId().equals(goods.getId())) {
										gg = gg1;
										break;
									}
								}
								if (gg.getGg_count() < CommUtil.null2Int(count)) {
									code = "200";
								}
							} else {
								String gsp = "";
								for (GoodsSpecProperty gs : gc.getGsps()) {
									gsp = gs.getId() + "," + gsp;
								}
								int inventory = goods.getGoods_inventory();
								if (("spec").equals(goods.getInventory_type())) {
									List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
									String[] gspIds = gsp.split(",");
									for (Map temp : list) {
										String[] tempIds = CommUtil.null2String(temp.get("id")).split("_");
										Arrays.sort(gspIds);
										Arrays.sort(tempIds);
										if (Arrays.equals(gspIds, tempIds)) {
											inventory = CommUtil.null2Int(temp.get("count"));
										}
									}
								}
								if (inventory < CommUtil.null2Int(count) || CommUtil.null2String(count).length() > 9
										|| gc.getGoods().getGroup_buy() == 2) {
									code = "200";
								}
							}
						}
						if (!"combin".equals(cart_type) || gc.getCombin_main() != 1) { // 组合销售的处理
							if (goods.getGoods_inventory() < CommUtil.null2Int(count)) {
								code = "200";
							}
						}
					}

				} else {
					code = "200";
				}
				if ("0".equals(count)) {
					code = "200";
				}
				codes.add(code);
				gc_ids.add(gc_id);
			}
		}
		map.put("codes", codes);
		map.put("gc_ids", gc_ids);
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
	 * 调整数据库中gc_id对应的购物车中的商品数量、规格等
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 *            对应复选框被用户当前click的购物车id
	 * @param count
	 *            对应gc_id购物车中的商品数量（为用户期望的值，即对应购物车中将要被设置的数量值）。用户可通过"+"或"-"或直接输入数字来设定购买的当前商品的数量
	 * @param gcs
	 *            当前被用户勾选了的所有购物车的ID（若用户当前的操作是勾选gc_id对应的购物车，则gcs中包含gc_id。若用户的操作是取消勾选或操作原本就未被勾选的购物车中的数量，则gcs中不包含gc_id）
	 * @param gift_id
	 *            购物车中对应复选框被当前用户click的GoodsCart所参加的满就送活动id
	 */
	@RequestMapping("/goods_count_adjust.htm")
	public void goods_count_adjust(HttpServletRequest request, HttpServletResponse response, String gc_id, String count,
			String gcs, String gift_id) {
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);// 获取当前用户的所有购物车(包括无效的)
		Map map = new HashMap();
		String code = "100"; // 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00; // 单个GoodsCart的总价钱
		double total_price = 0.00; // 购物车总价钱
		String cart_type = ""; // 判断是否为组合销售
		Goods goods = null;
		GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
		if (gc != null) {
			if (CommUtil.null2String(count).length() <= 9) {
				cart_type = CommUtil.null2String(gc.getCart_type());
				goods = gc.getGoods();

				if ("".equals(cart_type)) { // 普通商品的处理
					String gsp = "";
					for (GoodsSpecProperty gs : gc.getGsps()) {
						gsp = gs.getId() + "," + gsp;
					}
					int inventory = goods.getGoods_inventory();
					if (("spec").equals(goods.getInventory_type())) {
						List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
						String[] gspIds = gsp.split(",");
						for (Map temp : list) {
							String[] tempIds = CommUtil.null2String(temp.get("id")).split("_");
							Arrays.sort(gspIds);
							Arrays.sort(tempIds);
							if (Arrays.equals(gspIds, tempIds)) {
								inventory = CommUtil.null2Int(temp.get("count"));
							}
						}
					}
					if (inventory >= CommUtil.null2Int(count) && CommUtil.null2String(count).length() <= 9
							&& gc.getGoods().getGroup_buy() != 2) {
						if (gc.getId().toString().equals(gc_id)) {
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gc.getPrice(), count);
						}
					} else {
						if (inventory == 0) {
							gc.setCount(0);
							this.goodsCartService.update(gc);
						}
						code = "200";
					}
				}
				if ("group".equals(cart_type) && goods.getGroup_buy() == 2) {// 团购商品处理
					GroupGoods gg = new GroupGoods();
					for (GroupGoods gg1 : goods.getGroup_goods_list()) {
						if (gg1.getGg_goods().getId().equals(goods.getId()) && gg1.getGg_status() == 1) {// modify by liz
																											// 商品可重复参加同一团购，需要增加审核通过的过滤条件
							gg = gg1;
							break;
						}
					}
					if (gg.getGg_count() >= CommUtil.null2Int(count)) {
						gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(gg.getGg_price())));
						gc_price = CommUtil.mul(gg.getGg_price(), count);
						gc.setCount(CommUtil.null2Int(count));
						this.goodsCartService.update(gc);
					} else {
						if (gg.getGg_count() == 0) {
							gc.setCount(0);
							this.goodsCartService.update(gc);
						}
						code = "300";
					}
				}

				if ("seckill".equals(cart_type) && goods.getSeckill_buy() == 2) {// 秒杀商品处理
					Map params2 = new HashMap();
					params2.put("goods_id", goods.getId());

					String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=2";
					SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params2, 0, 1).get(0);

					if (seckillGoods.getGg_count() >= CommUtil.null2Int(count)) {
						gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(seckillGoods.getGg_price())));
						gc_price = CommUtil.mul(seckillGoods.getGg_price(), count);
						gc.setCount(CommUtil.null2Int(count));
						this.goodsCartService.update(gc);
					} else {
						if (seckillGoods.getGg_count() == 0) {
							gc.setCount(0);
							this.goodsCartService.update(gc);
						}
						code = "300";
					}
				}

				if ("combin".equals(cart_type) && gc.getCombin_main() == 1) { // 组合销售的处理
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
						if (goods.getGoods_inventory() == 0) {
							gc.setCount(0);
							this.goodsCartService.update(gc);
						}
						code = "200";
					}
				}
				// 判断出是否满足满就送条件
				if (gift_id != null) {
					BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(gift_id));// 获取当前购物车中的商品所参加的满就送活动对象
					if (bg != null) {
						Set<Long> bg_ids = new HashSet<Long>();// 保存当前被用户选中的所有购物车所关联的所有满就送活动的id的集合
						bg_ids.add(bg.getId());
						List<GoodsCart> g_carts = new ArrayList<GoodsCart>();// 保存当前被用户选中的所有购物车中参加了满就送活动的购物车
						if ("".equals(CommUtil.null2String(gcs))) {// 购物车页面似乎永远不会传一个空串的gcs上来吧？？？？
							for (GoodsCart gCart : carts) {// 遍历用户的所有购物车，而不是用户已勾选的购物车
								if (gCart.getGoods().getOrder_enough_give_status() == 1
										&& gCart.getGoods().getBuyGift_id() != null) {
									bg_ids.add(gCart.getGoods().getBuyGift_id());
								}
							}
							g_carts = carts;
						} else {
							String[] gc_ids = gcs.split(",");// 用户当前选中的所有购物车的id
							for (String g_id : gc_ids) {
								GoodsCart goodsCart = this.goodsCartService.getObjById(CommUtil.null2Long(g_id));
								if (goodsCart != null && goodsCart.getGoods().getOrder_enough_give_status() == 1
										&& goodsCart.getGoods().getBuyGift_id() != null) {// 判断当前购物车里的商品是否参加了满就送活动
									bg_ids.add(goodsCart.getGoods().getBuyGift_id());
									g_carts.add(goodsCart);
								}
							}
						}
						Map<Long, List<GoodsCart>> gc_map = new HashMap<Long, List<GoodsCart>>();// key：为满就送id，value:当前被用户选中的所有购物车中参加了key所对应活动的所有购物车
						for (Long id : bg_ids) {
							gc_map.put(id, new ArrayList<GoodsCart>());
						}
						for (GoodsCart cart : g_carts) {// 按满就送活动id对参加了满就送活动的所有购物车进行分类
							if (cart.getGoods().getOrder_enough_give_status() == 1
									&& cart.getGoods().getBuyGift_id() != null) {
								for (Map.Entry<Long, List<GoodsCart>> entry : gc_map.entrySet()) {
									if (cart.getGoods().getBuyGift_id().equals(entry.getKey())) {
										entry.getValue().add(cart);
									}
								}
							}
						}
						List<String> enough_bg_ids = new ArrayList<String>();// 计算出购物车价钱是否满足对应满就送
						for (Map.Entry<Long, List<GoodsCart>> entry : gc_map.entrySet()) {
							BuyGift buyGift = this.buyGiftService.getObjById(entry.getKey());
							List<GoodsCart> arrs = entry.getValue();
							BigDecimal bd = new BigDecimal("0.00");
							for (GoodsCart arr : arrs) {
								bd = bd.add(BigDecimal.valueOf(CommUtil.mul(arr.getPrice(), arr.getCount())));
							}
							if (bd.compareTo(buyGift.getCondition_amount()) >= 0) {
								enough_bg_ids.add(buyGift.getId().toString());
							}
						}
						map.put("bg_ids", enough_bg_ids);
					}
				}
			} else {
				code = "200";
			}
			map.put("count", gc.getCount());
		}
		total_price = orderFormTools.calCartPrice(carts, gcs);
		Map price_map = this.orderFormTools.calEnoughReducePrice(carts, gcs);
		Map<Long, String> erMap = (Map<Long, String>) price_map.get("erString");
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("code", code);
		map.put("enough_reduce_price", CommUtil.formatMoney(price_map.get("reduce")));
		map.put("before", CommUtil.formatMoney(price_map.get("all")));
		for (long k : erMap.keySet()) {
			map.put("erString" + k, erMap.get(k));
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

	@RequestMapping("/combin_carts_detail.htm")
	public void combin_carts_detail(HttpServletRequest request, HttpServletResponse response, String id) {
		int code = -100; // -100错误，100成功
		Map json_map = new HashMap();
		List<Map> map_list = new ArrayList<Map>();
		GoodsCart cart = this.goodsCartService.getObjById(CommUtil.null2Long(id));
		if (cart != null) {
			if (cart.getCart_type() != null && "combin".equals(cart.getCart_type()) && cart.getCombin_main() == 1) {
				String cart_ids[] = cart.getCombin_suit_ids().split(",");
				for (String cart_id : cart_ids) {
					if (!"".equals(cart_id) && !cart_id.equals(id)) {
						GoodsCart other = this.goodsCartService.getObjById(CommUtil.null2Long(cart_id));
						if (other != null) {
							Map temp_map = new HashMap();
							temp_map.put("id", other.getId());
							temp_map.put("name", other.getGoods().getGoods_name());
							temp_map.put("price", other.getGoods().getGoods_current_price());
							temp_map.put("count", other.getCount());
							temp_map.put("all_price", other.getPrice());
							temp_map.put("spec_info", other.getSpec_info());
							String goods_url = CommUtil.getURL(request) + "/goods_" + other.getGoods().getId() + ".htm";
							if (this.configService.getSysConfig().isSecond_domain_open()
									&& other.getGoods().getGoods_store().getStore_second_domain() != ""
									&& other.getGoods().getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ other.getGoods().getGoods_store().getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_url = store_second_domain + "/goods_" + other.getGoods().getId() + ".htm";
							}
							temp_map.put("url", goods_url);
							String img2 = CommUtil.getURL(request) + File.separator
									+ this.configService.getSysConfig().getGoodsImage().getPath() + File.separator
									+ this.configService.getSysConfig().getGoodsImage().getName();
							if (other.getGoods().getGoods_main_photo() != null) {
								img2 = other.getGoods().getGoods_main_photo().getPath() + File.separator
										+ other.getGoods().getGoods_main_photo().getName() + "_small."
										+ other.getGoods().getGoods_main_photo().getExt();
							}
							temp_map.put("img", img2); // 商品图片
							map_list.add(temp_map);
						}
						code = 100;
					}
				}
			}
		}
		json_map.put("map_list", map_list);
		json_map.put("code", code);
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

	@RequestMapping("/goods_cart_combin.htm")
	public ModelAndView goods_cart_combin(HttpServletRequest request, HttpServletResponse response, String id,
			String combin_ids, String type, String combin_version) {
		ModelAndView mv = new JModelAndView("goods_cart_suit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = false;
		Map suit_map = null;
		if (type != null && !"".equals(type)) {
			if ("suit".equals(type)) {
				String main_goods_info = null;
				if (combin_ids != null && !"".equals(combin_ids)) {
					ret = true;
				}
				if (ret) { // 组合套装商品添加
					Map params = new HashMap();
					params.put("main_goods_id", CommUtil.null2Long(id));
					params.put("combin_type", 0); // 组合套装
					params.put("combin_status", 1);
					List<CombinPlan> suits = this.combinplanService
							.query("select obj from CombinPlan obj where obj.main_goods_id=:main_goods_id and obj.combin_type=:combin_type and obj.combin_status=:combin_status",
									params, -1, -1);

					for (CombinPlan obj : suits) {
						List<Map> map_list = (List<Map>) Json.fromJson(obj.getCombin_plan_info());
						for (Map temp_map : map_list) {
							String ids = this.goodsViewTools.getCombinPlanGoodsIds(temp_map);
							if (ids.equals(combin_ids)) {
								suit_map = temp_map;
								main_goods_info = obj.getMain_goods_info();
								break;
							}
						}
					}
				}
				if (suit_map != null) {
					List<Map> map_list_temp = new ArrayList<Map>();
					if (main_goods_info != null) {
						Map main_map = (Map) Json.fromJson(main_goods_info);
						map_list_temp.add(main_map);
					}
					List<Map> other_goods_maps = this.goodsViewTools.getCombinPlanGoods(suit_map);
					for (Map other : other_goods_maps) {
						map_list_temp.add(other);
					}
					for (Map temp : map_list_temp) {
						int goods_inventory = this.goodsService.getObjById(CommUtil.null2Long(temp.get("id")))
								.getGoods_inventory();
						temp.put("inventory", goods_inventory);
					}
					mv.addObject("maps", map_list_temp);
					mv.addObject("plan_map", suit_map);
					mv.addObject("combin_version", combin_version);
					mv.addObject("goodsViewTools", goodsViewTools);
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "您所访问的地址不存在");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			mv.addObject("id", id);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("combin_ids", combin_ids);
			mv.addObject("type", type);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/goods_cart0.htm")
	public ModelAndView goods_cart0(HttpServletRequest request, HttpServletResponse response, String gid) {
		ModelAndView mv = new JModelAndView("goods_cart0.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 同类其他商品
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
		if (goods != null) {
			Map map = new HashMap();
			map.put("goods_status", 0);
			map.put("gc_id", goods.getGc().getId());
			map.put("gid", CommUtil.null2Long(gid));
			List<Goods> class_goods = this.goodsService
					.query("select obj from Goods obj where obj.gc.id=:gc_id and obj.id!=:gid and obj.goods_status=:goods_status order by goods_salenum desc",
							map, 0, 9);
			mv.addObject("class_goods", class_goods);
		}
		// 当天直通车商品，并且随机显示6个,显示在goods_cart0.html您可能还需要以下商品中
		List<Goods> ztc_goods = this.goodsViewTools.query_Ztc_Goods(6);
		mv.addObject("ztc_goods", ztc_goods);
		String return_url = CommUtil.getURL(request) + "/goods_" + gid + ".htm";
		if (goods != null && goods.getGoods_type() == 1) {
			if (this.configService.getSysConfig().isSecond_domain_open() && goods.getGoods_store() != null
					&& goods.getGoods_store().getStore_second_domain() != "") {
				String store_second_domain = "http://" + goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				return_url = store_second_domain + "/goods_" + gid + ".htm";
			}
		}
		mv.addObject("return_url", return_url);
		return mv;
	}

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1.htm")
	public ModelAndView goods_cart1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_cart1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 当天直通车商品，
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_load.htm")
	public ModelAndView goods_cart1_load(HttpServletRequest request, HttpServletResponse response, String load_class) {
		ModelAndView mv = new JModelAndView("goods_cart1_load.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);

		Date date = new Date();
		if (carts.size() > 0) {
			Set<Long> set = new HashSet<Long>();// 保存当前用户的所有购物车所关联的当前正在进行中的满就送活动的id
			List<GoodsCart> native_goods = new ArrayList<GoodsCart>();// 购物车中未参加活动的所有商品。参加了还没开始的满就送活动的商品所在的购物车也会保存到这里
			Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();// key:满减活动id，value：参加对应满减活动的用户购物车列表
			Map<Long, String> erString = new HashMap<Long, String>();// key:满减活动id，value：对应满减活动的说明
			for (GoodsCart cart : carts) {
				if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
					BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
					if (date.before(bg.getBeginTime()) || date.after(bg.getEndTime())) {
						native_goods.add(cart);
					} else {
						set.add(cart.getGoods().getBuyGift_id());
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
			mv.addObject("cart", (List<GoodsCart>) separate_carts.get("normal")); // 非组合套装中的商品的购物车列表
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
				mv.addObject("ac_goods", map);// map中有以下值：key=满赠活动的id(Long), value=对应满赠活动的购物车列表
			}
		}
		// 当天直通车商品，
		if (load_class != null) {
			mv.addObject("load_class", load_class);
		}
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 购物车修改商品规格
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_spec.htm")
	public ModelAndView goods_cart1_spec(HttpServletRequest request, HttpServletResponse response, String cart_id) {
		ModelAndView mv = new JModelAndView("goods_cart1_spec.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCart cart = this.goodsCartService.getObjById(CommUtil.null2Long(cart_id));
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("cart", cart);
		return mv;
	}

	/**
	 * 购物车修改商品规格
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_spec_save.htm")
	public void goods_cart1_spec_save(HttpServletRequest request, HttpServletResponse response, String gsp, String id) {
		Map json_map = new HashMap();
		int code = 100; // 100修改成功，-100库存不足
		GoodsCart obj = this.goodsCartService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			Map goodsCountAndPrice = this.goodsCartService.genericDefaultInfo(obj.getGoods(), gsp);
			int goods_inventory = CommUtil.null2Int(goodsCountAndPrice.get("count")); // 计算商品库存信息
			double price = CommUtil.null2Double(goodsCountAndPrice.get("price")); // 计算商品库存信息
			if (goods_inventory == 0) {
				code = -100;
			} else {
				String[] gspIds = CommUtil.null2String(gsp).split(",");
				String spec_info = "";
				obj.getGsps().removeAll(obj.getGsps());
				List<Map> goods_specs_info = (List<Map>) Json.fromJson(obj.getGoods().getGoods_specs_info());
				for (String gsp_id : gspIds) {
					GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gsp_id));
					if (spec_property != null) {
						obj.getGsps().add(spec_property);
						spec_info += spec_property.getSpec().getName() + "：";
						for (Map map : goods_specs_info) {
							if (CommUtil.null2Long(map.get("id")).equals(spec_property.getId())) {
								spec_info += map.get("name").toString();
								break;
							}
						}
						spec_info += "<br>";
					}
				}
				// TODO:合并相同商品相同规格的购物车

				obj.setCart_gsp(gsp);
				obj.setSpec_info(spec_info);
				obj.setPrice(BigDecimal.valueOf(price));
				this.goodsCartService.update(obj);
			}
		} else {
			code = -100;
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 分离组合销售活动购物车。返回的map中有以下值：key=combin,value=所有组合套装中主体商品对应的购物车列表；key=normal：非组合套装中的商品对应的购物车列表
	 * 
	 * @param carts
	 * @return
	 */
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

	/**
	 * 
	 * @param request
	 * @param response
	 * @param orderType
	 *            值为s+秒杀商品id，如s51
	 * @return
	 */
	@SecurityMapping(title = "确认秒杀商品购物车第二步", value = "/seckill_goods_cart2.htm*", rtype = "buyer", rname = "秒杀购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/seckill_goods_cart2.htm")
	public ModelAndView seckill_goods_cart2(HttpServletRequest request, HttpServletResponse response, String orderType) {
		ModelAndView mv = new JModelAndView("goods_cart2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		// 未登录状态下，购买秒杀商品遭单点登录拦截，参数丢失，需求cookie中获得参数
		// if (null == orderType || !orderType.matches("s\\d+")) {
		// Cookie[] cookies = request.getCookies();
		// if (cookies != null) {
		// for (Cookie cookie : cookies) {
		// if ("orderType".equals(cookie.getName())) {
		// orderType = CommUtil.null2String(cookie.getValue());
		// cookie.setValue("");
		// break;
		// }
		// }
		// }
		// }

		if (null == orderType || !orderType.matches("s\\d+")) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，请重新秒杀");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}

		// 未登录状态下，购买秒杀商品，购物车中userid为空，查出结果集为空，需将cookie购物车转换为user购物车
		this.goodsCartService.cart_calc(request, response);

		// 查询秒杀商品所在的购物车
		Goods goods = goodsService.getObjById(Long.parseLong(orderType.substring(1)));// 秒杀商品
		Map cart_map = new HashMap();
		cart_map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		cart_map.put("cart_status", 0);
		cart_map.put("goods_id", goods.getId());
		List<GoodsCart> carts = goodsCartService
				.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.goods.id=:goods_id",
						cart_map, 0, 1);
		String gcs = carts.get(0).getId().toString();

		return goods_cart2(request, response, gcs, null);
	}

	/**
	 * 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @param gcs
	 * @param giftids
	 * @return
	 */
	@SecurityMapping(title = "确认购物车第二步", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart2.htm")
	public ModelAndView goods_cart2(HttpServletRequest request, HttpServletResponse response, String gcs, String giftids) {
		ModelAndView mv = new JModelAndView("goods_cart2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (StringUtils.isNullOrEmpty(gcs)) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，请重新进入购物车");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}

		try {
			Map map = this.orderFormService.goodsCart2(request, response, gcs, giftids, null, false);
			mv.addObject("order_goods_price", map.get("all"));
			mv.addObject("order_er_price", map.get("reduce"));
			int selfPickupEnabled = CommUtil.null2Int(map.get("selfPickupEnabled"));
			mv.addObject("selfPickupEnabled", selfPickupEnabled);
			if (selfPickupEnabled == 1) {
				mv.addObject("areas",
						this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1));
			}
			mv.addObject("addrs", map.get("addrs"));
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
			// mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("cartTools", cartTools);
			// mv.addObject("transportTools", transportTools);
			mv.addObject("userTools", userTools);
			mv.addObject("map_list", map.get("map_list"));
			mv.addObject("gcs", gcs);
			mv.addObject("storeIds", map.get("store_list"));
			mv.addObject("goods_cod", map.get("goods_cod"));
			mv.addObject("tax_invoice", map.get("tax_invoice"));
			// mv.addObject("giftids", map.get("validGiftIds"));
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
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", e.getMessage());
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}

		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param cart_session
	 * @param addr_id
	 * @param gcs
	 * @param delivery_time
	 * @param delivery_type
	 *            配送方式：1自提点
	 * @param delivery_id
	 * @param payType
	 * @param gifts
	 * @param deliveryway
	 *            配送方式：0快递，2到店自提
	 * @param xmStoreId
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart3.htm")
	public ModelAndView goods_cart3(HttpServletRequest request, HttpServletResponse response, String cart_session,
			Long addr_id, String gcs, String delivery_time, String delivery_type, String delivery_id, String payType,
			String gifts, String deliveryway, String xmStoreId) {
		ModelAndView mv = new JModelAndView("goods_cart3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String cart_session1 = (String) request.getSession(true).getAttribute("cart_session");
		if (!CommUtil.null2String(cart_session1).equals(cart_session) || StringUtils.isNullOrEmpty(gcs)) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单已经失效");
			mv.addObject("url", CommUtil.getURL(request) + "/goods_cart1.htm");
		}

		try {
			User buyer = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			String[] gcIds = CommUtil.null2String(request.getSession(true).getAttribute("cartIds")).split(",");// 直接从上一步中取得用户已选择的购物车
			int needId = CommUtil.null2Int(request.getSession(true).getAttribute("needId")); // 是否需要身份证
			Address addr = null; // 收货地址
			DeliveryAddress deliveryAddr = null;// 自提点地址
			ShipAddress xmStore = null; // 到店自提门店地址
			if ("2".equals(deliveryway)) {// 到店自提
				if (CommUtil.null2Int(request.getSession(true).getAttribute("selfPickupEnabled")) == 0) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "该笔订单不支持到店自提，请重新选择配送方式。");
					mv.addObject("url", CommUtil.getURL(request) + "/goods_cart1.htm");
					return mv;
				}
				xmStore = this.shipAddressService.getObjById(CommUtil.null2Long(xmStoreId));
				if (xmStore == null) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "您选择了到店自提，请您选择要上门提货的门店。");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
				}
			} else {// 快递或快递自提点
				addr = this.addressService.getObjById(addr_id);
				if (addr == null) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "收货地址错误，请重新填写收货地址");
					mv.addObject("url", CommUtil.getURL(request) + "/goods_cart2.htm");
					return mv;
				}

				if (needId == 1 && StringUtils.isNullOrEmpty(addr.getCard())) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "收货地址错误，请填写身份证信息。");
					mv.addObject("url", CommUtil.getURL(request) + "/goods_cart2.htm");
					return mv;
				}

				if (CommUtil.null2Int(delivery_type) == 1) {// 自提点服务
					deliveryAddr = this.deliveryaddrService.getObjById(CommUtil.null2Long(delivery_id));
					if (deliveryAddr == null) {
						mv = new JModelAndView("error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "请选择自提点。");
						mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
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
			Map<Object, Set<Long>> storeCart = (Map<Object, Set<Long>>) request.getSession(true).getAttribute(
					"storeCartMapping");

			Map map = this.orderFormService.goodsCart3(request, buyer, gift_ids, addr, gcIds, delivery_time, deliveryAddr,
					payType, xmStore, false, giftCartMapping, hxGoods, transports, storeIds, storeCart);
			double all_of_price = (double) map.get("all_of_price");
			OrderForm main_order = (OrderForm) map.get("main_order");

			if ("payafter".equals(payType)) { // 使用货到付款
				mv = new JModelAndView("payafter_pay.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				String pay_session = CommUtil.randomString(32);
				request.getSession(true).setAttribute("pay_session", pay_session);
				mv.addObject("pay_session", pay_session);
			} else {
				String mark = PropertyUtil.getProperty("web_pay_center_mark");
				if ("1".equals(mark)) {
					String url = PropertyUtil.getProperty("web_return_url_order");
					String returnUrl = CommUtil.getURL(request) + url; // 回调业务系统地址
					// 不是货到付款，走支付中心在线支付
					this.orderFormService.orderPay(request, response, buyer, main_order, ChannelEnum.WEB.toString(), OrderTypeEnum.SHOPPING.getIndex(), returnUrl);
					return null;
				}
			}
			mv.addObject("user", buyer);
			mv.addObject("all_of_price", all_of_price);
			mv.addObject("paymentTools", paymentTools);
			mv.addObject("order", main_order); // 将主订单信息封装到前台视图中

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
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", e.getMessage());
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
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

	@SecurityMapping(title = "订单支付详情", value = "/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_view.htm")
	public ModelAndView order_pay_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("order_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		// User user = SecurityUserHolder.getCurrentUser();
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		String orderType = OrderTypeEnum.SHOPPING.getIndex(); 
		if (of != null && of.getUser_id().equals(user.getId().toString())) {
			if (of.getOrder_status() == 10) {
				if (of.getOrder_cat() == 1) { // 处理手机充值付款
					orderType = OrderTypeEnum.CHARGE.getIndex();
					mv = new JModelAndView("recharge_order.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					String ofcard_userid = this.configService.getSysConfig().getOfcard_userid();
					String ofcard_userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
					String rc_amount = CommUtil.null2String(of.getRc_amount());
					String mobile = of.getRc_mobile();
					String query_url = "http://api2.ofpay.com/telquery.do?userid=" + ofcard_userid + "&userpws="
							+ ofcard_userpws + "&phoneno=" + mobile + "&pervalue=" + rc_amount + "&version=6.0";
					String return_xml = this.getHttpContent(query_url, "gb2312", "POST");
					Map map = XMLUtil.parseXML(return_xml, true);
					double inprice = CommUtil.null2Double(map.get("inprice"));
					if (CommUtil.null2Double(map.get("inprice")) <= CommUtil.null2Double(rc_amount)) {
						inprice = CommUtil.add(map.get("inprice"), this.configService.getSysConfig()
								.getOfcard_mobile_profit());
						if (inprice > CommUtil.null2Double(rc_amount)) {
							inprice = CommUtil.null2Double(rc_amount);
						}
					}
					map.put("inprice", inprice);
					String recharge_pay_session = CommUtil.randomString(64);
					request.getSession(true).setAttribute("recharge_pay_session", recharge_pay_session);
					mv.addObject("recharge_pay_session", recharge_pay_session);
					mv.addObject("map", map);
					mv.addObject("rc_amount", rc_amount);
					mv.addObject("mobile", mobile);
					mv.addObject("order", of);
				} else {
					boolean exist = this.orderFormTools.order_goods_exist(id);
					if (!exist) {
						mv = new JModelAndView("error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "订单中商品已被删除，请重新下单");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
						return mv;
					}
					// boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(id, of);
					// if (inventory_very) { // 订单中商品库存验证成功
					mv.addObject("of", of);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("orderFormTools", this.orderFormTools);
					mv.addObject("url", CommUtil.getURL(request));
					/*
					 * } else { mv = new JModelAndView("error.html", configService.getSysConfig(),
					 * this.userConfigService.getUserConfig(), 1, request, response); mv.addObject("op_title",
					 * "订单中商品库存不足，请重新下单"); mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm"); }
					 */
				}
				mv.addObject("user", user);

				String mark = PropertyUtil.getProperty("web_pay_center_mark");
				if ("1".equals(mark)) {
					String url = PropertyUtil.getProperty("web_return_url_order");
					String returnUrl = CommUtil.getURL(request) + url; // 回调业务系统地址
					// 不是货到付款，走支付中心在线支付
					mv = (ModelAndView) this.orderFormService.orderPay(request, response, user, of,  ChannelEnum.WEB.toString(), orderType,
							returnUrl);
				}
			} else if (of.getOrder_status() < 10) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "该订单已经取消");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "该订单已付款");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "该订单已失效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付", value = "/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay.htm")
	public ModelAndView order_pay(HttpServletRequest request, HttpServletResponse response, String payType, String order_id,
			String pay_pwd, String gate_id) {
		ModelAndView mv = null;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null && order.getOrder_status() == 10) {
			boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(order_id, order);
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				return mv;
			}
			// if (inventory_very) { // 订单中商品库存验证成功
			if ("".equals(CommUtil.null2String(payType))) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				// 给订单添加支付方式 ,
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", payType);
				payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
				order.setPayment(payments.get(0));
				order.setPayType("online");
				this.orderFormService.update(order);
				if ("balance".equals(payType)) { // 使用预存款支付
					Date lockStartTime = (Date) request.getSession(true).getAttribute("lockStartTime");
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MINUTE, -30);// 密码输错5次后须等待30分钟后才可以继续输入密码
					if (lockStartTime == null || lockStartTime.before(cal.getTime())) {
						// 校验支付密码
						User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
						ResultDTO resultDTO = userManageConnector.payPwdCheck(user.getCustId(), Md5Encrypt.md5(pay_pwd)
								.toLowerCase());

						if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
							request.getSession(true).removeAttribute("lockStartTime");
							request.getSession(true).removeAttribute("errorCount");
							mv = new JModelAndView("balance_pay.html", configService.getSysConfig(),
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
							Integer errCnt = (Integer) request.getSession(true).getAttribute("errorCount");
							if (errCnt == null) {
								errCnt = 1;
								request.getSession(true).setAttribute("errorCount", 1);
							} else {
								if (errCnt > 5) {// lock for half hour
									if (lockStartTime != null && lockStartTime.before(cal.getTime())) {
										request.getSession(true).setAttribute("errorCount", 1);
										request.getSession(true).removeAttribute("lockStartTime");
									} else {
										request.getSession(true).setAttribute("lockStartTime", new Date());
									}
								} else {
									errCnt = errCnt + 1;
									request.getSession(true).setAttribute("errorCount", errCnt);
								}
							}
							mv = new JModelAndView("error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", "第" + errCnt + "次输入密码错误！请重新输入,有效只有6次");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
						}
					} else {// 继续等待
						mv = new JModelAndView("error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						Long timeSpan = cal.getTimeInMillis();
						cal.setTime(lockStartTime);
						timeSpan = timeSpan - cal.getTimeInMillis();

						mv.addObject("op_title", "请于 " + timeSpan / (1000 * 30) + "分钟后重试");
						mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					}
				} else { // 使用在线支付
					mv = new JModelAndView("line_pay.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("payType", payType);

					mv.addObject("gate_id", gate_id);

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
			}
			/*
			 * } else { mv = new JModelAndView("error.html", configService.getSysConfig(),
			 * this.userConfigService.getUserConfig(), 1, request, response); mv.addObject("op_title", "订单中商品库存不足，请重新下单");
			 * mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm"); }
			 */
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，付款失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_payafter.htm")
	public ModelAndView order_pay_payafter(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(true).getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				return mv;
			}
			// 验证订单中商品库存是否充足
			boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(order_id, order);
			if (!inventory_very) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				return mv;
			}
			order.setPay_msg(pay_msg);
			order.setPayTime(new Date());
			order.setPayType("payafter");
			order.setOrder_status(16);
			this.orderFormService.update(order);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
				this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_payafter_pay_ok_notify", store.getUser()
						.getMobile(), null, CommUtil.null2String(order.getId()), order.getStore_id());
				this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_payafter_pay_ok_notify", store
						.getUser().getEmail(), null, CommUtil.null2String(order.getId()), order.getStore_id());
			}
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
					if (child_order.getOrder_form() == 0) {
						this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_payafter_pay_ok_notify", store
								.getUser().getMobile(), null, CommUtil.null2String(child_order.getId()), child_order
								.getStore_id());
						this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_payafter_pay_ok_notify",
								store.getUser().getEmail(), null, CommUtil.null2String(child_order.getId()),
								child_order.getStore_id());
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
			request.getSession(true).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单预付款支付", value = "/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_balance.htm")
	public ModelAndView order_pay_balance(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null && order.getOrder_status() < 20) { // 订单不为空且订单状态为未付款才可以正常使用预存款付款
			boolean exist = this.orderFormTools.order_goods_exist(order_id);
			if (!exist) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				return mv;
			}
			// 验证订单中商品库存是否充足
			/*
			 * boolean inventory_very = this.orderFormTools.order_goods_InventoryVery(order_id, order); if (!inventory_very)
			 * { mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
			 * 1, request, response); mv.addObject("op_title", "订单中商品库存不足，请重新下单"); mv.addObject("url",
			 * CommUtil.getURL(request) + "/buyer/order.htm"); return mv; }
			 */
			Map params = new HashMap();
			params.put("mark", "balance");
			List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			double order_total_price = CommUtil.null2Double(order.getTotalPrice());
			if (!"".equals(CommUtil.null2String(order.getChild_order_detail())) && order.getOrder_cat() != 2) {
				order_total_price = this.orderFormTools.query_order_price(CommUtil.null2String(order.getId()));
			}
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {// 验证用户余额是否大于订单总金额
				// TODO 调用bill进行会员消费支付start
				ResultDTO resultDTO = feeManageservice.payment(user, order, order_total_price);

				if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
					order.setPay_msg(pay_msg);
					order.setOrder_status(20);
					if (payments.size() > 0) {
						order.setPayment(payments.get(0));
						order.setPayTime(new Date());
					}
					boolean ret = this.orderFormService.update(order);
					// 主订单记录支付日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("预付款支付成功");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					if (order.getOrder_form() == 0) {
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
					if (ret) {
						// 预存款付款成功后，执行子订单状态改变及发送提醒信息
						if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))
								&& order.getOrder_cat() != 2) {
							List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
								child_order.setOrder_status(20);
								child_order.setOut_order_id(order.getOut_order_id());
								if (payments.size() > 0) {
									child_order.setPayment(payments.get(0));
									child_order.setPayTime(new Date());
								}
								this.orderFormService.update(child_order);
								// 子订单记录支付日志
								// OrderFormLog child_ofl = new OrderFormLog();
								// child_ofl.setAddTime(new Date());
								// child_ofl.setLog_info("预付款支付");
								// child_ofl.setLog_user(SecurityUserHolder.getCurrentUser());
								// child_ofl.setOf(child_order);
								// this.orderFormLogService.save(child_ofl);
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
						// 如果是团购订单，则需呀执行团购订单相关流程及发送团购码
						if (order.getOrder_cat() == 2) {
							Calendar ca = Calendar.getInstance();
							ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
							SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String latertime = bartDateFormat.format(ca.getTime());
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date = sdf.parse(latertime);
							order.setReturn_shipTime(date);
							Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
							int count = CommUtil.null2Int(map.get("goods_count").toString());
							String goods_id = map.get("goods_id").toString();
							GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(goods_id));
							// goods.setGroup_count(goods.getGroup_count() - CommUtil.null2Int(count));
							// this.groupLifeGoodsService.update(goods);
							int i = 0;
							List<String> code_list = new ArrayList(); // 存放团购消费码
							String codes = "";
							while (i < count) {
								GroupInfo info = new GroupInfo();
								info.setAddTime(new Date());
								info.setLifeGoods(goods);
								info.setPayment(payments.get(0));
								info.setUser_id(user.getId());
								info.setUser_name(user.getUserName());
								info.setOrder_id(order.getId());
								info.setUser_mobile(user.getMobile());
								info.setGroup_sn(user.getId() + CommUtil.formatTime("yyyyMMddHHmmss" + i, new Date()));
								Calendar ca2 = Calendar.getInstance();
								ca2.add(ca2.DATE, this.configService.getSysConfig().getGrouplife_order_return());
								SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String latertime2 = bartDateFormat2.format(ca2.getTime());
								info.setRefund_Time(CommUtil.formatDate(latertime2));
								this.groupInfoService.save(info);
								codes = codes + info.getGroup_sn() + " ";
								code_list.add(info.getGroup_sn());
								i++;
							}
							if (order.getOrder_form() == 0) {
								Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
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
								plog.setTotal_amount(this.orderFormService.getPayoffAmount(order)); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
								plog.setShip_price(order.getShip_price());// 订单运费
								this.payoffLogService.save(plog);
								store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
										store.getStore_sale_amount()))); // 店铺本次结算总销售金额
								// 团购消费码，没有佣金，店铺总佣金不变
								store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
										store.getStore_payoff_amount()))); // 店铺本次结算总佣金
								this.storeService.update(store);
							}
							// 增加系统总销售金额、消费码没有佣金，系统总佣金不变
							SysConfig sc = this.configService.getSysConfig();
							sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
									sc.getPayoff_all_sale())));
							this.configService.update(sc);
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
							String msg_content = "恭喜您成功购买团购" + map.get("goods_name") + ",团购消费码分别为：" + codes
									+ "您可以到用户中心-我的生活购中查看消费码的使用情况";
							// 发送系统站内信给买家
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
						}
						// 判断是否有满就送如果有则进行库存操作
						/*
						 * if (order.getWhether_gift() == 1) { this.buyGiftViewTools.update_gift_invoke(order); }
						 */
						// user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(),
						// order_total_price)));//余额都在CRM端保存和增减，故注释掉
						// this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_user(user);
						log.setPd_op_type("消费");
						log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(order_total_price)));
						log.setPd_log_info(order.getOrder_id() + "订单购物减少可用预存款");
						log.setPd_type("可用预存款");
						this.predepositLogService.save(log);
						// 执行库存减少,如果是团购商品，团购库存同步减少
						if (order.getOrder_cat() != 2) {
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
												// goods.setGoods_inventory(goods.getGoods_inventory() - combin_count);
												this.goodsService.update(goods);
											}
										}
									}
								}
							}
							// 普通商品更新信息
							for (Goods goods : goods_list) {
								int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
										CommUtil.null2String(goods.getId()));
								if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
									for (GroupGoods gg : goods.getGroup_goods_list()) {
										if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
											// gg.setGg_count(gg.getGg_count() - goods_count);
											gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
											this.groupGoodsService.update(gg);
										}
									}
								} else if (!"".equals(CommUtil.null2String(order.getSeckill_info()))) { // 增加秒杀商品销量
									Map map = Json.fromJson(Map.class, order.getSeckill_info());
									SeckillGoods seckillGoods = seckillGoodsService.getObjById(Long.valueOf(String
											.valueOf(map.get("seckill_goods_id"))));
									seckillGoods.setGg_selled_count(seckillGoods.getGg_selled_count() + goods_count);
									seckillGoodsService.update(seckillGoods);
								}
								List<String> gsps = new ArrayList<String>();
								List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(
										CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
								String spectype = "";
								for (GoodsSpecProperty gsp : temp_gsp_list) {
									gsps.add(gsp.getId().toString());
									spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
								}
								String[] gsp_list = new String[gsps.size()];
								gsps.toArray(gsp_list);
								if ("".equals(CommUtil.null2String(order.getSeckill_info()))) {
									goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
								}
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

								if (logspecmap.containsKey(spectype)) {
									logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
								} else {
									logspecmap.put(spectype, goods_count);
								}
								todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));

								this.goodsLogService.update(todayGoodsLog);
								/*
								 * boolean inventory_warn = false; if ("all".equals(goods.getInventory_type())) {
								 * goods.setGoods_inventory(goods.getGoods_inventory() - goods_count); if
								 * (goods.getGoods_inventory() <= goods.getGoods_warn_inventory()) { inventory_warn = true; }
								 * } else { List<HashMap> list = Json.fromJson(ArrayList.class,
								 * goods.getGoods_inventory_detail()); for (Map temp : list) { String[] tempIds =
								 * CommUtil.null2String(temp.get("id")).split("_"); Arrays.sort(tempIds);
								 * Arrays.sort(gsp_list); if (Arrays.equals(tempIds, gsp_list)) { temp.put("count",
								 * CommUtil.null2Int(temp.get("count")) - goods_count); if
								 * (CommUtil.null2Int(temp.get("count")) <= CommUtil.null2Int(temp.get("supp"))) {
								 * inventory_warn = true; } } } goods.setGoods_inventory_detail(Json.toJson(list,
								 * JsonFormat.compact())); } for (GroupGoods gg : goods.getGroup_goods_list()) { if
								 * (goods.getGroup() != null && gg.getGroup().getId().equals(goods.getGroup().getId()) &&
								 * gg.getGg_count() == 0) { goods.setGroup_buy(3); // 标识商品的状态为团购数量已经结束 } } if
								 * (inventory_warn) { goods.setWarn_inventory_status(-1); // 该商品库存预警状态 }
								 */
								this.goodsService.update(goods);
								// 更新lucene索引
								if (goods.getGroup_buy() != 2) {
									String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
											+ File.separator + "goods";
									File file = new File(goods_lucene_path);
									if (!file.exists()) {
										CommUtil.createFolder(goods_lucene_path);
									}
									LuceneUtil lucene = LuceneUtil.instance();
									lucene.setIndex_path(goods_lucene_path);
									lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
								}
							}
						}
					}

					// 判断如果不是生活类团购订单则发送订单同步请求
					if (order.getOrder_cat() != 2) {
						// 支付成功发送同步订单事件
						this.synchronizeOrderPublisher.synchronizeOrder(order.getId());
					}

					mv.addObject("op_title", "预付款支付成功");
					if (order.getOrder_cat() == 2) {
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
					} else {
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
					}
				} else {
					// 主订单记录支付失败日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("预付款支付失败_" + resultDTO.getMsg());
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);

					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", resultDTO.getMsg());
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "可用余额不足，支付失败");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付结果", value = "/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_finish.htm")
	public ModelAndView order_finish(HttpServletRequest request, HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		mv.addObject("obj", obj);
		mv.addObject("all_price", this.orderFormTools.query_order_price(obj.getId().toString()));
		return mv;
	}

	@SecurityMapping(title = "地址修改", value = "/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address.htm")
	public ModelAndView cart_address(HttpServletRequest request, HttpServletResponse response, String id, Long needId) {
		ModelAndView mv = new JModelAndView("cart_address.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addrs = this.addressService.query(
				"select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc", params, -1, -1);
		if (!StringUtils.isNullOrEmpty(id)) {
			Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
			if (obj != null) {
				if (SecurityUserHolder.getCurrentUser().getId().equals(obj.getUser().getId())) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("error", true);
				}
			} else {
				mv.addObject("error", true);
			}
		}
		mv.addObject("addrs_size", addrs.size());
		mv.addObject("areas", areas);
		mv.addObject("needId", needId);
		return mv;
	}

	@SecurityMapping(title = "地址保存", value = "/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address_save.htm")
	public ModelAndView cart_address_save(HttpServletRequest request, HttpServletResponse response, String id,
			String area_id, String op_type, String gcs, Long needId) {

		// area_id 不能为空，如果为空，则跳转到首页面
		if (StringUtils.isNullOrEmpty(area_id)) {
			ModelAndView modelAndView = new ModelAndView("redirect:/index.htm");
			return modelAndView;
		}

		WebForm wf = new WebForm();
		Address address = null;
		if (StringUtils.isNullOrEmpty(id)) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) { // 只允许修改自己的地址信息
				address = (Address) wf.toPo(request, obj);
			}
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);

		address.setLatitude(null);// 修改地址后要重新计算经纬度
		address.setLongitude(null);

		if (StringUtils.isNullOrEmpty(id)) {
			this.addressService.save(address);
		} else {
			this.addressService.update(address);
		}

		logger.debug("重新获取地址(id=" + address.getId() + ")的经纬度。原经纬度为(" + address.getLongitude() + "," + address.getLatitude()
				+ ")");
		getAddressLngAndLat(address.getId());
		logger.debug("地址(id=" + address.getId() + ")的最新经纬度为(" + address.getLongitude() + "," + address.getLatitude() + ")");

		ModelAndView mv = null;
		if ("address_create".equals(CommUtil.null2String(op_type))) {
			mv = new JModelAndView("cart_address_create_result.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Address> addrs = this.addressService.query(
					"select obj from Address obj where obj.user.id=:user_id order by obj.default_val desc,obj.addTime desc",
					params, -1, -1);
			mv.addObject("addrs", addrs);
		} else {
			mv = new JModelAndView("cart_address_result.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("addr", address);
		}
		mv.addObject("needId", needId);
		return mv;
	}

	@Async
	private void getAddressLngAndLat(Long addressId) {
		Address addr = this.addressService.getObjById(addressId);
		boolean flag = AddressUtil.getLngAndLat(addr);
		if (flag) {
			this.addressService.update(addr);
		}
	}

	@SecurityMapping(title = "地址新增", value = "/cart_address_create.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address_create.htm")
	public ModelAndView cart_address_create(HttpServletRequest request, HttpServletResponse response, Long needId) {
		ModelAndView mv = new JModelAndView("cart_address_create.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addrs = this.addressService.query(
				"select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc", params, -1, -1);
		mv.addObject("addrs", addrs);
		mv.addObject("areas", areas);
		mv.addObject("needId", needId);
		return mv;
	}

	@SecurityMapping(title = "设置默认地址", value = "/cart_addr_default.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_addr_default.htm")
	public void cart_addr_default(HttpServletRequest request, HttpServletResponse response, String id) {
		boolean ret = false;
		if (!StringUtils.isNullOrEmpty(id)) {
			Address addr = this.addressService.getObjById(CommUtil.null2Long(id));
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("id", CommUtil.null2Long(id));
			params.put("default_val", 1);
			List<Address> addrs = this.addressService
					.query("select obj from Address obj where obj.user.id=:user_id and obj.id!=:id and obj.default_val=:default_val",
							params, -1, -1);
			for (Address addr1 : addrs) {
				addr1.setDefault_val(0);
				this.addressService.update(addr1);
			}
			if (addr != null) {
				addr.setDefault_val(1);
				ret = this.addressService.update(addr);
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

	@SecurityMapping(title = "订单加载自提点", value = "/cart_delivery.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_delivery.htm")
	public ModelAndView cart_delivery(HttpServletRequest request, HttpServletResponse response, String addr_id,
			String currentPage, String deliver_area_id) {
		ModelAndView mv = new JModelAndView("cart_delivery.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		DeliveryAddressQueryObject qo = new DeliveryAddressQueryObject(currentPage, mv, "addTime", "desc");
		if (!StringUtils.isNullOrEmpty(deliver_area_id)) {
			Area deliver_area = this.areaService.getObjById(CommUtil.null2Long(deliver_area_id));
			Set<Long> ids = this.genericIds(deliver_area);
			Map paras = new HashMap();
			paras.put("ids", ids);
			qo.addQuery("obj.da_area.id in(:ids)", paras);
			mv.addObject("deliver_area_id", deliver_area_id);
		} else {
			if (!StringUtils.isNullOrEmpty(addr_id)) {
				Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
				qo.addQuery("obj.da_area.parent.id", new SysMap("da_area_id", addr.getArea().getParent().getId()), "=");
				mv.addObject("area", addr.getArea().getParent());
			}
		}
		qo.addQuery("obj.da_status", new SysMap("da_status", 10), "=");
		qo.setPageSize(5);
		IPageList pList = this.deliveryaddrService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		String url = CommUtil.getURL(request) + "/cart_delivery.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	@SecurityMapping(title = "保存用户发票信息", value = "/invoice_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/invoice_save.htm")
	public void invoice_save(HttpServletRequest request, HttpServletResponse response, String invoice, String invoiceType) {
		boolean ret = false;
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user.setInvoice(invoice);
		user.setInvoiceType(CommUtil.null2Int(invoiceType));
		ret = this.userService.update(user);
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
	 * 在非goods_cart2.html（PC端页面）中调用此方法时要注意修改此方法中的(storeCartIds == null)分支，否则计算结果可能是错误的
	 * 
	 * @param request
	 * @param response
	 * @param addr_id
	 * @param store_id
	 */
	@SecurityMapping(title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_address.htm")
	public void order_address(HttpServletRequest request, HttpServletResponse response, String addr_id, String store_id) {
		List<SysMap> sms = this.orderFormService.calStoreShipTransports(request, response, store_id, addr_id);

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
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

	@Async
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

	@SecurityMapping(title = "F码购物第一步", value = "/f_code_cart.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/f_code_cart.htm")
	public ModelAndView f_code_cart(HttpServletRequest request, HttpServletResponse response, String goods_id, String gsp) {
		ModelAndView mv = new JModelAndView("f_code_cart.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getF_sale_type() == 0) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "该商品不需要F码购买");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				if ("".equals(CommUtil.null2String(gsp))) {
					gsp = this.goodsCartService.genericDefaultGsp(goods);
				}
				int goods_inventory = goods_inventory = CommUtil.null2Int(this.goodsCartService.genericDefaultInfo(goods,
						gsp).get("count")); // 计算该规格商品的库存量
				if (goods_inventory > 0) {
					String[] gspIds = CommUtil.null2String(gsp).split(",");
					String spec_info = "";
					List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
					for (String gsp_id : gspIds) {
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil
								.null2Long(gsp_id));
						boolean add = false;
						for (GoodsSpecProperty temp_gsp : goods.getGoods_specs()) { // 检查传入的规格信息是否是该商品对应的规格信息,杜绝用户私自修改传递参数
							if (temp_gsp != null && spec_property != null) {
								if (temp_gsp.getId().equals(spec_property.getId())) {
									add = true;
								}
							}
						}
						for (GoodsSpecProperty temp_gsp : specs) {
							if (temp_gsp.getSpec().getId().equals(spec_property.getSpec().getId())) {
								add = false;
							}
						}
						if (add) {
							specs.add(spec_property);
						}
					}
					if (this.goodsViewTools.generic_spec(goods_id).size() == specs.size()) { // 这里判断传入的规格数量和商品本身具有的是否一致,杜绝用户私自修改传递参数
						for (GoodsSpecProperty spec : specs) {
							spec_info = spec.getSpec().getName() + ":" + spec.getValue() + " " + spec_info;
						}
						String price = this.goodsTools.generGspgoodsPrice(gsp, goods_id);
						mv.addObject("spec_info", spec_info);
						mv.addObject("price", price);
						mv.addObject("obj", goods);
						mv.addObject("gsp", gsp);
						mv.addObject("goodsViewTools", this.goodsViewTools);
					} else {
						mv = new JModelAndView("error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "规格参数错误");
						mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					}
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "商品默认规格无库存，请选择其他规格购买");
					mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm");
				}

			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
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
	@SecurityMapping(title = "F码验证", value = "/f_code_validate.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/f_code_validate.htm")
	public void f_code_validate(HttpServletRequest request, HttpServletResponse response, String f_code, String goods_id) {
		int code = -100; // -100验证码错误，-200验证码已使用过，100验证成功，
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
		for (Map map : list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code)) { // 存在该F码且是未使用的则验证成功
				if (CommUtil.null2Int(map.get("status")) == 0) {
					code = 100; // 验证成功
				} else {
					code = -200; // 验证码已使用过
				}
				break;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "F码完成验证进入订单提交", value = "/f_code_validate.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/add_f_code_goods_cart.htm")
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
			String[] gspIds = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gspIds);
			for (GoodsCart gc : carts_list) {
				if (gspIds != null && gspIds.length > 0 && gc.getGsps() != null) {
					String[] gspIds1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gspIds1[i] = gc.getGsps().get(i) != null ? gc.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gspIds1);
					if (gc.getGoods().getId().toString().equals(goods_id) && Arrays.equals(gspIds, gspIds1)) {
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
				String price = this.goodsTools.generGspgoodsPrice(gsp, goods_id);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				String spec_info = "";
				for (String gspId : gspIds) {
					GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gspId));
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

}
