package com.iskyshop.foundation.domain;

import java.util.Date;

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
 * 订单日志表<br/>
 * Created by 亚翔 on 2015/11/9.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "order_syn_log")
public class OrderSynLog extends IdEntity {

	/**
	 * 订单ID,管理表iskyshop_orderform的id字段
	 */
	@JoinColumn(name = "order_id", columnDefinition = "bigint(20)", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm orderForm;

	/**
	 * 同步状态： 0 未同步， 1同步成 2 同步失败 3 同步中
	 */
	@Column(name = "syn_status", columnDefinition = "int default 0", nullable = false)
	private Integer synStatus;

	/**
	 * 同步操作者： 如果是后台调度器，该字段为 scheduler， 否则为操作人用的username
	 */
    @Column(name = "syn_user", columnDefinition = "varchar(64) default 'scheduler'")
	private String synUser;

	/**
	 * 开始同步时间
	 */
	@Column(name = "syn_time", columnDefinition = "datetime", nullable = false)
	private Date synTime;

	/**
	 * 失败描述： 如果失败，填写失败原因，成功则不填
	 */
	@Column(name = "fail_info", columnDefinition = "text")
	private String failInfo;

	/**
	 * 订单信息，已json或者xml格式存储
	 */
	@Column(name = "order_info", columnDefinition = "text")
	private String orderInfo;

	/**
	 * 同步订单的URL
	 */
	@Column(columnDefinition = "varchar(512)")
	private String synIUrl;

	public OrderForm getOrderForm() {
		return orderForm;
	}

	public void setOrderForm(OrderForm orderForm) {
		this.orderForm = orderForm;
	}

	public Integer getSynStatus() {
		return synStatus;
	}

	public void setSynStatus(Integer synStatus) {
		this.synStatus = synStatus;
	}

	public String getSynUser() {
		return synUser;
	}

	public void setSynUser(String synUser) {
		this.synUser = synUser;
	}

	public Date getSynTime() {
		return synTime;
	}

	public void setSynTime(Date synTime) {
		this.synTime = synTime;
	}

	public String getFailInfo() {
		return failInfo;
	}

	public void setFailInfo(String failInfo) {
		this.failInfo = failInfo;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getSynIUrl() {
		return synIUrl;
	}

	public void setSynIUrl(String synIUrl) {
		this.synIUrl = synIUrl;
	}
}
