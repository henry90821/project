package com.smi.am.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.CustQryService;
import com.smi.am.service.ICustCardPackQryService;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.CustPackVo;
import com.smi.am.service.vo.CustVo;
import com.smi.am.service.vo.GiftPackageResVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 会员查询控制器
 * 
 * @author smi
 *
 */
@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH + "/cust")
public class CustListQryController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private CustQryService custQryService;
	@Autowired
	private ICustCardPackQryService ICustCardPackQryService;

	/**
	 * 会员列表查询
	 * 
	 * @param custName
	 * @param custNbr
	 * @param orgId
	 * @param contactMobile
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "会员列表查询")
	@RequestMapping(value = "/qryList", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "会员列表查询")
	@LoginAuth
	public BaseValueObject custQryList(
			@ApiParam(value = "会员名称", name = "custName") @RequestParam(value = "custName", required = false) String custName,
			@ApiParam(value = "会员卡号", name = "custNbr") @RequestParam(value = "custNbr", required = false) String custNbr,
			@ApiParam(value = "渠道编号", name = "orgId") @RequestParam(value = "orgId", required = false) String orgId,
			@ApiParam(value = "手机号码", name = "contactMobile") @RequestParam(value = "contactMobile", required = false) String contactMobile,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,HttpServletRequest request)
			 {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custName", custName);
		paramMap.put("custNbr", custNbr);
		paramMap.put("orgId", orgId);
		paramMap.put("contactMobile", contactMobile);
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		LOGGER.info("发往会员中心的参数值:" + paramMap);
		SmiResult<List<CustVo>> smiResult = this.custQryService.qryCustList(paramMap);
		return smiResult;

	}

	/**
	 * 会员卡包查询
	 * 
	 * @param number
	 * @return
	 */
	@ApiOperation(value = "会员卡包查询")
	@RequestMapping(value = "/qryCardPack", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "会员卡包查询")
	@LoginAuth
	public SmiResult<Object> custQryCardPack(
			@ApiParam(value = "会员卡号/手机号码", name = "number") @RequestParam(value = "number", required = true) String number,
			@ApiParam(value = "卡包类型(1礼包,2优惠券)", name = "wraparoundType") @RequestParam(value = "wraparoundType", required = true) String wraparoundType,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,
			HttpServletRequest request) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("number", number);
		paramMap.put("wraparoundType", wraparoundType);
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		this.LOGGER.info("会员卡包查询开始,输入参数" + paramMap.toString());
		SmiResult<Object> result = this.ICustCardPackQryService.qryCustCardPack(paramMap);
		return result;
	}
}
