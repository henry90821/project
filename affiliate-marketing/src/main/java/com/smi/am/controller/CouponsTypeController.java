package com.smi.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.ICouponsTypeService;
import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH +"/couponsType")
@Api(value = "优惠券分类接口")
public class CouponsTypeController extends BaseController{
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private ICouponsTypeService couponsTypeService;

	@ApiOperation(value = "新增优惠券分类")
	@RequestMapping(value = "/saveCouponsType", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "新增优惠券分类")
	@LoginAuth
	public BaseValueObject saveCouponsType(HttpServletRequest request,
			@ApiParam(value = "新增优惠券分类实体类", required = true) @org.springframework.web.bind.annotation.RequestBody(required = true) @Valid CounponsTypeVo coupon) throws Exception{
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return couponsTypeService.saveCouponsType(coupon, userVo.getuUsername());
	}
	
	@ApiOperation(value = "优惠券分类分页查询")
	@RequestMapping(value = "/searchCounponsTypeByCondition", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "优惠券分类分页查询")
	@LoginAuth
	public SmiResult<List<CounponsTypeVo>> searchCounponsTypeByCondition(HttpServletRequest request,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize) {
		return couponsTypeService.getAllCouponsTypeList(pageNum, pageSize);
	}
}
