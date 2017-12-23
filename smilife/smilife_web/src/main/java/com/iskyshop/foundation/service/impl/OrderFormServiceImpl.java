package com.iskyshop.foundation.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.OrderGoodsStatistics;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.FreightGoods;
import com.iskyshop.foundation.domain.virtual.OrderEnoughReduceInfo;
import com.iskyshop.foundation.domain.virtual.OrderEnoughReduceInfo.EnoughReduceGoods;
import com.iskyshop.foundation.domain.virtual.OrderFreightInfo;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IOrderGoodsStatisticsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.oms.service.AbstractOmsManagerService;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.iskyshop.view.web.tools.SnapshotTools;
import com.tydic.framework.util.PropertyUtil;

@Service
@Transactional
public class OrderFormServiceImpl implements IOrderFormService {

	private static Logger logger = Logger.getLogger(OrderFormServiceImpl.class);
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private DeliveryAddressTools deliveryAddressTools;
	@Autowired
	private SnapshotTools snapshotTools;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private AbstractOmsManagerService<OrderForm> orderFormOmsServiceImpl;
	@Autowired
	private IOrderGoodsStatisticsService orderGoodsStatisticsService;
	@Autowired
	private IpayCenterService payCenterService;
	@Autowired
	private IUserConfigService userConfigService;


	@Transactional(readOnly = false)
	public boolean save(OrderForm orderForm) {
		/**
		 * init other field here
		 */
		try {
			this.orderFormDao.save(orderForm);
			// this.saveCrmOrder(orderForm);// 执行crm订单同步
			orderFormOmsServiceImpl.insertOrder(orderForm);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List executeNativeQuery(String sql) {
		return this.orderFormDao.executeNativeQuery(sql, null, -1, -1);
	}

	@Transactional(readOnly = true)
	public OrderForm getObjById(Long id) {
		OrderForm orderForm = this.orderFormDao.get(id);
		if (orderForm != null) {
			return orderForm;
		}
		return null;
	}

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.orderFormDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> orderFormIds) {
		// TODO Auto-generated method stub
		for (Serializable id : orderFormIds) {
			delete((Long) id);
		}
		return true;
	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(OrderForm.class, construct, query, params, this.orderFormDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize(), params);
		} else
			pList.doList(0, -1);
		return pList;
	}

