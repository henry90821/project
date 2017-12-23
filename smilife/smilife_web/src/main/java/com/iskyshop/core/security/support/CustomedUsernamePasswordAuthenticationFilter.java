package com.iskyshop.core.security.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.iskyshop.core.security.exception.VerifyErrorAuthenticationException;

public class CustomedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		//对店铺管理员和平台管理员登录进行校验码校验
		String code = request.getParameter("code");
		HttpSession session = request.getSession(true);
		
		if(code != null) {
			code = code.trim();
			
			String savedCode = (String)session.getAttribute("verify_code");
			if(code.equals(savedCode)) {//必须要验证码验证
				session.removeAttribute("verify_code");
				return super.attemptAuthentication(request, response);
			}
		}
		
		session.removeAttribute("verify_code");
		
		throw new VerifyErrorAuthenticationException("验证码错误");
	}
}
