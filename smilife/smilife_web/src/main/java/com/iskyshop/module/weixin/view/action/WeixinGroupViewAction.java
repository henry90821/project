package com.iskyshop.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.GroupClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.GroupPriceRange;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupPriceRangeService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GroupViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * <p>
 * Title: WapGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:团购管理控制器，超级后台用来发起团购、审核团购商品，添加团购商品类目、价格区间等等
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
 * @author jinxinzhe
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class WeixinGroupViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupAreaService groupAreaService;
	@Autowired
	private IGroupPriceRangeService groupPriceRangeService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;

	@RequestMapping("/wap/group/index.htm")
	public ModelAndView group(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String ga_id, String type) {
		ModelAndView mv = new JModelAndView("wap/group.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ("".equals(type) || "goods".equals(type) || type == null) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 0);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy, orderType);
				ggqo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0).getId()), "=");
				if (!StringUtils.isNullOrEmpty(gc_id)) {
					ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
				}
				if (!StringUtils.isNullOrEmpty(ga_id)) {
					ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
					mv.addObject("ga_id", ga_id);
				}
				GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					ggqo.addQuery("obj.gg_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
					ggqo.addQuery("obj.gg_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
				}
				ggqo.addQuery("obj.gg_status", new SysMap("gg_status", 1), "=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				ggqo.addQuery("obj.gg_goods.goods_status", new SysMap("status", 0), "=");
				IPageList pList = this.groupGoodsService.list(ggqo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=0 and obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if (StringUtils.isNullOrEmpty(orderBy)) {
					orderBy = "addTime";
				}
				if (StringUtils.isNullOrEmpty(orderType)) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
			}
		}

		if ("life".equals(type)) {
			mv = new JModelAndView("wap/group_life.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 1);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupLifeGoodsQueryObject ggqo = new GroupLifeGoodsQueryObject(currentPage, mv, orderBy, orderType);
				ggqo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0).getId()), "=");
				if (!StringUtils.isNullOrEmpty(gc_id)) {
					ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
				}
				if (!StringUtils.isNullOrEmpty(ga_id)) {
					ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
					mv.addObject("ga_id", ga_id);
				}
				GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					ggqo.addQuery("obj.group_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())),
							">=");
					ggqo.addQuery("obj.group_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
				}
				ggqo.addQuery("obj.group_status", new SysMap("group_status", 1), "=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				IPageList pList = this.groupLifeGoodsService.list(ggqo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=1 and obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if (StringUtils.isNullOrEmpty(orderBy)) {
					orderBy = "addTime";
				}
				if (StringUtils.isNullOrEmpty(orderType)) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
				mv.addObject("groupViewTools", groupViewTools);
			}
		}
		mv.addObject("type", type);
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
		mv.addObject("orderType", orderType);
		if (!StringUtils.isNullOrEmpty(ga_id)) {
			mv.addObject("ga", this.groupAreaService.getObjById(CommUtil.null2Long(ga_id)).getGa_name());
		} else {
			mv.addObject("ga", "全国");
		}
		if ("desc".equals(CommUtil.null2String(orderType))) {
			mv.addObject("c_orderType", "asc");
		} else if ("asc".equals(CommUtil.null2String(orderType))) {
			mv.addObject("c_orderType", "desc");
		}
		return mv;
	}

	@RequestMapping("/wap/group/data.htm")
	public ModelAndView group_data(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String gc_id, String gpr_id, String ga_id, String type) {
		ModelAndView mv = new JModelAndView("wap/group_data.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ("".equals(type) || "goods".equals(type) || type == null) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 0);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy, orderType);
				ggqo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0).getId()), "=");
				if (!StringUtils.isNullOrEmpty(gc_id)) {
					ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
				}
				if (!StringUtils.isNullOrEmpty(ga_id)) {
					ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
					mv.addObject("ga_id", ga_id);
				}
				GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					ggqo.addQuery("obj.gg_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
					ggqo.addQuery("obj.gg_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
				}
				ggqo.addQuery("obj.gg_status", new SysMap("gg_status", 1), "=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				ggqo.addQuery("obj.gg_goods.goods_status", new SysMap("status", 0), "=");
				IPageList pList = this.groupGoodsService.list(ggqo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=0 and obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if (StringUtils.isNullOrEmpty(orderBy)) {
					orderBy = "addTime";
				}
				if (StringUtils.isNullOrEmpty(orderType)) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
			}
		}

		if ("life".equals(type)) {
			mv = new JModelAndView("wap/group_life_data.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 1);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupLifeGoodsQueryObject ggqo = new GroupLifeGoodsQueryObject(currentPage, mv, orderBy, orderType);
				ggqo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0).getId()), "=");
				if (!StringUtils.isNullOrEmpty(gc_id)) {
					ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
				}
				if (!StringUtils.isNullOrEmpty(ga_id)) {
					ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
					mv.addObject("ga_id", ga_id);
				}
				GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					ggqo.addQuery("obj.group_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())),
							">=");
					ggqo.addQuery("obj.group_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
				}
				ggqo.addQuery("obj.group_status", new SysMap("group_status", 1), "=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				IPageList pList = this.groupLifeGoodsService.list(ggqo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=1 and obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if (StringUtils.isNullOrEmpty(orderBy)) {
					orderBy = "addTime";
				}
				if (StringUtils.isNullOrEmpty(orderType)) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
				mv.addObject("groupViewTools", groupViewTools);
			}
		}
		mv.addObject("type", type);
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
		return mv;
	}

	@RequestMapping("/wap/group/area.htm")
	public ModelAndView group_area(HttpServletRequest request, HttpServletResponse response, String ga_id, String type) {
		ModelAndView mv = new JModelAndView("wap/group_area.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GroupArea> gas = this.groupAreaService.query(
				"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1, -1);
		mv.addObject("gas", gas);
		if (!StringUtils.isNullOrEmpty(ga_id)) {
			mv.addObject("ga", this.groupAreaService.getObjById(CommUtil.null2Long(ga_id)).getGa_name());
		} else {
			mv.addObject("ga", "全国");
		}
		mv.addObject("ga_id", ga_id);
		mv.addObject("type", type);
		return mv;
	}

	@RequestMapping("/wap/group/view.htm")
	public ModelAndView group_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/group_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupGoods obj = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		boolean view = false;
		if (obj.getGroup().getBeginTime().before(new Date()) && obj.getGroup().getEndTime().after(new Date())
				&& obj.getGg_status() == 1) {
			view = true;
		}
		if (user != null && user.getUserRole().indexOf("ADMIN") >= 0) {
			view = true;
		}
		if (view) {
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("status", 0);
			params.put("group_type", 0);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject("1", mv, "gg_recommend", "desc");
				ggqo.addQuery("obj.gg_status", new SysMap("gg_status", 1), "=");
				ggqo.addQuery("obj.group.id", new SysMap("group_id", obj.getGroup().getId()), "=");
				ggqo.addQuery("obj.id", new SysMap("goods_id", obj.getId()), "!=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				ggqo.setPageSize(4);
				IPageList pList = this.groupGoodsService.list(ggqo);
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		return mv;
	}

	@RequestMapping("/wap/grouplife/view.htm")
	public ModelAndView grouplife_view(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("wap/grouplife_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupLifeGoods obj = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		boolean view = false;
		if (obj.getGroup().getBeginTime().before(new Date()) && obj.getGroup_status() == 1
				&& obj.getGroup().getEndTime().after(new Date())) {
			view = true;
		}
		if (user != null && user.getUserRole().indexOf("ADMIN") >= 0) {
			view = true;
		}
		if (view) {
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("status", 0);
			params.put("group_type", 1);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status and obj.group_type=:group_type",
					params, -1, -1);
			if (groups.size() > 0) {
				GroupLifeGoodsQueryObject ggqo = new GroupLifeGoodsQueryObject("1", mv, "group_recommend", "desc");
				ggqo.addQuery("obj.group_status", new SysMap("group_status", 1), "=");
				ggqo.addQuery("obj.group.id", new SysMap("group_id", obj.getGroup().getId()), "=");
				ggqo.addQuery("obj.id", new SysMap("goods_id", obj.getId()), "!=");
				ggqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), "<=");
				ggqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), ">=");
				ggqo.setPageSize(4);
				IPageList pList = this.groupLifeGoodsService.list(ggqo);
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
			}
		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}
		mv.addObject("groupViewTools", groupViewTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/wap/group_evaluation.htm")
	public ModelAndView group_evaluation(HttpServletRequest request, HttpServletResponse response, String id,
			String goods_id, String currentPage) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		ModelAndView mv = new JModelAndView("wap/group_evaluate.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0), "=");
		qo.setPageSize(8);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/wap/group_evaluation.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("store", store);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("goods", goods);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping("/wap/group_order.htm")
	public ModelAndView group_order(HttpServletRequest request, HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("wap/group_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.setPageSize(8);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/wap/group_order.htm", "", "", pList, mv);
		logger.info(pList.getPages());
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "生活类团购订单购买", value = "/wap/life_order.htm*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping("/wap/life_order.htm")
	public ModelAndView life_order(HttpServletRequest request, HttpServletResponse response, String gid) {
		ModelAndView mv = new JModelAndView("wap/life_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		GroupLifeGoods group = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(gid));
		if (group != null) {
			if (!user.getId().equals(group.getUser().getId())) {
				if (group.getGroup_count() != 0 && group.getGroup_count() <= group.getGroupInfos().size()
						|| group.getEndTime().before(new Date())) {
					mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "团购已到期或已售完。");
					mv.addObject("url", CommUtil.getURL(request) + "/group/index.htm?type=life");
				} else {
					if (user.getMobile() != null && !"".equals(user.getMobile())) {
						mv.addObject("user", user);
						mv.addObject("obj", group);
						String orderForm_session = CommUtil.randomString(32);
						request.getSession(true).setAttribute("orderForm_session", orderForm_session);
						mv.addObject("orderForm_session", orderForm_session);
					} else {
						mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "请先绑定您的手机");
						mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/account_mobile.htm");
					}
				}
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "无法购买自己的团购商品");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/group/index.htm?type=life");
			}

		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "您所访问的团购不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/group/index.htm?type=life");
		}

		return mv;
	}

	@SecurityMapping(title = "生活类团购订单保存", value = "/wap/life_order_save.htm*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping("/wap/life_order_save.htm")
	public ModelAndView life_order_save(HttpServletRequest request, HttpServletResponse response, String orderForm_session,
			String group_id, String order_count) {
		if (CommUtil.null2Int(order_count) <= 0) {
			order_count = "1";
		}
		ModelAndView mv = new JModelAndView("wap/life_order_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("user", user);
		GroupLifeGoods group = null;
		if (!StringUtils.isNullOrEmpty(group_id)) {
			group = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(group_id));
			String orderForm_session2 = CommUtil.null2String(request.getSession(true).getAttribute("orderForm_session"));
			double group_total_price = 0;
			OrderForm orderForm = new OrderForm();
			if (orderForm_session2.equals(orderForm_session)) {
				this.groupLifeGoodsService.update(group);
				group_total_price = CommUtil.null2Double(group.getGroup_price()) * CommUtil.null2Int(order_count);
				orderForm.setAddTime(new Date());
				orderForm.setUser_id(user.getId().toString());
				orderForm.setUser_name(user.getUserName());
				Map json = new HashMap();
				json.put("goods_id", group.getId().toString());
				json.put("goods_name", group.getGg_name());
				json.put("goods_type", group.getGoods_type());
				json.put("goods_price", group.getGroup_price());
				json.put("goods_count", CommUtil.null2Int(order_count));
				json.put("goods_total_price", group_total_price);
				json.put("goods_mainphoto_path", group.getGroup_acc().getPath() + "/" + group.getGroup_acc().getName());
				orderForm.setGroup_info(Json.toJson(json, JsonFormat.compact()));
				if (group.getGoods_type() == 0) {
					if (group.getUser().getStore() != null) {
						orderForm.setStore_id(group.getUser().getStore().getId().toString());
					}
					orderForm.setOrder_form(0);
				} else {
					orderForm.setOrder_form(1);
				}
				orderForm.setTotalPrice(BigDecimal.valueOf(group_total_price));
				orderForm.setOrder_id("life" + SecurityUserHolder.getCurrentUser().getId()
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
				orderForm.setOrder_status(10);
				orderForm.setOrder_cat(2);
				request.getSession(true).removeAttribute("orderForm_session");
				this.orderFormService.save(orderForm);
				mv.addObject("order_count", order_count);
				String orderpayment_session = CommUtil.randomString(32);
				request.getSession(true).setAttribute("orderpayment_session", orderpayment_session);
				mv.addObject("orderpayment_session", orderpayment_session);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setOf(orderForm);
				ofl.setLog_info("提交订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				this.orderFormLogService.save(ofl);
				mv.addObject("order", orderForm);
				mv.addObject("all_of_price", BigDecimal.valueOf(group_total_price));
				mv.addObject("paymentTools", paymentTools);
				mv.addObject("group", true);
				
				String mark = PropertyUtil.getProperty("wap_pay_center_mark");
				if("1".equals(mark)){
					String url = PropertyUtil.getProperty("wap_return_url_group");
					String returnUrl = CommUtil.getURL(request) + url; //回调业务系统地址
					mv = (ModelAndView) this.orderFormService.orderPay(request, response, user, orderForm, ChannelEnum.WAP.toString(), OrderTypeEnum.SHOPPING.getIndex(), returnUrl);
				}
		
			} else {
				mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "禁止重复提交");
				mv.addObject("url", CommUtil.getURL(request) + "/group/index.htm?type=life");
				request.getSession(true).removeAttribute("orderForm_session");
			}

		} else {
			mv = new JModelAndView("wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "此页面不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/group/index.htm?type=life");
			request.getSession(true).removeAttribute("orderForm_session");
		}

		return mv;
	}

	/**
	 * 数量调整
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "团购订单数量", value = "/wap/group_count_adjust.htm*", rtype = "user", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping("/wap/group_count_adjust.htm")
	public void group_count_adjust(HttpServletRequest request, HttpServletResponse response, String group_id, String count) {
		double group_total_price = 0;
		String error = "100"; // 100表示修改成功，200表示库存不足
		GroupLifeGoods group = null;
		if (!StringUtils.isNullOrEmpty(group_id)) {
			group = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(group_id));
		}
		if (CommUtil.null2Int(count) > group.getGroup_count()) {
			error = "200";
			count = CommUtil.null2String(group.getGroup_count());
		}
		group_total_price = CommUtil.null2Double(CommUtil.null2Double(group.getGroup_price()) * CommUtil.null2Int(count));
		DecimalFormat df = new DecimalFormat("0.00");
		Map map = new HashMap();
		map.put("count", count);
		map.put("group_total_price", Double.valueOf(df.format(group_total_price)));
		map.put("error", error);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			logger.info(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
