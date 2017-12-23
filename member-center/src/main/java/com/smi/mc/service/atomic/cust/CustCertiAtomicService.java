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
import com.smi.mc.dao.cust.CertiInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CustCertiAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CertiInfoMapper certiInfoMapper;

	/**
	 * 会员证件信息查询(联表code_list查)
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> qryCertiInfo(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustCertiAtomicService.qryCertiInfo");
		}
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
			list = certiInfoMapper.qryCertiInfo(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
			resMap.put("Cust_Certi", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员证件资料异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员证件基本资料新增
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> addCerti(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustCertiAtomicService.addCerti");
		try {
			int success = certiInfoMapper.addCerti(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_NUM", Integer.valueOf(success));
			return resMap;
		} catch (Exception e) {
			this.logger.error("新增会员证件信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 修改会员证件基本资料
	 * 
	 * @param requestMap
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> updCerti(Map<String, Object> requestMap) throws AtomicServiceException {
		if (this.logger.isDebugEnabled())
			this.logger.debug("enter method:CustCertiAtomicService.updCerti");
		try {
			int sucess = certiInfoMapper.updCerti(requestMap);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("SUCC_NUM", Integer.valueOf(sucess));
			return resMap;
		} catch (Exception e) {
			this.logger.error("更新会员证件信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

	/**
	 * 会员证件信息查询
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> queryCerti(Map<String, Object> param) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustCertiAtomicService.queryCerti");
		}
		List<Map<String, Object>> list = null;
		try {
			list = certiInfoMapper.queryCerti(param);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("Cust_Certi", list);
			return resMap;
		} catch (Exception e) {
			this.logger.error("查询会员证件资料异常", e);
			throw new AtomicServiceException(e);
		}
	}
}