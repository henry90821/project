package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.common.PageMap;
import com.smi.mc.dao.cust.InfoOrderMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoOrderAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	InfoOrderMapper infoOrderMapper;

	/**
	 * 订单表的新增
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addInfoOrder(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:InfoOrderAtomicService.addInfoOrder");
		}
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = infoOrderMapper.addInfoOrder(requestMap);
			resultMap.put("SUCC_SUM", Integer.valueOf(num));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单表新增操作异常", e);
			throw new AtomicServiceException(e);
		}
	}
     /**
      * 订单表查询
      * @param requestMap
      * @return
      * @throws AtomicServiceException
      */
	public Map<String, Object> qryInfoOrder(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoOrderAtomicService.qryInfoOrder");
		try {
			List<Map<String, Object>> list = null;

			PageMap pageMap = new PageMap();
			if ((!StringUtils.isBlank((String) requestMap.get("PAGE_INDEX")))
					&& (!StringUtils.isBlank((String) requestMap.get("PAGE_SIZE")))) {
				int pageSize = Integer.parseInt((String) requestMap.get("PAGE_SIZE"));
				int pageIndex = Integer.parseInt((String) requestMap.get("PAGE_INDEX"));
				pageMap.setPageSize(pageSize);
				pageMap.setStartNum((pageIndex - 1) * pageSize);
				requestMap.put("pageMap", pageMap);
			}
			list = infoOrderMapper.qryInfoOrder(requestMap);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resultMap.put("INFO_ORDER", list);

			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单表查询操作异常", e);
			throw new AtomicServiceException(e);
		}
	}
      /**
       * 订单信息的更新
       * @param requestMap
       * @return
       * @throws AtomicServiceException
       */
	public Map<String, Object> updateInfoOrder(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:InfoOrderAtomicService.updateInfoOrder");
		}
		try {
			int num = infoOrderMapper.updateInfoOrder(requestMap);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("SUCC_SUM", Integer.valueOf(num));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("订单表修改操作异常", e);
			throw new AtomicServiceException(e);
		}
	}
}