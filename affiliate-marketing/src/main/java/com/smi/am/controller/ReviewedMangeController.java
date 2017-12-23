package com.smi.am.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.IReviewedMangeService;
import com.smi.am.service.vo.CustPackVo;
import com.smi.am.service.vo.WraparoundResVo;
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
 * 审核管理控制器
 * 
 * @author smi
 *
 */

@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH + "/reviewManger")
@Api(value = "审核管理")
public class ReviewedMangeController extends BaseController {

	@Autowired
	private IReviewedMangeService iReviewedMangeService;

	/**
	 * 审核管理查询
	 * 
	 * @param reviewStatus
	 * @param roleType
	 * @return
	 */
	@ApiOperation(value = "审核管理查询")
	@RequestMapping(value = "/reviewMange", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "审核管理查询")
	@LoginAuth
	public SmiResult<List<WraparoundResVo>> reviewMange(HttpServletRequest request,
			@ApiParam(name = "reviewStatus", value = "审核状态(全部(2,3,4,5),待审核(2),审核通过(4),审核不通过(3),已发送(5))", required = true) @RequestParam(value = "reviewStatus", required = true) String reviewStatus,
			@ApiParam(name = "wraparoundType", value = "卷包类型(1礼包类型/2优惠券类型)", required = false) @RequestParam(value = "wraparoundType", required = true) String wraparoundType,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reviewStatus", reviewStatus);
		paramMap.put("wraparoundType", wraparoundType);
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		SmiResult<List<WraparoundResVo>> smiResult = this.iReviewedMangeService.reviewMange(paramMap, request);
		return smiResult;
	}

	/**
	 * 礼包审核
	 * 
	 * @param reviewStatus
	 * @param roleType
	 * @return
	 */
	@ApiOperation(value = "礼包审核")
	@RequestMapping(value = "/reviewed ", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "礼包审核")
	@LoginAuth
	public BaseValueObject reviewed(HttpServletRequest request,
			@ApiParam(name = "giftPackageId", value = "礼包id", required = true) @RequestParam(value = "giftPackageId", required = true) String giftPackageId,
			@ApiParam(name = "reviewStatus", value = "审核状态(4通过-3审核不通过)", required = true) @RequestParam(value = "reviewStatus", required = true) String reviewStatus,
			@ApiParam(name = "reason", value = "审核不通过的原因", required = true) @RequestParam(value = "reason", required = true) String reason) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("giftPackageId", giftPackageId);
		paramMap.put("reviewStatus", reviewStatus);
		paramMap.put("reason", reason);
		BaseValueObject baseValueObject = this.iReviewedMangeService.reviewed(paramMap, request);
		return baseValueObject;
	}

}
