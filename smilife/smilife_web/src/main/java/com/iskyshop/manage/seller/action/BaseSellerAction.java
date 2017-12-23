package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.seller.tools.MenuTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.OrderViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.smilife.bcp.service.UserManageConnector;

/**
 * 
 * <p>
 * Title: BaseSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台基础管理器 主要功能包括商家后台的基础管理、快捷菜单设置等
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class BaseSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private MenuTools menuTools;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private UserManageConnector manageConnector;
	@Autowired
	private MsgTools msgTools;

	/**
	 * 商城商家登录入口，商家登录后只能进行商家操作，不能进行购物等其他操作，系统严格区分商家、买家、管理员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/seller/login.htm")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User curUser = SecurityUserHolder.getCurrentUser();
		if(curUser != null) {
			if("ADMIN".equals(curUser.getCurrentLoginRole())) {
				response.sendRedirect(request.getContextPath() + "/admin/index.htm");
			} else if("BUYER".equals(curUser.getCurrentLoginRole())){
				response.sendRedirect(request.getContextPath() + "/buyer/index.htm");
			} else {
				response.sendRedirect(request.getContextPath() + "/seller/index.htm");
			}
			return null;
		}
		
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_login.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(request.getSession() != null) {
			request.getSession().removeAttribute("verify_code");// 如果系统未开启前台登录验证码，则需要移除session中保留的验证码信息
		}
		return mv;
	}

	/**
	 * 商家后台顶部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/top.htm")
	public ModelAndView top(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_top.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 商家中心首页，该请求受系统ss权限管理，对应角色名为"商家中心",商家中心添加子账户时默认添加“商家中心”权限，“ user_center_seller”不可更改
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/seller/index.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/index.htm")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_index.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("class_mark", "new_func");
		params.put("display", true);
		List<Article> func_articles = this.articleService.query(
				"select new Article(id, title) from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc",
				params, 0, 5);
		mv.addObject("func_articles", func_articles);
		
		params.clear();
		params.put("type", "store");// 只查询给商家看的文章信息
		params.put("display", true);
		params.put("class_mark", "new_func");
		List<Article> articles = this.articleService.query(
				"select new Article(id, title) from Article obj where obj.type=:type and obj.articleClass.mark!=:class_mark and obj.display=:display order by obj.addTime desc",
				params, 0, 5);
		mv.addObject("articles", articles);
		
		params.clear();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_sale_list = this.goodsService.query(
				"select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
				params, 0, 5);
		mv.addObject("goods_sale_list", goods_sale_list);
		
		long goods_num1 = 0;
		long goods_num2 = 0;
		long goods_num3 = 0;
		List goods = this.goodsService.executeNativeQuery("select goods_status, count(*) from iskyshop_goods where (goods_status=0 or goods_status=1 or goods_status=-2) and goods_store_id=" + store.getId() + " group by goods_status");
		for(int i = 0; i < goods.size(); i++) {
			Object[] row = (Object[])goods.get(i);
			if((int)row[0] == 0) {
				goods_num1 = Long.parseLong(row[1].toString());
			} else if((int)row[0] == 1) {
				goods_num2 = Long.parseLong(row[1].toString());
			} else if((int)row[0] == -2) {
				goods_num3 = Long.parseLong(row[1].toString());
			} 
		}
		mv.addObject("goods_num1", goods_num1);
		mv.addObject("goods_num2", goods_num2);
		mv.addObject("goods_num3", goods_num3);
		
		int returngoodsCount = this.returngoodslogService.getCountByNativeSql("select count(*) from iskyshop_returngoods_log where goods_return_status=5 and store_id=" + store.getId());
		mv.addObject("returngoodsCount", returngoodsCount);
		
		List  orders = this.orderformService.executeNativeQuery("select order_status, count(*) from iskyshop_orderform where order_status in (10,20,30,40) and store_id='" + store.getId() + "' group by order_status");
		long of10 = 0;
		long of20 = 0;
		long of30 = 0;
		long of40 = 0;
		for(int i = 0; i < orders.size(); i++) {
			Object[] row = (Object[])orders.get(i);
			if((int)row[0] == 10) {
				of10 = Long.parseLong(row[1].toString());
			} else if((int)row[0] == 20) {
				of20 = Long.parseLong(row[1].toString());
			} else if((int)row[0] == 30) {
				of30 = Long.parseLong(row[1].toString());
			} else if((int)row[0] == 40) {
				of40 = Long.parseLong(row[1].toString());
			}
		}
		mv.addObject("ordersCount", orders.size());
		mv.addObject("of10", of10);
		mv.addObject("of20", of20);
		mv.addObject("of30", of30);
		mv.addObject("of40", of40);
		
		int msgsCount = this.consultService.getCountByNativeSql("select count(*) from iskyshop_consult where reply=0 and store_id=" + store.getId());
		mv.addObject("msgsCount", msgsCount);
	
		int complaintsCount = this.complaintService.getCountByNativeSql("select count(*) from iskyshop_complaint where status=0 and to_user_id=" + user.getId());
		mv.addObject("complaintsCount", complaintsCount);
		
		
		mv.addObject("user", user);
		mv.addObject("store", store);
		
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		mv.addObject("menuTools", menuTools);
		return mv;
	}

	@SecurityMapping(title = "商家中心导航", value = "/seller/nav.htm*", rtype = "seller", rname = "商家中心导航", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/nav.htm")
	public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_nav.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("op", op);
		mv.addObject("user", user);
		int store_status = (user.getStore() == null ? 0 : user.getStore().getStore_status());
		if (store_status != 15) {
			mv.addObject("limit", true);
		}
		return mv;
	}

	@SecurityMapping(title = "商家中心快捷功能设置保存", value = "/seller/store_quick_menu_save.htm*", rtype = "seller", rname = "商家中心快捷功能设置保存", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/store_quick_menu_save.htm")
	public void store_quick_menu_save(HttpServletRequest request, HttpServletResponse response, String menus) {
		String[] menu_navs = menus.split(";");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> list = new ArrayList<Map>();
		for (String menu_nav : menu_navs) {
			if (!StringUtils.isNullOrEmpty(menu_nav)) {
				String[] infos = menu_nav.split(",");
				Map map = new HashMap();
				map.put("menu_name", infos[0]);
				map.put("menu_url", infos[1]);
				list.add(map);
			}
		}
		store.setStore_quick_menu(Json.toJson(list, JsonFormat.compact()));
		this.storeService.update(store);
		String ret = Json.toJson(list, JsonFormat.compact());
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

	/**
	 * 商家后台操作成功提示页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/success.htm")
	public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", request.getSession(true).getAttribute("op_title"));
		mv.addObject("url", request.getSession(true).getAttribute("url"));
		request.getSession(true).removeAttribute("op_title");
		request.getSession(true).removeAttribute("url");
		return mv;
	}

	/**
	 * 商家后台操作错误提示页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/seller/error.htm")
	public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", request.getSession(true).getAttribute("op_title"));
		mv.addObject("url", request.getSession(true).getAttribute("url"));
		request.getSession(true).removeAttribute("op_title");
		request.getSession(true).removeAttribute("url");
		return mv;
	}

	/**
	 * 店铺到期关闭后，商家申请续费重新开店
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "商家中心申请重新开店", value = "/seller/store_renew.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/store_renew.htm")
	public void store_renew(HttpServletRequest request, HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Boolean ret = false;
		if (user.getStore() != null) {
			Store store = user.getStore();
			store.setStore_status(26);
			this.storeService.update(store);
			ret = true;
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
	
	@RequestMapping("/seller/authority.htm")
	public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("/user/default/sellercenter/seller_error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "您没有权限");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		return mv;
	}
	
	@SecurityMapping(title = "登录密码修改", value = "/seller/account_password.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/account_password.htm")
	public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/account_password.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	@SecurityMapping(title = "密码修改保存", value = "/seller/account_password_save.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/account_password_save.htm")
	public ModelAndView account_password_save(HttpServletRequest request, HttpServletResponse response, String old_password,
			String new_password) throws Exception {
		ModelAndView mv = null;
		String errMsg = null;
		old_password = old_password.trim();//旧密码和新密码都不应该为null，否则抛空指针异常
		new_password = new_password.trim();
		if("".equals(new_password)) {
			errMsg = "新密码不能为空";
		} else {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if(user.getPassword().equals(Md5Encrypt.md5(old_password).toLowerCase())) {
				user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
				boolean ret = this.userService.update(user);
				if(ret) {
					if(!StringUtils.isNullOrEmpty(user.getMobile())) {
						try {
							String content = "尊敬的" + user.getSellerLoginAccount() + "，您好，您于"
									+ CommUtil.formatLongDate(new Date()) + "修改店铺登录密码成功，新密码为：" + new_password + ",请妥善保管。["
									+ this.configService.getSysConfig().getTitle() + "]";
							this.msgTools.sendSMS(user.getMobile(), content);
						} catch (Exception e) {
							logger.error("商家修改登录密码失败：" + e.getMessage(), e);
						}
					}
				} else {
					errMsg = "系统错误导致您修改登录密码失败，请重试";
				}
			} else {
				errMsg = "原始密码错误，修改失败";
			}
		}		

		if(errMsg == null) {			
			mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "密码修改成功");			
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
					request, response);
			mv.addObject("op_title", errMsg);
		}
		
		mv.addObject("url", CommUtil.getURL(request) + "/seller/account_password.htm");
		return mv;
	}
}
