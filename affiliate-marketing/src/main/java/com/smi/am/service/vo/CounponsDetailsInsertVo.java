package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息明细值对象")
public class CounponsDetailsInsertVo {
	

	@ApiModelProperty(value="是否礼包的优惠券 1:是,2:否")
    private Integer cdIsgitpackagecounpons;

	@ApiModelProperty(value="优惠券发放数量")
	private Integer couponsCount;
	
	
    public Integer getCouponsCount() {
		return couponsCount;
	}

	public void setCouponsCount(Integer couponsCount) {
		this.couponsCount = couponsCount;
	}


    public Integer getCdIsgitpackagecounpons() {
        return cdIsgitpackagecounpons;
    }

    public void setCdIsgitpackagecounpons(Integer cdIsgitpackagecounpons) {
        this.cdIsgitpackagecounpons = cdIsgitpackagecounpons;
    }

}