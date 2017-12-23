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
import com.smi.mc.dao.cust.OrderFeeItemMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class OrderFeeItemAtomicService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderFeeItemMapper orderFeeItemMapper; 

	public String addOrderFeeItem(Map<String, Object> requestMap) throws AtomicServiceException {
		try {
			orderFeeItemMapper.addOrderFeeItem(requestMap);
			return requestMap.get("ORDER_FEE_ITEM_ID").toString();
		} catch (Exception e) {
			this.logger.error("新增订单费用异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> qryOrderFeeItem(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			PageMap pageMap = new PageMap();
			if ((!StringUtils.isEmpty((String) param.get("PAGE_INDEX")))
					&& (!StringUtils.isEmpty((String) param.get("PAGE_SIZE")))) {
				int pageSize = Integer.parseInt((String) param.get("PAGE_SIZE"));
				int pageIndex = Integer.parseInt((String) param.get("PAGE_INDEX"));
				pageMap.setPageSize(pageSize);
				pageMap.setStartNum((pageIndex - 1) * pageSize);
				param.put("pageMap", pageMap);
			}
			list = orderFeeItemMapper.qryOrderFeeItem(param);
			
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("QRY_ORDER_FEE_RSP", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询订单费用异常", e);
			throw new AtomicServiceException(e);
		}
	}
}