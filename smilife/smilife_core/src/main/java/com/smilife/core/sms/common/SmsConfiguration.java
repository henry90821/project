package com.smilife.core.sms.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信所需配置类<br/>
 * Created by Andriy on 16/5/25.
 */
@Component
@ConfigurationProperties(locations = { "classpath:sms-configuration.yml" }, prefix = "smi-sms")
public class SmsConfiguration {

	/**
	 * 短信服务接口地址
	 */
	@Value(value = "url")
	private String url;

	/**
	 * 账号类型【1-生产账号(业务使用),2-服务账号(客服使用)】
	 */
	@Value(value = "account-type")
	private String accountType;

	/**
	 * 渠道编码【101-星美商场,102-星美电影,103-星美生活客户端】
	 */
	@Value(value = "channel-code")
	private String channelCode;

	/**
	 * 短信服务接口地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 短信服务接口地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 账号类型【1-生产账号(业务使用),2-服务账号(客服使用)】
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * 账号类型【1-生产账号(业务使用),2-服务账号(客服使用)】
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * 渠道编码【101-星美商场,102-星美电影,103-星美生活客户端】
	 */
	public String getChannelCode() {
		return channelCode;
	}

	/**
	 * 渠道编码【101-星美商场,102-星美电影,103-星美生活客户端】
	 */
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
}
