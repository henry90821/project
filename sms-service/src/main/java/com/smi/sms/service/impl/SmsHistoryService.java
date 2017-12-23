package com.smi.sms.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.sms.common.FinalValue;
import com.smi.sms.common.ReturnMsg;
import com.smi.sms.common.SendLimitCache;
import com.smi.sms.common.SmsParamsInit;
import com.smi.sms.common.SmsQueue;
import com.smi.sms.dao.SmsHistoryMapper;
import com.smi.sms.model.SmsHistory;
import com.smi.sms.model.query.PageQuery;
import com.smi.sms.model.query.QueryResult;
import com.smi.sms.service.ISmsHistoryService;
import com.smi.tools.kits.DateKit;
import com.smi.tools.kits.StrKit;
import com.smi.tools.lang.DateTime;

@Service("smsHistoryService")
public class SmsHistoryService implements ISmsHistoryService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	SmsHistoryMapper smsHistoryMapper;

	@Override
	public void save(SmsHistory sms) {
		smsHistoryMapper.save(sms);
	}

	@Override
	public void updateSendStatus(SmsHistory sms) {
		smsHistoryMapper.updateByReqNo(sms);
	}

	@Override
	public ReturnMsg sendMsg( String channel, String content, String phone,String company) {
		ReturnMsg msg = new ReturnMsg();
		// 生成序列号 yyyyMMddHHmmssSSS + 5位大写随机数
		String reqno = DateKit.format(new Date(), DateKit.NORM_DATETIME_MS_PATTERN_EXT)
				+ StrKit.getRandomStr(FinalValue.RANDOM_STR_LENGTH, true);
		msg.setReqNo(reqno);

		SmsHistory sms = new SmsHistory();
		sms.setCompanyType(company);
		sms.setChannel(channel);
		sms.setContent(content);
		sms.setPhoneNo(phone);
		sms.setCreateTime(new DateTime());
		sms.setReqNo(reqno);
		sms.setStatusCode(FinalValue.SEND_STATUS_READY);
		sms.setSendTime(new DateTime());
		
		// 0检查手机号码格式是否正确：末尾不能有逗号
		if (phone.lastIndexOf(",") == phone.length() - 1) {
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("手机格式不正确，末尾不能有逗号！");
			// 设置状态
			sms.setStatusCode(FinalValue.SEND_STATUS_REJECT);
			sms.setNote(msg.getMsg());
			// 保存数据
			smsHistoryMapper.save(sms);
			logger.error(msg.getMsg());
			return msg;
		}else{
			String[] phones=phone.split(",");
			int count=phones.length;
			sms.setMobiCount(count);
			if(count>1){
				sms.setAccountType(FinalValue.ACCOUNT_TYPE_SERVICE);//号码个数大于1，使用客服群发
			}else{
				sms.setAccountType(FinalValue.ACCOUNT_TYPE_PRODUCT);//使用生产类账户
			}
		}
		
		// 检查短信通道是否存在
		if(StrKit.isNotBlank(company)){
			if(!SmsParamsInit.getCompanyCodeMap().containsKey(company)){
				msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
				msg.setMsg("通道编码[" + company + "]不存在，请确认！");
				// 设置状态
				sms.setStatusCode(FinalValue.SEND_STATUS_REJECT);
				sms.setNote(msg.getMsg());
				// 保存数据
				smsHistoryMapper.save(sms);
				logger.error(msg.getMsg());
				return msg;
			}
		}

		// 1 检查渠道编码是否存在
		if (!SmsParamsInit.getChannelCodeMap().containsKey(channel)) {
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("渠道编码[" + channel + "]不存在，请确认！");
			// 设置状态
			sms.setStatusCode(FinalValue.SEND_STATUS_REJECT);
			sms.setNote(msg.getMsg());
			// 保存数据
			smsHistoryMapper.save(sms);
			logger.error(msg.getMsg());
			return msg;
		}
		// 2 检查 发送是否超限
		if (SmsParamsInit.smsLimitAccountType.contains(String.valueOf(sms.getAccountType()))) {
			if (SendLimitCache.isLimited(sms.getAccountType(),phone)) {
				msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
				msg.setMsg("发送次数超限:" + SmsParamsInit.getLimitConfig());
				// 设置状态
				sms.setStatusCode(FinalValue.SEND_STATUS_REJECT);
				sms.setNote(msg.getMsg());
				// 保存数据
				smsHistoryMapper.save(sms);
				logger.error(msg.getMsg());
				return msg;
			}
		} 
		// 3 将短信发送请求序列号放入到队列中
		msg.setCode(FinalValue.RtnCode.RTN_REC_OK);
		msg.setMsg("短信发送请求被接收");
		
		sms.setNote(msg.getMsg());
		smsHistoryMapper.save(sms);
		
		SmsQueue.offerElement(sms);

		return msg;
	}

	@Override
	public PageInfo<QueryResult> listHistory(Integer start, Integer pageSize,  String reqNo, String phoneNo, String statusCode, String startDate, String endDate, String channel) {
		if (start == null || start.intValue() <= 0) {
			start = 1;
		}
		if (pageSize == null || pageSize.intValue() <= 0) {
			pageSize = FinalValue.PAGE_SIZE;
		}
		PageHelper.startPage(start, pageSize);
		
		PageQuery query = new PageQuery();
		if (StrKit.isNotBlank(startDate)) {
			query.setStartDate(DateKit.parse(startDate));
		} 
		if (StrKit.isNotBlank(endDate)) {
			query.setEndDate(DateKit.parse(endDate));
		} 
		if(StrKit.isNotBlank(reqNo)){
			query.setReqNo(reqNo);
		}
		if(StrKit.isNotBlank(phoneNo)){
			query.setPhoneNo(phoneNo);
		}
		if(StrKit.isNotBlank(statusCode)){
			query.setStatusCode(statusCode);
		}
		if(StrKit.isNotBlank(channel)){
			query.setChannel(channel);
		}
		
		List<QueryResult> list = smsHistoryMapper.listAll(query);
		
		PageInfo<QueryResult> page = new PageInfo<QueryResult>(list);
		
		return page;
	}

	
}
