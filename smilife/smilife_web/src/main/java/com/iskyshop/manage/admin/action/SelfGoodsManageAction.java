package com.iskyshop.manage.admin.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.GoodsFormat;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.TransportQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IGoodsFormatService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IProductMappingService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: GoodsSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品管理控制器，平台可发布商品并进行管理
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
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfGoodsManageAction {
	private static Logger logger = Logger.getLogger(SelfGoodsManageAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private IGoodsFormatService goodsFormatService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private ImageTools imageTools;
	
	@Autowired
	private IGoodsConfigService goodsConfigService;
	@Autowired
	private IProductMappingService productMappingService;
	@Autowired
	private IStoreService storeService;
	/**
	 * 商品发布第一步
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第一步", value = "/admin/add_goods_first.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_first.htm")
	public ModelAndView add_goods_first(HttpServletRequest request, HttpServletResponse response, String id,
			String add_type) {
		ModelAndView mv = new JModelAndView("admin/blue/add_goods_first.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		request.getSession(true).removeAttribute("goods_class_info");
		
		String jsonStaples = "";
		if (!StringUtils.isNullOrEmpty(user.getStaple_gc())) {
			jsonStaples = user.getStaple_gc();
		}
		mv.addObject("goodsClassStaple", Json.fromJson(List.class, jsonStaples));
		
		List<GoodsClass> goodsClass = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
		
		mv.addObject("goodsClass", goodsClass);
		mv.addObject("id", CommUtil.null2String(id));
		mv.addObject("add_type", add_type);
		return mv;
	}

	/**
	 * 根据常用商品分类加载分类信息
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "根据常用商品分类加载分类信息", value = "/admin/load_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/load_goods_class_staple.htm")
	public void load_goods_class_staple(HttpServletRequest request, HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (!StringUtils.isNullOrEmpty(id)) {
			List<Map> list_map = Json.fromJson(List.class, user.getStaple_gc());
			for (Map map : list_map) {
				if (CommUtil.null2String(map.get("id")).equals(id)) {
					obj = this.goodsClassService.getObjById(CommUtil.null2Long(map.get("id")));
				}
			}
		}
		if (!StringUtils.isNullOrEmpty(name))
			obj = this.goodsClassService.getObjByProperty(null, "className", name);
		List<List<Map>> list = new ArrayList<List<Map>>();
		if (obj != null) {
			// 该版本要求三级分类才能添加到常用分类
			request.getSession(true).setAttribute("goods_class_info", obj);
			Map params = new HashMap();
			List<Map> second_list = new ArrayList<Map>();
			List<Map> third_list = new ArrayList<Map>();
			List<Map> other_list = new ArrayList<Map>();

			if (obj.getLevel() == 2) {
				params.put("pid", obj.getParent().getParent().getId());
				List<GoodsClass> second_gcs = this.goodsClassService.query(
						"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc : second_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService.query(
						"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					third_list.add(map);
				}
			}

			if (obj.getLevel() == 1) {
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService.query(
						"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
			}

			Map map = new HashMap();
			String staple_info = this.generic_goods_class_info(obj);
			map.put("staple_info", staple_info.substring(0, staple_info.length() - 1));
			other_list.add(map);

			list.add(second_list);
			list.add(third_list);
			list.add(other_list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * AJAX加载商品分类数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 *            上级分类Id
	 * @param session
	 *            是否加载到session中
	 */
	@SecurityMapping(title = "加载商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/load_goods_class.htm")
	public void load_goods_class(HttpServletRequest request, HttpServletResponse response, Long pid, String session, String level, String storeId) {
		List<Map> list = new ArrayList<Map>();
		Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
		if(store != null && !StringUtils.isNullOrEmpty(store.getGc_detail_info()) && "1".equals(level)){
			String json = store.getGc_detail_info();
			if (!StringUtils.isNullOrEmpty(json)) {
				List<Map> all_list = Json.fromJson(ArrayList.class, json);
				for (Map map : all_list) {
					long ls = Long.parseLong(map.get("m_id").toString());
					if(pid == ls) {
						List<Integer> ts = (List) map.get("gc_list");
						for(Integer i : ts){
							GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(i));
							if(gc != null) {
								Map m = new HashMap();
								m.put("id", gc.getId());
								m.put("className", gc.getClassName());
								list.add(m);
							}
						}
					}					
				}
			}
		} else {
			GoodsClass obj = this.goodsClassService.getObjById(pid);
			if (obj != null) {
				for (GoodsClass gc : obj.getChilds()) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					list.add(map);
				}
				if (CommUtil.null2Boolean(session)) {
					request.getSession(true).setAttribute("goods_class_info", obj);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 添加管理员常用商品分类
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "添加常用商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_class_staple.htm")
	public void add_goods_class_staple(HttpServletRequest request, HttpServletResponse response) {
		String ret = "error";
		String json = "";
		Map map = new HashMap();
		boolean flag = true;
		if (request.getSession(true).getAttribute("goods_class_info") != null) {
			GoodsClass gc = this.goodsClassService
					.getObjById(((GoodsClass) request.getSession(true).getAttribute("goods_class_info")).getId());
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			List<Map> list_map = new ArrayList<Map>();
			if (!StringUtils.isNullOrEmpty(user.getStaple_gc())) {
				list_map = Json.fromJson(List.class, user.getStaple_gc());
			}
			if (list_map.size() > 0) {
				for (Map staple : list_map) {
					if (gc.getId().toString().equals(CommUtil.null2String(staple.get("id")))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					map.put("name", gc.getParent().getParent().getClassName() + ">" + gc.getParent().getClassName() + ">"
									+ gc.getClassName());
					map.put("id", gc.getId());
					list_map.add(map);
					json = Json.toJson(list_map, JsonFormat.compact());
				}
			} else {
				map.put("name", gc.getParent().getParent().getClassName() + ">" + gc.getParent().getClassName() + ">"
								+ gc.getClassName());
				map.put("id", gc.getId());
				list_map.add(map);
				json = Json.toJson(list_map, JsonFormat.compact());
			}
			if (flag) {
				user.setStaple_gc(json);
				flag = this.userService.update(user);
			}
			if (flag) {
				ret = "success";
			}
		}
		map.put("ret", ret);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 删除管理员常用商品分类
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "删除常用商品分类", value = "/admin/del_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/del_goods_class_staple.htm")
	public void del_goods_class_staple(HttpServletRequest request, HttpServletResponse response, String id) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		List<Map> list_map = Json.fromJson(List.class, user.getStaple_gc());
		boolean ret = false;
		for (Map map : list_map) {
			if (CommUtil.null2String(map.get("id")).equals(id)) {
				ret = list_map.remove(map);
				break;
			}
		}
		user.setStaple_gc(Json.toJson(list_map, JsonFormat.compact()));
		this.userService.update(user);
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

	/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第二步", value = "/admin/add_goods_second.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_second.htm")
	public ModelAndView add_goods_second(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = null;
		if (request.getSession(true).getAttribute("goods_class_info") != null) {
			mv = new JModelAndView("admin/blue/add_goods_second.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
			GoodsClass gc = (GoodsClass) request.getSession(true).getAttribute("goods_class_info");
			gc = this.goodsClassService.getObjById(gc.getId());
			String goods_class_info = this.generic_goods_class_info(gc);
			mv.addObject("goods_class", gc);
			mv.addObject("goods_class_info", goods_class_info.substring(0, goods_class_info.length() - 1));
			request.getSession(true).removeAttribute("goods_class_info");
			Map params = new HashMap();
			
			List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
			if (gc.getLevel() == 2) {//发布商品选择类目时选择三级类目,查询出所有与该三级类目关联的规格
				params.clear();
				params.put("spec_type", 0);
				params.put("gc_id", gc.getParent().getId());
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.query(
						"select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);				
				for (GoodsSpecification gspec : goods_spec_list) {
					List<GoodsClass> gcs = gspec.getSpec_goodsClass_detail();
					if(gcs.size() == 0) {//表示当前规格适用于其关联的goodsclass类目下的所有子类目
						spec_list.add(gspec);
					} else {
						for (GoodsClass spec_goodsclass_detail : gcs) {
							if (gc.getId().equals(spec_goodsclass_detail.getId())) {
								spec_list.add(gspec);
								break;
							}
						}
					}					
				}				
			} else if (gc.getLevel() == 1) {//发布商品选择类目时选择二级类目时，将与此二级类目及其所有下级类目关联的规格取出来供用户选择
				params.clear();
				params.put("spec_type", 0);
				params.put("gc_id", gc.getId());
				spec_list = this.goodsSpecificationService.query(
						"select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);
			}
			mv.addObject("goods_spec_list", spec_list);
			
			params.clear();
			GoodsClass goods_class = null;
			if (gc.getLevel() == 2) {
				goods_class = gc.getParent().getParent();
			} else if (gc.getLevel() == 1) {
				goods_class = gc.getParent();
			}
			params.put("gc_id", goods_class.getId());
			List<GoodsBrand> gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.gc.id=:gc_id order by obj.sequence asc", params, -1, -1);
			mv.addObject("gbs", gbs);
			
			mv.addObject("imageSuffix",	this.storeViewTools.genericImageSuffix(this.configService.getSysConfig().getImageSuffix()));
			// 处理上传格式
			String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
			// 查询商品版式信息
			params.clear();
			params.put("gf_cat", 1);
			List<GoodsFormat> gfs = this.goodsFormatService.query("select obj from GoodsFormat obj where obj.gf_cat=:gf_cat",
					params, -1, -1);
			mv.addObject("gfs", gfs);
			// 查询地址信息，前端需要商家选择发货地址
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			mv.addObject("areas", areas);
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(true).setAttribute("goods_session", goods_session);
			mv.addObject("jsessionid", request.getSession().getId());
			// 生成user_id字符串，防止在特定环境下swf上传无法获取session
			String temp_begin = request.getSession().getId().toString().substring(0, 5);
			String temp_end = CommUtil.randomInt(5);
			String user_id = CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId());
			mv.addObject("session_u_id", temp_begin + user_id + temp_end);
			
			Map paraMap = new HashMap();
			paraMap.put("goodsSource", 1);
			List<GoodsConfig> goodsConfigList = this.goodsConfigService.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource", paraMap, -1, -1);
			mv.addObject("goodsConfigs",goodsConfigList);
			
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),	this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "请为商品选择正确的分类");
			mv.addObject("url", CommUtil.getURL(request) + "/admin/add_goods_first.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "产品规格显示", value = "/admin/goods_inventory.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_inventory.htm")
	public ModelAndView goods_inventory(HttpServletRequest request, HttpServletResponse response, String goods_spec_ids) {
		ModelAndView mv = mv = new JModelAndView("admin/blue/goods_inventory.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
		for (String spec_id : spec_ids) {
			if (!StringUtils.isNullOrEmpty(spec_id)) {
				GoodsSpecProperty gsp = this.specPropertyService.getObjById(Long.parseLong(spec_id));
				gsps.add(gsp);
			}
		}
		Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
		for (GoodsSpecProperty gsp : gsps) {
			specs.add(gsp.getSpec());
		}
		for (GoodsSpecification spec : specs) {
			spec.getProperties().clear();
			for (GoodsSpecProperty gsp : gsps) {
				if (gsp.getSpec().getId().equals(spec.getId())) {
					spec.getProperties().add(gsp);
				}
			}
		}
		GoodsSpecification[] spec_list = specs.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {
				// TODO Auto-generated method stub
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				} else {
					return a.getSequence() > b.getSequence() ? 1 : -1;
				}
			}
		});
		List<List<GoodsSpecProperty>> gsp_list = this.generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		mv.addObject("gsps", gsp_list);
		return mv;
	}

	/**
	 * arraylist转化为二维数组
	 * 
	 * @param list
	 * @return
	 */
	public static GoodsSpecProperty[][] list2group(List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = list.get(i).toArray(new GoodsSpecProperty[list.get(i).size()]);
		}
		return gps;
	}

	/**
	 * 生成库存组合
	 * 
	 * @param specs
	 * @return
	 */
	private List<List<GoodsSpecProperty>> generic_spec_property(Set<GoodsSpecification> specs) {
		List<List<GoodsSpecProperty>> result_list = new ArrayList<List<GoodsSpecProperty>>();
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}
		// 将List<List<GoodsSpecProperty>> 转换为二维数组
		GoodsSpecProperty[][] gsps = this.list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List<GoodsSpecProperty> temp_list = new ArrayList<GoodsSpecProperty>();
			int temp = 1; // 注意这个temp的用法。
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][i / (max / temp) % gsps[j].length]);
			}
			GoodsSpecProperty[] temp_gsps = temp_list.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					// TODO Auto-generated method stub
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					} else {
						return a.getSpec().getSequence() > b.getSpec().getSequence() ? 1 : -1;
					}
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}

	@SecurityMapping(title = "运费模板显示", value = "/admin/goods_transport.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_transport.htm")
	public ModelAndView goods_transport(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String ajax) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_transport.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new JModelAndView("admin/blue/goods_transport_list.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}

		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv, orderBy, orderType);
		Store store = store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()).getStore();
		qo.addQuery("obj.trans_user", new SysMap("obj_trans_user", 0), "=");
		qo.setPageSize(1);
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/goods_transport.htm", "", params, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * 商品发布第三步
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第三步", value = "/admin/add_goods_finish.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_finish.htm")
	public ModelAndView add_goods_finish(HttpServletRequest request, HttpServletResponse response, String id,
			String goods_class_id, String image_ids, String goods_main_img_id, String goods_brand_id, String goods_spec_ids,
			String goods_properties, String intentory_details, String goods_session, String transport_type,
			String transport_id, String goods_status, String publish_day, String publish_hour,String publish_min, String f_code_count, String f_code_profix, String advance_date,
			String goods_top_format_id, String goods_bottom_format_id, String delivery_area_id, String goods_spec_id_value,String goods_other_type,String goods_needGoodsCode,String currentPage,String status,
			String auto_advance_sale, String advance_sale_days, String advance_sale_inventory) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(true).getAttribute("goods_session"));
		if (StringUtils.isNullOrEmpty(goods_session1)) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/admin/add_goods_first.htm");
		} else {
			if (true) {
				if (StringUtils.isNullOrEmpty(id)) {
					mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request, response);
					mv.addObject("op_title", "商品发布成功");
					mv.addObject("list_url", CommUtil.getURL(request) + "/admin/add_goods_first.htm");
				} else {
					mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request, response);
					mv.addObject("op_title", "商品编辑成功");
					mv.addObject("list_url", CommUtil.getURL(request) + "/admin/goods_self_list.htm?currentPage="+currentPage+"&status="+status);
				}
				WebForm wf = new WebForm();
				Goods goods = null;
				String obj_status = null;
				Map temp_params = new HashMap();
				Set<Long> temp_ids = new HashSet<Long>();
				
				Map<String,String> goodsConfigMap = new HashMap<String,String>();
				goodsConfigMap.put("configCode", goods_other_type);
				List<GoodsConfig> goodsConfigList=this.goodsConfigService.query("select obj from GoodsConfig obj where obj.configCode=:configCode", goodsConfigMap, -1, -1);
				GoodsConfig goodsConfig=goodsConfigList.get(0);
				int needGoodCode=goodsConfig.getNeedGoodCode();
				
				if (StringUtils.isNullOrEmpty(id)) {
					goods = wf.toPo(request, Goods.class);
					goods.setAddTime(new Date());
					goods.setUser_admin(SecurityUserHolder.getCurrentUser());
					goods.setGoods_type(0);
				} else {
					Goods obj = this.goodsService.getObjById(Long.parseLong(id));
					BigDecimal old_price = obj.getGoods_current_price();
					obj_status = CommUtil.null2String(obj.getGoods_status());
					goods = (Goods) wf.toPo(request, obj);
					goods.setPrice_history(old_price);
					if(auto_advance_sale == null){
						goods.setAuto_advance_sale(false);
				}
				}
				goods.setGoodsConfig(goodsConfig);
				//当前商品不参加团购和秒杀活动时，商品的当前价格即等于商品的店铺价格
				if (goods.getGroup_buy() != 2 && 
						(goods.getSeckill_buy() == 0 || goods.getSeckill_buy() == 1)) {
					goods.setGoods_current_price(goods.getStore_price());
				}
				// 商品名称不可以带有任何html字样，进行过滤
				goods.setGoods_name(Jsoup.clean(goods.getGoods_name(), Whitelist.none()));
				// 商品详情不可以带有违规标签，如script等等
				goods.setGoods_details(goods.getGoods_details());
				GoodsClass gc = this.goodsClassService.getObjById(Long.parseLong(goods_class_id));
				goods.setGc(gc);
				
				// 自营立即发布或者放入仓库
				if ("0".equals(goods_status) || "1".equals(goods_status)) {
					goods.setGoods_seller_time(new Date());
				}
				else if ("2".equals(goods_status)) {// 定时发布
					String str = publish_day + " " + publish_hour + ":" + publish_min;
					Date date = CommUtil.formatDate(str, "yyyy-MM-dd HH:mm");
					goods.setGoods_seller_time(date);
				}
				
				Accessory main_img = null;
				if (!StringUtils.isNullOrEmpty(goods_main_img_id)) {
					main_img = this.accessoryService.getObjById(Long.parseLong(goods_main_img_id));
				}
				goods.setGoods_main_photo(main_img);
				if (!StringUtils.isNullOrEmpty(image_ids)) {
					String[] img_ids = image_ids.split(",");
					goods.getGoods_photos().clear();
					temp_ids.clear();
					for (String img_id : img_ids) {
						if (!StringUtils.isNullOrEmpty(img_id)) {
							temp_ids.add(CommUtil.null2Long(img_id));
						}
					}
					if (!temp_ids.isEmpty()) {
						temp_params.clear();
						temp_params.put("ids", temp_ids);
						List<Accessory> temp_list = this.accessoryService
								.query("select obj from Accessory obj where obj.id in(:ids)", temp_params, -1, -1);
						goods.getGoods_photos().addAll(temp_list);
					}
				}
				if (!StringUtils.isNullOrEmpty(goods_brand_id)) {
					GoodsBrand goods_brand = this.goodsBrandService.getObjById(Long.parseLong(goods_brand_id));
					goods.setGoods_brand(goods_brand);
				}
				goods.getGoods_specs().clear();
				String[] spec_ids = goods_spec_ids.split(",");
				temp_ids.clear();
				for (String spec_id : spec_ids) {
					if (!StringUtils.isNullOrEmpty(spec_id)) {
						temp_ids.add(CommUtil.null2Long(spec_id));
					}
				}
				if (!temp_ids.isEmpty()) {
					temp_params.clear();
					temp_params.put("ids", temp_ids);
					List<GoodsSpecProperty> temp_list = this.specPropertyService.query(
							"select new GoodsSpecProperty(id) from GoodsSpecProperty obj where obj.id in(:ids)", temp_params,
							-1, -1);
					goods.getGoods_specs().addAll(temp_list);
				}
				if (!goods_spec_id_value.isEmpty()) {
					String id_values[] = goods_spec_id_value.split(",");
					List list = new ArrayList();
					for (String id_value : id_values) {
						String single_id_value[] = id_value.split(":");
						Map map = new HashMap();
						map.put("id", single_id_value[0]);
						map.put("name", single_id_value[1]);
						list.add(map);
					}
					goods.setGoods_specs_info(Json.toJson(list, JsonFormat.compact()));
				}

				List<Map> maps = new ArrayList<Map>();
				String[] properties = goods_properties.split(";");
				for (String property : properties) {
					if (!StringUtils.isNullOrEmpty(property)) {
						String[] list = property.split(",");
						Map map = new HashMap();
						map.put("id", list[0]);
						map.put("val", list[1]);
						map.put("name", this.goodsTypePropertyService.getObjById(Long.parseLong(list[0])).getName());
						maps.add(map);
					}
				}
				goods.setGoods_property(Json.toJson(maps, JsonFormat.compact()));
				maps.clear();
				String[] inventory_list = intentory_details.split(";");
				for (String inventory : inventory_list) {
					if (!StringUtils.isNullOrEmpty(inventory)) {
						String[] list = inventory.split(",");
						Map map = new HashMap();
						map.put("id", list[0]);
						map.put("count", list[1]);
						map.put("price", list[2]);
						maps.add(map);
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));
				if (CommUtil.null2Int(transport_type) == 0) { // 使用运费模板
					Transport trans = this.transportService.getObjById(CommUtil.null2Long(transport_id));
					goods.setTransport(trans);
				}
				if (CommUtil.null2Int(transport_type) == 1) { // 使用固定运费
					goods.setTransport(null);
				}
				// 是否为F码商品，是则生成F码
				if (goods.getF_sale_type() == 1 && CommUtil.null2Int(f_code_count) > 0) {
					Set<String> set = new HashSet<String>();
					while (true) {
						if (set.size() == CommUtil.null2Int(f_code_count)) {
							break;
						}
						set.add((f_code_profix + CommUtil.randomString(12)).toUpperCase());
					}
					List<Map> f_code_maps = new ArrayList<Map>();
					if (!StringUtils.isNullOrEmpty(goods.getGoods_f_code())) {
						f_code_maps = Json.fromJson(List.class, goods.getGoods_f_code());
					}

					for (String code : set) {
						Map f_code_map = new HashMap();
						f_code_map.put("code", code);
						f_code_map.put("status", 0);// 0表示该F码未使用，1为已经使用
						f_code_maps.add(f_code_map);
					}
					if (f_code_maps.size() > 0) {
						goods.setGoods_f_code(Json.toJson(f_code_maps, JsonFormat.compact()));
					}
				}
				// 是否为预售商品，是则加入发货时间
				if (goods.getAdvance_sale_type() == 1) {
					goods.setAdvance_date(CommUtil.formatDate(advance_date));
				}

				// 添加商品版式
				goods.setGoods_top_format_id(CommUtil.null2Long(goods_top_format_id));
				GoodsFormat gf = this.goodsFormatService.getObjById(CommUtil.null2Long(goods_top_format_id));
				if (gf != null) {
					goods.setGoods_top_format_content(gf.getGf_content());
				} else {
					goods.setGoods_top_format_content(null);
				}
				goods.setGoods_bottom_format_id(CommUtil.null2Long(goods_bottom_format_id));
				gf = this.goodsFormatService.getObjById(CommUtil.null2Long(goods_bottom_format_id));
				if (gf != null) {
					goods.setGoods_bottom_format_content(gf.getGf_content());
				} else {
					goods.setGoods_bottom_format_content(null);
				}
				goods.setDelivery_area_id(CommUtil.null2Long(delivery_area_id));
				Area de_area = this.areaService.getObjById(CommUtil.null2Long(delivery_area_id));
				if (de_area != null) {
					String delivery_area = de_area.getParent().getParent().getAreaName() + de_area.getParent().getAreaName()
							+ de_area.getAreaName();
					goods.setDelivery_area(delivery_area);
				}
				if (StringUtils.isNullOrEmpty(id)) {
					goods.setDescription_evaluate(new BigDecimal(0));
					goods.setWell_evaluate(0);
					goods.setMiddle_evaluate(0);
					goods.setBad_evaluate(0);
					this.goodsService.save(goods);
					
					if(needGoodCode==1){
						ProductMapping productMapping=new ProductMapping();
						productMapping.setGoodsCode(goods_needGoodsCode);
						productMapping.setAddTime(new Date());
						productMapping.setGoods(goods);
						productMapping.setGoodsConfig(goodsConfig);
						this.productMappingService.save(productMapping);
					}
					
					// 自营商品第一次发布商品，生成二维码
					String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
					this.goodsTools.createGoodsQR(request, goods, uploadFilePath);
					if ("0".equals(goods_status)) {
						// 新增lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
								+ File.separator + "goods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
						SysConfig config = this.configService.getSysConfig();
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setConfig(config);
						lucene.setIndex_path(goods_lucene_path);
						lucene.writeIndex(vo);
					}
				} else {
					String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
							+ File.separator + "goods";
					if ("0".equals(obj_status) && "0".equals(goods_status)) { // 发布后直接上架
						// 更新lucene索引
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), vo);
					}
					if ("0".equals(obj_status) && ("1".equals(goods_status) || "2".equals(goods_status))) { // 发布后在仓库中
						// 删除lucene索引
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.delete_index(id);
					}
					if (("1".equals(obj_status) || "2".equals(obj_status)) && "0".equals(goods_status)) { // 在仓库中发布后上架
						// 添加lucene索引
						LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.writeIndex(vo);
					}
					this.goodsService.update(goods);
					
					if(needGoodCode==1){
						ProductMapping productMapping=goods.getProductMapping();
						if(productMapping == null){
							productMapping=new ProductMapping();
							productMapping.setGoodsCode(goods_needGoodsCode);
							productMapping.setGoods(goods);
							productMapping.setGoodsConfig(goodsConfig);
							this.productMappingService.save(productMapping);
						}else{
							productMapping.setGoodsCode(goods_needGoodsCode);
							productMapping.setGoods(goods);
							productMapping.setGoodsConfig(goodsConfig);
							this.productMappingService.update(productMapping);
						}
					}else{
						ProductMapping productMapping=goods.getProductMapping();
						if(productMapping != null){
							productMapping.setGoodsCode(null);
							productMapping.setGoods(null);
							productMapping.setGoodsConfig(null);
							this.productMappingService.delete(productMapping.getId());
						}
					}
				}
				mv.addObject("obj", goods);
				request.getSession(true).removeAttribute("goods_session");
			} else {
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "参数错误");
				mv.addObject("url", CommUtil.getURL(request) + "/admin/add_goods_first.htm");
			}
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "商品图片上传", value = "/admin/swf_upload.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/swf_upload.htm")
	public void swf_upload(HttpServletRequest request, HttpServletResponse response, String album_id, String session_u_id) {
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		} else {
			User session_user = (User) request.getSession(true).getAttribute("user");
			if (session_user != null) {
				user = this.userService.getObjById(session_user.getId());
			}
		}
		if (user == null) {
			int len = session_u_id.length();
			session_u_id = session_u_id.substring(0, len - 5);
			session_u_id = session_u_id.substring(5, session_u_id.length());
			user = this.userService.getObjById(CommUtil.null2Long(session_u_id));
		}
		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		Map json_map = new HashMap();
		try {
			Map map = CommUtil.saveFileToServer(request, "imgFile", path, null, null);
			
			String imgName = CommUtil.null2String(map.get("fileName"));
			String source = path + File.separator + imgName;//原图在web服务器上的路径
			String ext = (String) map.get("mime");
			ext = ext.indexOf(".") < 0 ? "." + ext : ext;	
			
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<WaterMark> wms = this.waterMarkService.query("select obj from WaterMark obj where obj.user.id=:user_id",
					params, -1, -1);
			if (wms.size() > 0) {
				WaterMark mark = wms.get(0);
				if (mark.isWm_image_open() && mark.getWm_image() != null) {
					// 将水印图片下载到cache文件夹中
					this.ftpTools.systemDownloadWaterImg(mark);
					String pressImg = path + File.separator + mark.getWm_image().getName();
					String targetImg = path + File.separator + map.get("fileName");
					int pos = mark.getWm_image_pos();
					float alpha = mark.getWm_image_alpha();
					CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
					// 删除下载到服务器上的水印图片
					this.ftpTools.DeleteWebImg(mark.getWm_image());
				}
				if (mark.isWm_text_open()) {
					String targetImg = path + File.separator + map.get("fileName");
					int pos = mark.getWm_text_pos();
					String text = mark.getWm_text();
					String markContentColor = mark.getWm_text_color();
					CommUtil.waterMarkWithText(targetImg, targetImg, text, markContentColor,
							new Font(mark.getWm_text_font(), Font.BOLD, mark.getWm_text_font_size()), pos, 100f);
				}
			}
			
			//生成大图
			String bigTarget = source;
			CommUtil.createSmall(source, bigTarget, this.configService.getSysConfig().getBigWidth(),
					this.configService.getSysConfig().getBigHeight());
			
			// 生成小图片
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.configService.getSysConfig().getSmallWidth(),
					this.configService.getSysConfig().getSmallHeight());
			
			// 生成中等图片
			String midtarget = source + "_middle" + ext;
			CommUtil.createSmall(source, midtarget, this.configService.getSysConfig().getMiddleWidth(),
					this.configService.getSysConfig().getMiddleHeight());
			
			// 上传大图
			String bigImgFTPPath = this.ftpTools.systemUpload(imgName, "/" + url);
			
			// 上传小图
			this.ftpTools.systemUpload(imgName + "_small" + ext, "/" + url);
			
			// 上传中图
			this.ftpTools.systemUpload(imgName + "_middle" + ext, "/" + url);
			

			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(bigImgFTPPath);
			image.setWidth(this.configService.getSysConfig().getBigWidth());
			image.setHeight(this.configService.getSysConfig().getBigHeight());
			image.setName(imgName);
			image.setSize(BigDecimal.valueOf((new File(bigTarget)).length()));
			image.setUser(user);
			Album album = null;
			if (!StringUtils.isNullOrEmpty(album_id)) {
				album = this.albumService.getObjById(CommUtil.null2Long(album_id));
			} else {
				album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册【" + user.getUserName() + "】");
					album.setAlbum_sequence(-10000);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.save(album);
				}
			}
			image.setAlbum(album);
			this.accessoryService.save(image);
			
			json_map.put("url", image.getPath() + "/" + image.getName());
			json_map.put("id", image.getId());
			json_map.put("remainSpace", 100000);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}

	@SecurityMapping(title = "商品图片删除", value = "/admin/goods_image_del.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_image_del.htm")
	public void goods_image_del(HttpServletRequest request, HttpServletResponse response, String image_id) {
		boolean ret = false;
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			Map map = new HashMap();
			Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(image_id));
			if (img != null) {
				for (Goods goods : img.getGoods_main_list()) {
					goods.setGoods_main_photo(null);
					this.goodsService.update(goods);
				}
				for (Goods goods1 : img.getGoods_list()) {
					goods1.getGoods_photos().remove(img);
					this.goodsService.update(goods1);
				}
				ret = this.accessoryService.delete(img.getId());
				if (ret) {
					this.ftpTools.systemDeleteFtpImg(img);
				}
			}
			map.put("result", ret);
			map.put("remainSpace", 100000);
			writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	/**
	 * 自营商品列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营商品列表", value = "/admin/goods_self_list.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_list.htm")
	public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String goods_status, String brand_id, String goods_name, String u_admin_id, String configCode,String status) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_self_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (goods_status == null) {
			goods_status = "0";
		}		
		if ("0".equals(goods_status)) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		}
		if ("1".equals(goods_status)) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", 1), "=");
		}
		if ("-2".equals(goods_status)) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		}
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("obj_goods_name", "%" + goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (!StringUtils.isNullOrEmpty(brand_id)) {
			qo.addQuery("obj.goods_brand.id", new SysMap("obj_goods_brand", CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
		}
		if (!StringUtils.isNullOrEmpty(u_admin_id)) {
			qo.addQuery("obj.user_admin.id", new SysMap("obj_admin_id", CommUtil.null2Long(u_admin_id)), "=");
			mv.addObject("u_admin_id", u_admin_id);
		}
		
		if(StringUtils.isNullOrEmpty(configCode) || !"hx".equals(configCode) && !"ppg".equals(configCode) && !"kjt".equals(configCode)) {
			configCode="ptsp";
		}
		qo.addQuery("obj.goodsConfig.configCode", new SysMap("configCode", configCode), "=");
		mv.addObject("configCode", configCode);

		mv.addObject("goods_status", goods_status);
		qo.addQuery("obj.goods_type", new SysMap("obj_goods_type", 0), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
		Map admin_map = new HashMap();
		admin_map.put("userRole", "ADMIN");
		List<User> user_admins = this.userService.query("select obj from User obj where obj.userRole=:userRole", admin_map,
				-1, -1);
		Map map0 = new HashMap();
		map0.put("goodsSource", 1);
		List<GoodsConfig> goodsConfigList = this.goodsConfigService
				.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource order by obj.id asc", map0, -1, -1);
		mv.addObject("user_admins", user_admins);
		mv.addObject("gbs", gbs);
		mv.addObject("status", status);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("goodsConfigList", goodsConfigList);
		return mv;
	}

	@SecurityMapping(title = "商品编辑", value = "/admin/goods_self_edit.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request, HttpServletResponse response, String id, String add_type,String currentPage,String status) {
		ModelAndView mv = new JModelAndView("admin/blue/add_goods_second.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (add_type == null) { // 非编辑分类
			request.getSession(true).removeAttribute("goods_class_info");
		}
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		mv.addObject("obj", obj);
		
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());		
		mv.addObject("edit", true);
		
		GoodsClass gc = null;
		HashMap params = new HashMap();
		if (request.getSession(true).getAttribute("goods_class_info") != null) {
			GoodsClass session_gc = (GoodsClass) request.getSession(true).getAttribute("goods_class_info");
			gc = this.goodsClassService.getObjById(session_gc.getId());
			request.getSession(true).removeAttribute("goods_class_info");	
		} else {
			gc = obj.getGc();
			
			// 查询商品版式信息
			params.clear();
			params.put("gf_cat", 1);
			List<GoodsFormat> gfs = this.goodsFormatService.query("select obj from GoodsFormat obj where obj.gf_cat=:gf_cat", params, -1, -1);
			mv.addObject("gfs", gfs);

			// 查询地址信息，前端需要商家选择发货地址
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			mv.addObject("areas", areas);
		}
		
		List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
		if (gc.getLevel() == 2) {//发布商品选择类目时选择三级类目,查询出所有与该三级类目关联的规格
			params.clear();
			params.put("spec_type", 0);
			params.put("gc_id", gc.getParent().getId());
			List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.query(
					"select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);				
			for (GoodsSpecification gspec : goods_spec_list) {
				List<GoodsClass> gcs = gspec.getSpec_goodsClass_detail();
				if(gcs.size() == 0) {//表示当前规格适用于其关联的goodsclass类目下的所有子类目
					spec_list.add(gspec);
				} else {
					for (GoodsClass spec_goodsclass_detail : gcs) {
						if (gc.getId().equals(spec_goodsclass_detail.getId())) {
							spec_list.add(gspec);
							break;
						}
					}
				}					
			}				
		} else if (gc.getLevel() == 1) {//发布商品选择类目时选择二级类目时，将与此二级类目及其所有下级类目关联的规格取出来供用户选择
			params.clear();
			params.put("spec_type", 0);
			params.put("gc_id", gc.getId());
			spec_list = this.goodsSpecificationService.query(
					"select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);
		}
		mv.addObject("goods_spec_list", spec_list);
		
		mv.addObject("goods_class_info", this.storeTools.generic_goods_class_info(gc));
		mv.addObject("goods_class", gc);	
		
		params.clear();
		if (gc.getLevel() == 2) {
			params.put("gc_id", gc.getParent().getParent().getId());
		} else if (gc.getLevel() == 1) {
			params.put("gc_id", gc.getParent().getId());
		}		
		List<GoodsBrand> gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.gc.id=:gc_id order by obj.sequence asc", params, -1, -1);
		mv.addObject("gbs", gbs);
		
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(true).setAttribute("goods_session", goods_session);
		mv.addObject("imageSuffix",	this.storeViewTools.genericImageSuffix(this.configService.getSysConfig().getImageSuffix()));
		
		// 处理上传格式
		String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		
		mv.addObject("jsessionid", request.getSession().getId());
		
		params.clear();
		params.put("goodsSource", 1);
		List<GoodsConfig> goodsConfigList = this.goodsConfigService.query("select obj from GoodsConfig obj where obj.goodsSource=:goodsSource", params, -1, -1);
		mv.addObject("goodsConfigs",goodsConfigList);
		
		mv.addObject("currentPage", currentPage);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "商品上下架", value = "/admin/goods_self_sale.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_sale.htm")
	public String goods_sale(HttpServletRequest request, HttpServletResponse response, String mulitId) {
		String status = "0";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Goods goods = this.goodsService.getObjById(Long.parseLong(id));
				int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
				goods.setGoods_status(goods_status);
				this.goodsService.update(goods);
				if (goods_status == 0) {
					status = "1";
					// 添加lucene索引
					String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
							+ File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.update(CommUtil.null2String(goods.getId()), vo);
				} else {
					// 删除lucene索引
					String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
							+ File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.delete_index(CommUtil.null2String(goods.getId()));
				}
			}
		}
		return "redirect:" + "/admin/goods_self_list.htm?goods_status=" + status;
	}

	@SecurityMapping(title = "商品删除", value = "/admin/goods_self_del.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_del.htm")
	public String goods_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String op, String configCode) {
		int status = 0;
		if ("storage".equals(CommUtil.null2String(op))) {
			status = 1;
		}
		if ("out".equals(CommUtil.null2String(op))) {
			status = -2;
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
				Map map = new HashMap();
				map.put("gid", goods.getId());
				List<GoodsCart> goodCarts = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.goods.id = :gid", map, -1, -1);
				Long ofid = null;
				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				
				ProductMapping productMapping=goods.getProductMapping();
				if(productMapping != null){
					productMapping.setGoodsCode(null);
					productMapping.setGoods(null);
					productMapping.setGoodsConfig(null);
					this.productMappingService.delete(productMapping.getId());
				}
				
				goods.getGoods_ugcs().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				for (GoodsCart gc : goods.getCarts()) {
					gc.getGsps().clear();
					this.goodsCartService.delete(gc.getId());
				}
				try {
					this.goodsService.delete(goods.getId());
					// 删除索引
					String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
							+ File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.delete_index(CommUtil.null2String(id));
				} catch (Exception e1) {
					logger.error(e1);
				}				
			}
		}
		return "redirect:/admin/goods_self_list.htm?goods_status=" + status + "&configCode=" + configCode;
	}

	@SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_self_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_ajax.htm")
	public void goods_self_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		Field[] fields = Goods.class.getDeclaredFields();
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
				if (!StringUtils.isNullOrEmpty(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		if ("store_recommend".equals(fieldName)) {
			if (obj.isStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else {
				obj.setStore_recommend_time(null);
			}
		}
		this.goodsService.update(obj);
		 if (obj.getGoods_status() == 0) {
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			LuceneUtil.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(obj.getId()), luceneVoTools.updateGoodsIndex(obj));
		 } 
		 //else {
		// String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
		// File file = new File(goods_lucene_path);
		// if (!file.exists()) {
		// CommUtil.createFolder(goods_lucene_path);
		// }
		// LuceneUtil lucene = LuceneUtil.instance();
		// lucene.setIndex_path(goods_lucene_path);
		// lucene.delete_index(CommUtil.null2String(id));
		// }
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

	@SecurityMapping(title = "商品相册列表", value = "/admin/goods_album.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_album.htm")
	public ModelAndView goods_album(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String ajax_type, String album_name) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_album.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		AlbumQueryObject aqo = new AlbumQueryObject();
		aqo.addQuery("obj.user.userRole", new SysMap("user_role", "admin"), "=");
		if (CommUtil.null2String(album_name) != "") {
			aqo.addQuery("obj.album_name", new SysMap("album_name", "%" + album_name + "%"), "like");
			mv.addObject("album_name", album_name);
		}
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setOrderBy("album_sequence");
		aqo.setOrderType("asc");
		aqo.setPageSize(5);
		IPageList pList = this.albumService.list(aqo);
		String album_url = CommUtil.getURL(request) + "/admin/goods_album.htm";
		if (pList.getResult().size() > 0) {
			mv.addObject("albums", pList.getResult());
		}
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(album_url, "", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("ajax_type", ajax_type);
		mv.addObject("ImageTools", imageTools);
		return mv;
	}

	@SecurityMapping(title = "商品图片列表", value = "/admin/goods_img.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_img.htm")
	public ModelAndView goods_img(HttpServletRequest request, HttpServletResponse response, String currentPage, String type,
			String album_id) {
		ModelAndView mv = new JModelAndView("admin/blue/" + type + ".html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv, "addTime", "desc");
		aqo.setPageSize(20);
		aqo.addQuery("obj.album.id", new SysMap("album_id", CommUtil.null2Long(album_id)), "=");
		aqo.addQuery("obj.user.userRole", new SysMap("user_role", "admin"), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request) + "/admin/goods_img.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url, "", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("album_id", album_id);
		return mv;
	}

	@SecurityMapping(title = "商品二维码生成", value = "/admin/goods_self_qr.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_self_qr.htm")
	public String goods_self_qr(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
				if (obj.getGoods_type() == 0) { // 只能操作自营商品
					String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
					String destPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
							+ File.separator + "cache";
					if (!CommUtil.fileExist(destPath)) {
						CommUtil.createFolder(destPath);
					}
					destPath = destPath + File.separator + id + "_qr.jpg";					
					
					String logoPath = ""; //商品图片要么在FTP上要么就在Web服务器上					
					if (obj.getGoods_main_photo() != null) {
						this.ftpTools.systemDownloadImg(obj.getGoods_main_photo());
						logoPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
								+ "cache" + File.separator + obj.getGoods_main_photo().getName();
					} else {//取默认的商品图片(默认的商品图片可能是项目中的资源文件，也可能是用户上传上去的新图片)
						Accessory  defaulImg = this.configService.getSysConfig().getGoodsImage();
						String imgPath = defaulImg.getPath();
						if(imgPath.startsWith("resources/style/common/images"))
							logoPath = CommUtil.getServerRealPathFromRequest(request) + imgPath + File.separator + this.configService.getSysConfig().getGoodsImage().getName();	
						else {							
							logoPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache" + File.separator + defaulImg.getName();
							File defaultGoodsImg = new File(logoPath);
							if(!defaultGoodsImg.exists()) {
								this.ftpTools.systemDownloadImg(defaulImg);
							}
						}							  
					}					
					
					QRCodeUtil.encode(configService.getSysConfig().getGoodsH5Url() + "?goodsId=" + id, logoPath, destPath, true);
					
					// 将二维码图片上传ftp
					String url = this.ftpTools.systemUpload(id + "_qr.jpg", "/goods_qr");
					obj.setQr_img_path(url + "/" + id + "_qr.jpg");
					this.goodsService.update(obj);
					// 删除主图
					this.ftpTools.DeleteWebImg(obj.getGoods_main_photo());
				}
			}
		}
		return "redirect:goods_self_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品二维码Excel生成并下载", value = "/admin/goods_self_f_code_download.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_self_f_code_download.htm")
	public void goods_self_f_code_download(HttpServletRequest request, HttpServletResponse response, String id)
			throws IOException {
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		String excel_url = "";
		if (obj.getF_sale_type() == 1 && obj.getGoods_type() == 0 && !StringUtils.isNullOrEmpty(obj.getGoods_f_code())) {
			List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
			String name = CommUtil.null2String(UUID.randomUUID());
			String path = CommUtil.getServerRealPathFromRequest(request) + "excel"
					+ File.separator;
			File f = new File(path);
			if(!f.exists()) {
				f.mkdirs();
			}			
			FileOutputStream out = new FileOutputStream(new File(path + name + ".xls"));
			this.exportList2Excel("F码列表", new String[] { "F码信息", "F码状态" }, list, out);
			excel_url = CommUtil.getURL(request) + "/excel/" + name + ".xls";
		}

		response.sendRedirect(excel_url);
	}

	private static boolean exportList2Excel(String title, String[] headers, List<Map> list, OutputStream out) {
		boolean ret = true;
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式
		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style_.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style_.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style_.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style_.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style_.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style_.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style_.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font_ = workbook.createFont();
		// font.setColor(color);
		font_.setFontHeightInPoints((short) 14);
		// font_.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		style_.setFont(font_);
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		// 遍历集合数据，产生数据行
		int index = 0;
		for (Map map : list) {
			index++;
			row = sheet.createRow(index);
			String value = CommUtil.null2String(map.get("code"));
			HSSFCell cell = row.createCell(0);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
			value = CommUtil.null2Int(map.get("status")) == 0 ? "未使用" : "已使用";
			cell = row.createCell(1);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
		}
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			ret = false;
			logger.error(e);
		}
		return ret;
	}

	private static boolean waterMarkWithText(String filePath, String outPath, String text, String markContentColor,
			Font font, int pos, float qualNum) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("宋体", Font.BOLD, 20);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(CommUtil.getColor(markContentColor));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		g.drawImage(theImg, 0, 0, null);
		FontMetrics metrics = new FontMetrics(font) {
		};
		Rectangle2D bounds = metrics.getStringBounds(text, null);
		int widthInPixels = (int) bounds.getWidth();
		int heightInPixels = (int) bounds.getHeight();
		int left = 0;
		int top = heightInPixels;

		if (pos == 2) {
			left = width / 2;
			top = heightInPixels;
		}
		if (pos == 3) {
			left = width - widthInPixels;
			top = heightInPixels;
		}
		if (pos == 4) {
			left = width - widthInPixels;
			top = height / 2;
		}
		if (pos == 5) {
			left = width - widthInPixels;
			top = height - heightInPixels;
		}
		if (pos == 6) {
			left = width / 2;
			top = height - heightInPixels;
		}
		if (pos == 7) {
			left = 0;
			top = height - heightInPixels;
		}
		if (pos == 8) {
			left = 0;
			top = height / 2;
		}
		if (pos == 9) {
			left = width / 2;
			top = height / 2;
		}
		g.drawString(text, left, top); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();
		try (FileOutputStream out = new FileOutputStream(outPath);) {
			ImageIO.write(bimage, filePath.substring(filePath.lastIndexOf(".") + 1), out);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}