package com.iskyshop.foundation.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: waitSynOrder.java
 * </p>
 * 
 * <p>
 * Description: 存储待同步的订单。
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
 * @author wuzhipeng
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "waitsynorder")
public class WaitSynOrder extends IdEntity {
	private int oms_interface; //0，为新增订单，1为修改订单。
	
	@Column(columnDefinition = "LongText")
	private String order_data; //订单详细信息
	@Column(unique=true)
	private String order_id; //订单Id  
	private Date updateTime;// 修改时间，这里为长时间格式
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getOms_interface() {
		return oms_interface;
	}
	public void setOms_interface(int oms_interface) {
		this.oms_interface = oms_interface;
	}
	public String getOrder_data() {
		return order_data;
	}
	public void setOrder_data(String order_data) {
		this.order_data = order_data;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
	
}
