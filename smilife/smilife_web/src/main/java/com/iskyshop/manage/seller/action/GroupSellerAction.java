package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.GroupClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.SalesLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.query.SalesLogQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISalesLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * 
 * <p>
 * Title:GroupSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心团购管理控制器
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
 * @author jy
 * 
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class GroupSellerAction {
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
	private IGroupClassService groupClassService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private ISalesLogService salesLogService;
	@Autowired
	private FTPServerTools ftpTools;

	@SecurityMapping(title = "卖家团购列表", value = "/seller/group.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group.htm")
	public ModelAndView group(HttpServletRequest request, HttpServletResponse response, String currentPage, String gg_name,
			String type) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (StringUtils.isNullOrEmpty(type)) {
			type = "goods";
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.gg_goods.goods_store.user.id", new SysMap("user_id", user.getId()), "=");
			if (!StringUtils.isNullOrEmpty(gg_name)) {
				qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name + "%"), "like");
			}
			IPageList pList = this.groupGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		} else {
			mv = new JModelAndView("user/default/sellercenter/group_life.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			type = "life";
			GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
			if (!StringUtils.isNullOrEmpty(gg_name)) {
				qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name + "%"), "like");
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupLifeGoods.class, mv);
			IPageList pList = this.groupLifeGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		}
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "卖家团购添加", value = "/seller/group_add.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_add.htm")
	public ModelAndView group_add(HttpServletRequest request, HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();  
		Store store = user.getStore();
		boolean ret = true;
		if (store.getGroup_meal_endTime() == null) {
			ret = false;
		} else {
			if (new Date().after(store.getGroup_meal_endTime())) {
				ret = false;
			}
		}
		if (ret) {
			if (StringUtils.isNullOrEmpty(type)) {
				type = "life";
			}
			if ("life".equals(type)) {
				mv = new JModelAndView("user/default/sellercenter/group_life_add.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				params.put("joinEndTime", new Date());
				List<GroupLifeGoods> groupGoods = this.groupLifeGoodsService
						.query("select obj from GroupLifeGoods obj where obj.endTime>:joinEndTime and obj.group_status!=-1 and obj.user.id="
								+ user.getId(), params, -1, -1);
				params.put("type", 1);
				List<Group> groups = this.groupService.query(
						"select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=:type and  (obj.status=0 or obj.status=1)",
						params, -1, -1);
				List<GroupArea> gas = this.groupAreaService.query(
						"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1,
						-1);
				params.clear();
				params.put("type", 1);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=:type and obj.parent.id is null order by obj.gc_sequence asc",
						params, -1, -1);
				if (groups.size() == 0 || gas.size() == 0 || gcs.size() == 0) {// 后台团购尚未开启
					mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request, response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/group.htm");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gas", gas);
				mv.addObject("groups", groups);
			} else {
				params.put("joinEndTime", new Date());
				List<GroupGoods> groupGoods = this.groupGoodsService
						.query("select obj from GroupGoods obj where obj.endTime>:joinEndTime and obj.gg_status!=-1 and obj.gg_goods.goods_store.id="
								+ user.getStore().getId(), params, -1, -1);
				params.put("type", 0);
				List<Group> groups = this.groupService.query(
						"select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=:type and  (obj.status=0 or obj.status=1) ",
						params, -1, -1);
				List<GroupArea> gas = this.groupAreaService.query(
						"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1,
						-1);
				params.clear();
				params.put("type", 0);
				List<GroupClass> gcs = this.groupClassService.query(
						"select obj from GroupClass obj where obj.gc_type=:type and obj.parent.id is null order by obj.gc_sequence asc",
						params, -1, -1);
				if (groups.size() == 0 || gas.size() == 0 || gcs.size() == 0) {// 后台团购尚未开启
					mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request, response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/group.htm");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gas", gas);
				mv.addObject("groups", groups);
			}
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有购买团购套餐");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/group_meal.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家团购编辑", value = "/seller/group_lifeedit.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_lifeedit.htm")
	public ModelAndView group_lifeedit(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_life_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService.query(
				"select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=1 and (obj.status=0 or obj.status=1)",
				params, -1, -1);
		List<GroupArea> gas = this.groupAreaService.query(
				"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1, -1);
		List<GroupClass> gcs = this.groupClassService.query(
				"select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", null, -1, -1);
		GroupLifeGoods obj = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购编辑", value = "/seller/group_edit.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_edit.htm")
	public ModelAndView group_edit(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService.query(
				"select obj from Group obj where obj.joinEndTime>=:joinEndTime  and obj.group_type=0 and  (obj.status=0 or obj.status=1)",
				params, -1, -1);
		List<GroupArea> gas = this.groupAreaService.query(
				"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1, -1);
		List<GroupClass> gcs = this.groupClassService.query(
				"select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", null, -1, -1);
		GroupGoods obj = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购商品", value = "/seller/group_goods.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_goods.htm")
	public ModelAndView group_goods(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<UserGoodsClass> gcs = this.userGoodsClassService.query(
				"select obj from UserGoodsClass obj where obj.parent.id is null and obj.user_id=:user_id order by obj.sequence asc",
				params, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/seller/group_goods_load.htm")
	public void group_goods_load(HttpServletRequest request, HttpServletResponse response, String goods_name, String gc_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("as", 0);
		params.put("combin_status", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("combin_status", 0);
		params.put("seckill_buy", 0);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		params.put("store_id", store.getId());
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
		Set<Long> ids = this.genericUserGcIds(ugc);
		List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
		for (Long g_id : ids) {
			UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
			ugc_list.add(temp_ugc);
		}
		String query = "select new Goods(id,goods_name,goods_current_price,goods_inventory) from Goods obj where obj.goods_name like :goods_name and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status  and obj.group_buy=:group_buy and obj.goods_store.id=:store_id and obj.activity_status=:as and obj.combin_status=:combin_status and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status and obj.enough_reduce=:enough_reduce and obj.goods_status=:goods_status and obj.seckill_buy=:seckill_buy";
		for (int i = 0; i < ugc_list.size(); i++) {
			if (i == 0) {
				query = query + " and (:ugc" + i + " member of obj.goods_ugcs";
				if (ugc_list.size() == 1) {
					query = query + ")";
				}
			} else {
				if (i == ugc_list.size() - 1) {
					query = query + " or :ugc" + i + " member of obj.goods_ugcs)";
				} else
					query = query + " or :ugc" + i + " member of obj.goods_ugcs";
			}
			params.put("ugc" + i, ugc_list.get(i));
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_price", obj.getGoods_current_price());
			map.put("store_inventory", obj.getGoods_inventory());
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@SecurityMapping(title = "商品类团购商品保存", value = "/seller/group_goods_save.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_goods_save.htm")
	public void group_goods_save(HttpServletRequest request, HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id, String gg_price) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		WebForm wf = new WebForm();
		GroupGoods gg = null;
		if (StringUtils.isNullOrEmpty(id)) {
			gg = wf.toPo(request, GroupGoods.class);
			gg.setAddTime(new Date());
		} else {
			GroupGoods obj = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
			gg = (GroupGoods) wf.toPo(request, obj);
		}
//		gg.setGg_count(gg.getGg_count());
		Group group = this.groupService.getObjById(CommUtil.null2Long(group_id));
		gg.setGroup(group);
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		gg.setGg_goods(goods);
		GroupClass gc = this.groupClassService.getObjById(CommUtil.null2Long(gc_id));
		gg.setGg_gc(gc);
		GroupArea ga = this.groupAreaService.getObjById(CommUtil.null2Long(ga_id));
		gg.setGg_ga(ga);
//		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(10, CommUtil.div(gg_price, goods.getStore_price()))));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = new HashMap();
		try {
			String fileName = gg.getGg_img() == null ? "" : gg.getGg_img().getName();
			map = CommUtil.saveFileToServer(request, "gg_acc", saveFilePathName, fileName, null);
			if (StringUtils.isNullOrEmpty(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.userUpload(gg_img.getName(), "/group", CommUtil.null2String(user.getId())));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					gg.setGg_img(gg_img);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = gg.getGg_img();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.userUpload(gg_img.getName(), "/group", CommUtil.null2String(user.getId())));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		gg.setGg_rebate(
				BigDecimal.valueOf(CommUtil.div(CommUtil.mul(gg.getGg_price(), 10), gg.getGg_goods().getGoods_price())));
		
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		LuceneUtil.setIndex_path(goods_lucene_path);
		lucene.setConfig(this.configService.getSysConfig());
		lucene.setIndex_path(goods_lucene_path);
		if (StringUtils.isNullOrEmpty(id)) {
			this.groupGoodsService.save(gg);
			LuceneVo vo = luceneVoTools.updateGroupGoodsIndex(gg);
			lucene.writeIndex(vo);
		} else {
			this.groupGoodsService.update(gg);
			LuceneVo vo = luceneVoTools.updateGroupGoodsIndex(gg);
			lucene.update(CommUtil.null2String(gg.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
		}
//		goods.setGoods_current_price(gg.getGg_price());
		goods.setGroup_buy(1);
		// 商品的团购价格
		this.goodsService.update(goods);
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "申请团购成功");
		json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@SecurityMapping(title = "生活类团购商品保存", value = "/seller/grouplife_goods_save.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_goods_save.htm")
	public void grouplife_goods_save(HttpServletRequest request, HttpServletResponse response, String id, String group_id,
			String gc_id, String ga_id, String beginTime, String endTime, String group_price, String cost_price) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		WebForm wf = new WebForm();
		GroupLifeGoods grouplifegoods = null;
		if (StringUtils.isNullOrEmpty(id)) {
			grouplifegoods = wf.toPo(request, GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.groupLifeGoodsService.getObjById(Long.parseLong(id));
			grouplifegoods = (GroupLifeGoods) wf.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(0);
		grouplifegoods.setGroup_status(0);
		GroupArea gg_ga = this.groupAreaService.getObjById(CommUtil.null2Long(ga_id));
		grouplifegoods.setGg_ga(gg_ga);
		GroupClass gg_gc = this.groupClassService.getObjById(CommUtil.null2Long(gc_id));
		grouplifegoods.setGg_gc(gg_gc);
		Group group = this.groupService.getObjById(CommUtil.null2Long(group_id));
		grouplifegoods.setGroup(group);
		grouplifegoods.setUser(user);
		grouplifegoods.setBeginTime(CommUtil.formatDate(beginTime));
		grouplifegoods.setEndTime(CommUtil.formatDate(endTime));
		grouplifegoods.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(10, CommUtil.div(group_price, cost_price))));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = new HashMap();
		try {
			String fileName = grouplifegoods.getGroup_acc() == null ? "" : grouplifegoods.getGroup_acc().getName();
			map = CommUtil.saveFileToServer(request, "group_acc", saveFilePathName, fileName, null);
			if (StringUtils.isNullOrEmpty(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.userUpload(gg_img.getName(), "/group", CommUtil.null2String(user.getId())));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					grouplifegoods.setGroup_acc(gg_img);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = grouplifegoods.getGroup_acc();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.userUpload(gg_img.getName(), "/group", CommUtil.null2String(user.getId())));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		if (StringUtils.isNullOrEmpty(id)) {
			this.groupLifeGoodsService.save(grouplifegoods);
		} else {
			this.groupLifeGoodsService.update(grouplifegoods);
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "生活类团购商品保存成功");
		json.put("url", CommUtil.getURL(request) + "/seller/group.htm?type=life");
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		if (ugc != null) {
			ids.add(ugc.getId());
			for (UserGoodsClass child : ugc.getChilds()) {
				Set<Long> cids = genericUserGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	@SecurityMapping(title = "团购商品删除", value = "/seller/group_del.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_del.htm")
	public String group_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupGoods gg = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);
			// 删除索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "groupgoods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
			this.goodsService.update(goods);
			CommUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:group.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购商品删除", value = "/seller/group_lifedel.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_lifedel.htm")
	public String group_lifedel(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupLifeGoods gg = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(id));
			if (gg != null && gg.getUser().getId().equals(user.getId())) {
				CommUtil.del_acc(request, gg.getGroup_acc());
				// 删除索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
						+ File.separator + "lifegoods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.delete_index(CommUtil.null2String(id));
				this.groupLifeGoodsService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:group.htm?type=life&currentPage=" + currentPage + "type=life";
	}

	@SecurityMapping(title = "团购套餐购买", value = "/seller/group_meal.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal.htm")
	public ModelAndView group_meal(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_meal.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "团购套餐购买", value = "/seller/group_meal_save.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal_save.htm")
	public void group_meal_save(HttpServletRequest request, HttpServletResponse response, String meal_day)
			throws ParseException {
		Map json = new HashMap();
		json.put("ret", false);
		if (configService.getSysConfig().isGroupBuy()) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = configService.getSysConfig().getGroup_meal_gold();
			int days = 30;
			if ("30".equals(meal_day)) {
				days = 30;
			}
			if ("90".equals(meal_day)) {
				days = 90;
			}
			if ("180".equals(meal_day)) {
				days = 180;
			}
			if ("360".equals(meal_day)) {
				days = 360;
			}
			int costday = days / 30;
			if (user.getGold() >= costday * cost) {
				user.setGold(user.getGold() - costday * cost);
				this.userService.update(user);
				Date day = user.getStore().getGroup_meal_endTime();
				Date d = new Date();
				if (day != null) {
					if (day.after(new Date())) {
						user.getStore().setGroup_meal_endTime(
								this.addDate(user.getStore().getGroup_meal_endTime(), CommUtil.null2Long(days)));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore().getGroup_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore().getGroup_meal_endTime(), CommUtil.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4); // 促销类型为满就送
						this.salesLogService.save(c_log);
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setGroup_meal_endTime(CommUtil.formatDate(latertime, "yyyy-MM-dd HH:mm:ss"));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore().getGroup_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore().getGroup_meal_endTime(), CommUtil.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4); // 促销类型为满就送
						this.salesLogService.save(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(ca.DATE, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setGroup_meal_endTime(CommUtil.formatDate(latertime, "yyyy-MM-dd HH:mm:ss"));
					this.storeService.update(user.getStore());
					// 记录金币日志
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买团购套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.save(log);
					// 保存套餐购买信息
					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(user.getStore().getGroup_meal_endTime());
					c_log.setEnd_time(this.addDate(user.getStore().getGroup_meal_endTime(), CommUtil.null2Long(days)));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(4); // 促销类型为满就送
					this.salesLogService.save(c_log);
				}
				json.put("ret", true);
				json.put("op_title", "购买成功");
				json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()), response);
			} else {
				json.put("ret", false);
				json.put("op_title", "您的金币不足，无法购买团购套餐");
				json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()), response);
			}
		} else {
			json.put("ret", false);
			json.put("op_title", "购买失败");
			json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		}
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}

	@RequestMapping("/seller/verify_gourp_begintime.htm")
	public void verify_gourp_begintime(HttpServletRequest request, HttpServletResponse response, String beginTime,
			String group_id) {
		boolean ret = false;
		Group group = this.groupService.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(beginTime);
		if (date.after(group.getBeginTime())
				|| CommUtil.formatLongDate(date).equals(CommUtil.formatLongDate(group.getBeginTime()))) {
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@RequestMapping("/seller/verify_gourp_endtime.htm")
	public void verify_gourp_endtime(HttpServletRequest request, HttpServletResponse response, String endTime,
			String group_id, String beginTime) {
		boolean ret = false;
		Group group = this.groupService.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(endTime);
		Date bdate = CommUtil.formatDate(beginTime);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (date.before(group.getEndTime()) && date.before(store.getGroup_meal_endTime())) {
			ret = true;
			if (date.after(bdate)) {
				ret = true;
			} else {
				ret = false;
			}
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "生活购订单列表", value = "/seller/grouplife_order.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_order.htm")
	public ModelAndView grouplife_selforder(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id, String status) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/grouplife_order.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 0), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 0), "="); // 无需查询主订单
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "=");
		ofqo.addQuery("obj.store_id",
				new SysMap("store_id", SecurityUserHolder.getCurrentUser().getStore().getId().toString()), "=");
		if (!StringUtils.isNullOrEmpty(status)) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status", CommUtil.null2Int(status)), "=");
		}
		if (!StringUtils.isNullOrEmpty(order_id)) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
			mv.addObject("order_id", order_id);
		}
		mv.addObject("orderFormTools", orderFormTools);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "生活购消费码列表", value = "/seller/grouplife_selfinfo.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_selfinfo.htm")
	public ModelAndView grouplife_selfinfo(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String info_id,String mobile,String status,String beginTime,String endTime) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/grouplife_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 0), "=");
		qo.addQuery("obj.lifeGoods.user.id", new SysMap("user_id", user.getId()), "=");
		if (!StringUtils.isNullOrEmpty(info_id)) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", info_id), "=");
			mv.addObject("info_id", info_id);
		}
		if (!StringUtils.isNullOrEmpty(mobile)) {
			qo.addQuery("obj.user_mobile", new SysMap("user_mobile", mobile), "=");
			mv.addObject("mobile", mobile);
		}
		if (!StringUtils.isNullOrEmpty(status)) {
			qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status)), "=");
		} 
		if (!"".equals(CommUtil.null2String(beginTime))) {
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		// start 2015-9-28 需求#61 by liuzheng
		//qo.addQuery("obj.status", new SysMap("status", 0), "!=");
		//WebForm wf = new WebForm();
		//wf.toQueryPo(request, qo, GroupInfo.class, mv);
		// end
		IPageList pList = this.groupInfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("status", status); //查询条件带回页面
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/seller/lifeorder_cancel.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/lifeorder_cancel.htm")
	public String lifeorder_cancel(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (of.getStore_id().equals(user.getStore().getId().toString())) {
			int old_status = of.getOrder_status();
			of.setOrder_status(0);
			this.orderFormService.update(of);
			
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单。原订单状态为：" + old_status);
			ofl.setLog_user(user);
			ofl.setOf(of);
			ofl.setState_info("原订单状态为：" + old_status);			
			this.orderFormLogService.save(ofl);
		}
		return "redirect:" + "/seller/grouplife_order.htm";
	}

	@SecurityMapping(title = "使用消费码", value = "/seller/check_group_code.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/check_group_code.htm")
	public void check_group_code(HttpServletRequest request, HttpServletResponse response, String value) {
		String code = "0"; // 不存在
		GroupInfo info = this.groupInfoService.getObjByProperty(null, "group_sn", value);
		if (info != null) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				if (info.getStatus() == 1) {
					code = "-30"; // 过期
				}
				if (info.getStatus() == -1) {
					code = "-50"; // 过期
				}
				if (info.getStatus() == 3) {
					code = "-100"; // 申请退款中
				}
				if (info.getStatus() == 5) {
					code = "-150"; // 平台退款中
				}
				if (info.getStatus() == 7) {
					code = "-200"; // 已经退款
				}
				if (info.getStatus() == 0) {
					info.setStatus(1);
					this.groupInfoService.update(info);
					code = "100"; // 成功
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@SecurityMapping(title = "消费码退款", value = "/seller/grouplife_return_confirm.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/grouplife_return_confirm.htm")
	public ModelAndView grouplife_return_confirm(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/grouplife_return_confirm.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo info = this.groupInfoService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (info != null) {
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				mv.addObject("obj", info);
			}
		}
		return mv;
	}

	@SecurityMapping(title = "消费码退款保存", value = "/seller/grouplife_return_confirm_save.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/grouplife_return_confirm_save.htm")
	public String grouplife_return_confirm_save(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		GroupInfo info = this.groupInfoService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (info != null) {
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				info.setStatus(5); // 商家确认退款，后平台进行退款
				this.groupInfoService.update(info);
			}
		}
		return "redirect:/seller/grouplife_selfinfo.htm";
	}

	public void return_json(String json, HttpServletResponse response) {
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

	@SecurityMapping(title = "虚拟团购码验证", value = "/seller/group_code.htm*", rtype = "seller", rname = "团购套餐", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/group_code.htm")
	public ModelAndView group_code(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_code.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
	
	// add start 2015-9-28 by liuzheng
	@SecurityMapping(title = "生活购消费码使用", value = "/seller/use_lifeinfo.htm*", rtype = "seller", rname = "团购管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/use_lifeinfo.htm")
	public String use_lifeinfo(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupInfoService.getObjById(CommUtil.null2Long(id));
		if (info != null && info.getStatus() == 0) {
			info.setStatus(1);
			this.groupInfoService.update(info);
		}
		return "redirect:" + "/seller/grouplife_selfinfo.htm";
	}
	// add end

	@SecurityMapping(title = "满就送销售套餐日志", value = "/seller/group_meal_log.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal_log.htm")
	public ModelAndView group_meal_log(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/group_meal_log.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		SalesLogQueryObject qo = new SalesLogQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.sales_type", new SysMap("sales_type", 4), "=");
		IPageList pList = this.salesLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	//商家后台团购码导出Excel。
	@SecurityMapping(title = "导出表格", value = "/seller/group_excel.htm*", rtype = "seller", rname = "商家团购码导出", rcode = "group_seller", rgroup = "商家")
	@RequestMapping("/seller/group_excel.htm")
	public void group_excel(HttpServletRequest request, HttpServletResponse response, String order_status, String info_id,
			String beginTime, String endTime, String mobile) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GroupInfoQueryObject qo = new GroupInfoQueryObject();
		qo.setPageSize(1000000000);
		qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 0), "=");
		qo.addQuery("obj.lifeGoods.user.id", new SysMap("user_id", user.getId()), "=");
		if (!StringUtils.isNullOrEmpty(info_id)) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", info_id), "=");
		}
		if (!StringUtils.isNullOrEmpty(mobile)) {
			qo.addQuery("obj.user_mobile", new SysMap("user_mobile", mobile), "=");
		}
		if (!StringUtils.isNullOrEmpty(order_status)) {
			qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(order_status)), "=");
		} 
		if (!"".equals(CommUtil.null2String(beginTime))) {
			String begin = beginTime + " 00:00:00";
			qo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(begin, "yyyy-MM-dd hh:mm:ss")), ">=");
			
		}
		if (!"".equals(CommUtil.null2String(endTime))) {
			String ends = endTime + " 23:59:59";
			qo.addQuery("obj.addTime", new SysMap("endTime", CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss")), "<=");
		}
		//未更改
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		IPageList pList = this.groupInfoService.list(qo);
		if (pList.getResult() != null) {
			List<GroupInfo> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet("团购码列表");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1, 2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 4000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 3000);
			sheet.setColumnWidth(4, 3000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 3000);
			// 创建字体样式
			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor(HSSFColor.BLUE.index);
			// 创建单元格样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置边框
			style.setBottomBorderColor(HSSFColor.RED.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setFont(font); // 设置字体
			// 创建Excel的sheet的一行
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500); // 设定行的高度
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "团购码列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1) + " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle() + title);
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true); // 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("手机号码");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("团购名称");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("团购消费码");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("团购单价");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("使用状态");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("生成时间");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("到期时间");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("操作");
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);
				// 设置单元格的样式格式
				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getUser_mobile());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getLifeGoods().getGg_name());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.genericStar(datas.get(j - 2).getGroup_sn(),2,12));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue("¥"+datas.get(j - 2).getLifeGoods().getGroup_price().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue("未使用");
				if(datas.get(j - 2).getStatus()==1){
					cell.setCellValue("已使用");
				}else if(datas.get(j - 2).getStatus()==-1){
					cell.setCellValue("已过期");
				}else if(datas.get(j - 2).getStatus()==3){
					cell.setCellValue("申请退款");
				}else if(datas.get(j - 2).getStatus()==5){
					cell.setCellValue("退款中");
				}else if(datas.get(j - 2).getStatus()==7){
					cell.setCellValue("退款完成");
				}
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getAddTime().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getRefund_Time().toString());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue("");
				if(datas.get(j - 2).getStatus()==1){
					cell.setCellValue("已使用");
				}else if(datas.get(j - 2).getStatus()==-1){
					cell.setCellValue("已过期");
				}else if(datas.get(j - 2).getStatus()==3){
					cell.setCellValue("申请退款");
				}else if(datas.get(j - 2).getStatus()==5){
					cell.setCellValue("退款中");
				}else if(datas.get(j - 2).getStatus()==7){
					cell.setCellValue("退款完成");
				}

				
			}	
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String excel_name = "商家团购码列表" + sdf.format(new Date());;
			try {
				String path = CommUtil.getServerRealPathFromRequest(request) + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition", "attachment;filename=" + new String((excel_name).getBytes("gbk"),"iso8859-1")+ ".xls"); 
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
	}
}
