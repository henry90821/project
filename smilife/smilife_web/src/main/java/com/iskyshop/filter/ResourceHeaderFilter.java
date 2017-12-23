package com.iskyshop.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CSS和JavaScript文件Header补全过滤器 Created by 亚翔 on 2015/9/9.
 */
public class ResourceHeaderFilter implements Filter {
	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request.getHeader("accept") != null) {
			if (request.getHeader("accept").contains("css")) {
				response.setHeader("Content-Type", "text/css");
			} else if (request.getHeader("accept").contains("javascript")) {
				response.setHeader("Content-Type", "text/javascript");
			}
		}
		chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
