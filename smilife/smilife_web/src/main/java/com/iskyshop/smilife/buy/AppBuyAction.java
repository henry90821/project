package com.iskyshop.smilife.buy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.GoodsInventory;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderGoodsStatistics;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.OrderFreightInfo;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsInventoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IOrderGoodsStatisticsService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.buyer.tools.CartTools;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.smilife.common.CommUtils;
import com.iskyshop.smilife.common.Constants;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

@Controller
@RequestMapping("/api/app")
public class AppBuyAction {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IAppGoodsCartService goodsCartService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IGoodsInventoryService goodsInventoryService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private DeliveryAddressTools deliveryAddressTools;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private UserManageConnector userManageConnector;
	@Autowired
	private IOrderGoodsStatisticsService orderGoodsStatisticsService;
	
	/**
	 * 3.1.4.1	获取购物车商品列表
	 * @param request
	 * @param response
	 * @param user
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value="/mall0701GetCartGoods.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@Token(userNullable = true)
	public Object getCartGoods(HttpServletRequest request, HttpServletResponse response,User user,String sessionId) {
		int code =Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map<String,Object> resultData = new HashMap<String,Object>();
		
		try {
			SysConfig config = configService.getSysConfig();
//			String webPath = getWebPath(request, config);
			
//			CustDto custDto = new CustDto();
//			if(StrKit.isNotEmpty(token)){
//				custDto = userManageConnector.getSessionUser(token);
//			}
			if(null == user){
				user = new User();
			}
			List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response,user.getCustId(),sessionId);
			if (carts.size() > 0) {
				Date date = new Date();
				List<GoodsCart> native_goods = new ArrayList<GoodsCart>(); //普通商品购物车列表
				List<GoodsCart> combin_carts = new ArrayList<GoodsCart>(); //组合商品购物车列表
				Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>(); //满减商品购物车列表
				Map<Long, String> erString = new HashMap<Long, String>();
				Map<Long, List<GoodsCart>> bgmap = new HashMap<Long, List<GoodsCart>>(); //满送商品购物车列表
				Map<Long, String> bgString = new HashMap<Long, String>();
				for (GoodsCart cart : carts) {
					if (cart.getGoods().getOrder_enough_give_status() == 1) { //满就送
						BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
						if (bg.getBeginTime().before(date)) {
							if (bgmap.containsKey(bg.getId())) {
								bgmap.get(bg.getId()).add(cart);
							} else {
								List<GoodsCart> list = new ArrayList<GoodsCart>();
								list.add(cart);
								bgmap.put(bg.getId(), list);
								
								bgString.put(bg.getId(), "活动商品已购满"+bg.getCondition_amount()+"元， 即可选择赠品");
							}
							continue;
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
							continue;
						} 
					}else if ("combin".equals(cart.getCart_type())) {
						if (cart.getCombin_main() == 1) {
							combin_carts.add(cart);
						}
						continue;
					}
					
					native_goods.add(cart);
				}
				
				// 满就减
				List<Map> erCarts = new ArrayList<Map>();
				for(long id : ermap.keySet()){
					Map<String,Object> erInfo = new HashMap<String,Object>();
					erInfo.put("id", id);
					erInfo.put("desc", erString.get(id));
					List<Map> gcInfos = new ArrayList<Map>();
					for(GoodsCart gc : ermap.get(id)){
						Goods goods = gc.getGoods();
						Map<String,Object> gcInfo = new HashMap<String,Object>();
						gcInfo.put("id", gc.getId());
						gcInfo.put("storeStatus", goods.getGoods_store()==null?"":goods.getGoods_store().getStore_status());
						gcInfo.put("cartType", gc.getCart_type());
//						gcInfo.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+goods.getId());
						Accessory photo = goods.getGoods_main_photo();
						if(null != photo){
							gcInfo.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
						}else{
							gcInfo.put("goodPicUrl", config.getGoodsImage().getPath()+"/"+config.getGoodsImage().getName());
						}
						gcInfo.put("fSaleType", goods.getF_sale_type());
						gcInfo.put("count", gc.getCount());
						gcInfo.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
						gcInfo.put("goodPrice",  CommUtil.formatMoney(gc.getPrice()));
						gcInfo.put("totalPrice", CommUtil.formatMoney(CommUtil.mul(gc.getPrice(), gc.getCount())));
						
						gcInfo.put("goodId", goods.getId());
						gcInfo.put("goodName", goods.getGoods_name());
						gcInfos.add(gcInfo);
					}
					
					erInfo.put("carts", gcInfos);
					erCarts.add(erInfo);
				}
				resultData.put("erCarts", erCarts);
				
				// 将有活动的商品分组(满就送)
				List<Map> bgCarts = new ArrayList<Map>();
				for(long id : bgmap.keySet()){
					Map<String,Object> bgInfo = new HashMap<String,Object>();
					bgInfo.put("id", id);
					bgInfo.put("desc", bgString.get(id));
					
					List<Map> gcInfos = new ArrayList<Map>();
					for(GoodsCart gc : bgmap.get(id)){
						Goods goods = gc.getGoods();
						Map<String,Object> gcInfo = new HashMap<String,Object>();
						gcInfo.put("goodId", goods.getId());
						gcInfo.put("goodName", goods.getGoods_name());
						Accessory photo = goods.getGoods_main_photo();
						if(null != photo){
							gcInfo.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
						}else{
							gcInfo.put("goodPicUrl", config.getGoodsImage().getPath()+"/"+config.getGoodsImage().getName());
						}
//						gcInfo.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+goods.getId());
						gcInfo.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
						gcInfo.put("totalPrice",CommUtil.formatMoney( CommUtil.mul(gc.getPrice(), gc.getCount())));
						
						gcInfo.put("seckillBuy", goods.getSeckill_buy());
						gcInfo.put("id", gc.getId());
						gcInfo.put("cartType", gc.getCart_type());
						gcInfo.put("groupBuy", goods.getGroup_buy());
						gcInfo.put("fSaleType", goods.getF_sale_type());
						gcInfo.put("count", gc.getCount());
						gcInfo.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
						gcInfo.put("activityStatus", goods.getActivity_status());
						gcInfos.add(gcInfo);
					}
					bgInfo.put("carts", gcInfos);
					
					List<Map> giftCarts = new ArrayList<Map>();
					BuyGift buyGift = goodsViewTools.query_buyGift(String.valueOf(id));
					for(Map map : CommUtil.json2List(buyGift.getGift_info())){
						int storegoods_count = (int)map.get("storegoods_count");
						int enough=1;
						if(storegoods_count==0 && (int)map.get("goods_count") <= 0){
			            	enough=0;
						}else if(storegoods_count==1){
							Goods g = goodsViewTools.query_Goods(String.valueOf(map.get("goods_id")));
							if(g.getGoods_inventory()<=0){
								enough=0;
							}
						}
						if(enough==1){
							Map<String,Object> gcInfo = new HashMap<String,Object>();
							gcInfo.put("goodId", map.get("goods_id"));
							gcInfo.put("cartType", map.get("cart_type"));
							String photo = (String) map.get("goods_main_photo");
							if(null != photo && !"".equals(photo)){
								gcInfo.put("goodPicUrl", photo);
							}else{
								gcInfo.put("goodPicUrl", config.getGoodsImage().getPath()+"/"+config.getGoodsImage().getName());
							}
//							gcInfo.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+map.get("goods_id"));
							gcInfo.put("goodName", map.get("goods_name"));
							gcInfo.put("fSaleType", map.get("f_sale_type"));
							gcInfo.put("count", map.get("count"));
							
							gcInfo.put("goodPrice", CommUtil.formatMoney(map.get("goods_price")));
							giftCarts.add(gcInfo);
						}
					}
					bgInfo.put("giftCarts", giftCarts);
					
					bgCarts.add(bgInfo);
				}
				resultData.put("bgCarts", bgCarts);

				// 无活动的商品购物车
				List<Map> gcInfos = new ArrayList<Map>();
				for(GoodsCart gc : native_goods){
					Goods goods = gc.getGoods();
					Map<String,Object> gcInfo = new HashMap<String,Object>();
					gcInfo.put("seckillBuy", goods.getSeckill_buy());
					gcInfo.put("id", gc.getId());
					gcInfo.put("cartType", gc.getCart_type());
					gcInfo.put("goodId", goods.getId());
					Accessory photo = goods.getGoods_main_photo();
					if(null != photo){
						gcInfo.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
					}else{
						gcInfo.put("goodPicUrl", config.getGoodsImage().getPath()+"/"+config.getGoodsImage().getName());
					}
//					gcInfo.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+goods.getId());
					gcInfo.put("goodName", goods.getGoods_name());
					gcInfo.put("groupBuy", goods.getGroup_buy());
					gcInfo.put("fSaleType", goods.getF_sale_type());
					gcInfo.put("count", gc.getCount());
					gcInfo.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
					gcInfo.put("totalPrice", CommUtil.formatMoney(CommUtil.mul(gc.getPrice(), gc.getCount())));
					
					gcInfo.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
					gcInfo.put("activityStatus", goods.getActivity_status());
					
					gcInfos.add(gcInfo);
				}
				resultData.put("carts", gcInfos);
				
				// 组合套装商品购物车
				List<Map> combinCarts = new ArrayList<Map>();
				for(GoodsCart gc : combin_carts){
					List<Map> gcInfos2 = new ArrayList<Map>();
					Map<String,Object> combinInfo = new HashMap<String,Object>();
					Map suit_map=goodsViewTools.getSuitInfo(String.valueOf(gc.getId()));
					combinInfo.put("all_goods_price", CommUtil.formatMoney(Double.valueOf((String)suit_map.get("all_goods_price"))));
					combinInfo.put("plan_goods_price", CommUtil.formatMoney(Double.valueOf((String)suit_map.get("plan_goods_price"))));
					
					Goods goods = gc.getGoods();
					Map<String,Object> gcInfo = new HashMap<String,Object>();
					gcInfo.put("goodId", goods.getId());
					gcInfo.put("cartType", gc.getCart_type());
//					gcInfo.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+goods.getId());
					Accessory photo = goods.getGoods_main_photo();
					if(null != photo){
						gcInfo.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
					}else{
						gcInfo.put("goodPicUrl", config.getGoodsImage().getPath()+"/"+config.getGoodsImage().getName());
					}
					gcInfo.put("fSaleType", goods.getF_sale_type());
					gcInfo.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
					gcInfo.put("count", gc.getCount());
					gcInfo.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
					gcInfo.put("combinMark", gc.getCombin_mark());
					gcInfo.put("totalPrice", CommUtil.formatMoney(CommUtil.mul(gc.getPrice(), gc.getCount())));
					
					gcInfo.put("id", gc.getId());
					gcInfo.put("goodName", goods.getGoods_name());
					gcInfos2.add(gcInfo);
					combinInfo.put("carts", gcInfos2);
					combinCarts.add(combinInfo);
				}
				resultData.put("combinCarts", combinCarts);
			}

		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(code, msg, resultData);
	}

	/**
	 * @param request
	 * @param config
	 * @return
	 */
	private String getWebPath(HttpServletRequest request, SysConfig config) {
		String webPath = CommUtil.getURL(request);
		String port = request.getServerPort() == 80 ? "" : ":" + CommUtil.null2Int(request.getServerPort());
		if (Globals.SSO_SIGN) {
			if (config.isSecond_domain_open() && !"localhost".equals(CommUtil.generic_domain(request)) && !CommUtil.isIp(request.getServerName())) {
				String contextPath = "";
				if (!("/").equals(request.getContextPath())) {
								contextPath = request.getContextPath();
				}
				webPath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
			}
		}
		logger.debug("webPath:"+webPath);
		return webPath;
	}
	
