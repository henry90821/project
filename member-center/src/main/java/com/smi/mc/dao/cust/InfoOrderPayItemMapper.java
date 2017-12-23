package com.smi.mc.dao.cust;

import java.util.Map;

public interface InfoOrderPayItemMapper {

	int addInfo(Map<String, Object> param);

	Map<String, Object> qryInfo(Map<String, Object> param);

}
