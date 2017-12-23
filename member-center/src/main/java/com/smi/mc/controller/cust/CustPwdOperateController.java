package com.smi.mc.controller.cust;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.mc.constants.BusiConstants;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.service.busi.cust.LoginPwdService;
import com.smilife.core.common.controller.BaseController;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 会员密码操作控制器
 * 
 * @author smi
 *
 */

@RestController
@RequestMapping(value = "/inside/")
public class CustPwdOperateController  extends BaseController{

	@Autowired
	private LoginPwdService loginPwdService;
	
	@ApiOperation(value = "验证支付密码")
	@RequestMapping(value = "cust/checkPayPwd", method = RequestMethod.POST)
	public Object checkPwd(
			@ApiParam(name = "custId", value = "会员编码", required = true) @RequestParam(value = "custId")  String custId,
			@ApiParam(name = "payPwd", value = "支付密码", required = true) @RequestParam(value = "payPwd") String payPwd,
			@ApiParam(name = "pwdType", value = "密码类型", required = true) @RequestParam(value = "pwdType") String pwdType,
			@ApiParam(name="channelCode", value = "渠道编码，由会员系统先行分配", required = true) @RequestParam(value = "channelCode") String channelCode,
			@ApiParam(name="extSystem", value = "系统编码，由会员系统先行分配", required = false) @RequestParam(value = "extSystem", required = false) String extSystem
			) throws BusiServiceException{
		Map<String,Object> reqMap=new HashMap<String, Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		reqMap.put("CUST_ID", custId);
		reqMap.put("PWD_TYPE", pwdType); 
		reqMap.put("PWD", payPwd);
		reqMap.put("CHANNEL_CODE", channelCode);
		reqMap.put("EXT_SYSTEM", StringUtils.isEmpty(extSystem) ? channelCode : extSystem);
		resMap=this.loginPwdService.checkLoginPwd(reqMap);
		return resMap;
		
	}
	
	
	@ApiOperation(value = "判断是否设置了支付密码")
	@RequestMapping(value = "cust/isSetPayPwd",method = RequestMethod.POST)
	public Object isSetPayPwd(@ApiParam(name = "custId", value = "会员编码", required = true) @RequestParam(value = "custId")  String custId
			) throws BusiServiceException{
		Map<String,Object> reqMap=new HashMap<String, Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		reqMap.put("CUST_ID", custId);
		resMap=this.loginPwdService.isSetPayPwd(reqMap);
		return resMap;
	}
	
	@ApiOperation(value = "设置支付密码")
	@RequestMapping(value = "cust/setPayPwd",method = RequestMethod.POST)
	public Object setPayPwd(@ApiParam(name = "custId", value = "会员编码", required = true) @RequestParam(value = "custId")  String custId,
			@ApiParam(name = "payPwd", value = "支付密码", required = true) @RequestParam(value = "payPwd")  String payPwd
			) throws BusiServiceException{
		Map<String,Object> reqMap=new HashMap<String, Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		reqMap.put("custId", custId);
		reqMap.put("payPwd", payPwd);
		resMap=this.loginPwdService.setPayPwd(reqMap);
		return resMap;
	}
	
	
	@ApiOperation(value = "重置/修改密码")
	@RequestMapping(value = "cust/restOrUpdatePwd",method = RequestMethod.POST)
	public Object resetPwd(@ApiParam(name = "custId", value = "会员编码", required = true) @RequestParam(value = "custId")  String custId,
			@ApiParam(name="channelCode", value = "渠道编码，由会员系统先行分配", required = true) @RequestParam(value = "channelCode") String channelCode,
			@ApiParam(name="extSystem", value = "系统编码，由会员系统先行分配", required = false) @RequestParam(value = "extSystem", required = false) String extSystem,
			@ApiParam(name="oldPwd", value = "旧密码",required = false) @RequestParam(value = "oldPwd",required = false) String oldPwd,
			@ApiParam(name="newPwd", value = "新密码", required = true) @RequestParam(value = "newPwd") String newPwd,
			@ApiParam(name="pwdType", value = "密码类型[1,登陆密码；2,服务密码]", required = true) @RequestParam(value = "pwdType") String pwdType,
			@ApiParam(name="restType", value = "更新类型[1、修改；2、重置；3、找回密码]", required = true) @RequestParam(value = "restType") String restType
			) throws BusiServiceException{
		Map<String,Object> reqMap=new HashMap<String, Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		reqMap.put("CUST_ID", custId);
		reqMap.put("PWD_TYPE", pwdType);
		reqMap.put("REST_TYPE", restType);
		reqMap.put("CHANNEL_CODE", channelCode);
		reqMap.put("EXT_SYSTEM", StringUtils.isEmpty(extSystem) ? channelCode : extSystem);
		reqMap.put("OLD_PWD", oldPwd);
		reqMap.put("NEW_PWD", newPwd);
		resMap=this.loginPwdService.restOrUpdatePwd(reqMap);
		return resMap;
	}

}
