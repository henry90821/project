package com.smi.am.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.smi.am.constant.SmiConstants;
import com.smi.am.utils.SessionManager;
import com.smilife.core.exception.NoLoginException;


/**
 * 登录认证切面类
 * 
 */
@Aspect
@Component
public class LoginAuthAspect {

	@Around(value = "@annotation( com.smi.am.annotation.LoginAuth)")
	public Object aroundManager(ProceedingJoinPoint pj) throws Exception {
		Object amuser = SessionManager.getAttribute(getRequest(pj.getArgs()), SmiConstants.USERNAME);
		if(amuser == null){
			//用户未登录
			throw new NoLoginException("用户未登录");
		}
		try {
			amuser = pj.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return amuser;
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
