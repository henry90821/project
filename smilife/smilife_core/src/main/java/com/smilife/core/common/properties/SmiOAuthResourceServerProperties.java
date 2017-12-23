package com.smilife.core.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Andriy on 2016/10/19.
 */
@ConfigurationProperties(locations = { "classpath:smi-oauth.yml" }, prefix = "smi-oauth")
public class SmiOAuthResourceServerProperties {

	/**
	 * 是否开启OAuth2认证服务
	 */
	private String enabled = "false";

	/**
	 * 允许授权访问的URL
	 */
	private List<String> authenticatedUrl;

	/**
	 * 是否开启OAuth2认证服务,默认为FALSE
	 */
	public Boolean isEnabled() {
		return Boolean.valueOf(enabled);
	}

	/**
	 * 是否开启OAuth2认证服务,默认为FALSE
	 */
	@Value(value = "enabled")
	public void setEnabled(String enabled) {
		// 判断设入的值是否是合法的布尔值
		if (this.isBoolean(enabled))
			this.enabled = enabled;
	}

	/**
	 * 允许授权访问的URL
	 */
	public List<String> getAuthenticatedUrl() {
		return authenticatedUrl;
	}

	/**
	 * 允许授权访问的URL
	 */
	@Value(value = "authenticated-url")
	public void setAuthenticatedUrl(List<String> authenticatedUrl) {
		this.authenticatedUrl = authenticatedUrl;
	}

	/**
	 * 判断字符串是否能转换成布尔值
	 *
	 * @param boolStr
	 *            需要判断的字符串
	 * @return 返回TRUE则表示改字符串值是合法的布尔值内容,反之亦然
	 */
	private Boolean isBoolean(String boolStr) {
		Boolean result = false;
		if (!StringUtils.isEmpty(boolStr) && ("true".equals(boolStr.toLowerCase()) || "false".equals(boolStr.toLowerCase())))
			result = true;
		return result;
	}
}
