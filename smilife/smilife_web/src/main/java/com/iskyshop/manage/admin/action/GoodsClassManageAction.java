package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GoodsType;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.query.GoodsClassQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGoodsTypeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: GoodsClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城商品分类管理控制器，用来管理商城商品分类信息，商城默认支持三级商品分类，遵循京东、一号店等B2B2C商城的商品分类模式
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsClassManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsbrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private IStoreService storeService;

	/**
	 * GoodsClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品分类列表", value = "/admin/goods_class_list.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_list.htm")
	public ModelAndView goods_class_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType,String goodsClass) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClassQueryObject qo = new GoodsClassQueryObject(currentPage, mv, "sequence", "asc");
		qo.addQuery("obj.parent.id is null", null);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsClass.class, mv);
		IPageList pList = this.goodsClassService.list(qo);

		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/goods_class_list.htm", "", "", pList, mv);

		mv.addObject("goodsClass",goodsClass);
		return mv;
	}

	/**
	 * goodsClass添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品分类添加", value = "/admin/goods_class_add.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_add.htm")
	public ModelAndView goods_class_add(HttpServletRequest request, HttpServletResponse response, String pid) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		List<GoodsType> gts = this.goodsTypeService.query("select obj from GoodsType obj", null, -1, -1);
		if (!StringUtils.isNullOrEmpty(pid)) {
			GoodsClass obj = new GoodsClass();
			GoodsClass parent = this.goodsClassService.getObjById(Long.parseLong(pid));
			obj.setParent(parent);
			obj.setDisplay(true);
			obj.setRecommend(true);
			mv.addObject("obj", obj);
		}
		Map params = new HashMap();
		params.put("ap_status", 1);
		params.put("ap_type", "img");
		List<AdvertPosition> aps = this.advertPositionService.query(
				"select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_type=:ap_type order by obj.addTime desc",
				params, -1, -1);
		mv.addObject("aps", aps);
		mv.addObject("gcs", gcs);
		mv.addObject("gts", gts);
		return mv;
	}

	/**
	 * goodsClass编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品分类编辑", value = "/admin/goods_class_edit.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_edit.htm")
	public ModelAndView goods_class_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			GoodsClass goodsClass = this.goodsClassService.getObjById(Long.parseLong(id));
			mv.addObject("obj", goodsClass);
			List<GoodsClass> gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null",
					null, -1, -1);
			List<GoodsType> gts = this.goodsTypeService.query("select obj from GoodsType obj", null, -1, -1);
			Map params = new HashMap();
			params.put("ap_status", 1);
			params.put("ap_type", "img");
			List<AdvertPosition> aps = this.advertPositionService.query(
					"select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_type=:ap_type order by obj.addTime desc",
					params, -1, -1);
			if (goodsClass.getGc_advert() != null) {
				Map map = (Map) Json.fromJson(goodsClass.getGc_advert());
				mv.addObject("adv_type", CommUtil.null2String(map.get("adv_type")));
				mv.addObject("adv_id", CommUtil.null2String(map.get("adv_id")));
				Accessory acc = this.accessoryService.getObjById(CommUtil.null2Long(map.get("acc_id")));
				if (acc != null) {
					mv.addObject("acc_img", acc.getPath() + "/" + acc.getName());
				}
				if (map.get("acc_url") != null) {
					mv.addObject("acc_url", CommUtil.null2String(map.get("acc_url")));
				}
			}
			if (!StringUtils.isNullOrEmpty(goodsClass.getGb_info())) {
				List<Map> map_list = (List<Map>) Json.fromJson(goodsClass.getGb_info());
				List gbs = new ArrayList();
				for (Map map : map_list) {
					GoodsBrand gb = this.goodsbrandService.getObjById(CommUtil.null2Long(map.get("id")));
					if (gb != null) {
						gbs.add(gb);
					}
				}
				mv.addObject("gbs", gbs);
			}
			mv.addObject("aps", aps);
			mv.addObject("gcs", gcs);
			mv.addObject("gts", gts);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodsClass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品分类保存", value = "/admin/goods_class_save.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_save.htm")
	public ModelAndView goods_class_save(HttpServletRequest request, HttpServletResponse response, String id, String pid,
			String goodsTypeId, String currentPage, String list_url, String add_url, String child_link,
			String commission_link, String commission_rate, String guarantee, String guarantee_link, String adv_type,
			String ap_id, String gc_color, String gbs_ids, String gc_adv_url) {
		WebForm wf = new WebForm();
		GoodsClass goodsClass = null;
		if (StringUtils.isNullOrEmpty(id)) {// 添加
			goodsClass = wf.toPo(request, GoodsClass.class);
			goodsClass.setAddTime(new Date());
		} else {// 编辑修改
			GoodsClass obj = this.goodsClassService.getObjById(Long.parseLong(id));
			goodsClass = (GoodsClass) wf.toPo(request, obj);
		}
		GoodsClass parent = this.goodsClassService.getObjById(CommUtil.null2Long(pid));
		if (parent != null) {
			goodsClass.setParent(parent);
			goodsClass.setLevel(parent.getLevel() + 1);
		}
		GoodsType goodsType = this.goodsTypeService.getObjById(CommUtil.null2Long(goodsTypeId));
		goodsClass.setGoodsType(goodsType);
		Set<Long> ids = this.genericIds(goodsClass);
		// 将商品类型关联到下级商品分类
		if (CommUtil.null2Boolean(child_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setGoodsType(goodsType);
					this.goodsClassService.update(gc);
				}
			}
		}
		// 将佣金比例关联到下级商品分类
		if (CommUtil.null2Boolean(commission_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setCommission_rate(new BigDecimal(commission_rate));
					this.goodsClassService.update(gc);
				}
			}
		}
		// 将保证金关联到下级商品分类
		if (CommUtil.null2Boolean(guarantee_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setGuarantee(new BigDecimal(guarantee));
					this.goodsClassService.update(gc);
				}
			}
		}
		// 品牌标识图片
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = new HashMap();
		try {
			String fileName = goodsClass.getIcon_acc() == null ? "" : goodsClass.getIcon_acc().getName();
			map = CommUtil.saveFileToServer(request, "icon_acc", saveFilePathName, fileName, null);
			if (StringUtils.isNullOrEmpty(fileName)) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/class_icon"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					goodsClass.setIcon_acc(photo);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory photo = goodsClass.getIcon_acc();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/class_icon"));
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
				}
			}

		} catch (IOException e) {
			logger.error(e);
		}
		// 分类广告
		Map map2 = new HashMap();
		try {
			String fileName = "";
			map2 = CommUtil.saveFileToServer(request, "gc_adv", saveFilePathName, fileName, null);
			if (StringUtils.isNullOrEmpty(fileName)) {
				if (!StringUtils.isNullOrEmpty(map2.get("fileName"))) {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/class_icon"));
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					Map map_temp = new HashMap();
					map_temp.put("acc_id", photo.getId());
					map_temp.put("acc_url", gc_adv_url);
					goodsClass.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
				}
			} else {
				if (map2.get("fileName") != "") {
					Accessory photo = null;
					if (goodsClass.getGc_advert() != null) {
						Map json_map = (Map) Json.fromJson(goodsClass.getGc_advert());
						String acc_id = CommUtil.null2String(json_map.get("acc_id"));
						photo = this.accessoryService.getObjById(CommUtil.null2Long(acc_id));
					}
					photo = goodsClass.getIcon_acc();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2.get("fileSize"))));
					photo.setPath(this.ftpTools.systemUpload(photo.getName(), "/class_icon"));
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					Map map_temp = new HashMap();
					map_temp.put("acc_id", photo.getId());
					map_temp.put("acc_url", gc_adv_url);
					goodsClass.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
				}
			}

		} catch (IOException e) {
			logger.error(e);
		}
		if (CommUtil.null2Int(adv_type) == 0) {// 设置系统广告
			AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(ap_id));
			Map map_temp = new HashMap();
			if (ap != null) {
				map_temp.put("adv_id", ap.getId());// 存入广告位id
			} else {
				map_temp.put("adv_id", "");
			}
			goodsClass.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		}
		if (goodsClass.getGc_advert() != null) {// 设置分类广告类型
			Map map_temp = (Map) Json.fromJson(goodsClass.getGc_advert());
			map_temp.put("adv_type", CommUtil.null2Int(adv_type));
			goodsClass.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		} else {
			Map map_temp = new HashMap();
			map_temp.put("adv_type", CommUtil.null2Int(adv_type));
			goodsClass.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		}
		if (!StringUtils.isNullOrEmpty(gc_color)) {
			goodsClass.setGc_color(gc_color);
		}
		if (!StringUtils.isNullOrEmpty(gbs_ids)) {
			String[] gb_ids = gbs_ids.split(",");
			List list_temp = new ArrayList();
			for (String gb_id : gb_ids) {
				if (!StringUtils.isNullOrEmpty(gb_id)) {
					GoodsBrand gb = this.goodsbrandService.getObjById(CommUtil.null2Long(gb_id));
					Map map_temp = new HashMap();
					map_temp.put("id", gb.getId());
					map_temp.put("name", gb.getName());
					list_temp.add(map_temp);
				}
			}
			goodsClass.setGb_info(Json.toJson(list_temp, JsonFormat.compact()));
		} else {
			goodsClass.setGb_info(null);
		}
		if (StringUtils.isNullOrEmpty(id)) {
			this.goodsClassService.save(goodsClass);
		} else {
			this.goodsClassService.update(goodsClass);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "保存商品分类成功");
		mv.addObject("list_url", list_url);
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid);
		}
		return mv;
	}

	@SecurityMapping(title = "商品分类下级加载", value = "/admin/goods_class_data.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_data.htm")
	public ModelAndView goods_class_data(HttpServletRequest request, HttpServletResponse response, String pid,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_data.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("pid", Long.parseLong(pid));
		List<GoodsClass> gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id =:pid", map,
				-1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "商品分类Ajax更新", value = "/admin/goods_class_ajax.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_ajax.htm")
	public void goods_class_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsClass gc = this.goodsClassService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsClass.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(gc);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				} else if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				
				if (!StringUtils.isNullOrEmpty(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {//只能处理boolean类型的取反
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
				break;
			}
		}
		this.goodsClassService.update(gc);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			logger.error(e);
		}

	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@SecurityMapping(title = "商品分类批量推荐", value = "/admin/goods_class_recommend.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_recommend.htm")
	public String goods_class_recommend(HttpServletRequest request, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				GoodsClass gc = this.goodsClassService.getObjById(Long.parseLong(id));
				gc.setRecommend(true);
				this.goodsClassService.update(gc);
			}
		}
		return "redirect:goods_class_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品分类批量删除", value = "/admin/goods_class_del.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_class_del.htm")
	public String goods_class_del(HttpServletRequest request, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		List<Store> stores = this.storeService.query("select obj.id,obj.gc_main_id,obj.gc_detail_info from Store obj where obj.gc_detail_info is not null or obj.gc_main_id is not null",null, -1, -1);
		try{
			//先所有判断
			for (String id : ids) {
				if (!StringUtils.isNullOrEmpty(id)) {
					Set<Long> list = this.genericIds(this.goodsClassService.getObjById(Long.parseLong(id)));
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("ids", list);
					List<GoodsClass> gcs = this.goodsClassService.query(
							"select obj from GoodsClass obj where obj.id in (:ids) order by obj.level desc", params, -1, -1);
					for (GoodsClass gc : gcs) {
						if(gc.getGoods_list() != null && gc.getGoods_list().size()>0){//如果有商品关联，不能删除  
							Goods goods=gc.getGoods_list().get(0);
							long goodsId=goods.getId();
							long goodsClassId=gc.getId();
							return "redirect:goods_class_list.htm?currentPage=" + currentPage+"&goodsClass=goods"+"_"+goodsId+"_"+goodsClassId;
						}
						if(gc.getChilds() != null && gc.getChilds().size()>0){ //  如果有子级分类关联,不能删除
							long goodsClassId=gc.getId();
							return "redirect:goods_class_list.htm?currentPage=" + currentPage+"&goodsClass=childs"+"_"+goodsClassId;
						}
					}
					//判断商家
					if(stores != null && stores.size() > 0){
						
						for(int i=0;i<stores.size();i++){
							
							Object store=stores.get(i);
							Object[] sList=new Object[3];
							if(store != null){
								sList=(Object[]) store;
							}
							String gc_detail_info="";
							long gc_main_id=0;
							long storeId=0;
							
							if(sList[0] != null && !"".equals(sList[0].toString())){
								storeId=Long.parseLong(sList[0].toString());
							}
							if(sList[1] != null && !"".equals(sList[1].toString())){
								gc_main_id=Long.parseLong(sList[1].toString());
							}
							if(sList[2] != null && !"".equals(sList[2].toString())){
								gc_detail_info=sList[2].toString();
							}
							
							List<Long> storeGcList=new ArrayList<Long>();
							if(gc_detail_info != null && !"".equals(gc_detail_info)){
								JSONArray jsonArray=JSON.parseArray(gc_detail_info);
								for(int j=0;j<jsonArray.size();j++){
									JSONObject jsonObject=jsonArray.getJSONObject(j);
									String gc_list=jsonObject.getString("gc_list");
									String m_id=jsonObject.getString("m_id");
									storeGcList=JSON.parseArray(gc_list,Long.class);
									storeGcList.add(Long.parseLong(m_id));
								}
							}
							if(gc_main_id != 0){
								storeGcList.add(gc_main_id);
							}
							Set<Long> storeGcSet=new HashSet<Long>(storeGcList);
							for(Long storeGcId:storeGcSet){
								for(Long gcId:list){
									if(storeGcId==gcId){//如果有商家关联，不能删除
										return "redirect:goods_class_list.htm?currentPage=" + currentPage+"&goodsClass=store"+"_"+storeId+"_"+gcId;
									}
								}
							}
							
						
						}
						
					}
					
				}
			}
        }catch(Exception e){
        	return "redirect:goods_class_list.htm?currentPage=" + currentPage+"&goodsClass=error";
		}
		
		
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Set<Long> list = this.genericIds(this.goodsClassService.getObjById(Long.parseLong(id)));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", list);
				List<GoodsClass> gcs = this.goodsClassService.query(
						"select obj from GoodsClass obj where obj.id in (:ids) order by obj.level desc", params, -1, -1);
				for (GoodsClass gc : gcs) {
					for (Goods goods : gc.getGoods_list()) {
						goods.setGc(null);
						this.goodsService.update(goods);
					}
					GoodsType type = gc.getGoodsType();
					if (type != null) {
						type.getGcs().remove(gc);
						this.goodsTypeService.update(type);
					}
					for (GoodsSpecification gsp : gc.getSpec_detail()) {
						gsp.getSpec_goodsClass_detail().remove(gc);
						this.goodsSpecificationService.update(gsp);
					}
					gc.setChilds(null);
					gc.getSpec_detail().clear();
					this.goodsClassService.update(gc);
					this.goodsClassService.delete(gc.getId());
				}
			}
		}
		return "redirect:goods_class_list.htm?currentPage=" + currentPage;

	}

	@RequestMapping("/admin/goods_class_verify.htm")
	public void goods_class_verify(HttpServletRequest request, HttpServletResponse response, String className, String id,
			String pid) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("className", className);
		params.put("id", CommUtil.null2Long(id));
		params.put("pid", CommUtil.null2Long(pid));
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.className=:className and obj.id!=:id and obj.parent.id=:pid",
				params, -1, -1);
		if (gcs != null && gcs.size() > 0) {
			ret = false;
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

	@SecurityMapping(title = "通过类型id查询相应品牌", value = "/admin/goods_goodsType_brand.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/goods_goodsType_brand.htm")
	public void goods_goodsType_brand(HttpServletRequest request, HttpServletResponse response, String type_id) {
		GoodsType gt = this.goodsTypeService.getObjById(CommUtil.null2Long(type_id));
		List<Map> list = new ArrayList<Map>();
		if (gt != null) {
			for (GoodsBrand gb : gt.getGbs()) {
				Map map = new HashMap();
				map.put("id", gb.getId());
				map.put("name", gb.getName());
				list.add(map);
			}
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
}