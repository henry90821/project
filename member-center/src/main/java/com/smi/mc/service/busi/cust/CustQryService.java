package com.smi.mc.service.busi.cust;

import java.util.List;
import java.util.Map;

import com.smi.mc.exception.BusiServiceException;
import com.smilife.core.exception.SmiBusinessException;

public abstract interface CustQryService
{
  public abstract Map<String, Object> qryCust(Map<String, Object> paramMap)
    throws BusiServiceException;

  public abstract Map<String, Object> qryFee(Map<String, Object> paramMap)
    throws BusiServiceException;

  public abstract Map<String, Object> qryCustFlag(Map<String, Object> paramMap)
    throws BusiServiceException;

  public abstract Map<String, Object> qryCustBusiInfo(Map<String, Object> paramMap)
    throws BusiServiceException;
  
  public abstract  Map<String, Object> qryCustInfo(Map<String, Object> paramMap)
		    throws SmiBusinessException;
  
  /**
   * 根据手机账号获取custId
   * @param loginMobiles
   * @return
   */
  public List<Map<String, Object>> getCustIdByloginMobile(String loginMobiles);
}