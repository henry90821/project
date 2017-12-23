package com.iskyshop.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: OrderBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家订单控制类，用来操作取消订单、查看订单、订单付款、物流查询、订单评价等操作；
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
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class OrderBuyerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private ShipTools shipTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private FTPServerTools ftpServerTools;
	@Autowired
	private PaymentTools paymentTools;

	@SecurityMapping(title = "买家订单列表", value = "/buyer/order.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order.htm")
	public ModelAndView order(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String order_status) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("obj.order_id in(")
		 		 .append(" select distinct b.mainOrderId from OrderForm b where b.user_id =:user_id and b.order_cat !=:order_cat");
		Map params = new HashMap();
		params.put("user_id", user.getId().toString());
		params.put("order_cat", 2);
		if (!"".equals(CommUtil.null2String(order_id))) {
			params.put("order_id", "%" + order_id + "%");
			sqlBuffer.append(" and b.order_id like :order_id ");
			mv.addObject("order_id", order_id);
		}
		if (!"".equals(CommUtil.null2String(beginTime))) {
			params.put("beginTime", CommUtil.formatDate(beginTime));
			sqlBuffer.append(" and b.addTime >=:beginTime");
			mv.addObject("beginTime", beginTime);
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			params.put("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss"));
			sqlBuffer.append(" and b.addTime <=:endTime");
			mv.addObject("endTime", endTime);
		}
		if (!"".equals(CommUtil.null2String(order_status))) {
			params.put("order_status",CommUtil.null2Int(order_status));
			sqlBuffer.append(" and b.order_status =:order_status");
			mv.addObject("order_status", order_status);
		}
		sqlBuffer.append(") ");
		ofqo.addQuery(sqlBuffer.toString(), params);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		// 查询订单信息
		int[] status = new int[] { 10, 30, 50 }; // 已提交 已发货 已完成
		String[] string_status = new String[] { "10", "30", "50" };
		Map orders_status = new LinkedHashMap();
		for (int i = 0; i < status.length; i++) {
			int size = this.orderFormService.query(
					"select obj.id from OrderForm obj where obj.order_main=1 and obj.user_id=" + user.getId().toString()
							+ " and obj.order_status =" + status[i] + "", null, -1, -1).size();
			mv.addObject("order_size_" + status[i], size);
			orders_status.put(string_status[i], size);
		}
		mv.addObject("orders_status", orders_status);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("goodscookie".equals(cookie.getName())) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					your_like_goods = this.goodsService.query(
							"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = " + your_like_GoodsClass
									+ " and obj.id is not " + goods.getId() + " order by obj.goods_salenum desc", null, 0,
							20);
					int gcs_size = your_like_goods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.id is not " + goods.getId()
										+ " order by obj.goods_salenum desc", null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < your_like_goods.size(); j++) {
								if (like_goods.get(i).getId().equals(your_like_goods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								your_like_goods.add(like_goods.get(i));
							}
						}
					}
					break;
				} else {
					your_like_goods = this.goodsService.query(
							"select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc", null, 0,
							20);
				}
			}
		} else {
			your_like_goods = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc", null, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);
		mv.addObject("paymentTools", paymentTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/buyer/order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_cancel.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消确定", value = "/buyer/order_cancel_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cancel_save.htm")
	public String order_cancel_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		List<OrderForm> objs = new ArrayList<OrderForm>();
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		objs.add(obj);
		boolean all_verify = true;
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_main() == 1 && obj.getChild_order_detail() != null) {
				List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
				if (maps != null) {
					for (Map map : maps) {
						logger.info(map.get("order_id"));
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
						objs.add(child_order);
					}
				}
			}

			for (OrderForm of : objs) {
				if (of.getOrder_status() >= 20) {
					all_verify = false;
				}
			}
		}
		if (all_verify) {
			if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				if (obj.getOrder_main() == 1) {
					List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
					if (maps != null) {
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(map.get("order_id")));
							child_order.setOrder_status(0);
							this.orderFormService.update(child_order);
							if (obj.getOrder_form() == 0) {
								Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
								Map map2 = new HashMap();
								if (store != null) {
									map2.put("seller_id", store.getUser().getId().toString());
								}
								map2.put("order_id", obj.getId().toString());
								this.msgTools.sendEmailCharge(CommUtil.getURL(request),
										"email_toseller_order_cancel_notify", store.getUser().getEmail(), map2, null,
										CommUtil.null2String(store.getId()));
								this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_cancel_notify",
										store.getUser().getMobile(), map2, null, CommUtil.null2String(store.getId()));
							}

						}
					}
				}
				int old_status = obj.getOrder_status();
				obj.setOrder_status(0);
				this.orderFormService.update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("取消订单。原订单状态为：" + old_status);
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				if ("other".equals(state_info)) {
					ofl.setState_info(other_state_info);
				} else {
					ofl.setState_info(state_info);
				}
				this.orderFormLogService.save(ofl);
				Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
				Map map = new HashMap();
				if (store != null) {
					map.put("seller_id", store.getUser().getId().toString());
				}
				map.put("order_id", obj.getId().toString());
				if (obj.getOrder_form() == 0) {
					this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_order_cancel_notify", store
							.getUser().getEmail(), map, null, CommUtil.null2String(store.getId()));
					this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_cancel_notify", store
							.getUser().getMobile(), map, null, CommUtil.null2String(store.getId()));
				}
				//还原商品库存
				this.goodsService.recover_goods_inventory(obj, null);
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	/**
	 * 买家打开收货确认对话框
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "收货确认", value = "/buyer/order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cofirm.htm")
	public ModelAndView order_cofirm(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_cofirm.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()))) {
			mv.addObject("obj", obj);
			mv.addObject("child_order", !"".equals(CommUtil.null2String(obj.getChild_order_detail())));
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	/**
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润， 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "收货确认保存", value = "/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cofirm_save.htm")
	public String order_cofirm_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
			throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()))) {
			obj.setOrder_status(40);
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
			obj.setReturn_shipTime(ca.getTime());
			obj.setConfirmTime(new Date()); // 设置确认收货时间
			boolean ret = this.orderFormService.update(obj);
			if (obj.getPayType() != null && "payafter".equals(obj.getPayType())) { // 如果买家支付方式为货到付款，买家确认收货时更新商品库存
				this.update_goods_inventory(obj); // 更新商品库存
			}
			if (ret) { // 订单状态更新成功，更新相关信息
				/**modify by liz 20151130 修改为确认收货不需要操作子订单**/
				/*if (obj.getOrder_main() == 1 && !"".equals(CommUtil.null2String(obj.getChild_order_detail()))) { // 更新子订单状态信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
						child_order.setOrder_status(40);
						child_order.setReturn_shipTime(ca.getTime());
						child_order.setConfirmTime(new Date()); // 设置确认收货时间
						this.orderFormService.update(child_order);
						if ("payafter".equals(obj.getPayment().getMark())) { // 如果买家支付方式为货到付款，买家确认收货，子订单商品销量增加
							List<Map> goods_map = this.orderFormTools.queryGoodsInfo(child_order.getGoods_info());
							for (Map child_map : goods_map) {
								Goods goods = this.goodsService.getObjById(CommUtil.null2Long(child_map.get("goods_id")));
								goods.setGoods_salenum(goods.getGoods_salenum()
										+ CommUtil.null2Int(child_map.get("goods_count"))); // 增加商品销量
								GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
								todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum()
										+ CommUtil.null2Int(child_map.get("goods_count")));
								Map<String, Integer> logordermap = (Map<String, Integer>) Json.fromJson(todayGoodsLog
										.getGoods_order_type());
								String ordertype = child_order.getOrder_type();
								if (logordermap.containsKey(ordertype)) {
									logordermap.put(ordertype,
											logordermap.get(ordertype) + CommUtil.null2Int(child_map.get("goods_count")));
								} else {
									logordermap.put(ordertype, CommUtil.null2Int(child_map.get("goods_count")));
								}
								todayGoodsLog.setGoods_order_type(Json.toJson(logordermap, JsonFormat.compact()));

								Map<String, Integer> logspecmap = (Map<String, Integer>) Json.fromJson(todayGoodsLog
										.getGoods_sale_info());
								String spectype = child_map.get("goods_gsp_val").toString();
								if (logspecmap.containsKey(spectype)) {
									logspecmap.put(spectype,
											logspecmap.get(spectype) + CommUtil.null2Int(child_map.get("goods_count")));
								} else {
									logspecmap.put(spectype, CommUtil.null2Int(child_map.get("goods_count")));
								}
								todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));

								this.goodsLogService.update(todayGoodsLog);

								goods.setGoods_inventory(goods.getGoods_inventory()
										- CommUtil.null2Int(child_map.get("goods_count"))); // 库存减少
								if (goods.getGroup_buy() == 2) { // 如果为团购商品，增加团购销量,减少团购库存
									for (GroupGoods gg : goods.getGroup_goods_list()) {
										if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
											gg.setGg_selled_count(CommUtil.null2Int(gg.getGg_selled_count()
													+ CommUtil.null2Int(map.get("goods_count")))); // 增加团购销量
											if (gg.getGg_count() - CommUtil.null2Int(map.get("goods_count")) > 0) { // 减少团购库存
												gg.setGg_count(gg.getGg_count() - CommUtil.null2Int(map.get("goods_count")));
											} else {
												gg.setGg_count(0);
											}
											this.ggService.update(gg);
										}
									}
								}
								this.goodsService.update(goods);
							}
						}
						// 向子订单商家发送提醒信息，同时生成结算日志，如果子订单为平台自营，则不发送短信和邮件,
						if (child_order.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
							Map json_map = new HashMap();
							json_map.put("seller_id", store.getUser().getId().toString());
							json_map.put("childorder_id", child_order.getId().toString());
							String json = Json.toJson(json_map);
							if (obj.getOrder_form() == 0) {
								this.msgTools.sendEmailCharge(CommUtil.getURL(request),
										"email_toseller_order_receive_ok_notify", store.getUser().getEmail(), json, null,
										obj.getStore_id());
								this.msgTools.sendSmsCharge(CommUtil.getURL(request),
										"sms_toseller_order_receive_ok_notify", store.getUser().getMobile(), json, null,
										obj.getStore_id());
							}
							// 订单生成商家结算日志
							PayoffLog plog = new PayoffLog();
							if (obj.getPayType() != null && "payafter".equals(obj.getPayType())) {
								plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
										+ store.getUser().getId());
								plog.setPl_info("货到付款");
								plog.setAddTime(new Date());
								plog.setSeller(store.getUser());
								plog.setO_id(CommUtil.null2String(child_order.getId()));
								plog.setOrder_id(child_order.getOrder_id().toString());
								plog.setCommission_amount(child_order.getCommission_amount()); // 该订单总佣金费用
								plog.setGoods_info(child_order.getGoods_info());
								plog.setOrder_total_price(child_order.getGoods_amount()); // 该订单总商品金额
								plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
										CommUtil.subtract(0, child_order.getCommission_amount()),
										child_order.getShip_price()))); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
							} else {
								plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
										+ store.getUser().getId());
								plog.setPl_info("确认收货");
								plog.setAddTime(new Date());
								plog.setSeller(store.getUser());
								plog.setO_id(CommUtil.null2String(child_order.getId()));
								plog.setOrder_id(child_order.getOrder_id().toString());
								plog.setCommission_amount(child_order.getCommission_amount()); // 该订单总佣金费用
								plog.setGoods_info(child_order.getGoods_info());
								plog.setOrder_total_price(child_order.getGoods_amount()); // 该订单总商品金额
								plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
										CommUtil.subtract(child_order.getGoods_amount(), child_order.getCommission_amount()),
										child_order.getShip_price()))); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
							}
							this.payoffLogservice.save(plog);
							store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(child_order.getGoods_amount(),
									store.getStore_sale_amount()))); // 店铺本次结算总销售金额
							store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(
									child_order.getCommission_amount(), store.getStore_commission_amount()))); // 店铺本次结算总佣金
							store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),
									store.getStore_payoff_amount()))); // 店铺本次结算总佣金
							this.storeService.update(store);
							// 增加系统总销售金额、总佣金
							SysConfig sc = this.configService.getSysConfig();
							sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(child_order.getGoods_amount(),
									sc.getPayoff_all_sale())));
							sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(child_order.getCommission_amount(),
									sc.getPayoff_all_commission())));
							this.configService.update(sc);
						}
					}
				}*/
				/**modify by liz 20151130 修改为确认收货不需要操作子订单end**/
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				// 主订单生成商家结算日志
				if (obj.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					if (obj.getPayType() != null && "payafter".equals(obj.getPayType())) {
						plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						plog.setPl_info("货到付款");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount()); // 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getTotalPrice().subtract(obj.getShip_price())); // 该订单总商品金额
						plog.setTotal_amount(this.orderFormService.getPayoffAmount(obj)); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
						plog.setShip_price(obj.getShip_price()); //该订单运费
					} else {
						plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						plog.setPl_info("确认收货");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount()); // 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getTotalPrice().subtract(obj.getShip_price())); // 该订单总商品金额
						plog.setTotal_amount(this.orderFormService.getPayoffAmount(obj)); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
						plog.setShip_price(obj.getShip_price()); //该订单运费
					}
					this.payoffLogservice.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(),
							store.getStore_sale_amount()))); // 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),
							store.getStore_commission_amount()))); // 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),
							store.getStore_payoff_amount()))); // 店铺本次结算总佣金
					this.storeService.update(store);
					// 增加系统总销售金额、总佣金
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),
							sc.getPayoff_all_commission())));
					this.configService.update(sc);
				}
			}
		}
		String url = "redirect:order.htm?currentPage=" + currentPage;
		return url;
	}

	@SecurityMapping(title = "买家评价", value = "/buyer/order_evaluate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_evaluate.htm")
	public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_evaluate.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
			String evaluate_session = CommUtil.randomString(32);
			request.getSession(true).setAttribute("evaluate_session", evaluate_session);
			mv.addObject("evaluate_session", evaluate_session);
			if (obj.getOrder_status() >= 50) {
				mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}

	@SecurityMapping(title = "买家评价图片上传", value = "/buyer/upload_evaluate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/upload_evaluate.htm")
	public void upload_evaluate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		Map json_map = new HashMap();
		SysConfig config = this.configService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map params = new HashMap();
		params.put("userid", user.getId());
		params.put("info", "eva_temp");
		List<Accessory> imglist = this.accessoryService.query(
				"select obj from Accessory obj where obj.user.id=:userid and obj.info=:info", params, -1, -1);
		if (imglist.size() > 40) { // 每个用户最多可以上传40张临时图片，半小时清理一次
			json_map.put("ret", false);
			json_map.put("msg", "您最近上传过多图片，请稍后重试");
			response.setContentType("text/plain");
			response.getWriter().print(Json.toJson(json_map));
		} else {
			json_map.put("ret", true);
			try {
				// <1>. 判断路径是否存在,若不存在则创建路径
				String goods_id = CommUtil.null2String(request.getParameter("goods_id"));
				String filePath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
				CommUtil.createFolder(filePath);
				Map map = CommUtil.saveFileToServer(request, goods_id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("eva_temp");
				String url = this.ftpServerTools
						.userUpload(image.getName(), "/evaluate", CommUtil.null2String(user.getId()));
				image.setPath(url);
				this.accessoryService.save(image);
				json_map.put("url", url + "/" + image.getName());
				json_map.put("id", image.getId());
				json_map.put("goods_id", goods_id);
				response.setContentType("text/plain");
				response.getWriter().print(Json.toJson(json_map));
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	@SecurityMapping(title = "买家评价保存", value = "/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_evaluate_save.htm")
	public ModelAndView order_evaluate_save(HttpServletRequest request, HttpServletResponse response, String id,
			String evaluate_session) throws Exception {

		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		String evaluate_session1 = (String) request.getSession(true).getAttribute("evaluate_session");
		if (evaluate_session1 != null && evaluate_session1.equals(evaluate_session)) {
			request.getSession(true).removeAttribute("evaluate_session");
			if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				if (obj.getOrder_status() == 40) {
					List<Map> json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
					for (Map map : json) {
						map.put("orderForm", obj.getId());
					}
					/**modify by liz 20151130 评价修改为只修改本订单的评价，不对子订单进行评价*/
					/*List<Map> child_order = this.orderFormTools.queryGoodsInfo(obj.getChild_order_detail());
					List<Map> child_goods = new ArrayList<Map>();
					for (Map c : child_order) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(c.get("order_goods_info").toString());
						for (Map cmap : maps) {
							cmap.put("orderForm", c.get("order_id"));
						}
						child_goods.addAll(maps);
					}
					if (child_goods.size() > 0) {
						json.addAll(child_goods);
					}*/
					/**modify by liz 20151130 评价修改为只修改本订单的评价，不对子订单进行评价 end*/
					for (Map map : json) {
						String goods_id = map.get("goods_id").toString();
						int description = eva_rate(request.getParameter("description_evaluate" + goods_id));
						int service = eva_rate(request.getParameter("service_evaluate" + goods_id));
						int ship = eva_rate(request.getParameter("ship_evaluate" + goods_id));
						int total = eva_total_rate(request.getParameter("evaluate_buyer_val" + goods_id));
						if (description == 0 || service == 0 || ship == 0 || total == -5) {
							ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", "参数错误，禁止评价");
							mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
							return mv;
						}
					}
					obj.setOrder_status(50);
					obj.setFinishTime(new Date());
					this.orderFormService.update(obj);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("评价订单");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(obj);
					this.orderFormLogService.save(ofl);					

					for (Map map : json) {
						Evaluate eva = new Evaluate();
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
						eva.setAddTime(new Date());
						eva.setEvaluate_goods(goods);
						eva.setGoods_num(CommUtil.null2Int(map.get("goods_count")));
						eva.setGoods_price(map.get("goods_price").toString());
						eva.setGoods_spec(map.get("goods_gsp_val").toString());
						eva.setEvaluate_info(request.getParameter("evaluate_info_" + goods.getId()));
						eva.setEvaluate_photos(request.getParameter("evaluate_photos_" + goods.getId()));
						int eva_val = eva_total_rate(request.getParameter("evaluate_buyer_val" + goods.getId()));
						eva.setEvaluate_buyer_val(CommUtil.null2Int(eva_val));
						int desc_eva_val = eva_rate(request.getParameter("description_evaluate" + goods.getId()));
						eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(desc_eva_val)));
						eva.setService_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request
								.getParameter("service_evaluate" + goods.getId())))));
						eva.setShip_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request
								.getParameter("ship_evaluate" + goods.getId())))));
						eva.setEvaluate_type("goods");
						eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
						//542 前台：个人中心-我的评价追加评论失败 原因是因为没有添加交易完成时间，导致追加和修改评价是出错
						OrderForm ord = this.orderFormService.getObjById(CommUtil.null2Long(map.get("orderForm")));
						ord.setFinishTime(new Date());
						this.orderFormService.update(ord);
						eva.setOf(ord);
						eva.setReply_status(0);
						this.evaluateService.save(eva);
						//保存商品中的评价相关信息
						BigDecimal eva_count = BigDecimal.valueOf(goods.getEvaluate_count());
                        BigDecimal desc_eva_total = goods.getDescription_evaluate().multiply(eva_count);
						eva_count = eva_count.add(BigDecimal.valueOf(1));
						
						goods.setEvaluate_count(eva_count.intValue());
						goods.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(desc_eva_total.add(BigDecimal.valueOf(desc_eva_val)), eva_count)));
						if(eva_val == 1) {//好评
							goods.setWell_evaluate(goods.getWell_evaluate() + 1);
						} else if(eva_val == 0) {//中评
							goods.setMiddle_evaluate(goods.getMiddle_evaluate() + 1);
						} else {//差评
							goods.setBad_evaluate(goods.getBad_evaluate() + 1);
						}						
						
						String im_str = request.getParameter("evaluate_photos_" + goods.getId());
						if (!StringUtils.isNullOrEmpty(im_str) && im_str.length() > 0) {
							for (String str : im_str.split(",")) {
								if (!StringUtils.isNullOrEmpty(str)) {
									Accessory image = this.accessoryService.getObjById(CommUtil.null2Long(str));
									if ("eva_temp".equals(image.getInfo())) {
										image.setInfo("eva_img");
										this.accessoryService.save(image);
									}
								}
							}
						}

						Map params = new HashMap();
						if (goods.getGoods_type() == 1) {
							Store store = this.storeService.getObjById(CommUtil.null2Long(goods.getGoods_store().getId()));
							params.put("store_id", store.getId().toString());
							List<Evaluate> evas = this.evaluateService.query(
									"select obj from Evaluate obj where obj.of.store_id=:store_id", params, -1, -1);
							double store_evaluate = 0;
							double description_evaluate = 0;
							double description_evaluate_total = 0;
							double service_evaluate = 0;
							double service_evaluate_total = 0;
							double ship_evaluate = 0;
							double ship_evaluate_total = 0;
							DecimalFormat df = new DecimalFormat("0.0");
							for (Evaluate eva1 : evas) {
								description_evaluate_total = description_evaluate_total
										+ CommUtil.null2Double(eva1.getDescription_evaluate());
								service_evaluate_total = service_evaluate_total
										+ CommUtil.null2Double(eva1.getService_evaluate());
								ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
							}
							description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
							service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
							ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
							store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3; // 综合评分=三项具体评分之和/3
							store.setStore_credit(store.getStore_credit() + eva.getEvaluate_buyer_val());
							this.storeService.update(store);
							params.clear();
							params.put("store_id", store.getId());
							List<StorePoint> sps = this.storePointService.query(
									"select obj from StorePoint obj where obj.store.id=:store_id", params, -1, -1);
							StorePoint point = null;
							if (sps.size() > 0) {
								point = sps.get(0);
							} else {
								point = new StorePoint();
							}
							point.setAddTime(new Date());
							point.setStore(store);
							point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
							point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
							point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
							point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
							if (sps.size() > 0) {
								this.storePointService.update(point);
							} else {
								this.storePointService.save(point);
							}
						} else {
							User sp_user = this.userService.getObjById(obj.getEva_user_id());
							params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
							List<Evaluate> evas = this.evaluateService.query(
									"select obj from Evaluate obj where obj.of.user_id=:user_id", params, -1, -1);
							double store_evaluate = 0;
							double description_evaluate = 0;
							double description_evaluate_total = 0;
							double service_evaluate = 0;
							double service_evaluate_total = 0;
							double ship_evaluate = 0;
							double ship_evaluate_total = 0;
							DecimalFormat df = new DecimalFormat("0.0");
							for (Evaluate eva1 : evas) {
								description_evaluate_total = description_evaluate_total
										+ CommUtil.null2Double(eva1.getDescription_evaluate());
								service_evaluate_total = service_evaluate_total
										+ CommUtil.null2Double(eva1.getService_evaluate());
								ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
							}
							description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
							service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
							ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
							store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3;
							params.clear();
							params.put("user_id", obj.getEva_user_id());
							List<StorePoint> sps = this.storePointService.query(
									"select obj from StorePoint obj where obj.user.id=:user_id", params, -1, -1);
							StorePoint point = null;
							if (sps.size() > 0) {
								point = sps.get(0);
							} else {
								point = new StorePoint();
							}
							point.setAddTime(new Date());
							point.setUser(sp_user);
							point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate > 5 ? 5
									: description_evaluate));
							point.setService_evaluate(BigDecimal.valueOf(service_evaluate > 5 ? 5 : service_evaluate));
							point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate > 5 ? 5 : ship_evaluate));
							point.setStore_evaluate(BigDecimal.valueOf(store_evaluate > 5 ? 5 : store_evaluate));
							if (sps.size() > 0) {
								this.storePointService.update(point);
							} else {
								this.storePointService.save(point);
							}
						}
						this.goodsService.update(goods);
						
						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
								+ "goods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
						
						User user = this.userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
						// 增加评价积分
						user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getIndentComment());
						// 增加用户消费金额
						user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(user.getUser_goods_fee(), obj.getTotalPrice())));

						this.userService.update(user);
						// 记录积分明细
						if (this.configService.getSysConfig().isIntegral()) {
							IntegralLog log = new IntegralLog();
							log.setAddTime(new Date());
							log.setContent("订单评价增加" + this.configService.getSysConfig().getIndentComment() + "分");
							log.setIntegral(this.configService.getSysConfig().getIndentComment());
							log.setIntegral_user(user);
							log.setType("order");
							this.integralLogService.save(log);
						}
					}
				}
				if (this.configService.getSysConfig().isEmailEnable()) {
					if (obj.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
						Map map = new HashMap();
						map.put("seller_id", store.getUser().getId().toString());
						map.put("order_id", obj.getId().toString());
						this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_evaluate_ok_notify", store
								.getUser().getEmail(), map, null, obj.getStore_id());
					}
				}
			}
			
			ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "订单评价成功");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "禁止重复评价!");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			return mv;
		}

	}

	@SecurityMapping(title = "删除订单信息", value = "/buyer/order_delete.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_delete.htm")
	public String order_delete(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
			throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_status() == 0) {
				if (obj.getOrder_main() == 1 && obj.getOrder_cat() == 0) {
					if (!StringUtils.isNullOrEmpty(obj.getChild_order_detail())) {
						List<Map> maps = (List<Map>) Json.fromJson(obj.getChild_order_detail());
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(map.get("order_id")));
							child_order.setOrder_status(0);
							this.orderFormService.delete(child_order.getId());
						}
					}
				}				
			
				this.orderFormService.delete(obj.getId());
				
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("删除订单(" + obj.getId() + ")。原订单状态为：" + obj.getOrder_status());
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(null);	
				this.orderFormLogService.save(ofl);				
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}
	
	@SecurityMapping(title = "买家订单详情", value = "/buyer/order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id,String type) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("paymentTools", paymentTools);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_cat() == 1) { // 手机充值订单
				mv = new JModelAndView("user/default/usercenter/recharge_order_view.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("paymentTools", paymentTools);
			}
			boolean query_ship = false; // 是否查询物流
			if (!StringUtils.isNullOrEmpty(obj.getShipCode())) {
				query_ship = true;
			}
			/*String of_ids = "," + obj.getId();
			if (obj.getOrder_main() == 1 && !StringUtils.isNullOrEmpty(CommUtil.null2String(obj.getChild_order_detail()))) { // 子订单中有商家已经发货，也需要显示
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getChild_order_detail());
				for (Map child_map : maps) {
					of_ids += "," + child_map.get("order_id");
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					if (!"".equals(CommUtil.null2String(child_order.getShipCode()))) {
						query_ship = true;
						break;
					}
				}
			}*/
			
			//add by dengyuqi 2015-11-2 新增显示子订单操作日志
			if(obj.getOrder_main() == 0){
				String sql = "select obj from OrderFormLog obj where obj.of.order_id=:mainOrderId order by obj.addTime";
				Map map = new HashMap();
				map.put("mainOrderId", obj.getMainOrderId());
				List<OrderFormLog> orderFormLogs = orderFormLogService.query(sql,map,0,Integer.MAX_VALUE);
				obj.setOfls(orderFormLogs);
			}
			//end by dengyuqi
			
			mv.addObject("obj", obj);
			mv.addObject("shipTools", shipTools);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("query_ship", query_ship);
			/*List<OrderFormLog> ofls = this.orderFormLogService.query(
					"select obj from OrderFormLog obj where obj.of.id in (" + of_ids.substring(1) +") order by obj.addTime asc", null, -1, -1);
			mv.addObject("ofls", ofls);*/
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		mv.addObject("type", type);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		return mv;
	}

	@SecurityMapping(title = "买家物流详情", value = "/buyer/ship_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_ship_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (!StringUtils.isNullOrEmpty(order)) {
			if (order.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
				TransInfo transInfo = this.orderFormTools.query_ship_getData(id);
				transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(order.getExpress_info(),
						"express_company_name"));
				transInfo.setExpress_ship_code(order.getShipCode());
				transInfo_list.add(transInfo);
				if (order.getOrder_main() == 1 && !StringUtils.isNullOrEmpty(order.getChild_order_detail())) { // 查询子订单的物流跟踪信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
								.get("order_id")));
						if (child_order.getExpress_info() != null) {
							TransInfo transInfo1 = this.orderFormTools.query_ship_getData(CommUtil.null2String(child_order.getId()));
							transInfo1.setExpress_company_name(this.orderFormTools.queryExInfo(
									child_order.getExpress_info(), "express_company_name"));
							transInfo1.setExpress_ship_code(child_order.getShipCode());
							transInfo_list.add(transInfo1);
						}
					}
				}
				mv.addObject("transInfo_list", transInfo_list);
				mv.addObject("obj", order);
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "您查询的物流不存在！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "物流跟踪查询", value = "/buyer/query_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/query_ship.htm")
	public ModelAndView query_ship(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/query_ship_data.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		TransInfo info = this.orderFormTools.query_ship_getData(id);
		mv.addObject("transInfo", info);
		return mv;
	}

	@SecurityMapping(title = "虚拟商品信息", value = "/buyer/order_seller_intro.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_seller_intro.htm")
	public ModelAndView order_seller_intro(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_seller_intro.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("obj", obj);
		}
		return mv;
	}

	@SecurityMapping(title = "买家退货申请", value = "/buyer/order_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply.htm")
	public ModelAndView order_return_apply(HttpServletRequest request, HttpServletResponse response, String id, String oid,
			String goods_gsp_ids,String order_id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_return_apply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(oid));
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			for (Map m : maps) {
				if (CommUtil.null2String(m.get("goods_id")).equals(id)) {
					mv.addObject("return_count", m.get("goods_count"));
					mv.addObject("goods", goods);
					if ("5".equals(CommUtil.null2String(m.get("goods_return_status")))) {
						mv.addObject("view", true);
						List<Map> return_maps = this.orderFormTools.queryGoodsInfo(obj.getReturn_goods_info());
						for (Map map : return_maps) {
							if (CommUtil.null2String(map.get("return_goods_id")).equals(id)) {
								mv.addObject("return_content", map.get("return_goods_content"));
							}
						}
					}
				}
			}
		}
		mv.addObject("oid", oid);
		mv.addObject("goods_gsp_ids", goods_gsp_ids);
		mv.addObject("order_id",order_id);
		return mv;
	}

	@SecurityMapping(title = "买家退货申请保存", value = "/buyer/order_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply_save.htm")
	public String order_return_apply_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String return_goods_content, String goods_id, String return_goods_count, String goods_gsp_ids,String order_id)
			throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId().toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
			}
		}
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()) && goods != null) {
			List<Map> list = new ArrayList<Map>();
			Map json = new HashMap();
			json.put("return_goods_id", goods.getId());
			json.put("return_goods_content", CommUtil.filterHTML(return_goods_content));
			json.put("return_goods_count", return_goods_count);
			json.put("return_goods_price", goods.getStore_price());
			json.put("return_goods_commission_rate", goods.getGc().getCommission_rate());
			json.put("return_order_id", id);
			json.put("return_goods_name", goods.getGoods_name()); //增加退货商品名称
			list.add(json);
			obj.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(goods_id)
						&& goods_gsp_ids.equals(StringUtils.isNullOrEmpty(m.get("goods_gsp_ids")) ? "" : m.get(
								"goods_gsp_ids").toString())) {
					m.put("goods_return_status", 5);
					gls.putAll(m);
				}
			}
			obj.setGoods_info(Json.toJson(maps, JsonFormat.compact()));
			this.orderFormService.update(obj);
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			// 生成退货日志
			ReturnGoodsLog rlog = new ReturnGoodsLog();
			rlog.setReturn_service_id("re" + user.getId() + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
			rlog.setUser_name(user.getUserName());
			rlog.setUser_id(user.getId());
			rlog.setReturn_content(CommUtil.filterHTML(return_goods_content));
			rlog.setGoods_all_price(gls.get("goods_all_price").toString());
			rlog.setGoods_count(gls.get("goods_count").toString());
			rlog.setGoods_id(CommUtil.null2Long(gls.get("goods_id").toString()));
			rlog.setGoods_mainphoto_path(gls.get("goods_mainphoto_path").toString());
			rlog.setGoods_commission_rate(BigDecimal.valueOf(CommUtil.null2Double(gls.get("goods_commission_rate"))));
			rlog.setGoods_name(gls.get("goods_name").toString());
			rlog.setGoods_price(gls.get("goods_price").toString());
			rlog.setGoods_return_status("5");
			rlog.setAddTime(new Date());
			rlog.setReturn_order_id(CommUtil.null2Long(id));
			rlog.setReturn_main_order_id(order_id);
			rlog.setGoods_type(goods.getGoods_type());
			if (goods.getGoods_store() != null) {
				rlog.setStore_id(goods.getGoods_store().getId());
			}
			this.returnGoodsLogService.save(rlog);
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class, obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map.get("express_company_mark")).split("_")[0]);
				String from_addr = "";
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put("callbackurl",
						CommUtil.getURL(request) + "/kuaidi100_callback.htm?order_id=" + obj.getId() + "&orderType=1");
				req.getParameters().put("salt", Md5Encrypt.md5(CommUtil.null2String(obj.getId())).toLowerCase());
				req.setKey(this.configService.getSysConfig().getKuaidi_id2());

				HashMap<String, String> p = new HashMap<String, String>();
				p.put("schema", "json");
				p.put("param", JacksonHelper.toJSON(req));
				try {
					String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
					TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
					if (resp.getResult()) {
						logger.info("订阅成功");
					} else {
						logger.info("订阅失败");
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (obj.getOrder_form() == 0) {
				User seller = this.userService.getObjById(this.storeService
						.getObjById(CommUtil.null2Long(obj.getStore_id())).getUser().getId());
				Map map = new HashMap();
				map.put("buyer_id", user.getId().toString());
				map.put("seller_id", seller.getId().toString());
				this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_order_return_apply_notify",
						seller.getEmail(), map, null, obj.getStore_id());
				map.clear();
				map.put("buyer_id", user.getId().toString());
				map.put("seller_id", seller.getId().toString());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_return_apply_notify",
						seller.getMobile(), map, null, obj.getStore_id());
			}
		}
		return "redirect:order_return_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货申请取消", value = "/buyer/order_return_apply_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply_cancel.htm")
	public String order_return_apply_cancel(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String goods_id, String goods_gsp_ids) throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId().toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
			}
		}
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()) && goods != null) {
			obj.setReturn_goods_info("");
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(goods_id) && goods_gsp_ids.equals(StringUtils.isNullOrEmpty(m.get("goods_gsp_ids")) ? "" : m.get("goods_gsp_ids").toString())) {
					m.put("goods_return_status", "");
					gls.putAll(m);
				}
			}
			obj.setGoods_info(Json.toJson(maps, JsonFormat.compact()));
			this.orderFormService.update(obj);
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			Map map = new HashMap();
			map.put("goods_id", CommUtil.null2Long(gls.get("goods_id").toString()));
			map.put("return_order_id", CommUtil.null2Long(id));
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<ReturnGoodsLog> objs = this.returnGoodsLogService
					.query("select obj from ReturnGoodsLog obj where obj.goods_id=:goods_id and obj.return_order_id=:return_order_id and obj.user_id=:uid",
							map, -1, -1);
			for (ReturnGoodsLog rl : objs) {
				this.returnGoodsLogService.delete(rl.getId());
			}
		}
		return "redirect:order_return_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货申请列表", value = "/buyer/order_return_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_list.htm")
	public ModelAndView order_return_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_return_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId().toString()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");
		if (!"".equals(CommUtil.null2String(order_id))) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
			mv.addObject("order_id", order_id);
		}
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		Map temp = new HashMap();
		temp.put("order_status1", 40);
		temp.put("order_status2", 50);
		temp.put("order_status3", 65);
		ofqo.addQuery("and (obj.order_status =:order_status1 or obj.order_status =:order_status2 or obj.order_status =:order_status3)", temp);
		//ofqo.addQuery("obj.order_status", new SysMap("order_status", 40), ">=");
		ofqo.addQuery("obj.return_shipTime", new SysMap("return_shipTime", new Date()), ">=");
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "生活购退款列表", value = "/buyer/group_life_return.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/group_life_return.htm")
	public ModelAndView group_life_return(HttpServletRequest request, HttpServletResponse response, String id, String view,
			String currentPage, String order_id, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/group_life_return.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfoQueryObject giqo = new GroupInfoQueryObject(currentPage, mv, "addTime", "desc");
		giqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "="); // 当前用户
		giqo.addQuery("obj.status", new SysMap("status", 1), "!=");
		giqo.addQuery("obj.status", new SysMap("status", -1), "!=");
		giqo.addQuery("obj.refund_Time", new SysMap("refund_Time", new Date()), ">=");
		IPageList pList = this.groupinfoService.list(giqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "生活购退款申请", value = "/buyer/group_life_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/group_life_return_apply.htm")
	public ModelAndView group_life_return_apply(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/group_life_return_apply.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo obj = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		}
		return mv;
	}

	@SecurityMapping(title = "生活购退款申请取消", value = "/buyer/grouplife_return_apply_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/grouplife_return_apply_cancel.htm")
	public String grouplife_return_apply_cancel(HttpServletRequest request, HttpServletResponse response, String id) {
		GroupInfo obj = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setStatus(0);
			this.groupinfoService.update(obj);
		}
		return "redirect:group_life_return.htm";
	}

	@SecurityMapping(title = "生活购退款申请保存", value = "/buyer/grouplife_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/grouplife_return_apply_save.htm")
	public String grouplife_return_apply_save(HttpServletRequest request, HttpServletResponse response, String id,
			String return_group_content, String reasion) throws Exception {
		GroupInfo obj = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) { // 订单为生活购订单
				String mark = "";
				if (!StringUtils.isNullOrEmpty(reasion)) {
					String rs_ids[] = reasion.split(",");
					for (String rid : rs_ids) {
						if (!StringUtils.isNullOrEmpty(rid)) {
							if ("1".equals(rid)) {
								mark = "买错了/重新买";
							} else if ("2".equals(rid)) {
								mark = "计划有变，没时间消费";
							} else if ("3".equals(rid)) {
								mark = "预约不上";
							} else if ("4".equals(rid)) {
								mark = "去过了，不太满意";
							} else if ("5".equals(rid)) {
								mark = "其他";
							}
						}
						obj.setStatus(3); // 申请退款中
						obj.setRefund_reasion(mark + "[" + return_group_content + "]"); // 退款说明
						this.groupinfoService.update(obj);
						OrderForm order = this.orderFormService.getObjById(obj.getOrder_id());
						if (order.getOrder_form() == 0) {
							User seller = this.userService.getObjById(this.storeService
									.getObjById(CommUtil.null2Long(order.getStore_id())).getUser().getId());
							Map map = new HashMap();
							map.put("buyer_id", SecurityUserHolder.getCurrentUser().getId().toString());
							map.put("seller_id", seller.getId().toString());
							this.msgTools.sendEmailCharge(CommUtil.getURL(request),
									"email_toseller_order_refund_apply_notify", seller.getEmail(), map, null,
									order.getStore_id());
							map.clear();
							map.put("buyer_id", SecurityUserHolder.getCurrentUser().getId().toString());
							map.put("seller_id", seller.getId().toString());
							this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_refund_apply_notify",
									seller.getMobile(), map, null, order.getStore_id());
						}
					}
				}
			}
		}
		return "redirect:group_life_return.htm";
	}

	@SecurityMapping(title = "买家退货申请列表记录", value = "/buyer/order_return_listlog.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_listlog.htm")
	public ModelAndView order_return_listlog(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_return_listlog.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(currentPage, mv, "addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		qo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
		qo.addQuery("obj.goods_return_status", new SysMap("goods_return_status", "1"), "!=");
		IPageList pList = this.returnGoodsLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "买家退货物流信息保存", value = "/buyer/order_return_ship_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_ship_save.htm")
	public ModelAndView order_return_ship_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String express_id, String express_code) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (obj != null && obj.getUser_id().equals(user.getId())) {
			ExpressCompany ec = this.expressCompayService.getObjById(CommUtil.null2Long(express_id));
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = Json.toJson(json_map);
			obj.setReturn_express_info(express_json);
			obj.setExpress_code(express_code);
			obj.setGoods_return_status("7");
			this.returnGoodsLogService.update(obj);
			mv.addObject("op_title", "保存退货物流成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_return_listlog.htm");
			OrderForm return_of = this.orderFormService.getObjById(obj.getReturn_order_id());
			List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(CommUtil.null2String(obj.getGoods_id()))) {
					m.put("goods_return_status", 7);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			return_of.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(return_of);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id() + "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_return_listlog.htm");

		}
		return mv;
	}

	@SecurityMapping(title = "买家退货填写物流", value = "/buyer/order_returnlog_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_returnlog_view.htm")
	public ModelAndView order_returnlog_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_returnlog_view.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (obj.getUser_id().equals(user.getId())) {
			if ("6".equals(obj.getGoods_return_status()) || "7".equals(obj.getGoods_return_status())) {
				Map params = new HashMap();
				params.put("status", 0);
				List<ExpressCompany> expressCompanys = this.expressCompayService.query(
						"select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
						params, -1, -1);
				mv.addObject("expressCompanys", expressCompanys);
				mv.addObject("obj", obj);
				mv.addObject("user", user);
				OrderForm of = this.orderFormService.getObjById(obj.getReturn_order_id());
				mv.addObject("of", of);
				Goods goods = this.goodsService.getObjById(obj.getGoods_id());
				if (goods.getGoods_type() == 1) {
					mv.addObject("name", goods.getGoods_store().getStore_name());
					mv.addObject("store_id", goods.getGoods_store().getId());
				} else {
					mv.addObject("name", "平台商");
				}
				if ("7".equals(obj.getGoods_return_status())) {
					TransInfo transInfo = this.query_Returnship_getData(CommUtil.null2String(obj.getId()));
					mv.addObject("transInfo", transInfo);
					Map map = Json.fromJson(HashMap.class, obj.getReturn_express_info());
					mv.addObject("express_company_name", map.get("express_company_name"));
				}

			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "当前状态无法对退货服务单进行操作");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_return_listlog.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id() + "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_return_listlog.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "物流ajax", value = "/buyer/ship_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ship_ajax.htm")
	public ModelAndView ship_ajax(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id,String type) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/ship_ajax.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("transInfo_list", orderFormTools.query_ship(order_id,type));
		return mv;
	}

	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
		// 更新订单中组合套装商品信息
		List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		for (Map map_combin : maps) {
			if (map_combin.get("combin_suit_info") != null) {
				Map suit_info = Json.fromJson(Map.class, CommUtil.null2String(map_combin.get("combin_suit_info")));
				int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
				List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
				for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
					for (Goods temp : goods_list) {
						if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(temp_goods.get("id")));
							goods.setGoods_salenum(goods.getGoods_salenum() + combin_count);
//							goods.setGoods_inventory(goods.getGoods_inventory() - combin_count);
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
//						gg.setGg_count(gg.getGg_count() - goods_count);
						gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
						this.groupGoodsService.update(gg);
						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
								+ File.separator + "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
//			List<String> gsps = new ArrayList<String>();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(
					CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
			String spectype = "";
			for (GoodsSpecProperty gsp : temp_gsp_list) {
//				gsps.add(gsp.getId().toString());
				spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
			}
//			String[] gsp_list = new String[gsps.size()];
//			gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);

			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
			todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum() + goods_count);

			Map<String, Integer> logordermap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_order_type());
			String ordertype = order.getOrder_type();
			if (logordermap.containsKey(ordertype)) {
				logordermap.put(ordertype, logordermap.get(ordertype) + goods_count);
			} else {
				logordermap.put(ordertype, goods_count);
			}
			todayGoodsLog.setGoods_order_type(Json.toJson(logordermap, JsonFormat.compact()));

			Map<String, Integer> logspecmap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_sale_info());

			if (logspecmap.containsKey(spectype)) {
				logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
			} else {
				logspecmap.put(spectype, goods_count);
			}
			todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));

			this.goodsLogService.update(todayGoodsLog);
