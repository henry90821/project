package com.iskyshop.pay.alipay.services;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipaySubmit;



public class AlipayRet {
	private static final Logger LOGGER = Logger.getLogger(AlipayRet.class);
	
	private static final String NOTIFY_URL = "/alipay_ret_pay_notify.htm";
	private static final String SERVICE = "refund_fastpay_by_platform_pwd";
	
	public static String alipayForm(OrderForm orderForm,String alipay_batch_no,String total_fee,String url,String retReason) throws Exception {
		String alipayForm="";
		Date date=new Date();
		Payment payment=orderForm.getPayment();
		
		AlipayConfig config = new AlipayConfig();
		config.setSign_type(payment.getAlipaySignType());
		config.setPartner(payment.getPartner());
		config.setSeller_email(payment.getSeller_email());
		config.setPrivate_key(payment.getApp_private_key());
		config.setKey(payment.getSafeKey());
		config.setSign_type(payment.getAlipaySignType());
		
		String service=SERVICE;
		String partner=config.getPartner();
		String notify_url=url+NOTIFY_URL;
		String seller_email=config.getSeller_email();
		String _input_charset=config.getInput_charset();
		String refund_date=CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", date);
		String batch_no = alipay_batch_no;
		String batch_num="1";
		String detail_data=orderForm.getOut_order_id()+"^"+totalFee(total_fee)+"^"+retReason;
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", service);
        sParaTemp.put("partner", partner);
        sParaTemp.put("_input_charset", _input_charset);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("refund_date", refund_date);
		sParaTemp.put("batch_no", batch_no);
		sParaTemp.put("batch_num", batch_num);
		sParaTemp.put("detail_data", detail_data);
				
		alipayForm=AlipaySubmit.buildForm(config, sParaTemp, getPropertiesByKey("alipay.ret.url"),"get","确认");
		return alipayForm;
	}
	
	/***
	 * alipay properties
	 * @param key
	 * @return
	 */
	public static String getPropertiesByKey(String key){
		InputStream is = null;
		Properties properties = null;
		String value="";
		try {
        	properties = new Properties();
        	is = AlipayRet.class.getResourceAsStream("/alipay.properties");
        	properties.load(is);
        	value=properties.getProperty(key);
        }
        catch (Exception e) {
        	LOGGER.error("获取alipay.properties失败");
        }
        return value;
	}
	/***
	 * 两位小数
	 * @param fee
	 * @return
	 */
	public static String totalFee(String fee){
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(Double.valueOf(fee));
	}
	
}
