package com.smi.am.dao.model;

public class AmDepartment {
    private Integer dDepartmentid;

    private String dDepartmentname;

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