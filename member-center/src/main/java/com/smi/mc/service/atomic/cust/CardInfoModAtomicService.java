package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.CinemaCardInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CardInfoModAtomicService 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  CinemaCardInfoMapper cinemaCardInfoMapper;
  
  /**
   * 鼎新老会员卡号修改
   * @param param
   * @return
   * @throws AtomicServiceException
   */
  public Map<String, Object> cinemaCardInfoMod(Map<String, Object> param)
    throws AtomicServiceException
  {
    Map<String, Object> resMap = new HashMap<String, Object>();
    int retNum = 0;
    try {
      retNum= cinemaCardInfoMapper.cinemaCardInfoMod(param);
      resMap.put("SUCC_SUM", Integer.valueOf(retNum));
    } catch (Exception e) {
      this.logger.error("鼎新老会员卡号修改原子操作异常", e);
      throw new AtomicServiceException(e);
    }
    return resMap;
  }
}