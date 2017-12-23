package com.iskyshop.manage.admin.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.nutz.json.Json;
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
import com.iskyshop.core.tools.ExcelUtil;
import com.iskyshop.core.tools.ExportSetInfo;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.seller.tools.OrderTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipaySubmit;

/**
 * 
 * 
 * <p>
 * Title: OrderSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营商品订单管理器，显示所有自营商品订单，添加权限的管理员都可进行管理。
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
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfOrderManageAction {
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
	private IUserService userService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private OrderTools orderTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsConfigService goodsConfigService;
	@Autowired
	private PaymentTools paymentTools;

	private static final BigDecimal WHETHER_ENOUGH = new BigDecimal(0.00);
	private static final Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>() {
		{
			put(0, "已取消");
			put(10, "待付款");
			put(15, "线下支付待审核");
			put(16, "货到付款待发货");
			put(20, "已付款");
			put(25, "审核中");
			put(27, "审核不通过");
			put(30, "已发货");
			put(35, "自提点代收");
			put(40, "已收货");
			put(50, "已完成");
			put(60, "已结束");
			put(65, "已完成");
			put(80, "退款中");
			put(85, "退款已提交");
			put(87, "退款失败");
			put(90, "退款完成");
		}
	};

	private static final Map<String, String> PAYMENT_MAP = new HashMap<String, String>() {
		{
			put(null, "未支付");
			put("", "未支付");
			put("alipay", "支付宝");
			put("alipay_wap", "手机网页支付宝");
			put("alipay_app", "手机支付宝APP");
			put("tenpay", "财付通");
			put("bill", "快钱");
			put("chinabank", "网银在线");
			put("outline", "线下支付");
			put("balance", "预存款支付");
			put("payafter", "货到付款");
			put("paypal", "paypal");
			put("wx_app", "手机微信");
			put("wx_pay", "手机网页微信");
			put("chinapay", "中国银联(PC端支付)");
			put("chinapay_nocard", "中国银联无卡支付");
			put("unionpay_app", "中国银联(手机APP支付)");
			put("unionpay_wap", "中国银联(手机网页支付)");
		}
	};

	private static final Map<String, String> TYPE_MAP = new HashMap<String, String>() {
		{
			put(null, "PC订单");
			put("", "PC订单");
			put("web", "PC订单");
			put("weixin", "微信订单");
			put("android", "Android订单");
			put("ios", "IOS订单");
		}
	};

	@SecurityMapping(title = "自营订单列表", value = "/admin/self_order.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/self_order.htm")
	public ModelAndView self_order(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime, String endTime, String buyer_userName, String configCode) {
		ModelAndView mv = new JModelAndView("admin/blue/self_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!"".equals(CommUtil.null2String(order_status))) {
			if ("order_submit".equals(order_status)) { // 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				ofqo.addQuery("(obj.order_status=:order_status1 or obj.order_status=:order_status2)", map);
			}
			if ("order_pay".equals(order_status)) { // 已经付款
				Map map = new HashMap();
				map.put("order_status1", 20);
				map.put("order_status2", 16);
				ofqo.addQuery("(obj.order_status=:order_status1 or obj.order_status=:order_status2)", map);
			}
			if ("order_shipping".equals(order_status)) { // 已经发货
				ofqo.addQuery("obj.order_status", new SysMap("order_status", 30), "=");
			}
			if ("order_evaluate".equals(order_status)) { // 买家已评价
				ofqo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");
			}
			if ("order_finish".equals(order_status)) { // 已经完成
				ofqo.addQuery("obj.order_status", new SysMap("order_status", 50), "=");
			}
			if ("order_cancel".equals(order_status)) { // 已经取消
				ofqo.addQuery("obj.order_status", new SysMap("order_status", 0), "=");
			}
		}
		if (!"".equals(CommUtil.null2String(order_id))) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
		}
		if (!"".equals(CommUtil.null2String(beginTime))) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!"".equals(CommUtil.null2String(buyer_userName))) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name", buyer_userName), "=");
		}
		if (!StringUtils.isNullOrEmpty(configCode)) {
			ofqo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", CommUtil.null2String(configCode)), "=");
			mv.addObject("configCode", configCode);
		}
		Map map0 = new HashMap();
		map0.put("goodsSource", 1);
		List<GoodsConfig> goodsConfigList = this.goodsConfigService
				.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource order by obj.id asc", map0, -1, -1);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("order_status", order_status == null ? "all" : order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("goodsConfigList", goodsConfigList);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "自营待发货订单列表", value = "/admin/self_order_ship.htm*", rtype = "admin", rname = "发货管理", rcode = "order_ship", rgroup = "自营")
	@RequestMapping("/admin/self_order_ship.htm")
	public ModelAndView self_order_ship(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id, String beginTime, String endTime, String buyer_userName, String configCode) {
		ModelAndView mv = new JModelAndView("admin/blue/self_order_ship.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "payTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		Map map = new HashMap();
		map.put("order_status1", 20);
		map.put("order_status2", 16);
		ofqo.addQuery("(obj.order_status=:order_status1 or obj.order_status=:order_status2)", map);
		if (!"".equals(CommUtil.null2String(order_id))) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
		}
		if (!"".equals(CommUtil.null2String(beginTime))) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!"".equals(CommUtil.null2String(buyer_userName))) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name", buyer_userName), "=");
		}
		if (!StringUtils.isNullOrEmpty(configCode)) {
			ofqo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", CommUtil.null2String(configCode)), "=");
			mv.addObject("configCode", configCode);
		}
		Map map0 = new HashMap();
		map0.put("goodsSource", 1);
		List<GoodsConfig> goodsConfigList = this.goodsConfigService
				.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource order by obj.id asc", map0, -1, -1);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("goodsConfigList", goodsConfigList);
		return mv;
	}

	@SecurityMapping(title = "自营待发货订单详情", value = "/admin/ship_order_view.htm*", rtype = "admin", rname = "发货管理", rcode = "order_ship", rgroup = "自营")
	@RequestMapping("/admin/ship_order_view.htm")
	public ModelAndView ship_order_view(HttpServletRequest request, HttpServletResponse response, String id, String view_type,String type) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_order_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_cat() == 1) {
			mv = new JModelAndView("admin/blue/order_recharge_view.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		} else {
			mv.addObject("obj", obj);
		}
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "自营待发货订单列表", value = "/admin/self_order_confirm.htm*", rtype = "admin", rname = "收货管理", rcode = "order_confirm", rgroup = "自营")
	@RequestMapping("/admin/self_order_confirm.htm")
	public ModelAndView self_order_confirm(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id, String beginTime, String endTime, String buyer_userName, String configCode) {
		ModelAndView mv = new JModelAndView("admin/blue/self_order_confirm.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "shipTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		Map temp = new HashMap();
		temp.put("order_status1", 30);
		temp.put("order_status2", 35);
		ofqo.addQuery("and (obj.order_status =:order_status1 or obj.order_status =:order_status2)", temp);
		if (!"".equals(CommUtil.null2String(order_id))) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id.trim() + "%"), "like");
		}
		if (!"".equals(CommUtil.null2String(beginTime))) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!"".equals(CommUtil.null2String(buyer_userName))) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name", buyer_userName), "=");
		}
		if (!StringUtils.isNullOrEmpty(configCode)) {
			ofqo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", CommUtil.null2String(configCode)), "=");
			mv.addObject("configCode", configCode);
		}
		Map map0 = new HashMap();
		map0.put("goodsSource", 1);
		List<GoodsConfig> goodsConfigList = this.goodsConfigService
				.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource order by obj.id asc", map0, -1, -1);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("orderTools", this.orderTools);
		mv.addObject("goodsConfigList", goodsConfigList);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "自营待收货订单详情", value = "/admin/confirm_order_view.htm*", rtype = "admin", rname = "收货管理", rcode = "order_confirm", rgroup = "自营")
	@RequestMapping("/admin/confirm_order_view.htm")
	public ModelAndView confirm_order_view(HttpServletRequest request, HttpServletResponse response, String id,
			String view_type,String type) {
		ModelAndView mv = new JModelAndView("admin/blue/confirm_order_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_cat() == 1) {
			mv = new JModelAndView("admin/blue/order_recharge_view.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		} else {
			mv.addObject("obj", obj);
		}
		TransInfo transInfo = this.orderFormTools.query_ship_getData(id);
		transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		transInfo.setExpress_ship_code(obj.getShipCode());
		mv.addObject("transInfo", transInfo);
		// mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(),
		// "express_company_name"));
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "自营订单延长收货时间", value = "/admin/self_order_comfirm_delay.htm*", rtype = "admin", rname = "收货管理", rcode = "order_confirm", rgroup = "自营")
	@RequestMapping("/admin/self_order_comfirm_delay.htm")
	public ModelAndView order_comfirm_delay(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/self_order_comfirm_delay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_form() == 1) { // 只能处理自营订单
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;

	}

	@SecurityMapping(title = "自营订单延长收货时间保存", value = "/admin/self_order_confirm_delay_save.htm*", rtype = "admin", rname = "收货管理", rcode = "order_confirm", rgroup = "自营")
	@RequestMapping("/admin/self_order_confirm_delay_save.htm")
	public String self_order_confirm_delay_save(HttpServletRequest request, HttpServletResponse response, String id,
			String delay_time, String currentPage) throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_form() == 1) { // 只能处理自营订单
			obj.setOrder_confirm_delay(obj.getOrder_confirm_delay() + CommUtil.null2Int(delay_time));
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("延长收货时间");
			ofl.setState_info("延长收货时间：" + delay_time + "天");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		return "redirect:self_order_confirm.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "调整订单费用", value = "/admin/order_fee.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_fee.htm")
	public ModelAndView order_fee(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_fee.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "调整订单费用保存", value = "/admin/order_fee_save.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_fee_save.htm")
	public String order_fee_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage,
			String goods_amount, String ship_price, String totalPrice) throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			obj.setGoods_amount(BigDecimal.valueOf(CommUtil.null2Double(goods_amount)));
			obj.setShip_price(BigDecimal.valueOf(CommUtil.null2Double(ship_price)));
			obj.setTotalPrice(BigDecimal.valueOf(CommUtil.null2Double(totalPrice)));
			obj.setOperation_price_count(obj.getOperation_price_count() + 1);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("调整订单费用");
			ofl.setState_info("调整订单总金额为:" + totalPrice + ",调整运费金额为:" + ship_price);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			map.put("order_id", obj.getId());
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_selforder_update_fee_notify",
					buyer.getEmail(), map, null);
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "sms_tobuyer_selforder_fee_notify", buyer.getMobile(),
					map, null);
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家个人信息", value = "/admin/order_query_userinfor.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_query_userinfor.htm")
	public ModelAndView order_query_userinfor(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_query_userinfor.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "取消订单", value = "/admin/order_cancel.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_cancel.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "取消订单保存", value = "/admin/order_cancel_save.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_cancel_save.htm")
	public String order_cancel_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
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
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			map.put("order_id", obj.getId());
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_selforder_cancel_notify", buyer.getEmail(),
					map, null);
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "sms_tobuyer_selforder_cancel_notify", buyer.getMobile(),
					map, null);
			// 还原商品库存
			this.goodsService.recover_goods_inventory(obj, null);
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "确认发货", value = "/admin/order_shipping.htm*", rtype = "admin", rname = "发货管理", rcode = "order_ship", rgroup = "自营")
	@RequestMapping("/admin/order_shipping.htm")
	public ModelAndView order_shipping(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String op) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_shipping.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			List<Goods> list_goods = this.orderFormTools.queryOfGoods(id);
			List<Goods> deliveryGoods = new ArrayList<Goods>();
			boolean physicalGoods = false;
			for (Goods g : list_goods) {
				if (g.getGoods_choice_type() == 1) {
					deliveryGoods.add(g);
				} else {
					physicalGoods = true;
				}
			}
			Map params = new HashMap();
			params.put("ecc_type", 1);
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.query(
					"select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type", params, -1, -1);
			params.clear();
			params.put("sa_type", 1);
			List<ShipAddress> shipAddrs = this.shipAddressService
					.query("select obj from ShipAddress obj where obj.sa_type=:sa_type order by obj.sa_default desc,obj.sa_sequence asc",
							params, -1, -1); // 按照默认地址倒叙、其他地址按照索引升序排序，保证默认地址在第一位
			mv.addObject("shipAddrs", shipAddrs);
			mv.addObject("eccs", eccs);
			mv.addObject("physicalGoods", physicalGoods);
			mv.addObject("deliveryGoods", deliveryGoods);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("op", op);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "确认发货保存", value = "/admin/order_shipping_save.htm*", rtype = "admin", rname = "发货管理", rcode = "order_ship", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_save.htm")
	public String order_shipping_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String shipCode, String state_info, String order_seller_intro, String ecc_id, String sa_id,
			String op) throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			obj.setEva_user_id(SecurityUserHolder.getCurrentUser().getId());
			obj.setOrder_status(30);
			obj.setShipCode(shipCode);
			obj.setShipTime(new Date());
			ExpressCompanyCommon ecc = this.expressCompanyCommonService.getObjById(CommUtil.null2Long(ecc_id));
			if (ecc != null) {
				Map json_map = new HashMap();
				json_map.put("express_company_id", ecc.getEcc_ec_id());
				json_map.put("express_company_id_common", ecc.getId());
				json_map.put("express_company_name", ecc.getEcc_name());
				json_map.put("express_company_mark", ecc.getEcc_code());
				json_map.put("express_company_type", ecc.getEcc_ec_type());
				String express_json = Json.toJson(json_map);
				obj.setExpress_info(express_json);
			}
			String[] order_seller_intros = request.getParameterValues("order_seller_intro");
			String[] goods_ids = request.getParameterValues("goods_id");
			String[] goods_names = request.getParameterValues("goods_name");
			String[] goods_counts = request.getParameterValues("goods_count");
			if (order_seller_intros != null && order_seller_intros.length > 0) {
				List<Map> list_map = new ArrayList<Map>();
				for (int i = 0; i < goods_ids.length; i++) {
					Map json_map = new HashMap();
					json_map.put("goods_id", goods_ids[i]);
					json_map.put("goods_name", goods_names[i]);
					json_map.put("goods_count", goods_counts[i]);
					json_map.put("order_seller_intro", order_seller_intros[i]);
					json_map.put("order_id", id);
					list_map.add(json_map);
				}
				obj.setOrder_seller_intro(Json.toJson(list_map));
			}
			ShipAddress sa = this.shipAddressService.getObjById(CommUtil.null2Long(sa_id));
			if (sa != null) {
				obj.setShip_addr_id(sa.getId());
				Area area = this.areaService.getObjById(sa.getSa_area_id());
				obj.setShip_addr(area.getParent().getParent().getAreaName() + area.getParent().getAreaName()
						+ area.getAreaName() + sa.getSa_addr());
			}
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("星美生活确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
			Map map = new HashMap();
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			
			if(!this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_selforder_ship_notify", buyer.getEmail(), map, obj.getId().toString())) {
				logger.error("向买家发送已发货邮件通知失败：user_id=" + buyer.getId() + ", user_name=" + buyer.getUsername() + ", order_id=" + obj.getId());
			}
			
			if(!this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_selforder_ship_notify", buyer.getMobile(), map, obj.getId().toString())) {
				logger.error("向买家发送已发货短信通知失败：user_id=" + buyer.getId() + ", user_name=" + buyer.getUsername() + ", order_id=" + obj.getId());
			}
			
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1 && obj.getExpress_info() != null) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class, obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map.get("express_company_mark")).split("_")[0]);
				String from_addr = obj.getShip_addr();
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put("callbackurl",
						CommUtil.getURL(request) + "/kuaidi100_callback.htm?order_id=" + obj.getId() + "&orderType=0");
				req.getParameters().put("salt", Md5Encrypt.md5(CommUtil.null2String(obj.getId())).toLowerCase());
				req.setKey(this.configService.getSysConfig().getKuaidi_id2());

				HashMap<String, String> p = new HashMap<String, String>();
				p.put("schema", "json");
				p.put("param", JacksonHelper.toJSON(req));
				try {
					String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
					TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
					logger.info("发货订阅返回信息："+Json.toJson(resp));
					if (resp.getResult() == true) {
						ExpressInfo ei = new ExpressInfo();
						ei.setAddTime(new Date());
						ei.setOrder_id(obj.getId());
						ei.setOrder_express_id(obj.getShipCode());
						ei.setOrder_type(0);
						Map ec_map = Json.fromJson(Map.class, CommUtil.null2String(obj.getExpress_info()));
						if (ec_map != null) {
							ei.setOrder_express_name(CommUtil.null2String(ec_map.get("express_company_name")));
						}
						this.expressInfoService.save(ei);
						logger.info("订阅成功");
					} else {
						logger.info("订阅失败");
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			// 异步通知支付宝,该方法用在支付宝担保支付，目的用来修改支付宝订单状态为“已发货”
			if (obj.getPayment() != null && "alipay".equals(obj.getPayment().getMark())
					&& obj.getPayment().getInterfaceType() == 1) {
				// 把请求参数打包成数组
				boolean synch = false;
				String safe_key = "";
				String partner = "";
				if (!"".equals(CommUtil.null2String(obj.getPayment().getSafeKey()))
						&& !"".equals(CommUtil.null2String(obj.getPayment().getPartner()))) {
					safe_key = obj.getPayment().getSafeKey();
					partner = obj.getPayment().getPartner();
					synch = true;
				}
				if (synch) {
					AlipayConfig config = new AlipayConfig();
					config.setKey(safe_key);
					config.setPartner(partner);
					Map<String, String> sParaTemp = new HashMap<String, String>();
					sParaTemp.put("service", "send_goods_confirm_by_platform");
					sParaTemp.put("partner", config.getPartner());
					sParaTemp.put("_input_charset", config.getInput_charset());
					sParaTemp.put("trade_no", obj.getOut_order_id());
					sParaTemp.put("logistics_name", ecc.getEcc_name());
					sParaTemp.put("invoice_no", shipCode);
					sParaTemp.put("transport_type", ecc.getEcc_ec_type());
					// 建立请求
					String sHtmlText = AlipaySubmit.buildRequest(config, "web", sParaTemp, "", "");
				}
			}
		}
		if ("self_order_ship".equals(CommUtil.null2String(op))) {
			return "redirect:self_order_ship.htm?currentPage=" + currentPage;
		} else {
			return "redirect:self_order.htm?currentPage=" + currentPage;
		}
	}

	@SecurityMapping(title = "快递单打印", value = "/admin/order_ship_print.htm*", rtype = "admin", rname = "收货管理", rcode = "order_confirm", rgroup = "自营")
	@RequestMapping("/admin/order_ship_print.htm")
	public ModelAndView order_ship_print(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_ship_print.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (order.getOrder_form() == 1) { // 只能打印自营的订单
			Map ec_map = Json.fromJson(Map.class, order.getExpress_info());
			ExpressCompanyCommon ecc = this.expressCompanyCommonService.getObjById(CommUtil.null2Long(ec_map
					.get("express_company_id_common")));
			if (ecc != null) {
				Map offset_map = Json.fromJson(Map.class, ecc.getEcc_template_offset());
				ShipAddress ship_addr = this.shipAddressService.getObjById(order.getShip_addr_id());
				mv.addObject("ecc", this.expressCompanyCommonService.getObjById(CommUtil.null2Long(ec_map
						.get("express_company_id_common"))));
				mv.addObject("offset_map", offset_map);
				mv.addObject("obj", order);
				mv.addObject("ship_addr", ship_addr);
				mv.addObject("area", this.areaService.getObjById(ship_addr.getSa_area_id()));
			} else {
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "老版物流订单，无法打印！");
			}
		}
		return mv;
	}

	@SecurityMapping(title = "修改物流", value = "/admin/order_shipping_code.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_code.htm")
	public ModelAndView order_shipping_code(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String mark) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_shipping_code.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			Map params = new HashMap();
			params.put("ecc_type", 1);
			//params.put("ecc_store_id", store.getId());
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.query(
					"select obj from ExpressCompanyCommon obj where obj.ecc_store_id is null and obj.ecc_type=:ecc_type",
					params, -1, -1);
			mv.addObject("eccs", eccs);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("mark", mark);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "修改物流保存", value = "/admin/order_shipping_code_save.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_code_save.htm")
	public String order_shipping_code_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String shipCode, String state_info, String ecc_id, String mark) {
		String url;
		//发货管理页面跳转
		if("shipments".equals(mark)){
			url = "self_order_confirm.htm";
		//订单管理页面跳转
		}else{
			url = "self_order.htm";
		}
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		ExpressCompanyCommon ecc = this.expressCompanyCommonService.getObjById(CommUtil.null2Long(ecc_id));
		if (1 == obj.getOrder_form()) {
			obj.setShipCode(shipCode);
			if (ecc != null) {
				Map json_map = new HashMap();
				json_map.put("express_company_id", ecc.getEcc_ec_id());
				json_map.put("express_company_id_common", ecc.getId());
				json_map.put("express_company_name", ecc.getEcc_name());
				json_map.put("express_company_mark", ecc.getEcc_code());
				json_map.put("express_company_type", ecc.getEcc_ec_type());
				obj.setExpress_info(Json.toJson(json_map));
			}
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1 && obj.getExpress_info() != null) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class, obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map.get("express_company_mark")).split("_")[0]);
				String from_addr = obj.getShip_addr();
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put("callbackurl",
						CommUtil.getURL(request) + "/kuaidi100_callback.htm?order_id=" + obj.getId() + "&orderType=0");
				req.getParameters().put("salt", Md5Encrypt.md5(CommUtil.null2String(obj.getId())).toLowerCase());
				req.setKey(this.configService.getSysConfig().getKuaidi_id2());

				HashMap<String, String> p = new HashMap<String, String>();
				p.put("schema", "json");
				p.put("param", JacksonHelper.toJSON(req));
				try {
					String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
					TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
					if (resp.getResult()) {
						ExpressInfo ei = new ExpressInfo();
						ei.setAddTime(new Date());
						ei.setOrder_id(obj.getId());
						ei.setOrder_express_id(obj.getShipCode());
						ei.setOrder_type(0);
						Map ec_map = Json.fromJson(Map.class, CommUtil.null2String(obj.getExpress_info()));
						if (ec_map != null) {
							ei.setOrder_express_name(CommUtil.null2String(ec_map.get("express_company_name")));
						}
						this.expressInfoService.save(ei);
						logger.info("订阅成功");
					} else {
						logger.info("订阅失败");
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return "redirect:"+ url +"?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货申请详情", value = "/admin/admin_order_return_apply_view.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/admin_order_return_apply_view.htm")
	public ModelAndView admin_order_return_apply_view(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_return_apply_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "打印订单", value = "/admin/order_print.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_print.htm")
	public ModelAndView order_print(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_print.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			OrderForm orderform = this.orderFormService.getObjById(CommUtil.null2Long(id));
			mv.addObject("obj", orderform);
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "物流详情", value = "/admin/ship_view.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_ship_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (!StringUtils.isNullOrEmpty(obj)) {
			if (1 == obj.getOrder_form()) {
				mv.addObject("obj", obj);
				TransInfo transInfo = this.orderFormTools.query_ship_getData(CommUtil.null2String(obj.getId()));
				mv.addObject("transInfo", transInfo);
				mv.addObject("express_company_name",
						this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "您查询的物流不存在！");
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "导出表格", value = "/admin/order_excel.htm*", rtype = "admin", rname = "订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_excel.htm")
	public void order_excel(HttpServletRequest request, HttpServletResponse response, String order_status, String order_id,
			String beginTime, String endTime, String buyer_userName, String configCode) {
		OrderFormQueryObject qo = new OrderFormQueryObject();
		qo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		qo.setPageSize(1000000000);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!"".equals(CommUtil.null2String(order_status))) {
			if ("order_submit".equals(order_status)) { // 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				qo.addQuery("(obj.order_status=:order_status1 or obj.order_status=:order_status2)", map);
			}
			if ("order_pay".equals(order_status)) { // 已经付款
				qo.addQuery("obj.order_status", new SysMap("order_status1", 16), ">=");
				qo.addQuery("obj.order_status", new SysMap("order_status2", 20), "<=");
			}
			if ("30_35".equals(order_status)) { // 30为已发货待收货，35自提点已经收货
				Map map = new HashMap();
				map.put("order_status1", 30);
				map.put("order_status2", 35);
				qo.addQuery("(obj.order_status=:order_status1 or obj.order_status=:order_status2)", map);
			}
			if ("order_shipping".equals(order_status)) { // 已经发货
				qo.addQuery("obj.order_status", new SysMap("order_status", 30), "=");
			}
			if ("order_evaluate".equals(order_status)) { // 等待评价
				qo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");
			}
			if ("order_finish".equals(order_status)) { // 已经完成
				qo.addQuery("obj.order_status", new SysMap("order_status", 50), "=");
			}
			if ("order_cancel".equals(order_status)) { // 已经取消
				qo.addQuery("obj.order_status", new SysMap("order_status", 0), "=");
			}
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		if (!StringUtils.isNullOrEmpty(buyer_userName)) {
			qo.addQuery("obj.user_name", new SysMap("user_name", buyer_userName), "=");
		}
		if (!StringUtils.isNullOrEmpty(configCode)) {
			qo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", CommUtil.null2String(configCode)), "=");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		qo.setOrderType("desc");
		IPageList pList = this.orderFormService.list(qo);
		if (pList.getResult() != null) {
			List<OrderForm> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet("订单列表");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1, 2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 8000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 8000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 6000);
			sheet.setColumnWidth(12, 8000);
			sheet.setColumnWidth(13, 6000);
			sheet.setColumnWidth(14, 4000);
			sheet.setColumnWidth(15, 4000);
			sheet.setColumnWidth(16, 6000);
			sheet.setColumnWidth(17, 8000);
			sheet.setColumnWidth(18, 6000);
			sheet.setColumnWidth(19, 6000);
			sheet.setColumnWidth(20, 6000);
			sheet.setColumnWidth(21, 6000);
			// 创建字体样式
			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor(HSSFColor.BLUE.index);
			// 创建单元格样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置边框
			style.setBottomBorderColor(HSSFColor.RED.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setFont(font); // 设置字体
			// 创建Excel的sheet的一行
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500); // 设定行的高度
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 21));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "订单列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1) + " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle() + title + "（" + time + "）");
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true); // 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("订单号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("支付单号");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("付款完成时间");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("确认收货时间");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("商品名称");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("商品编号");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("规格");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("单价");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("数量");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("小计");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(12);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(13);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(14);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(15);
			cell.setCellStyle(style2);
			cell.setCellValue("买家");
			cell = row.createCell(16);
			cell.setCellStyle(style2);
			cell.setCellValue("收货人");
			cell = row.createCell(17);
			cell.setCellStyle(style2);
			cell.setCellValue("身份证");
			cell = row.createCell(18);
			cell.setCellStyle(style2);
			cell.setCellValue("收货地址");
			cell = row.createCell(19);
			cell.setCellStyle(style2);
			cell.setCellValue("联系电话");
			cell = row.createCell(20);
			cell.setCellStyle(style2);
			cell.setCellValue("发票信息");
			cell = row.createCell(21);
			cell.setCellStyle(style2);
			cell.setCellValue("买家备注");
			int count = 0;
			double all_order_price = 0.00; // 订单总金额
			double all_total_amount = 0.00; // 商品总金额
			int index = 0;
			DecimalFormat format = new DecimalFormat("#.##");
			format.setRoundingMode(RoundingMode.HALF_UP);
			for (int j = 2; j <= datas.size() + 1; j++) {
				List<Map> goods_json = new ArrayList<Map>();
				if (datas.size() >= j - 2 && datas.get(j - 2) != null) {
					goods_json = Json.fromJson(List.class, CommUtil.null2String(datas.get(j - 2).getGoods_info()));
				}
				all_order_price = CommUtil.add(all_order_price, datas.get(j - 2).getTotalPrice());//计算订单总价
				all_total_amount = CommUtil.add(all_total_amount, datas.get(j - 2).getGoods_amount()); // 计算商品总价
				if (goods_json != null) {
					int k = 0;
					/*double summation = 0.00;
					for (Map map : goods_json) {
						summation = summation + CommUtil.mul(map.get("goods_price"), map.get("goods_count"));
					}*/
					for (Map map : goods_json) {
						int i = 0;
						row = sheet.createRow(j + count);
						// 设置单元格的样式格式
						cell = row.createCell(i);
						cell.setCellStyle(style2);
						cell.setCellValue(datas.get(j - 2).getOrder_id());// 订单号

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						if(!StringUtils.isNullOrEmpty(datas.get(j - 2).getOut_order_id())){
						  cell.setCellValue(datas.get(j - 2).getOut_order_id());// 支付单号
						}else{
						  cell.setCellValue(datas.get(j - 2).getPayCenterNo());// 支付单号
						}

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getAddTime()));// 下单时间

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getPayTime()));// 付款完成时间

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getConfirmTime()));// 确认收货时间

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(map.get("goods_name").toString());// 商品名称
						
						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(map.get("goods_id").toString());// 商品编号
						
						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(map.get("goods_gsp_val").toString().replaceAll("<br>", " "));// 规格
						
						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(map.get("goods_price").toString());// 单价

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(map.get("goods_count").toString());// 数量

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						double subtotal = 0.00;
						if (map.get("goods_price") != null && map.get("goods_count") != null) {
							subtotal = CommUtil.mul(map.get("goods_price"), map.get("goods_count"));
						}
						cell.setCellValue(subtotal);// 小计

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						double shipPrice = 0.00;
						if (datas.get(j - 2).getShip_price() != null) {
							shipPrice = datas.get(j - 2).getShip_price().doubleValue();
						}
						if (k == 0) {
							cell.setCellValue(shipPrice);// 运费
						} else {
							cell.setCellValue("");
						}

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						double totalPrice = 0.00;
						if (datas.get(j - 2).getTotalPrice() != null) {
							totalPrice = datas.get(j - 2).getTotalPrice().doubleValue();
						}
						if (k == 0) {
							cell.setCellValue(totalPrice);// 订单总额
						} else {
							cell.setCellValue("");
						}

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						if (datas.get(j - 2).getPayment() != null) {
							cell.setCellValue(PAYMENT_MAP.get(datas.get(j - 2).getPayment().getMark()));
						} else {
							cell.setCellValue("未支付");// 支付方式
						}

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(STATUS_MAP.get(datas.get(j - 2).getOrder_status()));// 订单状态

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(datas.get(j - 2).getUser_name());// 买家

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(datas.get(j - 2).getReceiver_Name());// 收货人

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(datas.get(j - 2).getReceiver_card());// 身份证

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						String receiver_area = datas.get(j - 2).getReceiver_area();
						String receiver_area_info = datas.get(j - 2).getReceiver_area_info();
						cell.setCellValue((!StringUtils.isNullOrEmpty(receiver_area) ? receiver_area : "")
								+ (!StringUtils.isNullOrEmpty(receiver_area_info) ? receiver_area_info : ""));// 收地址

						String receiver_mobile = !StringUtils.isNullOrEmpty(datas.get(j - 2).getReceiver_mobile())
								? datas.get(j - 2).getReceiver_mobile() : "";
						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(receiver_mobile);// 联系电话

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue((datas.get(j - 2).getInvoiceType() == 0 ? "个人" : "单位")
								+ (!StringUtils.isNullOrEmpty(datas.get(j - 2).getInvoice())
										? ": " + datas.get(j - 2).getInvoice() : ""));// 发票信息

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue(datas.get(j - 2).getMsg());// 买家备注
						if (k != goods_json.size() - 1) {
							count++;
						}
						if (goods_json.size() >= 2 && k == goods_json.size() - 1) {
							for (int a = 0; a < 16; a++) {
								if (a <= 4) {
									sheet.addMergedRegion(new CellRangeAddress(j + count - k, j + count, a, a));
								} else {
									sheet.addMergedRegion(new CellRangeAddress(j + count - k, j + count, a + 6, a + 6));
								}
							}
						}
						k++;
						index++;
					}
				}
			}
			// 设置底部统计信息
			int m = index + 3;
			row = sheet.createRow(m);
			// 设置单元格的样式格式
			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次订单金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次商品总金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_total_amount);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = CommUtil.getServerRealPathFromRequest(request) + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition", "attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}

	/**
	 * @Title: shipments_excel
	 * @Description: TODO(自营发货信息导出)
	 * @param 参数
	 * @return void 返回类型
	 * @throws
	 */
	@SecurityMapping(title = "导出发货信息表格", value = "/admin/shipments_excel.htm*", rtype = "admin", rname = "自营发货管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/shipments_excel.htm")
	public void shipments_excel(HttpServletRequest request, HttpServletResponse response,String order_status,String order_id,
			String beginTime, String endTime, String buyer_userName,String configCode) {
		
		LinkedHashMap<String, List> linkMap = new LinkedHashMap<String, List>();
		OutputStream outputStream = null;
		try {
			OrderFormQueryObject qo = new OrderFormQueryObject();
			qo.addQuery("obj.order_form", new SysMap("order_form", 1),"="); //自营订单
			qo.setOrderBy("shipTime");
			qo.setOrderType("desc");
			qo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
			Map temp = new HashMap();
			temp.put("order_status1", 30);
			temp.put("order_status2", 35);
			qo.addQuery("and (obj.order_status =:order_status1 or obj.order_status =:order_status2)", temp);
			if (!"".equals(CommUtil.null2String(order_id))) {
				qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id.trim() + "%"), "like");
			}
			if (!"".equals(CommUtil.null2String(beginTime))) {
				qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			}
			if (!"".equals(CommUtil.null2String(endTime))) {
				String ends = endTime + " 23:59:59";
				qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			}
			if (!"".equals(CommUtil.null2String(buyer_userName))) {
				qo.addQuery("obj.user_name", new SysMap("user_name", buyer_userName), "=");
			}
			if (!StringUtils.isNullOrEmpty(configCode)) {
				qo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", CommUtil.null2String(configCode)), "=");
			}
			IPageList pList = this.orderFormService.list(qo);
			if (pList.getResult() != null) {
				List<OrderForm> datas = pList.getResult();
				HSSFWorkbook wb = new HSSFWorkbook();
				ExcelUtil.init();
				linkMap.put("datas", datas);
			}
			outputStream = response.getOutputStream();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = "发货信息-"+sdf.format(new Date());
			String path = CommUtil.getServerRealPathFromRequest(request) + "excel";
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+new String(excel_name.getBytes("GBK"), "iso-8859-1")+".xls"); //显示中文
			ExportSetInfo setInfo = new ExportSetInfo();

			List<String[]> headNames = new ArrayList<String[]>();
			headNames.add(new String[] { "姓名", "地址以及详细地址","手机/电话号码","邮编" });
			List<String[]> fieldNames = new ArrayList<String[]>();
			
			// 带"-":表示需将两个字段的值拼接起来; 带"/":表示两个字段的值取不为空的,主取A字段,A字段就放前面
			fieldNames.add(new String[] {"receiver_Name","receiver_area-receiver_area_info","receiver_telephone/receiver_mobile","receiver_zip"});
			setInfo.setFieldNames(fieldNames);
			setInfo.setTitles(new String[] { "发货信息表格" });
			setInfo.setHeadNames(headNames);
			setInfo.setObjsMap(linkMap);
			// 将需要导出的数据输出到outputStream
			setInfo.setOut(outputStream);
			ExcelUtil.export2Excel(setInfo);
			
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				logger.error("关闭流异常", e);
			}
		}
	}

}
