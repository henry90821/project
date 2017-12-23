package com.iskyshop.core.mv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.HttpInclude;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.UserConfig;

/**
 * 顶级视图管理类，封装ModelAndView并进行系统扩展
 * 
 * @author Liangshuai
 *
 */
public class JModelAndView extends ModelAndView {
	/**
	 * 普通视图，根据velocity配置文件的路径直接加载视图
	 * 
	 * @param viewName
	 *            视图名称
	 */
	public JModelAndView(String viewName) {
		super.setViewName(viewName);
	}

	/**
	 * 自定义视图构造函数
	 * 
	 * @param viewName
	 *            用户自定义的视图，可以添加任意路径
	 * @param config
	 *            系统配置
	 * @param uconfig
	 *            用户配置
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig, HttpServletRequest request,
			HttpServletResponse response) {
		String contextPath = "";
		if (!("/").equals(request.getContextPath())) {
			contextPath = request.getContextPath();
		}
		String webPath = CommUtil.getURL(request);
		super.addObject("current_webPath", webPath);
		String port = request.getServerPort() == 80 ? "" : ":" + CommUtil.null2Int(request.getServerPort());
		if (Globals.SSO_SIGN && config.isSecond_domain_open() && !"localhost".equals(CommUtil.generic_domain(request))
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
		}
		super.setViewName(viewName);
		super.addObject("domainPath", CommUtil.generic_domain(request));
		if (!StringUtils.isNullOrEmpty(config.getImageWebServer())) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);
		}
		super.addObject("webPath", webPath);
		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));
		String queryUrl = "";
		if (!StringUtils.isNullOrEmpty(request.getQueryString())) {
			queryUrl = "?" + request.getQueryString();
		}
		super.addObject("current_url", request.getRequestURI() + queryUrl);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();
		if (serverName.indexOf("www.") < 0 && serverName.indexOf(".") >= 0
				&& serverName.indexOf(".") != serverName.lastIndexOf(".") && config.isSecond_domain_open()) {
			String secondDomain = serverName.substring(0, serverName.indexOf("."));
			second_domain_view = true; // 使用二级域名访问，相关js url需要处理，避免跨域
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view", second_domain_view);
	}

	/**
	 * 按指定路径加载视图，如不指定则系统默认路径加载
	 * 
	 * @param viewName
	 *            视图名称
	 * @param config
	 *            商城配置
	 * @param uconfig
	 *            自定配置，和type配合使用
	 * @param type
	 *            视图类型 0为后台，1为前台 大于1为自定义路径
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig, int type, HttpServletRequest request,
			HttpServletResponse response) {
		if (config.getSysLanguage() != null) {
			if (Globals.DEFAULT_SYSTEM_LANGUAGE.equals(config.getSysLanguage())) {
				if (type == 0) {
					super.setViewName(Globals.SYSTEM_MANAGE_PAGE_PATH + viewName);
				}
				if (type == 1) {
					super.setViewName(Globals.SYSTEM_FORNT_PAGE_PATH + viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}

			} else {
				if (type == 0) {
					super.setViewName(Globals.DEFAULT_SYSTEM_PAGE_ROOT + config.getSysLanguage() + "/system/" + viewName);
				}
				if (type == 1) {
					super.setViewName(Globals.DEFAULT_SYSTEM_PAGE_ROOT + config.getSysLanguage() + "/shop/" + viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}
			}
		} else {
			super.setViewName(viewName);
		}
		super.addObject("CommUtil", new CommUtil());
		String contextPath = "";
		if (!("/").equals(request.getContextPath())) {
			contextPath = request.getContextPath();
		}
		String webPath = CommUtil.getURL(request);
		String port = request.getServerPort() == 80 ? "" : ":" + CommUtil.null2Int(request.getServerPort());
		super.addObject("current_webPath", webPath);
		if (Globals.SSO_SIGN && config.isSecond_domain_open() && !"localhost".equals(CommUtil.generic_domain(request))
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
		}
		super.addObject("domainPath", CommUtil.generic_domain(request));
		super.addObject("webPath", webPath);
		if (!StringUtils.isNullOrEmpty(config.getImageWebServer())) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);

		}
		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));
		String queryUrl = "";
		if (request.getQueryString() != null && !"".equals(request.getQueryString())) {
			queryUrl = "?" + request.getQueryString();
		}
		super.addObject("current_url", request.getRequestURI() + queryUrl);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();
		if (serverName.indexOf("www.") < 0 && serverName.indexOf(".") >= 0
				&& serverName.indexOf(".") != serverName.lastIndexOf(".") && config.isSecond_domain_open()) {
			String secondDomain = serverName.substring(0, serverName.indexOf("."));
			second_domain_view = true; // 使用二级域名访问，相关js url需要处理，避免跨域
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view", second_domain_view);
	}
}
