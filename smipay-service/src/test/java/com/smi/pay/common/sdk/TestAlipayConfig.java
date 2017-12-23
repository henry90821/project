package com.smi.pay.common.sdk;



import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import org.junit.Test;
import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.sdk.ali.AlipayConfig;

public class TestAlipayConfig extends BaseWebIntegrationTests{
	@Resource
	private AlipayConfig alipayConfig;
	
	@Test
	public void TestConfig() throws Exception{
		Field[] fields=AlipayConfig.class.getDeclaredFields(); 
		for(Field field:fields){
			String fieldName=field.getName();
			String getterName="get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length());
			 //System.out.println("getterName="+getterName);
		    Method getMethod =alipayConfig.getClass().getDeclaredMethod(getterName);
		    Object fieldValue =getMethod.invoke(alipayConfig); 
		    System.out.println(fieldName+"="+fieldValue);
		}
		
	}
}
