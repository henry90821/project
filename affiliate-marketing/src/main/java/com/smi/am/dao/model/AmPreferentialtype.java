package com.smi.am.dao.model;

public class AmPreferentialtype {
    private Integer ptId;

    private String ptTypename;

    public Integer getPtId() {
        return ptId;
    }

    public void setPtId(Integer ptId) {
        this.ptId = ptId;
    }

    public String getPtTypename() {
        return ptTypename;
    }

    public void setPtTypename(String ptTypename) {
        this.ptTypename = ptTypename == null ? null : ptTypename.trim();
    }
}