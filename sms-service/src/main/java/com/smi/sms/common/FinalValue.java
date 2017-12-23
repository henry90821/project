package com.smi.sms.common;

public final class FinalValue {

	private FinalValue() {
	}

	/**
	 * 短信息发送接口（相同内容群发）,生产
	 */
//	public static final String URL_SMS_SEND_PRODUCT = "http://61.145.229.29:7903/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";

	/**
	 * 短信息发送接口（相同内容群发）,服务账号
	 */
//	public static final String URL_SMS_SEND_SERVICE = "http://61.145.229.28:7902/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";

	public static final String SMS_LIMITED_MINUTES = "minutes";
	public static final String SMS_LIMITED_MINUTES_COUNT = "minutes_count";
	public static final String SMS_LIMITED_DAYS_COUNT = "days_count";
	
	public static final String SMS_LIMITED_ACCOUNT_TYPE = "limit_account_type";
	
	/**
	 * 高峰期通道账户
	 */
	public static final String PEAK_PERIODS_COMPANY_TYPE = "peak_periods_company_type";
	/**
	 * 高峰期时间段
	 */
	public static final String PEAK_PERIODS = "peak_periods";
	
	/**
	 * 非高峰期通道账户
	 */
	public static final String OFF_PEAK_PERIODS_COMPANY_TYPE = "off_peak_periods_company_type";
	
	/**
	 * 非高峰期时间段
	 */
	public static final String OFF_PEAK_PERIODS = "off_peak_periods";
	
	/**
	 * 查询分页大小
	 */
	public static final int PAGE_SIZE = 15;
	
	/**
	 * 生产类账号
	 */
	public static final int ACCOUNT_TYPE_PRODUCT = 1;
	/**
	 * 服务类账号
	 */
	public static final int ACCOUNT_TYPE_SERVICE = 2;
	
	/**
	 * 梦网通道类型
	 */
	public static final String COMPANY_TYPE_MW = "1";
	/**
	 * 网景通道类型
	 */
	public static final String COMPANY_TYPE_WJ = "2";
	
	
	/**
	 * 字典类型： 1 代表 为 短信发送 梦网的返回代码
	 */
	public static final int DICT_TYPE_RETURN_CODE_MW = 1;
	
	/**
	 * 字典类型： 2 代表 使用短信服务的渠道编码
	 */
	public static final int DICT_TYPE_CHANNEL = 2;
	/**
	 * 字典类型： 3 代表 为 短信发送 网景的返回代码
	 */
	public static final int DICT_TYPE_RETURN_CODE_WJ = 3;
	
	/**
	 * 字典类型： 4 代表 为 短信服务的通道编码
	 */
	public static final int DICT_TYPE_COMPANY = 4;
	
	/**
	 * 短信发送队列大小
	 */
	public static final int QUEUE_SIZE = 100; 
	
	/**
	 * 随机字符串的长度
	 */
	public static final int RANDOM_STR_LENGTH = 5;

	/**
	 * 待发送
	 */
	public static final String SEND_STATUS_READY = "READY";
	/**
	 * 发送成功
	 */
	public static final String SEND_STATUS_SUECCESS = "SUECCESS";
	/**
	 * 发送失败
	 */
	public static final String SEND_STATUS_FAIL = "FAIL";
	
	/**
	 * 发送拒绝
	 */
	public static final String SEND_STATUS_REJECT = "REJECT";
	
	
	public class RtnCode {
		
		/**
		 * 请求被正确接收
		 */
		public static final String RTN_REC_OK = "22";
		
		/**
		 * 拒绝（超过短信发送的限制条件/渠道编码不存在）
		 */
		public static final String SEND_STATUS_REJECT = "3";
		
	}
}
