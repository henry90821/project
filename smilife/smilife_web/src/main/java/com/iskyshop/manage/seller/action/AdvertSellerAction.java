package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.AdvertPositionQueryObject;
import com.iskyshop.foundation.domain.query.AdvertQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: AdvertSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家广告管理类，商家可以使用金币购买广告位
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class AdvertSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private FTPServerTools FTPTools;

	@SecurityMapping(title = "广告列表", value = "/seller/advert_list.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_list.htm")
	public ModelAndView advert_list(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/advert_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AdvertPositionQueryObject qo = new AdvertPositionQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.ap_status", new SysMap("ap_status", 1), "=");// 已启用
		qo.addQuery("obj.ap_use_status", new SysMap("ap_use_status", 1), "!=");// 没有被
		qo.addQuery("obj.ap_sys_type", new SysMap("ap_sys_type", 0), "!=");// 非系统广告
		qo.setPageSize(30);
		IPageList pList = this.advertPositionService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "广告购买", value = "/seller/advert_apply.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_apply.htm")
	public ModelAndView advert_apply(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/advert_apply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (ap.getAp_price() > user.getGold()) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "金币不足，不能申请");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/advert_list.htm");
		} else {
			String ap_session = CommUtil.randomString(32);
			request.getSession(true).setAttribute("ap_session", ap_session);
			mv.addObject("ap_session", ap_session);
			mv.addObject("ap", ap);
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		}
		return mv;
	}

	@RequestMapping("/seller/advert_vefity.htm")
	public void advert_vefity(HttpServletRequest request, HttpServletResponse response, String day, String ap_id) {
		boolean ret = true;
		AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(ap_id));
		int total_price = ap.getAp_price() * CommUtil.null2Int(day);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (total_price > user.getGold()) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@SecurityMapping(title = "广告购买保存", value = "/seller/advert_apply_save.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_apply_save.htm")
	public void advert_apply_save(HttpServletRequest request, HttpServletResponse response, String id, String ap_id,
			String ad_begin_time, String day, String ap_session) {
		Map json = new HashMap();
		json.put("ret", false);
		String ap_session1 = CommUtil.null2String(request.getSession(true).getAttribute("ap_session"));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (StringUtils.isNullOrEmpty(ap_session1)) {
			json.put("op_title", "禁止表单重复提交");
			json.put("url", CommUtil.getURL(request) + "/seller/advert_list.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		} else {
			request.getSession(true).removeAttribute("ap_session");
			Advert advert = null;
			WebForm wf = new WebForm();
			if (StringUtils.isNullOrEmpty(id)) {
				advert = wf.toPo(request, Advert.class);
				advert.setAddTime(new Date());
				AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(ap_id));
				advert.setAd_ap(ap);
				advert.setAd_begin_time(CommUtil.formatDate(ad_begin_time));
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, CommUtil.null2Int(day));
				advert.setAd_end_time(cal.getTime());
				advert.setAd_user(user);
				advert.setAd_gold(ap.getAp_price() * CommUtil.null2Int(day));
			} else {
				Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
				advert = (Advert) wf.toPo(request, obj);
			}
			if (!"text".equals(advert.getAd_ap().getAp_type())) {
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
						+ File.separator + "cache";
				Map map = new HashMap();
				String fileName = "";
				if (advert.getAd_acc() != null) {
					fileName = advert.getAd_acc().getName();
				}
				try {
					map = CommUtil.saveFileToServer(request, "acc", saveFilePathName, fileName, null);
					Accessory acc = null;
					if (StringUtils.isNullOrEmpty(fileName)) {
						if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
							acc = new Accessory();
							acc.setName(CommUtil.null2String(map.get("fileName")));
							acc.setExt(CommUtil.null2String(map.get("mime")));
							acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
							acc.setPath(
									this.FTPTools.userUpload(acc.getName(), "/advert", CommUtil.null2String(user.getId())));
							acc.setWidth(CommUtil.null2Int(map.get("width")));
							acc.setHeight(CommUtil.null2Int(map.get("height")));
							acc.setAddTime(new Date());
							this.accessoryService.save(acc);
							advert.setAd_acc(acc);
						}
					} else {
						if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
							acc = advert.getAd_acc();
							acc.setName(CommUtil.null2String(map.get("fileName")));
							acc.setExt(CommUtil.null2String(map.get("mime")));
							acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
							acc.setPath(
									this.FTPTools.userUpload(acc.getName(), "/advert", CommUtil.null2String(user.getId())));
							acc.setWidth(CommUtil.null2Int(map.get("width")));
							acc.setHeight(CommUtil.null2Int(map.get("height")));
							acc.setAddTime(new Date());
							this.accessoryService.update(acc);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
			}
			if (StringUtils.isNullOrEmpty(id)) {
				this.advertService.save(advert);
				// 扣除用户金币
				user.setGold(user.getGold() - advert.getAd_gold());
				this.userService.update(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_content("购买广告扣除金币");
				log.setGl_count(advert.getAd_gold());
				log.setGl_money(advert.getAd_gold());
				log.setGl_user(user);
				log.setGl_type(-1);
				this.goldLogService.save(log);
			} else {
				this.advertService.update(advert);
			}
			json.put("ret", true);
			json.put("op_title", "广告申请成功");
			json.put("url", CommUtil.getURL(request) + "/seller/advert_my.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		}
	}

	@SecurityMapping(title = "广告编辑", value = "/seller/advert_apply_edit.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_apply_edit.htm")
	public ModelAndView advert_apply_edit(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/advert_apply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
		String ap_session = CommUtil.randomString(32);
		request.getSession(true).setAttribute("ap_session", ap_session);
		mv.addObject("ap_session", ap_session);
		mv.addObject("ap", obj.getAd_ap());
		mv.addObject("obj", obj);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "我的广告", value = "/seller/advert_my.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_my.htm")
	public ModelAndView advert_my(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/advert_my.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AdvertQueryObject qo = new AdvertQueryObject(currentPage, mv, "addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.ad_user.id", new SysMap("ad_user", user.getId()), "=");
		IPageList pList = this.advertService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "广告延时", value = "/seller/advert_delay.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_delay.htm")
	public ModelAndView advert_delay(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/advert_delay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (obj.getAd_ap().getAp_price() > user.getGold()) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "金币不足，不能申请");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/advert_list.htm");
		} else {
			String delay_session = CommUtil.randomString(32);
			request.getSession(true).setAttribute("delay_session", delay_session);
			mv.addObject("delay_session", delay_session);
			mv.addObject("obj", obj);
			mv.addObject("ap", obj.getAd_ap());
			mv.addObject("user", user);
		}
		return mv;
	}

	@SecurityMapping(title = "失败广告删除", value = "/seller/advert_delete.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_delete.htm")
	public ModelAndView advert_delete(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (obj.getAd_user().getId().toString().equals(user.getId().toString())) {
			if (obj.getAd_status() == -1) {
				this.advertService.delete(obj.getId());
				mv.addObject("op_title", "删除成功");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/advert_my.htm");
			}
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/advert_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "广告购买保存", value = "/seller/advert_delay_save.htm*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping("/seller/advert_delay_save.htm")
	public void advert_delay_save(HttpServletRequest request, HttpServletResponse response, String id, String day,
			String delay_session) {
		Map json = new HashMap();
		json.put("ret", false);
		String delay_session1 = CommUtil.null2String(request.getSession(true).getAttribute("delay_session"));
		if (StringUtils.isNullOrEmpty(delay_session1)) {
			json.put("op_title", "禁止表单重复提交");
			json.put("url", CommUtil.getURL(request) + "/seller/advert_list.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		} else {
			request.getSession(true).removeAttribute("delay_session");
			Advert advert = this.advertService.getObjById(CommUtil.null2Long(id));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int total_gold = advert.getAd_ap().getAp_price() * CommUtil.null2Int(day);
			if (total_gold <= user.getGold()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(advert.getAd_end_time());
				cal.add(Calendar.DAY_OF_YEAR, CommUtil.null2Int(day));
				advert.setAd_end_time(cal.getTime());
				advert.setAd_gold(advert.getAd_gold() + total_gold);
				this.advertService.update(advert);
				// 扣除用户金币
				user.setGold(user.getGold() - total_gold);
				this.userService.update(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_content("广告延时扣除金币");
				log.setGl_count(total_gold);
				log.setGl_user(user);
				log.setGl_type(-1);
				log.setGl_money(advert.getAd_gold());
				this.goldLogService.save(log);
				json.put("ret", true);
				json.put("op_title", "广告延时成功");
				json.put("url", CommUtil.getURL(request) + "/seller/advert_my.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()), response);
			} else {
				json.put("op_title", "金币不足，不能延时");
				json.put("url", CommUtil.getURL(request) + "/seller/advert_delay.htm?id=" + id);
				this.return_json(Json.toJson(json, JsonFormat.compact()), response);
			}
		}
	}

	public void return_json(String json, HttpServletResponse response) {
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
}
