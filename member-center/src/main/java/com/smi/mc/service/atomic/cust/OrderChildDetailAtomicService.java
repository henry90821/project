package com.smi.mc.service.atomic.cust;

import java.util.ArrayList;
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
import com.smi.mc.dao.cust.OrderChildDetailMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class OrderChildDetailAtomicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderChildDetailMapper orderChildDetailMapper; 

	public Map<String, Object> qryOrderChildDetail(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			PageMap pageMap = new PageMap();
			if ((!StringUtils.isEmpty((String) param.get("PAGE_INDEX")))
					&& (!StringUtils.isEmpty((String) param.get("PAGE_SIZE")))) {
				int page_index = Integer.parseInt((String) param.get("PAGE_INDEX"));
				int page_size = Integer.parseInt((String) param.get("PAGE_SIZE"));
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}

			list = orderChildDetailMapper.qryOrderChildDetail(param) ;

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("ORDER_DETAIL_LIST", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询订单明细资料异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> addOrderChildDetail(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			int success = orderChildDetailMapper.addOrderChildDetail(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_NUM", Integer.valueOf(success));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新增订单明细信息异常", e);
			throw new AtomicServiceException(e);
		}
	}
}