//			String inventory_type = goods.getInventory_type() == null ? "all" : goods.getInventory_type();
//			boolean inventory_warn = false;
//			if ("all".equals(inventory_type)) {
//				goods.setGoods_inventory(goods.getGoods_inventory() - goods_count);
//				if (goods.getGoods_inventory() <= goods.getGoods_warn_inventory()) {
//					inventory_warn = true;
//				}
//			} else {
//				List<HashMap> list = Json.fromJson(ArrayList.class, CommUtil.null2String(goods.getGoods_inventory_detail()));
//				for (Map temp : list) {
//					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
//					Arrays.sort(temp_ids);
//					Arrays.sort(gsp_list);
//					if (Arrays.equals(temp_ids, gsp_list)) {
//						temp.put("count", CommUtil.null2Int(temp.get("count")) - goods_count);
//						if (CommUtil.null2Int(temp.get("count")) <= CommUtil.null2Int(temp.get("supp"))) {
//							inventory_warn = true;
//						}
//					}
//				}
//				goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
//			}
//			for (GroupGoods gg : goods.getGroup_goods_list()) {
//				if (gg.getGroup().getId().equals(goods.getGroup().getId()) && gg.getGg_count() == 0) {
//					goods.setGroup_buy(3); // 标识商品的状态为团购数量已经结束
//				}
//			}
//			if (inventory_warn) {
//				goods.setWarn_inventory_status(-1); // 该商品库存预警状态
//			}
			this.goodsService.update(goods);
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		}
	}

	private TransInfo query_Returnship_getData(String id) {
		TransInfo info = new TransInfo();
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().getKuaidi_type() == 0) { // 免费物流接口
			try {
				ExpressCompany ec = this.orderFormTools.queryExpressCompany(obj.getReturn_express_info());
				String companyMark = ec.getCompany_mark().split("_")[0];
				String query_url = "http://api.kuaidi100.com/api?id=" + this.configService.getSysConfig().getKuaidi_id()
						+ "&com=" + (ec != null ? companyMark : "") + "&nu=" + obj.getExpress_code()
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
				if (StringUtils.isNullOrEmpty(type) || type.trim().indexOf("text/html") < 0) {
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
		if (this.configService.getSysConfig().getKuaidi_type() == 1) { // 收费物流接口
			ExpressInfo ei = this.expressInfoService.getObjByPropertyWithType("order_id", obj.getId(), 1);
			if (ei != null) {
				List<TransContent> data = (List<TransContent>) Json.fromJson(ei.getOrder_express_info());
				info.setData(data);
				info.setStatus("1");
			}
		}
		return info;
	}

	

	private int eva_rate(String rate) {
		int score = 0;
		if ("a".equals(rate)) {
			score = 1;
		} else if ("b".equals(rate)) {
			score = 2;
		} else if ("c".equals(rate)) {
			score = 3;
		} else if ("d".equals(rate)) {
			score = 4;
		} else if ("e".equals(rate)) {
			score = 5;
		}
		return score;
	}

	private int eva_total_rate(String rate) {
		int score = -5;
		if ("a".equals(rate)) {
			score = 1;
		} else if ("b".equals(rate)) {
			score = 0;
		} else if ("c".equals(rate)) {
			score = -1;
		}
		return score;
	}

	/**
	 * id为订单id，goods_id为商品id
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @return
	 */
	@SecurityMapping(title = "买家退款申请", value = "/buyer/order_application_refund.htm*", rtype = "buyer", rname = "用户订单退款", rcode = "buyer_order_refund", rgroup = "用户订单退款")
	@RequestMapping("/buyer/order_application_refund.htm")
	public ModelAndView order_application_refund(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/order_return_refund.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);; 
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		Map<String, Double> map = null;
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			//已经付款待发货或者审核不通过可以申请退款
			if(obj.getOrder_status() == 20 || obj.getOrder_status() == 27){
				//调用计算退款金额接口
				map = this.orderFormService.getOrderRefundAmount(obj.getOrder_id());
				double price = map.get("price");
				mv.addObject("price",price);
				mv.addObject("obj", obj);
			//退款中
			}else if(obj.getOrder_status() == 25){
				//调用计算退款金额接口
				map = this.orderFormService.getOrderRefundAmount(obj.getOrder_id());
				double price = map.get("price");
				mv.addObject("price",price);
				mv.addObject("obj", obj);
			}
		}
		return mv;
	}
	
	/**
	 * id为订单id，totalPrice为订单总金额，refund_amount为退款金额，refund_cause为退款原因
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param id
	 * @param totalPrice
	 * @param refund_amount
	 * @param refund_cause
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家退款申请保存", value = "/buyer/order_return_refund_save.htm*", rtype = "buyer", rname = "用户订单退款", rcode = "buyer_order_refund", rgroup = "用户订单退款")
	@RequestMapping("/buyer/order_return_refund_save.htm")
	public String order_return_refund_save(HttpServletRequest request, HttpServletResponse response, String currentPage, 
			String id, String totalPrice, String refund_amount, String refund_cause) throws Exception {
		String returnStr = null;
		if(!StringUtils.isNullOrEmpty(id)){
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if(obj.getOrder_status() == 20 || obj.getOrder_status() == 27){
				obj.setOrder_status(25);//申请退款审核中
				//设置申请退款原因
				obj.setRefund_cause(refund_cause);
				this.orderFormService.save(obj);
				if (obj.getOrder_form() == 0) {
					User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					User seller = this.userService.getObjById(this.storeService
							.getObjById(CommUtil.null2Long(obj.getStore_id())).getUser().getId());
					Map map = new HashMap();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_order_return_apply_notify",
							seller.getEmail(), map, null, obj.getStore_id());
					map.clear();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_return_apply_notify",
							seller.getMobile(), map, null, obj.getStore_id());
				}
				returnStr = "redirect:order.htm?currentPage=" + currentPage;
			}
		}
		return returnStr;
	}
	
	@SecurityMapping(title = "买家退款申请取消", value = "/buyer/order_return_refund_cancel.htm*", rtype = "buyer", rname = "用户订单退款", rcode = "buyer_order_refund", rgroup = "用户订单退款")
	@RequestMapping("/buyer/order_return_refund_cancel.htm")
	public String order_return_refund_cancel(HttpServletRequest request, HttpServletResponse response, String currentPage, String id, String refund_cause)
			throws Exception {
		if(!StringUtils.isNullOrEmpty(id)){
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if(obj.getOrder_status() == 25){
				obj.setOrder_status(20);//买家取消退款申请
				obj.setRefund_cause(refund_cause);
				this.orderFormService.save(obj);
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}
	
	/**
	 * 退款申请状态校验
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/buyer/canRefund.htm")
	public void canRefund(HttpServletRequest request, HttpServletResponse response, String id, String type){
		boolean code = false;
		if(!StringUtils.isNullOrEmpty(id)){
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if("1".equals(type)){
				if(obj.getOrder_status() == 20 || obj.getOrder_status() == 27){
					code = true;
				}
			}else if("2".equals(type)){
				if(obj.getOrder_status() == 25){
					code = true;
				}
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
}
