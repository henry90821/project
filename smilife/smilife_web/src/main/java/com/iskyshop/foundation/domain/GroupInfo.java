package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
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
 * Title: GroupInfo.java
 * </p>
 * 
 * <p>
 * Description: 生活类商品团购消费码类，如：电影票消费码
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
 * @author jinxinzhe
 * 
 * @date 2014-5-8
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "groupinfo")
public class GroupInfo extends IdEntity {
	private String group_sn;// 团购编号
	private Long user_id;// 团购拥有的用户id
	private String user_mobile; // 用户手机号,add 2015-09-28 需求#61 by liuzheng
	private String user_name;// 用户名
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupLifeGoods lifeGoods;// 对应的生活类团购商品
	@Column(columnDefinition = "int default 0")
	private int status;// 团购信息状态，默认为0，,使用后为1,-1为过期,3申请退款、5退款中、7退款完成、9退款请求已提交，等到银行退款（银联）
	@ManyToOne
	private Payment payment;// 支付方式
	private Long order_id;// 对应的订单id
	private Date refund_Time;// 买家退款截止日期
	private Date refund_complete_Time;// 退款时间
	@Column(columnDefinition = "LongText")
	private String refund_reasion;// 退款说明
	
	@Column(name = "alipay_batch_no")
	private String alipayBatchNo; // 支付宝退款批次号

	@Column(name = "return_remark")
	private String returnRemark; // 退款备注
	
	private String refundPayCenterNo; // 请求支付中心退款流水号
	
	@Column(name = "amount", precision = 12, scale = 2)
	private BigDecimal amount;// 实际退款金额

	public GroupInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupInfo(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Date getRefund_complete_Time() {
		return refund_complete_Time;
	}

	public void setRefund_complete_Time(Date refund_complete_Time) {
		this.refund_complete_Time = refund_complete_Time;
	}

	public String getRefund_reasion() {
		return refund_reasion;
	}

	public void setRefund_reasion(String refund_reasion) {
		this.refund_reasion = refund_reasion;
	}

	public Date getRefund_Time() {
		return refund_Time;
	}

	public void setRefund_Time(Date refund_Time) {
		this.refund_Time = refund_Time;
	}

	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public String getGroup_sn() {
		return group_sn;
	}

	public void setGroup_sn(String group_sn) {
		this.group_sn = group_sn;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public GroupLifeGoods getLifeGoods() {
		return lifeGoods;
	}

	public void setLifeGoods(GroupLifeGoods lifeGoods) {
		this.lifeGoods = lifeGoods;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getUser_mobile() {
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}

	public String getAlipayBatchNo() {
		return alipayBatchNo;
	}

	public void setAlipayBatchNo(String alipayBatchNo) {
		this.alipayBatchNo = alipayBatchNo;
	}

	public String getReturnRemark() {
		return returnRemark;
	}

	public void setReturnRemark(String returnRemark) {
		this.returnRemark = returnRemark;
	}

	public String getRefundPayCenterNo() {
		return refundPayCenterNo;
	}

	public void setRefundPayCenterNo(String refundPayCenterNo) {
		this.refundPayCenterNo = refundPayCenterNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	
}