	/**
	 * 3.1.4.2	加入购物车商品
	 * @param request
	 * @param response
	 * @param id 添加到购物车的商品id
	 * @param count 添加到购物车的商品数量
	 * @param gsp 商品的属性值，这里传递id值，如12,1,21
	 * @param suit_gsp 组合套装中各商品的规格值
	 * @param buy_type 购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids 组合搭配中配件id
	 * @param user
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value="/mall0702AddCartGood.htm",method={RequestMethod.POST})
	@ResponseBody
	@Token(userNullable = true)
	public Object addCartGood(HttpServletRequest request, HttpServletResponse response, Long id, Integer count,
			String gsp, String suit_gsp, String buy_type, String combin_ids,User user,String sessionId) {
		int code = Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map<String,Object> data=null;
		try {
//			CustDto custDto = new CustDto();
//			if(StrKit.isNotEmpty(token)){
//				custDto = userManageConnector.getSessionUser(token);
//			}
			if(null == user){
				user = new User();
			}
			
			Map json_map = this.goodsCartService.add_goodsCart(request, response, id, count, gsp, 
					suit_gsp, buy_type, combin_ids, 1l,user.getCustId(),sessionId);
			if( (int) json_map.get("code") != 0 ){
				code = Constants.RESPONSECODE_REQUEST_ERROR;
				msg = (String) json_map.get("msg");
			}
			else if(json_map.get("seckill_gcid")!=null){//如果添加的是秒杀商品 则结果集合里面包含秒杀商品购物车ID
				data=new HashMap<String,Object>();
				data.put("seckill_gcid",json_map.get("seckill_gcid"));
			}
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, data);
		}
		
		//包含秒杀商品购物车ID
		return CommUtils.buidResult(code, msg, data);
	}
	
	/**
	 * 3.1.4.3	删除购物车商品
	 * @param request
	 * @param response
	 * @param ids
	 * @param token
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value="/mall0703DelCartGood.htm",method={RequestMethod.POST})
	@ResponseBody
	public Object delCartGood(HttpServletRequest request, HttpServletResponse response, String ids,String token,String sessionId) {
		int code = Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		
		try {
			List<String> list_ids = new ArrayList<String>();
			if (!StringUtils.isNullOrEmpty(ids)) {
				String[] cart_ids = ids.split(",");
				for (String id : cart_ids) {
					if (!StringUtils.isNullOrEmpty(id)) {
						list_ids.add(id);
					}
				}
			}
			String isOk = this.goodsCartService.remove_carts(list_ids);
			if(!"100".equals(isOk)){
				code = Constants.RESPONSECODE_REQUEST_ERROR;
				msg = "请求错误";
			}

		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(code, msg, null);
	}
	
	
	/**
	 * 3.1.4.5	调整购物车商品数量
	 * @param request
	 * @param response
	 * @param gc_id 购物车id
	 * @param count 商品数量
	 * @param gcs 勾选的所有购物车ID
	 * @param gift_id 满就送活动id
	 * @param user
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value="/mall0705AdjustCartGoods.htm",method={RequestMethod.POST})
	@ResponseBody
	@Token(userNullable = true)
	public Object adjustCartGoods(HttpServletRequest request, HttpServletResponse response,String gc_id, String count,
			String gcs, String gift_id,User user,String sessionId) {
		int resultCode = Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map data = new HashMap();
		
		try {
//			CustDto custDto = new CustDto();
//			if(StrKit.isNotEmpty(token)){
//				custDto = userManageConnector.getSessionUser(token);
//			}
			if(null == user){
				user = new User();
			}
			List<GoodsCart> carts = this.goodsCartService.cart_calc(request, response,user.getCustId(),sessionId);// 获取当前用户的所有购物车(包括无效的)
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
							&& gc.getGoods().getGroup_buy() != 2) {//库存充足
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gc.getPrice(), count);
						} else {
							if (inventory == 0) {
								gc.setCount(0);
								this.goodsCartService.update(gc);
							}
							code = "200";
						}
					}
					if("group".equals(cart_type) && goods.getGroup_buy() == 2){// 团购商品处理
						GroupGoods gg = new GroupGoods();
						for (GroupGoods gg1 : goods.getGroup_goods_list()) {
							if (gg1.getGg_goods().getId().equals(goods.getId()) && gg1.getGg_status()==1) {//modify by liz 商品可重复参加同一团购，需要增加审核通过的过滤条件
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
					
					if("seckill".equals(cart_type) && goods.getSeckill_buy() == 2){// 秒杀商品处理
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
							suit_map.put("suit_all_price", CommUtil.formatMoney(suit_all_price)); // 套装整体价格=套装单价*数量
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
						if(bg != null) {
							Set<Long> bg_ids = new HashSet<Long>();// 保存当前被用户选中的所有购物车所关联的所有满就送活动的id的集合
							bg_ids.add(bg.getId());
							List<GoodsCart> g_carts = new ArrayList<GoodsCart>();// 保存当前被用户选中的所有购物车中参加了满就送活动的购物车
							if ("".equals(CommUtil.null2String(gcs))) {//购物车页面似乎永远不会传一个空串的gcs上来吧？？？？
								for (GoodsCart gCart : carts) {//遍历用户的所有购物车，而不是用户已勾选的购物车
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
							data.put("bg_ids", enough_bg_ids);
						}
					}			
				} else {
					code = "200";
				}
				data.put("count", gc.getCount());
			}
			
			if ("200".equals(code)||"300".equals(code)) {
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "200".equals(code)?"库存不足":"团购库存不足", null);
			}
			
			total_price = orderFormTools.calCartPrice(carts, gcs);
			Map price_map = this.orderFormTools.calEnoughReducePrice(carts, gcs);
			Map<Long, String> erMap = (Map<Long, String>) price_map.get("erString");
			data.put("gc_price", CommUtil.formatMoney(gc_price));
			data.put("total_price", CommUtil.formatMoney(total_price));
			data.put("enough_reduce_price", CommUtil.formatMoney(price_map.get("reduce")));
			data.put("before", CommUtil.formatMoney(price_map.get("all")));
			List<Map> erInfos = new ArrayList<Map>();
			for (long k : erMap.keySet()) {
				Map erInfo = new HashMap();
				erInfo.put("id", k);
				erInfo.put("msg", erMap.get(k));
				erInfos.add(erInfo);
			}
			data.put("erInfos" ,erInfos);
			
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		
		return CommUtils.buidResult(resultCode, msg, data);
	}

	/**
	 * 3.1.4.6	确认购物车
	 * @param request
	 * @param response
	 * @param gcs 购物车id集
	 * @param giftids 赠品id集
	 * @param addr_id 收货地址id
	 * @param orderType 秒杀标识，值为"s+秒杀商品id"
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/mall0706SubmitCartGoods.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@Token
	public Object submitCartGoods(HttpServletRequest request, HttpServletResponse response, 
			String gcs, String giftids, Long addr_id, String orderType,User user) {
		logger.info("submitCartGoods start params:[gcs:"+gcs+",giftids:"+giftids+",addr_id:"+addr_id+",orderType:"+orderType+",custId:"+user.getCustId());
		Map data = new HashMap(); //存储返回数据
		Map<String,Object> orderFormDTOMap =  new HashMap<String,Object>(); //存储待缓存数据
		
		try {
			List<GoodsCart> carts = null; // 用户选中的购物车
			Map erpMap = null; //满减活动信息
			
			//秒杀商品立即购买进入，需根据商品id和userid查询对应的购物车
			if (null != orderType && orderType.matches("s\\d+")) {
				//秒杀商品
				Goods goods = goodsService.getObjById(Long.parseLong(orderType.substring(1)));
				
				//秒杀商品对应的购物车
				Map cart_map = new HashMap();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				cart_map.put("goods_id", goods.getId());
				carts = goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.goods.id=:goods_id",
								cart_map, -1, -1);
				// 未登录状态下，购买秒杀商品，购物车中userid为空，查出结果集为空，需将cookie购物车转换为user购物车后重新查询
				if (null == carts || carts.isEmpty()) {
					throw new Exception("参数错误，请重新购买");
				}
				gcs = carts.get(0).getId().toString();
			} else {
				if (StringUtils.isNullOrEmpty(gcs)) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "参数错误，请重新选择购物车", null);
				}
				carts = this.goodsCartService.cart_calc(request, response,user.getCustId(),null);
				erpMap = this.orderFormTools.calEnoughReducePrice(carts, gcs);
				logger.info("gcs=" + gcs + ",用户选择的购物车进行满减计算后的内容：" + erpMap);
				if ("".equals((String) erpMap.get("gcIds"))) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "请勿篡改请求参数！", null);
				}
				gcs = (String) erpMap.get("gcIds"); // 用户选择的最终有效的购物车的id集
				gcs=gcs.endsWith(",")?gcs.substring(0,gcs.length()-1):gcs;
				carts = (List<GoodsCart>) erpMap.get("carts"); // 用户选择的最终有效的购物车
				
				data.put("order_goods_price", CommUtil.formatMoney(erpMap.get("all")));
				data.put("order_er_price", CommUtil.formatMoney(erpMap.get("reduce")));
			}

			if (carts.size() <= 0) {
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "购物车信息为空", null);
			}
			
			int needId = 0;// 用户购买商品时是否需要填写身份证，只要用户购买的商品中有一个需要身份证，则就需要填写身份证购买(此值不能由前台传过来)
			boolean isGoodsValid = true; // 检查商品状态是否为已上架
			boolean isStoreValid = true; // 检查店铺状态
			Goods g = null;
			boolean hadSeckillGoods=false;//是否有秒杀商品
			
			Set<Object> store_list = new HashSet<Object>();
			for (GoodsCart gc : carts) {
				g = gc.getGoods();
				if(1 == g.getGoods_type() && 15 != g.getGoods_store().getStore_status()){//非营业状态
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
				if(g.getSeckill_buy()==2 || g.getSeckill_buy()==4){//秒杀状态为 开始  ，或者为未开始 都不会使用全场优惠券
					hadSeckillGoods=true;
				}
			}
			if (!isStoreValid) {
				throw new Exception("商品（" + g.getGoods_name() + "）店铺已停业，请将其从购物车中移除后再重新下单。");
			}
			if (!isGoodsValid) {// 用户选择的购物车中有未上架的商品
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "商品（" + g.getGoods_name() + "）已下架，请将其从购物车中移除后再重新下单。", null);
			}

			//判断订单是否支持到店自提
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

			if (selfPickupEnabled == 1) {
				//查询省市区列表，为查询门店做准备
				List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
				List<Map> areaMaps = new ArrayList<Map>();
				if(!areas.isEmpty()){
					for(Area area : areas){
						Map areaMap = new HashMap<>();
						areaMap.put("id", area.getId());
						areaMap.put("areaName",area.getAreaName());
						areaMaps.add(areaMap);
					}
				}
				data.put("areas",areaMaps);
			}
			data.put("selfPickupEnabled", selfPickupEnabled);
			orderFormDTOMap.put("selfPickupEnabled",selfPickupEnabled);

			//查询收货地址
			Map params = new HashMap();
			String hqlPart = "select obj from Address obj where obj.user.id=:user_id ";
			params.put("user_id", user.getId());
			if (addr_id != null) {
				hqlPart = hqlPart + "and obj.id=:id ";
				params.put("id", addr_id);
			}
			hqlPart += "order by obj.default_val desc,obj.addTime desc";
			List<Address> addrs = this.addressService.query(hqlPart, params, 0, 1);

			Address defaultAddr = null; //默认收货地址
			List<Map> addressMaps = new ArrayList<Map>();
			if(!addrs.isEmpty()) {
				defaultAddr = addrs.get(0);
				data.put("addr_id", defaultAddr.getId());
				
				for(Address address : addrs){
					Map addressMap = new HashMap<>();
					addressMap.put("id", address.getId());
					addressMap.put("areaId", address.getArea().getId());
					addressMap.put("trueName", address.getTrueName());
					addressMap.put("mobile", address.getMobile());
					addressMap.put("province", address.getArea().getParent().getParent().getAreaName());
					addressMap.put("city", address.getArea().getParent().getAreaName());
					addressMap.put("county", address.getArea().getAreaName());
					addressMap.put("areaInfo", address.getArea_info());
					addressMap.put("card", address.getCard());
					addressMaps.add(addressMap);
				}
			}
			data.put("addrs", addressMaps);

			String cart_session = CommUtil.randomString(32);
			data.put("cart_session", cart_session);
			orderFormDTOMap.put("cartSession",cart_session);

			List<Goods> ac_goodses = new ArrayList<Goods>(); // 用户选择的满就赠活动的的赠品，在下面会作初步校验，在后面还要作进一步的校验，防止前端篡改数据
			if (!StringUtils.isNullOrEmpty(giftids)) {
				String[] gift_ids = giftids.split(",");
				for (String gift_id : gift_ids) {
					Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gift_id));
					if (goods != null
							&& goods.getOrder_enough_if_give() == 1
							&& goods.getBuyGift_id() != null
							&& store_list.contains(goods.getGoods_store() == null ? "self" : goods.getGoods_store().getId())) {
						ac_goodses.add(goods);
					}
				}
			}

			Map<Object, Map<Long, Set<Long>>> giftCartMapping = new HashMap<Object, Map<Long, Set<Long>>>();// key:店铺id或self；value为Map:key:赠品的id，value:赠品对应的购物车的id集
			Map<Object, Set<Long>> storeCartMapping = new HashMap<Object, Set<Long>>();// key:店铺id或self；value:此店铺对应的购物车的id集
			Map<Object, Map> storeTransportsMapping = new HashMap<Object, Map>();// key:店铺id或self；value:此店铺对应的默认运费计算结果及结果对应的收货地址的id
			double xmO2ODistance = CommUtil.null2Double(PropertyUtil.getProperty("XM_O2O_DISTANCE"));

			double order_total_price=0.00;//订单总金额
			double order_ship_price=0.00;//订单总运费
			
			Map<Object, Map> hxgoods =  new HashMap<Object, Map>();
			StringBuilder validGiftIds = new StringBuilder();// 合法的赠品的id集合
			List<Map> map_list = new ArrayList<Map>();// 其中每个元素为一个Map类型的对象，此对象代表了一个店铺（其中保存了用户在此店铺中购买的商品的相关信息）
			SysConfig config = configService.getSysConfig();
			String webPath = getWebPath(request, config);
			boolean goods_cod = true; // 默认支持货到付款，但只要有一款产品不支持货到付款，这个订单就不支持货到付款
			int tax_invoice = 1; // 默认可以开具增值税发票，但只要存在一款产品不支持增值税发票，整个订单就不可以开具增值税发票
			Date now = new Date();
			List<GoodsCart> all_normal_carts=new ArrayList<GoodsCart>();//最终所有普通商品
			for (Object sl : store_list) {
				List<GoodsCart> gc_list = new ArrayList<GoodsCart>(); // gc_list是amount_gc_list中剔除了参加满减活动和满赠活动的商品后的商品对应的购物车集合
				List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();// 用户在当前店铺所选择的商品对应的购物车集合
				Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();// 当前店铺中的相关赠品。key=赠品对象，value：赠品关联的购物车集合
				Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();// key=满减活动的id，value：满减活动关联的购物车集合
				Map<Long, String> erString = new HashMap<Long, String>(); // 保存满减活动在前台页面的提示内容（显示满减活动中最低限额的那个满减的内容）。如有满200减20，满100减10的满减活动，则保存内容为
				
				for (Goods gg : ac_goodses) {
					Store store = gg.getGoods_store();
					if ((store == null && "self".equals(sl)) || (store != null && store.getId().equals(sl))) {
						gift_map.put(gg, new ArrayList<GoodsCart>());
					}
				}
				for (GoodsCart gc : carts) { // 对用户选择的购物车进行遍历
					Goods goods = gc.getGoods();
					Store store = goods.getGoods_store();
					if ((store == null && "self".equals(sl)) || (store != null && store.getId().equals(sl))) {// 获得当前店铺的购物车
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
						List<Map> ac_goods = new ArrayList<Map>();
						for(Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()){
							Map giftMap = new HashMap<>();
							
							giftMap.put("goodsName", entry.getKey().getGoods_name());
							Accessory photo1 = entry.getKey().getGoods_main_photo();
							if(null != photo1){
								giftMap.put("goodsPicUrl", photo1.getPath()+"/"+photo1.getName()+"_small."+photo1.getExt());
							}
							giftMap.put("id", entry.getKey().getId());
							
							List<Map> list = new ArrayList<>();
							for(GoodsCart gc :entry.getValue()){
								Map map2 = new HashMap<>();
								map2.put("id", gc.getId());
								map2.put("goodId", gc.getGoods().getId());
								map2.put("goodName", gc.getGoods().getGoods_name());
								map2.put("choiceType", gc.getGoods().getGoods_choice_type());
								Accessory photo = gc.getGoods().getGoods_main_photo();
								if(null != photo){
									map2.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
								}
//									map2.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+gc.getGoods().getId());
								map2.put("specInfo", gc.getSpec_info());
								map2.put("count", gc.getCount());
//								map2.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
								map2.put("cartType", gc.getCart_type());
								map2.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
								map2.put("totalPrice", CommUtil.formatMoney(CommUtil.mul(gc.getPrice(), gc.getCount())));
								list.add(map2);
							}
							giftMap.put("gcs", list);
							
							ac_goods.add(giftMap);
						}
						map.put("ac_goods", ac_goods);
						
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
					List<Map> er_goods = new ArrayList<Map>();
					for(Map.Entry<Long, List<GoodsCart>> entry : ermap.entrySet()){
						Map giftMap = new HashMap<>();
						
						giftMap.put("id", entry.getKey());
						giftMap.put("desc", ((Map<Long, String>)erpMap.get("erString")).get(entry.getKey()));
						
						List<Map> list = new ArrayList<>();
						for(GoodsCart gc :entry.getValue()){
							Map map2 = new HashMap<>();
							map2.put("id", gc.getId());
							map2.put("goodId", gc.getGoods().getId());
							map2.put("goodName", gc.getGoods().getGoods_name());
							map2.put("choiceType", gc.getGoods().getGoods_choice_type());
							Accessory photo = gc.getGoods().getGoods_main_photo();
							if(null != photo){
								map2.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
							}
//								map2.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+gc.getGoods().getId());
							map2.put("specInfo", gc.getSpec_info());
							map2.put("count", gc.getCount());
							map2.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
							map2.put("cartType", gc.getCart_type());
							map2.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
							map2.put("totalPrice",CommUtil.formatMoney( CommUtil.mul(gc.getPrice(), gc.getCount())));
							list.add(map2);
							all_normal_carts.add(gc);
						}
						giftMap.put("gcs", list);
						
						er_goods.add(giftMap);
					}
					map.put("er_goods", er_goods);
				}

				map.put("store_id", sl);
				if (!"self".equals(sl)) {
					map.put("storeName", this.storeService.getObjById((Long) sl).getStore_name());
				}

				List<Map> list = new ArrayList<Map>();
				for(GoodsCart gc :gc_list){
					Map map2 = new HashMap<>();
					map2.put("id", gc.getId());
					map2.put("goodId", gc.getGoods().getId());
					map2.put("goodName", gc.getGoods().getGoods_name());
					map2.put("choiceType", gc.getGoods().getGoods_choice_type());
					Accessory photo = gc.getGoods().getGoods_main_photo();
					if(null != photo){
						map2.put("goodPicUrl", photo.getPath()+"/"+photo.getName()+"_small."+photo.getExt());
					}
//						map2.put("goodDetailUrl", webPath + "/wap/goods.htm?id="+gc.getGoods().getId());
					map2.put("specInfo", gc.getSpec_info());
					map2.put("activityStatus", gc.getGoods().getActivity_status());
					map2.put("groupStatus", gc.getGoods().getGroup_buy());
					map2.put("seckillStatus", gc.getGoods().getSeckill_buy());
					map2.put("count", gc.getCount());
					map2.put("iconUrl", gc.getGoods().getGoodsConfig().getIconUrl());
					map2.put("cartType", gc.getCart_type());
					map2.put("goodPrice", CommUtil.formatMoney(gc.getPrice()));
					ProductMapping productMapping = gc.getGoods().getProductMapping();
					if(null != productMapping){
						map2.put("goodsCode", productMapping.getGoodsCode());
					}
					map2.put("totalPrice", CommUtil.formatMoney(CommUtil.mul(gc.getPrice(), gc.getCount())));
					
					if("combin".equals(gc.getCart_type())){
						List<Map> suit_goodsMap = goodsViewTools.getsuitGoods(webPath,String.valueOf(gc.getId()));
						map2.put("suit_goods", suit_goodsMap);
					}
					
					list.add(map2);
					all_normal_carts.add(gc);
				}
				map.put("gc_list", list);
				
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
							return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, (String)results.get("result"), null);
						} else {
							List<Map> branches = (List<Map>) results.get("result");
							// 计算满足星美门店o2o两公里免运费的购物车及其中的商品数量
							for (Map store : branches) {
								double distance = (Double) store.get("distance");
								ShipAddress sa = (ShipAddress) store.get("address");
								store.put("address", sa.getId());
								if (distance <= xmO2ODistance && sa.getO2oCapable() == 1) {
									// TODO:除了上述条件外，可能还会增加消费限额（freeShipFeeThreashold）这一条件(注意本类中还有一处order_address函数中有和此处类似的关于海信o2o的代码要修改)。
									List<Long> goodscart_list = (List<Long>) store.get("goodscart_list");
									List<Integer> goods_count_list = (List<Integer>) store.get("goods_count_list");
									for (int i = 0; i < goodscart_list.size(); i++) {
										Long gcId = goodscart_list.get(i);
										Integer freeCount = goods_count_list.get(i);
										int idx = freeShipFeeGoodsCartIds_o2o.indexOf(gcId);
										if (idx >= 0) {// 已存在
											freeShipFeeCountsForGoodsCart_o2o.set(idx,
													freeShipFeeCountsForGoodsCart_o2o.get(idx) + freeCount);
										} else {
											freeShipFeeGoodsCartIds_o2o.add(gcId);
											freeShipFeeCountsForGoodsCart_o2o.add(freeCount);
										}
									}
								}
							}

							Map storeHXgoods = new HashMap();
							storeHXgoods.put("goodsDispached", branches);
							storeHXgoods.put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2o);
							storeHXgoods.put("freeShipFeeCountsForGoodsCart_o2o", freeShipFeeCountsForGoodsCart_o2o);
							hxgoods.put(sl, storeHXgoods);
						}
					}

				}
				List<Map> o2oList = new ArrayList<>();
				for(int i=0;i<freeShipFeeGoodsCartIds_o2o.size();i++){
					Map map1 = new HashMap<>();
					map1.put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2o.get(i));
					map1.put("freeShipFeeCountsForGoodsCart_o2o", freeShipFeeCountsForGoodsCart_o2o.get(i));
					o2oList.add(map1);
				}
				map.put("o2o", o2oList);

				//begin dengyuqi 2016-3-4 
				Map<String,List<OrderFreightInfo>> freightInfos = new HashMap<String, List<OrderFreightInfo>>(); //key为发货方式(平邮、快递、EMS)，value为运费信息
				//end

				String defaulAddrAreaId = "";
				if (defaultAddr != null) {
					defaulAddrAreaId = defaultAddr.getArea().getId().toString();
				}
				List<SysMap> storeTransports = transportTools.calStoreCartsTransFee(amount_gc_list, defaulAddrAreaId,
						freeShipFeeGoodsCartIds_o2o, freeShipFeeCountsForGoodsCart_o2o, freightInfos);
				List<Map> storeTransportList = new ArrayList();
				for(SysMap sm : storeTransports){
					Map map1 = new HashMap<>();
					map1.put("key",sm.getKey());
					map1.put("value", sm.getValue());
					storeTransportList.add(map1);
				}
				
				Map transportAndAddr = new HashMap();
				transportAndAddr.put("receiverAddrId", defaultAddr == null ? null : defaultAddr.getId());// 保存当前计算的运费结果对应的收货地址，以便在goods_cart3中进行校验
				transportAndAddr.put("transports", storeTransportList);
				
				//begin dengyuqi 2016-3-4
				transportAndAddr.put("freightInfos", freightInfos);
				//end
				
				storeTransportsMapping.put(sl, transportAndAddr);
				map.put("transports", storeTransports);
				
				map.put("store_goods_price", CommUtil.formatMoney(orderFormTools.calCartPrice(amount_gc_list, "")));//store_goods_price为已减去满减优惠的店铺商品总金额，可用的优惠券是根据此金额来判断的，而不是根据原商品总额来判断的
				
				order_ship_price = CommUtil.add(order_ship_price, storeTransports.get(0).getValue());
				order_total_price = CommUtil.add(order_total_price, CommUtil.add(map.get("store_goods_price"),storeTransports.get(0).getValue()));
				
				List<CouponInfo> couponInfos = cartTools.query_coupon(user.getCustId(),String.valueOf(sl),String.valueOf(Double.parseDouble((String)map.get("store_goods_price"))));
				if(!couponInfos.isEmpty()){
		    		if(!gc_list.isEmpty() 
		    				&& ((int)gc_list.get(0).getGoods().getSeckill_buy() == 2 || (int)gc_list.get(0).getGoods().getSeckill_buy() == 4)){
					}else{
						List<Map> cList = new ArrayList<>();
						for(CouponInfo c : couponInfos){
							Map map1 = new HashMap<>();
							map1.put("id", c.getId());
							map1.put("coupon_amount", c.getCoupon().getCoupon_amount());
							map1.put("coupon_name", c.getCoupon().getCoupon_name());
							cList.add(map1);
						}
			            map.put("couponinfos", cList);
					}
				}
				
				map_list.add(map);
			}
			
			data.put("order_total_price", CommUtil.formatMoney(order_total_price));
			data.put("order_ship_price", CommUtil.formatMoney(order_ship_price));
			
			// 生成7天时间区间
			List<String> days = new ArrayList<String>();
			for (int i = 0; i < 7; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, i);
				days.add(CommUtil.formatTime("MM-dd", cal.getTime()) + "<br />"
						+ this.generic_day(cal.get(Calendar.DAY_OF_WEEK)));
			}

			data.put("global_couponinfos", hadSeckillGoods ? null : cartTools.query_global_couponinfos(user.getCustId(), all_normal_carts) );//全场优惠券
			data.put("orderType",orderType);
			Map userMap = new HashMap<>();
			userMap.put("invoice", user.getInvoice());
			userMap.put("invoiceType", user.getInvoiceType());
			data.put("user", userMap);
			data.put("days", days);
			
			data.put("map_list", map_list);
			data.put("gcs", gcs);
			data.put("goods_cod", goods_cod);
			data.put("tax_invoice", tax_invoice);
			data.put("giftids", validGiftIds.toString());
			data.put("needId", needId);

			// 保存计算出来的用户已选择的购物车、相关的店铺、赠品，用于在goods_cart3中使用，以避免重复计算和校验
			orderFormDTOMap.put("hXGoods",hxgoods);
			orderFormDTOMap.put("gcs",gcs);
			orderFormDTOMap.put("storeList",store_list);
			orderFormDTOMap.put("validGiftIds",String.valueOf(validGiftIds));
			orderFormDTOMap.put("giftCartMapping",giftCartMapping);
			orderFormDTOMap.put("storeCartMapping",storeCartMapping);
			orderFormDTOMap.put("needId",String.valueOf(needId));
			orderFormDTOMap.put("storeTransportsMapping",storeTransportsMapping);
			//orderFormDTOMap e.g.:{"selfPickupEnabled":0,"cartSession":"xxxxxxxxxxxxxxxx",
			//"hXGoods":{"self":{"goodsDispached":[{"address":0,"distance":0,"goodscart_list":[0,0,0],"goods_count_list":[0,0,0]}],
			//"freeShipFeeGoodsCartIds_o2o":[0,0,0],"freeShipFeeCountsForGoodsCart_o2o":[0,0,0]}},
			//"gcs":"1,2,3","storeList":"self,2","validGiftIds":"1,2,3","giftCartMapping":{"self":{"0":[1,2,2]}},
			//"storeCartMapping":{"self":[1,2,1]},"needId":1,"storeTransportsMapping":{"self":{"receiverAddrId":0,
			//"transports":[{"平邮":12.1}]}}}
			logger.info("submitCartGoods start putRedisData:"+"【mall_order_"+user.getCustId()+"】："+JSON.toJSONString(orderFormDTOMap));
			ResultDTO resultDTO = userManageConnector.saveSessionOrder("mall_order_"+user.getCustId(),JSON.toJSONString(orderFormDTOMap));
			logger.info("call crm saveOrderSeesion,result code:"+resultDTO.getCode()+",msg:"+resultDTO.getMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		
		logger.info("返回给订单确认页面的数据：" + data);
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, data);
	
	}

	/**
	 * 获取发票须知协议
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/mall0707GetInvoiceProtocol.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object getInvoiceProtocol(HttpServletRequest request, HttpServletResponse response) {
		Map map = new HashMap<>();
		try {
			Document obj = this.documentService.getObjByProperty(null, "mark", "invoice");
			if(null == obj){
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "发票须知协议不存在", null);
			}
			map.put("title", obj.getTitle());
			map.put("content", obj.getContent());
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, map);
	}
	
	/**
	 * 保存为常用发票
	 * @param request
	 * @param response
	 * @param invoice 发票抬头
	 * @param invoiceType 发票类型 0:普通发票 1：增值税发票
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/mall0708SaveInvoice.htm",method={RequestMethod.POST})
	@ResponseBody
	@Token
	public Object saveInvoice(HttpServletRequest request, HttpServletResponse response, 
			String invoice, String invoiceType,User user) {
		try {
			boolean ret = false;
			user.setInvoice(invoice);
			user.setInvoiceType(CommUtil.null2Int(invoiceType));
			ret = this.userService.update(user);
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, null);
	}

	
	/**
	 * 获取自提点
	 * @param request
	 * @param response
	 * @param addr_id 收货地址id
	 * @param deliver_area_id 区域id
	 * @return
	 */
	@RequestMapping(value="/mall0709GetDelivery.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object getDelivery(HttpServletRequest request, HttpServletResponse response, Long addr_id,Long deliver_area_id) {
		Map data = new HashMap();
		try {
			long areaId = -1; //区域id
			if(null != deliver_area_id){
				areaId = deliver_area_id;
			}else{
				//查找收货地址中的城市id
				Area area = addressService.getObjById(addr_id).getArea();//区县id
				areaId = area.getParent().getId(); //城市id
				
				//返回城市区县列表
//				List<Map> list = new ArrayList<>();
//				Map m = new HashMap<>();
//				m.put("id", areaId);
//				m.put("name", area.getParent().getAreaName());
//				list.add(m);
//				for(Area a : area.getChilds()){
//					Map map = new HashMap<>();
//					map.put("id", a.getId());
//					map.put("name", a.getAreaName());
//					list.add(map);
//				}
//				data.put("area", area.getParent());
			}
			
			//返回自提点列表
			String jpql = "SELECT address FROM DeliveryAddress address "
					+ "WHERE (address.da_area.parent.id = :areaId OR address.da_area.id = :areaId) "
					+ "AND address.da_status = :daStatus ";
			Map params = new HashMap<>();
			params.put("areaId", areaId);
			params.put("daStatus", 10);
			List<DeliveryAddress> deliveryAddresses = deliveryaddrService.query(jpql, params, -1, -1);
			List<Map> list = new ArrayList<>();
			for(DeliveryAddress deliveryAddress : deliveryAddresses){
				Map map = new HashMap<>();
				map.put("id", deliveryAddress.getId());
				map.put("address", deliveryAddress.getDa_address());
				map.put("name", deliveryAddress.getDa_name());
				map.put("content", deliveryAddress.getDa_content());
				map.put("tel", deliveryAddress.getDa_tel());
				list.add(map);
			}
			data.put("objs", list);
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, data);
	}

