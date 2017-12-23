package com.smi.mc.service.external.bill;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.po.SOO;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;
import com.smi.mc.service.busi.bill.BalQryService;
import com.smi.mc.service.external.bill.impl.BalQryExtServ;
import com.smi.mc.utils.SOOUtils;
import com.smilife.core.exception.SmiBusinessException;

/**
 * 百度余额查询对外服务类
 * @author smi
 *
 */
@Service("balQryExtServ")
public class BalQryExtServImpl implements BalQryExtServ {
	@Autowired
	private BalQryService balQryService;

	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 百度会员余额查询
	 */
	@Override
	public JSONObject baiduBalQuery(JSONObject input) {

		this.logger.info("百度余额查询传入参数" + input.toString());
		Date head = new Date();
		@SuppressWarnings("unchecked")
		Map<String, Object> reqMap = JSONObject.fromObject(input);
		SOO soo = SOOUtils.createResponseSOO("BAIDU_BALANCE_QRY");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sooList = (List<Map<String, Object>>) reqMap.get("SOO");
		Map<String, Object> sooMap=sooList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, Object> balQryReq = (Map<String, Object>) sooMap.get("BALANCEQRY_REQ");
		try {
			Map<String, Object> map = this.balQryService.baiduBalQuery(balQryReq);
			soo.put("BALANCEQRY_RES", map);
		} catch (Exception e) {
			this.logger.error("获取数据异常", e);
			throw new SmiBusinessException("999", "获取数据异常");
		} finally {
			Date end = new Date();
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("REQUEST", input.toString());
			logMap.put("REQ_DATE", head);
			logMap.put("RESPONSE", SOOUtils.toResJsonObject(soo).toString());
			logMap.put("RES_DATE", end);
			logMap.put("REQ_SYSTEM", balQryReq.get("SYSTEM_ID"));
			logMap.put("RES_SYSTEM", "101");
			logMap.put("INTERFACE_CODE", "BAIDU_BALANCE_QRY");
			try {
				this.logInterfaceAtomicService.addLogInterface(logMap);
			} catch (AtomicServiceException e) {
				this.logger.error("接口日志新增异常", e);
			}
		}
		return SOOUtils.toResJsonObject(soo);
	}

}
