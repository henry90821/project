package com.iskyshop.foundation.advice;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.smilife.bcp.dto.response.CustDto;
import com.smilife.bcp.service.UserManageConnector;


/**
 * 自动注入User切面
 */
@Aspect
@Component
public class InjectionUserAdvice {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private UserManageConnector userManageConnector;
	@Autowired
	private UserTools			userTools;
	
	
	/**
	 * 自动注入用户信息
	 * @param jp
	 * @param token
	 */
	@Around(value = "execution(public * com.iskyshop.smilife..*.*(..))&&@annotation(token)")
	private Object around(ProceedingJoinPoint jp, Token token){
		
		HttpServletRequest req = getRequestParam(jp.getArgs());		
		String userToken = req.getParameter("token");
		User user = null;
		Result ret = new Result();
		
		try {
			if(StringUtils.hasText(userToken)) {
				CustDto custDto = userManageConnector.getSessionUser(userToken);
				if(custDto !=  null) {//用户已登录
					user = userTools.syncBuyerFromCRM(custDto.getCustId(), token.updateUser(), true);				
				} else if(!token.userNullable()){//用户未登录
					throw new SmiBusinessException("请求中的用户token信息无效：用户未登录，请求失败");				    
				}				
			} else if(!token.userNullable()){
				throw new SmiBusinessException("请求中必须要有用户token信息，请求失败");
			}
			
			return jp.proceed(this.injectionUserParam(jp.getArgs(), user));
			
		} catch (SmiBusinessException e) {
			logger.error(e.getMessage());
			ret.set(ErrorEnum.REQUEST_ERROR);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			ret.set(ErrorEnum.SYSTEM_ERROR);
		}
		
		return ret;
	}
	
    
	/**
	 * 向参数列表中注入User对象
	 * @param args
	 * @param user
	 * @return 修改后的参数列表
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private Object[] injectionUserParam(Object[] args, User user) {
		boolean found = false;
		if (args != null && args.length > 0) {
			for(int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof User) {
					args[i] = user;
					found = true;
					break;
				}
			}
		}
		if(!found) {
			throw new RuntimeException("注解Token使用错误：标注了Token注解的方法，其参数列表中必须要有一个com.iskyshop.foundation.domain.User类型的参数！");
		}
		return args;
	}

	/**
	 * 从参数列表args中获取HttpServletRequest对象
	 * @param args
	 * @return  若未找到request对象，则返回null
	 */
	private HttpServletRequest getRequestParam(Object[] args) {
		HttpServletRequest req = null;
		if (args != null && args.length > 0) {
			for (Object arg : args) {
				if (arg instanceof HttpServletRequest) {
					req =(HttpServletRequest)arg;
					break;
				}
			}
		}
		if(req == null) {
			throw new RuntimeException("注解Token使用错误：标注了Token注解的方法，其参数列表中必须要有一个HttpServletRequest类型的参数！");
		}
		return req;
	}
}
