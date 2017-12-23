package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IShipAddressService;

/**
 * @info 系统ajax数据加载控制器
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class LoadAction {
	
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGroupAreaService groupAreaService;
	@Autowired
	private IShipAddressService shipAddressService;

	/**
	 * 根据父id加载下级区域，返回json格式数据，这里只返回id和areaName，根据需要可以修改返回数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping("/load_area.htm")
	public void load_area(HttpServletRequest request, HttpServletResponse response, String pid) {
		Map params = new HashMap();
		params.put("pid", CommUtil.null2Long(pid));
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id=:pid", params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Area area : areas) {
			Map map = new HashMap();
			map.put("id", area.getId());
			map.put("areaName", area.getAreaName());
			list.add(map);
		}
		String temp = Json.toJson(list, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@RequestMapping("/load_group_area.htm")
	public void load_group_area(HttpServletRequest request, HttpServletResponse response, String pid) {
		Map params = new HashMap();
		params.put("pid", CommUtil.null2Long(pid));
		List<GroupArea> areas = this.groupAreaService.query("select obj from GroupArea obj where obj.parent.id=:pid",
				params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (GroupArea area : areas) {
			Map map = new HashMap();
			map.put("id", area.getId());
			map.put("areaName", area.getGa_name());
			list.add(map);
		}
		String temp = Json.toJson(list, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@RequestMapping("/load_ship_address.htm")
	public void load_ship_address(HttpServletRequest request, HttpServletResponse response, String pid) {
		Map params = new HashMap();
		params.put("sa_address_type", 1);
		List<ShipAddress> shipAddresses = this.shipAddressService.query(
				"select obj from ShipAddress obj where  obj.sa_address_type=:sa_address_type order by province_area_id,city_area_id desc", params, -1, -1);
		List<Map> dataList = new ArrayList<Map>();
		Map geoCoordMap = new HashMap();
		
		Map jsonMap = new HashMap();
		for (ShipAddress shipAddress : shipAddresses) {
			Area area = this.areaService.getObjById(shipAddress.getSa_area_id());
			Map map = new HashMap();
			map.put("name", shipAddress.getSa_addr());
			Map mapt = new HashMap();
			mapt.put("regName", area.getParent().getAreaName());
			mapt.put("shopName", shipAddress.getSa_name());
			map.put("value", mapt);
			dataList.add(map);
			
			Map map2 = new HashMap();
			List list=new ArrayList<>();
			list.add(shipAddress.getLongitude());
			list.add(shipAddress.getLatitude());
			geoCoordMap.put(shipAddress.getSa_addr(), list);
			
		}
		jsonMap.put("data", dataList);
		jsonMap.put("geoCoord", geoCoordMap);
		String temp = Json.toJson(jsonMap, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
