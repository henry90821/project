package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.RuleAtomicService;

public class RuleAtomicServiceTest extends BaseWebIntegrationTests {

	@Autowired
	private RuleAtomicService ruleAtomicService;

	@Test
	public void qryRuleInfo() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("BUSI_TYPE", "CUST_NBR_CHECK");
			param.put("SYS_ID", 1);
			List<Map<String, Object>> list= ruleAtomicService.qryRuleInfo(param);
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryRuleExp() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RULE_ID", 3);
			Map<String, String> map = ruleAtomicService.qryRuleExp(param);
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryRuleSql() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("OBJ_TYPE", 10);
			param.put("OBJ_ID", "R2");
			List<Map<String, Object>> list = ruleAtomicService.qryRuleSql(param);
			System.out.println(list.size());
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qrySqlParam() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("SQL_ID", 2);
			param.put("IN_OUT_TYPE", "IN");
			List<Map<String, Object>> list = ruleAtomicService.qrySqlParam(param);
			System.out.println(list.size());
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void qryRuleHandle() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("RULE_ID", 3);
			Map<String, Object> map = ruleAtomicService.qryRuleHandle(param);
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
