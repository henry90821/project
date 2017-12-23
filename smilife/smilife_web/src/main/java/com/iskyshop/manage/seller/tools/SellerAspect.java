package com.iskyshop.manage.seller.tools;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 商家相关操作的前切面，当店铺到期关闭时只可使用“交易管理”
 */
@Aspect
@Component
public class SellerAspect {
	@Autowired
	private IUserService userService;
	
	private Logger logger = Logger.getLogger(getClass());
	
	private static final String AOP = "execution(* com.iskyshop.manage.seller.action..*.*(..))&&args(request,response,..)";
	private static final String URL = "order_ship,order_confirm,order,ship_address,group_code,transport_list,ecc_set,error,index";
	
	@Around(value = AOP)
	public Object seller_aspect(ProceedingJoinPoint jp, HttpServletRequest request, HttpServletResponse response) {
		User cuerrentUser = SecurityUserHolder.getCurrentUser();
		if(cuerrentUser != null && "SELLER".equals(cuerrentUser.getCurrentLoginRole())) {
			User user = this.userService.getObjById(cuerrentUser.getId());
			user = user.getParent() == null ? user : user.getParent();
			int store_status = (user.getStore() == null ? 0 : user.getStore()
					.getStore_status());
			if(store_status!=15) {
				String[] urls = URL.split(",");
				Boolean ret = false;
				for (String url : urls) {
					if(jp.getSignature().getName().equals(url)){
						ret = true;
						break;
					}			
				}
				if(!ret){
					request.getSession().setAttribute("op_title","该操作不能执行");
					request.getSession().setAttribute("url","index.htm");
					try {
						response.sendRedirect("error.htm");
						return null;
					} catch (IOException e) {
						logger.error("重定向错误", e);
					}
				} 
			}
		}
		
		try {
			return jp.proceed(jp.getArgs());
		} catch (Throwable e) {
			logger.error("执行切面拦截方法出错", e);
		}
		
		return null; 
	}
}
