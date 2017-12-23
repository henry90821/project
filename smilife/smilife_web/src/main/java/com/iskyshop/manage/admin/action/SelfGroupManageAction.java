package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.GroupClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: GroupSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台平台商自营团购控制器。平台商也可以发布团购商品
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
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class SelfGroupManageAction {
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
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;

	@SecurityMapping(title = "自营商品类团购商品列表", value = "/admin/group_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self.htm")
	public ModelAndView group_self(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String gg_name) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.gg_goods.goods_type", new SysMap("gg_goods_goods_type", 0), "=");
		if (!"".equals(CommUtil.null2String(gg_name))) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name + "%"), "like");
		}
		IPageList pList = this.groupGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}

	/**
	 * grouplife_self列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营生活类团购商品列表", value = "/admin/grouplife_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_self.htm")
	public ModelAndView grouplife_self(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String gg_name) {
		ModelAndView mv = new JModelAndView("admin/blue/grouplife_self.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 1), "=");
		if (!"".equals(CommUtil.null2String(gg_name))) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name + "%"), "like");
		}
		IPageList pList = this.grouplifegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}

	@SecurityMapping(title = "自营商品类团购商品添加", value = "/admin/group_self_add.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_add.htm")
	public ModelAndView group_self_add(HttpServletRequest request, HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int g_type = 0;
		if ("life".equals(type)) {
			g_type = 1;
			mv = new JModelAndView("admin/blue/grouplife_self_add.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		params.put("g_type", g_type);
		List<Group> groups = this.groupService.query(
				"select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type= :g_type and  (obj.status=0 or obj.status=1)",
				params, -1, -1);
		List<GroupArea> gas = this.groupAreaService.query(
				"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1, -1);
		params.remove("joinEndTime");
		params.remove("status");
		List<GroupClass> gcs = this.groupClassService.query(
				"select obj from GroupClass obj where obj.gc_type=:g_type and obj.parent.id is null order by obj.gc_sequence asc",
				params, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "自营团购商品编辑", value = "/admin/group_self_edit.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_edit.htm")
	public ModelAndView group_self_edit(HttpServletRequest request, HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GroupArea> gas = this.groupAreaService.query(
				"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", null, -1, -1);
		List<GroupClass> gcs = this.groupClassService.query(
				"select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", null, -1, -1);
		if ("goods".equals(type)) {
			GroupGoods obj = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("group_type", 0);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.group_type=:group_type and  (obj.status=0 or obj.status=1)", params,
					-1, -1);
			mv.addObject("groups", groups);
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.getObjById(CommUtil.null2Long(id));
			mv = new JModelAndView("admin/blue/grouplife_self_add.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("group_type", 1);
			List<Group> groups = this.groupService.query(
					"select obj from Group obj where obj.group_type=:group_type and  (obj.status=0 or obj.status=1)", params,
					-1, -1);
			mv.addObject("groups", groups);
		}
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		return mv;

	}

	@SecurityMapping(title = "自营团购商品", value = "/admin/group_goods_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_goods_self.htm")
	public ModelAndView group_goods_self(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/group_goods_self.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/admin/group_goods_self_load.htm")
	public void group_goods_self_load(HttpServletRequest request, HttpServletResponse response, String goods_name,
			String gc_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("seckill_buy", 0);
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("combin_status", 0);
		params.put("seckill_buy", 0);
		String query = "select new Goods(id,goods_name,goods_current_price,goods_inventory) from Goods obj where obj.goods_name like:goods_name and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status and obj.group_buy=:group_buy and obj.seckill_buy=:seckill_buy and obj.goods_status=:goods_status and obj.goods_type=:goods_type and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status and obj.enough_reduce=:enough_reduce and obj.seckill_buy=:seckill_buy";
		if (!StringUtils.isNullOrEmpty(gc_id)) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
			Set<Long> ids = this.genericGcIds(gc);
			params.put("ids", ids);
			query = query + " and obj.gc.id in (:ids)";
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getGoods_current_price());
			map.put("goods_name", obj.getGoods_name());
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

	@SecurityMapping(title = "商品类团购商品保存", value = "/admin/group_goods_self_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_goods_self_save.htm")
	public ModelAndView group_goods_self_save(HttpServletRequest request, HttpServletResponse response, String id,
			String group_id, String goods_id, String gc_id, String ga_id, String gg_price) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
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
			map = CommUtil.saveFileToServer(request, "gg_acc", saveFilePathName, "", null);
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/group"));
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
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/group"));
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
		boolean wrire_vo = gg.getBeginTime().before(new Date());
		if (wrire_vo) {
			gg.setGg_status(1);
			goods.setGroup_buy(2);
			goods.setGoods_current_price(gg.getGg_price());
			goods.setGroup(gg.getGroup());
		} else {
			gg.setGg_status(2);
			goods.setGroup_buy(4);
		}
		
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		if (StringUtils.isNullOrEmpty(id)) {
			this.groupGoodsService.save(gg);
			if (wrire_vo) {
				LuceneVo vo = luceneVoTools.updateGroupGoodsIndex(gg);
				lucene.writeIndex(vo);
			}

		} else {
			this.groupGoodsService.update(gg);
			if (wrire_vo) {
				LuceneVo vo = luceneVoTools.updateGroupGoodsIndex(gg);

				lucene.update(CommUtil.null2String(gg.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
			}
		}
		this.goodsService.update(goods);
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/group_self.htm");
		if (id != null && !StringUtils.isNullOrEmpty(id)) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request) + "/admin/group_self_add.htm?type=goods");
		}
		return mv;
	}

	@SecurityMapping(title = "生活类团购商品保存", value = "/admin/group_lifegoods_self_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifegoods_self_save.htm")
	public ModelAndView group_lifegoods_self_save(HttpServletRequest request, HttpServletResponse response, String id,
			String group_id, String gc_id, String ga_id, String beginTime, String endTime, String group_price,
			String cost_price) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		WebForm wf = new WebForm();
		GroupLifeGoods grouplifegoods = null;
		if (StringUtils.isNullOrEmpty(id)) {
			grouplifegoods = wf.toPo(request, GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.getObjById(Long.parseLong(id));
			grouplifegoods = (GroupLifeGoods) wf.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(1);
		boolean wrire_vo = grouplifegoods.getBeginTime().before(new Date());
		if (wrire_vo) {
			grouplifegoods.setGroup_status(1);
		} else {
			grouplifegoods.setGroup_status(2);
		}
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
		if (StringUtils.isNullOrEmpty(id)) {
			this.grouplifegoodsService.save(grouplifegoods);
		} else
			this.grouplifegoodsService.update(grouplifegoods);
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
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/group"));
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
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/group"));
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
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		if (StringUtils.isNullOrEmpty(id)) {
			this.grouplifegoodsService.save(grouplifegoods);
			LuceneVo vo = this.luceneVoTools.updateLifeGoodsIndex(grouplifegoods);
			lucene.writeIndex(vo);
		} else {
			this.grouplifegoodsService.update(grouplifegoods);
			LuceneVo vo = this.luceneVoTools.updateLifeGoodsIndex(grouplifegoods);
			lucene.update(grouplifegoods.getId().toString(), vo);
		}
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/grouplife_self.htm");
		if (id != null && !StringUtils.isNullOrEmpty(id)) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request) + "/admin/group_self_add.htm?type=life");
		}
		return mv;
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	@SecurityMapping(title = "团购商品删除", value = "/admin/group_self_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_del.htm")
	public String group_self_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupGoods gg = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);
			goods.setGroup(null);
			goods.setGoods_current_price(goods.getStore_price());
			this.goodsService.update(goods);
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
			CommUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:/admin/group_self.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "生活类团购商品删除", value = "/admin/group_lifeself_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifeself_del.htm")
	public String group_lifeself_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				GroupLifeGoods grouplifegoods = this.grouplifegoodsService.getObjById(Long.parseLong(id));
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
				this.grouplifegoodsService.delete(Long.parseLong(id));
			}
		}
		return "redirect:grouplife_self?currentPage=" + currentPage;
	}

	@RequestMapping("/verify_gourp_begintime.htm")
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

	@RequestMapping("/verify_gourp_endtime.htm")
	public void verify_gourp_endtime(HttpServletRequest request, HttpServletResponse response, String endTime,
			String group_id) {
		boolean ret = false;
		Group group = this.groupService.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(endTime);
		if (date.before(group.getEndTime())) {
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

	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/admin/group_lifeself_ajax.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifeself_ajax.htm")
	public void group_lifeself_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupLifeGoods obj = this.grouplifegoodsService.getObjById(Long.parseLong(id));
		Field[] fields = GroupLifeGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				}
				if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!"".equals(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.grouplifegoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter(); 
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

	}

	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/admin/group_self_ajax.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_ajax.htm")
	public void group_self_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupGoods obj = this.groupGoodsService.getObjById(Long.parseLong(id));
		Field[] fields = GroupGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				}
				if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!"".equals(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.groupGoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

	}

	@SecurityMapping(title = "生活购订单列表", value = "/admin/grouplife_selforder.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_selforder.htm")
	public ModelAndView grouplife_selforder(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/grouplife_selforder.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 0), "=");// 无需查询主订单
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "=");
		if (!StringUtils.isNullOrEmpty(status)) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status", CommUtil.null2Int(status)), "=");
		}
		if (!"".equals(CommUtil.null2String(order_id))) {
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

	@SecurityMapping(title = "生活购消费码列表", value = "/admin/grouplife_selfinfo.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_selfinfo.htm")
	public ModelAndView grouplife_selfinfo(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String status, String info_id,String mobile,String beginTime,String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/grouplife_selfinfo.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 1), "=");
		if (!"".equals(CommUtil.null2String(info_id))) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", info_id), "=");
			mv.addObject("info_id", info_id);
		}
		if (!StringUtils.isNullOrEmpty(status)) {
			qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status)), "=");
		}
		if (!StringUtils.isNullOrEmpty(mobile)) {
			qo.addQuery("obj.user_mobile", new SysMap("user_mobile", mobile), "=");
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
		IPageList pList = this.groupinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/buyer/grouplife_selfinfo.htm", "", params, pList, mv);
		mv.addObject("status", status);
		mv.addObject("mobile", mobile);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}

	@SecurityMapping(title = "生活购订单取消", value = "/admin/lifeorder_cancel.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/lifeorder_cancel.htm")
	public String lifeorder_cancel(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		int old_status = of.getOrder_status();
		of.setOrder_status(0);
		this.orderFormService.update(of);
		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setLog_info("取消订单。原订单状态为：" + old_status);
		ofl.setLog_user(null);
		ofl.setOf(of);
		ofl.setState_info("原订单状态为：" + old_status);			
		this.orderFormLogService.save(ofl);
		
		return "redirect:" + "/admin/grouplife_selforder.htm";
	}

	@SecurityMapping(title = "生活购订单详细", value = "/admin/lifeorder_view.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/lifeorder_view.htm")
	public ModelAndView lifeorder_view(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id, String info_id, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/lifeorder_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getOrder_form() == 1 && obj.getOrder_status() == 20) {
			Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());

			String params = "";
			GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, "", "");
			if (!StringUtils.isNullOrEmpty(status)) {
				qo.addQuery("obj.status", new SysMap("status", CommUtil.null2Int(status)), "=");
			}
			qo.addQuery("obj.order_id", new SysMap("order_id", obj.getId()), "=");
			qo.addQuery("obj.lifeGoods.id", new SysMap("goods_id", CommUtil.null2Long(json.get("goods_id").toString())),
					"=");
			if (!StringUtils.isNullOrEmpty(info_id)) {
				qo.addQuery("obj.group_sn", new SysMap("info_id", info_id), "=");
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupInfo.class, mv);
			IPageList pList = this.groupinfoService.list(qo);
			CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/buyer/lifeorder_view.htm", "", params, pList, mv);
			GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(json.get("goods_id")));
			mv.addObject("infos", pList.getResult());
			mv.addObject("order", obj);
			mv.addObject("goods", goods);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/group.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "消费码退款", value = "/admin/grouplife_return_confirm.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_return_confirm.htm")
	public ModelAndView grouplife_return_confirm(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/grouplife_return_confirm.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo info = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", info);
		return mv;
	}

	@SecurityMapping(title = "消费码退款保存", value = "/admin/grouplife_return_confirm_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_return_confirm_save.htm")
	public String grouplife_return_confirm_save(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		GroupInfo info = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		info.setStatus(5);// 自营确认退款，后平台进行退款
		this.groupinfoService.update(info);
		return "redirect:/admin/grouplife_selfinfo.htm";
	}

	@SecurityMapping(title = "团购码管理", value = "/admin/group_code.htm*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping("/admin/group_code.htm")
	public ModelAndView group_code(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/group_code.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "生活购订单使用", value = "/admin/use_lifeinfo.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/use_lifeinfo.htm")
	public String use_lifeinfo(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupinfoService.getObjById(CommUtil.null2Long(id));
		if (info != null && info.getStatus() == 0) {
			info.setStatus(1);
			this.groupinfoService.update(info);
		}
		return "redirect:" + "/admin/grouplife_selforder.htm";
	}

	@SecurityMapping(title = "生活购订单使用", value = "/admin/check_group_code.htm*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping("/admin/check_group_code.htm")
	public void check_group_code(HttpServletRequest request, HttpServletResponse response, String value) {
		String code = "0";// 不存在
		GroupInfo info = this.groupinfoService.getObjByProperty(null, "group_sn", value);
		if (info != null) {
			if (info.getStatus() == 1) {
				code = "-30";// 过期
			}
			if (info.getStatus() == -1) {
				code = "-50";// 过期
			}
			if (info.getStatus() == 3) {
				code = "-100";// 申请退款中
			}
			if (info.getStatus() == 5) {
				code = "-150";// 平台退款中
			}
			if (info.getStatus() == 7) {
				code = "-200";// 已经退款
			}
			if (info.getStatus() == 0) {
				info.setStatus(1);
				this.groupinfoService.update(info);
				code = "100";// 成功
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
	
	//平台后台团购码导出Excel。
		@SecurityMapping(title = "导出表格", value = "/admin/group_excel.htm*", rtype = "admin", rname = "自营团购码导出", rcode = "group_self", rgroup = "自营")
		@RequestMapping("/admin/group_excel.htm")
		public void group_excel(HttpServletRequest request, HttpServletResponse response, String order_status, String info_id,
				String beginTime, String endTime, String mobile) {
			GroupInfoQueryObject qo = new GroupInfoQueryObject();
			qo.setPageSize(1000000000);
			qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 1), "=");
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
			IPageList pList = this.groupinfoService.list(qo);
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
				String excel_name = "自营团购码列表" + sdf.format(new Date());;
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
