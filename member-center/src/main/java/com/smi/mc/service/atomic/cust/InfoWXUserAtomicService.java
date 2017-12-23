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
import com.smi.mc.dao.cust.InfoWXUserMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class InfoWXUserAtomicService 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private InfoWXUserMapper infoWXUserMapper;
  public int addInfoWXUser(Map<String, Object> requestMap)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoWXUserAtomicService.addInfoWXUser");
    try {
      return infoWXUserMapper.addInfoWXUser(requestMap);
    }
    catch (Exception e)
    {
      this.logger.error("新增粉丝信息异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> queryInfoWXUser(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("enter method:InfoWXUserAtomicService.queryInfoWXUser");
    }
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

      Map<String,Object> list = infoWXUserMapper.queryInfoWXUser(param);

      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      resMap.put("InfoWXUser", list);
      return resMap;
    } catch (Exception e) {
      this.logger.error("查询微信用户资料异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public int updateStateByWXUserId(Map<String, Object> requestMap)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoWXUserAtomicService.updateStateByWXUserId");
    try {
      int sucess = infoWXUserMapper.updateStateByWXUserId(requestMap);
      this.logger.info("修改的记录有" + sucess + "行");
      return sucess;
    } catch (Exception e) {
      this.logger.error("更新微信用户信息异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public int updateCustIdByWXUserId(Map<String, Object> param)
    throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoWXUserAtomicService.updateCustIdByWXUserId");
    try {
      int sucess = infoWXUserMapper.updateCustIdByWXUserId(param);
      this.logger.info("修改的记录有" + sucess + "行");
      return sucess;
    } catch (Exception e) {
      this.logger.error("更新微信用户信息异常", e);
      throw new AtomicServiceException(e);
    }
  }

  public Map<String, Object> getInfoWXUserId(Map<String, Object> param) throws AtomicServiceException
  {
    if (this.logger.isDebugEnabled())
      this.logger.debug("enter method:InfoWXUserAtomicService.getInfoWXUserId");
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

      Map<String,Object> list = infoWXUserMapper.getInfoWXUserId(param);

      Map<String,Object> resMap = new HashMap<String,Object>();
      resMap.put("COUNT_TOTAL", Integer.valueOf(pageMap.getTotalRecord()));
      resMap.put("InfoWXUser", list);
      return resMap;
    } catch (Exception e) {
      this.logger.error("更新微信用户信息异常", e);
      throw new AtomicServiceException(e);
    }
  }
}