package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreSlide;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreSlideService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.view.web.tools.AreaViewTools;

/**
 * 
 * <p>
 * Title: StoreSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心店铺控制器
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
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class StoreSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreSlideService storeSlideService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private FTPServerTools ftpTools;

	@SecurityMapping(title = "店铺设置", value = "/seller/store_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_set.htm")
	public ModelAndView store_set(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/store_set.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		mv.addObject("areaViewTools", areaViewTools);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		if (user.getStore().getGc_detail_info() != null) {// 店铺详细类目
			Set<GoodsClass> detail_gcs = this.storeTools.query_store_DetailGc(user.getStore().getGc_detail_info());
			mv.addObject("detail_gcs", detail_gcs);
		}
		GoodsClass main_gc = this.goodsClassService.getObjById(user.getStore().getGc_main_id());// 店铺主营类目
		mv.addObject("main_gc", main_gc);
		return mv;
	}

	@SecurityMapping(title = "店铺设置保存", value = "/seller/store_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_set_save.htm")
	public void store_set_save(HttpServletRequest request, HttpServletResponse response, String area_id,
			String store_second_domain, String mobile) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		WebForm wf = new WebForm();
		wf.toPo(request, store);
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		Map map = null;
		try {
			map = CommUtil.saveFileToServer(request, "logo", saveFilePathName, null, null);
			if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
				Accessory store_logo = store.getStore_logo();
				if(store_logo == null) {
					store_logo = new Accessory();
					store_logo.setAddTime(new Date());
				}
				store_logo.setName(CommUtil.null2String(map.get("fileName")));
				store_logo.setExt(CommUtil.null2String(map.get("mime")));
				store_logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
				store_logo.setPath(this.ftpTools.userUpload(store_logo.getName(), "/store_logo", CommUtil.null2String(user.getId())));
				store_logo.setWidth(CommUtil.null2Int(map.get("width")));
				store_logo.setHeight(CommUtil.null2Int(map.get("height")));
				
				if(store.getStore_logo() == null) {
					this.accessoryService.save(store_logo);
					store.setStore_logo(store_logo);
				} else {
					this.accessoryService.update(store_logo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
		

		try {
			map = CommUtil.saveFileToServer(request, "banner", saveFilePathName, null, null);
			if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
				Accessory store_banner = store.getStore_banner();
				if(store_banner == null) {
					store_banner = new Accessory();
					store_banner.setAddTime(new Date());
				}
				store_banner.setName(CommUtil.null2String(map.get("fileName")));
				store_banner.setExt(CommUtil.null2String(map.get("mime")));
				store_banner.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
				store_banner.setPath(this.ftpTools.userUpload(store_banner.getName(), "/store_banner", CommUtil.null2String(user.getId())));
				store_banner.setWidth(CommUtil.null2Int(map.get("width")));
				store_banner.setHeight(CommUtil.null2Int(map.get("height")));
				
				if(store.getStore_banner() == null) {
					this.accessoryService.save(store_banner);
					store.setStore_banner(store_banner);
				} else {
					this.accessoryService.update(store_banner);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		store.setArea(area);
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& this.configService.getSysConfig().getDomain_allow_count() > store.getDomain_modify_count()) {
			if (!StringUtils.isNullOrEmpty(store_second_domain)
					&& !store_second_domain.equals(store.getStore_second_domain())) {
				store.setStore_second_domain(store_second_domain);
				store.setDomain_modify_count(store.getDomain_modify_count() + 1);
			}
		}
		this.storeService.update(store);
		if (!StringUtils.isNullOrEmpty(mobile)) {
			user.setMobile(mobile);
			this.userService.update(user);
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "店铺设置成功");
		json.put("url", CommUtil.getURL(request) + "/seller/store_set.htm");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 店铺二级域名申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名申请", value = "/seller/store_sld.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld.htm")
	public ModelAndView store_sld(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/store_sld.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sysconfig = this.configService.getSysConfig();
		String serverName = request.getServerName();
		if ("localhost".equals(serverName) || CommUtil.isIp(serverName) || serverName.indexOf("www.") > 0) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "系统未绑定顶级域名，无法设定二级域名");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			if (sysconfig.isSecond_domain_open()) {
				User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				String store_second_domain = "";
				if (!StringUtils.isNullOrEmpty(store.getStore_second_domain())) {
					store_second_domain = store.getStore_second_domain() + "." + CommUtil.generic_domain(request);
				} else {
					store_second_domain = user.getUsername() + "." + CommUtil.generic_domain(request);
				}
				mv.addObject("store_second_domain", store_second_domain.split("\\.")[0]);
				mv.addObject("store", store);
				mv.addObject("serverName", serverName.substring(4));
			} else {
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "系统未开启二级域名");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			}
		}

		return mv;
	}

	/**
	 * 二级域名申请保存成功
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "二级域名申请保存", value = "/seller/store_sld_save.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld_save.htm")
	public String store_sld_save(HttpServletRequest request, HttpServletResponse response, String store_second_domain) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		WebForm wf = new WebForm();
		wf.toPo(request, store);
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& this.configService.getSysConfig().getDomain_allow_count() > store.getDomain_modify_count()) {
			if (!StringUtils.isNullOrEmpty(store_second_domain)
					&& !store_second_domain.equals(store.getStore_second_domain())) {
				store.setStore_second_domain(store_second_domain.toLowerCase());
				store.setDomain_modify_count(store.getDomain_modify_count() + 1);
			}
		}
		this.storeService.update(store);
		request.getSession(true).setAttribute("url", CommUtil.getURL(request) + "/seller/store_sld.htm");
		request.getSession(true).setAttribute("op_title", "店铺二级域名设置成功");
		return "redirect:/seller/success.htm";
	}

	/**
	 * 店铺二级域名验证
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名验证", value = "/seller/store_sld_verify.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld_verify.htm")
	public void store_sld_verify(HttpServletRequest request, HttpServletResponse response, String sld_name, String store_id)
			throws IOException {
		SysConfig sysconfig = this.configService.getSysConfig();
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		for (String domain : CommUtil.str2list(sysconfig.getSys_domain())) {// 系统限定域名不可以绑定
			if (domain.equals(sld_name)) {
				ret = false;
				break;
			}
		}
		if (sysconfig.getDomain_allow_count() > 0 && store.getDomain_modify_count() >= sysconfig.getDomain_allow_count()) {// 超过系统允许修改的次数，不允许修改
			ret = false;
		}
		Map params = new HashMap();
		params.put("store_second_domain", CommUtil.null2String(sld_name));
		params.put("store_id", CommUtil.null2Long(store_id));
		List<Store> stores = this.storeService.query(
				"select obj from Store obj where obj.store_second_domain=:store_second_domain and obj.id!=:store_id", params,
				-1, -1);
		if (stores.size() > 0) {
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

	@SecurityMapping(title = "店铺幻灯", value = "/seller/store_slide.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide.htm")
	public ModelAndView store_slide(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/store_slide.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("sid", store.getId());
		params.put("slide_type", 0);
		List<StoreSlide> slides = this.storeSlideService.query(
				"select obj from StoreSlide obj where obj.store.id=:sid and obj.slide_type=:slide_type", params, -1, -1);
		mv.addObject("slides", slides);
		return mv;
	}

	@SecurityMapping(title = "店铺幻灯上传窗口", value = "/seller/store_slide_upload.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide_upload.htm")
	public ModelAndView store_slide_upload(HttpServletRequest request, HttpServletResponse response, String index) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/store_slide_upload.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		mv.addObject("index", CommUtil.null2Int(index));
		return mv;
	}

	@SecurityMapping(title = "店铺幻灯保存", value = "/seller/store_slide_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide_save.htm")
	public void store_slide_save(HttpServletRequest request, HttpServletResponse response, String index) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		if (!CommUtil.fileExist(saveFilePathName)) {
			CommUtil.createFolder(saveFilePathName);
		}
		Map map = new HashMap();
		String fileName = "";
		StoreSlide slide = null;
		Map params = new HashMap();
		params.put("sid", store.getId());
		params.put("slide_type", 0);
		List<StoreSlide> slides = this.storeSlideService.query(
				"select obj from StoreSlide obj where obj.store.id=:sid and obj.slide_type=:slide_type", params, -1, -1);
		if (slides.size() >= CommUtil.null2Int(index)) {
			fileName = slides.get(CommUtil.null2Int(index) - 1).getAcc().getName();
			slide = slides.get(CommUtil.null2Int(index) - 1);
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
					acc.setPath(this.ftpTools.userUpload(acc.getName(), "/store_slide", CommUtil.null2String(user.getId())));
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.save(acc);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					acc = slide.getAcc();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(this.ftpTools.userUpload(acc.getName(), "/store_slide", CommUtil.null2String(user.getId())));
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.update(acc);
				}
			}
			if (acc != null) {
				if (slide == null) {
					slide = new StoreSlide();
					slide.setAcc(acc);
					slide.setAddTime(new Date());
					slide.setStore(store);
					slide.setSlide_type(0);
					slide.setUrl(request.getParameter("acc_url"));
					this.storeSlideService.save(slide);
				} else {
					slide.setAcc(acc);
					slide.setUrl(request.getParameter("acc_url"));
					this.storeSlideService.update(slide);
				}
			} else {
				if (slide != null) {
					slide.setUrl(request.getParameter("acc_url"));
					this.storeSlideService.update(slide);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "店铺幻灯设置成功");
		json.put("url", CommUtil.getURL(request) + "/seller/store_slide.htm");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "店铺幻灯删除", value = "/seller/store_slide_del.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide_del.htm")
	public void store_slide_del(HttpServletRequest request, HttpServletResponse response, Integer index) {
		String retMsg = null;
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();

		Map params = new HashMap();
		params.put("sid", store.getId());
		params.put("slide_type", 0);
		List<StoreSlide> slides = this.storeSlideService.query(
				"select obj from StoreSlide obj where obj.store.id=:sid and obj.slide_type=:slide_type", params, -1, -1);
		
		boolean flag = false;
		if(index != null && slides.size() >= index && index > 0) {
			flag = this.storeSlideService.delete(slides.get(index - 1).getId());
			if(flag) {
				retMsg = "删除店铺幻灯片成功";
			} else {
				retMsg = "删除店铺幻灯片失败";
			}			
		} else {
			retMsg = "未找到要删除的幻灯片(index = " + index + ")，删除幻灯片失败"; 
		}
		
		Map json = new HashMap();
		json.put("ret", flag);
		json.put("retmsg", retMsg);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
}
