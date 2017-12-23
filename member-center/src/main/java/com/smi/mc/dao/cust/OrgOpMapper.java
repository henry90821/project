package com.smi.mc.dao.cust;

import java.util.Map;

public interface OrgOpMapper {

	/**
	 * 根据channel_code查询system_user_id
	 * @param param
	 * @return
	 */
	String queryOrgOpId(Map<String,Object> param);
	
}
