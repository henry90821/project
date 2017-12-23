package com.smi.mc.service.atomic.billing;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.billing.SmsInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@Service
public class SmsAtomicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	SmsInfoMapper smsInfoMapper;

	public Map<String, Object> smsSendInsert(Map<String, Object> param) throws AtomicServiceException  {
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			param.put("RETRY_TIMES", Integer.valueOf(0));
			int sucess = smsInfoMapper.smsSendInsert(param);
			resMap.put("SUCC_NUM", Integer.valueOf(sucess));
			return resMap;
		} catch (Exception e) {
			this.logger.error("记录短信接口日志异常", e);
			throw new AtomicServiceException(e);
		}
	}
}