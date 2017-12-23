package com.iskyshop.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.GoodsFormat;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.TransportQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
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
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IProductMappingService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
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
 * Title: GoodsSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家后台商品管理控制器
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
 * @date 2014-5-7
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsSellerAction {
	private static Logger logger = Logger.getLogger(GoodsSellerAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IAlbumService albumService;	
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IAreaService areaService;
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
	private IComplaintGoodsService complaintGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsFormatService goodsFormatService;
	@Autowired
	private IZTCGoldLogService iztcGoldLogService;
	@Autowired
	private IGoodsCartService cartService;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private FTPServerTools FTPTools;
	@Autowired
	private ImageTools ImageTools;
	@Autowired
	private IGoodsTypePropertyService gtpService;
	
	@Autowired
	private IGoodsConfigService goodsConfigService;
	@Autowired
	private IProductMappingService productMappingService;

	@SecurityMapping(title = "发布商品第一步", value = "/seller/add_goods_first.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_first.htm")
	public ModelAndView add_goods_first(HttpServletRequest request, HttpServletResponse response, String id,
			String add_type) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		request.getSession(true).removeAttribute("goods_class_info");
		int store_status = (user.getStore() == null ? 0 : user.getStore().getStore_status());
		if (store_status == 15) {
			StoreGrade grade = user.getStore().getGrade();
			if(StringUtils.isNullOrEmpty(grade)){
				mv.addObject("op_title", "您的店铺尚未设立店铺等级，不能发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
				return mv;
			}
			int user_goods_count = user.getStore().getGoods_list().size();
			if (grade.getGoodsCount() == 0 || user_goods_count < grade.getGoodsCount()) {
				mv = new JModelAndView("user/default/sellercenter/add_goods_first.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				String json_staples = "";
				if (!StringUtils.isNullOrEmpty(user.getStaple_gc())) {
					json_staples = user.getStaple_gc();
				}
				List<Map> staples = Json.fromJson(List.class, json_staples);
				mv.addObject("staples", staples);
				
				List<GoodsClass> gcs = this.storeTools.query_store_detail_MainGc(user.getStore().getGc_detail_info());
				if (gcs.size() == 0) { // 店铺未设置详细类目
					GoodsClass parent = this.goodsClassService.getObjById(user.getStore().getGc_main_id());
					gcs.add(parent);					
				}				
				mv.addObject("gcs", gcs);
				mv.addObject("id", CommUtil.null2String(id));
			} else {
				mv.addObject("op_title", "您的店铺等级只允许上传" + grade.getGoodsCount() + "件商品!");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/store_grade.htm");
			}
		} else if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else if (store_status == 10) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else if (store_status == 20) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			mv.addObject("op_title", "店铺信息错误，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		
		mv.addObject("add_type", add_type);
		return mv;
	}

	@SecurityMapping(title = "商品运费模板分页显示", value = "/seller/goods_transport.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_transport.htm")
	public ModelAndView goods_transport(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String ajax) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_transport.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new JModelAndView("user/default/sellercenter/goods_transport_list.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}

		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv, orderBy, orderType);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
		qo.setPageSize(1);
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/seller/goods_transport.htm", "", params, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	@SecurityMapping(title = "发布商品第二步", value = "/seller/add_goods_second.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_second.htm")
	public ModelAndView add_goods_second(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		int store_status = user.getStore().getStore_status();
		if (store_status == 15) {
			if (request.getSession(true).getAttribute("goods_class_info") != null) {
				mv = new JModelAndView("user/default/sellercenter/add_goods_second.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				// 查询自己店铺类型
				Map map = new HashMap();
				map.put("store_id", user.getStore().getId());
				List<GoodsTypeProperty> gtps = this.gtpService.query("select obj from GoodsTypeProperty obj where obj.store_id=:store_id order by sequence asc", map, -1, -1);
				mv.addObject("gtps", gtps);
				
				GoodsClass gc = (GoodsClass) request.getSession(true).getAttribute("goods_class_info");
				gc = this.goodsClassService.getObjById(gc.getId());
				String goods_class_info = this.generic_goods_class_info(gc);
				mv.addObject("goods_class", gc);
				mv.addObject("goods_class_info", goods_class_info.substring(0, goods_class_info.length() - 1));
				request.getSession(true).removeAttribute("goods_class_info");
				
				List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
				map.clear();
				if (gc.getLevel() == 2) {//发布商品选择类目时选择三级类目,查询出所有与该三级类目关联的规格
					map.put("store_id", user.getStore().getId());
					map.put("gc_id", gc.getParent().getId());
					List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.query(
							"select obj from GoodsSpecification obj where obj.store.id=:store_id and obj.goodsclass.id=:gc_id order by sequence asc", map, -1, -1);					
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
					map.put("store_id", user.getStore().getId());
					map.put("gc_id", gc.getId());
					spec_list = this.goodsSpecificationService.query(
							"select obj from GoodsSpecification obj where obj.store.id=:store_id and obj.goodsclass.id=:gc_id order by sequence asc", map, -1, -1);
				}
				mv.addObject("goods_spec_list", spec_list);

				BigDecimal sizeusing = user.getStore().getSpacesize();
				double remainSpace = 0;
				if (user.getStore().getGrade().getSpaceSize() > 0) {
					remainSpace = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
							BigDecimal.valueOf(CommUtil.null2Double(sizeusing)));
					mv.addObject("img_remain_size", remainSpace);
				}
				
				Map params = new HashMap();
				params.put("user_id", user.getId());
				params.put("display", true);
				List<UserGoodsClass> ugcs = this.userGoodsClassService.query(
						"select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
						params, -1, -1);
				mv.addObject("ugcs", ugcs);
				
				params.clear();
				if (gc.getLevel() == 2) {
					params.put("gc_id", gc.getParent().getParent().getId());
				} else if (gc.getLevel() == 1) {
					params.put("gc_id", gc.getParent().getId());
				}				
				List<GoodsBrand> gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.gc.id=:gc_id and obj.audit=1  order by obj.sequence asc",	params, -1, -1);
				mv.addObject("gbs", gbs);
				
				mv.addObject("imageSuffix",
						this.storeViewTools.genericImageSuffix(this.configService.getSysConfig().getImageSuffix()));
				// 处理上传格式
				String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
				StringBuffer sb = new StringBuffer();
				for (String str : strs) {
					sb.append("." + str + ",");
				}
				mv.addObject("imageSuffix1", sb);
				Date now = new Date();
				now.setDate(now.getDate() + 1);
				mv.addObject("default_publish_day", CommUtil.formatShortDate(now));
				String goods_session = CommUtil.randomString(32);
				mv.addObject("goods_session", goods_session);
				request.getSession(true).setAttribute("goods_session", goods_session);
				mv.addObject("store", user.getStore());
				// 查询商品版式信息
				params.clear();
				params.put("gf_store_id", user.getStore().getId());
				List gfs = this.goodsFormatService
						.query("select obj from GoodsFormat obj where obj.gf_store_id=:gf_store_id", params, -1, -1);
				mv.addObject("gfs", gfs);
				// 查询地址信息，前端需要商家选择发货地址
				List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1,
						-1);
				mv.addObject("areas", areas);
				mv.addObject("jsessionid", request.getSession().getId());
				// 生成user_id字符串，防止在特定环境下swf上传无法获取session
				String temp_begin = request.getSession().getId().toString().substring(0, 5);
				String temp_end = CommUtil.randomInt(5);
				String user_id = CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId());
				mv.addObject("session_u_id", temp_begin + user_id + temp_end);
			} else {
				mv.addObject("op_title", "Session信息丢失，请重新发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/add_goods_first.htm");
			}
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 10) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 20) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_inventory.htm")
	public ModelAndView goods_inventory(HttpServletRequest request, HttpServletResponse response, String goods_spec_ids,
			String supplement) {
		ModelAndView mv = mv = new JModelAndView("user/default/sellercenter/goods_inventory.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
		for (String spec_id : spec_ids) {
			if (!"".equals(spec_id)) {
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
		if (!StringUtils.isNullOrEmpty(supplement)) {
			mv.addObject("supplement", supplement);
		}
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

	@SecurityMapping(title = "上传商品图片", value = "/seller/swf_upload.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/swf_upload.htm")
	public void swf_upload(HttpServletRequest request, HttpServletResponse response, String album_id, String session_u_id) {
		Map json_map = new HashMap();
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		} else {
			int len = session_u_id.length();
			session_u_id = session_u_id.substring(0, len - 5);
			session_u_id = session_u_id.substring(5, session_u_id.length());
			user = this.userService.getObjById(CommUtil.null2Long(session_u_id));
		}
		String url = this.storeTools.createUserFolderURL(user.getStore());
		user = user.getParent() == null ? user : user.getParent();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		BigDecimal sizeusing = user.getStore().getSpacesize();
		double img_remain_size = 0;
		if (user.getStore().getGrade().getSpaceSize() > 0) {
			img_remain_size = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
					BigDecimal.valueOf(CommUtil.null2Double(sizeusing)));
			json_map.put("remainSpace", img_remain_size);
		}
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";		

		if (img_remain_size >= 0) {
			try {
				Map map = CommUtil.saveFileToServer(request, "imgFile", saveFilePathName, null, null);
				String imgName = CommUtil.null2String(map.get("fileName"));
				String source = saveFilePathName + File.separator + imgName;//原图在web服务器上的路径
				String ext = (String) map.get("mime");
				ext = ext.indexOf(".") < 0 ? "." + ext : ext;
				String userId = user.getId().toString();
				
				Map params = new HashMap();
				params.put("store_id", user.getStore().getId());
				List<WaterMark> wms = this.waterMarkService.query("select obj from WaterMark obj where obj.store.id=:store_id", params, -1, -1);
				if (wms.size() > 0) {
					WaterMark mark = wms.get(0);
					String targetImg = saveFilePathName + File.separator + map.get("fileName");

					if (mark.isWm_image_open() && mark.getWm_image() != null) {
						String pressImg = saveFilePathName + File.separator + mark.getWm_image().getName();
						this.FTPTools.userDownloadWaterImg(mark, CommUtil.null2String(user.getId()));

						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
						this.FTPTools.DeleteWebImg(mark.getWm_image());
					}
					if (mark.isWm_text_open()) {
						targetImg = saveFilePathName + File.separator + map.get("fileName");
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
				
				//生成小图片
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService.getSysConfig().getSmallWidth(),
						this.configService.getSysConfig().getSmallHeight());
				
				// 生成中等图片				
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService.getSysConfig().getMiddleWidth(),
						this.configService.getSysConfig().getMiddleHeight());
				
				
				// 上传大图
				String bigImgFTPPath = this.FTPTools.userUpload(imgName, "/" + url, userId);
				
				// 上传小图
				this.FTPTools.userUpload(imgName + "_small" + ext, "/" + url, userId);
				
				// 上传中图
				this.FTPTools.userUpload(imgName + "_middle" + ext, "/" + url, userId);
				
				
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
						album.setAlbum_name("默认相册");
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
				if (user.getStore().getGrade().getSpaceSize() > 0) {
					Double filesize = CommUtil.null2Double(map.get("fileSize"));
					BigDecimal storespace = user.getStore().getSpacesize();
					double allsize = CommUtil.add(storespace, BigDecimal.valueOf(filesize / 1048576));
					user.getStore().setSpacesize(BigDecimal.valueOf(allsize));
					this.storeService.update(user.getStore());
					double img_remain_size2 = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
							BigDecimal.valueOf(CommUtil.null2Double(user.getStore().getSpacesize())));
					json_map.put("remainSpace", img_remain_size2);
				} else {
					json_map.put("remainSpace", "null");
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			json_map.put("url", "");
			json_map.put("id", "");
			json_map.put("remainSpace", -1);
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

	@SecurityMapping(title = "商品图片删除", value = "/seller/goods_image_del.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_image_del.htm")
	public void goods_image_del(HttpServletRequest request, HttpServletResponse response, String image_id) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
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
					this.FTPTools.userDeleteFtpImg(img, CommUtil.null2String(user.getId()));
				}
			}
			double remainSpace;
			if (user.getStore().getGrade().getSpaceSize() > 0) {
				Double filesize = CommUtil.null2Double(img.getSize());
				BigDecimal storespace = user.getStore().getSpacesize();
				double allsize = CommUtil.subtract(storespace, BigDecimal.valueOf(filesize / 1048576));
				user.getStore().setSpacesize(BigDecimal.valueOf(allsize));
				this.storeService.update(user.getStore());
				remainSpace = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
						BigDecimal.valueOf(CommUtil.null2Double(user.getStore().getSpacesize())));
			} else {
				remainSpace = 0;
			}
			map.put("result", ret);
			map.put("remainSpace", remainSpace);
			writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SecurityMapping(title = "发布商品第三步", value = "/seller/add_goods_finish.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_finish.htm")
	public ModelAndView add_goods_finish(HttpServletRequest request, HttpServletResponse response, String id,
			String goods_class_id, String image_ids, String goods_main_img_id, String user_class_ids, String goods_brand_id,
			String goods_spec_ids, String goods_properties, String intentory_details, String goods_session,
			String transport_type, String transport_id, String publish_goods_status, String publish_day, String publish_hour,
			String publish_min, String f_code_profix, String f_code_count, String advance_date, String goods_top_format_id,
			String goods_bottom_format_id, String delivery_area_id, String goods_spec_id_value,String goods_other_type,String goods_needGoodsCode,
			String auto_advance_sale, String advance_sale_days, String advance_sale_inventory) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(true).getAttribute("goods_session"));
		if ("".equals(goods_session1)) {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
		} else if(goods_session1.equals(goods_session)){
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			Store store = user.getStore();
			if (user.getParent() != null) {
				store = user.getParent().getStore();
			}
			// 判断所发布的商品分类是否和店铺经营分类匹配
			GoodsClass gc = this.goodsClassService.getObjById(Long.parseLong(goods_class_id));
			if(gc.getLevel() == 0) {
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "发布的商品所选择的分类不能是一级分类！");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/add_goods_first.htm");
				return mv;
			}
			
			boolean m = false;
			GoodsClass gcLevel1 = gc.getLevel() == 2 ? gc.getParent() : gc; //商品不可能挂在一级类目下
			if (StringUtils.isNullOrEmpty(store.getGc_detail_info()) && store.getGc_main_id().equals(gcLevel1.getParent().getId())) {
				m = true;
			} else {//商家指定了详细类目
				List<Map> all_list = Json.fromJson(ArrayList.class, store.getGc_detail_info());
				int gcLevel1_id = gcLevel1.getId().intValue();
				end:for (Map map : all_list) {
					List<Integer> ls = (List) map.get("gc_list");//都是二级类目的id
					for (Integer l : ls) {
						if(l.intValue() == gcLevel1_id) {//商品一般挂在三级类目下，所以先判断此情况，不符合再判断商品挂在二级类目下的情况，这有利于改善性能
							m = true;
							break end;
						} 
					}
				}
			}
			
			//若所发布的商品分类和店铺经营分类不匹配，不允许发布
			if (!m) {
				mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "该商品的所属类目和店铺经营的类目不匹配,请确认后再重新发布！");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
				return mv;
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
			
			if ("".equals(id)) {
				goods = wf.toPo(request, Goods.class);
				goods.setAddTime(new Date());
				goods.setGoods_store(store);
				
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
			
			if (goods.getGroup_buy() != 2) {//只有非团购商品店铺价格与当前价格一致，团购商品价格为团购价格
				goods.setGoods_current_price(goods.getStore_price());
			}
			// 商品名称不可以带有任何html字样，进行过滤
			goods.setGoods_name(Jsoup.clean(goods.getGoods_name(), Whitelist.none()));
			// 商品详情不可以带有违规标签，如script等等
			goods.setGoods_details(CommUtil.filterHTML(goods.getGoods_details()));

			goods.setGc(gc);
			if (store.getGrade().getGoods_audit() == 0) {// 需要平台商审核
				goods.setGoods_status(-5);
				goods.setPublish_goods_status(CommUtil.null2Int(publish_goods_status)); // 发布审核后状态
				// 立即发布
				if ("0".equals(publish_goods_status)) {
					goods.setGoods_seller_time(new Date());
				}
				// 定时发布
				if ("2".equals(publish_goods_status)) {
					String str = publish_day + " " + publish_hour + ":" + publish_min;
					Date date = CommUtil.formatDate(str, "yyyy-MM-dd HH:mm");
					goods.setGoods_seller_time(date);
				}
			} else if (store.getGrade().getGoods_audit() == 1) {// 不需要平台商审核
				// 立即发布
				if ("0".equals(publish_goods_status)) {
					goods.setGoods_seller_time(new Date());
					goods.setGoods_status(0);
				}
				// 放入仓库
				if ("1".equals(publish_goods_status)) {
					goods.setGoods_seller_time(new Date());
					goods.setGoods_status(1);
				}
				// 定时发布
				if ("2".equals(publish_goods_status)) {
					String str = publish_day + " " + publish_hour + ":" + publish_min;
					Date date = CommUtil.formatDate(str, "yyyy-MM-dd HH:mm");
					goods.setGoods_seller_time(date);
					goods.setGoods_status(2);
				}
			}
			Accessory main_img = null;
			if (!StringUtils.isNullOrEmpty(goods_main_img_id)) {
				main_img = this.accessoryService.getObjById(Long.parseLong(goods_main_img_id));
			}
			goods.setGoods_main_photo(main_img);
			goods.getGoods_ugcs().clear();
			String[] ugc_ids = user_class_ids.split(",");
			temp_ids.clear();
			List u_class_list = new ArrayList();
			for (String ugc_id : ugc_ids) {
				if (!"".equals(ugc_id)) {
					UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.parseLong(ugc_id));
					u_class_list.add(ugc);
				}
			}
			goods.setGoods_ugcs(u_class_list);
			String[] img_ids = image_ids.split(",");
			goods.getGoods_photos().clear();
			temp_ids.clear();
			for (String img_id : img_ids) {
				if (!"".equals(img_id)) {
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
			if (!StringUtils.isNullOrEmpty(goods_brand_id)) {
				GoodsBrand goods_brand = this.goodsBrandService.getObjById(Long.parseLong(goods_brand_id));
				goods.setGoods_brand(goods_brand);
			}
			goods.getGoods_specs().clear();
			String[] spec_ids = goods_spec_ids.split(",");
			temp_ids.clear();
			for (String spec_id : spec_ids) {
				if (!"".equals(spec_id)) {
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
			if (!CommUtil.null2String(goods_spec_id_value).isEmpty()) {
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
				if (!"".equals(property)) {
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
			boolean warn_suppment = false;
			String[] inventory_list = intentory_details.split(";");
			for (String inventory : inventory_list) {
				if (!"".equals(inventory)) {
					String[] list = inventory.split(",");
					Map map = new HashMap();
					map.put("id", list[0]);
					map.put("count", list[1]);
					map.put("supp", list[2]);
					map.put("price", list[3]);
					maps.add(map);
					if (CommUtil.null2Int(list[2]) > CommUtil.null2Int(list[1])) {
						warn_suppment = true;
					}
				}
			}
			if (warn_suppment) {
				goods.setWarn_inventory_status(-1);
			} else {
				goods.setWarn_inventory_status(0);
			}
			goods.setGoods_type(1); // 商家发布商品
			goods.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));
			// 商品缺货状态操作
			if ("all".equals(goods.getInventory_type())) {
				int inventory = goods.getGoods_inventory();
				if (CommUtil.null2Int(inventory) - goods.getGoods_warn_inventory() > 0) {
					goods.setWarn_inventory_status(0); // 预警状态恢复
				} else {
					goods.setWarn_inventory_status(-1);
				}
			}
			if (CommUtil.null2Int(transport_type) == 0) {// 使用运费模板
				Transport trans = this.transportService.getObjById(CommUtil.null2Long(transport_id));
				goods.setTransport(trans);
			}
			if (CommUtil.null2Int(transport_type) == 1) {// 使用固定运费
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
				if (!"".equals(CommUtil.null2String(goods.getGoods_f_code()))) {
					f_code_maps = Json.fromJson(List.class, goods.getGoods_f_code());
				}

				for (String code : set) {
					Map f_code_map = new HashMap();
					f_code_map.put("code", code);
					f_code_map.put("status", 0); // 0表示该F码未使用，1为已经使用
					f_code_maps.add(f_code_map);
				}
				if (f_code_maps.size() > 0) {
					goods.setGoods_f_code(Json.toJson(f_code_maps, JsonFormat.compact()));
				}
			}
			// 是否为预售商品，是则加入发货时间
			if (goods.getAdvance_sale_type() == 1) {
				goods.setAdvance_date(CommUtil.formatDate(advance_date));
				goods.setGoods_status(-5); // 预售商品必须经过平台审核
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
			String delivery_area = de_area.getParent().getParent().getAreaName() + de_area.getParent().getAreaName()
					+ de_area.getAreaName();
			goods.setDelivery_area(delivery_area);
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
				
				// 生成商品二维码
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				this.goodsTools.createGoodsQR(request, goods, uploadFilePath);
				// 添加lucene索引
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
			} else {
				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
						+ File.separator + "goods";
				if ("0".equals(obj_status) && "0".equals(publish_goods_status)) {// 编辑后直接上架
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
				if ("0".equals(obj_status) && ("1".equals(publish_goods_status) || "2".equals(publish_goods_status))) {// 编辑后放入仓库
					// 删除lucene索引
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.delete_index(id);
				}
				if (("1".equals(obj_status) || "2".equals(obj_status)) && "0".equals(publish_goods_status)) {// 在仓库中编辑后上架
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
			String goods_view_url = CommUtil.getURL(request) + "/goods_" + goods.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store().getStore_second_domain() != "" && goods.getGoods_type() == 1) {
				String store_second_domain = "http://" + goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_view_url = store_second_domain + "/goods_" + goods.getId() + ".htm";
			}
			if (StringUtils.isNullOrEmpty(id)) {
				mv = new JModelAndView("user/default/sellercenter/add_goods_finish.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				if (store.getGrade().getGoods_audit() == 0) {
					mv.addObject("op_title", "商品发布成功,运营商会尽快为您审核！");
				}
				if (store.getGrade().getGoods_audit() == 1) {
					mv.addObject("op_title", "商品发布成功！");
				}
			} else {
				mv = new JModelAndView("user/default/sellercenter/seller_success.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				if (store.getGrade().getGoods_audit() == 0) {
					mv.addObject("op_title", "商品编辑成功,运营商会尽快为您审核");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
				}
				if (store.getGrade().getGoods_audit() == 1) {
					mv.addObject("op_title", "商品编辑成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
				}
			}
			mv.addObject("goods_view_url", goods_view_url);
			mv.addObject("goods_edit_url", CommUtil.getURL(request) + "/seller/goods_edit.htm?id=" + goods.getId());
			request.getSession(true).removeAttribute("goods_session");
		
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/goods.htm");
		}
		return mv;
	}

	/**
	 * AJAX加载当前商家的商品分类数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 *            上级分类Id
	 * @param session
	 *            是否加载到session中
	 */
	@SecurityMapping(title = "加载商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/load_goods_class.htm")
	public void load_goods_class(HttpServletRequest request, HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.getObjById(CommUtil.null2Long(pid));
		List<Map> list = new ArrayList<Map>();
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null) {
			if (obj.getLevel() == 0) {// 加载二级分类
				if(StringUtils.isNullOrEmpty(store.getGc_detail_info())) {//店铺未设置详细类目
					if(store.getGc_main_id().equals(obj.getId())) {
						for (GoodsClass child : obj.getChilds()) {
							Map map_gc = new HashMap();
							map_gc.put("id", child.getId());
							map_gc.put("className", child.getClassName());
							list.add(map_gc); 
						}
					}					
				} else {//设置了详细类目
					Map map = this.storeTools.query_MainGc_Map(CommUtil.null2String(obj.getId()), store.getGc_detail_info());
					List<Integer> ls = (List) map.get("gc_list");
					for (Integer l : ls) {
						GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(l));
						if(gc != null) {
							Map map_gc = new HashMap();						
							map_gc.put("id", gc.getId());
							map_gc.put("className", gc.getClassName());
							list.add(map_gc);
						}						
					}
				}				
			} else if (obj.getLevel() == 1) {// 加载三级分类
				for (GoodsClass child : obj.getChilds()) {
					Map map_gc = new HashMap();
					map_gc.put("id", child.getId());
					map_gc.put("className", child.getClassName());
					list.add(map_gc);
				}
			}
			if (CommUtil.null2Boolean(session)) {
				request.getSession(true).setAttribute("goods_class_info", obj);
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

	@SecurityMapping(title = "添加用户常用商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/add_goods_class_staple.htm")
	public void add_goods_class_staple(HttpServletRequest request, HttpServletResponse response) {
		String ret = "error";
		String json = "";
		Map map = new HashMap();
		boolean flag = true;
		if (request.getSession(true).getAttribute("goods_class_info") != null) {
			GoodsClass session_gc = (GoodsClass) request.getSession(true).getAttribute("goods_class_info");
			GoodsClass gc = this.goodsClassService.getObjById(session_gc.getId());
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			List<Map> list_map = new ArrayList<Map>();
			if (!StringUtils.isNullOrEmpty(user.getStaple_gc())) {
				json = user.getStaple_gc();
				list_map = Json.fromJson(List.class, json);
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
					ret = Json.toJson(map);
					user.setStaple_gc(json);
					this.userService.update(user);
				}
			} else {
				map.put("name", gc.getParent().getParent().getClassName() + ">" + gc.getParent().getClassName() + ">"
						+ gc.getClassName());
				map.put("id", gc.getId());
				list_map.add(map);
				json = Json.toJson(list_map, JsonFormat.compact());
				ret = Json.toJson(map);
				user.setStaple_gc(json);
				this.userService.update(user);
			}			
		}
		ret = Json.toJson(ret);
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

	@SecurityMapping(title = "删除用户常用商品分类", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/del_goods_class_staple.htm")
	public void del_goods_class_staple(HttpServletRequest request, HttpServletResponse response, String id) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
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

	@SecurityMapping(title = "根据用户常用商品分类加载分类信息", value = "/seller/load_goods_class_staple.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/load_goods_class_staple.htm")
	public void load_goods_class_staple(HttpServletRequest request, HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
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

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}

	@SecurityMapping(title = "出售中的商品列表", value = "/seller/goods.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods.htm")
	public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()), "=");
		qo.setOrderBy("warn_inventory_status,addTime");
		qo.setOrderType("desc");
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(user_class_id)) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.parseLong(user_class_id));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}

	@SecurityMapping(title = "仓库中的商品列表", value = "/seller/goods_storage.htm*", rtype = "seller", rname = "仓库中的商品", rcode = "goods_storage_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_storage.htm")
	public ModelAndView goods_storage(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_storage.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		Map prarms = new HashMap();
		Set ids = new TreeSet();
		ids.add(1); // 仓库中商品
		ids.add(2); // 定时发布商品
		ids.add(-5); // 未审核商品
		prarms.put("ids", ids);
		qo.addQuery("obj.goods_status in (:ids)", prarms);
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(user_class_id)) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.parseLong(user_class_id));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}

	@SecurityMapping(title = "违规下架商品", value = "/seller/goods_out.htm*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_out.htm")
	public ModelAndView goods_out(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_out.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
		}
		if (!StringUtils.isNullOrEmpty(user_class_id)) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.parseLong(user_class_id));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", storeTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}

	@SecurityMapping(title = "商品编辑", value = "/seller/goods_edit.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request, HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/add_goods_second.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (add_type == null) {// 非编辑商品的所属分类
			request.getSession(true).removeAttribute("goods_class_info");
		}
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		if (obj.getGoods_store().getUser().getId().equals(user.getId())) {
			String path = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
					+ File.separator + "store" + File.separator + user.getStore().getId();//会在应用服务器上保存商家上传上来的图片等数据文件吗？这些文件不是都保存在FTP上了吗？？？2016-07-05
			double img_remain_size = user.getStore().getGrade().getSpaceSize()
					- CommUtil.div(CommUtil.fileSize(new File(path)), 1024);
			if (img_remain_size < 0) {
				img_remain_size = -1;
			}
			mv.addObject("img_remain_size", img_remain_size);
			
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService.query(
					"select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
					params, -1, -1);
			mv.addObject("ugcs", ugcs);
			
			// 查询自己店铺类型
			params.clear();
			params.put("store_id", user.getStore().getId());
			List<GoodsTypeProperty> gtps = this.gtpService.query(
					"select obj from GoodsTypeProperty obj where obj.store_id=:store_id order by sequence asc", params, -1, -1);
			if (gtps.size() > 0) {
				mv.addObject("gtps", gtps);
			}		
			
			mv.addObject("obj", obj);
			
			GoodsClass gc = null;			
			if (request.getSession(true).getAttribute("goods_class_info") != null) {//表示是通过编辑商品所属分类，然后从add_goods_first.html进来的请求
				GoodsClass session_gc = (GoodsClass) request.getSession(true).getAttribute("goods_class_info");
				gc = this.goodsClassService.getObjById(session_gc.getId());				
				request.getSession(true).removeAttribute("goods_class_info");
			} else {//在商品列表中点击商品的“编辑”按钮进来的请求
				gc = obj.getGc();				
			}
			mv.addObject("goods_class_info", this.storeTools.generic_goods_class_info(gc));
			mv.addObject("goods_class", gc);
			
			params.clear();
			if(gc.getLevel() == 2) {
				params.put("gc_id", gc.getParent().getParent().getId());
			} else if(gc.getLevel() == 1) {
				params.put("gc_id", gc.getParent().getId());
			} 			
			List<GoodsBrand> gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.gc.id=:gc_id order by obj.sequence asc", params, -1, -1);
			mv.addObject("gbs", gbs);
			
			List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
			params.clear();
			if (gc.getLevel() == 2) {// 发布商品选择类目时选择三级类目,查询出所有与该三级类目关联的规格
				params.put("store_id", user.getStore().getId());
				params.put("gc_id", gc.getParent().getId());
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.query(
						"select obj from GoodsSpecification obj where obj.store.id=:store_id and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);					
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
			} else if (gc.getLevel() == 1) {// 发布商品选择类目时选择二级类目时，将与此二级类目及其所有下级类目关联的规格取出来供用户选择
				params.put("store_id", user.getStore().getId());
				params.put("gc_id", gc.getId());
				spec_list = this.goodsSpecificationService.query(
						"select obj from GoodsSpecification obj where obj.store.id=:store_id and obj.goodsclass.id=:gc_id order by sequence asc", params, -1, -1);
			}
			mv.addObject("goods_spec_list", spec_list);
			
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(true).setAttribute("goods_session", goods_session);
			mv.addObject("imageSuffix",
					this.storeViewTools.genericImageSuffix(this.configService.getSysConfig().getImageSuffix()));
			// 处理上传格式
			String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
			Date now = new Date();
			now.setDate(now.getDate() + 1);
			mv.addObject("default_publish_day", CommUtil.formatShortDate(now));
			mv.addObject("store", obj.getGoods_store());
			// 查询商品版式信息
			params.clear();
			params.put("gf_store_id", user.getStore().getId());
			List<GoodsFormat> gfs = this.goodsFormatService
					.query("select obj from GoodsFormat obj where obj.gf_store_id=:gf_store_id", params, -1, -1);
			mv.addObject("gfs", gfs);
			// 查询地址信息，前端需要商家选择发货地址
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			mv.addObject("areas", areas);
			Area de_area = this.areaService.getObjById(obj.getDelivery_area_id());
			mv.addObject("de_area", de_area);
			mv.addObject("jsessionid", request.getSession().getId());
		} else {
			mv = new JModelAndView("user/default/sellercenter/seller_error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "您没有该商品信息！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "商品上下架", value = "/seller/goods_sale.htm*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_sale.htm")
	public String goods_sale(HttpServletRequest request, HttpServletResponse response, String mulitId) {
		String url = "/seller/goods.htm";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				Goods goods = this.goodsService.getObjById(Long.parseLong(id));
				if (goods.getGoods_status() != -5) {
					User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (goods.getGoods_store().getUser().getId().equals(user.getId())) {
						int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
						goods.setGoods_status(goods_status);
						this.goodsService.update(goods);
						if (goods_status == 0) {
							url = "/seller/goods_storage.htm";
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
							// 删除索引
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
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(title = "商品删除", value = "/seller/goods_del.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_del.htm")
	public String goods_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String op) {
		String url = "/seller/goods.htm";
		if ("storage".equals(CommUtil.null2String(op))) {
			url = "/seller/goods_storage.htm";
		} else if ("out".equals(CommUtil.null2String(op))) {
			url = "/seller/goods_out.htm";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
				User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				if (goods.getGoods_store().getUser().getId().equals(user.getId())) {
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					Map params = new HashMap();
					params.put("gid", CommUtil.null2Long(id));
					List<ComplaintGoods> complaintGoodses = this.complaintGoodsService
							.query("select obj from ComplaintGoods obj where obj.goods.id=:gid", params, -1, -1);
					for (ComplaintGoods cg : complaintGoodses) {
						this.complaintGoodsService.delete(cg.getId());
					}
					List<GroupGoods> groupGoodses = this.groupGoodsService
							.query("select obj from GroupGoods obj where obj.gg_goods.id=:gid", params, -1, -1);
					for (GroupGoods gg : groupGoodses) {
						this.groupGoodsService.delete(gg.getId());
					}
					params.clear();
					for (Accessory acc : goods.getGoods_photos()) {
						params.put("acid", acc.getId());
						List<Album> als = this.albumService
								.query("select obj from Album obj where obj.album_cover.id = :acid", params, -1, -1);
						for (Album al : als) {
							al.setAlbum_cover(null);
							this.albumService.update(al);
						}
						params.clear();
					}
					if (goods.getGoods_main_photo() != null) {
						params.put("acid", goods.getGoods_main_photo().getId());
						List<Album> als = this.albumService
								.query("select obj from Album obj where obj.album_cover.id = :acid", params, -1, -1);
						for (Album al : als) {
							al.setAlbum_cover(null);
							this.albumService.update(al);
						}
						CommUtil.del_acc(request, goods.getGoods_main_photo());
						goods.setGoods_main_photo(null);
						this.goodsService.update(goods);
					}
					List<ZTCGoldLog> ztcGoldLogs = this.iztcGoldLogService.query(
							"select obj from ZTCGoldLog obj where obj.zgl_goods_id=" + CommUtil.null2Long(id), null, -1, -1);
					if (ztcGoldLogs.size() > 0) {
						for (ZTCGoldLog ztcGoldLog : ztcGoldLogs) {
							this.iztcGoldLogService.delete(ztcGoldLog.getId());
						}
					}
					for (GoodsCart cart : goods.getCarts()) {
						this.cartService.delete(cart.getId());
					}
					
					ProductMapping productMapping=goods.getProductMapping();
					if(productMapping != null){
						productMapping.setGoodsCode(null);
						productMapping.setGoods(null);
						productMapping.setGoodsConfig(null);
						this.productMappingService.delete(productMapping.getId());
					}
					
					goods.getCarts().clear();
					goods.getGoods_ugcs().clear();
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getAg_goods_list().clear();
					goods.getEvaluates().clear();
					goods.getGoods_photos().clear();
					goods.getGroup_goods_list().clear();
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
				}
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(title = "商家商品相册列表", value = "/seller/goods_album.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_album.htm")
	public ModelAndView goods_album(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String ajax_type, String album_name) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_album.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		AlbumQueryObject aqo = new AlbumQueryObject();
		aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
		if (CommUtil.null2String(album_name) != "") {
			aqo.addQuery("obj.album_name", new SysMap("album_name", "%" + album_name + "%"), "like");
			mv.addObject("album_name", album_name);
		}
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setOrderBy("album_sequence");
		aqo.setOrderType("asc");
		aqo.setPageSize(5);
		IPageList pList = this.albumService.list(aqo);
		String album_url = CommUtil.getURL(request) + "/seller/goods_album.htm";
		if (pList.getResult().size() > 0) {
			mv.addObject("albums", pList.getResult());
		}
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(album_url, "", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("ajax_type", ajax_type);
		mv.addObject("ImageTools", ImageTools);
		return mv;
	}

	@SecurityMapping(title = "商家商品图片列表", value = "/seller/goods_img.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_img.htm")
	public ModelAndView goods_img(HttpServletRequest request, HttpServletResponse response, String currentPage, String type,
			String album_id) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/" + type + ".html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv, "addTime", "desc");
		aqo.setPageSize(20);
		aqo.addQuery("obj.album.id", new SysMap("album_id", CommUtil.null2Long(album_id)), "=");
		aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request) + "/seller/goods_img.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url, "", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("album_id", album_id);
		return mv;
	}

	@SecurityMapping(title = "商品二维码生成", value = "/seller/goods_qr.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_qr.htm")
	public String goods_qr(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage)
			throws ClassNotFoundException {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
				if (obj.getGoods_type() == 1 && obj.getGoods_store().getId().equals(user.getStore().getId())) {// 只能操作商家自己的商品
					String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
					String destPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath
							+ File.separator + "cache";
					if (!CommUtil.fileExist(destPath)) {
						CommUtil.createFolder(destPath);
					}
					destPath = destPath + File.separator + id + "_qr.jpg";
					
					
					String logoPath = "";
					if (obj.getGoods_main_photo() != null) {
						this.FTPTools.userDownloadImg(obj.getGoods_main_photo(), String.valueOf(obj.getGoods_store().getUser().getId()));
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
								this.FTPTools.systemDownloadImg(defaulImg);
							}
						}										
					}					
					
					QRCodeUtil.encode(configService.getSysConfig().getGoodsH5Url() + "?goodsId=" + id, logoPath, destPath, true);
					
					// 将二维码图片上传ftp
					String url = this.FTPTools.systemUpload(id + "_qr.jpg", "/goods_qr");
					obj.setQr_img_path(url + "/" + id + "_qr.jpg");
					this.goodsService.update(obj);
					// 删除主图
					this.FTPTools.DeleteWebImg(obj.getGoods_main_photo());
				}
			}
		}
		return "redirect:goods.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "打开关联商品版式", value = "/seller/goods_format.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format.htm")
	public ModelAndView goods_format(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_format.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// 查询商品版式信息
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("gf_store_id", user.getStore().getId());
		List gfs = this.goodsFormatService.query("select obj from GoodsFormat obj where obj.gf_store_id=:gf_store_id",
				params, -1, -1);
		mv.addObject("gfs", gfs);
		mv.addObject("mulitId", mulitId);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "批量保存关联商品版式", value = "/seller/goods_format_link.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_format_link.htm")
	public void goods_format_link(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, String goods_top_format_id, String goods_bottom_format_id) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(CommUtil.null2String(id))) {
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
				Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()).getStore();
				if (goods.getGoods_store().getId().equals(store.getId())) {
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
					this.goodsService.update(goods);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "商品F码Excel下载", value = "/seller/goods_self_f_code_download.htm*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_self_f_code_download.htm")
	public void goods_self_f_code_download(HttpServletRequest request, HttpServletResponse response, String id)
			throws IOException {
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		String excel_url = "";
		Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()).getStore();
		if (obj.getF_sale_type() == 1 && obj.getGoods_type() == 1 && obj.getGoods_store().getId().equals(store.getId())
				&& !"".equals(CommUtil.null2String(obj.getGoods_f_code()))) {
			List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
			String name = CommUtil.null2String(UUID.randomUUID());
			String path = CommUtil.getServerRealPathFromRequest(request) + "excel"
					+ File.separator + name + ".xls";
			this.exportList2Excel("F码列表", new String[] { "F码信息", "F码状态" }, list, response, name);
		}
	}

	// supplement
	@SecurityMapping(title = "商品补货", value = "/seller/goods_supplement.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_supplement.htm")
	public ModelAndView goods_supplement(HttpServletRequest request, HttpServletResponse response, String id)
			throws IOException {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_supplement.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String gsp_ids = "";
		for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
			gsp_ids = gsp_ids + "," + gsp.getId();
		}
		if (obj != null && obj.getGoods_store().getId().equals(user.getStore().getId())) {
			Map spec_map = new HashMap();
			spec_map.put("store_id", user.getStore().getId());
			List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.query(
					"select obj from GoodsSpecification obj where obj.store.id=:store_id order by sequence asc", spec_map,
					-1, -1);
			List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
			for (GoodsSpecification gspec : goods_spec_list) {
				for (GoodsClass spec_goodsclass_detail : gspec.getSpec_goodsClass_detail()) {
					if (obj.getGc().getId().equals(spec_goodsclass_detail.getId())
							|| obj.getGc().getParent().getId().equals(spec_goodsclass_detail.getId())) {
						spec_list.add(gspec);
					}
				}
				mv.addObject("goods_spec_list", spec_list);
			}
			mv.addObject("gsp_ids", gsp_ids);
			mv.addObject("obj", obj);
		}
		return mv;
	}

	// supplement
	@SecurityMapping(title = "商品补货保存", value = "/seller/goods_supplement_save.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_supplement_save.htm")
	public void goods_supplement_save(HttpServletRequest request, HttpServletResponse response, String id, String inventory,
			String intentory_details) throws IOException {
		int code = -100;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map json_map = new HashMap();
		boolean warn_suppment = true;
		if (obj != null && obj.getGoods_store().getId().equals(user.getStore().getId())) {
			if ("all".equals(obj.getInventory_type())) {
				obj.setGoods_inventory(CommUtil.null2Int(inventory));
				if (CommUtil.null2Int(inventory) - obj.getGoods_warn_inventory() > 0) {
					obj.setWarn_inventory_status(0); // 预警状态恢复
				}
				code = 100;
			}
			if ("spec".equals(obj.getInventory_type())) {
				List<Map> maps = (List<Map>) Json.fromJson(obj.getGoods_inventory_detail());
				for (Map map : maps) {
					String[] inventory_list = intentory_details.split(";");
					for (String temp_inventory : inventory_list) {
						if (!"".equals(temp_inventory)) {
							String[] list = temp_inventory.split(",");
							if (list[0].equals(CommUtil.null2String(map.get("id")))) {
								map.put("count", list[1]);
								if (CommUtil.null2Int(map.get("count")) <= CommUtil.null2Int(map.get("supp"))) {
									warn_suppment = false;
								}
							}
						}
					}
				}
				if (warn_suppment) {
					obj.setWarn_inventory_status(0); // 预警状态恢复
				}
				obj.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));
				code = 100;
			}
			this.goodsService.update(obj);
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 商品发布页新增属性ajax保存,count为数据的条数，params为填写的数据包括名称序号以及属性值
	 * 
	 * @param request
	 * @param response
	 * @param count
	 * @param params
	 * @return
	 */
	@SecurityMapping(title = "属性Ajax保存", value = "/seller/ajax_save_gtp.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping("/seller/ajax_save_gtp.htm")
	public ModelAndView ajax_save_gtp(HttpServletRequest request, HttpServletResponse response, String count,
			String params) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/goods_type_load.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String gtps[] = params.substring(1).split("&");
		List gtpList = new ArrayList<>();
		for (int i = 0; i < CommUtil.null2Int(count); i++) {
			if (gtps.length > 0) {
				String gtp_info[] = gtps[i].split("/");
				GoodsTypeProperty goodsTypeProperty = new GoodsTypeProperty();
				goodsTypeProperty.setName(gtp_info[0]);
				goodsTypeProperty.setValue(gtp_info[1]);
				goodsTypeProperty.setSequence(CommUtil.null2Int(gtp_info[2]));
				goodsTypeProperty.setStore_id(user.getStore().getId());
				this.gtpService.save(goodsTypeProperty);
				gtpList.add(goodsTypeProperty);
			}
		}
		Comparator comparator = new Comparator<GoodsTypeProperty>() {
			public int compare(GoodsTypeProperty g1, GoodsTypeProperty g2) {
				if (g1.getSequence() != g2.getSequence()) {
					return g1.getSequence() - g2.getSequence();
				} else {
					return g1.getSequence();
				}
			}
		};
		Collections.sort(gtpList, comparator);
		mv.addObject("gtpList", gtpList);
		return mv;
	}

	/**
	 * 新增商品页属性新增的删除，id为属性的id;
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品类型属性AJAX删除", value = "/seller/gtp_delete.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping("/seller/gtp_delete.htm")
	public void goods_type_property_delete(HttpServletRequest request, HttpServletResponse response, String id) {
		boolean ret = true;
		if (!"".equals(id)) {
			GoodsTypeProperty property = this.gtpService.getObjById(Long.parseLong(id));
			property.setGoodsType(null);
			ret = this.gtpService.delete(property.getId());
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

	private static boolean exportList2Excel(String title, String[] headers, List<Map> list, HttpServletResponse response,
			String name) {
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
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + name + ".xls");
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return ret;
	}
}