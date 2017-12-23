package com.smilife.bcp.dto.request;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.tydic.framework.util.PropertyUtil;

public class Commodity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2060350697440611356L;

	@JSONField(name = "COMMODITY_ID")
	private String commodityId;// 靓号或套餐预存：-1 合约预存：对应合约机规则标识

	/**
	 * 商品类型 1靓号 2套餐 3合约机 4普通商品
	 */
	@JSONField(name = "COMMODITY_TYPE")
	private String commodityType;

	@JSONField(name = "COMMODITY_NAME")
	private String commodityName; // 商品名称

	@JSONField(name = "COMMODITY_NUM")
	private String commodityNum; // 商品数量

	@JSONField(name = "DISCOUNT_CHARGE")
	private String discountCharge; // 商品折后价，单位：元

	@JSONField(name = "PRICE")
	private String price; // 商品折后价，单位：元

	public Commodity() {
		commodityId = PropertyUtil.getProperty("COMMODITY_ID");
	}

	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(String commodityType) {
		this.commodityType = commodityType;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityNum() {
		return commodityNum;
	}

	public void setCommodityNum(String commodityNum) {
		this.commodityNum = commodityNum;
	}

	public String getDiscountCharge() {
		return discountCharge;
	}

	public void setDiscountCharge(String discountCharge) {
		this.discountCharge = discountCharge;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}
