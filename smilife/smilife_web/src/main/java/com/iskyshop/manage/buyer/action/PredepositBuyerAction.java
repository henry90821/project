package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PredepositLogQueryObject;
import com.iskyshop.foundation.domain.query.PredepositQueryObject;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.tydic.framework.util.PropertyUtil;

/**
 * 前端买家充值管理控制器，用来处理买家充值等
 */
@Controller
public class PredepositBuyerAction {
	
	private Logger logger = Logger.getLogger(PredepositBuyerAction.class);
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private PayTools payTools;
	@Autowired
	private IpayCenterService payCenterService;
	@Autowired
	private PaymentTools paymentTools;

	
	@SecurityMapping(title = "会员充值", value = "/buyer/predeposit.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit.htm")
	public ModelAndView predeposit(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_predeposit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			mv.addObject("paymentTools", this.paymentTools);
			String mark = PropertyUtil.getProperty("web_pay_center_mark");
			mv.addObject("mark", mark);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "会员充值", value = "/buyer/life_recharge.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/life_recharge.htm")
	public ModelAndView life_recharge(HttpServletRequest request, HttpServletResponse response, String mobile,
			String rc_amount) {
		SysConfig config = configService.getSysConfig();
		ModelAndView mv = new JModelAndView("life_recharge.html", config, this.userConfigService.getUserConfig(), 1, request, response);
		
		mv.addObject("paymentTools", this.paymentTools);
		
		if(!StringUtils.isNullOrEmpty(mobile)) {
			try {
				if(userService.getBuyerOrMainSellerByMobile(mobile)== null) {
					mobile = null;
				}
			} catch (Exception e) {
				logger.error("用户数据错误。mobile=" + mobile, e);
				mobile = null;
			}
		} 		
		
		mv.addObject("mobile", mobile);		
		
		String amountList = "," + config.getAmountAllowed() + ",";		
		if(StringUtils.isNullOrEmpty(rc_amount) || amountList.indexOf("," + rc_amount.trim() + ",") < 0) {
			rc_amount = amountList.substring(1, amountList.indexOf(",", 1));
		} 
		mv.addObject("pd_amount", rc_amount);
		
		String mark = PropertyUtil.getProperty("web_pay_center_mark");
		mv.addObject("mark", mark);
		
		return mv;
	}

	@SecurityMapping(title = "会员充值保存", value = "/buyer/predeposit_save.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_save.htm")
	public ModelAndView predeposit_save(HttpServletRequest request, HttpServletResponse response, String id, String pd_payment, String mobile, String gate_id) {
		ModelAndView mv = new JModelAndView("line_pay.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		//注意有三个页面会向此Action发送请求
		if (this.configService.getSysConfig().isDeposit()) {			
			WebForm wf = new WebForm();
			Predeposit obj = null;
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if (StringUtils.isNullOrEmpty(id)) {
				obj = wf.toPo(request, Predeposit.class);
				obj.setAddTime(new Date());
				if ("outline".equals(pd_payment)) {
					obj.setPd_pay_status(1);
				} else {
					obj.setPd_pay_status(0);
				}
				if (StringUtils.isNullOrEmpty(mobile)) {// 未传账号绑定的手机号时则表示当前用户给自己充值
					obj.setPd_user(user);
					obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
					mobile = user.getMobile();
				} else {
					User userBymobile = userService.getBuyerOrMainSellerByMobile(mobile);
					
					if(userBymobile == null) {
						mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "被充值手机号（" + mobile + "）绑定的账号在系统中不存在");
						mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
						return mv;
					}
					
					obj.setPd_user(userBymobile);
					obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + userBymobile.getId());
				}
				obj.setPayUser(user);// 充值用户为当前登录者
				this.predepositService.save(obj);
			} else {
				Predeposit pre = this.predepositService.getObjById(CommUtil.null2Long(id));
				if(pre.getPayUser().getId().equals(user.getId())) {
					obj = (Predeposit) wf.toPo(request, pre);
					this.predepositService.update(obj);
				}				
			}
			
			if ("outline".equals(pd_payment)) {
				mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "线下支付提交成功，等待审核");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
			} else {
				String mark = PropertyUtil.getProperty("web_pay_center_mark");
				if("1".equals(mark)){
					String returnUrl = CommUtil.getURL(request) + PropertyUtil.getProperty("web_return_url_predeposit"); //回调业务系统地址
					Result payResult =  this.payCenterService.placeOrder(user, obj.getPd_sn(), "", ChannelEnum.WEB.name(), OrderTypeEnum.CHARGE.getIndex(), "", returnUrl);
					if(payResult != null && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((payResult.getCode()))){
						Map<String, Object> json = (Map<String, Object>) payResult.getData();
						try {
							response.getWriter().write("<script>window.location.href='"+ CommUtil.null2String(json.get("url")) +"'</script>");
						} catch (Exception e) {
							logger.error("调用 response.getWriter()时失败", e);
						}
						return null;
					} else {
						mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "调用支付中心进行充值失败");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
						return mv;
					}
				}
				
