package com.smi.pay.listener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import com.smi.pay.sdk.ali.AlipayConfig;
import com.smi.pay.sdk.union.SDKConfig;
import com.smi.pay.sdk.wx.WXConfig; 

@Component
public class SystemInit  implements ApplicationListener<ContextRefreshedEvent>{

	@Resource
	private AlipayConfig alipayConfig;
	
	@Resource
	private WXConfig wXConfig;
	
	@Resource
	private SDKConfig sDKConfig;
	
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		
		AlipayConfig.init(alipayConfig);
		System.out.println("======================AlipayConfig===============================");
		printObject(AlipayConfig.getConfig());
		WXConfig.init(wXConfig);
		System.out.println("======================WXConfig===============================");
		printObject(WXConfig.getConfig());
		
		SDKConfig.init(sDKConfig);
		System.out.println("======================SDKConfig===============================");
		printObject(SDKConfig.getConfig());
		
		
		
	}
	
	private static void printObject(Object obj){
		try{
		Field[] fields=obj.getClass().getDeclaredFields(); 
		for(Field field:fields){
			String fieldName=field.getName();
			String getterName="get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length());
			 //System.out.println("getterName="+getterName);
			 Method getMethod=null;
				try{
			       getMethod =obj.getClass().getDeclaredMethod(getterName);
				}catch (Exception e) {
					continue;
				}
		   
		       Object fieldValue =getMethod.invoke(obj); 
		       System.out.println(fieldName+"="+fieldValue);
		}
		}catch (Exception e) {
			
		}
		
	}

}
