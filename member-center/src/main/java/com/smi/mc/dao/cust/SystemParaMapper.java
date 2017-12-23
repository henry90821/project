package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface SystemParaMapper {

	/**
	 * 系统参数查询 
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryPara(Map<String,Object> param);
	
	/**
	 * code_list查询
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryCodeList(Map<String,Object> param);
	
}
