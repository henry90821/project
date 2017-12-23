package com.smi.cloud;

import com.smilife.core.common.application.BaseBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@MapperScan("com.smi.cloud.**.dao")
@EnableAuthorizationServer
public class SmiCloudApplication extends BaseBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmiCloudApplication.class, args);
	}
}
