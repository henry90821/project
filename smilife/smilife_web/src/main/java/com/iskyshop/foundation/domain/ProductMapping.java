package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 商品映射表
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "product_mapping")
public class ProductMapping extends IdEntity {

	/**
	 * 商品编码： 对应于海信系统中的商品编码
	 */
	@Column(columnDefinition = "varchar(128)")
	private String goodsCode;

	/**
	 * 备注信息
	 */
	@Column(columnDefinition = "varchar(256)")
	private String note;

	/**
	 * 商品Id：对应表iskyshop_goods中的ID
	 */
	@JoinColumn(name = "goodsId", columnDefinition = "bigint(20)")
	@OneToOne(fetch = FetchType.LAZY)
	private Goods goods;

	/**
	 * 同步订单的配置编码，关联表 iskyshop_goods_config的configCode
	 */
	// @Column(name = "configCode", columnDefinition = "varchar(64)", nullable = false)
	@JoinColumn(name = "configCode", referencedColumnName = "configCode", nullable = false, columnDefinition = "varchar(64)")
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsConfig goodsConfig;

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public GoodsConfig getGoodsConfig() {
		return goodsConfig;
	}

	public void setGoodsConfig(GoodsConfig goodsConfig) {
		this.goodsConfig = goodsConfig;
	}
}
