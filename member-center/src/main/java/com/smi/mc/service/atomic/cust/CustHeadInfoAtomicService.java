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

import com.smi.mc.dao.cust.CustHeadInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CustHeadInfoAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private CustHeadInfoMapper custHeadInfoMapper;

	/**
	 * 会员头像信息新增
	 * 
	 * @param map
	 * @return
	 * @throws AtomicServiceException
	 */
	public boolean custHeadInfoAdd(Map<String, Object> map) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustHeadInfoAtomicService.imageFileAdd");
		}
		boolean flag = false;
		try {
			int i = custHeadInfoMapper.insertCustHeadInfo(map);
			if (i > 0)
				flag = true;
		} catch (Exception e) {
			this.logger.error("会员头像新增异常");
			throw new AtomicServiceException(e);
		}
		return flag;
	}

	/**
	 * 会员头像信息查询
	 * 
	 * @param map
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> custHeadInfoQuery(Map<String, Object> map) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustHeadInfoAtomicService.custHeadInfoQuery");
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			list = custHeadInfoMapper.queryCustHeadInfo(map);
		} catch (Exception e) {
			this.logger.error("会员头像查询异常");
			throw new AtomicServiceException(e);
		}
		resultMap.put("cust_size", Integer.valueOf(list.size()));
		resultMap.put("cust_list", list);
		return resultMap;
	}

	/**
	 * 修改会员头像信息
	 * 
	 * @param map
	 * @return
	 * @throws AtomicServiceException
	 */
	public Map<String, Object> custHeadInfoUpdate(Map<String, Object> map) throws AtomicServiceException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("enter method:CustHeadInfoAtomicService.custHeadInfoUpdate");
		}
		int size = 0;
		boolean flag = false;
		try {
			size = custHeadInfoMapper.updateCustHeadInfo(map);
		} catch (Exception e) {
			this.logger.error("会员头像更新原子服务失败");
			throw new AtomicServiceException(e);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("flag", Boolean.valueOf(flag));
		resultMap.put("size", Integer.valueOf(size));
		return resultMap;
	}
}