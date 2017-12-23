package com.smi.mc.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smi.mc.constants.SeqConstants;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;
import com.smilife.core.exception.SmiBusinessException;

@Component
public class MemberCenterLog {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonAtomicService commonAtomicService;
	
	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;
	
	public void addInterfaceLog(Map<String, Object> logMap){
		try {
			String logId = commonAtomicService.getSequence(SeqConstants.SEQ_LOG_INTERFACE);
			logMap.put("LOG_ID", logId);
			logInterfaceAtomicService.addLogInterface(logMap);
		} catch (AtomicServiceException e) {
			LOGGER.error("接口日志新增异常", e);
			throw new SmiBusinessException("接口日志新增异常");
		}
	}
	
}
