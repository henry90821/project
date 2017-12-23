package com.smilife.core.common.configurations;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Andriy on 16/7/4.
 */
@Configuration
public class CorsConfiguration {
	private static final Logger LOGGER = LoggerUtils.getLogger(CorsConfiguration.class);
	private static final String CORS_PATH = "/**";

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				LOGGER.info("启用跨域访问服务配置,跨域授权路径地址:{}", CORS_PATH);
				registry.addMapping(CORS_PATH);
			}
		};
	}
}
