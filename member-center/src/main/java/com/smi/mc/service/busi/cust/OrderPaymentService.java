package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface OrderPaymentService {

	public abstract Map<String,Object> orderPayment(Map<String,Object> map) throws BusiServiceException;

	public abstract Map<String,Object> isNullCheck(Map<String,Object> map, Map<String,Object> map1, Object obj) throws BusiServiceException;

	public abstract Map<String,Object> isNullCheck(Object obj);
}