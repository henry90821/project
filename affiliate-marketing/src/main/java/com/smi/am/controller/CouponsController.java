package com.smi.am.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.ICouponsService;
import com.smi.am.service.vo.CouponsEditVo;
import com.smi.am.service.vo.CouponsInsertVo;
import com.smi.am.service.vo.CouponsShowVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 优惠券入口
 * @author yanghailong
 *
 */
@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH + "/coupons")
@Api(value = "优惠券接口")
public class CouponsController extends BaseController {
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private ICouponsService couponsService;
	
	@ApiOperation(value = "新增优惠券")
	@RequestMapping(value = "/saveCoupons", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "新增优惠券")
	@LoginAuth
	public BaseValueObject saveCoupons(HttpServletRequest request,
			@ApiParam(value = "新增优惠券实体类", required = true) @org.springframework.web.bind.annotation.RequestBody(required = true) @Valid CouponsInsertVo couponsVo) throws Exception{
		
		BaseValueObject baseValueObject = new BaseValueObject();
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		int result = couponsService.saveCoupons(couponsVo,userVo.getuUsername());
		if(result > 0){
			baseValueObject.setCode(CodeEnum.SUCCESS);
			baseValueObject.setMsg("新增优惠券成功");
		}else {
			baseValueObject.setCode(CodeEnum.SERVER_INNER_ERROR);
		}
		
		return baseValueObject;
	}
	
	@ApiOperation(value = "优惠券查詢")
	@RequestMapping(value = "/searchCouponsByConditions", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "优惠券查詢")
	@LoginAuth
	public SmiResult<List<CouponsVo>> searchCouponsByConditions(HttpServletRequest request,
			@ApiParam(name = "couponsId", value = "优惠券ID", required = false) @RequestParam(value = "couponsId", required = false)  String couponsId,
			@ApiParam(name = "couponsName", value = "优惠券名称", required = false) @RequestParam(value = "couponsName", required = false) String couponsName,
			@ApiParam(name = "couponsType", value = "优惠券类别", required = false) @RequestParam(value = "couponsType", required = false) @Valid @Pattern(regexp="^[0-9]*$",message="请输入数字") String couponsType,
			@ApiParam(name = "permanent", value = "是否永久有效;1:是,2:否", required = false) @RequestParam(value = "permanent", required = false) String permanent,
			@ApiParam(name = "limitStart", value = "使用期限开始时间", required = false) @RequestParam(value = "limitStart", required = false) String limitStart,
			@ApiParam(name = "limitEnd", value = "使用期限结束时间", required = false) @RequestParam(value = "limitEnd", required = false) String limitEnd,
			@ApiParam(name = "channel", value = "投放渠道;1:星美生活,2:满天星", required = false) @RequestParam(value = "channel", required = false) String channel,
			@ApiParam(name = "auditStatus", value = "当前审核状态;1:未提交,2:待审核,3:未通过,4:审核通过,5:已发放,6:已过期", required = false) @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
			@ApiParam(name = "activityArea", value = "活动区域", required = false) @RequestParam(value = "activityArea", required = false) String activityArea,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("COUPONS_ID", couponsId);
		conditionMap.put("COUPONS_NAME", couponsName);
		conditionMap.put("COUPONS_TYPE", couponsType);
		conditionMap.put("PERMANENT", permanent);
		conditionMap.put("CHANNEL", channel);
		conditionMap.put("AUDIT_STATUS", auditStatus);
		if (StringUtils.isNotEmpty(activityArea)) {
			conditionMap.put("ACTIVITY_AREA", activityArea.split(","));
		}
		conditionMap.put("LIMIT_START",limitStart);
		conditionMap.put("LIMIT_END",limitEnd);
		conditionMap.put("PAGE_NUM", pageNum);
		conditionMap.put("PAGE_SIZE", pageSize);
		SmiResult<List<CouponsVo>> smiResult = couponsService.searchCouponsByConditions(conditionMap);
		return smiResult;
	}	
	
