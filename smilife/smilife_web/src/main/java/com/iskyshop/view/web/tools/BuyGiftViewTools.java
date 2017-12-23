package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: BuyGiftViewTools.java
 * </p>
 * 
 * <p>
 * Description: 满就送信息管理工具
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
 * @date 2014-10-24
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class BuyGiftViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	
	public static ReadWriteLock mallInventoryRWLock = new ReentrantReadWriteLock();//商城商品库存读写锁，用于同步更新商城商品的库存的定时任务与查询商城商品库存

	public void update_gift_invoke(OrderForm order, String msg) {
		if (order != null) {
			if (order.getGift_infos() != null && !"".equals(order.getGift_infos())) {
				List<Map> maps = Json.fromJson(List.class, order.getGift_infos());
				for (Map map : maps) {
					BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(map.get("buyGify_id")));
					if (bg != null) {
						List<Map> gifts = Json.fromJson(List.class, bg.getGift_info());
						boolean mark = true;
						for (Map gift : gifts) {
							String goods_id = gift.get("goods_id").toString();
							if (goods_id.equals(map.get("goods_id").toString())) {
								if ("1".equals(gift.get("storegoods_count").toString())) {//共享库存
									Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
									//goods.setGoods_inventory(goods.getGoods_inventory() - 1);
									//this.goodsService.update(goods);
									if(!this.goodsService.executeSql(" UPDATE `iskyshop_goods` SET goods_inventory = goods_inventory - 1 WHERE id = "+ goods.getId()+" AND goods_inventory - 1 >= 0 ")){
										msg = goods.getGoods_name() + "库存不足！";
									}
									
									/*if (goods.getGoods_inventory() == 0) {
										bg.setGift_status(20);
										List<Map> g_maps = Json.fromJson(List.class, bg.getGift_info());
										g_maps.addAll(Json.fromJson(List.class, bg.getGift_info()));
										for (Map m : g_maps) {
											Goods g_goods = this.goodsService.getObjById(CommUtil.null2Long(m
													.get("goods_id")));
											if (g_goods != null) {
												g_goods.setOrder_enough_give_status(0);
												g_goods.setOrder_enough_if_give(0);
												g_goods.setBuyGift_id(null);
												g_goods.setBuyGift_amount(new BigDecimal(0.00));
												this.goodsService.update(g_goods);
											}
										}
									}*/
									
									if((goods.getGoods_inventory() - 1) > 0){
										mark = false;
									}
								} else {
									if (gift.get("goods_count") != null) {
										mallInventoryRWLock.writeLock().lock();
										gift.put("goods_count", CommUtil.null2Int(gift.get("goods_count")) - 1);
										/*if (CommUtil.null2Int(gift.get("goods_count")) == 0) {
											bg.setGift_status(20);
											List<Map> g_maps = Json.fromJson(List.class, bg.getGift_info());
											g_maps.addAll(Json.fromJson(List.class, bg.getGift_info()));
											for (Map m : g_maps) {
												Goods g_goods = this.goodsService.getObjById(CommUtil.null2Long(m
														.get("goods_id")));
												if (g_goods != null) {
													g_goods.setOrder_enough_give_status(0);
													g_goods.setOrder_enough_if_give(0);
													g_goods.setBuyGift_id(null);
													g_goods.setBuyGift_amount(new BigDecimal(0.00));
													this.goodsService.update(g_goods);
												}
											}
										}*/
										
										if(CommUtil.null2Int(gift.get("goods_count")) > 0){
											mark = false;
										}
										mallInventoryRWLock.writeLock().unlock();
									}
								}
							}else{
								if ("1".equals(gift.get("storegoods_count").toString())) {//共享库存
									Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
									if(goods != null && goods.getGoods_inventory() > 0){
										mark = false;
									}
								}else{
									if(CommUtil.null2Int(gift.get("goods_count")) > 0){
										mark = false;
									}
								}
							}
						}
						if(mark){
							bg.setGift_status(20);
							List<Map> g_maps = Json.fromJson(List.class, bg.getGift_info());
							for (Map m : g_maps) {
								Goods g_goods = this.goodsService.getObjById(CommUtil.null2Long(m
										.get("goods_id")));
								if (g_goods != null) {
									g_goods.setOrder_enough_give_status(0);
									g_goods.setOrder_enough_if_give(0);
									g_goods.setBuyGift_id(null);
									g_goods.setBuyGift_amount(new BigDecimal(0.00));
									this.goodsService.update(g_goods);
								}
							}
						}
						bg.setGift_info(Json.toJson(gifts, JsonFormat.compact()));
						this.buyGiftService.update(bg);
					}
				}
			}
			if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) {
				List<Map> cmaps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : cmaps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					this.update_gift_invoke(child_order, msg);
				}
			}
		}
	}
	
	/**
	 * add tianbotao
	 * 还原满就送库存
	 * @param order
	 */
	public void recover_gift_invoke(OrderForm order, String type){
		if (order != null) {
			if (order.getGift_infos() != null && !"".equals(order.getGift_infos())) {
				List<Map> maps = Json.fromJson(List.class, order.getGift_infos());
				for (Map map : maps) {
					BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(map.get("buyGify_id")));
					if (bg != null) {
						List<Map> gifts = Json.fromJson(List.class, bg.getGift_info());
						for (Map gift : gifts) {
							String goods_id = gift.get("goods_id").toString();
							if (goods_id.equals(map.get("goods_id").toString())) {
								if ("1".equals(gift.get("storegoods_count").toString())) {
									Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
									goods.setGoods_inventory(goods.getGoods_inventory() + 1);
									this.goodsService.update(goods);
								} else {
									if (gift.get("goods_count") != null) {
										gift.put("goods_count", CommUtil.null2Int(gift.get("goods_count")) + 1);
									}
								}
							}
						}
						bg.setGift_info(Json.toJson(gifts, JsonFormat.compact()));
						this.buyGiftService.update(bg);
					}
				}
			}
			if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail())) && !"refund".equals(type)) {
				List<Map> cmaps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : cmaps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					this.recover_gift_invoke(child_order, null);
				}
			}
		}
	}
}
