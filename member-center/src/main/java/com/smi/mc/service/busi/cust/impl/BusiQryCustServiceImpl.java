package com.smi.mc.service.busi.cust.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.BusiQryFeeAtomicService;
import com.smi.mc.service.busi.cust.BusiQryCustService;

/**
 * 会员业务查询服务类
 * 
 * @author smi
 *
 */
@Service("busiQryCustService")
public class BusiQryCustServiceImpl implements BusiQryCustService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BusiQryFeeAtomicService busiQryFeeAtomicService;

	/**
	 * 会员业务查询方法
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> busiCustQry(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();

		List<Object> offerId = new ArrayList<Object>();
		offerId = (List<Object>) param.get("OFFER_ID");

		String servCode = (String) param.get("SERV_CODE");
		if (StringUtils.isEmpty(servCode)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数SERV_CODE不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}

		Map<String, Object> paramIn = new HashMap<String, Object>();
		Map<String, Object> paramOut = new HashMap<String, Object>();
		paramIn.put("OFFER_ID", offerId);
		paramIn.put("SERV_CODE", servCode);
		try {
			paramOut = this.busiQryFeeAtomicService.qryBusiFeeInfo(paramIn);
		} catch (Exception e) {
			this.logger.error("调用业务定义查询原子服务失败", e);
			throw new BusiServiceException(e);
		}

		List<Object> resSuccessList = (List<Object>) paramOut.get("CODE_OFFER");

		if (resSuccessList.size() <= 0) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "业务参数错误!");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}

		long SumAmount = 0L;
		for (int i = 0; i < resSuccessList.size(); i++) {
			String billingCode = (String) ((Map<String, Object>) resSuccessList.get(i)).get("BILLING_CODE");
			if ("1".equals(billingCode)) {
				BigDecimal temp = (BigDecimal) ((Map<String, Object>) resSuccessList.get(i)).get("PAY_NUMBER");
				SumAmount += temp.longValue();
			}

		}

		long maxAmount = 0L;
		for (int i = 0; i < resSuccessList.size(); i++) {
			String billingCode = (String) ((Map<String, Object>) resSuccessList.get(i)).get("BILLING_CODE");
			if ("0".equals(billingCode)) {
				BigDecimal payNum = (BigDecimal) ((Map<String, Object>) resSuccessList.get(i)).get("PAY_NUMBER");
				if (payNum.longValue() >= maxAmount) {
					maxAmount = payNum.longValue();
				}
			}
		}

		if (SumAmount >= maxAmount) {
			respMap.put("ORDER_SUM", Long.valueOf(SumAmount));
			for (int i = 0; i < resSuccessList.size(); i++) {
				String billingCode = (String) ((Map<String, Object>) resSuccessList.get(i)).get("BILLING_CODE");
				if ("0".equals(billingCode)) {
					((Map<String, Object>) resSuccessList.get(i)).put("PAY_NUMBER", Integer.valueOf(0));
				}
			}

		}

		if (SumAmount < maxAmount) {
			respMap.put("ORDER_SUM", Long.valueOf(SumAmount + maxAmount));
		}

		List<Object> assembleList = new ArrayList<Object>();
		for (int i = 0; i < resSuccessList.size(); i++) {
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("PAY_TYPE", ((Map<String, Object>) resSuccessList.get(i)).get("PAY_TYPE"));
			resMap.put("PAY_NUMBER", ((Map<String, Object>) resSuccessList.get(i)).get("PAY_NUMBER"));
			resMap.put("OBJ_TYPE", ((Map<String, Object>) resSuccessList.get(i)).get("FEE_OBJ_ID"));
			assembleList.add(resMap);
		}
		respMap.put("CODE_OFFER", assembleList);

		RESP resp = RESP.createSuccessResp();
		respMap.put("QRY_RESULTS", resp);

		return respMap;
	}
}