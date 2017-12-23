package com.smi.mc.service.busi.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface OrderService {

	public abstract Map<String,Object> addOrders(Map<String,Object> map) throws BusiServiceException;

	public abstract Map<String,Object> queryOrders(Map<String,Object> map) throws BusiServiceException;

	public abstract Map<String,Object> queryAllOrders(Map<String,Object> map) throws BusiServiceException;

	public abstract Map<String,Object> queryOtherOrders(Map<String,Object> map) throws BusiServiceException;

	public abstract Map<String,Object> checkIsNull(Map<String,Object> map);

	public abstract Map<String,Object> parameterChange(Map<String,Object> map, String s) throws Exception;
}
