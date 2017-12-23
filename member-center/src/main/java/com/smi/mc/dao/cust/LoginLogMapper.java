package com.smi.mc.dao.cust;

import java.util.Map;

public interface LoginLogMapper {

	/**
	 * 登录日志新增 -
	 * @param params
	 * @return
	 */
	int addLoginLog(Map<String,Object> params);
	
}
