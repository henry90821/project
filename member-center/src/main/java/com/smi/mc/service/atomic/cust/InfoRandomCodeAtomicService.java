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
import com.smi.mc.dao.cust.InfoRandomCodeMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoRandomCodeAtomicService
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private InfoRandomCodeMapper infoRandomCodeMapper;
  public Map<String, Object> qryRandomCode(Map<String, Object> param) throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter InfoRandomCodeService.qryRandomCode");
    Map<String,Object> resMap = new HashMap<String,Object>();
    try
    {
      PageMap pageMap = new PageMap();
      if ((!StringUtils.isEmpty((String)param.get("PAGE_INDEX"))) && (!StringUtils.isEmpty((String)param.get("PAGE_SIZE")))) {
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

     List<Map<String, Object>> rep = infoRandomCodeMapper.qryInfoRandomCode(param);
      resMap.put("INFO_RANDOM_CODE", rep);
    } catch (Exception e) {
      this.logger.error("调用查询随机码原子服异常", e);
      throw new AtomicServiceException(e);
    }

    return resMap;
  }

  public Map<String, Object> addRandomCode(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter InfoRandomCodeService.addRandomCode");
    Map<String,Object> resMap = new HashMap<String,Object>();
    try {
      int rows =infoRandomCodeMapper.addInfoRandomCode(param);
      resMap.put("SUCC_SUM", Integer.valueOf(rows));
    } catch (Exception e) {
      this.logger.error("新增随机码异常", e);
      throw new AtomicServiceException(e);
    }
    return resMap;
  }

  public Map<String, Object> updRandomCode(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter InfoRandomCodeService.updRandomCode");
    }
    Map<String,Object> resMap = new HashMap<String,Object>();
    try {
      int rows = infoRandomCodeMapper.updInfoRandomCode(param);
      this.logger.info("修改的记录有" + rows + "行");
      resMap.put("SUCC_SUM", Integer.valueOf(rows));
      return resMap;
    } catch (Exception e) {
      this.logger.error("修改随机码异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> updRandomCode1(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter InfoRandomCodeService.updRandomCode1");
    }
    Map<String,Object> resMap = new HashMap<String,Object>();
    try {
      int rows = infoRandomCodeMapper.updInfoRandomCode1(param);
      this.logger.info("修改的记录有" + rows + "行");
      resMap.put("SUCC_SUM", Integer.valueOf(rows));
      return resMap;
    } catch (Exception e) {
      this.logger.error("修改随机码异常", e);
      throw new AtomicServiceException(e);
    }
  }
}