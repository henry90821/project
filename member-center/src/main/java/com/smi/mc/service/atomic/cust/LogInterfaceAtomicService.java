package com.smi.mc.service.atomic.cust;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.LogInterfaceMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class LogInterfaceAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CommonAtomicService commonAtomicService;
	
	@Autowired 
	private LogInterfaceMapper logInterfaceMapper;  

	public String addLogInterface(Map<String, Object> requestMap) throws AtomicServiceException {

		requestMap.put("LOG_ID", createLogSequece());
		requestMap.put("MONTH", getMonth());
		try {
			logInterfaceMapper.addLogInterface(requestMap);
			return requestMap.get("LOG_ID").toString();
		} catch (Exception e) {
			this.logger.error("新增接口日志异常", e);
			throw new AtomicServiceException(e);
		}
	}

	private String createLogSequece() throws AtomicServiceException {
		SimpleDateFormat sFormat = new SimpleDateFormat("MM");
		String head = sFormat.format(new Date());
		String end = this.commonAtomicService.getSequence("SEQ_LOG_INTERFACE");
		return head + end;
	}

	private String getMonth() {
		Calendar c = Calendar.getInstance();
		String year = c.get(1) + "";
		String month = c.get(2) + 1 + "";
		if (month.length() == 1) {
			month = "0" + month;
		}
		return year + month;
	}
}