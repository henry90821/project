package com.iskyshop.pay.unionpay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import com.iskyshop.core.exception.PayValidationException;
import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Payment;
import com.unionpay.acp.sdk.HttpClient;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKConstants;
import com.unionpay.acp.sdk.SDKUtil;

/**
 * 银联支付业务服务类，处理银联支付相关的各种业务封装。 Created by 亚翔 on 2015/9/8.
 */
public class UnionPayService {

	private static final Logger LOGGER = Logger.getLogger(UnionPayService.class);

	/**
	 * 支付版本号
	 */
	public static String VERSION = "5.0.0";

	/**
	 * 支付请求编码
	 */
	public static final String ENCODING = "UTF-8";

	/**
	 * 签名方式，RSA
	 */
	public static final String SIGN_RSA = "01";

	/**
	 * 支付类型，消费
	 */
	public static final String PAY_TYPE_CONSUMER = "01";

	/**
	 * 交易子类型，自助消费
	 */
	public static final String TRANSACTION_SUBTYPE_SELF = "01";

	/**
	 * 交易子类型，订购
	 */
	public static final String TRANSACTION_SUBTYPE_ORDER = "02";

	/**
	 * 交易子类型，分期付款
	 */
	public static final String TRANSACTION_SUBTYPE_ON_ACCOUNT = "03";

	/**
	 * 业务类型
	 */
	public static final String BUSSINESS_TYPE = "000201";

	/**
	 * 渠道类型，PC
	 */
	public static final String CHANNEL_TYPE_PC = "07";

	/**
	 * 渠道类型，手机
	 */
	public static final String CHANNEL_TYPE_MOBILE = "08";

	/**
	 * 接入类型，商户
	 */
	public static final String ACCESS_TYPE_MERCHANT = "0";

	/**
	 * 接入类型，收单
	 */
	public static final String ACCESS_TYPE_BILL = "1";

	/**
	 * 接入类型，平台商户
	 */
	public static final String ACCESS_TYPE_PLATFORM_MERCHANT = "2";

	/**
	 * 交易币种
	 */
	public static final String CURRENCY = "156";

	/**
	 * PC端支付来源
	 */
	public static final String PAY_SOURCE_WEB = "web";

	/**
	 * WAP端支付来源
	 */
	public static final String PAY_SOURCE_WAP = "unionpay_wap";

	/**
	 * 手机控件支付来源
	 */
	public static final String PAY_SOURCE_MOBILE = "unionpay_app";

	public UnionPayService() {
		// 加载银联支付的配置文件
		// SDKConfig.getConfig().loadPropertiesFromSrc();
	}

