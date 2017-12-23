package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.TransArea;
import com.iskyshop.foundation.domain.query.AreaQueryObject;
import com.iskyshop.foundation.domain.query.TransAreaQueryObject;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITransAreaService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: TransAreaManageAction.java
 * </p>
 * 
 * <p>
 * Description:运费区域管理控制器，用来管理控制系统配送区域信息，和系统常用区域不同
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
 * @date 2014-10-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class TransAreaManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ITransAreaService transareaService;

	/**
	 * TransArea列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "运费地区列表", value = "/admin/trans_area_list.htm*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping("/admin/trans_area_list.htm")
	public ModelAndView trans_area_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String pid, String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/trans_area_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		TransAreaQueryObject qo = null;
		if (StringUtils.isNullOrEmpty(pid)) {
			qo = new TransAreaQueryObject(currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.parent.id is null", null);
		} else {
			qo = new TransAreaQueryObject(currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.parent.id", new SysMap("pid", Long.parseLong(pid)), "=");
			params = "&pid=" + pid;
			TransArea parent = this.transareaService.getObjById(Long.parseLong(pid));
			mv.addObject("parent", parent);
			if (parent.getLevel() == 0) {
				Map map = new HashMap();
				map.put("pid", parent.getId());
				List<TransArea> seconds = this.transareaService
						.query("select obj from TransArea obj where obj.parent.id=:pid", map, -1, -1);
				mv.addObject("seconds", seconds);
				mv.addObject("first", parent);
			}
			if (parent.getLevel() == 1) {
				Map map = new HashMap();
				map.put("pid", parent.getId());
				List<TransArea> thirds = this.transareaService
						.query("select obj from TransArea obj where obj.parent.id=:pid", map, -1, -1);
				map.clear();
				map.put("pid", parent.getParent().getId());
				List<TransArea> seconds = this.transareaService
						.query("select obj from TransArea obj where obj.parent.id=:pid", map, -1, -1);
				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("second", parent);
				mv.addObject("first", parent.getParent());
			}
			if (parent.getLevel() == 2) {
				Map map = new HashMap();
				map.put("pid", parent.getParent().getId());
				List<TransArea> thirds = this.transareaService
						.query("select obj from TransArea obj where obj.parent.id=:pid", map, -1, -1);
				map.clear();
				map.put("pid", parent.getParent().getParent().getId());
				List<TransArea> seconds = this.transareaService
						.query("select obj from TransArea obj where obj.parent.id=:pid", map, -1, -1);
				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("third", parent);
				mv.addObject("second", parent.getParent());
				mv.addObject("first", parent.getParent().getParent());
			}
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, TransArea.class, mv);
		IPageList pList = this.transareaService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/trans_area_list.htm", "", params, pList, mv);
		List<TransArea> areas = this.transareaService.query("select obj from TransArea obj where obj.parent.id is null",
				null, -1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * area保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "运费地区保存", value = "/admin/trans_area_save.htm*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping("/admin/trans_area_save.htm")
	public ModelAndView trans_area_save(HttpServletRequest request, HttpServletResponse response, String areaId, String pid,
			String count, String list_url, String currentPage) {
		// 批量更新
		if (areaId == null) {
		} else {
			String[] ids = areaId.split(",");
			int i = 1;
			for (String id : ids) {
				String areaName = request.getParameter("areaName_" + i);
				TransArea area = this.transareaService.getObjById(Long.parseLong(request.getParameter("id_" + i)));
				area.setAreaName(areaName);
				area.setSequence(CommUtil.null2Int(request.getParameter("sequence_" + i)));
				this.transareaService.update(area);
				i++;
			}
		}
		// 批量更新完毕
		// 批量保存
		TransArea parent = null;
		if (!StringUtils.isNullOrEmpty(pid))
			parent = this.transareaService.getObjById(Long.parseLong(pid));
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			TransArea area = new TransArea();
			area.setAddTime(new Date());
			String areaName = request.getParameter("new_areaName_" + i);
			int sequence = CommUtil.null2Int(request.getParameter("new_sequence_" + i));
			if (parent != null) {
				area.setLevel(parent.getLevel() + 1);
				area.setParent(parent);
			}
			area.setAreaName(areaName);
			area.setSequence(sequence);
			this.transareaService.save(area);
		}
		// 批量保存完毕
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "更新配送区域成功");
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage + "&pid=" + pid);
		return mv;

	}

	private Set<Long> genericIds(TransArea obj) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(obj.getId());
		for (TransArea child : obj.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@SecurityMapping(title = "运费地区删除", value = "/admin/trans_area_del.htm*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping("/admin/trans_area_del.htm")
	public String trans_area_del(HttpServletRequest request, String mulitId, String currentPage, String pid) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Set<Long> list = this.genericIds(this.transareaService.getObjById(Long.parseLong(id)));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", list);
				List<TransArea> objs = this.transareaService.query("select obj from TransArea obj where obj.id in (:ids)",
						params, -1, -1);
				for (TransArea obj : objs) {
					obj.setParent(null);
					this.transareaService.delete(obj.getId());
				}
			}
		}
		return "redirect:trans_area_list.htm?pid=" + pid + "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "运费地区Ajax更新", value = "/admin/trans_area_ajax.htm*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping("/admin/trans_area_ajax.htm")
	public void trans_area_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		TransArea obj = this.transareaService.getObjById(Long.parseLong(id));
		Field[] fields = TransArea.class.getDeclaredFields();
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
		this.transareaService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

	}
}