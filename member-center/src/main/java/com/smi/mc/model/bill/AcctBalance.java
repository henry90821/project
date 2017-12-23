
package com.smi.mc.model.bill;

import java.io.Serializable;

public class AcctBalance implements Serializable {

	public AcctBalance() {
	}

	public long getBALANCE_ID() {
		return BALANCE_ID;
	}

	public void setBALANCE_ID(long bALANCE_ID) {
		BALANCE_ID = bALANCE_ID;
	}

	public long getBALANCE_TYPE_ID() {
		return BALANCE_TYPE_ID;
	}

	public void setBALANCE_TYPE_ID(long bALANCE_TYPE_ID) {
		BALANCE_TYPE_ID = bALANCE_TYPE_ID;
	}

	public long getREAL_BALANCE() {
		return REAL_BALANCE;
	}

	public void setREAL_BALANCE(long rEAL_BALANCE) {
		REAL_BALANCE = rEAL_BALANCE;
	}

	public long getPAY_CODE() {
		return PAY_CODE;
	}

	public void setPAY_CODE(long pAY_CODE) {
		PAY_CODE = pAY_CODE;
	}

	public long getBALANCE() {
		return BALANCE;
	}

	public void setBALANCE(long bALANCE) {
		BALANCE = bALANCE;
	}

	public String getEFF_DATE() {
		return EFF_DATE;
	}

	public void setEFF_DATE(String eFF_DATE) {
		EFF_DATE = eFF_DATE;
	}

	public String getEXP_DATE() {
		return EXP_DATE;
	}

	public void setEXP_DATE(String eXP_DATE) {
		EXP_DATE = eXP_DATE;
	}

	public long getUNITE_TYPE_ID() {
		return UNITE_TYPE_ID;
	}

	public void setUNITE_TYPE_ID(long uNITE_TYPE_ID) {
		UNITE_TYPE_ID = uNITE_TYPE_ID;
	}

	public String getBALANCE_NAME() {
		return BALANCE_NAME;
	}

	public void setBALANCE_NAME(String bALANCE_NAME) {
		BALANCE_NAME = bALANCE_NAME;
	}

	private static final long serialVersionUID = 6695024110226488837L;
	private long BALANCE_ID;
	private long BALANCE_TYPE_ID;
	private long REAL_BALANCE;
	private long PAY_CODE;
	private long BALANCE;
	private String EFF_DATE;
	private String EXP_DATE;
	private long UNITE_TYPE_ID;
	private String BALANCE_NAME;
}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from: D:\workspace\vip-service-1.0.2\lib\vip-api-1.0.0.jar Total
 * time: 85 ms Jad reported messages/errors: Exit status: 0 Caught exceptions:
 */