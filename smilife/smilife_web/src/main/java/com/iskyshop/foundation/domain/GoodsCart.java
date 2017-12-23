package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.core.tools.CommUtil;

/**
 * <p>
 * Title: GoodsCart.java
 * </p>
 * <p>
 * Description: * 商城购物车类， ，购物车信息直接保存到数据库中
 * ，未登录用户根据随机唯一Id保存（包括手机端），已经登录的用户根据User来保存
 * ，未登录用户购物车间隔1天自动删除（包括手机端），已经登录用户购物车保存7天 ，7天未提交为订单自动删除,购物车信息存在及时性，不加入缓存管理
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author hezeng
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodscart")
public class GoodsCart extends IdEntity {
	@ManyToOne
	private Goods					goods;											// 对应的商品
	private int						count;											// 数量
	@Column(precision = 12, scale = 2)
	private BigDecimal				price;											// 单价。当购物车中包含的是组合中的主体商品时，此字段的值即为组合的最终价格（但经走读代码，发现取组合的最终价格是从combin_suit_info的suit_all_price中取得的）（我觉得应该尽量统一，从此字段取价格）
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "gsp_id"))
	private List<GoodsSpecProperty>	gsps	= new ArrayList<GoodsSpecProperty>();	// 对应的规格属性
	@Lob
	@Column(columnDefinition = "LongText")
	private String					spec_info;										// 具体的规格属性内容，如“开门方式：三开门<br>颜色：白色<br>”
	private String					cart_type;										// // 组合销售时候为"combin"，团购商品为“group”，秒杀商品为“seckill”，其它情况为null
	private String					cart_gsp;										// 对应的规格属性的id列表（以英文逗号分隔），对应gsps和spec_info的内容
	@Column(columnDefinition = "int default 0")
	private int						combin_main;									// 1为套装主购物车，0为其他套装购物车
	private String					combin_mark;									// 组合套装标识，为一随机数类的字符串。购买套装时，一个套装中的套装标识为相同
	private String					combin_suit_ids;								// 组合套装购物车id（不包括当前这个购物车）,当购物车为套装主购物车时有值
	@Column(columnDefinition = "LongText")
	private String					combin_suit_info;								// 套装购物车详情（不包括当前这个购物车对应商品的详情），使用json管理，{"all_goods_price":"221.10","plan_goods_price":"160","goods_list":[{"id":9,"price":100.0,"inventory":1114,"store_price":100.0,"name":"自营女童装1","img":"http://192.168.68.23/shopftpuser/upload/system/self_goods/079ebe17-e7c1-4225-b1d2-8d0013f1dd95.png_small.png","url":"http://192.168.22.56:8080/smilife_web/goods_9.htm"},
																					// {"id":23,"price":111.0,"inventory":3999,"store_price":111.0,"name":"自营女童装11","img":"http://192.168.68.23/ftpplatform/upload/system/self_goods/bf28bfd6-3de6-4ddc-9a07-14c54bb6a35b.png_small.png","url":"http://192.168.22.56:8080/smilife_web/goods_23.htm"},
																					// {"id":10,"price":0.1,"inventory":11167,"store_price":0.1,"name":"自营女童装2","img":"http://192.168.68.23/shopftpuser/upload/system/self_goods/df432b98-5324-4329-8b77-224ba403cf9a.png_small.png","url":"http://192.168.22.56:8080/smilife_web/goods_10.htm"}],"suit_count":1,"suit_all_price":"160.00"}
	private String					combin_version;								// 组合套装版本，当选择同一个主商品的套装时，分为[套装1]、[套装2]
	@ManyToOne
	private User					user;											// 对应的购物车用户
	private String					cart_session_id;								// 未登录用户会话Id
	private String					cart_mobile_id;								// 手机端未登录用户会话Id
	@Column(columnDefinition = "LongText")
	private String					gift_info;										// 买就送赠品信息
																					// 采用json管理{"goods_id":"1","goods_name";"阿迪达斯运动鞋"}
	@Column(columnDefinition = "int default 0")
	private int						whether_choose_gift;							// 是否选择了赠品
																					// 默认为0
																					// 未选择
																					// 1为已选择（大头注：好像没起作用。我选择了赠品，但此字段仍然显示为0??????????????）
	@Column(columnDefinition = "int default 0")
	private int						cart_status;									// 用户购物车状态，0表示没有提交为订单，1表示已经提交为订单，已经提交为订单信息的不再为缓存购物车，同时定时器也不进行删除操作

	public String getCombin_version() {
		return combin_version;
	}

	public void setCombin_version(String combin_version) {
		this.combin_version = combin_version;
	}

	public GoodsCart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GoodsCart(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public String getCart_gsp() {
		return cart_gsp;
	}

	public void setCart_gsp(String cart_gsp) {
		this.cart_gsp = cart_gsp;
	}

	public int getWhether_choose_gift() {
		return whether_choose_gift;
	}

	public void setWhether_choose_gift(int whether_choose_gift) {
		this.whether_choose_gift = whether_choose_gift;
	}

	public String getCombin_suit_info() {
		return combin_suit_info;
	}

	public void setCombin_suit_info(String combin_suit_info) {
		this.combin_suit_info = combin_suit_info;
	}

	public int getCombin_main() {
		return combin_main;
	}

	public void setCombin_main(int combin_main) {
		this.combin_main = combin_main;
	}

	public String getCombin_mark() {
		return combin_mark;
	}

	public void setCombin_mark(String combin_mark) {
		this.combin_mark = combin_mark;
	}

	public String getCombin_suit_ids() {
		return combin_suit_ids;
	}

	public void setCombin_suit_ids(String combin_suit_ids) {
		this.combin_suit_ids = combin_suit_ids;
	}

	public String getGift_info() {
		return gift_info;
	}

	public void setGift_info(String gift_info) {
		this.gift_info = gift_info;
	}

	public int getCart_status() {
		return cart_status;
	}

	public void setCart_status(int cart_status) {
		this.cart_status = cart_status;
	}

	public String getCart_mobile_id() {
		return cart_mobile_id;
	}

	public void setCart_mobile_id(String cart_mobile_id) {
		this.cart_mobile_id = cart_mobile_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCart_session_id() {
		return cart_session_id;
	}

	public void setCart_session_id(String cart_session_id) {
		this.cart_session_id = cart_session_id;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<GoodsSpecProperty> getGsps() {
		return gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCart_type() {
		return cart_type;
	}

	public void setCart_type(String cart_type) {
		this.cart_type = cart_type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof GoodsCart)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		GoodsCart gc = (GoodsCart) o;
		if (this.getId() != null && this.getId().equals(gc.getId())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return CommUtil.null2Int(this.getId());
	}
}
