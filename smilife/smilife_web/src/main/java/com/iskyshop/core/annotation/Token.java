package com.iskyshop.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解用于在APP的请求中注入User对象
 * 注：
 * （1）添加此注解的方法的返回值类型必须为com.iskyshop.smilife.common.Result类型或其子类型，否则可能会出现ClassCastException异常
 * （2）添加此注解的方法的的参数中必须要有一个HttpServletRequest类型的参数和一个com.iskyshop.foundation.domain.User类型的参数
 */
@Target({ElementType.TYPE,ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Token {
	/**
	 * 是否允许注入的User对象为null，默认为false，表示不允许注入的User对象为null
	 * @return
	 */
	public boolean userNullable() default false;
	
	
	/**
	 * 若商城端有对应的User记录，那么是否检查并更新商城端对应的User记录：true：表示检查并同步CRM端的的对应会员信息到商城端；false：不更新商城端已存在的User记录，默认为false。
	 * @return
	 */
	public boolean updateUser() default false;
}
