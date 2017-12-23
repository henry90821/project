package com.smi.mc.model.cust;

import java.util.Date;

public class InfoCerti {
    private String certiId;

    private String custId;

    private Short certiType;

    private String certiNbr;

    private String certiAddr;

    private String statusCd;

    private String authFlag;

    private Date crtDate;

    private Date authDate;

    private Date modDate;

    public String getCertiId() {
        return certiId;
    }

    public void setCertiId(String certiId) {
        this.certiId = certiId == null ? null : certiId.trim();
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public Short getCertiType() {
        return certiType;
    }

    public void setCertiType(Short certiType) {
        this.certiType = certiType;
    }

    public String getCertiNbr() {
        return certiNbr;
    }

    public void setCertiNbr(String certiNbr) {
        this.certiNbr = certiNbr == null ? null : certiNbr.trim();
    }

    public String getCertiAddr() {
        return certiAddr;
    }

    public void setCertiAddr(String certiAddr) {
        this.certiAddr = certiAddr == null ? null : certiAddr.trim();
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd == null ? null : statusCd.trim();
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(String authFlag) {
        this.authFlag = authFlag == null ? null : authFlag.trim();
    }

    public Date getCrtDate() {
        return crtDate;
    }

    public void setCrtDate(Date crtDate) {
        this.crtDate = crtDate;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }
}