package com.smi.sms.common;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.smi.tools.lang.DateTime;

public class SendLimitCache {

	private static Logger logger = Logger.getLogger(SendLimitCache.class);
	private SendLimitCache() {
	}

	// 将短信发送的记录缓存到map中， 以便进行限制操作
	private static ConcurrentHashMap<String, SmsLimits> sendCache = new ConcurrentHashMap<String, SmsLimits>();

	public static ConcurrentHashMap<String, SmsLimits> getSendCache() {
		return sendCache;
	}
	
	/**
	 * 清空缓存
	 */
	public static void cleanCache() {
		sendCache = new ConcurrentHashMap<String, SmsLimits>();
	}

	/**
	 * 生成缓存key
	 * 
	 * @param accountType
	 *            账户类型
	 * @param phone
	 *            手机号码
	 * @return key
	 */
	private static String getCacheKey(int accountType, String phone) {
		return phone + "_" + accountType;
	}

	public static boolean isLimited(int accountType, String phone) {
		String key = getCacheKey(accountType, phone);
		logger.debug("cache key: " + key);
		if (sendCache.containsKey(key)) {
			// 超出了每天的限制次数
			logger.debug("每天已发送：" + sendCache.get(key).getDaysCount() + "，系统限制：" + SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_DAYS_COUNT));
			if (sendCache.get(key).getDaysCount() >= SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_DAYS_COUNT)) {
				return true;
			}
			// 超出了 分钟数限制次数 (时间范围内)
			logger.debug("时间差：" + sendCache.get(key).getDiffMinutes());
			if (sendCache.get(key).getDiffMinutes() <= SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_MINUTES)) {
				if (sendCache.get(key).getMinutesCount() >= SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_MINUTES_COUNT)) {
					return true;
				} else {
					sendCache.get(key).setDaysCount(sendCache.get(key).getDaysCount() + 1);
					sendCache.get(key).setMinutesCount(sendCache.get(key).getMinutesCount() + 1);
				}
			} else {  // 时间范围外
				sendCache.get(key).setMinutesCount(1);
				sendCache.get(key).setMinutesStartTime(new DateTime());
				sendCache.get(key).setDaysCount(sendCache.get(key).getDaysCount() + 1);
			}
			return false;
		} else { 
			SmsLimits limit = new SmsLimits();
			limit.setDaysCount(1);
			limit.setMinutesCount(1);
			limit.setPhoneNo(phone);
			limit.setMinutesStartTime(new DateTime());
			sendCache.put(key, limit);
			return false;
		}

	}

}
