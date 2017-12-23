package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.OrgOpMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class OrgOpAtomicService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrgOpMapper orgOpMapper;

	public String querySysUId(String ORG_ID) throws AtomicServiceException {

		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("ORG_ID", ORG_ID);
		try {
			return orgOpMapper.queryOrgOpId(requestMap);
		} catch (Exception e) {
			this.logger.error("ORG_OP表查询操作异常", e);
			throw new AtomicServiceException(e);
		}
	}
}