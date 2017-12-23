package com.iskyshop.smilife.coupon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.module.weixin.manage.coupon.activity.CouponActivityComm;
import com.tydic.framework.util.PropertyUtil;

@Service
public class AppCouponServiceImpl implements IAppCouponService {
	public static final Logger logger = Logger.getLogger(AppCouponServiceImpl.class);
	
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	
	/**
     * 获取指定团购中的商品列表(只返回未开始和正在进行的团购商品，不返回已过期的团购商品)
     * @method getGroupGoodsList
     * @return List<Map>
     */
	public List<Map> getCouponList(Long goods_id,Integer currentPage, Integer pageSize) {
		List<Map> map_list = new ArrayList<Map>();
		String activity_id = PropertyUtil.getProperty("couponActivityId");
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		if(obj == null) {
			throw new RuntimeException("未找到id=" + goods_id + "的商品。");
		}
		if (obj != null && obj.getGoods_status() == 0) {
			if (obj.getGoods_type() == 0) { // 平台自营商品
				//自营优惠券活动
				Map allCouponParams = new HashMap();
				allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
				allCouponParams.put("acType", 1);// 活动类型，0为商品活动，1为优惠券活动
				allCouponParams.put("ac_status", 1);// 活动状态，0为关闭，1为启动
				allCouponParams.put("ag_status", 1);// 活动商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2为已经到期关闭
				allCouponParams.put("activity_id",CommUtil.null2Long(activity_id));				
				allCouponParams.put("coupon_type", 0);// 优惠券类型，0为平台优惠券，抵消自营商品订单金额，1为商家优惠券，抵消订单中该商家商品部分金额
				
				List<Coupon> allCouponList = new ArrayList<Coupon>();
				List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(
						"select obj from ActivityGoods obj where obj.act.ac_status=:ac_status "
						+ "and obj.act.id=:activity_id and obj.acType=:acType and obj.ag_status=:ag_status "
						+ "and obj.act.ac_begin_time<=:current_time "
						+ "and obj.act.ac_end_time>=:current_time "
						+ "and obj.coupon.coupon_begin_time<=:current_time "
						+ "and obj.coupon.coupon_end_time>=:current_time "
						+ "and obj.coupon.coupon_type=:coupon_type "
						+ "and obj.coupon.store is null "
						+ "order by obj.coupon.coupon_amount",
						allCouponParams, -1, -1);
				for (ActivityGoods allActivityGoods : allActivityGoodsList) {
					allCouponList.add(allActivityGoods.getCoupon());
				}
				
				for (Coupon allCoupons : allCouponList) {
					List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
					int allCouponInfoLen = 0;
					if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
						allCouponInfoLen = allCouponInfoList.size();
					}
					if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
						Map coupon_map =new HashMap();
						coupon_map.put("coupon_id", allCoupons.getId());//优惠券ID
						coupon_map.put("coupon_amount", allCoupons.getCoupon_amount());//优惠券金额
						coupon_map.put("coupon_order_amount", allCoupons.getCoupon_order_amount());//优惠券使用的订单满金额
						coupon_map.put("coupon_begin_time",CommUtil.formatTime("yyyy-MM-dd",allCoupons.getCoupon_begin_time()));//优惠券使用开始时间
						coupon_map.put("coupon_end_time", CommUtil.formatTime("yyyy-MM-dd",allCoupons.getCoupon_end_time()));//优惠券使用结束时间
						map_list.add(coupon_map);
					}
				}	
			}else{
				//商家优惠券活动
				Map allCouponParams = new HashMap();
				allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
				allCouponParams.put("acType", 1);
				allCouponParams.put("ac_status", 1);
				allCouponParams.put("ag_status", 1);
				allCouponParams.put("coupon_type", 1);
				allCouponParams.put("activity_id",CommUtil.null2Long(activity_id));
				allCouponParams.put("storeId", obj.getGoods_store().getId());

				List<Coupon> allCouponList = new ArrayList<Coupon>();
				List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(
						"select obj from ActivityGoods obj where obj.act.ac_status=:ac_status "
						+ "and obj.act.id=:activity_id and obj.acType=:acType and obj.ag_status=:ag_status "
						+ "and obj.act.ac_begin_time<=:current_time "
						+ "and obj.act.ac_end_time>=:current_time "
						+ "and obj.coupon.coupon_begin_time<=:current_time "
						+ "and obj.coupon.coupon_end_time>=:current_time "
						+ "and obj.coupon.coupon_type=:coupon_type "
						+ "and obj.coupon.store.id =:storeId "
						+ "order by obj.coupon.coupon_amount",
						allCouponParams, -1, -1);
				for (ActivityGoods allActivityGoods : allActivityGoodsList) {
					allCouponList.add(allActivityGoods.getCoupon());
				}

				for (Coupon allCoupons : allCouponList) {
					//获取优惠券详情信息数量
					List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
					int allCouponInfoLen = 0;
					if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
						allCouponInfoLen = allCouponInfoList.size();
					}
					if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
						Map coupon_map =new HashMap();
						coupon_map.put("coupon_id", allCoupons.getId());//优惠券ID
						coupon_map.put("coupon_amount", allCoupons.getCoupon_amount());//优惠券金额
						coupon_map.put("coupon_order_amount", allCoupons.getCoupon_order_amount());//优惠券使用的订单满金额
						coupon_map.put("coupon_begin_time",CommUtil.formatTime("yyyy-MM-dd",allCoupons.getCoupon_begin_time()));//优惠券使用开始时间
						coupon_map.put("coupon_amount", CommUtil.formatTime("yyyy-MM-dd",allCoupons.getCoupon_end_time()));//优惠券使用结束时间
						map_list.add(coupon_map);
					}
				}
			}
		}
		return map_list;
	}		
}