	@Transactional(readOnly = false)
	public boolean update(OrderForm orderForm) {
		try {

			this.orderFormDao.update(orderForm);
			// this.saveCrmOrder(orderForm);// 执行crm订单同步
			orderFormOmsServiceImpl.updateOrder(orderForm);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<OrderForm> query(String query, Map params, int begin, int max) {
		return this.orderFormDao.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public List queryFromOrderForm(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.orderFormDao.query(query, params, begin, max);
	}

	@Transactional(readOnly = true)
	public Boolean validateSeckillOrderForm(Long userId, Long goodsId) {
		String query = "select obj from OrderForm obj where obj.user_id=:user_id "
				+ "and obj.seckill_info is not null and obj.order_status!=:order_status";
		Map params = new HashMap();
		params.put("user_id", String.valueOf(userId));
		params.put("order_status", 0);
		List<OrderForm> orderForms = orderFormDao.query(query, params, -1, -1);
		if (orderForms != null && !orderForms.isEmpty()) {
			for (OrderForm orderForm : orderForms) {
				Map map = Json.fromJson(Map.class, orderForm.getSeckill_info());
				if (Long.valueOf(String.valueOf(map.get("seckill_goods_id"))).equals(goodsId)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void saveOrderForm(List<Map<String, Object>> orderForms) {
		List<Map> child_order_maps = new ArrayList<Map>();
		List<GoodsCart> allGoodsCarts = new ArrayList<GoodsCart>();
		List<OrderGoodsStatistics> allOrderGoodsStatisticses = new ArrayList<>();
		OrderForm mainOf = null;
		// 检查库存是否满足要求
		for (Map<String, Object> map : orderForms) {
			List<GoodsCart> gc_list = (List<GoodsCart>) map.get("gc_list");
			List<OrderGoodsStatistics> orderGoodsStatisticses = (List<OrderGoodsStatistics>) map
					.get("order_goods_statistics");

			for (GoodsCart gc : gc_list) { // 校验购物车中的商品库存
				int goods_inventory = CommUtil.null2Int(this.goodsCartService.genericDefaultInfo(gc.getGoods(),
						gc.getCart_gsp()).get("count")); // 计算商品库存信息
				// System.err.println("threadName:"+Thread.currentThread().getName()+",beforeInventory:"+goods_inventory);
				logger.info("threadName:" + Thread.currentThread().getName() + ",beforeInventory:" + goods_inventory);
				if (goods_inventory < gc.getCount()) {
					throw new RuntimeException(gc.getGoods().getGoods_name() + "库存不足！");
				}
			}
			allGoodsCarts.addAll(gc_list);
			allOrderGoodsStatisticses.addAll(orderGoodsStatisticses);
		}

		for (Map<String, Object> map : orderForms) {
			OrderForm orderForm = (OrderForm) map.get("of");
			if (orderForm.getOrder_main() == 1) {
				mainOf = orderForm;
			} else { // 保存子订单
				save(orderForm);

				Map order_map = new HashMap();
				order_map.put("order_id", orderForm.getId());
				order_map.put("order_goods_info", orderForm.getGoods_info());
				child_order_maps.add(order_map);
			}
		}

		// 如果是多个店铺的订单同时提交，则记录子订单信息到主订单中，用在买家中心统一显示及统一付款
		if (child_order_maps.size() > 0) {
			mainOf.setChild_order_detail(Json.toJson(child_order_maps, JsonFormat.compact()));
		}
		save(mainOf);

		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setOf(mainOf);
		ofl.setLog_info("提交订单");
		ofl.setLog_user(SecurityUserHolder.getCurrentUser());
		this.orderFormLogService.save(ofl);

		// 更新库存
		String msg = goodsService.subtract_goods_inventory(mainOf);
		if (!StringUtils.isNullOrEmpty(msg)) {
			throw new RuntimeException(msg);
		}

		// 删除已经提交订单的购物车信息
		for (GoodsCart gc : allGoodsCarts) {
			if ("combin".equals(gc.getCart_type()) && gc.getCombin_main() == 1) { // 购物车提交订单时如果为组合套装购物车，只提交组合套装主购物车，删除主购物车同时删除该套装中其他购物车
				Map combin_map = new HashMap();
				combin_map.put("combin_mark", gc.getCombin_mark());
				combin_map.put("combin_main", 1);
				List<GoodsCart> suits = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.combin_mark=:combin_mark and obj.combin_main!=:combin_main",
								combin_map, -1, -1);
				for (GoodsCart suit : suits) {
					gc.getGsps().clear();
					this.goodsCartService.delete(suit.getId());
				}
			}
			gc.getGsps().clear();
			this.goodsCartService.delete(gc.getId());
		}

		orderGoodsStatisticsService.batchSave(allOrderGoodsStatisticses);
	}

	@Transactional(readOnly = true)
	public OrderForm getObjByProperty(String construct, String propertyName, String value) {
		return this.orderFormDao.getBy(construct, propertyName, value);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderForm> getUnSynchronizeOrders(Long id) {
		// 声明待同步订单列表集合
		List<OrderForm> resultList = new ArrayList<OrderForm>();
		List<String> syncOrderIdList = new ArrayList<String>();
		// 根据获得的ID值查询出对应的订单对象
		logger.info("开始查询数据库中订单主键ID值为" + id + "的订单对象数据。");
		OrderForm orderForm = this.getObjById(id);
		// 判断是否查询到对应订单数据
		if (!StringUtils.isNullOrEmpty(orderForm)) {
			logger.info("查询到主键ID值为" + orderForm.getId() + "的订单数据，订单号值为" + orderForm.getOrder_id());
			// 首先校验当前订单是否有同步需求
			GoodsConfig goodsConfig = orderForm.getGoodsConfig();
			// 从配置中取出商品来源剔除列表数据
			String[] excludeList = PropertyUtil.getProperty("exclude-list").split(",");
			// 如果存在商品配置项且不是普通商品则表示需要进行同步操作
			// if (!StringUtils.isNullOrEmpty(goodsConfig) && !GoodsConfig.CODE_PTSP.equals(goodsConfig.getConfigCode())) {
			if (!StringUtils.isNullOrEmpty(goodsConfig) && !ArrayUtils.contains(excludeList, goodsConfig.getConfigCode())) {
				resultList.add(orderForm);
				syncOrderIdList.add(orderForm.getOrder_id());
			}
			// 然后开始检查是否有子订单,直接通过查询数据的结果判断是否有子订单数据
			Map<String, String> params = new HashMap<String, String>();
			params.put("main_order_id", orderForm.getOrder_id());
			logger.info("开始查询主订单号为" + orderForm.getOrder_id() + "的子订单数据集合。");
			// 查询子订单对象
			List<OrderForm> childOrderList = this.query(
					"FROM OrderForm WHERE mainOrderId=:main_order_id and order_id<>:main_order_id", params, -1, -1);
			logger.info("根据主订单号" + orderForm.getOrder_id() + "查询子订单数据集合结束，一共查询到" + childOrderList.size() + "条子订单数据");
			// 如果子订单数据集合不为空则进行下一步业务逻辑处理
			if (!StringUtils.isNullOrEmpty(childOrderList)) {
				for (OrderForm childOrderForm : childOrderList) {
					// 首先校验当前订单是否有同步需求
					GoodsConfig childGoodsConfig = childOrderForm.getGoodsConfig();
					// 如果存在商品配置项且不是普通商品则表示需要进行同步操作
					/*
					 * if (!StringUtils.isNullOrEmpty(childGoodsConfig) &&
					 * !GoodsConfig.CODE_PTSP.equals(childGoodsConfig.getConfigCode())) {
					 */
					if (!StringUtils.isNullOrEmpty(goodsConfig)
							&& !ArrayUtils.contains(excludeList, childGoodsConfig.getConfigCode())) {
						resultList.add(childOrderForm);
						syncOrderIdList.add(orderForm.getOrder_id());
					}
				}
			}
			if (!StringUtils.isNullOrEmpty(resultList))
				logger.info("已经准备好待同步" + ArrayUtils.toString(syncOrderIdList) + "的订单数据！");
		} else {
			logger.warn("无法在数据库中查询到主键ID值为" + id + "的订单数据，同步订单业务逻辑结束！");
		}
		return resultList;
	}

	public Map goodsCart2(HttpServletRequest request, HttpServletResponse response, String gcs, String giftids,
			Long addr_id, Boolean isWap) throws Exception {
		List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response); // 用户的所有购物车
		if (null == carts || carts.size() <= 0) {
			throw new Exception("购物车信息为空");
		}
		Map erpMap = this.orderFormTools.calEnoughReducePrice(carts, gcs);
		if ("".equals((String) erpMap.get("gcIds"))) {
			throw new Exception("请勿篡改请求参数！");
		}
		gcs = (String) erpMap.get("gcIds"); // 用户选择的最终有效的购物车的id集
		carts = (List<GoodsCart>) erpMap.get("carts"); // 用户选择的最终有效的购物车

		boolean goods_cod = true; // 默认支持货到付款，但只要有一款产品不支持货到付款，这个订单就不支持货到付款
		int tax_invoice = 1; // 默认可以开具增值税发票，但只要存在一款产品不支持增值税发票，整个订单就不可以开具增值税发票
		int needId = 0;// 用户购买商品时是否需要填写身份证，只要用户购买的商品中有一个需要身份证，则就需要填写身份证购买(此值不能由前台传过来)
		boolean isGoodsValid = true; // 检查商品状态是否为已上架
		boolean isStoreValid = true; // 检查店铺状态
		Goods g = null;
		Set<Object> store_list = new HashSet<Object>();
		for (GoodsCart gc : carts) {
			g = gc.getGoods();
			if (1 == g.getGoods_type() && 15 != g.getGoods_store().getStore_status()) {// 非营业状态
				isStoreValid = false;
				break;
			}
			if (g.getGoods_status() != 0) {
				isGoodsValid = false;
				break;
			}
			if (g.getGoods_type() == 1) {
				store_list.add(g.getGoods_store().getId());
			} else {
				store_list.add("self");
			}
			if (needId == 0 && g.getGoodsConfig().getNeedId() == 1) {
				needId = 1;
			}
		}
		if (!isStoreValid) {
			throw new Exception("商品（" + g.getGoods_name() + "）已失效，请重新选择商品后再结算。");
		}
		if (!isGoodsValid) {// 用户选择的购物车中有未上架的商品
			throw new Exception("商品（" + g.getGoods_name() + "）已失效，请重新选择商品后再结算。");
		}

		int selfPickupEnabled = 0; // 是否显示到店自提选项。1：显示,0：不显示。显示到店自提的条件：用户购买的商品全部是自营且海信商品
		if (store_list.size() == 1 && store_list.contains("self")) {
			selfPickupEnabled = 1;
			for (GoodsCart gc : carts) {
				if (!GoodsConfig.CODE_HX.equals(gc.getGoods().getGoodsConfig().getConfigCode())) {
					selfPickupEnabled = 0;
					break;
				}
			}
		}

		// 查询收货地址
		List<Address> addrs = null;
		String hqlPart = "select obj from Address obj where obj.user.id=:user_id ";
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		if (addr_id != null) {
			params.put("id", addr_id);
			hqlPart = hqlPart + "and obj.id=:id ";
		}
		hqlPart += "order by obj.default_val desc,obj.addTime desc";
		int beginIdx = -1, maxCount = -1;
		if (isWap) {
			beginIdx = 0;
			maxCount = 1;
		}
		addrs = this.addressService.query(hqlPart, params, beginIdx, maxCount);
		// 默认选中的收货地址
		Address defaultAddr = null;
		if (addrs.size() > 0) {
			defaultAddr = addrs.get(0);
		}

		List<Goods> ac_goodses = new ArrayList<Goods>(); // 用户选择的满就赠活动的的赠品，在下面会作初步校验，在后面还要作进一步的校验，防止前端篡改数据
		if (!StringUtils.isNullOrEmpty(giftids)) {
			String[] gift_ids = giftids.split(",");
			for (String gift_id : gift_ids) {
				Long id = CommUtil.null2Long(gift_id);
				if (id != -1l) {
					Goods goods = this.goodsService.getObjById(id);
					if (goods != null && goods.getOrder_enough_if_give() == 1 && goods.getBuyGift_id() != null
							&& store_list.contains(goods.getGoods_store() == null ? "self" : goods.getGoods_store().getId())) {
						ac_goodses.add(goods);
					}
				}
			}
		}

		Map<Object, Map<Long, Set<Long>>> giftCartMapping = new HashMap<Object, Map<Long, Set<Long>>>();// key:店铺id或self；value为Map:key:赠品的id，value:赠品对应的购物车的id集
		Map<Object, Set<Long>> storeCartMapping = new HashMap<Object, Set<Long>>();// key:店铺id或self；value:此店铺对应的购物车的id集
		Map<Object, Map> storeTransportsMapping = new HashMap<Object, Map>();// key:店铺id或self；value:此店铺对应的默认运费计算结果及结果对应的收货地址的id
		double freeShipFeeThreashold = CommUtil.null2Double(PropertyUtil.getProperty("FREE_SHIPFEE_THREASHOLD"));
		double xmO2ODistance = CommUtil.null2Double(PropertyUtil.getProperty("XM_O2O_DISTANCE"));
		Date now = new Date();
		StringBuilder validGiftIds = new StringBuilder();// 合法的赠品的id集合
		List map_list = new ArrayList();// 其中每个元素为一个Map类型的对象，此对象代表了一个店铺（其中保存了用户在此店铺中购买的商品的相关信息）
		Map<Object, Map> hxgoods = new HashMap<Object, Map>();
		for (Object sl : store_list) {
			List<GoodsCart> gc_list = new ArrayList<GoodsCart>(); // gc_list是amount_gc_list中剔除了参加满减活动和满赠活动的商品后的商品对应的购物车集合
			List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();// 用户在当前店铺所选择的商品对应的购物车集合
			Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();// 当前店铺中的相关赠品。key=赠品对象，value：赠品关联的购物车集合
			Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();// key=满减活动的id，value：满减活动关联的购物车集合
			Map<Long, String> erString = new HashMap<Long, String>(); // 保存满减活动在前台页面的提示内容（显示满减活动中最低限额的那个满减的内容）。如有满200减20，满100减10的满减活动，则保存内容为
																		// "活动商品购满 100 元，即可享受满减"
			for (Goods gg : ac_goodses) {
				Store store = gg.getGoods_store();
				if (store == null && "self".equals(sl) || store != null && store.getId().equals(sl)) {
					gift_map.put(gg, new ArrayList<GoodsCart>());
				}
			}
			for (GoodsCart gc : carts) { // 对用户选择的购物车进行遍历
				Goods goods = gc.getGoods();
				Store store = goods.getGoods_store();
				if (store == null && "self".equals(sl) || store != null && store.getId().equals(sl)) {// 获得当前店铺的购物车
					if (gift_map.size() > 0 && goods.getOrder_enough_give_status() == 1 && goods.getBuyGift_id() != null) {
						BuyGift bg = this.buyGiftService.getObjById(goods.getBuyGift_id());
						if (bg.getBeginTime().before(now)) {
							for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()) {
								if (entry.getKey().getBuyGift_id().equals(goods.getBuyGift_id())) {
									entry.getValue().add(gc);
								} else {
									gc_list.add(gc);
								}
							}
						} else {
							gc_list.add(gc);
						}
					} else if (goods.getEnough_reduce() == 1) {
						String er_id = goods.getOrder_enough_reduce_id();
						EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
						if (er.getErbegin_time().before(now)) {
							if (ermap.containsKey(er.getId())) {
								ermap.get(er.getId()).add(gc);
							} else {
								List<GoodsCart> list = new ArrayList<GoodsCart>();
								list.add(gc);
								ermap.put(er.getId(), list);

								Map<String, Double> map = (Map<String, Double>) Json.fromJson(er.getEr_json());
								double k = 99999999.0;
								for (String key : map.keySet()) {
									double en = Double.parseDouble(key);
									if (en < k) {
										k = en;
									}
								}
								erString.put(er.getId(), "活动商品购满" + k + "元，即可享受满减");
							}
						} else {
							gc_list.add(gc);
						}
					} else {
						gc_list.add(gc);
					}

					amount_gc_list.add(gc);

					if (goods_cod && (goods.getGoods_cod() == -1 || goods.getGoods_choice_type() == 1)) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
						goods_cod = false;
					}
					if (tax_invoice == 1 && goods.getTax_invoice() == 0) { // 只要存在一件不支持开具增值税发票的商品，整个订单就不允许开具增值税发票
						tax_invoice = 0;
					}
				}
			}

			Set<Long> cartIds = new HashSet<Long>();
			for (GoodsCart gc : amount_gc_list) {
				cartIds.add(gc.getId());
			}
			storeCartMapping.put(sl, cartIds);

			Map map = new HashMap();
			// 处理满赠，防止前端篡改数据
			if (gift_map.size() > 0) {
				Set<Goods> gift_mapKeysCopy = new HashSet<Goods>(gift_map.keySet());
				for (Goods gift : gift_mapKeysCopy) {
					List<GoodsCart> goodsCarts = gift_map.get(gift);
					if (goodsCarts.size() > 0) {
						double totalPrice = 0.0;
						for (GoodsCart gc : goodsCarts) {
							totalPrice += CommUtil.mul(gc.getPrice(), gc.getCount());
						}
						if (totalPrice < gift.getBuyGift_amount().doubleValue()) {// 用户篡改了数据，取消赠品，并将对应的商品放回普通购物车中去
							gift_map.remove(gift);
							gc_list.addAll(goodsCarts);
						}
					} else {// 用户篡改了数据，取消赠品
						gift_map.remove(gift);
					}
				}
				if (gift_map.size() > 0) {
					map.put("ac_goods", gift_map);
					Map<Long, Set<Long>> storeGiftCartMaping = new HashMap<Long, Set<Long>>();

					for (Goods gift : gift_map.keySet()) {
						validGiftIds.append(gift.getId()).append(",");

						Set<Long> cartIdsTemp = new HashSet<Long>();
						for (GoodsCart gc : (List<GoodsCart>) gift_map.get(gift)) {
							cartIdsTemp.add(gc.getId());
						}
						storeGiftCartMaping.put(gift.getId(), cartIdsTemp);
					}
					giftCartMapping.put(sl, storeGiftCartMaping);
				}
			}

			// 处理满减
			if (ermap.size() > 0) {
				map.put("er_goods", ermap);// 包含未达到满减条件的购物车，这样会在页面上提示对应商品参加了满减活动
				map.put("erString", (Map<Long, String>) erpMap.get("erString"));// 虽然erpMap包含所有店铺的满减活动，但因为满减活动都是店铺范围内的，所以传erpMap给当前店铺显示没有问题
			}
			double store_total_er_reduce = 0.0;
			Map er_json = (Map) Json.fromJson((String) erpMap.get("er_json"));
			for (Long erId : ermap.keySet()) {
				if (er_json.containsKey(erId)) {
					store_total_er_reduce += (Double) er_json.get("reduce_" + erId);
				}
			}
			map.put("store_enough_reduce", store_total_er_reduce);

			map.put("store_id", sl);
			if (!"self".equals(sl)) {
				map.put("store", this.storeService.getObjById((Long) sl));
			}

			map.put("store_goods_price", orderFormTools.calCartPrice(amount_gc_list, ""));// store_goods_price为已减去满减优惠的店铺商品总金额，可用的优惠券是根据此金额来判断的，而不是根据原商品总额来判断的

			map.put("gc_list", gc_list);

			List<Long> freeShipFeeGoodsCartIds_o2o = new ArrayList<Long>();// 当前店铺中满足星美o2o免费配送的购物车的id列表
			List<Integer> freeShipFeeCountsForGoodsCart_o2o = new ArrayList<Integer>();// 对应freeShipFeeGoodsCartIds_o2o中的每个购物车可以免运费的购买数量

			if (defaultAddr != null) {
				// 针对海信商品，选择发货的星美门店
				List<GoodsCart> hxGoodsCarts = new ArrayList<GoodsCart>();
				for (GoodsCart gc : amount_gc_list) {
					if (GoodsConfig.CODE_HX.equals(gc.getGoods().getGoodsConfig().getConfigCode())) {
						hxGoodsCarts.add(gc);
					}
				}

				logger.info("hxGoodsCarts.size():" + hxGoodsCarts.size() + ", defaultAddr.id:" + defaultAddr.getId());

				if (hxGoodsCarts.size() > 0) {
					Map results = goodsTools.selectCinemaStore(hxGoodsCarts, defaultAddr);
					if (CommUtil.null2Int(results.get("flag")) == 1) {// 分派海信商品到星美门店失败
						throw new Exception("" + results.get("result"));
					}
					List<Map> branches = (List<Map>) results.get("result");
					// 计算满足星美门店o2o两公里免运费的购物车及其中的商品数量
					for (Map store : branches) {
						double distance = (Double) store.get("distance");
						ShipAddress sa = (ShipAddress) store.get("address");
						if (distance <= xmO2ODistance && sa.getO2oCapable() == 1) {
							// TODO:除了上述条件外，可能还会增加消费限额（freeShipFeeThreashold）这一条件(注意本类中还有一处order_address函数中有和此处类似的关于海信o2o的代码要修改)。
							List<Long> goodscart_list = (List<Long>) store.get("goodscart_list");
							List<Integer> goods_count_list = (List<Integer>) store.get("goods_count_list");
							for (int i = 0; i < goodscart_list.size(); i++) {
								Long gcId = goodscart_list.get(i);
								Integer freeCount = goods_count_list.get(i);
								int idx = freeShipFeeGoodsCartIds_o2o.indexOf(gcId);
								if (idx >= 0) {// 已存在
									freeShipFeeCountsForGoodsCart_o2o.set(idx, freeShipFeeCountsForGoodsCart_o2o.get(idx)
											+ freeCount);
								} else {
									freeShipFeeGoodsCartIds_o2o.add(gcId);
									freeShipFeeCountsForGoodsCart_o2o.add(freeCount);
								}
							}
						}
					}

					// 将各店铺的海信商品的分派信息保存到session中
					// 将计算出来的结果保存到session中以便用户提交订单时直接取出来而不需要重新计算，另一方面，重新计算的结果可能不同，这样会导致用户提交订单时看到的运费与实际订单中的运费有差异
					Map storeHXgoods = new HashMap();
					storeHXgoods.put("goodsDispached", branches);
					storeHXgoods.put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2o);
					storeHXgoods.put("freeShipFeeCountsForGoodsCart_o2o", freeShipFeeCountsForGoodsCart_o2o);
					hxgoods.put(sl, storeHXgoods);
				}
			}
			map.put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2o);
			map.put("freeShipFeeCountsForGoodsCart_o2o", freeShipFeeCountsForGoodsCart_o2o);

			// begin dengyuqi 2016-3-4
			Map<String, List<OrderFreightInfo>> freightInfos = new HashMap<String, List<OrderFreightInfo>>(); // key为发货方式(平邮、快递、EMS)，value为运费信息
			// end

			String defaulAddrAreaId = "";
			if (defaultAddr != null) {
				defaulAddrAreaId = defaultAddr.getArea().getId().toString();
			}
			List<SysMap> storeTransports = transportTools.calStoreCartsTransFee(amount_gc_list, defaulAddrAreaId,
					freeShipFeeGoodsCartIds_o2o, freeShipFeeCountsForGoodsCart_o2o, freightInfos);
			Map transportAndAddr = new HashMap();
			transportAndAddr.put("receiverAddrId", defaultAddr == null ? null : defaultAddr.getId());// 保存当前计算的运费结果对应的收货地址，以便在goods_cart3中进行校验
			transportAndAddr.put("transports", storeTransports);

			// begin dengyuqi 2016-3-4
			transportAndAddr.put("freightInfos", freightInfos);
			// end

			storeTransportsMapping.put(sl, transportAndAddr);
			map.put("transports", storeTransports);
			map_list.add(map);
		}
		// 生成7天时间区间
		List<String> days = new ArrayList<>();
		List<String> day_list = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			days.add(CommUtil.formatTime("MM-dd", cal.getTime()) + "<br />"
					+ this.generic_day(cal.get(Calendar.DAY_OF_WEEK)));
			day_list.add(CommUtil.formatTime("MM-dd", cal.getTime()) + this.generic_day(cal.get(Calendar.DAY_OF_WEEK)));
		}

		Map map = new HashMap();
		map.put("all", erpMap.get("all"));
		map.put("reduce", erpMap.get("reduce"));
		map.put("selfPickupEnabled", selfPickupEnabled);
		map.put("addrs", addrs);
		map.put("days", days);
		map.put("day_list", day_list);
		map.put("map_list", map_list);
		map.put("goods_cod", goods_cod);
		map.put("tax_invoice", tax_invoice);
		map.put("validGiftIds", validGiftIds.toString());
		map.put("needId", needId);
		map.put("hxgoods", hxgoods);
		map.put("store_list", store_list);
		map.put("giftCart", giftCartMapping);
		map.put("storeCart", storeCartMapping);
		map.put("storeTransports", storeTransportsMapping);
		return map;
	}

	@Transactional(readOnly = false)
	public Map goodsCart3(HttpServletRequest request, User buyer, String[] gift_ids, Address addr, String[] gcIds,
			String delivery_time, DeliveryAddress deliveryAddr, String payType, ShipAddress xmStore, Boolean isWap,
			Map<Object, Map<Long, Set<Long>>> giftCartMapping, Map<Object, Map> hxGoods, Map<Object, Map> transports,
			Set<Object> storeIds, Map<Object, Set<Long>> storeCart) throws Exception {

		String seckill_info = null;
		List<GoodsCart> order_carts = new ArrayList<GoodsCart>();// 用户选择要购买的商品的购物车
		for (String gcId : gcIds) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gcId));
			// 校验支付方式
			if (gc.getGoods().getGoods_cod() == -1 && !"online".equals(payType)) { // 商品不支持货到付款
				throw new Exception("本订单不支持货到付款");
			}

