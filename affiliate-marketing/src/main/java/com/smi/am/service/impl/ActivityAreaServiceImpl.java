package com.smi.am.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.am.dao.AmActivityAreaMapper;
import com.smi.am.service.IActivityAreaService;

/**
 * 查询活动区域门店服务基类
 * 
 * @author smi
 *
 */
@Service
public class ActivityAreaServiceImpl implements IActivityAreaService {

	@Autowired
	private AmActivityAreaMapper amActivityAreaMapper;

	/**
	 * 查询活动区域
	 * 
	 * @param activityId
	 * @return
	 */
	@Override
	public List<Map<String, Object>> findShopByAreaId(Integer activityId) {
		List<Map<String, Object>> areaList = this.amActivityAreaMapper.selectShopByAreaId(activityId);
		return areaList;
	}

}
