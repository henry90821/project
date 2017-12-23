package com.iskyshop.foundation.service.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.view.web.tools.BuyGiftViewTools;

@Service
@Transactional
public class GoodsServiceImpl implements IGoodsService {
	private Logger logger = Logger.getLogger(this.getClass());
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDao;
	@Resource(name = "groupGoodsDAO")
	private IGenericDAO<GroupGoods> groupGoodsDAO;
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDAO;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private FTPServerTools ftpTools;
	@Autowired
	private ISysConfigService sysConfigService;

	
	@Transactional(readOnly = false)
	public boolean save(Goods goods) {
		/**
		 * init other field here
		 */
		try {
			this.goodsDao.save(goods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public Goods getObjById(Long id) {
		Goods goods = this.goodsDao.get(id);
		if (goods != null) {
			return goods;
		}
		return null;
	}
	
	public List executeNativeQuery(String sql) {
		return this.goodsDao.executeNativeQuery(sql, null, -1, -1);
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.goodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> goodsIds) {
		for (Serializable id : goodsIds) {
			delete((Long) id);
		}
		return true;
	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Goods.class, construct, query, params, this.goodsDao);
		PageObject pageObj = properties.getPageObj();
		pList.doList(pageObj.getCurrentPage(), pageObj.getPageSize());
			
		return pList;
	}

	@Transactional(readOnly = false)
	public boolean update(Goods goods) {
		try {

			this.goodsDao.update(goods);
			return true;
		} catch (Exception e) {
			logger.error("更新商品失败：goodsId=" + goods.getId() + ", error:" + e.getMessage(), e);
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean executeSql(String sql) {
		try {
			this.goodsDao.executeSql(sql);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Transactional(readOnly = true)
	public List<Goods> query(String query, Map params, int begin, int max) {
		return this.goodsDao.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public Goods getObjByProperty(String construct, String propertyName, Object value) {
		return this.goodsDao.getBy(construct, propertyName, value);
	}

	/**
	 * add tianbotao 取消订单，修改订单状态
	 * 
	 * @param orderForm
	 */
	public void order_cancel(OrderForm orderForm) {
		List<OrderForm> objs = new ArrayList<OrderForm>();
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(orderForm.getId()));
		objs.add(obj);
		boolean all_verify = true;
		if (obj != null) {
			if (obj.getOrder_main() == 1 && obj.getChild_order_detail() != null) {
				List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
				if (maps != null) {
					for (Map map : maps) {
						logger.info(map.get("order_id"));
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
						objs.add(child_order);
					}
				}
			}

			for (OrderForm of : objs) {
				if (of.getOrder_status() >= 20) {
					all_verify = false;
				}
			}
		}
		if (all_verify) {
			if (obj != null) {
				if (obj.getOrder_main() == 1) {
					List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
					if (maps != null) {
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(map.get("order_id")));
							child_order.setOrder_status(0);
							this.orderFormService.update(child_order);
						}
					}
				}
				int old_status = obj.getOrder_status();
				obj.setOrder_status(0);
				this.orderFormService.update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("取消订单。原订单状态为：" + old_status);
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
			}
		}
	}

	/**
	 * add tianbotao 还原商品库存
	 * 
	 * @param orderForm
	 */
	public void recover_goods_inventory(OrderForm order, String type) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		if (order.getOrder_cat() == 2) {
			Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
			int count = CommUtil.null2Int(map.get("goods_count").toString());
			String goods_id = map.get("goods_id").toString();
			GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(goods_id));
			goods.setGroup_count(goods.getGroup_count() + CommUtil.null2Int(count));
			this.groupLifeGoodsService.update(goods);
		}
		// 判断是否有满就送如果有则进行库存操作
		if (order.getWhether_gift() == 1) {
			this.buyGiftViewTools.recover_gift_invoke(order, type);
		}
		if (order.getOrder_cat() != 2) {
			List<Goods> goods_list = new ArrayList<Goods>();
			if("refund".equals(type)){
				goods_list = this.orderFormTools.queryOrderOfGoods(CommUtil.null2String(order.getId()));
			}else{
				goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
			}
			// 更新订单中组合套装商品信息
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
			for (Map map_combin : maps) {
				if (map_combin.get("combin_suit_info") != null) {
					Map suit_info = Json.fromJson(Map.class, CommUtil.null2String(map_combin.get("combin_suit_info")));
					int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
					List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
					for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
						for (Goods temp : goods_list) {
							if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
								Goods goods = this.getObjById(CommUtil.null2Long(temp_goods.get("id")));
								if("refund".equals(type)){
									goods.setGoods_salenum(goods.getGoods_salenum() - combin_count);
								}
								goods.setGoods_inventory(goods.getGoods_inventory() + combin_count);
								this.update(goods);
							}
						}
					}
				}
			}
			for (Goods goods : goods_list) {
				int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
						CommUtil.null2String(goods.getId()));
				if (goods.getGroup() != null && goods.getGroup_buy() == 2) { // 更新团购商品库存
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
							gg.setGg_count(gg.getGg_count() + goods_count);
							if("refund".equals(type)){
								gg.setGg_selled_count(gg.getGg_selled_count() - goods_count);
							}
							this.groupGoodsDAO.update(gg);
							// 更新lucene索引
							String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
									+ "groupgoods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneUtil lucene = LuceneUtil.instance();
							lucene.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
						}
					}
				} else if (!"".equals(CommUtil.null2String(order.getSeckill_info()))) { // 更新秒杀商品库存
					// Map seckillInfo = Json.fromJson(Map.class, CommUtil.null2String(order.getSeckill_info()));
					// String good_id = CommUtil.null2String(seckillInfo.get("good_id"));
					// if(good_id.equals(goods.getId())){}
					Map map = Json.fromJson(Map.class, order.getSeckill_info());
					SeckillGoods seckillGoods = seckillGoodsService
							.getObjById(Long.valueOf(String.valueOf(map.get("seckill_goods_id"))));
					if (seckillGoods != null) {
						if (seckillGoods.getGg_status() != 3) { // 秒杀未结束，则更新秒杀商品库存
							seckillGoods.setGg_count(seckillGoods.getGg_count() + goods_count);
							if("refund".equals(type)){
								seckillGoods.setGg_selled_count(seckillGoods.getGg_selled_count() - goods_count);
							}
							seckillGoodsService.update(seckillGoods);
						} else { // 更新商品库存
							goods.setGoods_inventory(goods.getGoods_inventory() + goods_count);
							if("refund".equals(type)){
								goods.setGoods_salenum(goods.getGoods_salenum() - goods_count);
							}
						}
					}
				}

