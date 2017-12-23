package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
import com.iskyshop.foundation.domain.CustomerRelMana;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CustomerRelManaQueryObject;
import com.iskyshop.foundation.service.ICustomerRelManaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * 
 * <p>
 * Title: SelfCrmManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商家中心crm管理控制器
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
 * @author jinxinzhe
 * 
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfCrmManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICustomerRelManaService customerRelManaService;
	@Autowired
	private MsgTools msgTools;

	@SecurityMapping(title = "自营crm管理", value = "/admin/crm_list.htm*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping("/admin/crm_list.htm")
	public ModelAndView crm_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String type,
			String userName, String email, String message) {
		ModelAndView mv = new JModelAndView("admin/blue/crm_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		CustomerRelManaQueryObject qo = new CustomerRelManaQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.whether_self", new SysMap("whether_self", 1), "=");
		if (email != null) {
			if ("yes".equals(email)) {
				qo.addQuery("obj.whether_send_email", new SysMap("whether_send_email", 1), "=");
			}
			if ("no".equals(email)) {
				qo.addQuery("obj.whether_send_email", new SysMap("whether_send_email", 0), "=");
			}
			mv.addObject("email", email);
		}
		if (message != null) {
			if ("yes".equals(message)) {
				qo.addQuery("obj.whether_send_message", new SysMap("whether_send_message", 1), "=");
			}
			if ("no".equals(message)) {
				qo.addQuery("obj.whether_send_message", new SysMap("whether_send_message", 0), "=");
			}
			mv.addObject("message", message);
		}
		if (type != null) {
			if ("order".equals(type)) {
				qo.addQuery("obj.cus_type", new SysMap("cus_type", 0), "=");
			}
			if ("consult".equals(type)) {
				qo.addQuery("obj.cus_type", new SysMap("cus_type", 1), "=");
			}
			if ("fav".equals(type)) {
				qo.addQuery("obj.cus_type", new SysMap("cus_type", 2), "=");
			}
		}

		if (!StringUtils.isNullOrEmpty(userName)) {
			qo.addQuery("obj.userName", new SysMap("userName", userName), "=");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, CustomerRelMana.class, mv);
		IPageList pList = this.customerRelManaService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/crm_list.htm", "", params, pList, mv);
		mv.addObject("type", type);
		mv.addObject("userName", userName);
		return mv;
	}

	@SecurityMapping(title = "自营crm管理", value = "/admin/send_crm_info.htm*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping("/admin/send_crm_info.htm")
	public ModelAndView send_crm_info(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String type) {
		ModelAndView mv = new JModelAndView("admin/blue/send_crm_email.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (type != null && "message".equals(type)) {
			mv = new JModelAndView("admin/blue/send_crm_message.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		mv.addObject("ids", mulitId);
		return mv;
	}

	@SecurityMapping(title = "自营crm管理", value = "/admin/send_email_save.htm*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping("/admin/send_email_save.htm")
	public void send_email_save(HttpServletRequest request, HttpServletResponse response, String ids, String message) {
		String subject = this.configService.getSysConfig().getTitle() + "邮件";
		String status = "success";
		if (!StringUtils.isNullOrEmpty(message)) {
			String[] cids = ids.split(",");
			for (String id : cids) {
				CustomerRelMana crm = this.customerRelManaService.getObjById(CommUtil.null2Long(id));
				if (crm != null && crm.getWhether_self() == 1) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(crm.getUser_id()));
					if (buyer != null && !StringUtils.isNullOrEmpty(buyer.getEmail())) {
						boolean ret = this.msgTools.sendEmail(buyer.getEmail(), subject, message);
						if (ret) {
							crm.setWhether_send_email(1);
							this.customerRelManaService.update(crm);
						}
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(status);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "自营crm管理", value = "/admin/send_message_save.htm*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping("/admin/send_message_save.htm")
	public void send_message_save(HttpServletRequest request, HttpServletResponse response, String ids, String message)
			throws UnsupportedEncodingException {
		String subject = this.configService.getSysConfig().getTitle();
		String status = "success";
		if (!StringUtils.isNullOrEmpty(message)) {
			String[] cids = ids.split(",");
			for (String id : cids) {
				CustomerRelMana crm = this.customerRelManaService.getObjById(CommUtil.null2Long(id));
				if (crm != null && crm.getWhether_self() == 1) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(crm.getUser_id()));
					if (buyer != null && !StringUtils.isNullOrEmpty(buyer.getMobile())) {
						boolean ret = false;
						try {
							ret = this.msgTools.sendSMS(buyer.getMobile(), subject + ":" + message);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						} 
						if (ret) {
							crm.setWhether_send_message(1);
							this.customerRelManaService.update(crm);
						}
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@SecurityMapping(title = "自营crm管理", value = "/admin/crm_del.htm*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping("/admin/crm_del.htm")
	public String crm_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				CustomerRelMana customerrelmana = this.customerRelManaService.getObjById(Long.parseLong(id));
				if (customerrelmana != null && customerrelmana.getWhether_self() == 1) {
					this.customerRelManaService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:crm_list.htm?currentPage=" + currentPage;
	}
}
