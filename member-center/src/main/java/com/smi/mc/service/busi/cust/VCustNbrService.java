package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface VCustNbrService
{
  public Map<String, Object> addVCustNbr()
    throws BusiServiceException;
}