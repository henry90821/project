package com.smi.am.service.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "部门信息值对象")
public class DepartmentVo {
	
	@ApiModelProperty(value="部门ID")
    private Integer dDepartmentid;

	@ApiModelProperty(value="部门名称")
    private String dDepartmentname;

	@ApiModelProperty(value="所属区域ID")
    private Integer dAreaid;

    public Integer getdDepartmentid() {
        return dDepartmentid;
    }

    public void setdDepartmentid(Integer dDepartmentid) {
        this.dDepartmentid = dDepartmentid;
    }

    public String getdDepartmentname() {
        return dDepartmentname;
    }

    public void setdDepartmentname(String dDepartmentname) {
        this.dDepartmentname = dDepartmentname == null ? null : dDepartmentname.trim();
    }

    public Integer getdAreaid() {
        return dAreaid;
    }

    public void setdAreaid(Integer dAreaid) {
        this.dAreaid = dAreaid;
    }
}