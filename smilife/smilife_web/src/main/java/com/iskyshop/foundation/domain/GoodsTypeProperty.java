package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: GoodsTypeProperty.java
 * </p>
 * 
 * <p>
 * Description: 商品类型属性
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
 * @author erikzhang
 * 
 * @date 2014-4-25
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstypeproperty")
public class GoodsTypeProperty extends IdEntity {
	private int sequence;// 属性排序
	private String name;// 属性名称
	@Column(columnDefinition = "LongText")
	private String value;// 属性可选值
	private boolean display;// 属性可见性
	@Column(columnDefinition = "LongText")
	private String hc_value;// 互斥属性值
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;// 商品类型
    private Long store_id;//店铺id
	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public GoodsTypeProperty(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public GoodsTypeProperty() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getHc_value() {
		return hc_value;
	}

	public void setHc_value(String hc_value) {
		this.hc_value = hc_value;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GoodsType getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

}
