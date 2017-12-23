package com.iskyshop.module.cms.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.module.cms.domain.Information;
import com.iskyshop.module.cms.domain.InformationClass;
import com.iskyshop.module.cms.domain.query.InformationQueryObject;
import com.iskyshop.module.cms.service.IInformationClassService;
import com.iskyshop.module.cms.service.IInformationService;
import com.iskyshop.module.cms.view.tools.CmsTools;

/**
 * 
 * <p>
 * Title: InformationManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯管理；发布，提交审核
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
 * @author lixiaoyang
 * 
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class InformationSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IInformationService informationService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IInformationClassService informationClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private CmsTools cmsTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private FTPServerTools ftpTools;

	/**
	 * Information列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "资讯列表", value = "/seller/information_list.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_list.htm")
	public ModelAndView information_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String title, String author) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/information_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		InformationQueryObject qo = new InformationQueryObject(currentPage, mv, "addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		if (!StringUtils.isNullOrEmpty(title)) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"), "LIKE");
			mv.addObject("title", title);
		}
		if (!StringUtils.isNullOrEmpty(author)) {
			qo.addQuery("obj.author", new SysMap("author", "%" + author + "%"), "LIKE");
			mv.addObject("author", author);
		}
		IPageList pList = this.informationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<InformationClass> infoclass = this.informationClassService.query("select obj from InformationClass obj", null,
				-1, -1);
		Map map = new HashMap();
		for (InformationClass informationClass : infoclass) {
			map.put(informationClass.getId(), informationClass.getIc_name());
		}
		mv.addObject("classmap", map);
		return mv;
	}

	/**
	 * information添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯添加", value = "/seller/information_add.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_add.htm")
	public ModelAndView information_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/information_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		List<InformationClass> infoclass = this.informationClassService
				.query("select obj from InformationClass obj where obj.ic_pid is null", null, -1, -1);
		mv.addObject("cmsTools", cmsTools);
		mv.addObject("infoclass", infoclass);
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(true).setAttribute("goods_session", goods_session);
		return mv;
	}

	/**
	 * information编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "资讯编辑", value = "/seller/information_edit.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_edit.htm")
	public ModelAndView information_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/information_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			Information information = this.informationService.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			
			//begin dengyuqi 2015-9-6 修复编辑资讯时页面空白的bug
//			if (information.getStore_id() == store.getId()) {
			if (information.getStore_id().intValue() == store.getId().intValue()) {
			//end
				
				mv.addObject("obj", information);
				mv.addObject("edit", true);
			}
			mv.addObject("currentPage", currentPage);
			List<InformationClass> infoclass = this.informationClassService
					.query("select obj from InformationClass obj where obj.ic_pid is null", null, -1, -1);
			mv.addObject("cmsTools", cmsTools);
			mv.addObject("imageTools", imageTools);
			mv.addObject("infoclass", infoclass);
		}
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(true).setAttribute("goods_session", goods_session);
		return mv;
	}

	/**
	 * information保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯保存", value = "/seller/information_save.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_save.htm")
	public ModelAndView information_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String cmd, String list_url, String add_url, String goods_session) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(true).getAttribute("goods_session"));
		if (StringUtils.isNullOrEmpty(goods_session1)) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/information_list.htm");
		} else {
			if (goods_session1.equals(goods_session)) {
				WebForm wf = new WebForm();
				Information information = null;
				User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (StringUtils.isNullOrEmpty(id)) {
					information = wf.toPo(request, Information.class);
					information.setAddTime(new Date());
					information.setAuthor(user.getUsername());
					information.setAuthor_id(user.getId());
					information.setStore_id(store.getId());
					information.setStore(store.getStore_name());
					information.setType(1);
					information.setStatus(0);
				} else {
					information = this.informationService.getObjById(Long.parseLong(id));
					if (information.getStore_id() == store.getId()) {
						information = (Information) wf.toPo(request, information);
					} else {
						information = wf.toPo(request, Information.class);
						information.setAddTime(new Date());
						information.setAuthor(user.getUsername());
						information.setAuthor_id(user.getId());
						information.setStore_id(store.getId());
						information.setStore(store.getStore_name());
						information.setType(1);
						information.setStatus(0);
					}
				}
				// 封面图片
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
						+ File.separator + "cache";

				Map map = new HashMap();
				try {
					String fileName = "";
					Accessory photo = null;
					if (information.getCover() != null && information.getCover() != 0) {
						photo = this.accessoryService.getObjById(information.getCover());
						fileName = photo.getName();
					}

					map = CommUtil.saveFileToServer(request, "cover", saveFilePathName, fileName, null);

					if (StringUtils.isNullOrEmpty(fileName)) {
						if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
							photo = new Accessory();
							photo.setName(CommUtil.null2String(map.get("fileName")));
							photo.setExt(CommUtil.null2String(map.get("mime")));
							photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
							photo.setPath(
									this.ftpTools.userUpload(photo.getName(), "/cover", CommUtil.null2String(user.getId())));

							// 使用com.iskyshop.manage.ftp.tools.FTPServerTools的userUpload方法将图片上传至Ftp服务器并且返回服务器路径，保存到图片路径（path）中，userUpload有三个参数，第一个为图片名称，第二个为图片存在Ftp服务器中的文件夹名字,第三个参数为当前用户id，
							photo.setWidth(CommUtil.null2Int(map.get("width")));
							photo.setHeight(CommUtil.null2Int(map.get("height")));
							photo.setAddTime(new Date());
							photo.setUser(user);
							this.accessoryService.save(photo);
							information.setCover(photo.getId());
						}
					} else {
						if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {

							photo.setName(CommUtil.null2String(map.get("fileName")));
							photo.setExt(CommUtil.null2String(map.get("mime")));
							photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
							photo.setPath(
									this.ftpTools.userUpload(photo.getName(), "/cover", CommUtil.null2String(user.getId())));
							photo.setWidth(CommUtil.null2Int(map.get("width")));
							photo.setHeight(CommUtil.null2Int(map.get("height")));
							photo.setAddTime(new Date());
							photo.setUser(user);
							this.accessoryService.update(photo);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (StringUtils.isNullOrEmpty(id)) {
					this.informationService.save(information);
				} else {
					this.informationService.update(information);
				}
				mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "保存资讯成功");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/information_list.htm");
				request.getSession(true).removeAttribute("goods_session");
			} else {
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "参数错误");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
			}
		}
		return mv;
	}

	@SecurityMapping(title = "资讯提交审核", value = "/seller/information_apply.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_apply.htm")
	public String information_apply(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		Information information = null;
		if (!StringUtils.isNullOrEmpty(id)) {
			information = this.informationService.getObjById(CommUtil.null2Long(id));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			
			//begin dengyuqi 2015-9-6 修复咨询无法提交审批bug
//			if (information.getStore_id() == store.getId()) {
			if (information.getStore_id().intValue() == store.getId().intValue()) {
			//end
				
				information.setStatus(10);
				this.informationService.update(information);
			}
		}
		return "redirect:information_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯删除", value = "/seller/information_del.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_del.htm")
	public String information_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Information information = this.informationService.getObjById(Long.parseLong(id));
				User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (information.getStore_id() == store.getId()) {
					this.informationService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:information_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "资讯商品", value = "/seller/information_goods.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_goods.htm")
	public ModelAndView information_goods(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String gc_id, String goods_name) {
		ModelAndView mv = new JModelAndView("admin/blue/information_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()), "=");
		qo.setPageSize(5);
		if (!StringUtils.isNullOrEmpty(gc_id)) {
			Set<Long> ids = this.genericIds(this.goodsClassService.getObjById(CommUtil.null2Long(gc_id)));
			Map paras = new HashMap();
			paras.put("ids", ids);
			qo.addQuery("obj.gc.id in (:ids)", paras);
			mv.addObject("gc_id", gc_id);
		}
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		IPageList pList = this.goodsService.list(qo);
		String photo_url = CommUtil.getURL(request) + "/admin/information_goods.htm";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url, "", pList.getCurrentPage(), pList.getPages()));
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "商品图片", value = "/seller/information_goods_imgs.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/information_goods_imgs.htm")
	public ModelAndView information_goods_imgs(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String currentPage, String gc_id, String goods_name) {
		ModelAndView mv = new JModelAndView("admin/blue/information_goods_imgs.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		List list = new ArrayList();
		if (goods.getGoods_main_photo() != null) {
			list.add(goods.getGoods_main_photo());
			list.addAll(goods.getGoods_photos());
		}
		mv.addObject("photos", list);
		mv.addObject("goods_id", goods_id);
		return mv;
	}

	@SecurityMapping(title = "资讯预览", value = "/seller/info_preview.htm*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping("/seller/info_preview.htm")
	public ModelAndView info_preview(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("/cms/detail.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information info = this.informationService.getObjById(CommUtil.null2Long(id));
		if (info != null) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (info.getStore_id().equals(user.getStore().getId())) {
				mv.addObject("obj", info);
				mv.addObject("className", this.informationClassService.getObjById(info.getClassid()).getIc_name());
				Map map = new HashMap();
				map.put("addTime", info.getAddTime());
				List<Information> before = this.informationService.query(
						"select obj from Information obj where obj.addTime>:addTime and obj.status=20 order by addTime asc",
						map, 0, 1);
				if (before.size() > 0) {
					mv.addObject("before", before.get(0));
				}
				List<Information> after = this.informationService.query(
						"select obj from Information obj where obj.addTime<:addTime and obj.status=20 order by addTime desc",
						map, 0, 1);
				if (after.size() > 0) {
					mv.addObject("after", after.get(0));
				}
				List<InformationClass> infoclass = this.informationClassService
						.query("select obj from InformationClass obj order by ic_sequence asc", null, -1, -1);
				mv.addObject("infoclass", infoclass);
				List<Information> hotinfo = this.informationService
						.query("select obj from Information obj where obj.status=20 order by sequence asc", null, 0, 5);
				mv.addObject("hotinfo", hotinfo);
				mv.addObject("imageTools", imageTools);
			} else {
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "参数不正确");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/information_list.htm");
			}

		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "参数不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/information_list.htm");
		}

		return mv;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}