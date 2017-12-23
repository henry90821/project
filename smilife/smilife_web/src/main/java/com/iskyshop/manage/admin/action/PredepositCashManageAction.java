package com.iskyshop.manage.admin.action;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.PredepositCash;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PredepositCashQueryObject;
import com.iskyshop.foundation.service.IPredepositCashService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: PredepositCashManageAction.java
 * </p>
 * 
 * <p>
 * Description:商城用户提现管理控制器，用来显示用户提现列表、处理用户提现申请、查询提现详情
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
public class PredepositCashManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPredepositCashService predepositcashService;
	@Autowired
	private IUserService userService;

	/**
	 * PredepositCash列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "提现申请列表", value = "/admin/predeposit_cash.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_cash.htm")
	public ModelAndView predeposit_cash(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String q_pd_userName, String q_beginTime, String q_endTime,
			String q_cash_payment, String q_cash_pay_status, String q_cash_status, String q_cash_userName,
			String q_cash_remittance_user, String q_cash_remittance_bank) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_cash.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			PredepositCashQueryObject qo = new PredepositCashQueryObject(currentPage, mv, orderBy, orderType);
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, PredepositCash.class, mv);
			if (!StringUtils.isNullOrEmpty(q_cash_payment)) {
				qo.addQuery("obj.cash_payment", new SysMap("cash_payment", q_cash_payment), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_cash_userName)) {
				qo.addQuery("obj.cash_userName", new SysMap("cash_userName", q_cash_userName), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_cash_status)) {
				qo.addQuery("obj.cash_status", new SysMap("cash_status", CommUtil.null2Int(q_cash_status)), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_cash_remittance_user)) {
				qo.addQuery("obj.cash_user.userName", new SysMap("cash_remittance_user", q_cash_remittance_user), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_cash_remittance_bank)) {
				qo.addQuery("obj.cash_bank", new SysMap("cash_remittance_bank", q_cash_remittance_bank), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_cash_pay_status)) {
				qo.addQuery("obj.cash_bank", new SysMap("cash_pay_status", CommUtil.null2Int(q_cash_pay_status)), "=");
			}
			if (!StringUtils.isNullOrEmpty(q_beginTime)) {
				qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(q_beginTime)), ">=");
			}
			if (!StringUtils.isNullOrEmpty(q_endTime)) {
				qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(q_endTime)), "<=");
			}
			IPageList pList = this.predepositcashService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("q_cash_payment", q_cash_payment);
			mv.addObject("q_cash_pay_status", q_cash_pay_status);
			mv.addObject("q_cash_userName", q_cash_userName);
			mv.addObject("q_cash_status", q_cash_status);
			mv.addObject("q_cash_remittance_user", q_cash_remittance_user);
			mv.addObject("q_cash_remittance_bank", q_cash_remittance_bank);
			mv.addObject("q_beginTime", q_beginTime);
			mv.addObject("q_endTime", q_endTime);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * predepositcash编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "提现申请编辑", value = "/admin/predeposit_cash_edit.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_cash_edit.htm")
	public ModelAndView predeposit_cash_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_cash_edit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			if (!StringUtils.isNullOrEmpty(id)) {
				PredepositCash predepositcash = this.predepositcashService.getObjById(Long.parseLong(id));
				mv.addObject("obj", predepositcash);
				mv.addObject("currentPage", currentPage);
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * predepositcash保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "提现申请编辑保存", value = "/admin/predeposit_cash_save.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_cash_save.htm")
	public ModelAndView predeposit_cash_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String cmd, String list_url) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			WebForm wf = new WebForm();
			PredepositCash obj = this.predepositcashService.getObjById(Long.parseLong(id));
			PredepositCash predepositcash = (PredepositCash) wf.toPo(request, obj);
			if (obj.getCash_pay_status() == 1) {
				obj.setCash_status(1);
			}
			obj.setCash_admin(SecurityUserHolder.getCurrentUser());
			this.predepositcashService.update(predepositcash);
			//User user = obj.getCash_user();
			//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), predepositcash.getCash_amount())));//余额都在CRM端保存和增减，故注释掉
			//this.userService.update(user);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "审核提现申请成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "提现申请详情", value = "/admin/predeposit_cash_view.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_cash_view.htm")
	public ModelAndView predeposit_cash_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_cash_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			if (!StringUtils.isNullOrEmpty(id)) {
				PredepositCash predepositcash = this.predepositcashService.getObjById(Long.parseLong(id));
				mv.addObject("obj", predepositcash);
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