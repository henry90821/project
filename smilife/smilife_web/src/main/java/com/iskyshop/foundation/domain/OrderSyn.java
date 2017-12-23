package com.iskyshop.foundation.domain;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单同步表<br/>
 * Created by 亚翔 on 2015/11/9.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "order_syn")
public class OrderSyn extends IdEntity {
	public static final String SYN_USER_DEFAULT_VALUE = "scheduler";

	/**
	 * 订单ID,管理表iskyshop_orderform的id字段
	 */
	@JoinColumn(name = "order_id", columnDefinition = "bigint(20)", nullable = false)
	@ManyToOne
	@Fetch(value = FetchMode.SELECT)
	private OrderForm orderForm;

	/**
	 * 同步状态： 0 未同步， 1同步成功 2 同步失败 3 同步中
	 */
	@Column(name = "syn_status", columnDefinition = "int default 0")
	private int synStatus;

	/**
	 * 同步次数：最多同步3次，失败告警
	 */
	@Column(name = "syn_count", columnDefinition = "int default 0")
	private int synCount;

	/**
	 * 同步描述： 如果失败，填写失败原因，成功则不填
	 */
	@Column(name = "syn_desc", columnDefinition = "varchar(512)")
	private String synDesc;

	/**
	 * 同步操作者： 如果是后台调度器，该字段为 scheduler， 否则为操作人用的username
	 */
	@Column(name = "syn_user", columnDefinition = "varchar(64) default 'scheduler'")
	private String synUser;

	/**
	 * 开始同步时间，新增数据时不需要填充次字段的值，每次进行数据同步时需要更新此字段的值
	 */
	@Column(name = "syn_time", columnDefinition = "datetime", insertable = false)
	private Date synTime;

	/**
	 * 商品配置属性编码，关联表iskyshop_Good_config表的configCode字段
	 */
	@JoinColumn(name = "config_code", referencedColumnName = "configCode", columnDefinition = "varchar(64)", nullable = false)
	@ManyToOne
	private GoodsConfig goodsConfig;

	/**
	 * 订单信息，已json或者xml格式存储
	 */
	@Column(name = "order_info", columnDefinition = "text")
	private String orderInfo;

	public OrderForm getOrderForm() {
		return orderForm;
	}

	public void setOrderForm(OrderForm orderForm) {
		this.orderForm = orderForm;
	}

	public int getSynStatus() {
		return synStatus;
	}

	public void setSynStatus(int synStatus) {
		this.synStatus = synStatus;
	}

	public int getSynCount() {
		return synCount;
	}

	public void setSynCount(int synCount) {
		this.synCount = synCount;
	}

	public String getSynDesc() {
		return synDesc;
	}

	public void setSynDesc(String synDesc) {
		this.synDesc = synDesc;
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

	public GoodsConfig getGoodsConfig() {
		return goodsConfig;
	}

	public void setGoodsConfig(GoodsConfig goodsConfig) {
		this.goodsConfig = goodsConfig;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
}
