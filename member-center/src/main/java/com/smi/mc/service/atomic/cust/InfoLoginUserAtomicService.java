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
import com.smi.mc.dao.cust.InfoLoginUserMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoLoginUserAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	InfoLoginUserMapper infoLoginUserMapper;

	/**
	 * 会员账号查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryInfoLoginUser(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoLoginUserAtomicService.qryInfoLoginUser");
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
			list = infoLoginUserMapper.qryInfoLoginUser(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("CUST", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员账号", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员账号新增
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addInfoLoginUser(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoLoginUserAtomicService.addInfoLoginUser");
		try {
			int sum = infoLoginUserMapper.addInfoLoginUser(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_SUM", Integer.valueOf(sum));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新增会员账号异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员账号更新
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> updateInfoLoginUser(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoLoginUserAtomicService.updateInfoLoginUser");
		try {
			int sum = infoLoginUserMapper.updateInfoLoginUser(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_SUM", Integer.valueOf(sum));
			return resMap;
		} catch (Exception e) {
			this.logger.error("修改会员账号异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 查询有效的会员账号
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryInfoLoginUserForCheck(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:InfoLoginUserAtomicService.qryInfoLoginUserForCheck");
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
			list = infoLoginUserMapper.qryInfoLoginUserForCheck(param);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalPage()));
			resMap.put("CUST", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询有效的会员账号", e);
			throw new AtomicServiceException(e);
		}
	}
}