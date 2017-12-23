package com.smi.sms.common;

import java.util.HashMap;
import java.util.Map;

import com.smi.sms.model.SmsAccount;
import com.smi.tools.kits.IoKit;

public class SmsParamsInit {
	/**
	 * 缓存梦网短信发送状态编码和描述，type=1
	 */
	private static Map<String, String> mwReturnCode = null;
	
	/**
	 * 缓存网景短信发送状态编码和描述，type=3
	 */
	private static Map<String, String> wjReturnCode = null;

	/**
	 * 梦网短信账号信息
	 */
	private static Map<Integer, SmsAccount> mwAccountInfo = null;
	
	/**
	 * 网景短信账号信息
	 */
	private static Map<Integer, SmsAccount> wjAccountInfo = null;

	/**
	 * 使用短信的渠道编码，type=2
	 */
	private static Map<String, String> channelCode = null;
	
	/**
	 * 使短信通道编码，type=4
	 */
	private static Map<String, String> companyCode = null;
	
	/**
	 * 短信发送的限制条件
	 */
	private static Map<String, Integer> smsLimit = null;

	/**
	 * 短信发送限制 账号类型
	 */
	public static String smsLimitAccountType = "";
	
	/**
	 * 高峰期通道账户
	 */
	public static String smsPeakPeriodsCompanyType ;
	/**
	 * 高峰期时间段
	 */
	public static String smsPeakPeriods = "";
	
	/**
	 *  非高峰期通道账户
	 */
	public static String smsOffPeakPeriodsCompanyType ;
	

	static {
		smsLimit = new HashMap<String, Integer>();
		Map<String, String> map = IoKit.get("config.properties");

		Integer minutes = Integer.parseInt(map.get(FinalValue.SMS_LIMITED_MINUTES).trim().split(":")[1]);
		Integer minCount = Integer.parseInt(map.get(FinalValue.SMS_LIMITED_MINUTES).trim().split(":")[0]);
		Integer dayCount = Integer.parseInt(map.get("days").trim());
		smsLimitAccountType = map.get(FinalValue.SMS_LIMITED_ACCOUNT_TYPE).trim();
		
		smsPeakPeriodsCompanyType = map.get(FinalValue.PEAK_PERIODS_COMPANY_TYPE).trim();
		smsPeakPeriods = map.get(FinalValue.PEAK_PERIODS).trim();
		smsOffPeakPeriodsCompanyType = map.get(FinalValue.OFF_PEAK_PERIODS_COMPANY_TYPE).trim();
		

		smsLimit.put(FinalValue.SMS_LIMITED_MINUTES, minutes);
		smsLimit.put(FinalValue.SMS_LIMITED_MINUTES_COUNT, minCount);
		smsLimit.put(FinalValue.SMS_LIMITED_DAYS_COUNT, dayCount);
	}

	/**
	 * 梦网短信发送状态编码， key->code， value-> key的描述
	 * 
	 * @return Map<String, String>
	 */
	public static Map<String, String> getMwReturnCodeMap() {

		if (null == mwReturnCode) {
			mwReturnCode = new HashMap<String, String>();
		}
		return mwReturnCode;
	}

	/**
	 * 网景短信发送状态编码， key->code， value-> key的描述
	 * 
	 * @return Map<String, String>
	 */
	public static Map<String, String> getWjReturnCodeMap() {

		if (null == wjReturnCode) {
			wjReturnCode = new HashMap<String, String>();
		}
		return wjReturnCode;
	}
	
	/**
	 * 获取梦网短信账号信息 key->账户类型accountType， value->账号实体
	 * 
	 * @return Map<Integer, SmsAccount>
	 */
	public static Map<Integer, SmsAccount> getMwAccountInfoMap() {

		if (null == mwAccountInfo) {
			mwAccountInfo = new HashMap<Integer, SmsAccount>();
		}
		return mwAccountInfo;
	}
	
	/**
	 * 获取网景短信账号信息 key->账户类型accountType， value->账号实体
	 * 
	 * @return Map<Integer, SmsAccount>
	 */
	public static Map<Integer, SmsAccount> getWjAccountInfoMap() {

		if (null == wjAccountInfo) {
			wjAccountInfo = new HashMap<Integer, SmsAccount>();
		}
		return wjAccountInfo;
	}

	/**
	 * 获取渠道信息，key->code， value-> key的描述
	 * 
	 * @return Map<String, String>
	 */
	public static Map<String, String> getChannelCodeMap() {

		if (null == channelCode) {
			channelCode = new HashMap<String, String>();
		}
		return channelCode;
	}
	
	/**
	 * 获取短信通道信息，key->code， value-> key的描述
	 * 
	 * @return Map<String, String>
	 */
	public static Map<String, String> getCompanyCodeMap() {

		if (null == companyCode) {
			companyCode = new HashMap<String, String>();
		}
		return companyCode;
	}

	/**
	 * 获取短信发送的限制条件， key（minutes，minutes_count，days_count）
	 * 
	 * @return Map<String, Integer>
	 */
	public static Map<String, Integer> getSmsLimitMap() {
		return smsLimit;
	}

	/**
	 * 获取短信发送限制配置的描述信息
	 * 
	 * @return 字符串
	 */
	public static String getLimitConfig() {
		StringBuilder sb = new StringBuilder();

		sb.append(SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_MINUTES));
		sb.append("分钟内，每个号码只能发送");
		sb.append(SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_MINUTES_COUNT));
		sb.append("条短信；");
		sb.append("每天每个号码最多只能发送");
		sb.append(SmsParamsInit.getSmsLimitMap().get(FinalValue.SMS_LIMITED_DAYS_COUNT));
		sb.append("条短信");
		return sb.toString();
	}
}
