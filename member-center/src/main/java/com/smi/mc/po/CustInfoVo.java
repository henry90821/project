package com.smi.mc.po;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 会员信息实体
 * @author smi
 *
 */
public class CustInfoVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String custNbr;

	private String custName;

	private String orgId;

	private String contactMobile;
	
	private Integer pageNum; //页码
	
	private Integer pageSize; //每页显示数量
	

	public String getCustNbr() {
		return custNbr;
	}

	public void setCustNbr(String custNbr) {
		this.custNbr = custNbr;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "CustInfoVo [custNbr=" + custNbr + ", custName=" + custName + ", orgId=" + orgId + ", contactMobile="
				+ contactMobile + ", pageNum=" + pageNum + ", pageSize=" + pageSize + "]";
	}



}
