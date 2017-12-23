package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface ChkPinService {

	public abstract Map<String,Object> checkPin(Map<String,Object> map) throws BusiServiceException;
}
