package com.smi.mc.api;

import com.smilife.core.common.application.BaseBootApplication;
import org.springframework.boot.SpringApplication;

//import org.mybatis.spring.annotation.MapperScan;

//@MapperScan("com.smi.mc.api.**.dao")
public class MemberAPIServiceApplication extends BaseBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberAPIServiceApplication.class, args);
	}
}
