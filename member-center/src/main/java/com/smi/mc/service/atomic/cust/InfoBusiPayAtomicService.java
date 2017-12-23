package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smi.mc.common.PageMap;
import com.smi.mc.dao.cust.InfoBusiPayMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoBusiPayAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	InfoBusiPayMapper infoBusiPayMapper;

	/**
	 * 服务账户关系新增
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addInfoBusiPay(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:InfoBusiPayAtomicService.addInfoBusiPay");
		}
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = infoBusiPayMapper.addInfoBusiPay(requestMap);
			resultMap.put("SUCC_SUM", Integer.valueOf(num));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("新增服务账户关系异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 修改服务账户关系
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> updateInfoBusiPay(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoBusiPayAtomicService.updateInfoBusiPay");
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = infoBusiPayMapper.updateInfoBusiPay(requestMap);
			resultMap.put("SUCC_SUM", Integer.valueOf(num));
			return resultMap;
		} catch (Exception e) {
			this.logger.error("服务账户关系修改异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 查询服务账号关系
	 * 
	 * @param map
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryInfoBusiPay(Map<String, Object> map) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoBusiPayAtomicService.qryInfoBusiPay");
		try {
			List<Map<String, Object>> list = null;

			PageMap pageMap = new PageMap();

			if ((!StringUtils.isEmpty(map.get("PAGE_INDEX"))) && (!StringUtils.isEmpty(map.get("PAGE_SIZE")))) {
				int pageIndex = Integer.parseInt((String) map.get("PAGE_INDEX"));
				int pageSize = Integer.parseInt((String) map.get("PAGE_SIZE"));
				pageMap.setPageSize(pageSize);
				pageMap.setStartNum((pageIndex - 1) * pageSize);
				map.put("pageMap", pageMap);
			}
			list = infoBusiPayMapper.qryInfoBusiPay(map);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resultMap.put("Info_Busi_Pay", list);
			return resultMap;
		} catch (Exception e) {
			this.logger.error("服务账户关系查询", e);
			throw new AtomicServiceException(e);
		}
	}
}