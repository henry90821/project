package com.iskyshop.manage.admin.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.domain.query.CouponQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: CouponManageAction.java
 * </p>
 * 
 * <p>
 * Description: 优惠券控制管理器，管理商城系统优惠券信息 优惠券使用方法： 1、管理员添加优惠券，包括优惠券面额、使用条件（订单满多少可以可以用），优惠券数量、使用时间区间 2、优惠券只有平台管理员赠送用户才能获取
 * 3、用户购物订单金额满足优惠券使用条件后可以使用优惠券
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
 * @date 2014年5月27日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CouponManageAction {
	private static Logger logger = Logger.getLogger(CouponManageAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponinfoService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
//	@Autowired
//	private IQueryService queryService;
	@Autowired
	private FTPServerTools FTPTools;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;

	/**
	 * 优惠券列表信息页面，分页显示优惠券列表信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return 优惠券列表信息页
	 */
	@SecurityMapping(title = "优惠券列表", value = "/admin/coupon_list.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_list.htm")
	public ModelAndView coupon_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String coupon_name, String coupon_begin_time, String coupon_end_time) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.coupon_type", new SysMap("coupon_type", 0), "=");
		if (!"".equals(CommUtil.null2String(coupon_name))) {
			qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%" + coupon_name + "%"), "like");
			mv.addObject("coupon_name", coupon_name);
		}
		if (!"".equals(CommUtil.null2String(coupon_begin_time))) {
			qo.addQuery("obj.coupon_begin_time", new SysMap("coupon_begin_time", CommUtil.formatDate(coupon_begin_time)),
					">=");
			mv.addObject("coupon_begin_time", coupon_begin_time);
		}
		if (!"".equals(CommUtil.null2String(coupon_end_time))) {
			qo.addQuery("obj.coupon_end_time", new SysMap("coupon_end_time", CommUtil.formatDate(coupon_end_time)), "<=");
			mv.addObject("coupon_end_time", coupon_end_time);
		}
		IPageList pList = this.couponService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/coupon_list.htm", "", "", pList, mv);
		
		String ids = "";
		for(Coupon c : (List<Coupon>)pList.getResult()){
			ids += c.getId() + ",";
		}
		if(!"".equals(ids)){
			ids = ids.substring(0,ids.length()-1);
			Map<Long,BigInteger> couponInfoSize = new HashMap<Long, BigInteger>();
			List<Object[]> list = couponinfoService.executeNativeQuery("select coupon_id,count(id) from iskyshop_coupon_info where coupon_id in("+ids+") GROUP BY coupon_id", null, -1, -1);
			for(Object[] os : list){
				couponInfoSize.put(Long.parseLong(os[0].toString()), (BigInteger)os[1]);
			}
			mv.addObject("couponInfoSize",couponInfoSize);
		}
		//全场优惠券标志
		mv.addObject("global_coupon_flag",request.getParameter("global_coupon_flag"));
		return mv;
	}

	/**
	 * 添加优惠券信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券添加", value = "/admin/coupon_add.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_add.htm")
	public ModelAndView coupon_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		//全场优惠券标志
		String global_coupon_flag=request.getParameter("global_coupon_flag");
		if("1".equals(global_coupon_flag)){
			List<GoodsBrand> gbs = this.goodsBrandService
					.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
			List<GoodsClass> gcs = this.goodsClassService.query(
					"select new GoodsClass(id,className) from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
					null, -1, -1);
			mv.addObject("gcs", gcs);
			mv.addObject("gbs", gbs);
		}
		mv.addObject("global_coupon_flag",global_coupon_flag);
		return mv;
	}

	/**
	 * 优惠券保存，保存或者更新一个优惠券信息
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存", value = "/admin/coupon_save.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_save.htm")
	public String coupon_save(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		WebForm wf = new WebForm();
		Coupon coupon = wf.toPo(request, Coupon.class);
		
		coupon.setAssociated_ids(CommUtil.arrayToString(request.getParameterValues("associated_ids"), ','));
		//全局优惠券栏中选择自营类型，取消关联的品牌品类
		if(coupon.getCoupon_type()==0){
			coupon.setAssociated_type(0);
			coupon.setAssociated_ids(null);
		}
		coupon.setAddTime(new Date());
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(request, "coupon_img", saveFilePathName, null, null);
			String currencyCode=CommUtil.null2String(this.configService.getSysConfig().getCurrency_code());
			if (map.get("fileName") != "") {
				
				String pressImg = saveFilePathName + File.separator + CommUtil.null2String(map.get("fileName"));
				String targetImg = saveFilePathName + File.separator + CommUtil.null2String(map.get("fileName"));
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(pressImg, targetImg,
							currencyCode + coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
					font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(targetImg, targetImg,
							"满 " + coupon.getCoupon_order_amount() + " 减" + coupon.getCoupon_amount(), "#726960", font, 95,
							90, 1);
				} catch (Exception e) {
					logger.error(e);
				}
				
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(CommUtil.null2String(map.get("fileName")));
				coupon_acc.setExt((String) map.get("mime"));
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
				coupon_acc.setPath(this.FTPTools.systemUpload(coupon_acc.getName(), "/coupon"));
				coupon_acc.setWidth(CommUtil.null2Int(map.get("width")));
				coupon_acc.setHeight(CommUtil.null2Int(map.get("height")));
				coupon_acc.setAddTime(new Date());
				this.accessoryService.save(coupon_acc);
				
				coupon.setCoupon_acc(coupon_acc);
			} else {
				String pressImg = CommUtil.getServerRealPathFromRequest(request) + "resources"
						+ File.separator + "style" + File.separator + "common" + File.separator + "template" + File.separator
						+ "coupon_template.jpg";
				
				/***
				String targetImgPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "coupon" + File.separator;
				*/
				
				String targetImgPath = saveFilePathName + File.separator;
				
				if (!CommUtil.fileExist(targetImgPath)) {
					CommUtil.createFolder(targetImgPath);
				}
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				try {
					Font font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(pressImg, targetImgPath + targetImgName,
							currencyCode + coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
					font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(targetImgPath + targetImgName, targetImgPath + targetImgName,
							"满 " + coupon.getCoupon_order_amount() + " 减" + coupon.getCoupon_amount(), "#726960", font, 95,
							90, 1);
				} catch (Exception e) {
					logger.error(e);
				}
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(targetImgName);
				coupon_acc.setExt("jpg");
				coupon_acc.setPath(this.FTPTools.systemUpload(coupon_acc.getName(), "/coupon"));
				coupon_acc.setAddTime(new Date());
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(28.4)));
				this.accessoryService.save(coupon_acc);
				coupon.setCoupon_acc(coupon_acc);
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
		//全场优惠券返回链接
		String appendString="1".equals(request.getParameter("global_coupon_flag")) ? "&global_coupon_flag=1" : ""; 
		
		this.couponService.save(coupon);
		return "redirect:coupon_success.htm?currentPage=" + currentPage+appendString;
	}

	@SecurityMapping(title = "优惠券保存成功", value = "/admin/coupon_success.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_success.htm")
	public ModelAndView coupon_success(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		//全场优惠券返回链接
		String appendString="1".equals(request.getParameter("global_coupon_flag")) ? "?global_coupon_flag=1" : ""; 
		
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/coupon_list.htm"+appendString);
		mv.addObject("op_title", "优惠券保存成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/coupon_add.htm" +appendString+"&currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "优惠券发放", value = "/admin/coupon_send.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_send.htm")
	public ModelAndView coupon_send(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_send.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<StoreGrade> grades = this.storeGradeService.query("select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		mv.addObject("currentPage", currentPage);
		//全场优惠券标志
		mv.addObject("global_coupon_flag",request.getParameter("global_coupon_flag"));
		mv.addObject("obj", this.couponService.getObjById(CommUtil.null2Long(id)));
		return mv;
	}

	@SecurityMapping(title = "优惠券发放保存", value = "/admin/coupon_send_save.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_send_save.htm")
	public ModelAndView coupon_send_save(HttpServletRequest request, HttpServletResponse response, String id, String type,
			String users, String grades, String order_amount) throws IOException {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<User> user_list = new ArrayList<User>();
		if ("all_user".equals(type)) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			user_list = this.userService.query(
					"select new User(id) from User obj where obj.userRole!=:userRole order by obj.user_goods_fee desc", params, -1,
					-1);
		}else if ("the_user".equals(type)) {
			List<String> user_names = CommUtil.str2list(users);
			for (String user_name : user_names) {
				User user = this.userService.getObjByProperty(null, "userName", user_name);
				user_list.add(user);
			}
		}else if ("all_store".equals(type)) {
			user_list = this.userService.query(
					"select new User(id) from User obj where obj.store.id is not null order by obj.addTime desc", null, -1, -1);
		}else if ("the_store".equals(type)) {
			Map params = new HashMap();
			Set<Long> store_ids = new TreeSet<Long>();
			for (String grade : grades.split(",")) {
				store_ids.add(Long.parseLong(grade));
			}
			params.put("store_ids", store_ids);
			user_list = this.userService.query("select new User(id) from User obj where obj.grade.id in(:store_ids)", params, -1, -1);
		}else if ("the_order".equals(type)) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			params.put("fee", BigDecimal.valueOf(CommUtil.null2Double(order_amount)));
			user_list = this.userService.query(
					"select new User(id,user_goods_fee) from User obj where obj.userRole!=:userRole and obj.user_goods_fee >:fee " +
					"order by obj.user_goods_fee desc", params, -1, -1);			
		}
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		int number = user_list.size();//待发放张数
		if(coupon.getCoupon_count() != 0){//此优惠券有发放张数限制
			number = coupon.getCoupon_count() - coupon.getCouponinfos().size();//此优惠券剩余发放张数
		}
		List<CouponInfo> couponInfos = new ArrayList<>();
		Date addTime = new Date();
		CouponInfo info = null;
		for (int i = 0; i < number && i < user_list.size(); i++) {			
			info = new CouponInfo();
			info.setAddTime(addTime);
			info.setCoupon(coupon);
			info.setCoupon_sn(UUID.randomUUID().toString());
			info.setUser(user_list.get(i));
			couponInfos.add(info);
		}
		this.couponinfoService.batchInsert(couponInfos);
		
		//全场优惠券返回链接
		String appendString="1".equals(request.getParameter("global_coupon_flag")) ? "?global_coupon_flag=1" : ""; 
				
		mv.addObject("op_title", "优惠券发放成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/coupon_list.htm"+appendString);
		return mv;
	}

	@SecurityMapping(title = "优惠券AJAX更新", value = "/admin/coupon_ajax.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_ajax.htm")
	public void coupon_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Coupon obj = this.couponService.getObjById(Long.parseLong(id));
		Field[] fields = Coupon.class.getDeclaredFields();
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
		this.couponService.update(obj);
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

	@SecurityMapping(title = "优惠券详细信息", value = "/admin/coupon_info_list.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_info_list.htm")
	public ModelAndView coupon_info_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String coupon_id) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_info_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.coupon.id", new SysMap("coupon_id", CommUtil.null2Long(coupon_id)), "=");
		IPageList pList = this.couponinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
		mv.addObject("coupon_id", coupon_id);
		//全场优惠券标志
		mv.addObject("global_coupon_flag",request.getParameter("global_coupon_flag"));
		return mv;
	}

	private static boolean waterMarkWithText(String filePath, String outPath, String text, String markContentColor,
			Font font, int left, int top, float qualNum) {
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
		g.drawString(text, left, top); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outPath);
			ImageIO.write(bimage, filePath.substring(filePath.lastIndexOf(".") + 1), out);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}finally{
			theImg.flush();
			try {
				out.close();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return true;
	}
	
	@SecurityMapping(title = "自营活动优惠券加载", value = "/admin/activity_self_coupon_load.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_coupon_load.htm")
	public ModelAndView activity_self_coupon_load(HttpServletRequest request, HttpServletResponse response, String coupon_name,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self_coupon_load.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
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
		
		if("1".equals(request.getParameter("global_coupon_flag"))){
			//全场优惠券模式
			sql.append(" AND obj.coupon_type =:coupon_type ");
			params.put("coupon_type", 2);
		}
		else{
			sql.append(" AND obj.coupon_type =:coupon_type ");
			params.put("coupon_type", 0);
		}

		//全场优惠券参数
		String global_coupon_flag=request.getParameter("global_coupon_flag");
		String urlParam="";
		if("1".equals(global_coupon_flag))
			urlParam="&global_coupon_flag=1";
		
		cqo.addQuery(sql.toString(), params);
		String url = CommUtil.getURL(request) + "/admin/activity_self_coupon_load.htm";
		IPageList pList = this.couponService.list(cqo);
		CommUtil.saveIPageList2ModelAndView(url, "", urlParam, pList, mv);
		return mv;
	}
	
	/**
	 * 全场优惠券管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param coupon_name
	 * @param coupon_begin_time
	 * @param coupon_end_time
	 * @return
	 */
	@SecurityMapping(title = "全场优惠券列表", value = "/admin/global_coupon_list.htm*", rtype = "admin", rname = "全场优惠券管理", rcode = "global_coupon_admin", rgroup = "运营")
	@RequestMapping("/admin/global_coupon_list.htm")
	public ModelAndView global_coupon_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String coupon_name, String coupon_begin_time, String coupon_end_time) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.coupon_type", new SysMap("coupon_type", 2), "=");
		if (!"".equals(CommUtil.null2String(coupon_name))) {
			qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%" + coupon_name + "%"), "like");
			mv.addObject("coupon_name", coupon_name);
		}
		if (!"".equals(CommUtil.null2String(coupon_begin_time))) {
			qo.addQuery("obj.coupon_begin_time", new SysMap("coupon_begin_time", CommUtil.formatDate(coupon_begin_time)),
					">=");
			mv.addObject("coupon_begin_time", coupon_begin_time);
		}
		if (!"".equals(CommUtil.null2String(coupon_end_time))) {
			qo.addQuery("obj.coupon_end_time", new SysMap("coupon_end_time", CommUtil.formatDate(coupon_end_time)), "<=");
			mv.addObject("coupon_end_time", coupon_end_time);
		}
		IPageList pList = this.couponService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/coupon_list.htm", "", "", pList, mv);
		
		String ids = "";
		Map<Long,String> associatedNames=new HashMap<Long,String>();
		for(Coupon c : (List<Coupon>)pList.getResult()){
			ids += c.getId() + ",";
			
			//读取每个优惠券所关联的品类 品牌名称
			if(c.getAssociated_ids()!=null && c.getAssociated_ids().length()>0){
				if(c.getAssociated_type()==1)
					associatedNames.put(c.getId(), goodsClassService.getClassNamesStr(c.getAssociated_ids()));
				else if(c.getAssociated_type()==2)
					associatedNames.put(c.getId(), goodsBrandService.getBrandNamesStr(c.getAssociated_ids()));
			}
		}
		mv.addObject("associatedNames",associatedNames);
		
		if(!"".equals(ids)){
			ids = ids.substring(0,ids.length()-1);
			Map<Long,BigInteger> couponInfoSize = new HashMap<Long, BigInteger>();
			List<Object[]> list = couponinfoService.executeNativeQuery("select coupon_id,count(id) from iskyshop_coupon_info where coupon_id in("+ids+") GROUP BY coupon_id", null, -1, -1);
			for(Object[] os : list){
				couponInfoSize.put(Long.parseLong(os[0].toString()), (BigInteger)os[1]);
			}
			mv.addObject("couponInfoSize",couponInfoSize);
		}
		
		mv.addObject("global_coupon_flag","1");
		return mv;
	}
	
	
}