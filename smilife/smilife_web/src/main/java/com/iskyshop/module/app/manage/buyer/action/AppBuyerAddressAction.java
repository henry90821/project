package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MobileBuyAddressManageAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端用户中心用户收货地址管理控制器
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
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppBuyerAddressAction {

	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;

	/**
	 * 手机端买家收货地址列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/buyer/address.htm")
	public void buyer_address(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		List<Address> addrs = this.addressService
				.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
						params, -1, -1);
		int default_mark = 0;
		for (Address addr : addrs) {
			if (addr.getDefault_val() == 1) {
				default_mark++;
			}
			Map map = new HashMap();
			map.put("addr_id", addr.getId());
			if (addr.getDefault_val() == 1) {
				map.put("default", true);
			}
			map.put("trueName", addr.getTrueName());
			map.put("areaInfo", addr.getArea_info());
			map.put("zip", addr.getZip());
			map.put("mobile", addr.getMobile());
			map.put("telephone", addr.getTelephone());
			map.put("area", addr.getArea().getParent().getParent().getAreaName() + ","
							+ addr.getArea().getParent().getAreaName() + ","
							+ addr.getArea().getAreaName());
			map_list.add(map);
		}
		if (default_mark == 0 && addrs.size() > 0) {
			addrs.get(0).setDefault_val(1);
			this.addressService.update(addrs.get(0));
		}
		json_map.put("address_list", map_list);
		json_map.put("verify", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机端买家收货地址新增
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/buyer/address_save.htm")
	public void buyer_address_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String trueName, String zip, String telephone, String mobile,
			String area_id, String area_info, String addr_id) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		Address address = new Address();
		boolean edit = false;
		if (!StringUtils.isNullOrEmpty(addr_id)) {
			address = this.addressService.getObjById(CommUtil
					.null2Long(addr_id));
			edit = true;
		} else {
			edit = false;
		}
		address.setAddTime(new Date());
		address.setTrueName(trueName);
		address.setArea(area);
		address.setArea_info(area_info);
		address.setMobile(mobile);
		address.setTelephone(telephone);
		address.setUser(user);
		address.setZip(zip);
		this.addressService.save(address);
		Map map = new HashMap();
		map.put("addr_id", address.getId());
		map.put("trueName", address.getTrueName());
		map.put("areaInfo", address.getArea_info());
		map.put("zip", address.getZip());
		map.put("mobile", address.getMobile());
		map.put("telephone", address.getTelephone());
		map.put("area", address.getArea().getParent().getParent().getAreaName()
				+ address.getArea().getParent().getAreaName()
				+ address.getArea().getAreaName());
		map_list.add(map);
		json_map.put("edit", edit);
		json_map.put("verify", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 手机端买家收货地址设置默认地址，设置默认地址后，手机端下单收货地址栏默认显示默认收货地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/buyer/address_default_set.htm")
	public void buyer_address_default_set(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String addr_id) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map params = new HashMap();
		params.put("uid", CommUtil.null2Long(user_id));
		List<Address> objs = this.addressService
				.query("select obj from Address obj where obj.user.id=:uid order by addTime desc",
						params, -1, -1);
		int default_mark = 0;
		for (Address obj : objs) {
			if (CommUtil.null2Long(addr_id).equals(obj.getId())) {
				obj.setDefault_val(1);
				default_mark++;
			} else {
				obj.setDefault_val(0);
			}
			this.addressService.update(obj);
		}
		if (default_mark > 0) {
			json_map.put("verify", true);
		} else {
			json_map.put("verify", false);
		}
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 手机端买家默认收货地址查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/buyer/address_default.htm")
	public void buyer_address_default(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map params = new HashMap();
		params.put("uid", CommUtil.null2Long(user_id));
		params.put("default_val", 1);
		List<Address> objs = this.addressService
				.query("select obj from Address obj where obj.user.id=:uid and obj.default_val=:default_val order by addTime desc",
						params, -1, -1);
		if (objs.size() == 1) {
			json_map.put("addr_id", objs.get(0).getId());
			json_map.put("trueName", objs.get(0).getTrueName());
			json_map.put("areaInfo", objs.get(0).getArea_info());
			json_map.put("zip", objs.get(0).getZip());
			json_map.put("mobile", objs.get(0).getMobile());
			json_map.put("telephone", objs.get(0).getTelephone());
			json_map.put("area", objs.get(0).getArea().getParent().getParent()
					.getAreaName()
					+ ","
					+ objs.get(0).getArea().getParent().getAreaName()
					+ "," + objs.get(0).getArea().getAreaName());
		}
		json_map.put("verify", true);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 手机端买家收货地址删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/buyer/address_delete.htm")
	public void buyer_address_delete(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String addr_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user.getApp_login_token().equals(token.toLowerCase())) {
			 this.addressService.delete(CommUtil.null2Long(addr_id));
			json_map.put("verify", true);
		} else {
			json_map.put("verify", false);
		}
		String json = Json.toJson(json_map, JsonFormat.compact());
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