	@ApiOperation(value = "查询优惠券详情")
	@RequestMapping(value = "/showCouponsById/{couponsId}", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "查询优惠券详情")
	public SmiResult<CouponsShowVo> showCouponsById(
			@ApiParam(name = "couponsId", value = "优惠券ID", required = true) @PathVariable(value = "couponsId") String couponsId){
		return couponsService.getCouponsVoById(couponsId);
	}
	
	@ApiOperation(value = "编辑优惠券详情(编辑打开优惠券详情页面接口")
	@RequestMapping(value = "/editCouponsPageShowById/{couponsId}", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "编辑优惠券详情")
	public SmiResult<CouponsShowVo> editCouponsPageShowById(
			@ApiParam(name = "couponsId", value = "优惠券ID", required = true) @PathVariable(value = "couponsId") String couponsId) throws Exception{
		return couponsService.editCouponsPageShowById(couponsId);
	}
	
	@ApiOperation(value = "编辑优惠券信息")
	@RequestMapping(value = "/editCouponsById", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "编辑优惠券信息")
	@LoginAuth
	public BaseValueObject editCouponsById(HttpServletRequest request,
			@ApiParam(value = "编辑优惠券实体类", required = true) @org.springframework.web.bind.annotation.RequestBody(required = true) @Valid CouponsEditVo couponsVo){
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return couponsService.editCouponsById(couponsVo,userVo.getuUsername());
	}
	
	@ApiOperation(value = "删除优惠券")
	@RequestMapping(value = "/deleteCouponsByIds", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "删除优惠券")
	public BaseValueObject deleteCouponsByIds(HttpServletRequest request,
			@ApiParam(name = "couponsIds", value = "优惠券IDs", required = true) @RequestParam(value = "couponsIds") String couponsIds){
		return  couponsService.deleteCouponsByIds(couponsIds);
	}
	
	@ApiOperation(value = "优惠券提交审核")
	@RequestMapping(value = "/commitAuditCouponsById", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "优惠券提交审核")
	@LoginAuth
	public BaseValueObject commitAuditCouponsById(HttpServletRequest request,
			@ApiParam(name = "couponsIds", value = "优惠券IDs", required = true) @RequestParam(value = "couponsIds") String couponsIds){
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return  couponsService.commitAuditCouponsById(couponsIds,SmiConstants.INAUDIT, userVo.getuUsername());
	}
	
	@ApiOperation(value = "复制优惠券信息")
	@RequestMapping(value = "/copeCouponsById", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "复制优惠券信息")
	@LoginAuth
	public BaseValueObject copeCouponsById(HttpServletRequest request,
			@ApiParam(name = "couponsId", value = "优惠券ID", required = true) @RequestParam(value = "couponsId") String couponsId){
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return couponsService.copyCouponsById(couponsId, userVo.getuUsername());
	}
	
	@ApiOperation(value = "优惠券审核")
	@RequestMapping(value = "/auditCouponsById", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "优惠券审核")
	@LoginAuth
	public BaseValueObject auditCouponsById(HttpServletRequest request,
			@ApiParam(name = "couponsId", value = "优惠券IDs", required = true) @RequestParam(value = "couponsId") String couponsId,
			@ApiParam(name = "auditStatus", value = "审核;3:未通过,4:审核通过", required = true) @RequestParam(value = "auditStatus") Integer auditStatus,
			@ApiParam(name = "remark", value = "审核失败原因", required = true) @RequestParam(value = "remark", required = true) String remark) throws IOException{
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return couponsService.auditCouponsById(couponsId, auditStatus, userVo.getuUsername(),remark);
	}
	
	@ApiOperation(value = "优惠券发放")
	@RequestMapping(value = "/deliveryCouponsById", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "优惠券发放")
	@LoginAuth
	public BaseValueObject deliveryCouponsById(HttpServletRequest request,
			@ApiParam(name = "couponsId", value = "优惠券IDs", required = true) @RequestParam(value = "couponsId") String couponsId){
		UserVo userVo = (UserVo)SessionManager.getUserInfo(request);
		return couponsService.deliveryCouponsById(couponsId, userVo.getuUsername());
	}
	
}
