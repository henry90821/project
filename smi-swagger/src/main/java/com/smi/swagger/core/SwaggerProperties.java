package com.smi.swagger.core;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by Andriy on 16/6/29.
 */
@Component
@ConfigurationProperties(locations = { "classpath:smi-swagger.yml" }, prefix = "smi-swagger")
public class SwaggerProperties {
	private static final Logger LOGGER = LoggerUtils.getLogger(SwaggerProperties.class);

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 描述信息
	 */
	private String description;

	/**
	 * 服务条款网址
	 */
	private String termsOfServiceUrl;

	/**
	 * 联系方式
	 */
	private String contact;

	/**
	 * 是否允许提供API服务
	 */
	private String isEnabled;

	/**
	 * 是否允许提供接口文档页面服务
	 */
	private String isAllowPage;

	@Autowired
	private com.smi.swagger.core.SpringProperties springProperties;

	/**
	 * 标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 标题
	 */
	@Value(value = "${title:星美接口管理系统}")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 描述信息
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 描述信息
	 */
	@Value(value = "${description:各个controller下对应相应业务接口}")
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 服务条款网址
	 */
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	/**
	 * 服务条款网址
	 */
	@Value(value = "${terms-service-url:各个controller下对应相应业务接口}")
	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	/**
	 * 联系方式
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * 联系方式
	 */
	@Value(value = "${contact:huangyaxiang@smimovie.com}")
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * 是否允许提供API服务,默认为TRUE
	 */
	public Boolean isEnabled() {
		return Boolean.valueOf(isEnabled);
	}

	/**
	 * 是否允许提供API服务,默认为TRUE
	 */
	@Value(value = "${enable:true}")
	public void setEnabled(String isEnabled) {
		// TODO 遗留问题:无法正常读取YAML配置文件中设置的值。
		// 判断设入的值是否是合法的布尔值
		if (this.isBoolean(isEnabled)) {
			// 加入判断如果是生产环境则默认设置为不允许访问接口,用来处理无法正常杜宇YAML配置值的问题
			if ("production".equals(this.springProperties.getProfiles())) {
				LOGGER.warn("当前系统激活的是生产环境配置,强制关闭接口管理系统的访问!");
				this.isEnabled = "false";
			} else
				this.isEnabled = isEnabled;
		}
		if (!this.isEnabled())
			LOGGER.warn("在线接口系统服务已关闭!");
		else
			LOGGER.info("在线接口系统服务已开启!");
	}

	/**
	 * 是否允许提供接口文档页面服务,默认为FALSE
	 */
	public Boolean isAllowPage() {
		return Boolean.valueOf(isAllowPage);
	}

	/**
	 * 是否允许提供接口文档页面服务,默认为FALSE
	 */
	@Value(value = "${allow-page:false}")
	public void setAllowPage(String allowPage) {
		// 判断设入的值是否是合法的布尔值
		if (this.isBoolean(allowPage))
			isAllowPage = allowPage;
		if (!this.isAllowPage())
			LOGGER.warn("在线接口系统已关闭页面访问服务!");
		else
			LOGGER.info("已开启在线接口系统页面访问服务!");
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
