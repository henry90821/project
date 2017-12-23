package com.smi.mc.api.aspect;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.utils.RedisUtil;
import com.smi.tools.kits.StrKit;
import com.smilife.core.exception.NoLoginException;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;


/**
 * 登录认证切面类
 * 
 */
@Aspect
@Component
public class LoginAuthAspect {
	private static final Logger LOGGER = LoggerUtils.getLogger(LoginAuthAspect.class);
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;
	
	@Around(value = "@annotation(com.smi.mc.api.annotation.LoginAuth)")
	public Object aroundManager(ProceedingJoinPoint pj) throws Exception {
		Object object = null;
		String token = getRequest(pj.getArgs()).getHeader("token");
		byte[] decodeToken = Base64Utils.decodeFromString(token);
		String tokenStr = StrKit.str(decodeToken, "UTF8");
		boolean isExists = redisUtil.exists(memberCenterConfiguration.getTokenPrefix() + tokenStr);
		if(!isExists){
			//用户未登录
			throw new NoLoginException("用户未登录或者登录已失效");
		}else {
			// 每次操作后，重新设置过期时间为7天
			redisUtil.expire(memberCenterConfiguration.getTokenPrefix() + tokenStr,memberCenterConfiguration.getTokenTimeOut());
		}
		try {
			object = pj.proceed();
		}catch (ConstraintViolationException e) {
			throw e;
		}
		catch (Throwable e) {
			LOGGER.error("登录切面功能异常", e);
			throw new SmiBusinessException("登录切面功能异常", e);
		}
		return object;
	}


	private HttpServletRequest getRequest(Object[] args) {
		HttpServletRequest result = null;
		if (args != null && args.length > 0) {
			for (Object arg : args) {
				if (arg instanceof HttpServletRequest) {
					result = (HttpServletRequest) arg;
					break;
				}
			}
		}
		return result;
	}
}
