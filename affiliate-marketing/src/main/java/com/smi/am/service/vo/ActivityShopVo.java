package com.smi.am.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "活动门店信息值对象")
public class ActivityShopVo {
	
	@ApiModelProperty(value="门店ID")
    private Integer asId;

	@ApiModelProperty(value="所属活动大区ID")
    private Integer asActivityareaid;

	@ApiModelProperty(value="活动门店名称")
    private String asActivityshop;

	@ApiModelProperty(value="门店所属渠道id")
	private String asOrgid;
	
	@ApiModelProperty(value="门店所属影城编码")
	private String asCinemacode;
	
	
	
	
    public String getAsOrgid() {
		return asOrgid;
	}

	public void setAsOrgid(String asOrgid) {
		this.asOrgid = asOrgid;
	}

	public String getAsCinemacode() {
		return asCinemacode;
	}

	public void setAsCinemacode(String asCinemacode) {
		this.asCinemacode = asCinemacode;
	}

	public Integer getAsId() {
        return asId;
    }

    public void setAsId(Integer asId) {
        this.asId = asId;
    }

    public Integer getAsActivityareaid() {
        return asActivityareaid;
    }

    public void setAsActivityareaid(Integer asActivityareaid) {
        this.asActivityareaid = asActivityareaid;
    }

    public String getAsActivityshop() {
        return asActivityshop;
    }

    public void setAsActivityshop(String asActivityshop) {
        this.asActivityshop = asActivityshop == null ? null : asActivityshop.trim();
    }
}