package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.IntegralGoodsOrderQueryObject;
import com.iskyshop.foundation.domain.query.IntegralGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: IntegralGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 积分商品管理控制器，积分商品由平台运营商发布
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
 * @author jy
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class IntegralGoodsManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralGoodsService integralgoodsService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private FTPServerTools ftpTools;

	/**
	 * IntegralGoods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "积分礼品列表", value = "/admin/integral_goods.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods.htm")
	public ModelAndView integral_goods(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String ig_goods_name, String ig_goods_sn, String ig_show) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoodsQueryObject qo = new IntegralGoodsQueryObject(currentPage, mv, orderBy, orderType);
			if (!StringUtils.isNullOrEmpty(ig_goods_name)) {
				qo.addQuery("obj.ig_goods_name", new SysMap("ig_goods_name", "%" + ig_goods_name.trim() + "%"), "like");
			}
			if (!StringUtils.isNullOrEmpty(ig_show)) {
				qo.addQuery("obj.ig_show", new SysMap("ig_show", CommUtil.null2Boolean(ig_show)), "=");
			}
			if (!StringUtils.isNullOrEmpty(ig_goods_sn)) {
				qo.addQuery("obj.ig_goods_sn", new SysMap("ig_goods_sn", CommUtil.null2String(ig_goods_sn)), "=");
			}
			IPageList pList = this.integralgoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("ig_goods_name", ig_goods_name);
			mv.addObject("ig_goods_sn", ig_goods_sn);
			mv.addObject("ig_show", ig_show);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * integralgoods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "积分礼品添加", value = "/admin/integral_goods_add.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods_add.htm")
	public ModelAndView integral_goods_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_goods_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			mv.addObject("currentPage", currentPage);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);
			mv.addObject("default_begin_time", CommUtil.formatShortDate(cal.getTime()));
			cal.add(Calendar.DATE, 1);
			mv.addObject("default_end_time", CommUtil.formatShortDate(cal.getTime()));
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * integralgoods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "积分礼品编辑", value = "/admin/integral_goods_edit.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods_edit.htm")
	public ModelAndView integral_goods_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_goods_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			if (!StringUtils.isNullOrEmpty(id)) {
				IntegralGoods integralgoods = this.integralgoodsService.getObjById(Long.parseLong(id));
				mv.addObject("obj", integralgoods);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", true);
				if (integralgoods.isIg_time_type()) {
					mv.addObject("default_begin_time", CommUtil.formatShortDate(integralgoods.getIg_begin_time()));
					mv.addObject("default_end_time", CommUtil.formatShortDate(integralgoods.getIg_end_time()));
				} else {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, 1);
					mv.addObject("default_begin_time", CommUtil.formatShortDate(cal.getTime()));
					cal.add(Calendar.DATE, 1);
					mv.addObject("default_end_time", CommUtil.formatShortDate(cal.getTime()));
				}
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * integralgoods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "积分礼品保存", value = "/admin/integral_goods_save.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods_save.htm")
	public ModelAndView integral_goods_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String begin_hour, String end_hour, String list_url, String add_url, String user_level) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			WebForm wf = new WebForm();
			IntegralGoods goods = null;
			if (StringUtils.isNullOrEmpty(id)) {
				goods = wf.toPo(request, IntegralGoods.class);
				goods.setAddTime(new Date());
				goods.setIg_goods_sn("gift" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ SecurityUserHolder.getCurrentUser().getId());
			} else {
				IntegralGoods obj = this.integralgoodsService.getObjById(Long.parseLong(id));
				goods = (IntegralGoods) wf.toPo(request, obj);
			}
			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
					+ File.separator + "cache";
			Map map = new HashMap();
			String fileName = "";
			if (goods.getIg_goods_img() != null) {
				fileName = goods.getIg_goods_img().getName();
			}
			Accessory acc = null;
			try {
				map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, fileName, null);
				if (StringUtils.isNullOrEmpty(fileName)) {
					if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
						acc = new Accessory();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						acc.setPath(uploadFilePath + "/cache");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.save(acc);
						goods.setIg_goods_img(acc);
					}
				} else {
					if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
						acc = goods.getIg_goods_img();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						acc.setPath(uploadFilePath + "/cache");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.update(acc);
					}
				}
			} catch (IOException e) {
				logger.error(e);
			}
			
			if(null != acc){
				// 同步生成小图片
				String ext = acc.getExt().indexOf(".") < 0 ? "." + acc.getExt() : acc.getExt();
				String source = CommUtil.getServerRealPathFromRequest(request) + acc.getPath() + File.separator
						+ acc.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService.getSysConfig().getSmallWidth(),
						this.configService.getSysConfig().getSmallHeight());
				// 上传小图
				this.ftpTools.systemUpload(goods.getIg_goods_img().getName() + "_small" + ext, "/integral_goods");
				// 上传原图并更新路径
				String new_url = this.ftpTools.systemUpload(goods.getIg_goods_img().getName(), "/integral_goods");
				goods.getIg_goods_img().setPath(new_url);
				this.accessoryService.update(goods.getIg_goods_img());
			}
			
			Calendar cal = Calendar.getInstance();
			if (goods.getIg_begin_time() != null) {
				cal.setTime(goods.getIg_begin_time());
				cal.add(Calendar.HOUR, CommUtil.null2Int(begin_hour));
				goods.setIg_begin_time(cal.getTime());
			}
			if (goods.getIg_end_time() != null) {
				cal.setTime(goods.getIg_end_time());
				cal.add(Calendar.HOUR, CommUtil.null2Int(end_hour));
				goods.setIg_end_time(cal.getTime());
			}
			goods.setIg_user_Level(CommUtil.null2Int(user_level));
			if (StringUtils.isNullOrEmpty(id)) {
				this.integralgoodsService.save(goods);
			} else {
				this.integralgoodsService.update(goods);
			}

			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存积分商品成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分礼品删除", value = "/admin/integral_goods_del.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods_del.htm")
	public String integral_goods_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				IntegralGoods integralgoods = this.integralgoodsService.getObjById(Long.parseLong(id));
				this.integralgoodsService.delete(Long.parseLong(id));
				try {
					this.ftpTools.systemDeleteFtpImg(integralgoods.getIg_goods_img());
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return "redirect:integral_goods.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "积分礼品Ajax更新", value = "/admin/integral_goods_ajax.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_goods_ajax.htm")
	public void integral_goods_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		IntegralGoods ig = this.integralgoodsService.getObjById(Long.parseLong(id));
		Field[] fields = IntegralGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(ig);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				}
				if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!StringUtils.isNullOrEmpty(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.integralgoodsService.update(ig);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			logger.error(e);
		}

	}

	@SecurityMapping(title = "积分礼品兑换列表", value = "/admin/integral_order.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order.htm")
	public ModelAndView integral_order(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String igo_order_sn, String userName, String igo_payment, String igo_status) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(currentPage, mv, orderBy, orderType);
			if (!StringUtils.isNullOrEmpty(igo_order_sn)) {
				qo.addQuery("obj.igo_order_sn", new SysMap("igo_order_sn", "%" + igo_order_sn.trim() + "%"), "like");
			}
			if (!StringUtils.isNullOrEmpty(userName)) {
				qo.addQuery("obj.igo_user.userName", new SysMap("uesrName", userName), "like");
			}
			if ("alipay".equals(CommUtil.null2String(igo_payment))) {
				String scope = "(obj.igo_payment =:alipay_pc or obj.igo_payment =:alipay_app or obj.igo_payment =:alipay_wap)";
				HashMap<String,String> params = new HashMap<String,String>();
				params.put("alipay_pc", "alipay");
				params.put("alipay_app", "alipay_app");
				params.put("alipay_wap", "alipay_wap");
				qo.addQuery(scope, params);
				
			} else if ("wx".equals(CommUtil.null2String(igo_payment))) {
				String scope = "(obj.igo_payment =:wx_app or obj.igo_payment =:wx_pay)";
				HashMap<String,String> params = new HashMap<String,String>();
				params.put("wx_app", "wx_app");
				params.put("wx_pay", "wx_pay");
				qo.addQuery(scope, params);
				
			} else if (!StringUtils.isNullOrEmpty(igo_payment)) {
				qo.addQuery("obj.igo_payment", new SysMap("igo_payment", igo_payment.trim()), "=");
			}
			if (!StringUtils.isNullOrEmpty(igo_status)) {
				qo.addQuery("obj.igo_status", new SysMap("igo_status", CommUtil.null2Int(igo_status)), "=");
			}
			IPageList pList = this.integralGoodsOrderService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("igo_order_sn", igo_order_sn);
			mv.addObject("userName", userName);
			mv.addObject("igo_payment", igo_payment);
			mv.addObject("igo_status", igo_status);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分礼品兑换详情", value = "/admin/integral_order_view.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_view.htm")
	public ModelAndView integral_order_view(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_order_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			mv.addObject("express_company_name",
					this.orderFormTools.queryExInfo(obj.getIgo_express_info(), "express_company_name"));
			mv.addObject("orderFormTools", orderFormTools);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "取消积分订单", value = "/admin/integral_order_cancel.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_cancel.htm")
	public ModelAndView integral_order_cancel(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			obj.setIgo_status(-1);
			this.integralGoodsOrderService.update(obj);
			// 执行库存减少
			List<IntegralGoods> igs = this.orderFormTools.query_integral_all_goods(CommUtil.null2String(obj.getId()));
			for (IntegralGoods ig : igs) {
				int count = this.orderFormTools.query_integral_one_goods_count(obj, CommUtil.null2String(obj.getId()));
				ig.setIg_goods_count(ig.getIg_goods_count() + count);
				this.integralgoodsService.update(ig);
			}
			User user = obj.getIgo_user();
			user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
			this.userService.update(user);
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
			log.setIntegral(obj.getIgo_total_integral());
			log.setIntegral_user(obj.getIgo_user());
			log.setOperate_user(SecurityUserHolder.getCurrentUser());
			log.setType("integral_order");
			this.integralLogService.save(log);
			mv.addObject("op_title", "积分兑换取消成功");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/integral_order.htm");
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单确认付款", value = "/admin/integral_order_payok.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_payok.htm")
	public ModelAndView integral_order_payok(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			obj.setIgo_status(20);
			this.integralGoodsOrderService.update(obj);
			mv.addObject("op_title", "确认收款成功");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/integral_order.htm");
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单删除", value = "/admin/integral_order_del.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_del.htm")
	public ModelAndView integral_order_del(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			if (obj.getIgo_status() == -1) {
				this.integralGoodsOrderService.delete(obj.getId());
			}
			mv.addObject("op_title", "删除兑换订单成功");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/integral_order.htm");
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单费用调整", value = "/admin/integral_order_fee.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_fee.htm")
	public ModelAndView integral_order_fee(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_order_fee.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单费用调整保存", value = "/admin/integral_order_fee_save.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_fee_save.htm")
	public String integral_order_fee_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String igo_trans_fee) {
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			obj.setIgo_trans_fee(BigDecimal.valueOf(CommUtil.null2Double(igo_trans_fee)));
			if (CommUtil.null2Double(obj.getIgo_trans_fee()) == 0) {
				obj.setIgo_pay_time(new Date());
				obj.setIgo_status(20);
			}
			this.integralGoodsOrderService.update(obj);
		}
		return "redirect:integral_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "确认发货", value = "/admin/integral_order_ship.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_ship.htm")
	public ModelAndView integral_order_ship(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/integral_order_ship.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			Map params = new HashMap();
			params.put("status", 0);
			List<ExpressCompany> expressCompanys = this.expressCompanyService.query(
					"select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
					params, -1, -1);
			mv.addObject("expressCompanys", expressCompanys);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "确认发货保存", value = "/admin/integral_order_ship_save.htm*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping("/admin/integral_order_ship_save.htm")
	public String integral_order_ship_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String igo_ship_code, String igo_ship_content, String ec_id) {
		IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
		ExpressCompany ec = this.expressCompanyService.getObjById(CommUtil.null2Long(ec_id));
		if (this.configService.getSysConfig().isIntegralStore()) {
			obj.setIgo_status(30);
			obj.setIgo_ship_code(igo_ship_code);
			if (obj.getIgo_ship_time() == null) {// 为空存进发货时间，不为空表示是修改物流，不操作。
				obj.setIgo_ship_time(new Date());
				String msg_content = "您积分订单号为：" + obj.getIgo_order_sn() + "的商品已发货，物流单号为：" + igo_ship_code + "，请注意查收！";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(obj.getIgo_user());
				this.messageService.save(msg);
			} else {// 修改物流单号
				String msg_content = "您积分订单号为：" + obj.getIgo_order_sn() + "的商品已经更改物流信息，新物流单号为：" + igo_ship_code + "，请注意查收！";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(obj.getIgo_user());
				this.messageService.save(msg);
			}
			obj.setIgo_ship_content(igo_ship_content);
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = Json.toJson(json_map, JsonFormat.compact());
			obj.setIgo_express_info(express_json);
			this.integralGoodsOrderService.update(obj);
		}
		return "redirect:integral_order.htm?currentPage=" + currentPage;
	}

}
