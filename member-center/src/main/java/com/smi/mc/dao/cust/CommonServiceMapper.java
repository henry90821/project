package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CommonServiceMapper {

	String getSequenceByTable(Map<String, Object> param);

	List<Map<String, Object>> excuteSQL(Map<String, Object> param);
}
