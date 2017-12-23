package com.smi.am.service;

import java.util.List;
import java.util.Map;

import com.smi.am.dao.model.AmRole;
import com.smi.am.service.vo.SystemLogVo;
import com.smi.am.utils.SmiResult;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface IRoleService {
	AmRole selectByRoleId(Integer rRoleid);

	SmiResult<List<SystemLogVo>> findSystemLog(Map<String, Object> param);

}
