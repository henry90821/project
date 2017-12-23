package com.smilife.core.utils;

import com.smi.tools.exception.HttpException;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smilife.core.exception.HttpSmiRuntimeException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 公共的-http请求工具类
 * 
 * @author xzr
 * @date 2016/05/20
 */
public class CommHttpUtil {

	private static final Logger LOGGER = LoggerUtils.getLogger(CommHttpUtil.class);

	/**
	 * post 请求 
	 * 
	 * paramMap数据
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * 
	 * return
	 */
	public String executePost(String requestUrl, Map<String, Object> paramsMap) {

		// 参数校验
		ParamCheckUtil.checkRequired("requestUrl", requestUrl);

		String resultString = "";

		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JsonKit.toJsonString(paramsMap)));

		try {
			if (null == paramsMap) {
				resultString = HttpKit.post(requestUrl);
			} else {
				resultString = HttpKit.post(requestUrl, paramsMap);
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}
	
	/**
	 * post 请求
	 * 
	 * params数据
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * 
	 * return
	 */
	public String executePostParams(String requestUrl, Map<String, Object> paramsMap) {

		// 参数校验
		ParamCheckUtil.checkRequired("requestUrl", requestUrl);

		String resultString = "";

		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JsonKit.toJsonString(paramsMap)));

		try {
			if (null == paramsMap) {
				resultString = HttpKit.post(requestUrl);
			} else {
				resultString = HttpKit.post(requestUrl, JsonKit.toJsonString(paramsMap));
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}

	/**
	 * get 请求
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * 
	 * return
	 */
	public String executeGet(String requestUrl, Map<String, Object> paramsMap) {

		// 参数校验
		ParamCheckUtil.checkRequired("requestUrl", requestUrl);

		String resultString = "";

		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JsonKit.toJsonString(paramsMap)));

		try {
			if (null!=paramsMap&&!paramsMap.isEmpty()) {
				String paramStr = HttpKit.toParams(paramsMap);
				requestUrl = requestUrl + "?" + paramStr;
			}
			resultString = HttpKit.get(requestUrl);
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}
	
	/**
	 * put 请求
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * 
	 * return
	 */
	public String executePut(String requestUrl, Map<String, Object> paramsMap){
		
		//参数校验
		ParamCheckUtil.checkRequired("requestUrl",requestUrl);
					
		String resultString = "";
		
		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JsonKit.toJsonString(paramsMap)));

		try {
			if (null == paramsMap) {
				resultString = HttpKit.put(requestUrl);
			} else {
				resultString = HttpKit.put(requestUrl, paramsMap);
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}
	
	/**
	 * delete 请求
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * 
	 * return
	 */
	public String executedelete(String requestUrl, Map<String, Object> paramsMap){
		
		//参数校验
		ParamCheckUtil.checkRequired("requestUrl",requestUrl);
					
		String resultString = "";
		
		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JsonKit.toJsonString(paramsMap)));

		try {
			if (null == paramsMap) {
				resultString = HttpKit.delete(requestUrl);
			} else {
				resultString = HttpKit.delete(requestUrl, paramsMap);
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}

	/**
	 * PostFile 请求
	 * 
	 * @param requestUrl 请求的对应方法URL
	 * @param paramsMap  请求的参数
	 * @param file       上传的文件
	 * @param paramName  接收文件的名字
	 * 
	 * return
	 */
	public String executePostFile(String requestUrl, Map<String, Object> paramsMap,File file,String paramName){
		
		//参数校验
		ParamCheckUtil.checkRequired("requestUrl",requestUrl);
		
		String resultString = "";
		if (null!=paramsMap&&!paramsMap.isEmpty()) {
			String paramStr = HttpKit.toParams(paramsMap);
			requestUrl = requestUrl + "?" + paramStr;
		}
		LOGGER.info("请求地址:" + requestUrl);
		
		HttpClient httpclient = HttpClients.createDefault();
		try {
			
		    HttpPost httpPost = new HttpPost(requestUrl); 
		    
			FileBody fileBody = new FileBody(file);  
			MultipartEntity entity = new MultipartEntity();
			entity.addPart(paramName, fileBody);
			httpPost.setEntity(entity); 
			HttpResponse response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				LOGGER.debug("服务器正常响应.....");
				HttpEntity resEntity = response.getEntity();
				resultString=EntityUtils.toString(resEntity);
				LOGGER.debug(resultString);
				LOGGER.debug(resEntity.getContent().toString());
				EntityUtils.consume(resEntity);
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("comm.http.connect.err", requestUrl, paramsMap, resultString);
		}
		
		LOGGER.info("响应报文:" + JsonKit.formatJson(resultString));
		return resultString;
	}

}
