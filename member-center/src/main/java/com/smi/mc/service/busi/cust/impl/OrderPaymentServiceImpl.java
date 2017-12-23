package com.smi.mc.service.busi.cust.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.ConfigConstants;
import com.smi.mc.esb.http.HttpToEsb;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderPayItemAtomicService;
import com.smi.mc.service.busi.cust.OrderPaymentService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("orderPaymentService")
public class OrderPaymentServiceImpl implements OrderPaymentService {

	@Autowired
	private InfoOrderAtomicService infoOrderAtomicService;

	// @Autowired
	// private RechargeVip rechargeVip;

	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private InfoOrderPayItemAtomicService infoOrderPayItemAtomicService;

	@Autowired
	private CustAtomicService custAtomicService;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Map<String, Object> orderPayment(Map<String, Object> requestMap) throws BusiServiceException {
		Map<String, Object> returnMap = null;

		@SuppressWarnings("unchecked")
		Map<String, Object> orderInfoMap = (Map<String, Object>) requestMap.get("ORDER_INFO");

		@SuppressWarnings("unchecked")
		Map<String, Object> rechargeInfoMap = (Map<String, Object>) requestMap.get("RECHARGE_INFO");

		@SuppressWarnings("unchecked")
		Map<String, Object> orderPayReqMap = (Map<String, Object>) requestMap.get("ORDER_PAY_REQ");
		try {
			returnMap = isNullCheck(orderInfoMap);
			if (null != returnMap) {
				return returnMap;
			}

			Object order_id = orderInfoMap.get("ORDER_ID");
			returnMap = isNullCheck(rechargeInfoMap, orderPayReqMap, order_id);
			this.logger.debug("返回参数为：" + returnMap);
			return returnMap;
		} catch (BusiServiceException e) {
			this.logger.error("订单缴费服务异常", e);
			throw new BusiServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> isNullCheck(Map<String, Object> rechargeInfoMap, Map<String, Object> orderPayReqMap,
			Object order_id) throws BusiServiceException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			String cust_id = "";
			String serv_code = "";
			if ((null != rechargeInfoMap) && (rechargeInfoMap.size() > 0)) {
				if ((null != orderPayReqMap) && (orderPayReqMap.size() > 0)) {
					Object PAY_AMOUNT = rechargeInfoMap.get("PAY_AMOUNT");

					Object ChannelCode = rechargeInfoMap.get("ChannelCode");
					Object PAY_METHOD = rechargeInfoMap.get("PAY_METHOD");
					Object BANK_CODE = rechargeInfoMap.get("BANK_CODE");
					Object BANK_SERIAL_NBR = rechargeInfoMap.get("BANK_SERIAL_NBR");

					Object STAFF_ID = rechargeInfoMap.get("STAFF_ID");
					Object SYSTEM_ID = rechargeInfoMap.get("SYSTEM_ID");

					returnMap.put("ORDER_ID", order_id);
					Map<String, Object> orderMap = this.infoOrderAtomicService.qryInfoOrder(returnMap);
					returnMap.clear();
					List<Object> list = (List<Object>) orderMap.get("INFO_ORDER");
					if (CollectionUtils.isNotEmpty(list)) {
						returnMap = (Map<String, Object>) list.get(0);
						cust_id = (String) returnMap.get("CUST_ID");
						serv_code = (String) returnMap.get("SERV_CODE");
					}
					try {
						Map<String, Object> reqMap = new HashMap<String, Object>();
						int succSum = 0;
						SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
						String REQUEST_ID = this.commonAtomicService.getSequence("SEQ_REQUEST_ID");
						REQUEST_ID = ChannelCode + sdf.format(new Date()) + REQUEST_ID;
						this.logger.info("调用计费充值接口服务开始！");
						String reqJson = "\"SvcCont\":{\"SOO\":[{\"PUBSEQ\":{\"TYPE\":\"RECHARGE_INFO\"},\"RECHARGE_INFO\":{\"BANK_CODE\":\""
								+ BANK_CODE + "\",\"BANK_SERIAL_NBR\":\"" + BANK_SERIAL_NBR + "\",\"MEMBER_ID\":\""
								+ cust_id + "\",\"PAY_AMOUNT\":\"" + PAY_AMOUNT + "\",\"PAY_METHOD\":\"" + PAY_METHOD
								+ "\",\"REQUEST_ID\":\"" + REQUEST_ID + "\",\"STAFF_ID\":\"" + STAFF_ID
								+ "\",\"SYSTEM_ID\":\"" + SYSTEM_ID + "\"}}]}";
						// 改用直接调用esb,废弃原来的dubbo调用
						String resultJson=HttpToEsb.httpToEsb(reqJson, "132", "", "/ServiceBus/custView/cust/Recharge001",ConfigConstants.rechargeVipURL);
						this.logger.info("计费充值接口服务返回报文： " + resultJson);
						JSONObject jsonObject = JSONObject.fromObject(resultJson);
						JSONArray array = jsonObject.getJSONArray("SOO");
						JSONObject object = (JSONObject) array.get(0);
						Map<String, Object> Resp = (Map<String, Object>) object.get("RESP");
						if ("0".equals(Resp.get("RESULT"))) {
							String ORDER_PAY_ID = this.commonAtomicService.getSequenceByTable("INFO_ORDER_PAY_ITEM");

							Object ORDER_ID = orderPayReqMap.get("ORDER_ID");
							Object ORG_ID = orderPayReqMap.get("ORG_ID");
							Object PAY_TYPE = orderPayReqMap.get("PAY_TYPE");
							Object PAY_NUMBER = orderPayReqMap.get("PAY_NUMBER");

							Object DEDU_AMOUNT = orderPayReqMap.get("DEDU_AMOUNT");
							Object EXT_PAY_SERIAL = orderPayReqMap.get("EXT_PAY_SERIAL");
							reqMap.clear();
							reqMap.put("ORDER_PAY_ITEM_ID", ORDER_PAY_ID);
							reqMap.put("ORDER_ID", ORDER_ID);
							reqMap.put("PAY_SERIAL", ORG_ID);
							reqMap.put("PAY_TYPE", PAY_TYPE);
							reqMap.put("PAY_AMOUNT", PAY_NUMBER);
							reqMap.put("DEDU_AMOUNT", DEDU_AMOUNT);
							reqMap.put("EXT_PAY_SERIAL", EXT_PAY_SERIAL);
							this.logger.info("订单支付数据：" + reqMap);
							this.infoOrderPayItemAtomicService.addInfo(reqMap);
						} else {
							returnMap.put("MSG", Resp.get("MSG"));
							returnMap.put("RESULT", "1");
							returnMap.put("CODE", Resp.get("CODE"));
							return returnMap;
						}
						this.logger.info("调用计费充值接口服务结束！");
					} catch (Exception e) {
						this.logger.error("调用计费中心会员充值接口服务", e);
						throw new BusiServiceException(e);
					}
				} else {
					returnMap.put("MSG", "订单缴费信息缺失");
					returnMap.put("RESULT", "1");
					returnMap.put("CODE", "10");
					return returnMap;
				}
			} else {
				returnMap.put("MSG", "订单缴费信息缺失");
				returnMap.put("RESULT", "1");
				returnMap.put("CODE", "10");
				return returnMap;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("STATUS_CD", "1000");
			map.put("PAY_STATUS", "1");
			map.put("ORDER_ID", order_id);
			this.logger.info("调用订单事件修改原子服务开始" + map);
			this.infoOrderAtomicService.updateInfoOrder(map);
			this.logger.info("调用订单事件修改服务结束");

			if (("100".equals(serv_code)) || ("111".equals(serv_code)) || ("101".equals(serv_code))) {
				map.clear();
				map.put("CUST_ID", cust_id);
				map.put("STATUS_CD", "1000");
				this.custAtomicService.updCust(map);
				this.logger.info("调用用户信息修改服务结束");
			}
			returnMap.put("RESULT", "0");
			return returnMap;
		} catch (Exception e1) {
			this.logger.error("订单确认缴费服务异常", e1);
			throw new BusiServiceException(e1);
		}
	}

	public Map<String, Object> isNullCheck(Object mapName) {
		Map<String, Object> resultMap = null;

		if ((null == mapName) || ("".equals(mapName))) {
			resultMap = new HashMap<String, Object>();
			resultMap.put("MSG", "参数ORDER_INFO不能为空");
			resultMap.put("RESULT", "1");
			resultMap.put("CODE", "4");
		} else {
			Object orderId = ((Map) mapName).get("ORDER_ID");
			if ((null == orderId) || ("".equals(orderId))) {
				resultMap = new HashMap<String, Object>();
				resultMap.put("MSG", "参数ORDER_ID不能为空");
				resultMap.put("RESULT", "1");
				resultMap.put("CODE", "4");
			}
		}
		return resultMap;
	}
}