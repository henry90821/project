package com.smi.am.dao;

import java.util.List;
import java.util.Map;

import com.smi.am.dao.model.SystemLog;

public interface SystemLogMapper {
	
	int addSystemLog(SystemLog systemLog);
	
	List<SystemLog> selectSystemLog(Map<String,Object> paramMap);

}
