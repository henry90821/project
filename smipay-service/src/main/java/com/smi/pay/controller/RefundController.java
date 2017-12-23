package com.smi.pay.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
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
public class RefundController {
	@Autowired
	private WXPayService		wxPayService;
	@Autowired
	private XMPayService		xmPayService;
	@Autowired
	private PayService			payService;
	@Autowired
	private RefundService		refundService;

	private SimpleDateFormat	format		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat	batchformat	= new SimpleDateFormat("yyyyMMddHHmmss");

	// 退款接口
	@ResponseBody
	@RequestMapping("/refund")
	public OrderReturn refund(@RequestBody
	RefundLog refundLog, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

	@RequestMapping("/alirefund")
	public ModelAndView refund(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String refundId = request.getParameter("refund_id");
		Refund refund = refundService.getRefund(Integer.parseInt(refundId));
		Order order = payService.getOrderByBillNo(refund.getBillNo());

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();

		sParaTemp.put("service", AlipayConfig.getConfig().refundservice);
		sParaTemp.put("partner", AlipayConfig.getConfig().partner);
		sParaTemp.put("_input_charset", AlipayConfig.getConfig().input_charset);
		sParaTemp.put("notify_url", AlipayConfig.getConfig().notify_url);
		sParaTemp.put("seller_user_id", AlipayConfig.getConfig().partner);
		sParaTemp.put("refund_date", format.format(new Date()));
		// 注意：退款批次号(batch_no)，必填(时间格式是yyyyMMddHHmmss+数字或者字母)
		sParaTemp.put("batch_no", refund.getRefundNo());
		sParaTemp.put("batch_num", "1");
		sParaTemp.put("detail_data", order.getPaySn() + "^" + String.valueOf(refund.getRefundFee() / 100f) + "^协商退款");

		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		// (sHtmlText);
		response.setCharacterEncoding("UTF_8");// 设置Response的编码方式为UTF-8
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getWriter().write(sHtmlText);
		refundService.updateRefund(refund);
		return null;
	}

}
