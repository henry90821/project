package com.iskyshop.core.security.support;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.iskyshop.core.tools.CommUtil;

public class CustomedAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, Map> {

	@Override
	public Map buildDetails(HttpServletRequest req) {
		String uri = CommUtil.getPureUri(req.getRequestURI());
        
        Map m = new HashMap(1);
        
        if(uri.indexOf("xmh_wap_cas_login.xmh") >= 0) {
        	m.put("isWap", "1");
        } else {
        	m.put("isWap", "0");
        }
        
		return m;
	}

}
