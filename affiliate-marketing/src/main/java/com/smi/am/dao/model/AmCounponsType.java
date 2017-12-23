package com.smi.am.dao.model;

import java.util.Date;

public class AmCounponsType {
    private Integer ctId;

    private String ctCounponsname;

    private Integer ctCounponstype;

    private String ctRemark;

    private Date ctCreatedate;

    private String ctCreateuser;

    private Date ctLastmoddate;

    private String ctLastmoduser;

    public Integer getCtId() {
        return ctId;
    }

    public void setCtId(Integer ctId) {
        this.ctId = ctId;
    }

    public String getCtCounponsname() {
        return ctCounponsname;
    }

    public void setCtCounponsname(String ctCounponsname) {
        this.ctCounponsname = ctCounponsname == null ? null : ctCounponsname.trim();
    }

    public Integer getCtCounponstype() {
        return ctCounponstype;
    }

    public void setCtCounponstype(Integer ctCounponstype) {
        this.ctCounponstype = ctCounponstype;
    }

    public String getCtRemark() {
        return ctRemark;
    }

    public void setCtRemark(String ctRemark) {
        this.ctRemark = ctRemark == null ? null : ctRemark.trim();
    }

    public Date getCtCreatedate() {
        return ctCreatedate;
    }

    public void setCtCreatedate(Date ctCreatedate) {
        this.ctCreatedate = ctCreatedate;
    }

    public String getCtCreateuser() {
        return ctCreateuser;
    }

    public void setCtCreateuser(String ctCreateuser) {
        this.ctCreateuser = ctCreateuser == null ? null : ctCreateuser.trim();
    }

    public Date getCtLastmoddate() {
        return ctLastmoddate;
    }

    public void setCtLastmoddate(Date ctLastmoddate) {
        this.ctLastmoddate = ctLastmoddate;
    }

    public String getCtLastmoduser() {
        return ctLastmoduser;
    }

    public void setCtLastmoduser(String ctLastmoduser) {
        this.ctLastmoduser = ctLastmoduser == null ? null : ctLastmoduser.trim();
    }
}