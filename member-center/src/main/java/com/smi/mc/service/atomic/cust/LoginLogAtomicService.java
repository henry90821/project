package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.LoginLogMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class LoginLogAtomicService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private LoginLogMapper loginLogMapper;

	public Map<String, Object> addLoginLog(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			String LOGIN_LOG_ID = (String) requestMap.get("LOGIN_LOG_ID");
			if (StringUtils.isEmpty(LOGIN_LOG_ID)) {
				LOGIN_LOG_ID = this.commonAtomicService.getSequence("SEQ_LOGIN_LOG");
				requestMap.put("LOGIN_LOG_ID", LOGIN_LOG_ID);
			}

			int i = loginLogMapper.addLoginLog(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_SUM", Integer.valueOf(i));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新增日志异常", e);
			throw new AtomicServiceException(e);
		}
	}
}