package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.RoleGroup;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IRoleGroupService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: SubAccountSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家子账户管理，卖家根据店铺等级，可以有多个子账户，子账户可以协助卖家管理店铺，卖家自行添加子账户信息，并可以自行给子账户赋予相关卖家中心权限
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
public class SubAccountSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private IRoleService roleService;

	@SecurityMapping(title = "子账户列表", value = "/seller/sub_account_list.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_list.htm")
	public ModelAndView sub_account_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/sub_account_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy, orderType);
		uqo.addQuery("obj.parent.id", new SysMap("user_ids", user.getId()), "=");
		IPageList pList = this.userService.list(uqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "子账户添加", value = "/seller/sub_account_add.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_add.htm")
	public ModelAndView sub_account_add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/sub_account_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		if (user.getChilds().size() >= store.getGrade().getAcount_num()) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您的店铺等级不能继续添加子账户,请升级店铺等级");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/sub_account_list.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc", params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "子账户编辑", value = "/seller/sub_account_edit.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_edit.htm")
	public ModelAndView sub_account_edit(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/sub_account_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc", params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("obj", this.userService.getObjById(CommUtil.null2Long(id)));
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "子账户保存", value = "/seller/sub_account_save.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_save.htm")
	public void sub_account_save(HttpServletRequest request, HttpServletResponse response, String id, String loginAccount,
			String trueName, String sex, String birthday, String QQ, String telephone, String mobile, String password, String role_ids) {
		String ret = "succeed";
		String msg = "子账户创建成功";	
		
		User parent = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (parent.getParent() != null) {
			parent = parent.getParent();
		}
		Store store = parent.getStore();
		
		User user = null;
		boolean toUpdateOrSave = false;
		if (StringUtils.isNullOrEmpty(id)) {//新增子账号
			if (parent.getChilds().size() >= store.getGrade().getAcount_num()) {
				ret = "error";
				msg = "已经超过子账户上限";
			} else {
				loginAccount = CommUtil.clearContent(loginAccount);
				if("admin".equalsIgnoreCase(loginAccount)) {
					ret = "error";
					msg = "账户名不能为admin";
				} else if(this.userService.isSellerAccountExisted(loginAccount)) {
					ret = "error";
					msg = "子账户已存在";
				} else {
					user = new User();
					user.setAddTime(new Date());
					user.setUserName(loginAccount);
					user.setSellerLoginAccount(loginAccount);
					user.setTrueName(trueName);
					user.setSex(CommUtil.null2Int(sex));
					user.setBirthday(birthday == null? null:CommUtil.formatDate(birthday));
					user.setQQ(QQ);
					user.setMobile(mobile);
					user.setTelephone(telephone);
					user.setParent(parent);
					user.setUserRole("SELLER");
					user.setPassword(Md5Encrypt.md5(password).toLowerCase());
					toUpdateOrSave = true;
				}		
			}
		} else {//编辑子账号			
			user = this.userService.getObjById(CommUtil.null2Long(id));
			if(user == null || user.getParent() == null || user.getParent().getId() != parent.getId()) {//用户篡改了前端数据
				ret = "error";
				msg = "提交的数据有误";
			} else {
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(birthday == null? null:CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				if(StringUtils.hasText(password)) {
					user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				}
				msg = "子账户更新成功";
				toUpdateOrSave = true;
			}				
		}
		
		if(toUpdateOrSave) {
			user.getRoles().clear();
			for (String role_id : role_ids.split(",")) {
				if (!StringUtils.isNullOrEmpty(role_id)) {
					Role role = this.roleService.getObjById(CommUtil.null2Long(role_id));
					if (role != null && "SELLER".equalsIgnoreCase(role.getType())) {
						user.getRoles().add(role);
					}
				}
			}
			// 默认赋予“商家中心”权限
			Map params = new HashMap();
			params.put("type", "SELLER");
			params.put("roleCode", "ROLE_USER_CENTER_SELLER");
			List<Role> center_roles = this.roleService
					.query("select obj from Role obj where obj.type=:type and obj.roleCode=:roleCode", params, -1, -1);
			for (Role r : center_roles) {
				user.getRoles().add(r);
			}
			
			if(StringUtils.isNullOrEmpty(id)) {
				this.userService.save(user);
			} else {
				this.userService.update(user);
			}
		}
		
		Map map = new HashMap();
		map.put("ret", ret);
		map.put("msg", msg);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error("保存子账号异常：" + e);
		}
	}

	@SecurityMapping(title = "子账户删除", value = "/seller/sub_account_del.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_del.htm")
	public String sub_account_del(HttpServletRequest request, HttpServletResponse response, String mulitId) {
		User user = this.userService.getObjById(CommUtil.null2Long(mulitId));
		user.getRoles().clear();
		this.userService.delete(user.getId());
		return "redirect:sub_account_list.htm";
	}	
	
	@RequestMapping("/seller/is_sub_account_exist.htm")
	public void isSubAccountExisted(HttpServletResponse response, String loginAccount) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		try {
			PrintWriter writer = response.getWriter();
			writer.print(!this.userService.isSellerAccountExisted(CommUtil.clearContent(loginAccount)));
		} catch (IOException e) {
			logger.error("检查子账号是否存在时异常：" + e);
		}
	}

	
}
