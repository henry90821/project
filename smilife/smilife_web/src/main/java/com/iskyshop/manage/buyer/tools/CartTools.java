package com.iskyshop.manage.buyer.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 
 * <p>
 * Title:CartTools.java
 * </p>
 * 
 * <p>
 * Description:订单工具类，用以处理各个店铺的优惠劵信息
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
 * @author jy
 * 
 * @date 2014年5月12日
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class CartTools {
	
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService classService;


	/**
	 * 查出该用户在店铺（包括自营）所可以使用的优惠券
	 * 
	 * @param store_id
	 * @param total_price
	 * @return
	 */
	public List<CouponInfo> query_coupon(String store_id, String total_price) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("coupon_order_amount", BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);

		if ("self".equals(store_id)) { // 自营
			params.put("coupon_type", 0);
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		} else { // 第三方经营
			params.put("store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.store.id=:store_id "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		}
		return couponinfos;
	}
	
	public List<CouponInfo> query_coupon(String custId,String store_id, String total_price) {
		User user = this.userService.getObjById(userService.getObjByProperty(null, "custId", custId).getId());
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("coupon_order_amount", BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);
		if ("self".equals(store_id)) { // 自营
			params.put("coupon_type", 0);
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		} else { // 第三方经营
			params.put("store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.store.id=:store_id "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		}
		return couponinfos;
	}

	/**
	 * 手机端优惠券查询
	 * 
	 * @param store_id
	 * @param total_price
	 * @return
	 */
	public List<CouponInfo> mobile_query_coupon(String store_id, String total_price, String user_id) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("coupon_order_amount", BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);
		if ("self".equals(store_id)) { // 自营
			params.put("coupon_type", 0);
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		} else { // 第三方经营
			params.put("store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.store.id=:store_id "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
		}
		return couponinfos;
	}

	/**
	 * 计算用户在购物车所有的全场优惠券
	 * @param custId
	 * @param all_normal_carts 普通商品（不检查是否是礼品商品）
	 * @return 可用的优惠券与不可用的优惠券
	 */
	public Map<String,List<Map>> query_global_couponinfos(String custId, List<GoodsCart> all_normal_carts) {
		//查询出所有的全场优惠券
		User user = this.userService.getObjById(userService.getObjByProperty(null, "custId", custId).getId());
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);
		//全场优惠券
		params.put("coupon_type", 2);
		
		couponinfos = this.couponInfoService.query(
				"select obj from CouponInfo obj where obj.coupon.coupon_type=:coupon_type "
						+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
						+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
				params, -1, -1);
		
		if(couponinfos==null || couponinfos.size()<=0 || all_normal_carts==null || all_normal_carts.size()<=0)
			return null;
		
		//制作品类、品牌的总金额
		Map<Long,Double> categoryAmount=new HashMap<Long,Double>();
		Map<Long,Double> brandAmount=new HashMap<Long,Double>();
		for(GoodsCart cart:all_normal_carts){
			Double amount=CommUtil.mul(cart.getPrice(),cart.getCount());
			//获取品类品类
			GoodsClass gc=getGoodsTopLevelClass(cart.getGoods().getGc());
			
			if(categoryAmount.containsKey(gc.getId()))
				categoryAmount.put(gc.getId(), CommUtil.add(categoryAmount.get(gc.getId()),amount));
			else
				categoryAmount.put(gc.getId(), amount);
			
			//获取品牌
			GoodsBrand brand=cart.getGoods().getGoods_brand();
			if(brand==null)
				continue;
			
			if(brandAmount.containsKey(brand.getId()))
				brandAmount.put(brand.getId(),CommUtil.add(brandAmount.get(brand.getId()), amount));
			else
				brandAmount.put(brand.getId(), amount);
				
		}
		
		List<Map> validCouponinfo=new ArrayList<Map>();
		List<Map> invalidCouponinfo=new ArrayList<Map>();
		
		for(CouponInfo couponInfo:couponinfos){
			boolean isValidCouponinfo=false;//是否是有效的优惠券
			String associatedIds=couponInfo.getCoupon().getAssociated_ids();
			if(associatedIds==null || associatedIds.length()<=0){
				continue;
			}
			
			//优惠券信息
			Map<String,Object> couponInfoMap=new HashMap<String,Object>();
			couponInfoMap.put("id", couponInfo.getId());
			couponInfoMap.put("coupon_amount", couponInfo.getCoupon().getCoupon_amount());
			couponInfoMap.put("coupon_name", couponInfo.getCoupon().getCoupon_name());
			couponInfoMap.put("associated_type",couponInfo.getCoupon().getAssociated_type());//优惠券适用的类型
			couponInfoMap.put("associated_ids", couponInfo.getCoupon().getAssociated_ids());
			String associatedNames="";
			//品类名称列表
			if(couponInfo.getCoupon().getAssociated_type()==1)
				associatedNames=classService.getClassNamesStr(couponInfo.getCoupon().getAssociated_ids());
			
			//品牌名称列表
			if(couponInfo.getCoupon().getAssociated_type()==2)
				associatedNames=brandService.getBrandNamesStr(couponInfo.getCoupon().getAssociated_ids());
			couponInfoMap.put("coupon_range_str", associatedNames);//优惠券适用的具体名称
			
			
			String[] ids=associatedIds.split(",");
			
			double amount=0.00;
			switch(couponInfo.getCoupon().getAssociated_type()){
			//品类
			case 1:
				//ids是优惠券可以使用的品类范围
				for(String id:ids){
					Long categoryId=CommUtil.null2Long(id);
					if(categoryId==-1L)
						continue;
					
					if(categoryAmount.get(categoryId)==null)
						continue;
					
					amount+=categoryAmount.get(categoryId);
				}
				
				//优惠券品类集合总金额大于等于优惠券订单金额 即有效优惠券
				if(couponInfo.getCoupon().getCoupon_order_amount().doubleValue()<=amount){
					validCouponinfo.add(couponInfoMap);
					isValidCouponinfo=true;
					break;
				}
					
				break;
			//品牌
			case 2:
				for(String id:ids){
					Long brandId=CommUtil.null2Long(id);
					if(brandId==-1L)
						continue;
					
					if(brandAmount.get(brandId)==null)
						continue;
					
					amount+=brandAmount.get(brandId);
				}
				
				//优惠券品牌集合总金额大于等于优惠券订单金额 即有效优惠券
				if(couponInfo.getCoupon().getCoupon_order_amount().doubleValue()<=amount){
					validCouponinfo.add(couponInfoMap);
					isValidCouponinfo=true;
					break;
				}
				
				break;
			default:
				break;
			}
			
			if(!isValidCouponinfo){
				invalidCouponinfo.add(couponInfoMap);
			}
		}
		
		Map<String,List<Map>> result=new HashMap<String,List<Map>>();
		result.put("validCouponinfos", validCouponinfo);
		result.put("invalidCouponinfos", invalidCouponinfo);
		return result;
	}
	
	/**
	 * 获取商品的品类
	 * @param gc 商品分类
	 * @return
	 */
	public GoodsClass getGoodsTopLevelClass(GoodsClass gc)
	{
		if(gc.getLevel()==0)
			return gc;
		else if(gc.getParent()==null)
			return gc;
		
		return getGoodsTopLevelClass(gc.getParent());
	}
	
	
	public Double calcGlobalCouponGoodsTotalPrice(List<GoodsCart> carts,int associated_type,List<String> global_coupon_associated_ids)
	{
		Double coupon_goods_total_price=0.00;
		
		for (GoodsCart gc : carts) {
			//品类
			if(associated_type==1){
				GoodsClass tmpClass=getGoodsTopLevelClass(gc.getGoods().getGc());
				if(global_coupon_associated_ids.indexOf(String.valueOf(tmpClass.getId()))!=-1)
					coupon_goods_total_price=CommUtil.add(coupon_goods_total_price,CommUtil.mul(gc.getPrice(),gc.getCount()));
			}
			//品牌
			else if(associated_type==2){
				GoodsBrand tmpBrand=gc.getGoods().getGoods_brand();
				if(tmpBrand!=null && global_coupon_associated_ids.indexOf(String.valueOf(tmpBrand.getId()))!=-1)
					coupon_goods_total_price=CommUtil.add(coupon_goods_total_price,CommUtil.mul(gc.getPrice(),gc.getCount()));
			}
		}
		
		return coupon_goods_total_price;
	}
}
