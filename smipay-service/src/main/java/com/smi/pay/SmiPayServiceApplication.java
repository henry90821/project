package com.smi.pay;

import com.smilife.core.common.application.BaseBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@MapperScan("com.smi.pay.**.dao")
public class SmiPayServiceApplication extends BaseBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmiPayServiceApplication.class, args);
	}
}
