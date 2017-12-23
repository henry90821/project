package com.smi.am.dao.model;

import java.util.Date;

public class AmCounponsDetailHistory {
    private Integer cdhDetailid;

    private String cdhCounponsid;

    private String cdLoginmobile;

    private String cdhCustid;

    private Integer cdhCounponschanneltype;

    private Integer cdhIsgitpackagecounpons;

    private Integer cdhGitpackageid;

    private Date cdhUsedtime;

    public Integer getCdhDetailid() {
        return cdhDetailid;
    }

    public void setCdhDetailid(Integer cdhDetailid) {
        this.cdhDetailid = cdhDetailid;
    }

    public String getCdhCounponsid() {
        return cdhCounponsid;
    }

    public void setCdhCounponsid(String cdhCounponsid) {
        this.cdhCounponsid = cdhCounponsid == null ? null : cdhCounponsid.trim();
    }

    public String getCdLoginmobile() {
        return cdLoginmobile;
    }

    public void setCdLoginmobile(String cdLoginmobile) {
        this.cdLoginmobile = cdLoginmobile == null ? null : cdLoginmobile.trim();
    }

    public String getCdhCustid() {
        return cdhCustid;
    }

    public void setCdhCustid(String cdhCustid) {
        this.cdhCustid = cdhCustid == null ? null : cdhCustid.trim();
    }

    public Integer getCdhCounponschanneltype() {
        return cdhCounponschanneltype;
    }

    public void setCdhCounponschanneltype(Integer cdhCounponschanneltype) {
        this.cdhCounponschanneltype = cdhCounponschanneltype;
    }

    public Integer getCdhIsgitpackagecounpons() {
        return cdhIsgitpackagecounpons;
    }

    public void setCdhIsgitpackagecounpons(Integer cdhIsgitpackagecounpons) {
        this.cdhIsgitpackagecounpons = cdhIsgitpackagecounpons;
    }

    public Integer getCdhGitpackageid() {
        return cdhGitpackageid;
    }

    public void setCdhGitpackageid(Integer cdhGitpackageid) {
        this.cdhGitpackageid = cdhGitpackageid;
    }

    public Date getCdhUsedtime() {
        return cdhUsedtime;
    }

    public void setCdhUsedtime(Date cdhUsedtime) {
        this.cdhUsedtime = cdhUsedtime;
    }
}