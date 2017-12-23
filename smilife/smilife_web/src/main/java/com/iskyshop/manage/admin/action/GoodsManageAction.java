package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: GoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品管理类
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
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class GoodsManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
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
	private ITemplateService templateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IZTCGoldLogService ztcglService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsViewTools goodsViewTools;

	/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品列表", value = "/admin/goods_list.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String store_name, String brand_id, String gc_id, String goods_type,
			String goods_name, String store_recommend, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (!StringUtils.isNullOrEmpty(store_name)) {
			qo.addQuery("obj.goods_store.store_name", new SysMap("store_name", "%" + CommUtil.null2String(store_name) + "%"),
					"like");
			mv.addObject("store_name", store_name);
		}
		if (!StringUtils.isNullOrEmpty(brand_id)) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
		}
		if (!StringUtils.isNullOrEmpty(gc_id)) {
			//begin dengyuqi 2015-9-8 解决通过分类搜索不了目标商品的bug
			String scope = "(obj.gc.parent.id=:goods_gc_parent_id1 or obj.gc.parent.parent.id=:goods_gc_parent_id2)";
			Map<String,Long> params = new HashMap<String,Long>();
			params.put("goods_gc_parent_id1", CommUtil.null2Long(gc_id));
			params.put("goods_gc_parent_id2", CommUtil.null2Long(gc_id));
			qo.addQuery(scope, params);
			//end
			
			mv.addObject("gc_id", gc_id);
		}
		if (!StringUtils.isNullOrEmpty(goods_type)) {
			qo.addQuery("obj.goods_type", new SysMap("goods_goods_type", CommUtil.null2Int(goods_type)), "=");
			mv.addObject("goods_type", goods_type);
		}
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%" + goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (!StringUtils.isNullOrEmpty(store_recommend)) {
			qo.addQuery("obj.store_recommend", new SysMap("goods_store_recommend", CommUtil.null2Boolean(store_recommend)),
					"=");
			mv.addObject("store_recommend", store_recommend);
		}
		if (!StringUtils.isNullOrEmpty(status)) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", CommUtil.null2Int(status)), "=");
			mv.addObject("status", status);
		} else {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select new GoodsClass(id,className) from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	@SecurityMapping(title = "违规商品列表", value = "/admin/goods_outline.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_outline.htm")
	public ModelAndView goods_outline(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String goods_name, String gb_id, String gc_id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(goods_name)) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%" + goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (!StringUtils.isNullOrEmpty(gb_id)) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", CommUtil.null2Long(gb_id)), "=");
			mv.addObject("gb_id", gb_id);
		}
		if (!StringUtils.isNullOrEmpty(gc_id)) {
			qo.addQuery("obj.gc.id", new SysMap("goods_class_id", CommUtil.null2Long(gc_id)), "=");
			mv.addObject("gc_id", gc_id);
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/goods_list.htm", "", params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.level=1 order by obj.sequence asc", null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	@SecurityMapping(title = "商品删除", value = "/admin/goods_del.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_del.htm")
	public String goods_del(HttpServletRequest request, String mulitId) throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!StringUtils.isNullOrEmpty(id)) {
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
				String goods_name = goods.getGoods_name();
				User seller = null;
				if (goods.getGoods_store() != null) {
					Long seller_id = goods.getGoods_store().getUser().getId();
					seller = this.userService.getObjById(seller_id);
				}
				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				Map params = new HashMap();
				params.put("gid", goods.getId());// 直通车商品记录
				List<ZTCGoldLog> ztcgls = this.ztcglService
						.query("select obj from ZTCGoldLog obj where obj.zgl_goods_id=:gid", params, -1, -1);
				for (ZTCGoldLog ztc : ztcgls) {
					this.ztcglService.delete(ztc.getId());
				}
				
				//删除所有购物车中对应当前要删除的商品
				params.clear(); 
				params.put("goodsId", goods.getId());
				List<GoodsCart> carts = this.goodsCartService.query("select obj from GoodsCart obj where obj.goods.id=:goodsId", params, -1, -1);
				if(carts != null && carts.size() > 0) {
					List<String> cart_ids = new ArrayList<String>();
					for(GoodsCart gc: carts) {
						cart_ids.add(gc.getId().toString());
					}
					this.goodsCartService.remove_carts(cart_ids);
				}
				
				// 删除相应商品收藏
				params.clear();
				params.put("gid", goods.getId());
				List<Favorite> favs = this.favoriteService.query("select obj from Favorite obj where obj.goods_id=:gid",
						params, -1, -1);
				for (Favorite obj : favs) {
					this.favoriteService.delete(obj.getId());
				}
				goods.setGoods_main_photo(null);
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
				// 发送站内短信提醒卖家
				// 发送站内短信提醒卖家
				if (goods.getGoods_type() == 1) {
					try {
						this.send_site_msg(request, "sms_toseller_goods_delete_by_admin_notify", seller, goods_name, "商品存在违规，系统已删除");
					} catch (Exception e1) {
						logger.error("发送短信失败。",e1);
					}
				}
			}
		}
		return "redirect:goods_list.htm";
	}

	private void send_site_msg(HttpServletRequest request, String mark, User seller, String goods_name, String reason)
			throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		if (template.isOpen() && seller != null) {
			if (seller.getMobile() != null) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				context.setVariable("reason", reason);
				context.setVariable("user", seller);
				context.setVariable("goods_name", goods_name);
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("webPath", CommUtil.getURL(request));
				Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
				String content = ex.getValue(context, String.class);
				try {
					this.msgTools.sendSMS(seller.getMobile(), content);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_ajax.htm")
	public void goods_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
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
		} else {
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setConfig(this.configService.getSysConfig());
			lucene.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
		}
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

	@SecurityMapping(title = "商品审核", value = "/admin/goods_audit.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_audit.htm")
	public String goods_audit(HttpServletRequest request, HttpServletResponse response, String mulitId, String status)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
				obj.setGoods_status(obj.getPublish_goods_status()); // 设置商品发布审核后状态
				goodsService.update(obj);
			}
		}
		return "redirect:goods_list.htm?status=" + status;
	}

	@SecurityMapping(title = "商品二维码生成", value = "/admin/goods_qr.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_qr.htm")
	public String goods_qr(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String destPath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
						+ "cache";
				if (!CommUtil.fileExist(destPath)) {
					CommUtil.createFolder(destPath);
				}
				destPath = destPath + File.separator + obj.getId() + "_qr.jpg";
				
				String logoPath = "";
				if (obj.getGoods_main_photo() != null) {
					if(1 == obj.getGoods_type()){ 
						this.ftpTools.userDownloadImg(obj.getGoods_main_photo(), String.valueOf(obj.getGoods_store().getUser().getId()));
					} else {
						this.ftpTools.systemDownloadImg(obj.getGoods_main_photo());
					}
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
				String url = this.ftpTools.systemUpload(obj.getId() + "_qr.jpg", "/goods_qr");
				obj.setQr_img_path(url + "/" + obj.getId() + "_qr.jpg");
				this.goodsService.update(obj);
				// 删除主图
				this.ftpTools.DeleteWebImg(obj.getGoods_main_photo());
			}
		}
		return "redirect:goods_list.htm?currentPage=" + currentPage;
	}
	
	@SecurityMapping(title = "商品二维码生成", value = "/admin/goods_qr.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/ajax_goods_qr.htm")
	@ResponseBody
	public void ajaxUpdateAllGoodsQRTask(HttpServletRequest request,HttpServletResponse response)
	{
		try{
			SysConfig sysconfig=configService.getSysConfig();
			int result=sysconfig.getUpdate_all_qr_status();
			if(result==1){
				response.getWriter().write("-1");
				return;
			}
			
			updateAllGoodsQRtask(null, CommUtil.getServerRealPathFromRequest(request));
			
			response.getWriter().write("1");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return;
	}
	
	/**
	 * 刷新所有的二维码
	 * @param store_id
	 * @param serverRealPath
	 * @return
	 */
	public void updateAllGoodsQRtask(final Integer store_id,final String serverRealPath)
	{
		Thread thread=new Thread(new Runnable(){

			@Override
			public void run() {
				//检查更新任务是否已经启动了
				SysConfig sysconfig=configService.getSysConfig();
				int result=sysconfig.getUpdate_all_qr_status();
				if(result==1){
					return; 
				}
				
				sysconfig.setUpdate_all_qr_status(0);//设定为1 表示已经锁定这次运行
				configService.update(sysconfig);
				
				String tmpStr="";
				if(store_id!=null && store_id>0){
					tmpStr=" and goods_store_id="+store_id;
				}
				
				final String filterSql=tmpStr;
				//删除所有商品的二维码图片
				boolean cleanAllQR=goodsService.executeSql("update iskyshop_goods set qr_img_path='' where 1 "+filterSql);
				Date startTime=new Date();
				if(!cleanAllQR)
					return;
			
				logger.info("update all goods QR step1. clean store id:  "+store_id+ "success!  (store_id is null:all goods)");
				//打开线程
				logger.info("update all goods QR step2. open thread  start task...");
				try{
					while(true){		
						//检查是否有剩余需要更新的
						Object tmp=goodsService.query("select count(obj.id) from Goods obj where qr_img_path='' "+filterSql, null, 0, 1);
						long count=(long)((List)tmp).get(0);
						if(count<=0)
							break;
						
						//一次性更新100个产品
						List<Goods> list=goodsService.query("select obj from Goods obj where qr_img_path=''"+filterSql, null, 0, 100);
						if(list==null || list.size()<=0)
							break;
						
						for(Goods goods:list){
							if(!goodsService.createGoodsQR(goods.getId(),serverRealPath));
								logger.info("update goods id "+goods.getId()+" fail!  and update qr_img_path as 1.");
						}
					}	
					
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					long elapsed_time=((new Date()).getTime()-startTime.getTime())/1000;
					sysconfig.setUpdate_all_qr_status(0);//重置运行状态
					configService.update(sysconfig);
					System.gc();//清理遗留下来的垃圾对象
					logger.info("update all goods QR complete elapsed time "+elapsed_time+ " s");
				}
			}
			
		});
		
		thread.start();
	}
	
}