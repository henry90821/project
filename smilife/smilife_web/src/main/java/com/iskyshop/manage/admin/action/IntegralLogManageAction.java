package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.IntegralLogQueryObject;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: IntegralLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统积分日志管理类
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
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class IntegralLogManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralLogService integrallogService;
	@Autowired
	private IUserService userService;

	/**
	 * IntegralLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "积分明细", value = "/admin/integrallog_list.htm*", rtype = "admin", rname = "积分明细", rcode = "user_integral", rgroup = "会员")
	@RequestMapping("/admin/integrallog_list.htm")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/integrallog_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		String params = "";
		IntegralLogQueryObject qo = new IntegralLogQueryObject(currentPage, mv, orderBy, orderType);

		if (!StringUtils.isNullOrEmpty(userName))
			qo.addQuery("obj.integral_user.userName", new SysMap("userName", userName), "=");
		mv.addObject("userName", userName);
		IPageList pList = this.integrallogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/integrallog_list.htm", "",
				"&userName=" + CommUtil.null2String(userName), pList, mv);
		return mv;
	}

	@SecurityMapping(title = "积分管理", value = "/admin/user_integral.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/user_integral.htm")
	public ModelAndView user_integral(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/user_integral.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isIntegral()) {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未开启积分功能，设置失败");
			mv.addObject("open_url", "admin/operation_base_set.htm");
			mv.addObject("open_op", "积分开启");
			mv.addObject("open_mark", "operation_base_op");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/welcome.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分动态获取", value = "/admin/verify_user_integral.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/verify_user_integral.htm")
	public void verify_user_integral(HttpServletRequest request, HttpServletResponse response, String userName) {
		User user = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
		int ret = -1;
		if (user != null) {
			ret = user.getIntegral();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "积分管理保存", value = "/admin/user_integral_save.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/user_integral_save.htm")
	public ModelAndView user_integral_save(HttpServletRequest request, HttpServletResponse response, String userName,
			String operate_type, String integral, String content) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// 更新用户积分
		User user = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
		if ("add".equals(operate_type)) {
			user.setIntegral(user.getIntegral() + CommUtil.null2Int(integral));
		} else {
			if (user.getIntegral() > CommUtil.null2Int(integral)) {
				user.setIntegral(user.getIntegral() - CommUtil.null2Int(integral));
			} else {
				user.setIntegral(0);
			}
		}
		this.userService.update(user);
		// 增加积分日志
		IntegralLog log = new IntegralLog();
		log.setAddTime(new Date());
		log.setContent(content);
		if ("add".equals(operate_type)) {
			log.setIntegral(CommUtil.null2Int(integral));
		} else {
			log.setIntegral(-CommUtil.null2Int(integral));
		}
		log.setOperate_user(SecurityUserHolder.getCurrentUser());
		log.setIntegral_user(user);
		log.setType("system");
		this.integrallogService.save(log);
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/user_integral.htm");
		mv.addObject("op_title", "操作用户积分成功");
		return mv;
	}
}