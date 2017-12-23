package com.smi.am.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.am.dao.SystemLogMapper;
import com.smi.am.dao.model.SystemLog;
import com.smi.am.service.ISystemLogService;

/**
 * 系统日志服务类
 * 
 * @author smi
 *
 */
@Service
public class SystemServiceImpl implements ISystemLogService {

	@Autowired
	private SystemLogMapper systemLogMapper;

	/**
	 * 新增系统日志
	 */
	@Override
	public int addSystemLog(SystemLog systemLog) {
		int num = this.systemLogMapper.addSystemLog(systemLog);
		return num;
	}

}
