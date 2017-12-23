package com.smilife.core.sms.external.impl;

import com.alibaba.fastjson.JSON;
import com.smilife.core.sms.common.SmsConfiguration;
import com.smilife.core.sms.common.SmsHttpUtil;
import com.smilife.core.sms.external.ISmsExternalService;
import com.smilife.core.sms.vo.SmsVo;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信发送对外接口
 * 
 * @author xzr
 * @date 2016/03/25
 */
@Service("smsExternalService")
public class SmsExternalServiceImpl extends SmsHttpUtil implements ISmsExternalService {
	private static final Logger LOGGER = LoggerUtils.getLogger(SmsHttpUtil.class);

	@Autowired
	private SmsConfiguration smsConfiguration;

	/**
	 * 短信发送 sms0001sendSMS
	 */
	public SmsVo sms0001sendSMS(Map<String, Object> params) throws Exception {
		String resultStr = null;

		try {
			resultStr = executePost(this.smsConfiguration.getUrl(), params);
		} catch (Exception e) {
			LOGGER.error("调用短息服务异常!", e);
			throw e;
		}
		SmsVo smsVo = JSON.parseObject(resultStr, SmsVo.class);
		return smsVo;
	}

}