				if ("".equals(CommUtil.null2String(order.getSeckill_info()))) {
					String inventory_type = goods.getInventory_type() == null ? "all" : goods.getInventory_type();
					if ("all".equals(inventory_type)) {
						goods.setGoods_inventory(goods.getGoods_inventory() + goods_count);
						if("refund".equals(type)){
							goods.setGoods_salenum(goods.getGoods_salenum() - goods_count);
						}
					} else {
						List<String> gsps = new ArrayList<String>();
						List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
								.queryOfGoodsGsps(CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
						// String spectype = "";
						for (GoodsSpecProperty gsp : temp_gsp_list) {
							gsps.add(gsp.getId().toString());
							// spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
						}
						String[] gsp_list = new String[gsps.size()];
						gsps.toArray(gsp_list);
						List<HashMap> list = Json.fromJson(ArrayList.class,
								CommUtil.null2String(goods.getGoods_inventory_detail()));
						for (Map temp : list) {
							String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
							Arrays.sort(temp_ids);
							Arrays.sort(gsp_list);
							if (Arrays.equals(temp_ids, gsp_list)) {
								temp.put("count", CommUtil.null2Int(temp.get("count")) + goods_count);
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
						goods.setGoods_inventory(goods.getGoods_inventory() + goods_count);
						if("refund".equals(type)){
							goods.setGoods_salenum(goods.getGoods_salenum() - goods_count);
						}
					}
				}
				this.goodsDAO.update(goods);
				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
			}
		}
	}

