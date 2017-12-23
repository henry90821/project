
package com.smi.mc.api.common;

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
	 * token会话时间(默认7天)
	 */
	private String tokenTimeOut;
	
	/**
	 * token前缀
	 */
	private String tokenPrefix;

	/**
	 * 会员中心接口URL
	 */
	private String custUrl;
	
	/**
	 * 会员支付密码验证Url
	 */
	private String custCheckpwdUrl;

	/**
	 * 判断是否设置了支付密码url
	 */
	private String custJudgeSetpaypwdUrl;

	/**
	 * 设置支付密码Url
	 */
	private String custSetpaypwdUrl;

	/**
	 * 重置登录密码Url
	 */
	private String custRestLoginpwdUrl;

	/**
	 * 会员登陆接口URL,校验账户和密码
	 */
	private String checkLoginUrl;
	
	/**
	 * 会员资料新增接口URL
	 */
	private String custAddUrl;
	
	/**
	 * 会员资料修改接口URL
	 */
	private String custUpdateUrl;
	
	/**
	 * 会员资料查询接口URL
	 */
	private String custQueryUrl;
	
	/**
	 * 根据手机号码查询资料
	 */
	private String custQuerybynumUrl;

	/**
	 * 发送短信验证码接口URL
	 */
	private String smsVerifycodeUrl;
	
	/**
	 * @return the custUrl
	 */
	public String getCustUrl() {
		return custUrl;
	}

	/**
	 * @param custUrl
	 *            the custUrl to set
	 */
	@Value(value = "cust_url")
	public void setCustUrl(String custUrl) {
		this.custUrl = custUrl;
	}

	public String getCustCheckpwdUrl() {
		return custCheckpwdUrl;
	}

	@Value(value = "cust_checkpwd_url")
	public void setCustCheckpwdUrl(String custCheckpwdUrl) {
		this.custCheckpwdUrl = custCheckpwdUrl;
	}

	public String getCustJudgeSetpaypwdUrl() {
		return custJudgeSetpaypwdUrl;
	}

	@Value(value = "cust_judge_setpaypwd_url")
	public void setCustJudgeSetpaypwdUrl(String custJudgeSetpaypwdUrl) {
		this.custJudgeSetpaypwdUrl = custJudgeSetpaypwdUrl;
	}

	public String getCustSetpaypwdUrl() {
		return custSetpaypwdUrl;
	}

	@Value(value = "cust_setpaypwd_url")
	public void setCustSetpaypwdUrl(String custSetpaypwdUrl) {
		this.custSetpaypwdUrl = custSetpaypwdUrl;
	}

	public String getCustRestLoginpwdUrl() {
		return custRestLoginpwdUrl;
	}

	@Value(value = "cust_rest_loginpwd_url")
	public void setCustRestLoginpwdUrl(String custRestLoginpwdUrl) {
		this.custRestLoginpwdUrl = custRestLoginpwdUrl;
	}
	
	public String getCheckLoginUrl() {
		return checkLoginUrl;
	}

	@Value(value = "check_login_url")
	public void setCheckLoginUrl(String checkLoginUrl) {
		this.checkLoginUrl = checkLoginUrl;
	}

	public String getCustAddUrl() {
		return custAddUrl;
	}

	@Value(value = "cust_add_url")
	public void setCustAddUrl(String custAddUrl) {
		this.custAddUrl = custAddUrl;
	}

	public String getCustUpdateUrl() {
		return custUpdateUrl;
	}

	@Value(value = "cust_update_url")
	public void setCustUpdateUrl(String custUpdateUrl) {
		this.custUpdateUrl = custUpdateUrl;
	}

	public String getCustQueryUrl() {
		return custQueryUrl;
	}

	@Value(value = "cust_query_url")
	public void setCustQueryUrl(String custQueryUrl) {
		this.custQueryUrl = custQueryUrl;
	}

	public long getTokenTimeOut() {
		return Long.parseLong(tokenTimeOut);
	}

	@Value(value = "token_time_out")
	public void setTokenTimeOut(String tokenTimeOut) {
		this.tokenTimeOut = tokenTimeOut;
	}

	public String getTokenPrefix() {
		return tokenPrefix;
	}

	@Value(value = "token_prefix")
	public void setTokenPrefix(String tokenPrefix) {
		this.tokenPrefix = tokenPrefix;
	}

	public String getCustQuerybynumUrl() {
		return custQuerybynumUrl;
	}
	
	@Value(value = "cust_querybynum_url")
	public void setCustQuerybynumUrl(String custQuerybynumUrl) {
		this.custQuerybynumUrl = custQuerybynumUrl;
	}

	public String getSmsVerifycodeUrl() {
		return smsVerifycodeUrl;
	}

	@Value(value = "sms_verifycode_url")
	public void setSmsVerifycodeUrl(String smsVerifycodeUrl) {
		this.smsVerifycodeUrl = smsVerifycodeUrl;
	}

	
}
