package com.smi.mc.service.external.cust.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.po.SOO;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;
import com.smi.mc.service.busi.cust.CustQryService;
import com.smi.mc.service.external.cust.CustQryExtService;
import com.smi.mc.utils.SOOUtils;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import net.sf.json.JSONObject;

/**
 * 会员基本资料查询的对外服务
 * 
 * @author smi
 *
 */
@Service
public class CustQryExtServiceImpl implements CustQryExtService {

	private final Logger logger = LoggerUtils.getLogger(this.getClass());
	@Autowired
	private CustQryService custQryService;
	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;

	/**
	 * 会员资料查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> custQry(JSONObject JSON) {
		this.logger.info("会员资料查询对外服务的请求报文：" + JSON.toString());
		Date head = new Date();
		Map<String, Object> reqMap = JSONObject.fromObject(JSON);
		List<Map<String, Object>> sooList = (List<Map<String, Object>>) reqMap.get("SOO");
		SOO resSoo = SOOUtils.createResponseSOO("CUST_QUERY");
		if (sooList.size() > 0) {
			for (Map<String, Object> qryMap : sooList) {
				if (qryMap.containsKey("PUB_REQ")) {
					Map<String, Object> typeMap = (Map<String, Object>) qryMap.get("PUB_REQ");
					String type = (String) typeMap.get("TYPE");
					if ("CUST_QUERY".equals(type)) {
						Map<String, Object> custQryMap = (Map<String, Object>) qryMap.get("CUST_QRY");
						try {
							if (qryMap.containsKey("CUST_QRY")) {
								Map<String, Object> resQryMap = this.custQryService.qryCust(qryMap);
								RESP resp = (RESP) resQryMap.get("RESP");
								String result = resp.getResult();
								if ("0".equals(result)) {
									resSoo.put("CUST", resQryMap.get("CUST"));
								}
								resSoo.put("RESP", resp.toString());
								this.logger.info("会员资料查询对外服务的响应报文：" + resQryMap.toString());
								return SOOUtils.toResJsonObject(resSoo);
							}
						} catch (BusiServiceException e) {
							this.logger.error("会员资料查询异常" + e);
						} finally {
							Date end = new Date();
							Map<String, Object> logMap = new HashMap<String, Object>();
							logMap.put("REQUEST", JSON.toString());
							logMap.put("REQ_DATE", head);
							logMap.put("RESPONSE", SOOUtils.toResJsonObject(resSoo).toString());
							logMap.put("RES_DATE", end);
							logMap.put("REQ_SYSTEM", custQryMap.get("EXT_SYSTEM"));
							logMap.put("RES_SYSTEM", "101");
							logMap.put("INTERFACE_CODE", "CUST_QUERY");
							try {
								this.logInterfaceAtomicService.addLogInterface(logMap);
							} catch (AtomicServiceException e) {
								this.logger.info("调用日志新增服务失败！ " + e.getMessage());
							}
						}
					} else {
						SOO failSoo = SOOUtils.createResponseSOO("CUST_QUERY");
						failSoo.put("RESP",
								RESP.createFailResp(Constants.ERROT_INFO_CODE, "报文格式错误，报文中未找到TYPE：CUST_QUERY"));
						return SOOUtils.toResJsonObject(failSoo);
					}
				}
			}
		}
		SOO failSoo = SOOUtils.createResponseSOO("CUST_QUERY");
		failSoo.put("RESP", RESP.createFailResp(Constants.ERROT_INFO_CODE, "报文格式错误，报文中SOO列表为空"));
		return SOOUtils.toResJsonObject(failSoo);
	}

}
