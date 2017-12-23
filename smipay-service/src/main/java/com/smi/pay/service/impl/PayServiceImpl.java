package com.smi.pay.service.impl;

import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.dao.AppInfoDao;
import com.smi.pay.dao.OrderDao;
import com.smi.pay.dao.OrderLogDao;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderLog;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.sdk.wx.HttpClientConnectionManager;
import com.smi.pay.service.PayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("PayService")
@Transactional(readOnly = false)
public class PayServiceImpl implements PayService {

	@Autowired
	private OrderLogDao orderLogDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private AppInfoDao appInfoDao;

	@Override
	public OrderReturn check(OrderLog orderLog) {
		// 选将业务系统传过来的数据存入log表，再做合法性验效果
		orderLog.setCreateTime(new Date());
		saveOrderLog(orderLog);
		OrderReturn or = new OrderReturn();
		or.setCode(0);
		or.setMsg("OK");
		if (CommonUtil.isExpire(orderLog.getExpDate(), orderLog.getCreateTime())) {
			or.setCode(2);
			or.setMsg("ORDER_EXPIRED");
			or.setErrDetail("订单已经过期");
		} else if (!"WEB".equals(orderLog.getChannel()) && !"WAP".equals(orderLog.getChannel())
				&& !"WX".equals(orderLog.getChannel()) && !"APP".equals(orderLog.getChannel())) {
			or.setCode(2);
			or.setMsg("CHANNEL_INVALID");
			or.setErrDetail("channel参数不合法");
		} else if (StringUtils.isNotBlank(orderLog.getPayType()) && !"WX".equals(orderLog.getPayType())
				&& !"ALI".equals(orderLog.getPayType()) && !"YE".equals(orderLog.getPayType())
				&& !"YP".equals(orderLog.getPayType()) && !"SBH".equals(orderLog.getPayType())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("payType参数不合法");
		} else if ("".equals(orderLog.getCustId())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("custId不能为空");
		} else if ("".equals(orderLog.getBillNo())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("billNo不能为空");
		} else if ("".equals(orderLog.getBillType())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("billType不能为空");
		} else if (!"1".equals(orderLog.getBillType()) && !"2".equals(orderLog.getBillType())
				&& !"3".equals(orderLog.getBillType())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("billType参数不合法");
		} else if ("".equals(orderLog.getCommodity())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("commodity不能为空");
		} else if ((orderLog.getChannel().equals("WEB") || orderLog.getChannel().equals("WAP"))
				&& "".equals(orderLog.getReturnUrl())) {
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("returnUrl不能为空");
		} else {
			Order history = getHistory(orderLog);
			if (history != null && "0".equals(history.status)) {
				or.setCode(5);
				or.setMsg("ORDER HAS BE DONE");
				or.setErrDetail("订单已经支付成功,不能重复下单");
			}
		}

		return or;
	}

	public Order getHistory(OrderLog orderLog) {
		Order history = null;
		// 获取已经下单成功的单据时，爱星美的需要特殊处理
		if (orderLog.getBillType().equals("3")) {
			String orgBill = orderLog.billNo.substring(14);
			history = orderDao.getLikeNO("%" + orgBill);
		} else {
			history = orderDao.getByNO(orderLog.getAppcode() + orderLog.billNo);
		}
		return history;
	}

	public void saveOrderLog(OrderLog order) {
		orderLogDao.insert(order);
	}

	public void getLogOrder(Integer orderId) {
		OrderLog order = orderLogDao.load(orderId);
	}

	@Override
	public OrderLog getOrderLog(Integer id) {
		return orderLogDao.load(id);
	}

	@Override
	public void updateOrderLog(OrderLog order) {
		orderLogDao.update(order);
	}

	/**
	 * 临时单生成正式单
	 */
	public synchronized Order saveOrder(OrderLog orderLog) {
		Order history = getHistory(orderLog);
		if (history != null) {
			orderDao.deleteByPrimaryKey(history.getId());
		}
		Order order = new Order();
		order.setAppcode(orderLog.getAppcode());
		order.setReqno(orderLog.getReqno());
		order.setSign(orderLog.getSign());
		order.setChannel(orderLog.getChannel());
		order.setPayType(orderLog.getPayType());
		if (orderLog.getChannel().equals("WX")) {
			order.setPayType(orderLog.getChannel());
		}
		order.setPayPwd(orderLog.getPayPwd());
		order.setCustId(orderLog.getCustId());
		order.setTotalFee(orderLog.getTotalFee());
		// 爱星美订单可能重复，生成正式订单时，appcode后面加上时间
		order.setBillNo(orderLog.getAppcode() + orderLog.getBillNo());
		order.setBillType(orderLog.getBillType());
		order.setTitle(orderLog.getTitle());
		order.setReturnUrl(orderLog.getReturnUrl());
		order.setOpenId(orderLog.getOpenId());
		order.setCommodity(orderLog.getCommodity());
		order.setCreateTime(new Date());
		order.setNoticeTimes(0);
		order.setExpDate(orderLog.getExpDate());
		order.setInstNumber(orderLog.getInstNumber());
		orderDao.insert(order);
		return order;
	}

