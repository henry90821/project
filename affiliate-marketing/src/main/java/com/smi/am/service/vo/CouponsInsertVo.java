package com.smi.am.service.vo;


import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息新增值对象")
public class CouponsInsertVo  extends CouponsParentVo{
	
	@NotNull
	@ApiModelProperty(value="是否可累加,1:否;2:是")
	private Integer cCumulative;
	
	@ApiModelProperty(value="可关联优惠券IDS")
	private String cAccumulatecouponsids;

	@NotEmpty
	@ApiModelProperty(value="活动门店")
	private String cActivitystore;
	
/*	@NotEmpty
	@ApiModelProperty(value="活动大区ID")
	private String cActivityzone;*/
	
	@ApiModelProperty(value="发放对象")
	private String cProvideuser;
	
	@NotNull
	@ApiModelProperty(value="发放方式,1:账户发放;2:用户领取")
	private Integer cDeliveringway;
	
	@NotNull
	@ApiModelProperty(value="用户领取单发优惠券剩余数量")
	private Integer cRemainnum;
	
	@NotEmpty
	@ApiModelProperty(value="优惠券明细")
	private List<CounponsDetailsInsertVo> detailList;

	

	public Integer getcRemainnum() {
		return cRemainnum;
	}

	public void setcRemainnum(Integer cRemainnum) {
		this.cRemainnum = cRemainnum;
	}


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