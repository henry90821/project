package com.iskyshop.manage.admin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MsgTools.java
 * </p>
 * 
 * <p>
 * Description: 订单工具类（如对订单的json数据进行解析，对购物车计算金额等）
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
 * @date 2014-5-4
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class OrderFormTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService gspService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IEnoughReduceService enoughReduceService;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 解析订单商品信息json数据
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {

		List<Map> map_list = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(json)) {
			map_list = Json.fromJson(ArrayList.class, json);
		}
		return map_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品,包括子订单中的商品(对于组合套装，只返回主体商品，非主体商品不返回。也不返回赠品)
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Goods> queryOfGoods(String of_id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(of_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		List<Goods> goods_list = new ArrayList<Goods>();
		for (Map map : map_list) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
			if (!StringUtils.isNullOrEmpty(goods)) {
				goods_list.add(goods);
			}
		}
		if (!StringUtils.isNullOrEmpty(of.getChild_order_detail())) { // 查询子订单中的商品信息
			List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
			for (Map childMap : maps) {
				OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(childMap.get("order_id")));
				// map_list.clear();
				map_list = this.queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					Goods childGoods = this.goodsService.getObjById(CommUtil.null2Long(map1.get("goods_id")));
					if (!StringUtils.isNullOrEmpty(childGoods)) {
						goods_list.add(childGoods);
					}
				}
			}
		}
		return goods_list;
	}
	
	/**
	 * 根据订单id查询该订单中所有商品,不包括子订单中的商品(对于组合套装，只返回主体商品，非主体商品不返回。也不返回赠品)
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Goods> queryOrderOfGoods(String of_id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(of_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		List<Goods> goods_list = new ArrayList<Goods>();
		for (Map map : map_list) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
			if (!StringUtils.isNullOrEmpty(goods)) {
				goods_list.add(goods);
			}
		}
		return goods_list;
	}
	

	/**
	 * 根据订单id查询该订单中所有商品的价格总和
	 * 
	 * @param order_id
	 * @return
	 */
	public double queryOfGoodsPrice(String order_id) {
		double price = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			price = price + CommUtil.null2Double(map.get("goods_all_price"));
		}
		return price;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id) {
		int count = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		List<Map> map_list = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(of)) {
			map_list = this.queryGoodsInfo(of.getGoods_info());
		}
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if (count == 0) { // 主订单无数量信息，继续从子订单中查询
			if (!StringUtils.isNullOrEmpty(of.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map1 : map_list) {
						if (CommUtil.null2String(map1.get("goods_id")).equals(goods_id)) {
							count = CommUtil.null2Int(map1.get("goods_count"));
							break;
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的规格
	 * 
	 * @param order_id
	 * @return
	 */
	public List<GoodsSpecProperty> queryOfGoodsGsps(String order_id, String goods_id) {
		List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
		String goods_gsp_ids = "";
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		boolean add = false;
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
				break;
			}
		}
		String gsp_ids[] = goods_gsp_ids.split(",");
		Arrays.sort(gsp_ids);
		for (String id : gsp_ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				GoodsSpecProperty gsp = this.gspService.getObjById(CommUtil.null2Long(id));
				list.add(gsp);
				add = true;
			}
		}
		if (!add) { // 如果主订单中添加失败，则从子订单中添加
			if (!StringUtils.isNullOrEmpty(of.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map : map_list) {
						if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
							goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
							break;
						}
					}

					// begin dengyuqi 2015-11-2
					// String[] child_gsp_ids = goods_gsp_ids.split("/");
					String[] child_gsp_ids = goods_gsp_ids.split(",");
					// end dengyuqi 2015-11-2

					for (String id : child_gsp_ids) {
						if (!StringUtils.isNullOrEmpty(id)) {
							GoodsSpecProperty gsp = this.gspService.getObjById(CommUtil.null2Long(id));
							list.add(gsp);
							add = true;
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * 解析订单物流信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public String queryExInfo(String json, String key) {
		Map map = new HashMap();
		if (!StringUtils.isNullOrEmpty(json)) {
			map = Json.fromJson(HashMap.class, json);
		}
		return CommUtil.null2String(map.get(key));
	}

	/**
	 * 解析订单优惠券信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryCouponInfo(String json) {
		Map map = new HashMap();
		if (!StringUtils.isNullOrEmpty(json)) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 解析生活类团购订单json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryGroupInfo(String json) {
		Map map = new HashMap();
		if (!StringUtils.isNullOrEmpty(json)) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 根据订单id查询订单信息
	 * 
	 * @param id
	 * @return
	 */
	public OrderForm query_order(String id) {
		return this.orderFormService.getObjById(CommUtil.null2Long(id));
	}

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null) {
			order_status = order.getOrder_status();
			if (order.getOrder_main() == 1 && !StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					if (child_order.getOrder_status() < 30) {
						order_status = child_order.getOrder_status();
					}
				}
			}
		}
		return order_status;
	}

	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_order_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getTotalPrice());
			if (!StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = CommUtil.add(all_price, child_order.getTotalPrice());
				}

			}
		}
		return all_price;
	}

	/**
	 * 查询订单总邮费（如果包含子订单，将子订单邮费与主订单邮费相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_ship_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getShip_price());
			if (!StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = all_price + CommUtil.null2Double(child_order.getShip_price());
				}

			}
		}
		return all_price;
	}

	/**
	 * 查询订单(包括子订单)的可退金额
	 * 
	 * @param order_id(主订单id)
	 * @return
	 */
	public double query_refundable_price(String order_id) {
		double all_price = this.query_order_price(order_id);
		double ship_price = this.query_ship_price(order_id);
		double refund_price = this.query_refund_price(order_id);
		return CommUtil.subtract(all_price - ship_price, refund_price);
	}

	/**
	 * 查询订单(包括子订单)的已退金额
	 * 
	 * @param order_id(主订单id)
	 * @return
	 */
	public double query_refund_price(String order_id) {
		double all_price = 0;
		Map params = new HashMap();
		params.put("return_main_order_id", order_id);
		params.put("goods_return_status", "11");
		params.put("refund_status", 0);
		String sqlString = "select obj from ReturnGoodsLog obj where  obj.return_main_order_id=:return_main_order_id and obj.goods_return_status=:goods_return_status and obj.refund_status>:refund_status ";

		List<ReturnGoodsLog> returnlogs = returngoodslogService.query(sqlString, params, -1, -1);
		for (ReturnGoodsLog returnGoodsLog : returnlogs) {
			all_price = all_price + CommUtil.null2Double(returnGoodsLog.getRefund());
		}
		return all_price;
	}

	public double query_order_goods(String order_id) {
		double all_goods = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null) {
			all_goods = CommUtil.null2Double(order.getGoods_amount());
			if (!StringUtils.isNullOrEmpty(order.getChild_order_detail())) {
				List<Map> maps = this.queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_goods = all_goods + CommUtil.null2Double(child_order.getGoods_amount());
				}
			}
		}
		return all_goods;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_suitinfo(String goods_info) {
		Map map = (Map) Json.fromJson(goods_info);
		return map;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_order_suitgoods(Map suit_map) {
		List<Map> map_list = new ArrayList();
		if (!StringUtils.isNullOrEmpty(suit_map)) {
			map_list = (List<Map>) suit_map.get("goods_list");
		}
		return map_list;
	}

	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if ("store".equals(type)) {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if ("goods".equals(type)) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (goods != null && goods.getGoods_type() == 1) {
				store = goods.getGoods_store();
			}
		}
		return store;
	}

	/**
	 * 查询订单物流信息，包括子订单等
	 * 
	 * @param order_id
	 * @param type
	 *            type=all时同时查询子订单物流
	 * @return
	 */
	public List<TransInfo> query_ship(String order_id, String type) {
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		TransInfo transInfo = this.query_ship_getData(CommUtil.null2String(order_id));
		if (transInfo != null) {
			transInfo.setOrder_id(order.getOrder_id());
			transInfo.setExpress_company_name(this.queryExInfo(order.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getShipCode());
			transInfo_list.add(transInfo);
		}
		if ("all".equals(CommUtil.null2String(type))) {
			if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 查询子订单的物流跟踪信息
				List<Map> maps = this.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					TransInfo transInfo1 = this.query_ship_getData(CommUtil.null2String(child_order.getId()));
					if (transInfo1 != null) {
						transInfo1.setOrder_id(child_order.getOrder_id());
						transInfo1.setExpress_company_name(
								this.queryExInfo(child_order.getExpress_info(), "express_company_name"));
						transInfo1.setExpress_ship_code(child_order.getShipCode());
						transInfo_list.add(transInfo1);
					}
				}

			}
		}
		return transInfo_list;
	}

	public TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && !StringUtils.isNullOrEmpty(obj.getShipCode())) {
			if (this.configService.getSysConfig().getKuaidi_type() == 0 && obj.getExpress_info() != null) { // 免费物流接口
				try {
					ExpressCompany ec = this.queryExpressCompany(obj.getExpress_info());
					String companyMark = ec.getCompany_mark().split("_")[0];
					String query_url = "http://api.kuaidi100.com/api?id=" + this.configService.getSysConfig().getKuaidi_id()
							+ "&com=" + (ec != null ? companyMark : "") + "&nu=" + obj.getShipCode()
							+ "&show=0&muti=1&order=asc";
					URL url = new URL(query_url);
					URLConnection con = url.openConnection();
					con.setAllowUserInteraction(false);
					InputStream urlStream = url.openStream();
					String type = con.guessContentTypeFromStream(urlStream);
					String charSet = null;
					if (type == null) {
						type = con.getContentType();
					}
					if (type == null || type.trim().length() == 0 || type.trim().indexOf("text/html") < 0) {
						return info;
					}
					if (type.indexOf("charset=") > 0) {
						charSet = type.substring(type.indexOf("charset=") + 8);
					}
					byte[] b = new byte[10000];
					int numRead = urlStream.read(b);
					String content = new String(b, 0, numRead, charSet);
					while (numRead != -1) {
						numRead = urlStream.read(b);
						if (numRead != -1) {
							// String newContent = new String(b, 0, numRead);
							String newContent = new String(b, 0, numRead, charSet);
							content += newContent;
						}
					}
					info = Json.fromJson(TransInfo.class, content);
					urlStream.close();
				} catch (MalformedURLException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (this.configService.getSysConfig().getKuaidi_type() == 1 && obj.getExpress_info() != null) { // 收费物流接口
				ExpressInfo ei = this.expressInfoService.getObjByPropertyWithType("order_id", obj.getId(), 0);
				if (ei != null) {
					List<TransContent> data = (List<TransContent>) Json
							.fromJson(CommUtil.null2String(ei.getOrder_express_info()));
					info.setData(data);
					info.setStatus("1");
				}
			}
		}
		if(obj!=null){
			info.setOrder_id(obj.getId()+"");//设置订单id(app调用接口添加)
		}
		return info;
	}

	/**
	 * 解析订单中自提点信息
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_delivery(String delivery_info) {
		Map map = (Map) Json.fromJson(delivery_info);
		return map;
	}

	public ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (!StringUtils.isNullOrEmpty(json)) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map.get("express_company_id")));
		}
		return ec;
	}

	/**
	 * 查询订单中所以商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_goods_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		List<Map> list_map = new ArrayList<Map>();
		int count = 0;
		if (orderForm != null) {
			list_map = this.queryGoodsInfo(orderForm.getGoods_info());
			for (Map map : list_map) {
				count = count + CommUtil.null2Int(map.get("goods_count"));
			}
			if (orderForm.getOrder_main() == 1 && !StringUtils.isNullOrEmpty(orderForm.getChild_order_detail())) {
				list_map = this.queryGoodsInfo(orderForm.getChild_order_detail());
				for (Map map : list_map) {
					List<Map> list_map1 = new ArrayList<Map>();
					list_map1 = this.queryGoodsInfo(map.get("order_goods_info").toString());
					for (Map map2 : list_map1) {
						count = count + CommUtil.null2Int(map2.get("goods_count"));
					}
				}
			}
		}
		return count;
	}

	/**
	 * 查询订单中所有团购数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_group_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		Map map = new HashMap();
		int count = 0;
		if (orderForm != null) {
			map = this.queryGroupInfo(orderForm.getGroup_info());
			count = CommUtil.null2Int(map.get("goods_count"));
		}
		return count;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_integral_goodsinfo(String json) {
		List<Map> maps = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(json)) {
			maps = Json.fromJson(List.class, json);
		}
		return maps;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_integral_count(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_id));
		if (igo != null) {
			List<Map> objs = Json.fromJson(List.class, igo.getGoods_info());
			int count = objs.size();
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * 查询积分订单中所有商品，返回IntegralGoods集合
	 * 
	 * @param order_id
	 * @return
	 */
	public List<IntegralGoods> query_integral_all_goods(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_id));
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			IntegralGoods ig = this.integralGoodsService.getObjById(CommUtil.null2Long(obj.get("id")));
			if (ig != null) {
				objs.add(ig);
			}
		}
		return objs;
	}

	/**
	 * 查询积分订单中某商品的下单数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_integral_one_goods_count(IntegralGoodsOrder igo, String ig_id) {
		int count = 0;
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			if (obj.get("id") != null && obj.get("id").toString().equals(ig_id)) {
				count = CommUtil.null2Int(obj.get("ig_goods_count"));
				break;
			}
		}
		return count;
	}

	/**
	 * 查询订单中某件是否评价
	 * 
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public Evaluate query_order_evaluate(Object order_id, Object goods_id) {
		Map para = new HashMap();
		para.put("order_id", CommUtil.null2Long(order_id));
		para.put("goods_id", CommUtil.null2Long(goods_id));
		List<Evaluate> list = this.evaluateService.query(
				"select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.of.id=:order_id", para, -1, -1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 判断是否可修改评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_edit_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 判断是否可追加评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_add_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_add_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 计算今天到指定时间天数
	 * 
	 * @param date
	 * @return
	 */
	public int how_soon(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			long day = (end - begin) / 86400000;
			return CommUtil.null2Int(day);
		}
		return 999;
	}

	/**
	 * 验证订单中商品库存是否充足，是否可以支付订单，在选择支付方式请求中验证、在选择支付方式后支付中验证,返回true说明验证成功， 返回false说明验证失败
	 * 
	 * @param id
	 * @return
	 */
	public boolean order_goods_InventoryVery(String id, OrderForm of) {
		boolean inventory_very = true;
		List<Goods> goodsList = this.queryOfGoods(id);
		for (Goods obj : goodsList) {
			int orderGoodsCount = this.queryOfGoodsCount(id, CommUtil.null2String(obj.getId()));
			String orderGoodsGspIds = "";
			List<Map> goods_maps = this.queryGoodsInfo(of.getGoods_info());
			for (Map obj_map : goods_maps) {
				if (CommUtil.null2String(obj_map.get("goods_id")).equals(obj.getId().toString())) {
					orderGoodsGspIds = CommUtil.null2String(obj_map.get("goods_gsp_ids"));
					break;
				}
			}
			// 真实商品库存
			int realGoodsCount = CommUtil.null2Int(this.generic_default_info(obj, orderGoodsGspIds).get("count")); // 计算商品库存信息
			if (orderGoodsCount > realGoodsCount) {
				inventory_very = false;
				break;
			}
		}
		return inventory_very;
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
			price = CommUtil.mul(rebate, price);
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}

	/**
	 * 验证订单中所有商品是否都存在，全部存在返回true,如果有商品被删除返回false
	 * 
	 * @param id
	 * @return
	 */
	public boolean order_goods_exist(String id) {
		boolean verify = true;
		List<Goods> objs = this.queryOfGoods(id);
		for (Goods obj : objs) {
			if (StringUtils.isNullOrEmpty(obj)) {
				verify = false;
			}
		}
		return verify;
	}

	/**
	 * 计算指定购物车的总金额及满减金额
	 * 
	 * @param carts
	 *            待计算的购物车列表(此参数不能包含组合套装中的非主体商品，否则计算结果会不准确)
	 * @param gcs
	 *            用户指定要计算的购物车的id集（以英文逗号分隔）。若此参数为空或null，则当前待计算的购物车为carts参数中的所有购物车；若为其它非法值（如："-1"）则返回的Map中各value值为空串或空Map或空List
	 *            ，否则，当前待计算的购物车为carts与gcs的交集对应的购物车
	 * @return 若最终待计算的购物车为空，则返回的Map中各value值为空串或空Map或空List。否则返回的Map中：key=carts：最终被计算的有效购物车对象集(购买数量为0的购物车不在此集合中)；key=gcIds:
	 *         对应carts返回值的购物车的id集（以逗号分隔）；key=all:购物车中的商品总金额；key=reduce：购物车中的商品总的满减金额；key=after：购物车中的商品扣除总的满减金额后的商品总金额;key=
	 *         erString:Map类型，为各满减在前台显示的满减提示内容;key=er_json:保存了所有满减的详情(包括满减对应的商品的id集，商品总金额，满减的满的金额和减的金额，各商品购买数量和单价)。
	 *         er_json可以转化为Map对象：key=满减活动的id:对应的商品的id列表；
	 *         key=prices_满减活动的id:对应商品的单价；key=counts_满减活动的id:对应商品的购买数量；key=all_满减活动的id:对应当前满减活动用户的消费总金额；Key=reduce_满减活动的id:
	 *         对应用户的消费总金额而进行的减免金额；key=enouhg_满减活动的id:对应用户的消费总金额而达到的满减的满的金额;key=return_满减活动的id:退货时会用到此key
	 */
	public Map<String, Object> calEnoughReducePrice(List<GoodsCart> carts, String gcs) {
		Map<Long, String> erString = new HashMap<Long, String>();// key：满减活动的id，value：提示用户选择的商品满足哪个满减标准的信息
		double all_price = 0.0; // 商品总价格（未减去满减的金额）
		Map<String, Double> ermap = new HashMap<String, Double>(); // key:满减活动的id，value:用户选择的商品中为当前满减活动的商品的总金额
		Map erid_goodsidsTmp = new HashMap();
		Map erid_goodsids = new HashMap(); // key:满减活动的id，value: 用户选择的商品中为当前满减活动的商品的id列表;
											// 还包括对应满减活动id的all_xxx、reduce_xxx、enouhg_xxx等key
		Map<String, EnoughReduce> ers = new HashMap<String, EnoughReduce>();// key:erId,
																			// value:er对象。此map的作用是避免在数据库中对er对象进行多次查询
		Date now = new Date();
		Map prices = new HashMap();
		StringBuilder sb = new StringBuilder(); // 最终的待计算的所有购物车的id集

		// 选择待计算金额的所有购物车
		List<GoodsCart> toCalCarts = new ArrayList<GoodsCart>();
		if (StringUtils.isNullOrEmpty(gcs)) {
			for (GoodsCart gc : carts) {
				if (gc.getCount() > 0) {
					toCalCarts.add(gc);
					sb.append(gc.getId()).append(",");
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().toString().equals(gc_id)) {
						if (gc.getCount() > 0) {
							toCalCarts.add(gc);
							sb.append(gc_id).append(",");
							break;
						}
					}
				}
			}
		}
		if (toCalCarts.size() == 0) {
			prices.put("gcIds", "");
			prices.put("carts", toCalCarts);
			prices.put("er_json", "");
			prices.put("erString", erString);
			prices.put("all", 0.0); // 商品总价
			prices.put("reduce", 0.0); // 满减价格
			prices.put("after", 0.0); // 减后价格
			return prices;
		} else {
			prices.put("gcIds", sb.toString());
			prices.put("carts", toCalCarts);
		}

		// 计算购物车的商品总金额和满减总金额
		for (GoodsCart gc : toCalCarts) {
			double goodsTotalPrice = CommUtil.mul(gc.getCount(), gc.getPrice());
			all_price = CommUtil.add(all_price, goodsTotalPrice);

			Goods cartGoods = gc.getGoods();
			if (cartGoods.getEnough_reduce() == 1) { // 为满减商品
				String er_id = cartGoods.getOrder_enough_reduce_id();
				EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
				if (er != null && er.getErstatus() == 10 && er.getErbegin_time().before(now)) {
					ers.put(er_id, er);

					if (ermap.containsKey(er_id)) {
						double last_price = (double) ermap.get(er_id);
						ermap.put(er_id, CommUtil.add(last_price, goodsTotalPrice));

						((List) erid_goodsidsTmp.get(er_id)).add(cartGoods.getId());
						((List) erid_goodsidsTmp.get("prices_" + er_id)).add(gc.getPrice());
						((List) erid_goodsidsTmp.get("counts_" + er_id)).add(gc.getCount());
					} else {
						ermap.put(er_id, goodsTotalPrice);

						List idlist = new ArrayList();
						idlist.add(cartGoods.getId());
						erid_goodsidsTmp.put(er_id, idlist);

						List pricelist = new ArrayList();
						pricelist.add(gc.getPrice());
						erid_goodsidsTmp.put("prices_" + er_id, pricelist);// 对应idlist的各商品的单价

						List countlist = new ArrayList();
						countlist.add(gc.getCount());
						erid_goodsidsTmp.put("counts_" + er_id, countlist);// 对应idlist的各商品的购买数量
					}
				}
			}
		}

		double all_enough_reduce = 0.0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = ers.get(er_id);
			Map<String, Double> fromJson = (Map<String, Double>) Json.fromJson(er.getEr_json());
			double er_money = ermap.get(er_id); // 购物车中对应此满减活动的商品的总金额
			double reduce = 0.0;
			String erstr = "";
			double currentEnough = 0.0;
			for (String enough : fromJson.keySet()) {
				double en = CommUtil.null2Double(enough);
				if (er_money >= en && currentEnough < en) { // 匹配满足条件的最大面额的券
					reduce = fromJson.get(enough);
					erstr = "活动商品已购满" + enough + "元,已减" + reduce + "元";
					erid_goodsids.put("enouhg_" + er_id, enough);
					currentEnough = en;
				}
			}
			if (!"".equals(erstr)) {
				erString.put(er.getId(), erstr);
				erid_goodsids.put("all_" + er_id, er_money); // 针对当前满减活动，用户消费的商品总金额
				erid_goodsids.put("reduce_" + er_id, reduce); // 满减的金额
				erid_goodsids.put("return_" + er_id, 0.0); // 满减已退金额
				erid_goodsids.put(er_id, erid_goodsidsTmp.get(er_id));
				erid_goodsids.put("prices_" + er_id, erid_goodsidsTmp.get("prices_" + er_id));
				erid_goodsids.put("counts_" + er_id, erid_goodsidsTmp.get("counts_" + er_id));

				all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
			}
		}

		prices.put("er_json", Json.toJson(erid_goodsids, JsonFormat.compact()));
		prices.put("erString", erString);

		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("all", CommUtil.null2Double(bd2)); // 商品总价

		double er = Math.round(all_enough_reduce * 100) / 100.0;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("reduce", CommUtil.null2Double(erbd2)); // 满减价格

		double af = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("after", CommUtil.null2Double(afbd2)); // 减后价格

		return prices;
	}

	/**
	 * 计算购物车中所有商品的总金额（若满足满减条件，则会减去满减的金额）。注：此总价格已扣除了满减金额（若满足满减条件的话）。此函数的功能是OrderForm中的calEnoughReducePrice函数子功能
	 * 
	 * @param carts
	 *            所有购物车对象，请保证购物车中的商品的状态都是正常的。本函数不对商品状态进行校验
	 * @param gcs
	 *            指定购物车的id列表，以逗号连接。若gcs为空，则计算carts中所有商品的总价格；若gcs不为空，则计算carts与gcs的交集部分的商品的总价格。
	 * @return 返回商品总金额
	 */
	public double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0; // 商品总价格（未减去满减的金额）
		Map<String, Double> ermap = new HashMap<String, Double>(); // key:满减活动的id，value:用户选择的商品中为当前满减活动的商品的总金额
		Map<String, EnoughReduce> ers = new HashMap<String, EnoughReduce>();// key:erId,
																			// value:er对象。此map的作用是避免在数据库中对er对象进行多次查询
		Date now = new Date();

		// 选择待计算金额的所有购物车
		List<GoodsCart> toCalCarts = new ArrayList<GoodsCart>();
		if (StringUtils.isNullOrEmpty(gcs)) {
			toCalCarts.addAll(carts);
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().toString().equals(gc_id)) {
						toCalCarts.add(gc);
						break;
					}
				}
			}

		}

		// 计算购物车的商品总金额和满减总金额
		for (GoodsCart gc : toCalCarts) {
			double goodsTotalPrice = CommUtil.mul(gc.getCount(), gc.getPrice());
			all_price = CommUtil.add(all_price, goodsTotalPrice);

			Goods cartGoods = gc.getGoods();
			if (cartGoods.getEnough_reduce() == 1) { // 为满减商品
				String er_id = cartGoods.getOrder_enough_reduce_id();
				EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
				if (er != null && er.getErstatus() == 10 && er.getErbegin_time().before(now)) {
					ers.put(er_id, er);

					if (ermap.containsKey(er_id)) {
						ermap.put(er_id, CommUtil.add((double) ermap.get(er_id), goodsTotalPrice));
					} else {
						ermap.put(er_id, goodsTotalPrice);
					}
				}
			}
		}

		double all_enough_reduce = 0.0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = ers.get(er_id);
			Map<String, Double> fromJson = (Map<String, Double>) Json.fromJson(er.getEr_json());
			double er_money = ermap.get(er_id); // 购物车中对应此满减活动的商品的总金额
			double reduce = 0.0;
			double currentEnough = 0.0;
			for (String enough : fromJson.keySet()) {
				double en = CommUtil.null2Double(enough);
				if (er_money >= en && currentEnough < en) { // 匹配满足条件的最大面额的券
					reduce = fromJson.get(enough);
					currentEnough = en;
				}
			}
			all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
		}

		double af = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);

		return CommUtil.null2Double(CommUtil.formatMoney(afbd2));
	}

	/**
	 * 计算购物车中所有商品的总金额（不会减去满减的金额，只是简单地数量与单价的乘积之和）。
	 * 
	 * @param toCalCarts
	 *            所有购物车对象，请保证购物车中的商品的状态都是正常的。本函数不对商品状态进行校验
	 * @param cartGoodsCounts
	 *            对应carts，表示对应的购物车中的商品数量中有多少个是要在此次计算中被计算在内的。若为null或size()==
	 *            0则表示要购买的商品数量即为各购物车中的Count字段的值。否则cartGoodsCounts的size()必须与toCalCarts的size()相等
	 * @return 返回商品总金额
	 */
	public double calCartTotalGoodsAmount(List<GoodsCart> toCalCarts, List<Integer> cartGoodsCounts) {
		double all_price = 0.0; // 商品总价格

		// 计算购物车的商品总金额
		if (cartGoodsCounts == null || cartGoodsCounts.size() == 0) {
			for (GoodsCart gc : toCalCarts) {
				double goodsTotalPrice = CommUtil.mul(gc.getCount(), gc.getPrice());
				all_price = CommUtil.add(all_price, goodsTotalPrice);
			}
		} else {
			int i = 0;
			for (GoodsCart gc : toCalCarts) {
				double goodsTotalPrice = CommUtil.mul(cartGoodsCounts.get(i++), gc.getPrice());
				all_price = CommUtil.add(all_price, goodsTotalPrice);
			}
		}

		BigDecimal afbd = new BigDecimal(all_price);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);

		return CommUtil.null2Double(CommUtil.formatMoney(afbd2));
	}
	
	public Map getDiscountAmounts(Long orderId){
		OrderForm of = orderFormService.getObjById(orderId);
		return orderFormService.getDiscountAmounts(of);
	}
}
