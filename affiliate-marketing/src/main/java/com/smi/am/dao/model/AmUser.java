package com.smi.am.dao.model;

import java.util.Date;

public class AmUser {
    private Integer uId;

    private Integer uRoletype;

    private String uJobnum;

    private Integer uDepartid;

    private String uName;

    private String uUsername;

    private String uPassword;

    private String uCreateuser;

    private Date uCreatedate;

    private Date uLastmoddate;

    private String uLastmoduser;

    private Date uLastlogindate;

    private Integer uDeletestate;

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public Integer getuRoletype() {
        return uRoletype;
    }

    public void setuRoletype(Integer uRoletype) {
        this.uRoletype = uRoletype;
    }

    public String getuJobnum() {
        return uJobnum;
    }

    public void setuJobnum(String uJobnum) {
        this.uJobnum = uJobnum == null ? null : uJobnum.trim();
    }

    public Integer getuDepartid() {
        return uDepartid;
    }

    public void setuDepartid(Integer uDepartid) {
        this.uDepartid = uDepartid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName == null ? null : uName.trim();
    }

    public String getuUsername() {
        return uUsername;
    }

    public void setuUsername(String uUsername) {
        this.uUsername = uUsername == null ? null : uUsername.trim();
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword == null ? null : uPassword.trim();
    }

    public String getuCreateuser() {
        return uCreateuser;
    }

    public void setuCreateuser(String uCreateuser) {
        this.uCreateuser = uCreateuser == null ? null : uCreateuser.trim();
    }

    public Date getuCreatedate() {
        return uCreatedate;
    }

    public void setuCreatedate(Date uCreatedate) {
        this.uCreatedate = uCreatedate;
    }

    public Date getuLastmoddate() {
        return uLastmoddate;
    }

    public void setuLastmoddate(Date uLastmoddate) {
        this.uLastmoddate = uLastmoddate;
    }

    public String getuLastmoduser() {
        return uLastmoduser;
    }

    public void setuLastmoduser(String uLastmoduser) {
        this.uLastmoduser = uLastmoduser == null ? null : uLastmoduser.trim();
    }

    public Date getuLastlogindate() {
        return uLastlogindate;
    }

    public void setuLastlogindate(Date uLastlogindate) {
        this.uLastlogindate = uLastlogindate;
    }

    public Integer getuDeletestate() {
        return uDeletestate;
    }

    public void setuDeletestate(Integer uDeletestate) {
        this.uDeletestate = uDeletestate;
    }
}