	/**
	 * 查找门店
	 * 
	 * @param request
	 * @param response
	 * @param sa_area_id 城市id
	 */
	@RequestMapping(value="/mall0710GetDelivery.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object load_shipAddress(HttpServletRequest request, HttpServletResponse response, String sa_area_id) {
		Map data = new HashMap<>();
		try {
			Map params = new HashMap();
			params.put("city_area_id", CommUtil.null2Long(sa_area_id));
			params.put("sa_address_type", 1);
			List<ShipAddress> shipAddresses = this.shipAddressService.query(
					"select obj from ShipAddress obj where obj.city_area_id=:city_area_id and obj.sa_address_type=:sa_address_type",
					params, -1, -1);
			List<Map> list = new ArrayList<Map>();
			for (ShipAddress shipAddress : shipAddresses) {
				Map map = new HashMap();
				map.put("id", shipAddress.getId());
				map.put("name", shipAddress.getSa_name());
				list.add(map);
			}
			data.put("shipAddresses", list);
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, data);
	}

	/**
	 * 查询指定门店中各指定商品的库存信息
	 * 
	 * @param request
	 * @param response
	 * @param shipAddress_id 门店id
	 * @param goodsCodes 商品编码集
	 */
	@RequestMapping(value="/mall0711getGoodsInventory.htm",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object getGoodsInventory(HttpServletRequest request, HttpServletResponse response, long shipAddress_id,
			String goodsCodes) {
		int code = Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map data = new HashMap();
		try {
//		Integer selfPickupEnabled = (Integer) request.getSession(true).getAttribute("selfPickupEnabled");

//		if (selfPickupEnabled == null || selfPickupEnabled == 0 || StringUtils.isNullOrEmpty(shipAddress_id)
//				|| StringUtils.isNullOrEmpty(goodsCodes)) {
			if (StringUtils.isNullOrEmpty(shipAddress_id)
					|| StringUtils.isNullOrEmpty(goodsCodes)) {
				code = Constants.RESPONSECODE_REQUEST_ERROR;
				msg = "非法请求!";
			} else {
				// 查询海信商品库存
				Set<String> goodsCodesSet = new HashSet<String>();
				for (String code2 : goodsCodes.split(",")) {
					goodsCodesSet.add(code2);
				}
				
				List<GoodsInventory> goodsInventories = null;
				Long saId = CommUtil.null2Long(shipAddress_id);
				Map params = new HashMap();
				params.put("shipAddress_id", saId);
				params.put("goodsCodes", goodsCodesSet);			
				GoodsTools.hxInventoryRWLock.readLock().lock();			
				try {
					goodsInventories = this.goodsInventoryService.query(
							"select obj from GoodsInventory obj where obj.shipAddress.id=:shipAddress_id and obj.goodsCode in (:goodsCodes) and obj.inventory > 0",
							params, -1, -1);
				} catch (Exception e) {
					logger.error("查询海信商品库存出现异常。", e);
					code = Constants.RESPONSECODE_REQUEST_ERROR;
					msg = "查询海信商品库存出现异常!";
				} finally {
					GoodsTools.hxInventoryRWLock.readLock().unlock();
				}
				
				if(code == Constants.RESPONSECODE_SUCCESS) {
					List<Map> list = new ArrayList<>();
					for (GoodsInventory gi : goodsInventories) {
						Map map = new HashMap<>();
						map.put("goodsCode", gi.getGoodsCode());
						map.put("inventory", gi.getInventory());
						
						list.add(map);
					}
					data.put("goodsInventories", list);
	
					ShipAddress address = shipAddressService.getObjById(saId);
					String addressStr = "地址：" + address.getSa_addr();
					if (!StringUtils.isNullOrEmpty(address.getSa_telephone())) {
						addressStr = addressStr + "。联系方式：" + address.getSa_telephone();
					}
					data.put("address", addressStr); // 门店地址
				}			
			}

		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(code, msg, data);
	}
	
	/**
	 * 提交订单
	 * @param request
	 * @param response
	 * @param user
	 * @param cartParamsVo 
	 * @return
	 */
	@RequestMapping(value="/mall0712SubmitOrder.htm",method={RequestMethod.POST})
	@ResponseBody
	@Token
	public Object submitOrder(HttpServletRequest request, HttpServletResponse response, User user,HttpEntity<CartParamsVo> cartParamsVo) {
		Map data = new HashMap();

		SysConfig config = configService.getSysConfig();
		String webPath = getWebPath(request, config);
		OrderForm main_order = null;
		try {
			CartParamsVo cartParams= cartParamsVo.getBody();
			String cart_session = cartParams.getCartSession();
			Long addr_id = CommUtil.null2Long(cartParams.getAddrId());
			String delivery_time = cartParams.getDeliveryTime();
			int delivery_type = CommUtil.null2Int(cartParams.getDeliveryType());
			Long delivery_id = CommUtil.null2Long(cartParams.getDeliveryId());
			String payType = cartParams.getPayType();
			Long xmStoreId = CommUtil.null2Long(cartParams.getXmStoreId());
			List<SubmitStoreInfoVo> transportAndCoupon = cartParams.getStoreInfoVos();
            Long global_coupon_id=CommUtil.null2Long(cartParams.getGlobal_coupon_id());//全场优惠券ID 
			String redisKey="mall_order_"+user.getCustId();
			String orderFormDTOStr = userManageConnector.getSessionOrder(redisKey);
			logger.info("submitOrder redisKey:"+redisKey+" value:"+orderFormDTOStr);
			Map orderFormDTOMap = Json.fromJson(Map.class, orderFormDTOStr);
			String cart_session1 =(String)orderFormDTOMap.get("cartSession");
			if (!CommUtil.null2String(cart_session1).equals(cart_session)) {
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "订单已经失效", null);
			}
			
			String seckill_info = null, seckill_goods = null;
			List<GoodsCart> order_carts = new ArrayList<GoodsCart>();// 用户选择要购买的商品的购物车
			String[] gcIds = ((String)orderFormDTOMap.get("gcs")).split(",");// 直接从上一步中取得用户已选择的购物车
			
			for (String gcId : gcIds) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gcId));
				if(null == gc){//防止二次提交订单
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "订单已经失效", null);
				}
				// 支付方式
				if (gc.getGoods().getGoods_cod() == -1) { // 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
					payType = "online"; // 订单不支持货到付款，用户从页面前端恶意篡改支付方式为货到付款
				}

