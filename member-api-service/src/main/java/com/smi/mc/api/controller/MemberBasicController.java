package com.smi.mc.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.mc.api.annotation.LoginAuth;
import com.smi.mc.api.common.ApiBaseController;
import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.service.IMemberInfoService;
import com.smi.mc.api.service.IPayPwdService;
import com.smi.mc.api.utils.RedisUtil;
import com.smi.mc.api.valueobject.CustVo;
import com.smi.mc.api.valueobject.MemberInfoVo;
import com.smi.mc.api.valueobject.enums.CertiTypeEnum;
import com.smi.mc.api.valueobject.enums.LoginTypeEnum;
import com.smi.mc.api.valueobject.enums.PwdTypeEnum;
import com.smi.mc.api.valueobject.enums.SexEnum;
import com.smi.mc.api.valueobject.enums.SmsVerifyCodeEnum;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 会员基础接口服务类<br/>
 * Created by Andriy on 16/9/19.
 */
@RestController
@Api(value = "会员基础服务接口类")
@Validated
public class MemberBasicController extends ApiBaseController {
	private static final Logger LOGGER = LoggerUtils.getLogger(MemberBasicController.class);
	
	@Autowired
	private IPayPwdService payPwdService;
	
	@Autowired
	private IMemberInfoService memberInfoService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;

