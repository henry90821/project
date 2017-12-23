package com.smi.mc.service.busi.cust.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderChildAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderPayItemAtomicService;
import com.smi.mc.service.atomic.cust.OrderChildDetailAtomicService;
import com.smi.mc.service.atomic.cust.OrderFeeItemAtomicService;
import com.smi.mc.service.atomic.cust.OrgOpAtomicService;
import com.smi.mc.service.atomic.cust.SystemParaAtomicService;
import com.smi.mc.service.busi.cust.OrderPaymentService;
import com.smi.mc.service.busi.cust.OrderService;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected InfoOrderAtomicService infoOrderAtomicService;

	@Autowired
	protected InfoOrderChildAtomicService infoOrderChildAtomicService;

	@Autowired
	protected OrderChildDetailAtomicService orderChildDetailAtomicService;

	@Autowired
	protected OrderFeeItemAtomicService orderFeeItemAtomicService;

	@Autowired
	protected InfoOrderPayItemAtomicService infoOrderPayItemAtomicService;

	@Autowired
	protected CommonAtomicService commonAtomicService;

	@Autowired
	protected OrgOpAtomicService orgOpAtomicService;

	@Autowired
	protected SystemParaAtomicService systemParaAtomicService;

	@Autowired
	protected OrderPaymentService orderPaymentService;

	@SuppressWarnings("unchecked")
	public Map<String, Object> addOrders(Map<String, Object> requestMap) throws BusiServiceException {
		Map<String, Object> map = null;
		List<Map<String, Object>> list = null;

		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			map = checkIsNull(requestMap);
			if (!((Boolean) map.get("flag")).booleanValue()) {
				return map;
			}

			map = (Map<String, Object>) requestMap.get("ADD_ORDER_REQ");

			String extOrderId = (String) map.get("EXT_ORDER_ID");
			String EXT_SYSTEM = (String) map.get("EXT_SYSTEM");
			if ((null != extOrderId) && (!"".equals(extOrderId))) {
				Map<String, Object> orderRequest = new HashMap<String, Object>();
				orderRequest.put("EXT_ORDER_ID", extOrderId);
				orderRequest.put("EXT_SYSTEM", EXT_SYSTEM);
				map = this.infoOrderAtomicService.qryInfoOrder(orderRequest);
				if (null != map && ((List) map.get("INFO_ORDER")).size() > 0) {
					resultMap.put("MSG", "订单已存在");
					resultMap.put("RESULT", Constants.STATE_CODE_FAIL);
					resultMap.put("CODE", Constants.EXCEPTION_CODE_TWO);
					resultMap.put("ORDER_ID", map.get("ORDER_ID"));
					resultMap.put("PAY_STATUS", "1");
					return resultMap;
				}

			}

			Map<String, Object> orderMap1 = (Map) requestMap.get("ADD_ORDER_REQ");
			String serv_code = (String) orderMap1.get("SERV_CODE");

			Map<String, Object> sysMap = new HashMap<String, Object>();
			sysMap.put("PARA_CODE", serv_code);
			sysMap.put("PARA_TYPE", "OrderStatusFeeFlag");
			this.logger.debug("查询SYSTEM_PARA表开始");
			map = this.systemParaAtomicService.qryPara(sysMap);
			this.logger.debug("查询SYSTEM_PARA表结束");
			String order_status = null;
			String pay_indent = null;
			String pay_status = null;

			List systemList = (List) map.get("para");
			if ((null != systemList) && (systemList.size() > 0)) {
				map = (Map) systemList.get(0);
				order_status = (String) map.get("PARA_VALUE1");
				pay_indent = (String) map.get("PARA_VALUE2");
				pay_status = (String) map.get("PARA_VALUE3");
			} else {
				this.logger.debug("查询SYSTEM_PARA配置信息为空");
			}

			map = (Map<String,Object>) requestMap.get("ADD_ORDER_REQ");

			String orderId = this.commonAtomicService.getSequenceByTable("info_order");
			map.put("ORDER_ID", orderId);

			map = parameterChange(map, "ADD_ORDER_REQ");

			map.put("STATUS_CD", order_status);
			map.put("PAY_STATUS", pay_status);
			this.infoOrderAtomicService.addInfoOrder(map);

			list = (List) map.get("ADD_ORDER_CHILD_REQ");

			for (Map<String, Object> m : list) {
				String childOrderId = this.commonAtomicService.getSequenceByTable("INFO_ORDER_CHILD");
				m.put("ORDER_CHILD_ID", childOrderId);
				m.put("ORDER_ID", orderId);

				m = parameterChange(m, "ADD_ORDER_CHILD_REQ");
				this.infoOrderChildAtomicService.addInfo(m);

				List<Map<String, Object>> childList = (List) m.get("ADD_ORDER_DETAIL_REQ");

				if ((null != childList) && (childList.size() > 0)) {
					for (Map<String, Object> cl : childList) {
						String nextId = this.commonAtomicService.getSequenceByTable("INFO_ORDER_CHILD_DETAIL");
						cl.put("ORDER_CHILD_DETAIL_ID", nextId);
						cl.put("ORDER_CHILD_ID", childOrderId);

						cl = parameterChange(cl, "ADD_ORDER_DETAIL_REQ");
						this.orderChildDetailAtomicService.addOrderChildDetail(cl);
					}

				}

				childList = (List) m.get("ADD_ORDER_FEE_REQ");

				if ((null != childList) && (childList.size() > 0))
					for (Map<String, Object> cl : childList) {
						String nextId = this.commonAtomicService.getSequenceByTable("INFO_ORDER_FEE_ITEM");
						cl.put("ORDER_FEE_ITEM_ID", nextId);
						cl.put("ORDER_CHILD_ID", childOrderId);

						cl = parameterChange(cl, "ADD_ORDER_FEE_REQ");
						this.orderFeeItemAtomicService.addOrderFeeItem(cl);
					}
			}
			list = (ArrayList) map.get("ADD_ORDER_PAY_REQ");
			if ((null != list) && (list.size() > 0)) {
				for (Map<String, Object> l : list) {
					String nextId = this.commonAtomicService.getSequenceByTable("INFO_ORDER_PAY_ITEM");
					l.put("ORDER_PAY_ITEM_ID", nextId);
					l.put("ORDER_ID", orderId);

					l = parameterChange(l, "ADD_ORDER_PAY_REQ");
					this.infoOrderPayItemAtomicService.addInfo(l);
				}

			}

			if ("0".equals(pay_indent)) {
				resultMap.put("PAY_STATUS", pay_indent);
			} else if ("1".equals(pay_indent)) {
				Object payMap = new HashMap<String, Object>();
				Map<String, Object> orderMap = new HashMap<String, Object>();
				orderMap.put("ORDER_ID", orderId);
				((Map) payMap).put("ORDER_INFO", orderMap);

				Map<String,Object> resultPayMap = null;
				this.orderPaymentService.orderPayment((Map<String,Object>) payMap);
				this.logger.info("缴费业务服务结果：" + resultPayMap);

				resultMap.put("PAY_STATUS", pay_indent);
			}

			resultMap.put("ORDER_ID", orderId);
			resultMap.put("RESULT", Integer.valueOf(0));

			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单新增异常");
			throw new BusiServiceException(e);
		}
	}

	public Map<String, Object> queryOrders(Map<String, Object> requestMap) throws BusiServiceException {
		Map<String, Object> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			map = this.infoOrderAtomicService.qryInfoOrder(requestMap);
			resultMap.put("ORDER_INFO", map);
			resultMap.put("RESP", RESP.createSuccessResp());
			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单查询异常");
			throw new BusiServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> checkIsNull(Map<String, Object> requestMap) {
		Map<String, Object> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean flag = true;

		map = (Map<String, Object>) requestMap.get("ADD_ORDER_REQ");
		if ((null != map) && (map.size() > 0)) {
			if (null != map.get("CUST_ID")) {
				if (null == map.get("ORDER_TYPE"))
					;
			}

			flag = (null != map.get("CREATE_CHANNEL_ID")) && (null != map.get("EXT_SYSTEM"))
					&& (null != map.get("BUSINESS_TYPE")) && (null != map.get("SERV_CODE"));

			if (!flag) {
				map.put("flag", Boolean.valueOf(false));
				map.put("MSG", "ADD_ORDER_REQ参数错误");
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", Constants.EXCEPTION_CODE_FOUR);
				map.put("ORDER_ID", map.get("ORDER_ID"));
				return map;
			}
		} else {
			resultMap.put("flag", Boolean.valueOf(false));
			resultMap.put("MSG", "ADD_ORDER_REQ参数错误");
			resultMap.put("RESULT", Constants.STATE_CODE_FAIL);
			resultMap.put("CODE", Constants.EXCEPTION_CODE_FOUR);
			resultMap.put("ORDER_ID", map.get("ORDER_ID"));
			return resultMap;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, String>> childList = (ArrayList<Map<String, String>>) map.get("ADD_ORDER_CHILD_REQ");
		if ((null != childList) && (childList.size() > 0)) {
			for (Map<String, String> m : childList) {
				flag = (null != m.get("OBJ_TYPE")) && (!"".equals(m.get("OBJ_TYPE"))) && (null != m.get("OBJ_ID"))
						&& (!"".equals(m.get("OBJ_ID")));
				if (!flag) {
					map.put("flag", Boolean.valueOf(false));
					map.put("MSG", "ADD_ORDER_CHILD_REQ参数错误");
					map.put("RESULT", Constants.STATE_CODE_FAIL);
					map.put("CODE", Constants.EXCEPTION_CODE_FOUR);
					map.put("ORDER_ID", map.get("ORDER_ID"));
					return map;
				}
			}
		} else {
			resultMap.put("flag", Boolean.valueOf(false));
			resultMap.put("MSG", "ADD_ORDER_CHILD_REQ参数错误");
			resultMap.put("RESULT", Constants.STATE_CODE_FAIL);
			resultMap.put("CODE", Constants.EXCEPTION_CODE_FOUR);
			resultMap.put("ORDER_ID", map.get("ORDER_ID"));
			return resultMap;
		}

		map.put("flag", Boolean.valueOf(true));
		return map;
	}

	public Map<String, Object> queryAllOrders(Map<String, Object> requestMap) throws BusiServiceException {
		Map<String, Object> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			map = this.infoOrderAtomicService.qryInfoOrder(requestMap);
			if ((null == map) || (map.size() == 0)) {
				resultMap.put("RESP", RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "订单不存在"));
				resultMap.put("ORDER_ID", requestMap.get("ORDER_ID"));
				return resultMap;
			}

			resultMap.put("ORDER_INFO", map);

			map = this.infoOrderChildAtomicService.qryInfo(resultMap);

			resultMap.put("CHILD_ORDER_INFO", map);

			map = this.infoOrderPayItemAtomicService.qryInfo(resultMap);

			resultMap.put("resultMap", map);
			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单详情查询异常");
			throw new BusiServiceException(e);
		}
	}

	public Map<String, Object> queryOtherOrders(Map<String, Object> requestMap) throws BusiServiceException {
		Map<String, Object> map = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			map = this.orderFeeItemAtomicService.qryOrderFeeItem(requestMap);

			resultMap.put("ORDER_FEE_INFO", map);

			map = this.orderChildDetailAtomicService.qryOrderChildDetail(requestMap);
			resultMap.put("ORDER_DETAIL_INFO", map);
			return resultMap;
		} catch (Exception e) {
			this.logger.error("子订单详情查询2异常");
			throw new BusiServiceException(e);
		}
	}

	public Map<String, Object> parameterChange(Map<String, Object> requestMap, String requestName) throws Exception {
		if ("ADD_ORDER_REQ".equals(requestName)) {
			requestMap.put("CRT_DATE", requestMap.get("CREATE_DATE"));
			String sysUserId = "";

			if ((requestMap.get("CREATE_OPERATOR_ID") == null) || ("".equals(requestMap.get("CREATE_OPERATOR_ID")))) {
				sysUserId = this.orgOpAtomicService.querySysUId(requestMap.get("CREATE_CHANNEL_ID").toString());
			} else
				sysUserId = requestMap.get("CREATE_OPERATOR_ID").toString();

			requestMap.put("SYSTEM_USER_ID", sysUserId);
			requestMap.put("ORG_ID", requestMap.get("CREATE_CHANNEL_ID"));
			requestMap.put("SYSTEM_ID", requestMap.get("EXT_SYSTEM"));
			return requestMap;
		}
		if ("ADD_ORDER_CHILD_REQ".equals(requestName)) {
			requestMap.put("CRT_DATE", requestMap.get("CREATE_DATE"));
			return requestMap;
		}
		if ("ADD_ORDER_DETAIL_REQ".equals(requestName)) {
			requestMap.put("OBJ_CODE", requestMap.get("FIELD_PARAMETER"));

			requestMap.put("ENTITY_CODE", requestMap.get("FIELD_NAME"));
			requestMap.put("BEFORE_VALUE", requestMap.get("BEFORE_PARAMETER"));
			requestMap.put("AFTER_VALUE", requestMap.get("AFTER_PARAMETER"));
			requestMap.put("CRT_DATE", requestMap.get("CREATE_DATE\t"));
			return requestMap;
		}
		if ("ADD_ORDER_FEE_REQ".equals(requestName)) {
			requestMap.put("AMOUNT", requestMap.get("ORDER_FEE"));
			requestMap.put("DISCOUNT_AMOUNT", requestMap.get("DISCOUNT_FEE"));
			requestMap.put("AMOUNT", requestMap.get("ORDER_FEE"));
			requestMap.put("DISCOUNT_RESON", requestMap.get("DISCOUNT_REASON"));
			requestMap.put("REAL_AMOUNT", requestMap.get("REAL_FEE"));
			requestMap.put("CRT_DATE", requestMap.get("CREATE_DATE"));
			return requestMap;
		}
		if ("ADD_ORDER_PAY_REQ".equals(requestName)) {
			requestMap.put("PAY_AMOUNT", requestMap.get("PAY_NUMBER"));
			requestMap.put("CRT_DATE", requestMap.get("CREATE_DATE"));
			return requestMap;
		}
		return null;
	}
}