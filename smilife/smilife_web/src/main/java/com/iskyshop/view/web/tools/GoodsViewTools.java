package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

@Component
public class GoodsViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private IGoodsCartService goodscartService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;

	/**
	 * 将商品属性归类,便于前台显示
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsSpecProperty> getGoodsSpaec(String id) {
		List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
		if (!StringUtils.isNullOrEmpty(id)) {
			Goods goods = this.goodsService.getObjById(Long.parseLong(id));
			specs = goods.getGoods_specs();
		}
		return specs;
	}

	/**
	 * 返回商品的规格属性对应的的所有规格（按sequence进行了升序排序），若商品不存在或不是规格库存，则返回一个size()==0的列表
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<GoodsSpecification> generic_spec(String goodsId) {
		List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
		Set<Long> specIds = new HashSet<Long>();
		if (!StringUtils.isNullOrEmpty(goodsId)) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
			if ("spec".equals(goods.getInventory_type())) {
				for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
					GoodsSpecification spec = gsp.getSpec();
					if(!specIds.contains(spec.getId())) {
						specs.add(spec);
						specIds.add(spec.getId());
					}					
				}
				java.util.Collections.sort(specs, new Comparator<GoodsSpecification>() {
					@Override
					public int compare(GoodsSpecification gs1, GoodsSpecification gs2) {
						return gs1.getSequence() - gs2.getSequence();
					}
				});
			}
		}
		return specs;
	}

	/**
	 * 查询用户商品分类信息
	 * 
	 * @param pid
	 * @return
	 */
	public List<UserGoodsClass> query_user_class(String pid) {
		List<UserGoodsClass> list = new ArrayList<UserGoodsClass>();
		if (StringUtils.isNullOrEmpty(pid)) {
			Map map = new HashMap();
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService.query(
					"select obj from UserGoodsClass obj where obj.parent.id is null and obj.user_id = :uid order by obj.sequence asc",
					map, -1, -1);
		} else {
			Map params = new HashMap();
			params.put("pid", Long.parseLong(pid));
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService.query(
					"select obj from UserGoodsClass obj where obj.parent.id=:pid and obj.user_id = :uid order by obj.sequence asc",
					params, -1, -1);
		}
		return list;
	}

	/**
	 * 根据商城分类查询对应的商品
	 * 
	 * @param gc_id
	 *            商城分类id
	 * @param count
	 *            需要查询的数量
	 * @return
	 */
	public List<Goods> query_with_gc(String gc_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
		if (gc != null) {
			Set<Long> ids = this.gcViewTools.genericIds(gc.getId(), false, false);
			ids.add(gc.getId());
			Map params = new HashMap();
			params.put("ids", ids);
			params.put("goods_status", 0);
			list = this.goodsService.query(
					"select obj from Goods obj where obj.gc.id in (:ids) and obj.goods_status=:goods_status order by obj.goods_click desc",
					params, 0, count);
		}
		return list;
	}

	
	public List<Goods> sort_sale_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService.query(
				"select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
				params, 0, count);
		return list;
	}

	public List<Goods> sort_collect_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService.query(
				"select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
				params, 0, count);
		return list;
	}

	public List<Goods> self_goods_sale(int goods_type, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("goods_type", goods_type);
		params.put("goods_status", 0);
//		list = this.goodsService.query(
//				"select new Goods(id, goods_name, goods_current_price,goods_collect, goods_salenum,goods_main_photo) from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",
//				params, 0, count);
		//防止外层调用对返回值进行update操作，故查询所有字段
		list = this.goodsService.query(
				"select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",
				params, 0, count);
		return list;
	}

	public List<Goods> self_goods_collect(int goods_type, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("goods_type", goods_type);
		params.put("goods_status", 0);
//		list = this.goodsService.query(
//				"select new Goods(id, goods_name, goods_current_price,goods_collect, goods_salenum,goods_main_photo) from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_collect desc",
//				params, 0, count);
		//防止外层调用对返回值进行update操作，故查询所有字段
		list = this.goodsService.query(
				"select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_collect desc",
				params, 0, count);
		
		return list;
	}

	/**
	 * 直通车商品查询，查询当天的直通车商品，如果系统没有开启直通车，则查询系统推荐商品， size：需要查询的商品数量
	 */
	public List<Goods> query_Ztc_Goods(int size) {
		List<Goods> ztc_goods = new ArrayList<Goods>();
		if (this.configService.getSysConfig().isZtc_status()) {
			ztc_goods = this.randomZtcGoods(CommUtil.null2Int(size));
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
//			ztc_goods = this.goodsService.query(
//					"select new Goods( id, goods_name, goods_current_price, goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
//					params, 0, CommUtil.null2Int(size));
			//防止外层调用对返回值进行update操作，故查询所有字段
			ztc_goods = this.goodsService.query(
					"select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
					params, 0, CommUtil.null2Int(size));
			
		}
		return ztc_goods;
	}

	/**
	 * 随机显示当天的直通车商品，显示数量由count控制
	 * 
	 * @param count
	 * @return
	 */
	public List<Goods> randomZtcGoods(int count) {
		Map ztc_map = new HashMap();
		ztc_map.put("ztc_status", 3);
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", 0);
		List<Goods> goods = this.goodsService.query(
				"select obj.id from Goods obj where obj.ztc_status =:ztc_status "
						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
				ztc_map, -1, -1);
		Random random = new Random();
		int random_num = 0;
		int num = 0;
		if (goods.size() - count > 0) {
			num = goods.size() - count;
			random_num = random.nextInt(num);
		}
		ztc_map.clear();
		ztc_map.put("ztc_status", 3);
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", 0);
//		List<Goods> ztc_goods = this.goodsService.query(
//				"select new Goods( id, goods_name, goods_current_price, goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
//						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
//				ztc_map, random_num, count);
		//防止外层调用对返回值进行update操作，故查询所有字段
		List<Goods> ztc_goods = this.goodsService.query(
				"select obj from Goods obj where obj.ztc_status =:ztc_status "
						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
				ztc_map, random_num, count);
		
		
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 从list中随机显示若干个对象，数量由count控制
	 * 
	 * @param goods_list
	 * @param count
	 * @return
	 */
	public List<Goods> randomZtcGoods2(List<Goods> goods_list, int count) {
		List<Goods> ztc_goods = new ArrayList<Goods>();
		Random ran = new Random();
		for (int i = 0; i < count; i++) {
			if (i < goods_list.size()) {
				int ind = ran.nextInt(goods_list.size());
				boolean flag = true;
				for (Goods obj : ztc_goods) {
					if (obj.getId().equals(goods_list.get(ind).getId())) {
						flag = false;
					}
				}
				if (flag) {
					ztc_goods.add(goods_list.get(ind));
				} else {
					i--;
				}
			}
		}
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 根据当前会员的会员等级，显示相应等级的名称
	 */
	public String query_user_level_name(String user_id) {
		String level_name = "";
		if (!StringUtils.isNullOrEmpty(user_id)) {
			level_name = this.integralViewTools.query_user_level_name(user_id);
		}
		return level_name;
	}

	/**
	 * 查询LuceneVo的图片路径
	 */
	public List<String> query_LuceneVo_photos_url(String json) {
		List<String> list = new ArrayList();
		if (!StringUtils.isNullOrEmpty(json)) {
			list = Json.fromJson(ArrayList.class, json);
		}
		return list;
	}

	public Store query_LuceneVo_goods_store(String id) {
		Store store = null;
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (goods != null) {
			store = goods.getGoods_store();
		}
		return store;
	}

	public List<GoodsClass> query_GC_third(String gc_id, Set<String> list_gc) {
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		for (String gc_str : list_gc) {
			if (gc_str.split("_").length > 1) {
				if (gc_str.split("_")[0].equals(gc_id)) {
					gcs.add(this.goodsClassService.getObjById(CommUtil.null2Long(gc_str.split("_")[1])));
				}
			}
		}
		return gcs;
	}

	public BuyGift query_buyGift(String bg_id) {
		BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(bg_id));
		return bg;
	}

	public Goods query_Goods(String goods_id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		return goods;
	}

	/**
	 * 解析商品中的组合方案
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Map> getCombinPlans(String goods_id, String type) {
		List<Map> map_temps = new ArrayList<Map>();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		if (obj.getCombin_status() == 1) {
			if (obj.getCombin_suit_id() != null || obj.getCombin_parts_id() != null) {
				CombinPlan com = null;
				if ("suit".equals(type) && obj.getCombin_suit_id() != null) {
					com = this.combinplanService.getObjById(obj.getCombin_suit_id());
				} else if ("parts".equals(type) && obj.getCombin_parts_id() != null) {
					com = this.combinplanService.getObjById(obj.getCombin_parts_id());
				}
				if (com != null) {
					boolean verify_date = false;
					Date now = new Date();
					Date s = com.getBeginTime();
					if (com.getBeginTime().before(now) && com.getEndTime().after(now)) {
						verify_date = true;
					}
					if (verify_date && com.getCombin_status() == 1) { // 已审核
						map_temps = (List<Map>) Json.fromJson(com.getCombin_plan_info());
					}
				}
			}
		}
		return map_temps;
	}
	
	/**
	 * 传入Id判断该商品是否为组合套餐中的子商品 不是返回true 是返回false
	 * 
	 * @param goods_id
	 * @return boolean
	 */
	public boolean getSuitInfotr(String goods_id){
		boolean bool = true;
		Map params = new HashMap();
		params.put("combin_type", 0);
		List<CombinPlan> list_combin = this.combinplanService.query("select obj from CombinPlan obj where obj.combin_type=:combin_type", params, -1, -1);
		for (CombinPlan combinPlan : list_combin) {
			JSONArray jarray = JSON.parseArray(combinPlan.getCombin_plan_info());
			for(int i=0;i<jarray.size();i++){
				JSONObject jobj = jarray.getJSONObject(i);
				String goodslist = jobj.getString("goods_list");
				JSONArray jarray1 = JSON.parseArray(goodslist);
				for(int j=0;j<jarray1.size();j++){
					JSONObject jobj1 = jarray1.getJSONObject(j);
					String id = jobj1.getString("id");
					if(goods_id.equals(id)){
						bool = false; 
					}
				}
				
			}
		}
		return bool;
	}

	/**
	 * 解析商品中的组合方案
	 * 
	 * @param goods_id
	 * @return
	 */
	public Map getSuitInfo(String cart_id) {
		Map map = null;
		GoodsCart gc = this.goodscartService.getObjById(CommUtil.null2Long(cart_id));
		if (gc != null && gc.getCombin_suit_info() != null) {
			map = (Map) Json.fromJson(gc.getCombin_suit_info());
		}
		return map;
	}

	/**
	 * 解析方案中商品信息,返回List<Map>
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinPlanGoods(Map map) {
		List<Map> map_temps = new ArrayList<Map>();
		map_temps = (List<Map>) map.get("goods_list");
		return map_temps;
	}

	/**
	 * 解析方案中商品id,返回id，以逗号间隔
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getCombinPlanGoodsIds(Map map) {
		String ids = "";
		List<Map> map_temps = (List<Map>) map.get("goods_list");
		for (Map map2 : map_temps) {
			ids = ids + "," + CommUtil.null2String(map2.get("id"));
		}
		return ids;
	}

	/**
	 * 解析套装主购物车中的所有套装商品(不包括主体商品的信息)(这些商品的信息都是商品当前的最新信息，而不是套装中保存的商品的信息)
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getsuitGoods(String web_url, String id) {
		List<Map> map_list = new ArrayList<Map>();
		GoodsCart cart = this.goodscartService.getObjById(CommUtil.null2Long(id));
		if (cart != null) {
			if (cart.getCart_type() != null && "combin".equals(cart.getCart_type()) && cart.getCombin_main() == 1) {
				String cart_ids[] = cart.getCombin_suit_ids().split(",");
				for (String cart_id : cart_ids) {
					if (!"".equals(cart_id) && !cart_id.equals(id)) {
						GoodsCart other = this.goodscartService.getObjById(CommUtil.null2Long(cart_id));
						if (other != null) {
							Map temp_map = new HashMap();
							temp_map.put("id", other.getId());
							temp_map.put("name", other.getGoods().getGoods_name());
							temp_map.put("goodName", other.getGoods().getGoods_name());
							temp_map.put("price", other.getGoods().getGoods_current_price());
							temp_map.put("goodPrice", other.getGoods().getGoods_current_price());
							temp_map.put("count", other.getCount());
							temp_map.put("all_price", other.getPrice());
							temp_map.put("spec_info", other.getSpec_info());
							temp_map.put("specInfo", other.getSpec_info());
							String goods_url = web_url + "/goods_" + other.getGoods().getId() + ".htm";
							temp_map.put("url", goods_url);
							temp_map.put("goodDetailUrl", goods_url);
							if(!StringUtils.isNullOrEmpty(web_url) && !web_url.endsWith("/") ){
								web_url += "/";
							}
							String img2 = web_url +  this.configService.getSysConfig().getGoodsImage().getPath() + "/"
									+ this.configService.getSysConfig().getGoodsImage().getName();
							if (other.getGoods().getGoods_main_photo() != null) {
								img2 = web_url  + other.getGoods().getGoods_main_photo().getPath() + "/"
										+ other.getGoods().getGoods_main_photo().getName() + "_small."
										+ other.getGoods().getGoods_main_photo().getExt();
							}
							temp_map.put("img", img2); // 商品图片
							temp_map.put("goodPicUrl", img2); // 商品图片
							temp_map.put("totalPrice", CommUtil.mul(other.getPrice(), other.getCount())); // 小计
							map_list.add(temp_map);
						}
					}
				}
			}
		}
		return map_list;
	}

	/**
	 * 解析套装主购物车中的套装名称
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getsuitName(String suit_info) {
		String suit_name = "";
		if (suit_info != null && !"".equals(suit_info)) {
			Map map = Json.fromJson(Map.class, suit_info);
			suit_name = CommUtil.null2String(map.get("suit_name"));
		}
		return suit_name;
	}

	/**
	 * 查询商品正在进行的优惠活动，(luence搜索页面中引用)
	 * 
	 * @param id
	 * @return
	 */
	public String query_activity_status(String id, String mark) {
		String str = "false";
		Goods goods = null;
		Map params = new HashMap();
		params.put("id", CommUtil.null2Long(id));
		List objs = this.goodsService.query("select obj." + mark + " from Goods obj where obj.id=:id", params, 0, 1);
		if (objs.size() > 0) {
			String status = CommUtil.null2String(objs.get(0));
			if ("group_buy".equals(mark) || "activity_status".equals(mark)) {
				if ("2".equals(status)) {
					str = "true";
				}
			} else if ("combin_status".equals(mark) || "order_enough_give_status".equals(mark)
					|| "enough_reduce".equals(mark) || "f_sale_type".equals(mark) || "advance_sale_type".equals(mark)) {
				if ("1".equals(status)) {
					str = "true";
				}
			}else if("seckill_buy".equals(mark)){
				if("2".equals(status) || "4".equals(status)){
					str = "true";
				}
			}
		}
		return str;
	}

	/**
	 * 商家中心商品列表查询商品活动状态
	 * 
	 * @param id
	 * @param webUrl
	 * @return
	 */
	public String query_goods_single_preferential(String id, String webUrl) {
		String str = "";
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Date nowDate = new Date();
		if (goods.getGroup_buy() == 2) {
			if (goods.getGroup().getBeginTime().before(nowDate)) {
				str = "/resources/style/system/front/default/images/tuan.png";
			}
		}
		if (goods.getActivity_status() == 2) {
			ActivityGoods acg = this.activityGoodsService.getObjById(CommUtil.null2Long(goods.getActivity_goods_id()));
			if (acg != null && acg.getAg_status() == 1) {
				str = "/resources/style/system/front/default/images/c.png";
			}
		}
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.getObjById(goods.getBuyGift_id());
			if (bg != null && bg.getBeginTime().before(nowDate)) {
				str = "/resources/style/system/front/default/images/s.png";
			}
		}
		if (goods.getEnough_reduce() == 1) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(goods.getOrder_enough_reduce_id()));
			if (er.getErbegin_time().before(nowDate)) {
				str = "/resources/style/system/front/default/images/j.png";
			}
		}
		if (goods.getCombin_status() == 1) {
			str = "/resources/style/system/front/default/images/zu.png";
		}
		if (goods.getF_sale_type() == 1) {
			str = "/resources/style/system/front/default/images/f.png";
		}
		if (goods.getAdvance_sale_type() == 1) {
			str = "/resources/style/system/front/default/images/y.png";
		}
		if (!"".equals(str)) {
			str = webUrl + "/" + str;
		}
		return str;
	}
	
	/**
	 * 查询预售商品发货时间在wap端显示
	 * 
	 * @param id
	 * @return Date
	 */
	public Date query_goods_date(String goods_id){
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		return goods.getAdvance_date();
	}
	

	/**
	 * F码商品排除，F码商品已在页面判断
	 * 
	 * @param id
	 * @return
	 */
	public Map query_goods_preferential(long id) {
		Goods goods = this.goodsService.getObjById(id);
		String str = "";
		String info = "";
		Date nowDate = new Date();
		if (goods.getGroup_buy() == 2) {
			if (goods.getGroup().getBeginTime().before(nowDate)) {
				str = "团购";
			}
		}
		if (goods.getActivity_status() == 2) {
			Activity act = null;
			if (goods.getActivity_goods_id() != null) {
				ActivityGoods ag = this.activityGoodsService.getObjById(CommUtil.null2Long(goods.getActivity_goods_id()));
				act = ag.getAct();
			} else {
				for (ActivityGoods ag : goods.getAg_goods_list()) {
					if (ag.getAg_goods().getId() == goods.getId()) {
						act = ag.getAct();
						break;
					}
				}
			}
			if (act != null && act.getAc_begin_time().before(nowDate) && act.getAc_end_time().after(nowDate)) {
				str = "促销";
			}
		}
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.getObjById(goods.getBuyGift_id());
			if (bg != null && bg.getBeginTime().before(nowDate)) {
				str = "满送";
				info = "活动商品购满" + bg.getCondition_amount() + "元，即可领取赠品";
			}
		}
		if (goods.getEnough_reduce() == 1) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(goods.getOrder_enough_reduce_id()));
			if (er.getErbegin_time().before(nowDate)) {
				str = "满减";
				info = "活动商品" + er.getErtag();
			}
		}
		if (goods.getCombin_status() == 1) {
			str = "组合";
			info = "点击查看组合套装完整信息";
		}
		if (goods.getAdvance_sale_type() == 1) {
			str = "预售";
			info = "预售商品，" + CommUtil.formatShortDate(goods.getAdvance_date()) + "开始发货";
		}
		if (goods.getF_sale_type() == 1) {
			str = "F码";
			info = "F码商品凭F码购买";
		}
		Map map = new HashMap();
		map.put("name", str);
		map.put("info", info);
		return map;
	}

	/**
	 * 查询商品对应店铺的店主的用户名
	 * 
	 * @param goods_id
	 * @return
	 */
	public String query_Store_userName(Long goods_id) {
		Goods goods = this.goodsService.getObjById(goods_id);
		if (goods.getGoods_type() != 0) {
			User user = goods.getGoods_store().getUser();
			return user.getUserName();
		}
		return null;
	}

	public GoodsLog getTodayGoodsLog(long id) {
		Map logParams = new HashMap();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		logParams.put("beginDate", cal.getTime());
		now = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		logParams.put("endDate", cal.getTime());
		logParams.put("goods_id", id);
		List<GoodsLog> goodsLogList = this.goodsLogService.query(
				"select obj from GoodsLog obj where obj.goods_id=:goods_id and obj.addTime>=:beginDate and obj.addTime<:endDate",
				logParams, -1, -1);
		if (goodsLogList.size() == 0) {
			Goods good = this.goodsService.getObjById(id);
			GoodsLog todayGoodsLog = new GoodsLog();
			todayGoodsLog.setAddTime(now);
			todayGoodsLog.setGoods_id(id);
			todayGoodsLog.setGoods_name(good.getGoods_name());
			if (good.getGoods_main_photo() != null) {
				todayGoodsLog.setImg_id(good.getGoods_main_photo().getId());
			}
			if(null!=good.getGc()){
				todayGoodsLog.setGc_id(good.getGc().getId());
			}
			if (good.getGoods_brand() != null) {
				todayGoodsLog.setGoods_brand_id(good.getGoods_brand().getId());
			}
			todayGoodsLog.setPrice(good.getGoods_current_price());
			Map map = new HashMap();
			todayGoodsLog.setGoods_order_type(Json.toJson(map, JsonFormat.compact()));
			todayGoodsLog.setGoods_sale_info(Json.toJson(map, JsonFormat.compact()));

			if (good.getGoods_type() == 0) {
				todayGoodsLog.setLog_form(0);
			} else {
				todayGoodsLog.setLog_form(1);
				todayGoodsLog.setStore_id(good.getGoods_store().getId());
				todayGoodsLog.setStore_name(good.getGoods_store().getStore_name());
			}

			Map preferentialMap = this.query_goods_preferential(id);
			todayGoodsLog.setPreferential(preferentialMap.get("name").toString());
			todayGoodsLog.setPreferential_info(preferentialMap.get("info").toString());
			this.goodsLogService.save(todayGoodsLog);
			return todayGoodsLog;
		} else {
			return goodsLogList.get(0);
		}
	}

	public List getGiftList(String gift_info) {
		List list = new ArrayList();
		List<Map> gift_list = (List<Map>) Json.fromJson(gift_info);
		for (Map map : gift_list) {
			if ("0".equals(map.get("storegoods_count"))) {
				if (Integer.parseInt(map.get("goods_count").toString()) > 0) {
					list.add(map);
				}
			} else {
				if (query_Goods(map.get("goods_id").toString()).getGoods_inventory() > 0) {
					list.add(map);
				}
			}

		}
		return list;
	}

	public int queryInventory(String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		int inventory = 0;
		if (goods != null) {
			inventory = goods.getGoods_inventory();
		}
		return inventory;
	}
	
	/**
	 * 查询商品当前价格
	 * @param id 商品id
	 * @return
	 */
	public BigDecimal queryCurrentPrice(String id){
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		if(goods != null){
			if(goods.getSeckill_buy() == 2 || goods.getSeckill_buy()==4){
				return goods.getGoods_current_price();
			}
			return goods.getStore_price();
		}
		return null;
	}
}
