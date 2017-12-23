package com.smi.pay.common.sdk;



import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import org.junit.Test;
import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.sdk.wx.WXConfig;

public class TestWXConfig extends BaseWebIntegrationTests{
	@Resource
	private WXConfig wXConfig;
	
	@Test
	public void TestConfig() throws Exception{
		Field[] fields=WXConfig.class.getDeclaredFields(); 
		for(Field field:fields){
			
			String fieldName=field.getName();
			String getterName="get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length());
			 if("FILE_NAME".equals(fieldName)){
			    	continue;
			    }
		    Method getMethod =wXConfig.getClass().getDeclaredMethod(getterName);
		   
		   
		  
		    Object fieldValue =getMethod.invoke(wXConfig); 
		    System.out.println(fieldName+"="+fieldValue);
			
		  
		}
		
	}
}
