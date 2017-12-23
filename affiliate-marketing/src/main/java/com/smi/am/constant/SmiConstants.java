package com.smi.am.constant;

public class SmiConstants {

	public static final String ADMIN_PATH = "/admin";// 系统登录验证路径

	public static final String USERNAME = "username";// session的key
	
	public static final String USERNAME_REDIS_KEY = "am:username:";//redis存放sessionId的key
	
	public static final String SESSION_REDIS_KEY = "spring:session:sessions:";//redis存放session的key
	
	public static final String CHANNEL_REDIS_KEY = "am:channel:";//redis存放门店的key

	public static final String SESSION_KEY_SECURITY_CODE = "securityCode";// 验证码key

	/********************* 审核状态 start *********************************/
	/**
	 * 1:未提交,2:待审核,3:未通过,4:审核通过,5:已发放,6:已过期
	 * 未提交
	 */
	public static final Integer UNCOMMITTED = 1;

	/**
	 * 审核中
	 */
	public static final Integer INAUDIT = 2;

	/**
	 *  未通过
	 */
	public static final Integer FAILED = 3;

	/**
	 * 待发放
	 */
	public static final Integer STAYOUT = 4;

	/**
	 * 已发放
	 */
	public static final Integer HANDEDOUT = 5;
	
	/**
	 * 已过期
	 */
	public static final Integer EXPIRED = 6;
	/**
	 * 确认上线
	 */
	public static final Integer CONFIRMONLINE = 7;
	
	/********************* 审核状态 end *********************************/

	/********************* 优惠券使用状态 start *********************************/
	public static final Integer UNCLAIMED = 1;// 未领取

	public static final Integer UNUSED = 2;// 未使用

	public static final Integer USED = 3;// 已使用
	/********************* 优惠券使用状态 end *******************************/

	/********************* 渠道编码start *******************************/
	public static final Integer MEMBERCARD = 1;// 实体会员卡

	public static final Integer APP = 2;// APP

	public static final Integer WECHAT = 3;// 微信H5
	/********************* 渠道编码 end *******************************/

	/********************* 是否永久有效 start *******************************/
	public static final Integer NEVEREXPIRES = 1;// 永久有效

	public static final Integer EXPIRES = 2;// 非永久爱有效
	/********************* 是否永久有效 end *******************************/

	/********************* 使用渠道 start *******************************/
	public static final Integer ONLINE = 1;// 线上

	public static final Integer OFFLINE = 2;// 线下
	/********************* 使用渠道 end *******************************/

	/********************* 删除状态 *******************************/
	public static final Integer NOREMOVE = 0; // 未删除状态

	public static final Integer REMOVE = 1; // 删除状态

	/********************* 卷包类型 *******************************/

	public static final String GIFT_PACKAGE_TYPE = "1";// 礼包类型

	public static final String COUPONS_TYPE = "2";// 优惠券类型
	
	
	/**
	 * 账户发放
	 */
	public static final Integer ACCOUNT_ISSUE = 1;
	
	/**
	 * 用户领取
	 */ 
	public static final Integer USER_RECEIVE = 2;// 优惠券类型
	
	/**
	 * 星美总部
	 */
	public static final Integer SMI_HEADER=5; 
	
	/**
	 * 星美生活渠道
	 */
	public static final Integer SMILIFE_CHANNEL=1;
	
	/**
	 * 满天星渠道
	 */
		
	 public static final Integer MTX_CHANNLE=2;
	
	/**
	 * 用户默认密码
	 */

	public static final String DEFAULT_PWD="smi123456"; 
	
	/**
	 * 电渠号编码
	 */
	public static final String DIANQUCODE="10002"; 
	
	/**
	 * 电渠号编码名称
	 */
	public static final String DIANQUCODE_NAME="电渠"; 
}
