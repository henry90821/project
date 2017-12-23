package com.smilife.bcp.service;

import com.smilife.bcp.dto.response.LoginResp;

/**
 * Created by 亚翔 on 2015/8/24.
 */
public interface LoginConnector {

	/**
	 * 通过BCP调用CRM进行登陆验证
	 * 
	 * @param account
	 *            账号
	 * @param password
	 *            密码
	 * @return
	 */
	LoginResp login(String account, String password);
}
