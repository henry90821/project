package com.smi.am.service;

import com.smi.am.dao.model.SystemLog;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface ISystemLogService {
	
	int addSystemLog(SystemLog systemLog);

}