	/**
	 * 扣减订单相关的商品的库存（只要传递主订单进来，其关联的所有子订单中的商品的库存在本函数中也会被处理）
	 * 
	 * @param order
	 *            主订单对象
	 */
	public String subtract_goods_inventory(OrderForm order) {
		String msg = null;
		if (order.getOrder_cat() == 2) {
			Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
			int count = CommUtil.null2Int(map.get("goods_count").toString());
			String goods_id = map.get("goods_id").toString();
			GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(goods_id));
			//goods.setGroup_count(goods.getGroup_count() - CommUtil.null2Int(count));
			//this.groupLifeGoodsService.update(goods);
			if(!this.executeSql(" UPDATE `iskyshop_group_lifegoods` SET group_count = group_count - "+count+" WHERE id = "+ goods.getId()+" AND group_count - "+count+" >= 0  ")){
				msg = goods.getGg_name() + "库存不足！";
			}

		}
		// 判断是否有满就送如果有则进行库存操作
		if (order.getWhether_gift() == 1) {
			this.buyGiftViewTools.update_gift_invoke(order, msg);
		}
		if (order.getOrder_cat() != 2) {
			List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
			// 更新订单中组合套装商品信息
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
			for (Map map_combin : maps) {
				if (map_combin.get("combin_suit_info") != null) {
					Map suit_info = Json.fromJson(Map.class, CommUtil.null2String(map_combin.get("combin_suit_info")));
					int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
					List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
					for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
						for (Goods temp : goods_list) {
							if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
								Goods goods = this.getObjById(CommUtil.null2Long(temp_goods.get("id")));
								//goods.setGoods_inventory(goods.getGoods_inventory() - combin_count);
								//this.update(goods);
								if(!this.executeSql(" UPDATE `iskyshop_goods` SET goods_inventory = goods_inventory - "+combin_count+" WHERE id = "+ goods.getId()+" AND goods_inventory - "+combin_count+" >= 0  ")){
									msg = goods.getGoods_name()+ "库存不足！";
								}
							}
						}
					}
				}
			}
			// 普通商品更新信息
			for (Goods goods : goods_list) {
				int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
						CommUtil.null2String(goods.getId()));
				if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
							//gg.setGg_count(gg.getGg_count() - goods_count);
							//this.groupGoodsService.update(gg);
							if(!this.executeSql(" UPDATE `iskyshop_group_goods` SET gg_count = gg_count - "+goods_count+" WHERE id = "+ gg.getId()+" AND gg_count - "+goods_count+" >= 0  ")){
								msg = goods.getGoods_name()+ "库存不足！";
							}

							String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
									+ "groupgoods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneUtil lucene = LuceneUtil.instance();
							lucene.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGroupGoodsIndex(
									goods.getGroup_goods_list().get(goods.getGroup_goods_list().size() - 1)));
						}
					}
				} else if (goods.getSeckill_buy() == 2) { // 更新秒杀商品库存
					String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
					Map params = new HashMap();
					params.put("goods_id", goods.getId());
					params.put("gg_status", 2);
					SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
					//seckillGoods.setGg_count(seckillGoods.getGg_count() - goods_count);
					//seckillGoodsService.update(seckillGoods);
					if(!this.executeSql(" UPDATE `iskyshop_seckill_goods` SET gg_count = gg_count - "+goods_count+" WHERE id = "+ seckillGoods.getId()+" AND gg_count - "+goods_count+" >= 0  ")){
						msg = goods.getGoods_name()+ "库存不足！";
					}
				}
				List<String> gsps = new ArrayList<String>();
				List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
						.queryOfGoodsGsps(CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
				// String spectype = "";
				for (GoodsSpecProperty gsp : temp_gsp_list) {
					gsps.add(gsp.getId().toString());
					// spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
				}
				String[] gsp_list = new String[gsps.size()];
				gsps.toArray(gsp_list);
				boolean inventory_warn = false;

				if (goods.getSeckill_buy() != 2) { // 秒杀商品不减原商品的库存
					if ("all".equals(goods.getInventory_type())) {
						//goods.setGoods_inventory(goods.getGoods_inventory() - goods_count);
						if(!this.executeSql(" UPDATE `iskyshop_goods` SET goods_inventory = goods_inventory - "+goods_count+" WHERE id = "+ goods.getId()+" AND goods_inventory - "+goods_count+" >= 0  ")){
							msg = goods.getGoods_name()+ "库存不足！";
						}
						if ((goods.getGoods_inventory() - goods_count) <= goods.getGoods_warn_inventory()) {
							inventory_warn = true;
						}
					} else {
						List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
						for (Map temp : list) {
							String[] tempIds = CommUtil.null2String(temp.get("id")).split("_");
							Arrays.sort(tempIds);
							Arrays.sort(gsp_list);
							if (Arrays.equals(tempIds, gsp_list)) {
								temp.put("count", CommUtil.null2Int(temp.get("count")) - goods_count);
								if (CommUtil.null2Int(temp.get("count")) <= CommUtil.null2Int(temp.get("supp"))) {
									inventory_warn = true;
								}
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
						//goods.setGoods_inventory(goods.getGoods_inventory() - goods_count);
						if(!this.executeSql(" UPDATE `iskyshop_goods` SET goods_inventory = goods_inventory - "+goods_count+" WHERE id = "+ goods.getId()+" AND goods_inventory - "+goods_count+" >= 0  ")){
							msg = goods.getGoods_name()+ "库存不足！";
						}
					}
				}
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (goods.getGroup() != null && gg.getGroup().getId().equals(goods.getGroup().getId())
							&& gg.getGg_count() == 0) {
						goods.setGroup_buy(3); // 标识商品的状态为团购数量已经结束
					}
				}
				if (inventory_warn) {
					goods.setWarn_inventory_status(-1); // 该商品库存预警状态
				}
				this.update(goods);

				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
			}
		}
		return msg;
	}
	
	/**
	 * 更新商品二维码
	 * @param goods_id
	 * @param serverRealPath
	 * @return
	 */
	public boolean createGoodsQR(Long goods_id,String serverRealPath)
	{
		
		Goods obj = getObjById(CommUtil.null2Long(goods_id));
		try{
			String uploadFilePath = this.sysConfigService.getSysConfig().getUploadFilePath();
			String destPath = serverRealPath + uploadFilePath + File.separator
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
				logoPath = serverRealPath + uploadFilePath + File.separator
						+ "cache" + File.separator + obj.getGoods_main_photo().getName();
			} else {//取默认的商品图片(默认的商品图片可能是项目中的资源文件，也可能是用户上传上去的新图片)
				Accessory  defaulImg = this.sysConfigService.getSysConfig().getGoodsImage();
				String imgPath = defaulImg.getPath();
				if(imgPath.startsWith("resources/style/common/images"))
					logoPath = serverRealPath + imgPath + File.separator + this.sysConfigService.getSysConfig().getGoodsImage().getName();	
				else {							
					logoPath = serverRealPath + uploadFilePath + File.separator + "cache" + File.separator + defaulImg.getName();
					File defaultGoodsImg = new File(logoPath);
					if(!defaultGoodsImg.exists()) {
						this.ftpTools.systemDownloadImg(defaulImg);
					}
				}					
			}
			
			QRCodeUtil.encode(sysConfigService.getSysConfig().getGoodsH5Url() + "?goodsId=" + goods_id, logoPath, destPath, true);
			
			// 将二维码图片上传ftp
			String url = this.ftpTools.systemUpload(obj.getId() + "_qr.jpg", "/goods_qr");
			obj.setQr_img_path(url + "/" + obj.getId() + "_qr.jpg");
			update(obj);
			// 删除主图
			ftpTools.DeleteWebImg(obj.getGoods_main_photo());
		}catch(Exception e){
			obj.setQr_img_path("1");//必须标记为一个非空值，否则就会出现循环更新二维码图片
			update(obj);
			logger.info("update goods_id "+ goods_id+ " fail!,Exception msg:"+e.getMessage());
			return false;
		}
			
		return true;
	}
}
