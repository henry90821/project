package com.smilife.core.sms.service.impl;

import com.smi.tools.kits.ReKit;
import com.smilife.core.exception.SmiBusinessCodeException;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.sms.common.SmsConfiguration;
import com.smilife.core.sms.common.SmsConstant;
import com.smilife.core.sms.external.ISmsExternalService;
import com.smilife.core.sms.service.ISmsService;
import com.smilife.core.sms.vo.SmsVo;
import com.smilife.core.utils.ParamCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送接口
 * 
 * @author xzr
 * @date 2016/03/28
 */
@Service("smsService")
public class SmsServiceImpl implements ISmsService {

	@Autowired
	private SmsConfiguration smsConfiguration;
	@Autowired
	private ISmsExternalService smsExternalService;

	public void sendSMS(String mobile, String content, String accountType, String channelCode) {
		// 数据校验
		ParamCheckUtil.checkRequired("mobile", mobile, "content", content);

		if (!ReKit.isMobile(mobile)) {
			throw new SmiBusinessCodeException("vip.mobile.format.err");
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phone", mobile);
		params.put("content", content);
		params.put("accounttype", StringUtils.isEmpty(accountType) ? this.smsConfiguration.getAccountType() : accountType);
		params.put("channel", StringUtils.isEmpty(channelCode) ? this.smsConfiguration.getChannelCode() : channelCode);

		SmsVo smsVo = new SmsVo();
		try {
			smsVo = smsExternalService.sms0001sendSMS(params);
		} catch (Exception e) {
			throw new SmiBusinessException(e.getMessage());
		}

		// 返回结果失败，则抛出异常
		if (SmsConstant.SEND_SUCCESS_CODE != smsVo.getSysCode()) {
			if (smsVo.getMsg().contains("发送次数超限")) {
				throw new SmiBusinessCodeException("sms.number.limit.err");
			} else {
				throw new SmiBusinessException(smsVo.getMsg());
			}
		}

	}

	public void sendSMS(String mobile, String content) {
		this.sendSMS(mobile, content, null, null);
	}

}
