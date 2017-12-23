package com.smi.mc.service.busi.bill.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smi.mc.service.atomic.billing.AcctBalanceAtomicService;
import com.smi.mc.service.atomic.cust.PayAtomicService;
import com.smi.mc.service.busi.bill.BalQryService;
import com.smilife.core.exception.SmiBusinessException;

/**
 * 百度余额基础类
 * 
 * @author smi
 *
 */
@Service("balQryService")
public class BalQryServiceImpl implements BalQryService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected AcctBalanceAtomicService acctBalanceAtomicService;

	@Autowired
	protected PayAtomicService payAtomicService;

	/**
	 * 百度余额查询
	 */
	@Override
	public Map<String, Object> baiduBalQuery(Map<String, Object> param) throws SmiBusinessException {
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();

		checkParameter(param, resp, map);
		String payId = null;
		try {
			Map<String, Object> mapIn = new HashMap<String, Object>();
			mapIn.put("cust_id", map.get("CUST_ID"));
			Map<String, Object> respQryPayMap = this.payAtomicService.qryInfoPay(mapIn);
			@SuppressWarnings("unchecked")
			List<Object> reqListPayMaps = (List<Object>) respQryPayMap.get("PAY");
			if ((!StringUtils.isEmpty(reqListPayMaps)) && (reqListPayMaps.size() > 0)
					&& (!StringUtils.isEmpty(reqListPayMaps.get(0)))) {
				@SuppressWarnings("unchecked")
				Map<String, Object> repInfoPayMap = (Map<String, Object>) reqListPayMaps.get(0);
				payId = (String) repInfoPayMap.get("PAY_ID");
			} else {
				throw new SmiBusinessException("3", "查询账户pay_id不存在");
			}

		} catch (Exception e) {
			this.logger.error("调用百度会员资料查询 原子服务失败", e);
			throw new SmiBusinessException("999", "调用帐户渠道支付余额查询原子服务失败");
		}
		this.logger.debug("===================payId:" + payId + "======================");
		map.put("PAY_ID", payId);
		Integer num = null;
		try {
			num = this.acctBalanceAtomicService.baiDuAcctBalanceQuery(map);
		} catch (Exception e) {
			this.logger.error("调用帐户渠道支付余额查询原子服务失败", e);
			throw new SmiBusinessException("999", "调用帐户渠道支付余额查询原子服务异常");
		}
		if (num != null) {
			double bd = num.doubleValue() * 0.01D;
			resp.put("CODE", "0");
			resp.put("MSG", "成功");
			resp.put("CASH", String.format("%2.2f", bd));
		}
		try {
			List<Map<String, Object>> listMap = this.acctBalanceAtomicService.baiDuAcctBalanceDetailQuery(map);
			if (listMap.size() > 0) {
				assembleDetail(listMap, resp);
			} else {
				resp.put("Bal_Detail", "");
			}
		} catch (Exception e) {
			this.logger.error("调用帐户渠道支付余额查询原子服务失败", e);
			throw new SmiBusinessException("999", "调用帐户渠道支付余额查询原子服务失败");
		}
		return resp;
	}

	/**
	 * 校验必填参数是否为空
	 * 
	 * @param param
	 * @param resp
	 * @param map
	 */
	public void checkParameter(Map<String, Object> param, Map<String, Object> resp, Map<String, Object> map) {
		if (!StringUtils.isEmpty((String) param.get("MEMBER_ID"))) {
			map.put("CUST_ID", param.get("MEMBER_ID").toString());
		} else {
			resp.put("CODE", "2");
			resp.put("MSG", "入参缺失");
		}
		if (!StringUtils.isEmpty((String) param.get("SYSTEM_ID"))) {
			map.put("SYSTEM_ID", param.get("SYSTEM_ID").toString());
		} else {
			resp.put("CODE", "2");
			resp.put("MSG", "入参缺失");
		}
	}

	public void assembleDetail(List<Map<String, Object>> acctBalance, Map<String, Object> result) {
		List<Object> balList = new ArrayList<Object>();
		for (int i = 0; i < acctBalance.size(); i++) {
			Map<String, Object> bal = new HashMap<String, Object>();
			Map<String, Object> acct = acctBalance.get(i);
			bal.put("ACCT_BALANCE_ID", acct.get("BALANCE_ID"));
			bal.put("BALANCE_NAME", acct.get("BALANCE_TYPE_NAME"));
			bal.put("BALANCE_TYPE_ID", acct.get("BALANCE_TYPE_ID"));
			bal.put("EFFECTIVE_DATE", acct.get("EFF_DATE"));
			bal.put("EXPIRY_DATE", acct.get("EXP_DATE"));
			BigDecimal b = (BigDecimal) acct.get("REAL_BALANCE");
			double d = b.doubleValue() * 0.01D;
			bal.put("USABLE_BAL", String.format("%2.2f", d));
			balList.add(bal);
		}
		result.put("Bal_Detail", balList);
	}
}