package com.smi.pay.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.pay.model.AppInfo;
import com.smi.pay.model.User;
import com.smi.pay.service.AppInfoService;
import com.smi.pay.service.PayService;
import com.smi.pay.service.RefundService;
import com.smi.pay.service.UserService;

@Controller
public class AdminController {
	@Autowired
	private PayService		payService;
	@Autowired
	private RefundService	refundService;
	@Autowired
	private AppInfoService	appInfoService;
	@Autowired
	private UserService		userService;

	@RequestMapping("/admin/orderlogquery")
	public ModelAndView orderLogList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appcode = request.getParameter("appcode");
		String billNo = request.getParameter("billNo");
		String status = request.getParameter("status");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");

		Map filter = new HashMap();
		if (StringUtils.isNotBlank(appcode)) {
			filter.put("appcode", appcode);
		}
		if (StringUtils.isNotBlank(billNo)) {
			filter.put("billNo", billNo);
		}
		if (StringUtils.isNotBlank(status)) {
			filter.put("code", status);
		}
		if (StringUtils.isNotBlank(beginDate)) {
			filter.put("beginDate", beginDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			filter.put("endDate", endDate);
		}
		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;

		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		// String orderby=request.getParameter("pager.orderBy");
		PageHelper.orderBy("id desc");
		List orderLogs = payService.queryOrderLog(filter);
		PageInfo page = new PageInfo(orderLogs);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/orderlogs", model);
	}

	@RequestMapping("/admin/orderquery")
	public ModelAndView orderList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appcode = request.getParameter("appcode");
		String billNo = request.getParameter("billNo");
		String status = request.getParameter("status");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String curentPage = request.getParameter("endDate");

		Map filter = new HashMap();
		if (StringUtils.isNotBlank(appcode)) {
			filter.put("appcode", appcode);
		}
		if (StringUtils.isNotBlank(billNo)) {
			filter.put("billNo", billNo);
		}
		if (StringUtils.isNotBlank(status)) {
			filter.put("status", status);
		}
		if (StringUtils.isNotBlank(beginDate)) {
			filter.put("beginDate", beginDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			filter.put("endDate", endDate);
		}

		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;

		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		// String orderby=request.getParameter("pager.orderBy");
		PageHelper.orderBy("id desc");
		List orders = payService.queryOrder(filter);
		PageInfo page = new PageInfo(orders);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/orders", model);
	}

