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
import com.smi.mc.dao.cust.CodeOfferMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CodeOfferAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CodeOfferMapper codeOfferMapper;

	/**
	 * 业务定义查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCodeOffer(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = null;
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
			list = codeOfferMapper.qryCodeOffer(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SVC_COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("Code_Offer", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("业务定义查询", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 业务定义根据offer_name字段查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCodeOfferByOffername(Map<String, Object> param) throws AtomicServiceException {
		List<Map<String, Object>> list = null;
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
			list = codeOfferMapper.qryCodeOfferByOffername(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SVC_COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("CODE_OFFER", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("业务定义查询", e);
			throw new AtomicServiceException(e);
		}
	}
}