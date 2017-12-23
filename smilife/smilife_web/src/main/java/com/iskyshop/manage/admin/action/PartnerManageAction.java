package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.domain.query.PartnerQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IPartnerService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

@Controller
public class PartnerManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FTPServerTools ftpTools;

	/**
	 * Partner列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴列表", value = "/admin/partner_list.htm*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping("/admin/partner_list.htm")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String title) {
		ModelAndView mv = new JModelAndView("admin/blue/partner_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PartnerQueryObject qo = new PartnerQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(title)) {
			qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"), "like");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Partner.class, mv);
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.partnerService.list(qo);
		String url = CommUtil.getURL(request);
		
		if (!StringUtils.isNullOrEmpty(title)) {
			CommUtil.saveIPageList2ModelAndView(url + "/admin/partner_list.htm", "", "title=" + title, pList, mv);
		} else {
			CommUtil.saveIPageList2ModelAndView(url + "/admin/partner_list.htm", "", "", pList, mv);
		}

		return mv;
	}

	/**
	 * partner添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "合作伙伴添加", value = "/admin/partner_add.htm*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping("/admin/partner_add.htm")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/partner_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * partner编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "合作伙伴编辑", value = "/admin/partner_edit.htm*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping("/admin/partner_edit.htm")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/partner_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			Partner partner = this.partnerService.getObjById(Long.parseLong(id));
			mv.addObject("obj", partner);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * partner保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴保存", value = "/admin/partner_save.htm*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping("/admin/partner_save.htm")
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String list_url,
			String add_url) {
		WebForm wf = new WebForm();
		Partner partner = null;
		if (StringUtils.isNullOrEmpty(id)) {
			partner = wf.toPo(request, Partner.class);
			partner.setAddTime(new Date());
		} else {
			Partner obj = this.partnerService.getObjById(Long.parseLong(id));
			partner = (Partner) wf.toPo(request, obj);
		}
		// 标识图片
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = new HashMap();
		try {
			String fileName = partner.getImage() == null ? "" : partner.getImage().getName();
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName, fileName, null);
			if (StringUtils.isNullOrEmpty(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/partner"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					partner.setImage(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = partner.getImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/partner"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}

		if (StringUtils.isNullOrEmpty(id)) {
			this.partnerService.save(partner);
		} else {
			this.partnerService.update(partner);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存合作伙伴成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "合作伙伴删除", value = "/admin/partner_del.htm*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping("/admin/partner_del.htm")
	public String delete(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Partner partner = this.partnerService.getObjById(Long.parseLong(id));
				try {
					this.ftpTools.systemDeleteFtpImg(partner.getImage());
				} catch (IOException e) {
					logger.error(e);
				}
				this.partnerService.delete(Long.parseLong(id));
			}
		}
		return "redirect:partner_list.htm";
	}

}