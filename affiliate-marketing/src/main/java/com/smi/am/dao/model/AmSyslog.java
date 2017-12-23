package com.smi.am.dao.model;

import java.util.Date;

public class AmSyslog {
    private Integer slId;

    private String slUsername;

    private String slName;

    private String slRolename;

    private Integer slAreaid;

    private Integer slRoletype;

    private String slLoginip;

    private String slLogtitle;

    private String slLogdetail;

    private Date slOperatedate;

    private String slRemark;

    public Integer getSlId() {
        return slId;
    }

    public void setSlId(Integer slId) {
        this.slId = slId;
    }

    public String getSlUsername() {
        return slUsername;
    }

    public void setSlUsername(String slUsername) {
        this.slUsername = slUsername == null ? null : slUsername.trim();
    }

    public String getSlName() {
        return slName;
    }

    public void setSlName(String slName) {
        this.slName = slName == null ? null : slName.trim();
    }

    public String getSlRolename() {
        return slRolename;
    }

    public void setSlRolename(String slRolename) {
        this.slRolename = slRolename == null ? null : slRolename.trim();
    }

    public Integer getSlAreaid() {
        return slAreaid;
    }

    public void setSlAreaid(Integer slAreaid) {
        this.slAreaid = slAreaid;
    }

    public Integer getSlRoletype() {
        return slRoletype;
    }

    public void setSlRoletype(Integer slRoletype) {
        this.slRoletype = slRoletype;
    }

    public String getSlLoginip() {
        return slLoginip;
    }

    public void setSlLoginip(String slLoginip) {
        this.slLoginip = slLoginip == null ? null : slLoginip.trim();
    }

    public String getSlLogtitle() {
        return slLogtitle;
    }

    public void setSlLogtitle(String slLogtitle) {
        this.slLogtitle = slLogtitle == null ? null : slLogtitle.trim();
    }

    public String getSlLogdetail() {
        return slLogdetail;
    }

    public void setSlLogdetail(String slLogdetail) {
        this.slLogdetail = slLogdetail == null ? null : slLogdetail.trim();
    }

    public Date getSlOperatedate() {
        return slOperatedate;
    }

    public void setSlOperatedate(Date slOperatedate) {
        this.slOperatedate = slOperatedate;
    }

    public String getSlRemark() {
        return slRemark;
    }

    public void setSlRemark(String slRemark) {
        this.slRemark = slRemark == null ? null : slRemark.trim();
    }
}