				mv.addObject("payType", pd_payment);
				mv.addObject("gate_id", gate_id);
				mv.addObject("type", "cash");
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("cash_id", obj.getId());
				Map params = new HashMap();
				params.put("install", true);
				params.put("mark", obj.getPd_payment());
				List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", params, -1, -1);
				mv.addObject("payment_id", payments.size() > 0 ? payments.get(0).getId() : new Payment());
			}			
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值列表", value = "/buyer/predeposit_list.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_list.htm")
	public ModelAndView predeposit_list(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/predeposit_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			User user = SecurityUserHolder.getCurrentUser();
			PredepositQueryObject qo = new PredepositQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.pd_user.id", new SysMap("user_id", user.getId()), "=");
			IPageList pList = this.predepositService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("mark", PropertyUtil.getProperty("web_pay_center_mark"));
			mv.addObject("user", user);
			mv.addObject("paymentTools", paymentTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		
		return mv;
	}

	@SecurityMapping(title = "会员充值详情", value = "/buyer/predeposit_view.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_view.htm")
	public ModelAndView predeposit_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/predeposit_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Predeposit obj = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (obj.getPd_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
			}
			mv.addObject("paymentTools", paymentTools);

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值支付", value = "/buyer/predeposit_pay.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_pay.htm")
	public ModelAndView predeposit_pay(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/predeposit_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Predeposit obj = this.predepositService.getObjById(CommUtil.null2Long(id));
			User user = SecurityUserHolder.getCurrentUser();
			if (obj.getPd_user().getId().equals(user.getId())) {
				String mark = PropertyUtil.getProperty("web_pay_center_mark");
				if("1".equals(mark)){
					String returnVal = "";
					String returnUrl = CommUtil.getURL(request) + PropertyUtil.getProperty("web_return_url_predeposit"); //回调业务系统地址
					Result payResult = this.payCenterService.placeOrder(null, obj.getPd_sn(), "", ChannelEnum.WEB.name(), OrderTypeEnum.CHARGE.getIndex(), "账户充值", returnUrl);
					if(payResult != null && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((payResult.getCode()))){
						Map<String, Object> json = (Map<String, Object>) payResult.getData();
						if(json != null){
							String url = CommUtil.null2String(json.get("url"));
							try {
								response.sendRedirect(url);
							} catch (IOException e) {
								logger.error("重定向客户端到支付中心支付页面失败", e);
							}
							return null;
						} else {
							returnVal = "Error：支付中心返回值为null";
						}
					} else {
						returnVal = "Error:" + payResult.getMsg();	
						logger.error("支付充值订单发生错误:" + payResult.getMsg() + "。 参数列表如下：user_id:" + user.getId() + ", pd_sn:" + obj.getPd_sn() + ", channel:" + ChannelEnum.WEB.name() + ", orderType:" + OrderTypeEnum.CHARGE.getIndex());
					}
					try {
						response.setContentType("text/plain");
						response.setHeader("Cache-Control", "no-cache");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().print(returnVal);
					} catch (IOException e) {
						logger.error("支付充值订单时向客户端返回信息失败", e);
					}
					return null;
				}
				mv.addObject("obj", obj);
				mv.addObject("paymentTools", paymentTools);
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员收入明细", value = "/buyer/predeposit_log.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_log.htm")
	public ModelAndView predeposit_log(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_predeposit_log.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			PredepositLogQueryObject qo = new PredepositLogQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.pd_log_user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
			IPageList pList = this.predepositLogService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

}
