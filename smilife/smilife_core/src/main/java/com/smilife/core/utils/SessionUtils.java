package com.smilife.core.utils;

import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Andriy on 16/4/5.
 */
public class SessionUtils {

	// private static HttpSession session;

	public enum SessionEnum {

		/**
		 * 用户标识
		 */
		USER,

		/**
		 * 未登录时请求的URL地址,登录后需要直接跳转至该URL
		 */
		UNLOGIN_REQUEST_URL,

		/**
		 * 返回给客户端告知需要跳转的URL
		 */
		REDIRECT_URL
	}

	/**
	 * 验证会话中是否存在用户信息
	 * 
	 * @param request
	 *            当前请求对象
	 * @return 返回true表示存在,false则表示不存在
	 */
	public static boolean validateUser(HttpServletRequest request) {
		return ObjectUtils.isEmpty(getAttribute(request, SessionEnum.USER)) ? false : true;
	}

	/**
	 * 获得当前请求的会话对象
	 * 
	 * @param request
	 *            当前请求
	 * @return
	 */
	public static HttpSession getSession(HttpServletRequest request) {
		// if (ObjectUtils.isEmpty(session))
		// session = request.getSession();
		return request.getSession();
	}

	/**
	 * 保存用户登录状态
	 * 
	 * @param request
	 *            当前请求对象
	 * @param value
	 *            用户登录状态
	 */
	public static void saveLoginStatus(HttpServletRequest request, Object value) {
		setAttribute(request, SessionEnum.USER, value);
	}

	/**
	 * 通过枚举值获取到Session中存储的对应值
	 * 
	 * @param request
	 *            当前请求对象
	 * @param sessionEnum
	 *            枚举值
	 * @return
	 */
	public static Object getAttribute(HttpServletRequest request, SessionEnum sessionEnum) {
		return getSession(request).getAttribute(sessionEnum.toString());
	}

	/**
	 * 通过枚举值设置在Session中存储值
	 * 
	 * @param request
	 *            当前请求对象
	 * @param sessionEnum
	 *            枚举值
	 * @param value
	 *            需要保存的值
	 */
	public static void setAttribute(HttpServletRequest request, SessionEnum sessionEnum, Object value) {
		getSession(request).setAttribute(sessionEnum.toString(), value);
	}

	/**
	 * 通过枚举值移除保存在Session中的值
	 * 
	 * @param request
	 *            当前请求对象
	 * @param sessionEnum
	 *            枚举值
	 */
	public static void removeAttribute(HttpServletRequest request, SessionEnum sessionEnum) {
		getSession(request).removeAttribute(sessionEnum.toString());
	}

	/**
	 * 在请求响应中添加头部信息
	 * 
	 * @param response
	 *            当前响应对象
	 * @param sessionEnum
	 *            枚举值
	 * @param value
	 *            需要保存的值
	 */
	public static void addHeader(HttpServletResponse response, SessionEnum sessionEnum, String value) {
		response.addHeader(sessionEnum.toString(), value);
	}
}
