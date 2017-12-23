package com.iskyshop.manage.admin.action;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.RefundLogQueryObject;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
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
import com.iskyshop.manage.admin.tools.RefundTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipayNotify;
import com.iskyshop.pay.chinapay.ChinaPayCommon;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.pay.tools.RetPayTools;
import com.iskyshop.pay.weixi.refund.RefundResBean;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;

/**
 * 
 * 
 * <p>
 * Title: RefundManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台向买家进行退款，退款统一给买家发放预存款，买家通过预存款兑换现金
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
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class RefundManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IMessageService messageService;

	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private FeeManageConnector feeManageConnector;
	@Autowired
	private IFeeManageservice feeManageservice;

	@Autowired
	private RefundTools refundTools;

	@Autowired
	private RetPayTools retPayTools;
	@Autowired
	private GoodsViewTools goodsViewTools;

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品退款列表", value = "/admin/refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_list.htm")
	public ModelAndView refund_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String name, String user_name, String refund_status) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(currentPage, mv, orderBy, orderType);
		// begin dengyuqi 2015-9-8 修改退款订单审核中时平台就能进行退款的bug
		String scope = "(obj.goods_return_status=:goods_return_status1 or obj.goods_return_status=:goods_return_status2)";
		Map<String, String> params = new HashMap<String, String>();
		params.put("goods_return_status1", "10");
		params.put("goods_return_status2", "11");
		qo.addQuery(scope, params);
		// end
		if (!StringUtils.isNullOrEmpty(refund_status)) {
			qo.addQuery("obj.refund_status", new SysMap("refund_status", CommUtil.null2Int(refund_status)), "=");
			mv.addObject("refund_status", refund_status);
		}
		if (!StringUtils.isNullOrEmpty(user_name)) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name), "=");
		}
		if (!StringUtils.isNullOrEmpty(name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + name + "%"), "like");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReturnGoodsLog.class, mv);
		IPageList pList = this.returngoodslogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);

		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "消费码退款列表", value = "/admin/groupinfo_refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/groupinfo_refund_list.htm")
	public ModelAndView groupinfo_refund_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String group_sn, String user_name, String refund_status) {
		ModelAndView mv = new JModelAndView("admin/blue/groupinfo_refund_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.status", new SysMap("status", 5), "=");
		if (!StringUtils.isNullOrEmpty(group_sn)) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", group_sn), "=");
			mv.addObject("group_sn", group_sn);
		}
		if (!StringUtils.isNullOrEmpty(user_name)) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name), "=");
			mv.addObject("user_name", user_name);
		}
		if (!StringUtils.isNullOrEmpty(refund_status)) {
			qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(refund_status)), "=");
			mv.addObject("refund_status", refund_status);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupInfo.class, mv);
		IPageList pList = this.groupinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * refund_view查看
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "查看退款", value = "/admin/refund_view.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_view.htm")
	public ModelAndView refund_view(HttpServletRequest request, HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_predeposit_modify.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(type)) {
			if ("groupinfo".equals(type)) { // 消费码退款
				mv.addObject("type", type);
				GroupInfo gi = this.groupinfoService.getObjById(CommUtil.null2Long(id));
				User user = this.userService.getObjById(gi.getUser_id());
				mv.addObject("refund_money", gi.getLifeGoods().getGroup_price());
				mv.addObject("user", user);
				mv.addObject("gi", gi);
				mv.addObject("msg", gi.getLifeGoods().getGg_name() + "消费码" + gi.getGroup_sn() + "退款成功，预存款"
						+ gi.getLifeGoods().getGroup_price() + "元已存入您的账户");
			}
		} else { // 商品退款
			ReturnGoodsLog obj = this.returngoodslogService.getObjById(CommUtil.null2Long(id));
			OrderForm of = this.orderFormService.getObjById(obj.getReturn_order_id());
			double tempRefundMoney = 0.0;
			double goods_all_price = CommUtil.null2Double(obj.getGoods_all_price());// 商品总价
			double goods_price = CommUtil.null2Double(obj.getGoods_price());// 商品单价
			int goods_count = CommUtil.null2Int(obj.getGoods_count());
			
			Map discountMap = refundTools.calGoodsDiscount(of, obj.getGoods_all_price(), obj.getGoods_id().toString());
			double coupon_discount = CommUtil.null2Double(discountMap.get("coupon_discount"));
			double enough_reduce_discount = CommUtil.null2Double(discountMap.get("enough_reduce_discount"));
			double single_discount = CommUtil
					.null2Double(CommUtil.formatMoney((coupon_discount + enough_reduce_discount) / goods_count));// 单件折扣价（四舍五入的）
			tempRefundMoney = CommUtil.subtract(goods_all_price, (single_discount * goods_count));
			double refundable_price = this.orderFormTools.query_refundable_price(obj.getReturn_main_order_id());
			double ret = 0.0;
			if (tempRefundMoney > refundable_price) {
				ret = refundable_price - tempRefundMoney;
				tempRefundMoney = refundable_price;
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append("退货服务号为" + obj.getReturn_service_id() + "的商品退款成功，预存款" + tempRefundMoney + "元已存入您的账户");
			if (enough_reduce_discount > 0) {
				buffer.append(",扣除了" + enough_reduce_discount + "元满减金额");
			}
			if (coupon_discount > 0) {
				buffer.append(",扣除了" + coupon_discount + "元优惠券金额");
			}
			if (ret != 0) {
				buffer.append(",平台自动调整金额" + ret + "元");
			}
			mv.addObject("coupon_discount", coupon_discount);
			mv.addObject("enough_reduce_discount", enough_reduce_discount);
			mv.addObject("refund_money", tempRefundMoney);
			mv.addObject("refundable_price", refundable_price);
			mv.addObject("msg", buffer.toString());

			mv.addObject("obj", obj);
			User user = this.userService.getObjById(obj.getUser_id());
			mv.addObject("user", user);

			mv.addObject("of", of);

		}
		return mv;
	}

	/**
	 * refundlog_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "退款日志列表", value = "/admin/refundlog_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refundlog_list.htm")
	public ModelAndView refundlog_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String refund_id, String user_name, String return_service_id, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/refundlog_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(user_name)) {
			qo.addQuery("obj.returnLog_userName", new SysMap("returnLog_userName", user_name), "=");
		}
		if (!StringUtils.isNullOrEmpty(refund_id)) {
			qo.addQuery("obj.refund_id", new SysMap("refund_id", refund_id), "=");
		}
		if (!StringUtils.isNullOrEmpty(return_service_id)) {
			qo.addQuery("obj.returnService_id", new SysMap("returnService_id", return_service_id), "=");
		}
		if (!StringUtils.isNullOrEmpty(beginTime)) {
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, RefundLog.class, mv);
		IPageList pList = this.refundLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/refundlog_list.htm", "", params, pList, mv);
		mv.addObject("refund_id", refund_id);
		mv.addObject("user_name", user_name);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("return_service_id", return_service_id);
		return mv;
	}

	@SecurityMapping(title = "银联平台退款", value = "/admin/ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/ret_pay.htm")
	public String ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id, String obj_id, String currentPage, String order_id, String payMark, String paymentId,
			String amount, String main_order_id, String coupon_discount, String enough_reduce_discount, String gi_id) {
		String tipInfo = "银联平台退款提交成功";
		boolean flag = false;
		String priv = obj_id;

		String reqUrl = "";

		if (!StringUtils.isNullOrEmpty(obj_id)) {
			reqUrl = "/admin/refund_list.htm";
			ReturnGoodsLog returnGoodsLog = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));

			int refund_status = returnGoodsLog.getRefund_status();
			if (refund_status > 0) {
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}

			returnGoodsLog.setReturnRemark(info);
			returnGoodsLog.setCouponDiscount(BigDecimal.valueOf(CommUtil.null2Double(coupon_discount)));
			returnGoodsLog.setEnoughReduceDiscount(BigDecimal.valueOf(CommUtil.null2Double(enough_reduce_discount)));
			this.returnGoodsLogService.update(returnGoodsLog);
			priv = priv + "~0";
		} else {
			reqUrl = "/admin/groupinfo_refund_list.htm";
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
			int status = groupInfo.getStatus();
			if (status > 5) {
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}
			groupInfo.setReturnRemark(info);
			this.groupinfoService.update(groupInfo);
			OrderForm orderForm = this.orderFormService.getObjById(groupInfo.getOrder_id());
			order_id = String.valueOf(orderForm.getId());
			priv = String.valueOf(groupInfo.getId()) + "~1";
			main_order_id = String.valueOf(orderForm.getId());
		}

		Map<String, String> retMap = retPayTools.retPay(CommUtil.getURL(request), order_id, priv, amount, main_order_id);
		String responseCode = retMap.get("ResponseCode");
		if ("0".equals(responseCode)) {// 退款提交成功
			flag = ChinaPayCommon.getVerifyAuthToken(retMap);
			if (flag) {
				String status = retMap.get("Status");
				String retAmount = "0";
				if ("1".equals(status)) {
					retAmount = retMap.get("RefundAmout");
					retAmount = AmountUtils.changeF2Y(retAmount); // 分转元
					this.ret_pay_save(request, response, user_id, retAmount, info, refund_user_id, obj_id, status, gi_id);
					tipInfo = tipInfo + "，退款金额：" + retAmount;
				} else if ("3".equals(status)) {
					retAmount = retMap.get("RefundAmout");
					retAmount = AmountUtils.changeF2Y(retAmount); // 分转元
					this.ret_pay_save(request, response, user_id, retAmount, info, refund_user_id, obj_id, status, gi_id);
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
		return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
	}

	@SecurityMapping(title = "微信平台退款", value = "/admin/weixi_ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/weixi_ret_pay.htm")
	public String weixi_ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id, String obj_id, String currentPage, String order_id, String payMark, String paymentId,
			String amount, String main_order_id, String coupon_discount, String enough_reduce_discount, String gi_id) {
		String tipInfo = "";
		String reqUrl = "";

		if (!StringUtils.isNullOrEmpty(obj_id)) {
			reqUrl = "/admin/refund_list.htm";
			ReturnGoodsLog returnGoodsLog = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));

			int refund_status = returnGoodsLog.getRefund_status();
			if (refund_status > 0) {
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}

			returnGoodsLog.setReturnRemark(info);
			returnGoodsLog.setCouponDiscount(BigDecimal.valueOf(CommUtil.null2Double(coupon_discount)));
			returnGoodsLog.setEnoughReduceDiscount(BigDecimal.valueOf(CommUtil.null2Double(enough_reduce_discount)));
			this.returnGoodsLogService.update(returnGoodsLog);
		} else {
			reqUrl = "/admin/groupinfo_refund_list.htm";
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
			int status = groupInfo.getStatus();
			if (status > 5) {
				tipInfo = "已退款完成过";
				tipInfo = URLEncoder.encode(tipInfo);
				return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
			}
			groupInfo.setReturnRemark(info);
			this.groupinfoService.update(groupInfo);
			OrderForm orderForm = this.orderFormService.getObjById(groupInfo.getOrder_id());
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
				this.ret_pay_save(request, response, user_id, retAmount, info, refund_user_id, obj_id, "3", gi_id);
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
		return "redirect:ret_pay_after.htm?currentPage=" + currentPage + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
	}

	@SecurityMapping(title = "支付宝平台退款", value = "/admin/alipay_ret_pay.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/alipay_ret_pay.htm")
	public ModelAndView alipay_ret_pay(HttpServletRequest request, HttpServletResponse response, String user_id, String info,
			String refund_user_id, String obj_id, String currentPage, String order_id, String payMark, String paymentId,
			String retReason, String amount, String coupon_discount, String enough_reduce_discount, String gi_id) {
		ModelAndView mv = new JModelAndView("admin/blue/ret_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String tipInfo = "";
		String reqUrl = "";
		if (!StringUtils.isNullOrEmpty(obj_id)) {
			reqUrl = "/admin/refund_list.htm";
			ReturnGoodsLog returnGoodsLog = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));

			int refund_status = returnGoodsLog.getRefund_status();
			if (refund_status > 0) {
				tipInfo = "已退款完成过";
				mv = this.ret_pay_after(request, response, currentPage, tipInfo, reqUrl);
				return mv;
			}

			returnGoodsLog.setReturnRemark(info);
			returnGoodsLog.setCouponDiscount(BigDecimal.valueOf(CommUtil.null2Double(coupon_discount)));
			returnGoodsLog.setEnoughReduceDiscount(BigDecimal.valueOf(CommUtil.null2Double(enough_reduce_discount)));
			this.returnGoodsLogService.update(returnGoodsLog);
		} else {
			reqUrl = "/admin/groupinfo_refund_list.htm";
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
			int status = groupInfo.getStatus();
			if (status > 5) {
				tipInfo = "已退款完成过";
				mv = this.ret_pay_after(request, response, currentPage, tipInfo, reqUrl);
				return mv;
			}
			groupInfo.setReturnRemark(info);
			this.groupinfoService.update(groupInfo);
			OrderForm orderForm = this.orderFormService.getObjById(groupInfo.getOrder_id());
			order_id = String.valueOf(orderForm.getId());

		}

		mv.addObject("retPayTools", retPayTools);
		mv.addObject("url", CommUtil.getURL(request));
		mv.addObject("order_id", order_id);
		mv.addObject("obj_id", obj_id);
		mv.addObject("retReason", retReason);
		mv.addObject("pageAmount", amount);
		mv.addObject("gi_id", gi_id);

		return mv;
	}

	@SecurityMapping(title = "平台退款提交", value = "/admin/ret_pay_after.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/ret_pay_after.htm")
	public ModelAndView ret_pay_after(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String tipInfo, String reqUrl) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (StringUtils.isNullOrEmpty(reqUrl)) {
			reqUrl = "/admin/refund_list.htm";
		}
		mv.addObject("list_url", CommUtil.getURL(request) + reqUrl);
		mv.addObject("op_title", tipInfo);
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/refund_view.htm" + "?currentPage=" + currentPage);
		return mv;
	}
    /**
     * 银联回调
     * @param request
     * @param response
     */
	@RequestMapping("/ret_pay_notify.htm")
	public void ret_pay_notify(HttpServletRequest request, HttpServletResponse response) {
		boolean flag = false;
		// 订单数据准备
		String resCode = request.getParameter("ResponseCode");
		String merId = request.getParameter("MerID");
		String ordId = request.getParameter("OrderId");
		String transType = request.getParameter("TransType");
		String refundAmout = request.getParameter("RefundAmout");
		String processDate = request.getParameter("ProcessDate");
		String sendTime = request.getParameter("SendTime");
		String status = request.getParameter("Status");
		String priv1 = request.getParameter("Priv1");
		String checkValue = request.getParameter("CheckValue");
		String plainData = "";
		if ("0".equals(resCode)) {
			plainData = merId + processDate + transType + ordId + refundAmout + status + priv1;
			flag = ChinaPayCommon.getVerifyAuthToken(plainData, checkValue);
			if (flag) {
				String[] privArr = priv1.split("~");
				String privType = privArr[1];
				if ("0".equals(privType)) {//商品退款
					String obj_id = privArr[0];
					ReturnGoodsLog rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));
					rgl.setRefund_status(1);
					this.returnGoodsLogService.update(rgl);
					long user_id = rgl.getUser_id();
					User user = this.userService.getObjById(user_id);
					long id = rgl.getReturn_order_id();
					OrderForm orderForm = this.orderFormService.getObjById(id);
					String msg_content = "成功为订单号：" + orderForm.getOrder_id() + "退款" + refundAmout + "元退款完成。";
					// 发送系统站内信
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user);
					this.messageService.save(msg);
				} else if("1".equals(privType)){//消费码退款
					String gi_id = privArr[0];
					GroupInfo gi = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
					gi.setStatus(7); // 退款完成
					this.groupinfoService.update(gi);
					long user_id = gi.getUser_id();
					User user = this.userService.getObjById(user_id);
					long id = gi.getOrder_id();
					OrderForm orderForm = this.orderFormService.getObjById(id);
					String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功退款，退款金额为："
							+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
					// 发送系统站内信
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user);
					this.messageService.save(msg);
				}else if("2".equals(privType)){//订单退款
					String order_id = privArr[0];
					//查询退款订单信息
					OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
					orderForm.setOrder_status(90);
					//回退成功后更新订单状态
					this.orderFormService.update(orderForm);
					User user = this.userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));
					String msg_content = "成功为订单号：" + orderForm.getOrder_id() + "退款" + refundAmout + "元退款完成。";
					//退款成功后修改满减json
					orderFormService.modifyEnoughReduceInfo(orderForm);
					//退款成功修改运费json
					orderFormService.modifyFreightInfo(orderForm);
					//成功后，修改优惠的活动金额
					orderFormService.modifyActivityAmount(orderForm);
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

			} else {
				logger.error("银联退款后续回调验证失败");
			}
		}

	}
    /**
     * 支付宝回调
     * @param request
     * @param response
     */
	@RequestMapping("/alipay_ret_pay_notify.htm")
	public String alipay_ret_pay_notify(HttpServletRequest request, HttpServletResponse response) {
		String tipInfo = "";
		String reqUrl = "";
		boolean flag = false;
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		String batch_no = request.getParameter("batch_no");
		String success_num = request.getParameter("success_num");
		String result_details = request.getParameter("result_details");

		String order_id = null;

		String refund_user_id = "";
		String obj_id = "";
		String info = "";
		String gi_id = "";
		String enough_reduce_discount="";

		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("batch_no", batch_no);

		int batchNoLen = batch_no.length();
		String batchNoType = batch_no.substring(batchNoLen - 1, batchNoLen);
		if ("0".equals(batchNoType)) {
			reqUrl = "/admin/refund_list.htm";
			List<ReturnGoodsLog> returnGoodsLogList = this.returnGoodsLogService
					.query("select obj from ReturnGoodsLog obj where obj.alipayBatchNo=:batch_no", paraMap, -1, -1);
			ReturnGoodsLog returnGoodsLog = returnGoodsLogList.get(0);
			order_id = String.valueOf(returnGoodsLog.getReturn_order_id());
			refund_user_id = String.valueOf(returnGoodsLog.getUser_id());
			obj_id = String.valueOf(returnGoodsLog.getId());
			info = returnGoodsLog.getReturnRemark();
		} else if ("1".equals(batchNoType)) {
			reqUrl = "/admin/groupinfo_refund_list.htm";
			List<GroupInfo> groupInfoList = this.groupinfoService
					.query("select obj from GroupInfo obj where obj.alipayBatchNo=:batch_no", paraMap, -1, -1);
			GroupInfo groupInfo = groupInfoList.get(0);
			order_id = String.valueOf(groupInfo.getOrder_id());
			refund_user_id = String.valueOf(groupInfo.getUser_id());
			gi_id = String.valueOf(groupInfo.getId());
			info = groupInfo.getReturnRemark();
		}else if("2".equals(batchNoType)){//订单退款
			OrderForm of=this.orderformService.getObjByProperty(null, "alipay_batch_no", batch_no);
			order_id=of.getId().toString();
			refund_user_id=of.getUser_id();
			info=of.getRefund_cause();
			enough_reduce_discount=of.getEnough_reduced_amount().toString();
		}

		OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		Payment payment = orderForm.getPayment();
		String partner = payment.getPartner();
		String ali_public_key = payment.getApp_public_key();
		String private_key = payment.getApp_private_key();
		String sign_type = payment.getAlipaySignType();
		String safeKey = payment.getSafeKey();

		AlipayConfig alipayConfig = new AlipayConfig();
		alipayConfig.setSign_type(sign_type);
		alipayConfig.setKey(safeKey);
		alipayConfig.setPartner(partner);
		alipayConfig.setAli_public_key(ali_public_key);
		alipayConfig.setPrivate_key(private_key);
		alipayConfig.setKey(safeKey);
		alipayConfig.setSign_type(sign_type);

		flag = AlipayNotify.verify(alipayConfig, params);
		if (flag) {
			String[] retFee = result_details.split("\\$");

			String retResultStr = retFee[0];
			String[] retValue = retResultStr.split("\\^");
			String resultVal = retValue[2];
			if ("SUCCESS".equals(resultVal)) {
				String retAmount = retValue[1];
				if("2".equals(batchNoType)){//订单退款
				  this.ret_pay_save(request,response,"",retAmount,info,refund_user_id,order_id,enough_reduce_discount, "","3"); 
				}else{
				this.ret_pay_save(request, response, "", retAmount, info, refund_user_id, obj_id, "3", gi_id);
				}
				tipInfo = "alipay平台退款成功，退款金额：" + retAmount;
			} else {
				tipInfo = "alipay平台退款失败";
				logger.error(tipInfo);
			}

		} else {
			tipInfo = "alipay平台退款回调验证失败";
			logger.error(tipInfo);
		}
		tipInfo = URLEncoder.encode(tipInfo);
		return "redirect:ret_pay_after.htm?currentPage=" + "&tipInfo=" + tipInfo + "&reqUrl=" + reqUrl;
	}

	public void ret_pay_save(HttpServletRequest request, HttpServletResponse response, String user_id, String amount,
			String info, String refund_user_id, String obj_id, String status, String gi_id) {

		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		} else {
			user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
		}

		if (!StringUtils.isNullOrEmpty(obj_id)) {// 商品退款
			ReturnGoodsLog rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));

			OrderForm order = this.orderformService.getObjById(rgl.getReturn_order_id());
			// 修改优惠券已退金额和满减已退金额
			refundTools.updateOrderDiscount(order, rgl.getCouponDiscount().toString(),
					rgl.getEnoughReduceDiscount().toString(), CommUtil.null2String(rgl.getGoods_id()));

			rgl.setRefund_status(1);
			if ("1".equals(status)) {
				rgl.setRefund_status(2);
			}
			rgl.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			rgl.setGoods_return_status("11");// 平台退款完成
			this.returnGoodsLogService.update(rgl);

			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setReturnLog_id(rgl.getId());
			r_log.setReturnService_id(rgl.getReturn_service_id());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type("退回支付方");
			r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
			r_log.setReturnLog_userName(rgl.getUser_name());
			r_log.setReturnLog_userId(rgl.getUser_id());
			this.refundLogService.save(r_log);

			OrderForm of = this.orderFormService.getObjById(rgl.getReturn_order_id());
			Goods goods = this.goodsService.getObjById(rgl.getGoods_id());
			// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
			if (goods.getGoods_type() == 1) {
				Store store = this.goodsService.getObjById(rgl.getGoods_id()).getGoods_store();
				PayoffLog pol = new PayoffLog();
				pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
				pol.setAddTime(new Date());
				pol.setGoods_info(of.getReturn_goods_info());
				pol.setRefund_user_id(rgl.getUser_id());
				pol.setSeller(goods.getGoods_store().getUser());
				pol.setRefund_userName(rgl.getUser_name());
				pol.setReturn_service_id(rgl.getReturn_service_id());
				pol.setPayoff_type(-1);
				pol.setPl_info("退款完成");
				BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
				BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(1, rgl.getGoods_commission_rate()));// 商品的佣金比例
				BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, CommUtil.mul(price, mission)));
				pol.setTotal_amount(final_money);
				List<Map> list = new ArrayList<Map>();
				Map json = new HashMap();
				json.put("goods_id", rgl.getGoods_id());
				json.put("goods_name", rgl.getGoods_name());
				json.put("goods_price", rgl.getGoods_price());
				json.put("goods_mainphoto_path", rgl.getGoods_mainphoto_path());
				json.put("goods_commission_rate", rgl.getGoods_commission_rate());
				json.put("goods_count", rgl.getGoods_count());
				json.put("goods_all_price", rgl.getGoods_all_price());
				json.put("goods_commission_price", CommUtil.mul(rgl.getGoods_all_price(), rgl.getGoods_commission_rate()));
				json.put("goods_payoff_price", final_money);
				list.add(json);
				pol.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
				pol.setO_id(CommUtil.null2String(rgl.getReturn_order_id()));
				pol.setOrder_id(of.getOrder_id());
				pol.setCommission_amount(BigDecimal.valueOf(0));
				pol.setOrder_total_price(final_money);
				pol.setShip_price(BigDecimal.valueOf(0.00)); //退货运费为零
				this.payoffLogService.save(pol);

				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
				store.setStore_payoff_amount(
						BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), CommUtil.mul(price, mission))));// 减少店铺本次结算总金额
				this.storeService.update(store);

				// 减少系统总销售金额、总结算金额
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				sc.setPayoff_all_amount(
						BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(price, mission))));
				sc.setPayoff_all_amount_reality(
						BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
				this.configService.update(sc);
			}

			String msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元退款完成。";
			if ("1".equals(status)) {
				msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元提交完成。";
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
		} else {
			GroupInfo gi = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
			gi.setStatus(7); // 退款完成
			if ("1".equals(status)) {
				gi.setStatus(9);
			}
			this.groupinfoService.update(gi);
			OrderForm of = this.orderFormService.getObjById(gi.getOrder_id());
			if (of.getOrder_form() == 0) { // 商家订单生成退款结算账单
				Store store = this.storeService.getObjById(CommUtil.null2Long(of.getStore_id()));
				PayoffLog pol = new PayoffLog();
				pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
				pol.setAddTime(new Date());
				pol.setGoods_info(of.getReturn_goods_info());
				pol.setRefund_user_id(gi.getUser_id());
				pol.setSeller(store.getUser());
				pol.setRefund_userName(gi.getUser_name());
				pol.setPayoff_type(-1);
				pol.setPl_info("退款完成");
				BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
				BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, price));
				pol.setTotal_amount(final_money);
				// 将订单中group_info（{}）转换为List<Map>([{}])
				List<Map> Map_list = new ArrayList<Map>();
				Map group_map = this.orderFormTools.queryGroupInfo(of.getGroup_info());
				Map_list.add(group_map);
				pol.setReturn_goods_info(Json.toJson(Map_list, JsonFormat.compact()));
				pol.setO_id(of.getId().toString());
				pol.setOrder_id(of.getOrder_id());
				pol.setCommission_amount(BigDecimal.valueOf(0));
				pol.setOrder_total_price(final_money);
				pol.setShip_price(BigDecimal.valueOf(0.00)); //退货运费为零
				this.payoffLogService.save(pol);

				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount))); // 减少店铺本次结算总销售金额
				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), price))); // 减少店铺本次结算总金额
				this.storeService.update(store);
				// 减少系统总销售金额、总结算金额
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				sc.setPayoff_all_amount(
						BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(amount, 0))));
				sc.setPayoff_all_amount_reality(
						BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
				this.configService.update(sc);
			}
			// 生成退款日志
			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setReturnLog_id(gi.getId());
			r_log.setReturnService_id(gi.getGroup_sn());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type("退回支付方");
			r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
			r_log.setReturnLog_userName(gi.getUser_name());
			r_log.setReturnLog_userId(gi.getUser_id());
			this.refundLogService.save(r_log);
			String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功退款，退款金额为："
					+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
			if ("1".equals(status)) {
				msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功提交退款，退款金额为："
						+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
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
	//订单退款
	public void ret_pay_save(HttpServletRequest request, HttpServletResponse response, String user_id, String amount,
			String info, String refund_user_id, String o_id,String enough_reduce_discount, String coupon_discount, String status) {

		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		} else {
			user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
		}
		// 订单退款
		if (!StringUtils.isNullOrEmpty(o_id)) {

			OrderForm order = this.orderformService.getObjById(CommUtil.null2Long(o_id));
			//退款成功后修改满减json
			orderFormService.modifyEnoughReduceInfo(order);
			//退款成功修改运费json
			orderFormService.modifyFreightInfo(order);
			//成功后，修改优惠的活动金额
			orderFormService.modifyActivityAmount(order);
			// 修改优惠券已退金额和满减已退金额和订单状态
			order.setOrder_status(90);
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
	
	@SecurityMapping(title = "平台退款完成", value = "/admin/refund_finish.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_finish.htm")
	public ModelAndView predeposit_modify_save(HttpServletRequest request, HttpServletResponse response, String user_id,
			String amount, String type, String info, String list_url, String refund_user_id, String obj_id, String gi_id,
			String enough_reduce_discount, String coupon_discount) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			User user = null;
			if (!StringUtils.isNullOrEmpty(user_id)) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
			}
			OrderForm order = null;
			ReturnGoodsLog rgl = null;
			GroupInfo gi = null;
			ResultDTO resultDTO = null;
			String trade_no = null;
			if (!StringUtils.isNullOrEmpty(obj_id)) { // 商品退款
				rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));
				order = this.orderformService.getObjById(rgl.getReturn_order_id());
				trade_no = rgl.getReturn_service_id();
			}
			if (!StringUtils.isNullOrEmpty(gi_id)) { // 消费码退款
				gi = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
				order = this.orderformService.getObjById(gi.getOrder_id());
				trade_no = gi.getGroup_sn();
			}
			if ("balance".equals(order.getPayment().getMark())) { // 预存卡支付，则进行退款操作
				if (!StringUtils.isNullOrEmpty(obj_id)) { // 商品退款
					resultDTO = feeManageservice.orderCancle(order, rgl, amount);
				}
				if (!StringUtils.isNullOrEmpty(gi_id)) { // 消费码退款
					resultDTO = feeManageservice.orderCancle(order, gi, amount);
				}
			} else {// 否则进行充值操作
				// TODO 当前如果是使用第三方平台支付的在进行退款时则进行充值操作，后续修改为哪里支付的则退到哪里
				resultDTO = feeManageConnector.recharge(user.getCustId(), amount, trade_no); // 该订单号为退款
			}

			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), amount)));//余额都在CRM端保存和增减，故注释掉
				//this.userService.update(user);
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
				if (!StringUtils.isNullOrEmpty(obj_id)) { // 商品退款
					// ReturnGoodsLog rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));
					rgl.setRefund_status(1);
					rgl.setGoods_return_status("11"); // 平台退款完成
					rgl.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));// 实际退款金额
					rgl.setCouponDiscount(BigDecimal.valueOf(CommUtil.null2Double(coupon_discount)));// 优惠券折扣
					rgl.setEnoughReduceDiscount(BigDecimal.valueOf(CommUtil.null2Double(enough_reduce_discount)));// 满减折扣
					this.returnGoodsLogService.update(rgl);
					RefundLog rLog = new RefundLog();
					rLog.setAddTime(new Date());
					rLog.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
					rLog.setReturnLog_id(rgl.getId());
					rLog.setReturnService_id(rgl.getReturn_service_id());
					rLog.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
					rLog.setRefund_log(info);
					rLog.setRefund_type("预存款");
					rLog.setRefund_user(SecurityUserHolder.getCurrentUser());
					rLog.setReturnLog_userName(rgl.getUser_name());
					rLog.setReturnLog_userId(rgl.getUser_id());
					this.refundLogService.save(rLog);
					OrderForm of = this.orderFormService.getObjById(rgl.getReturn_order_id());
					// 修改优惠券已退金额和满减已退金额
					refundTools.updateOrderDiscount(order, coupon_discount, enough_reduce_discount,
							CommUtil.null2String(rgl.getGoods_id()));

					Goods goods = this.goodsService.getObjById(rgl.getGoods_id());
					// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
					if (goods.getGoods_type() == 1) {
						Store store = this.goodsService.getObjById(rgl.getGoods_id()).getGoods_store();
						PayoffLog pol = new PayoffLog();
						pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						pol.setAddTime(new Date());
						pol.setGoods_info(of.getReturn_goods_info());
						pol.setRefund_user_id(rgl.getUser_id());
						pol.setSeller(goods.getGoods_store().getUser());
						pol.setRefund_userName(rgl.getUser_name());
						pol.setReturn_service_id(rgl.getReturn_service_id());
						pol.setPayoff_type(-1);
						pol.setPl_info("退款完成");
						BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
						BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(1, rgl.getGoods_commission_rate())); // 商品的佣金比例
						BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, CommUtil.mul(price, mission)));
						pol.setTotal_amount(final_money);
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
						pol.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
						pol.setO_id(CommUtil.null2String(rgl.getReturn_order_id()));
						pol.setOrder_id(of.getOrder_id());
						pol.setCommission_amount(BigDecimal.valueOf(0));
						pol.setOrder_total_price(final_money);
						pol.setShip_price(BigDecimal.valueOf(0.00)); //退货运费为零
						this.payoffLogService.save(pol);
						store.setStore_sale_amount(
								BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount))); // 减少店铺本次结算总销售金额
						store.setStore_payoff_amount(BigDecimal
								.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), CommUtil.mul(price, mission)))); // 减少店铺本次结算总金额
						this.storeService.update(store);
						// 减少系统总销售金额、总结算金额
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
						sc.setPayoff_all_amount(BigDecimal
								.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(price, mission))));
						sc.setPayoff_all_amount_reality(BigDecimal
								.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
						this.configService.update(sc);
					}
					String msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元，请到收支明细中查看。";
					// 发送系统站内信
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user);
					this.messageService.save(msg);
					mv.addObject("list_url", CommUtil.getURL(request) + "/admin/refund_list.htm");
				}
				if (!StringUtils.isNullOrEmpty(gi_id)) { // 消费码退款
					// GroupInfo gi = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
					gi.setStatus(7); // 退款完成
					this.groupinfoService.update(gi);
					OrderForm of = this.orderFormService.getObjById(gi.getOrder_id());
					if (of.getOrder_form() == 0) { // 商家订单生成退款结算账单
						Store store = this.storeService.getObjById(CommUtil.null2Long(of.getStore_id()));
						PayoffLog pol = new PayoffLog();
						pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						pol.setAddTime(new Date());
						pol.setGoods_info(of.getReturn_goods_info());
						pol.setRefund_user_id(gi.getUser_id());
						pol.setSeller(store.getUser());
						pol.setRefund_userName(gi.getUser_name());
						pol.setPayoff_type(-1);
						pol.setPl_info("退款完成");
						BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
						BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, price));
						pol.setTotal_amount(final_money);
						// 将订单中group_info（{}）转换为List<Map>([{}])
						List<Map> Map_list = new ArrayList<Map>();
						Map group_map = this.orderFormTools.queryGroupInfo(of.getGroup_info());
						Map_list.add(group_map);
						pol.setReturn_goods_info(Json.toJson(Map_list, JsonFormat.compact()));
						pol.setO_id(of.getId().toString());
						pol.setOrder_id(of.getOrder_id());
						pol.setCommission_amount(BigDecimal.valueOf(0));
						pol.setOrder_total_price(final_money);
						pol.setShip_price(BigDecimal.valueOf(0.00)); //退货运费为零
						this.payoffLogService.save(pol);

						store.setStore_sale_amount(
								BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount))); // 减少店铺本次结算总销售金额
						store.setStore_payoff_amount(
								BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), price))); // 减少店铺本次结算总金额
						this.storeService.update(store);
						// 减少系统总销售金额、总结算金额
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
						sc.setPayoff_all_amount(
								BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), CommUtil.mul(amount, 0))));
						sc.setPayoff_all_amount_reality(BigDecimal
								.valueOf(CommUtil.add(pol.getReality_amount(), sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
						this.configService.update(sc);
					}
					// 生成退款日志
					RefundLog r_log = new RefundLog();
					r_log.setAddTime(new Date());
					r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
					r_log.setReturnLog_id(gi.getId());
					r_log.setReturnService_id(gi.getGroup_sn());
					r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
					r_log.setRefund_log(info);
					r_log.setRefund_type("预存款");
					r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
					r_log.setReturnLog_userName(gi.getUser_name());
					r_log.setReturnLog_userId(gi.getUser_id());
					this.refundLogService.save(r_log);
					String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功退款，退款金额为："
							+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
					// 发送系统站内信
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user);
					this.messageService.save(msg);
					mv.addObject("op_title", "退款成功");
					mv.addObject("list_url", CommUtil.getURL(request) + "/admin/groupinfo_refund_list.htm");
				}
			} else {
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", resultDTO.getMsg());
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

}
