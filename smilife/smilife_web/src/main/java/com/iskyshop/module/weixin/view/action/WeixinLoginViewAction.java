package com.iskyshop.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.common.EPwdResetType;
import com.smilife.bcp.dto.common.EPwdType;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * <p>
 * Title: WapLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端登录请求管理类
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
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class WeixinLoginViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IVerifyCodeService verifyCodeService;
	private static final String REGEX1 = "(.*管理员.*)";
	private static final String REGEX2 = "(.*admin.*)";

	@Autowired
	private UserManageConnector manageConnector;

	/**
	 * 手机客户端用户登录
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/wap/iskyshop_user_login.htm")
	public void mobile_login(HttpServletRequest request, HttpServletResponse response, String userName, String password) {
		String code = "-300";// 100,登陆成功,-100账号不存在，-200,密码不正确，-300登录失败
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify) { // 头文件验证成功
			String user_id = "";
			String user_name = "";
			String login_token = "";
			if (!StringUtils.isNullOrEmpty(userName) && !StringUtils.isNullOrEmpty(password)) {
				password = Md5Encrypt.md5(password).toLowerCase();
				Map map = new HashMap();
				map.put("userName", userName);
				List<User> users = this.userService.query(
						"select obj from User obj where obj.userName=:userName order by addTime asc", map, -1, -1);
				if (users.size() > 0) {
					for (User u : users) {
						if (!u.getPassword().equals(password)) {
							code = "-200";
						} else {
							if ("admin".equalsIgnoreCase(u.getUserRole())) {
								code = "-100";
							} else {
								user_id = CommUtil.null2String(u.getId());
								user_name = u.getUserName();
								code = "100";
								login_token = CommUtil.randomString(12) + user_id;
								u.setApp_login_token(login_token.toLowerCase());
								this.userService.update(u);
							}
						}
					}
				} else {
					code = "-100";
				}
			}
			if ("100".equals(code)) {

				json_map.put("user_id", user_id.toString());
				json_map.put("userName", user_name);
				json_map.put("token", login_token);
			}
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			logger.error(e);
		}
	}


	/**
	 * 移动端用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/wap/register.htm")
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect(PropertyUtil.getProperty("USER_REGISTER_WAP_URL"));
		} catch (IOException e) {
			logger.error("重定向出错：", e);
		}
		return null;
	}

	/**
	 * 移动端注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/wap/register_finish.htm")
	public String register_finish(HttpServletRequest request, HttpServletResponse response, String userName, String password)
			throws HttpException, IOException {
		boolean reg = true;
		// 进一步控制用户名不能重复
		Map params = new HashMap();
		/*
		 * params.put("userName", userName); List<User> users =
		 * this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1); if (users != null
		 * && users.size() > 0) { reg = false; }
		 */
		// 通过bcp调用crm进行用户唯一性校验
		ResultDTO resultDTO = manageConnector.isExist(userName);
		if (null == resultDTO || EInterfaceState.FAIL.getState().equals(resultDTO.getResult())) {
			reg = false;
		}

		if (reg) {
			String custId = manageConnector.register(userName, Md5Encrypt.md5(password).toLowerCase());
			if (null != custId) {
				User user = new User();
				user.setCustId(custId);
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				//user.setAvailableBalance(BigDecimal.valueOf(0));//余额都在CRM端保存和增减，故注释掉
				user.setFreezeBlance(BigDecimal.valueOf(0));
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
				user.getRoles().addAll(roles);
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
				return "redirect:iskyshop_login.htm?username=" + CommUtil.encode(userName) + "&password=" + password
						+ "&encode=true";

			} else {
				return "redirect:register.htm";
			}
		} else {
			return "redirect:register.htm";
		}
	}

	/**
	 * 手机客户端查看注册协议
	 * 
	 */
	@RequestMapping("/wap/register_doc.htm")
	public ModelAndView mobile_register_doc(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/register_doc.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty(null, "mark", "reg_agree");
		mv.addObject("doc", doc);
		return mv;
	}

	/**
	 * 找回密码第一步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/forget1.htm")
	public ModelAndView forget1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/forget1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
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
	@RequestMapping("/wap/forget2.htm")
	public ModelAndView forget2(HttpServletRequest request, HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView("wap/forget2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		} else {
			Map params = new HashMap();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile =:mobile",
					params, -1, -1);
			if (users.size() > 0) {
				User user = users.get(0);
				if (!StringUtils.isNullOrEmpty(user.getEmail()) || !StringUtils.isNullOrEmpty(user.getMobile())) {
					mv.addObject("user", user);
				} else {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/forget1.htm");
				}

			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "不存在该用户");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/forget1.htm");
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
	@RequestMapping("/wap/forget3.htm")
	public ModelAndView forget3(HttpServletRequest request, HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new JModelAndView("wap/forget3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ("email".equalsIgnoreCase(accept_type)) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "email", email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64).toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(true).setAttribute("verify_session", verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/forget2.htm?userName=" + userName);
			}
		}
		if ("mobile".equalsIgnoreCase(accept_type)) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null, "mobile", mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request) + "/wap/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64).toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(true).setAttribute("verify_session", verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/forget2.htm?userName=" + userName);
			}
		}
		return mv;
	}

	@RequestMapping("/wap/forget4.htm")
	public ModelAndView forget4(HttpServletRequest request, HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new JModelAndView("wap/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(true).getAttribute("verify_session"));
		if (!StringUtils.isNullOrEmpty(verify_session1) && verify_session1.equals(verify_session)) {
			User user = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
			// TODO 会员密码修改
			ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), null, Md5Encrypt.md5(password).toLowerCase(),
					EPwdResetType.FIND.getState(), EPwdType.LOGIN_PWD.getState());
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				// 商城数据库密码定死为123qwe了，所以需要去掉修改数据库的处理
				/*user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				this.userService.update(user);*/
				request.getSession(true).removeAttribute("verify_session");
				mv.addObject("op_title", "密码修改成功，请使用新密码登录");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/login.htm");
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", resultDTO.getMsg());
				mv.addObject("url", CommUtil.getURL(request) + "/wap/forget1.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/forget1.htm");
		}
		return mv;
	}

}
