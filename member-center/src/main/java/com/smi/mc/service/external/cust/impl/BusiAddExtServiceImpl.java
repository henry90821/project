package com.smi.mc.service.external.cust.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.po.SOO;
import com.smi.mc.service.busi.cust.BusiAddService;
import com.smi.mc.service.external.cust.BusiAddExtService;
import com.smi.mc.utils.SOOUtils;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import net.sf.json.JSONObject;

/**
 * 业务受理外部服务类，主要解析外部json格式，获取数据
 * 
 * @author smi
 */
@Service
public class BusiAddExtServiceImpl implements BusiAddExtService {

	private final Logger logger = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private BusiAddService busiAddService;

	/**
	 * 解析json 获取数据，传参给业务受理内部服务
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> addBusi(JSONObject JSON) throws BusiServiceException {
		this.logger.info("会员业务受理对外服务请求报文：" + JSON.toString());
		Map<String, Object> reqMap = JSONObject.fromObject(JSON);
		SOO soo = SOOUtils.createResponseSOO("BUSI_ADD_BUSI", "");
		List<Map<String, Object>> sooList = (List<Map<String, Object>>) reqMap.get("SOO");
		Map<String, Object> busiAdd = null;
		for (Map<String, Object> addBusiMap : sooList) {
			if (addBusiMap.containsKey("ADD_BUSI_REQ")) {
				Map<String, Object> addBusiReq = (Map<String, Object>) addBusiMap.get("ADD_BUSI_REQ");
				if (!addBusiReq.isEmpty()) {
					busiAdd = (Map<String, Object>) addBusiReq.get("BUSI_ADD");
				}
			}
		}
		Map<String, Object> resMap = busiAddService.addBusi(busiAdd);
		String result = (String) resMap.get("RESULT");
		if ("0".equals(result)) {
			soo.put("RESP", RESP.createSuccessResp());
		} else {
			soo.put("RESP", RESP.createFailResp((String) resMap.get("CODE"), (String) resMap.get("MSG")));
		}
		this.logger.info("会员业务受理对外服务结束，返回结果：" + resMap.toString());
		return SOOUtils.toResJsonObject(soo);
	}
}
