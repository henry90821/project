package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface RuleInfoMapper {

	/**
	 * 规则查询
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryRuleInfo(Map<String,Object> param) ;
	
	/**
	 * 规则触发表达式查询
	 * @param param
	 * @return
	 */
	Map<String,String> qryRuleExp(Map<String,Object> param) ;
	
	/**
	 * 规则处理sql查询 
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryRuleSql(Map<String,Object> param) ;
	
	/**
	 * SQL参数查询
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qrySqlParam(Map<String,Object> param) ;
	
	/**
	 * 规则处理查询 
	 * @param param
	 * @return
	 */
	Map<String,Object> qryRuleHandle(Map<String,Object> param) ;
	
}
