package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 商品库存信息： 来自海信系统中商品的库存信息：影城-商品-库存<br/>
 * Created by 亚翔 on 2015/11/9.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_inventory")
public class GoodsInventory extends IdEntity {

	/**
	 * 数据同步时的批次号
	 */
	@Column(name = "sequence_no", columnDefinition = "varchar(16)", nullable = false)
	private String		sequenceNo;

	/**
	 * 影院编码，关联iskyshop_cinema表的主键
	 */
	@JoinColumn(name = "sa_code", columnDefinition = "varchar(128)", nullable = false, referencedColumnName = "sa_code")
	@ManyToOne(fetch = FetchType.LAZY)
	private ShipAddress	shipAddress;

	/**
	 * 商品编码： 对应于海信系统中的商品编码
	 */
	@Column(name = "goods_code", columnDefinition = "varchar(64)", nullable = false)
	private String		goodsCode;

	/**
	 * 商品库存数
	 */
	@Column(name = "inventory", nullable = false)
	private Integer		inventory;

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public ShipAddress getShipAddress() {
		return shipAddress;
	}

	public void setShipAddress(ShipAddress shipAddress) {
		this.shipAddress = shipAddress;
	}

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
}
