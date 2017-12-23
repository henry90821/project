package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.StoreQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.manage.admin.tools.AreaManageTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;

/**
 * 
 * 运营商店铺管理控制器，用来管理店铺，可以添加、修改、删除店铺，运营商所有对店铺的操作均通过该管理控制器完成
 */
@Controller
public class StoreManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private AreaManageTools areaManageTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IComplaintGoodsService complaintGoodsService;
	
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IPayoffLogService paylogService;
	@Autowired
	private IGoodsSpecPropertyService specpropertyService;
	@Autowired
	private IGoldRecordService grService;
	@Autowired
	private IZTCGoldLogService ztcglService;
	@Autowired
	private IGoldLogService glService;
	@Autowired
	private FTPServerTools FTPTools;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IGoodsConfigService goodsConfigService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private UserTools userTools;

	/**
	 * Store列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_list.htm")
	public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String store_status, String store_name, String grade_id) {
		ModelAndView mv = new JModelAndView("admin/blue/store_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(store_status)) {
			qo.addQuery("obj.store_status", new SysMap("store_status", CommUtil.null2Int(store_status)), "=");
			mv.addObject("store_status", store_status);
		}
		if (!StringUtils.isNullOrEmpty(store_name)) {
			qo.addQuery("obj.store_name", new SysMap("store_name", "%" + CommUtil.null2String(store_name) + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (!StringUtils.isNullOrEmpty(grade_id)) {
			qo.addQuery("obj.grade.id", new SysMap("grade_id", CommUtil.null2Long(grade_id)), "=");
			mv.addObject("grade_id", grade_id);
		}
		IPageList pList = this.storeService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		List<StoreGrade> grades = this.storeGradeService.query("select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		return mv;
	}

	/**
	 * store添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_add.htm")
	public ModelAndView store_add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺添加2", value = "/admin/store_new.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_new.htm")
	public ModelAndView store_new(HttpServletRequest request, HttpServletResponse response, String mobile, String list_url, String uId) {
		ModelAndView mv = new JModelAndView("admin/blue/store_new.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = null;
		if(!StringUtils.isNullOrEmpty(mobile)) {
			user = this.userService.getBuyerOrMainSellerByMobile(mobile);
		} else if(!StringUtils.isNullOrEmpty(uId)){
			user = this.userService.getObjById(CommUtil.null2Long(uId));
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "参数有误");
			
			return mv;
		}
		
		if (user == null) {
			mv = new JModelAndView("admin/blue/tip.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_tip", "未找到手机号为" + mobile + "的会员");
			mv.addObject("list_url", list_url);
		} else {
			List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			List<StoreGrade> grades = this.storeGradeService.query("select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
			mv.addObject("grades", grades);
			mv.addObject("areas", areas);
			mv.addObject("user", user);
			List<GoodsClass> gcs = this.goodsclassService.query("select obj from GoodsClass obj where obj.parent.id is null ", null, -1, -1);
			mv.addObject("goodsClass", gcs);
			
			mv.addObject("goodsConfigs", this.goodsConfigService.query("select obj from GoodsConfig obj where obj.goodsSource=1", null, -1, -1));			
			
			Store store = user.getStore();//可能用户已提交了开店申请，但还未提交或未审核通过
			if(store != null) {
				mv.addObject("obj", store);
				
				mv.addObject("goodsClass_main", this.goodsclassService.getObjById(store.getGc_main_id()));
				mv.addObject("goodsClass_detail", this.storeTools.query_store_DetailGc(store.getGc_detail_info()));
				mv.addObject("m_ids", this.storeTools.query_m_id(store.getGc_detail_info()));
	            mv.addObject("Gc_commission_info", (List) JSON.parse(store.getGc_commission_info()));
				
				StringBuilder sb = new StringBuilder();
				for(GoodsConfig gc: store.getGoodsConfigList()) {
					sb.append(gc.getId()).append(",");
				}
				if(sb.length() > 0) {
					mv.addObject("storeGoodsConfigIds", sb.substring(0, sb.length() - 1));
				}
				
				String gcDetails = store.getGc_detail_info();
				if(!StringUtils.isNullOrEmpty(gcDetails)) {
					List<Map> list = Json.fromJson(List.class, gcDetails);
					sb.delete(0, sb.length());
					for(Map m: list) {
						List gc_list = (List)m.get("gc_list");
						for(int i = 0; i < gc_list.size(); i++) {
							sb.append(gc_list.get(i)).append(",");
						}						
					}
					if(sb.length() > 0) {
						mv.addObject("gcDetailsIds", sb.toString());
					}
				}
			}			
		}
		return mv;
	}

	/**
	 * 店铺经营类目Ajax加载
	 * 
	 * @param request
	 * @param response
	 * @param cid
	 * @return
	 */
	@SecurityMapping(title = "店铺添加2", value = "/admin/store_gc_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gc_ajax.htm")
	public ModelAndView store_goodsclass_dialog(HttpServletRequest request, HttpServletResponse response, String cid) {
		ModelAndView mv = new JModelAndView("admin/blue/store_gc_ajax.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(cid)) {
			GoodsClass goodsClass = this.goodsclassService.getObjById(CommUtil.null2Long(cid));
			Set<GoodsClass> gcs = goodsClass.getChilds();
			mv.addObject("gcs", gcs);
		}
		return mv;
	}

	/**
	 * store公司信息查看
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺公司信息查看", value = "/admin/store_company.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_company.htm")
	public ModelAndView store_company(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_company.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		mv.addObject("store", store);
		Area area1 = store.getLicense_area();
		mv.addObject("license_area_info", this.areaManageTools.generic_area_info(area1));
		Area area2 = store.getLicense_c_area();
		mv.addObject("license_c_area_info", this.areaManageTools.generic_area_info(area2));
		Area area3 = store.getBank_area();
		mv.addObject("bank_area_info", this.areaManageTools.generic_area_info(area3));
		return mv;
	}

	/**
	 * store保存管理
	 * 
	 * @param id
	 * @param gc_main_id
	 *            :主营类目id
	 * @param gc_detail_ids
	 *            ：详细类目id
	 * @param id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_save.htm")
	public ModelAndView store_save(HttpServletRequest request, HttpServletResponse response, String id, String store_status,
			String currentPage, String cmd, String list_url, String add_url, String user_id, String grade_id, Long area_id,
			String validity, String gc_main_id_clone, String gc_detail_ids, String gc_detail_info, String sellerLoginAccount) throws Exception {
		
		Long uId = Long.parseLong(user_id);
		User user = this.userService.getObjById(uId);
		sellerLoginAccount = CommUtil.clearContent(sellerLoginAccount);
		
		if(this.userTools.getUser("SELLER", sellerLoginAccount, uId) != null) {
			ModelAndView mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "店铺登录账号名：" + sellerLoginAccount + " 已存在，请使用其它登录账号名");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
			return mv;
		} else {
			user.setSellerLoginAccount(sellerLoginAccount);
		}
		
		String sellerAccountInitPwd = "123qwe";
		WebForm wf = new WebForm();
		Store store = null;
		if (StringUtils.isNullOrEmpty(id)) {
			store = wf.toPo(request, Store.class);
			store.setAddTime(new Date());
			user.setStore(store);
		} else {
			Store obj = this.storeService.getObjById(Long.parseLong(id));
			store = (Store) wf.toPo(request, obj);
		}
		
		String[] store_types = request.getParameterValues("store_type"); 
		String[] gc_commission_arr = request.getParameterValues("gc_commission"); 
		String[] gc_id_arr = request.getParameterValues("gc_id");
		Map map = null;
		List<Map> json = new ArrayList<Map>();// 用于转换成店铺中的详细经营类目json
		String regex = "^0{1}(\\.{1}\\d{1,2})?$"; 
		if(!StringUtils.isNullOrEmpty(store.getGc_commission_info())){
			json = Json.fromJson(ArrayList.class, store.getGc_commission_info());
		}
		Map<String, String> m = new HashMap<String, String>();
		List<Map> list = new ArrayList<Map>();
		if(gc_id_arr != null){
			for(int i=0; i<gc_id_arr.length; i++){
				if(gc_commission_arr != null){
					String g_id = gc_id_arr[i];
					String gcommission = gc_commission_arr[i];
					m.put(g_id, gcommission);
				}
			}
			//gc_commission_info不为空
			if(json != null){
				for(Map ma : json) {
					String str_id = CommUtil.null2String(ma.get("id"));
					if(m.containsKey(str_id)){
						if(!StringUtils.isNullOrEmpty(m.get(str_id)) && Pattern.matches(regex, m.get(str_id))){
							GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(str_id));
							map = new HashMap();
							map.put("id", str_id);
							map.put("commission", m.get(str_id));
							map.put("parentId", gc.getParent()== null ? "" : CommUtil.null2String(gc.getParent().getId()));
							map.put("className", gc.getClassName());
							m.remove(CommUtil.null2String(ma.get("id")));
							list.add(map);
							
						}
					}else{
						map = new HashMap();
						map.put("id", CommUtil.null2String(ma.get("id")));
						map.put("commission", CommUtil.null2String(ma.get("commission")));
						map.put("parentId", CommUtil.null2String(ma.get("parentId")));
						map.put("className", CommUtil.null2String(ma.get("className")));
						list.add(map);
					}
				}
				for (String key : m.keySet()) {
					if(!StringUtils.isNullOrEmpty(m.get(key)) && Pattern.matches(regex, m.get(key))){
						GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(key));
						map = new HashMap();
						map.put("id", key);
						map.put("commission", m.get(key));
						map.put("parentId", gc.getParent()== null ? "" : CommUtil.null2String(gc.getParent().getId()));
						map.put("className", gc.getClassName());
						list.add(map);
					}
				}
			//gc_commission_info为空
			}else{
				if(gc_id_arr != null){
					for(int i=0; i<gc_id_arr.length; i++){
						if(gc_commission_arr != null){
							if(!StringUtils.isNullOrEmpty(gc_commission_arr[i]) && Pattern.matches(regex, gc_commission_arr[i])){
								GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(gc_id_arr[i]));
								map = new HashMap();
								map.put("id", gc_id_arr[i]);
								map.put("commission", gc_commission_arr[i]);
								map.put("parentId", gc.getParent()== null ? "" : CommUtil.null2String(gc.getParent().getId()));
								map.put("className", gc.getClassName());
								list.add(map);
							}
						}
					}
				}
			}
			String listJson = Json.toJson(list, JsonFormat.compact());
			store.setGc_commission_info(listJson);
		}
		
		if(store_types != null){
			StringBuffer stringBuffer = new StringBuffer();
			for(String store_type : store_types){
				stringBuffer.append(store_type).append(",");
			}
			String ids=stringBuffer.toString().substring(0, stringBuffer.toString().lastIndexOf(","));
			ids="("+ids+")";
		    
			List<GoodsConfig> goodsConfigList = this.goodsConfigService.query("select obj from GoodsConfig obj where obj.id in "+ids, null, -1, -1);
			store.setGoodsConfigList(goodsConfigList);
		}
		
		store.setArea(this.areaService.getObjById(area_id));
		store.setGrade(this.storeGradeService.getObjById(CommUtil.null2Long(grade_id)));
		store.setGc_main_id(CommUtil.null2Long(gc_main_id_clone));
		store.setValidity(CommUtil.formatDate(validity));
		
		if(!StringUtils.isNullOrEmpty(gc_detail_info) && !"[]".equals(gc_detail_info.trim())) {
			store.setGc_detail_info(gc_detail_info);
		} else {
			store.setGc_detail_info(null);
		}
		
		if (store.getStore_start_time() == null) { // 开店时间为空，意味着入驻审核通过，成功开店
			store.setStore_start_time(new Date());
		}
		
		if (!StringUtils.isNullOrEmpty(store_status) && CommUtil.null2Int(store_status) != store.getStore_status()) {
			if ("15".equals(store_status)) {// 入驻成功		
				if("BUYER".equals(user.getUserRole())) {
					user.setUserRole("SELLER");
					
					// 给用户赋予卖家权限
					Map params = new HashMap();
					params.put("type", "SELLER");
					List<Role> roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
					user.getRoles().addAll(roles);
				} 
				
				//初始化商家的登录密码
				user.setPassword(Md5Encrypt.md5(sellerAccountInitPwd).toLowerCase());
				
			} else if ("20".equals(store_status)) { // 关闭违规店铺发送站内信提醒
				if (!StringUtils.isNullOrEmpty(id) && store.getStore_status() == 20) {
					this.send_site_msg(request, "msg_toseller_store_closed_notify", store);
				}
			}
			
			store.setStore_status(CommUtil.null2Int(store_status));
		}
		
		if (store.isStore_recommend()) {
			store.setStore_recommend_time(new Date());
		} else {
			store.setStore_recommend_time(null);
		}
			
		boolean success = true;
		if (StringUtils.isNullOrEmpty(id)) {//为买家直接创建店铺
			success = this.storeService.save(store);
			if(success) {
				if (store.getPoint() == null) {
					StorePoint sp = new StorePoint();
					sp.setAddTime(new Date());
					sp.setStore(store);
					sp.setStore_evaluate(BigDecimal.valueOf(0));
					sp.setDescription_evaluate(BigDecimal.valueOf(0));
					sp.setService_evaluate(BigDecimal.valueOf(0));
					sp.setShip_evaluate(BigDecimal.valueOf(0));
					sp.setUser(user);
					this.storePointService.save(sp);
				}
			}
		} else {
			success = this.storeService.update(store);
		}
		
		if(success) {
			if(this.userService.update(user)) {
				String content = "尊敬的" + user.getUserName() + "您好！恭喜您通过审核，正式成为星美汇商家；您登录商家后台的初始密码为" + sellerAccountInitPwd + "，请您及时登录商家后台修改密码。";
				this.msgTools.sendSMS(user.getMobile(), content);
			}
		}
		
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	private void send_site_msg(HttpServletRequest request, String mark, Store store) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		if (template != null && template.isOpen()) {
			if (store.getUser() != null) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				context.setVariable("reason", store.getViolation_reseaon());
				context.setVariable("user", store.getUser());
				context.setVariable("store", store);
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
				Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
				String content = ex.getValue(context, String.class);
				Map params = new HashMap();
				params.put("userName", "admin");
				params.put("userRole", "ADMIN");
				List<User> fromUsers = this.userService.query(
						"select obj from User obj where obj.userName=:userName and obj.userRole=:userRole", params, -0, 1);
				if (fromUsers.size() > 0) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					msg.setFromUser(fromUsers.get(0));
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageService.save(msg);
				}
			}
		}
	}

	@SecurityMapping(title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_del.htm")
	public String store_del(HttpServletRequest request, String mulitId) throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				Store store = this.storeService.getObjById(CommUtil.null2Long(id));
				Map params = new HashMap();
				if (store.getUser() != null) {
					store.getUser().setStore(null);
					User user = store.getUser();
					if (user != null) {
						Set<Role> roles = user.getRoles();
						Set<Role> new_roles = new HashSet<Role>();
						for (Role role : roles) {
							if (!"SELLER".equals(role.getType())) {
								new_roles.add(role);
							}
						}
						user.getRoles().clear();// 清除所有权限，重新添加不含商家的权限信息
						user.getRoles().addAll(new_roles);//
						user.setStore_apply_step(0);
						this.userService.update(user);
						for (User u : user.getChilds()) { // 清除子账户所有权限信息
							roles = u.getRoles();
							Set<Role> new_roles2 = new HashSet<Role>();
							for (Role role : roles) {
								if (!"SELLER".equals(role.getType())) {
									new_roles2.add(role);
								}
							}
							u.getRoles().clear();// 清除所有权限，重新添加不含商家的权限信息
							u.getRoles().addAll(new_roles2);//
							u.setStore_apply_step(0);
							this.userService.update(u);
						}
					}
					params.clear();// 删除商家优惠券
					params.put("store_id", store.getId());
					List<Coupon> coupons = this.couponService
							.query("select obj from Coupon obj where obj.store.id=:store_id", params, -1, -1);
					for (Coupon coupon : coupons) {
						for (CouponInfo couponInfo : coupon.getCouponinfos()) {
							this.couponInfoService.delete(couponInfo.getId());
						}
						this.couponService.delete(coupon.getId());
					}

					for (GoldRecord gr : user.getGold_record()) { // 商家充值记录
						this.grService.delete(gr.getId());
					}
					params.clear();
					params.put("uid", user.getId());
					List<GoldLog> gls = this.glService.query("select obj from GoldLog obj where obj.gl_user.id=:uid", params,
							-1, -1);
					for (GoldLog gl : gls) {
						this.glService.delete(gl.getId());
					}
					for (GoldRecord gr : user.getGold_record()) {
						this.grService.delete(gr.getId());
					}
					for (GroupLifeGoods glg : user.getGrouplifegoods()) { // 商家发布的生活购
						for (GroupInfo gi : glg.getGroupInfos()) {
							this.groupinfoService.delete(gi.getId());
						}
						glg.getGroupInfos().removeAll(glg.getGroupInfos());
						this.grouplifegoodsService.delete(CommUtil.null2Long(glg.getId()));
					}
					for (PayoffLog log : user.getPaylogs()) { // 商家结算日志
						this.paylogService.delete(log.getId());
					}
					for (Album album : user.getAlbums()) { // 商家相册删除
						album.setAlbum_cover(null);
						this.albumService.update(album);
						params.clear();
						params.put("album_id", album.getId());
						List<Accessory> accs = this.accessoryService
								.query("select obj from Accessory obj where obj.album.id=:album_id", params, -1, -1);
						for (Accessory acc : accs) {
							this.FTPTools.userDeleteFtpImg(acc, CommUtil.null2String(acc.getId()));
							this.accessoryService.delete(acc.getId());
						}
						this.albumService.delete(album.getId());
					}
				}
				for (Goods goods : store.getGoods_list()) { // 店铺内的商品
					goods.setGoods_main_photo(null);
					goods.setGoods_brand(null);
					this.goodsService.update(goods);
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getGoods_ugcs().clear();
				}
				for (Goods goods : store.getGoods_list()) { // 删除店铺内的商品
					for (GoodsCart gc : goods.getCarts()) {
						this.goodsCartService.delete(gc.getId());
					}
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					for (ComplaintGoods cg : goods.getCgs()) {
						this.complaintGoodsService.delete(cg.getId());
					}
					// 删除相应商品收藏
					params.clear();
					params.put("gid", goods.getId());
					List<Favorite> favs = this.favoriteService.query("select obj from Favorite obj where obj.goods_id=:gid",
							params, -1, -1);
					for (Favorite obj : favs) {
						this.favoriteService.delete(obj.getId());
					}
					goods.getCarts().removeAll(goods.getCarts());// 移除对象中的购物车
					goods.getEvaluates().removeAll(goods.getEvaluates());
					goods.getCgs().removeAll(goods.getCgs());
					params.clear();// 直通车商品记录
					params.put("gid", goods.getId());
					List<ZTCGoldLog> ztcgls = this.ztcglService
							.query("select obj from ZTCGoldLog obj where obj.zgl_goods_id=:gid", params, -1, -1);
					for (ZTCGoldLog ztc : ztcgls) {
						this.ztcglService.delete(ztc.getId());
					}
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
				store.getGoods_list().removeAll(store.getGoods_list());

				for (GoodsSpecification spec : store.getSpecs()) { // 店铺规格
					for (GoodsSpecProperty pro : spec.getProperties()) {
						this.specpropertyService.delete(pro.getId());
					}
					spec.getProperties().removeAll(spec.getProperties());
				}
				String path = CommUtil.getServerRealPathFromRequest(request)
						+ this.configService.getSysConfig().getUploadFilePath() + File.separator + "store" + File.separator
						+ store.getId();
				CommUtil.deleteFolder(path);
				// 删除店铺收藏
				params.clear();
				params.put("store_id", CommUtil.null2Long(id));
				List<Favorite> favs = this.favoriteService.query("select obj from Favorite obj where obj.store_id=:store_id",
						params, -1, -1);
				for (Favorite obj : favs) {
					this.favoriteService.delete(obj.getId());
				}
				
				List<GoodsConfig> goodsConfigList = store.getGoodsConfigList();
				if(goodsConfigList != null){
					goodsConfigList.clear();
				}
				
				this.storeService.delete(CommUtil.null2Long(id));
				this.send_site_msg(request, "msg_toseller_store_delete_notify", store);
			}
		}
		return "redirect:store_list.htm";
	}

	@SecurityMapping(title = "店铺AJAX更新", value = "/admin/store_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_ajax.htm")
	public void store_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Store obj = this.storeService.getObjById(Long.parseLong(id));
		Field[] fields = Store.class.getDeclaredFields();
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
		if ("store_recommend".equals(fieldName)) {
			if (obj.isStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else {
				obj.setStore_recommend_time(null);
			}
		}
		this.storeService.update(obj);
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

	@SecurityMapping(title = "入驻管理", value = "/admin/store_base.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_base.htm")
	public ModelAndView store_base(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "卖家信用保存", value = "/admin/store_set_save.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_set_save.htm")
	public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url,
			String store_allow) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		sc.setStore_allow(CommUtil.null2Boolean(store_allow));
		if ("".equals(id)) {
			this.configService.save(sc);
		} else
			this.configService.update(sc);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺设置成功");
		return mv;
	}

	@SecurityMapping(title = "开店申请Ajax更新", value = "/admin/store_base_ajax.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_base_ajax.htm")
	public void integral_goods_ajax(HttpServletRequest request, HttpServletResponse response, String fieldName)
			throws ClassNotFoundException {
		SysConfig sc = this.configService.getSysConfig();
		Field[] fields = SysConfig.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(sc);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.Boolean");
				val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.configService.update(sc);
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

	@SecurityMapping(title = "等级限制时可选的类目", value = "/admin/sg_limit_gc.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/sg_limit_gc.htm")
	public void storeGrade_limit_goodsClass(HttpServletRequest request, HttpServletResponse response, String storeGrade_id,
			String goodsClass_id) {
		String jsonList = "";
		StoreGrade storeGrade = this.storeGradeService.getObjById(CommUtil.null2Long(storeGrade_id));
		if (storeGrade != null && storeGrade.getMain_limit() != 0) {
			GoodsClass goodsClass = this.goodsclassService.getObjById(CommUtil.null2Long(goodsClass_id));
			if (goodsClass != null) {
				List<Map<String, String>> gcList = new ArrayList<Map<String, String>>();
				for (GoodsClass gc : goodsClass.getChilds()) {
					Map map = new HashMap();
					map.put("gc_id", gc.getId());
					map.put("gc_name", gc.getClassName());
					gcList.add(map);
				}
				jsonList = Json.toJson(gcList, JsonFormat.compact());
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	@SecurityMapping(title = "新增详细经营类目", value = "/admin/add_gc_detail.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/add_gc_detail.htm")
	public ModelAndView addStore_GoodsClass_detail(HttpServletRequest request, HttpServletResponse response, String did,
			String gc_detail_info) {
		ModelAndView mv = new JModelAndView("admin/blue/store_detailgc_ajax.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);		
		if(!StringUtils.isNullOrEmpty(did)) {			
			String[] gIds = did.split(",");
			
			List<Map> gcDetailsList = null;
			if (StringUtils.isNullOrEmpty(gc_detail_info)) {
				gcDetailsList = new ArrayList<Map>();
			} else {
				gcDetailsList = (List<Map>)Json.fromJson(List.class, gc_detail_info);
			}
			
			for(int i = 0; i < gIds.length; i++) {
				Long gId = CommUtil.null2Long(gIds[i]);
				if(gId != -1L) {
					GoodsClass gc = this.goodsclassService.getObjById(gId);
					if(gc != null && gc.getLevel() == 1) {
						GoodsClass parent = gc.getParent();
						Map map = null;
						for(Map m: gcDetailsList) {
							if(CommUtil.null2Long(m.get("m_id")).equals(parent.getId())) {
								map = m;
								break;
							}
						}
						Integer id = new Integer(gId.intValue());
						if(map != null) {
							List gc_list = (List)map.get("gc_list");
							if(gc_list.indexOf(id) < 0) {
								gc_list.add(id);
							}
						} else {
							Map mapNew = new HashMap();
							mapNew.put("m_id", parent.getId());
							List<Integer> gc_list = new ArrayList<Integer>();
							gc_list.add(id);
							mapNew.put("gc_list", gc_list);
							gcDetailsList.add(mapNew);
						}
					}
				}
			}
			
			String listJson = Json.toJson(gcDetailsList, JsonFormat.compact());
			mv.addObject("gc_detail_info", listJson);
			mv.addObject("gcs", storeTools.query_store_DetailGc(listJson));
		}

		return mv;
	}

	@SecurityMapping(title = "删除详细经营类目", value = "/admin/del_gc_detail.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/del_gc_detail.htm")
	public ModelAndView delStore_GoodsClass_detail(HttpServletRequest request, HttpServletResponse response, String did,
			String gc_detail_info) {
		ModelAndView mv = new JModelAndView("admin/blue/store_detailgc_ajax.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClass gc = this.goodsclassService.getObjById(CommUtil.null2Long(did));
		if (!StringUtils.isNullOrEmpty(gc_detail_info) && gc != null) {
			GoodsClass parent = gc.getParent();
			List<Map> oldList = Json.fromJson(ArrayList.class, gc_detail_info);
			List<Map> list = new ArrayList<Map>();
			for (Map oldMap : oldList) {
				if (!CommUtil.null2Long(oldMap.get("m_id")).equals(parent.getId())) {
					list.add(oldMap);
				} else {
					List<Integer> gc_list = (List<Integer>) oldMap.get("gc_list");
					for (Integer integer : gc_list) {
						if (integer.equals(CommUtil.null2Int(did))) {
							gc_list.remove(integer);
							break;
						}
					}
					if (gc_list.size() > 0) {
						Map map = new HashMap();
						map.put("gc_list", gc_list);
						map.put("m_id", parent.getId());
						list.add(oldMap);
					}
				}
			}
			if (list.size() > 0) {
				String listJson = Json.toJson(list, JsonFormat.compact());
				mv.addObject("gc_detail_info", listJson);
				mv.addObject("gcs", storeTools.query_store_DetailGc(listJson));
			}
		}
		return mv;
	}
	
	@SecurityMapping(title = "商品分类下级加载", value = "/admin/store_classify_list.htm*", rtype = "admin", rname = "分类管理", rcode = "store_classify_list", rgroup = "店铺")
	@RequestMapping("/admin/store_classify_list.htm")
	public ModelAndView store_classify_list(HttpServletRequest request, HttpServletResponse response, String storeId, String classify1, String classify2, String classify3, String commission) {
		ModelAndView mv = new JModelAndView("admin/blue/store_classify_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> goodsClassList = new ArrayList<GoodsClass>();
		List<GoodsClass> goodsClassList1 = new ArrayList<GoodsClass>();
		List<GoodsClass> goodsClassList2 = new ArrayList<GoodsClass>();
		List<GoodsClass> goodsClassList3 = new ArrayList<GoodsClass>();
		Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
		List<Map> list = new ArrayList<Map>();// 用于转换成店铺中的详细经营类目json
		if(!StringUtils.isNullOrEmpty(store.getGc_commission_info())){
			list = Json.fromJson(ArrayList.class, store.getGc_commission_info());
		}
		Map<String, String> map = new HashMap<String, String>();
		if(list != null){
			for(Map m : list){
				map.put(CommUtil.null2String(m.get("id")), CommUtil.null2String(m.get("commission")));
			}
		}
		GoodsClass gc = null;
		Map params = new HashMap();
		if(!StringUtils.isNullOrEmpty(classify1)){
			gc = this.goodsclassService.getObjById(CommUtil.null2Long(classify1));
			if(map.containsKey(classify1)){
				gc.setValue(map.get(classify1));
			}
			goodsClassList1.add(gc);
			if(!StringUtils.isNullOrEmpty(classify2)){
				gc = this.goodsclassService.getObjById(CommUtil.null2Long(classify2));
				if(map.containsKey(classify2)){
					gc.setValue(map.get(classify2));
				}
				goodsClassList2.add(gc);
				if(!StringUtils.isNullOrEmpty(classify3)){
					gc = this.goodsclassService.getObjById(CommUtil.null2Long(classify3));
					if(map.containsKey(classify3)){
						gc.setValue(map.get(classify3));
					}
					goodsClassList3.add(gc);
				}else{
					params.clear();
					params.put("pid", CommUtil.null2Long(classify2));
					goodsClassList = this.goodsClassService.query(
							"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
					for(GoodsClass gooc : goodsClassList){
						if(map.containsKey(CommUtil.null2String(gooc.getId()))){
							gooc.setValue(map.get(CommUtil.null2String(gooc.getId())));
						}
						goodsClassList3.add(gooc);
					}
				}
			}else{
				//判断店铺经营类是否设置二三级分类
				if(store != null && !StringUtils.isNullOrEmpty(store.getGc_detail_info())){
					String json = store.getGc_detail_info();
					if (!StringUtils.isNullOrEmpty(json)) {
						List<Map> all_list = Json.fromJson(ArrayList.class, json);
						for (Map ma : all_list) {
							int ls = (int) ma.get("m_id");
							if(!StringUtils.isNullOrEmpty(ls) && CommUtil.null2Int(classify1) == ls){
								List<Integer> ts = (List) ma.get("gc_list");
								if(!StringUtils.isNullOrEmpty(ts)){
									for(Integer i : ts){
										gc = this.goodsClassService.getObjById(CommUtil.null2Long(i));
										if(gc != null) {
											if(map.containsKey(CommUtil.null2String(gc.getId()))){
												gc.setValue(map.get(CommUtil.null2String(gc.getId())));
											}
											goodsClassList2.add(gc);
										}
									}
								}
							}
						}
						//三级分类
						for(GoodsClass goodsClass:goodsClassList2){
							params.clear();
							params.put("pid", goodsClass.getId());
							goodsClassList = this.goodsClassService.query(
									"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
							for(GoodsClass go:goodsClassList){
								if(map.containsKey(CommUtil.null2String(CommUtil.null2String(go.getId())))){
									go.setValue(map.get(CommUtil.null2String(go.getId())));
								}
								goodsClassList3.add(go);
							}
						}
					}
				}else{
					params.clear();
					params.put("pid", CommUtil.null2Long(classify1));
					goodsClassList = this.goodsClassService.query(
							"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
					for(GoodsClass gooc : goodsClassList){
						if(map.containsKey(CommUtil.null2String(gooc.getId()))){
							gooc.setValue(map.get(CommUtil.null2String(gooc.getId())));
						}
						goodsClassList2.add(gooc);
					}
					for(GoodsClass goodsClass:goodsClassList2){
						params.clear();
						params.put("pid", goodsClass.getId());
						goodsClassList = this.goodsClassService.query(
								"select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
						for(GoodsClass go:goodsClassList){
							if(map.containsKey(CommUtil.null2String(gc.getId()))){
								go.setValue(map.get(CommUtil.null2String(go.getId())));
							}
							goodsClassList3.add(go);
						}
					}
				}
			}
		}
		mv.addObject("goodsClassList1", goodsClassList1);
		mv.addObject("goodsClassList2", goodsClassList2);
		mv.addObject("goodsClassList3", goodsClassList3);
		return mv;
	}

	
	@SecurityMapping(title = "店铺保存", value = "/admin/seller_account_check.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/seller_account_check.htm")
	public void seller_account_check(HttpServletRequest request, HttpServletResponse response, String account, Long id) {
		String accountValid = CommUtil.clearContent(account);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(this.userTools.getUser("SELLER", accountValid, id) == null? true: false);
		} catch (IOException e) {
			logger.error("判断商家登录账号是否存在时出现错误", e);
		}
	}
}