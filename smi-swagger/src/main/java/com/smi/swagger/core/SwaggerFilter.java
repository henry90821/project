package com.smi.swagger.core;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Andriy on 16/6/29.
 */
@WebFilter(filterName = SwaggerFilter.FILTER_NAME, urlPatterns = { SwaggerFilter.URL_PATTERN_DOCS,
		SwaggerFilter.URL_PATTERN_DOCS + "/", SwaggerFilter.URL_PATTERN_HTML, SwaggerFilter.URL_PATTERN_API })
public class SwaggerFilter implements Filter {
	private static final Logger LOGGER = LoggerUtils.getLogger(SwaggerFilter.class);

	/**
	 * 过滤器名称
	 */
	public static final String FILTER_NAME = "SMI-SWAGGER-FILTER";

	public static final String URL_PATTERN_DOCS = "/docs";
	public static final String URL_PATTERN_HTML = "/docs/index.html";
	public static final String URL_PATTERN_API = "/api-docs";

	@Autowired
	private SwaggerProperties swaggerProperties;

	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("初始化'{}'过滤器成功!", FILTER_NAME);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOGGER.info("客户端请求访问接口文档相关资源,'{}'过滤器开始进行工作!", FILTER_NAME);
		// 获取当前请求访问的地址信息
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final String requestURI = httpServletRequest.getRequestURI();
		LOGGER.info("客户端请求访问的地址是: {}", requestURI);
		// 判断是否允许通过页面访问接口系统
		if (requestURI.contains(URL_PATTERN_HTML) && !this.swaggerProperties.isAllowPage()) {
			LOGGER.warn("系统不允许通过页面访问接口系统!");
			return;
		}
		// 判断是否允许访问接口系统
		if (!this.swaggerProperties.isEnabled()) {
			LOGGER.warn("系统不允许访问接口系统!");
			return;
		}
		// 判断请求地址如果是"/docs"则直接跳转至接口系统文档页面
		final String apiURI = httpServletRequest.getContextPath() + URL_PATTERN_DOCS;
		if (requestURI.equals(apiURI) || requestURI.equals(apiURI + "/")) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + URL_PATTERN_HTML);
		}
		// 正常执行客户端请求
		chain.doFilter(request, response);
	}

	public void destroy() {
		LOGGER.warn("开始销毁'{}'过滤器!", FILTER_NAME);
	}
}
