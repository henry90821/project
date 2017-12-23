package com.smilife.bcp.dto.response;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员余额DTO
 * @author liz
 * 2015-8-26
 */
public class MemberBalanceResp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8292221620349958908L;

	private BigDecimal cash; // 现金余额
	
	private int xingCoin; // 星币数量
	
	private String xingCoinRate = "0.01"; // 星币汇率
	
	

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public int getXingCoin() {
		return xingCoin;
	}

	public void setXingCoin(int xingCoin) {
		this.xingCoin = xingCoin;
	}

	public String getXingCoinRate() {
		return xingCoinRate;
	}

	public void setXingCoinRate(String xingCoinRate) {
		this.xingCoinRate = xingCoinRate;
	}

	
}
