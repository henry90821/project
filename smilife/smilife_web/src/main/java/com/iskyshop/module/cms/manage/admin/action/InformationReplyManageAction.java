package com.iskyshop.module.cms.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.module.cms.domain.InformationReply;
import com.iskyshop.module.cms.domain.query.InformationReplyQueryObject;
import com.iskyshop.module.cms.service.IInformationReplyService;

@Controller
public class InformationReplyManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IInformationReplyService informationreplyService;

	/**
	 * InformationReply列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯回复列表", value = "/admin/information_reply.htm*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping("/admin/information_reply.htm")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/information_reply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationReplyQueryObject qo = new InformationReplyQueryObject(currentPage, mv, orderBy, orderType);
		IPageList pList = this.informationreplyService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * informationreply添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/informationreply_add.htm")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/informationreply_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * informationreply编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/admin/informationreply_edit.htm")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/informationreply_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			InformationReply informationreply = this.informationreplyService.getObjById(Long.parseLong(id));
			mv.addObject("obj", informationreply);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * informationreply保存管理
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/informationreply_save.htm")
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		InformationReply informationreply = null;
		if (StringUtils.isNullOrEmpty(id)) {
			informationreply = wf.toPo(request, InformationReply.class);
			informationreply.setAddTime(new Date());
		} else {
			InformationReply obj = this.informationreplyService.getObjById(Long.parseLong(id));
			informationreply = (InformationReply) wf.toPo(request, obj);
		}

		if (StringUtils.isNullOrEmpty(id)) {
			this.informationreplyService.save(informationreply);
		} else
			this.informationreplyService.update(informationreply);
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存informationreply成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "资讯回复删除", value = "/admin/information_reply_del.htm*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping("/admin/information_reply_del.htm")
	public String delete(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				InformationReply informationreply = this.informationreplyService.getObjById(Long.parseLong(id));
				this.informationreplyService.delete(Long.parseLong(id));
			}
		}
		return "redirect:information_reply.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/informationreply_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value)
			throws ClassNotFoundException {
		InformationReply obj = this.informationreplyService.getObjById(Long.parseLong(id));
		Field[] fields = InformationReply.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				}
				if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!"".equals(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.informationreplyService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			logger.error(e);
		}

	}
}