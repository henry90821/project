package com.smilife.bcp.service;

import com.smilife.bcp.dto.common.EQryType;
import com.smilife.bcp.dto.request.CustUpdateReq;
import com.smilife.bcp.dto.response.CustDto;
import com.smilife.bcp.dto.response.LoginRepDto;
import com.smilife.bcp.dto.response.MemberInfoResp;
import com.smilife.bcp.dto.response.ResultDTO;
import com.tydic.eshop.model.order.ReqCrmOrder;

/**
 * CRM接口管理
 * 
 * @author liz 
 * 2015-8-25
 */
public interface UserManageConnector {

	/**
	 * 通过BCP调用CRM进行注册
	 * 
	 * @param account 会员账号
	 * @param password 会员密码
	 * @return 会员ID
	 */
	public String register(String account, String password);

	/**
	 * 通过BCP调用CRM进行账户校验
	 * 
	 * @param account 会员账号
	 * @return CRM返回对象
	 */
	public ResultDTO isExist(String account);

	/**
	 * 通过BCP调用CRM进行会员资料查询
	 * 
	 * @param custId 会员ID
	 * @return 会员资料
	 */
	public MemberInfoResp queryMemberInfo(String custId);
	
	
	/**
	 * 根据查询值的类型来查找会员资料
	 * @param qryType 查询类型
	 * @param qryValue 对应查询类型的值
	 * @return 若查询出错或未找到对应的会员，则返回null。此函数不会抛异常
	 */
	public MemberInfoResp queryMemberInfo(EQryType qryType, String qryValue);
	

	/**
	 * 通过BCP调用CRM进行会员资料修改
	 * 
	 * @param req 会员资料
	 * @return CRM返回对象
	 */
	public ResultDTO updateCust(CustUpdateReq req);

	/**
	 * 通过BCP调用CRM进行密码修改
	 * 
	 * @param custId 会员ID
	 * @param oldPassword 原密码
	 * @param newPassword 新密码
	 * @param resetType 确认密码
	 * @param pwdType 密码类型
	 * @return CRM返回对象
	 */
	public ResultDTO updatePwd(String custId, String oldPassword, String newPassword, String resetType, String pwdType);

	/**
	 * 通过BCP调用CRM进行支付密码校验
	 * 
	 * @param custId 会员ID
	 * @param payPassword 支付密码
	 * @return CRM返回对象
	 */
	public ResultDTO payPwdCheck(String custId, String payPassword);
	
	/**
	 * 通过BCP调用CRM进行订单保存
	 * 
	 * @param reqCrmOrder crm订单对象
	 * @return crm订单号
	 */
	public String saveCrmOrder(ReqCrmOrder reqCrmOrder);
	
	/**
	 * 根据token获取缓存用户对象
	 * @param token
	 * @return
	 */
	public CustDto getSessionUser(String token);

	/**
	 * 缓存订单
	 * @param custId
	 * @param orderForm
	 */
	public ResultDTO saveSessionOrder(String custId, String orderForm);

	/**
	 * 根据custId获取缓存订单对象
	 * @param custId
	 * @return
	 */
	public String getSessionOrder(String custId);
	
	/**
	 * 登录接口
	 * @param loginName
	 * @param password
	 * @return LoginRepDto
	 */
	public LoginRepDto login(String loginName, String password);
	
}
