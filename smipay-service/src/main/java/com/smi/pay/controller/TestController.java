package com.smi.pay.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderLog;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.model.Refund;
import com.smi.pay.model.RefundLog;
import com.smi.pay.sdk.ali.AlipayConfig;
import com.smi.pay.sdk.ali.AlipaySubmit;
import com.smi.pay.sdk.union.AcpService;
import com.smi.pay.sdk.union.SDKConfig;
import com.smi.pay.sdk.wx.WXConfig;
import com.smi.pay.service.PayService;
import com.smi.pay.service.RefundService;
import com.smi.pay.service.WXPayService;
import com.smi.pay.service.XMPayService;

@Controller
public class TestController {
	@Autowired
	private WXPayService	wxPayService;
	@Autowired
	private PayService		payService;
	@Autowired
	private XMPayService	xmPayService;
	@Autowired
	private RefundService	refundService;

	// 统一下单接口
	@ResponseBody
	@RequestMapping("/testbill")
	public OrderReturn createOrder(OrderLog orderLog, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ("3".equals(orderLog.getBillType())) {
			orderLog.setBillNo(CommonUtil.tformat.format(new Date()) + orderLog.getBillNo());
		}
		OrderReturn orderReturn = payService.check(orderLog);
		if (orderReturn.getCode() == 0) {
			if (orderLog.getChannel().equals("WEB")) {
				orderReturn.setUrl(CommonUtil.getContextPath(request) + "/webpay.do?order_id=" + orderLog.getId());
			}
			else if (orderLog.getChannel().equals("WAP") || orderLog.getChannel().equals("WX")) {
				orderReturn.setUrl(CommonUtil.getContextPath(request) + "/wappay.do?order_id=" + orderLog.getId());
			}
			else if (orderLog.getChannel().equals("APP")) {
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
					if (StringUtils.isNotBlank(expDate)) {
						expDate = CommonUtil.getDefaultExpTime(order.getCreateTime()).toString();
					}
					Date epDate = new Date(Long.parseLong(order.expDate));
					String it_b_pay = CommonUtil.sformat.format(epDate);
					sParaTemp.put("it_b_pay", "10d");
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
					}
					else {
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
			}
		}
		orderLog.setCode(orderReturn.getCode().toString());
		orderLog.setMsg(orderReturn.getMsg());
		orderLog.setReturnDetail(JSONObject.toJSONString(orderReturn));
		payService.updateOrderLog(orderLog);
		return orderReturn;
	}

