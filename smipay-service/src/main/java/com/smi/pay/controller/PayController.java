package com.smi.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.smi.pay.common.BarcodeUtil;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderLog;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.sdk.ali.AlipayConfig;
import com.smi.pay.sdk.ali.AlipayNotify;
import com.smi.pay.sdk.ali.AlipaySubmit;
import com.smi.pay.sdk.union.AcpService;
import com.smi.pay.sdk.union.SDKConfig;
import com.smi.pay.sdk.union.SDKConstants;
import com.smi.pay.sdk.wx.MD5Util;
import com.smi.pay.sdk.wx.WXConfig;
import com.smi.pay.service.PayService;
import com.smi.pay.service.WXPayService;
import com.smi.pay.service.XMPayService;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Api(value = "支付控制器")
@Controller
public class PayController {
	@Autowired
	private WXPayService wxPayService;
	@Autowired
	private PayService payService;
	@Autowired
	private XMPayService xmPayService;

	// 统一下单接口
    @ApiOperation(value = "统一下单接口")
	@ResponseBody
	@RequestMapping(value = "/bill", method = {RequestMethod.POST})
	public OrderReturn createOrder(@ApiParam(name = "orderLog", required = true) @RequestBody OrderLog orderLog,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// orderLog.setBillNo(orderLog.getBillNo().toLowerCase());
		if ("3".equals(orderLog.getBillType())) {
			orderLog.setBillNo(CommonUtil.tformat.format(new Date()) + orderLog.getBillNo());
		}

		OrderReturn orderReturn = payService.check(orderLog);
		if (orderReturn.getCode() == 0) {
			if (orderLog.getChannel().equals("WEB")) {
				orderReturn.setUrl(CommonUtil.getContextPath(request) + "/webpay.do?order_id=" + orderLog.getId());
			} else if (orderLog.getChannel().equals("WAP") || orderLog.getChannel().equals("WX")) {
				orderReturn.setUrl(CommonUtil.getContextPath(request) + "/wappay.do?order_id=" + orderLog.getId());
			} else if (orderLog.getChannel().equals("APP")) {
				// APP下单直接生成正式订单，等待回调
				Order order = payService.saveOrder(orderLog);
				String paytype = order.getPayType();
				// 微信支付,返回预支付信息给移动端,供移动端调起本地微信支付
				if (paytype.equals("WX")) {
					// 组装预支付定单信息
					wxPayService.prepay(order, orderReturn);
				}
				// 支付宝支付，返回预支付信息给移动端,供移动端调起本地支付宝支付
				else if (paytype.equals("ALI")) {
					String out_trade_no = order.getBillNo();
					// 订单名称，必填
					String subject = order.getTitle();
					// 付款金额，必填
					String total_fee = String.valueOf(order.getTotalFee() / 100f);
					// 商品描述，可空
					String body = "";// new
										// String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
					// 把请求参数打包成数组
					Map<String, String> sParaTemp = new HashMap<String, String>();
					// sParaTemp.put("service",
					// AlipayConfig.getConfig().service);
					sParaTemp.put("service", "mobile.securitypay.pay");
					sParaTemp.put("partner", AlipayConfig.getConfig().partner);
					sParaTemp.put("seller_id", AlipayConfig.getConfig().seller_id);
					sParaTemp.put("_input_charset", AlipayConfig.getConfig().input_charset);
					sParaTemp.put("payment_type", AlipayConfig.getConfig().payment_type);
					sParaTemp.put("notify_url", URLEncoder.encode(AlipayConfig.getConfig().notify_url, "UTF-8"));
					// sParaTemp.put("return_url",
					// AlipayConfig.getConfig().return_url);
					// sParaTemp.put("return_url", order.getReturnUrl());
					sParaTemp.put("out_trade_no", out_trade_no);
					sParaTemp.put("subject", subject);
					sParaTemp.put("total_fee", total_fee);
					sParaTemp.put("body", subject);
					String expDate = order.getExpDate();
					String expMin = CommonUtil.getExpMin();
					if (StringUtils.isNotBlank(expDate)) {
						expMin = String.valueOf((Long.parseLong(expDate) - new Date().getTime()) / (1000 * 60));
					}
					sParaTemp.put("it_b_pay", expMin + "m");
					// 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
					// 如sParaTemp.put("参数名","参数值");
					// 建立请求
					// 待请求参数数组
					// Map<String, String> appParam =
					// AlipaySubmit.buildAppParam(sParaTemp);
					// String sign = AlipaySubmit.buildRequestMysign(sParaTemp);
					String sign = AlipaySubmit.buildAppParam(sParaTemp);
					orderReturn.setPaySign(sign);
				}
				// 余额或影票支付,直接发起支付,返回支付结果给移动端
				else if (paytype.equals("YE") || paytype.equals("YP")) {
					// 移动端传过来的是加密过的密码
					if (!xmPayService.checkPassword(orderLog.getCustId(), order.getPayPwd())) {
						order.setCallBackStatus("1");
						order.setCallBackMemo("密码验证失败");
						order.setCallBackTime(new Date());
					} else {
						Map result = xmPayService.pay(order);
						order.setStatus(result.get("status").toString());
						order.setCallBackStatus(result.get("status").toString());
						order.setCallBackMemo(result.get("msg").toString());
						order.setCallBackTime(new Date());
					}

					payService.updateOrder(order);
					// 支付成功，直接回调
					if (order.getCallBackStatus().equals("0")) {
						payService.payNotify(order);
					}

				}
				// 需进一步完善星美随便花支付功能
				else if ("SBH".equals(paytype)) { // 随便花支付方式创建订单
					if (!xmPayService.checkPassword(orderLog.getCustId(), order.getPayPwd())) {
						order.setCallBackStatus("1");
						order.setCallBackMemo("密码验证失败");
						order.setCallBackTime(new Date());
					} else {
						BaseValueObject resultObject = this.xmPayService.sbhPay(order);
						orderReturn.setErrDetail(resultObject.getMsg());
					}
				}
			}
		}
		orderLog.setCode(orderReturn.getCode().toString());
		orderLog.setMsg(orderReturn.getMsg());
		orderLog.setReturnDetail(JSONObject.toJSONString(orderReturn));
		payService.updateOrderLog(orderLog);
		return orderReturn;
	}

