package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface BusiQryCustService {

	public abstract Map<String, Object> busiCustQry(Map<String, Object> map) throws BusiServiceException;
}
