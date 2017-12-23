package com.smi.mc.service.cust;


import java.util.Map;

import com.smi.mc.vo.cust.InfoCustVo;
import com.smi.mc.vo.cust.InfoLoginUserVo;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface IMemberInfoService {

	
	/**
	 * 根据账号和密码登录
	 * 登录方式：密码+随机数后再进行MD5加密进行验证
	 * @param accountNo
	 * @param pwd
	 * @param loginType
	 * @return
	 * @throws Exception
	 */
	DataVo<InfoCustVo> login(String accountNo, String pwd, String verifiyCode, String loginType,String channelCode) throws Exception;
	
	/**
	 * 会员资料新增
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	BaseValueObject custAdd(Map<String, Object> paramMap) ;
	
	/**
	 * 会员资料修改
	 * @param paramMap
	 * @return
	 */
	BaseValueObject custUpdate(Map<String, Object> paramMap);
	
	/**
	 * 根据custId获取会员信息
	 * @param custId
	 * @return
	 */
	DataVo<InfoCustVo> showCustInfoByCustId(String custId);
	
	/**
	 * <p>Description: 根据手机号查询是否星美会员</p>
	 * <p>Company: SMI</p> 
	 * @author YANGHAILONG
	 * @date 2016年10月13日 上午11:41:47
	 */
	DataVo<InfoLoginUserVo> findInfoLoginUserByMobile(String mobile);
	
	/**
	 * <p>Description: 发送短信验证码</p>
	 * <p>Company: SMI</p> 
	 * @author YANGHAILONG
	 * @date 2016年10月25日 上午10:48:36
	 */
	BaseValueObject getVerifyCode(String mobile,String verifyCode,String codeType);
	
}
