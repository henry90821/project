package com.iskyshop.foundation.domain.virtual;

import java.util.ArrayList;
import java.util.List;

import com.iskyshop.core.tools.CommUtil;

/**
 * 订单满减优惠信息
 * @author dengyuqi
 *
 */
public class OrderEnoughReduceInfo {
	
	/**
	 * 满减活动id
	 */
	private long enoughReduceId;
	
	/**
	 * 满多少
	 */
	private double enouhgPrice;
	
	/**
	 * 减多少
	 */
	private double reducePrice;
	
	/**
	 * 参与满减活动的商品列表
	 */
	private List<EnoughReduceGoods> enoughReduceGoodss;

	/**
	 * 参与满减活动的已退商品列表
	 */
	private List<EnoughReduceGoods> tkEnoughReduceGoodss;
	
	/**
	 * 参与满减活动的商品列表总额
	 */
	private double totalPrice;
	
	public class EnoughReduceGoods{
		private long goodsId;
		private double price;
		private int count;

		public EnoughReduceGoods(long goodsId) {
			super();
			this.goodsId = goodsId;
		}

		public EnoughReduceGoods(long goodsId, double price, int count) {
			this.goodsId = goodsId;
			this.price = price;
			this.count = count;
		}

		public long getGoodsId() {
			return goodsId;
		}

		public double getPrice() {
			return price;
		}

		public int getCount() {
			return count;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj==null){
				return false;
			}else if(this == obj){
		      return true;
		    }else if (obj instanceof EnoughReduceGoods) {
		    	EnoughReduceGoods other = (EnoughReduceGoods) obj;
		    	return  other.getGoodsId() == this.goodsId;
		    }
		    return false;
		}
	}
	
	public OrderEnoughReduceInfo(long enoughReduceId) {
		this.enoughReduceId = enoughReduceId;
	}

	public double getEnouhgPrice() {
		return enouhgPrice;
	}

	public void setEnouhgPrice(double enouhgPrice) {
		this.enouhgPrice = enouhgPrice;
	}

	public double getReducePrice() {
		return reducePrice;
	}

	public void setReducePrice(double reducePrice) {
		this.reducePrice = reducePrice;
	}

	public List<EnoughReduceGoods> getEnoughReduceGoodss() {
		return enoughReduceGoodss;
	}

	public void setEnoughReduceGoodss(List<EnoughReduceGoods> enoughReduceGoodss) {
		this.enoughReduceGoodss = enoughReduceGoodss;
	}

	public List<EnoughReduceGoods> getTkEnoughReduceGoodss() {
		return tkEnoughReduceGoodss;
	}

	public void setTkEnoughReduceGoodss(List<EnoughReduceGoods> tkEnoughReduceGoodss) {
		this.tkEnoughReduceGoodss = tkEnoughReduceGoodss;
	}

	public long getEnoughReduceId() {
		return enoughReduceId;
	}

	/**
	 * 获取参与满减活动的商品列表总额
	 * @param enoughReduceGoods 待过滤的商品列表
	 * @return
	 */
//	public double getOtherTotalPrice(List<EnoughReduceGoods> enoughReduceGoods) {
//		List<EnoughReduceGoods> list = new ArrayList<EnoughReduceGoods>();
//		list.addAll(enoughReduceGoodss);
//		
//		list.removeAll(enoughReduceGoods);//去除当前订单中的商品
//		
//		for(EnoughReduceGoods goods : list){
//			totalPrice += goods.price * goods.count;
//		}
//		return totalPrice;
//	}
	
	public double getTotalPrice() {
		for(EnoughReduceGoods goods : enoughReduceGoodss){
			totalPrice += CommUtil.mul(goods.price,goods.count);
		}
		return totalPrice;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}else if(this == obj){
	      return true;
	    }else if (obj instanceof OrderEnoughReduceInfo) {
	    	OrderEnoughReduceInfo other = (OrderEnoughReduceInfo) obj;
	    	return  other.getEnoughReduceId() == other.enoughReduceId;
	    }
	    return false;
	}

}
