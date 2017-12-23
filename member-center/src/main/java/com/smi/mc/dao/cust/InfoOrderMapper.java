package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoOrderMapper {

	int addInfoOrder(Map<String, Object> param);

	List<Map<String, Object>> qryInfoOrder(Map<String, Object> param);

	int updateInfoOrder(Map<String, Object> param);

}
