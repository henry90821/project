package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smi.mc.common.PageMap;
import com.smi.mc.dao.cust.InfoOrderPayItemMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoOrderPayItemAtomicService
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private InfoOrderPayItemMapper infoOrderPayItemMapper;
  public boolean addInfo(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter method:InfoOrderPayItemAtomicService.addInfo");
    }
    boolean flag = false;
    try {
      int i = infoOrderPayItemMapper.addInfo(param);
      if (i > 0)
        flag = true;
    }
    catch (Exception e) {
      this.logger.error("订单支付新增失败");
      throw new AtomicServiceException(e);
    }
    return flag;
  }

  public Map<String, Object> qryInfo(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter method:InfoOrderPayItemAtomicService.qryInfo");
    }
    Map<String,Object> rep = new HashMap<String,Object>();
    try
    {
      PageMap pageMap = new PageMap();
      if ((!StringUtils.isEmpty((String)param.get("PAGE_INDEX"))) && 
        (!StringUtils.isEmpty((String)param
        .get("PAGE_SIZE"))))
      {
        int page_index = Integer.parseInt(
          (String)param
          .get("PAGE_INDEX"));

        int page_size = Integer.parseInt(
          (String)param
          .get("PAGE_SIZE"));

        pageMap.setStartNum((page_index - 1) * page_size);
        pageMap.setPageSize(page_size);
        param.put("pageMap", pageMap);
      }
     Map<String,Object> list = infoOrderPayItemMapper.qryInfo(param);

      rep.put("qryInfo", list);
      rep.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      return rep;
    } catch (Exception e) {
      this.logger.error("订单支付查询失败");
      throw new AtomicServiceException(e);
    }
  }
}