	// 跳转到PC支付页面
	@RequestMapping("webpay")
	public ModelAndView webpay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String orderId = request.getParameter("order_id");
		OrderLog orderLog = payService.getOrderLog(Integer.parseInt(orderId));
		Order order = payService.getOrderByBillNo(orderLog.appcode + orderLog.billNo);
		if (CommonUtil.isExpire(orderLog.getExpDate(), orderLog.getCreateTime())) {
			// 组装预支付定单信息
			if (order == null) {
				order = payService.saveOrder(orderLog);
			}
			Map model = new HashMap();
			order.setStatus("1");
			order.setCallBackMemo("订单已经过期");
			model.put("order", order);
			return new ModelAndView("payresult", model);
		}
		// 如果对应订单已经支付，直接跳转到支付成功页面
		if (order != null && "0".equals(order.getStatus())) {
			Map model = new HashMap();
			model.put("order", order);
			return new ModelAndView("payresult", model);
		}
		Map model = new HashMap();
		model.put("order", orderLog);
		if (!"2".equals(orderLog.getBillType())) {
			Map dataMap = getTestMap();
			if (!WXConfig.FILE_NAME.equals("sdk_wx_test.properties")) {
				dataMap = xmPayService.getBal(orderLog.getCustId());
			}
			model.put("xminfo", dataMap);
		}

