package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.query.SeckillGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: SelfSeckillManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台自营秒杀销售管理控制器
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
 * @author dengyuqi
 * 
 * @date 2015-10-10
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfSeckillManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FTPServerTools ftpTools;

	/**
	 * 商品类秒杀商品列表 
	 * @author jiangchi
	 * 
	 */
	@SecurityMapping(title = "商品类秒杀商品列表", value = "/admin/self_seckill.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill.htm")
	public ModelAndView self_seckill_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String gg_name,Integer gg_recommend,Integer gg_status) {
		ModelAndView mv = new JModelAndView("admin/blue/self_seckill.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SeckillGoodsQueryObject qo = new SeckillGoodsQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.gg_goods.goods_type", new SysMap("gg_goods_goods_type", 0), "=");// 平台自营
		StringBuffer sb=new StringBuffer();
		if (!"".equals(CommUtil.null2String(gg_name))) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name + "%"), "like");
			sb.append("&gg_name=").append(gg_name);
			mv.addObject("gg_name", gg_name);
		}
		if(gg_recommend!=null && (gg_recommend==1 || gg_recommend==0)){
			qo.addQuery("obj.weixin_shop_recommend",new SysMap("weixin_shop_recommend",gg_recommend==1 ? true : false),"=");
			mv.addObject("gg_recommend",gg_recommend);
			sb.append("&gg_recommend=").append(gg_recommend);
		}
		if(gg_status!=null && gg_status!=-1){
			qo.addQuery("obj.gg_status", new SysMap("gg_status",gg_status),"=");
			mv.addObject("gg_status",gg_status);
			sb.append("&gg_status=").append(gg_status);
		}
		IPageList pList = seckillGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", sb.toString(), pList, mv);
		return mv;
	}
	
	/**
	 * 秒杀管理新增
	 * @author jiangchi
	 * 
	 */
	@SecurityMapping(title = "自营商品类秒杀商品添加", value = "/admin/self_seckill_add.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill_add.htm")
	public ModelAndView self_seckill_add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/self_seckill_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param gg_price
	 * @return
	 */
	@SecurityMapping(title = "商品类秒杀商品保存", value = "/admin/self_seckill_save.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill_save.htm")
	public ModelAndView self_seckill_save(HttpServletRequest request, HttpServletResponse response, String id,String store_inventory_info,
			String goods_id, String gg_price) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		SeckillGoods gg = null;  //秒杀商品
		int oldCount = 0;  //秒杀商品编辑前库存
		Goods beforeGoods = null; //秒杀商品编辑前选择的商品
		
		WebForm wf = new WebForm();
		if (StringUtils.isNullOrEmpty(id)) {
			gg = wf.toPo(request, SeckillGoods.class);
			gg.setAddTime(new Date());
		} else {
			SeckillGoods obj = seckillGoodsService.getObjById(CommUtil.null2Long(id));
			oldCount = obj.getGg_count();
			beforeGoods = obj.getGg_goods();
			gg = (SeckillGoods) wf.toPo(request, obj);
		}
		
		//更新商品库存、标识商品为待秒杀商品
		Goods goods = goodsService.getObjById(CommUtil.null2Long(goods_id));
		if(beforeGoods == null || !beforeGoods.getId().equals(goods.getId())){ //新增或编辑时更换商品，需要验证商品是否符合规则
			if(goods.getSeckill_buy() != 0){
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "不能添加已经参加了促销活动的商品！");
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_seckill.htm");
				return mv;
			}
			if(!"all".equals(goods.getInventory_type())){
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "不能添加非全局库存的商品！");
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_seckill.htm");
				return mv;
			}
			if(goods.getGoods_inventory() - gg.getGg_count() < 0){
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "商品库存不够，商品总库存为：！"+goods.getGoods_inventory());
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_seckill.htm");
				return mv;
			}
		}else{
			if(goods.getGoods_inventory() + oldCount - gg.getGg_count() < 0){
				mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "商品库存不够，商品总库存为：！"+goods.getGoods_inventory() + oldCount);
				mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_seckill.htm");
				return mv;
			}
		}
		
		if(beforeGoods == null){ //新增
			logger.debug("add seckillgoods");
			goods.setGoods_inventory(goods.getGoods_inventory() - gg.getGg_count()); //更新商品库存
			goods.setSeckill_buy(1);
		}else if(!beforeGoods.getId().equals(goods.getId())){ //编辑时重新选择了商品
			logger.debug("edit seckillgoods with change goods");
			beforeGoods.setSeckill_buy(0);
			beforeGoods.setGoods_inventory(beforeGoods.getGoods_inventory() + oldCount);
			goods.setSeckill_buy(1);
			goods.setGoods_inventory(goods.getGoods_inventory() - gg.getGg_count()); //更新商品库存
		}else{ //编辑时没有重新选择商品
			logger.debug("edit seckillgoods with no change goods");
			goods.setGoods_inventory(goods.getGoods_inventory() - gg.getGg_count() + oldCount); //更新商品库存
		}
		
		if(gg.isWeixin_shop_recommend()){
			gg.setWeixin_shop_recommendTime(new Date());
		}
		
		gg.setGg_goods(goods);
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.div(CommUtil.mul(gg.getGg_price(), 10), 
				gg.getGg_goods().getGoods_price())));
		
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator
				+ "cache";
		Map map = null;
		try {
			String fileName = gg.getWx_photo() == null ? "" : gg.getWx_photo().getName();
			map = CommUtil.saveFileToServer(request, "gg_acc", saveFilePathName, "", null);
			if ("".equals(fileName)) {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/seckill"));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					gg.setWx_photo(gg_img);
				}
			} else {
				if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
					Accessory gg_img = gg.getWx_photo();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					gg_img.setPath(this.ftpTools.systemUpload(gg_img.getName(), "/seckill"));
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
		seckillGoodsService.saveOrUpdateSeckill(id, gg, beforeGoods, goods);
		
		// 更新lucene索引
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		LuceneUtil.setIndex_path(goods_lucene_path);
		lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		if(null != beforeGoods){
			lucene.update(CommUtil.null2String(beforeGoods.getId()), luceneVoTools.updateGoodsIndex(beforeGoods));
		}
		
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/self_seckill.htm");
		if (id != null && !StringUtils.isNullOrEmpty(id)) {
			mv.addObject("op_title", "秒杀商品编辑成功");
		} else {
			mv.addObject("op_title", "秒杀商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request) + "/admin/self_seckill_add.htm");
		}
		return mv;
	}

	
	
	@SecurityMapping(title = "商品类秒杀商品编辑", value = "/admin/self_seckill_edit.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill_edit.htm")
	public ModelAndView self_seckill_edit(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/self_seckill_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SeckillGoods obj = seckillGoodsService.getObjById(CommUtil.null2Long(id));
		
		//初始化时分
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(obj.getBeginTime());
		int sel1 = calendar.get(Calendar.HOUR_OF_DAY);
		int sel2 = calendar.get(Calendar.MINUTE);
		calendar.setTime(obj.getEndTime());
		int sel3 = calendar.get(Calendar.HOUR_OF_DAY);
		int sel4 = calendar.get(Calendar.MINUTE);
		mv.addObject("sel1", sel1);
		mv.addObject("sel2", sel2);
		mv.addObject("sel3", sel3);
		mv.addObject("sel4", sel4);
		
		mv.addObject("edit", true);
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "商品类秒杀商品发布", value = "/admin/self_seckill_publish.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill_publish.htm")
	public String self_seckill_publish(HttpServletRequest request, HttpServletResponse response, String id) {
		SeckillGoods obj = seckillGoodsService.getObjById(CommUtil.null2Long(id));
		Goods goods = obj.getGg_goods();
		
		if(obj.getEndTime().before(new Date())){
			obj.setGg_status(3); //秒杀结束
			goods.setSeckill_buy(0);
			goods.setGoods_inventory(goods.getGoods_inventory()+obj.getGg_count());
			goods.setGoods_current_price(goods.getStore_price());
		}else if(obj.getBeginTime().before(new Date())){
			obj.setGg_status(2);//正常进行中
			goods.setSeckill_buy(2);
			goods.setGoods_current_price(obj.getGg_price());
		}else {
			obj.setGg_status(1);//即将秒杀
			goods.setSeckill_buy(4);
			goods.setGoods_current_price(obj.getGg_price());
		}
		obj.setPublish_time(new Date());
		
		this.goodsService.update(goods);
		seckillGoodsService.update(obj);
		
		// 更新lucene索引
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		LuceneUtil.setIndex_path(goods_lucene_path);
		lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		
		return "redirect:" + "/admin/self_seckill.htm";
	}
	
	@SecurityMapping(title = "商品类秒杀商品取消", value = "/admin/self_seckill_cancel.htm*", rtype = "admin", rname = "秒杀管理", rcode = "self_seckill", rgroup = "自营")
	@RequestMapping("/admin/self_seckill_cancel.htm")
	public String self_seckill_cancel(HttpServletRequest request, HttpServletResponse response, String id) {
		SeckillGoods obj = seckillGoodsService.getObjById(CommUtil.null2Long(id));
		obj.setGg_status(3);
		obj.setCancel_time(new Date());
		seckillGoodsService.update(obj);
		
		Goods goods = obj.getGg_goods();
		goods.setSeckill_buy(0);
		goods.setGoods_inventory(goods.getGoods_inventory()+obj.getGg_count());
		goods.setGoods_current_price(goods.getStore_price());
		this.goodsService.update(goods);
		
		// 更新lucene索引
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		LuceneUtil.setIndex_path(goods_lucene_path);
		lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		
		return "redirect:" + "/admin/self_seckill.htm";
	}
}
