package com.smi.mc.dao.billing;

import java.util.List;
import java.util.Map;

import com.smi.mc.model.bill.AcctBalance;

public interface InfoPayBalanceMapper {

	int createAcctBalance(Map<String, Object> param);

	int updateAcctBalance(Map<String, Object> param);

	List<AcctBalance> queryBalance(Map<String, Object> param);

	List<AcctBalance> queryAcctBalance(Map<String, Object> param);

	List<Map<String, Object>> queryRuleBalType(Map<String, Object> param);

	List<AcctBalance> queryBalanceExp(Map<String, Object> param);

	int baiDuAcctBalanceQuery(Map<String, Object> param);
	
	List<Map<String,Object>> baiDuAcctBalanceDetailQuery(Map<String, Object> param);
}
