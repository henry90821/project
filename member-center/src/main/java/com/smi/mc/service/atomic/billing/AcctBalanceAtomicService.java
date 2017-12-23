package com.smi.mc.service.atomic.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.billing.InfoPayBalanceMapper;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.model.bill.AcctBalance;

@Service
public class AcctBalanceAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public static final long DEFAULT_BALANCE_TYPE_ID = 1L;
	@Autowired
	private InfoPayBalanceMapper infoPayBalanceMapper;

	/**
	 * 新增余额账户
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> acctBalanceInsert(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:AcctBalanceAtomicService.acctBalanceInsert");
		try {
			int succNum = infoPayBalanceMapper.createAcctBalance(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_NUM", Integer.valueOf(succNum));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新建帐户余额帐本异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 更新账户余额
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> acctBalanceUpdate(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:AcctBalanceAtomicService.acctBalanceUpdate");
		try {
			String OPERATE_TYPE = param.get("OPERATE_TYPE") + "";
			double AMOUTE = 0.0D;
			if (OPERATE_TYPE.equals("1"))
				AMOUTE = Double.parseDouble(param.get("AMOUTE") + "");
			else {
				AMOUTE = -Double.parseDouble(param.get("AMOUTE") + "");
			}
			param.put("BALANCE", Double.valueOf(AMOUTE));

			int succNum = infoPayBalanceMapper.updateAcctBalance(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_NUM", Integer.valueOf(succNum));
			return resMap;
		} catch (Exception e) {
			this.logger.error("修改帐户余额帐本异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 查询余额账户信息
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> acctBalanceQuery(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:AcctBalanceAtomicService.acctBalanceQuery");
		try {
			List<AcctBalance> list = infoPayBalanceMapper.queryAcctBalance(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("QUERY_RESULT", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询帐户余额帐本异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 账户渠道余额查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> balanceQuery(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:AcctBalanceAtomicService.balanceQuery");
		try {
			List<AcctBalance> list = null;
			// 根据systemId去rule_sys_out_bal_type_rel表查询 是否有数据
			List<Map<String, Object>> ruleTypelist = infoPayBalanceMapper.queryRuleBalType(param);
			if (ruleTypelist.size() > 0) {
				list = infoPayBalanceMapper.queryBalance(param); // 有数据的查询情况
			} else {
				list = infoPayBalanceMapper.queryBalanceExp(param);// 无数据的查询情况
			}
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("ACCT_BALANCE", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("帐户渠道支付余额查询异常", e);
			throw new AtomicServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> balanceDeduct(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:AcctBalanceAtomicService.balanceDeduct");
		}

		try {

			List<AcctBalance> acctBalanceList = (List<AcctBalance>) param.get("ACCT_BALANCE");

			long defaultBalanceTypeId = 0L;
			long defaultBalanceId = 0L;
			BigDecimal defaultAcctBalance = new BigDecimal(0);

			for (AcctBalance acctBalance : acctBalanceList) {
				if (acctBalance.getBALANCE_TYPE_ID() == 1L) {
					defaultBalanceTypeId = acctBalance.getBALANCE_TYPE_ID();
					defaultBalanceId = acctBalance.getBALANCE_ID();
					defaultAcctBalance = new BigDecimal(acctBalance.getREAL_BALANCE());
				}

			}

			List<Object> payMethodList = (List<Object>) param.get("PAYMETHOD_INFO");

			Map<String, Object> resMap = new HashMap<String, Object>();
			List<Object> thirdPayList = new ArrayList<Object>();
			List<Object> deductAcctBalanceList = new ArrayList<Object>();

			for (int i = 0; i < payMethodList.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) payMethodList.get(i);
				long payCode = Long.parseLong(map.get("PAY_CODE") + "");
				BigDecimal amount = new BigDecimal(0);
				if (payCode == 0L)
					amount = new BigDecimal((String) map.get("AMOUNT")).multiply(new BigDecimal("100"));
				else
					amount = new BigDecimal((String) map.get("AMOUNT"));
				Map<String, Object> thirdPay;
				if ((payCode == 11L) || (payCode == 12L) || (payCode == 14L)) {
					thirdPay = new HashMap<String, Object>();

					amount = new BigDecimal((String) map.get("AMOUNT")).multiply(new BigDecimal("100"));
					thirdPay.put("PAY_CODE", map.get("PAY_CODE"));
					thirdPay.put("AMOUNT", amount);
					thirdPay.put("BANK_SERIAL_NBR", map.get("BANK_SERIAL_NBR"));
					thirdPay.put("DEFAULT_ACCT_BALANCE", defaultAcctBalance);
					thirdPay.put("DEFAULT_BALANCE_ID", Long.valueOf(defaultBalanceId));
					thirdPay.put("DEFAULT_BALANCE_TYPE_ID", Long.valueOf(defaultBalanceTypeId));

					thirdPayList.add(thirdPay);
				} else {
					for (AcctBalance acctBalance : acctBalanceList) {
						Map<String, Object> acctBalanceMap = new HashMap<String, Object>();
						if (payCode == acctBalance.getPAY_CODE()) {
							BigDecimal REAL_BALANCE = new BigDecimal(acctBalance.getREAL_BALANCE()).subtract(amount);
							int flag = REAL_BALANCE.compareTo(new BigDecimal(0));
							if (flag == -1) {
								amount = amount.subtract(new BigDecimal(acctBalance.getREAL_BALANCE()));

								if (acctBalance.getBALANCE_TYPE_ID() == 1L) {
									defaultAcctBalance = new BigDecimal(0);
								}

								acctBalanceMap.put("BALANCE_ID", Long.valueOf(acctBalance.getBALANCE_ID()));
								acctBalanceMap.put("BALANCE_TYPE_ID", Long.valueOf(acctBalance.getBALANCE_TYPE_ID()));
								acctBalanceMap.put("DEDUCT_BALANCE", new BigDecimal(acctBalance.getREAL_BALANCE()));
								acctBalanceMap.put("OLD_BALANCE", Long.valueOf(acctBalance.getREAL_BALANCE()));
								acctBalanceMap.put("NEW_BALANCE", new BigDecimal(0));
								deductAcctBalanceList.add(acctBalanceMap);
							} else {
								if (acctBalance.getBALANCE_TYPE_ID() == 1L) {
									defaultAcctBalance = REAL_BALANCE;
								}

								acctBalanceMap.put("BALANCE_ID", Long.valueOf(acctBalance.getBALANCE_ID()));
								acctBalanceMap.put("BALANCE_TYPE_ID", Long.valueOf(acctBalance.getBALANCE_TYPE_ID()));
								acctBalanceMap.put("DEDUCT_BALANCE", amount);
								acctBalanceMap.put("OLD_BALANCE", Long.valueOf(acctBalance.getREAL_BALANCE()));
								acctBalanceMap.put("NEW_BALANCE", REAL_BALANCE);
								deductAcctBalanceList.add(acctBalanceMap);
								amount = new BigDecimal(0);
							}
						}
						int flag = amount.compareTo(new BigDecimal(0));
						if (flag == 0) {
							break;
						}

					}

					int flag = amount.compareTo(new BigDecimal(0));
					if (flag > 0) {
						resMap.put("DEDUCT_FLAG", Integer.valueOf(-1));
						return resMap;
					}
				}
			}

			resMap.put("THIRD_PAY", thirdPayList);
			resMap.put("DEDUCT_ACCT_BALANCE", deductAcctBalanceList);
			resMap.put("DEFAULT_BALANCE", defaultAcctBalance);
			resMap.put("DEDUCT_FLAG", Integer.valueOf(0));
			return resMap;
		} catch (Exception e) {
			this.logger.error("支付余额扣减异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 百度余额查询
	 * 
	 * @param param
	 * @return
	 */
	public Integer baiDuAcctBalanceQuery(Map<String, Object> param) {
		int num = this.infoPayBalanceMapper.baiDuAcctBalanceQuery(param);
		return num;

	}

	/**
	 * 百度余额明细查询
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> baiDuAcctBalanceDetailQuery(Map<String, Object> param) {
		List<Map<String, Object>> list = this.infoPayBalanceMapper.baiDuAcctBalanceDetailQuery(param);
		return list;

	}
}