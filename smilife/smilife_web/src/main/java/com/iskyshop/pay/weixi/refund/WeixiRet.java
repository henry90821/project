package com.iskyshop.pay.weixi.refund;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;

import com.iskyshop.pay.tenpay.util.MD5Util;
import com.iskyshop.pay.tenpay.util.Sha1Util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;




public class WeixiRet{
	private static final Logger LOGGER = Logger.getLogger(WeixiRet.class);

    public static RefundResBean getRetPayInfo(OrderForm orderForm,
			String out_trade_no, String total_fee,String refund_fee,String encode) {
    	Date date = new Date();
    	RefundResBean refundResBean = new RefundResBean();
    	RefundReqBean refundReqBean = new RefundReqBean();
        String responseString="";
        
        Payment payment=orderForm.getPayment();
        
        String appid = payment.getWx_appid();
        String mch_id = payment.getTenpay_partner();
        String nonce_str = Sha1Util.getNonceStr();
        String key = payment.getWx_paySignKey();
        String transaction_id = out_trade_no;
        String out_refund_no = CommUtil.formatTime("yyyyMMddHHmmssSSS", date);
        String wx_total_fee = AmountUtils.changeY2F(total_fee);
        String wx_refund_fee = AmountUtils.changeY2F(refund_fee);
        
        TreeMap<String,String> treeMap=new TreeMap<String,String>();
        treeMap.put("appid", appid);
        treeMap.put("mch_id", mch_id);
        treeMap.put("nonce_str", nonce_str);
        treeMap.put("transaction_id", transaction_id);
        treeMap.put("out_refund_no", out_refund_no);
        treeMap.put("total_fee", wx_total_fee);
        treeMap.put("refund_fee", wx_refund_fee);
        treeMap.put("op_user_id", mch_id);
        
        LOGGER.info("微信退款参数：appid=" + appid + ",mch_id=" + mch_id + ",nonce_str=" + nonce_str + ",transaction_id=" + transaction_id + ",out_refund_no=" + out_refund_no + ",total_fee=" + wx_total_fee + ",refund_fee=" + wx_refund_fee + ",op_user_id=" + mch_id);
        
        String sign = getSign(treeMap, key, encode);
        
        LOGGER.info("微信退款签名参数：key=" + key + ", encode=" + encode + ",sign=" + sign);
        
        refundReqBean.setAppid(appid);
        refundReqBean.setMch_id(mch_id);
        refundReqBean.setNonce_str(nonce_str);
        refundReqBean.setSign(sign);
        refundReqBean.setTransaction_id(transaction_id);
        refundReqBean.setOut_refund_no(out_refund_no);
        refundReqBean.setTotal_fee(Integer.parseInt(wx_total_fee));
        refundReqBean.setRefund_fee(Integer.parseInt(wx_refund_fee));
        refundReqBean.setOp_user_id(mch_id);
    	//解决XStream对出现双下划线的bug
        XStream xStream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        //将要提交给API的数据对象转换成XML格式数据Post给API
        xStream.alias("xml", RefundReqBean.class);
        xStream.autodetectAnnotations(true);
        String postDataXML = xStream.toXML(refundReqBean);
        
        LOGGER.info("微信退款请求地址：" + getPropertiesByKey("weixi.ret.url"));
        LOGGER.info("微信退款请求报文：" + postDataXML);
        
        responseString = sendHttp(mch_id, postDataXML, getPropertiesByKey("weixi.ret.url"));
        
        LOGGER.info("微信退款请求的响应：" + responseString);
        
        XStream xStreamForResponseData = new XStream();
        xStreamForResponseData.alias("xml", RefundResBean.class);
        xStreamForResponseData.autodetectAnnotations(true);
        refundResBean=(RefundResBean) xStreamForResponseData.fromXML(responseString);
        return refundResBean;
    }
    
    /***
     * 发送数据请求
     * @param mch_id
     * @param dataXml
     * @param reqUrl
     * @return
     */
    public static String sendHttp(String mch_id,String dataXml,String reqUrl){
    	String retStr="";
    	FileInputStream keyStream=null;
    	BufferedOutputStream out=null;
    	HttpsURLConnection conn=null;
		try {
			keyStream = new FileInputStream(getRetFilePathName(mch_id));
		    final char[] kp = str2CharArray(mch_id);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(keyStream, kp);
			kmf.init(ks, kp);

			SecureRandom rand = new SecureRandom();
			SSLContext ctx = SSLContext.getInstance("TLSv1");
			ctx.init(kmf.getKeyManagers(), null, rand);
        
		    SSLSocketFactory sf = ctx.getSocketFactory();

		    URL url = new URL(reqUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(sf);
		
			// 以post方式通信
			conn.setRequestMethod("POST");
	        //设置连接超时时间
			conn.setConnectTimeout(60000);
			//不使用缓存
			conn.setUseCaches(false);
		    //允许输入输出
			conn.setDoInput(true);
			conn.setDoOutput(true);
            
			conn.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
			conn.connect();  
			out = new BufferedOutputStream(conn.getOutputStream());
            out.write(dataXml.getBytes()); //写入请求的字符串  
	        out.flush();  
	        
	        int responseCode = conn.getResponseCode();
		    if(responseCode==200){
				InputStream inputStream = conn.getInputStream();
				byte[] retData = new byte[inputStream.available()];  
			    inputStream.read(retData);  
			    retStr = new String(retData);  
		    }
				
		} catch (Exception e) {
			LOGGER.error("微信退款请求错误:" + e.getMessage(), e);	
			RuntimeException ex = new RuntimeException();
			ex.initCause(e);
			throw ex;
		}finally{
			try {
				keyStream.close();
				out.close(); 
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return retStr;
    }
    /***
	 * weixi properties
	 * @param key
	 * @return
	 */
	public static String getPropertiesByKey(String key){
		InputStream is = null;
		Properties properties = null;
		String value="";
		try {
        	properties = new Properties();
        	is = WeixiRet.class.getResourceAsStream("/weixi.properties");
        	properties.load(is);
        	value=properties.getProperty(key);
        }
        catch (Exception e) {
        	LOGGER.error("获取weixi.properties失败");
        }
        return value;
	}
	/***
	 * 字符串转换成char数组
	 * @param str
	 * @return
	 */
	public static char[] str2CharArray(String str) {
		if (null == str){
			return null;
		}
		return str.toCharArray();
	}
	/***
     * 文件路径
     * @return
     */
    public static String getRetFilePathName(String mch_id){
		return CommUtil.getServerRealPathFromSystemProp()+"WEB-INF"+File.separator+"weixicert"+File.separator+"apiclient_cert_"+mch_id+".p12";
	}
    /***
     * 签名
     * @param treeMap
     * @param key
     * @param encode
     * @return
     */
    public static String getSign(TreeMap<String,String> treeMap,String key,String encode){
    	String sign="";
    	StringBuffer stringBuffer = new StringBuffer();
        Set<Entry<String, String>> entrySet = treeMap.entrySet();
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Entry<String,String> entry =  (Entry<String, String>) iterator.next();
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            if (null != mapValue && !"".equals(mapValue) && !"sign".equals(mapKey) && !"key".equals(mapKey)) {
				stringBuffer.append(mapKey + "=" + mapValue + "&");
			}
		}
        stringBuffer.append("key=" + key); 
        sign = MD5Util.md5Encode(stringBuffer.toString(), encode).toUpperCase();
    	return sign;
    }
}
