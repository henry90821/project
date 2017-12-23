package com.smi.am;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

import com.smilife.core.common.application.BaseBootApplication;

@MapperScan("com.smi.am.**.dao")
public class AffiliateMarketingApplication extends BaseBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(AffiliateMarketingApplication.class, args);
	}
}
