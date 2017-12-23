package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;

@Component
public class RefundTools {
	@Autowired
	private OrderFormTools orderFormTools;

	@Autowired
	private IOrderFormService orderFormService;

	@Autowired
	private IReturnGoodsLogService returngoodslogService;

	/**
	 * 更新订单优惠券和活动的已退款金额,商品的退款状态
	 * 
	 * @param order
	 * @param coupon_discount
	 * @param enough_reduce_discount
	 */
	public void updateOrderDiscount(OrderForm order, String coupon_discount, String enough_reduce_discount,
			String goods_id) {
		if (!StringUtils.isNullOrEmpty(order.getCoupon_info()) && !"0.0".equals(coupon_discount)) { // 优惠券信息
			Map couponMap = orderFormTools.queryCouponInfo(order.getCoupon_info());
			double returns = CommUtil.null2Double(couponMap.get("coupon_return_amount")); // 优惠券已退金额
			double discount = CommUtil.null2Double(coupon_discount);
			couponMap.put("coupon_return_amount", returns + discount);
			order.setCoupon_info(Json.toJson(couponMap, JsonFormat.compact()));
		}
		if (CommUtil.null2Double(order.getEnough_reduce_amount()) > 0 && !"0.0".equals(enough_reduce_discount)) { // 满就减金额退款
			Map er_info = (Map) Json.fromJson(order.getEnough_reduce_info());
			Iterator<String> it = er_info.keySet().iterator();
			// {"all_23":1110.0,"enouhg_23":"500.00","23":[13],"reduce23":100.0}
			while (it.hasNext()) {
				String key = it.next();
				if ("a".equals(key.substring(0, 1))) { // 查找该商品的满就减信息
					String key2 = key.substring(4, key.length());
					double returns = CommUtil.null2Double(er_info.get("return_" + key2)); // 满就减已退金额
					double discount = CommUtil.null2Double(enough_reduce_discount);
					er_info.put("return_" + key2, returns + discount);
					order.setEnough_reduce_info(Json.toJson(er_info, JsonFormat.compact()));
				}
			}
		}
		if (!StringUtils.isNullOrEmpty(goods_id)) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(goods_id)) {
					m.put("goods_return_status", 9);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			order.setGoods_info(Json.toJson(new_maps));
		}

		if ((!StringUtils.isNullOrEmpty(order.getCoupon_info()) && !"0.0".equals(coupon_discount))
				|| (CommUtil.null2Double(order.getEnough_reduce_amount()) > 0 && !"0.0".equals(enough_reduce_discount))
				|| !StringUtils.isNullOrEmpty(goods_id)) {
			this.orderFormService.save(order);
		}

	}

	/**
	 * 计算本订单商品总价减去已退货商品的余额 计算参加满减商品的总价减去已退满减商品的余额
	 * 
	 * @param order_id
	 * @param all_price
	 * @param list
	 * @return
	 */
	public double calNextPrice(Long order_id, double all_price, List list) {
		Map params = new HashMap();
		params.put("return_order_id", CommUtil.null2Long(order_id));
		params.put("goods_return_status", "11");
		params.put("refund_status", 0);
		String sqlString = "select obj from ReturnGoodsLog obj where  obj.return_order_id=:return_order_id and obj.goods_return_status=:goods_return_status and obj.refund_status>:refund_status ";
		if (!StringUtils.isNullOrEmpty(list)) {
			sqlString += " and(";
			for (Object good_id : list) {
				sqlString += " obj.goods_id=" + good_id + "or";
			}
			sqlString += " 1=2)";
		}
		List<ReturnGoodsLog> returnlogs = returngoodslogService.query(sqlString, params, -1, -1);
		for (ReturnGoodsLog returnGoodsLog : returnlogs) {
			all_price = all_price - CommUtil.null2Double(returnGoodsLog.getGoods_all_price());
		}
		return all_price;
	}

	/**
	 * 计算商品的折扣价
	 * 
	 * @param order
	 * @param map(coupon_discount:优惠券折扣；enough_reduce_discount：满减折扣)
	 * @return
	 */
	public Map<String, Double> calGoodsDiscount(OrderForm order, String goodsAllPrice, String goodsId) {
		Map<String, Double> map = new HashMap<String, Double>();
		double goods_all_price = CommUtil.null2Double(goodsAllPrice); // 商品总价
		double return_account = 0.00; // 折扣
		if (!StringUtils.isNullOrEmpty(order.getCoupon_info())) { // 优惠券信息
			Map couponMap = orderFormTools.queryCouponInfo(order.getCoupon_info());
			double all = CommUtil.null2Double(order.getGoods_amount()); // 本订单商品总价
			double nextAll = this.calNextPrice(order.getId(), all, null);
			double enouhg = CommUtil.null2Double(couponMap.get("coupon_order_amount").toString()); // 优惠券限制金额
			double reduce = CommUtil.null2Double(couponMap.get("coupon_amount").toString()); // 优惠券金额
			double returns = CommUtil.null2Double(couponMap.get("coupon_return_amount").toString()); // 优惠券已退金额
			if (nextAll - goods_all_price < enouhg) {// 去掉该商品后不满足优惠规则,则全部扣除优惠
				return_account = reduce - returns;
				map.put("coupon_discount", CommUtil.null2Double(CommUtil.formatMoney(return_account)));
			} else {
				return_account = goods_all_price / all * reduce; // 商品使用优惠券的折扣
				map.put("coupon_discount", CommUtil.null2Double(CommUtil.formatMoney(return_account)));
			}

		}

		double return_account2 = 0.00; // 折扣2
		if (CommUtil.null2Double(order.getEnough_reduce_amount()) > 0) { // 满就减金额退款
			Map er_info = (Map) Json.fromJson(order.getEnough_reduce_info());
			Iterator<String> it = er_info.keySet().iterator();
			// {"all_23":1110.0,"enouhg_23":"500.00","23":[13],"reduce23":100.0}
			while (it.hasNext()) {
				String key = it.next();
				if ("a".equals(key.substring(0, 1))) { // 查找该商品的满就减信息
					String key2 = key.substring(4, key.length());
					List list = (List) er_info.get(key2);
					for (Object good_id : list) {
						if (good_id.toString().equals(goodsId)) { // 有该商品
							double all = CommUtil.null2Double(er_info.get(key).toString()); // 参加满减商品的总价
							double nextAll = this.calNextPrice(order.getId(), all, list);
							double enouhg = CommUtil.null2Double(er_info.get("enouhg_" + key2)); // 满**
							double reduce = CommUtil.null2Double(er_info.get("reduce_" + key2)); // 减**
							double returns = CommUtil.null2Double(er_info.get("return_" + key2)); // 已退金额
							if (nextAll - goods_all_price < enouhg) { // 去掉该商品后不满足满就减,则全部扣除满减的优惠
								return_account2 = reduce - returns;
								map.put("enough_reduce_discount",
										CommUtil.null2Double(CommUtil.formatMoney(return_account2)));
							} else {
								return_account2 = goods_all_price / all * reduce; // 商品使用满减的折扣
								map.put("enough_reduce_discount",
										CommUtil.null2Double(CommUtil.formatMoney(return_account2)));
							}
						}
					}
				}
			}
		}
		return map;
	}

}
