package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoOrderChildMapper {

	int addInfo(Map<String, Object> param);

	List<Map<String, Object>> qryInfo(Map<String, Object> param);

	List<Map<String, Object>> qryBaiDu(Map<String, Object> param);

}
