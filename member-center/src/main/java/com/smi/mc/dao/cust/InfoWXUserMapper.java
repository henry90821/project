package com.smi.mc.dao.cust;

import java.util.Map;

public interface InfoWXUserMapper {

	Map<String, Object> queryInfoWXUser(Map<String, Object> param);

	int addInfoWXUser(Map<String, Object> param);

	int updateStateByWXUserId(Map<String, Object> param);
	
	int updateCustIdByWXUserId(Map<String, Object> param);

	Map<String, Object> getInfoWXUserId(Map<String, Object> param);
	
}
