package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IFTPServerService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.module.app.domain.QRLogin;
import com.iskyshop.module.app.service.IQRLoginService;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.ImageViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.common.EPwdResetType;
import com.smilife.bcp.dto.common.EPwdType;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * <p>
 * Title: LoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 用户登录、注册管理控制器，用来管理用户登录、注册、密码找回
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
public class LoginViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IVerifyCodeService verifyCodeService;
	@Autowired
	private IQRLoginService qRLoginService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IFTPServerService ftpService;
	private static final String REGEX1 = "(.*管理员.*)";
	private static final String REGEX2 = "(.*admin.*)";

	@Autowired
	private UserManageConnector manageConnector;


	/**
	 * 用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/register.htm")
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect(PropertyUtil.getProperty("USER_REGISTER_WEB_URL"));
		} catch (IOException e) {
			logger.error("重定向错误", e);
		}
		return null;
	}

	/**
	 * 企业用户注册，打开企业注册入口页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/company/register.htm")
	public ModelAndView company_register(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("company_register.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(true).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty(null, "mark", "reg_agree");
		mv.addObject("doc", doc);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * 注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 * @throws InterruptedException
	 */
	//@RequestMapping("/register_finish.htm") //注册模块已转移到CRM端
	public String register_finish(HttpServletRequest request, HttpServletResponse response, String userName,
			String password, String email, String code, String user_type) throws HttpException, IOException,
			InterruptedException {
		try {
			boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
			if (code != null && !"".equals(code)) {
				code = CommUtil.filterHTML(code);// 过滤验证码
			}
			if (this.configService.getSysConfig().isSecurityCodeRegister()) {
				if (!request.getSession(true).getAttribute("verify_code").equals(code)) {
					reg = false;
				}
			}
			// 禁止用户注册带有 管理员 admin 等字样用户名
			if (userName.matches(REGEX1) || userName.toLowerCase().matches(REGEX2)) {
				reg = false;
			}
			if (reg) {
				String custId = manageConnector.register(userName, Md5Encrypt.md5(password).toLowerCase());
				if (null != custId) {
					User user = new User();
					user.setCustId(custId);
					user.setUserName(userName);
					user.setMobile(userName);
					user.setUserRole("BUYER");
					user.setAddTime(new Date());
					user.setEmail(email);
					//user.setAvailableBalance(BigDecimal.valueOf(0));//余额都在CRM端保存和增减，故注释掉
					user.setFreezeBlance(BigDecimal.valueOf(0));
					if (!StringUtils.isNullOrEmpty(user_type)) {
						user.setUser_type(CommUtil.null2Int(user_type));
						user.setContact_user(request.getParameter("contact_user"));
						user.setDepartment(request.getParameter("department"));
						user.setTelephone(request.getParameter("telephone"));
						user.setMobile(request.getParameter("mobile"));
						user.setCompany_name(request.getParameter("company_name"));
						Area area = this.areaService.getObjById(CommUtil.null2Long(request.getParameter("area_id")));
						user.setCompany_address(area.getParent().getParent().getAreaName() + area.getParent().getAreaName()
								+ area.getAreaName() + " " + request.getParameter("company_address"));
						if (request.getParameter("company_purpose") != null
								&& !"".equals(request.getParameter("company_purpose"))) {
							user.setCompany_purpose(request.getParameter("company_purpose").substring(0,
									request.getParameter("company_purpose").length() - 1));
						}
						user.setCompany_url(request.getParameter("company_url"));
						user.setCompany_person_num(request.getParameter("company_person_num"));
						user.setCompany_trade(request.getParameter("company_trade"));
						user.setCompany_nature(request.getParameter("company_nature"));
					}
					user.setPassword(Md5Encrypt.md5(password).toLowerCase());
					Map params = new HashMap();
					params.put("type", "BUYER");
					List<Role> roles = this.roleService.query("select new Role(id) from Role obj where obj.type=:type",
							params, -1, -1);
					user.getRoles().addAll(roles);
					// 为用户配置在用户数量限制内的FTP，如果没有则选择用户数量最少的FTP
					Map ftp_map = new HashMap();
					ftp_map.put("ftp_type", 0);
					List<FTPServer> FtpServers = this.ftpService
							.query("select obj from FTPServer obj where obj.ftp_users.size<obj.ftp_amount and obj.ftp_type=:ftp_type",
									ftp_map, -1, -1);
					if (FtpServers.size() > 0) {
						user.setFtp(FtpServers.get(0));
					} else {
						ftp_map.clear();
						ftp_map.put("ftp_type", 0);
						List<FTPServer> FtpServers2 = this.ftpService
								.query("select obj from FTPServer obj where obj.ftp_type=:ftp_type order by obj.ftp_users.size asc",
										ftp_map, -1, -1);
						if (FtpServers2.size() > 0) {
							user.setFtp(FtpServers2.get(0));
						}
					}
					if (this.configService.getSysConfig().isIntegral()) {
						user.setIntegral(this.configService.getSysConfig().getMemberRegister());
						if(!this.userService.save(user)) {
							user = this.userService.getObjByProperty(null, "custId", user.getCustId());
						}
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister() + "分");
						log.setIntegral(this.configService.getSysConfig().getMemberRegister());
						log.setIntegral_user(user);
						log.setType("reg");
						this.integralLogService.save(log);
					} else {
						if(!this.userService.save(user)) {
							user = this.userService.getObjByProperty(null, "custId", user.getCustId());
						}
					}
					// 创建用户默认相册
					Album album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_default(true);
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(-10000);
					album.setUser(user);
					this.albumService.save(album);
					request.getSession(true).removeAttribute("verify_code");
					return "redirect:iskyshop_login.htm?username=" + CommUtil.encode(userName) + "&password=" + password
							+ "&encode=true";
				} else {
					return "redirect:register.htm";
				}
			} else {
				return "redirect:register.htm";
			}
		} catch (Exception e) {
			logger.error(e);
			return "redirect:index.htm";
		}

	}

	/**
	 * springsecurity登录成功后跳转到该页面，如有登录相关处理可以在该方法中完成
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_login_success.htm")
	public String user_login_success(HttpServletRequest request, HttpServletResponse response) {
		String url = CommUtil.getURL(request) + "/index.htm";
		HttpSession session = request.getSession(true);
		if (!StringUtils.isNullOrEmpty(session.getAttribute("refererUrl"))) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		String bind = CommUtil.null2String(request.getSession(true).getAttribute("bind"));
		if (!"".equals(bind)) {
			return "redirect:out_login_success.htm";
		}
		return "redirect:" + url;
	}

	/**
	 * 外部系统登录成功提示页，系统默认支持QQ登录、新浪微博账号登录，使用第三方账号登录后，要求输入一个本系统的用户名和密码进行绑定， 绑定成功后就以使用QQ、新浪微博或者本系统账号登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/out_login_success.htm")
	public ModelAndView out_login_success(HttpServletRequest request, HttpServletResponse response) {
		String bind = CommUtil.null2String(request.getSession(true).getAttribute("bind"));
		ModelAndView mv = new JModelAndView(bind + "_login_bind.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		mv.addObject("user", user);
		request.getSession(true).removeAttribute("bind");
		return mv;
	}

	/**
	 * 弹窗登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/user_dialog_login.htm")
	public void user_dialog_login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + "/buyer/index.htm");
	}

	/**
	 * 找回密码第一步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget1.htm")
	public ModelAndView forget1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("forget1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 找回密码第二步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget2.htm")
	public ModelAndView forget2(HttpServletRequest request, HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView("forget2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile =:mobile",
					params, -1, -1);
			if (users.size() > 0) {
				User user = users.get(0);
				if (!"".equals(CommUtil.null2String(user.getEmail())) || !"".equals(CommUtil.null2String(user.getMobile()))) {
					mv.addObject("user", user);
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
				}

			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "不存在该用户");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping("/forget3.htm")
	public ModelAndView forget3(HttpServletRequest request, HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new JModelAndView("forget3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ("email".equals(accept_type)) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "email", email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request) + "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64).toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(true).setAttribute("verify_session", verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request) + "/forget2.htm?userName=" + userName);
			}
		}
		if ("mobile".equals(accept_type)) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "mobile", mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request) + "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64).toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(true).setAttribute("verify_session", verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request) + "/forget2.htm?userName=" + userName);
			}
		}
		return mv;
	}

	@RequestMapping("/forget4.htm")
	public ModelAndView forget4(HttpServletRequest request, HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new JModelAndView("forget4.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(true).getAttribute("verify_session"));
		if (!"".equals(verify_session1) && verify_session1.equals(verify_session)) {
			// TODO 会员密码修改
			User user = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
			ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), null, Md5Encrypt.md5(password).toLowerCase(),
					EPwdResetType.FIND.getState(), EPwdType.LOGIN_PWD.getState());
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				// 商城数据库密码定死为123qwe了，所以需要去掉修改数据库的处理
				/*user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				this.userService.update(user);*/
				request.getSession(true).removeAttribute("verify_session");
				mv.addObject("op_title", "密码修改成功，请使用新密码登录");
				mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", resultDTO.getResult());
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param code_name
	 * @throws UnsupportedEncodingException
	 */

	@RequestMapping("/reg_code_get.htm")
	public void reg_code_get(HttpServletRequest request, HttpServletResponse response, String accept_type, String email,
			String mobile, String userName) throws UnsupportedEncodingException {
		int ret = 0;// 0为发送失败，1为发送成功，-1为邮件发送失败，-2为手机短信发送失败
		if ("email".equals(accept_type)) {
			if (this.configService.getSysConfig().isEmailEnable()) {
				String subject = this.configService.getSysConfig().getWebsiteName() + " 邮件验证码";
				String code = CommUtil.randomString(4).toUpperCase();
				VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "email", email);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "您的邮件验证码为:" + code + ",验证码有效时间为30分钟！";
				boolean ret1 = this.msgTools.sendEmail(email, subject, content);
				if (ret1) {
					ret = 1;
				}
			} else {
				ret = -1;
			}
		}
		if ("mobile".equals(accept_type)) {
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String code = CommUtil.randomString(4);
				VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "mobile", mobile);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "您的邮件验证码为:" + code + ",验证码有效时间为30分钟！【" + this.configService.getSysConfig().getWebsiteName()
						+ "】";
				boolean ret1 = false;
				try {
					ret1 = this.msgTools.sendSMS(mobile, content);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				if (ret1) {
					ret = 1;
				}

			} else {
				ret = -2;
			}
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
	 * 通过邮件发送新密码来找回密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/find_pws.htm")
	public ModelAndView find_pws(HttpServletRequest request, HttpServletResponse response, String userName, String email,
			String code) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(true);
		String verify_code = (String) session.getAttribute("verify_code");
		if (code.toUpperCase().equals(verify_code)) {
			User user = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
			if (user.getEmail().equals(email.trim())) {
				String pws = CommUtil.randomString(6).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle() + "密码找回邮件";
				String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为：" + pws;
				boolean ret = this.msgTools.sendEmail(email, subject, content);
				if (ret) {
					user.setPassword(Md5Encrypt.md5(pws));
					this.userService.update(user);
					mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>" + email + "</font>，请查收后重新登录");
					mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
					mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "用户名、邮箱不匹配");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "验证码不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

	/**
	 * 获得登录二维码，使用app扫描二维码可以完成在线pc端自动登录功能,为提高系统效率，二维码图片不上传至ftp服务器
	 * 
	 * @param request
	 * @param response
	 * @param accept_type
	 * @param email
	 * @param mobile
	 * @param userName
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/qr_login_img.htm")
	public void qr_login_img(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String qr_session_id = CommUtil.null2String(request.getSession().getAttribute("qr_session_id"));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		Map<String, String> map = new HashMap<String, String>();
		String qr_img_url = "";
		if ("".equals(qr_session_id)) {
			qr_session_id = this.generic_qr(request, qr_session_id);
			qr_img_url = CommUtil.getURL(request) + "/" + uploadFilePath + "/qr_login/" + "qr_" + qr_session_id + ".png";
		} else {
			String logo = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
					+ File.separator + "qr_login" + File.separator + "qr_" + qr_session_id + ".png";
			if (!CommUtil.fileExist(logo)) {
				this.generic_qr(request, qr_session_id);
			}
			qr_img_url = CommUtil.getURL(request) + "/" + uploadFilePath + "/qr_login/" + "qr_" + qr_session_id + ".png";
		}
		map.put("qr_img_url", qr_img_url);
		map.put("qr_session_id", qr_session_id);
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

	@RequestMapping("/qr_login.htm")
	public void qr_login(HttpServletRequest request, HttpServletResponse response, String qr_session_id)
			throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("qr_session_id", qr_session_id);
		List<QRLogin> qrlist = this.qRLoginService.query(
				"select obj from QRLogin obj where obj.qr_session_id=:qr_session_id", params, -1, -1);
		Map map = new HashMap();
		if (qrlist.size() > 0) {
			QRLogin qrlogin = qrlist.get(0);
			map.put("ret", "true");
			map.put("user_id", qrlogin.getUser_id());
			this.qRLoginService.delete(qrlogin.getId());
			String qr_log_mark = CommUtil.randomString(16);
			map.put("qr_log_mark", qr_log_mark);
			HttpSession session = request.getSession(true);
			session.setAttribute("qr_log_mark", qr_log_mark);
		}
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

	@RequestMapping("/qr_login_confirm.htm")
	public String qr_login_confirm(HttpServletRequest request, HttpServletResponse response, String user_id,
			String qr_log_mark) throws UnsupportedEncodingException {
		HttpSession session = request.getSession(true);
		String session_qr_log_mark = session.getAttribute("qr_log_mark").toString();
		String url = "";
		if (qr_log_mark != null && qr_log_mark.equals(session_qr_log_mark)) {
			User user = this.userService.getObjById(CommUtil.null2Long(user_id));
			request.getSession(true).removeAttribute("verify_code");// 清空验证码信息
			url = "redirect:" + CommUtil.getURL(request) + "/iskyshop_login.htm?username="
					+ CommUtil.encode(user.getUsername()) + "&password=" + Globals.THIRD_ACCOUNT_LOGIN + user.getPassword();

		}
		return url;
	}

	/**
	 * 生成登录二维码，封装一个随机信息到二维码中，同时保存到系统session中
	 * 
	 * @param request
	 * @param qr_session_id
	 * @return
	 */
	private String generic_qr(HttpServletRequest request, String qr_session_id) {
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
				+ File.separator + "qr_login";
		CommUtil.createFolder(path);
		// 生成登录地址二维码
		String rand_str = "";
		if (!"".equals(CommUtil.null2String(qr_session_id))) {
			rand_str = qr_session_id;
		} else {
			rand_str = UUID.randomUUID().toString();
		}
		request.getSession().setAttribute("qr_session_id", rand_str);
		String login_url = CommUtil.getURL(request) + "/iskyshop_login.htm?qr_session_id=" + rand_str;
		request.getSession().setAttribute("qr_session_id", rand_str);
		String logoPath = "";
		if (this.configService.getSysConfig().getQr_logo() != null) {
			logoPath = CommUtil.getServerRealPathFromRequest(request)
					+ this.configService.getSysConfig().getQr_logo().getPath() + File.separator
					+ this.configService.getSysConfig().getQr_logo().getName();
		}
		QRCodeUtil.encode(login_url, logoPath, path + "/qr_" + rand_str + ".png", true);
		return rand_str;
	}
}