				if (gc.getGoods().getSeckill_buy() != 0) { // 秒杀订单
					if (gc.getGoods().getSeckill_buy() != 2) { // 秒杀已停止
						return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "秒杀失败，活动已结束", null);
					} else {
						if (seckill_info != null) {// 一次下单只允许最多提交一件秒杀商品
							return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "不能同时购买两种秒杀商品，请移除其中一件秒杀商品！您当前购物车中的秒杀商品有：" + seckill_goods + ", "
									+ gc.getGoods().getGoods_name(), null);
						}
						String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
						Map params = new HashMap();
						params.put("goods_id", gc.getGoods().getId());
						params.put("gg_status", 2);
						SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
						boolean isOK = this.orderFormService.validateSeckillOrderForm(user.getId(),seckillGoods.getId());
						if (!isOK) { // 同一用户ID只能购买一次同一个秒杀商品
							return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "您已购买过秒杀商品“" + gc.getGoods().getGoods_name() + "”不能重复购买，抱歉！", null);
						}
						seckill_info = "{good_id:" + gc.getGoods().getId() + ",seckill_goods_id:" + seckillGoods.getId()
								+ "}";
						seckill_goods = gc.getGoods().getGoods_name();
					}
				}
				order_carts.add(gc);
			}

			if (seckill_info != null && order_carts.size() > 1) {
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "订单中存在秒杀商品和非秒杀商品，请分开下单", null);
			}else if (seckill_info != null && order_carts.get(0).getCount() != 1) {
				return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "秒杀商品数量必须为1", null);
			}

			Address addr = null;// 用户选择的收货地址。isSelfPickup为false且addr为null，则表示用户选择的是代收点
			DeliveryAddress deliveryAddr = null;// 自提点地址
			boolean isSelfPickup = false; // 指示用户是否选择了“到店自提”
			ShipAddress xmStore = null; // 发货的星美门店。到店自提时使用
			if (2 == delivery_type) {// 用户选择的发货方式为到店自提
				if (CommUtil.null2Int(orderFormDTOMap.get("selfPickupEnabled")) == 0) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "请勿篡改前台请求数据", null);
				}
				xmStore = this.shipAddressService.getObjById(xmStoreId);
				if (xmStore == null) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "您选择了到店自提，请您选择要上门提货的门店。", null);
				}
				// 校验库存是否满足(暂不执行：只是在前台页面用户选择星美门店时友情提示库存不足，让用户与门店联系。)
				isSelfPickup = true;
			} else {// 快递。即使选择自提点服务，也要让用户选择收货地址，因为要用户的联系方式，以及用于计算运费
				addr = this.addressService.getObjById(addr_id);
				if (addr == null || !addr.getUser().getId().equals(user.getId())) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "收货地址错误", null);
				}

				int needId = CommUtil.null2Int(orderFormDTOMap.get("needId"));
				if (needId == 1 && StringUtils.isNullOrEmpty(addr.getCard())) {
					return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "您的购物车中有些商品要求提供身份证，请在收货地址中填写正确的身份证。", null);
				}

				if (delivery_type == 1) {// 自提点服务
					deliveryAddr = this.deliveryaddrService.getObjById(delivery_id);
					if (deliveryAddr == null) {
						return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "您选择的自提点在系统中未找到。", null);
					}
				}
			}

			if ("payafter".equals(payType)) { // 使用货到付款
				String pay_session = CommUtil.randomString(32);
				data.put("pay_session", pay_session);
			}

			Date date = new Date();
			data.put("availableBalance", user.getAvailableBalance());

			List<Goods> gift_goods = new ArrayList<Goods>();// 当前用户的所有赠品
			String[] gift_ids = CommUtil.null2String(orderFormDTOMap.get("validGiftIds")).split(",");
			for (String gid : gift_ids) {
				if (!"".equals(gid)) {
					Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
					if (goods != null) {
						gift_goods.add(goods);// 赠品已在goods_cart2中进行了校验，故此处不再进行校验
					}
				}
			}

			Map<Object, Map<Long, List<Long>>> giftCartMapping =(Map<Object, Map<Long, List<Long>>>) orderFormDTOMap.get("giftCartMapping");
			Map<Object, Map> hxGoods = (Map<Object, Map>)orderFormDTOMap.get("hXGoods");// 可能为null
			Map<Object, Map> transports = (Map<Object, Map>)orderFormDTOMap.get("storeTransportsMapping");// 可能为null
			String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			List<Object> storeIds = (List<Object>)orderFormDTOMap.get("storeList");// 可能是Long类型的店铺id，也可能是String类型的"self"

			List<Map<String, Object>> allForms = new ArrayList<Map<String, Object>>();
			double all_of_price = 0;// 所有订单的最终费用总和

			
			
			List<GoodsCart> all_normal_carts=new ArrayList<GoodsCart>();//计算出所有有效的购物车
			for (Object sid : storeIds){
				List<Long> cartIds = ((Map<Object, List<Long>>)orderFormDTOMap.get("storeCartMapping")).get(sid.toString());
				for (GoodsCart gc : order_carts) {
					if (cartIds.contains(gc.getId().intValue())) {
						all_normal_carts.add(gc);
					}
				}
			}
			
			//校验用户全场优惠券
			Map<String,List<Map>> userGlobalCouponMap=cartTools.query_global_couponinfos(user.getCustId(), all_normal_carts);
			CouponInfo global_couponinfo=null;//全场优惠券信息
			if(userGlobalCouponMap!=null && userGlobalCouponMap.get("validCouponinfos")!=null && userGlobalCouponMap.get("validCouponinfos").size()>0){
				List<Map> valid_global_couponinfo=userGlobalCouponMap.get("validCouponinfos");
				for(Map couponinfo : valid_global_couponinfo)
					if(global_coupon_id.equals(couponinfo.get("id"))){
						global_couponinfo=couponInfoService.getObjById(global_coupon_id);
						break;
					}
			}
			
			Double global_coupon_goods_total_price=0.00;//购物车中全场优惠券所关联的类型总金额
			List<String> global_coupon_associated_ids=new ArrayList<String>();//全场优惠券关联类型ID集合
			if(global_couponinfo!=null){
				global_coupon_associated_ids=CommUtil.string2List(global_couponinfo.getCoupon().getAssociated_ids(),",");
				global_coupon_goods_total_price=cartTools.calcGlobalCouponGoodsTotalPrice(all_normal_carts, global_couponinfo.getCoupon().getAssociated_type(), global_coupon_associated_ids);
			}
			
			for (Object sid : storeIds) {
				Store store = null;
				if (!"self".equals(sid.toString())) {
					store = this.storeService.getObjById(CommUtil.null2Long(sid));
				}

				OrderForm of = new OrderForm();
				of.setTotalPrice(new BigDecimal(0.0));
				List<GoodsCart> gc_list = new ArrayList<GoodsCart>(); // 当前店铺的购物车
				List<Map> hxGoodsDispached = new ArrayList<Map>();// 当前店铺中的海信商品选择发货门店的情况
				if (hxGoods != null && hxGoods.containsKey(sid)) {
					hxGoodsDispached = (List<Map>) ((Map) hxGoods.get(sid)).get("goodsDispached");
					for(Map m : hxGoodsDispached){
						long addressId = CommUtil.null2Long(m.get("address"));
						m.put("address", shipAddressService.getObjById(addressId));
						
						List<Long> gcIdsTmp = new ArrayList<>();
						for(Integer gcId : (List<Integer>)m.get("goodscart_list")){
							gcIdsTmp.add(CommUtil.null2Long(gcId));
						}
						m.put("goodscart_list", gcIdsTmp);
					}
					
					List<Long> freeShipFeeGoodsCartIds_o2os = new ArrayList<>();
					for(Integer gcId : (List<Integer>)((Map) hxGoods.get(sid)).get("freeShipFeeGoodsCartIds_o2o")){
						freeShipFeeGoodsCartIds_o2os.add(CommUtil.null2Long(gcId));
					}
					((Map) hxGoods.get(sid)).put("freeShipFeeGoodsCartIds_o2o", freeShipFeeGoodsCartIds_o2os);
				}

				// 购物车信息
				List<Long> cartIds = ((Map<Object, List<Long>>)orderFormDTOMap.get("storeCartMapping")).get(sid.toString());
				for (GoodsCart gc : order_carts) {
					if (cartIds.contains(gc.getId().intValue())) {
						gc_list.add(gc);
					}
				}

				// 赠品信息，放在同一店铺的第一个订单中
				if (giftCartMapping.containsKey(sid)) {
					List<Map> gift_map = new ArrayList<Map>();

					Map<Long, List<Long>> storeGiftCartMapping = giftCartMapping.get(sid);
					for (Goods gift : gift_goods) {
						if (storeGiftCartMapping.containsKey(gift.getId())) {// 当前店铺的赠品
							List<Long> cs = storeGiftCartMapping.get(gift.getId());
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
				long coupon_id = 0;
				for(SubmitStoreInfoVo submitStoreInfoVo : transportAndCoupon){
					if(sid.toString().equals(submitStoreInfoVo.getStoreId())){
						coupon_id = CommUtil.null2Long(submitStoreInfoVo.getCouponId()) ;
						break;
					}
				}
				
				//全场优惠券中其中店铺占得百分比
				Double global_coupon_store_rate=0.00;
				
				//判断这个店铺是否可以全场优惠券
				if(global_couponinfo!=null && global_coupon_goods_total_price>0){
					Double store_coupon_goods_total_price=cartTools.calcGlobalCouponGoodsTotalPrice(gc_list, global_couponinfo.getCoupon().getAssociated_type(), global_coupon_associated_ids);
					if(store_coupon_goods_total_price>0 && global_coupon_goods_total_price>= store_coupon_goods_total_price)
						global_coupon_store_rate=CommUtil.div(store_coupon_goods_total_price,global_coupon_goods_total_price);
				}
				
				CouponInfo ci =null;
				//如果能够使用全场优惠券，优先使用它
				if(global_coupon_store_rate>0)
					ci=global_couponinfo;
				else
					ci=this.couponInfoService.getObjById(coupon_id);
				
				if (ci != null && seckill_info == null) { // 秒杀订单不允许使用优惠券
					
					if (ci.getStatus() == 0 && user.getId().equals(ci.getUser().getId())) {
						// 检查优惠券是否满足使用条件
						Coupon coupon = ci.getCoupon();
						boolean flag = !date.before(coupon.getCoupon_begin_time())
								&& !date.after(coupon.getCoupon_end_time());
						
						//这里的coupon是指全场优惠券分担的金额，不做店铺验证(这里的优惠券是全场优惠券)
						if(flag && coupon.getCoupon_type()!=2){
							flag = flag&& (coupon.getCoupon_type() == 0 ? "self".equals(sid.toString()) : coupon.getStore().getId().intValue()==(Integer)sid);
							double goodsAmountAfterReduced = Double.parseDouble(ermap.get("after").toString());
							flag = flag && goodsAmountAfterReduced >= coupon.getCoupon_order_amount().doubleValue();//使用扣除满减后的商品总金额来判断是否可以使用优惠券
						}
						if (flag) {
							//如果是全场优惠券，那么说明这个是几分之几的优惠券，不属于完整的优惠券（global_coupon_store_rate:是说本店使用全场优惠券所占的比重）
							if(global_coupon_store_rate<=0){
								ci.setStatus(1);
								this.couponInfoService.update(ci);
							}
							
							Map coupon_map = new HashMap();
							coupon_map.put("couponinfo_id", ci.getId());
							coupon_map.put("couponinfo_sn", ci.getCoupon_sn());
							
							//判断是普通优惠券 还是 全场优惠券，如果是全场优惠券按照店铺的分类商品所占的比例来计算优惠金额
							BigDecimal coupon_amount=global_coupon_store_rate>0 ? new BigDecimal(CommUtil.mul(coupon.getCoupon_amount(),global_coupon_store_rate)) :coupon.getCoupon_amount();
							coupon_map.put("coupon_amount", coupon_amount.doubleValue());
								
							coupon_map.put("coupon_order_amount", coupon.getCoupon_order_amount()); // 优惠券限制金额
							coupon_map.put("coupon_return_amount", 0); // 优惠券已退金额
							double rate = CommUtil.div(coupon_amount, all_goods);
							coupon_map.put("coupon_goods_rate", rate);
							of.setCoupon_info(Json.toJson(coupon_map, JsonFormat.compact()));
							of.setTotalPrice(coupon_amount.negate().add(of.getTotalPrice())); // 优惠券可减免的金额，设为负数。
						}
					}
				}

				// 秒杀信息，放在同一店铺的第一个订单中
				of.setSeckill_info(seckill_info);

				of.setAddTime(date);
				String orderId = order_suffix + user.getId() + ("self".equals(sid.toString()) ? "0" : sid.toString()) + CommUtil.randomInt(2);
				of.setOrder_id(orderId);
				of.setPayType(payType);
				of.setOrder_status(10);
				of.setUser_id(user.getId().toString());
				of.setUser_name(user.getUserName());
				of.setInvoiceType(CommUtil.null2Int(cartParams.getInvoiceType()));//发票类型
				of.setInvoice(CommUtil.convertScriptTag(cartParams.getInvoice()));//发票抬头
				for(SubmitStoreInfoVo submitStoreInfoVo : transportAndCoupon){
					if(sid.toString().equals(submitStoreInfoVo.getStoreId())){
						of.setMsg(CommUtil.convertScriptTag(submitStoreInfoVo.getRemark()));
						break;
					}
				}

				of.setOrder_type("app2.0"); // 设置为星美生活app2.0订单

				of.setDelivery_time(CommUtil.convertScriptTag(delivery_time));

				if ("self".equals(sid.toString())) {
					of.setOrder_form(1); // 平台自营商品订单
				} else {
					of.setOrder_form(0); // 商家商品订单
					of.setStore_id(store.getId().toString());
					of.setStore_name(store.getStore_name());
				}

				if (isSelfPickup) {// 到店自提
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
					of.setReceiver_Name(user.getUserName());
					String receiverMobile = user.getUserName();
					String receiverTel = user.getUserName();
					if (!StringUtils.isNullOrEmpty(user.getMobile())) {
						receiverMobile = user.getMobile();
					}

					if (!StringUtils.isNullOrEmpty(user.getTelephone())) {
						receiverTel = user.getTelephone();
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
					Map smsAndAddr = transports.get(sid.toString());
					Long receiverAddrId = CommUtil.null2Long(smsAndAddr.get("receiverAddrId")) ;
					if (receiverAddrId == null || !receiverAddrId.equals(addr.getId())) {
						return CommUtils.buidResult(Constants.RESPONSECODE_REQUEST_ERROR, "请设置一个收货地址！", null);
					}
					List<Map> sms = (List<Map>) smsAndAddr.get("transports");
					String transport = null;
					for(SubmitStoreInfoVo submitStoreInfoVo : transportAndCoupon){
					    // TODO 这个地方存在取不到值,变量transport为null从而导致空指针异常
						if(sid.toString().equals(submitStoreInfoVo.getStoreId())){
							transport = submitStoreInfoVo.getTransport();
							break;
						}
					}
					float transFee = -1;
					for (Map sm : sms) {
						String keyStr = sm.get("key").toString();
                        // TODO 因transport值为null从而抛出空指针异常
						if (keyStr.indexOf(transport) >= 0) {
							transport = keyStr;
							transFee = CommUtil.null2Float(sm.get("value")) ;
							break;
						}
					}
					if (transFee == -1) {// 用户上传的数据有问题，则为其自动选择一个物流方式
						transport = sms.get(0).get("key").toString();
						transFee = CommUtil.null2Float(sms.get(0).get("value"));
					}
					
					//begin dengyuqi 2016-3-4  保存运费信息
					Map<String,List<OrderFreightInfo>> freightInfoMaps = (Map<String,List<OrderFreightInfo>>) smsAndAddr.get("freightInfos");
					if(null != freightInfoMaps){
						for (Entry entry : freightInfoMaps.entrySet()) {
							String keyStr = entry.getKey().toString();
							if ((transport+"_freightInfos").equals(keyStr)) {
								List<OrderFreightInfo> freightInfos = (List<OrderFreightInfo>) entry.getValue();
								of.setFreight_info(Json.toJson(freightInfos));
								break;
							}
						}
					}
					//end
					
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
								+ deliveryAddr.getDa_area().getParent().getAreaName()
								+ deliveryAddr.getDa_area().getAreaName() + deliveryAddr.getDa_address());
						params.put("da_service_day",
								this.deliveryAddressTools.query_service_day(deliveryAddr.getDa_service_day()));
						params.put("da_service_time", service_time);
						of.setDelivery_address_id(deliveryAddr.getId());
						of.setDelivery_info(Json.toJson(params, JsonFormat.compact()));
					}
				}

				// 拆解当前店铺的订单
				List<Map<String, Object>> storeAllOfs = this.orderFormService.refineOrderForm(request, sid, store, user, of, gc_list,
						isSelfPickup, hxGoodsDispached, addr, of.getEnough_reduce_info(),of.getCoupon_info());
				allForms.addAll(storeAllOfs);
			}

			main_order = (OrderForm) allForms.get(allForms.size() - 1).get("of");// 最后一个订单作为主订单
			main_order.setOrder_main(1);
			
			for (int i = 0; i < allForms.size(); i++) {
				OrderForm of = (OrderForm) allForms.get(i).get("of");
				all_of_price = CommUtil.add(all_of_price, of.getTotalPrice());
				of.setMainOrderId(main_order.getOrder_id());
			}
			
			//更新全场优惠券使用状态
			if(global_couponinfo!=null && global_coupon_goods_total_price>0){
				//确认全场优惠券已经使用了
				global_couponinfo.setStatus(1);
				couponInfoService.update(global_couponinfo);
			}

			this.orderFormService.saveOrderForm(allForms);

			data.put("main_order_id", main_order.getOrder_id());
			data.put("all_of_price", CommUtil.formatMoney(all_of_price));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		
		try {
			Boolean install = (Boolean) paymentTools.queryShopPayment("balance").get("install");
			data.put("install", install);
			List<Payment> payments=paymentTools.queryByTerminalMark("wap");
			if(payments!=null && !payments.isEmpty()){
				List<Map> list = new ArrayList<>();
				for(Payment payment : payments){
					Map map = new HashMap<>();
					map.put("Mark", payment.getMark());
					map.put("Icon_url", webPath+"/resources/style/common/images/payment/"+payment.getIcon_url());
					list.add(map);
				}
				data.put("payments", list);
			}
			Map params = new HashMap();
			params.put("mark", "wx_pay");
			payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
			if (payments.size() > 0) {
				Payment payment = payments.get(0);
				data.put("appid", payment.getWx_appid());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, "订单已成功生成，支付方式获取失败，可前往订单中心进行支付", null);
		}
		
		try {
			// 在循环外，给买家只发送一次短信邮件
			if (main_order.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
						user.getEmail(), null, CommUtil.null2String(main_order.getId()), main_order.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
						user.getMobile(), null, CommUtil.null2String(main_order.getId()), main_order.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
						user.getEmail(), null, CommUtil.null2String(main_order.getId()));
				this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
						user.getMobile(), null, CommUtil.null2String(main_order.getId()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return CommUtils.buidResult(Constants.RESPONSECODE_SUCCESS, Constants.RESPONSECODE_SUCCESS_DESC, data);
	}

	/**
	 * 获取购物车中的商品数量
	 * @author herendian
	 * @version 1.0
	 * @date 2016年4月5日 下午5:43:45
	 * @param request
	 * @param response
	 * @param user  
	 * @param sessionId
	 * @return
	 */
	@RequestMapping(value="/mall0713GetCartCount.htm",method={RequestMethod.POST})
	@ResponseBody
	@Token(userNullable = true)
	public Object getCartCount(HttpServletRequest request, HttpServletResponse response,User user,String sessionId) {
		int code =Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map<String,Object> resultData = new HashMap<String,Object>();
		if (null==user) {
			user=new User();
		}
		try {
		int count=	goodsCartService.getCartCount(user.getCustId(), sessionId);
			resultData.put("value", count);
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(code, msg, resultData);
	}
	
	
	
	private String generic_day(int day) {
		String[] list = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return list[day - 1];
	}

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
	
	/**
	 * 将订单表中的商品信息保存到订单商品统计表中，只方法执行一次，处理历史数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/test.htm",method={RequestMethod.GET})
	@ResponseBody
	public Object getCartGoods(HttpServletRequest request, HttpServletResponse response) {
		//查询订单列表
		OrderFormQueryObject queryObject = new OrderFormQueryObject();
		queryObject.setConstruct("order_id,goods_info");
		queryObject.setPageSize(Integer.MAX_VALUE);
		List<Object[]> orderForms = orderFormService.list(queryObject).getResult();
		logger.info("result size:"+orderForms.size());
		List<OrderGoodsStatistics> orderGoodsStatisticses = new ArrayList<>();
		OrderGoodsStatistics orderGoodsStatistics = null;
		Date date = new Date();
		Goods goods = null;
		GoodsClass goodsClass = null;
		//遍历订单列表，解析商品信息
		for(Object[] fields : orderForms){
			List<Map> maps = CommUtil.json2List((String)fields[1]);
			for(Map map : maps){
				goods = goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
				//因部分商品已删除，故统计结果将产生遗漏
				if(null != goods){
					goodsClass = goods.getGc();
					if(null != goodsClass){
						//生成订单商品统计信息
						orderGoodsStatistics = new OrderGoodsStatistics();
						orderGoodsStatistics.setAddTime(date);
						orderGoodsStatistics.setGoodsId(goods.getId());
						orderGoodsStatistics.setGoodsPrice(new BigDecimal((Double)map.get("goods_price")));
						orderGoodsStatistics.setOrderId((String)fields[0]);
						if(Globals.GOODS_CLASS_LEVEL_THRID == goodsClass.getLevel()){
							orderGoodsStatistics.setThridGcId(goodsClass.getId());
							orderGoodsStatistics.setSecondGcId(goodsClass.getParent().getId());
							orderGoodsStatistics.setFirstGcId(goodsClass.getParent().getParent().getId());
						}else if(Globals.GOODS_CLASS_LEVEL_SECOND == goodsClass.getLevel()){
							orderGoodsStatistics.setSecondGcId(goodsClass.getId());
							orderGoodsStatistics.setFirstGcId(goodsClass.getParent().getId());
						}else if(Globals.GOODS_CLASS_LEVEL_FIRST == goodsClass.getLevel()){
							orderGoodsStatistics.setFirstGcId(goodsClass.getId());
						}
						orderGoodsStatisticses.add(orderGoodsStatistics);
					}
				}
			}
		}
		logger.info("orderGoodsStatisticses size:"+orderGoodsStatisticses.size());
		orderGoodsStatisticsService.batchSave(orderGoodsStatisticses,3000);
		
		return null;
	}
}