		return new ModelAndView("webpay", model);
	}

	// 跳转到WAP支付页面
	@RequestMapping("wappay")
	public ModelAndView wappay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String orderId = request.getParameter("order_id");
		OrderLog orderLog = payService.getOrderLog(Integer.parseInt(orderId));
		Order order = payService.getOrderByBillNo(orderLog.appcode + orderLog.billNo);
		if (CommonUtil.isExpire(orderLog.getExpDate(), orderLog.getCreateTime())) {
			// 组装预支付定单信息
			if (order == null) {
				order = payService.saveOrder(orderLog);
			}
			Map model = new HashMap();
			order.setStatus("1");
			order.setCallBackMemo("订单已经过期");
			model.put("order", order);
			return new ModelAndView("payresult", model);
		}
		// 如果对应订单已经支付，直接跳转到支付成功页面
		if (order != null && "0".equals(order.getStatus())) {
			Map model = new HashMap();
			model.put("order", order);
			return new ModelAndView("payresult", model);
		}
		Map model = new HashMap();
		model.put("order", orderLog);
		// 组装星美余额和星币信息
		if (!"2".equals(orderLog.getBillType())) {
			Map dataMap = getTestMap();
			if (!WXConfig.FILE_NAME.equals("sdk_wx_test.properties")) {
				dataMap = xmPayService.getBal(orderLog.getCustId());
			}
			model.put("xminfo", dataMap);
		}
		// 如果是微信支付，需要获取微信的预支付信息
		if (orderLog.getChannel().equals("WX")) {

			OrderReturn orderReturn = new OrderReturn();
			// 组装预支付定单信息
			if (order == null) {
				order = payService.saveOrder(orderLog);
			}

			wxPayService.prepay(order, orderReturn);
			model.put("orderReturn", orderReturn);
		}
		return new ModelAndView("wappay", model);
	}

	// WAP和PC版点确认支付时呼叫第三方支付接口
	@RequestMapping("topay")
	public ModelAndView topay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String orderId = request.getParameter("order_id");
		String paytype = request.getParameter("payType");
		String channel = request.getParameter("channel");
		Map model = new HashMap();
		OrderLog orderLog = payService.getOrderLog(Integer.parseInt(orderId));
		Order history = payService.getOrderByBillNo(orderLog.appcode + orderLog.billNo);
		if (history != null && "0".equals(history.getStatus())) {
			history.setStatus("1");
			history.setCallBackMemo("不能重复下单");
			model.put("order", history);
			return new ModelAndView("payresult", model);
		}
		Order order = payService.saveOrder(orderLog);
		if (CommonUtil.isExpire(order.getExpDate(), order.getCreateTime())) {
			order.setStatus("1");
			order.setCallBackMemo("订单已经过期");
			model.put("order", order);
			return new ModelAndView("payresult", model);
		}

		else if (paytype.equals("1") || paytype.equals("2")) // 余额/影票支付
		{
			order.setPayType(paytype.equals("1") ? "YE" : "YP");
			payService.updateOrder(order);
			String password = request.getParameter("password");
			if (StringUtils.isBlank(password)) {
				order.setCallBackStatus("1");
				order.setCallBackMemo("支付密码不能为空");
				order.setCallBackTime(new Date());
			} else if (!xmPayService.checkPassword(orderLog.getCustId(), MD5Util.MD5(password))) {
				order.setCallBackStatus("1");
				order.setCallBackMemo("密码验证失败");
				order.setCallBackTime(new Date());
			} else {
				Map result = xmPayService.pay(order);
				order.setStatus(result.get("status").toString());
				order.setCallBackStatus(result.get("status").toString());
				order.setCallBackMemo(result.get("msg").toString());
				order.setCallBackTime(new Date());
			}

			payService.updateOrder(order);
			// 支付成功，直接回调
			if (order.getCallBackStatus().equals("0")) {
				payService.payNotify(order);
			}
			model.put("order", order);
			return new ModelAndView("payresult", model);
		} else if (paytype.equals("3")) // 支付宝支付
		{
			order.setPayType("ALI");
			payService.updateOrder(order);
			String out_trade_no = order.getBillNo();
			// 订单名称，必填
			String subject = order.getTitle();
			// 付款金额，必填
			String total_fee = String.valueOf(Float.parseFloat(orderLog.getTotalFee().toString()) / 100f);
			// 商品描述，可空
			String body = "";// new
								// String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			if (channel.equals("WEB")) {
				sParaTemp.put("service", AlipayConfig.getConfig().service);
			} else {
				sParaTemp.put("service", AlipayConfig.getConfig().wapservice);
			}

			sParaTemp.put("partner", AlipayConfig.getConfig().partner);
			sParaTemp.put("seller_id", AlipayConfig.getConfig().seller_id);
			sParaTemp.put("_input_charset", AlipayConfig.getConfig().input_charset);
			sParaTemp.put("payment_type", AlipayConfig.getConfig().payment_type);
			sParaTemp.put("notify_url", AlipayConfig.getConfig().notify_url);
			sParaTemp.put("return_url", AlipayConfig.getConfig().return_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			String expDate = order.getExpDate();
			String expMin = CommonUtil.getExpMin();
			if (StringUtils.isNotBlank(expDate)) {
				expMin = String.valueOf((Long.parseLong(expDate) - new Date().getTime()) / (1000 * 60));
			}
			LogUtil.writeLog("--------支付宝支付超期时间限制：" + expMin);
			sParaTemp.put("it_b_pay", expMin + "m");
			// 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
			// 如sParaTemp.put("参数名","参数值");
			// 建立请求
			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
			// (sHtmlText);
			response.setCharacterEncoding("UTF-8");// 设置Response的编码方式为UTF-8
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.getWriter().write(sHtmlText);
		} else if (paytype.equals("4"))// 微信支付
		{
			order.setPayType("WX");
			payService.updateOrder(order);
			OrderReturn or = new OrderReturn();
			or.setCode(0);
			or.setMsg("OK");
			// 组装预支付定单信息
			wxPayService.prepay(order, or);
			// 生成二维码图片
			String filePath = request.getSession().getServletContext().getRealPath("") + "/barcode/";
			String fileName = BarcodeUtil.createBarcode(filePath, or.getPrepayId());
			// response.sendRedirect("/barcode/"+fileName);
			Map param = new HashMap();
			param.put("order", order);
			param.put("fileName", fileName);
			return new ModelAndView("webwx", param);
		} else if (paytype.equals("5"))// 银联支付
		{
			order.setPayType("UN");
			payService.updateOrder(order);
//			String realPath = request.getSession().getServletContext().getRealPath("");
//			SDKConfig.getConfig().loadPropertiesFromSrc(realPath);

			// 前台页面传过来的
			String merId = SDKConfig.getConfig().getMerId();
			String txnAmt = orderLog.getTotalFee().toString();
			Map<String, String> requestData = new HashMap<String, String>();
			/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
			requestData.put("version", "5.0.0"); // 版本号，全渠道默认值
			requestData.put("encoding", CommonUtil.encoding_UTF8); // 字符集编码，可以使用UTF-8,GBK两种方式
			requestData.put("signMethod", "01"); // 签名方法，只支持 01：RSA方式证书加密
			requestData.put("txnType", "01"); // 交易类型 ，01：消费
			requestData.put("txnSubType", "01"); // 交易子类型， 01：自助消费
			requestData.put("bizType", "000201"); // 业务类型，B2C网关支付，手机wap支付
			requestData.put("channelType", "07"); // 渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板
													// 08：手机
			/*** 商户接入参数 ***/
			requestData.put("merId", merId); // 商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
			requestData.put("accessType", "0"); // 接入类型，0：直连商户
			requestData.put("orderId", order.getBillNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
			requestData.put("txnTime", CommonUtil.getCurrTime()); // 订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
			requestData.put("currencyCode", "156"); // 交易币种（境内商户一般是156 人民币）
			requestData.put("txnAmt", txnAmt); // 交易金额，单位分，不要带小数点
			// requestData.put("reqReserved", "透传字段");
			// //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节

			// 前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面
			// 点击“返回商户”按钮的时候将异步通知报文post到该地址
			// 如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
			// 异步通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 消费交易 商户通知
			requestData.put("frontUrl", SDKConfig.getConfig().getReturnUrl());

			// 后台通知地址（需设置为【外网】能访问 http
			// https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
			// 后台通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 消费交易 商户通知
			// 注意:1.需设置为外网能访问，否则收不到通知 2.http https均可
			// 3.收单后台通知后需要10秒内返回http200或302状态码
			// 4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
			// 5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d
			// 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
			requestData.put("backUrl", SDKConfig.getConfig().getNotifyUrl());

			// ////////////////////////////////////////////////
			//
			// 报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
			//
			// ////////////////////////////////////////////////

			/** 请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面 **/
			Map<String, String> submitFromData = AcpService.sign(requestData, CommonUtil.encoding_UTF8); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

			String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl(); // 获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
			String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData, CommonUtil.encoding_UTF8); // 生成自动跳转的Html表单

			LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
			// 将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
			response.getWriter().write(html);
		}
		return null;// new ModelAndView("webpay",model);
	}

	// ALI支付成功后跳转页面
	@RequestMapping("alireturn")
	public ModelAndView alireturn(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		// request.setCharacterEncoding("utf-8");
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		Order order = payService.getOrderByBillNo(out_trade_no);
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if (AlipayNotify.verify(params)) {// 验证成功
			// 支付成功
			if (trade_status.equals("TRADE_SUCCESS")) {
				order.setStatus("0");
			}
			payService.updateOrder(order);

		}

		Map model = new HashMap();
		model.put("order", order);
		return new ModelAndView("payresult", model);
	}

	// 银联支付成功后跳转页面
	@RequestMapping("unreturn")
	public ModelAndView chinareturn(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String realPath = request.getSession().getServletContext().getRealPath("");
		LogUtil.writeLog("FrontRcvResponse前台接收报文返回开始");
		request.setCharacterEncoding("ISO-8859-1");
		String encoding = request.getParameter(SDKConstants.param_encoding);
		LogUtil.writeLog("返回报文中encoding=[" + encoding + "]");

		Map<String, String> respParam = CommonUtil.getAllRequestParam(request);
		// 打印请求报文
		LogUtil.printRequestLog(respParam);
		Map<String, String> valideData = null;
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet().iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}
		Order order = payService.getOrderByBillNo(valideData.get("orderId"));
		if (AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("银联支付验证签名结果[成功].");
			order.setStatus("0");
			payService.updateOrder(order);
		}
		Map model = new HashMap();
		model.put("order", order);
		return new ModelAndView("payresult", model);
	}

	// 微信扫码时，前台ajax检查订单状态
	@ResponseBody
	@RequestMapping("/checkOrder")
	public String checkOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String order_id = request.getParameter("order_id");
		Order order = payService.getOrder(Integer.parseInt(order_id));
		return order.getStatus();
	}

	// 支付结果界面
	@RequestMapping("/payresult")
	public ModelAndView payresult(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String order_id = request.getParameter("order_id");
		Order order = payService.getOrder(Integer.parseInt(order_id));
		Map model = new HashMap();
		model.put("order", order);
		return new ModelAndView("payresult", model);
	}

	public static Map getTestMap() {
		Map dataMap = new HashMap();
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		dataMap.put("totalAll", decimalFormat.format(345.5)); // 合计可用的余额
		dataMap.put("totalBal", decimalFormat.format(340)); // 界面显示的账户余额
		dataMap.put("xingCoin", 450); // 界面显示的星币数量
		dataMap.put("coinBal", 4.5); // 界面显示的星币可兑换余额
		dataMap.put("totalYp", 14); // 电影票数量
		return dataMap;

	}

	// 验证订单是否已经支付成功
	@RequestMapping("/ordercheck")
	public void ordercheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String order_id = request.getParameter("order_id");
		OrderLog orderLog = payService.getOrderLog(Integer.parseInt(order_id));
		Order order = payService.getOrderByBillNo(orderLog.appcode + orderLog.billNo);
		if (CommonUtil.isExpire(orderLog.getExpDate(), orderLog.getCreateTime())) {
			response.getWriter().write("2");
		} else if (order != null && order.getStatus() != null && order.getStatus().equals("0")) {
			response.getWriter().write("0");
		} else {
			response.getWriter().write("1");
		}
	}

}
