package com.iskyshop.core.ehcache;

import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: PageCacheFiler.java
 * </p>
 * 
 * <p>
 * Description:Eacache文件缓存处理过滤器，系统相对固定页面及资源文件，纳入缓存管理，避免每次都重复加载资源文件
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class PageCacheFiler extends SimplePageFragmentCachingFilter {
	private Logger logger = Logger.getLogger(this.getClass());
	private static String[] cacheURLs;

	private void init() throws CacheException {
		String patterns = "/floor.htm,/advert_invoke.htm,"
				+ "error.css,goods.css,groupbuy.css,index.css,integral.css,public.css,sparegoods.css,user_phptp.css,user.css,window.css,phone.css,jquery-ui-1.8.22.custom.css,jquery.bigcolorpicker.css,jquery.fancybox-1.3.4.css,jquery.jqzoom.css,jquery.rating.css,jquery..window.css,jvectormap.css,layer.css,layer.ext.css,"
				+ "jcarousellite_1.0.1.min.js,jquery.ad-gallery.js,jquery.bigcolorpicker.js,jquery.cookie.js,jquery.fancybox-1.3.4.pack.js,jquery.form.js,jquery.jqzoom-core.js,jquery.KinSlideshow.min.js,jquery.lazyload.js,jquery.metadata.js,jquery.poshytip.min.js,jquery.rating.pack.js,jquery.shop.base.js,jquery.shop.common.js,jquery.shop.edit.js,jquery.validate.min.js,jquery.zh.cn.js,jquery-1.6.2.js,jquery-ui-1.8.21.js,swfobject.js,swfupload.js,swfupload.queue.js,ajaxfileupload.js,anchor.js,ar.js,backbone-min.js,canvas-tools.js";
		cacheURLs = StringUtils.split(patterns, ",");
	}

	@Override
	protected void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
			throws AlreadyGzippedException, AlreadyCommittedException, FilterNonReentrantException, LockTimeoutException,
			Exception {
		if (cacheURLs == null) {
			init();
		}
		String url = request.getRequestURI();
		String includeUrl = CommUtil.null2String(request.getAttribute("javax.servlet.include.request_uri"));
		boolean flag = false;
		if (cacheURLs != null && cacheURLs.length > 0) {
			for (String cacheURL : cacheURLs) {
				if ("".equals(includeUrl.trim())) {
					if (url.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				} else {
					if (includeUrl.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				}
			}
		}
		// 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
		if (flag) {
			String query = request.getQueryString();
			if (query != null) {
				query = "?" + query;
			}
			logger.info("当前请求被缓存：" + url + query);
			super.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean headerContains(final HttpServletRequest request, final String header, final String value) {
		logRequestHeaders(request);
		@SuppressWarnings("rawtypes")
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see net.sf.ehcache.constructs.web.filter.Filter#acceptsGzipEncoding(javax.servlet.http.HttpServletRequest)
	 * 
	 *      <b>function:</b> 兼容ie6/7 gzip压缩
	 * 
	 * @author erikzhang
	 * 
	 * @createDate 2012-7-4 上午11:07:11
	 * 
	 */
	@Override
	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return acceptsEncoding(request, "gzip") || ie6 || ie7;
	}

	/**
	 * 这个方法非常重要，重写计算缓存key的方法，没有该方法include的url值会出错
	 */
	@Override
	protected String calculateKey(HttpServletRequest httpRequest) {
		// String url = httpRequest.getRequestURI();
		String includeUrl = CommUtil.null2String(httpRequest.getAttribute("javax.servlet.include.request_uri"));
		StringBuffer stringBuffer = new StringBuffer();
		if ("".equals(includeUrl)) {
			stringBuffer.append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
			String key = stringBuffer.toString();
			return key;
		} else {
			stringBuffer.append(CommUtil.null2String(httpRequest.getAttribute("javax.servlet.include.request_uri"))).append(
					CommUtil.null2String(httpRequest.getAttribute("javax.serlvet.include.query_string")));
			String key = stringBuffer.toString();
			return key;
		}
	}
}
