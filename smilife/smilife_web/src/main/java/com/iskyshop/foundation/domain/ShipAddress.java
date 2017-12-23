package com.iskyshop.foundation.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * 发货地址管理类，用来管理发货地址，无论自营商品、商家商品在设置发货时，需要选择发货地址及快递信息才可以设置发货， 该地址用在快递跟踪订阅、快递运单打印
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ship_address")
public class ShipAddress extends IdEntity {
	private String sa_name;// 发货地址名称
	@Column(nullable = false)
	private Long province_area_id; // 省份ID， 关联表iskyshop_area
	@Column(nullable = false)
	private Long city_area_id; // 城市ID， 关联表iskyshop_area
	@Column(nullable = false)
	private Long sa_area_id;// 发货地址区域id，对应系统区域管理类Area
	@Column(columnDefinition = "int default 0")
	private int sa_type;// 发货地址类型，0为商家发货地址，1为管理员发货地址,默认为0
	private Long sa_user_id;// 此发货地址的属主（商家或平台用户）。注意：子账号新增的发货地址中的此字段值应该为店铺主账号的id，而不能是子账号的Id
	private String sa_user_name;// 新增此发货地址的用户。注意：若发货地址是子账号新增的，则此字段就保存此子账号的名称
	private String sa_addr;// 发货地址的详细信息，指的是完整地址
	@Column(columnDefinition = "int default 0")
	private int sa_default;// 是否默认发货地址,1为默认
	@Column(columnDefinition = "int default 0")
	private int sa_sequence;// 发货地址序号，按照升序排列
	private String sa_user;// 发货人姓名
	private String sa_telephone;// 发货人联系电话
	private String sa_company;// 发货公司
	private String sa_zip;// 发货人区号或者邮编

	@Column(columnDefinition = "varchar(128)")
	private String sa_code; // 发货地址编码
	private String cinema_id; // 影院ID
	@Column(name = "longitude", columnDefinition = "float(18,15)")
	private Float longitude; // 影院地址：经度
	@Column(name = "latitude", columnDefinition = "float(18,15)")
	private Float latitude; // 影院地址：纬度
	@Column(columnDefinition = "int default 0")
	private int sa_address_type; // 线上线下发货地址类型，0表示为普通发货地址，1表示线下门店发货地址，默认值为0

	@OneToMany(mappedBy = "shipAddress", fetch = FetchType.LAZY)
	private List<GoodsInventory> goodsInventoryList;

	@OneToMany(mappedBy = "shipAddress", fetch = FetchType.LAZY)
	private List<GoodsInventoryLog> goodsInventoryLogList;
	
	@Column(columnDefinition = "int default 0")
	private int o2oCapable; // 0：不支持o2o配送（默认不支持）； 1：支持o2o配送   
	
	@Column(columnDefinition = "int default 0")
	private int needSyn;//0:不需要同步海信库存；1：需要同步海信库存

	

	public ShipAddress() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShipAddress(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}
	
	public ShipAddress(Long id, String sa_code) {
		this.setId(id);
		this.setSa_code(sa_code);
	}

	public String getSa_zip() {
		return sa_zip;
	}

	public void setSa_zip(String sa_zip) {
		this.sa_zip = sa_zip;
	}

	public String getSa_user() {
		return sa_user;
	}

	public void setSa_user(String sa_user) {
		this.sa_user = sa_user;
	}

	public String getSa_telephone() {
		return sa_telephone;
	}

	public void setSa_telephone(String sa_telephone) {
		this.sa_telephone = sa_telephone;
	}

	public String getSa_company() {
		return sa_company;
	}

	public void setSa_company(String sa_company) {
		this.sa_company = sa_company;
	}

	public String getSa_name() {
		return sa_name;
	}

	public void setSa_name(String sa_name) {
		this.sa_name = sa_name;
	}

	public int getSa_default() {
		return sa_default;
	}

	public void setSa_default(int sa_default) {
		this.sa_default = sa_default;
	}

	public int getSa_sequence() {
		return sa_sequence;
	}

	public void setSa_sequence(int sa_sequence) {
		this.sa_sequence = sa_sequence;
	}

	public Long getSa_area_id() {
		return sa_area_id;
	}

	public void setSa_area_id(Long sa_area_id) {
		this.sa_area_id = sa_area_id;
	}

	public int getSa_type() {
		return sa_type;
	}

	public void setSa_type(int sa_type) {
		this.sa_type = sa_type;
	}

	public Long getSa_user_id() {
		return sa_user_id;
	}

	public void setSa_user_id(Long sa_user_id) {
		this.sa_user_id = sa_user_id;
	}

	public String getSa_user_name() {
		return sa_user_name;
	}

	public void setSa_user_name(String sa_user_name) {
		this.sa_user_name = sa_user_name;
	}

	public String getSa_addr() {
		return sa_addr;
	}

	public void setSa_addr(String sa_addr) {
		this.sa_addr = sa_addr;
	}

	public Long getProvince_area_id() {
		return province_area_id;
	}

	public void setProvince_area_id(Long province_area_id) {
		this.province_area_id = province_area_id;
	}

	public Long getCity_area_id() {
		return city_area_id;
	}

	public void setCity_area_id(Long city_area_id) {
		this.city_area_id = city_area_id;
	}

	public String getSa_code() {
		return sa_code;
	}

	public void setSa_code(String sa_code) {
		this.sa_code = sa_code;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public int getSa_address_type() {
		return sa_address_type;
	}

	public void setSa_address_type(int sa_address_type) {
		this.sa_address_type = sa_address_type;
	}

	public List<GoodsInventory> getGoodsInventoryList() {
		return goodsInventoryList;
	}

	public void setGoodsInventoryList(List<GoodsInventory> goodsInventoryList) {
		this.goodsInventoryList = goodsInventoryList;
	}

	public List<GoodsInventoryLog> getGoodsInventoryLogList() {
		return goodsInventoryLogList;
	}

	public void setGoodsInventoryLogList(List<GoodsInventoryLog> goodsInventoryLogList) {
		this.goodsInventoryLogList = goodsInventoryLogList;
	}

	public int getO2oCapable() {
		return o2oCapable;
	}

	public void setO2oCapable(int o2oCapable) {
		this.o2oCapable = o2oCapable;
	}
	
	public int getNeedSyn() {
		return needSyn;
	}

	public void setNeedSyn(int needSyn) {
		this.needSyn = needSyn;
	}

	public String getCinema_id() {
		return cinema_id;
	}

	public void setCinema_id(String cinema_id) {
		this.cinema_id = cinema_id;
	}
	
	
}
