package com.smi.mc.api.service;

import com.smi.mc.api.common.BaseWebIntegrationTests;
import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.valueobject.InfoCustVo;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andriy on 2016/11/1.
 */
public class RestTemplateTest extends BaseWebIntegrationTests {

	@Autowired
	private MemberCenterConfiguration mcConfig;

	@Test
	public void testLogin() throws Exception {
		LoginReq loginReq = new LoginReq("3", "18688109310", "dasdsadasds", "", "1");
		String requestUrl = mcConfig.getCustUrl() + mcConfig.getCheckLoginUrl();

		RestTemplate restTemplate = new RestTemplate();
		DataVo<InfoCustVo> result = restTemplate.postForObject(requestUrl, loginReq, DataVo.class);

		Assert.assertEquals(CodeEnum.NOT_LOGGED_IN.code(), result.getCode());
	}

	@Test
	public void testOldLogin() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("channelCode", "3");
		paramMap.put("account", "18688109310");
		paramMap.put("password", "dasfadsfsadfdasf");
		paramMap.put("verifyCode", "");
		paramMap.put("loginType", "1");
		String requestUrl = mcConfig.getCustUrl() + mcConfig.getCheckLoginUrl();

		String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCheckLoginUrl(), paramMap);
		DataVo<InfoCustVo> result = (DataVo<InfoCustVo>) JsonKit.parseObject(jsonstr, DataVo.class);

		Assert.assertEquals(CodeEnum.NOT_LOGGED_IN.code(), result.getCode());
	}
}

class LoginReq {

	private String channelCode;
	private String account;
	private String password;
	private String verifyCode;
	private String loginType;

	/**
	 * @param channelCode
	 * @param account
	 * @param password
	 * @param verifyCode
	 * @param loginType
	 */
	public LoginReq(String channelCode, String account, String password, String verifyCode, String loginType) {
		super();
		this.channelCode = channelCode;
		this.account = account;
		this.password = password;
		this.verifyCode = verifyCode;
		this.loginType = loginType;
	}

	/**
	 * @return the channelCode
	 */
	public String getChannelCode() {
		return channelCode;
	}

	/**
	 * @param channelCode
	 *            the channelCode to set
	 */
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the verifyCode
	 */
	public String getVerifyCode() {
		return verifyCode;
	}

	/**
	 * @param verifyCode
	 *            the verifyCode to set
	 */
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	/**
	 * @return the loginType
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * @param loginType
	 *            the loginType to set
	 */
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
}
