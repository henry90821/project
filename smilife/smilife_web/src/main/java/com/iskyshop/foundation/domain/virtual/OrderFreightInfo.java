package com.iskyshop.foundation.domain.virtual;

import java.util.List;

/**
 * 订单运费信息
 * @author dengyuqi
 */
public class OrderFreightInfo{

	/**
	 * 运费模板id(静态模板为0)
	 */
	private long trans_id;
	
	/**
	 * 首件或首重或首立方米
	 */
	private int trans_weight;
	
	/**
	 * 首件或首重或首立方米单价
	 */
	private double trans_fee;
	
	/**
	 * 续件或续重或续立方米
	 */
	private int trans_add_weight;
	
	/**
	 * 续件或续重或续立方米单价
	 */
	private double trans_add_fee;
	
	/**
	 * 参加运费模板的商品列表
	 */
	private List<FreightGoods> goods_info;

	/**
	 * 参加运费模板的已退商品列表
	 */
	private List<Long> tk_goods_info;
	
	public OrderFreightInfo() {
		super();
	}

	public OrderFreightInfo(long trans_id) {
		super();
		this.trans_id = trans_id;
	}

	public int getTrans_weight() {
		return trans_weight;
	}

	public void setTrans_weight(int trans_weight) {
		this.trans_weight = trans_weight;
	}

	public double getTrans_fee() {
		return trans_fee;
	}

	public void setTrans_fee(double trans_fee) {
		this.trans_fee = trans_fee;
	}

	public int getTrans_add_weight() {
		return trans_add_weight;
	}

	public void setTrans_add_weight(int trans_add_weight) {
		this.trans_add_weight = trans_add_weight;
	}

	public double getTrans_add_fee() {
		return trans_add_fee;
	}

	public void setTrans_add_fee(double trans_add_fee) {
		this.trans_add_fee = trans_add_fee;
	}

	public List<FreightGoods> getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(List<FreightGoods> goods_info) {
		this.goods_info = goods_info;
	}

	public List<Long> getTk_goods_info() {
		return tk_goods_info;
	}

	public void setTk_goods_info(List<Long> tk_goods_info) {
		this.tk_goods_info = tk_goods_info;
	}

	public long getTrans_id() {
		return trans_id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}else if(this == obj){
	      return true;
	    }else if (obj instanceof OrderFreightInfo) {
	    	OrderFreightInfo other = (OrderFreightInfo) obj;
	    	return  other.getTrans_id() == this.trans_id;
	    }
	    return false;
	}
}
