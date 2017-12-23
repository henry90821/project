package com.smi.am.service.vo;


import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息编辑值对象")
public class CouponsEditVo  extends CouponsParentVo{
	
	@NotEmpty
	@ApiModelProperty(value="优惠券ID")
	private String cCouponsid;

	@NotNull
	@ApiModelProperty(value="是否可累加,1:否;2:是")
	private Integer cCumulative;
	
	@ApiModelProperty(value="可关联优惠券IDS")
	private String cAccumulatecouponsids;

	/*@NotEmpty
	@ApiModelProperty(value="活动门店")
	private String cActivityzone;*/
	
	@NotEmpty
	@ApiModelProperty(value="活动门店")
	private String cActivitystore;
	
	@ApiModelProperty(value="发放对象")
	private String cProvideuser;
	
	@NotNull
	@ApiModelProperty(value="发放方式,1:账户发放;2:用户领取")
	private Integer cDeliveringway;
	
	@NotEmpty
	@ApiModelProperty(value="优惠券明细")
	private List<CounponsDetailsInsertVo> detailList;

	
	
	public String getcAccumulatecouponsids() {
		return cAccumulatecouponsids;
	}

	public void setcAccumulatecouponsids(String cAccumulatecouponsids) {
		this.cAccumulatecouponsids = cAccumulatecouponsids;
	}

	public Integer getcDeliveringway() {
		return cDeliveringway;
	}

	public void setcDeliveringway(Integer cDeliveringway) {
		this.cDeliveringway = cDeliveringway;
	}

	public String getcCouponsid() {
		return cCouponsid;
	}

	public void setcCouponsid(String cCouponsid) {
		this.cCouponsid = cCouponsid;
	}

	public Integer getcCumulative() {
		return cCumulative;
	}

	public void setcCumulative(Integer cCumulative) {
		this.cCumulative = cCumulative;
	}


	public String getcActivitystore() {
		return cActivitystore;
	}

	public void setcActivitystore(String cActivitystore) {
		this.cActivitystore = cActivitystore;
	}


	public String getcProvideuser() {
		return cProvideuser;
	}

	public void setcProvideuser(String cProvideuser) {
		this.cProvideuser = cProvideuser;
	}

	public List<CounponsDetailsInsertVo> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<CounponsDetailsInsertVo> detailList) {
		this.detailList = detailList;
	}



}