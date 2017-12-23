package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoBusiPayMapper {

	int addInfoBusiPay(Map<String, Object> param);

	int updateInfoBusiPay(Map<String, Object> param);

	List<Map<String, Object>> qryInfoBusiPay(Map<String, Object> param);
}
