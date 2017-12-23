package com.smilife.core.sms.common;

import com.alibaba.fastjson.JSON;
import com.smi.tools.exception.HttpException;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;
import com.smilife.core.exception.HttpSmiRuntimeException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 短信服务-http请求工具类
 * 
 * @author xzr
 * @date 2016/03/28
 */
public class SmsHttpUtil {
	private static final Logger LOGGER = LoggerUtils.getLogger(SmsHttpUtil.class);

	/**
	 * post 请求
	 *
	 * @param url
	 *            请求的对应方法URL
	 * @param paramsMap
	 *            请求的参数
	 *
	 * @author xzr
	 * @date 2016/03/24
	 * @return
	 * @throws Exception
	 */
	public String executePost(String url, Map<String, Object> paramsMap) throws Exception {
		String requestUrl = url;
		String resultString = "";
		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JSON.toJSONString(paramsMap)));

		try {
			if (null == paramsMap) {
				resultString = HttpKit.post(requestUrl);
			} else {
				resultString = HttpKit.post(requestUrl, paramsMap);
			}
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("sms.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("sms.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (Exception e) {
			throw e;
		}
		LOGGER.info("响应报文:" + JsonKit.formatJson(StrKit.removeSuffix(resultString, "\n")));
		return resultString;
	}

	/**
	 * get 请求
	 *
	 * @param url
	 *            请求的对应方法URL
	 * @param paramsMap
	 *            请求的参数
	 *
	 * @author xzr
	 * @date 2016/03/24
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String executeGet(String url, Map<String, Object> paramsMap) throws Exception {
		String requestUrl = url;
		String resultString = "";
		LOGGER.info("请求地址:" + requestUrl);
		LOGGER.info("请求报文:" + JsonKit.formatJson(JSON.toJSONString(paramsMap)));

		try {
			if (!paramsMap.isEmpty()) {
				String paramStr = HttpKit.toParams(paramsMap);
				requestUrl = requestUrl + "?" + paramStr;
			}
			resultString = HttpKit.get(requestUrl);
		} catch (IOException e) {
			throw new HttpSmiRuntimeException("sms.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (HttpException e) {
			throw new HttpSmiRuntimeException("sms.http.connect.err", requestUrl, paramsMap, resultString);
		} catch (Exception e) {
			throw e;
		}
		LOGGER.info("响应报文:" + JsonKit.formatJson(StrKit.removeSuffix(resultString, "\n")));
		return resultString;
	}

}
