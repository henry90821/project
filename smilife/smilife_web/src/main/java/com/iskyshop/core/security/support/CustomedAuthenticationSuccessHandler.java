package com.iskyshop.core.security.support;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.IUserService;


public class CustomedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISysLogService sysLogService;
	
    private RequestCache requestCache = new HttpSessionRequestCache();
   
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		User user = (User)authentication.getPrincipal();
		
		Map details = authentication.getDetails() == null ? null: (authentication.getDetails() instanceof Map? (Map)authentication.getDetails(): null); 
		if(details != null && "1".equals(details.get("isWap"))) {
			user.setWapContext(true);
		} 
		
		Date now = new Date();
		
		User u = userService.getObjById(user.getId());//重新获取user
		u.setLastLoginDate(u.getLoginDate());
		u.setLastLoginIp(u.getLoginIp());
		u.setLoginDate(now);
		u.setLoginIp(CommUtil.getIpAddr(request));
		userService.update(u);
		
		if(!"BUYER".equals(user.getCurrentLoginRole())) {
			SysLog log = new SysLog();
			log.setAddTime(now);
			log.setContent(user.getTrueName() + "(id=" + user.getId() + ")于" + CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", now) + "以角色" + user.getCurrentLoginRole() + "成功登入系统");
			log.setTitle("管理员登录");
			log.setType(0);
			log.setUser_id(user.getId());
			log.setUser_name(user.getUserName());
			log.setIp(CommUtil.getIpAddr(request));
			sysLogService.save(log);
		}
		
		//将当前登录的买家用户的id保存到Cookie中去，供前端页面上的统计代码统计
		Cookie uIdCookie = new Cookie("uid", user.getId().toString());
		uIdCookie.setMaxAge(-1);
		uIdCookie.setPath("".equals(request.getContextPath()) ? "/" : request.getContextPath());
		response.addCookie(uIdCookie);
		
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		
        if (savedRequest != null) {
        	String targetUrl = savedRequest.getRedirectUrl();
        	
        	requestCache.removeRequest(request, response);
            
            HttpSession session = request.getSession();

            if (session != null) {
            	session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
            
           response.sendRedirect(targetUrl);            
           return;
        }
        
        //根据用户当前的角色重定向到对应的页面中去
        if("ADMIN".equals(user.getCurrentLoginRole())) {
        	response.sendRedirect(request.getContextPath() + "/admin/index.htm");
        } else if("SELLER".equals(user.getCurrentLoginRole())) {
        	response.sendRedirect(request.getContextPath() + "/seller/index.htm");
        } else if(user.isWapContext()) {
        	response.sendRedirect(request.getContextPath() + "/wap/buyer/center.htm");
        } else {
        	response.sendRedirect(request.getContextPath() + "/buyer/index.htm");
        }
	}
}
