package com.smilife.core.sms.external;

import com.smilife.core.sms.vo.SmsVo;
import net.bull.javamelody.MonitoredWithSpring;

import java.util.Map;

/**
 * 短信发送对外接口
 * 
 * @author xzr
 * @date 2016/03/25
 */
@MonitoredWithSpring
public interface ISmsExternalService {

	/**
	 * 短信发送 sms0001sendSMS
	 */
	SmsVo sms0001sendSMS(Map<String, Object> params) throws Exception;

}
