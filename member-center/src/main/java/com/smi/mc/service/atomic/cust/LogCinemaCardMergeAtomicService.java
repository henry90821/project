package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.LogCinemaCardMergeMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class LogCinemaCardMergeAtomicService 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private LogCinemaCardMergeMapper logCinemaCardMergeMapper;
  public Map<String, Object> addLogCinemaCardMerge(Map<String, Object> requestMap)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:LogCinemaCardMergeAtomicService.addLogCinemaCardMerge");
    try {
      int sum = logCinemaCardMergeMapper.addLogCinemaCardMerge(requestMap);

      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("SUCC_SUM", Integer.valueOf(sum));
      return resMap;
    } catch (Exception e) {
      this.logger.error("老会员归集日志新增异常", e);
      throw new AtomicServiceException(e);
    }
  }
}