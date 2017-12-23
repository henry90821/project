package com.smi.am.service;

import java.util.List;
import java.util.Map;

public interface IActivityAreaService {
	
	   /**
     * 查询活动区域
     * @param activityId
     * @return
     */
    List<Map<String,Object>> findShopByAreaId(Integer activityId);
	
	

}
