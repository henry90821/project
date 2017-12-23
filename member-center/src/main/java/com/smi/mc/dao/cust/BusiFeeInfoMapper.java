package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface BusiFeeInfoMapper {

	List<Map<String,Object>> qryBusiFeeInfo(Map<String,Object> param);
	
}
