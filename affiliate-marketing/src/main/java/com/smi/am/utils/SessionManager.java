package com.smi.am.utils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.smi.am.constant.SmiConstants;
import com.smi.am.service.vo.UserVo;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;

/**
 * Session管理
 * 
 * @author yhl
 *
 */
public class SessionManager {

	/**
	 * 添加session
	 * 
	 * @param request
	 * @param sessionName
	 * @param object
	 */
	public static void setAttribute(HttpServletRequest request, String sessionName, Object object) {
		request.getSession().setAttribute(sessionName, object);
	}

	/**
	 * 取得session
	 * 
	 * @param request
	 * @param sessionName
	 *            session名称
	 * @return
	 */
	public static Object getAttribute(HttpServletRequest request, String sessionName) {
		return request.getSession().getAttribute(sessionName);
	}

	/**
	 * 删除session
	 * 
	 * @param request
	 * @param sessionName
	 *            session名称
	 */
	public static void removeAttribute(HttpServletRequest request, String sessionName) {
		request.getSession().removeAttribute(sessionName);
	}

	/**
	 * 用户登出
	 * @param request
	 * @param sessionName
	 */
	public static void logout(HttpServletRequest request){
		request.getSession().invalidate();
	}
	
	/**
	 * 获取请求的客户端IP
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request){
		return request.getRemoteAddr();
	}
	
	/**
	 * 设置session失效时间
	 * @param request
	 * @param secounds
	 */
	public static void setTimeOut(HttpServletRequest request,int secounds){
		request.getSession().setMaxInactiveInterval(secounds);
	}
	/**
	 * 从session中取得用户信息
	 * 
	 * @param request
	 * @return
	 */
	public static Object getUserInfo(HttpServletRequest request) {
		if (SessionManager.getAttribute(request, SmiConstants.USERNAME) == null) {
			BaseValueObject baseValueObject = new BaseValueObject();
			baseValueObject.setCode(CodeEnum.NOT_LOGGED_IN);
			return baseValueObject;
		}
		UserVo uservo = (UserVo) SessionManager.getAttribute(request, SmiConstants.USERNAME);
		return uservo;
	}
	
	/**
	 * 获取服务端全局 web application
	 * @param request
	 * @return
	 */
	public static ServletContext getWebServletContext(HttpServletRequest request){
		return request.getSession().getServletContext();
	}
	

}
