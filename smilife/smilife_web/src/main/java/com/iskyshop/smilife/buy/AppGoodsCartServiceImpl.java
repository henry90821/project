package com.iskyshop.smilife.buy;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smi.tools.kits.CollectionKit;
import com.smi.tools.kits.StrKit;

@Service
@Transactional
public class AppGoodsCartServiceImpl implements IAppGoodsCartService{
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private GoodsTools goodsTools;
	
	
	@Transactional(readOnly = false)
	public boolean save(GoodsCart goodsCart) {
		/**
		 * init other field here
		 */
		try {
			this.goodsCartDao.save(goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public GoodsCart getObjById(Long id) {
		GoodsCart goodsCart = this.goodsCartDao.get(id);
		if (goodsCart != null) {
			return goodsCart;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsCartDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> goodsCartIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsCartIds) {
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
		GenericPageList pList = new GenericPageList(GoodsCart.class,construct, query,
				params, this.goodsCartDao);
		PageObject pageObj = properties.getPageObj();
		if (pageObj != null)
			pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
					.getCurrentPage(), pageObj.getPageSize() == null ? 0
					: pageObj.getPageSize());
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(GoodsCart goodsCart) {
		try {
			this.goodsCartDao.update( goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<GoodsCart> query(String query, Map params, int begin, int max){
		return this.goodsCartDao.query(query, params, begin, max);
		
	}
	
	
	public String remove_carts(List<String> list_ids) {
		String code = "100"; // 100表示删除成功，200表示删除失败
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		Map params = new HashMap();
		
		if (list_ids != null && list_ids.size() > 0) {
			for (String id : list_ids) {
				if (id.indexOf("combin") < 0) {
					GoodsCart gc = this.getObjById(CommUtil.null2Long(id));
					if (gc != null) {
						String combin_mark = null;
						if ("combin".equals(gc.getCart_type())) {//只要被删除的购物车中的商品为组合套装中的商品，就会将套装中的所的商品变为普通商品，然后再删除用户要删除的购物车 
							combin_mark = gc.getCombin_mark();
						}
						gc.getGsps().clear();
						this.delete(gc.getId());
						
						if(combin_mark != null) {
							params.put("combin_mark", combin_mark);
							List<GoodsCart> suit_carts = this.query("select obj from GoodsCart obj where obj.combin_mark=:combin_mark", params, -1, -1);
							for(GoodsCart suit_cart: suit_carts) {
								suit_cart.setCart_type(null);
								suit_cart.setCombin_mark(null);
								suit_cart.setCombin_main(0);
								suit_cart.setCombin_suit_ids(null);
								String default_gsp = this.genericDefaultGsp(suit_cart.getGoods());
								double default_price = CommUtil.null2Double(this.genericDefaultInfo(suit_cart.getGoods(), default_gsp).get("price"));
								suit_cart.setPrice(BigDecimal.valueOf(default_price));
								suit_cart.setCart_gsp(default_gsp);										
								this.setGoodsCartSpec(suit_cart.getGoods(), suit_cart, CommUtil.null2String(default_gsp).split(","));
								this.update(suit_cart);
							}
						}					
					}
				} else {
					params.put("combin_mark", id);
					List<GoodsCart> suit_carts = this.query(
							"select obj from GoodsCart obj where obj.combin_mark=:combin_mark", params, -1, -1);
					for (GoodsCart suit_gc : suit_carts) {
						suit_gc.getGsps().clear();
						this.delete(suit_gc.getId());
					}
				}
			}
		} else {
			code = "200";
		}
		return code;
	}

	
	/**
	 * 取出商品的所有规格的第一个规格属性的id，这些规格属性的id以逗号拼接(最后面有一个逗号)然后返回(序号越小的规格的规格属性的id在列表中排得越后)。若商品不是规格库存，则返回空串
	 */
	/**
	 * 取出商品的所有规格的第一个规格属性的id，这些规格属性的id以逗号拼接(最后面有一个逗号)然后返回(序号越小的规格的规格属性的id在列表中排得越后)。若商品不是规格库存，则返回空串
	 */
	public String genericDefaultGsp(Goods goods) {
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
	 * 返回商品的规格库存和价格。若此商品参加了团购或秒杀活动，则返回此商品的团购价或秒杀价及对应库存；若此商品为全局库存商品，则返回全局库存的单价和库存；若此商品为规格库存商品，则返回对应gspIds参数的单价和库存（若gspIds为空或为错误的规格属性id列表，则使用默认的规格属性列表）；若此商品参加了商城活动且当前用户已登录，则会返回此商品折扣后的价格（是前面的价格基础上再折扣的价格））
	 * 
	 * 返回值：key=price:商品对应规格的价格（可能是折扣后的价格）；key=count：商品对应规格的库存
	 */
	public Map genericDefaultInfo(Goods goods, String gsp) {
		Map map = new HashMap();
		Map result = this.goodsTools.getGoodsPriceAndInventory(goods, gsp, null);
		if(result.containsKey("act_price")) {
			map.put("price", result.get("act_price"));
		} else {
			map.put("price", result.get("price"));
		}
		map.put("count", result.get("count"));
		return map;
	}


	@Transactional(readOnly = false)
	public Map add_goodsCart(HttpServletRequest request, HttpServletResponse response, Long id, 
			Integer count, String gsp, String suit_gsp,
			String buy_type, String combin_ids, Long combin_version,String custId,String cart_sessionId) {
		int next = 0; // 0为添加成功,-1添加失败，-2商品下架，-3库存不足
		String msg = "添加购物车成功！";
		User user = null;
		User topUser = null;
		GoodsCart seckillGoodsCart = null;//秒杀商品购物车

		Goods goods = this.goodsService.getObjById(id);

		if (goods == null || goods.getGoods_status() != 0) {
			next = -2;
			msg = "商品不存在或商品已下架！";
		} else if (goods.getF_sale_type() == 1) {// F码商品不可以添加到购物车
			next = -1;
			msg = "F码商品中不可以添加到购物车！";
		} else if (count == null || count <= 0) {
			next = -1;
			msg = "添加到购物车的数量必须为正数！";
		} else {
			// 判断自家商品不可以添加到自已的购物车中去
			if(null != custId){
				user = userService.getObjByProperty(null, "custId", custId);
			}

			if (user != null) {
				topUser = user.getParent() == null ? user : user.getParent();

				if (topUser.getUserRole().contains("ADMIN") && goods.getGoods_store() == null
						|| topUser.getUserRole().contains("SELLER") && goods.getGoods_store() != null
						&& goods.getGoods_store().getId().equals(topUser.getStore().getId())) {
					next = -1;
					msg = "不可将自家店铺的商品添加到自己的购物车中！";
				}
			}
		}

		Map goodsInfo = null;// 商品的价格和库存信息

		// 判断库存
		if (next == 0) {
			if (StringUtils.isNullOrEmpty(gsp) || this.goodsTools.getGSPsMap(goods, gsp) == null) { // 从商品列表页添加到购车，生成默认的gsp信息
				gsp = this.genericDefaultGsp(goods);
			}
			// 查询商品价格和库存数
			goodsInfo = this.genericDefaultInfo(goods, gsp);

			if (CommUtil.null2Int(goodsInfo.get("count")) <= 0) {
				next = -3;
				msg = "商品库存不足！";
			}
		}

		List<GoodsCart> carts_list = this.cart_calc(request, response,custId,cart_sessionId);
		List<GoodsCart> newGoodsCarts = new ArrayList<GoodsCart>();
		// 开始将商品添加到购物车
		if (next == 0) {
//			String cart_sessionId = "";
//			Cookie[] cookies = request.getCookies();
//			if (cookies != null) {
//				for (Cookie cookie : cookies) {
//					if ("cart_session_id".equals(cookie.getName())) {
//						cart_sessionId = CommUtil.null2String(cookie.getValue());
//					}
//				}
//			}
			if ("".equals(cart_sessionId) && user == null) {
				cart_sessionId = UUID.randomUUID().toString();
//				Cookie cookie = new Cookie("cart_session_id", cart_sessionId);
//				cookie.setDomain(CommUtil.generic_domain(request));
//				response.addCookie(cookie);
			}

			// 获取当前的所有购物车
			Map params = new HashMap();
			Date addTime = new Date();
			GoodsCart matchedGC = null;

			if ("suit".equals(buy_type) && !StringUtils.isNullOrEmpty(combin_ids) && combin_version != null) {// 添加的是组合套装
				if (goods.getCombin_status() == 1 && goods.getCombin_suit_id() != null && combin_version > 0) {					
					// 检查套装在购物车中是否已存在
					Map suitInfo = null;
					for (GoodsCart gc : carts_list) {
						if (gc.getCombin_main() == 1 && gc.getGoods().getId().equals(goods.getId())) {
							suitInfo = Json.fromJson(Map.class, gc.getCombin_suit_info());
							String ids = this.goodsViewTools.getCombinPlanGoodsIds(suitInfo);
							if (ids.equals(combin_ids)) {
								matchedGC = gc;
								break;
							}
						}
					}
					if (matchedGC != null) {// 购物车中已存在套装，则更新此购物车的购买数量
						matchedGC.setCount(matchedGC.getCount() + 1);
						suitInfo.put("suit_count", matchedGC.getCount());
						matchedGC.setCombin_suit_info(Json.toJson(suitInfo, JsonFormat.compact()));
						this.update(matchedGC);
					} else {// 购物车中没有用户添加的套装
						// 查找商品对应的组合套装
						params.put("main_goods_id", id);
						params.put("combin_type", 0); // 组合套装
						params.put("combin_status", 1);
						List<CombinPlan> suits = this.combinplanService
								.query("select obj from CombinPlan obj where obj.main_goods_id=:main_goods_id and obj.combin_type=:combin_type and obj.combin_status=:combin_status",
										params, -1, -1);

						if (suits.size() > 0) {
							// 检查用户添加的是此主体商品的哪一个套装
							suitInfo = null;
							CombinPlan plan = suits.get(0);// 一个主体商品只能有一个CombinPlan实例，所以只取第一个实例
							List<Map> map_list = (List<Map>) Json.fromJson(plan.getCombin_plan_info());
							for (Map temp_map : map_list) {
								String ids = this.goodsViewTools.getCombinPlanGoodsIds(temp_map);
								if (ids.equals(combin_ids)) {
									suitInfo = temp_map;
									break;
								}
							}
							if (suitInfo != null) {// 已找到用户添加的套装，开始添加套装到购物车
								Map<Long, String> suit_gsps = new HashMap<Long, String>();
								if(!StringUtils.isNullOrEmpty(suit_gsp)) {
									String[] suit_gsps_splited = suit_gsp.split("gsp");
									for(String gspTmp: suit_gsps_splited) {
										if(!"".equals(gspTmp)) {
											String[] parts = gspTmp.split("_");
											suit_gsps.put(CommUtil.null2Long(parts[0]), parts[1]);
										}
									}
								}
								
								String combin_mark = "combin" + UUID.randomUUID();
								String suit_cartIds = ""; // 子套装的购物车列表
								String cart_type = "combin";
								String combin_suit_version = "【套装" + combin_version + "】";

								List<Map> goods_list = (List<Map>) suitInfo.get("goods_list");
								for (Map good_map : goods_list) {// 保存套装中的非主体商品
									Goods suit_goods = this.goodsService.getObjById(CommUtil.null2Long(good_map.get("id")));
									GoodsCart cart = new GoodsCart();
									cart.setAddTime(addTime);
									cart.setCount(1);
									cart.setGoods(suit_goods);
									String combin_child_gsp = suit_gsps.get(suit_goods.getId());
									if(combin_child_gsp == null || this.goodsTools.getGSPsMap(suit_goods, combin_child_gsp) == null) {
										combin_child_gsp = this.genericDefaultGsp(suit_goods);
									}								
									
									this.setGoodsCartSpec(suit_goods, cart, combin_child_gsp.split(","));
									cart.setCart_gsp(combin_child_gsp);
									cart.setCombin_mark(combin_mark);
									cart.setCart_type(cart_type);
									cart.setPrice(new BigDecimal(0));// 套装中的非主购物车的价格置为0
									if (user == null) {
										cart.setCart_session_id(cart_sessionId);
									} else {
										cart.setUser(user);
									}
									this.save(cart);
									suit_cartIds = suit_cartIds + "," + cart.getId();
								}
								// 保存套装中的主体商品到购物车中去
								GoodsCart obj = new GoodsCart();
								obj.setAddTime(addTime);
								obj.setCount(1);
								obj.setGoods(goods);
								obj.setCombin_suit_ids(suit_cartIds);
								obj.setCombin_version(combin_suit_version);
								obj.setCombin_main(1);
								obj.setCombin_mark(combin_mark);
								obj.setCart_type(cart_type);
								
								obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(CommUtil.formatMoney(CommUtil.null2Double(suitInfo.get("plan_goods_price")))))); 
								
								String combin_main_default_gsp = this.genericDefaultGsp(goods);
								obj.setCart_gsp(combin_main_default_gsp);
								this.setGoodsCartSpec(goods, obj, combin_main_default_gsp.split(","));								
								
								if (user == null) {
									obj.setCart_session_id(cart_sessionId);
								} else {
									obj.setUser(user);
								}

								suitInfo.put("suit_count", 1);
								suitInfo.put("suit_all_price",
										CommUtil.formatMoney(CommUtil.mul(obj.getCount(), obj.getPrice()))); // 套装整体价格=套装单价*数量
								suitInfo.put("suit_name", combin_suit_version);
								obj.setCombin_suit_info(Json.toJson(suitInfo, JsonFormat.compact()));
								this.save(obj);
								newGoodsCarts.add(obj);
							} else {
								next = -1;
								msg = "请勿篡改请求的数据！";
							}
						} else {
							next = -1;
							msg = "系统内部错误：商品对应的套装不存在！";
						}
					}
				} else {
					next = -1;
					msg = "请勿篡改请求的数据！";
				}
			} else {// 因为组合配件中的商品没有什么关联，实际上配件中的商品添加到购物车与普通商品添加到购物车的过程及效果是一样的，所以不对用户提交上来的combin_ids及商品状态是否正常、是否有配件配置进行检查。此处直接将添加配件到购物车与单个普通商品添加到购物车进行合并
				Set<Long> ids = new HashSet<Long>();// 将要放入到购物车中去的商品的id
				ids.add(id);
				if ("parts".equals(buy_type) && goods.getCombin_status() == 1 && goods.getCombin_parts_id() != null
						&& !StringUtils.isNullOrEmpty(combin_ids)) {// 不需要进行太严格的校验
					for (String partsId : combin_ids.split(",")) {
						Long tmpId = CommUtil.null2Long(partsId);
						if (tmpId != -1l) {
							ids.add(tmpId);
						}
					}
				}
				// 将商品逐个添加到购物车中去
				for (Long gId : ids) {
					Goods g = this.goodsService.getObjById(gId);
					if (g != null) {
						// 禁止用户通过修改combin_ids来添加自家店铺的商品到购物车中去(gId==id的商品在最前面已通过检查了，所以此商品不用再检查了)
						if (gId != id
								&& user != null
								&& (topUser.getUserRole().contains("ADMIN") && g.getGoods_store() == null || topUser
										.getUserRole().contains("SELLER")
										&& g.getGoods_store() != null
										&& g.getGoods_store().getId().equals(topUser.getStore().getId()))) {
							continue;
						}
						// 检查商品是否已在购物车中。若已在购物车中则更新对应购物车中商品的数量
						boolean add = true;//能否将商品添加到购物车
						int flag = 1;//0：表示不更新购物车中的商品数量    1：表示要更新对应的购物车中的商品数量
						String tmpGsps = gsp;
						int tmpCount = count.intValue();
						Double tmpPrice = (Double) goodsInfo.get("price");
						if (gId != id) {
							tmpGsps = this.genericDefaultGsp(g);
							tmpCount = 1;
							tmpPrice = (Double) this.genericDefaultInfo(g, tmpGsps).get("price");
						} else if ("parts".equals(buy_type)) {
							tmpCount = 1;
						}
						for (GoodsCart gc : carts_list) {
							if (!"combin".equals(gc.getCart_type()) && gc.getGoods().getId().equals(g.getId())) {
								if(g.getSeckill_buy() >= 2) {//商品当前为秒杀商品
									add = false;
									flag = 0;
									seckillGoodsCart=gc;//解决两次添加同一个秒杀商品不返回的问题
									break;
								} else if(g.getGroup() != null && g.getGroup_buy() != 3) {//商品当前为团购商品
									add = false;
									matchedGC = gc;
									break;
								} else if(tmpGsps.equals(CommUtil.null2String(gc.getCart_gsp()))){
									add = false;
									matchedGC = gc;
									break;
								}								
							}
						}

						if (add) {// 添加商品到购物车
							GoodsCart newGc = new GoodsCart();
							newGc.setAddTime(addTime);
							// 秒杀限购一件
							if (g.getSeckill_buy() >= 2) {
								newGc.setCount(1);
								newGc.setCart_type("seckill");
								seckillGoodsCart=newGc;//保存秒杀商品购物车
							} else {
								newGc.setCount(tmpCount);
								if(g.getGroup() != null && g.getGroup_buy() != 3) {
									newGc.setCart_type("group");
								}
							}

							newGc.setCart_gsp(tmpGsps);
							this.setGoodsCartSpec(g, newGc, tmpGsps.split(","));
							if (user == null) {
								newGc.setCart_session_id(cart_sessionId);
							} else {
								newGc.setUser(user);
							}
							newGc.setGoods(g);
							newGc.setPrice(new BigDecimal(tmpPrice));
							this.save(newGc);
							newGoodsCarts.add(newGc);
						} else {// 更新对应购物车中商品的数量
							if (flag == 1) {
								matchedGC.setCount(matchedGC.getCount() + tmpCount);
								matchedGC.setPrice(new BigDecimal(tmpPrice));// 更新原购物车中商品的价格为最新的价格
								this.update(matchedGC);
							}
						}
					}
				}
			}
		}

		Map json_map = new HashMap();
		json_map.put("count", carts_list.size() + newGoodsCarts.size());
		json_map.put("code", next);
		json_map.put("msg", msg);
		json_map.put("seckill_gcid",seckillGoodsCart!=null ? seckillGoodsCart.getId() : null);
		return json_map;
	}
	
	
	/**
	 * 设置购物车的规格文字说明，将原有规格的名字替换成自定义的，同时设置购物车与这些规格的关联关系
	 * 
	 * @param goods
	 * @param cart  
	 * @param gspIds
	 */
	public void setGoodsCartSpec(Goods goods, GoodsCart cart, String[] gspIds) {
		String spec_info = "";
		cart.getGsps().clear();
		if(gspIds != null && gspIds.length > 0) {
			List<Map> goods_specs_info = goods.getGoods_specs_info() == null ? new ArrayList<Map>() : (List<Map>) Json
					.fromJson(goods.getGoods_specs_info());
			for (String gsp_id : gspIds) {
				GoodsSpecProperty spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gsp_id));
				if (spec_property != null) {
					cart.getGsps().add(spec_property);
					spec_info += spec_property.getSpec().getName() + "：";
					if (goods_specs_info.size() > 0) {
						for (Map map : goods_specs_info) {
							if (CommUtil.null2Long(map.get("id")).equals(spec_property.getId())) {
								spec_info += map.get("name").toString();
							}
						}
					} else {
						spec_info += spec_property.getValue();
					}
					spec_info += "<br>";
				}
			}
		}
		cart.setSpec_info(spec_info);
	}
	
	
	/**
	 * 获得当前请求对应的购物车。若当前用户未登录，则返回cookie中的购物车；若用户已登录，则将cookie购物车合并到用户购物车（不同规格的相同商品不合并）(对于组合套装，只会返回套装中的主体商品对应的购物车)(若当前用户已登录，
	 * 则购物车cookie会被删除)(此函数会刷新购物车中商品的信息)
	 * @param request
	 * @param response
	 * @param custId
	 * @return
	 */
	public List<GoodsCart> cart_calc(HttpServletRequest request, HttpServletResponse response,String custId,String cart_session_id) {
//		String cart_session_id = "";
//		Cookie[] cookies = request.getCookies();
//		Cookie cartCookie = null;
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if ("cart_session_id".equals(cookie.getName())) {
//					cart_session_id = CommUtil.null2String(cookie.getValue());
//					cartCookie = cookie;
//					break;
//				}
//			}
//		}

		List<GoodsCart> carts_list = new ArrayList<GoodsCart>(); // 用户总购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>(); // 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>(); // 未提交的用户user购物车
		Set<String> combin_marks_toDel = new HashSet<String>();
		Set<String> combin_marks_toUpdate = new HashSet<String>();
		Map cart_map = new HashMap();

		if (null != cart_session_id && !"".equals(cart_session_id)) {
			cart_map.put("cart_session_id", cart_session_id);
			carts_cookie = this.query(
					"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=0 ",
					cart_map, -1, -1);
		}

		User user = null;
		if(StrKit.isNotEmpty(custId)){
			user = userService.getObjByProperty(null, "custId", custId);
		}
		User topUser = null;
		if (user != null) {// 用户已登录
			cart_map.clear();
			cart_map.put("user_id", user.getId());
			carts_user = this.query(
					"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=0 ", cart_map, -1, -1);

			user = userService.getObjById(user.getId());
			topUser = user.getParent() == null ? user : user.getParent();// 为了获取用户对应的店铺

//			if (cartCookie != null) {
//				cartCookie.setMaxAge(0);
//				response.addCookie(cartCookie);
//			}
		}

		// 将用户的购物车添加到总的购物车中去(不添加组合套装中的非主体商品)
		for (GoodsCart gc : carts_user) {
			if (!"combin".equalsIgnoreCase(gc.getCart_type()) || gc.getCombin_main() == 1) {
				carts_list.add(gc);
			}
		}

		carts_user.clear();
		carts_user.addAll(carts_list);
		
		this.refreshGoodsCarts(carts_user);
		this.refreshGoodsCarts(carts_cookie);

		// 将cookie购物车合并到总的购物车中去
		for (GoodsCart cookiegc : carts_cookie) {
			if ("combin".equalsIgnoreCase(cookiegc.getCart_type()) && cookiegc.getCombin_main() != 1) {
				continue;
			}
			boolean add = true;
			for (GoodsCart gc2 : carts_user) {
				if (cookiegc.getGoods().getId().equals(gc2.getGoods().getId())) {
					if (gc2.getCombin_main() == 1) {// 主体商品
						if (cookiegc.getCombin_main() == 1 && cookiegc.getCombin_version().equals(gc2.getCombin_version())) {
							add = false;
							break;
						}
					} else if("seckill".equals(gc2.getCart_type())) {
						if("seckill".equals(cookiegc.getCart_type())) {
							add = false;
							break;
						}
					} else {// 普通商品						
						if (cookiegc.getCombin_main() != 1 && CommUtil.null2String(cookiegc.getCart_gsp()).equals(CommUtil.null2String(gc2.getCart_gsp()))) {
							add = false;
							break;
						}
					}
				}
			}
			if (add) {
				if (user != null) {// 用户已登录
					Store store = cookiegc.getGoods().getGoods_store();
					if (user.getUserRole().contains("ADMIN") && store == null || user.getUserRole().contains("SELLER")
							&& store != null && topUser.getStore().getId().equals(store.getId())) {// cookie中的购物车中的商品不应是当前用户店铺的商品

						if (cookiegc.getCombin_main() == 1) {// 如果删除的是组合套装中的主体商品，则要将此组合套装中的所有商品删除
							combin_marks_toDel.add(cookiegc.getCombin_mark());
						} else {
							this.delete(cookiegc.getId());
						}
						continue;
					}

					if (cookiegc.getCombin_main() == 1) {// 如果修改的是组合套装中的主体商品，则要修改此组合套装中的所有商品
						combin_marks_toUpdate.add(cookiegc.getCombin_mark());
					} else {
						cookiegc.setCart_session_id(null);
						cookiegc.setUser(user);
						this.update(cookiegc);
					}

					carts_list.add(cookiegc);
				} else {
					carts_list.add(cookiegc);
				}
			} else {
				if (cookiegc.getCombin_main() == 1) {// 如果删除的是组合套装中的主体商品，则要将此组合套装中的所有商品删除
					combin_marks_toDel.add(cookiegc.getCombin_mark());
				} else {
					this.delete(cookiegc.getId());
				}
			}
		}
		// 对应cookie购物车进行处理
		for (GoodsCart gc : carts_cookie) {
			if (combin_marks_toUpdate.contains(gc.getCombin_mark())) {
				gc.setCart_session_id(null);
				gc.setUser(user);
				this.update(gc);
			}
			if (combin_marks_toDel.contains(gc.getCombin_mark())) {
				this.delete(gc.getId());
			}
		}
		
		return carts_list;
	}

	
	
	/**
	 * 刷新PC端和H5端购物车中的价格、套装、规格等信息(此函数中，会将最新信息保存到数据库中)(在商品被删除时，商品对应的购物车也会被删除，所以此函数不必须对此情况进行处理)。不对下架的商品对应的购物车进行删除(要在用户购物车界面进行提示)
	 * @param carts  用户购物车（不包含套装中的非主体商品）
	 */
	public void refreshGoodsCarts(List<GoodsCart> carts) {
		if(carts != null && carts.size() > 0) {
			for(GoodsCart gc: carts) {
				Goods goods = gc.getGoods();
				boolean toClear = false;
				if(gc.getCombin_main() == 1 && "combin".equals(gc.getCart_type())) {//更新组合套装的主体商品
					if(goods.getCombin_status() != 1 || goods.getCombin_suit_id() == null) {//套装已结束，则将当前购物车恢复为普通商品的购物车
						toClear = true;				
					} else {
						CombinPlan cp = this.combinplanService.getObjById(goods.getCombin_suit_id());
						if(cp == null || cp.getCombin_status() != 1) {
							toClear = true;
							if(cp == null) {
								if(goods.getCombin_parts_id() == null) {
									goods.setCombin_status(0);
								}								
								goods.setCombin_suit_id(null);
								this.goodsService.update(goods);
							}
						} else {
							Map gcCombinInfo = Json.fromJson(Map.class, gc.getCombin_suit_info());
							List<Map> gcGoodsInfo =  (List<Map>)gcCombinInfo.get("goods_list");
							List<Integer> gcGoodsIds = new ArrayList<Integer>();
							for(Map gm: gcGoodsInfo) {
								gcGoodsIds.add((Integer)gm.get("id"));
							}
							
							boolean found = false;
							List<Map> cpCombinInfo = Json.fromJson(List.class, cp.getCombin_plan_info());
							List<Integer> cpGoodsIds = new ArrayList<Integer>();
							Map foundSuit = null;
							
							for(Map cpMap: cpCombinInfo) {
								cpGoodsIds.clear();								
								List<Map> cpGoodsInfo =  (List<Map>)cpMap.get("goods_list");
								for(Map gm: cpGoodsInfo) {
									cpGoodsIds.add((Integer)gm.get("id"));
								}
								if(gcGoodsIds.size() == cpGoodsIds.size()) {
									boolean flag = true;
									for(Integer i: gcGoodsIds) {
										if(!cpGoodsIds.contains(i)) {
											flag = false;
											break;
										}
									}
									if(flag) {
										found = true;
										foundSuit = cpMap;
										break;
									}
								}								
							}
							if(found) {//套装中的商品没有修改，则检查是否更新价格(没有更新套装中非主体商品对应的购物车)
								Double cp_plan_goods_price = CommUtil.null2Double(foundSuit.get("plan_goods_price"));
								if(cp_plan_goods_price != gc.getPrice().doubleValue()) {//更新购物车的价格等信息
									gc.setPrice(new BigDecimal(cp_plan_goods_price));
									foundSuit.put("suit_all_price", CommUtil.formatMoney(CommUtil.mul(gc.getCount(), gc.getPrice())));
									foundSuit.put("suit_count", gc.getCount());
									foundSuit.put("suit_name", gcCombinInfo.get("suit_name"));
									gc.setCombin_suit_info(Json.toJson(foundSuit));
									this.update(gc);
								}								
							} else {//套装中的商品有修改
								toClear = true;
							}
						}
					}
					if(toClear) {
						String[] gcIds = gc.getCombin_suit_ids().split(",");
						for(String gcId: gcIds) {//删除套装中非主体商品对应的购物车
							Long gcIdTmp = CommUtil.null2Long(gcId);
							GoodsCart gcTmp = this.getObjById(gcIdTmp);
							if(gcTmp != null) {
								gcTmp.getGsps().clear();
								this.delete(gcIdTmp);
							}
						}
						gc.setCart_type(null);
						gc.setCombin_main(0);						
						gc.setCombin_mark(null);
						gc.setCombin_suit_ids(null);
						gc.setCombin_suit_info(null);
						gc.setCombin_version(null);
						this.update(gc);	
					}					
				} else {//更新gsp、price、count等相关字段
					boolean toUpdate = false;
					Map result = this.goodsTools.getGoodsPriceAndInventory(goods, gc.getCart_gsp(), null);
					Double price = (Double)result.get("price");
					Integer count = (Integer)result.get("count");
					if(result.containsKey("act_price")) {
						price = (Double)result.get("act_price");
					}
					
					if(result.containsKey("group") && !"group".equals(gc.getCart_type())) {
						toUpdate = true;
						gc.setCart_type("group");
					} else if(!result.containsKey("group") && "group".equals(gc.getCart_type())) {
						toUpdate = true;
						gc.setCart_type(null);
					}
					
					if(result.containsKey("seckill") && !"seckill".equals(gc.getCart_type())) {
						toUpdate = true;
						gc.setCart_type("seckill");
					} else if(!result.containsKey("seckill") && "seckill".equals(gc.getCart_type())) {
						toUpdate = true;
						gc.setCart_type(null);
					}
					
					if(result.containsKey("useDefaultGsps")) {
						toUpdate = true;
						String[] gspIds = (String[])result.get("useDefaultGsps");
						StringBuilder sb = new StringBuilder();
						for(String id: gspIds) {
							sb.append(id).append(",");
						}
						gc.setCart_gsp(sb.toString());
						this.setGoodsCartSpec(goods, gc, gspIds);
					} else {//暂不去检查购物车中的规格属性值是否有变化												
					}
					
					if(gc.getPrice().doubleValue() != price.doubleValue()) {
						toUpdate = true;
						gc.setPrice(new BigDecimal(price));
					}
					
					if(gc.getCount() > count) {
						toUpdate = true;
						gc.setCount(count);
					}
					
					if(toUpdate) {
						this.update(gc);
					}
				}
			}
			
		}
	}
	
	/**
	 * 获取购物车商品的数量
	 * @author herendian
	 * @version 1.0
	 * @date 2016年4月5日 下午5:37:31
	 * @param request
	 * @param response
	 * @param custId
	 * @param cart_session_id
	 * @return
	 */
	public int getCartCount(String custId,String sessionId){
		int resultCount=0;
		List<GoodsCart> totalGoods=new ArrayList<GoodsCart>();
		Map<String, Object> cartParamsMap=new HashMap<String,Object>();
		//用户没有登录的情况下
		if (StrKit.isEmpty(custId)) {
			cartParamsMap.put("cart_session_id", sessionId);
			totalGoods = this.query(
					"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=0 ",
					cartParamsMap, -1, -1);
		}else {
		    User user = userService.getObjByProperty(null, "custId", custId);
			//用户登录后的
			cartParamsMap.put("user_id", user.getId());
			totalGoods = this.query(
					"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=0 ", cartParamsMap, -1, -1);

		}
		if (CollectionKit.isNotEmpty(totalGoods)) {
			for (GoodsCart good:totalGoods) {
				resultCount=resultCount+good.getCount();
			}
		}
		return resultCount;
	}


}
