package com.iskyshop.smilife.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.iskyshop.core.tools.CommUtil;
import com.tydic.framework.util.PropertyUtil;

public class IpWhiteListFilter implements Filter {

	private static Logger logger = Logger.getLogger(IpWhiteListFilter.class);
	
	/**
	 * 保存访问APP2.0接口的ip白名单。若此列表的size为0，则表示所有ip都被允许通过此过滤器
	 */
	private List<String> ipWhiteList = new ArrayList<String>();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//默认只允许本地ip访问本机的接口
		ipWhiteList.add("127.0.0.1");		
		try {
			java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
			ipWhiteList.add(CommUtil.null2String(addr.getHostAddress()));
		} catch (java.net.UnknownHostException e) {
			logger.error("获取本地址失败。", e);
		}		
		
		String ips = PropertyUtil.getProperty("ipWhiteList");
		if("*".equals(ips)) {
			ipWhiteList.clear();//允许所有ip
		} else if(ips != null) {
			String[] ipList = ips.split(",");
			for(String ip: ipList) {
				ipWhiteList.add(ip);
			}			
		}		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if(isAccessAllowed((HttpServletRequest)request)) {
			chain.doFilter(request, response);
		} else {
			logger.error("Access from ip(" + CommUtil.getIpAddr((HttpServletRequest)request) + ") is not allowed.");
		}
	}
	
	private boolean isAccessAllowed(HttpServletRequest request) {
		if(ipWhiteList.size() > 0) {
			String ip = CommUtil.getIpAddr(request);
			return ipWhiteList.contains(ip);
		}
		return true;
	}

	@Override
	public void destroy() {}
}
