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
import com.smi.mc.dao.cust.InfoPayMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class PayAtomicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private InfoPayMapper infoPayMapper; 

	public Map<String, Object> addInfoPay(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			Map<String, Object> resMap = new HashMap<String, Object>();
			int sum = infoPayMapper.addInfoPay(requestMap);
			resMap.put("SUCC_SUM", Integer.valueOf(sum));
			return resMap;

		} catch (Exception e) {
			this.logger.error("新增会员账户异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> updInfoPay(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			Map<String, Object> resMap = new HashMap<String, Object>();
			int sum = infoPayMapper.updInfoPay(requestMap);
			resMap.put("SUCC_SUM", Integer.valueOf(sum));
			return resMap;
		} catch (Exception e) {
			this.logger.error("更新会员账户异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> qryInfoPay(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = null;
		try {
			PageMap pageMap = new PageMap();
			String pageIndex = (String) param.get("PAGE_INDEX");
			String pageSize = (String) param.get("PAGE_SIZE");
			int pageIndexNbr = (null == pageIndex) || ("".equals(pageIndex)) ? 1
					: Integer.valueOf(pageIndex).intValue();
			int pageSizeNbr = (null == pageSize) || ("".equals(pageSize)) ? 10 : Integer.valueOf(pageSize).intValue();
			pageMap.setStartNum((pageIndexNbr - 1) * pageSizeNbr);
			pageMap.setPageSize(pageSizeNbr);
			param.put("pageMap", pageMap);

			list = infoPayMapper.qryInfoPay(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("PAY", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员账号异常", e);
			throw new AtomicServiceException(e);
		}
	}
}