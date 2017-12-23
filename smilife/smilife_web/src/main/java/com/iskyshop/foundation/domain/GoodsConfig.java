package com.iskyshop.foundation.domain;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.core.tools.CommUtil;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品配置表，包括身份证标识，同步url等信息
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_config")
public class GoodsConfig extends IdEntity {

	/**
	 * 普通商品
	 */
	public static final String	CODE_PTSP	= "ptsp";

	/**
	 * 拍拍购
	 */
	public static final String	CODE_PPG	= "ppg";

	/**
	 * 跨境通
	 */
	public static final String	CODE_KJT	= "kjt";

	/**
	 * 海信
	 */
	public static final String	CODE_HX		= "hx";

	/**
	 * 订单APP系统
	 */
	public static final String	CODE_DDAPP	= "ddapp";

	/**
	 * 配置编码，全局唯一
	 */
	@Column(columnDefinition = "varchar(64)", unique = true, nullable = false)
	private String				configCode;

	/**
	 * 同步名称：如海信订单同步，跨境通订单同步，可以是任意取名，但要有意义
	 */
	@Column(columnDefinition = "varchar(128)")
	private String				synName;

	/**
	 * 同步订单的url地址
	 */
	@Column(columnDefinition = "varchar(512)")
	private String				synIUrl;

	/**
	 * 是否需要身份证标识： 0 不需要， 1 需要，默认为0不需要
	 */
	@Column(columnDefinition = "int default 0")
	private int					needId;

	/**
	 * 是否需要商品编码：0 不需要商品编码，1 需要商品编码，默认为0不需要
	 */
	@Column(columnDefinition = "int default 0")
	private int					needGoodCode;

	/**
	 * 备注信息
	 */
	@Column(columnDefinition = "varchar(512)")
	private String				note;

	/**
	 * 图标URL
	 */
	@Column(columnDefinition = "varchar(256)")
	private String				iconUrl;

	/**
	 * 是否商品来源：0 不是，1 是
	 */
	@Column(columnDefinition = "int default 0")
	private int					goodsSource;

	@OneToMany(mappedBy = "goodsConfig")
	private List<Goods>			goodsList	= new ArrayList<Goods>();

	@ManyToMany(mappedBy = "goodsConfigList")
	private List<Store>			storeList	= new ArrayList<Store>();

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	public String getSynName() {
		return synName;
	}

	public void setSynName(String synName) {
		this.synName = synName;
	}

	public String getSynIUrl() {
		return synIUrl;
	}

	public void setSynIUrl(String synIUrl) {
		this.synIUrl = synIUrl;
	}

	public int getNeedId() {
		return needId;
	}

	public void setNeedId(int needId) {
		this.needId = needId;
	}

	public int getNeedGoodCode() {
		return needGoodCode;
	}

	public void setNeedGoodCode(int needGoodCode) {
		this.needGoodCode = needGoodCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public List<Goods> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<Goods> goodsList) {
		this.goodsList = goodsList;
	}

	public List<Store> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<Store> storeList) {
		this.storeList = storeList;
	}

	public int getGoodsSource() {
		return goodsSource;
	}

	public void setGoodsSource(int goodsSource) {
		this.goodsSource = goodsSource;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof GoodsConfig)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		GoodsConfig config = (GoodsConfig) o;
		if (this.getId() != null && this.getId().equals(config.getId())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return CommUtil.null2Int(this.getId());
	}

}
