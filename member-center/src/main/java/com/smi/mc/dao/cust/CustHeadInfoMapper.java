package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CustHeadInfoMapper {

	int insertCustHeadInfo(Map<String, Object> param);

	List<Map<String, Object>> queryCustHeadInfo(Map<String, Object> param);

	int updateCustHeadInfo(Map<String, Object> param);
}
