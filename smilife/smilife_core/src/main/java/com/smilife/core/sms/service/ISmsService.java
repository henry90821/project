package com.smilife.core.sms.service;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 短信发送接口
 * 
 * @author xzr
 * @date 2016/03/28
 */
@MonitoredWithSpring
public interface ISmsService {

	/**
	 * 短信发送
	 * 
	 * @param mobile
	 *            手机号
	 * @param content
	 *            短信内容
	 * @param accountType
	 *            账号类型
	 * @param channelCode
	 *            渠道编码
	 * 
	 * @return 发送失败，报业务异常
	 */
	void sendSMS(String mobile, String content, String accountType, String channelCode);

	/**
	 * 短信发送
	 * 
	 * @param mobile
	 *            手机号
	 * @param content
	 *            短信内容
	 */
	void sendSMS(String mobile, String content);

}
