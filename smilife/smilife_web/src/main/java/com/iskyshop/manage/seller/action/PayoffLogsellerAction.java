package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PayoffLogQueryObject;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
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
 * Description: 系统结算管理类,商家可以和平台进行结算
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
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class PayoffLogsellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private PayoffLogTools payofflogTools;
	@Autowired
	private IOrderFormService orderFormServer;
	@Autowired
	private IOrderFormService orderformService;

	/**
	 * 结算列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "结算列表页", value = "/seller/payofflog_list.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_list.htm")
	public ModelAndView payofflog_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String beginTime, String endTime, String pl_sn, String order_id,
			String status) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/payofflog_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLogQueryObject qo = new PayoffLogQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(pl_sn)) {
			qo.addQuery("obj.pl_sn", new SysMap("obj_pl_sn", pl_sn), "=");
			mv.addObject("pl_sn", pl_sn);
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			qo.addQuery("obj.order_id", new SysMap("obj_order_id", order_id), "=");
			mv.addObject("order_id", order_id);
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			String begin = beginTime + " 00:00:00";
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(begin, "yyyy-MM-dd hh:mm:ss")), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		int st = 0;
		if (!StringUtils.isNullOrEmpty(status)) {
			if ("not".equals(status)) {
				st = 0;
			}
			if ("underway".equals(status)) {
				st = 3;
			}
			if ("already".equals(status)) {
				st = 6;
			}
		} else {
			status = "not";
		}
		qo.addQuery("obj.status", new SysMap("status", st), "=");
		mv.addObject("status", status);
		qo.setPageSize(20);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.seller.id", new SysMap("seller_id", user.getId()), "=");
		IPageList pList = this.payoffLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		//
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
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		if(obj.getPayoff_count()==0){
			ms="每天";
		}
		mv.addObject("payoff_mag_default",
				"今天是" + DateFormat.getDateInstance(DateFormat.FULL).format(new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		return mv;
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
	 * 验证今天是否为结算日，是返回true，不是返回false
	 * 
	 * @return
	 */
	private boolean validatePayoffDate() {
		boolean payoff = false;
		Date Payoff_data = this.configService.getSysConfig().getPayoff_date();
		Date now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		Date next = new Date();
		next.setDate(next.getDate() + 1);
		next.setHours(0);
		next.setMinutes(0);
		next.setSeconds(0);
		if (Payoff_data.after(now) && Payoff_data.before(next)) {
			payoff = true;
		}
		return payoff;
	}

	private void check_payoff_list() {
		// 系统处理最近结算日期
		Map params = new HashMap();
		SysConfig sysConfig = this.configService.getSysConfig();
		params.clear();
		params.put("seller_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("status", 0);
		params.put("PayoffTime", sysConfig.getPayoff_date());
		List<PayoffLog> payofflogs = this.payoffLogService.query(
				"select obj from PayoffLog obj where obj.status=:status and obj.addTime<:PayoffTime and obj.seller.id=:seller_id order by addTime desc",
				params, -1, -1); // 本店结算日之前的所有未结算账单
		for (PayoffLog obj : payofflogs) {
			OrderForm of = this.orderformService.getObjById(CommUtil.null2Long(obj.getO_id()));
			Date Payoff_date = this.configService.getSysConfig().getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			if (of.getOrder_cat() == 2) {
				if (of.getOrder_status() == 20) { // 团购消费码订单
					obj.setStatus(1); // 设置当天可结算的账单
				}
			}
			 // 72 V2.0.1：调整商家结算时间为用户确认收货后 判断不正确
			if (of.getOrder_cat() == 0) {
				if (of.getOrder_status() >= 40) { // 账单对应订单已经评价完成或者不可评价时
					obj.setStatus(1); // 设置当天可结算的账单
				}
				if (obj.getPayoff_type() == -1) { // 账单为退款账单，系统自动判定该退款账单为申请状态
					if (of.getOrder_status() >= 40) { // 账单对应订单已经评价完成或者不可评价时
						obj.setStatus(3);
						obj.setApply_time(new Date());
					}
				}
			}
			this.payoffLogService.update(obj);
		}

	}

	/**
	 * 可结算列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "可结算列表页", value = "/seller/payofflog_ok_list.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_ok_list.htm")
	public ModelAndView payofflog_ok_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String beginTime, String endTime, String pl_sn, String order_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/payofflog_ok_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean verify = this.validatePayoffDate();
		if (verify) { // 如果今天是结算日，更新可结算账单
			this.check_payoff_list();
		}
		PayoffLogQueryObject qo = new PayoffLogQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(pl_sn)) {
			qo.addQuery("obj.pl_sn", new SysMap("obj_pl_sn", pl_sn), "=");
			mv.addObject("pl_sn", pl_sn);
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			qo.addQuery("obj.order_id", new SysMap("obj_order_id", order_id), "=");
			mv.addObject("order_id", order_id);
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		qo.addQuery("obj.status", new SysMap("status", 1), "=");
		qo.setPageSize(20);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.seller.id", new SysMap("seller_id", user.getId()), "=");
		IPageList pList = this.payoffLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		boolean payoff = this.validatePayoffDate();
		mv.addObject("payoff", payoff);
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
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		if(obj.getPayoff_count()==0){
			ms="每天";
		}
		mv.addObject("payoff_mag_default",
				"今天是" + DateFormat.getDateInstance(DateFormat.FULL).format(new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		return mv;
	}

	/**
	 * 账单详情
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "账单详情", value = "/seller/payofflog_info.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_info.htm")
	public ModelAndView payofflog_info(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String op) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/payofflog_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			PayoffLog payofflog = this.payoffLogService.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (user.getId().equals(payofflog.getSeller().getId())) {
				mv.addObject("payofflogTools", payofflogTools);
				mv.addObject("obj", payofflog);
				mv.addObject("currentPage", currentPage);
				mv.addObject("op", op);
			} else {
				mv.addObject("list_url", CommUtil.getURL(request) + "/payofflog_list.htm");
				mv.addObject("op_title", "您没有该账单");
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
			}
		} else {
			mv.addObject("list_url", CommUtil.getURL(request) + "/payofflog_list.htm");
			mv.addObject("op_title", "账单不存在");
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		return mv;
	}

	/**
	 * 结算账单
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "账单结算", value = "/seller/payofflog_edit.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_edit.htm")
	public String payofflog_edit(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		for (String id : mulitId.split(",")) {
			if (!StringUtils.isNullOrEmpty(id)) {
				PayoffLog obj = this.payoffLogService.getObjById(CommUtil.null2Long(id));
				if (obj != null) {
					User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (user.getId().equals(obj.getSeller().getId()) && obj.getStatus() == 1) {
						OrderForm of = this.orderFormServer.getObjById(CommUtil.null2Long(obj.getO_id()));
						if (of != null) {
							boolean payoff = this.validatePayoffDate();
							boolean goods = false; // 购物
							boolean group = false; // 团购
							//658 商家后台：可结算数据点击结算失败系统报错 点击结算还会进行一次判断，并且判断值能是评价完成的才可以评价
							if (of.getOrder_status() >= 40 && payoff) {
								goods = true;
							}
							if (of.getOrder_cat() == 2) {
								if (of.getOrder_status() == 20 && payoff) { // 团购消费码订单
									group = true;
								}
							}
							if (goods || group) { // 已经完成的订单，并且今天为结算日
								obj.setStatus(3); // 设置结算中
								obj.setApply_time(new Date());
								this.payoffLogService.update(obj);
							}
						}
					}
				}
			}
		}
		return "redirect:payofflog_ok_list.htm?currentPage" + currentPage;
	}

	/**
	 * 批量统计账单
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "批量统计", value = "/seller/payofflog_ajax.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_ajax.htm")
	public void payofflog_ajax(HttpServletRequest request, HttpServletResponse response, String mulitId)
			throws ClassNotFoundException {
		String[] ids = mulitId.split(",");
		double orderTotalPrice = 0.00; // 账单总订单金额
		double commissionAmount = 0.00; // 账单总佣金
		double totalAmount = 0.00; // 账单总结算金额
		double reality_amount = 0.00; //实际结算金额
		boolean error = true;
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				PayoffLog obj = this.payoffLogService.getObjById(Long.parseLong(id));
				if (obj != null) {
					User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (user.getId().equals(obj.getSeller().getId())) {
						totalAmount = CommUtil.add(totalAmount, obj.getTotal_amount());
						commissionAmount = CommUtil.add(commissionAmount, obj.getCommission_amount());
						orderTotalPrice = CommUtil.add(orderTotalPrice, obj.getOrder_total_price());
						reality_amount = CommUtil.add(reality_amount, obj.getReality_amount());
					} else {
						error = false;
						break;
					}
				} else {
					error = false;
					break;
				}
			}
		}
		Map map = new HashMap();
		map.put("order_total_price", orderTotalPrice);
		map.put("commission_amount", commissionAmount);
		map.put("total_amount", totalAmount);
		map.put("reality_amount", reality_amount);
		map.put("error", error);
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

	// 以excel形式导出账单数据
	@SecurityMapping(title = "账单数据导出", value = "/seller/payofflog_excel.htm*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_excel.htm")
	public void payofflog_excel(HttpServletRequest request, HttpServletResponse response, String beginTime, String endTime,
			String pl_sn, String order_id, String status) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		PayoffLogQueryObject qo = new PayoffLogQueryObject();
		qo.addQuery("obj.seller.id", new SysMap("seller_id", user.getId()), "=");
		qo.setPageSize(1000000000);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		String status2 = "0";
		if (!StringUtils.isNullOrEmpty(status)) {
			status2 = CommUtil.null2String(status);
		}
		if (!StringUtils.isNullOrEmpty(pl_sn)) {
			qo.addQuery("obj.pl_sn", new SysMap("obj_pl_sn", pl_sn), "=");
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			qo.addQuery("obj.order_id", new SysMap("obj_order_id", order_id), "=");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			String begin = beginTime + " 00:00:00";
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(begin, "yyyy-MM-dd hh:mm:ss")), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		int st = 0;
		if (!StringUtils.isNullOrEmpty(status)) {
			if ("not".equals(status)) {
				st = 0;
			}
			if ("underway".equals(status)) {
				st = 3;
			}
			if ("already".equals(status)) {
				st = 6;
			}
		} else {
			status = "not";
		}
		qo.addQuery("obj.status", new SysMap("status", st), "=");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		qo.setOrderType("desc");
		IPageList pList = this.payoffLogService.list(qo);
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
			int count = 0;
			sheet.setColumnWidth(count, 6000);
			sheet.setColumnWidth(++count, 4000);
			sheet.setColumnWidth(++count, 4000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			if("already".equals(status)){
				sheet.setColumnWidth(++count, 6000);
			}
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 6000);
			sheet.setColumnWidth(++count, 8000);
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, count));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "结算账单";
			String time = CommUtil.null2String(CommUtil.formatDate(beginTime) + " - " + CommUtil.formatDate(endTime));
			cell.setCellValue(this.configService.getSysConfig().getTitle() + title + "（" + time + "）");
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true); // 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			int colIndex = 0;
			cell = row.createCell(colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单流水号");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("商家名称");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单说明");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单入账时间");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("申请结算时间");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("完成结算时间");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总金额（元）");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总佣金（元）");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("运费（元）");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("账单应结算（元）");
			if("already".equals(status)){
				cell = row.createCell(++colIndex);
				cell.setCellStyle(style2);
				cell.setCellValue("实际结算（元）");
			}
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("操作财务");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
			cell.setCellValue("操作管理员");
			cell = row.createCell(++colIndex);
			cell.setCellStyle(style2);
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
				
				if("already".equals(status)){
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
			String excelName = sdf.format(new Date());
			try {
				String path = CommUtil.getServerRealPathFromRequest(request) + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition", "attachment;filename=" + excelName + ".xls");
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
}