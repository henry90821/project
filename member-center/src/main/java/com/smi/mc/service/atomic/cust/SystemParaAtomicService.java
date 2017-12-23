package com.smi.mc.service.atomic.cust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.SystemParaMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class SystemParaAtomicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SystemParaMapper systemParaMapper; 

	public Map<String, Object> qryPara(Map<String, Object> param) throws AtomicServiceException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = systemParaMapper.qryPara(param);
			
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("para", list);
			return resMap;
		} catch (Exception e) {
			logger.error("系统参数查询", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> qryCodeList(Map<String, Object> param) throws AtomicServiceException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = systemParaMapper.qryCodeList(param);
			
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("codeList", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("系统参数查询", e);
			throw new AtomicServiceException(e);
		}
	}
}