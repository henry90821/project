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
 * Title: Address.java
 * </p>
 * 
 * <p>
 * Description: 用户收货地址类，用来管理所有买家的收货地址信息，买家在提交订单时候可以选择这里的收货地址
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "address")
public class Address extends IdEntity {
	private String	trueName;		// 收货人姓名
	@ManyToOne(fetch = FetchType.LAZY)
	private Area	area;			// 收货人所在地区（一般是第三级，如果广东深圳下的福田区）
	private String	area_info;		// 收货人详细地址
	private String	zip;			// 邮政编码
	private String	telephone;		// 联系电话
	private String	mobile;			// 手机号码
	@ManyToOne(fetch = FetchType.LAZY)
	private User	user;			// 地址对应的用户
	@Column(columnDefinition = "int default 0")
	private int		default_val;	// 是否为默认收货地址，1为默认地址

	private String	card;			// 收货身份证

	private Double	longitude;		// 经纬度由后台程序自动计算，用于下单时选择星美门店。在用户界面不显示
	private Double	latitude;

	public Address(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Address() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getDefault_val() {
		return default_val;
	}

	public void setDefault_val(int default_val) {
		this.default_val = default_val;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getArea_info() {
		return area_info;
	}

	public void setArea_info(String area_info) {
		this.area_info = area_info;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

}
