package com.smilife.core.common.configurations;

import com.smilife.core.common.properties.SmiOAuthResourceServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.util.ObjectUtils;

/**
 * Created by Andriy on 2016/10/17.
 */
@Configuration
@EnableConfigurationProperties(value = { SmiOAuthResourceServerProperties.class })
@EnableResourceServer
public class OAuthResourceServerConfigurer implements ResourceServerConfigurer {

	@Autowired
	private ResourceServerProperties resourceServerProperties;
	@Autowired
	private SmiOAuthResourceServerProperties oAuthResourceServerProperties;

	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(this.resourceServerProperties.getResourceId());
	}

	public void configure(HttpSecurity http) throws Exception {
		// 如果是启用了OAuth2认证服务则开始进行资源配置
		if (this.oAuthResourceServerProperties.isEnabled()) {
			// 设置指定允许授权访问的URL
			if (!ObjectUtils.isEmpty(this.oAuthResourceServerProperties.getAuthenticatedUrl())) {
				for (String authUrl : this.oAuthResourceServerProperties.getAuthenticatedUrl()) {
					http.authorizeRequests().antMatchers(authUrl).authenticated();
				}
			}
		} else {
			// 没有启用OAuth2认证服务则默认通过所有请求
			http.authorizeRequests().antMatchers("/**").permitAll();
		}
	}
}
