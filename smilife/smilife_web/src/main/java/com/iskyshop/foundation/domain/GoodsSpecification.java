package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: GoodsSpecification.java
 * </p>
 * 
 * <p>
 * Description: 商品规格管理类，商家可以自定义规格属性，并与商家主营类目及详细类目进行关联，商家发布商品时只显示与当前分类进行关联的规格
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
 * @author erikzhan、hezeng
 * 
 * @date 2014-10-22
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsspecification")
public class GoodsSpecification extends IdEntity {
	private String name;// 名称
	private int sequence;// 序号
	private String type;// 规格类型(文字或图片)
	@OneToMany(mappedBy = "spec")
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> properties = new ArrayList<GoodsSpecProperty>();//规格中定义的各规格值
	@Column(columnDefinition = "int default 0")
	private int spec_type;// 规格类型，0为平台自营规格，1为商家规格，商家规格时需要有对应的店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 当规格类型为商家规格时对应的店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass goodsclass;// 规格对应的主营商品分类,该分类为平台二级商品分类（level=1）。不会为null

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "spec_gc_detail", joinColumns = @JoinColumn(name = "spec_id"), inverseJoinColumns = @JoinColumn(name = "spec_gc_id"))
	private List<GoodsClass> spec_goodsClass_detail = new ArrayList<GoodsClass>();// 规格对应的详细商品分类,该分类为平台三级商品分类（level=2）。当此列表size()==0时，表示当前规格适用于字段goodsclass分类下的所有子分类，这时，不管后续再增加多少个goodsclass下的子分类，当前规格都会自动适用这些新增的子分类。
														//若此列表size()>0时，则表示当前规格只适用于此列表中的子分类。字段goodsclass分类下的其它子分类以及以后增加的子分类都不适用于当前规格。

	public GoodsSpecification() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GoodsSpecification(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}
	
	public GoodsClass getGoodsclass() {
		return goodsclass;
	}

	public void setGoodsclass(GoodsClass goodsclass) {
		this.goodsclass = goodsclass;
	}

	public List<GoodsClass> getSpec_goodsClass_detail() {
		return spec_goodsClass_detail;
	}

	public void setSpec_goodsClass_detail(
			List<GoodsClass> spec_goodsClass_detail) {
		this.spec_goodsClass_detail = spec_goodsClass_detail;
	}

	public int getSpec_type() {
		return spec_type;
	}

	public void setSpec_type(int spec_type) {
		this.spec_type = spec_type;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GoodsSpecProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GoodsSpecProperty> properties) {
		this.properties = properties;
	}

}
