package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.msg.MsgTools;

/**
 * 对接外部系统接口
 * 
 * @ClassName: OutEntranceAction
 * @Description: TODO(对接外部系统接口<公司内部，公司内部系统:http协议.公司外部系统WebService>)
 * @author wangyun
 * @date 2015-11-11
 * 
 */
@Controller
public class OutEntranceAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private ShipTools shipTools;

	/**
	 * 更新订单状态
	 * 
	 * @Title: updateOrderStatus
	 * @Description: TODO(把接收到的订单app后台系统订单数据，更新到数据库)
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/outentranceaction/updateOrderStatus.htm")
	public void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			String orderSn = request.getParameter("order_sn");
			String orderState = request.getParameter("order_state");
			String shipCode = request.getParameter("shipCode");
			String userName = request.getParameter("user_name");
			String expressCompanyCommonId = request.getParameter("expressCompanyCommonId");
			logger.info(orderSn + "	" + orderState + "	" + shipCode + "	" + expressCompanyCommonId);
			Map map = new HashMap<>();
			map.put("order_sn", orderSn);
			if (!StringUtils.isNullOrEmpty(orderSn)) {
				OrderForm orderForm = this.orderFormService.getObjByProperty(null, "order_id", orderSn);
				if(!StringUtils.isNullOrEmpty(orderForm)){
					if ("30".equals(orderState)) { // 30：发货
						// /admin/order_shipping_save.htm
						this.order_shipping_save(request, orderForm, shipCode, expressCompanyCommonId, userName);
					} else if ("40".equals(orderState)) { // 40：收货
						this.order_cofirm_save(orderForm, userName); // 收货逻辑参照 /buyer/order_cofirm_save.htm
					}
					map.put("status", 100);
				}else{
					map.put("status", -100);
				}
			} else {
				map.put("status", -100);
			}
			out = response.getWriter();
			out.write(Json.toJson(map));
		} catch (IOException e) {
			logger.error("update app system orderstatus to xingmeihui error:", e);
		}
	}

	/**
	 * 掌上订单确认发货
	 * 
	 * @param request
	 * @param orderForm
	 * @param shipCode
	 * @param expressCompanyCommonId

	 * @param user_name
	 */
	private void order_shipping_save(HttpServletRequest request, OrderForm orderForm, String shipCode,
			String expressCompanyCommonId, String user_name) {
		orderForm.setOrder_status(30);
		if(!StringUtils.isNullOrEmpty(shipCode)){
		orderForm.setShipCode(shipCode);
		}
		orderForm.setShipTime(new Date());
		ExpressCompanyCommon ecc = this.expressCompanyCommonService.getObjById(CommUtil.null2Long(expressCompanyCommonId));
		if (ecc != null) {
			Map<Object, Object> json_map = new HashMap<Object, Object>();
			json_map.put("express_company_id", ecc.getEcc_ec_id());
			json_map.put("express_company_id_common", ecc.getId());
			json_map.put("express_company_name", ecc.getEcc_name());
			json_map.put("express_company_mark", ecc.getEcc_code());
			json_map.put("express_company_type", ecc.getEcc_ec_type());
			String express_json = Json.toJson(json_map);
			orderForm.setExpress_info(express_json);
		}
		this.orderFormService.update(orderForm);

		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setLog_info("星美生活确认发货");
		ofl.setState_info(user_name + "于掌上订单APP确认发货");
		ofl.setOf(orderForm);
		this.orderFormLogService.save(ofl);

		User buyer = this.userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));
		Map map = new HashMap();
		map.put("buyer_id", buyer.getId().toString());
		map.put("self_goods", this.configService.getSysConfig().getTitle());
		map.put("order_id", orderForm.getId());
		try {
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_selforder_ship_notify", buyer.getEmail(),
					map, null);
		} catch (Exception e1) {
			logger.error("系统给用户发送邮件失败。", e1);
		}
		try {
			this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_selforder_ship_notify", buyer.getMobile(),
					map, null);
		} catch (Exception e1) {
			logger.error("系统给用户发送短信失败。", e1);
		}
		// 如果是收费接口，则通知快递100，建立订单物流查询推送
		if (this.configService.getSysConfig().getKuaidi_type() == 1 && orderForm.getExpress_info() != null) {
			shipTools.postOrder(orderForm, CommUtil.getURL(request));
		}
	}

	/**
	 * 掌上订单收货
	 * 
	 * @Title: order_cofirm_save
	 * @Description: TODO(同步掌上app已收货的订单状态)
	 * @param @param orderform 参数
	 * @return void 返回类型
	 * @throws
	 */
	public void order_cofirm_save(OrderForm orderform, String user_name) {
		if (!StringUtils.isNullOrEmpty(orderform)) {
			orderform.setOrder_status(40);
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
			orderform.setReturn_shipTime(ca.getTime());
			orderform.setConfirmTime(new Date()); // 设置确认收货时间
			boolean ret = this.orderFormService.update(orderform);

			// 生成收货日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("掌上订单APP确认收货");
			ofl.setState_info(user_name + "于掌上订单APP确认收货");
			ofl.setOf(orderform);
			this.orderFormLogService.save(ofl);
		}
	}

	/**
	 * @Title: queryOrderStatus
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/outentranceaction/queryOrderStatus.htm")
	public void queryOrderStatus(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			String orderSn = request.getParameter("order_sn");
			Map map = new HashMap<>();
			map.put("order_sn", orderSn);
			if (!StringUtils.isNullOrEmpty(orderSn)) {
				OrderForm orderForm = this.orderFormService.getObjByProperty(null, "order_id", orderSn);
				if (orderForm != null) {
					map.put("status", orderForm.getOrder_status());
					map.put("confirmTime", CommUtil.formatLongDate(orderForm.getConfirmTime()));// 确认收货时间
				} else {
					map.put("status", -100);
					map.put("message", "订单号错误!");
				}
			}
			out = response.getWriter();
			out.write(Json.toJson(map));
		} catch (IOException e) {
			logger.error("queryorderstatus app system queryorderstatus to xingmeihui error:", e);
		}
	}

	/**
	 * @Title: queryCinemaData
	 * @Description: TODO(查询星美汇影院编码数据)
	 * @param @param request
	 * @param @param response 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/outentranceaction/queryCinemaData.htm")
	public void queryCinemaData(HttpServletRequest request, HttpServletResponse response) {
		List list = new ArrayList();
		Map<String, Object> mainMap = new HashMap<String, Object>();
		PrintWriter print = null;
		try {
			print = response.getWriter();
			Map<String, Integer> sqlMap = new HashMap<String, Integer>();
			sqlMap.put("addressType", 1);
			sqlMap.put("delStatus", 0);
			String hql = "select t from ShipAddress t where t.sa_address_type=:addressType and t.deleteStatus=:delStatus";
			List<ShipAddress> shipAddres = this.shipAddressService.query(hql, sqlMap, -1, -1);
			for (ShipAddress shipAddr : shipAddres) {
				Area cityArea = this.areaService.getObjById(shipAddr.getCity_area_id());
				Area provinceArea = this.areaService.getObjById(shipAddr.getProvince_area_id());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", shipAddr.getId());
				map.put("shopName", shipAddr.getSa_name());
				map.put("province", shipAddr.getProvince_area_id());
				map.put("city", shipAddr.getCity_area_id());
				map.put("description", shipAddr.getSa_company());
				map.put("createDate", CommUtil.formatLongDate(shipAddr.getAddTime()));
				map.put("address", shipAddr.getSa_addr());
				map.put("xm_id", shipAddr.getSa_code());
				map.put("province_name", provinceArea.getAreaName());
				map.put("city_name", cityArea.getAreaName());
				list.add(map);
			}
			mainMap.put("code", 0);
			mainMap.put("retDesc", "succeed!");
			mainMap.put("datas", list);
		} catch (IOException e) {
			mainMap.put("code", -100);
			mainMap.put("retDesc", "io system error!");
			logger.error("io stream error", e);
		}
		String jsonStr = JSONObject.fromObject(mainMap).toString();
		print.write(jsonStr);
	}

	/**
	 * 查询自营常用物流列表
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 */
	@RequestMapping("/outentranceaction/ecc_list.htm")
	public void queryEccList(HttpServletRequest request, HttpServletResponse response) {
		Map params = new HashMap<>();
		params.put("ecc_type", 1);
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.query(
				"select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type", params, -1, -1);
		List list = new ArrayList();
		for (ExpressCompanyCommon expressCompanyCommon : eccs) {
			Map map = new HashMap<>();
			map.put("id", expressCompanyCommon.getId());
			map.put("name", expressCompanyCommon.getEcc_name());
			map.put("code", expressCompanyCommon.getEcc_code());
			list.add(map);
		}
		String jsonString = JSON.toJSONString(list);
		logger.info("json data:" + jsonString);
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: wifiActivity
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/outentranceaction/wifiActivity.htm")
	public void wifiActivity() {
          
	}
}
