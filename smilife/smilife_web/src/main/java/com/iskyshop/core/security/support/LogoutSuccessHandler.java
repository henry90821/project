package com.iskyshop.core.security.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysLogService;
import com.tydic.framework.util.PropertyUtil;

public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

	@Autowired
	private ISysLogService sysLogService;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		User user = (User)authentication.getPrincipal();
		
		if(!"BUYER".equals(user.getCurrentLoginRole())) {
			Date now = new Date();
			SysLog log = new SysLog();
			log.setAddTime(now);
			log.setContent(user.getTrueName() + "(id=" + user.getId() + ",loginRole=" + user.getCurrentLoginRole() + ")于" + CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", now) + "登出系统");
			log.setTitle("管理员登出");
			log.setType(0);
			log.setUser_id(user.getId());
			log.setUser_name(user.getUserName());
			log.setIp(CommUtil.getIpAddr(request));
			sysLogService.save(log);
		}
		
		String targetUrl = null;//登出后的跳转地址
		
		if("ADMIN".equals(user.getCurrentLoginRole())) {
			targetUrl = request.getContextPath() + "/admin/login.htm";
        } else if("SELLER".equals(user.getCurrentLoginRole())) {
        	targetUrl = request.getContextPath() + "/seller/login.htm";
        } else {//单点登出时，将清除cas server端的对应session
        	String redirectUrlPostfix = null;
        	//优先根据用户登出时所在页面来判断登出后要重定向的页面
    		String ref = request.getHeader("referer");
    		if(ref != null) {
    			ref = CommUtil.getPureUri(ref);
    			if(ref.indexOf("/wap/") >= 0) {
    				redirectUrlPostfix = "/wap/index.htm";
    			} else {
    				redirectUrlPostfix = "/index.htm";
    			}
    		} else {
    			if(user.isWapContext()) {
            		redirectUrlPostfix = "/wap/index.htm";
                } else {
                	redirectUrlPostfix = "/index.htm";
                }
    		}
        	
    		targetUrl = PropertyUtil.getProperty("CAS_SERVER_LOGOUT_URL")  + "?service=" +  URLEncoder.encode(CommUtil.getURL(request) + redirectUrlPostfix, "utf-8");
        }
		
		response.sendRedirect(targetUrl);
	}

}