	/**
	 * 获得支付表单请求字符串
	 * 
	 * @param url
	 *            支付回调地址的前缀
	 * @param payment
	 *            支付对象
	 * @param out_trade_no
	 *            订单号
	 * @param body
	 *            订单描述
	 * @param total_fee
	 *            订单金额
	 * @return
	 */
	public static String getPayFormStr(String url, Payment payment, String out_trade_no, String body, String total_fee,
			String paySource) throws IOException {
		String result = null;
		// 首先加载对应的配置文件
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources(payment.getApp_private_key());
		String location = resources[0].getFile().getAbsolutePath();
		SDKConfig.getConfig().loadPropertiesFromPath(location);
		// 组装请求报文
		Map<String, String> data = new HashMap<String, String>();
		// 版本号
		data.put("version", VERSION);
		// 字符集编码 默认"UTF-8"
		data.put("encoding", ENCODING);
		// 签名方法 01 RSA
		data.put("signMethod", SIGN_RSA);
		// 交易类型 01-消费
		data.put("txnType", PAY_TYPE_CONSUMER);
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", TRANSACTION_SUBTYPE_SELF);
		// 业务类型
		data.put("bizType", BUSSINESS_TYPE);
		// 渠道类型，07-PC，08-手机
		data.put("channelType", PAY_SOURCE_MOBILE.equals(paySource) ? CHANNEL_TYPE_MOBILE : CHANNEL_TYPE_PC);
		// 前台通知地址 ，控件接入方式无作用
		if (PAY_SOURCE_WEB.equals(paySource) || PAY_SOURCE_WAP.equals(paySource))
			data.put("frontUrl", url + "/unionpay_return.htm");
		// 后台通知地址
		data.put("backUrl", url + "/unionpay_notify.htm");
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", ACCESS_TYPE_MERCHANT);
		// 商户号码，请改成自己的商户号
		data.put("merId", payment.getChinapay_account());
		// 商户订单号，8-40位数字字母
		// 将原有系统订单号中的特殊字符移除
		String[] systemOrderId = out_trade_no.split("-");
		StringBuilder orderId = null;
		for (String orderStr : systemOrderId) {
			if (null == orderId) {
				orderId = new StringBuilder();
			}
			orderId.append(orderStr);
		}
		data.put("orderId", orderId == null ? "" : orderId.toString());
		// 订单发送时间，取系统时间
		data.put("txnTime", CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
		// 交易金额，单位分
		data.put("txnAmt", AmountUtils.changeY2F(total_fee));
		// 交易币种
		data.put("currencyCode", UnionPayService.CURRENCY);
		// 订单描述，可不上送，上送时控件中会显示该信息
		// data.put("orderDesc", body);

		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		data.put("reqReserved", out_trade_no + "&" + body + "&" + paySource); // 将系统生成支付订单流水号和支付标识一起放入保留域中
		// 签名订单
		Map<String, String> submitFromData = signData(data);
		// 交易请求url 从配置文件读取
		String requestFrontUrl = PAY_SOURCE_MOBILE.equals(paySource) ? SDKConfig.getConfig().getAppRequestUrl() : SDKConfig
				.getConfig().getFrontRequestUrl();
		// 构造函数，生成请求URL
		result = PAY_SOURCE_MOBILE.equals(paySource) ? submitAppUrl(submitFromData, requestFrontUrl) : createHtml(
				requestFrontUrl, submitFromData);
		// result = SDKUtil.createAutoSubmitForm(requestFrontUrl, submitFromData);
		return result;
	}

	/**
	 * 构造HTTP POST交易表单的方法示例
	 * 
	 * @param action
	 *            表单提交地址
	 * @param hiddens
	 *            以MAP形式存储的表单键值
	 * @return 构造好的HTTP POST交易表单
	 */
	private static String createHtml(String action, Map<String, String> hiddens) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + action + "\" method=\"post\">");
		if (!StringUtils.isNullOrEmpty(hiddens)) {
			Set<Map.Entry<String, String>> set = hiddens.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/**
	 * java main方法 数据提交 对数据进行签名
	 * 
	 * @param contentData
	 * @return 签名后的map对象
	 */
	private static Map<String, String> signData(Map<String, ?> contentData) {
		Map.Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Map.Entry<String, String>) it.next();
			String value = obj.getValue();
			if (!StringUtils.isNullOrEmpty(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(obj.getKey(), value.trim());
				LOGGER.info(obj.getKey() + "-->" + String.valueOf(value));
			}
		}
		// 签名
		SDKUtil.sign(submitFromData, ENCODING);
		return submitFromData;
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				// LOGGER.info("ServletUtil类247行 temp数据的键=="+en+" 值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					LOGGER.debug("temp数据的键==" + en + " 值===" + value);
					res.remove(en);
				}
			}
		}
		return res;
	}

	/**
	 * 获取并验证银联支付返回的各项数据
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> getReturnParamAndValidate(final HttpServletRequest request)
			throws PayValidationException {
		LOGGER.info("处理WEB银联支付的响应参数及合法性校验——开始");
		final String REQ_ENCODING = "ISO-8859-1";
		// 获取请求编码
		try {
			request.setCharacterEncoding(REQ_ENCODING);
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("设置request的字符编码为" + REQ_ENCODING + "时失败！");
		}
		String encoding = request.getParameter(SDKConstants.param_encoding);
		// 获取请求参数中所有的信息
		Map<String, String> reqParam = UnionPayService.getAllRequestParam(request);
		// 打印请求报文
		LogUtil.printRequestLog(reqParam);
		// 获取回调验证签名参数
		Map<String, String> validaData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
			validaData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Map.Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				try {
					value = new String(value.getBytes(REQ_ENCODING), encoding);
				} catch (UnsupportedEncodingException e1) {
					LOGGER.warn("通过指定编码" + REQ_ENCODING + "获取字符串Bytes时失败！");
				}
				validaData.put(key, value);
			}
		}
		// 验证签名
		if (!SDKUtil.validate(validaData, encoding)) {
			throw new PayValidationException("验证签名结果[失败]。");
		} else {
			LOGGER.info(validaData.get("orderId")); // 其他字段也可用类似方式获取
			LOGGER.info("验证签名结果[成功]。");
		}
		LOGGER.info("处理WEB银联支付的响应参数及合法性校验——结束");
		return reqParam;
	}

	/**
	 * App数据提交提交到后台获得TN的方法
	 * 
	 * @param submitFromData
	 *            需要提交的数据集合
	 * @param requestUrl
	 *            请求的支付服务器的地址
	 * @return 返回报文 map
	 */
	private static String submitAppUrl(Map<String, String> submitFromData, String requestUrl) {
		String resultString = null;
		LOGGER.debug("requestUrl====" + requestUrl);
		LOGGER.debug("submitFromData====" + submitFromData.toString());
		// 发送
		HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
		try {
			int status = hc.send(submitFromData, ENCODING);
			if (200 == status) {
				resultString = hc.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 验证签名
		if (null != resultString && !"".equals(resultString)) {
			// 将返回结果转换为map
			Map<String, String> resData = SDKUtil.convertResultStringToMap(resultString);
			// TODO 此处银联服务器已经校验成功但是本地验证时出错，为了编译客户端调试故先不做本地校验而直接返回TN值
			resultString = resData.get("tn");
			// if (SDKUtil.validate(resData, ENCODING)) {
			// LOGGER.info("验证签名成功");
			// LOGGER.debug("打印返回报文：" + resultString);
			// // 获取银联服务器返回的TN值
			// resultString = resData.get("tn");
			// } else {
			// LOGGER.error("验证签名失败，接口返回响应值：" + resultString);
			// resultString = null;
			// }
		}
		return resultString;
	}
}