			// 校验秒杀订单
			if (gc.getGoods().getSeckill_buy() != 0) { // 秒杀订单
				if (gc.getGoods().getSeckill_buy() != 2) { // 秒杀已停止
					throw new Exception("秒杀失败，活动已结束");
				}
				String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
				Map params = new HashMap();
				params.put("goods_id", gc.getGoods().getId());
				params.put("gg_status", 2);
				SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
				boolean isOK = this.validateSeckillOrderForm(buyer.getId(), seckillGoods.getId());
				if (!isOK) { // 同一用户ID只能购买一次同一个秒杀商品
					throw new Exception("秒杀失败，您购买过'" + gc.getGoods().getGoods_name() + "',不能重复购买");
				}
				seckill_info = "{good_id:" + gc.getGoods().getId() + ",seckill_goods_id:" + seckillGoods.getId() + "}";

				// 重置购买数量为1，以防前端篡改
				gc.setCount(1);
				this.goodsCartService.update(gc);
			}
			order_carts.add(gc);
		}

		// 秒杀订单不能包含多个商品
		if (seckill_info != null && order_carts.size() > 1) {
			throw new Exception("秒杀失败，请确保订单中只有一件秒杀商品且不包含非秒杀商品");
		}

		// 赠品
		List<Goods> gift_goods = new ArrayList<Goods>();// 当前用户的所有赠品
		for (String gid : gift_ids) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
			if (goods != null) {
				gift_goods.add(goods);// 赠品已在goods_cart2中进行了校验，故此处不再进行校验
			}
		}

		List<Map<String, Object>> allForms = new ArrayList<Map<String, Object>>();
		double all_of_price = 0;// 所有订单的最终费用总和
		Date date = new Date();
		String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		for (Object sid : storeIds) {
			Store store = null;
			if (!"self".equals(sid.toString())) {
				store = this.storeService.getObjById((Long) sid);
			}

			OrderForm of = new OrderForm();
			of.setTotalPrice(new BigDecimal(0.0));
			List<GoodsCart> gc_list = new ArrayList<GoodsCart>(); // 当前店铺的购物车
			List<Map> hxGoodsDispached = new ArrayList<Map>();// 当前店铺中的海信商品选择发货门店的情况
			if (hxGoods != null && hxGoods.containsKey(sid)) {
				hxGoodsDispached = (List<Map>) ((Map) hxGoods.get(sid)).get("goodsDispached");
			}

			// 购物车信息
			Set<Long> cartIds = storeCart.get(sid);
			for (GoodsCart gc : order_carts) {
				if (cartIds.contains(gc.getId())) {
					gc_list.add(gc);
				}
			}

			// 赠品信息，放在同一店铺的第一个订单中
			if (giftCartMapping.containsKey(sid)) {
				List<Map> gift_map = new ArrayList<Map>();

				Map<Long, Set<Long>> storeGiftCartMapping = giftCartMapping.get(sid);
				for (Goods gift : gift_goods) {
					if (storeGiftCartMapping.containsKey(gift.getId())) {// 当前店铺的赠品
						Set<Long> cs = storeGiftCartMapping.get(gift.getId());
						Set<GoodsCart> goodsCarts = new HashSet<GoodsCart>();
						for (GoodsCart g : gc_list) {
							if (cs.contains(g.getId())) {
								goodsCarts.add(g);
							}
						}
						gift_map.add(this.createFormGiftInfo(request, gift, goodsCarts));
					}
				}
				of.setGift_infos(Json.toJson(gift_map, JsonFormat.compact()));
				of.setWhether_gift(1);
			}

			// 满减信息，放在同一店铺的第一个订单中
			Map ermap = this.orderFormTools.calEnoughReducePrice(gc_list, "");
			String er_json = (String) ermap.get("er_json");
			double all_goods = Double.parseDouble(ermap.get("all").toString());// 当前店铺的商品总额
			double reduce = Double.parseDouble(ermap.get("reduce").toString());// 当前店铺满减的总金额
			of.setEnough_reduce_info(er_json);
			BigDecimal theReduce = BigDecimal.valueOf(reduce);
			of.setEnough_reduce_amount(theReduce);
			of.setTotalPrice(theReduce.negate().add(of.getTotalPrice())); // 用满减金额初始化订单的总金额，负数。注意：在最终生成的订单中，某个订单的totalPrice字段的值可能为负数（因为优惠券和满减金额被放在了此订单中，且优惠券面额和满减金额之和可能会大于此订单的
																			// 商品总额与运费之和）

			// 优惠券信息，放在同一店铺的第一个订单中
			String coupon_id = request.getParameter("coupon_id_" + sid.toString());// 前台只可使用一张优惠券
			CouponInfo ci = this.couponInfoService.getObjById(CommUtil.null2Long(coupon_id));
			if (ci != null && seckill_info == null) { // 秒杀订单不允许使用优惠券
				if (ci.getStatus() == 0 && buyer.getId().equals(ci.getUser().getId())) {
					// 检查优惠券是否满足使用条件
					Coupon coupon = ci.getCoupon();
					boolean flag = !date.before(coupon.getCoupon_begin_time()) && !date.after(coupon.getCoupon_end_time());
					flag = flag
							&& (coupon.getCoupon_type() == 0 ? "self".equals(sid.toString()) : coupon.getStore().getId()
									.equals((Long) sid));
					double goodsAmountAfterReduced = Double.parseDouble(ermap.get("after").toString());
					flag = flag && goodsAmountAfterReduced >= coupon.getCoupon_order_amount().doubleValue();// 使用扣除满减后的商品总金额来判断是否可以使用优惠券
					if (flag) {
						ci.setStatus(1);
						this.couponInfoService.update(ci);
						Map coupon_map = new HashMap();
						coupon_map.put("couponinfo_id", ci.getId());
						coupon_map.put("couponinfo_sn", ci.getCoupon_sn());
						coupon_map.put("coupon_amount", ci.getCoupon().getCoupon_amount());
						coupon_map.put("coupon_order_amount", ci.getCoupon().getCoupon_order_amount()); // 优惠券限制金额
						coupon_map.put("coupon_return_amount", 0); // 优惠券已退金额
						double rate = CommUtil.div(ci.getCoupon().getCoupon_amount(), all_goods);
						coupon_map.put("coupon_goods_rate", rate);
						of.setCoupon_info(Json.toJson(coupon_map, JsonFormat.compact()));
						of.setTotalPrice(coupon.getCoupon_amount().negate().add(of.getTotalPrice())); // 优惠券可减免的金额，设为负数。
					}
				}
			}

			// 秒杀信息，放在同一店铺的第一个订单中
			of.setSeckill_info(seckill_info);
			of.setAddTime(date);
			String orderId = order_suffix + buyer.getId() + ("self".equals(sid.toString()) ? "0" : sid.toString())
					+ CommUtil.randomInt(2);
			of.setOrder_id(orderId);

			of.setOrder_status(10);
			of.setUser_id(buyer.getId().toString());
			of.setUser_name(buyer.getUserName());
			of.setInvoiceType(CommUtil.null2Int(request.getParameter("invoiceType")));
			of.setInvoice(CommUtil.convertScriptTag(request.getParameter("invoice")));
			of.setMsg(CommUtil.convertScriptTag(request.getParameter("msg_" + sid.toString())));
			of.setPayType(payType);
			if (isWap) {
				of.setOrder_type("weixin"); // 设置为PC网页订单
			} else {
				of.setOrder_type("web"); // 设置为PC网页订单
			}

			of.setDelivery_time(CommUtil.convertScriptTag(delivery_time));

			if ("self".equals(sid.toString())) {
				of.setOrder_form(1); // 平台自营商品订单
			} else {
				of.setOrder_form(0); // 商家商品订单
				of.setStore_id(store.getId().toString());
				of.setStore_name(store.getStore_name());
			}

			if (null != xmStore) {// 到店自提
				of.setDelivery_type(2);
				of.setShip_addr_id(xmStore.getId());
				of.setShip_addr(xmStore.getSa_addr());
				of.setShip_price(new BigDecimal(0));
				of.setShipCode("星美到店自提");

				Area sa = this.areaService.getObjById(xmStore.getSa_area_id());
				of.setReceiverArea(sa);
				of.setReceiver_area(sa.getParent().getParent().getAreaName() + sa.getParent().getAreaName()
						+ sa.getAreaName());
				of.setReceiver_area_info(xmStore.getSa_addr());
				of.setReceiver_Name(buyer.getUserName());
				String receiverMobile = buyer.getUserName();
				String receiverTel = buyer.getUserName();
				if (!StringUtils.isNullOrEmpty(buyer.getMobile())) {
					receiverMobile = buyer.getMobile();
				}

				if (!StringUtils.isNullOrEmpty(buyer.getTelephone())) {
					receiverTel = buyer.getTelephone();
				}
				of.setReceiver_mobile(receiverMobile);
				of.setReceiver_telephone(receiverTel);
				of.setReceiver_zip(xmStore.getSa_zip());
			} else {
				of.setReceiver_Name(addr.getTrueName());
				of.setReceiver_area(addr.getArea().getParent().getParent().getAreaName()
						+ addr.getArea().getParent().getAreaName() + addr.getArea().getAreaName());
				of.setReceiver_area_info(addr.getArea_info());
				of.setReceiver_mobile(addr.getMobile());
				of.setReceiver_telephone(addr.getTelephone());
				of.setReceiver_zip(addr.getZip());
				of.setReceiverArea(addr.getArea());
				// 收货地址中是否需要身份证在拆单中根据GoodsConfig的配置来填写

				// 运费信息，放在同一店铺的第一个订单中
				Map smsAndAddr = transports.get(sid);
				Long receiverAddrId = (Long) smsAndAddr.get("receiverAddrId");
				if (receiverAddrId == null || !receiverAddrId.equals(addr.getId())) {
					throw new Exception("请设置一个收货地址");
				}
				List<SysMap> sms = (List<SysMap>) smsAndAddr.get("transports");
				String transport = request.getParameter("transport_" + sid);
				logger.info("date:" + date + ",transport:" + transport);
				float transFee = -1;
				for (SysMap sm : sms) {
					String keyStr = sm.getKey().toString();
					if (keyStr.indexOf(transport) >= 0) {
						transport = keyStr;
						transFee = (float) sm.getValue();
						break;
					}
				}
				if (transFee == -1) {// 用户上传的数据有问题，则为其自动选择一个物流方式
					throw new Exception("物流信息错误！");
				}

				// begin dengyuqi 2016-3-4 保存运费信息
				Map<String, List<OrderFreightInfo>> freightInfoMaps = (Map<String, List<OrderFreightInfo>>) smsAndAddr
						.get("freightInfos");
				if (null != freightInfoMaps) {
					for (Entry entry : freightInfoMaps.entrySet()) {
						String keyStr = entry.getKey().toString();
						if ((transport + "_freightInfos").equals(keyStr)) {
							List<OrderFreightInfo> freightInfos = (List<OrderFreightInfo>) entry.getValue();
							of.setFreight_info(Json.toJson(freightInfos));
							break;
						}
					}
				}
				// end

				of.setTransport(transport);
				BigDecimal transFeeBD = new BigDecimal(transFee);
				of.setShip_price(transFeeBD);
				of.setTotalPrice(transFeeBD.add(of.getTotalPrice()));

				if (deliveryAddr == null) {// 快递
					of.setDelivery_type(0);
				} else {// 自提点
					of.setDelivery_type(1);

					String service_time = "全天";
					if (deliveryAddr.getDa_service_type() == 1) {
						service_time = deliveryAddr.getDa_begin_time() + "点至" + deliveryAddr.getDa_end_time() + "点";
					}
					Map params = new HashMap();
					params.put("id", deliveryAddr.getId());
					params.put("da_name", deliveryAddr.getDa_name());
					params.put("da_content", deliveryAddr.getDa_content());
					params.put("da_contact_user", deliveryAddr.getDa_contact_user());
					params.put("da_tel", deliveryAddr.getDa_tel());
					params.put("da_address", deliveryAddr.getDa_area().getParent().getParent().getAreaName()
							+ deliveryAddr.getDa_area().getParent().getAreaName() + deliveryAddr.getDa_area().getAreaName()
							+ deliveryAddr.getDa_address());
					params.put("da_service_day",
							this.deliveryAddressTools.query_service_day(deliveryAddr.getDa_service_day()));
					params.put("da_service_time", service_time);
					of.setDelivery_address_id(deliveryAddr.getId());
					of.setDelivery_info(Json.toJson(params, JsonFormat.compact()));
				}
			}

			// 拆解当前店铺的订单
			List<Map<String, Object>> storeAllOfs = this.refineOrderForm(request, sid, store, buyer, of, gc_list,
					xmStore == null ? false : true, hxGoodsDispached, addr, of.getEnough_reduce_info(), of.getCoupon_info());
			allForms.addAll(storeAllOfs);
		}

		OrderForm main_order = (OrderForm) allForms.get(allForms.size() - 1).get("of");// 最后一个订单作为主订单
		main_order.setOrder_main(1);

		for (int i = 0; i < allForms.size(); i++) {
			OrderForm of = (OrderForm) allForms.get(i).get("of");
			all_of_price = CommUtil.add(all_of_price, of.getTotalPrice());
			of.setMainOrderId(main_order.getOrder_id());
		}

		this.saveOrderForm(allForms);

		Map map = new HashMap();
		map.put("all_of_price", all_of_price);
		map.put("main_order", main_order);
		// System.err.println("threadName:"+Thread.currentThread().getName()+",outTime:"+new Date());
		logger.info("threadName:" + Thread.currentThread().getName() + ",outTime:" + new Date());
		return map;
	}

	/**
	 * 构造用户购物车中商品保存到订单中的内容，以Map形式返回
	 * 
	 * @param request
	 * @param storeId
	 *            可能是店铺的Long类型的id，也可能是String类型的self（表示自营）
	 * @param buyer
	 * @param gc
	 * @param realGoodsCount
	 *            ：购物车gc中在当前店铺实际购买到的商品数量。若为null或0，则表示实际购买到的数量即为gc.getCount()的值
	 * @param enoughReduceInfo
	 *            满减信息
	 * @param couponInfo
	 *            优惠券信息
	 * @param gcs
	 * @return
	 */
	private Map createFormGoodsInfo(HttpServletRequest request, Object storeId, User buyer, GoodsCart gc,
			Integer realGoodsCount, String enoughReduceInfo, String couponInfo, List<GoodsCart> gcs) {
		Goods goods = gc.getGoods();
		int count = gc.getCount();
		if (realGoodsCount != null && realGoodsCount > 0) {
			count = realGoodsCount;
		}
		Map json_map = new HashMap();
		json_map.put("goods_id", goods.getId());
		json_map.put("goods_name", goods.getGoods_name());
		json_map.put("goods_choice_type", goods.getGoods_choice_type());
		String goods_type = "";
		if ("combin".equals(gc.getCart_type())) {
			if (gc.getCombin_main() == 1) {
				goods_type = "combin";
				json_map.put("combin_suit_info", gc.getCombin_suit_info());// 设置商品组合套装信息
			}
		} else if ("group".equals(gc.getCart_type())) {
			goods_type = "group";
		} else if ("seckill".equals(gc.getCart_type())) {
			goods_type = "seckill";
		}
		json_map.put("goods_type", goods_type);
		json_map.put("goods_count", count);
		json_map.put("goods_price", gc.getPrice()); // 商品单价
		json_map.put("goods_all_price", CommUtil.mul(gc.getPrice(), count)); // 商品总价
		String goodsUrl = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
		String goods_snapshoot = this.snapshotTools.createGoodsSnapshot(goodsUrl, buyer.getId().toString());
		json_map.put("goods_gsp_val", gc.getSpec_info());
		json_map.put("goods_gsp_ids", gc.getCart_gsp());
		json_map.put("goods_snapshoot", goods_snapshoot);
		if (goods.getGoods_main_photo() != null) {
			json_map.put("goods_mainphoto_path", goods.getGoods_main_photo().getPath() + "/"
					+ goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt());
		} else {
			json_map.put("goods_mainphoto_path", this.configService.getSysConfig().getGoodsImage().getPath() + "/"
					+ this.configService.getSysConfig().getGoodsImage().getName());
		}
		if (!"self".equals(storeId.toString())) { // 商家商品
			// begin dengyuqi 2016-3-4
			// json_map.put("goods_commission_price", this.getGoodscartCommission(gc,enoughReduceInfo,couponInfo)); //
			// 设置该商品总佣金
			// json_map.put("goods_payoff_price",
			// CommUtil.subtract(CommUtil.mul(gc.getPrice(), count),
			// this.getGoodscartCommission(gc,enoughReduceInfo,couponInfo))); // 该商品结账价格=该商品总价格-商品总佣金
			// json_map.put("goods_commission_rate", gc.getGoods().getGc().getCommission_rate()); // 设置该商品的佣金比例
			double commission = this.getGoodscartCommission(gc, gcs, enoughReduceInfo, couponInfo);
			json_map.put("goods_commission_price", commission); // 设置该商品总佣金
			// json_map.put("goods_payoff_price",
			// CommUtil.subtract(CommUtil.mul(gc.getPrice(), count), commission)); // 该商品结账价格=该商品总价格-商品总佣金
			json_map.put("goods_payoff_price",
					CommUtil.formatMoney(this.getGoodscartPayoff(gc, gcs, enoughReduceInfo, couponInfo) - commission));// 该商品结账价格=该商品总价格-满减优惠-优惠券优惠-商品总佣金（优惠按商品比例计算）
			json_map.put("goods_commission_rate", getCommissionRate(gc.getGoods())); // 设置该商品的佣金比例
			// end

			String goods_domainPath = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
			String store_domainPath = CommUtil.getURL(request) + "/store_" + goods.getGoods_store().getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store().getStore_second_domain() != "") {
				String store_second_domain = "http://" + goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/goods_" + goods.getId() + ".htm";
				store_domainPath = store_second_domain;
			}
			json_map.put("goods_domainPath", goods_domainPath); // 商品二级域名路径
			json_map.put("store_domainPath", store_domainPath); // 店铺二级域名路径
		} else {
			json_map.put("goods_domainPath", CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm"); // 商品二级域名路径
		}

		return json_map;
	}

	/**
	 * 构造指定赠品保存到订单中的内容，以Map形式返回
	 * 
	 * @param request
	 * @param goods
	 *            赠品
	 * @param refCarts
	 *            与此赠品对象关联的购物车对象列表，不能为null，而且size()要大于0，否则会返回null
	 * @return
	 */
	private Map createFormGiftInfo(HttpServletRequest request, Goods goods, Set<GoodsCart> refCarts) {
		if (refCarts == null || refCarts.size() == 0) {
			return null;
		}
		Map map = new HashMap();
		map.put("goods_id", goods.getId());
		map.put("goods_name", goods.getGoods_name());
		map.put("goods_main_photo", goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName()
				+ "_small." + goods.getGoods_main_photo().getExt());
		map.put("goods_price", goods.getGoods_current_price());
		String goods_domainPath = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& goods.getGoods_store().getStore_second_domain() != "" && goods.getGoods_type() == 1) {
			String store_second_domain = "http://" + goods.getGoods_store().getStore_second_domain() + "."
					+ CommUtil.generic_domain(request);
			goods_domainPath = store_second_domain + "/goods_" + goods.getId() + ".htm";
		}
		map.put("goods_domainPath", goods_domainPath); // 商品二级域名路径
		map.put("buyGify_id", goods.getBuyGift_id());

		List<Long> goodsIds = new ArrayList<Long>();
		List<Integer> goodsCounts = new ArrayList<Integer>();
		List<Double> goodsPrices = new ArrayList<Double>();
		List<String> goodsSpecInfo = new ArrayList<String>();

		for (GoodsCart gc : refCarts) {
			goodsIds.add(gc.getGoods().getId());
			goodsCounts.add(gc.getCount());
			goodsPrices.add(gc.getPrice().doubleValue());
			goodsSpecInfo.add(gc.getSpec_info());
		}
		map.put("goodsIds", goodsIds);
		map.put("goodsCounts", goodsCounts);
		map.put("goodsPrices", goodsPrices);
		map.put("goodsSpecInfo", goodsSpecInfo);

		return map;
	}
	
	private String refineErmapInfo(List<GoodsCart> currentOrderGCs ,String enoughReduceInfo)
	{
		if(enoughReduceInfo==null || "{}".equals(enoughReduceInfo) || "".equals(enoughReduceInfo))
			return enoughReduceInfo;
		
		//执行满减map拆分
		Map<String, Object> ermap = (Map<String, Object>) Json.fromJson(enoughReduceInfo);
		//{"return_61":0.0,"reduce_61":100.0,"counts_61":[4],"61":[393],"enouhg_61":"199.0","prices_61":[99.00],"all_61":396.0}
		Set<String> keys = ermap.keySet();
		
		for (String key : keys) {
			if (key.matches("^\\d+$")) { // 满减活动id
				List<Integer> goodsIds = (List) ermap.get(key); // 参加该满减活动的商品id列表
				List<Integer> goodsCounts=(List) ermap.get("counts_"+key);
				List<Double> goodsPrices=(List) ermap.get("prices_"+key);
				
				for(GoodsCart gc:currentOrderGCs){
					int pos=goodsIds.indexOf(gc.getGoods().getId().intValue());//由于满减里面用的goods_id都是integer类型，所以必须转为integer才能找的到					
					if(pos!=-1){
						goodsCounts.set(pos, goodsCounts.get(pos)-gc.getCount());
						goodsIds.add(gc.getGoods().getId().intValue());
						goodsCounts.add(gc.getCount());
						goodsPrices.add(gc.getPrice().doubleValue());
					}
						
				}
				
				ermap.put(key,goodsIds);
				ermap.put("counts_"+key,goodsCounts);
				ermap.put("prices_"+key, goodsPrices);
			}
		}
		
		return Json.toJson(ermap);
	}

	public List<Map<String, Object>> refineOrderForm(HttpServletRequest request, Object sid, Store store, User buyer,
			OrderForm storeOf, List<GoodsCart> carts, boolean isSelfPickup, List<Map> hxGoodsDispached,
			Address receiverAddr, String enoughReduceInfo, String couponInfo) {
		List<Map<String, Object>> storeAllOfs = new ArrayList<Map<String, Object>>();

		if (isSelfPickup) {// 到店自提，只生成一个订单。直接使用传入的订单对象storeOf作为要返回的订单对象，而不再去new一个新的订单对象
			List<Map> goodsInfo_list = new ArrayList<Map>();
			for (GoodsCart gc : carts) {
				goodsInfo_list.add(this.createFormGoodsInfo(request, sid, buyer, gc, null, enoughReduceInfo, couponInfo,
						carts));
			}
			storeOf.setGoods_info(Json.toJson(goodsInfo_list, JsonFormat.compact()));

			GoodsConfig hxGoodsConf = carts.get(0).getGoods().getGoodsConfig();
			storeOf.setGoodsConfig(hxGoodsConf);
			if (hxGoodsConf.getNeedId() == 1) {
				storeOf.setReceiver_card(receiverAddr.getCard());
			}

			storeOf.setGoods_amount(new BigDecimal(orderFormTools.calCartTotalGoodsAmount(carts, null)));
			storeOf.setTotalPrice(storeOf.getTotalPrice().add(storeOf.getGoods_amount()));

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("of", storeOf);
			map.put("gc_list", carts);

			// begin dengyuqi 2016-4-27
			List<OrderGoodsStatistics> orderGoodsStatisticses = saveOrderGoodsStatistics(storeOf, carts);
			map.put("order_goods_statistics", orderGoodsStatisticses);
			// end dengyuqi 2016-4-27

			storeAllOfs.add(map);
			return storeAllOfs;
		}

		// 对购物车按商品来源进行分组
		Map<GoodsConfig, List<GoodsCart>> cartsGroupedByConfig = new HashMap<GoodsConfig, List<GoodsCart>>();// key:GoodsConfig对象，value:对应的购物车列表
		for (GoodsCart cart : carts) {
			GoodsConfig goodsConfig = cart.getGoods().getGoodsConfig();
			List<GoodsCart> cartsTmp = cartsGroupedByConfig.get(goodsConfig);
			if (cartsTmp == null) {
				cartsTmp = new ArrayList<GoodsCart>();
				cartsGroupedByConfig.put(goodsConfig, cartsTmp);
			}
			cartsTmp.add(cart);
		}

		int orderFormSequence = 0;
		for (GoodsConfig goodsConfig : cartsGroupedByConfig.keySet()) {// 按商品来源进行拆单(普通商品，海信商品，拍拍购商品)
			List<GoodsCart> cartsTmp = cartsGroupedByConfig.get(goodsConfig);
			
			
			if (GoodsConfig.CODE_HX.equals(goodsConfig.getConfigCode())) {// 海信商品，可能需要进一步进行拆单。对非自营店铺中的海信商品也要计算平台返点金额
				double xmO2ODistance = CommUtil.null2Double(PropertyUtil.getProperty("XM_O2O_DISTANCE"));
				for (Map hxGoodsInfo : hxGoodsDispached) {// 按门店进行拆单
					List<GoodsCart> cinemaCarts = new ArrayList<GoodsCart>();
					List<Integer> cinemaCartCounts = new ArrayList<Integer>();
					List<Long> goodscart_list = (List<Long>) hxGoodsInfo.get("goodscart_list");// 购物车的id列表
					List<Integer> goods_count_list = (List<Integer>) hxGoodsInfo.get("goods_count_list");
					ShipAddress cinemaStore = (ShipAddress) hxGoodsInfo.get("address");
					double distance = (Double) hxGoodsInfo.get("distance");

					OrderForm newOf = this.copyOrderFormCommonInfo(storeOf, orderFormSequence++);

					if (distance <= xmO2ODistance && cinemaStore.getO2oCapable() == 1) {
						newOf.setDelivery_type(3);
						newOf.setShipCode("星美直送");
					}

					List<Map> goodsInfo_list = new ArrayList<Map>();
					for (GoodsCart gc : cartsTmp) {
						int idx = goodscart_list.indexOf(gc.getId());
						if (idx >= 0) {
							cinemaCarts.add(gc);
							Integer count = goods_count_list.get(idx);
							cinemaCartCounts.add(count);
							goodsInfo_list.add(this.createFormGoodsInfo(request, sid, buyer, gc, count, enoughReduceInfo,
									couponInfo, carts));
						}
					}
					newOf.setGoods_info(Json.toJson(goodsInfo_list, JsonFormat.compact()));

					newOf.setShip_addr_id(cinemaStore.getId());
					newOf.setShip_addr(cinemaStore.getSa_addr());

					newOf.setGoodsConfig(goodsConfig);
					if (goodsConfig.getNeedId() == 1) {
						newOf.setReceiver_card(receiverAddr.getCard());
					}

					newOf.setGoods_amount(new BigDecimal(orderFormTools.calCartTotalGoodsAmount(cinemaCarts,
							cinemaCartCounts)));

					// 拼装当前门店订单的购物车集合
					List<GoodsCart> currentOrderGCs = new ArrayList<>();
					for (int j = 0; j < cinemaCarts.size(); j++) {
						GoodsCart gc = new GoodsCart();
						gc.setId(cinemaCarts.get(j).getId());
						gc.setCount(cinemaCartCounts.get(j));
						gc.setPrice(cinemaCarts.get(j).getPrice());
						gc.setGoods(cinemaCarts.get(j).getGoods());
						currentOrderGCs.add(gc);
					}
					
					//对满减进行拆分［满减计算过程是按照比例计算的］
					String childEnoughReduceInfo=refineErmapInfo(currentOrderGCs, enoughReduceInfo);
					
					newOf.setTotalPrice(this.getOrderTotalPrice(currentOrderGCs, carts, childEnoughReduceInfo, couponInfo));

					if (!"self".equals(sid)) {
						newOf.setCommission_amount(new BigDecimal(this.getOrderCommission(cinemaCarts, carts,
								enoughReduceInfo, couponInfo)));
					}

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("of", newOf);
					map.put("gc_list", cinemaCarts);

					// begin dengyuqi 2016-4-27
					List<OrderGoodsStatistics> orderGoodsStatisticses = saveOrderGoodsStatistics(storeOf, cartsTmp);
					map.put("order_goods_statistics", orderGoodsStatisticses);
					// end dengyuqi 2016-4-27

					storeAllOfs.add(map);
				}

			} else {// 非海信商品
				OrderForm newOf = this.copyOrderFormCommonInfo(storeOf, orderFormSequence++);

				List<Map> goodsInfo_list = new ArrayList<Map>();
				for (GoodsCart gc : cartsTmp) {
					goodsInfo_list.add(this.createFormGoodsInfo(request, sid, buyer, gc, null, enoughReduceInfo, couponInfo,
							carts));
				}
				newOf.setGoods_info(Json.toJson(goodsInfo_list, JsonFormat.compact()));

				newOf.setGoodsConfig(goodsConfig);
				if (goodsConfig.getNeedId() == 1) {
					newOf.setReceiver_card(receiverAddr.getCard());
				}

				newOf.setGoods_amount(new BigDecimal(orderFormTools.calCartTotalGoodsAmount(cartsTmp, null)));
				
				//对满减进行拆分［满减计算过程是按照比例计算的］
				enoughReduceInfo=refineErmapInfo(cartsTmp, enoughReduceInfo);
				
				newOf.setTotalPrice(this.getOrderTotalPrice(cartsTmp, carts, enoughReduceInfo, couponInfo));

				if (!"self".equals(sid)) {
					newOf.setCommission_amount(new BigDecimal(this.getOrderCommission(cartsTmp, carts, enoughReduceInfo,
							couponInfo)));
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("of", newOf);
				map.put("gc_list", cartsTmp);

				// begin dengyuqi 2016-4-27
				List<OrderGoodsStatistics> orderGoodsStatisticses = saveOrderGoodsStatistics(storeOf, cartsTmp);
				map.put("order_goods_statistics", orderGoodsStatisticses);
				// end dengyuqi 2016-4-27

				storeAllOfs.add(map);
			}
		}
		// 将店铺的初始订单中的秒杀、赠品、满减、优惠券信息、总运费保存到店铺的某个订单中去(可能是主订单，也可能是子订单)
		OrderForm storeMainOf = (OrderForm) storeAllOfs.get(0).get("of");
		if (storeOf.getShip_price().compareTo(new BigDecimal(0)) == 1) {// 有运费时，星美直送的单不能作为主订单
			for (Map map : storeAllOfs) {
				if (((OrderForm) map.get("of")).getDelivery_type() != 3) { // 星美直送的单
					storeMainOf = (OrderForm) map.get("of");
					break;
				}
			}
		}
		storeMainOf.setSeckill_info(storeOf.getSeckill_info());
		storeMainOf.setGift_infos(storeOf.getGift_infos());
		storeMainOf.setWhether_gift(storeOf.getWhether_gift());
		storeMainOf.setEnough_reduce_amount(storeOf.getEnough_reduce_amount());
		storeMainOf.setEnough_reduce_info(storeOf.getEnough_reduce_info());
		storeMainOf.setCoupon_info(storeOf.getCoupon_info());
		// 订单总金额=商品总金额-优惠金额(满减金额、优惠券金额)+运费金额
		storeMainOf.setTotalPrice(storeMainOf.getTotalPrice().add(storeOf.getShip_price()));
		storeMainOf.setShip_price(storeOf.getShip_price());

		// begin dengyuqi 2016-3-4
		storeMainOf.setFreight_info(storeOf.getFreight_info());
		// end

		return storeAllOfs;
	}

	private List<OrderGoodsStatistics> saveOrderGoodsStatistics(OrderForm storeOf, List<GoodsCart> carts) {
		List<OrderGoodsStatistics> orderGoodsStatisticses = new ArrayList<>();
		for (GoodsCart gc : carts) {
			GoodsClass goodsClass = gc.getGoods().getGc();
			if (null != goodsClass) {
				OrderGoodsStatistics orderGoodsStatistics = new OrderGoodsStatistics();
				orderGoodsStatistics.setAddTime(new Date());
				orderGoodsStatistics.setGoodsId(gc.getGoods().getId());
				orderGoodsStatistics.setGoodsPrice(gc.getPrice());
				orderGoodsStatistics.setOrderId(storeOf.getOrder_id());
				if (Globals.GOODS_CLASS_LEVEL_THRID == goodsClass.getLevel()) {
					orderGoodsStatistics.setThridGcId(goodsClass.getId());
					orderGoodsStatistics.setSecondGcId(goodsClass.getParent().getId());
					orderGoodsStatistics.setFirstGcId(goodsClass.getParent().getParent().getId());
				} else if (Globals.GOODS_CLASS_LEVEL_SECOND == goodsClass.getLevel()) {
					orderGoodsStatistics.setSecondGcId(goodsClass.getId());
					orderGoodsStatistics.setFirstGcId(goodsClass.getParent().getId());
				} else if (Globals.GOODS_CLASS_LEVEL_FIRST == goodsClass.getLevel()) {
					orderGoodsStatistics.setFirstGcId(goodsClass.getId());
				}
				orderGoodsStatisticses.add(orderGoodsStatistics);
			}
		}
		return orderGoodsStatisticses;
	}

	/**
	 * 在拆单时复制原始订单(其中一些公共字段)
	 * 
	 * @param mainOf
	 *            被复制的原始订单对象
	 * @param orderFormSequence
	 *            用于区别order_id
	 * @return 返回复制出来的新订单对象
	 */
	private OrderForm copyOrderFormCommonInfo(OrderForm mainOf, int orderFormSequence) {
		OrderForm newOf = new OrderForm();
		newOf.setAddTime(mainOf.getAddTime());
		newOf.setDelivery_address_id(mainOf.getDelivery_address_id());
		newOf.setDelivery_info(mainOf.getDelivery_info());
		newOf.setDelivery_time(mainOf.getDelivery_time());
		newOf.setDelivery_type(mainOf.getDelivery_type());
		newOf.setInvoice(mainOf.getInvoice());
		newOf.setInvoiceType(mainOf.getInvoiceType());
		newOf.setMsg(mainOf.getMsg());
		newOf.setOrder_form(mainOf.getOrder_form());
		newOf.setOrder_status(mainOf.getOrder_status());
		newOf.setOrder_type(mainOf.getOrder_type());
		newOf.setReceiver_area(mainOf.getReceiver_area());
		newOf.setReceiver_area_info(mainOf.getReceiver_area_info());
		newOf.setReceiver_card(mainOf.getReceiver_card());
		newOf.setReceiver_mobile(mainOf.getReceiver_mobile());
		newOf.setReceiver_Name(mainOf.getReceiver_Name());
		newOf.setReceiver_telephone(mainOf.getReceiver_telephone());
		newOf.setReceiver_zip(mainOf.getReceiver_zip());
		newOf.setReceiverArea(mainOf.getReceiverArea());
		newOf.setStore_id(mainOf.getStore_id());
		newOf.setStore_name(mainOf.getStore_name());
		newOf.setTransport(mainOf.getTransport());
		newOf.setUser_id(mainOf.getUser_id());
		newOf.setUser_name(mainOf.getUser_name());

		BigDecimal zero = new BigDecimal(0);
		newOf.setShip_price(zero);
		newOf.setTotalPrice(zero);
		newOf.setEnough_reduce_amount(zero);
		newOf.setEnough_reduce_info("{}");// 默认为空
		newOf.setOrder_id(mainOf.getOrder_id() + orderFormSequence);

		return newOf;
	}

	/**
	 * 计算用户在店铺store_id中购买的商品发到收货地址addr_id所支持的运送方式
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @param addr_id
	 * @return
	 */
	public List<SysMap> calStoreShipTransports(HttpServletRequest request, HttpServletResponse response, String store_id,
			String addr_id) {
		List<GoodsCart> gc_list = new ArrayList<GoodsCart>();// 用户在对应店铺store_id中已选择的所有购物车
		List<GoodsCart> hxGoodsCarts = new ArrayList<GoodsCart>();// 用户在对应店铺中海信商品对应的购物车

		Map<Object, Map> storeTransportsMapping = (Map<Object, Map>) request.getSession(true).getAttribute(
				"storeTransportsMapping");// 保存了每个店铺当前计算出来的运费结果
		if (storeTransportsMapping == null) {
			storeTransportsMapping = new HashMap<Object, Map>();
			request.getSession(true).setAttribute("storeTransportsMapping", storeTransportsMapping);
		}

		Map<Object, Set<Long>> storesCartIds = (Map<Object, Set<Long>>) request.getSession(true).getAttribute(
				"storeCartMapping");// PC端CartViewAction.java中的goods_cart2函数中会将店铺中用户的购物车ID保存在session中

		Object sid = "self";
		if (!sid.toString().equals(store_id)) {
			sid = CommUtil.null2Long(store_id);
		}

		if (storesCartIds == null || !storesCartIds.containsKey(sid)) {// 如果在除了PC端的goods_cart2.html中调用了此方法，则要在此分支中进行进一步处理。当前此分支中计算的运费是基于用户在当前店铺的所有购物车上计算的，即假设用户全选了当前店铺中的所有购物车
			List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response);
			for (GoodsCart gc : carts) {
				Goods goods = gc.getGoods();
				if (!"self".equals(store_id)) {
					if (goods.getGoods_type() == 1 && goods.getGoods_store().getId().equals(CommUtil.null2Long(store_id))) {
						gc_list.add(gc);
						if (GoodsConfig.CODE_HX.equals(goods.getGoodsConfig().getConfigCode())) {
							hxGoodsCarts.add(gc);
						}
					}
				} else {
					if (goods.getGoods_type() == 0) {
						gc_list.add(gc);
						if (GoodsConfig.CODE_HX.equals(goods.getGoodsConfig().getConfigCode())) {
							hxGoodsCarts.add(gc);
						}
					}
				}
			}
		} else {
			Set<Long> cartIds = storesCartIds.get(sid);
			for (Long gcId : cartIds) {
				GoodsCart gc = this.goodsCartService.getObjById(gcId);
				gc_list.add(gc);
				if (GoodsConfig.CODE_HX.equals(gc.getGoods().getGoodsConfig().getConfigCode())) {
					hxGoodsCarts.add(gc);
				}
			}
		}

		List<Long> freeShipFeeGoodsCartIds_o2o = null;
		List<Integer> freeShipFeeGoodsCounts_o2o = null;
		Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
		List<SysMap> sms = null;

		if (hxGoodsCarts.size() > 0) {
			Map results = goodsTools.selectCinemaStore(hxGoodsCarts, addr);
			if (CommUtil.null2Int(results.get("flag")) == 1) {// 分派海信商品到星美门店失败
				sms = new ArrayList<SysMap>();
				sms.add(new SysMap(results.get("result"), (float) 9999.0));
			} else {
				List<Map> branches = (List<Map>) results.get("result");
				freeShipFeeGoodsCartIds_o2o = new ArrayList<Long>();
				freeShipFeeGoodsCounts_o2o = new ArrayList<Integer>();
				double freeShipFeeThreashold = CommUtil.null2Double(PropertyUtil.getProperty("FREE_SHIPFEE_THREASHOLD"));
				double xmO2ODistance = CommUtil.null2Double(PropertyUtil.getProperty("XM_O2O_DISTANCE"));

				// 计算满足星美门店o2o两公里免运费的购物车及其中的商品数量
				for (Map store : branches) {
					double distance = (Double) store.get("distance");
					ShipAddress sa = (ShipAddress) store.get("address");
					if (distance <= xmO2ODistance && sa.getO2oCapable() == 1) {
						// TODO:除了上述条件外，可能还会增加消费限额（freeShipFeeThreashold）这一条件(注意本类中还有一处goods_cart2函数中和此处类似的关于海信o2o的代码要修改)。
						List<Long> goodscart_list = (List<Long>) store.get("goodscart_list");
						List<Integer> goods_count_list = (List<Integer>) store.get("goods_count_list");
						for (int i = 0; i < goodscart_list.size(); i++) {
							Long gcId = goodscart_list.get(i);
							Integer freeCount = goods_count_list.get(i);
							int idx = freeShipFeeGoodsCartIds_o2o.indexOf(gcId);
							if (idx >= 0) {// 已存在
								freeShipFeeGoodsCounts_o2o.set(idx, freeShipFeeGoodsCounts_o2o.get(idx) + freeCount);
							} else {
								freeShipFeeGoodsCartIds_o2o.add(gcId);
								freeShipFeeGoodsCounts_o2o.add(freeCount);
							}
						}
					}
				}

				Map<Object, Map> hxgoods = (Map<Object, Map>) request.getSession(true).getAttribute("hxgoods");
				if (hxgoods == null) {
					hxgoods = new HashMap<Object, Map>();
					request.getSession(true).setAttribute("hxgoods", hxgoods);
				}
				Map storeHXgoods = new HashMap();
				storeHXgoods.put("goodsDispached", branches);
				storeHXgoods.put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2o);
				storeHXgoods.put("freeShipFeeCountsForGoodsCart_o2o", freeShipFeeGoodsCounts_o2o);
				hxgoods.put(sid, storeHXgoods);
			}
		}

		Map transportsAndAddr = new HashMap();

		// begin dengyuqi 2016-3-4 发货方式(平邮、快递、EMS)及对应的运费信息
		Map<String, List<OrderFreightInfo>> freightInfos = new HashMap<String, List<OrderFreightInfo>>();
		// end

		if (sms == null) {
			sms = this.transportTools.calStoreCartsTransFee(gc_list, addr.getArea().getId().toString(),
					freeShipFeeGoodsCartIds_o2o, freeShipFeeGoodsCounts_o2o, freightInfos);
			if (freeShipFeeGoodsCartIds_o2o != null && freeShipFeeGoodsCartIds_o2o.size() > 0) {
				StringBuilder partFreeGoodsCartIds = new StringBuilder(); // 购物车中部分数量的商品满足o2o两公里内免运费配送的购物车的id
				StringBuilder fullFreeGoodsCartIds = new StringBuilder(); // 购物车中的全部商品满足o2o两公里内免运费配送的购物车的id
				for (GoodsCart gc : hxGoodsCarts) {
					int idx = freeShipFeeGoodsCartIds_o2o.indexOf(gc.getId());
					if (idx >= 0) {
						Integer freeCount = freeShipFeeGoodsCounts_o2o.get(idx);
						if (freeCount < gc.getCount()) {
							partFreeGoodsCartIds.append(gc.getId().toString()).append(":").append(freeCount.toString())
									.append(",");
						} else {
							fullFreeGoodsCartIds.append(gc.getId().toString()).append(",");
						}
					}
				}
				if (partFreeGoodsCartIds.length() > 0) {
					sms.add(new SysMap(partFreeGoodsCartIds.toString(), -1));
				}

				if (fullFreeGoodsCartIds.length() > 0) {
					sms.add(new SysMap(fullFreeGoodsCartIds.toString(), -2));
				}
			}
			transportsAndAddr.put("receiverAddrId", addr.getId());
		} else {
			transportsAndAddr.put("receiverAddrId", null);
		}
		transportsAndAddr.put("transports", sms);

		// begin dengyuqi
		transportsAndAddr.put("freightInfos", freightInfos);
		// end

		storeTransportsMapping.put(sid, transportsAndAddr);
		return sms;
	}

	private String generic_day(int day) {
		String[] list = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return list[day - 1];
	}

	/**
	 * 获得商品佣金比例
	 * 
	 * @param goods
	 * @return
	 */
	private double getCommissionRate(Goods goods) {
		GoodsClass goodsClass = goods.getGc();
		// 共用的佣金比
		double defaultCommissionRate = goodsClass.getCommission_rate().doubleValue();
		// 定制的佣金比
		String commissionInfo = goods.getGoods_store().getGc_commission_info();
		List<Map> list = CommUtil.json2List(commissionInfo);
		for (Map map : list) {
			if (goodsClass.getId() == Long.parseLong((String) map.get("id"))) {
				defaultCommissionRate = Double.parseDouble(((String) map.get("commission")));
				break;
			}
		}
		return defaultCommissionRate;
	}

	/**
	 * 获得单个商品佣金
	 * 
	 * @param gc
	 * @param gcs
	 * @param enoughReduceInfo
	 * @param couponInfo
	 * @return
	 */
	private double getGoodscartCommission(GoodsCart gc, List<GoodsCart> gcs, String enoughReduceInfo, String couponInfo) {
		// begin dengyuqi 2016-3-3
		// double commission_price = CommUtil.mul(gc.getGoods().getGc().getCommission_rate(),
		// CommUtil.mul(gc.getPrice(), gc.getCount()));
		// return commission_price;
		Goods goods = gc.getGoods();
		// 佣金比
		double defaultCommissionRate = getCommissionRate(goods);
		return CommUtil.mul(getGoodscartPayoff(gc, gcs, enoughReduceInfo, couponInfo), defaultCommissionRate);
		// end
	}

	/**
	 * 商品按比例满减优惠券优惠后的价格
	 * 
	 * @param gc
	 * @param gcs
	 * @param enoughReduceInfo
	 * @param couponInfo
	 * @return
	 */
	private double getGoodscartPayoff(GoodsCart gc, List<GoodsCart> gcs, String enoughReduceInfo, String couponInfo) {
		Goods goods = gc.getGoods();
		// GoodsClass goodsClass = goods.getGc();
		// 扣点结算价=单个商品原金额- sum（（单个商品参与优惠的金额/所有商品参与优惠总额）*优惠总金额）
		double beforePrice = CommUtil.mul(gc.getCount(), gc.getPrice());// 商品原金额
		double enoughReducePrice = 0.00;// 满减优惠的金额
		double couponPrice = 0.00;// 优惠券优惠的金额

		if (null != enoughReduceInfo) {
			enoughReducePrice = getEnoughReducePrice(goods.getId(), enoughReduceInfo);
		}

		if (null != couponInfo) {
			double allBeforePrice = 0.00;
			double allEnoughReducePrice = 0.00;
			for (GoodsCart goodsCart : gcs) {
				allBeforePrice += CommUtil.mul(goodsCart.getCount(), goodsCart.getPrice());
				if (null != enoughReduceInfo) {
					allEnoughReducePrice += getEnoughReducePrice(goodsCart.getGoods().getId(), enoughReduceInfo);
				}
			}

			double selfAmount = beforePrice - enoughReducePrice;// 单个商品参与优惠的金额
			double allAmount = allBeforePrice - allEnoughReducePrice;// 所有商品参与优惠总额

			Map<String, Object> couponInfoMap = (Map<String, Object>) Json.fromJson(couponInfo); // 优惠券优惠信息
			//double reducePrice = (Integer) couponInfoMap.get("coupon_amount");
			double reducePrice =  Double.parseDouble(couponInfoMap.get("coupon_amount").toString());//现在加入了全场优惠券，所以优惠金额不在是整形的，而是带有小数的浮点数字
			couponPrice = CommUtil.mul(selfAmount / allAmount, reducePrice);
		}
		return beforePrice - enoughReducePrice - couponPrice;
	}

	/**
	 * 获得订单商品总佣金
	 * 
	 * @param orderGcs
	 *            当前订单购物车集合
	 * @param allOrderGcs
	 *            所有订单购物车集合
	 * @param enoughReduceInfo
	 *            满减信息 e.g.
	 *            {"prices_2":[1.00],"2":[5487],"return_2":0.0,"reduce_2":1.0,"counts_2":[3],"all_2":3.0,"enouhg_2":"2.0"}
	 * @param couponInfo
	 *            优惠券信息 e.g.
	 *            {"coupon_goods_rate":0.01,"couponinfo_sn":"b57ab85e-ead7-49df-8323-5785bb17021d","coupon_return_amount"
	 *            :0,"couponinfo_id":113,"coupon_amount":1,"coupon_order_amount":1}
	 * @return
	 */
	private double getOrderCommission(List<GoodsCart> orderGcs, List<GoodsCart> allOrderGcs, String enoughReduceInfo,
			String couponInfo) {
		double totalCommissionPrice = 0.00;

		for (GoodsCart gc : orderGcs) {
			double commissionPrice = getGoodscartCommission(gc, allOrderGcs, enoughReduceInfo, couponInfo);

			totalCommissionPrice += commissionPrice;
		}

		return totalCommissionPrice;
	}

	/**
	 * 获得订单总额
	 * 
	 * @param orderGcs
	 *            当前订单购物车集合
	 * @param allOrderGcs
	 *            所有订单购物车集合
	 * @param enoughReduceInfo
	 *            满减信息 e.g.
	 *            {"prices_2":[1.00],"2":[5487],"return_2":0.0,"reduce_2":1.0,"counts_2":[3],"all_2":3.0,"enouhg_2":"2.0"}
	 * @param couponInfo
	 *            优惠券信息 e.g.
	 *            {"coupon_goods_rate":0.01,"couponinfo_sn":"b57ab85e-ead7-49df-8323-5785bb17021d","coupon_return_amount"
	 *            :0,"couponinfo_id":113,"coupon_amount":1,"coupon_order_amount":1}
	 * @return
	 */
	private BigDecimal getOrderTotalPrice(List<GoodsCart> orderGcs, List<GoodsCart> allOrderGcs, String enoughReduceInfo,
			String couponInfo) {
		double totalPrice = 0.00;

		for (GoodsCart gc : orderGcs) {
			totalPrice += getGoodscartPayoff(gc, allOrderGcs, enoughReduceInfo, couponInfo);
		}

		return new BigDecimal(totalPrice);
	}

	/**
	 * 测试回调成功修改满减json
	 * 
	 * @param orderId
	 * @return
	 */
	public boolean modifyEnoughReduceInfo(OrderForm orderForm) {
		Map params = new HashMap();
		boolean b = false;
		StringBuffer sql = new StringBuffer();
		sql.append(" select obj from OrderForm obj where obj.mainOrderId=:mainOrderId ");
		sql.append(" and obj.enough_reduce_info != :enoughReduceInfo ");
		params.put("mainOrderId", orderForm.getMainOrderId());
		params.put("enoughReduceInfo", "{}");
		if (null == orderForm.getStore_id()) {
			sql.append(" and obj.store_id is null ");
		} else {
			sql.append(" and obj.store_id = :storeId ");
			params.put("storeId", orderForm.getStore_id());
		}
		List<OrderForm> orderForms = query(sql.toString(), params, -1, -1);
		if (orderForms != null && !orderForms.isEmpty()) {
			OrderForm of = orderForms.get(0);
			List<Map> list = new ArrayList<Map>();
			Map<String, Integer> goodsIdMap = new HashMap<String, Integer>();
			String regex = "^[1-9]\\d*$";

			Map map = null;
			Map ma = null;
			if (of != null) {
				if (!StringUtils.isNullOrEmpty(of.getGoods_info())) {
					list = Json.fromJson(ArrayList.class, of.getGoods_info());
					if (list != null && !"[]".equals(list)) {
						for (Map m : list) {
							int goods_id = CommUtil.null2Int(m.get("goods_id"));
							goodsIdMap.put(CommUtil.null2String(goods_id), goods_id);
						}
					}
				}
				if (!StringUtils.isNullOrEmpty(of.getEnough_reduce_info())) {
					map = Json.fromJson(Map.class, of.getEnough_reduce_info());
					Map js = Json.fromJson(Map.class, of.getEnough_reduce_info());
					if (map != null && !"{}".equals(map)) {
						for (Object o : js.keySet()) {
							String key = CommUtil.null2String(o);
							if (Pattern.matches(regex, key)) {
								List<Integer> ts = (List) map.get(key);
								if (!StringUtils.isNullOrEmpty(ts)) {
									for (Integer i : ts) {
										if (goodsIdMap.containsKey(CommUtil.null2String(i))) {
											if (map.get("tk_" + key) != null) {
												List<Integer> tk_s = (List) map.get("tk_" + key);
												if (!tk_s.contains(goodsIdMap.get(CommUtil.null2String(i)))) {
													tk_s.add(goodsIdMap.get(CommUtil.null2String(i)));
													map.put("tk_" + key, tk_s);
												}
											} else {
												List<Integer> ls = new ArrayList<Integer>();
												ls.add(goodsIdMap.get(CommUtil.null2String(i)));
												map.put("tk_" + key, ls);
											}
										}
									}
								}
							}
						}
					}
				}
				String listJson = Json.toJson(map, JsonFormat.compact());
				of.setEnough_reduce_info(listJson);
				b = update(of);
			}
		}
		return b;
	}

	/**
	 * 测试回调成功修改运费json
	 * 
	 * @param of
	 */
	public boolean modifyFreightInfo(OrderForm orderForm) {
		List<Map> list = new ArrayList<Map>();
		List<Map> json = new ArrayList<Map>();
		List<Long> tk_goods_info = null;
		Map<Long, Long> transportIdMap = new HashMap<Long, Long>();
		Map<Long, Long> goodsIdMap = new HashMap<Long, Long>();
		boolean b = false;
		StringBuffer sql = new StringBuffer();
		sql.append(" select obj from OrderForm obj where obj.mainOrderId=:mainOrderId ");
		sql.append(" and obj.freight_info != :freight_info ");
		Map params = new HashMap();
		params.put("mainOrderId", orderForm.getMainOrderId());
		params.put("freight_info", "{}");
		if (null == orderForm.getStore_id()) {
			sql.append(" and obj.store_id is null ");
		} else {
			sql.append(" and obj.store_id = :storeId ");
			params.put("storeId", orderForm.getStore_id());
		}
		List<OrderForm> orderForms = query(sql.toString(), params, -1, -1);
		if (orderForms != null && !orderForms.isEmpty()) {
			OrderForm of = orderForms.get(0);
			if (of != null) {
				if (!StringUtils.isNullOrEmpty(of.getGoods_info())) {
					list = Json.fromJson(ArrayList.class, of.getGoods_info());
					if (list != null && !"[]".equals(list)) {
						for (Map m : list) {
							Long goods_id = CommUtil.null2Long(m.get("goods_id"));
							params.clear();
							params.put("goods_id", goods_id);
							List<Goods> goodsList = this.goodsService.query(
									"select obj from Goods obj where obj.id=:goods_id", params, -1, -1);
							Goods goods = goodsList.get(0);
							if (goods != null && goods.getTransport() != null) {
								transportIdMap.put(goods.getTransport().getId(), goods.getId());
							} else {
								goodsIdMap.put(goods.getId(), goods.getId());
							}
						}
					}
				}

				if (!StringUtils.isNullOrEmpty(of.getFreight_info())) {
					json = Json.fromJson(ArrayList.class, of.getFreight_info());
					if (json != null && !"[]".equals(json)) {
						for (Map m : json) {
							Long trans_id = CommUtil.null2Long(m.get("trans_id"));
							if (trans_id != 0) {
								if (transportIdMap.containsKey(trans_id)) {
									if (m.get("tk_goods_info") != null) {
										tk_goods_info = (List<Long>) m.get("tk_goods_info");
										if (!tk_goods_info.contains(transportIdMap.get(trans_id))) {
											tk_goods_info.add(transportIdMap.get(trans_id));
											m.put("tk_goods_info", tk_goods_info);
										}
									} else {
										tk_goods_info = new ArrayList<Long>();
										tk_goods_info.add(transportIdMap.get(trans_id));
										m.put("tk_goods_info", tk_goods_info);
									}
								}
							} else {
								for (Long l : goodsIdMap.keySet()) {
									if (m.get("tk_goods_info") != null) {
										tk_goods_info = (List<Long>) m.get("tk_goods_info");
										if (!tk_goods_info.contains(transportIdMap.get(trans_id))) {
											tk_goods_info.add(l);
											m.put("tk_goods_info", tk_goods_info);
										}
									} else {
										tk_goods_info = new ArrayList<Long>();
										tk_goods_info.add(l);
										m.put("tk_goods_info", tk_goods_info);
									}
								}
							}
						}
					}
				}
				String listJson = Json.toJson(json, JsonFormat.compact());
				of.setFreight_info(listJson);
				b = update(of);
			}
		}
		return b;
	}

	@Override
	public Map<String, Double> getOrderRefundAmount(String orderId) {
		Map<String, Double> prices = new HashMap<String, Double>();

		// 当前订单商品总金额
		OrderForm orderForm = this.getObjByProperty(null, "order_id", orderId);
		double totalPrice = orderForm.getGoods_amount().doubleValue();
		prices.put("totalPrice", totalPrice);

		// 当前订单的运费
		double freight = 0.00;
		String freightInfo = getFreightInfo(orderForm);
		logger.debug("freightInfo:" + freightInfo);
		if (null != freightInfo) {
			// 总运费
			freight = getFreightAmount(orderForm);

			double staticPrice = 0.00; // 剩余订单的静态运费
			double totalDynamicPrice = 0.00; // 剩余订单的动态运费
			// 获取剩余订单的运费模板及对应的商品信息
			List<OrderFreightInfo> freightInfos = getOrderFreightInfo(orderForm, freightInfo);
			// 遍历运费模板，计算剩余订单的运费和
			for (OrderFreightInfo orderFreightInfo : freightInfos) {
				if (0 == orderFreightInfo.getTrans_id()) { // 静态模板
					// 遍历剩余订单，寻找剩余订单静态模板下最高运费的订单
					for (FreightGoods gi : orderFreightInfo.getGoods_info()) {
						if (gi.getPrice() > staticPrice) {
							staticPrice = CommUtil.null2Double(gi.getPrice());
						}
					}
				} else { // 动态模板
					if (!orderFreightInfo.getGoods_info().isEmpty()) {
						double totalCount = 0;
						// 遍历剩余订单，计算剩余订单商品总件数或总重量或总立方米
						for (FreightGoods gi : orderFreightInfo.getGoods_info()) {
							totalCount += CommUtil.null2Double(gi.getCount());
						}
						// 超重运费=超重单价*超出的重量
						double morePrice = 0.00;
						if (totalCount - orderFreightInfo.getTrans_add_weight() > 0) {
							morePrice = CommUtil.mul((totalCount - orderFreightInfo.getTrans_add_weight()),
									orderFreightInfo.getTrans_add_fee());
						}
						// 运费=首重运费+超重运费
						totalDynamicPrice += orderFreightInfo.getTrans_fee() + morePrice;
					}
				}
			}

			// 当前订单运费=总运费-剩余订单的运费
			freight -= (staticPrice + totalDynamicPrice);
		}
		prices.put("freight", freight);

		// 当前订单享受的满减活动优惠金额
		double totalReducePrice = 0.00;
		// 当前订单使用的优惠券金额
		double couponPrice = 0.00;

		// 是否为最后一单
		String jpql = "SELECT o FROM OrderForm o WHERE o.mainOrderId=:mainOrderId AND o.order_status != 90";
		Map<String, String> params = new HashMap<String, String>();
		params.put("mainOrderId", orderForm.getMainOrderId());
		List<OrderForm> orderForms = orderFormDao.query(jpql, params, -1, -1);
		if (orderForms.size() == 1) {// 当前订单应退金额=总支付金额-已退款总额
			// step1 计算整笔订单总金额及总优惠金额
			jpql = "SELECT SUM(o.totalPrice) AS totalPrice," + "SUM(o.enough_reduce_amount) AS enough_reduce_amount "
					+ "FROM OrderForm o WHERE o.mainOrderId=:mainOrderId";
			params.clear();
			params.put("mainOrderId", orderForm.getMainOrderId());
			List<Object[]> columns = orderFormDao.query(jpql, params, 0, 1);
			double price = ((BigDecimal) columns.get(0)[0]).doubleValue();
			totalReducePrice = ((BigDecimal) columns.get(0)[1]).doubleValue();

			// step2 计算整笔订单已退款金额
			jpql = "SELECT SUM(o.refund_amount) AS totalPrice," + "SUM(o.enough_reduced_amount) AS enough_reduce_amount "
					+ "FROM OrderForm o WHERE o.mainOrderId=:mainOrderId AND o.order_status=90";
			columns = orderFormDao.query(jpql, params, 0, 1);
			if (null != columns.get(0)[0]) {
				price = CommUtil.div(price, ((BigDecimal) columns.get(0)[0]).doubleValue());
				totalReducePrice -= ((BigDecimal) columns.get(0)[1]).doubleValue();
			}
			prices.put("price", price);// 退款金额
			prices.put("totalReducePrice", totalReducePrice);

			couponPrice = totalPrice + freight - price - totalReducePrice;
			prices.put("couponPrice", couponPrice);
		} else {
			// 获取满减信息
			String enoughReduceInfo = getEnoughReduceInfo(orderForm);
			logger.debug("enoughReduceInfo:" + enoughReduceInfo);
			if (null != enoughReduceInfo) {
				// 当前订单参与的剩余订单的满减信息
				List<OrderEnoughReduceInfo> allnoughReduceInfos = getOrderEnoughReduceInfo(orderForm, enoughReduceInfo);
				for (OrderEnoughReduceInfo oeri : allnoughReduceInfos) {
					// 剩下的订单不满足满减，则取消订单要减去全部满减优惠金额
					if (oeri.getTotalPrice() < oeri.getEnouhgPrice()) {
						totalReducePrice += oeri.getReducePrice();
					}
				}
			}
			prices.put("totalReducePrice", totalReducePrice);

			// 获取优惠券信息
			String couponInfo = getCouponInfo(orderForm);
			logger.debug("couponInfo:" + couponInfo);
			if (null != couponInfo) {
				// 剩余订单中的商品总额(优惠前金额)
				double totalGoodsPrice = getGoodsAmount(orderForm);

				// 剩余订单满减优惠金额
				double otherTotalReducePrice = getEnoughReduceAmount(orderForm) - totalReducePrice;

				Map<String, Object> couponInfoMap = (Map<String, Object>) Json.fromJson(couponInfo); // 优惠券优惠信息
				// 满多少
				double enouhgPrice = (Integer) couponInfoMap.get("coupon_order_amount");
				// 优惠多少
				double reducePrice = (Integer) couponInfoMap.get("coupon_amount") - getCouponAmount(orderForm);
				// 剩下的订单不满足满减，则取消订单要减去全部优惠券优惠金额
				if (totalGoodsPrice - otherTotalReducePrice < enouhgPrice) {
					couponPrice += reducePrice;
				}
			}
			prices.put("couponPrice", couponPrice);

			prices.put("price", CommUtil.add(totalPrice - totalReducePrice - couponPrice, freight));// 退款金额
		}

		logger.info("orderNo:" + orderForm.getOrder_id() + ",totalPrice:" + totalPrice + "," + "totalReducePrice:"
				+ totalReducePrice + "," + "couponPrice:" + couponPrice + ",freight:" + freight);
		return prices;
	}

	/**
	 * 获取订单已使用的满减信息
	 * 
	 * @param orderForm
	 * @return
	 */
	private String getEnoughReduceInfo(OrderForm orderForm) {
		String enoughReduceInfo = null; // 满减信息

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.enough_reduce_info != :enoughReduceInfo " + "and order.enough_reduce_info is not null";

		Map<String, String> params = new HashMap<String, String>();
		params.put("mainOrderId", orderForm.getMainOrderId());
		params.put("enoughReduceInfo", "{}");

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> orderForms = orderFormDao.query(hql, params, 0, 1);
		if (!orderForms.isEmpty()) {
			OrderForm of = orderForms.get(0);
			enoughReduceInfo = of.getEnough_reduce_info();
		}

		return enoughReduceInfo;
	}

	/**
	 * 获取订单已使用的优惠券信息
	 * 
	 * @param orderForm
	 * @return
	 */
	private String getCouponInfo(OrderForm orderForm) {
		String couponInfo = null; // 优惠券信息

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.coupon_info is not null";

		Map<String, String> params = new HashMap<String, String>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> orderForms = orderFormDao.query(hql, params, 0, 1);
		if (!orderForms.isEmpty()) {
			couponInfo = orderForms.get(0).getCoupon_info();
		}

		return couponInfo;
	}

	/**
	 * 获取订单运费信息
	 * 
	 * @param orderForm
	 * @return
	 */
	private String getFreightInfo(OrderForm orderForm) {
		String freightInfo = null;

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.freight_info is not null";

		Map<String, String> params = new HashMap<String, String>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> orderForms = orderFormDao.query(hql, params, 0, 1);
		if (!orderForms.isEmpty()) {
			freightInfo = orderForms.get(0).getFreight_info();
		}
		return freightInfo;
	}

	/**
	 * 获取剩余未退款订单的满减优惠信息
	 * 
	 * @param orderForm
	 *            订单
	 * @param enoughReduceInfo
	 *            满减信息
	 * @return
	 */
	private List<OrderEnoughReduceInfo> getOrderEnoughReduceInfo(OrderForm orderForm, String enoughReduceInfo) {
		// 满减活动与参与该满减活动的所有商品列表
		List<OrderEnoughReduceInfo> enoughReduceInfos = new ArrayList<OrderEnoughReduceInfo>();

		// 当前订单中的商品列表
		String goodsInfoStr = orderForm.getGoods_info();
		List<Map> goodsInfos = CommUtil.json2List(goodsInfoStr);
		List<Integer> reduceGoodss = new ArrayList<Integer>();
		for (Map goodsInfo : goodsInfos) {
			reduceGoodss.add((Integer) goodsInfo.get("goods_id"));
		}

		Map<String, Object> enoughReduceInfoMap = (Map<String, Object>) Json.fromJson(enoughReduceInfo); // 满减优惠信息
		Set<String> keys = enoughReduceInfoMap.keySet();
		for (String key : keys) {
			if (key.matches("^\\d+$")) { // 满减活动id
				// 参与当前满减活动的所有商品
				List<Integer> goodsIds = (List<Integer>) enoughReduceInfoMap.get(key);

				// 剩余未退款订单的商品id集
				List<Integer> list = new ArrayList<Integer>();
				list.addAll(goodsIds);

				// 去除当前订单中的商品
				boolean isEnoughReduce = list.removeAll(reduceGoodss);
				if (isEnoughReduce) { // 当前订单参与了该满减活动
					// 去除已退款订单中的商品
					List<Integer> tkGoodsIds = (List<Integer>) enoughReduceInfoMap.get("tk_" + key);
					if (null != tkGoodsIds) {
						list.removeAll(tkGoodsIds);
					}

					OrderEnoughReduceInfo orderEnoughReduceInfo = new OrderEnoughReduceInfo(Long.parseLong(key));
					// 满多少
					double enouhgPrice = Double.parseDouble((String) enoughReduceInfoMap.get("enouhg_" + key));
					orderEnoughReduceInfo.setEnouhgPrice(enouhgPrice);
					// 减多少
					double reducePrice = (Double) enoughReduceInfoMap.get("reduce_" + key);
					orderEnoughReduceInfo.setReducePrice(reducePrice);

					List<EnoughReduceGoods> enoughReduceGoodss = new ArrayList<EnoughReduceGoods>();
					for (Integer goodsId : list) {
						int offset = goodsIds.indexOf(goodsId);
						double price = (Double) ((List) enoughReduceInfoMap.get("prices_" + key)).get(offset);
						int count = (Integer) ((List) enoughReduceInfoMap.get("counts_" + key)).get(offset);
						EnoughReduceGoods enoughReduceGoods = orderEnoughReduceInfo.new EnoughReduceGoods(goodsId, price,
								count);
						enoughReduceGoodss.add(enoughReduceGoods);
					}
					orderEnoughReduceInfo.setEnoughReduceGoodss(enoughReduceGoodss);

					enoughReduceInfos.add(orderEnoughReduceInfo);
				}
			}
		}
		return enoughReduceInfos;
	}

	/**
	 * 获取剩余未退款订单的运费信息
	 * 
	 * @param orderForm
	 * @param freightInfo
	 *            运费信息
	 * @return
	 */
	private List<OrderFreightInfo> getOrderFreightInfo(OrderForm orderForm, String freightInfo) {
		// 运费模板与该运费模板下的所有商品列表
		List<OrderFreightInfo> freightInfos = (List<OrderFreightInfo>) CommUtil.json2List(OrderFreightInfo.class,
				freightInfo); // 运费信息

		// 当前订单中的商品列表
		List<FreightGoods> freightGoods = new ArrayList<FreightGoods>();
		String goodsInfoStr = orderForm.getGoods_info();
		List<Map> goodsInfos = CommUtil.json2List(goodsInfoStr);
		for (Map gi : goodsInfos) {
			freightGoods.add(new FreightGoods((Integer) gi.get("goods_id")));
		}

		// 剩余订单的运费信息
		for (OrderFreightInfo orderFreightInfo : freightInfos) {
			List<FreightGoods> goodsInfos2 = orderFreightInfo.getGoods_info();

			goodsInfos2.removeAll(freightGoods);// 去除当前订单中的商品

			// 去除已退款订单中的商品
			List<Long> goodsInfos3 = orderFreightInfo.getTk_goods_info();
			if (null != goodsInfos3) {
				List<FreightGoods> tkFreightGoods = new ArrayList<FreightGoods>();
				for (Long goodsId : goodsInfos3) {
					tkFreightGoods.add(new FreightGoods(goodsId));
				}
				goodsInfos2.removeAll(tkFreightGoods);
			}
		}
		return freightInfos;
	}

	/**
	 * 获取剩余未退款订单的商品总额
	 * 
	 * @param orderForm
	 * @return
	 */
	private double getGoodsAmount(OrderForm orderForm) {
		double goodsAmount = 0.00; // 商品总额

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.id != :id and order.order_status != 90";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mainOrderId", orderForm.getMainOrderId());
		params.put("id", orderForm.getId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> ofs = orderFormDao.query(hql, params, 0, -1);
		if (!ofs.isEmpty()) {
			for (OrderForm of : ofs) {
				goodsAmount += of.getGoods_amount().doubleValue();
			}
		}
		return goodsAmount;
	}

	/**
	 * 获取未退款订单的满减优惠总额
	 * 
	 * @param orderForm
	 * @return
	 */
	private double getEnoughReduceAmount(OrderForm orderForm) {
		double enoughReduceAmount = 0.00; // 满减优惠总额

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.enough_reduce_amount > 0";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> ofs = orderFormDao.query(hql, params, 0, 1);
		if (!ofs.isEmpty()) {
			OrderForm of = ofs.get(0);
			enoughReduceAmount = CommUtil.null2Double(of.getEnough_reduce_amount())
					- CommUtil.null2Double(of.getEnough_reduced_amount());
		}
		return enoughReduceAmount;
	}

	/**
	 * 获取已减去的优惠券优惠金额
	 * 
	 * @param orderForm
	 * @return
	 */
	private double getCouponAmount(OrderForm orderForm) {
		double couponAmount = 0.00;

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.discount_coupon > 0";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> ofs = orderFormDao.query(hql, params, 0, 1);
		if (!ofs.isEmpty()) {
			OrderForm of = ofs.get(0);
			couponAmount = CommUtil.null2Double(of.getDiscount_coupon());
		}
		return couponAmount;
	}

	/**
	 * 获取剩余未退款订单的总运费
	 * 
	 * @param orderForm
	 * @return
	 */
	private double getFreightAmount(OrderForm orderForm) {
		double freight = 0.00; // 运费

		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId "
				+ "and order.ship_price > 0";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> ofs = orderFormDao.query(hql, params, 0, 1);
		if (!ofs.isEmpty()) {
			OrderForm of = ofs.get(0);
			freight = CommUtil.null2Double(of.getShip_price()) - CommUtil.null2Double(of.getFreighted_amount());
		}
		return freight;
	}

	/**
	 * 按比例获取商品满减优惠价格
	 * 
	 * @param goodsId
	 *            商品ID
	 * @param enoughReduceInfoStr
	 *            满减信息 e.g.
	 *            {"prices_2":[1.00],"2":[5487],"return_2":0.0,"reduce_2":1.0,"counts_2":[3],"all_2":3.0,"enouhg_2":"2.0"}
	 * @return
	 */
	private Double getEnoughReducePrice(Long goodsId, String enoughReduceInfoStr) {
		// 商品满减优惠价格
		double enoughReducePrice = 0.00;
		Map<String, Object> enoughReduceInfo = (Map<String, Object>) Json.fromJson(enoughReduceInfoStr); // 满减优惠信息
		// 保存当前商品所在的满减活动
		OrderEnoughReduceInfo orderEnoughReduceInfo = null;
		Set<String> keys = enoughReduceInfo.keySet();
		for (String key : keys) {
			if (key.matches("^\\d+$")) { // 满减活动id
				List<Integer> goodsIds = (List) enoughReduceInfo.get(key); // 参加该满减活动的商品id列表
				if (goodsIds.contains(goodsId.intValue())) {
					orderEnoughReduceInfo = new OrderEnoughReduceInfo(Long.parseLong(key));

					// 减
					double reducePrice = (Double) enoughReduceInfo.get("reduce_" + key);
					orderEnoughReduceInfo.setReducePrice(reducePrice);

					List<EnoughReduceGoods> enoughReduceGoodss = new ArrayList<EnoughReduceGoods>();
					for (int i = 0; i < goodsIds.size(); i++) {
						double price = (Double) ((List) enoughReduceInfo.get("prices_" + key)).get(i);
						int count = (Integer) ((List) enoughReduceInfo.get("counts_" + key)).get(i);
						EnoughReduceGoods enoughReduceGoods = orderEnoughReduceInfo.new EnoughReduceGoods(goodsIds.get(i),
								price, count);
						enoughReduceGoodss.add(enoughReduceGoods);
					}
					orderEnoughReduceInfo.setEnoughReduceGoodss(enoughReduceGoodss);
					break;
				}
			}
		}

		if (null != orderEnoughReduceInfo) {
			double selfAmount = 0.00;// 单个商品参与优惠的金额
			double allAmount = 0.00;// 所有商品参与优惠总额
			List<EnoughReduceGoods> enoughReduceGoodss = orderEnoughReduceInfo.getEnoughReduceGoodss();
			for (EnoughReduceGoods enoughReduceGoods : enoughReduceGoodss) {
				if (enoughReduceGoods.getGoodsId() == goodsId) {//可能存在goods_id[393,393] counts[3,1]这种拆分过的满减，所以取最后一个值才是正确的
					selfAmount = CommUtil.mul(enoughReduceGoods.getCount(), enoughReduceGoods.getPrice());
				}
				allAmount += CommUtil.mul(enoughReduceGoods.getCount(), enoughReduceGoods.getPrice());
			}
			enoughReducePrice = CommUtil.mul(selfAmount / allAmount, orderEnoughReduceInfo.getReducePrice());
		}
		return enoughReducePrice;
	}

	@Override
	public boolean modifyOrderStatus(String orderId) {
		// 当前订单总额(优惠前总额)
		String hql = "select order from OrderForm order where order.order_id = :orderId";
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		OrderForm orderForm = (OrderForm) orderFormDao.query(hql, params, 0, 1).get(0);
		orderForm.setOrder_status(90);
		this.orderFormDao.update(orderForm);
		return true;
	}

	/**
	 * 退单成功后，修改优惠的活动金额
	 * 
	 * @param orderForm
	 * @return
	 */
	public boolean modifyActivityAmount(OrderForm orderForm) {
		boolean b = false;
		OrderForm of = null;
		Map<String, Double> map = new HashMap<String, Double>();
		if (orderForm != null) {
			of = getObjById(orderForm.getId());
			// 调用计算退款金额接口
			map = getOrderRefundAmount(orderForm.getOrder_id());
			double totalReducePrice = map.get("totalReducePrice");// 满减折扣金额
			double couponPrice = map.get("couponPrice");// 优惠券折扣金额
			double freight = map.get("freight");// 运费金额
			if (!StringUtils.isNullOrEmpty(of.getFreighted_amount())) {
				of.setFreighted_amount(of.getFreighted_amount().add(BigDecimal.valueOf(freight)));
			} else {
				of.setFreighted_amount(BigDecimal.valueOf(freight));
			}
			if (!StringUtils.isNullOrEmpty(of.getEnough_reduced_amount())) {
				of.setEnough_reduced_amount(of.getEnough_reduced_amount().add(BigDecimal.valueOf(totalReducePrice)));
			} else {
				of.setEnough_reduced_amount(BigDecimal.valueOf(totalReducePrice));
			}
			if (!StringUtils.isNullOrEmpty(of.getDiscount_coupon())) {
				of.setDiscount_coupon(of.getDiscount_coupon().add(BigDecimal.valueOf(couponPrice)));
			} else {
				of.setDiscount_coupon(BigDecimal.valueOf(couponPrice));
			}
			b = update(of);
		}
		return b;
	}

	/**
	 * 获取订单结算金额
	 */
	public BigDecimal getPayoffAmount(OrderForm orderForm) {
		double allPayoffAmount = 0.0;
		if (orderForm != null) {
			String json = orderForm.getGoods_info();
			if (json != null && !"[]".equals(json)) {
				List<Map<String, Object>> list = Json.fromJson(ArrayList.class, orderForm.getGoods_info());
				if (list != null && !list.isEmpty()) {
					for (Map<String, Object> map : list) {
						double payoffAmount = CommUtil.null2Double(map.get("goods_payoff_price"));// 单个商品计算金额
						allPayoffAmount += payoffAmount;
					}
				}
			}
		}
		return BigDecimal.valueOf(allPayoffAmount).add(orderForm.getShip_price());
	}

	public Map getDiscountAmounts(OrderForm orderForm) {
		String hql = "select order from OrderForm order " + "where order.mainOrderId = :mainOrderId";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mainOrderId", orderForm.getMainOrderId());

		if (null == orderForm.getStore_id()) {
			hql += " and order.store_id is null";
		} else {
			hql += " and order.store_id = :storeId";
			params.put("storeId", orderForm.getStore_id());
		}

		List<OrderForm> ofs = orderFormDao.query(hql, params, 0, -1);
		String goodsInfos = "";
		for (OrderForm of : ofs) {
			goodsInfos += of.getGoods_info();
		}
		goodsInfos = goodsInfos.replaceAll("\\]\\[", ",");
		List<Map> allOrderGoodss = CommUtil.json2List(goodsInfos);
		String enoughReduceInfo = getEnoughReduceInfo(orderForm);
		String couponInfo = getCouponInfo(orderForm);

		double totalEnoughReducePrice = 0.00;// 满减优惠的金额
		double totalCouponPrice = 0.00;// 优惠券优惠的金额
		List<Map> goodss = CommUtil.json2List(orderForm.getGoods_info());
		for (Map map : goodss) {
			double enoughReducePrice = 0.00;// 满减优惠的金额
			double couponPrice = 0.00;// 优惠券优惠的金额

			if (null != enoughReduceInfo) {
				enoughReducePrice = getEnoughReducePrice(CommUtil.null2Long(map.get("goods_id")), enoughReduceInfo);
			}

			if (null != couponInfo) {
				double allBeforePrice = 0.00;
				double allEnoughReducePrice = 0.00;
				for (Map map1 : allOrderGoodss) {
					allBeforePrice += CommUtil.mul(map1.get("goods_price"), map1.get("goods_count"));
					if (null != enoughReduceInfo) {
						allEnoughReducePrice += getEnoughReducePrice(CommUtil.null2Long(map1.get("goods_id")),
								enoughReduceInfo);
					}
				}

				double beforePrice = CommUtil.mul(map.get("goods_price"), map.get("goods_count"));// 商品原金额
				double selfAmount = beforePrice - enoughReducePrice;// 单个商品参与优惠的金额
				double allAmount = allBeforePrice - allEnoughReducePrice;// 所有商品参与优惠总额

				Map<String, Object> couponInfoMap = (Map<String, Object>) Json.fromJson(couponInfo); // 优惠券优惠信息
				double reducePrice = Double.parseDouble(couponInfoMap.get("coupon_amount").toString()); 


				couponPrice = CommUtil.mul(selfAmount / allAmount, reducePrice);
			}
			totalEnoughReducePrice += enoughReducePrice;
			totalCouponPrice += couponPrice;
		}

		Map map = new HashMap();
		map.put("totalEnoughReducePrice", totalEnoughReducePrice);
		map.put("totalCouponPrice", totalCouponPrice);
		return map;
	}

	/**
	 * 获取支付单号
	 */
	@Override
	public String getOriginalNo(OrderForm of) {
		String originalNo;
		if (of.getOrder_main() == 1) {
			originalNo = of.getOrder_id();
		} else {
			// 查询主订单对象
			OrderForm obj = getObjByProperty(null, "order_id", of.getMainOrderId());
			originalNo = obj.getOrder_id();
		}
		return originalNo;
	}

	/**
	 * 判断商品退款、订单退款和消费码部分退还是全不退
	 */
	@Override
	public String getRefundType(OrderForm of, String type) {
		String refundType;
		// 订单退款
		if ("order".equals(type)) {
			// 查看退款订单是否为主订单，为主订单
			if (of.getOrder_main() == 1) {
				if (StringUtils.isNullOrEmpty(of.getChild_order_detail())) {// 没有子单，全部退
					refundType = "2";
				} else {// 有子单，部分退款
					refundType = "1";
				}
				// 为子单
			} else {
				// 查询主订单对象
				refundType = "1"; // 部分退款
			}
			// 商品退款
		} else {
			/**
			 * 运费大于0；购买的商品种类大于0；子订单不为空；不为主订单时；
			 */
			List<Map> goodList = this.orderFormTools.queryGoodsInfo(of.getGoods_info());
			if (of.getShip_price().compareTo(BigDecimal.ZERO) == 1 || goodList.size() > 1
					|| !StringUtils.isNullOrEmpty(of.getChild_order_detail()) || of.getOrder_main() != 1) {
				// 部分退
				refundType = "1";
			} else {
				// 全部退
				refundType = "2";
			}
		}
		return refundType;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param user
	 *            登陆用户
	 * @param mainOrder
	 *            主订单对象
	 * @param channel
	 *            支付渠道APP、WX、WEB、WAP
	 * @param orderType
	 *            支付类型: 1：购物 2：充值3：购票
	 * @param returnUrl
	 *            回调业务系统地址
	 * @return
	 */
	public Object orderPay(HttpServletRequest request, HttpServletResponse response, User user, Object order,
			String channel, String orderType, String returnUrl) {
		ModelAndView mv = new ModelAndView();
		try {
			String str = "";
			String orderId = "";
			if ("WAP".equals(channel)) {
				str = "/wap";
			}
			if ("4".equals(orderType)) {
				IntegralGoodsOrder igo = (IntegralGoodsOrder) order;
				orderId = igo.getIgo_order_sn();
			} else {
				OrderForm of = (OrderForm) order;
				orderId = of.getOrder_id();
			}
			Result result = this.payCenterService.placeOrder(user, orderId, "", channel, orderType, "", returnUrl);
			if (result != null && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((result.getCode()))) {
				Map<String, Object> json = (Map<String, Object>) result.getData();
				if (json != null) {
					String url = CommUtil.null2String(json.get("url"));
					response.getWriter().write("<script>window.location.href='" + url + "'</script>");
					return null;
				}
			} else {
				mv = new JModelAndView(str + "/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", result.getMsg());
				mv.addObject("url", CommUtil.getURL(request) + str + "/index.htm");
				return mv;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mv;
	}
}
