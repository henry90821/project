package com.smi.pay.controller;

import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.Refund;
import com.smi.pay.sdk.ali.AlipayNotify;
import com.smi.pay.sdk.union.AcpService;
import com.smi.pay.sdk.union.SDKConstants;
import com.smi.pay.sdk.wx.GetWxOrderno;
import com.smi.pay.service.PayService;
import com.smi.pay.service.RefundService;
import com.smi.pay.service.WXPayService;
import com.smi.pay.service.XMPayService;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Controller
public class CallBackController {

	@Autowired
	private WXPayService wxPayService;
	@Autowired
	private PayService payService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private XMPayService xmPayService; // ("/payplatform/rest/bill")

	// 回调接口
	@RequestMapping("/alicallback")
	public ModelAndView alicallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		LogUtil.writeLog("--------------ali call back----------" + params.toString());

		if (AlipayNotify.verify(params)) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 付款成功的回调
			if (params.containsKey("out_trade_no")) {
				LogUtil.writeLog("--------------ali pay call back-------verify ok---");
				// 商户订单号
				String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
				// 支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
				// 交易状态
				String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
				if (trade_status.equals("TRADE_SUCCESS")) {
					// 付款完成后，支付宝系统发送该交易状态通知
					Order order = payService.getOrderByBillNo(out_trade_no);
					if (order != null && !"0".equals(order.getCallBackStatus())) {
						order.setPaySn(trade_no);
						order.setStatus("0");
						order.setCallBackStatus("0");
						order.setCallBackTime(new Date());
						order.setCallBackMemo("success");
						payService.updateOrder(order);
						payService.payNotify(order);
					}
				}
			}
			// 退款成功的回调
			else if (params.containsKey("batch_no")) {
				LogUtil.writeLog("--------------ali refund call back-------verify ok---");
				String batch_no = params.get("batch_no");
				Integer success_num = Integer.parseInt(params.get("success_num"));
				Refund refund = refundService.getRefundByRefundBill(batch_no);
				if (refund != null && success_num > 0) {
					refund.setStatus("0");
					refund.setCallBackStatus("0");
					refund.setCallBackMemo("success");
					refundService.updateRefund(refund);
					refundService.refundNotify(refund);
				}
			}

			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			response.getWriter().write("success"); // 请不要修改或删除
			// ////////////////////////////////////////////////////////////////////////////////////////
		} else {// 验证失败
			response.getWriter().write("fail");
		}
		return null;
	}

	// 回调接口
	@RequestMapping("/wxcallback")
	public ModelAndView wxcallback(HttpServletRequest request, HttpServletResponse response) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		// sb为微信返回的xml
		LogUtil.writeLog("-----接收到微信支付成功的回调信息-----内容:" + sb.toString());
		try {
			Map map = GetWxOrderno.doXMLParse(sb.toString());
			String return_code = (String) map.get("return_code");
			if ("SUCCESS".equals(return_code)) {
				String out_trade_no = (String) map.get("out_trade_no");
				String transaction_id = (String) map.get("transaction_id");
				Order order = payService.getOrderByBillNo(out_trade_no);
				if (order != null && !"0".equals(order.getCallBackStatus())) {
					order.setPaySn(transaction_id);
					order.setStatus("0");
					order.setCallBackStatus("0");
					order.setCallBackTime(new Date());
					order.setCallBackMemo("success");
					payService.updateOrder(order);
					payService.payNotify(order);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String returnStr = "<xml> <return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg> </xml>";
		response.getWriter().write(returnStr);
		return null;
	}

	// 回调接口
	@RequestMapping("/uncallback")
	public ModelAndView uncallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LogUtil.writeLog("uncallback接收后台通知开始");
		request.setCharacterEncoding("ISO-8859-1");
		String encoding = request.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = CommonUtil.getAllRequestParam(request);

		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}

		// 重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			// 验签失败，需解决验签问题

		} else {
			LogUtil.writeLog("验证签名结果[成功].");

			String orderId = valideData.get("orderId"); // 获取后台通知的数据，其他字段也可用类似方式获取
			String txnType = valideData.get("txnType");// 交易类型 01:付款 04:退货
			if (txnType.equals("01")) {
				Order order = payService.getOrderByBillNo(orderId);
				if (order != null && !"0".equals(order.getCallBackStatus())) {
					LogUtil.writeLog("--------------union pay call back-------verify ok---");
					order.setPaySn(valideData.get("queryId"));
					order.setStatus("0");
					order.setCallBackStatus("0");
					order.setCallBackTime(new Date());
					order.setCallBackMemo("success");
					payService.updateOrder(order);
					payService.payNotify(order);
				}
			} else if (txnType.equals("04")) {
				LogUtil.writeLog("--------------union refund call back-------verify ok---");
				Refund refund = refundService.getRefundByRefundBill(orderId);
				if (refund != null) {
					refund.setStatus("0");
					refund.setCallBackStatus("0");
					refund.setCallBackMemo("success");
					refundService.updateRefund(refund);
					refundService.refundNotify(refund);
				}

			}

		}
		LogUtil.writeLog("BackRcvResponse接收后台通知结束");
		// 返回给银联服务器http 200 状态码
		response.getWriter().print("ok");
		return null;
	}

	/**
	 * 钱包服务回调支付中心
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = "/walletcallback", method = RequestMethod.POST)
	@ResponseBody
	public BaseValueObject walletCallback(@RequestParam(value = "interface") String interFace,
			@RequestParam(value = "payNo") String payNo, @RequestParam(value = "thirdUserNo") String thirdUserNo,
			@RequestParam(value = "payResultCode") String payResultCode,
			@RequestParam(value = "payResultMessage", required = false) String payResultMessage,
			@RequestParam(value = "paySuccDate", required = false) String paySuccDate,
			@RequestParam(value = "payAmount", required = false) String payAmount) throws IOException {
		LogUtil.writeLog("==================触发星美钱包支付回调函数==================");
		BaseValueObject result = new BaseValueObject();
		String returnMsg = "";
		LogUtil.writeLog("第三方系统回调时的参数值是: [interface=" + interFace + ",payNo=" + payNo + ",thirdUserNo=" + thirdUserNo
				+ ",payResultCode=" + payResultCode + ",payResultMessage=" + payResultMessage + ",paySuccDate=" + paySuccDate
				+ ",payAmount=" + payAmount);
		LogUtil.writeLog("获取支付流水号: " + payNo);
		Order order = payService.getOrderByReqNo(payNo);
		if (null != order) {
			if ("1".equals(payResultCode)) {
				LogUtil.writeLog("众安服务回调通知支付成功!");
				if (!"0".equals(order.getCallBackStatus())) {
					LogUtil.writeLog("开始验证当前请求更新的订单与数据库中查询到的订单所属会员是否一致...");
					// String thirdUserNo = request.getParameter("thirdUserNo");
					if (order.getCustId().equals(thirdUserNo)) {
						LogUtil.writeLog("根据支付流水号(请求序列)查询到主键ID值为" + order.getId() + "的订单信息,订单号为: " + order.getBillNo());
						LogUtil.writeLog("开始准备更新订单的回调状态数据...");
						order.setPaySn(payNo);
						order.setStatus("0");
						order.setCallBackStatus("0");
						order.setCallBackTime(new Date());
						order.setCallBackMemo("success");
						LogUtil.writeLog("开始发起业务系统回调通知...");
						payService.payNotify(order);
						LogUtil.writeLog("业务系统回调结束...");
						result.setCode(CodeEnum.SUCCESS);
					} else {
						returnMsg = "第三方系统回调时传递的CUST_ID与数据库中订单对应的数值不一致,不做订单支付成功业务处理![thirdUserNo: " + thirdUserNo
								+ "|custId: " + order.getCustId() + "]";
						LogUtil.writeErrorLog(returnMsg);
						result.setCode(CodeEnum.REQUEST_ERROR);
						result.setMsg(returnMsg);
					}
				} else {
					returnMsg = "订单回调状态已经为成功,不再重复进行订单状态业务处理!";
					LogUtil.writeErrorLog(returnMsg);
					result.setCode(CodeEnum.REQUEST_ERROR);
					result.setMsg(returnMsg);
				}
			} else {
				returnMsg = "第三方系统通知支付失败,开始记录失败具体原因...\n[payResultCode: ]" + payResultCode + "|payResultMessage: "
						+ payResultMessage;
				LogUtil.writeErrorLog(returnMsg);
				order.setCallBackMemo(payResultMessage);
				order.setCallBackTime(new Date());
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg(returnMsg);
			}
			LogUtil.writeLog("开始更新订单信息...");
			payService.updateOrder(order);
			LogUtil.writeLog("更新订单信息结束...");
		} else {
			returnMsg = "无法根据支付流水号查询到订单信息!";
			LogUtil.writeErrorLog(returnMsg);
			result.setCode(CodeEnum.REQUEST_ERROR);
			result.setMsg(returnMsg);
		}
		LogUtil.writeLog("==================星美钱包支付回调函数执行结束==================");
		return result;
	}

}
