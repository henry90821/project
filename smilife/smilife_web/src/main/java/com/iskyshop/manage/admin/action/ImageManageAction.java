package com.iskyshop.manage.admin.action;

import java.io.IOException;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: ImageManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台图片管理控制器，删除数据的同时也删除物理文件
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
 * @author jxz
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class ImageManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private ImageTools imageTools;

	@SecurityMapping(title = "会员相册列表", value = "/admin/user_photo_list.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_photo_list.htm")
	public ModelAndView user_album_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/photo_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AlbumQueryObject qo = new AlbumQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(store_name)) {
			qo.addQuery("obj.user.store.store_name", new SysMap("store_store_name", "%" + store_name.trim() + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		qo.addQuery("obj.user.userRole", new SysMap("user_userRole", "ADMIN"), "!=");
		qo.setPageSize(15);
		IPageList pList = this.albumService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ImageTools", imageTools);
		return mv;
	}

	@SecurityMapping(title = "会员相册删除", value = "/admin/user_photo_del.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_photo_del.htm")
	public String user_album_del(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Album album = this.albumService.getObjById(CommUtil.null2Long(id));
				Map params = new HashMap();
				params.put("album_id", album.getId());
				List<Accessory> accs = this.accessoryService
						.query("select obj from Accessory obj where obj.album_id=:album_id", params, -1, -1);
				for (Accessory acc : accs) {
					try {
						this.ftpTools.systemDeleteFtpImg(acc);
					} catch (IOException e) {
						logger.error(e);
					}
					for (Goods goods : acc.getGoods_main_list()) {
						goods.setGoods_main_photo(null);
						this.goodsService.update(goods);
					}
					for (Goods goods1 : acc.getGoods_list()) {
						goods1.getGoods_photos().remove(acc);
						this.goodsService.update(goods1);
					}
					this.accessoryService.delete(acc.getId());
				}
				this.albumService.delete(Long.parseLong(id));
			}
		}
		String url = "redirect:/admin/user_photo_list.htm?currentPage=" + currentPage;
		return url;
	}

	@SecurityMapping(title = "会员相册图片列表", value = "/admin/user_pic_list.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_pic_list.htm")
	public ModelAndView user_pic_list(HttpServletRequest request, HttpServletResponse response, String aid,
			String currentPage, String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/pic_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject qo = new AccessoryQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.album.id", new SysMap("obj_album_id", CommUtil.null2Long(aid)), "=");
		qo.setPageSize(50);
		IPageList pList = this.accessoryService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		Album album = this.albumService.getObjById(CommUtil.null2Long(aid));
		mv.addObject("album", album);
		return mv;
	}

	/**
	 * 会员相册图片删除，删除数据的同时删除服务器上的图片资源
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "会员相册图片删除", value = "/admin/user_pic_del.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_pic_del.htm")
	public String user_pic_del(HttpServletRequest request, HttpServletResponse response, String currentPage, String mulitId,
			String aid) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			boolean flag = false;
			Accessory obj = this.accessoryService.getObjById(CommUtil.null2Long(id));
			for (Goods goods : obj.getGoods_list()) {
				if (goods.getGoods_main_photo().getId().equals(obj.getId())) {
					goods.setGoods_main_photo(null);
					this.goodsService.update(goods);
				}
				goods.getGoods_photos().remove(obj);
			}
			flag = this.accessoryService.delete(CommUtil.null2Long(id));
			if (flag) {
				try {
					this.ftpTools.systemDeleteFtpImg(obj);
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		String url = "redirect:/admin/user_pic_list.htm?currentPage=" + currentPage + "&aid=" + aid;
		return url;
	}
}
