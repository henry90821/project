
package com.smi.mc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * mc-config.yml配置文件信息
 */
@Component
@ConfigurationProperties(locations = { "classpath:mc-config.yml" }, prefix = "member-center")
public class MemberCenterConfiguration {

	/**
	 * 是否开启登录密码逻辑:MD5(密文+随机数)
	 */
	private String openRndnumEncryption;
	
	/**
	 * 短信登录模板
	 */
	private String smsLoginModel;
	
	/**
	 * 短信注册模板
	 */
	private String smsRegisterModel;

	

	public String getSmsLoginModel() {
		return smsLoginModel;
	}

	@Value(value = "sms-login-model")
	public void setSmsLoginModel(String smsLoginModel) {
		this.smsLoginModel = smsLoginModel;
	}

	public String getOpenRndnumEncryption() {
		return openRndnumEncryption;
	}

	@Value(value = "open_rndnum_encryption")
	public void setOpenRndnumEncryption(String openRndnumEncryption) {
		this.openRndnumEncryption = openRndnumEncryption;
	}

	public String getSmsRegisterModel() {
		return smsRegisterModel;
	}
	
	@Value(value = "sms-register-model")
	public void setSmsRegisterModel(String smsRegisterModel) {
		this.smsRegisterModel = smsRegisterModel;
	}

	
	
}
