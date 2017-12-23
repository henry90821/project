package com.smi.mc.controller.bill;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.smi.mc.service.busi.bill.ISmsInfoService;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * bill接口实例<br/>
 * Created by Andriy on 16/5/20.
 */
@RestController
@RequestMapping(value = "/bill/smsInfoInsert")
/*@Api(value = "bill接口实例")*/
@ApiIgnore
public class SmsInfoController extends BaseController {
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private ISmsInfoService smsInfoService;

	@ApiOperation(value = "查询会员信息")
	@RequestMapping(value = "/smsInsert", method = RequestMethod.POST)
	public int smsInsert(
		@ApiParam(value = "MSG_ID") @PathVariable(value = "MSG_ID") String MSG_ID,
		@ApiParam(value = "MSISDN_SEND") @PathVariable(value = "MSISDN_SEND") String MSISDN_SEND,
		@ApiParam(value = "MSISDN_RECEIVE") @PathVariable(value = "MSISDN_RECEIVE") String MSISDN_RECEIVE,
		@ApiParam(value = "PRIORITY") @PathVariable(value = "PRIORITY") String PRIORITY,
		@ApiParam(value = "MESSAGE_TEXT") @PathVariable(value = "MESSAGE_TEXT") String MESSAGE_TEXT,
		@ApiParam(value = "RETRY_TIMES") @PathVariable(value = "RETRY_TIMES") String RETRY_TIMES
		) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("MSG_ID", MSG_ID);
		map.put("MSISDN_SEND",MSISDN_SEND);
		map.put("MSISDN_RECEIVE", MSISDN_RECEIVE);
		map.put("PRIORITY", PRIORITY);
		map.put("MESSAGE_TEXT", MESSAGE_TEXT);
		map.put("RETRY_TIMES", RETRY_TIMES);
		int result = -1;
		try {
			result = smsInfoService.smsInsert(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
