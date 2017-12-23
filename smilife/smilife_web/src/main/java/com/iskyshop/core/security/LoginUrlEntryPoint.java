package com.iskyshop.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.Assert;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.SpringUtils;

/**
 * 
 * @author Ryan Wu
 *
 */
public class LoginUrlEntryPoint implements AuthenticationEntryPoint {
	private CasAuthenticationEntryPoint pcCasEntryPoint = null;
	private CasAuthenticationEntryPoint wapCasEntryPoint = null;
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		String uri = null;
		
		SavedRequest savedRequest = requestCache.getRequest(request, response);//获取保存的请求
		if (savedRequest != null) {
			uri = savedRequest.getRedirectUrl();
		} else {
			uri = request.getRequestURI();
		}
		
        uri = CommUtil.getPureUri(uri);
        
		if(uri.indexOf("/seller/") >= 0) {//卖家页面
			response.sendRedirect(request.getContextPath() + "/seller/login.htm");
		} else if(uri.indexOf("/admin/") >= 0) {//后台页面
			response.sendRedirect(request.getContextPath() + "/admin/login.htm");
		} else {//为买家页面：/user/xxx或/wap/xxx。注：cas server会自动识别请求来自PC还是WAP
			if(wapCasEntryPoint == null) {
				this.getCasEntryPoint();
			}
			
			if(uri.indexOf("/wap/") >= 0) {
				wapCasEntryPoint.commence(request, response, authException);
			} else {
				pcCasEntryPoint.commence(request, response, authException);
			}
		}
	}
	
	
	/**
	 * 从Spring容器中获取cas entypoint实例
	 */
	private void getCasEntryPoint() {
		pcCasEntryPoint = (CasAuthenticationEntryPoint)SpringUtils.getContext().getBean("casEntryPoint");
		wapCasEntryPoint = (CasAuthenticationEntryPoint)SpringUtils.getContext().getBean("wapCasEntryPoint");
		Assert.notNull(pcCasEntryPoint);
		Assert.notNull(wapCasEntryPoint);
	}
}
