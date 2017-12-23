package com.iskyshop.core.security.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.iskyshop.foundation.domain.User;

public class CustomedAccessDeniedHandler implements AccessDeniedHandler {
	public static final String SPRING_SECURITY_ACCESS_DENIED_EXCEPTION_KEY = "SPRING_SECURITY_403_EXCEPTION";
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		String errorPage = null;
		User user = SecurityUserHolder.getCurrentUser();//用户肯定是已登录了才有可能进入此方法

		if("ADMIN".equals(user.getCurrentLoginRole())) {
			errorPage = "/authority.htm";
        } else if("SELLER".equals(user.getCurrentLoginRole())) {
        	errorPage = "/seller/authority.htm";
        } else {
        	if(user.isWapContext()) {
        		errorPage = "/wap/authority.htm";
        	} else {
        		errorPage = "/buyer/authority.htm";
        	}
        }
		
		((HttpServletRequest) request).setAttribute(SPRING_SECURITY_ACCESS_DENIED_EXCEPTION_KEY, accessDeniedException);

		request.getRequestDispatcher(errorPage).forward(request, response);

		if (!response.isCommitted()) {//如果上面的errorPage请求没有对应的Handler进行响应，则返回系统默认的响应
			((HttpServletResponse) response).sendError(403, accessDeniedException.getMessage());
		}
	}

}