	// 退款接口
	@ResponseBody
	@RequestMapping("/testrefund")
	public OrderReturn refund(RefundLog refundLog, HttpServletRequest request, HttpServletResponse response) throws IOException {
		OrderReturn orderReturn = refundService.check(refundLog);
		if (orderReturn.getCode() == 0) {
			// 生成正式退款单
			Refund refund = refundService.saveRefund(refundLog);
			Order orginOrder = payService.getOrderByBillNo(refund.getBillNo());
			Map refundMap = new HashMap();
			if (orginOrder.getPayType().equals("WX")) {
				// 微信退款，必需加载证书
				String realPath = request.getSession().getServletContext().getRealPath("");
				WXConfig.getConfig().setCertpath(realPath + WXConfig.getConfig().getCertpath());
				WXConfig.getConfig().setAppcertpath(realPath + WXConfig.getConfig().getAppcertpath());
				refundMap = wxPayService.refund(refund, orginOrder);
				refund.setStatus(refundMap.get("status").toString());
				refund.setCallBackStatus(refundMap.get("status").toString());
				refund.setCallBackMemo(refundMap.get("msg").toString());
				refund.setCallBackTime(new Date());
				refundService.updateRefund(refund);
				// 退款成功，直接回调
				if (refund.getCallBackStatus().equals("0")) {
					refundService.refundNotify(refund);
				}
				else {
					orderReturn.setCode(1);
					orderReturn.setMsg("REFUND ERROR");
					orderReturn.setErrDetail(refund.callBackMemo);

				}
			}
			else if (orginOrder.getPayType().equals("ALI")) {
				orderReturn.setUrl(CommonUtil.getContextPath(request) + "/alirefund.do?refund_id=" + refund.getId());

			}
			else if (orginOrder.getPayType().equals("YP") || orginOrder.getPayType().equals("YE")) {
				refundMap = xmPayService.refund(refund, orginOrder);
				refund.setStatus(refundMap.get("status").toString());
				refund.setCallBackStatus(refundMap.get("status").toString());
				refund.setCallBackMemo(refundMap.get("msg").toString());
				refund.setCallBackTime(new Date());
				refundService.updateRefund(refund);
				// 退款成功，直接回调
				if (refund.getCallBackStatus().equals("0")) {
					refundService.refundNotify(refund);
				}
				else {
					orderReturn.setCode(1);
					orderReturn.setMsg("REFUND ERROR");
					orderReturn.setErrDetail(refund.callBackMemo);

				}
			}
			else if (orginOrder.getPayType().equals("UN")) {
//				String realPath = request.getSession().getServletContext().getRealPath("");
//				SDKConfig.getConfig().loadPropertiesFromSrc(realPath);
				Map<String, String> data = new HashMap<String, String>();

				/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
				data.put("version", "5.0.0"); // 版本号
				data.put("encoding", CommonUtil.encoding_UTF8); // 字符集编码
																// 可以使用UTF-8,GBK两种方式
				data.put("signMethod", "01"); // 签名方法 目前只支持01-RSA方式证书加密
				data.put("txnType", "04"); // 交易类型 04-退货
				data.put("txnSubType", "00"); // 交易子类型 默认00
				data.put("bizType", "000201"); // 业务类型 B2C网关支付，手机wap支付
				data.put("channelType", "07"); // 渠道类型，07-PC，08-手机

				/*** 商户接入参数 ***/
				data.put("merId", SDKConfig.getConfig().getMerId()); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
				data.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改
				data.put("orderId", refund.getRefundNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
				data.put("txnTime", CommonUtil.getCurrTime()); // 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
				data.put("currencyCode", "156"); // 交易币种（境内商户一般是156 人民币）
				data.put("txnAmt", refund.getRefundFee().toString()); // ****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
				data.put("reqReserved", refund.getCustId()); // 请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节
				data.put("backUrl", SDKConfig.getConfig().getNotifyUrl()); // 后台通知地址，后台通知参数详见open.unionpay.com帮助中心
																			// 下载
																			// 产品接口规范
																			// 网关支付产品接口规范
																			// 退货交易
																			// 商户通知,其他说明同消费交易的后台通知
				/*** 要调通交易以下字段必须修改 ***/
				data.put("origQryId", orginOrder.getPaySn()); // ****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取

				/** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/
				Map<String, String> reqData = AcpService.sign(data, CommonUtil.encoding_UTF8);// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
				String url = SDKConfig.getConfig().getBackRequestUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的
																		// acpsdk.backTransUrl

				Map<String, String> rspData = AcpService.post(reqData, url, CommonUtil.encoding_UTF8);// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

				/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
				// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
				if (!rspData.isEmpty()) {
					if (AcpService.validate(rspData, CommonUtil.encoding_UTF8)) {
						LogUtil.writeLog("验证签名成功");
						String respCode = rspData.get("respCode");
						if ("00".equals(respCode)) {
							refund.setStatus("0");
						}
						else if ("03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode)) {
							refund.setStatus("0");
						}
						else {
							refund.setStatus("1");
							orderReturn.setCode(1);
							orderReturn.setMsg("银联返回退款失败,CODE:" + respCode);
						}
					}
					else {
						LogUtil.writeErrorLog("验证签名失败");
						refund.setStatus("1");
						orderReturn.setCode(1);
						orderReturn.setMsg("验证签名失败");
						// TODO 检查验证签名失败的原因
					}

				}
				else {
					// 未返回正确的http状态
					LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
					refund.setStatus("1");
					orderReturn.setCode(1);
					orderReturn.setMsg("未获取到返回报文或返回http状态码非200");
				}
				refundService.updateRefund(refund);
			}
		}
		refundLog.setCode(orderReturn.getCode().toString());
		refundLog.setMsg(orderReturn.getMsg());
		refundLog.setReturnDetail(JSONObject.toJSONString(orderReturn));
		refundService.updateRefundLog(refundLog);
		return orderReturn;
	}

	@RequestMapping("/wxlogin")
	// 公众号登录获取CODE测试
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 公从号APPID
		String appid = WXConfig.getConfig().getH5appid();
		String backUri = "http://test.vk30.com/smipay/getopenid.do";
		// URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
		backUri = URLEncoder.encode(backUri);
		// scope 参数视各自需求而定，这里用scope=snsapi_base 不弹出授权页面直接授权目的只获取统一支付接口的openid
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=" + appid + "&redirect_uri=" + backUri + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
		response.sendRedirect(url);
		return null;
	}

	@RequestMapping("/getopenid")
	// 公众号登录获取后获取openid测试
	public ModelAndView getopenid(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return wxPayService.getopenid(request, response);
	}

	@RequestMapping("/refundtest")
	// 公众号登录获取后获取openid测试
	public ModelAndView refund(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Refund refund = refundService.getRefund(77);

		refundService.refundNotify(refund);
		return wxPayService.getopenid(request, response);
	}

}
