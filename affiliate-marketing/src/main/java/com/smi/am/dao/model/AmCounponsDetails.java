package com.smi.am.dao.model;

public class AmCounponsDetails {
    private String cdDetailid;

    private String cdCounponsid;

    private String cdLoginmobile;

    private String cdCustid;

    private Integer cdCounponschanneltype;

    private Integer cdUsed;

    private Integer cdIsgitpackagecounpons;

    private Integer cdGitpackageid;

    public String getCdDetailid() {
        return cdDetailid;
    }

    public void setCdDetailid(String cdDetailid) {
        this.cdDetailid = cdDetailid == null ? null : cdDetailid.trim();
    }

    public String getCdCounponsid() {
        return cdCounponsid;
    }

    public void setCdCounponsid(String cdCounponsid) {
        this.cdCounponsid = cdCounponsid == null ? null : cdCounponsid.trim();
    }

    public String getCdLoginmobile() {
        return cdLoginmobile;
    }

    public void setCdLoginmobile(String cdLoginmobile) {
        this.cdLoginmobile = cdLoginmobile == null ? null : cdLoginmobile.trim();
    }

    public String getCdCustid() {
        return cdCustid;
    }

    public void setCdCustid(String cdCustid) {
        this.cdCustid = cdCustid == null ? null : cdCustid.trim();
    }

    public Integer getCdCounponschanneltype() {
        return cdCounponschanneltype;
    }

    public void setCdCounponschanneltype(Integer cdCounponschanneltype) {
        this.cdCounponschanneltype = cdCounponschanneltype;
    }

    public Integer getCdUsed() {
        return cdUsed;
    }

    public void setCdUsed(Integer cdUsed) {
        this.cdUsed = cdUsed;
    }

    public Integer getCdIsgitpackagecounpons() {
        return cdIsgitpackagecounpons;
    }

    public void setCdIsgitpackagecounpons(Integer cdIsgitpackagecounpons) {
        this.cdIsgitpackagecounpons = cdIsgitpackagecounpons;
    }

    public Integer getCdGitpackageid() {
        return cdGitpackageid;
    }

    public void setCdGitpackageid(Integer cdGitpackageid) {
        this.cdGitpackageid = cdGitpackageid;
    }
}