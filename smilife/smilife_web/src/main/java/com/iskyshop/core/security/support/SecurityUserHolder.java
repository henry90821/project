package com.iskyshop.core.security.support;

import org.springframework.security.core.context.SecurityContextHolder;

import com.iskyshop.foundation.domain.User;

/**
 * 
 * SpringSecurity用户获取工具类，该类的静态方法可以直接获取已经登录的用户信息
 */
public final class SecurityUserHolder {
	private SecurityUserHolder() {
	}

	public static User getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {

			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} 
		
		return null;
	}
}
