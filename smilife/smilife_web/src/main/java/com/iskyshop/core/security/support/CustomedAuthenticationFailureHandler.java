package com.iskyshop.core.security.support;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.iskyshop.core.security.exception.VerifyErrorAuthenticationException;
import com.iskyshop.core.tools.CommUtil;

public class CustomedAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String role = request.getParameter("role");
		String targetUrl = null;
		String errMsg = "登录失败";
		
		if(role != null) {//只有ADMIN和SELLER用户登录时会提交role参数上来
			if("ADMIN".equals(role)) {
				targetUrl = "/admin/login.htm";
			} else {
				role = "SELLER";
				targetUrl = "/seller/login.htm";
			}
			
			if(exception instanceof VerifyErrorAuthenticationException) {
				errMsg = "验证码错误";
			}
			
		} else {//买家单点登录
			role = "BUYER";
			Authentication auth = exception.getAuthentication();
			
			Map details = null;
			if(auth != null) {
				details = (Map)auth.getDetails();
			}
			
			if(details != null && "1".equals(details.get("isWap"))) {
				targetUrl = "/wap/buyer/center.htm";
			} else {
				targetUrl = "/buyer/index.htm";
			}
		}
		
		request.getSession().setAttribute("url", CommUtil.getURL(request) + targetUrl);
		request.getSession().setAttribute("errMsg", errMsg);
		request.getSession().setAttribute("role", role);
		
		request.getRequestDispatcher("/login_error_new.htm").forward(request, response);
	}

}
