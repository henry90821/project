package com.smi.mc.utils;

import java.util.Date;

import com.smi.mc.constants.Constants;


public class GenCustIdAndPayIdUtil {

	/**
	 * 生成cust_id
	 * 
	 * @param seq
	 * @param CUST_ID
	 * @return
	 */
	public static String genCustId(String seq) {
		String appendStr = "";
		String custId="";
		for (int i = 0; i < 12 - seq.length(); i++) {
			appendStr = appendStr + "0";
		}
		custId = Constants.CUST_ID_HEAD + appendStr + seq;
		return custId;

	}

	/**
	 * 生成pay_id
	 * 
	 * @param seqId
	 * @param payID
	 * @return
	 */
	public static String genPayId(String seqId) {
		if (seqId.length() == 1) {
			seqId = "000" + seqId;
		} else if (seqId.length() == 2) {
			seqId = "00" + seqId;
		} else if (seqId.length() == 3) {
			seqId = "0" + seqId;
		}
		Date date = new Date();
		String time = date.getTime() + "";
		String payID = Constants.PAY_ID_HEAD + time.substring(3, 13) + seqId;
		return payID;

	}
}
