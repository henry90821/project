package com.iskyshop.foundation.domain.virtual;

/**
 * 
 * @author dengyuqi
 *
 */
public class FreightGoods {
	/**
	 * 商品ID
	 */
	private long goods_id;
	/**
	 * 总件数或总重量或总立方米
	 */
	private double count;
	
	private double price;

	public FreightGoods() {
		super();
	}

	public FreightGoods(long goods_id) {
		super();
		this.goods_id = goods_id;
	}

//	public FreightGoods(long goods_id, double count) {
//		super();
//		this.goods_id = goods_id;
//		this.count = count;
//	}

	public long getGoods_id() {
		return goods_id;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}else if(this == obj){
	      return true;
	    }else if (obj instanceof FreightGoods) {
	    	FreightGoods other = (FreightGoods) obj;
	    	return  other.getGoods_id() == this.goods_id;
	    }
	    return false;
	}
}
