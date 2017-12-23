package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ActivityGoods.java
 * </p>
 * 
 * <p>
 * Description: 商城活动商品类,参加活动的商品提交到该类中进行管理，审核后通过后的商品在活动页显示，同时活动过期后系统定时器自动将处理活动商品
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "activity_goods")
public class ActivityGoods extends IdEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private Goods ag_goods;// 活动商品
	private int ag_status;// 活动商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2为已经到期关闭
	@ManyToOne(fetch = FetchType.LAZY)
	private User ag_admin;// 活动审核管理员
	@ManyToOne(fetch = FetchType.LAZY)
	private Activity act;// 对应的活动
	@Column(columnDefinition = "int default 0")
	private int ag_type;// 活动商品类型，0为商家活动商品，1为平台自营活动商品
	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 推荐时间，
	@Column(columnDefinition = "int default 0")
	private int weixin_recommend;// 微信端推荐， 1为推荐，推荐后在微信端首页显示
	@Temporal(TemporalType.DATE)
	private Date weixin_recommendTime;// 微信推荐时间，

	@Column(name = "ac_type", columnDefinition = "int default 0")
	private int acType; // 活动类型，0为商品活动，1为优惠券活动(活动关联的优惠券会在PC端对应店铺的首页中展示以供用户领取或在wap端的商品详情页中显示以供用户领取),2为优惠券包(打包多个优惠券供用户一次性领取)
	@OneToOne(fetch = FetchType.LAZY)
	private Coupon coupon; // 活动优惠券

	public ActivityGoods(Long id, Date addTime) {
		super(id, addTime);
	}

	public ActivityGoods() {
		super();
	}

	public int getWeixin_recommend() {
		return weixin_recommend;
	}

	public void setWeixin_recommend(int weixin_recommend) {
		this.weixin_recommend = weixin_recommend;
	}

	public Date getWeixin_recommendTime() {
		return weixin_recommendTime;
	}

	public void setWeixin_recommendTime(Date weixin_recommendTime) {
		this.weixin_recommendTime = weixin_recommendTime;
	}

	public int getMobile_recommend() {
		return mobile_recommend;
	}

	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public int getAg_type() {
		return ag_type;
	}

	public void setAg_type(int ag_type) {
		this.ag_type = ag_type;
	}

	public Activity getAct() {
		return act;
	}

	public void setAct(Activity act) {
		this.act = act;
	}

	public Goods getAg_goods() {
		return ag_goods;
	}

	public void setAg_goods(Goods ag_goods) {
		this.ag_goods = ag_goods;
	}

	public int getAg_status() {
		return ag_status;
	}

	public void setAg_status(int ag_status) {
		this.ag_status = ag_status;
	}

	public User getAg_admin() {
		return ag_admin;
	}

	public void setAg_admin(User ag_admin) {
		this.ag_admin = ag_admin;
	}

	public int getAcType() {
		return acType;
	}

	public void setAcType(int acType) {
		this.acType = acType;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
}
