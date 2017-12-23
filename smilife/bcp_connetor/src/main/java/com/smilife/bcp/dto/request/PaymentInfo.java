package com.smilife.bcp.dto.request;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class PaymentInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3612375359492751964L;
	/**
	 * 例如 星币、资源账本，现金账本，第三方支付编码（支付宝、银行等） 星币：8 支付宝：11 余额宝：12 网上银行：14 资源：7 现金账本：0
	 */
	@JSONField(name = "PAY_CODE")
	private String payCode;

	@JSONField(name = "AMOUNT")
	private String amount;// 支付数值，星币单位为个，金额单位为元

	@JSONField(name = "BANK_SERIAL_NBR")
	private String bankSerialNbr;// 第三方流水号

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBankSerialNbr() {
		return bankSerialNbr;
	}

	public void setBankSerialNbr(String bankSerialNbr) {
		this.bankSerialNbr = bankSerialNbr;
	}

}
