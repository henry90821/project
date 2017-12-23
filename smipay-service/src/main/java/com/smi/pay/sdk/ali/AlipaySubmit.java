package com.smi.pay.sdk.ali;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

/* *
 * 类名：AlipaySubmit功能：支付宝各接口请求提交类详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据版本：3.3
 * 日期：2012-08-13说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {
    
	@Resource
	private AlipayConfig alipayConfig;
	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static final String	ALIPAY_GATEWAY_NEW	= "https://mapi.alipay.com/gateway.do?";

	/**
	 * 生成签名结果
	 * 
	 * @param sPara
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestMysign(Map<String, String> sPara) {
		String prestr = AlipayCore.createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		if (AlipayConfig.getConfig().sign_type.equals("MD5")) {
			mysign = MD5.sign(prestr, AlipayConfig.getConfig().key, AlipayConfig.getConfig().input_charset);
		}
		return mysign;
	}

	/**
	 * 生成要请求给支付宝的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @return 要请求的参数数组
	 */
	private static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = buildRequestMysign(sPara);

		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);
		sPara.put("sign_type", AlipayConfig.getConfig().sign_type);

		return sPara;
	}

	/**
	 * 建立请求，以表单HTML形式构造（默认）
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
	public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(sParaTemp);
		List<String> keys = new ArrayList<String>(sPara.keySet());

		StringBuffer sbHtml = new StringBuffer();

		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW + "_input_charset=" + AlipayConfig.getConfig().input_charset + "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		// submit按钮控件请不要含有name属性
		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

		return sbHtml.toString();
	}

	/**
	 * 建立请求，以表单HTML形式构造（默认）
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
	public static String buildAppParam(Map<String, String> sParaTemp) throws UnsupportedEncodingException {

		return rsaSign(sParaTemp);
	}

	/**
	 * 生成签名结果
	 * 
	 * @param sPara
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
	public static String rsaSign(Map<String, String> sPara) {
		String prestr = AlipayCore.createAppString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		try {
			String mysign = URLEncoder.encode(SignUtils.sign(prestr, AlipayConfig.getConfig().ras_private), "UTF-8");
			prestr = prestr + "&sign=\"" + mysign + "\"&sign_type=\"RSA\"";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return prestr;
	}

	// 特殊字符处理
	public static String UrlEncode(String src) throws UnsupportedEncodingException {
		if (StringUtils.isBlank(src)) {
			return src;
		}
		return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
	}

}
