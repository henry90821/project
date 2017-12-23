package com.smi.sms.service;

import com.github.pagehelper.PageInfo;
import com.smi.sms.common.ReturnMsg;
import com.smi.sms.model.SmsHistory;
import com.smi.sms.model.query.QueryResult;

public interface ISmsHistoryService {

	/**
	 * 保持原始的请求信息
	 * @param sms
	 */
	public void save(SmsHistory sms);
	
	/**
	 * 更新发送状态
	 * @param sms
	 */
	public void updateSendStatus(SmsHistory sms);
	
	/**
	 * 發送短信
	 * @param channel 渠道
	 * @param content 內容
	 * @param phones 接收手機號碼
	 */
	public ReturnMsg sendMsg( String channel, String content, String phones,String company);
	
	/**
	 * 分页查询数据
	 * @param start 起始页码
	 * @param pageSize 每页数据个数
	 * @param reqNo 请求序列号
	 * @param phoneNo 接收的手机号码
	 * @param statusCode 发送状态
	 * @param startDate 查询发送开始时间
	 * @param endDate 查询发送结束时间
	 * @param channel 渠道
	 * @return 分页数据
	 */
	public PageInfo<QueryResult> listHistory(Integer start, Integer pageSize, String reqNo, String phoneNo, String statusCode, String startDate, String endDate, String channel);
}
