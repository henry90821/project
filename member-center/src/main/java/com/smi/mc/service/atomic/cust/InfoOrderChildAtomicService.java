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
import com.smi.mc.dao.cust.InfoOrderChildMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoOrderChildAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	InfoOrderChildMapper infoOrderChildMapper;

	/**
	 * 子订单新增
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public boolean addInfo(Map<String, Object> param) throws AtomicServiceException {
		boolean flag = false;
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoOrderChildAtomicService.addInfo");
		try {
			int rep = infoOrderChildMapper.addInfo(param);
			if (rep > 0)
				flag = true;
		} catch (Exception e) {
			this.logger.error("子订单新增失败", e);
			throw new AtomicServiceException(e);
		}
		return flag;
	}

	/**
	 * 子订单查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryInfo(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:InfoOrderChildAtomicService.qryInfo");
		}

		Map<String, Object> rep = new HashMap<String, Object>();
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
			List<Map<String, Object>> list = infoOrderChildMapper.qryInfo(param);
			rep.put("qryInfo", list);
			rep.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			return rep;
		} catch (Exception e) {
			this.logger.error("子订单查询失败", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, Object> qryBaiDu() throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:InfoOrderChildAtomicService.qryBaiDu");
		}

		Map<String, Object> rep = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = infoOrderChildMapper.qryBaiDu(new HashMap<String, Object>());
			rep.put("QRY_RESULTS", list);
			return rep;
		} catch (Exception e) {
			this.logger.error("百度联名查询失败", e);
			throw new AtomicServiceException(e);
		}
	}
}