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
import com.smi.mc.dao.cust.CodeAttrMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class OfferAtomicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CodeAttrMapper codeAttrMapper;

	public Map<String, Object> qryOffer(Map<String, Object> param) throws AtomicServiceException {

		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			PageMap pageMap = new PageMap();
			if ((StringUtils.isNumeric((String) param.get("PAGE_INDEX")))
					&& (StringUtils.isNumeric((String) param.get("PAGE_SIZE")))) {
				int page_index = Integer.parseInt(param.get("PAGE_INDEX").toString());
				int page_size = Integer.parseInt(param.get("PAGE_SIZE").toString());
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}

			List<Map<String, Object>> list = codeAttrMapper.qryOffer(param); 

			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("QRY_RESULTS", list);
		} catch (Exception e) {
			this.logger.error("业务查询异常", e);
			throw new AtomicServiceException(e);
		}

		return resMap;
	}

	public Map<String, Object> qryCodeAttr(Map<String, Object> param) throws AtomicServiceException {

		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			PageMap pageMap = new PageMap();
			if ((StringUtils.isNumeric((String) param.get("PAGE_INDEX")))
					&& (StringUtils.isNumeric((String) param.get("PAGE_SIZE")))) {
				int page_index = Integer.parseInt(param.get("PAGE_INDEX").toString());
				int page_size = Integer.parseInt(param.get("PAGE_SIZE").toString());
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}

			List<Map<String, Object>> list = codeAttrMapper.qryCodeAttr(param);

			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalPage()));
			resMap.put("QRY_RESULTS", list);
		} catch (Exception e) {
			this.logger.error("属性查询异常", e);
			throw new AtomicServiceException(e);
		}

		return resMap;
	}

	public Map<String, Object> qryCodeAttrValue(Map<String, Object> param) throws AtomicServiceException {

		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			PageMap pageMap = new PageMap();
			if ((StringUtils.isNumeric((String) param.get("PAGE_INDEX")))
					&& (StringUtils.isNumeric((String) param.get("PAGE_SIZE")))) {
				int page_index = Integer.parseInt(param.get("PAGE_INDEX").toString());
				int page_size = Integer.parseInt(param.get("PAGE_SIZE").toString());
				pageMap.setStartNum((page_index - 1) * page_size);
				pageMap.setPageSize(page_size);
				param.put("pageMap", pageMap);
			}

			List<Map<String, Object>> list = codeAttrMapper.qryCodeAttrVal(param);

			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalPage()));
			resMap.put("QRY_RESULTS", list);
		} catch (Exception e) {
			this.logger.error("属性值查询异常", e);
			throw new AtomicServiceException(e);
		}

		return resMap;
	}
}