package com.smi.am.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.smilife.core.common.valueobject.BaseValueObject;

/**
 * 拦截controller登录认证注解类
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface LoginAuth {
	@SuppressWarnings("rawtypes")
	Class value() default BaseValueObject.class;
}
