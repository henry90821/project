/**
 * 
 */
package com.smi.am.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nihao
 *
 */
@Component
@ConfigurationProperties(locations={"classpath:mc-config.yml"},prefix="member-center")
public class MemberCenterConfiguration {

	/**
	 * 账户发放投放渠道
	 */
	private String accountDelivery;
	
	/**
	 * 星美生活投放渠道
	 */
	private String smilifeDelivery;
	
	/**
	 * 满天星投放渠道
	 */
	private String mtxDelivery;
	
	/**
	 * 会员中心查询内部接口
	 */
	private String custQryUrl;
	
	/**
	 * 会员中心接口URL
	 */
	private String custUrl;

	/**
	 * @return the accountDelivery
	 */
	public String getAccountDelivery() {
		return accountDelivery;
	}

	/**
	 * @param accountDelivery the accountDelivery to set
	 */
	@Value(value="ACCOUNT_DELIVERY")
	public void setAccountDelivery(String accountDelivery) {
		this.accountDelivery = accountDelivery;
	}

	/**
	 * @return the smilifeDelivery
	 */
	public String getSmilifeDelivery() {
		return smilifeDelivery;
	}

	/**
	 * @param smilifeDelivery the smilifeDelivery to set
	 */
	@Value(value="SMILIFE_DELIVERY")
	public void setSmilifeDelivery(String smilifeDelivery) {
		this.smilifeDelivery = smilifeDelivery;
	}

	/**
	 * @return the mtxDelivery
	 */
	public String getMtxDelivery() {
		return mtxDelivery;
	}

	/**
	 * @param mtxDelivery the mtxDelivery to set
	 */
	@Value(value="MTX_DELIVERY")
	public void setMtxDelivery(String mtxDelivery) {
		this.mtxDelivery = mtxDelivery;
	}

	/**
	 * @return the custQryUrl
	 */
	public String getCustQryUrl() {
		return custQryUrl;
	}

	/**
	 * @param custQryUrl the custQryUrl to set
	 */
	@Value(value="cust_qry_url")
	public void setCustQryUrl(String custQryUrl) {
		this.custQryUrl = custQryUrl;
	}

	/**
	 * @return the custUrl
	 */
	public String getCustUrl() {
		return custUrl;
	}

	/**
	 * @param custUrl the custUrl to set
	 */
	@Value(value="cust_url")
	public void setCustUrl(String custUrl) {
		this.custUrl = custUrl;
	}
}
