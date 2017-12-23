package com.smi.am.service.vo;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "活动区域信息值对象")
public class ActivityAreaVo {
	
	@ApiModelProperty(value="活动区ID")
    private Integer aId;

	@ApiModelProperty(value="活动区名称")
    private String aActivityarea;
    
	@ApiModelProperty(value="所属运营中心ID")
    private Integer aAreapid;
	
	@ApiModelProperty(value="活动区下的所有门店信息")
    private List<ActivityShopVo> activityShopVos;
    
    
    

    public List<ActivityShopVo> getActivityShopVos() {
		return activityShopVos;
	}

	public void setActivityShopVos(List<ActivityShopVo> activityShopVos) {
		this.activityShopVos = activityShopVos;
	}

	public Integer getaAreapid() {
		return aAreapid;
	}

	public void setaAreapid(Integer aAreapid) {
		this.aAreapid = aAreapid;
	}

	public Integer getaId() {
        return aId;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public String getaActivityarea() {
        return aActivityarea;
    }

    public void setaActivityarea(String aActivityarea) {
        this.aActivityarea = aActivityarea == null ? null : aActivityarea.trim();
    }
}