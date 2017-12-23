package com.smi.mc.api.service;


import java.util.Map;

import com.smi.mc.api.valueobject.MemberInfoVo;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface IMemberInfoService {

	/**
	 * 登录接口，调用会员中心接口校验用户名和密码(支持手机验证码登录方式)
	 * @param channelCode 渠道编码
	 * @param accountNo 账号
	 * @param pwd 密码
	 * @param verifyCode 短信验证码
	 * @param loginType 登录类型
	 * @param tokeTimeOut token失效时间
	 * @return
	 */
	DataVo<MemberInfoVo> login(String channelCode,String accountNo, String pwd, String verifyCode, String loginType,Long tokeTimeOut) ;
	
	/**
	 * 登出接口：删除redis缓存key=token的会员信息
	 * @param token
	 * @return
	 */
	BaseValueObject logout(String token);
	
	/**
	 * 会员资料查询接口
	 * @param token 令牌
	 * @return
	 */
	DataVo<MemberInfoVo> custQuery(String token);
	
	/**
	 * 会员资料新增接口
	 * @param paramMap
	 * @return
	 */
	BaseValueObject custAdd(Map<String, Object> paramMap);
	
	/**
	 * 会员资料修改接口
	 * @param paramMap
	 * @return
	 */
	BaseValueObject custUpdate(Map<String, Object> paramMap);
	
	/**
	 * <p>Description: 根据账号查询是否注册过星美会员</p>
	 * <p>Company: SMI</p> 
	 * @author YANGHAILONG
	 * @date 2016年10月13日 下午2:27:13
	 */
	BaseValueObject isSmiMember(String loginUser);
	
	/**
	 * <p>Description: 发送短信验证码</p>
	 * <p>Company: SMI</p> 
	 * @author YANGHAILONG
	 * @date 2016年10月25日 下午4:55:41
	 */
	BaseValueObject getVerifyCode(String mobile,String codeType);
}
