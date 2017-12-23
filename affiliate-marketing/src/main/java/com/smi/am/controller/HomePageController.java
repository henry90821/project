package com.smi.am.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.IHomePageService;
import com.smi.am.service.vo.HomePageVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH +"/homePage")
@Api(value = "首页显示接口")
public class HomePageController extends BaseController{

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private IHomePageService homePageService;
	
	@ApiOperation(value = "首页信息查询")
	@RequestMapping(value = "/showHomePageInfo", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "首页信息查询")
	@LoginAuth
	public SmiResult<HomePageVo> showHomePageInfo(HttpServletRequest request) throws Exception{
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return homePageService.showHomePageInfo(userVo);
	}
}
