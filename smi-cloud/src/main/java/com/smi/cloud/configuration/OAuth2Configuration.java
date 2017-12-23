package com.smi.cloud.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andriy on 2016/10/16.
 */
@Configuration
public class OAuth2Configuration implements AuthorizationServerConfigurer {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DataSource dataSource;

	/**
	 * 声明TokenStore实现
	 * 
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(this.dataSource);
	}

	/**
	 * 声明 ClientDetails实现
	 * 
	 * @return
	 */
	@Bean
	public ClientDetailsService clientDetails() {
		return new JdbcClientDetailsService(this.dataSource);
	}

	@Bean
	public JdbcAuthorizationCodeServices jdbcAuthorizationCodeServices() {
		return new JdbcAuthorizationCodeServices(this.dataSource);
	}

	@Bean
	public JdbcApprovalStore jdbcApprovalStore() {
		return new JdbcApprovalStore(this.dataSource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.checkTokenAccess("permitAll()").allowFormAuthenticationForClients();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(this.clientDetails());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
		endpoints.authorizationCodeServices(this.jdbcAuthorizationCodeServices());
		endpoints.approvalStore(this.jdbcApprovalStore());
		endpoints.tokenStore(this.tokenStore());
		// endpoints.userDetailsService(new JdbcUserDetailsManager());

		// 配置TokenServices参数
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(endpoints.getTokenStore());
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
		tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
		// tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));
		tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(2));
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(2));
		endpoints.tokenServices(tokenServices);
	}
}
