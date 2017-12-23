package com.iskyshop.view.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.XMLUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.SystemTip;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISystemTipService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: RechargeAction.java
 * </p>
 * 
 * <p>
 * Description:系统充值控制器,用来查询并计算充值应缴纳的金额、手机充值等服务
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
public class RechargeAction {
	private static Logger logger = Logger.getLogger(RechargeAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPaymentService paymentService;

	@RequestMapping("/recharge_query.htm")
	public void recharge_query(HttpServletRequest request, HttpServletResponse response, String mobile, String rc_amount) {
		String userid = this.configService.getSysConfig().getOfcard_userid();
		String userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
		String queryUrl = "http://api2.ofpay.com/telquery.do?userid=" + userid + "&userpws=" + userpws + "&phoneno="
				+ mobile + "&pervalue=" + rc_amount + "&version=6.0";
		String returnXml = this.getHttpContent(queryUrl, Globals.CharSet.GB2312, "POST");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding(Globals.CharSet.UTF_8);
		PrintWriter writer;
		try {
			writer = response.getWriter();
			Map map = XMLUtil.parseXML(returnXml, true);
			if (1 == CommUtil.null2Int(map.get("retcode"))) {
				double inprice = CommUtil.null2Double(map.get("inprice"));
				if (CommUtil.null2Double(map.get("inprice")) <= CommUtil.null2Double(rc_amount)) {
					inprice = CommUtil.add(map.get("inprice"), this.configService.getSysConfig().getOfcard_mobile_profit());
					if (inprice > CommUtil.null2Double(rc_amount)) {
						inprice = CommUtil.null2Double(rc_amount);
					}
				}
				map.put("inprice", inprice);
			}
			writer.print(XMLUtil.map2Json(map));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "手机充值", value = "/recharge.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/recharge.htm")
	public ModelAndView recharge(HttpServletRequest request, HttpServletResponse response, String mobile, String rc_amount) {
		ModelAndView mv = new JModelAndView("recharge.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userid = this.configService.getSysConfig().getOfcard_userid();
		String userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
		String query_url = "http://api2.ofpay.com/telquery.do?userid=" + userid + "&userpws=" + userpws + "&phoneno="
				+ mobile + "&pervalue=" + rc_amount + "&version=6.0";
		String return_xml = this.getHttpContent(query_url, Globals.CharSet.GB2312, "POST");
		Map map = XMLUtil.parseXML(return_xml, true);
		double inprice = CommUtil.null2Double(map.get("inprice"));
		if (CommUtil.null2Double(map.get("inprice")) <= CommUtil.null2Double(rc_amount)) {
			inprice = CommUtil.add(map.get("inprice"), this.configService.getSysConfig().getOfcard_mobile_profit());
			if (inprice > CommUtil.null2Double(rc_amount)) {
				inprice = CommUtil.null2Double(rc_amount);
			}
		}
		map.put("inprice", inprice);
		String recharge_session = CommUtil.randomString(64);
		request.getSession(true).setAttribute("recharge_session", recharge_session);
		mv.addObject("recharge_session", recharge_session);
		mv.addObject("map", map);
		mv.addObject("rc_amount", rc_amount);
		mv.addObject("mobile", mobile);
		return mv;
	}

	@SecurityMapping(title = "手机充值订单保存", value = "/recharge_order.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/recharge_order.htm")
	public ModelAndView recharge_order(HttpServletRequest request, HttpServletResponse response, String mobile,
			String rcAmount, String recharge_session) {
		ModelAndView mv = new JModelAndView("recharge_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String recharge_session1 = CommUtil.null2String(request.getSession(true).getAttribute("recharge_session"));
		if (!"".equals(recharge_session1) && recharge_session1.equals(recharge_session)) {
			request.getSession(true).removeAttribute("recharge_session");
			String userid = this.configService.getSysConfig().getOfcard_userid();
			String userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
			String query_url = "http://api2.ofpay.com/telquery.do?userid=" + userid + "&userpws=" + userpws + "&phoneno="
					+ mobile + "&pervalue=" + rcAmount + "&version=6.0";
			String return_xml = this.getHttpContent(query_url, Globals.CharSet.GB2312, "POST");
			Map xml_map = XMLUtil.parseXML(return_xml, true);
			double inprice = CommUtil.null2Double(xml_map.get("inprice"));
			double rc_price = inprice;
			if (CommUtil.null2Double(xml_map.get("inprice")) <= CommUtil.null2Double(rcAmount)) {
				inprice = CommUtil.add(xml_map.get("inprice"), this.configService.getSysConfig().getOfcard_mobile_profit());
				if (inprice > CommUtil.null2Double(rcAmount)) {
					inprice = CommUtil.null2Double(rcAmount);
				}
			}
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			OrderForm of = new OrderForm();
			of.setAddTime(new Date());
			of.setOrder_form(1);
			String tradeId = CommUtil.formatTime(Globals.DateFormat.DATE_FORMAT_SECOND, new Date());
			of.setOrder_id(tradeId + user.getId().toString());
			of.setOrder_cat(1); // 手机充值订单
			of.setUser_id(CommUtil.null2String(user.getId()));
			of.setUser_name(user.getUsername());
			of.setTotalPrice(BigDecimal.valueOf(inprice));
			of.setGoods_amount(BigDecimal.valueOf(inprice));
			of.setOrder_status(10);
			of.setShip_price(BigDecimal.valueOf(0.0));
			String trade_no = CommUtil.formatTime(Globals.DateFormat.DATE_FORMAT_SECOND, new Date());
			of.setTrade_no(trade_no + user.getId());
			List<Map> goods_maps = new ArrayList<Map>();
			Map goods_map = new HashMap();
			goods_map.put("goods_id", "-1");
			goods_map.put("goods_name", xml_map.get("game_area") + "充值" + rcAmount + "元");
			goods_map.put("goods_mainphoto_path", "resources/style/common/images/mobile_" + rcAmount + ".jpg");
			goods_map.put("goods_price", inprice);
			goods_map.put("goods_count", 1);
			goods_maps.add(goods_map);
			of.setGoods_info(Json.toJson(goods_maps, JsonFormat.compact()));
			of.setRc_amount(CommUtil.null2Int(rcAmount));
			of.setRc_mobile(mobile);
			of.setRc_price(BigDecimal.valueOf(rc_price));
			of.setRc_type("mobile");
			of.setOrder_main(1);
			this.orderFormService.save(of);
			mv.addObject("user", user);
			mv.addObject("mobile", mobile);
			mv.addObject("rc_amount", rcAmount);
			mv.addObject("map", xml_map);
			mv.addObject("order", of);
			String recharge_pay_session = CommUtil.randomString(64);
			request.getSession(true).setAttribute("recharge_pay_session", recharge_pay_session);
			mv.addObject("recharge_pay_session", recharge_pay_session);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单失效，请重新进行提交");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "手机充值缴费", value = "/recharge_pay.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/recharge_pay.htm")
	public ModelAndView recharge_pay(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("recharge_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (order != null && order.getUser_id().equals(user.getId().toString()) && order.getOrder_status() == 10) {
			String ofcard_userid = this.configService.getSysConfig().getOfcard_userid();
			String ofcard_userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
			String rc_amount = CommUtil.null2String(order.getRc_amount());
			String mobile = order.getRc_mobile();
			String query_url = "http://api2.ofpay.com/telquery.do?userid=" + ofcard_userid + "&userpws=" + ofcard_userpws
					+ "&phoneno=" + mobile + "&pervalue=" + rc_amount + "&version=6.0";
			String return_xml = this.getHttpContent(query_url, Globals.CharSet.GB2312, "POST");
			Map map = XMLUtil.parseXML(return_xml, true);
			double inprice = CommUtil.null2Double(map.get("inprice"));
			if (CommUtil.null2Double(map.get("inprice")) <= CommUtil.null2Double(rc_amount)) {
				inprice = CommUtil.add(map.get("inprice"), this.configService.getSysConfig().getOfcard_mobile_profit());
				if (inprice > CommUtil.null2Double(rc_amount)) {
					inprice = CommUtil.null2Double(rc_amount);
				}
			}
			map.put("inprice", inprice);
			String rechargePaySession = CommUtil.randomString(64);
			request.getSession(true).setAttribute("recharge_pay_session", rechargePaySession);
			mv.addObject("recharge_pay_session", rechargePaySession);
			mv.addObject("map", map);
			mv.addObject("rc_amount", rc_amount);
			mv.addObject("mobile", mobile);
			mv.addObject("order", order);
			mv.addObject("user", user);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，充值失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "手机充值完成", value = "/recharge_pay2.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/recharge_pay2.htm")
	public ModelAndView recharge_pay2(HttpServletRequest request, HttpServletResponse response, String id,
			String recharge_pay_session) {
		ModelAndView mv = new JModelAndView("recharge_success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String recharge_pay_session1 = CommUtil.null2String(request.getSession(true).getAttribute("recharge_pay_session"));
		request.getSession(true).removeAttribute("recharge_pay_session");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (!"".equals(recharge_pay_session1) && recharge_pay_session1.equals(recharge_pay_session) && order != null
				&& order.getUser_id().equals(user.getId().toString()) && order.getOrder_status() == 10) {
			if (CommUtil.null2Double(user.getAvailableBalance()) >= CommUtil.null2Double(order.getRc_price())) {
				//
				String userid = this.configService.getSysConfig().getOfcard_userid();
				String userpws = Md5Encrypt.md5(this.configService.getSysConfig().getOfcard_userpws());
				String cardid = "140101";
				String sporderTime = CommUtil.formatTime(Globals.DateFormat.DATE_FORMAT_SECOND, new Date());
				String cardnum = CommUtil.null2String(order.getRc_amount());
				String sporderId = sporderTime + "-" + order.getId();
				String gameUserId = order.getRc_mobile();
				String retUrl = CommUtil.getURL(request) + "/recharge_return.htm";
				String md5Str = Md5Encrypt
						.md5(userid + userpws + cardid + cardnum + sporderId + sporderTime + gameUserId + "OFCARD")
						.toUpperCase();
				String version = "6.0";
				String rechargeUrl = "http://api2.ofpay.com/onlineorder.do?userid=" + userid + "&userpws=" + userpws
						+ "&cardid=" + cardid + "&cardnum=" + cardnum + "&sporder_id=" + sporderId + "&sporder_time="
						+ sporderTime + "&game_userid=" + gameUserId + "&md5_str=" + md5Str + "&ret_url=" + retUrl
						+ "&version=" + version;
				String returnXml = this.getHttpContent(rechargeUrl, Globals.CharSet.GB2312, "POST");
				logger.debug(returnXml);
				Map map = XMLUtil.parseXML(returnXml, true);
				if (CommUtil.null2Int(map.get("retcode")) == 1) { // 表示充值成功,扣除用户预存款并改变订单状态
					//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), order.getRc_price())));//余额都在CRM端保存和增减，故注释掉
					//this.userService.update(user);
					order.setOrder_status(50);
					Map params = new HashMap();
					params.put("mark", "balance");
					List<Payment> list = this.paymentService.query("select obj from Payment obj where obj.mark =:mark",
							params, -1, -1);
					Payment payment = list != null ? list.get(0) : new Payment();
					order.setPayment(payment);
					this.orderFormService.update(order);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_op_type("消费");
					log.setPd_log_amount(BigDecimal.valueOf(-order.getRc_amount()));
					log.setPd_log_info(order.getRc_mobile() + "手机话费充值购物减少可用预存款");
					log.setPd_type("可用预存款");
					this.predepositLogService.save(log);
				} else if (CommUtil.null2Int(map.get("retcode")) == 1007) {
					// 向管理员发送系统提醒，通知管理员殴飞账户余额不足
					SystemTip st = new SystemTip();
					st.setAddTime(new Date());
					st.setSt_content("手机充值账户余额不足，请及时充值");
					st.setSt_level(5);
					st.setSt_status(0);
					st.setSt_title("充值失败提示");
					this.systemTipService.save(st);
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "网络忙，请稍后尝试！");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				} else {
					// 向管理员发送系统提醒，通知管理员殴飞账户出现问题
					SystemTip st = new SystemTip();
					st.setAddTime(new Date());
					st.setSt_content("殴飞账户出现问题，错误代码为:" + map.get("retcode"));
					st.setSt_level(5);
					st.setSt_status(0);
					this.systemTipService.save(st);
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "接口故障，充值失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
				}
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "预存款余额不足，请充值");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误，订单已失效");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 接收殴飞充值接口的返回处理
	 * 
	 * @param request
	 * @param response
	 * @param retCode
	 * @param sporderId
	 * @param ordersuccesstime
	 * @param errMsg
	 *            错误信息
	 */
	@RequestMapping("/recharge_return.htm")
	public void recharge_return(HttpServletRequest request, HttpServletResponse response, String retCode, String sporderId,
			String ordersuccesstime, String errMsg) {
		if (!StringUtils.isNullOrEmpty(sporderId)) {
			String[] orderIds = sporderId.split("-");
			if (orderIds.length == 2) {
				OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(orderIds[1]));
				if (order.getOrder_status() == 10) {
					order.setOrder_status(40); // 设置订单状态为已经付款完毕
					order.setFinishTime(new Date());
					boolean ret = this.orderFormService.update(order);
					//if (ret) {
						//User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), order.getTotalPrice())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
					//}
				}
			}
		}
	}

	@RequestMapping("/refresh_balance.htm")
	public void refresh_balance(HttpServletRequest request, HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(user.getAvailableBalance());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private static String getHttpContent(String url, String charSet, String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");
			// 设置访问超时时间及读取网页流的超时时间,毫秒值
			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);
			// 得到访问页面的返回值
			int response_code = connection.getResponseCode();
			if (response_code == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content += line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}
}
