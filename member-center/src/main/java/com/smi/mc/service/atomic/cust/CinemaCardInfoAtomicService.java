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
import com.smi.mc.dao.cust.CinemaCardInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CinemaCardInfoAtomicService 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  CinemaCardInfoMapper cinemaCardInfoMapper;
  
  /**
   * 老会员卡号查询
   * @param param
   * @return
   * @throws AtomicServiceException
   */
  public Map<String, Object> qryCinemaCardInfo(Map<String, Object> param)
    throws AtomicServiceException
  {
    List<Map<String, Object>> list =null;
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
       
      list=cinemaCardInfoMapper.qryCinemaCardInfo(param);      
      Map<String, Object> resMap = new HashMap<String, Object>();
      resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      resMap.put("CinemaCardInfo", list);
      return resMap;
    } catch (Exception e) {
      this.logger.error("查询会员账号", e);
      throw new AtomicServiceException(e);
    }
  }
}