package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoRandomCodeMapper {

	List<Map<String, Object>> qryInfoRandomCode(Map<String, Object> param);

	int addInfoRandomCode(Map<String, Object> param);

	int updInfoRandomCode(Map<String, Object> param);

	int updInfoRandomCode1(Map<String, Object> param);

}
