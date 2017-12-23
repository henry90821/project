package com.smi.am.dao.model;

public class AmActivityShop {
    private Integer asId;

    private Integer asActivityareaid;

    private String asActivityshop;

    private String asOrgid;

    private String asCinemacode;

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

    public String getAsOrgid() {
        return asOrgid;
    }

    public void setAsOrgid(String asOrgid) {
        this.asOrgid = asOrgid == null ? null : asOrgid.trim();
    }

    public String getAsCinemacode() {
        return asCinemacode;
    }

    public void setAsCinemacode(String asCinemacode) {
        this.asCinemacode = asCinemacode == null ? null : asCinemacode.trim();
    }
}