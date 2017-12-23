package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.common.PageMap;
import com.smi.mc.dao.cust.CustRelMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CustRelAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CustRelMapper custRelMapper;

	/**
	 * 会员关系新增
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addCustRel(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustRelAtomicService.addCustRel");
		try {
			int resultCount = custRelMapper.addCustRel(requestMap);
			// int resultCount = insert(null, "custRel.addCustRel", requestMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("SUCC_SUM", Integer.valueOf(resultCount));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("新增会员异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员关系更新
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> updateCustRel(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustRelAtomicService.updateCustRel");
		try {
			int resultCount = custRelMapper.updateCustRel(requestMap);
			// int resultCount = update(null, "custRel.updateCustRel",
			// requestMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("SUCC_SUM", Integer.valueOf(resultCount));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("更新会员关系异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员关系的查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCustRel(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustRelAtomicService.qryCustRel");
		List<Map<String, Object>> list = null;
		try {
			PageMap pageMap = new PageMap();
			pageMap.setStartNum(0);
			pageMap.setPageSize(10);
			param.put("pageMap", pageMap);
			list = custRelMapper.qryCustRel(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SVC_COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("QRY_RESULTS", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员关系异常", e);
			throw new AtomicServiceException(e);
		}
	}
}