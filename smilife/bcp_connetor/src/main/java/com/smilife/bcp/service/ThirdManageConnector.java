package com.smilife.bcp.service;


/**
 * 第三方接口管理
 * @author liz
 * 2015-8-27
 */
public interface ThirdManageConnector {
	
	/**
	 * 通过BCP调用第三方短信发送接口
	 * @param moblie
	 * @param content
	 * @return
	 */
	public boolean sendMess(String moblie, String content);

}
