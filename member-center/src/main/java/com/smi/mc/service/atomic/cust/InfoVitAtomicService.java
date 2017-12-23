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
import com.smi.mc.dao.cust.InfoVitMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoVitAtomicService
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private InfoVitMapper infoVitMapper;
  public Map<String, Object> qryInfoVit(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter method:InfoVitAtomicService.qryInfoVit");
    }
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

      Map<String,Object> rep=infoVitMapper.qryInfoVit(param);
      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      resMap.put("CUST", rep);
      return resMap;
    } catch (Exception e) {
      this.logger.error("查询活力值异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> qrySumInfoVit(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoVitAtomicService.qrySumInfoVit");
    try
    {
      List<Long> sumVitMap = infoVitMapper.qrySumInfoVit(param);

      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("SUM_VIT", sumVitMap);
      return resMap;
    } catch (Exception e) {
      this.logger.error("查询总活力值异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> addInfoVit(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoVitAtomicService.addInfoVit");
    try {
      int sum = infoVitMapper.addInfoVit(param);
      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("SUCC_SUM", Integer.valueOf(sum));
      return resMap;
    } catch (Exception e) {
      this.logger.error("新增活力值异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> updateInfoVit(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoVitAtomicService.updateInfoVit");
    try {
      int sum = infoVitMapper.updateInfoVit(param);
      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("SUCC_SUM", Integer.valueOf(sum));
      return resMap;
    } catch (Exception e) {
      this.logger.error("修改活力值异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> infoVitQry(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoVitAtomicService.infoVitQry");
    try {
      Map<String,Object> rep= infoVitMapper.infoVitQry(param);
      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("CUST", rep);
      return resMap;
    } catch (Exception e) {
      this.logger.error("根据活力值查询会员等级异常", e);
      throw new AtomicServiceException(e);
    }
  }
}