package com.smi.mc.controller.cust;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.mc.service.cust.IMemberInfoService;
import com.smi.mc.vo.cust.InfoCustVo;
import com.smi.mc.vo.cust.InfoLoginUserVo;
import com.smi.tools.kits.RandomKit;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/inside")
@Api(value = "会员基础服务接口实例")
public class MemberInfoController extends BaseController {

	private static final Logger LOGGER = LoggerUtils.getLogger(MemberInfoController.class);
	
	@Autowired
	private IMemberInfoService memberInfoService; 
	
	@ApiOperation(value = "会员登录接口")
	@RequestMapping(value = "/memberInfo/checkLoginAccountAndPwd", method = RequestMethod.POST)
	public DataVo<InfoCustVo> checkLoginAccountAndPwd(
			@ApiParam(value = "渠道编码", required = true) @RequestParam(value = "channelCode", required = true) String channelCode,
			@ApiParam(value = "会员登录账号", required = true) @RequestParam(value = "account") String account,
			@ApiParam(value = "密码(前端加密)", required = false) @RequestParam(value = "password", required = false) String password,
			@ApiParam(value = "短信验证码(若用户不存在，注册并登录)", required = false) @RequestParam(value = "verifyCode", required = false) String verifyCode,
			@ApiParam(value = "会员登录类型(3:手机验证码方式登录,其他类型是账号密码登录)", required = true) @RequestParam(value = "loginType") String loginType) throws Exception {
		LOGGER.info("会员登录新接口,/inside/memberInfo/checkLoginAccountAndPwd: start...");
		DataVo<InfoCustVo> infoCust = memberInfoService.login(account, password,verifyCode, loginType,channelCode);
		infoCust.setInterfaceIdentity("/inside/memberInfo/checkLoginAccountAndPwd");
		return infoCust;
	}

