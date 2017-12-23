package com.smi.sms.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.smi.sms.common.ReturnMsg;
import com.smi.sms.common.SmsParamsInit;
import com.smi.sms.common.SmsQueue;
import com.smi.sms.model.SmsHistory;
import com.smi.sms.model.query.QueryResult;
import com.smi.sms.service.ISmsHistoryService;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/sms")
public class SmsContorller {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	ISmsHistoryService smsHistoryService;

	@ApiOperation(value = "短信发送接口接口", response = ReturnMsg.class)
	@RequestMapping(value ="/send" ,method = { RequestMethod.POST })
	@ResponseBody
	public ReturnMsg sendMsg(
			@ApiParam(name = "phone", value = "手机号码", required = true) @RequestParam(value = "phone", required = true) String phone,
			@ApiParam(name = "content", value = "短信内容", required = true) @RequestParam(value = "content", required = true) String content,
			@ApiParam(name = "channel", value = "渠道ID(星美商城:101<br/>爱星美:102<br/>星美生活2.0:103<br/>大数据:104<br/>活动服务:105)", required = true) @RequestParam(value = "channel", required = true) String channel,
			@ApiParam(name = "company", value = "通道类型( 1-梦网       2-网景互动)，不传则按高峰期时段计算", required = false) @RequestParam(value = "company", required = false) String company) {

		ReturnMsg msg = new ReturnMsg();

		logger.info("接收到的请求信息为=>[ Content:" + content + ", phones:" + phone + ", channel:" + channel + ",company:" + company);

		logger.debug(SmsParamsInit.getLimitConfig());

		// 1 将接收的到的请求存入数据库（无论什么情况，都会存储数据）
		msg = smsHistoryService.sendMsg(channel, content, phone, company);

		return msg;
	}

	@ApiOperation(value = "短信发送历史接口")
	@RequestMapping(value ="/gethistory" ,method = { RequestMethod.POST })
	@ResponseBody
	public Object getSendHistory(
			@ApiParam(name = "start", value = "起始页，默认为1", required = false) @RequestParam(value = "start", required = false) Integer start,
			@ApiParam(name = "pageSize", value = "页数大小，默认为15", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(name = "reqNo", value = "请求序列号", required = false) @RequestParam(value = "reqNo", required = false) String reqNo,
			@ApiParam(name = "phoneNo", value = "手机号码", required = false) @RequestParam(value = "phoneNo", required = false) String phoneNo,
			@ApiParam(name = "statusCode", value = "状态码", required = false) @RequestParam(value = "statusCode", required = false) String statusCode,
			@ApiParam(name = "startDate", value = "开始时间", required = false) @RequestParam(value = "startDate", required = false) String startDate,
			@ApiParam(name = "endDate", value = "结束时间", required = false) @RequestParam(value = "endDate", required = false) String endDate,
			@ApiParam(name = "channel", value = "渠道编码", required = false) @RequestParam(value = "channel", required = false) String channel) {
			
		PageInfo<QueryResult> page = smsHistoryService.listHistory(start, pageSize, reqNo, phoneNo, statusCode, startDate,
				endDate, channel);
		return page;
	}

	@ApiOperation(value = "获取队列接口")
	@RequestMapping(value ="/getQueue" ,method = { RequestMethod.POST })
	@ResponseBody
	public Object queryQueue() {
		List<SmsHistory> list = new ArrayList<SmsHistory>();
		list = SmsQueue.getQueueAsList();
		return list;
	}
}
