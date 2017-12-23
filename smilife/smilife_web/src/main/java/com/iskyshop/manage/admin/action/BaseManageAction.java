package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.LogType;
import com.iskyshop.foundation.domain.PredepositCash;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.SystemTip;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.SystemTipQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IFTPServerService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IPredepositCashService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreStatService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.ISystemTipService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.module.app.domain.AppNewlyVersionInfo;
import com.iskyshop.module.app.service.IAppNewlyVerionService;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.service.ISnsAttentionService;
import com.iskyshop.msg.MsgTools;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台管理基础控制，这里包含平台管理的基础方法、系统全局配置信息的保存、修改及一些系统常用请求
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
public class BaseManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private ISysLogService syslogService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IGoldLogService goldlogService;
	@Autowired
	private IStorePointService storepointService;
	@Autowired
	private IPredepositCashService redepositcashService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private ISnsAttentionService snsAttentionService;
	@Autowired
	private IDeliveryAddressService deliveryAddressService;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IFTPServerService ftpService;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private IAppNewlyVerionService appNewlyVerionService;

	/**
	 * 用户登录后去向控制，根据用户角色UserRole进行控制,该请求不纳入权限管理
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Log(title = "用户登陆", type = LogType.LOGIN)
	@RequestMapping("/login_success.htm")
	public void login_success(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if (this.configService.getSysConfig().isIntegral()) {
				if (user.getLoginDate() == null
						|| user.getLoginDate().before(CommUtil.formatDate(CommUtil.formatShortDate(new Date())))) {
					user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getMemberDayLogin());
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户" + CommUtil.formatLongDate(new Date()) + "登录增加"
							+ this.configService.getSysConfig().getMemberDayLogin() + "分");
					log.setIntegral(this.configService.getSysConfig().getMemberRegister());
					log.setIntegral_user(user);
					log.setType("login");
					this.integralLogService.save(log);
				}
			}
			user.setLoginDate(new Date());
			user.setLoginIp(CommUtil.getIpAddr(request));
			user.setLoginCount(user.getLoginCount() + 1);
			this.userService.update(user);
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("lastLoginDate", new Date()); // 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request)); // 设置登录IP
			session.setAttribute("login", true); // 设置登录标识
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success.htm";
			if (!"".equals(CommUtil.null2String(request.getSession(true).getAttribute("refererUrl")))) {
				url = CommUtil.null2String(request.getSession(true).getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					logger.error(e);
				}
			} else {
				if ("admin".equalsIgnoreCase(login_role)) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/admin/index.htm";
						request.getSession(true).setAttribute("admin_login", true);
					}
				}
				if ("seller".equalsIgnoreCase(login_role) && role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/seller/index.htm";
					request.getSession(true).setAttribute("seller_login", true);
				}
				if (!"".equals(CommUtil.null2String(request.getSession(true).getAttribute("refererUrl")))) {
					url = CommUtil.null2String(request.getSession(true).getAttribute("refererUrl"));
					request.getSession(true).removeAttribute("refererUrl");
				}
				// String userAgent = request.getHeader("user-agent");
				// if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
				// url = CommUtil.getURL(request) + "/wap/index.htm";
				// }
				response.sendRedirect(url);
				return;
			}
		} else {
			String url = CommUtil.getURL(request) + "/index.htm";
			response.sendRedirect(url);
			return;
		}

	}

	/**
	 * 用户成功退出后的URL导向
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/logout_success.htm")
	public void logout_success(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		boolean admin_login = CommUtil.null2Boolean(session.getAttribute("admin_login"));
		boolean seller_login = CommUtil.null2Boolean(session.getAttribute("seller_login"));
		// String targetUrl = CommUtil.getURL(request) + "/user/login.htm";
		// 此处设置注销登录时默认跳转的页面
		String targetUrl = CommUtil.getURL(request) + "/index.htm";
		if (admin_login) {
			targetUrl = CommUtil.getURL(request) + "/admin/index.htm";
		}
		if (seller_login) {
			targetUrl = CommUtil.getURL(request) + "/index.htm";
		}

		//
		String userName = CommUtil.null2String(session.getAttribute("userName"));
		List<Object> objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.size(); i++) {
			if (CommUtil.null2String(objs.get(i)).equals(userName)) {
				List<SessionInformation> ilist = this.sessionRegistry.getAllSessions(objs.get(i), true);
				for (int j = 0; j < ilist.size(); j++) {
					SessionInformation sif = ilist.get(j);
					// 以下踢出用户
					sif.expireNow();
					this.sessionRegistry.removeSessionInformation(sif.getSessionId());
				}
			}
		}
		//
		session.removeAttribute("admin_login");
		session.removeAttribute("seller_login");
		session.removeAttribute("user");
		session.removeAttribute("userName");
		session.removeAttribute("login");
		session.removeAttribute("role");
		session.removeAttribute("cart");
		// 移除会员ID信息
		session.removeAttribute("custId");
		// 移除CAS登录信息
		session.removeAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession(true)
				.removeAttribute("user");
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
			targetUrl = CommUtil.getURL(request) + "/wap/index.htm";
		}
		Map map = Json.fromJson(Map.class,
				session.getAttribute("weixin_bind") != null ? session.getAttribute("weixin_bind").toString() : "");
		if (map != null) {
			if (map.get("login") != null) {
				boolean login = CommUtil.null2Boolean(map.get("login").toString());
				if (login) {
					if (map.get("userName") != null && map.get("passwd") != null) {
						userName = map.get("userName").toString();
						String passwd = map.get("passwd").toString();
						targetUrl = CommUtil.getURL(request) + "/iskyshop_login.htm?username=" + CommUtil.encode(userName)
								+ "&password=" + Globals.THIRD_ACCOUNT_LOGIN + passwd + "&encode=true&login_role=user";
						if (!StringUtils.isNullOrEmpty(map.get("userId"))) {
							Long id = CommUtil.null2Long(map.get("userId"));
							User user = this.userService.getObjById(id);
							user.getRoles().clear();
							
							for (CouponInfo ci : user.getCouponinfos()) { // 用户拥有的优惠券
								this.couponInfoService.delete(ci.getId());
							}
							user.getCouponinfos().remove(user.getCouponinfos());
							for (Accessory acc : user.getFiles()) { // 用户附件
								if (acc.getAlbum() != null) {
									if (acc.getAlbum().getAlbum_cover() != null) {
										if (acc.getAlbum().getAlbum_cover().getId().equals(acc.getId())) {
											acc.getAlbum().setAlbum_cover(null);
											this.albumService.update(acc.getAlbum());
										}
									}
								}
								CommUtil.del_acc(request, acc);
								this.accessoryService.delete(acc.getId());
							}
							user.getFiles().removeAll(user.getFiles());
							user.getCouponinfos().remove(user.getCouponinfos()); // 用户的所有购物车
							for (GoodsCart cart : user.getGoodscarts()) {
								this.goodsCartService.delete(cart.getId());
							}
							// 充值记录
							Map params = new HashMap();
							params.put("uid", user.getId());
							List<PredepositCash> PredepositCash_list = this.redepositcashService
									.query("select obj from PredepositCash obj where obj.cash_user.id=:uid", params, -1, -1);
							for (PredepositCash pc : PredepositCash_list) {
								this.redepositcashService.delete(pc.getId());
							}
							// 删除积分订单
							params.clear();
							params.put("user_id", user.getId());
							List<IntegralGoodsOrder> integralGoodsOrders = this.integralGoodsOrderService.query(
									"select obj from IntegralGoodsOrder obj where obj.igo_user.id=:user_id", params, -1, -1);
							for (IntegralGoodsOrder integralGoodsOrder : integralGoodsOrders) {
								this.integralGoodsOrderService.delete(integralGoodsOrder.getId());
							}
							// 删除积分日志
							params.clear();
							params.put("user_id", user.getId());
							List<IntegralLog> integralLogs = this.integralLogService.query(
									"select obj from IntegralLog obj where obj.integral_user.id=:user_id", params, -1, -1);
							for (IntegralLog integralLog : integralLogs) {
								this.integralLogService.delete(integralLog.getId());
							}

							params.clear();
							params.put("uid", user.getId());
							List<GoldLog> GoldLog_list = this.goldlogService
									.query("select obj from GoldLog obj where obj.gl_user.id=:uid", params, -1, -1);
							for (GoldLog gl : GoldLog_list) {
								this.goldlogService.delete(gl.getId());
							}
							params.clear();
							params.put("uid", user.getId());
							List<StorePoint> storepoint_list = this.storepointService
									.query("select obj from StorePoint obj where obj.user.id=:uid", params, -1, -1);
							for (StorePoint sp : storepoint_list) {
								this.storepointService.delete(sp.getId());
							}
							params.clear();
							params.put("uid", user.getId()); // 商家广告
							List<Advert> adv_list = this.advertService
									.query("select obj from Advert obj where obj.ad_user.id=:uid", params, -1, -1);
							for (Advert ad : adv_list) {
								this.advertService.delete(ad.getId());
							}
							this.userService.delete(user.getId());
							// 自提点
							if (!StringUtils.isNullOrEmpty(user.getDelivery_id())) {
								this.deliveryAddressService.delete(user.getDelivery_id());
							}
							// 删除sns关注信息
							params.clear();
							params.put("fromUser", user.getId());
							params.put("toUser", user.getId());
							List<SnsAttention> list = this.snsAttentionService.query(
									"select obj from SnsAttention obj where obj.fromUser.id=:fromUser or obj.toUser.id=:toUser",
									params, -1, -1);
							for (SnsAttention sa : list) {
								this.snsAttentionService.delete(sa.getId());
							}
							// 删除用户日志
							params.clear();
							params.put("user_id", user.getId());
							List<SysLog> logs = this.syslogService
									.query("select obj from SysLog obj where obj.user_id=:user_id", params, -1, -1);
							for (SysLog log : logs) {
								this.syslogService.delete(log.getId());
							}
						}
					}
				}
			}
			session.removeAttribute("weixin_bind");
		}
		String casUrl = PropertyUtil.getProperty("CAS_SERVER_URL_PREFIX");
		response.sendRedirect(casUrl + "/logout?service=" + targetUrl);
		return;
	}

	
	/**
	 * cas改造后版本的登录错误处理流程：2016-11-04 Ryan Wu
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_error_new.htm")
	public ModelAndView login_error_new(HttpServletRequest request, HttpServletResponse response) {
		String url = (String)request.getSession().getAttribute("url");
		String errMsg = (String)request.getSession().getAttribute("errMsg");
		String role = (String)request.getSession().getAttribute("role");
		Object wapFlag = request.getSession().getAttribute("wap");
		
		request.getSession().removeAttribute("url");
		request.getSession().removeAttribute("errMsg");
		request.getSession().removeAttribute("role");
		request.getSession().removeAttribute("wap");
		
		String errPage = null;
		int viewType = 0;
		if("ADMIN".equals(role)) {
			errPage = "admin/blue/login_error.html";
		} else {
			viewType = 1;
			
			if(wapFlag == null) {
				errPage = "error.html";
			} else {
				errPage = "wap/error.html";
			}
		} 
		
		ModelAndView mv = new JModelAndView(errPage, configService.getSysConfig(), this.userConfigService.getUserConfig(), viewType, request, response); 
		mv.addObject("url", url);
		mv.addObject("op_title", errMsg);
		
		return mv;
	}
	
	@RequestMapping("/login_error.htm")
	public ModelAndView login_error(HttpServletRequest request, HttpServletResponse response) {
		String login_role = (String) request.getSession(true).getAttribute("login_role");
		ModelAndView mv = null;
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
			String targetUrl = CommUtil.getURL(request) + "/wap/index.htm";
			try {
				response.sendRedirect(targetUrl);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		String iskyshop_view_type = CommUtil.null2String(request.getSession(true).getAttribute("iskyshop_view_type"));
		if (!StringUtils.isNullOrEmpty(iskyshop_view_type)) {
			if ("weixin".equals(iskyshop_view_type)) {
				String store_id = CommUtil.null2String(request.getSession(true).getAttribute("store_id"));
				mv = new JModelAndView("weixin/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id);
			}
		} else {
			if (login_role == null) {
				login_role = "user";
			}
			if ("admin".equalsIgnoreCase(login_role)) {
				mv = new JModelAndView("admin/blue/login_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
			}
			if ("seller".equalsIgnoreCase(login_role)) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("url", CommUtil.getURL(request) + "/seller/login.htm");
			}
			if ("user".equalsIgnoreCase(login_role)) {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				// mv.addObject("url", CommUtil.getURL(request) +
				// "/user/login.htm");
				String casUrl = PropertyUtil.getProperty("CAS_SERVER_URL_PREFIX");
				StringBuffer url = request.getRequestURL();
				String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/")
						.toString();
				mv.addObject("url", casUrl + "/logout?service=" + tempContextUrl + request.getContextPath() + "/index.htm");
				// 移除CAS登录信息
				request.getSession().removeAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
			}
		}
		mv.addObject("op_title", "登录失败");
		return mv;
	}

	/**
	 * 管理页面
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商城后台管理", value = "/admin/index.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/index.htm")
	public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/manage.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "欢迎页面", value = "/admin/welcome.htm*", rtype = "admin", rname = "欢迎页面", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/welcome.htm")
	public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/welcome.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Properties props = System.getProperties();
		mv.addObject("os", props.getProperty("os.name"));
		mv.addObject("java_version", props.getProperty("java.version"));
		mv.addObject("shop_version", Globals.DEFAULT_SHOP_VERSION);
		mv.addObject("database_version", this.databaseTools.queryDatabaseVersion());
		mv.addObject("web_server_version", request.getSession(true).getServletContext().getServerInfo());
		List<StoreStat> stats = this.storeStatService.query("select obj from StoreStat obj order by obj.addTime desc", null,
				-1, -1);
		Map params = new HashMap();
		params.put("st_status", 0);
		List<SystemTip> sts = this.systemTipService.query(
				"select obj from SystemTip obj where obj.st_status=:st_status order by obj.st_level desc", params, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		mv.addObject("stat", stat);
		mv.addObject("sts", sts);
		return mv;
	}

	@SecurityMapping(title = "系统提醒页", value = "/admin/sys_tip_list.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_list.htm")
	public ModelAndView sys_tip_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/sys_tip_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SystemTipQueryObject qo = new SystemTipQueryObject(currentPage, mv, orderBy, orderType);
		qo.setOrderBy("st_status asc,obj.st_level desc,obj.addTime");
		qo.setOrderType("desc");
		IPageList pList = this.systemTipService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "系统提醒删除", value = "/admin/sys_tip_del.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_del.htm")
	public String sys_tip_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				SystemTip st = this.systemTipService.getObjById(CommUtil.null2Long(id));
				this.systemTipService.delete(Long.parseLong(id));
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "系统提醒处理", value = "/admin/sys_tip_do.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_do.htm")
	public String sys_tip_do(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				SystemTip st = this.systemTipService.getObjById(CommUtil.null2Long(id));
				st.setSt_status(1);
				this.systemTipService.save(st);
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "关于我们", value = "/admin/aboutus.htm*", rtype = "admin", rname = "关于我们", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/aboutus.htm")
	public ModelAndView aboutus(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/aboutus.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "站点设置", value = "/admin/set_site.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_site.htm")
	public ModelAndView site_set(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_site_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("jsessionid", request.getSession().getId());
		mv.addObject("vers", this.appNewlyVerionService.queryAllLatestVers());
		return mv;
	}

	@SecurityMapping(title = "上传设置", value = "/admin/set_image.htm*", rtype = "admin", rname = "上传设置", rcode = "admin_set_image", rgroup = "设置")
	@RequestMapping("/admin/set_image.htm")
	public ModelAndView set_image(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_image_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "验证是否存在ftp服务器", value = "/admin/verfresh_ftpserver.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/verfresh_ftpserver.htm")
	public void verfresh_ftpserver(HttpServletResponse response, String mobile) throws UnsupportedEncodingException {
		String code = "100"; // 100正确,-100没有用户ftp服务器，-200没有系统ftp服务器
		Map map = new HashMap();
		map.put("ftp_type", 0); // 用户服务器
		List<FTPServer> user_ftps = this.ftpService.query("select obj from FTPServer obj where obj.ftp_type=:ftp_type ", map,
				-1, -1);
		if (user_ftps.size() <= 0) {
			code = "-100";
		}
		map.clear();
		map.put("ftp_type", 1); // 系统服务器
		List<FTPServer> admin_ftps = this.ftpService.query("select obj from FTPServer obj where obj.ftp_type=:ftp_type", map,
				-1, -1);
		if (admin_ftps.size() <= 0) {
			code = "-200";
		} else {
			int temp = 0;
			for (FTPServer ftp : admin_ftps) {
				if (ftp.getFtp_system() == 1) {
					temp++;
				}
			}
			if (temp == 0) {
				code = "-250"; // 系统服务器没有当前使用的
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 接收上传上来的一个APP安装包
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 *            标识此上传上来的APP安装包的id，用于在request中提取此安装包
	 * @param fileName
	 *            上传上来的APP安装包的文件名
	 */
	@SecurityMapping(title = "保存App", value = "/admin/sys_app_save.htm*", rtype = "admin", display = false, rname = "保存APP", rcode = "admin_app_save", rgroup = "设置")
	@RequestMapping("/admin/sys_app_save.htm")
	public void saveNewlyApp(HttpServletRequest request, HttpServletResponse response, String fileName) {
		Map json_map = new HashMap();
		if (fileName == null || fileName.indexOf("_") <= 0 || fileName.lastIndexOf(".") + 4 != fileName.length()) {
			json_map.put("ret", false);
			json_map.put("msg", "文件(" + fileName + ")的命名不符合业务规则。命名规则：软件名称 + \"_\" + 渠道id + \".\" + 后缀");
		} else {

			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
					+ "cache";
			CommUtil.createFolder(saveFilePathName);

			try {
				CommUtil.saveFileToServer(request, "app", saveFilePathName, fileName, null);
				json_map.put("ret", true);
			} catch (IOException e) {
				logger.error(e);
				json_map.put("ret", false);
				json_map.put("msg", "保存文件" + fileName + "出错。" + e.getMessage());
			}
		}

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("text/plain");
		try {
			response.getWriter().print(Json.toJson(json_map));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "保存商城配置", value = "/admin/sys_config_save.htm*", rtype = "admin", display = false, rname = "保存商城配置", rcode = "admin_config_save", rgroup = "设置")
	@RequestMapping("/admin/sys_config_save.htm")
	public ModelAndView sys_config_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url,
			String op_title, String app_download, String android_seller_download, String ios_seller_download,
			String app_seller_download, String toUploadFiles, Integer updateFlag, Integer platformFlag, String verName,
			Integer verNumber, String verDesc) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if ("".equals(id)) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (!StringUtils.isNullOrEmpty(sysConfig.getAddress())) {
			String address = sysConfig.getAddress();
			if (address.indexOf("http://") < 0) {
				address = "http://" + address;
				sysConfig.setAddress(address);
			}
		}
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig().getWebsiteLogo() == null ? ""
					: this.configService.getSysConfig().getWebsiteLogo().getName();
			map = CommUtil.saveFileToServer(request, "websiteLogo", saveFilePathName, fileName, null);
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory logo = new Accessory();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map.get("fileSize")))));
					logo.setPath(this.ftpTools.systemUpload(logo.getName(), "/system"));
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setWebsiteLogo(logo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory logo = sysConfig.getWebsiteLogo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map.get("fileSize")))));
					logo.setPath(this.ftpTools.systemUpload(logo.getName(), "/system"));
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 默认商品图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "goodsImage", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getGoodsImage() == null ? ""
					: this.configService.getSysConfig().getGoodsImage().getName();
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setGoodsImage(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = sysConfig.getGoodsImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 默认店铺标识
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "storeImage", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getStoreImage() == null ? ""
					: this.configService.getSysConfig().getStoreImage().getName();
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setStoreImage(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = sysConfig.getStoreImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 默认会员图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "memberIcon", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getMemberIcon() == null ? ""
					: this.configService.getSysConfig().getMemberIcon().getName();
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setMemberIcon(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = sysConfig.getMemberIcon();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 平台登录Logo
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "admin_login_img", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getAdmin_login_logo() == null ? ""
					: this.configService.getSysConfig().getAdmin_login_logo().getName();
			if ("".equals(fileName)) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_login_logo(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = sysConfig.getAdmin_login_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 平台管理Logo
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "admin_manage_img", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getAdmin_manage_logo() == null ? ""
					: this.configService.getSysConfig().getAdmin_manage_logo().getName();
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_manage_logo(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = sysConfig.getAdmin_manage_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/system"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 平台上传ico，考虑系统效率，本图片不上传至ftp
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "website_ico",
					CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "system",
					"favicon.ico", null);
			String fileName = this.configService.getSysConfig().getWebsite_ico() == null ? ""
					: this.configService.getSysConfig().getWebsite_ico().getName();
			if ("".equals(fileName)) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setWebsite_ico(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getWebsite_ico();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		// 上传系统二维码中心Logo图片,考虑系统效率，本图片不上传至ftp，
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "qrLogo",
					CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "system", null, null);
			String fileName = this.configService.getSysConfig().getQr_logo() == null ? ""
					: this.configService.getSysConfig().getQr_logo().getName();
			if ("".equals(fileName)) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName((String) map.get("fileName"));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setQr_logo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getQr_logo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}

		if (!StringUtils.isNullOrEmpty(toUploadFiles)) {
			Date addTime = new Date();
			String appsDir = CommUtil.getServerRealPathFromRequest(request)
					+ this.configService.getSysConfig().getUploadFilePath() + File.separator + "cache"; // 已经在上传APP时创建了此目录
			for (String name : toUploadFiles.split(",")) {
				name = name.trim();
				if (!StringUtils.isNullOrEmpty(name)) {
					int idx = name.indexOf("_");
					if (idx > 0) {
						String appName = name.substring(0, idx);
						String channelId = name.substring(idx + 1, name.length() - 4);
						AppNewlyVersionInfo ver = new AppNewlyVersionInfo();
						ver.setAddTime(addTime);
						ver.setChannelId(channelId);
						ver.setPlatformFlag(platformFlag);
						ver.setUpdateFlag(updateFlag);
						ver.setVerDesc(verDesc);
						ver.setVerName(verName);
						ver.setVerNumber(verNumber);
						ver.setInstallUrl(this.ftpTools.systemUpload(name, "/apps") + "/" + name);
						this.appNewlyVerionService.save(ver);
						if ("smi".equalsIgnoreCase(channelId)) {
							sysConfig.setAndroid_download(ver.getInstallUrl());
						} else if ("ios".equalsIgnoreCase(channelId)) {
							sysConfig.setIos_download(ver.getInstallUrl());
						}
					}
				}
			}
		}

		sysConfig.setApp_download(CommUtil.null2Int(app_download));

		if (CommUtil.null2Int(app_download) == 1) { // 开启app下载生成下载链接二维码
			String destPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = CommUtil.getServerRealPathFromRequest(request)
						+ this.configService.getSysConfig().getQr_logo().getPath() + File.separator
						+ this.configService.getSysConfig().getQr_logo().getName();
			}
			String download_url = CommUtil.getURL(request) + "/app_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator + "app_dowload.png", true);
		}
		// 商家app
		sysConfig.setApp_seller_download(CommUtil.null2Int(app_seller_download));
		sysConfig.setAndroid_seller_download(android_seller_download);
		sysConfig.setIos_seller_download(ios_seller_download);
		if (CommUtil.null2Int(app_seller_download) == 1) { // 开启商家app下载生成下载链接二维码
			String destPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = CommUtil.getServerRealPathFromRequest(request)
						+ this.configService.getSysConfig().getQr_logo().getPath() + File.separator
						+ this.configService.getSysConfig().getQr_logo().getName();
			}
			String download_url = CommUtil.getURL(request) + "/app_seller_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator + "app_seller_download.png", true);
		}

		if (!StringUtils.isNullOrEmpty(sysConfig.getHotSearch())) {
			sysConfig.setHotSearch(sysConfig.getHotSearch().replaceAll("，", ",")); // 替换全角分隔号
		}
		if (!StringUtils.isNullOrEmpty(sysConfig.getKeywords())) {
			sysConfig.setKeywords(sysConfig.getKeywords().replaceAll("，", ","));
		}
		if ("".equals(id)) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		for (int i = 0; i < 4; i++) {
			try {
				map.clear();
				String fileName = "";
				if (sysConfig.getLogin_imgs().size() > i) {
					fileName = sysConfig.getLogin_imgs().get(i).getName();
				}
				map = CommUtil.saveFileToServer(request, "img" + i, saveFilePathName, fileName, null);
				if (StringUtils.isNullOrEmpty(fileName)) {
					if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
						Accessory img = new Accessory();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt((String) map.get("mime"));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						img.setPath(this.ftpTools.systemUpload(img.getName(), "/system"));
						img.setWidth((Integer) map.get("width"));
						img.setHeight((Integer) map.get("height"));
						img.setAddTime(new Date());
						img.setConfig(sysConfig);
						this.accessoryService.save(img);
					}
				} else {
					if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
						Accessory img = sysConfig.getLogin_imgs().get(i);
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt(CommUtil.null2String(map.get("mime")));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						img.setPath(this.ftpTools.systemUpload(img.getName(), "/system"));
						img.setWidth(CommUtil.null2Int(map.get("width")));
						img.setHeight(CommUtil.null2Int(map.get("height")));
						img.setConfig(sysConfig);
						this.accessoryService.update(img);
					}
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", op_title);
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "Email设置", value = "/admin/set_email.htm*", rtype = "admin", rname = "Email设置", rcode = "admin_set_email", rgroup = "设置")
	@RequestMapping("/admin/set_email.htm")
	public ModelAndView set_email(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_email_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "短信设置", value = "/admin/set_sms.htm*", rtype = "admin", rname = "短信设置", rcode = "admin_set_sms", rgroup = "设置")
	@RequestMapping("/admin/set_sms.htm")
	public ModelAndView set_sms(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_sms_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "SEO设置", value = "/admin/set_seo.htm*", rtype = "admin", rname = "SEO设置", rcode = "admin_set_seo", rgroup = "设置")
	@RequestMapping("/admin/set_seo.htm")
	public ModelAndView set_seo(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_seo_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置", value = "/admin/set_second_domain.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain.htm")
	public ModelAndView set_second_domain(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_second_domain.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置保存", value = "/admin/set_second_domain_save.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain_save.htm")
	public ModelAndView set_second_domain_save(HttpServletRequest request, HttpServletResponse response, String id,
			String domain_allow_count, String sys_domain, String second_domain_open) {
		String serverName = request.getServerName().toLowerCase();
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (Globals.SSO_SIGN) {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(CommUtil.null2Boolean(second_domain_open));
			if ("".equals(id)) {
				this.configService.save(config);
			} else {
				this.configService.update(config);
			}
			mv.addObject("op_title", "二级域名保存成功");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_second_domain.htm");
		} else {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(false);
			if (StringUtils.isNullOrEmpty(id)) {
				this.configService.save(config);
			} else {
				this.configService.update(config);
			}
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "当前网站无法开启二级域名");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_second_domain.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "QQ互联登录", value = "/admin/set_site_qq.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_site_qq.htm")
	public ModelAndView set_site_qq(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_second_domain.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}


	/**
	 * 登录页面
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/admin/login.htm")
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
		
		ModelAndView mv = new JModelAndView("admin/blue/login.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(request.getSession() != null) {
			request.getSession().removeAttribute("verify_code"); 
		}
		return mv;
	}

	@RequestMapping("/success.htm")
	public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", request.getSession(true).getAttribute("op_title"));
		mv.addObject("url", request.getSession(true).getAttribute("url"));
		request.getSession(true).removeAttribute("op_title");
		request.getSession(true).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认错误页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/error.htm")
	public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && "ADMIN".equalsIgnoreCase(user.getUserRole())) {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);

		}
		mv.addObject("op_title", request.getSession(true).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(true).getAttribute("url"));
		mv.addObject("url", request.getSession(true).getAttribute("url"));
		request.getSession(true).removeAttribute("op_title");
		request.getSession(true).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认异常出现
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/exception.htm")
	public ModelAndView exception(HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && "ADMIN".equalsIgnoreCase(user.getUserRole())) {
			mv = new JModelAndView("admin/blue/exception.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		} else {
			mv.addObject("op_title", "系统出现异常");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 超级后台默认无权限页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/authority.htm")
	public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/authority.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean domain_error = CommUtil.null2Boolean(request.getSession(true).getAttribute("domain_error"));
		if (domain_error) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "域名绑定错误，请与http://www.iskyshop.com联系");
		}
		return mv;
	}

	/**
	 * 语言验证码处理
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/voice.htm")
	public ModelAndView voice(HttpServletRequest request, HttpServletResponse response) {
		return new JModelAndView("include/flash/soundPlayer.swf", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}

	/**
	 * flash获取验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getCode.htm")
	public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code=" + (String) session.getAttribute("verify_code"));
	}

	/**
	 * 初始化系统相关图片，如商品默认图等，管理员修改后可以选择恢复默认
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws IOException
	 */
	@SecurityMapping(title = "初始化系统默认图片", value = "/admin/restore_img.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/restore_img.htm")
	public void restore_img(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
		SysConfig config = this.configService.getSysConfig();
		Map map = new HashMap();
		if ("member".equals(type)) { // 恢复系统默认会员头像
			Accessory acc = config.getMemberIcon();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getMemberIcon();
			}
			acc.setPath("/resources/style/common/images");
			acc.setName("member.jpg");
			config.setMemberIcon(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/member.jpg");
		}
		if ("goods".equals(type)) { // 恢复系统默认商品头像
			Accessory acc = config.getGoodsImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getGoodsImage();
			}
			acc.setPath("/resources/style/common/images");
			acc.setName("good.jpg");
			config.setGoodsImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/good.jpg");
		}
		if ("store".equals(type)) { // 恢复系统默认店铺头像
			Accessory acc = config.getStoreImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getStoreImage();
			}
			acc.setPath("/resources/style/common/images");
			acc.setName("store.jpg");
			config.setStoreImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/store.jpg");
		}
		if ("admin_login_img".equals(type)) { // 恢复平台管理登录页左上角Logo
			Accessory acc = config.getAdmin_login_logo();
			config.setAdmin_login_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path", CommUtil.getURL(request) + "/resources/style/system/manage/blue/images/login/login_logo.png");
		}
		if ("admin_manage_img".equals(type)) { // 恢复平台管理中心左上角的Logo
			Accessory acc = config.getAdmin_manage_logo();
			config.setAdmin_manage_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path", CommUtil.getURL(request) + "/resources/style/system/manage/blue/images/logo.png");
		}
		map.put("type", type);
		HttpSession session = request.getSession(true);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print(Json.toJson(map, JsonFormat.compact()));
	}

	/**
	 * 系统编辑器图片上传
	 * 
	 * @param request
	 * @param response
	 * @throws ClassNotFoundException
	 */
	@RequestMapping("/upload.htm")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException {
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request)
				+ this.configService.getSysConfig().getUploadFilePath() + File.separator + "cache";
		String webPath = CommUtil.getURL(request);
		
		JSONObject obj = new JSONObject();
		try {
			Map map = CommUtil.saveFileToServer(request, "imgFile", saveFilePathName, null, null);
			String url = this.ftpTools.systemUpload(CommUtil.null2String(map.get("fileName")), "/common/")
					+ CommUtil.null2String(map.get("fileName"));
			obj.put("error", 0);
			obj.put("url", url);
		} catch (IOException e) {
			obj.put("error", 1);
			obj.put("message", e.getMessage());
			logger.error(e);
		}
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
		} catch (IOException e) {
			logger.error(e);
		}

	}

	@RequestMapping("/js.htm")
	public ModelAndView js(HttpServletRequest request, HttpServletResponse response, String js) {
		ModelAndView mv = new JModelAndView("resources/js/" + js + ".js", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}

	@RequestMapping("/admin/test_mail.htm")
	public void test_email(HttpServletResponse response, String email) {
		String subject = this.configService.getSysConfig().getTitle() + "测试邮件";
		boolean ret = this.msgTools.sendEmail(email, subject, subject);
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

	@RequestMapping("/admin/test_sms.htm")
	public void test_sms(HttpServletResponse response, String mobile) throws UnsupportedEncodingException {
		String content = this.configService.getSysConfig().getTitle() + "亲,如果您收到短信，说明发送成功！";
		boolean ret = false;
		try {
			ret = this.msgTools.sendSMS(mobile, content);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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

	/**
	 * 商城平台样式设置，默认样式为blue
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "websiteCss设置", value = "/admin/set_websiteCss.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_websiteCss.htm")
	public void set_websiteCss(HttpServletRequest request, HttpServletResponse response, String webcss) {
		SysConfig obj = this.configService.getSysConfig();
		if (!"blue".equals(webcss) && !"black".equals(webcss)) {
			webcss = "blue";
		}
		obj.setWebsiteCss(webcss);
		this.configService.update(obj);
	}

}
