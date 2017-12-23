package com.smi.mc.constants;

public class Constants {

	public static final String STATE_CODE_SUCC = "0";

	public static final String STATE_MSG_SUCC = "成功";

	public static final String STATE_CODE_FAIL = "1";

	public static final String EXCEPTION_CODE = "999";

	public static final String ERROT_INFO_CODE = "998";

	public static final String EXCEPTION_CODE_ONE = "1";
	public static final String EXCEPTION_CODE_TWO = "2";
	public static final String EXCEPTION_CODE_THREE = "3";
	public static final String EXCEPTION_CODE_FOUR = "4";
	public static final String EXCEPTION_CODE_FIVE = "5";
	public static final String EXCEPTION_CODE_SIX = "6";
	public static final String EXCEPTION_CODE_SEVEN = "7";

	public static final String ERROT_CODE_NULL = "4";

	public static final String DEF_SPACE_STR = "";

	public static final String BILL_DATASOURCE = "billDataSource";

	public static final String BAIDU_BIND = "baidu.api.bind";

	public static final String BAIDU_QUERY = "baidu.api.info";

	public static final String BAIDU_CREATE = "baidu.api.createcard";

	public static final String ORDER_PAYMENT_LOSE = "10";

	public static final String ORDER_PAYMENT_EXCEPTION = "14";

	public static final String ORDER_PAY_LOSE = "11";

	public static final String ORDER_HAVED = "2";

	public static final String ORDER_PAY_STATUS_HAVED = "1";

	public static final String ORDER_STATUS_COMPLETED = "1000";

	public static final String CUST_STATUS_EFFECTIVE = "1000";

	public static final String CUST_STATUS_STAY = "1098";

	public static final String CUST_STATUS_INVALID = "1100";

	public static final String CUST_STATUS_FROZEN = "1198";

	public static final String RULE_CHECK_TYPE = "BUSI_RULE_CHECK";

	public static final String PARAM_TYPE_JSON = "10";

	public static final String PARAM_TYPE_SQL = "20";

	public static final String PARAM_TYPE_CONSTANT = "30";

	public static final String SQL_REL_OBJTYPE_EXP = "10";

	public static final String SQL_PARAM_TYPE_IN = "IN";

	public static final String SQL_PARAM_TYPE_OUT = "OUT";

	public static final String RULE_CHECK_CHAROPR_EQUAL = "=";

	public static final String RULE_CHECK_CHAROPR_UNEQUAL = "!=";

	public static final String RULE_CHECK_CHAROPR_GREATER = ">";

	public static final String RULE_CHECK_CHAROPR_LESS = "<";

	public static final String RULE_CHECK_CHAROPR_GREATER_EQUAL = ">=";

	public static final String RULE_CHECK_CHAROPR_LESS_EQUAL = "<=";

	public static final String PAYMENT_TYPE_MONEY = "10";

	public static final String SYSTEM_PARA_TYPE = "OrderStatusFeeFlag";

	public static final String MEMBERSHIP_NO_PHONE = "100";

	public static final String BATCH_MEMBERSHIP_PHONE = "111";

	public static final String OLD_MEMBERSHIP_NO_PHONE = "101";

	public static final String CUST_BUSI_STATUS_CD_PEND = "1098";

	public static final String CUST_BUSI_STATUS_CD_VALID = "1000";

	public static final String CUST_BUSI_STATUS_CD_INVALID = "1100";

	public static final String COL_CINEMA_NEW_FLAG_VALID = "0";

	public static final String COL_CINEMA_NEW_FLAG_INVALID = "1";

	public static final String CARD_TYPE_ENTITY = "10";

	public static final String CARD_TYPE_VIRTUAL = "20";

	public static final String SMI_SOURCE_NEW = "0";

	public static final String SMI_SOURCE_OLD = "1";

	public static final String SMI_SOURCE_BAIDU = "2";

	public static final String CERTI_TYPE_IDENT_CARD = "10";

    public static final String PAY_ID_HEAD="22"; //生成pay_id的头数字
    
    public static final String CUST_ID_HEAD="32"; //生成cust_id的头数字

    public static final String SMI_BUSI_OFFER="1001"; //星美生活会员业务
    
    public static final String SMS_LOGIN_VERIFYCODE="sms:login:verifycode:"; //登录短信验证码存redis的key前缀
    
    public static final String SMS_REGISTER_VERIFYCODE="sms:register:verifycode:"; //注册短信验证码存redis的key前缀
}
