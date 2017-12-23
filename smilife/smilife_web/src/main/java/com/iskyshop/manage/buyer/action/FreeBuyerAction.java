package com.iskyshop.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: FreeBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心0元试用中心
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
 * @date 2014-11-18
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class FreeBuyerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeApplyLogService freeapplylogService;
	@Autowired
	private ShipTools shipTools;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private FTPServerTools ftpServerTools;

	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_logs.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapply_logs.htm")
	public ModelAndView freeapply_logs(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String status) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/freeapplylog_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.user_id", new SysMap("user_id", CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId())),"=");
		if (status != null) {
			if ("yes".equals(status)) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", 5), "=");
				mv.addObject("status", status);
			}
			if ("waiting".equals(status)) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", 0), "=");
				mv.addObject("status", status);
			}
			if ("no".equals(status)) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", -5), "=");
				mv.addObject("status", status);
			}
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeApplyLog.class, mv);
		IPageList pList = this.freeapplylogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapply_log_info.htm")
	public ModelAndView freeapply_log_info(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/freeapplylog_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		FreeApplyLog fal = this.freeapplylogService.getObjById(CommUtil.null2Long(id));
		String pIds = "";
		if (fal != null && fal.getUser_id().equals(user.getId())) {			
			if(fal.getEvaluate_status() == 1) {//已评价
				String photo_ids = CommUtil.null2String(fal.getEvaluate_photos());
				if(!"".equals(photo_ids)) {
					String ids[] = photo_ids.split(",");
					for(String tmp: ids) {
						if(tmp != null && !"".equals(tmp)) {
							Accessory image = this.accessoryService.getObjById(CommUtil.null2Long(tmp));
							if ("eva_temp".equals(image.getInfo())) {
								image.setInfo("eva_img");
								this.accessoryService.save(image);
							}
							pIds += "," + tmp;
						}
					}
					Map params = new HashMap();
					params.put("info", "eva_img");
					List<Accessory> imglist = this.accessoryService.query("select obj from Accessory obj where obj.info=:info and obj.id in ("+pIds.substring(1)+")", params, -1, -1);
					if(imglist != null && imglist.size() > 0) {
						mv.addObject("photos", imglist);
						mv.addObject("pids", pIds);
					}
				}				
			} 			
			mv.addObject("obj", fal);
			mv.addObject("shipTools", shipTools);

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "此0元试用申请无效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/freeapply_logs.htm");
		}
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}
	
	
	@SecurityMapping(title = "0元试用详情,买家评价图片上传", value = "/buyer/upload_freeapplylog_info.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/upload_freeapplylog_info.htm")
	public void upload_evaluate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		Map json_map = new HashMap();
		SysConfig config = this.configService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Map params = new HashMap();
		params.put("userid", user.getId());
		params.put("info", "eva_temp");
		List<Accessory> imglist = this.accessoryService.query(
				"select obj from Accessory obj where obj.user.id=:userid and obj.info=:info", params, -1, -1);
		if (imglist.size() > 40) { // 每个用户最多可以上传40张临时图片，半小时清理一次
			json_map.put("ret", false);
			json_map.put("msg", "您最近上传过多图片，请稍后重试");
			response.setContentType("text/plain");
			response.getWriter().print(Json.toJson(json_map));
		} else {
			json_map.put("ret", true);
			try {
				// <1>. 判断路径是否存在,若不存在则创建路径
				String fal_id = CommUtil.null2String(request.getParameter("fal_id"));
				String filePath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
				CommUtil.createFolder(filePath);
				Map map = CommUtil.saveFileToServer(request, fal_id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("eva_temp");
				String url = this.ftpServerTools
						.userUpload(image.getName(), "/freeapplylog", CommUtil.null2String(user.getId()));
				image.setPath(url);
				this.accessoryService.save(image);
				json_map.put("url", url + "/" + image.getName());
				json_map.put("id", image.getId());
				json_map.put("fal_id", fal_id);
				response.setContentType("text/plain");
				response.getWriter().print(Json.toJson(json_map));
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/freeapplylog_save.htm")
	public void freeapplylog_save(HttpServletRequest request, HttpServletResponse response, String id,
			String use_experience) {
		User user = SecurityUserHolder.getCurrentUser();
		FreeApplyLog fal = this.freeapplylogService.getObjById(CommUtil.null2Long(id));
		if (fal.getUser_id().equals(user.getId())) {
			fal.setUse_experience(use_experience);
			fal.setEvaluate_time(new Date());
			fal.setEvaluate_status(1);
			String photo_ids = request.getParameter("evaluate_photos_" + fal.getId());
			fal.setEvaluate_photos(photo_ids);
			this.freeapplylogService.save(fal);
		}
	}
}
