package com.smi.am.dao.model;

public class AmCouponsWithBLOBs extends AmCoupons {
    private byte[] cActivitystore;

    private byte[] cProvideuser;

    public byte[] getcActivitystore() {
        return cActivitystore;
    }

    public void setcActivitystore(byte[] cActivitystore) {
        this.cActivitystore = cActivitystore;
    }

    public byte[] getcProvideuser() {
        return cProvideuser;
    }

    public void setcProvideuser(byte[] cProvideuser) {
        this.cProvideuser = cProvideuser;
    }
}