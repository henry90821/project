package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface RightMapper {

	List<Map<String,Object>> qryRights(Map<String,Object> param);
	
}