	/**
	 * 会员登录鉴权接口
	 * (接口逻辑:密码密文加随机数再加密方式进行登录)
	 * @param account
	 *            会员登录账号，当【loginType】字段值为【SMS_VERIFYCODE_LOGIN】时该字段值必须为手机号，其它值则为用户登录名即可
	 * @param password
	 *            会员登录密码，放置于HTTP请求的HEADER中
	 * @param loginType
	 *            会员登录类型<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;【<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MOBILE_LOGIN：手机登录，<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EMAIL_LOGIN：邮箱登录，<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SMS_VERIFYCODE_LOGIN：短信验证码登录<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】
	 * @return
	 */
	@ApiOperation(value = "会员登录鉴权接口")
	@RequestMapping(value = API_VERSION_V1 + "/authorization/{account}", method = RequestMethod.POST)
	public DataVo<MemberInfoVo> authorization(
			@ApiParam(value = "渠道编码", required = true) @RequestHeader(value = "channelCode", required = true) String channelCode,
			@ApiParam(value = "会员登录账号", required = true) @PathVariable(value = "account") String account,
			@ApiParam(value = "会员登录密码（一次MD5加密后密文），放置于HTTP请求的HEADER中", required = false) @RequestHeader(value = "password", required = false) String password,
			@ApiParam(value = "短信验证码(若用户不存在，注册并登录)", required = false) @RequestHeader(value = "verifyCode", required = false) String verifyCode,
			@ApiParam(value = "登录方式<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ACCOUNT_LOGIN：账号登录，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SMS_VERIFYCODE_LOGIN：短信验证码登录<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = true) @RequestParam(value = "pwdType") LoginTypeEnum loginTypeEnum,
			@ApiParam(value = "令牌失效时间（非必填，默认7天）", required = false) @RequestHeader(value = "tokenTimeOut",required = false) Long tokenTimeOut){
		LOGGER.info("当前登录的账户：{}，密码：{}登录类型", account, password);
		if (tokenTimeOut == null || tokenTimeOut == 0) {
			tokenTimeOut = memberCenterConfiguration.getTokenTimeOut();
		}
		DataVo<MemberInfoVo> infoCustVo = memberInfoService.login(channelCode, account, password,verifyCode, loginTypeEnum.getCode(),tokenTimeOut);
		infoCustVo.setInterfaceIdentity("/authorization");
		return infoCustVo;
	}
	
	@ApiOperation(value = "会员登出接口")
	@RequestMapping(value = API_VERSION_V1 + "/logout", method = RequestMethod.POST)
	public BaseValueObject logout(
			@ApiParam(value = "令牌", required = true) @RequestHeader(value = "token") String token){
		LOGGER.info("当前令牌：{}", token);
		BaseValueObject baseValueObject = memberInfoService.logout(token);
		baseValueObject.setInterfaceIdentity("/logout");
		return baseValueObject;
	}

	@CrossOrigin
	@ApiOperation(value = "会员登录密码重置接口")
	@RequestMapping(value = API_VERSION_V1 + "/restLoginPwd", method = {RequestMethod.PUT})
	public BaseValueObject resetPwd(
			@Size(max=11,message="不能输入超过11个字符") @ApiParam(value = "联系号码，最大11个字符）", required = true) @RequestParam(value = "contactNumer") String contactNumer,
			@Size(max=10,message="不能输入超过10个字符") @ApiParam(value = "渠道编码，由会员系统先行分配（最大10个字符）", required = true) @RequestHeader(value = "channelCode") String channelCode,
			@ApiParam(value = "新密码(登录的待修改的新密码，输入参数是Md5密文，参数存放在http的head中)", required = true) @RequestHeader(value = "newPwd") String newPwd,
			HttpServletRequest request) {
		Map<String,Object> reqParam=new HashMap<String,Object>();
		reqParam.put("mobile", contactNumer);
		reqParam.put("channelCode",channelCode);
		reqParam.put("newPwd",newPwd);
		reqParam.put("pwdType",PwdTypeEnum.LOGIN_PWD.getCode());
		BaseValueObject result=this.payPwdService.restLoginPwd(reqParam);
		return result;
	}
	 
	@CrossOrigin
	@LoginAuth
	@ApiOperation(value = "会员登录密码修改接口")
	@RequestMapping(value = API_VERSION_V1 + "/updateLoginPwd", method = {RequestMethod.PUT})
	public BaseValueObject updatePwd(@ApiParam(value = "访问令牌，通过令牌去缓存库查询会员信息", required = true) @RequestHeader(value = "token") String token,
			@Size(max=10,message="不能输入超过10个字符") @ApiParam(value = "渠道编码，由会员系统先行分配（最大10个字符）", required = true) @RequestHeader(value = "channelCode") String channelCode,
			@ApiParam(value = "老密码(登录的初始密码，输入参数是Md5密文，参数存放在http的head中)", required = true) @RequestHeader(value = "oldPwd" ,required = true) String oldPwd,
			@ApiParam(value = "新密码(登录的待修改的新密码，输入参数是Md5密文，参数存放在http的head中)", required = true) @RequestHeader(value = "newPwd") String newPwd,
			HttpServletRequest request) {
		Map<String,Object> reqParam=new HashMap<String,Object>();
		reqParam.put("token",token);
		reqParam.put("channelCode",channelCode);
		reqParam.put("oldPwd",oldPwd);
		reqParam.put("newPwd",newPwd);
		reqParam.put("pwdType",PwdTypeEnum.LOGIN_PWD.getCode());
		BaseValueObject result=this.payPwdService.updateLoginPwd(reqParam);
		return result;
	}
	
	@LoginAuth
	@ApiOperation(value = "验证是否设置了支付密码")
	@RequestMapping(value = API_VERSION_V1 + "/isSetVerifyPayPwd", method = RequestMethod.GET)
	public BaseValueObject isSetVerifyPayPwd(
			@ApiParam(value = "访问令牌，通过令牌去缓存库查询会员信息", required = true) @RequestHeader(value = "token") String token,
			HttpServletRequest request) {
		Map<String,Object> reqParam=new HashMap<String,Object>();
		reqParam.put("token",token);
	    BaseValueObject result=this.payPwdService.judgeIsSetPayPwd(reqParam);
		result.setInterfaceIdentity("/isSetVerifyPayPwd");
		return result;
	}
	
	@CrossOrigin
	@LoginAuth
	@ApiOperation(value = "设置支付密码")
	@RequestMapping(value = API_VERSION_V1 + "/setPayPwd", method = {RequestMethod.PUT})
	public BaseValueObject setPayPwd(@ApiParam(value = "访问令牌，通过令牌去缓存库查询会员信息", required = true) @RequestHeader(value = "token") String token,
			@ApiParam(value = "支付密码(输入参数是会员的支付密码，Md5密文输入，参数存放在http的head中)", required = true) @RequestHeader(value = "payPwd") String payPwd,
			@ApiParam(value = "密码类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LOGIN_PWD：登录密码，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SERVICE_PWD：服务密码（支付密码）<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = true) @RequestParam(value = "pwdType") PwdTypeEnum pwdType,
			HttpServletRequest request) {
		 Map<String,Object> param=new HashMap<String,Object>();
		 param.put("token",token);
		 param.put("payPwd", payPwd);
		 param.put("pwdType", pwdType);
		BaseValueObject result=this.payPwdService.settingPayPwd(param);
		result.setInterfaceIdentity("/setPayPwd");
		return result;
	}   
	
	@LoginAuth
	@ApiOperation(value = "验证支付密码")
	@RequestMapping(value = API_VERSION_V1 + "/verifyPayPwd", method = RequestMethod.GET)
	public DataVo<CustVo> verifyPayPwd(
			HttpServletRequest request,
			@ApiParam(value = "访问令牌，通过令牌去缓存库查询会员信息", required = true) @RequestHeader(value = "token") String token,
			@Size(max=10,message="不能输入超过10个字符") @ApiParam(value = "渠道编码，由会员系统先行分配（最大10个字符）", required = true) @RequestHeader(value = "channelCode") String channelCode,
			@ApiParam(value = "支付密码(输入参数是会员的支付密码，Md5密文输入，参数存放在http的head中)", required = true) @RequestHeader(value = "payPwd") String payPwd,
			@ApiParam(value = "查询方式【1，手机号码；2，会员卡号；3，线上登陆账号；4，会员编码】", required = false) @RequestParam(value = "qryType",required = false) String qryType,
			@ApiParam(value = "用于查询的号码", required = false) @RequestParam(value = "qryNumber",required = false) String qryNumber,
			@ApiParam(value = "密码类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LOGIN_PWD：登录密码，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SERVICE_PWD：服务密码（支付密码）<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = true) @RequestParam(value = "pwdType") PwdTypeEnum pwdType){
		 Map<String,Object> param=new HashMap<String,Object>();
		 param.put("token", token);
		 param.put("channelCode", channelCode);
		 param.put("payPwd", payPwd);
		 param.put("qryType", qryType);
		 param.put("qryNumber", qryNumber);
		 param.put("pwdType", pwdType);
		 DataVo<CustVo> result = this.payPwdService.verifyPayPwd(param);
		 result.setInterfaceIdentity("/verifyPayPwd");
	   	return result;
	}
	
	
	@ApiOperation(value = "会员新增接口")
	@RequestMapping(value = API_VERSION_V1 + "/custAdd", method = RequestMethod.POST)
	public BaseValueObject custAdd(HttpServletRequest request,
			@Size(max=10,message="不能输入超过10个字符") @ApiParam(value = "渠道编码，由会员系统先行分配（最大10个字符）", required = true) @RequestHeader(value = "channelCode") String channelCode,
			@Size(max=11,message="不能输入超过11个字符") @ApiParam(value = "联系电话（最大11个字符）", required = true) @RequestHeader(value = "contactMobile") String contactMobile,
			@ApiParam(value = "登陆密码（一次MD5加密的密文）", required = true) @RequestHeader(value = "loginPwd") String loginPwd,
			@ApiParam(value = "支付密码（一次MD5加密的密文）", required = false) @RequestHeader(value = "payPwd", required = false) String payPwd,
			@Size(max=60,message="不能输入超过60个字符") @ApiParam(value = "会员姓名（最大60个字符）", required = true) @RequestParam(value = "custName") String custName,
			@ApiParam(value = "性别<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MALE：男，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FEMALE：女<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "sex", required = false) SexEnum sex,
			@ApiParam(value = "证件类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IDCARD：身份证，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;OTHER：其他，<br/>&nbsp;&nbsp;&nbsp;&nbsp;ORGCODE：组织机构代码证<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "certiType", required = false) CertiTypeEnum certiType,
			@Size(max=20,message="不能输入超过20个字符") @ApiParam(value = "证件号码（最大20个字符）", required = false) @RequestParam(value = "certiNbr", required = false) String certiNbr,
			@Size(max=200,message="不能输入超过200个字符") @ApiParam(value = "证件地址（最大200个字符）", required = false) @RequestParam(value = "certiAddr", required = false) String certiAddr){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("channelCode", channelCode);
		paramMap.put("systemUserId", channelCode);
		paramMap.put("contactMobile", contactMobile);
		paramMap.put("loginPwd", loginPwd);
		paramMap.put("payPwd", payPwd);
		paramMap.put("custName", custName);
		paramMap.put("sex", sex == null ? null : sex.getCode());
		paramMap.put("certiType", certiType == null ? "" : certiType.getCode());
		paramMap.put("certiNbr", certiNbr);
		paramMap.put("certiAddr", certiAddr);
		BaseValueObject baseValueObject = memberInfoService.custAdd(paramMap);
		baseValueObject.setInterfaceIdentity("/custAdd");
		return baseValueObject;
	}
	
	@LoginAuth
	@ApiOperation(value = "会员资料查询接口")
	@RequestMapping(value = API_VERSION_V1 + "/custQuery", method = RequestMethod.GET)
	public DataVo<MemberInfoVo> custQuery(HttpServletRequest request,@ApiParam(value = "请求令牌，存放会员基本信息", required = true) @RequestHeader(value = "token") String token){
		DataVo<MemberInfoVo> memberInfo = memberInfoService.custQuery(token);
		memberInfo.setInterfaceIdentity("/custQuery");
		return memberInfo;
	}
	
	@CrossOrigin
	@LoginAuth
	@ApiOperation(value = "会员信息修改接口")
	@RequestMapping(value = API_VERSION_V1 + "/custUpdate", method = {RequestMethod.PUT})
	public BaseValueObject custUpdate(HttpServletRequest request, @ApiParam(value = "请求令牌，存放会员基本信息", required = true) @RequestHeader(value = "token") String token,
	@Size(max=10,message="不能输入超过10个字符") @ApiParam(value = "渠道编码，由会员系统先行分配（最大10个字符）", required = true) @RequestHeader(value = "channelCode") String channelCode,
	@Size(max=60,message="不能输入超过60个字符") @ApiParam(value = "会员姓名（最大60个字符）", required = false) @RequestParam(value = "custName", required = false) String custName,
	@Size(max=60,message="不能输入超过60个字符") @ApiParam(value = "会员昵称（最大60个字符）", required = false) @RequestParam(value = "nickName", required = false) String nickName,
	@Size(max=11,message="不能输入超过11个字符") @ApiParam(value = "会员的联系电话（最大11个字符）", required = false) @RequestParam(value = "mobile", required = false) String mobile,
	@Email(message="邮箱输入不合法") @Size(max=50,message="不能输入超过50个字符")@ApiParam(value = "电子邮箱（最大50个字符）", required = false) @RequestParam(value = "email", required = false)  String email,
	@Pattern(regexp="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))",message="生日格式不正确")
	@ApiParam(value = "生日（格式：1990-01-01）", required = false) @RequestParam(value = "birthdate", required = false) String birthdate,
	@ApiParam(value = "性别<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MALE：男，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FEMALE：女<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "sex", required = false) SexEnum sex,
	@Size(max=200,message="不能输入超过200个字符") @ApiParam(value = "联系地址（最大200个字符）", required = false) @RequestParam(value = "contactAddr", required = false) String contactAddr,
	@ApiParam(value = "证件类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IDCARD：身份证，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;OTHER：其他，<br/>&nbsp;&nbsp;&nbsp;&nbsp;ORGCODE：组织机构代码证<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "certiType", required = false) CertiTypeEnum certiType,
	@Size(max=20,message="不能输入超过20个字符") @ApiParam(value = "证件号码（最大20个字符）", required = false) @RequestParam(value = "certiNbr", required = false) String certiNbr,
	@Size(max=200,message="不能输入超过200个字符") @ApiParam(value = "证件地址（最大200个字符）", required = false) @RequestParam(value = "certiAddr", required = false) String certiAddr){
		
		String custId = redisUtil.getInfoCustIdByToken(token);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("channelCode", channelCode);
		paramMap.put("custId", custId);
		paramMap.put("email", email);
		paramMap.put("birthdate", birthdate);
		paramMap.put("mobile", mobile);
		paramMap.put("sex", sex == null ? null : sex.getCode());
		paramMap.put("nickName", nickName);
		paramMap.put("custName", custName);
		paramMap.put("contactAddr", contactAddr);
		paramMap.put("certiType", certiType == null ? "" : certiType.getCode());
		paramMap.put("certiNbr", certiNbr);
		paramMap.put("certiAddr", certiAddr);
		BaseValueObject baseValueObject = memberInfoService.custUpdate(paramMap);
		baseValueObject.setCode(CodeEnum.SUCCESS);
		baseValueObject.setInterfaceIdentity("/custUpdate");
		return baseValueObject;
	}
	
	@ApiOperation(value = "根据账号查询是否注册过星美会员")
	@RequestMapping(value = API_VERSION_V1 + "/isSmiMember", method = RequestMethod.GET)
	public BaseValueObject isSmiMember(HttpServletRequest request,@ApiParam(value = "账号（手机号码或邮箱）", required = true) @RequestParam(value = "loginUser") String loginUser){
		BaseValueObject baseValueObject = new BaseValueObject();
		baseValueObject = memberInfoService.isSmiMember(loginUser);
		return baseValueObject;
	}
	
	@ApiOperation(value = "发送短信验证码接口")
	@RequestMapping(value = API_VERSION_V1 + "/getVerifyCode", method = RequestMethod.GET)
	public BaseValueObject getVerifyCode(HttpServletRequest request,@ApiParam(value = "手机号码", required = true) @RequestParam(value = "mobile") String mobile,
			@ApiParam(value = "验证码类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LOGIN_CODE：登录验证码，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;REGISTER_CODE：注册验证码<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = true) @RequestParam(value = "smsVerifyCodeEnum") SmsVerifyCodeEnum smsVerifyCodeEnum){
		BaseValueObject baseValueObject = new BaseValueObject();
		baseValueObject = memberInfoService.getVerifyCode(mobile,smsVerifyCodeEnum.getCode());
		return baseValueObject;
	}

}
