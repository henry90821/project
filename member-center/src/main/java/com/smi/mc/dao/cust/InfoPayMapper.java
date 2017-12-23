package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoPayMapper {

	/**
	 * 会员账户信息更新
	 * @param param
	 * @return
	 */
	int updInfoPay(Map<String, Object> param);

	/**
	 * 会员账户信息新增
	 * @param param
	 * @return
	 */
	int addInfoPay(Map<String, Object> param);

	/**
	 * 会员账户信息查询 
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> qryInfoPay(Map<String, Object> param);

}
