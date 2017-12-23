package com.smi.am.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.constant.SmiConstants;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.ValidateCode;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
@RestController
@RequestMapping(value = "/verifyCode")
@Api(value = "验证码生成接口")
public class GenerateVerifyCodeImg extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@ApiOperation(value = "生成验证码")
	@RequestMapping(value = "/generateVerifyCode", method = { RequestMethod.GET })
	public void generateVerifyCode(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("image/jpeg");  
		response.setHeader("Pragma", "no-cache");  
		response.setHeader("Cache-Control", "no-cache");  
		response.setDateHeader("Expires", 0);  
		ValidateCode vCode = new ValidateCode(100,30,4,100);  
		SessionManager.removeAttribute(request,SmiConstants.SESSION_KEY_SECURITY_CODE);  
		SessionManager.setAttribute(request, SmiConstants.SESSION_KEY_SECURITY_CODE, vCode.getCode()); 
		LOGGER.info("验证码："+vCode.getCode());
		LOGGER.info("GenerateVerifyCodeImg sessionId是："+request.getSession().getId());
		LOGGER.info(" sessionId isNew ："+request.getSession().isNew());
		vCode.write(response.getOutputStream());  
	}
}