	@ApiOperation(value = "会员资料新增接口")
	@RequestMapping(value = "/memberInfo/custAdd", method = RequestMethod.POST)
	public BaseValueObject custAdd(@ApiParam(value = "渠道编码", required = true) @RequestParam(value = "channelCode") String channelCode,
			@ApiParam(value = "系统编码", required = false) @RequestParam(value = "extSystem", required = false) String extSystem,
			@ApiParam(value = "员工号", required = false) @RequestParam(value = "systemUserId", required = false) String systemUserId,
			@ApiParam(value = "联系电话", required = true) @RequestParam(value = "contactMobile") String contactMobile,
			@ApiParam(value = "登陆密码", required = true) @RequestParam(value = "loginPwd") String loginPwd,
			@ApiParam(value = "支付密码", required = true) @RequestParam(value = "payPwd") String payPwd,
			@ApiParam(value = "会员姓名", required = true) @RequestParam(value = "custName") String custName,
			@ApiParam(value = "性别性别<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MALE：男，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FEMALE：女<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "sex", required = false) String sex,
			@ApiParam(value = "证件类型证件类型<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IDCARD：身份证，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;OTHER：其他，<br/>&nbsp;&nbsp;&nbsp;&nbsp;ORGCODE：组织机构代码证<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "certiType", required = false) String certiType,
			@ApiParam(value = "证件号码", required = false) @RequestParam(value = "certiNbr", required = false) String certiNbr,
			@ApiParam(value = "证件地址", required = false) @RequestParam(value = "certiAddr", required = false) String certiAddr) throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CHANNEL_CODE", channelCode);
		paramMap.put("EXT_SYSTEM", StringUtils.isEmpty(extSystem) ? channelCode : extSystem);
		paramMap.put("SYSTEM_USER_ID", systemUserId);
		paramMap.put("LOGIN_USER", contactMobile);
		paramMap.put("LOGIN_PWD", loginPwd);
		paramMap.put("PAY_PWD", payPwd);
		paramMap.put("SMI_SOURCE", "0");
		paramMap.put("SEX", sex);
		paramMap.put("CUST_NAME", custName);
		paramMap.put("LOGIN_USER_TYPE", "1000");
		paramMap.put("certiType", certiType);
		paramMap.put("certiNbr", certiNbr);
		paramMap.put("certiAddr", certiAddr);
		
		BaseValueObject baseValueObject = memberInfoService.custAdd(paramMap);
        baseValueObject.setInterfaceIdentity("/inside/memberInfo/custAdd");
		return baseValueObject;
	}
	
	@ApiOperation(value = "会员信息修改接口")
	@RequestMapping(value = "/memberInfo/custUpdate", method = RequestMethod.POST)
	public BaseValueObject custUpdate(@ApiParam(value = "渠道编码，由会员系统先行分配", required = true) @RequestParam(value = "channelCode") String channelCode,
			@ApiParam(value = "系统编码，由会员系统先行分配", required = false) @RequestParam(value = "extSystem", required = false) String extSystem,
			@ApiParam(value = "会员标识", required = false) @RequestParam(value = "custId", required = true) String custId,
			@ApiParam(value = "会员姓名", required = false) @RequestParam(value = "custName", required = false) String custName,
			@ApiParam(value = "会员昵称", required = false) @RequestParam(value = "nickName", required = false) String nickName,
			@ApiParam(value = "会员的联系电话", required = false) @RequestParam(value = "mobile", required = false) String mobile,
			@ApiParam(value = "电子邮箱", required = false) @RequestParam(value = "email", required = false) String email,
			@ApiParam(value = "生日", required = false) @RequestParam(value = "birthdate", required = false) String birthdate,
			@ApiParam(value = "性别性别<br/>&nbsp;&nbsp;&nbsp;&nbsp;【<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MALE：男，<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FEMALE：女<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;】", required = false) @RequestParam(value = "sex", required = false) String sex,
			@ApiParam(value = "联系地址", required = false) @RequestParam(value = "contactAddr", required = false) String contactAddr,
			@ApiParam(value = "证件地址", required = false) @RequestParam(value = "certiAddr", required = false) String certiAddr,
			@ApiParam(value = "证件号码", required = false) @RequestParam(value = "certiNbr", required = false) String certiNbr,
			@ApiParam(value = "证件类型", required = false) @RequestParam(value = "certiType", required = false) String certiType){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("channelCode", channelCode);
		paramMap.put("extSystem", StringUtils.isEmpty(extSystem) ? channelCode : extSystem);
		paramMap.put("custId", custId);
		paramMap.put("email", email);
		paramMap.put("birthdate", birthdate);
		paramMap.put("mobile", mobile);
		paramMap.put("sex", sex);
		paramMap.put("nickName", nickName);
		paramMap.put("custName", custName);
		paramMap.put("contactAddr", contactAddr);
		paramMap.put("certiNbr", certiNbr);
		paramMap.put("certiAddr", certiAddr);
		paramMap.put("certiType", certiType);
		
		BaseValueObject baseValueObject = memberInfoService.custUpdate(paramMap);
		baseValueObject.setCode(CodeEnum.SUCCESS);
		baseValueObject.setInterfaceIdentity("/inside/memberInfo/custUpdate");
		return baseValueObject;
	}
	
	@ApiOperation(value = "会员资料查询接口")
	@RequestMapping(value = "/memberInfo/showCustInfoByCustId", method = RequestMethod.POST)
	public DataVo<InfoCustVo> custQuery(HttpServletRequest request,@ApiParam(value = "会员标识", required = true) @RequestParam(value = "custId", required = true) String custId){
		DataVo<InfoCustVo> infoCustVo = memberInfoService.showCustInfoByCustId(custId);
		infoCustVo.setInterfaceIdentity("/inside/memberInfo/showCustInfoByCustId");
		return infoCustVo;
	}
	
	@ApiOperation(value = "根据账户号（手机号码或邮箱）获取账户信息")
	@RequestMapping(value = "/memberInfo/getInfoLoginUserByLoginUser", method = RequestMethod.POST)
	public DataVo<InfoLoginUserVo> getInfoLoginUserByLoginUser(HttpServletRequest request,@ApiParam(value = "手机号码", required = true) @RequestParam(value = "mobile", required = true) String mobile){
		DataVo<InfoLoginUserVo> baseValueObject = memberInfoService.findInfoLoginUserByMobile(mobile);
		baseValueObject.setInterfaceIdentity("/inside/memberInfo/getInfoLoginUserByLoginUser");
		return baseValueObject;
	}
	
	@ApiOperation(value = "发送验证码短信接口")
	@RequestMapping(value = "/memberInfo/getVerifyCode", method = RequestMethod.POST)
	public BaseValueObject getVerifyCode(@ApiParam(value = "手机号码", required = true) @RequestParam(value = "mobile") String mobile,
			@ApiParam(value = "短信验证码类型【1：登录；2：注册】", required = true) @RequestParam(value = "codeType") String codeType) throws Exception {
		String verifyCode = RandomKit.randomNumbers(4); 
		BaseValueObject baseValueObject = memberInfoService.getVerifyCode(mobile, verifyCode,codeType);
		LOGGER.info("发送验证码短信接口,/inside/memberInfo/getVerifyCode end...，验证码："+verifyCode);
		return baseValueObject;
	}
	
}
