package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.common.EPwdResetType;
import com.smilife.bcp.dto.common.EPwdType;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;

@Controller
public class AppBuyerAccountAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IUserService userService;

	@Autowired
	private UserManageConnector manageConnector;

	/**
	 * 手机端用户中心-账户密码修改
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_cat
	 * @param beginCount
	 * @param selectCount
	 */
	@RequestMapping("/app/buyer/password_modify.htm")
	public void password_modify(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String new_password, String old_password) {
		Map map = new HashMap();
		int code = 100; // 100，修改成功，-100原密码不正确，-200用户信息不正确
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
//			String old_psw = Md5Encrypt.md5(old_password).toLowerCase();
			if (user.getPassword().equals(old_password)) {
				// TODO 会员密码修改
				ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(),
						old_password,new_password,
				EPwdResetType.MODIFY.getState(), EPwdType.LOGIN_PWD.getState());
				if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
					// 商城数据库密码定死为123qwe了，所以需要去掉修改数据库的处理
					/*user.setPassword(new_password);
					this.userService.update(user);*/
					code = 100;
				} else {
					code = -200;
				}
			} else {
				code = -100;   
			}
		} else {
			code = -200;
		}
		map.put("code", code);
		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 手机端查询支付密码
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/app/buyer/pay_password.htm")
	public void pay_password(HttpServletRequest request, HttpServletResponse response, String user_id, String token)
			throws UnsupportedEncodingException {
		Map json_map = new HashMap();
		int code = 100; // 100修改成功，-100参数错误，-200用户信息错误,-300未设置支付密码
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			String pay_password = user.getMobile_pay_password();
			if (!StringUtils.isNullOrEmpty(pay_password)) {
				/*pay_password = this.decodeStr(pay_password);
				for (int i = 0; i < pay_password.length(); i++) {
					if (i < pay_password.length() - 1) {
						pay_password = pay_password.replaceFirst(CommUtil.null2String(pay_password.charAt(i)), "*");
					}
				}*/
				json_map.put("pay_password", pay_password);
			} else {
				code = -300;
			}
		} else {
			code = -200;
		}
		json_map.put("ret", "true");
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
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 手机端支付密码设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/pay_password_setting.htm")
	public void pay_password_setting(HttpServletRequest request, HttpServletResponse response, String user_id, String token,
			String pay_psw, String login_psw) {
		Map json_map = new HashMap();
		int code = 100; // 100修改成功，-100参数错误，-200用户信息错误，-300登录密码错误
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
//			login_psw = Md5Encrypt.md5(login_psw).toLowerCase();
			if (user.getPassword().equals(login_psw)) {
//				ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), null, Md5Encrypt.md5(pay_psw)
//						.toLowerCase(), EPwdResetType.FIND.getState(), EPwdType.PAY_PWD.getState());
				ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), null,pay_psw, EPwdResetType.FIND.getState(), EPwdType.PAY_PWD.getState());
				if(null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())){
					//String temp_pay_psw = this.encodeStr(pay_psw);
//					String temp_pay_psw = Md5Encrypt.md5(pay_psw).toLowerCase();
					user.setMobile_pay_password(pay_psw);
					this.userService.update(user);
				}else{
					code = -100;
				}
			} else {
				code = -300;
			}
		} else {
			code = -200;
		}
		json_map.put("ret", "true");
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
	 * 加密
	 * 
	 * @param pwd
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String encodeStr(String str) {
		byte[] enbytes = Base64.encodeBase64Chunked(str.getBytes());
		return new String(enbytes);
	}

	/**
	 * 解密
	 * 
	 * @param pwd
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String decodeStr(String str) {
		byte[] debytes = Base64.decodeBase64(new String(str).getBytes());
		return new String(debytes);
	}

}
