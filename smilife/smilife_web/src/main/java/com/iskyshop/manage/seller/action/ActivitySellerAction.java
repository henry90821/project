package com.iskyshop.manage.seller.action;

import java.text.SimpleDateFormat;
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
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.ActivityQueryObject;
import com.iskyshop.foundation.domain.query.CouponQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.QueryTools;

/**
 * 
 * <p>
 * Title: ActivitySellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家活动管理控制器，商家可以申请活动套餐，申请完成后可以发布活动唱片，活动会在商家店铺页面显示
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
 * @date 2014-4-25
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class ActivitySellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private QueryTools queryTools;
	@Autowired
	private ICouponService couponService;

	@SecurityMapping(title = "活动列表", value = "/seller/activity.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity.htm")
	public ModelAndView activity(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/activity.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.ac_status", new SysMap("ac_status", 1), "=");
		qo.addQuery("obj.ac_begin_time", new SysMap("ac_begin_time", new Date()), "<=");
		qo.addQuery("obj.ac_end_time", new SysMap("ac_end_time", new Date()), ">=");
		String acType = request.getParameter("acType");
		if (!StringUtils.isNullOrEmpty(acType)) {
			//0位商品活动，其它是优惠券活动
			qo.addQuery("obj.acType", new SysMap("acType", 0), "!=");
			qo.setPageSize(15);
		}else{
			acType ="0";
			qo.addQuery("obj.acType", new SysMap("acType", CommUtil.null2Int(acType)), "=");
			qo.setPageSize(1);
		}
		qo.setOrderBy("ac_sequence");
		qo.setOrderType("asc");
		IPageList pList = this.activityService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		logger.debug(qo.getPageSize());
		mv.addObject("acType", acType);
		return mv;
	}

	@SecurityMapping(title = "申请参加活动", value = "/seller/activity_apply.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity_apply.htm")
	public ModelAndView activity_apply(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/activity_apply.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean ret = false;
		if (!StringUtils.isNullOrEmpty(id)) {
			Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
			if (act != null) {
				if (act.getAc_status() == 1) {
					ret = true;
				}
				if (act.getAc_begin_time().before(new Date())) {
					ret = true;
				}
				if (act.getAc_end_time().after(new Date())) {
					ret = true;
				}
				if (ret) {
					mv.addObject("act", act);
				}
			}
		}
		if (!ret) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "活动参数不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/activity.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "活动商品加载", value = "/seller/activity_goods.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity_goods.htm")
	public ModelAndView activity_goods(HttpServletRequest request, HttpServletResponse response, String goods_name,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/activity_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!"".equals(CommUtil.null2String(goods_name))) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + CommUtil.null2String(goods_name) + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		qo.addQuery("obj.goods_store.id", new SysMap("store_id", store.getId()), "=");
		this.queryTools.shieldGoodsStatus(qo, null);
		qo.setPageSize(15);
		IPageList pList = this.goodsService.list(qo);
		String url = CommUtil.getURL(request) + "/seller/activity_goods.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url, "&goods_name=" + goods_name, pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	@SecurityMapping(title = "申请参加活动", value = "/seller/activity_apply_save.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity_apply_save.htm")
	public String activity_apply_save(HttpServletRequest request, HttpServletResponse response, String goods_ids,
			String act_id, String acType) {
		String url = "redirect:/seller/success.htm";
		if (!StringUtils.isNullOrEmpty(goods_ids)) {
			Activity act = this.activityService.getObjById(CommUtil.null2Long(act_id));
			String[] ids = goods_ids.split(",");
			for (String id : ids) {
				if (!"".equals(id)) {
					ActivityGoods ag = new ActivityGoods();
					ag.setAddTime(new Date());
					ag.setAg_status(0);
					ag.setAct(act);
					ag.setAg_type(0); // 商家活动商品
					if("0".equals(acType)){//活动商品
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
						ag.setAg_goods(goods);
						ag.setAcType(0);
						this.activityGoodsService.save(ag);
						goods.setActivity_status(1);
						goods.setActivity_goods_id(ag.getId());
						this.goodsService.update(goods);
					}else{//活动优惠券
						Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
						ag.setCoupon(coupon);
						ag.setAcType(1);
						this.activityGoodsService.save(ag);
					}
				}
			}
			request.getSession(true).setAttribute("url", CommUtil.getURL(request) + "/seller/activity.htm");
			request.getSession(true).setAttribute("op_title", "申请参加活动成功");
		} else {
			url = "redirect:/seller/error.htm";
			request.getSession(true).setAttribute("url", CommUtil.getURL(request) + "/seller/activity.htm");
			request.getSession(true).setAttribute("op_title", "至少选择一件商品");
		}
		return url;
	}

	@SecurityMapping(title = "活动商品列表", value = "/seller/activity_goods_list.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String act_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/activity_goods_list.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.getObjById(CommUtil.null2Long(act_id));
		if (act != null) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage, mv, "addTime", "desc");
			if("0".equals(CommUtil.null2String(act.getAcType()))){
				qo.addQuery("obj.ag_goods.goods_store.user.id", new SysMap("user_id", user.getId()), "=");
			}else{
				qo.addQuery("obj.coupon.store.user.id", new SysMap("user_id", user.getId()), "=");
			}
			
			qo.addQuery("obj.acType", new SysMap("acType", 2), "!=");//商家看不到全场优惠券
			qo.addQuery("obj.act.id", new SysMap("act_id", CommUtil.null2Long(act_id)), "=");
			qo.setPageSize(30);
			IPageList pList = this.activityGoodsService.list(qo);	
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("acType", act.getAcType());
		}
		mv.addObject("act_id", act_id);
		return mv;
	}
	
	
	@SecurityMapping(title = "活动优惠券加载", value = "/seller/activity_self_coupon_load.htm*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping("/seller/activity_self_coupon_load.htm")
	public ModelAndView activity_self_coupon_load(HttpServletRequest request, HttpServletResponse response, String coupon_name,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/activity_self_coupon_load.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("ag_status1", -1);
		params.put("ag_status2", -2);
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		CouponQueryObject cqo = new CouponQueryObject(currentPage, mv, "addTime", "desc");
		cqo.setPageSize(15);
		StringBuffer sql = new StringBuffer("obj.id NOT IN (SELECT b.coupon.id FROM ActivityGoods b WHERE b.coupon.id IS NOT NULL AND (b.ag_status != :ag_status1 OR b.ag_status != :ag_status2)) AND (obj.coupon_count > (SELECT COUNT(c.id) FROM CouponInfo c WHERE c.coupon.id=obj.id) OR obj.coupon_count = 0)");
		if (!StringUtils.isNullOrEmpty(coupon_name)) {
			params.put("coupon_name", "%" + CommUtil.null2String(coupon_name) + "%");
			sql.append(" AND obj.coupon_name LIKE :coupon_name ");
		}
		sql.append(" AND obj.coupon_end_time >=:coupon_end_time ");
		params.put("coupon_end_time", CommUtil.formatDate(time));
		sql.append(" AND obj.coupon_type =:coupon_type ");
		params.put("coupon_type", 1);
		sql.append(" AND obj.store.id =:store_id ");
		params.put("store_id", store.getId());
		cqo.addQuery(sql.toString(), params);
		IPageList pList = this.couponService.list(cqo);
		String url = CommUtil.getURL(request) + "/seller/activity_self_coupon_load.htm";
		CommUtil.saveIPageList2ModelAndView(url, "", "", pList, mv);
		return mv;
	}
}
