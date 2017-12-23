package com.smi.mc;

import com.smilife.core.common.application.BaseBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan("com.smi.mc")
@MapperScan("com.smi.mc.**.dao")
public class MemberCenterApplication extends BaseBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberCenterApplication.class, args);
	}
}
