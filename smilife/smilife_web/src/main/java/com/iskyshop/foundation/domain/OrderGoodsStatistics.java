package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 订单商品统计
 * @author dengyuqi
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ordergoods_statistics")
public class OrderGoodsStatistics extends IdEntity {
	
	/**
	 * 订单号
	 */
	@Column(name="order_id")
	private String orderId;
	
	/**
	 * 商品id
	 */
	@Column(name="goods_id")
	private Long goodsId;
	
	/**
	 * 商品单价
	 */
	@Column(name="goods_price",precision = 12, scale = 2)
	private BigDecimal goodsPrice;
	
	/**
	 * 商品所属一级分类id
	 */
	@Column(name="first_gc_id")
	private Long firstGcId;
	
	/**
	 * 商品所属二级分类id
	 */
	@Column(name="second_gc_id")
	private Long secondGcId;
	
	/**
	 * 商品所属三级分类id
	 */
	@Column(name="thrid_gc_id")
	private Long thridGcId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public BigDecimal getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(BigDecimal goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public Long getFirstGcId() {
		return firstGcId;
	}

	public void setFirstGcId(Long firstGcId) {
		this.firstGcId = firstGcId;
	}

	public Long getSecondGcId() {
		return secondGcId;
	}

	public void setSecondGcId(Long secondGcId) {
		this.secondGcId = secondGcId;
	}

	public Long getThridGcId() {
		return thridGcId;
	}

	public void setThridGcId(Long thridGcId) {
		this.thridGcId = thridGcId;
	}
}
