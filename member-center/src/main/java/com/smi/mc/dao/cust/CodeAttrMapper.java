package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CodeAttrMapper {

	/**
	 * 业务管理-业务查询
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> qryOffer(Map<String, Object> param);

	/**
	 * 业务管理-属性查询 
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> qryCodeAttr(Map<String, Object> param);

	/**
	 * 业务管理-属性值查询
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> qryCodeAttrVal(Map<String, Object> param);

}
