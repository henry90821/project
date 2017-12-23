package com.iskyshop.pay.chinapay;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;




import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.pay.chinapay.bean.RefundBean;

public class ChinaPayRet {
	private static final Logger LOGGER = Logger.getLogger(ChinaPayRet.class);

	private static final String TRANSTYPE = "0002";
	private static final String VERSION = "20070129";

	private static final String RETURN_URL = "/ret_pay_notify.htm";
	
	private static final String HTTPTYPE="SSL";
	private static final String TIMEOUT="60000";
	
    public static Map<String,String> getRetPayInfo(String url, Payment payment,
			String out_trade_no, String total_fee, String priv,String subtansdate) {

		String retStr = "";
		Map<String,String> retPayMap=new HashMap<String,String>();
		
		
		String merId = payment.getChinapay_account();
		String ordId = out_trade_no;
		String transAmt = ChinaPayCommon.changeFee(total_fee);
		
		String transDate = subtansdate;
		String transType = TRANSTYPE;
		String version = VERSION;
		String returnUrl=url+RETURN_URL;
		
		String priv1 = priv;

		String str = merId + transDate + transType + ordId + transAmt + priv1;
		String chkValue = ChinaPayCommon.getSign(merId, str);
		
		String httpType = HTTPTYPE;
		String timeOut = TIMEOUT;

		RefundBean refund = new RefundBean();
		refund.setMerId(merId);
		refund.setOrdId(ordId);
		refund.setRefundAmount(transAmt);
		refund.setTransDate(transDate);
		refund.setTransType(transType);
		refund.setVersion(version);
		refund.setReturnUrl(returnUrl);
		refund.setPriv1(priv1);
		refund.setChkValue(chkValue);
		
		retStr=ChinaPayCommon.sendHttpMsg(ChinaPayCommon.getPropertiesByKey("chinapay.ret.url"), refund.toString(), httpType, timeOut);
		
		retPayMap=ChinaPayCommon.getRetMap(retStr);
		
		return retPayMap;
	}
    
    
}
