package com.smilife.bcp.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smilife.bcp.service.ThirdManageConnector;
import com.tydic.framework.base.exception.ServiceException;
import com.tydic.shop.constant.InterfaceConstant;
import com.tydic.shop.util.ThirdInvokeUtil;

@Service
public class ThirdManageConnectorImpl implements ThirdManageConnector {

	@Autowired
	private ThirdInvokeUtil thirdInvokeUtil;
	private Logger logger = Logger.getLogger(this.getClass());

	public boolean sendMess(String moblie, String content) {
		boolean result = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", moblie);
		map.put("content", content);
		try {
			thirdInvokeUtil.invoke(InterfaceConstant.BIC_SMS_SEND, map);
			result = true;
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
		}
		return result;
	}

}
