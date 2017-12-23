package com.smilife.bcp.dto.request;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.framework.util.PropertyUtil;

/**
 * 支付取消请求对象
 * 
 * @author liz 2015-9-7
 */
public class OrderCancleReq {

	public static final String TYPE = "ORDER_CANCEL";

	public static final String ATT_KEY_NAME = "ORDER";

	@JSONField(name = "ORDER_ID")
	private String orderId; // 订单编码，编码规则：系统标识+YYYYMMDD+8位序列

	/**
	 * 退订支付方式，默认102：原方式(101：现金;102：原方式)
	 */
	@JSONField(name = "CANCEL_TYPE")
	private String cancelType; // 退订支付方式

	/**
	 * 退订方式101：部分退;102：全部退
	 */
	@JSONField(name = "CANCEL_CLASS")
	private String cancelClass; // 退订方式101：部分退;102：全部退

	@JSONField(name = "CANCEL_CHARGE")
	private String cancelCharge; // 退单金额，部分退时需要传

	@JSONField(name = "DESC")
	private String desc; // 取消原因

	@JSONField(name = "COMMODITY")
	private List<CommodityCancle> commodity; // 商品信息

	@JSONField(name = "STAFF_ID")
	private String staffId; // 受理员工

	@JSONField(name = "SYSTEM_ID")
	private String systemId; // 接入系统标识

	public OrderCancleReq() {
		staffId = PropertyUtil.getProperty("STAFF_ID");
		systemId = PropertyUtil.getProperty("SYSTEM_ID");
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCancelType() {
		return cancelType;
	}

	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
	}

	public String getCancelClass() {
		return cancelClass;
	}

	public void setCancelClass(String cancelClass) {
		this.cancelClass = cancelClass;
	}

	public String getCancelCharge() {
		return cancelCharge;
	}

	public void setCancelCharge(String cancelCharge) {
		this.cancelCharge = cancelCharge;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<CommodityCancle> getCommodity() {
		return commodity;
	}

	public void setCommodity(List<CommodityCancle> commodity) {
		this.commodity = commodity;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

}
