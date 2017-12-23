package com.smilife.bcp.dto.request;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 订单取消商品信息
 * @author liz
 * 2015-9-7
 */
public class CommodityCancle implements Serializable {

	private static final long serialVersionUID = -2060350697440611356L;

	/**
	 * 商品编码2D：1000;3D：1001;IMAX：1002;VIP：1003;卖品直接传商品编码;普通商品编码直接传商品编码
	 */
	@JSONField(name = "COMMODITY_ID")
	private String commodityId;

	@JSONField(name = "COMMODITY_NAME")
	private String commodityName; // 商品名称

	@JSONField(name = "PRICE")
	private String price; // 商品折后价，单位：元

	@JSONField(name = "COMMODITY_NUM")
	private String commodityNum; // 商品数量

	@JSONField(name = "DISCOUNT")
	private String discount; // 商品折后价，单位：元

	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCommodityNum() {
		return commodityNum;
	}

	public void setCommodityNum(String commodityNum) {
		this.commodityNum = commodityNum;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

}
