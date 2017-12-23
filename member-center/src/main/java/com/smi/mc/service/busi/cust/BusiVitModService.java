package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface BusiVitModService {

	public abstract Map<String,Object> busiVitMod(Map<String,Object> map) throws BusiServiceException;
}