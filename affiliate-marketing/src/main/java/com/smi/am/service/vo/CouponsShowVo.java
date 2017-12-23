package com.smi.am.service.vo;


import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息显示值对象")
public class CouponsShowVo  extends CouponsParentVo{
	
	@ApiModelProperty(value="优惠券ID")
	private String cCouponsid;
	
	@ApiModelProperty(value="优惠券类别名称")
	private String cCouponsTypeName;

	@ApiModelProperty(value="是否可累加,1:否;2:是")
	private Integer cCumulative;
	
	@ApiModelProperty(value="可关联优惠券IDS")
	private String cAccumulatecouponsids;
	
	/*@ApiModelProperty(value="所属活动大区ids")
	private String cActivityzone;*/

	@ApiModelProperty(value="活动门店")
	private JSONArray cActivitystoreArray;
	
	@ApiModelProperty(value="发放对象")
	private String cProvideuser;
	
	@ApiModelProperty(value="实发数量")
	private Integer cRealNum;
	
	@ApiModelProperty(value="已用数量")
	private Integer cUsedNum;
	
	@ApiModelProperty(value="发放方式,1:账户发放;2:用户领取")
	private Integer cDeliveringway;
	
	@ApiModelProperty(value="用户礼包领取剩余数量")
	private Integer cRemainnum;
	
	@ApiModelProperty(value="优惠券明细")
	private List<CounponsDetailsInsertVo> detailList;

	@ApiModelProperty(value="关联优惠券信息")
	private List<CouponRelationVo> couponRelationVoList;
	
	

	public String getcCouponsTypeName() {
		return cCouponsTypeName;
	}

	public void setcCouponsTypeName(String cCouponsTypeName) {
		this.cCouponsTypeName = cCouponsTypeName;
	}


	public List<CouponRelationVo> getCouponRelationVoList() {
		return couponRelationVoList;
	}

	public void setCouponRelationVoList(List<CouponRelationVo> couponRelationVoList) {
		this.couponRelationVoList = couponRelationVoList;
	}

	public String getcAccumulatecouponsids() {
		return cAccumulatecouponsids;
	}

	public void setcAccumulatecouponsids(String cAccumulatecouponsids) {
		this.cAccumulatecouponsids = cAccumulatecouponsids;
	}

	public String getcCouponsid() {
		return cCouponsid;
	}

	public void setcCouponsid(String cCouponsid) {
		this.cCouponsid = cCouponsid;
	}

	public Integer getcRemainnum() {
		return cRemainnum;
	}

	public void setcRemainnum(Integer cRemainnum) {
		this.cRemainnum = cRemainnum;
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



	public JSONArray getcActivitystoreArray() {
		return cActivitystoreArray;
	}

	public void setcActivitystoreArray(JSONArray cActivitystoreArray) {
		this.cActivitystoreArray = cActivitystoreArray;
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

	public Integer getcRealNum() {
		return cRealNum;
	}

	public void setcRealNum(Integer cRealNum) {
		this.cRealNum = cRealNum;
	}

	public Integer getcUsedNum() {
		return cUsedNum;
	}

	public void setcUsedNum(Integer cUsedNum) {
		this.cUsedNum = cUsedNum;
	}
	

}