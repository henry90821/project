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
import com.smi.mc.dao.cust.BusiFeeInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class BusiQryFeeAtomicService 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
   BusiFeeInfoMapper busiFeeInfoMapper; 
  
  /**
   * 业务定义查询
   * @param param
   * @return
   * @throws AtomicServiceException
   */
  public Map<String, Object> qryBusiFeeInfo(Map<String, Object> param)
    throws AtomicServiceException
  {
    try
    {
      PageMap pageMap = new PageMap();
      if ((!StringUtils.isEmpty((String)param.get("PAGE_INDEX"))) && (!StringUtils.isEmpty((String)param.get("PAGE_SIZE")))) {
        int page_index = Integer.parseInt((String)param.get("PAGE_INDEX"));
        int page_size = Integer.parseInt((String)param.get("PAGE_SIZE"));
        pageMap.setStartNum((page_index - 1) * page_size);
        pageMap.setPageSize(page_size);
        param.put("pageMap", pageMap);
      }
      List<Map<String, Object>> list= busiFeeInfoMapper.qryBusiFeeInfo(param);
      Map<String, Object> resMap = new HashMap<String, Object>();
      resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      resMap.put("CODE_OFFER", list);
      return resMap;
    } catch (Exception e) {
      this.logger.error("业务定义查询失败", e);
      throw new AtomicServiceException(e);
    }
  }
}