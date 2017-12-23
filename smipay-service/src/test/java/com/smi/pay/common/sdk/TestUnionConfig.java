package com.smi.pay.common.sdk;



import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import org.junit.Test;
import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.sdk.union.SDKConfig;

public class TestUnionConfig extends BaseWebIntegrationTests{
	@Resource
	private SDKConfig sDKConfig;
	
	@Test
	public void TestConfig() throws Exception{
		Field[] fields=SDKConfig.class.getDeclaredFields(); 
		for(Field field:fields){
			
			String fieldName=field.getName();
			String getterName="get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length());
			 if("FILE_NAME".equals(fieldName)){
			    	continue;
			    }
			 Method getMethod=null;
			try{
		       getMethod =sDKConfig.getClass().getDeclaredMethod(getterName);
			}catch (Exception e) {
				continue;
			}
		   
		   
		  
		    Object fieldValue =getMethod.invoke(sDKConfig); 
		    System.out.println(fieldName+"="+fieldValue);
			
		  
		}
		
	}
}