	public Order getOrder(Integer id) {
		return orderDao.load(id);
	}

	public void updateOrder(Order order) {
		orderDao.update(order);
	}

	public Order getOrderByBillNo(String billNo) {
		Order history = orderDao.getByNO(billNo);
		return history;
	}

	@Override
	public Order getOrderByReqNo(String reqNo) {
		Order history = orderDao.getByReqNo(reqNo);
		return history;
	}

	@Override
	public void payNotify(final Order order) {
		if ("0".equals(order.noticeStatus)) {
			return;
		}
		final String url = appInfoDao.getByCodePay(order.getAppcode()).getCallBackUrl();
		// String url=
		// "http://192.168.68.23:8083/smimall420/api/app/mall2304payCallBack.htm";
		Map backMap = new HashMap();
		// backMap.put("code", order.getCallBackStatus());
		// backMap.put("msg", order.getCallBackMemo());
		// backMap.put("errorDetail", order.getCallBackMemo());
		// backMap.put("tradeNo", order.getBillNo());
		// backMap.put("thirdNo", order.getPaySn());
		// backMap.put("billNo", order.getBillNo().substring(4));
		// backMap.put("billType", order.getBillType());
		// final String callData=JSONObject.toJSONString(backMap);
		// LogUtil.writeLog("-------------payCallBack--callStr是:"+callData);

		Thread t = new Thread() {
			boolean flag = false;
			int num = 0;

			public void run() {
				DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);// 连接时间
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);// 数据传输时间
				client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
				while (!flag && num < 5) {

					HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
					String returnStr = "";
					try {
						if (num > 0) {
							Thread.sleep(10000 * num);
						}
						num++;
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("code", order.getCallBackStatus()));
						nvps.add(new BasicNameValuePair("msg", order.getCallBackMemo()));
						nvps.add(new BasicNameValuePair("errorDetail", order.getCallBackMemo()));
						nvps.add(new BasicNameValuePair("tradeNo", order.getBillNo()));
						nvps.add(new BasicNameValuePair("thirdNo", order.getPaySn()));
						String billNo = "";
						if ("3".equals(order.getBillType())) {
							billNo = order.getBillNo().substring(19);
						} else {
							billNo = order.getBillNo().substring(5);
						}
						nvps.add(new BasicNameValuePair("billNo", billNo));
						nvps.add(new BasicNameValuePair("billType", order.getBillType()));
						nvps.add(new BasicNameValuePair("channel", order.getChannel()));
						nvps.add(new BasicNameValuePair("payType", order.getPayType()));
						final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
						// httpost.setEntity(new StringEntity(StringEntity,
						// "UTF-8"));
						httpost.setEntity(entity);
						HttpResponse response = client.execute(httpost);
						returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
						LogUtil.writeLog("-------------payNotify--returnStr是:" + returnStr + "---BILLNO:" + billNo);
						num = num + 1;
						if (StringUtils.isNotBlank(returnStr) && "success".equals(returnStr.trim().toLowerCase())) {
							order.setNoticeStatus("0");
							order.setNoticeReturn(returnStr);
							order.setLastNoticeTime(new Date());
							order.setNoticeTimes((order.getNoticeTimes() == null ? 0 : order.getNoticeTimes()) + 1);
							updateOrder(order);
							flag = true;
						} else {
							order.setNoticeStatus("1");
							order.setNoticeReturn(returnStr);
							order.setLastNoticeTime(new Date());
							order.setNoticeTimes((order.getNoticeTimes() == null ? 0 : order.getNoticeTimes()) + 1);
							updateOrder(order);
						}
					} catch (Exception e) {
						e.printStackTrace();
						order.setNoticeStatus("1");
						order.setLastNoticeTime(new Date());
						order.setNoticeReturn("通知业务系统出现错误:" + CommonUtil.getExceptionStr(e));
						order.setNoticeTimes((order.getNoticeTimes() == null ? 0 : order.getNoticeTimes()) + 1);
						updateOrder(order);
					}

				}

			}
		};

		t.start();
	}

	public List queryOrderLog(Map params) {

		return orderLogDao.getAll(params);
	}

	public List queryOrder(Map params) {
		return orderDao.getAll(params);
	}

}
