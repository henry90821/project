package com.smi.am.dao.model;

public class AmRole {
    private Integer rRoleid;

    private String rRolename;

    public Integer getrRoleid() {
        return rRoleid;
    }

    public void setrRoleid(Integer rRoleid) {
        this.rRoleid = rRoleid;
    }

    public String getrRolename() {
        return rRolename;
    }

    public void setrRolename(String rRolename) {
        this.rRolename = rRolename == null ? null : rRolename.trim();
    }
}