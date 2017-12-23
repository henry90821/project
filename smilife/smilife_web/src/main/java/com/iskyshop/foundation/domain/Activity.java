package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
 * Title: Activity.java
 * </p>
 * 
 * <p>
 * Description: 商城活动管理类,描述商城活动相关信息，商城活动由平台管理员发起，所有卖家都可以参加，参加的商品需要按照活动要求进行折扣， 审核后的商品在对应的活动页面显示
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "activity")
public class Activity extends IdEntity {
	private String ac_title;// 活动标题
	@Temporal(TemporalType.DATE)
	private Date ac_begin_time;// 开始时间
	@Temporal(TemporalType.DATE)
	private Date ac_end_time;// 结束时间
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Accessory ac_acc;// 标题页面横幅,主图片最佳尺寸：950X320
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Accessory ac_acc3;// 活动专题页右侧图片,最佳尺寸250X320
	private int ac_sequence;// 活动序号
	private int ac_status;// 活动状态，0为关闭，1为启动
	@Column(columnDefinition = "LongText")
	private String ac_content;// 活动说明
	@OneToMany(mappedBy = "act", cascade = CascadeType.REMOVE)
	private List<ActivityGoods> ags = new ArrayList<ActivityGoods>();// 活动商品/活动优惠券
	@Column(precision = 3, scale = 2)
	private BigDecimal ac_rebate;// 铜牌会员折扣率
	@Column(precision = 3, scale = 2)
	private BigDecimal ac_rebate1;// 银牌会员折扣率
	@Column(precision = 3, scale = 2)
	private BigDecimal ac_rebate2;// 金牌会员折扣率
	@Column(precision = 3, scale = 2)
	private BigDecimal ac_rebate3;// 超级会员折扣率

	@Column(name="ac_type",columnDefinition = "int default 0")
	private int acType; // 活动类型，0为商品活动，1为优惠券活动(活动关联的优惠券会在PC端对应店铺的首页中展示以供用户领取或在wap端的商品详情页中显示以供用户领取),2为优惠券包(打包多个优惠券供用户一次性领取)
	@ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "activity_user", joinColumns = @JoinColumn(name = "activity_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users=new ArrayList<User>();//活动参加的用户列表

	public Activity(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Activity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getAc_rebate1() {
		return ac_rebate1;
	}

	public void setAc_rebate1(BigDecimal ac_rebate1) {
		this.ac_rebate1 = ac_rebate1;
	}

	public BigDecimal getAc_rebate2() {
		return ac_rebate2;
	}

	public void setAc_rebate2(BigDecimal ac_rebate2) {
		this.ac_rebate2 = ac_rebate2;
	}

	public BigDecimal getAc_rebate3() {
		return ac_rebate3;
	}

	public void setAc_rebate3(BigDecimal ac_rebate3) {
		this.ac_rebate3 = ac_rebate3;
	}

	public Accessory getAc_acc3() {
		return ac_acc3;
	}

	public void setAc_acc3(Accessory ac_acc3) {
		this.ac_acc3 = ac_acc3;
	}

	public List<ActivityGoods> getAgs() {
		return ags;
	}

	public void setAgs(List<ActivityGoods> ags) {
		this.ags = ags;
	}

	public String getAc_title() {
		return ac_title;
	}

	public void setAc_title(String ac_title) {
		this.ac_title = ac_title;
	}

	public Date getAc_begin_time() {
		return ac_begin_time;
	}

	public void setAc_begin_time(Date ac_begin_time) {
		this.ac_begin_time = ac_begin_time;
	}

	public Date getAc_end_time() {
		return ac_end_time;
	}

	public void setAc_end_time(Date ac_end_time) {
		this.ac_end_time = ac_end_time;
	}

	public Accessory getAc_acc() {
		return ac_acc;
	}

	public void setAc_acc(Accessory ac_acc) {
		this.ac_acc = ac_acc;
	}

	public int getAc_sequence() {
		return ac_sequence;
	}

	public void setAc_sequence(int ac_sequence) {
		this.ac_sequence = ac_sequence;
	}

	public int getAc_status() {
		return ac_status;
	}

	public void setAc_status(int ac_status) {
		this.ac_status = ac_status;
	}

	public String getAc_content() {
		return ac_content;
	}

	public void setAc_content(String ac_content) {
		this.ac_content = ac_content;
	}

	public BigDecimal getAc_rebate() {
		return ac_rebate;
	}

	public void setAc_rebate(BigDecimal ac_rebate) {
		this.ac_rebate = ac_rebate;
	}

	public int getAcType() {
		return acType;
	}

	public void setAcType(int acType) {
		this.acType = acType;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
