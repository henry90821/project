package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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

import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.weixin.view.action.WeixinPayViewAction;
import com.iskyshop.view.web.action.VerifyAction;
import com.smilife.bcp.dto.response.MemberInfoResp;
import com.smilife.bcp.service.UserManageConnector;

/**
 * @ClassName: AppRecharge
 * @Description: TODO(手机app生活卡充值)
 * @author wangyun
 * @date 2015-9-22
 * 
 */
@Controller
public class AppRechargeAction {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private AppPayViewAction appPayViewAction;

	@Autowired
	private IUserService userService;

	@Autowired
	private IPredepositService predepositService;

	@Autowired
	private WeixinPayViewAction weixinPayViewAction;
	@Autowired
	private VerifyAction verifyAction;

	@Autowired
	private UserManageConnector manageConnector;

	/**
	 * 
	 * @Title: predeposit
	 * @Description: TODO(获取移动支付方式签名信息)
	 * @param @param request
	 * @param @param response
	 * @param @param password
	 * @param @param new_password
	 * @param @param code
	 * @param @return 参数
	 * @return ModelAndView 返回类型
	 * @throws
	 */

	/**
	 * @RequestMapping("/app/appPredeposit.htm") public void predeposit(HttpServletRequest request, HttpServletResponse
	 *                                           response, String payType, String type,String orderId) { int code = 100;//
	 *                                           100请求成功,-100请求异常 try { // 支付宝支付 if ("alipay_app".equals(payType)) {
	 *                                           this.appPayViewAction.query_payment_online(request, response, payType); } //
	 *                                           微信支付 if ("wx_app".equals(payType) && "cash".equals(type)) {
	 *                                           this.appPayViewAction.wx_pay(request, response, String.valueOf(orderId),
	 *                                           type); } // 银联支付 if ("unionpay_app".equals(payType)) {
	 *                                           this.appPayViewAction.unionPay(request, response, type,
	 *                                           String.valueOf(orderId)); } } catch (Exception e) { code=-100;
	 *                                           e.printStackTrace(); } }
	 */
	/**
	 * 
	 * @Title: returnNominalValue
	 * @Description: TODO(获取生活卡充值面额)
	 * @param @return 参数
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping("/app/returnNominalValue.htm")
	public void returnNominalValue(HttpServletRequest request, HttpServletResponse response) {

		int code = 100;// 100请求成功,-100请求异常
		boolean ret = true;
		int k = 4;
		List nominal_list = new ArrayList();
		Map json_map = new HashMap();
		PrintWriter printWriter = null;
		for (int i = 1; i <= k; i++) {
			Map value_map = new HashMap();
			value_map.put("nominal_name", i * 100);
			value_map.put("nominal_price", i * 100);
			nominal_list.add(value_map);
		}
		Map value_map1 = new HashMap();
		value_map1.put("nominal_name", "0.01");
		value_map1.put("nominal_price", "0.01");
		Map value_map2 = new HashMap();
		value_map2.put("nominal_name", "0.1");
		value_map2.put("nominal_price", "0.1");
		nominal_list.add(value_map1);
		nominal_list.add(value_map2);

		json_map.put("nominal_list", nominal_list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			printWriter = response.getWriter();
		} catch (IOException e) {
			code = -100;
			ret = false;
			e.printStackTrace();
			logger.error("【app充值:获取充值面额异常】>>>" + e.getMessage());
		}
		json_map.put("code", code);
		json_map.put("ret", ret);
		String json = Json.toJson(json_map, JsonFormat.compact());
		logger.info("获取生活卡充值面额输出参数:" + json);
		printWriter.write(json);
	}

	/**
	 * 
	 * @Title: getOrderIdAndOrderNum
	 * @Description: TODO(获取预存款的主键ID和编码)
	 * @param @param request
	 * @param @param response
	 * @param @param pdAmount
	 * @param @param userName 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/app/buyer/orderIdAndOrderNum.htm")
	public void getOrderIdAndOrderNum(HttpServletRequest request, HttpServletResponse response, String pdAmount,
			String userName) {

		logger.info("获取预存款的主键ID和编码传入参数:userName=" + userName + ",pdAmount=" + pdAmount);

		String msg = ""; // 返回信息
		int code = 100;// 100请求成功,-100请求异常
		Map json_map = new HashMap();
		PrintWriter printWriter = null;
		try {

			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			printWriter = response.getWriter();

			// 判断用户是否存在crm
			MemberInfoResp memberInfoResp = null; //this.manageConnector.queryMemberInfoByUserName(userName);
			if (StringUtils.isNullOrEmpty(memberInfoResp)) {
				msg = "crm不存在此用户!";
				code = -200;
				json_map.put("code", code);
				json_map.put("msg", msg);
				String json = Json.toJson(json_map, JsonFormat.compact());
				printWriter.write(json);
				return;
			}
			Predeposit obj = new Predeposit();
			obj.setAddTime(new Date());
			obj.setPd_pay_status(0);
			if (!StringUtils.isNullOrEmpty(pdAmount)) {
				obj.setPd_amount(new BigDecimal(pdAmount));
			}
			obj.setPayUser(SecurityUserHolder.getCurrentUser());

			User userByName = this.userService.getBuyerOrMainSellerByMobile(userName);//查找会员不能用userName字段，而是要用mobile字段
			if (userByName != null) {
				obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + userByName.getId());
			} else {
				throw new SmiBusinessException("未找到对应的会员：userName/mobile：" + userName);
			}
			obj.setPd_user(userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
			this.predepositService.save(obj);

			json_map.put("order_id", obj.getId());
			json_map.put("order_num", obj.getPd_sn());
			msg = "请求成功";
		} catch (Exception e) {
			code = -100;
			msg = "系统异常";
			logger.error("【app充值：获取预存款订单ID异常】>>>" + e.getMessage());
		}
		json_map.put("code", code);
		json_map.put("msg", msg);
		String json = Json.toJson(json_map, JsonFormat.compact());
		logger.info("获取预存款的主键ID和编码返回参数:" + json);
		printWriter.write(json);
	}
}
