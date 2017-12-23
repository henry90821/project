package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PayoffLogQueryObject;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PayoffLogTools;

/**
 * 
 * <p>
 * Title: PayoffLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统结算管理类,只要设置一次每月结算次数，系统根据次数自动计算每月的结算日期，
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
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class PayoffLogManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPayoffLogService payofflogService;
	@Autowired
	private PayoffLogTools payofflogTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 结算设置
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "结算设置", value = "/admin/payofflog_set.htm*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping("/admin/payofflog_set.htm")
	public ModelAndView payofflog_set(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/payofflog_set.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE); // 当月总天数
		List list = new ArrayList();
		for (int i = 1; i <= maxDate; i++) {
			list.add(i);
		}
		SysConfig obj = configService.getSysConfig();
		String select = getSelectedDate(obj.getPayoff_count());
		String[] str = select.split(",");
		String ms = "";
		int strLen=str.length;
		for (int i = 0; i < strLen; i++) {
			if (i + 1 == strLen) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		if(obj.getPayoff_count()==0){
			ms="每天";
		}
		mv.addObject("ms",
				"今天是" + DateFormat.getDateInstance(DateFormat.FULL).format(new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		mv.addObject("now", DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));
		mv.addObject("list", list);
		return mv;
	}

	/**
	 * 结算设置切换月结算次数
	 * 
	 * @param request
	 * @param response
	 * @param payoff_type
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "结算设置切换月结算次数", value = "/admin/payofflog_set_ajax.htm*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping("/admin/payofflog_set_ajax.htm")
	public void payofflog_set_ajax(HttpServletRequest request, HttpServletResponse response, String payoff_count)
			throws ClassNotFoundException {
		String selected = getSelectedDate(CommUtil.null2Int(payoff_count));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(selected);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

	}

	private String getSelectedDate(int payoff_count) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int allDate = a.get(Calendar.DATE); // 当月总天数
		String selected = "";

		if (payoff_count == 1) {
			selected = CommUtil.null2String(allDate);
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		} else if (payoff_count == 0) { // 每天
			String commonDay = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27";
			if (allDate == 31) {
				selected = commonDay + ",28,29,30,31";
			}
			if (allDate == 30) {
				selected = commonDay + ",28,29,30";
			}
			if (allDate == 29) {
				selected = commonDay + ",28,29";
			}
			if (allDate == 28) {
				selected = commonDay + ",28";
			}
		}
		return selected;
	}

	/**
	 * 结算设置保存
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "结算设置保存", value = "/admin/payofflog_set_save.htm*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping("/admin/payofflog_set_save.htm")
	public ModelAndView payofflog_set_save(HttpServletRequest request, HttpServletResponse response, String id) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (StringUtils.isNullOrEmpty(id)) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		Date now = new Date();
		int now_date = now.getDate();
		String select = getSelectedDate(CommUtil.null2Int(sysConfig.getPayoff_count()));
		String[] str = select.split(",");
		for (String payoff_date : str) {
			if (CommUtil.null2Int(payoff_date) >= now_date) {
				logger.info(payoff_date);
				now.setDate(CommUtil.null2Int(payoff_date));
				now.setHours(0);
				now.setMinutes(00);
				now.setSeconds(01);
				break;
			}
		}
		sysConfig.setPayoff_date(now);

		if ("".equals(id)) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "结算周期设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/payofflog_set.htm");
		return mv;
	}

	/**
	 * PayoffLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "账单列表", value = "/admin/payofflog_list.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_list.htm")
	public ModelAndView payofflog_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String type, String type_data, String apply_beginTime, String apply_endTime,
			String addTime_beginTime, String addTime_endTime, String begin_price, String end_price, String status,
			String complete_beginTime, String complete_endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/payofflog_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLogQueryObject qo = new PayoffLogQueryObject(currentPage, mv, orderBy, orderType);
		String status2 = "0";
		if (!StringUtils.isNullOrEmpty(status)) {
			status2 = CommUtil.null2String(status);
		}
		qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status2)), "=");
		if (!StringUtils.isNullOrEmpty(type_data)) {
			if (!StringUtils.isNullOrEmpty(type_data)) {
				if ("payoff".equals(type)) {
					qo.addQuery("obj.pl_sn", new SysMap("pl_sn", type_data), "=");
				}
				if ("seller".equals(type)) {
					qo.addQuery("obj.seller.userName", new SysMap("userName", type_data), "=");
				}
				if ("order".equals(type)) {
					qo.addQuery("obj.order_id", new SysMap("order_id", type_data), "=");
				}
			}
			mv.addObject("type", type);
			mv.addObject("type_data", type_data);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		if (StringUtils.isNullOrEmpty(addTime_beginTime)) {
			addTime_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(addTime_endTime)) {
			addTime_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(apply_beginTime)) {
			apply_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(apply_endTime)) {
			apply_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(complete_beginTime)) {
			complete_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(complete_endTime)) {
			complete_endTime = last;
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			qo.addQuery("obj.total_amount", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			qo.addQuery("obj.total_amount", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(status2)) {
			if ("0".equals(status2)) {
				String ends = addTime_endTime + " 23:59:59";
				qo.addQuery("obj.addTime", new SysMap("addTime1", CommUtil.formatDate(addTime_beginTime)), ">=");
				qo.addQuery("obj.addTime", new SysMap("addTime2", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
				qo.setOrderBy("addTime");
			}
			if ("3".equals(status2)) {
				String ends = apply_endTime + " 23:59:59";
				qo.addQuery("obj.apply_time", new SysMap("apply_time2", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
				qo.addQuery("obj.apply_time", new SysMap("apply_time1", CommUtil.formatDate(apply_beginTime)), ">=");
				qo.setOrderBy("apply_time");
			}
			if ("6".equals(status2)) {
				String ends = complete_endTime + " 23:59:59";
				qo.addQuery("obj.complete_time", new SysMap("complete_endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")),
						"<=");
				qo.addQuery("obj.complete_time", new SysMap("complete_beginTime", CommUtil.formatDate(complete_beginTime)),
						">=");
				qo.setOrderBy("complete_time");
			}
		}
		qo.setOrderType("desc");
		IPageList pList = this.payofflogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		mv.addObject("addTime_beginTime", addTime_beginTime);
		mv.addObject("addTime_endTime", addTime_endTime);
		mv.addObject("apply_beginTime", apply_beginTime);
		mv.addObject("apply_endTime", apply_endTime);
		mv.addObject("complete_beginTime", complete_beginTime);
		mv.addObject("complete_endTime", complete_endTime);
		mv.addObject("status", status2);
		return mv;
	}

	/**
	 * 账单详情
	 * 
	 * @return
	 */
	@SecurityMapping(title = "账单详情", value = "/admin/payofflog_view.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_view.htm")
	public ModelAndView payofflog_view(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/payofflog_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLog obj = this.payofflogService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("payofflogTools", payofflogTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 账单结算
	 * 
	 * @return
	 */
	@SecurityMapping(title = "账单结算", value = "/admin/payofflog_edit.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_edit.htm")
	public ModelAndView payofflog_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/payofflog_edit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLog obj = this.payofflogService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 账单结算保存
	 * 
	 * @return
	 */
	@SecurityMapping(title = "账单结算保存", value = "/admin/payofflog_save.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_save.htm")
	public String payofflog_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/payofflog_edit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId()));
		PayoffLog obj = this.payofflogService.getObjById(CommUtil.null2Long(id));
		WebForm wf = new WebForm();
		obj = (PayoffLog) wf.toPo(request, obj);
		obj.setStatus(6);
		obj.setComplete_time(new Date());
		obj.setAdmin(user);
		this.payofflogService.update(obj);
		Store store = obj.getSeller().getStore();
		store.setStore_sale_amount(
				BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), obj.getOrder_total_price()))); // 减少店铺本次结算总销售金额
		store.setStore_commission_amount(
				BigDecimal.valueOf(CommUtil.subtract(store.getStore_commission_amount(), obj.getCommission_amount()))); // 减少店铺本次结算总佣金
		store.setStore_payoff_amount(
				BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), obj.getTotal_amount()))); // 减少店铺本次结算金额
		this.storeService.update(store);
		SysConfig sc = this.configService.getSysConfig();
		sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.add(obj.getTotal_amount(), sc.getPayoff_all_amount()))); // 增加系统总结算（应结算）
		sc.setPayoff_all_amount_reality(
				BigDecimal.valueOf(CommUtil.add(obj.getReality_amount(), sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
		this.configService.update(sc);
		return "redirect:payofflog_list.htm?currentPage=" + currentPage;
	}

	/**
	 * PayoffLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "账单列表", value = "/admin/payofflog_stat.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_stat.htm")
	public void payofflog_stat(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type, String type_data, String apply_beginTime, String apply_endTime,
			String addTime_beginTime, String addTime_endTime, String begin_price, String end_price, String status,
			String complete_beginTime, String complete_endTime) {
		PayoffLogQueryObject qo = new PayoffLogQueryObject();
		String status2 = "0";
		if (!StringUtils.isNullOrEmpty(status)) {
			status2 = CommUtil.null2String(status);
		}
		qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status2)), "=");
		if (!StringUtils.isNullOrEmpty(type)) {
			if (!StringUtils.isNullOrEmpty(type_data)) {
				if ("payoff".equals(type)) {
					qo.addQuery("obj.pl_sn", new SysMap("pl_sn", type_data), "=");
				}
				if ("seller".equals(type)) {
					qo.addQuery("obj.seller.userName", new SysMap("userName", type_data), "=");
				}
				if ("order".equals(type)) {
					qo.addQuery("obj.order_id", new SysMap("order_id", type_data), "=");
				}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		if (StringUtils.isNullOrEmpty(addTime_beginTime)) {
			addTime_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(addTime_endTime)) {
			addTime_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(apply_beginTime)) {
			apply_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(apply_endTime)) {
			apply_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(complete_beginTime)) {
			complete_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(complete_endTime)) {
			complete_endTime = last;
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			qo.addQuery("obj.total_amount", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			qo.addQuery("obj.total_amount", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(status2)) {
			if (StringUtils.isNullOrEmpty(status2)) {
				qo.addQuery("obj.addTime", new SysMap("addTime1", CommUtil.formatDate(addTime_beginTime)), ">=");
				qo.addQuery("obj.addTime", new SysMap("addTime2", CommUtil.formatDate(addTime_endTime)), "<=");
				qo.setOrderBy("addTime");
			}
			if ("3".equals(status2)) {
				qo.addQuery("obj.apply_time", new SysMap("apply_time2", CommUtil.formatDate(apply_endTime)), "<=");
				qo.addQuery("obj.apply_time", new SysMap("apply_time1", CommUtil.formatDate(apply_beginTime)), ">=");
				qo.setOrderBy("apply_time");
			}
			if ("6".equals(status2)) {
				qo.addQuery("obj.complete_time", new SysMap("complete_endTime", CommUtil.formatDate(complete_endTime)),
						"<=");
				qo.addQuery("obj.complete_time", new SysMap("complete_beginTime", CommUtil.formatDate(complete_beginTime)),
						">=");
				qo.setOrderBy("complete_time");
			}
		}
		qo.setOrderType("desc");
		IPageList pList = this.payofflogService.list(qo);
		List<PayoffLog> objs = pList.getResult();
		double all_order_price = 0.00; // 账单总金额
		double all_commission_price = 0.00; // 账单总佣金
		double all_total_amount = 0.00; // 账单总结算金额
		double all_reality_amount = 0.00;//实际结算金额
		boolean code = false;
		if (objs != null) {
			code = true;
			for (PayoffLog obj : objs) {
				all_order_price = CommUtil.add(all_order_price, obj.getOrder_total_price());
				all_commission_price = CommUtil.add(all_commission_price, obj.getCommission_amount());
				all_total_amount = CommUtil.add(all_total_amount, obj.getTotal_amount());
				all_reality_amount = CommUtil.add(all_reality_amount, obj.getReality_amount());
			}
		}
		Map map = new HashMap();
		map.put("all_order_price", all_order_price);
		map.put("all_commission_price", all_commission_price);
		map.put("all_total_amount", all_total_amount);
		map.put("all_reality_amount", all_reality_amount);
		if (objs != null) {
			map.put("data_size", objs.size());
		}
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
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */

	// 以excel形式导出账单数据
	@SecurityMapping(title = "账单数据导出", value = "/admin/payofflog_excel.htm*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping("/admin/payofflog_excel.htm")
	public void payofflog_excel(HttpServletRequest request, HttpServletResponse response, String type, String type_data,
			String apply_beginTime, String apply_endTime, String addTime_beginTime, String addTime_endTime,
			String begin_price, String end_price, String status, String complete_beginTime, String complete_endTime) {
		PayoffLogQueryObject qo = new PayoffLogQueryObject();
		qo.setPageSize(1000000000);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		String status2 = "0";
		if (!StringUtils.isNullOrEmpty(status)) {
			status2 = CommUtil.null2String(status);
		}
		qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status2)), "=");
		if (!StringUtils.isNullOrEmpty(type)) {
			if (!StringUtils.isNullOrEmpty(type_data)) {
				if ("payoff".equals(type)) {
					qo.addQuery("obj.pl_sn", new SysMap("pl_sn", type_data), "=");
				}
				if ("seller".equals(type)) {
					qo.addQuery("obj.seller.userName", new SysMap("userName", type_data), "=");
				}
				if ("order".equals(type)) {
					qo.addQuery("obj.order_id", new SysMap("order_id", type_data), "=");
				}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		if (StringUtils.isNullOrEmpty(addTime_beginTime)) {
			addTime_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(addTime_endTime)) {
			addTime_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(apply_beginTime)) {
			apply_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(apply_endTime)) {
			apply_endTime = last;
		}
		if (StringUtils.isNullOrEmpty(complete_beginTime)) {
			complete_beginTime = first;
		}
		if (StringUtils.isNullOrEmpty(complete_endTime)) {
			complete_endTime = last;
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			qo.addQuery("obj.total_amount", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			qo.addQuery("obj.total_amount", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(status2)) {
			if ("0".equals(status2)) {
				String ends = addTime_endTime + " 23:59:59";
				qo.addQuery("obj.addTime", new SysMap("addTime1", CommUtil.formatDate(addTime_beginTime)), ">=");
				qo.addQuery("obj.addTime", new SysMap("addTime2", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
				qo.setOrderBy("addTime");
			}
			if ("3".equals(status2)) {
				String ends = apply_endTime + " 23:59:59";
				qo.addQuery("obj.apply_time", new SysMap("apply_time2", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
				qo.addQuery("obj.apply_time", new SysMap("apply_time1", CommUtil.formatDate(apply_beginTime)), ">=");
				qo.setOrderBy("apply_time");
			}
			if ("6".equals(status2)) {
				String ends = complete_endTime + " 23:59:59";
				qo.addQuery("obj.complete_time", new SysMap("complete_endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")),
						"<=");
				qo.addQuery("obj.complete_time", new SysMap("complete_beginTime", CommUtil.formatDate(complete_beginTime)),
						">=");
				qo.setOrderBy("complete_time");
			}
		}
		qo.setOrderType("desc");
		IPageList pList = this.payofflogService.list(qo);
		if (pList.getResult() != null) {
			List<PayoffLog> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet("结算账单");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1, 2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			int colIndex = 0;
			sheet.setColumnWidth(colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 4000);
			
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			
			sheet.setColumnWidth(++colIndex, 4000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			if("6".equals(status)){
				sheet.setColumnWidth(++colIndex, 6000);
			}
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 6000);
			sheet.setColumnWidth(++colIndex, 8000);
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colIndex));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "结算账单";
			Date time1 = null;
			Date time2 = null;
			if (!StringUtils.isNullOrEmpty(status2)) {
				if (StringUtils.isNullOrEmpty(status2) || "0".equals(status2)) {
					title = "未结算账单";
					time1 = CommUtil.formatDate(addTime_beginTime);
					time2 = CommUtil.formatDate(addTime_endTime);
				}
				if ("3".equals(status2)) {
					title = "可结算账单";
					time1 = CommUtil.formatDate(apply_beginTime);
					time2 = CommUtil.formatDate(apply_endTime);
				}
				if ("6".equals(status2)) {
					title = "已结算账单";
					time1 = CommUtil.formatDate(complete_beginTime);
					time2 = CommUtil.formatDate(complete_endTime);
				}
			}
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1) + " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle() + title + "（" + time + "）");
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true); // 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			HSSFFont font3=wb.createFont();
			font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
			HSSFCellStyle style3 = wb.createCellStyle();
			style3.setFont(font3); 
			style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			row = sheet.createRow(1);

			int cellIndex = 0;

			cell = row.createCell(cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单流水号");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("商家名称");

			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("商家银行账号");

			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("商家银行名称");
			
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("开户行名称");

			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单说明");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单入账时间");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("申请结算时间");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("完成结算时间");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单总金额（元）");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单总佣金（元）");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("运费（元）");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("账单应结算（元）");
			if("6".equals(status)){
				cell = row.createCell(++cellIndex);
				cell.setCellStyle(style3);
				cell.setCellValue("实际结算（元）");
			}
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("操作财务");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("操作管理员");
			cell = row.createCell(++cellIndex);
			cell.setCellStyle(style3);
			cell.setCellValue("结算备注");
			double all_order_price = 0.00; // 账单总销售金额
			double all_commission_amount = 0.00; // 账单总佣金
			double all_total_amount = 0.00; // 账单总结算
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);
				// 设置单元格的样式格式
				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getPl_sn());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getSeller().getUserName());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getSeller().getStore().getBank_c_account());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getSeller().getStore().getBank_type_name());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getSeller().getStore().getBank_name());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getPl_info());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getApply_time()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2).getComplete_time()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getOrder_total_price()));
				all_order_price = CommUtil.add(all_order_price, datas.get(j - 2).getOrder_total_price()); // 计算账单总订单价格

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getCommission_amount()));
				all_commission_amount = CommUtil.add(all_commission_amount, datas.get(j - 2).getCommission_amount());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getShip_price()));//运费

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getTotal_amount()));
				all_total_amount = CommUtil.add(all_total_amount, datas.get(j - 2).getTotal_amount());
				
				if("6".equals(status)){
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getReality_amount()));//实际结算
				}

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getFinance_userName()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				if (datas.get(j - 2).getAdmin() != null) {
					cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getAdmin().getUserName()));
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getPayoff_remark()));
			}
			// 设置底部统计信息
			int m = datas.size() + 2;
			row = sheet.createRow(m);
			// 设置单元格的样式格式
			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总销售金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总销售佣金：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_commission_amount);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总结算金额：");

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
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}