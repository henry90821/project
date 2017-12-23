package com.smi.pay.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.model.Refund;
import com.smi.pay.sdk.wx.CommonHttp;
import com.smi.pay.sdk.wx.GetWxOrderno;
import com.smi.pay.sdk.wx.RequestHandler;
import com.smi.pay.sdk.wx.Sha1Util;
import com.smi.pay.sdk.wx.WXConfig;
import com.smi.pay.service.WXPayService;
@Service("WXPayService")
public class WXPayServiceImpl implements WXPayService{

	/**
	 * 微信公众号登录，获取授权code  测试用，实际场景由公众号服务端处理
	 * @return ModelAndView
	 * @throws Exception
	 * @since 2016-03-15
	 */
	@Override
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//公从号APPID
		String appid = WXConfig.getConfig().getH5appid(); 
		String backUri = "http://test.vk30.com/smipay/getopenid.do";
	   //URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
		backUri = URLEncoder.encode(backUri);
	   //scope 参数视各自需求而定，这里用scope=snsapi_base 不弹出授权页面直接授权目的只获取统一支付接口的openid
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
						"appid=" + appid+
						"&redirect_uri=" +
						 backUri+
						"&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
				response.sendRedirect(url);
		return null;
	}
	
	/**
	 * 微信公众号授权后，获取openid 测试用，实际场景由公众号服务端处理
	 * @return ModelAndView
	 * @throws Exception
	 * @since 2016-03-15
	 */
	@Override
	public ModelAndView getopenid(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//网页授权后获取传递的参数
		String code = request.getParameter("code");
		//商户相关资料 
		String appid = WXConfig.getConfig().getH5appid(); //"wx30124f4f6d7992a2";
		String appsecret = WXConfig.getConfig().getH5secret();//"90eb4f995498b5781ffdfbc0aa6e2458";
		String partner = WXConfig.getConfig().getPartner();//"1326795801";
		String partnerkey = WXConfig.getConfig().getPartnerkey();//"wuhualong1234567wuhualong1234567";

		String openid ="";
		String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
		JSONObject jsonObject = CommonHttp.httpsRequest(URL, "GET", null);
		if (null != jsonObject) {
			openid = jsonObject.getString("openid");
		}
		Map model = new HashMap();
		model.put("openid", openid);
		return new ModelAndView("wxtest",model);
	}

	@Override
	public void prepay(Order order,OrderReturn orderReturn) throws IOException {
		 
		     //商户相关资料 
				String appid = WXConfig.getConfig().getH5appid();
				String appsecret = WXConfig.getConfig().getH5secret();
				String partner = WXConfig.getConfig().getPartner();
				String partnerkey =  WXConfig.getConfig().getPartnerkey();
				//获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
						String currTime = CommonUtil.getCurrTime();
						 
						//四位随机数
						String strRandom = CommonUtil.buildRandom(4) + "";
						//10位序列号,可以自行调整。
						String strReq = CommonUtil.getCurrTime().substring(8,currTime.length()) + strRandom;				
						//商户号
						String mch_id = partner;
						//设备号   非必输
						//String device_info="";
						//随机数 
						String nonce_str = strReq;			 
						//商品描述根据情况修改
						String body = order.getTitle();
						//附加数据
						String attach = order.getCustId();
						//商户订单号
						String out_trade_no = order.getBillNo();	
						//订单超期时间
						String longtime=order.getExpDate();
						if(StringUtils.isBlank(longtime))
						{
							//获取默认超期时间
							longtime=CommonUtil.getDefaultExpTime(order.getCreateTime()).toString();
						}
						String time_expire=CommonUtil.tformat.format(new Date(Long.parseLong(longtime)+300000));
						
						//这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
						String notify_url =WXConfig.getConfig().getNotify_url();
						
						String openId="";
						if(order.getChannel().equals("WX"))
						{
							openId=order.getOpenId();
						}
						
						String trade_type = "JSAPI"; 
						if(order.getChannel().equals("WEB"))
						{
							trade_type="NATIVE";	
						}
						else if(order.getChannel().equals("APP"))
						{
							trade_type="APP";	
						    appid = WXConfig.getConfig().getAppid();
						    appsecret = WXConfig.getConfig().getSecret();
						    mch_id = WXConfig.getConfig().getApppartner();
						    partnerkey = WXConfig.getConfig().getApppartnerkey();
						}
						//非必输
//						String product_id = "";
						SortedMap<String, String> packageParams = new TreeMap<String, String>();
						packageParams.put("appid", appid);  
						packageParams.put("mch_id", mch_id);  
						packageParams.put("nonce_str", nonce_str);  
						packageParams.put("body", body);  
						packageParams.put("attach", attach);  
						packageParams.put("out_trade_no", out_trade_no);  
									
						//这里写的金额为1 分到时修改
						packageParams.put("total_fee", order.getTotalFee().toString());   
						//packageParams.put("spbill_create_ip", spbill_create_ip);  
						packageParams.put("notify_url", notify_url);  
						packageParams.put("trade_type", trade_type);  
						
						packageParams.put("openid", openId);  
						packageParams.put("time_expire", time_expire);  

						RequestHandler reqHandler = new RequestHandler();
						reqHandler.init(appid, appsecret, partnerkey);
						
						String sign = reqHandler.createSign(packageParams);
						String xml="<xml>"+
								"<appid>"+appid+"</appid>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonce_str+"</nonce_str>"+
								"<sign>"+sign+"</sign>"+
								"<body><![CDATA["+body+"]]></body>"+
								"<attach>"+attach+"</attach>"+
								"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
								//金额，这里写的1 分到时修改
								"<total_fee>"+order.getTotalFee().toString()+"</total_fee>"+ 
								//"<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>"+
								"<notify_url>"+notify_url+"</notify_url>"+
								"<trade_type>"+trade_type+"</trade_type>"+
								"<openid>"+openId+"</openid>"+
								"<time_expire><![CDATA["+time_expire+"]]></time_expire>"+
								"</xml>";
						System.out.println(xml);
						String allParameters = "";
						try {
							allParameters =  reqHandler.genPackage(packageParams);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
						String prepay_id="";
						try {
							prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml,trade_type);
							if("".equals(prepay_id)){
								 
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						String timestamp = Sha1Util.getTimeStamp();
						String finalsign="";
						String packagevalue="";
						//返APP的签名参数
						 if(order.getChannel().equals("APP"))
						 {
							 packagevalue="Sign=WXPay";
							 SortedMap<String, String> appMap = new TreeMap<String, String>(); 
							 appMap.put("appid", appid); 
							 appMap.put("noncestr",nonce_str); 
							 appMap.put("package",packagevalue); 
							 appMap.put("partnerid", mch_id); 
							 appMap.put("prepayid", prepay_id); 
							 appMap.put("timestamp", timestamp);
							 RequestHandler appReqHandler = new RequestHandler();
							 appReqHandler.init(appid, appsecret, partnerkey);
							 finalsign = reqHandler.createSign(appMap);
						 }
						//返回给H5的签名参数
						 else if(order.getChannel().equals("WX"))
						 {
							    SortedMap<String, String> finalpackage = new TreeMap<String, String>();
								packagevalue = "prepay_id="+prepay_id;
								finalpackage.put("appId", appid);  
								finalpackage.put("timeStamp", timestamp);  
								finalpackage.put("nonceStr", nonce_str);  
								finalpackage.put("package", packagevalue);  
								finalpackage.put("signType", "MD5");
								finalsign = reqHandler.createSign(finalpackage);
						 }
							
							orderReturn.setAppId(appid);
							orderReturn.setPartnerId(mch_id);
							orderReturn.setPackageValue(packagevalue);
							orderReturn.setNonceStr(nonce_str);
							orderReturn.setTimestamp(timestamp);
							orderReturn.setPaySign(finalsign);
							orderReturn.setSignType("MD5");
							orderReturn.setPrepayId(prepay_id);
	}

	@Override
	public Map refund(Refund refund,Order order) throws IOException {
		       Map returnDate=new HashMap();
		     //商户相关资料 
		        
				String appid = WXConfig.getConfig().getH5appid();
				String appsecret = WXConfig.getConfig().getH5secret();
				String partner = WXConfig.getConfig().getPartner();
				String partnerkey =  WXConfig.getConfig().getPartnerkey();
				if(order.getChannel().equals("APP"))
				{
					appid=WXConfig.getConfig().getAppid();
					appsecret = WXConfig.getConfig().getSecret();
					partner = WXConfig.getConfig().getApppartner();
					 partnerkey =  WXConfig.getConfig().getApppartnerkey();
				}
				String currTime = CommonUtil.getCurrTime();
				//四位随机数
				String strRandom = CommonUtil.buildRandom(4) + "";
				//10位序列号,可以自行调整。
				String nonce_str = CommonUtil.getCurrTime().substring(8,currTime.length()) + strRandom;				
				//商户号
				String mch_id = partner;
				
				 SortedMap<String, String> packageParams = new TreeMap<String, String>();
					packageParams.put("appid", appid);  
					packageParams.put("mch_id", mch_id);  
					packageParams.put("nonce_str", nonce_str);  
					packageParams.put("transaction_id", order.getPaySn());  
					packageParams.put("out_trade_no", refund.getBillNo());  
					packageParams.put("out_refund_no", refund.getRefundNo());  
					//这里写的金额为1 分到时修改
					packageParams.put("total_fee", order.getTotalFee().toString());   
					//packageParams.put("spbill_create_ip", spbill_create_ip);  
					packageParams.put("refund_fee", refund.getRefundFee().toString());  
					packageParams.put("op_user_id", mch_id);  
					RequestHandler reqHandler = new RequestHandler();
					reqHandler.init(appid, appsecret, partnerkey);
					String sign = reqHandler.createSign(packageParams);
				  String xmlParam="<xml>"+
								"<appid>"+appid+"</appid>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonce_str+"</nonce_str>"+
								"<sign>"+sign+"</sign>"+
								"<transaction_id>"+order.getPaySn()+"</transaction_id>"+
								"<out_trade_no>"+refund.getBillNo()+"</out_trade_no>"+
								"<out_refund_no>"+refund.getRefundNo()+"</out_refund_no>"+
								//金额，这里写的1 分到时修改
								"<total_fee>"+order.getTotalFee().toString()+"</total_fee>"+ 
								"<refund_fee>"+refund.getRefundFee().toString()+"</refund_fee>"+
								"<op_user_id>"+mch_id+"</op_user_id>"+
								"</xml>";
				String refundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
				returnDate=GetWxOrderno.refund(refundUrl, xmlParam,order.getChannel());
				return returnDate;
	}

}
