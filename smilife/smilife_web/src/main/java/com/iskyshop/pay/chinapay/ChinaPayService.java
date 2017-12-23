package com.iskyshop.pay.chinapay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Payment;

public class ChinaPayService {
	private static final Logger LOGGER = Logger.getLogger(ChinaPayService.class);
	
	private static final String CURYID = "156";
	private static final String TRANSTYPE = "0001";
	private static final String VERSION = "20141120";
	
	private static final String BG_URL="/chinapay_notify.htm";
    private static final String PAGE_URL="/chinapay_return.htm";
	
    /***
     * 返回html
     * @param url
     * @param payment
     * @param out_trade_no
     * @param body
     * @param total_fee
     * @param paySource
     * @param trade_order_id
     * @return
     */
	public static String getPayFormStr(String url, Payment payment, String out_trade_no, String body, String total_fee,
			String paySource,String trade_order_id,String gate_id,String subtransdate){
		
		String htmlStr="";
		Map<String, String> requestMap=new HashMap<String,String>();
		if(gate_id==null){
			gate_id="";
		}
		
		String merId=payment.getChinapay_account();
		String ordId=trade_order_id;
		String transAmt=ChinaPayCommon.changeFee(total_fee);
		String curyId=CURYID;
		String transDate=subtransdate;
		String transType=TRANSTYPE;
		String version=VERSION;
		String bgRetUrl=url + BG_URL;
		String pageRetUrl=url + PAGE_URL;
		String gateId=gate_id;
		String priv1=out_trade_no+ChinaPayCommon.PAY_SEPARATOR+body+ChinaPayCommon.PAY_SEPARATOR+paySource;
		
		String str=merId+ordId+transAmt+curyId+transDate+transType+version+bgRetUrl+pageRetUrl+gateId+priv1;
		String chkValue=ChinaPayCommon.getSign(merId, str);
		
		requestMap.put("MerId", merId);
		requestMap.put("OrdId", ordId);
		requestMap.put("TransAmt", transAmt);
		requestMap.put("CuryId", curyId);
		requestMap.put("TransDate", transDate);
		requestMap.put("TransType", transType);
		requestMap.put("Version", version);
		requestMap.put("BgRetUrl", bgRetUrl);
		requestMap.put("PageRetUrl", pageRetUrl);
		requestMap.put("GateId", gateId);
		requestMap.put("Priv1", priv1);
		requestMap.put("ChkValue", chkValue);
		
		htmlStr=ChinaPayCommon.createHtml(requestMap,ChinaPayCommon.getPropertiesByKey("chinapay.url"));
		return htmlStr;
	}
	
}