	@RequestMapping("/admin/refundquery")
	public ModelAndView refundquery(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appcode = request.getParameter("appcode");
		String billNo = request.getParameter("billNo");
		String status = request.getParameter("status");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String curentPage = request.getParameter("endDate");

		Map filter = new HashMap();
		if (StringUtils.isNotBlank(appcode)) {
			filter.put("appcode", appcode);
		}
		if (StringUtils.isNotBlank(billNo)) {
			filter.put("billNo", billNo);
		}
		if (StringUtils.isNotBlank(status)) {
			filter.put("status", status);
		}
		if (StringUtils.isNotBlank(beginDate)) {
			filter.put("beginDate", beginDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			filter.put("endDate", endDate);
		}

		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;
		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		PageHelper.orderBy("id desc");
		List orders = refundService.queryRefund(filter);
		PageInfo page = new PageInfo(orders);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/refunds", model);
	}

	@RequestMapping("/admin/refundlogquery.do")
	public ModelAndView refundlogquery(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appcode = request.getParameter("appcode");
		String billNo = request.getParameter("billNo");
		String status = request.getParameter("status");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String curentPage = request.getParameter("endDate");

		Map filter = new HashMap();
		if (StringUtils.isNotBlank(appcode)) {
			filter.put("appcode", appcode);
		}
		if (StringUtils.isNotBlank(billNo)) {
			filter.put("billNo", billNo);
		}
		if (StringUtils.isNotBlank(status)) {
			filter.put("code", status);
		}
		if (StringUtils.isNotBlank(beginDate)) {
			filter.put("beginDate", beginDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			filter.put("endDate", endDate);
		}

		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;

		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		PageHelper.orderBy("id desc");
		List orders = refundService.queryRefundLog(filter);
		PageInfo page = new PageInfo(orders);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/refundlogs", model);
	}

	@RequestMapping("/admin/callback.do")
	public ModelAndView callback(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;

		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		// PageHelper.orderBy("kind id");
		List infos = appInfoService.listAppInfo();
		PageInfo page = new PageInfo(infos);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/infos", model);
	}

	@RequestMapping("/admin/callbackedit.do")
	public ModelAndView callbackedit(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");
		AppInfo info = appInfoService.loadAppInfo(Integer.parseInt(id));
		Map model = new HashMap();
		model.put("info", info);
		return new ModelAndView("admin/infoedit", model);
	}

	@RequestMapping("/admin/callbackesave.do")
	public ModelAndView callbackesave(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");
		String appCode = request.getParameter("appCode");
		String appDesc = request.getParameter("appDesc");
		String callBackUrl = request.getParameter("callBackUrl");
		String kind = request.getParameter("kind");
		AppInfo info = appInfoService.loadAppInfo(Integer.parseInt(id));
		info.setAppCode(appCode);
		info.setAppDesc(appDesc);
		info.setCallBackUrl(callBackUrl);
		info.setLastUpdateDate(new Date());
		info.setKind(kind);
		appInfoService.updateAppInfoById(info);
		Map model = new HashMap();
		model.put("info", info);
		return callback(request, response);
	}

	@RequestMapping("/admin/userlist.do")
	public ModelAndView userlist(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String pageSize = request.getParameter("page.pageSize");
		pageSize = StringUtils.isBlank(pageSize) ? "20" : pageSize;

		String pageno = request.getParameter("page.pageNumber");
		pageno = StringUtils.isBlank(pageno) ? "1" : pageno;

		PageHelper.startPage(Integer.parseInt(pageno), Integer.parseInt(pageSize));
		PageHelper.orderBy("id desc");
		List users = userService.listUser();
		PageInfo page = new PageInfo(users);
		Map model = new HashMap();
		model.put("page", page);
		return new ModelAndView("admin/users", model);
	}

	@RequestMapping("/admin/useredit.do")
	public ModelAndView useredit(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");
		User user = new User();
		if (StringUtils.isNotBlank(id)) {
			user = userService.loadUser(Integer.parseInt(id));
		}
		Map model = new HashMap();
		model.put("info", user);
		return new ModelAndView("admin/useredit", model);
	}

	@RequestMapping("/admin/usersave.do")
	public ModelAndView usersave(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String realname = request.getParameter("realname");
		String mobile = request.getParameter("mobile");
		String status = request.getParameter("status");
		String role = request.getParameter("role");
		User user = new User();
		if (StringUtils.isNotBlank(id)) {
			user = userService.loadUser(Integer.parseInt(id));
		}
		user.setUsername(username);
		user.setPassword(password);
		user.setRealname(realname);
		user.setMobile(mobile);
		user.setStatus(status);
		user.setRole(role);
		user.setLastUpdateDate(new Date());
		if (StringUtils.isNotBlank(id)) {
			userService.updateUserById(user);
		}
		else {
			user.setCreateDate(new Date());
			userService.saveUser(user);
		}
		return userlist(request, response);
	}

	// 检查用户名称是否重复
	@RequestMapping("/admin/checkUserName")
	public void checkUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userName = request.getParameter("userName");
		String id = request.getParameter("id");
		User user = userService.getByUserName(userName);
		if (user != null && !user.getId().toString().equals(id)) {
			response.getWriter().print("false");
		}
		else {
			response.getWriter().print("true");
		}

	}

	// 检查用户名密码是否正确
	@RequestMapping("/login.do")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		User user = userService.getByUserName(userName);
		if (user != null && user.getPassword().equals(password) && user.getStatus().equals("0")) {
			request.getSession().setAttribute("user", user);
			response.sendRedirect(request.getContextPath() + "/admin/orderquery.do");
		}
		else {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		}

	}

}
