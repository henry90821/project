package com.iskyshop.core.loader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@EnableSwagger
@ComponentScan(basePackages = { "com.iskyshop.module.app.view.action" })
// Loads the spring beans required by the framework
public class SmiSwaggerConfig extends SpringSwaggerConfig {

	/**
	 * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple swagger groups
	 * i.e. same code base multiple swagger resource listings.
	 */
	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this).apiInfo(this.apiInfo()).includePatterns(".*?");
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("星美生活V2版API接口管理系统", "各个Action下对应相应业务接口", "My Apps API terms of service",
				"lizhi@smimovie.com", null, null);
		return apiInfo;
	}
}