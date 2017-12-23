package com.iskyshop.manage.admin.action;

import java.util.Date;

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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.ActivityQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.QueryTools;

/**
 * 
 * <p>
 * Title: ActivitySelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营活动管理类
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
public class SelfActivityManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGoodsService goodService;
	@Autowired
	private QueryTools queryTools;
	@Autowired
	private ICouponService couponService;

	/**
	 * Activity列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营活动列表", value = "/admin/group_self.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self.htm")
	public ModelAndView activity_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String ac_title, String acType, String ac_status, String ac_begin_time, String ac_end_time) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(ac_title)) {
			qo.addQuery("obj.ac_title", new SysMap("ac_title", "%" + CommUtil.null2String(ac_title) + "%"), "like");
			mv.addObject("ac_title", ac_title);
		}
		if (!StringUtils.isNullOrEmpty(acType)) {
			qo.addQuery("obj.acType", new SysMap("acType", CommUtil.null2Int(acType)), "=");
			mv.addObject("acType", acType);
		}
			
		if (!StringUtils.isNullOrEmpty(ac_status)) {
			qo.addQuery("obj.ac_status", new SysMap("ac_status", CommUtil.null2Int(ac_status)), "=");
			mv.addObject("ac_status", ac_status);
		}
		if (!StringUtils.isNullOrEmpty(ac_begin_time)) {
			qo.addQuery("obj.ac_begin_time", new SysMap("ac_begin_time", CommUtil.formatDate(ac_begin_time)), ">=");
			mv.addObject("ac_begin_time", ac_begin_time);
		}
		if (!StringUtils.isNullOrEmpty(ac_end_time)) {
			String end = ac_end_time + " 23:59:59";
			qo.addQuery("obj.ac_end_time", new SysMap("ac_end_time", CommUtil.formatDate(end, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("ac_end_time", ac_end_time);
		}
		IPageList pList = this.activityService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	@SecurityMapping(title = "自营活动申请", value = "/admin/activity_self_apply.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_apply.htm")
	public ModelAndView activity_apply(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self_apply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
		mv.addObject("act", act);
		mv.addObject("global_coupon_flag",request.getParameter("global_coupon_flag"));
		return mv;
	}

	@SecurityMapping(title = "自营活动商品加载", value = "/admin/activity_self_goods_load.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_goods_load.htm")
	public ModelAndView activity_self_goods_load(HttpServletRequest request, HttpServletResponse response, String goods_name,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self_goods_load.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + CommUtil.null2String(goods_name) + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 0), "=");
		this.queryTools.shieldGoodsStatus(qo, null);
		qo.setPageSize(15);
		IPageList pList = this.goodService.list(qo);
		String url = CommUtil.getURL(request) + "/admin/activity_self_goods_load.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	@SecurityMapping(title = "自营活动商品保存", value = "/admin/activity_self_apply_save.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_apply_save.htm")
	public ModelAndView activity_apply_save(HttpServletRequest request, HttpServletResponse response, String goods_ids,
			String act_id, String acType) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.getObjById(CommUtil.null2Long(act_id));
		String[] ids = goods_ids.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				ActivityGoods ag = new ActivityGoods();
				ag.setAddTime(new Date());
				ag.setAg_status(1);
				ag.setAct(act);
				ag.setAg_type(1); // 自营活动商品
				if("0".equals(acType)){//活动商品
					Goods goods = this.goodService.getObjById(CommUtil.null2Long(id));
					ag.setAg_goods(goods);
					ag.setAcType(0);
					this.activityGoodsService.save(ag);
					goods.setActivity_status(2);
					goods.setActivity_goods_id(ag.getId());
					this.goodService.update(goods);
				}else{//活动优惠券
					Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
					ag.setCoupon(coupon);
					ag.setAcType(Integer.parseInt(acType));
					this.activityGoodsService.save(ag);
				}
			}
		}
		
		//全场优惠券返回链接
		String global_coupon_flag=request.getParameter("global_coupon_flag");
		if("1".equals(global_coupon_flag))
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/couponbag_activity.htm");
		else
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/activity_self.htm");
		mv.addObject("op_title", "参加活动成功");
		return mv;
	}

	@SecurityMapping(title = "活动商品列表", value = "/admin/activity_self_goods_list.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String act_id) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self_goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.ag_type", new SysMap("ag_type", CommUtil.null2Int(1)), "=");
		
		if (!StringUtils.isNullOrEmpty(act_id)) {
			qo.addQuery("obj.act.id", new SysMap("obj_act_id", CommUtil.null2Long(act_id)), "=");
		}
		//全场优惠券情况下处理
		if("1".equals(request.getParameter("global_coupon_flag")))
			qo.addQuery("obj.acType", new SysMap("acType", 2), "=");
		else
			qo.addQuery("obj.acType", new SysMap("acType", 2), "!=");//自营看不到全场优惠券
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("act_id", act_id);
		mv.addObject("acType",request.getParameter("acType"));
		mv.addObject("global_coupon_flag",request.getParameter("global_coupon_flag"));
		return mv;
	}

}