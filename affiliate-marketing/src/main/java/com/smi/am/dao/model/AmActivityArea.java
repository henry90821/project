package com.smi.am.dao.model;

public class AmActivityArea {
    private Integer aId;

    private String aActivityarea;

    private Integer aAreapid;

    public Integer getaId() {
        return aId;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public String getaActivityarea() {
        return aActivityarea;
    }

    public void setaActivityarea(String aActivityarea) {
        this.aActivityarea = aActivityarea == null ? null : aActivityarea.trim();
    }

    public Integer getaAreapid() {
        return aAreapid;
    }

    public void setaAreapid(Integer aAreapid) {
        this.aAreapid = aAreapid;
    }
}