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
import com.smi.mc.dao.cust.CustBusiInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CustBusiAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CustBusiInfoMapper custBusiInfoMapper;

	/**
	 * 会员业务查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCustBusiInfo(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustBusiAtomicService.qryCustBusiInfo");
		}
		List<Map<String, Object>> list = null;
		try {
			PageMap pageMap = new PageMap();
			if ((param.containsKey("PAGE_INDEX")) && (param.containsKey("PAGE_SIZE"))) {
				int page_index = Integer.parseInt(param.get("PAGE_INDEX").toString());
				int page_size = Integer.parseInt(param.get("PAGE_SIZE").toString());
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}
			list = custBusiInfoMapper.qryCustBusiInfo(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("QRY_RESULTS", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员业务异常", e);
			throw new AtomicServiceException(e);
		}
	}
	
	/**
	 * 会员业务查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> isExistCardNbr(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustBusiAtomicService.qryCustBusiInfo");
		}
		List<Map<String, Object>> list = null;
		try {
			PageMap pageMap = new PageMap();
			if ((param.containsKey("PAGE_INDEX")) && (param.containsKey("PAGE_SIZE"))) {
				int page_index = Integer.parseInt(param.get("PAGE_INDEX").toString());
				int page_size = Integer.parseInt(param.get("PAGE_SIZE").toString());
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}
			list = custBusiInfoMapper.isExistCardNbr(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("QRY_RESULTS", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员业务异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 增加会员的业务
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addCustBusi(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustBusiAtomicService.addCustBusi");
		try {
			int i = custBusiInfoMapper.addCustBusi(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_SUM", Integer.valueOf(i));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新增会员业务异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 修改会员的业务
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> updateCustBusi(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustBusiAtomicService.updateCustBusi");
		try {
			int i = custBusiInfoMapper.updateCustBusi(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_SUM", Integer.valueOf(i));
			return resMap;
		} catch (Exception e) {
			this.logger.error("修改会员业务异常", e);
			throw new AtomicServiceException(e);
		}
	}
}