package com.iskyshop.module.weixin.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.view.web.tools.AreaViewTools;

/**
 * 
 * 
 * <p>
 * Title:MobileUserAddressAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心地址管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jy
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinUserAddressAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaViewTools areaViewTools;

	/**
	 * 买家中心地址管理
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "收货地址管理", value = "/wap/buyer/address.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address.htm")
	public ModelAndView address(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/address.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addresses = this.addressService.query("select obj from Address obj where obj.user.id =:user_id",
				params, -1, -1);
		mv.addObject("addrs", addresses);
		mv.addObject("areaViewTools", areaViewTools);
		String returnUrl = CommUtil.getWebPath(request, configService.getSysConfig()) + "/wap/buyer/address.htm";
		mv.addObject("returnUrl", URLEncoder.encode(returnUrl));
		return mv;
	}

	/**
	 * 买家中心新增地址
	 * 
	 * @param request
	 * @param response
	 * @param returnUrl 为成功添加完地址后应跳转到的URL，此参数应进行了url编码
	 * @return
	 */
	@SecurityMapping(title = "新增收货地址", value = "/wap/buyer/address_add.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_add", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_add.htm")
	public ModelAndView address_add(HttpServletRequest request, HttpServletResponse response, String returnUrl, Long needId) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		// common为true时，用户添加地址时可以默认选择；20150703
		Map params = new HashMap();
		params.put("common", true);
		List<Area> bjs = this.areaService.query(
				"select new Area(id,addTime) from Area obj where obj.common = :common order by obj.sequence asc", params, 0,
				1);
		if (bjs.size() > 0) {
			mv.addObject("bj", this.areaService.getObjById(bjs.get(0).getId()));
		} else {
			params.clear();
			params.put("areaName", "东城区");
			List<Area> bjs2 = this.areaService.query("select obj from Area obj where obj.areaName = :areaName", params, 0,
					1);
			mv.addObject("bj", bjs2.size() > 0 ? bjs2.get(0) : null);
		}
		mv.addObject("areaViewTools", areaViewTools);
		mv.addObject("needId", needId);
		mv.addObject("returnUrl", returnUrl);
		return mv;
	}

	/**
	 * 买家中心编辑地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "编辑收货地址", value = "/wap/buyer/address_edit.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_edit", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_edit.htm")
	public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String type, Long needId, String returnUrl) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("areaViewTools", areaViewTools);
		mv.addObject("type", type);
		mv.addObject("needId", needId);
		
		mv.addObject("returnUrl", returnUrl);
		return mv;
	}

	/**
	 * 买家中心地址保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/wap/buyer/address_save.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_save", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_save.htm")
	public ModelAndView address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id,
			String currentPage, String returnUrl) {
		ModelAndView mv = new JModelAndView("wap/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		//area_id 不能为空，如果为空，则跳转到首页面
        if(StringUtils.isNullOrEmpty(area_id)){
        	mv.addObject("op_title", "地址id为空");
        	mv.addObject("url", CommUtil.getURL(request)+"/wap/index.htm");
    		return mv;
		}
        
		WebForm wf = new WebForm();
		Address address = null;
		if (StringUtils.isNullOrEmpty(id)) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		
		address.setLatitude(null);// 修改地址后要重新计算经纬度
		address.setLongitude(null);
		
		if (StringUtils.isNullOrEmpty(id)) {
			List<Address> list = this.addressService.query("select obj from Address obj where obj.user.id = "+SecurityUserHolder.getCurrentUser().getId(), null, -1, -1);
			if(list.size()==0){
				address.setDefault_val(1);
			}
			this.addressService.save(address);
		} else {
			this.addressService.update(address);
		}
		mv.addObject("op_title", "保存成功");
		
		mv.addObject("url", URLDecoder.decode(returnUrl));
		
		return mv;
	}

	/**
	 * 买家中心地址删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "收货地址删除", value = "/wap/buyer/address_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_del.htm")
	public String address_del(HttpServletRequest request, HttpServletResponse response, String id) {
		Address address = this.addressService.getObjById(CommUtil.null2Long(id));
		if (address != null && address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.addressService.delete(Long.parseLong(id));
			List<Address> list = this.addressService.query("select obj from Address obj where obj.user.id = "+
					SecurityUserHolder.getCurrentUser().getId()+"ORDER BY obj.addTime DESC", null, -1, -1);
					for (Address address2 : list) {
						if(address2.getDefault_val()!=1){
							address2.setDefault_val(1);
							this.addressService.update(address2);
							break;
						}
					}
		}
		return "redirect:/wap/buyer/address.htm";
	}

	@SecurityMapping(title = "收货地址删除", value = "/wap/buyer/ajax_address_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/ajax_address_del.htm")
	public String ajax_address_del(HttpServletRequest request, HttpServletResponse response, String id) {
		Address address = this.addressService.getObjById(CommUtil.null2Long(id));
		if (address != null && address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.addressService.delete(Long.parseLong(id));
		}
		return "success";
	}
	
	@SecurityMapping(title = "收货地址删除2", value = "/wap/buyer/ajax_address_del2.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/ajax_address_del2.htm")
	public void ajax_address_del2(HttpServletRequest request, HttpServletResponse response, String id) {
		Address address = this.addressService.getObjById(CommUtil.null2Long(id));
		if (address != null && address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.addressService.delete(Long.parseLong(id));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/wap/address_add_ajax.htm")
	public void address_add_ajax(HttpServletRequest request, HttpServletResponse response, String aid) {
		Area area = this.areaService.getObjById(CommUtil.null2Long(aid));
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		if(area == null) {
			json_map.put("info", "您选择的地址在后台不存在");
		} else if (area.getLevel() != 2) {
			json_map.put("level", true);
			List<Area> childs = area.getChilds();
			for (Area child : childs) {
				Map map = new HashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
		} else {
			json_map.put("level", false);
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			for (Area child : areas) {
				Map map = new HashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
			json_map.put("info", areaViewTools.generic_area_info(aid));
			json_map.put("aid", aid);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
