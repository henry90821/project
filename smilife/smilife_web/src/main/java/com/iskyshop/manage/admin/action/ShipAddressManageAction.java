package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.GoodsInventory;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.query.ShipAddressQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsInventoryService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.GoodsTools;

/**
 * 
 * <p>
 * Title: ShipAddressManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商家发货地址管理控制器，用来管理自营发货地址信息
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
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class ShipAddressManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGoodsInventoryService goodsInventoryService;

	/**
	 * ShipAddress列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址列表", value = "/admin/ship_address_list.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_list.htm")
	public ModelAndView ship_address_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_address_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShipAddressQueryObject qo = new ShipAddressQueryObject(currentPage, mv, orderBy, orderType);
		String sa_name = request.getParameter("sa_name");
		String sa_address_type = request.getParameter("sa_address_type");
		String area1 = request.getParameter("area1");
		String area2 = request.getParameter("area2");
		String area3 = request.getParameter("area3");
		if (!StringUtils.isNullOrEmpty(sa_name)) {
			qo.addQuery("obj.sa_name", new SysMap("sa_name", "%" + sa_name.trim() + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(sa_address_type)) {
			qo.addQuery("obj.sa_address_type", new SysMap("sa_address_type", CommUtil.null2Int(sa_address_type)), "=");
		} else {
			sa_address_type = "0";
			qo.addQuery("obj.sa_address_type", new SysMap("sa_address_type", CommUtil.null2Int(sa_address_type)), "=");
		}
		if (!StringUtils.isNullOrEmpty(area1)) {
			qo.addQuery("obj.province_area_id", new SysMap("province_area_id", CommUtil.null2Long(area1)), "=");
			mv.addObject("area1Obj", this.areaService.getObjById(CommUtil.null2Long(area1)));
		}
		if (!StringUtils.isNullOrEmpty(area2)) {
			qo.addQuery("obj.city_area_id", new SysMap("city_area_id", CommUtil.null2Long(area2)), "=");
			mv.addObject("area2Obj", this.areaService.getObjById(CommUtil.null2Long(area2)));
		}
		if (!StringUtils.isNullOrEmpty(area3)) {
			qo.addQuery("obj.sa_area_id", new SysMap("sa_area_id", CommUtil.null2Long(area3)), "=");
		}
		qo.addQuery("obj.sa_type", new SysMap("sa_type", 1), "=");
		IPageList pList = this.shipAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("sa_name", sa_name);
		mv.addObject("sa_address_type", sa_address_type);
		mv.addObject("area1", area1);
		mv.addObject("area2", area2);
		mv.addObject("area3", area3);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * shipaddress添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营发货地址添加", value = "/admin/ship_address_add.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_add.htm")
	public ModelAndView ship_address_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * shipaddress编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营发货地址编辑", value = "/admin/ship_address_edit.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_edit.htm")
	public ModelAndView ship_address_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			ShipAddress shipaddress = this.shipAddressService.getObjById(CommUtil.null2Long(id));
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			mv.addObject("sa_area", this.areaService.getObjById(shipaddress.getSa_area_id()));
			mv.addObject("areas", areas);
			mv.addObject("obj", shipaddress);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * shipaddress保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址保存", value = "/admin/ship_address_save.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_save.htm")
	public ModelAndView ship_address_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String o2oCapable, String needSyn) {
		ModelAndView mv = null;
		WebForm wf = new WebForm();
		ShipAddress shipaddress = null;
		if (StringUtils.isNullOrEmpty(id)) {
			shipaddress = wf.toPo(request, ShipAddress.class);
			shipaddress.setAddTime(new Date());
		} else {
			ShipAddress obj = this.shipAddressService.getObjById(Long.parseLong(id));
			shipaddress = (ShipAddress) wf.toPo(request, obj);
		}
		shipaddress.setSa_type(1);
		shipaddress.setSa_user_id(SecurityUserHolder.getCurrentUser().getId());
		shipaddress.setSa_user_name(SecurityUserHolder.getCurrentUser().getUsername());
		if (!StringUtils.isNullOrEmpty(shipaddress.getSa_address_type())) {
			if (StringUtils.isNullOrEmpty(id)) {
				this.shipAddressService.save(shipaddress);
			} else {
				this.shipAddressService.update(shipaddress);
			}
		}
		mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "发货地址保存成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/ship_address_list.htm?currentPage=" + currentPage
				+ "&sa_address_type=" + shipaddress.getSa_address_type());
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/ship_address_add.htm?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "自营发货地址删除", value = "/admin/ship_address_del.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_del.htm")
	public String ship_address_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, String sa_address_type) {
		String[] ids = mulitId.split(",");
		String[] sa_types = sa_address_type.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				ShipAddress obj = this.shipAddressService.getObjById(CommUtil.null2Long(id));
				if (obj.getSa_type() == 1) { // 只能删除自营发货地址
					this.shipAddressService.delete(obj.getId());
				}
			}
		}
		for (String sa_type : sa_types) {
			if (!StringUtils.isNullOrEmpty(sa_type) && "0".equals(CommUtil.null2String(sa_type))) {
				return "redirect:ship_address_list.htm?currentPage=" + currentPage + "&sa_address_type=" + sa_type;
			} else if (!StringUtils.isNullOrEmpty(sa_type) && "1".equals(CommUtil.null2String(sa_type))) {
				return "redirect:ship_address_list.htm?currentPage=" + currentPage + "&sa_address_type=" + sa_type;
			}
		}
		return "redirect:ship_address_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "设置默认发货地址", value = "/admin/ship_address_default.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_default.htm")
	public String ship_address_default(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String sa_address_type) {
		Map params = new HashMap();
		params.put("sa_default", 1);
		params.put("sa_type", 1);
		List<ShipAddress> sa_list = this.shipAddressService.query(
				"select obj from ShipAddress obj where obj.sa_default=:sa_default and obj.sa_type=:sa_type", params, -1, -1);
		for (ShipAddress sa : sa_list) {
			sa.setSa_default(0);
			this.shipAddressService.update(sa);
		}
		ShipAddress obj = this.shipAddressService.getObjById(CommUtil.null2Long(id));
		if (obj.getSa_type() == 1) { // 只能设置自营发货地址
			obj.setSa_default(1);
			this.shipAddressService.update(obj);
		}
		if (!StringUtils.isNullOrEmpty(sa_address_type) && "0".equals(CommUtil.null2String(sa_address_type))) {
			return "redirect:ship_address_list.htm?currentPage=" + currentPage + "&sa_address_type=" + sa_address_type;
		} else if (!StringUtils.isNullOrEmpty(sa_address_type) && "1".equals(CommUtil.null2String(sa_address_type))) {
			return "redirect:ship_address_list.htm?currentPage=" + currentPage + "&sa_address_type=" + sa_address_type;
		}
		return "redirect:ship_address_list.htm?currentPage=" + currentPage;
	}

	/**
	 * 按县区查找门店
	 * 
	 * @param request
	 * @param response
	 * @param sa_area_id
	 */
	@RequestMapping("/load_shipAddress.htm")
	public void load_shipAddress(HttpServletRequest request, HttpServletResponse response, String sa_area_id) {
		Map params = new HashMap();
		params.put("city_area_id", CommUtil.null2Long(sa_area_id));
		params.put("sa_address_type", 1);
		List<ShipAddress> shipAddresses = this.shipAddressService.query(
				"select obj from ShipAddress obj where obj.city_area_id=:city_area_id and obj.sa_address_type=:sa_address_type",
				params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (ShipAddress shipAddress : shipAddresses) {
			Map map = new HashMap();
			map.put("id", shipAddress.getId());
			map.put("name", shipAddress.getSa_name());
			// map.put("name", shipAddress.getSa_name());
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

	/**
	 * 查询指定门店中各指定商品的库存信息
	 * 
	 * @param request
	 * @param response
	 * @param shipAddress_id
	 *            门店id
	 * @param goodsCodes
	 *            商品编码(以","分隔)
	 */
	@RequestMapping("/load_goodsInventory.htm")
	public void load_goodsInventory(HttpServletRequest request, HttpServletResponse response, String shipAddress_id,
			String goodsCodes) {
		int errCode = 0;
		String msg = "";
		Map map = new HashMap();
		Integer selfPickupEnabled = (Integer) request.getSession(true).getAttribute("selfPickupEnabled");

		if (selfPickupEnabled == null || selfPickupEnabled == 0 || StringUtils.isNullOrEmpty(shipAddress_id)
				|| StringUtils.isNullOrEmpty(goodsCodes)) {
			errCode = 1;
			msg = "非法请求!";
		} else {
			// 查询海信商品库存
			Set<String> goodsCodesSet = new HashSet<String>();
			for (String code : goodsCodes.split(",")) {
				goodsCodesSet.add(code);
			}
			
			List<GoodsInventory> goodsInventories = null;
			Long saId = CommUtil.null2Long(shipAddress_id);
			Map params = new HashMap();
			params.put("shipAddress_id", saId);
			params.put("goodsCodes", goodsCodesSet);			
			GoodsTools.hxInventoryRWLock.readLock().lock();			
			try {
				goodsInventories = this.goodsInventoryService.query(
						"select obj from GoodsInventory obj where obj.shipAddress.id=:shipAddress_id and obj.goodsCode in (:goodsCodes) and obj.inventory > 0",
						params, -1, -1);
			} catch (Exception e) {
				logger.error("查询海信商品库存出现异常。", e);
				errCode = 2;
				msg = "查询海信商品库存出现异常!";
			} finally {
				GoodsTools.hxInventoryRWLock.readLock().unlock();
			}
			
			if(errCode == 0) {
				for (GoodsInventory gi : goodsInventories) {
					map.put(gi.getGoodsCode(), gi.getInventory());
				}

				ShipAddress address = shipAddressService.getObjById(saId);
				String addressStr = "地址：" + address.getSa_addr();
				if (!StringUtils.isNullOrEmpty(address.getSa_telephone())) {
					addressStr = addressStr + "。联系方式：" + address.getSa_telephone();
				}
				map.put("address", addressStr); // 门店地址
			}			
		}

		map.put("errCode", errCode);
		map.put("msg", msg);

		String temp = Json.toJson(map, JsonFormat.compact());
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