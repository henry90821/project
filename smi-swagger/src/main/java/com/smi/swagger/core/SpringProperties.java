package com.smi.swagger.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Andriy on 16/7/8.
 */
@Component
@ConfigurationProperties(prefix = "spring")
public class SpringProperties {

	/**
	 * 当前系统配置激活的PROFILES
	 */
	private String profiles;

	/**
	 * 当前系统配置激活的PROFILES
	 */
	public String getProfiles() {
		return profiles;
	}

	/**
	 * 当前系统配置激活的PROFILES
	 */
	@Value(value = "profiles")
	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}
}
