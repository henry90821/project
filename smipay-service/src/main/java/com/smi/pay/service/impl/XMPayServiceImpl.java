package com.smi.pay.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.Refund;
import com.smi.pay.sdk.wx.HttpClientConnectionManager;
import com.smi.pay.service.XMPayService;
import com.smi.pay.service.request.OrderType;
import com.smi.pay.service.request.PayRequest;
import com.smi.pay.service.request.RepaymentType;
import com.smi.tools.http.HttpRequest;
import com.smi.tools.kits.DateKit;
import com.smi.tools.kits.JsonKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service("XMPayService")
@Transactional(readOnly = false)
public class XMPayServiceImpl implements XMPayService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Map getBal(String custId) {
		String url = CommonUtil.getConfig().getXm_jf_host();// HOST+"/xm/bill/service";
		String queryJson = "{\"SID\":\"SC1003011\",\"BODY\": {\"SOO\": [{\"BALANCEQRY_REQ\": {" + "\"MemberId\": \"" + custId
				+ "\",\"QryType\": \"2\",\"BalType\": \"6\"," + "\"SystemID\": \"103\"}}] }}";
		Map<String, Object> dataMap = new HashMap<String, Object>();
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		try {
			httpost.setEntity(new StringEntity(queryJson, "UTF-8"));
			HttpResponse response = CommonUtil.httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("---------------getBaljson是:" + jsonStr);

			JSONObject obj = JSONObject.parseObject(jsonStr);
			JSONArray bodys = obj.getJSONArray("BODY");
			JSONObject body = bodys.getJSONObject(0);

			JSONArray ssos = body.getJSONArray("SOO");
			JSONObject sso = ssos.getJSONObject(0);

			JSONObject resp = sso.getJSONObject("RESP");
			String rate = resp.getString("XingCoinRate");
			String xingCoin = "0";
			// if(resp.containsKey("XingCoin"))
			// {
			// xingCoin= resp.getString("XingCoin");
			// }
			Float totalBal = 0f;
			Integer totalYp = 0;
			JSONArray details = resp.getJSONArray("BalDetail");
			for (int i = 0; i < details.size(); i++) {
				JSONObject zb = details.getJSONObject(i);
				String type = zb.getString("BalanceTypeId");
				// 余额
				if (type.equals("1") || type.equals("9") || type.equals("50")) {
					String balance = zb.getString("Balance");
					if (StringUtils.isNoneBlank(balance)) {
						totalBal = totalBal + Float.parseFloat(balance);
					}
				}
				// 影票
				else if (type.equals("101") || type.equals("102")) {
					String yb = zb.getString("Balance");
					if (StringUtils.isNoneBlank(yb)) {
						totalYp = totalYp + Integer.parseInt(yb);
					}
				}
				// 星币
				else if (type.equals("8")) {
					String yb = zb.getString("Balance");
					if (StringUtils.isNoneBlank(yb)) {
						xingCoin = String.valueOf(Integer.parseInt(xingCoin) + Integer.parseInt(yb));
					}
				}
			}
			Float coinBal = Float.parseFloat(xingCoin) * Float.parseFloat(rate);// 星币换算的余额
			Float totalAll = totalBal + coinBal;
			DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
			dataMap.put("totalAll", decimalFormat.format(totalAll)); // 合计可用的余额
			dataMap.put("totalBal", decimalFormat.format(totalBal)); // 界面显示的账户余额
			dataMap.put("xingCoin", xingCoin); // 界面显示的星币数量
			dataMap.put("coinBal", decimalFormat.format(coinBal)); // 界面显示的星币可兑换余额
			dataMap.put("totalYp", totalYp); // 电影票数量
			dataMap.put("rate", rate); // 积分和余额汇率
			System.out.println("---------------返回的MAP是:" + dataMap.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	@Override
	public boolean checkPassword(String custId, String passWord) {
		String url = CommonUtil.getConfig().getXm_hy_host();// HOST+"/xm/crm/service";
		boolean falg = false;
		String queryJson = "{\"SID\":\"SC1001016\",\"BODY\": {\"SOO\": [{\"CUST_PAY\": {"
				+ "\"CHANNEL_CODE\": \"10002\",\"EXT_SYSTEM\": \"102\",\"PWD\": \"" + passWord + "\"," + "\"CUST_ID\": \""
				+ custId + "\",\"QRY_NUMBER\": \"\",\"QRY_TYPE\": \"\"}}] }}";
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		try {
			httpost.setEntity(new StringEntity(queryJson, "UTF-8"));
			// HttpResponse response = CommonUtil.httpclient.execute(httpost);
			HttpResponse response = client.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			// System.out.println("---------------getBaljson是:"+jsonStr);
			System.out.println("---------------调用计费中心checkPassword返回结果是:" + jsonStr);
			JSONObject obj = JSONObject.parseObject(jsonStr);
			JSONArray bodys = obj.getJSONArray("BODY");
			JSONObject body = bodys.getJSONObject(0);

			JSONArray ssos = body.getJSONArray("SOO");
			JSONObject sso = ssos.getJSONObject(0);

			JSONObject resp = sso.getJSONObject("RESP");
			String result = resp.getString("RESULT");
			if (result.equals("0")) {
				falg = true;
			}
		} catch (Exception e) {
			System.out.println("----ERROR-----------调用会员中心验证密码接口失败，用户ID:" + custId + " 失败原因:");
			e.printStackTrace();
		}
		return falg;
	}

	// 星美生活支付接口
	@Override
	public Map pay(Order order) {
		String url = CommonUtil.getConfig().getXm_jf_host();// HOST+"/xm/bill/service";
		Map payResult = new HashMap();
		String submitJson = "{" + "\"SID\": \"SC1003020\"," + "\"BODY\": {" + "        \"SOO\": [" + "           {"
				+ "                \"PUB_REQ\": {" + "                   \"TYPE\": \"PAYMENT\"" + "               },"
				+ "               \"PAYMENT_REQ\": {" + "                    \"COMMODITY\": " + order.getCommodity() + ","
				+ "                    \"DISCOUNT_CHARGE\": \"" + order.getTotalFee() / 100 + "\","
				+ "                   \"MEMBER_ID\": \"" + order.getCustId() + "\"," + "                    \"ORDER_ID\": \""
				+ order.getBillNo() + "\"," + "                    \"PAYMETHOD_INFO\": " + getPayMethodInfo(order)
				+ "                     ," + "                    \"STAFF_ID\": \"10002\","
				+ "                    \"SUM_CHARGE\": \"" + order.getTotalFee() / 100 + "\","
				+ "                    \"SYSTEM_ID\": \"102\"" + "          }" + "          }" + "      ]" + "    } }";
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		try {
			System.out.println("---------------pay提交的数据:" + submitJson);
			httpost.setEntity(new StringEntity(submitJson, "UTF-8"));
			// HttpResponse response = CommonUtil.httpclient.execute(httpost);
			HttpResponse response = client.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.info("---------------pay之后的返回值是:" + jsonStr);
			JSONObject obj = JSONObject.parseObject(jsonStr);
			JSONArray bodys = obj.getJSONArray("BODY");
			JSONObject body = bodys.getJSONObject(0);

			JSONArray ssos = body.getJSONArray("SOO");
			JSONObject sso = ssos.getJSONObject(0);

			JSONObject resp = sso.getJSONObject("RESP");

			String result = resp.getString("RESULT");
			if (result.equals("0")) {
				payResult.put("status", "0");
				payResult.put("msg", "支付成功");
			} else {
				String msg = resp.getString("MSG");
				payResult.put("status", "1");
				payResult.put("msg", "支付失败,原因:" + msg);

			}
		} catch (Exception e) {
			logger.error("---------------调用计费中心支付接口失败,失败原因:" + e.getStackTrace());
			payResult.put("status", "1");
			// payResult.put("msg", "程序内部错误:"+CommonUtil.getExceptionStr(e));
			payResult.put("msg", "调用计费中心支付接口失败!");
		}
		return payResult;
	}

	// 组装支付节点信息
	public String getPayMethodInfo(Order order) {
		String info = "[";
		if (order.getPayType().equals("YE")) {
			// 余额支付，先扣星币
			float fee = order.getTotalFee() / 100f;
			Map bal = getBal(order.getCustId());
			// 如果用户有星币，生成扣星币的节点
			if (!"0".equals(bal.get("xingCoin"))) {
				float coinBal = Float.parseFloat(bal.get("coinBal").toString()); // 星币可兑换余额
				// 星币余额比订单金额大时，只扣等额星币
				if (fee < coinBal) {
					float rate = Float.parseFloat(bal.get("rate").toString());
					float costCoin = fee / rate;
					info = info + "{" + "\"AMOUNT\": \"" + costCoin + "\"," + "\"PAY_CODE\": \"8\"" + "}";
					fee = 0;
				}
				// 星币余额比订单金额小时，扣完星币
				else {
					info = info + "{" + "\"AMOUNT\": \"" + bal.get("xingCoin") + "\"," + "\"PAY_CODE\": \"8\"" + "}";
					fee = fee - coinBal;
					if (fee > 0) // 扣不够时，后面还要扣余额
					{
						info = info + ",";
					}
				}
			}
			// 星币支付不够,再扣余额
			if (fee > 0) {
				info = info + "{" + "\"AMOUNT\": \"" + fee + "\"," + "\"PAY_CODE\": \"0\"" + "}";
			}
		} else if (order.getPayType().equals("YP")) {
			Integer totalNum = 0;
			JSONArray yps = JSONArray.parseArray(order.getCommodity());
			for (int i = 0; i < yps.size(); i++) {
				JSONObject yp = yps.getJSONObject(i);
				int num = Integer.parseInt(yp.getString("COMMODITY_NUM"));
				totalNum = totalNum + num;
			}
			info = info + "{" + "\"AMOUNT\": \"" + totalNum + "\"," + "\"PAY_CODE\": \"7\"" + "}";

		}
		info = info + "]";
		return info;
	}

	// 星美生活退款接口
	@Override
	public Map refund(Refund refund, Order order) {
		String url = CommonUtil.getConfig().getXm_jf_host();// HOST+"/xm/bill/service";
		// Map payResult=new HashMap();
		// String cancel_class="102";
		// String desc="全额退";
		// if(refund.getRefundFee()<order.getTotalFee())
		// {
		// cancel_class="101";
		// desc="部分退";
		// }
		String submitJson = "{" + "\"SID\": \"SC1003021\"," + "\"BODY\": {" + "        \"SOO\": " + refund.getPackageValue()
				+ "    } }";
		// String submitJson=refund.getPackageValue();
		Map payResult = new HashMap();
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		try {
			System.out.println("---------------refund提交的数据:" + submitJson);
			httpost.setEntity(new StringEntity(submitJson, "UTF-8"));
			HttpResponse response = CommonUtil.httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("---------------refund之后的返回值是:" + jsonStr);
			JSONObject obj = JSONObject.parseObject(jsonStr);
			JSONArray bodys = obj.getJSONArray("BODY");
			JSONObject body = bodys.getJSONObject(0);

			JSONArray ssos = body.getJSONArray("SOO");
			JSONObject sso = ssos.getJSONObject(0);

			JSONObject resp = sso.getJSONObject("RESP");

			String result = resp.getString("RESULT");
			if (result.equals("0")) {
				payResult.put("status", "0");
				payResult.put("msg", "退款成功");
			} else {
				String msg = resp.getString("MSG");
				payResult.put("status", "1");
				payResult.put("msg", "退款失败,原因:" + msg);
			}
		} catch (Exception e) {
			System.out.println("---------------调用计费中心退款接口失败,失败原因:");
			e.printStackTrace();
			payResult.put("status", "1");
			payResult.put("msg", "调用计费中心退款接口失败!");
		}
		return payResult;
	}

	/**
	 * 星美钱包－－随便花支付
	 */
	@Override
	public BaseValueObject sbhPay(Order order) {
		logger.info("===================进入星美随便花支付接口===================");
		String returnValue = null;
		BaseValueObject result = new BaseValueObject();
		logger.info("开始组装请求支付参数");
		PayRequest payRequest = new PayRequest();
		payRequest.setOrderName(order.getTitle());
		payRequest.setOrderNo(order.getBillNo());
		payRequest.setPayNo(order.getReqno());
		// 传递支付时间时需要先讲时间格式化
		payRequest.setTradeDate(DateKit.format(order.getCreateTime(), DateKit.NORM_DATETIME_PATTERN));
		payRequest.setTradeAmount(BigDecimal.valueOf(BigDecimal.valueOf(order.getTotalFee()).doubleValue() / 100));
		// 这里需要获取到各个业务系统传递过来的分期数
		payRequest.setPeriodNum(order.getInstNumber());
		// 目前订单类型是固定为虚拟卡消费
		payRequest.setOrderType(OrderType.VIRTUALCARD);
		// 目前还款方式暂定为等本等息
		payRequest.setRepayMode(RepaymentType.DBDX);
		// 设置众安支付回调路径
		// payRequest.setNotifyUrl(CommonUtil.getSmiWalletNotifyUrl());
		logger.info("开始将参数对象PayRequest转换成JSON格式字符串");
		String reqParam = JsonKit.toJsonString(payRequest);
		logger.info("请求星美钱包支付的参数是: " + reqParam);
		String reqUrl = CommonUtil.getXm_sbh_host() + "/" + order.getCustId();
		logger.info("星美钱包的请求路径是: " + reqUrl);
		try {
			// 发起随便花支付
			// returnValue = HttpKit.post(reqUrl, reqParam);
			returnValue = HttpRequest.post(reqUrl).contentType("application/json").body(reqParam).execute().body();
			logger.info("请求星美钱包支付服务的返回结果值是: " + returnValue);
			result = JsonKit.parseObject(returnValue, BaseValueObject.class);
			if (CodeEnum.SUCCESS.code().equals(result.getCode())) {
				logger.warn("星美钱包支付成功!");
			}
			// } catch (IOException e) {
		} catch (Exception e) {
			logger.error("请求星美随便花服务支付接口出错!", e);
		}
		logger.info("===================星美随便花支付结束===================");
		return result;
	}
}
