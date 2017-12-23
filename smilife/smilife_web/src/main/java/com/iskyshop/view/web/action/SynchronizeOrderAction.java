package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;

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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderSynQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IOrderSynService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;

/**
 * 
 * <p>
 * Title: VerifyAction.java
 * </p>
 * 
 * <p>
 * Description: 系统验证控制器，用来管理系统验证码生成、用户名验证、邮箱验证等各类验证请求
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
 * @date 2014-5-16
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class SynchronizeOrderAction {
	private Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private SynchronizeOrderPublisher synchronizeOrderPublisher;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IOrderSynService orderSynService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private PaymentTools paymentTools;

	@RequestMapping("/test-sync-order.htm")
	public void testSyncOrder(HttpServletRequest request, HttpServletResponse response, Integer orderId) {
		PrintWriter writer = null;
		String resultMsg = "Fail!";

		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			writer = response.getWriter();
			if (this.synchronizeOrderPublisher.synchronizeOrder(orderId)) {
				resultMsg = "Success!";
			}
		} catch (IOException e) {
			resultMsg = e.getMessage();
		} catch (Exception e) {
			resultMsg = e.getMessage();
		} finally {
			writer.print(resultMsg);
		}
	}
	
	@SecurityMapping(title = "订单同步", value = "/admin/order_syn_list.htm*", rtype = "admin", rname = "订单同步", rcode = "order_syn_admin", rgroup = "交易")
	@RequestMapping("/admin/order_syn_list.htm")
	public ModelAndView order_syn_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String config_code, String syn_status, String order_id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_syn_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderSynQueryObject ofqo = new OrderSynQueryObject(currentPage, mv, "addTime", "desc");
		if (!StringUtils.isNullOrEmpty(config_code)) {
			ofqo.addQuery("obj.goodsConfig.configCode", new SysMap("config_code", config_code), "=");
			mv.addObject("config_code", config_code);
		}
		if (!StringUtils.isNullOrEmpty(syn_status)) {
			ofqo.addQuery("obj.synStatus", new SysMap("syn_status", CommUtil.null2Int(syn_status)), "=");
			mv.addObject("syn_status", syn_status);
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			ofqo.addQuery("obj.orderForm.order_id", new SysMap("order_id", order_id), "=");
			mv.addObject("order_id", order_id);
		}
		IPageList pList = this.orderSynService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	@SecurityMapping(title = "订单同步详情", value = "/admin/order_syn_view.htm*", rtype = "admin", rname = "订单同步管理", rcode = "order_syn_admin", rgroup = "交易")
	@RequestMapping("/admin/order_syn_view.htm")
	public ModelAndView order_syn_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_syn_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderSyn orderSyn = this.orderSynService.getObjById(CommUtil.null2Long(id));
		if(orderSyn != null){
			Long order_id = orderSyn.getOrderForm().getId();
			OrderForm obj = this.orderFormService.getObjById(order_id);
			if (obj.getOrder_cat() == 1) {
				mv = new JModelAndView("admin/blue/order_recharge_view.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("paymentTools", paymentTools);
			} else {
				Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
				TransInfo transInfo = this.orderFormTools.query_ship_getData(CommUtil.null2String(order_id));
				transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
				transInfo.setExpress_ship_code(obj.getShipCode());
				mv.addObject("transInfo", transInfo);
				mv.addObject("store", store);
				mv.addObject("obj", obj);
			}
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("obj", obj);
			mv.addObject("orderSyn", orderSyn);
			mv.addObject("paymentTools", paymentTools);
		}
		return mv;
	}
	
	@SecurityMapping(title = "手动订单同步", value = "/admin/order_syn_operation.htm*", rtype = "admin", rname = "订单同步管理", rcode = "order_syn_admin", rgroup = "交易")
	@RequestMapping("/admin/order_syn_operation.htm")
	public ModelAndView order_syn_operation(HttpServletRequest request, HttpServletResponse response, String ids, String currentPage) {
		//Map<String, String> map = new HashMap<String, String>();
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if(!StringUtils.isNullOrEmpty(ids)){
			String[] idArry = ids.split(",");
			for(int i=0; i<idArry.length; i++){
				String id = idArry[i];
				if(!StringUtils.isNullOrEmpty(id)){
					OrderSyn orderSyn = this.orderSynService.getObjById(CommUtil.null2Long(id));
					orderSyn.setSynStatus(3);//同步中
					orderSyn.setSynUser(user.getUsername());
					orderSynService.update(orderSyn);
					/*if(!map.containsKey(orderSyn.getOrderForm().getOrder_id())){
						map.put(orderSyn.getOrderForm().getOrder_id(), orderSyn.getOrderForm().getOrder_id());
					} */
				}
			}
		/*	for (String key : map.keySet()) {
				String orderId = map.get(key);
				if(!StringUtils.isNullOrEmpty(orderId)){
					testSyncOrder(request, response, CommUtil.null2Int(orderId));
				}
			}*/
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "定单同步推送成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/order_syn_list.htm?currentPage=" + currentPage);
		return mv;
	}
}
