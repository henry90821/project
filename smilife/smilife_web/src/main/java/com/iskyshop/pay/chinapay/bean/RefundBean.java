/**
 * 
 */
package com.iskyshop.pay.chinapay.bean;

/**
 * @author Jackie.Gao
 *
 */
public class RefundBean {

	private String MerId;
	private String OrdId;
	private String RefundAmount;
	private String TransDate;
	private String TransType;
	private String Version;
	private String ReturnUrl;
	private String Priv1;
	private String ChkValue;
	
	public RefundBean(){
		
	}
	
	public String toString(){
		return new StringBuffer("MerID=").append(MerId)
		.append("&OrderId=").append(OrdId)
		.append("&RefundAmount=").append(RefundAmount)
		.append("&TransDate=").append(TransDate)
		.append("&TransType=").append(TransType)
		.append("&Version=").append(Version)
		.append("&ReturnURL=").append(ReturnUrl)
		.append("&Priv1=").append(Priv1)
		.append("&ChkValue=").append(ChkValue).toString();
	}
	
	public String getMerId() {
		return MerId;
	}
	public void setMerId(String merId) {
		MerId = merId;
	}
	public String getOrdId() {
		return OrdId;
	}
	public void setOrdId(String ordId) {
		OrdId = ordId;
	}
	public String getTransDate() {
		return TransDate;
	}
	public void setTransDate(String transDate) {
		TransDate = transDate;
	}
	public String getTransType() {
		return TransType;
	}
	public void setTransType(String transType) {
		TransType = transType;
	}
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}
	public String getRefundAmount() {
		return RefundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		RefundAmount = refundAmount;
	}
	public String getReturnUrl() {
		return ReturnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		ReturnUrl = returnUrl;
	}
	public String getPriv1() {
		return Priv1;
	}
	public void setPriv1(String priv1) {
		Priv1 = priv1;
	}
	public String getChkValue() {
		return ChkValue;
	}
	public void setChkValue(String chkValue) {
		ChkValue = chkValue;
	}
}
