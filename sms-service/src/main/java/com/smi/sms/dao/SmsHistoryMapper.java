package com.smi.sms.dao;

import java.util.List;

import com.smi.sms.model.SmsHistory;
import com.smi.sms.model.query.PageQuery;
import com.smi.sms.model.query.QueryResult;

public interface SmsHistoryMapper {

	/**
	 * 保持短信
	 * 
	 * @param sms
	 *            發送短信對象
	 * @return 影響行數
	 */
	int save(SmsHistory sms);

	/**
	 * 根据请求序列号 更新短信内容
	 * 
	 * @param reqNo
	 *            请求序列号
	 * @return 影响行数
	 */
	int updateByReqNo(SmsHistory sms);

	/**
	 * 根据请求序列号查询
	 * 
	 * @param reqNo
	 *            请求序列号
	 * @return 短信对象
	 */
	SmsHistory loadByReqNo(String reqNo);

	/**
	 * 更加手机号码查询
	 * 
	 * @param phoneNo
	 *            手机号码
	 * @return 短信列表
	 */
	List<SmsHistory> listByPhoneNo(String phoneNo);
	
	/**
	 * 分页查询
	 * @param query 查询对象
	 * @return 查询结果
	 */
	List<QueryResult> listAll(PageQuery query);

}