package com.iskyshop.oms.service;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.nutz.json.Json;

import com.tydic.framework.util.PropertyUtil;

/**
 * 抽象策略类
 * @author dengyuqi
 *
 * @param <T>
 */
public abstract class AbstractOmsManagerService<T> {
	
	private static Logger logger = Logger.getLogger(AbstractOmsManagerService.class);
	
	/**
	 * 请求报文
	 */
	protected String data;

	/**
	 * 获得新增订单接口请求报文
	 * @param t
	 * @return
	 */
	public abstract String getInsertData(T t);

	/**
	 * 校验新增订单接口请求报文
	 * @param t
	 * @return
	 */
	public boolean validateInsertOrder(T t){
		return true;
	}

	/**
	 * 新增oms订单
	 * @param t
	 * @return
	 */
	public Boolean insertOrder(T t){
		//
		if(!validateInsertOrder(t)){
			return false;
		}
		
		String url = PropertyUtil.getProperty("OMS.URL")+"/"+PropertyUtil.getProperty("INSERT_ORDER");
		data = getInsertData(t);
		logger.debug("call oms insertOrder,orderFormData:"+data);
		
		try {
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
			StringEntity requestEntity = new StringEntity(data,"utf-8");//解决中文乱码问题   
			requestEntity.setContentEncoding("UTF-8");    
			requestEntity.setContentType("application/json");    
			HttpPost method = new HttpPost(url);  
			method.setEntity(requestEntity);   
			HttpResponse response = client.execute(method);
			logger.debug("status:" + response.getStatusLine()); 
			HttpEntity responseEntity = response.getEntity();  
			if(null != responseEntity){
				String resData = EntityUtils.toString(responseEntity);
				logger.debug("oms insertOrder responseData:"+resData);
				Map map = Json.fromJson(Map.class, resData);
				if(1 == (int)map.get("code")){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}  
		
		afterInsertOrder(t);
		return false;
	}

	/**
	 * 新增oms订单后续处理
	 * @param t
	 */
	public void afterInsertOrder(T t){}
	
	/**
	 * 获得更新订单接口请求报文
	 * @param t
	 * @return
	 */
	public abstract String getUpdateData(T t);
	
	/**
	 * 校验更新订单接口请求报文
	 * @param t
	 * @return
	 */
	public boolean validateUpdateOrder(T t){
		return true;
	}
	
	/**
	 * 更新oms订单
	 * @param t
	 * @return
	 */
	public Boolean updateOrder(T t){
		if(!validateUpdateOrder(t)){
			return false;
		}
		
		String url = PropertyUtil.getProperty("OMS.URL")+"/"+PropertyUtil.getProperty("UPDATE_ORDER");
		data = getUpdateData(t);
		logger.debug("call oms updateOrder,orderFormData:"+data);
		
		try {
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
			StringEntity requestEntity = new StringEntity(data,"utf-8");//解决中文乱码问题   
			requestEntity.setContentEncoding("UTF-8");    
			requestEntity.setContentType("application/json");    
			HttpPost method = new HttpPost(url);  
			method.setEntity(requestEntity);   
			HttpResponse response = client.execute(method);
			logger.debug("status:" + response.getStatusLine()); 
			HttpEntity responseEntity = response.getEntity();  
			if(null != responseEntity){
				String resData = EntityUtils.toString(responseEntity);
				logger.debug("oms insertOrder responseData:"+resData);
				Map map = Json.fromJson(Map.class, resData);
				if(1 == (int)map.get("code")){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		afterUpdateOrder(t);
		return false;
	}
	
	/**
	 * 更新oms订单后续处理
	 * @param t
	 */
	public void afterUpdateOrder(T t){}
	
}
