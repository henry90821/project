package com.iskyshop.manage.admin.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.RefundTools;
import com.iskyshop.pay.chinapay.ChinaPayCommon;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.pay.tools.RetPayTools;
import com.iskyshop.pay.weixi.refund.RefundResBean;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;
import com.tydic.framework.util.StringUtil;

/**
 * 
 * <p>
 * Title: OrderManageAction.java
 * </p>
 * 
 * <p>
 * Description:商城后台订单管理器
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
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class OrderManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private FeeManageConnector feeManageConnector;
	@Autowired
	private IFeeManageservice feeManageservice;
	@Autowired
	private RefundTools refundTools;
	@Autowired
	private RetPayTools retPayTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IPredepositLogService predepositLogService; 
	@Autowired
	private IpayCenterService payCenterService; 
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;  
	@Autowired
	private IGroupInfoService groupInfoService;

	@SecurityMapping(title = "订单设置", value = "/admin/set_order_confirm.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping("/admin/set_order_confirm.htm")
	public ModelAndView set_order_confirm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_order_confirm.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "订单设置保存", value = "/admin/set_order_confirm_save.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping("/admin/set_order_confirm_save.htm")
	public ModelAndView set_order_confirm_save(HttpServletRequest request, HttpServletResponse response, String id,
			String auto_order_confirm, String auto_order_notice, String auto_order_return, String auto_order_evaluate,
			String grouplife_order_return, String evaluate_edit_deadline, String evaluate_add_deadline,
			String pay_order_time) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig config = null;
		if (StringUtils.isNullOrEmpty(id)) {
			config = wf.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) wf.toPo(request, obj);
		}
		config.setAuto_order_confirm(CommUtil.null2Int(auto_order_confirm));
		config.setAuto_order_notice(CommUtil.null2Int(auto_order_notice));
		config.setAuto_order_return(CommUtil.null2Int(auto_order_return));
		config.setAuto_order_evaluate(CommUtil.null2Int(auto_order_evaluate));
		config.setGrouplife_order_return(CommUtil.null2Int(grouplife_order_return));
		config.setEvaluate_edit_deadline(CommUtil.null2Int(evaluate_edit_deadline));
		config.setEvaluate_add_deadline(CommUtil.null2Int(evaluate_add_deadline));
		config.setPayOrderTime(CommUtil.null2Int(pay_order_time));
		if (StringUtils.isNullOrEmpty(id)) {
			this.configService.save(config);
		} else {
			this.configService.update(config);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "订单设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_order_confirm.htm");
		return mv;
	}

	@SecurityMapping(title = "订单列表", value = "/admin/order_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_list.htm")
	public ModelAndView order_list(HttpServletRequest request, HttpServletResponse response, String order_status,
			String type, String type_data, String payment, String beginTime, String endTime, String begin_price,
			String end_price, String currentPage, String receiver_Name, String startPayTime, String endPayTime,
			String startConfirmTime, String endConfirmTime) {
		ModelAndView mv = new JModelAndView("admin/blue/order_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 0), "=");// 这里只查询商品订单，手机充值订单独立出来
		if (!StringUtils.isNullOrEmpty(order_status)) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status", CommUtil.null2Int(order_status)), "=");
		}
		if (!StringUtils.isNullOrEmpty(type_data)) {
			if ("store".equals(type)) {
				if ("自营".equals(type_data) || "自营商品".equals(type_data)) {
					ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
				} else {
					ofqo.addQuery("obj.store_name", new SysMap("store_name", type_data), "=");
				}
			}
			if ("buyer".equals(type)) {
				ofqo.addQuery("obj.user_name", new SysMap("userName", type_data), "=");
			}
			if ("order".equals(type)) {
				ofqo.addQuery("obj.order_id", new SysMap("order_id", type_data), "=");
			}
			if ("outOrder".equals(type)) {
				String sql = "( obj.out_order_id = :out_order_id or obj.payCenterNo = :payCenterNo )";
				Map<String, String> params = new HashMap<String, String>();
				params.put("out_order_id", type_data);
				params.put("payCenterNo", type_data);
				ofqo.addQuery(sql, params);
				//ofqo.addQuery("obj.out_order_id", new SysMap("out_order_id", type_data), "=");
				//ofqo.addQuery("obj.out_order_id", new SysMap("payCenterNo", type_data), "=");
			}
		}
		if ("apyafter".equals(CommUtil.null2String(payment))) {
			ofqo.addQuery("obj.payType", new SysMap("mark", "payafter"), "=");

		} else if (!StringUtils.isNullOrEmpty(payment)) {
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(receiver_Name)) {
			ofqo.addQuery("obj.receiver_Name", new SysMap("receiver_Name", receiver_Name), "=");
		}
		if (!StringUtils.isNullOrEmpty(startPayTime)) {
			ofqo.addQuery("obj.payTime", new SysMap("startPayTime", CommUtil.formatDate(startPayTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endPayTime)) {
			String endPays = endPayTime + " 23:59:59";
			ofqo.addQuery("obj.payTime", new SysMap("endPayTime", CommUtil.formatDate(endPays, "yyyy-MM-dd hh:mm:ss")),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(startConfirmTime)) {
			ofqo.addQuery("obj.confirmTime", new SysMap("startConfirmTime", CommUtil.formatDate(startConfirmTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endConfirmTime)) {
			String endConfirm = endConfirmTime + " 23:59:59";
			ofqo.addQuery("obj.confirmTime",
					new SysMap("endConfirmTime", CommUtil.formatDate(endConfirm, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		mv.addObject("type_data", type_data);
		mv.addObject("payment", payment);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		mv.addObject("receiver_Name", receiver_Name);
		mv.addObject("startPayTime", startPayTime);
		mv.addObject("endPayTime", endPayTime);
		mv.addObject("startConfirmTime", startConfirmTime);
		mv.addObject("endConfirmTime", endConfirmTime);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "手机充值订单列表", value = "/admin/order_recharge.htm*", rtype = "admin", rname = "充值列表", rcode = "ofcard_list", rgroup = "交易")
	@RequestMapping("/admin/order_recharge.htm")
	public ModelAndView order_recharge(HttpServletRequest request, HttpServletResponse response, String order_status,
			String beginTime, String endTime, String begin_price, String end_price, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/order_recharge.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 1), "=");// 这里只查手机充值订单
		if (!StringUtils.isNullOrEmpty(order_status)) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status", CommUtil.null2Int(order_status)), "=");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "订单详情", value = "/admin/order_view.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id, String view_type,String type) {
		ModelAndView mv = new JModelAndView("admin/blue/order_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj.getOrder_cat() == 1) {
			mv = new JModelAndView("admin/blue/order_recharge_view.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("paymentTools", paymentTools);
		} else {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
			TransInfo transInfo = this.orderFormTools.query_ship_getData(id);
			transInfo
					.setExpress_company_name(this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(obj.getShipCode());
			mv.addObject("transInfo", transInfo);
			mv.addObject("store", store);
			mv.addObject("obj", obj);
		}
		// mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(),
		// "express_company_name"));
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "导出表格", value = "/admin/order_list_excel.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping("/admin/order_list_excel.htm")
	public void order_list_excel(HttpServletRequest request, HttpServletResponse response, String order_status, String type,
			String type_data, String payment, String beginTime, String endTime, String begin_price, String end_price,
			String receiver_Name, String startPayTime, String endPayTime, String startConfirmTime, String endConfirmTime) {
		OrderFormQueryObject ofqo = new OrderFormQueryObject();
		ofqo.setPageSize(1000000000);
		ofqo.setOrderBy("addTime");
		ofqo.setOrderType("desc");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 0), "=");// 这里只查询商品订单，手机充值订单独立出来
		if (!StringUtils.isNullOrEmpty(order_status)) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status", CommUtil.null2Int(order_status)), "=");
		}
		if (!StringUtils.isNullOrEmpty(type_data)) {
			if ("store".equals(type)) {
				if ("自营".equals(type_data) || "自营商品".equals(type_data)) {
					ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
				} else {
					ofqo.addQuery("obj.store_name", new SysMap("store_name", type_data), "=");
				}
			}
			if ("buyer".equals(type)) {
				ofqo.addQuery("obj.user_name", new SysMap("userName", type_data), "=");
			}
			if ("order".equals(type)) {
				ofqo.addQuery("obj.order_id", new SysMap("order_id", type_data), "=");
			}
			if ("outOrder".equals(type)) {
				ofqo.addQuery("obj.out_order_id", new SysMap("out_order_id", type_data), "=");
			}
		}
		if ("alipay".equals(CommUtil.null2String(payment))) {
			String scope = "(obj.payment.mark =:alipay_pc or obj.payment.mark =:alipay_app or obj.payment.mark =:alipay_wap)";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("alipay_pc", "alipay");
			params.put("alipay_app", "alipay_app");
			params.put("alipay_wap", "alipay_wap");
			ofqo.addQuery(scope, params);

		} else if ("apyafter".equals(CommUtil.null2String(payment))) {
			ofqo.addQuery("obj.payType", new SysMap("mark", "payafter"), "=");

		} else if ("wx_app".equals(CommUtil.null2String(payment))) {
			String scope = "(obj.payment.mark =:wx_app or obj.payment.mark =:wx_pay)";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("wx_app", "wx_app");
			params.put("wx_pay", "wx_pay");
			ofqo.addQuery(scope, params);

		} else if (!StringUtils.isNullOrEmpty(payment)) {
			ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		if (!StringUtils.isNullOrEmpty(begin_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
					">=");
		}
		if (!StringUtils.isNullOrEmpty(end_price)) {
			ofqo.addQuery("obj.totalPrice", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(receiver_Name)) {
			ofqo.addQuery("obj.receiver_Name", new SysMap("receiver_Name", receiver_Name), "=");
		}
		if (!StringUtils.isNullOrEmpty(startPayTime)) {
			ofqo.addQuery("obj.payTime", new SysMap("startPayTime", CommUtil.formatDate(startPayTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endPayTime)) {
			String endPays = endPayTime + " 23:59:59";
			ofqo.addQuery("obj.payTime", new SysMap("endPayTime", CommUtil.formatDate(endPays, "yyyy-MM-dd hh:mm:ss")),
					"<=");
		}
		if (!StringUtils.isNullOrEmpty(startConfirmTime)) {
			ofqo.addQuery("obj.confirmTime", new SysMap("startConfirmTime", CommUtil.formatDate(startConfirmTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endConfirmTime)) {
			String endConfirm = endConfirmTime + " 23:59:59";
			ofqo.addQuery("obj.confirmTime",
					new SysMap("endConfirmTime", CommUtil.formatDate(endConfirm, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
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
			sheet.setColumnWidth(22, 6000);
			sheet.setColumnWidth(23, 6000);
			sheet.setColumnWidth(24, 8000);
			sheet.setColumnWidth(25, 8000);
			sheet.setColumnWidth(26, 8000);
			sheet.setColumnWidth(27, 8000);
			sheet.setColumnWidth(28, 8000);
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 28));
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
			cell.setCellValue("店铺");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("付款完成时间");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("确认收货时间");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("商品名称");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品编号");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("规格");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("单价");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("数量");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("小计");
			cell = row.createCell(12);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(13);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(14);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(15);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(16);
			cell.setCellStyle(style2);
			cell.setCellValue("买家");
			cell = row.createCell(17);
			cell.setCellStyle(style2);
			cell.setCellValue("收货人");
			cell = row.createCell(18);
			cell.setCellStyle(style2);
			cell.setCellValue("身份证");
			cell = row.createCell(19);
			cell.setCellStyle(style2);
			cell.setCellValue("收货地址");
			cell = row.createCell(20);
			cell.setCellStyle(style2);
			cell.setCellValue("联系电话");
			cell = row.createCell(21);
			cell.setCellStyle(style2);
			cell.setCellValue("发票信息");
			cell = row.createCell(22);
			cell.setCellStyle(style2);
			cell.setCellValue("买家备注");
			cell = row.createCell(23);
			cell.setCellStyle(style2);
			cell.setCellValue("扣点结算");
			cell = row.createCell(24);
			cell.setCellStyle(style2);
			cell.setCellValue("扣点（%）");
			cell = row.createCell(25);
			cell.setCellStyle(style2);
			cell.setCellValue("扣除金额（元）");
			cell = row.createCell(26);
			cell.setCellStyle(style2);
			cell.setCellValue("支付商家商品价格（元）");
			cell = row.createCell(27);
			cell.setCellStyle(style2);
			cell.setCellValue("增减款项（补运费）");
			cell = row.createCell(28);
			cell.setCellStyle(style2);
			cell.setCellValue("实际支付商家（元）");
			int count = 0;
			DecimalFormat format = new DecimalFormat("#.##");
			format.setRoundingMode(RoundingMode.HALF_UP);
			for (int j = 2; j <= datas.size() + 1; j++) {
				List<Map> goods_json = new ArrayList<Map>();
				if (datas.size() >= j - 2 && datas.get(j - 2) != null) {
					goods_json = Json.fromJson(List.class, CommUtil.null2String(datas.get(j - 2).getGoods_info()));
				}
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
						cell.setCellValue(!StringUtils.isNullOrEmpty(datas.get(j - 2).getStore_name())
								? datas.get(j - 2).getStore_name() : "自营商品");// 店铺

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

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						// double settlement = CommUtil.mul(map.get("goods_price"), map.get("goods_count"));
						/*double settlement = 0.00;
						if (summation != 0) {
							settlement = subtotal - ((subtotal / summation) * (summation - (totalPrice - shipPrice)));
						}
						settlement = Double.parseDouble(format.format(settlement));*/
						cell.setCellValue(CommUtil.null2Double(map.get("goods_payoff_price")) + CommUtil.null2Double(map.get("goods_commission_price")));// 扣点结算价（元）

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						double commission_rate = 0.0;
						String commission_rate_str = "";
						if (!StringUtils.isNullOrEmpty(map.get("goods_commission_rate"))) {
							commission_rate = Double.parseDouble(map.get("goods_commission_rate").toString());
							double commission_rate_percent = CommUtil.mul(commission_rate, 100);
							commission_rate_str = String.valueOf(commission_rate_percent).substring(0,
									String.valueOf(commission_rate_percent).indexOf(".")) + "%";
						}
						cell.setCellValue(commission_rate_str);// 扣点（%）

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						/*double deducted = 0.0;
						String deductedStr = "";
						if (!StringUtils.isNullOrEmpty(map.get("goods_commission_rate"))) {
							deducted = CommUtil.mul(settlement, commission_rate);
							deductedStr = String.valueOf(deducted);
						}*/
						cell.setCellValue(CommUtil.null2String(map.get("goods_commission_price")));// 扣除金额（元）

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						/*String payMerchantStr = "";
						if (!StringUtils.isNullOrEmpty(map.get("goods_commission_rate"))) {
							double payMerchant = CommUtil.subtract(settlement, deducted);
							payMerchantStr = String.valueOf(payMerchant);
						}*/
						cell.setCellValue(CommUtil.null2String(map.get("goods_payoff_price")));// 支付商家商品价格（元）

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue("");// 增减款项（补运费）

						cell = row.createCell(++i);
						cell.setCellStyle(style2);
						cell.setCellValue("");// 实际支付商家（元）
						
						if (k != goods_json.size() - 1) {
							count++;
						}
						if (goods_json.size() >= 2 && k == goods_json.size() - 1) {
							for (int a = 0; a < 17; a++) {
								if (a <= 5) {
									sheet.addMergedRegion(new CellRangeAddress(j + count - k, j + count, a, a));
								} else {
									sheet.addMergedRegion(new CellRangeAddress(j + count - k, j + count, a + 6, a + 6));
								}
							}
						}
						k++;
					}
				}
			}

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

	private static final Map<String, String> PAYMENT_MAP = new HashMap<String, String>() {
		{
			put(null, "未支付");
			put("", "未支付");
			put("alipay", "支付宝(PC端支付)");
			put("alipay_wap", "支付宝(手机网页支付)");
			put("alipay_app", "支付宝(手机APP支付)");
			put("tenpay", "财付通");
			put("bill", "快钱支付");
			put("chinabank", "网银在线");
			put("paypal", "aypal");
			put("outline", "线下支付");
			put("balance", "预存款支付");
			put("payafter", "货到付款");
			put("wx_app", "微信(手机APP支付)");
			put("wx_pay", "微信(手机网页支付)");
			put("unionpay_wap", "中国银联(手机网页支付)");
			put("chinapay", "中国银联(PC端支付)");
			put("unionpay_app", "中国银联(手机APP支付)");
			put("chinapay_nocard", "中国银联无卡支付");
		}
	};

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
			put(40, "已收货");
			put(50, "已完成");
			put(60, "已结束");
			put(80, "退款中");
			put(85, "退款已提交");
			put(87, "退款失败");
			put(90, "退款完成");
		}
	};
	
	
	/**
	 * user_name用户名
	 * order_id订单号
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param user_name
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "自营退款列表", value = "/admin/self_refund_list.htm*", rtype = "admin", rname = "自营退款", rcode = "self_order_refund", rgroup = "自营")
	@RequestMapping("/admin/self_refund_list.htm")
	public ModelAndView self_refund_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String user_name, String order_id) {
		ModelAndView mv = new JModelAndView("admin/blue/self_refund_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		StringBuffer sql = new StringBuffer();
		sql.append(" ((obj.order_status >= 25 and obj.order_status <= 27) ");
		sql.append(" or (obj.order_status >= 80 and obj.order_status <= 90)) ");
		Map params = new HashMap();
		params.put("order_form", 1);
		sql.append(" and obj.order_form = :order_form ");
		if(!StringUtils.isNullOrEmpty(user_name)) {
			params.put("user_name", user_name);
			sql.append(" and obj.user_name = :user_name ");
			mv.addObject("user_name", user_name);
		}
		if(!StringUtils.isNullOrEmpty(order_id)) {
			params.put("order_id", order_id);
			sql.append(" and obj.order_id = :order_id ");
			mv.addObject("order_id", order_id);
		}
		qo.addQuery(sql.toString(), params);
		IPageList pList = this.orderFormService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("store", user.getStore());
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}
	
	/**
	 * id订单表id
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营退款列表", value = "/admin/self_refund_check.htm*", rtype = "admin", rname = "自营退款", rcode = "self_order_refund", rgroup = "自营")
	@RequestMapping("/admin/self_refund_check.htm")
	public ModelAndView self_refund_check(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		ModelAndView mv = new JModelAndView("admin/blue/self_refund_check.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		//调用计算退款金额接口
		Map<String, Double> map = this.orderFormService.getOrderRefundAmount(obj.getOrder_id());
		double price = map.get("price");
		mv.addObject("price",price);
		mv.addObject("obj", obj);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}
	
	/**
	 * id订单表id
	 * order_status订单状态
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param order_status
	 * @return
	 * @throws IOException 
	 */
	@SecurityMapping(title = "自营退款保存", value = "/admin/self_refund_check_save.htm*", rtype = "admin", rname = "自营退款保存", rcode = "self_order_refund", rgroup = "自营")
	@RequestMapping("/admin/self_refund_check_save.htm")
	public ModelAndView self_refund_check_save(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id, String order_status) throws IOException {
		OrderForm obj = this.orderFormService.getObjById(Long.parseLong(id));
		if(obj.getOrder_status() == 25){
			String content;
			obj.setOrder_status(CommUtil.null2Int(order_status));
			this.orderFormService.update(obj);
			if("80".equals(order_status)){
				//审核通过，还原商品库存
				this.goodsService.recover_goods_inventory(obj, "refund");
				content = "退款申请审核通过。";
			}else{
				content = "退款申请审核不通过。";
			}
			//obj.setOrder_status(CommUtil.null2Int(order_status));
			//this.orderFormService.update(obj);
			String msg_content = "订单号：" + obj.getOrder_id() + content;
			User user = userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.save(msg);
			String url = CommUtil.getURL(request) + "/admin/self_refund_list.htm";
			response.getWriter().write("<script>window.location.href='"+url+"'</script>");
			return null;
		}else{
			ModelAndView mv = new ModelAndView();
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "此订单状态不支持审核！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_refund_list.htm");
			return mv;
		}
	}
	
	/**
	 * order_id订单号
	 * user_name用户名
	 * order_status订单状态
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param user_name
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "订单退款列表", value = "/admin/order_refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_refund_list.htm")
	public ModelAndView order_refund_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id, String user_name, String order_status) {
		ModelAndView mv = new JModelAndView("admin/blue/order_refund_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		StringBuffer sql = new StringBuffer();
		sql.append(" and obj.order_status >= 80 and obj.order_status <= 90 ");
		Map params = new HashMap();
		if(!StringUtils.isNullOrEmpty(user_name)) {
			params.put("user_name", user_name);
			sql.append(" and obj.user_name = :user_name ");
			mv.addObject("user_name", user_name);
		}
		if(!StringUtils.isNullOrEmpty(order_id)) {
			params.put("order_id", order_id);
			sql.append(" and obj.order_id = :order_id ");
			mv.addObject("order_id", order_id);
		}
		if(!StringUtils.isNullOrEmpty(order_status)) {
			params.put("order_status", CommUtil.null2Int(order_status));
			sql.append(" and obj.order_status = :order_status ");
			mv.addObject("order_status", order_status);
		}
		qo.addQuery(sql.toString(), params);
		IPageList pList = this.orderFormService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}
	
	/**
	 * id订单表id
	 * type 退款类型
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param user_name
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "订单退款查看", value = "/admin/order_refund_view.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_refund_view.htm")
	public ModelAndView order_refund_view(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/order_refund_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm of = this.orderFormService.getObjById(Long.parseLong(id));
		if(of != null){
			Map<String, Double> map = this.orderFormService.getOrderRefundAmount(of.getOrder_id());
			StringBuffer buffer = new StringBuffer();
			double tempRefundMoney = map.get("price");//可退款金额（写死测试使用）
			double enough_reduce_discount = map.get("totalReducePrice");//满减折扣金额
			double coupon_discount = map.get("couponPrice");//优惠券折扣金额
			double freight = map.get("freight");//运费金额
			double ret = map.get("totalPrice") + freight - tempRefundMoney;//平台调整金额
			String payCenterNo = null;
			buffer.append("订单号为" + of.getOrder_id() + "的订单退款成功，预存款" + tempRefundMoney + "元已存入您的账户");
			if (enough_reduce_discount > 0) {
				buffer.append(",扣除了" + enough_reduce_discount + "元满减金额");
			}
			if (coupon_discount > 0) {
				buffer.append(",扣除了" + coupon_discount + "元优惠券金额");
			}
			if (ret != 0) {
				buffer.append(",平台自动调整金额" + ret + "元");
			}
			//判断是否为主订单，不是主订单
			if(of.getOrder_main() != 1){
				mv.addObject("refundType", "101");
				OrderForm orderForm = this.orderFormService.getObjByProperty(null, "order_id", of.getMainOrderId());
				payCenterNo = orderForm.getPayCenterNo();
			}else{
				payCenterNo = of.getPayCenterNo();
				if(StringUtils.isNullOrEmpty(of.getChild_order_detail())){
					mv.addObject("refundType", "102");
				}else{
					mv.addObject("refundType", "101");
				}
			}
			mv.addObject("payCenterNo", payCenterNo);
			mv.addObject("of", of);
			mv.addObject("currentPage", currentPage);
			mv.addObject("coupon_discount", coupon_discount);
			mv.addObject("enough_reduce_discount", enough_reduce_discount);
			mv.addObject("refund_money", tempRefundMoney);
			mv.addObject("freight", freight);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("msg", buffer.toString());
		}
		return mv;
	}
	
	@SecurityMapping(title = "平台退款提交", value = "/admin/ret_pay_after.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_ret_pay_after.htm")
	public ModelAndView ret_pay_after(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String tipInfo, String reqUrl) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (StringUtils.isNullOrEmpty(reqUrl)) {
			reqUrl = "/admin/order_refund_list.htm";
		}
		mv.addObject("list_url", CommUtil.getURL(request) + reqUrl);
		mv.addObject("op_title", tipInfo);
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/order_refund_view.htm" + "?currentPage=" + currentPage);
		return mv;
	}
	
	public void ret_pay_save(HttpServletRequest request, HttpServletResponse response, String user_id, String amount,
			String info, String refund_user_id, String o_id,String enough_reduce_discount, String coupon_discount, String status) {

		User user = null;
		int order_status=90;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		} else {
			user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
		}
		if("1".equals(status)){//已提交退款申请
			order_status=85;
		}
		// 订单退款
		if (!StringUtils.isNullOrEmpty(o_id)) {

			OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(o_id));
			// 修改优惠券已退金额和满减已退金额和订单状态
			order.setOrder_status(order_status);
			refundTools.updateOrderDiscount(order,coupon_discount,enough_reduce_discount,null);
			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type("退回支付方");
			r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
			r_log.setReturnLog_userName(user.getUserName());
			r_log.setReturnLog_userId(user.getId());
			this.refundLogService.save(r_log);
			// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
/*			if (order.getOrder_form() == 0) {
				Store store = storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
				PayoffLog pol = new PayoffLog();
				pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
				pol.setAddTime(new Date());
				pol.setGoods_info(order.getReturn_goods_info());
				pol.setRefund_user_id(store.getUser().getId());
				pol.setSeller(store.getUser());
				pol.setRefund_userName(user.getUserName());
				pol.setPayoff_type(-1);
				pol.setPl_info("退款完成");
				BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
				//BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(1, rgl.getGoods_commission_rate()));// 商品的佣金比例
				//BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, CommUtil.mul(price, mission)));
				pol.setTotal_amount(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				//List<Map> list = new ArrayList<Map>();
				//Map json = new HashMap();
				//json.put("goods_id", rgl.getGoods_id());
				//json.put("goods_name", rgl.getGoods_name());
				//json.put("goods_price", rgl.getGoods_price());
				//json.put("goods_mainphoto_path", rgl.getGoods_mainphoto_path());
				//json.put("goods_commission_rate", rgl.getGoods_commission_rate());
				//json.put("goods_count", rgl.getGoods_count());
				//json.put("goods_all_price", rgl.getGoods_all_price());
				//json.put("goods_commission_price", CommUtil.mul(rgl.getGoods_all_price(), rgl.getGoods_commission_rate()));
				//json.put("goods_payoff_price", final_money);
				//list.add(json);
				pol.setReturn_goods_info(order.getGoods_info());
				pol.setO_id(o_id);
				pol.setOrder_id(order.getOrder_id());
				pol.setCommission_amount(BigDecimal.valueOf(0));
				pol.setOrder_total_price(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				this.payoffLogService.save(pol);

				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
				store.setStore_payoff_amount(
						BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), CommUtil.mul(price, 0))));// 减少店铺本次结算总金额
				this.storeService.update(store);

				// 减少系统总销售金额、总结算金额
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				sc.setPayoff_all_amount(
						BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(price, 0))));
				sc.setPayoff_all_amount_reality(
						BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
				this.configService.update(sc);
			}*/

			String msg_content = "成功为订单号：" + order.getOrder_id() + "退款" + amount + "元退款完成。";
			if ("1".equals(status)) {
				msg_content = "成功为订单号：" + order.getOrder_id() + "退款" + amount + "元提交完成。";
			}
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.save(msg);
		} 
	}
	
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "银联平台退款", value = "/admin/ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_ret_pay.htm")
	public String ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id,  String currentPage, String o_id, String order_id, String payMark, String paymentId,
			String amount, String main_order_id, String coupon_discount, String enough_reduce_discount) {
		String tipInfo = "银联平台退款提交成功";
		boolean flag = false;
		String priv = o_id+ "~2";
		String reqUrl = "/admin/order_refund_list.htm";
		if (!StringUtils.isNullOrEmpty(o_id)) {
			OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(o_id));
			int status = orderForm.getOrder_status();
			if (status == 85||status==90) {
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:order_ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}
			order_id = orderForm.getId().toString();
			main_order_id = String.valueOf(orderForm.getMainOrderId());
		}
		Map<String, String> retMap = retPayTools.retPay(CommUtil.getURL(request), order_id, priv, amount, main_order_id);
		String responseCode = retMap.get("ResponseCode");
		if ("0".equals(responseCode)) {// 退款提交成功
			flag = ChinaPayCommon.getVerifyAuthToken(retMap);
			if (flag) {
				String status = retMap.get("Status");
				String retAmount = "0";
				if ("1".equals(status)||"3".equals(status)) {//状态为1表示退款请求已提交
					retAmount = retMap.get("RefundAmout");
					retAmount = AmountUtils.changeF2Y(retAmount); // 分转元
					this.ret_pay_save(request,response,user_id,retAmount,info,refund_user_id,o_id,enough_reduce_discount,coupon_discount,status);
					tipInfo = tipInfo + "，退款金额：" + retAmount;
				} else {
					tipInfo = "银联平台退款失败";
					logger.error(tipInfo);
				}

			} else {
				tipInfo = "银联平台退款返回验证失败";
				logger.error(tipInfo);
			}

		} else {
			tipInfo = retMap.get("Message");
			logger.error(tipInfo);
		}

		tipInfo = URLEncoder.encode(tipInfo);
		return "redirect:order_ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
	}
	
	@SecurityMapping(title = "微信平台退款", value = "/admin/weixi_ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_weixi_ret_pay.htm")
	public String weixi_ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id, String currentPage,String o_id, String order_id, String payMark, String paymentId,
			String amount, String main_order_id, String coupon_discount, String enough_reduce_discount) {
		String tipInfo = "";
		String reqUrl = "";
		if (!StringUtils.isNullOrEmpty(o_id)) {
			reqUrl = "/admin/order_refund_list.htm";
			//查询订单信息
			OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(o_id));
			int status = orderForm.getOrder_status();
			if (status == 85||status==90) {//85退款已提交，87退款失败，90为退款完成
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:order_ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}
			order_id = String.valueOf(orderForm.getId());
			main_order_id = String.valueOf(orderForm.getId());
		}

		String encode = TenpayUtil.getCharacterEncoding(request, response);
		RefundResBean refundResBean = new RefundResBean();
		refundResBean = retPayTools.weixiRetPay(main_order_id, order_id, amount, encode);
		String return_code = refundResBean.getReturn_code();
		if ("SUCCESS".equals(return_code)) {
			String result_code = refundResBean.getResult_code();
			if ("SUCCESS".equals(result_code)) {
				int refund_fee = refundResBean.getRefund_fee();
				String retAmount = AmountUtils.changeF2Y(String.valueOf(refund_fee));
				//this.ret_pay_save(request, response, user_id, retAmount, info, refund_user_id, obj_id, "3", gi_id);
				this.ret_pay_save(request,response,user_id,retAmount,info,refund_user_id,o_id,enough_reduce_discount,coupon_discount,"3");
				//查询订单信息
				OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(o_id));
				//退款成功后修改满减json
				orderFormService.modifyEnoughReduceInfo(order);
				//退款成功修改运费json
				orderFormService.modifyFreightInfo(order);
				//成功后，修改优惠的活动金额
				orderFormService.modifyActivityAmount(order);
				tipInfo = "微信平台退款提交成功，退款金额：" + retAmount;
			} else {
				String err_code_des = refundResBean.getErr_code_des();
				tipInfo = err_code_des;
				logger.error(tipInfo);
			}

		} else {
			String return_msg = refundResBean.getReturn_msg();
			tipInfo = return_msg;
			logger.error(tipInfo);
		}
		tipInfo = URLEncoder.encode(tipInfo);
		return "redirect:order_ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
	}
	
	@SecurityMapping(title = "支付宝平台退款", value = "/admin/alipay_ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_alipay_ret_pay.htm")
	public ModelAndView alipay_ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id,String currentPage,String o_id, String order_id, String payMark, String paymentId,
			String retReason, String amount, String coupon_discount, String enough_reduce_discount) {
		ModelAndView mv = new JModelAndView("admin/blue/order_ret_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String tipInfo = "";
		String reqUrl = "";

		if (!StringUtils.isNullOrEmpty(o_id)) {
			reqUrl = "/admin/order_refund_list.htm";
			//查询订单信息
			OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(o_id));
			int status = orderForm.getOrder_status();
			if (status == 85||status==90) {//85退款已提交，87退款失败，90为退款完成
				tipInfo = "已退款完成过";
				mv = this.ret_pay_after(request, response, currentPage, tipInfo, reqUrl);
				return mv;
			}
			order_id = String.valueOf(orderForm.getId());
		}

		mv.addObject("retPayTools", retPayTools);
		mv.addObject("url", CommUtil.getURL(request));
		mv.addObject("order_id", order_id);
		//mv.addObject("obj_id", obj_id);
		mv.addObject("retReason", retReason);
		mv.addObject("pageAmount", amount);
		//mv.addObject("gi_id", gi_id);
		return mv;
	}
	
	@SecurityMapping(title = "平台退款完成", value = "/admin/refund_finish.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/order_refund_finish.htm")
	public ModelAndView predeposit_modify_save(HttpServletRequest request, HttpServletResponse response, String user_id,
			String amount, String type, String info, String list_url, String refund_user_id,String o_id,String order_id, String main_order_id,
			String enough_reduce_discount, String coupon_discount) {
 		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
     		if (this.configService.getSysConfig().isDeposit()) {
			//查询退款用户信息
     	    User user = null;
			if (!StringUtils.isNullOrEmpty(user_id)) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
			}
			//查询订单信息
			OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(o_id)); 
			ResultDTO resultDTO = null;
			// 预存卡支付，则进行退款操作
			if ("balance".equals(order.getPayment().getMark())) { 
				//订单退款
				resultDTO = feeManageservice.orderCancle(order,amount);
			} 
            //退款成功后记录日志
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), amount)));//余额都在CRM端保存和增减，故注释掉
				//this.userService.update(user);
				//退款成功后查询订单信息
				OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(o_id)); 
				// 保存充值日志
				PredepositLog log = new PredepositLog();
				log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
				log.setAddTime(new Date());
				log.setPd_log_amount(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				log.setPd_log_info(info);
				log.setPd_log_user(user);
				log.setPd_op_type("人工退款");
				log.setPd_type("可用预存款");
				this.predepositLogService.save(log);
				   //订单退款
					RefundLog rLog = new RefundLog();
					rLog.setAddTime(new Date());
					rLog.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
					rLog.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
					rLog.setRefund_log(info);
					rLog.setRefund_type("预存款");
					rLog.setRefund_user(SecurityUserHolder.getCurrentUser());
					rLog.setReturnLog_userName(user.getUserName());
					rLog.setReturnLog_userId(user.getId());
					this.refundLogService.save(rLog);
					//退款成功后修改满减json
					orderFormService.modifyEnoughReduceInfo(orderForm);
					//退款成功修改运费json
					orderFormService.modifyFreightInfo(orderForm);
					//成功后，修改优惠的活动金额
					orderFormService.modifyActivityAmount(orderForm);
					// 修改优惠券已退金额和满减已退金额，同时修改订单状态为退款已完成
					orderForm.setOrder_status(90);
					orderForm.setRefund_amount(new BigDecimal(CommUtil.null2Double(amount)));
					refundTools.updateOrderDiscount(orderForm, coupon_discount, enough_reduce_discount,
							null);
					// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
/*					if (orderForm.getOrder_form() == 0) {
						Store store = storeService.getObjById(CommUtil.null2Long(orderForm.getStore_id()));//this.goodsService.getObjById(rgl.getGoods_id()).getGoods_store();
						PayoffLog pol = new PayoffLog();
						pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						pol.setAddTime(new Date());
						pol.setGoods_info(order.getReturn_goods_info());
						pol.setRefund_user_id(user.getId());
						pol.setSeller(store.getUser());
						pol.setRefund_userName(user.getUserName());
						pol.setPayoff_type(-1);
						pol.setPl_info("退款完成");
						BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
						//BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(1, rgl.getGoods_commission_rate())); // 商品的佣金比例
						//BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, CommUtil.mul(price, mission)));
						//pol.setTotal_amount(final_money);
						List<Map> list = new ArrayList<Map>();
						Map json = new HashMap();
						json.put("goods_id", rgl.getGoods_id());
						json.put("goods_name", rgl.getGoods_name());
						json.put("goods_price", rgl.getGoods_price());
						json.put("goods_mainphoto_path", rgl.getGoods_mainphoto_path());
						json.put("goods_commission_rate", rgl.getGoods_commission_rate());
						json.put("goods_count", rgl.getGoods_count());
						json.put("goods_all_price", rgl.getGoods_all_price());
						json.put("goods_commission_price",
								CommUtil.mul(rgl.getGoods_all_price(), rgl.getGoods_commission_rate()));
						json.put("goods_payoff_price", final_money);
						list.add(json);
						pol.setReturn_goods_info(orderForm.getGoods_info());
						pol.setO_id(o_id);
						pol.setOrder_id(orderForm.getOrder_id());
						pol.setCommission_amount(BigDecimal.valueOf(0));
						pol.setOrder_total_price(BigDecimal.valueOf(CommUtil.null2Double(amount)));
						this.payoffLogService.save(pol);
						store.setStore_sale_amount(
								BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount))); // 减少店铺本次结算总销售金额
						//store.setStore_payoff_amount(BigDecimal
						//		.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), CommUtil.mul(price, mission)))); // 减少店铺本次结算总金额
						this.storeService.update(store);
						// 减少系统总销售金额、总结算金额
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				    	//sc.setPayoff_all_amount(BigDecimal
						//		.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(price, mission))));
						sc.setPayoff_all_amount_reality(BigDecimal
								.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
						this.configService.update(sc);
					}*/
					String msg_content = "成功为订单号：" + orderForm.getOrder_id() + "退款" + amount + "元，请到收支明细中查看。";
					// 发送系统站内信
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user);
					this.messageService.save(msg);
					mv.addObject("list_url", CommUtil.getURL(request) + "/admin/order_refund_list.htm");
			} else {
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", resultDTO.getMsg());
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/order_refund_list.htm");
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/order_refund_list.htm");
		}
		return mv;
	}

	/**
	 * 支付中心订单退款
	 * @param request
	 * @param response
	 * @param user_id
	 * @param amount
	 * @param type 
	 * @param info
	 * @param refund_user_id
	 * @param o_id 退款主键id
	 * @param obj_id ReturnGoodsLog 主键
	 * @param gi_id  GroupInfo 主键
	 * @param enough_reduce_discount
	 * @param coupon_discount
	 * @return
	 * @throws IOException 
	 */
	@SecurityMapping(title = "平台退款完成", value = "/admin/refund_pay_center.htm*", rtype = "admin", rname = "退款管理", rcode = "admin_order_refund", rgroup = "交易")
	@RequestMapping("/admin/refund_pay_center.htm")
	public ModelAndView refund_pay_center(HttpServletRequest request, HttpServletResponse response,
			String amount, String type, String info,String o_id, String obj_id, String gi_id,
			String enough_reduce_discount, String coupon_discount) throws IOException {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response); 
		OrderForm order;
		String orderType = ""; //0:商品退款，1:消费码退款，2:订单退款
		String list_url = "/admin/order_refund_list.htm"; //退款提交成功后，返回地址
		String originalNo = ""; //支付单号
		String refundType = ""; //退款类型，1为部分退款，2全部退款
		String id = ""; //主键
		String mark = ""; //支付类型
		//订单退款
		if("order_refund".equals(type)){
			orderType = "2";
			//查询订单信息
			order = this.orderFormService.getObjById(CommUtil.null2Long(o_id)); 
			if(order != null){
				originalNo = this.orderFormService.getOriginalNo(order);
				mark = order.getPayment().getMark();
				
				refundType = this.orderFormService.getRefundType(order, "order");
				id = CommUtil.null2String(order.getId());
			}
		}else if("available".equals(type)){
			//商品退款
			if(!StringUtil.isEmpty(obj_id)){
				orderType = "0";
				ReturnGoodsLog rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));
				if(rgl != null){
					order = this.orderFormService.getObjById(rgl.getReturn_order_id());
					originalNo = this.orderFormService.getOriginalNo(order);
					mark = order.getPayment().getMark();
					if ("balance".equals(mark)) { // 预存款支付
						refundType = this.orderFormService.getRefundType(order, "goods");
					}
					id = CommUtil.null2String(rgl.getId());
					list_url = "/admin/order_refund_list.htm";
				}
			}
			//消费码退款
			if(!StringUtil.isEmpty(gi_id)){
				orderType = "1";
				GroupInfo gi = this.groupInfoService.getObjById(CommUtil.null2Long(gi_id));
				if(gi != null){
					order = this.orderFormService.getObjById(gi.getOrder_id());
					originalNo = this.orderFormService.getOriginalNo(order);
					mark = order.getPayment().getMark();
					if ("balance".equals(mark)) { // 预存款支付
						refundType = this.orderFormService.getRefundType(order, "group");
					}
					id = CommUtil.null2String(gi.getId());
					list_url = "/admin/groupinfo_refund_list.htm";
				}
			}
		}
		//调用退款接口
		Result result = this.payCenterService.refund(amount, originalNo, info, orderType, refundType, id);
		if (null != result && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((result.getCode()))) {
				if ("alipay".equals(mark) || "alipay_wap".equals(mark)) { // 支付宝支付
					Map<String, Object> map = (Map<String, Object>) result.getData();
					String url = CommUtil.null2String(map.get("url"));
					response.getWriter().write("<script>window.location.href='"+url+"'</script>");
					return null;
				}else{
					mv.addObject("list_url", CommUtil.getURL(request) + list_url);
				}
		}else{
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", result.getMsg());
			mv.addObject("list_url", CommUtil.getURL(request) + list_url);
		}
		return mv;
	}
}
