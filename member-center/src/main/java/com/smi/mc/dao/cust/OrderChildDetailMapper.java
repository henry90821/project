package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface OrderChildDetailMapper {

	/**
	 * 订单明细信息查询  
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryOrderChildDetail(Map<String,Object> param) ;
	
	/**
	 * 订单明细资料新增
	 * @param param
	 * @return
	 */
	int addOrderChildDetail(Map<String,Object> param) ;
}
