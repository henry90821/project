package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.CodeOfferRegionMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CodeOfferRegionAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	CodeOfferRegionMapper codeOfferRegionMapper;
      /**
       * 业务区域关系查询
       * @param param
       * @return
       * @throws AtomicServiceException
       */
	public Map<String, Object> qryOfferIdByRegion(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list =null;
		try {
			list = codeOfferRegionMapper.qryOfferIdByRegion(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("OFFER", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查业务区域关系查询异常", e);
			throw new AtomicServiceException(e);
		}
	}
}