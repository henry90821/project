package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface OrderFeeItemMapper {

	/**
	 * 订单费用新增
	 * @param param
	 * @return
	 */
	int addOrderFeeItem(Map<String,Object> param);
	
	/**
	 * 订单费用查询
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> qryOrderFeeItem(Map<String,Object> param);
	
}
