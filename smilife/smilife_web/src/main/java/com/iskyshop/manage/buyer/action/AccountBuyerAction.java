package com.iskyshop.manage.buyer.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.msg.MsgTools;
import com.mysql.jdbc.StringUtils;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.common.EPwdResetType;
import com.smilife.bcp.dto.common.EPwdType;
import com.smilife.bcp.dto.request.CustUpdateReq;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;

/**
 * “我的账户”管理控制器
 */
@Controller
public class AccountBuyerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private FTPServerTools ftpServerTools;
	/** 默认的头像文件扩展名 */
	private static final String DEFAULT_AVATAR_FILE_EXT = ".jpg";
	/** 解码器 */
	@SuppressWarnings("restriction")
	private static BASE64Decoder decoder = new BASE64Decoder();
	/** 上传成功 */
	public static final String OPERATE_RESULT_CODE_SUCCESS = "200";
	/** 上传失败 */
	public static final String OPERATE_RESULT_CODE_FAIL = "400";

	@Autowired
	private UserManageConnector manageConnector;

	@SecurityMapping(title = "个人信息导航", value = "/buyer/account_nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_nav.htm")
	public ModelAndView account_nav(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_nav.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "个人信息", value = "/buyer/account.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account.htm")
	public ModelAndView account(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "个人信息保存", value = "/buyer/account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_save.htm")
	public ModelAndView account_save(HttpServletRequest request, HttpServletResponse response, String trueName, Long sex, String card, String birthday, Integer month_income, String area_id,
			String address) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		CustUpdateReq custUpdateReq = new CustUpdateReq();
		
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user.setTrueName(trueName);
		custUpdateReq.setCustName(trueName);
		
		if(sex != 1 && sex != 0) {
			sex = -1l;
		} 
		user.setSex(sex.intValue());
		if(sex == 0) {//女
			custUpdateReq.setCustSex("2");
		} else {
			custUpdateReq.setCustSex(sex.toString());
		}
		
		user.setCard(card);
		custUpdateReq.setCustCertNbr(card);	
		
		SimpleDateFormat format = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_SHORT);
		try {
			Date date = format.parse(birthday);
			user.setBirthday(date);
			String year = birthday.split("-")[0];
			Calendar calendar = new GregorianCalendar();
			user.setYears(calendar.get(Calendar.YEAR) - CommUtil.null2Int(year));
		} catch (ParseException e) {
			logger.error("转化用户的生日失败", e);			
		}
		
		if(user.getBirthday() != null) {
			custUpdateReq.setCustBirthdate(format.format(user.getBirthday()));//保证不会错误清空CRM端用户的生日字段的值
		}
		
		user.setMonth_income(month_income);
		
		if (!StringUtils.isNullOrEmpty(area_id)) {
			Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
			if(area != null && area.getLevel() == 2)
			user.setArea(area);
		} else {
			user.setArea(null);
		}
		
		user.setAddress(address);
		custUpdateReq.setCustContactAddr(address);	
		
		custUpdateReq.setCustId(user.getCustId());
		
		ResultDTO resultDTO = manageConnector.updateCust(custUpdateReq);
		if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
			this.userService.update(user);
			mv.addObject("op_title", "个人信息修改成功");
		} else {
			mv.addObject("op_title", "修改个人信息失败：" + resultDTO.getMsg());
		}
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
		return mv;
	}

	@SecurityMapping(title = "登录密码修改", value = "/buyer/account_password.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_password.htm")
	public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_password.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "密码修改保存", value = "/buyer/account_password_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_password_save.htm")
	public ModelAndView account_password_save(HttpServletRequest request, HttpServletResponse response, String old_password,
			String new_password) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), Md5Encrypt.md5(old_password.trim()).toLowerCase(),
				Md5Encrypt.md5(new_password.trim()).toLowerCase(), EPwdResetType.MODIFY.getState(),
				EPwdType.LOGIN_PWD.getState());
		if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
			mv.addObject("op_title", "登录密码修改成功");
			try {
				String content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName() + "用户您好，您于"
						+ CommUtil.formatLongDate(new Date()) + "修改登录密码成功，新密码为：" + new_password.trim() + ",请妥善保管。["
						+ this.configService.getSysConfig().getTitle() + "]";
				if(!this.msgTools.sendSMS(user.getMobile(), content)) {
					logger.error("向买家(custId=" + user.getCustId() + ")发送修改登录密码的短信失败");
				}
			} catch (Exception e) {
				logger.error("发送通知短信失败", e);
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "修改密码失败：" + resultDTO.getMsg());
		}
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_password.htm");
		return mv;
	}

	@SecurityMapping(title = "支付密码修改", value = "/buyer/account_paypwd.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_paypwd.htm")
	public ModelAndView account_paypwd(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_paypwd.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "支付密码修改保存", value = "/buyer/account_paypwd_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_paypwd_save.htm")
	public ModelAndView account_paypwd_save(HttpServletRequest request, HttpServletResponse response, String new_password,
			String mobile_verify_code) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null, "mobile", user.getMobile());
		if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
			this.mobileverifycodeService.delete(mvc.getId());
			ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), null, Md5Encrypt.md5(new_password)
					.toLowerCase(), EPwdResetType.RESET.getState(), EPwdType.PAY_PWD.getState());
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				mv.addObject("op_title", "支付密码修改成功");
				try {
					String content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName() + "用户您好，您于"
							+ CommUtil.formatLongDate(new Date()) + "修改您的余额支付密码成功，新密码为：" + new_password + ",请妥善保管。["
							+ this.configService.getSysConfig().getTitle() + "]";
					if(!this.msgTools.sendSMS(user.getMobile(), content)) {
						logger.error("向买家(custId=" + user.getCustId() + ")发送修改余额支付密码的通知短信失败");
					}
				} catch (Exception e) {
					logger.error("发送通知短信失败", e);
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", resultDTO.getMsg());
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "验证码错误，支付密码修改失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_paypwd.htm");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_paypwd.htm");
		return mv;
	}

	@SecurityMapping(title = "图像修改", value = "/buyer/account_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_avatar.htm")
	public ModelAndView account_avatar(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_avatar.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "图像上传", value = "/buyer/upload_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/upload_avatar.htm")
	public void upload_avatar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
					+ File.separator + "cache";
			// <2>.自定义参数：可用于传递用户Id、用户标识之类的，以区分不同的用户
			String customParams = CommUtil.null2String(request.getParameter("custom_params"));
			logger.debug("custom_params = " + customParams);
			// <3>. 保存文件
			// ---文件类型
			String imageType = CommUtil.null2String(request.getParameter("image_type"));
			if (StringUtils.isNullOrEmpty(imageType)) {
				imageType = DEFAULT_AVATAR_FILE_EXT;
			}
			// 大头像内容
			String bigAvatarContent = CommUtil.null2String(request.getParameter("big_avatar"));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			String bigAvatarName = SecurityUserHolder.getCurrentUser().getId() + "_big";
			// @@@保存大头像
			saveImage(saveFilePathName, imageType, bigAvatarContent, bigAvatarName);
			Accessory photo = new Accessory();
			if (user.getPhoto() != null) {
				photo = user.getPhoto();
			} else {
				photo.setAddTime(new Date());
				photo.setWidth(132);
				photo.setHeight(132);
			}
			photo.setName(bigAvatarName + imageType);
			photo.setExt(imageType);
			photo.setPath(this.ftpServerTools.userUpload(photo.getName(), "/account", CommUtil.null2String(user.getId())));
			if (user.getPhoto() == null) {
				this.accessoryService.save(photo);
			} else {
				this.accessoryService.update(photo);
			}
			user.setPhoto(photo);
			this.userService.update(user);
			// ###中头像内容
			// String middleAvatarContent = CommUtil.null2String(request
			// .getParameter("middle_avatar"));
			// // ###中头像名称
			// String middleAvatarName = CommUtil.null2String(request
			// .getParameter("middle_avatar_name"));
			// // ###保存中头像
			// saveImage(filePath, imageType, middleAvatarContent,
			// middleAvatarName);
			// // $$$小头像内容
			// String littleAvatarContent = CommUtil.null2String(request
			// .getParameter("little_avatar"));
			// // $$$小头像名称
			// String littleAvatarName = CommUtil.null2String(request
			// .getParameter("little_avatar_name"));
			// // $$$保存小头像
			// saveImage(filePath, imageType, littleAvatarContent,
			// littleAvatarName);
			// <4>. 设置返回值: 200表示成功，其它表示失败
			response.setContentType("text/xml");
			// 上传成功标识
			response.getWriter().write(OPERATE_RESULT_CODE_SUCCESS);
		} catch (Exception e) {
			logger.error("上传图片异常(AccountBuyerAction->upload_avatar)：" + e);
			response.setContentType("text/xml");
			// 上传失败标识
			response.getWriter().write(OPERATE_RESULT_CODE_FAIL);
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param filePath
	 *            保存路径
	 * @param imageType
	 *            文件类型(.jpg、.png、.gif)
	 * @param avatarContent
	 *            文件内容
	 * @param avatarName
	 *            文件名称(不包括扩展名)
	 * @throws IOException
	 */
	private void saveImage(String filePath, String imageType, String avatarContent, String avatarName) throws IOException {
		avatarContent = CommUtil.null2String(avatarContent);
		if (!StringUtils.isNullOrEmpty(avatarContent)) {
			if (StringUtils.isNullOrEmpty(avatarName)) {
				avatarName = UUID.randomUUID().toString() + DEFAULT_AVATAR_FILE_EXT;
			} else {
				avatarName = avatarName + imageType;
			}
			@SuppressWarnings("restriction")
			byte[] data = decoder.decodeBuffer(avatarContent);
			File f = new File(filePath + File.separator + avatarName);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
			dos.write(data);
			dos.flush();
			dos.close();
		}
	}

	@SecurityMapping(title = "手机号码修改", value = "/buyer/account_mobile.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile.htm")
	public ModelAndView account_mobile(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_mobile.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@SecurityMapping(title = "手机号码保存", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile_save.htm")
	public ModelAndView account_mobile_save(HttpServletRequest request, HttpServletResponse response,
			String mobile_verify_code, String mobile) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null, "mobile", mobile);
		if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {			
			CustUpdateReq custUpdateReq = new CustUpdateReq();
			custUpdateReq.setCustId(user.getCustId());
			custUpdateReq.setCustMobile(mobile);
			ResultDTO resultDTO = manageConnector.updateCust(custUpdateReq);
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				this.mobileverifycodeService.delete(mvc.getId());
				user.setMobile(mobile);
				this.userService.update(user);//注意：一个手机号不能绑定到多个CRM账户上，否则会出现数据错误
				
				mv.addObject("op_title", "手机绑定成功");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
				try {
					// 绑定成功后发送手机短信提醒
					String content = "尊敬的" + user.getUserName() + "用户您好，您于"
							+ CommUtil.formatLongDate(new Date()) + "为您的星美账户绑定新手机号" + mobile + "成功。[" + this.configService.getSysConfig().getTitle() + "]";
					if(!this.msgTools.sendSMS(user.getMobile(), content)) {
						logger.error("向用户(id=" + user.getId() + ")发送账户绑定新手机号的通知短信失败。新手机号为：" + mobile);
					}
				} catch (Exception e) {
					logger.error("发送短信失败", e);
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "手机绑定失败，请稍后重试");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
				
				logger.error("用户" + user.getCustId() + "绑定新手机号" + mobile + "失败：向CRM请求变更用户手机号失败(code=" + resultDTO.getCode() + ",msg=" + resultDTO.getMsg() + ")");
			}			
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "验证码错误，手机绑定失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
		}
		return mv;
	}

	/**
	 * 手机短信发送
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机短信发送", value = "/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile_sms.htm")
	public void account_mobile_sms(HttpServletRequest request, HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		String msg = "发送短信成功";
		
		if (this.configService.getSysConfig().isSmsEnbale()) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			
			String code = CommUtil.randomString(4).toUpperCase();
			String content = null;
			if ("mobile_vetify_code".equals(type)) {//修改账号绑定的手机号			
				content = "尊敬的" + user.getUserName() + "用户您好，您正在试图修改您在星美网站上绑定的手机号，手机验证码为：" + code + "。["
						+ this.configService.getSysConfig().getTitle() + "]";
				
			} else if ("paypwd_vetify_code".equals(type)) {//修改账号的余额的支付密码
				content = "尊敬的" + user.getUserName() + "用户您好，您正在试图修改您在星美网站上的支付密码，手机验证码为：" + code + "。["
						+ this.configService.getSysConfig().getTitle() + "]";
				mobile = user.getMobile();//不能使用前台传递上来的手机号，因为这个手机号可能已被前端篡改过
			} else {
				return;
			}
			
			if(!this.msgTools.sendSMS(mobile, content)) {
				ret = "200";
				msg = "短信发送失败";
			}
						
			if ("100".equals(ret)) {
				VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null, "mobile", mobile);
				if (mvc == null) {
					mvc = new VerifyCode();
				}
				mvc.setAddTime(new Date());
				mvc.setCode(code);
				mvc.setMobile(mobile);
				
				if(mvc.getId() == null) {
					this.mobileverifycodeService.save(mvc);
				} else {
					this.mobileverifycodeService.update(mvc);
				}
			} 
		} else {
			ret = "300";
			msg = "商城未开启短信服务";
		}
		
		Map result = new HashMap();
		result.put("code", ret);
		result.put("msg", msg);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result));
		} catch (IOException e) {
			logger.debug(e);
		}
	}

	@SecurityMapping(title = "账号绑定", value = "/buyer/account_bind.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_bind.htm")
	public ModelAndView account_bind(HttpServletRequest request, HttpServletResponse response, String error) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_bind.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("user", user);
		mv.addObject("error", error);
		return mv;
	}

	@SecurityMapping(title = "账号解除绑定", value = "/buyer/account_bind_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_bind_cancel.htm")
	public String account_bind_cancel(HttpServletRequest request, HttpServletResponse response, String account) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/account_bind.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if ("qq".equals(CommUtil.null2String(account))) {
			user.setQq_openid(null);
		}
		if ("sina".equals(CommUtil.null2String(account))) {
			user.setSina_openid(null);
		}
		this.userService.update(user);
		return "redirect:account_bind.htm";
	}
}
