package com.smi.swagger.core;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Loads the spring beans required by the framework
 */
@EnableSwagger
@Component
public class SwaggerConfig {

	/**
	 * 默认标题
	 */
	public final String DEFAULT_TITLE = "星美接口管理系统";

	/**
	 * 默认描述信息
	 */
	public final String DEFAULT_DESCRIPTION = "各个controller下对应相应业务接口";

	/**
	 * 默认服务条款网址
	 */
	public final String DEFAULT_TERMS_SERVICE_URL = "各个controller下对应相应业务接口";

	/**
	 * 默认联系方式
	 */
	public final String DEFAULT_CONTACT = "huangyaxiang@smimovie.com";

	@Autowired
	private SwaggerProperties swaggerProperties;
	@Autowired
	private SpringSwaggerConfig springSwaggerConfig;

	/**
	 * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple swagger groups
	 * i.e. same code base multiple swagger resource listings.
	 */
	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(this.apiInfo()).includePatterns(".*?");
	}

	/**
	 * 获得接口系统基础信息
	 * 
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfo(this.swaggerProperties.getTitle(), this.swaggerProperties.getDescription(),
				this.swaggerProperties.getTermsOfServiceUrl(), this.swaggerProperties.getContact(), null, null);
	}
}