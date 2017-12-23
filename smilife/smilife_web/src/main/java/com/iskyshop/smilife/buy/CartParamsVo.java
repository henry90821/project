package com.iskyshop.smilife.buy;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 購物車接口入參
 * @author herendian
 * @version 1.0
 * @date 2016年3月29日 上午11:29:26
 */
@JsonSerialize
@ApiModel
public class CartParamsVo {
	@ApiModelProperty("Token")
	private String cartSession;
	
	@ApiModelProperty("收货地址id")
	private String addrId;
	
	@ApiModelProperty("配送时间")
	private String deliveryTime;
	
	@ApiModelProperty("配送方式0:快递1:自提点2:到店自提")
	private String deliveryType;
	
	@ApiModelProperty("自提点id")
	private String deliveryId;
	
	@ApiModelProperty("付款方式online:在线支付payafter:货到付款")
	private String payType;
	
	@ApiModelProperty("门店id")
	private String xmStoreId;
	
	@ApiModelProperty("发票类型0:普通发票1：增值税发票")
	private String invoiceType;
	
	@ApiModelProperty("发票抬头")
	private String invoice;
	
	@ApiModelProperty("用戶提交的商店信息")
	private List<SubmitStoreInfoVo> storeInfoVos;
	
	@ApiModelProperty("全场优惠券")
	private Long global_coupon_id;

	public Long getGlobal_coupon_id() {
		return global_coupon_id;
	}

	public void setGlobal_coupon_id(Long global_coupon_id) {
		this.global_coupon_id = global_coupon_id;
	}

	public String getCartSession() {
		return cartSession;
	}

	public void setCartSession(String cartSession) {
		this.cartSession = cartSession;
	}

	public String getAddrId() {
		return addrId;
	}

	public void setAddrId(String addrId) {
		this.addrId = addrId;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getXmStoreId() {
		return xmStoreId;
	}

	public void setXmStoreId(String xmStoreId) {
		this.xmStoreId = xmStoreId;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public List<SubmitStoreInfoVo> getStoreInfoVos() {
		return storeInfoVos;
	}

	public void setStoreInfoVos(List<SubmitStoreInfoVo> storeInfoVos) {
		this.storeInfoVos = storeInfoVos;
	}
	
	
	
	
	
}
