package com.iskyshop.foundation.advice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

/**
 * Created by 亚翔 on 2015/9/16.
 */
@Aspect
@Component
public class AppExceptionAdvice {

	@AfterThrowing(value = "execution(public * com.iskyshop.module.app.view.action..*.*(..))", throwing = "throwable")
	public void serviceExceptionHandler(JoinPoint joinPoint, Throwable throwable) {
		final Logger LOG = Logger.getLogger(joinPoint.getThis().getClass());
		LOG.error("====================开始-打印业务逻辑层异常信息日志====================");
		try {
			// 获取请求的response对象
			HttpServletResponse response = this.getResponse(joinPoint.getArgs());
			// 设置异常输出格式
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			// 打印日志
			LOG.error(throwable.getMessage(), throwable);
			LOG.error("====================结束-打印业务逻辑层异常信息日志====================");
			// 输出异常接口给APP端
			PrintWriter printWriter = response.getWriter();
			Map json_map = new HashMap();
			json_map.put("msg", "fail");
			json_map.put("tips", throwable.getMessage());
			String json = Json.toJson(json_map, JsonFormat.compact());
			printWriter.write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private HttpServletResponse getResponse(Object[] args) {
		HttpServletResponse result = null;
		if (args != null && args.length > 0) {
			for (Object arg : args) {
				if (arg instanceof HttpServletResponse) {
					result = (HttpServletResponse) arg;
					break;
				}
			}
		}
		return result;
	}

}
