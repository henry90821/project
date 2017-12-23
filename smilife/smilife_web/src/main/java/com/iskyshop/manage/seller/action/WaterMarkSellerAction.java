package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * @info 水印管理控制器，卖家可以管理图片的文字水印、图片水印
 * @since v1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class WaterMarkSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IWaterMarkService watermarkService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private FTPServerTools ftpTools;

	@SecurityMapping(title = "图片水印", value = "/seller/watermark.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/watermark.htm")
	public ModelAndView watermark(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/watermark.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store != null) {
			Map params = new HashMap();
			params.put("store_id", store.getId());
			List<WaterMark> wms = this.watermarkService.query("select obj from WaterMark obj where obj.store.id=:store_id",
					params, -1, -1);
			if (wms.size() > 0) {
				mv.addObject("obj", wms.get(0));
			}
		}
		return mv;
	}

	/**
	 * watermark保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "图片水印保存", value = "/seller/watermark_save.htm*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping("/seller/watermark_save.htm")
	public ModelAndView watermark_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String cmd) {
		ModelAndView mv = null;
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			WebForm wf = new WebForm();
			WaterMark watermark = null;
			if (StringUtils.isNullOrEmpty(id)) {
				watermark = wf.toPo(request, WaterMark.class);
				watermark.setAddTime(new Date());
			} else {
				WaterMark obj = this.watermarkService.getObjById(Long.parseLong(id));
				watermark = (WaterMark) wf.toPo(request, obj);
			}
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			watermark.setStore(SecurityUserHolder.getCurrentUser().getStore());
			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
					+ File.separator + "cache";

			try {
				Map map = CommUtil.saveFileToServer(request, "wm_img", saveFilePathName, null, null);
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory wm_image = new Accessory();
					wm_image.setAddTime(new Date());
					wm_image.setHeight(CommUtil.null2Int(map.get("height")));
					wm_image.setName(CommUtil.null2String(map.get("fileName")));
					wm_image.setPath(
							this.ftpTools.userUpload(wm_image.getName(), "/wm", CommUtil.null2String(user.getId())));
					wm_image.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					wm_image.setUser(SecurityUserHolder.getCurrentUser());
					wm_image.setWidth(CommUtil.null2Int("width"));
					this.accessoryService.save(wm_image);
					watermark.setWm_image(wm_image);
				}
			} catch (IOException e) {
				logger.error(e);
			}
			if (StringUtils.isNullOrEmpty(id)) {
				this.watermarkService.save(watermark);
			} else {
				this.watermarkService.update(watermark);
			}
			mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "水印设置成功");
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您尚未开店");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/seller/watermark.htm");
		return mv;
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