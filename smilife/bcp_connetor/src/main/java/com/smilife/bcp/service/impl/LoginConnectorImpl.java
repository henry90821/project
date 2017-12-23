package com.smilife.bcp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smilife.bcp.dto.response.LoginResp;
import com.smilife.bcp.service.LoginConnector;
import com.smilife.bcp.service.common.BaseServiceImpl;
import com.tydic.eshop.dto.shop.ei.ReqShopUser;
import com.tydic.framework.base.exception.ServiceException;
import com.tydic.shop.service.LoginService;

/**
 * 本接口为测试接口，loginService注入不进，未交给spring管理
 * Created by 亚翔 on 2015/8/24.
 */
public class LoginConnectorImpl extends BaseServiceImpl implements LoginConnector {
	private LoginService loginService;

	@Override
	public LoginResp login(String account, String password) {
		JSONObject jsonObject = null;
		ReqShopUser user = null;
		JSONArray jsonSoo = null;
		JSONObject tmpSooValue = null;
		JSONObject jsonCust = null;
		String custName = null;
		String custId = null;
		LoginResp result = null;

		try {
			// 组装请求对象及参数
			user = new ReqShopUser();
			user.setUserAccount(account);
			user.setPassword(password);
			// 调用服务获得返回结果
			jsonObject = this.loginService.login(user);
			// 开始解析返回报文
			jsonSoo = jsonObject.getJSONArray("SOO");
			tmpSooValue = jsonSoo.getJSONObject(0);
			jsonCust = tmpSooValue.getJSONObject("CUST");
			custName = jsonCust.getString("CUST_NAME");
			custId = jsonCust.getString("CUST_ID");
			// 将返回报文解析成DTO对象
			result = new LoginResp();
			result.setCustId(custId);
			result.setCustName(custName);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return result;
	}
}
