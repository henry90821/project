package com.smi.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.ICommonService;
import com.smi.am.service.vo.ActivityAreaVo;
import com.smi.am.service.vo.ActivityShopVo;
import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.service.vo.DepartmentVo;
import com.smi.am.service.vo.PreferentialtypeVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 基础数据入口
 * @author yanghailong
 *
 */
@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH + "/common")
@Api(value = "基础数据接口")
public class CommonController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private ICommonService commonService;
	
	
	@ApiOperation(value = "获取所有优惠券类型基础数据")
	@RequestMapping(value = "/getAllCouponsType", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "获取所有优惠券类型基础数据")
	@LoginAuth
	public SmiResult<List<CounponsTypeVo>> getAllCouponsType(HttpServletRequest request) throws Exception{
		SmiResult<List<CounponsTypeVo>> smiResult = new SmiResult<List<CounponsTypeVo>>();
		List<CounponsTypeVo> allCouponsType = commonService.getAllCouponsType();
		smiResult.setData(allCouponsType);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@ApiOperation(value = "获取星美运营中心的基础数据")
	@RequestMapping(value = "/getAllActivityArea", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "获取星美运营中心的基础数据")
	@LoginAuth
	public SmiResult<List<ActivityAreaVo>> getAllActivityArea(HttpServletRequest request) throws Exception{
		SmiResult<List<ActivityAreaVo>> smiResult = new SmiResult<List<ActivityAreaVo>>();
		List<ActivityAreaVo> allAreaList = commonService.getAllActivityArea(0);
		smiResult.setData(allAreaList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@ApiOperation(value = "获取所有优惠券分类类别的基础数据")
	@RequestMapping(value = "/getAllPreferentialTypeData", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "获取所有优惠券分类类别的基础数据")
	@LoginAuth
	public SmiResult<List<PreferentialtypeVo>> getAllPreferentialTypeData(HttpServletRequest request) throws Exception{
		SmiResult<List<PreferentialtypeVo>> smiResult = new SmiResult<List<PreferentialtypeVo>>();
		List<PreferentialtypeVo> allPreferentialtype = commonService.getAllPreferentialtype();
		smiResult.setData(allPreferentialtype);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@ApiOperation(value = "根据运营中心ID获取该运营中心的所有大区")
	@RequestMapping(value = "/getAllActivityZoneByAreaId/{areaId}", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "根据运营中心ID获取该运营中心的所有大区")
	@LoginAuth
	public SmiResult<List<ActivityAreaVo>> getAllActivityZoneByAreaId(HttpServletRequest request,
			@ApiParam(name = "areaId", value = "活动区ID", required = true) @PathVariable(value = "areaId") Integer areaId) throws Exception{
		SmiResult<List<ActivityAreaVo>> smiResult = new SmiResult<List<ActivityAreaVo>>();
		List<ActivityAreaVo> allAreaList = commonService.getAllActivityArea(areaId);
		smiResult.setData(allAreaList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@ApiOperation(value = "根据大区ID获取该大区下所有的门店信息")
	@RequestMapping(value = "/getAllActivityStoreByZoneId/{zoneId}", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "根据大区ID获取该大区下所有的门店信息")
	@LoginAuth
	public SmiResult<List<ActivityShopVo>> getAllActivityStoreByZoneId(HttpServletRequest request,
			@ApiParam(name = "zoneId", value = "活动区ID", required = true) @PathVariable(value = "zoneId") Integer zoneId) throws Exception{
		SmiResult<List<ActivityShopVo>> smiResult = new SmiResult<List<ActivityShopVo>>();
		List<ActivityShopVo> shoplist = commonService.getAllActivityStoreByZoneId(zoneId);
		smiResult.setData(shoplist);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	
	
	@ApiOperation(value = "根据活动区Id得到所属活动门店")
	@RequestMapping(value = "/getShopByAreaId/{areaId}", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "根据活动区Id得到所属活动门店")
	@LoginAuth
	public SmiResult<List<ActivityAreaVo>> getShopByAreaId(HttpServletRequest request,
			@ApiParam(name = "areaId", value = "活动区ID", required = true) @PathVariable(value = "areaId") Integer areaId) throws Exception{
		SmiResult<List<ActivityAreaVo>> smiResult = new SmiResult<List<ActivityAreaVo>>();
		List<ActivityAreaVo> allAreaList = commonService.getShopByAreaId(areaId);
		smiResult.setData(allAreaList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@ApiOperation(value = "获取部门基础数据")
	@RequestMapping(value = "/getAllDeptInfo", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "获取部门基础数据")
	@LoginAuth
	public SmiResult<List<DepartmentVo>> getAllDeptInfo(HttpServletRequest request) throws Exception{
		SmiResult<List<DepartmentVo>> smiResult = new SmiResult<List<DepartmentVo>>();
		List<DepartmentVo> deptList = commonService.getAllDeptInfo();
		smiResult.setData(deptList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
}
