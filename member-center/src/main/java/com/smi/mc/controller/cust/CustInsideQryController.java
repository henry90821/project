package com.smi.mc.controller.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.smi.mc.po.CustInfoVo;
import com.smi.mc.po.ReqJsonBody;
import com.smi.mc.service.busi.cust.CustQryService;
import com.smi.mc.service.external.cust.CustQryExtService;
import com.smi.mc.vo.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import net.sf.json.JSONObject;

/**
 * 会员基本资料查询的控制器
 * 
 * @author smi
 *
 */

@RestController
@RequestMapping(value = "/inside")
public class CustInsideQryController extends BaseController {

	private final Logger logger = LoggerUtils.getLogger(this.getClass());
	@Autowired
	private CustQryService custQryService;

	@ApiOperation(value = "会员基本资料查询")
	@RequestMapping(value = "/cust/qryCust", method = RequestMethod.POST)
	public Object custQry(
			@ApiParam(value = "会员基本资料查询") @org.springframework.web.bind.annotation.RequestBody CustInfoVo custInfoVo) {
		this.logger.info("会员资料查询请求报文：" + custInfoVo.toString());
		Map<String,Object> paramMap=new HashMap<String, Object>();
		paramMap.put("CUST_NBR", custInfoVo.getCustNbr());
		paramMap.put("CUST_NAME", custInfoVo.getCustName());
		paramMap.put("ORG_ID", custInfoVo.getOrgId());
		paramMap.put("CONTACT_MOBILE", custInfoVo.getContactMobile());
		paramMap.put("PAGE_INDEX", String.valueOf(custInfoVo.getPageNum()));
		paramMap.put("PAGE_SIZE", String.valueOf(custInfoVo.getPageSize()));
		Map<String, Object> resMap = this.custQryService.qryCustInfo(paramMap);
		return resMap;
	}
	
	@ApiOperation(value = "根据手机账号查询会员标识")
	@RequestMapping(value = "/cust/getCustIdByLoginMobiles", method = RequestMethod.POST)
	public SmiResult<List<Map<String, Object>>> getCustIdByLoginMobiles(@ApiParam(name = "loginMobiles", value = "手机账号(英文逗号分隔,如：10000024089,17100000836,19087865631)", required = true) @RequestParam(value = "loginMobiles", required = true)  String loginMobiles){
		this.logger.info("手机账号报文：" + loginMobiles);
		SmiResult<List<Map<String, Object>>> smiResult = new SmiResult<List<Map<String, Object>>>();
		List<Map<String, Object>> custIdByloginMobile = custQryService.getCustIdByloginMobile(loginMobiles);
		
		smiResult.setCode(CodeEnum.SUCCESS);
		smiResult.setData(custIdByloginMobile);
		return smiResult;
	} 

}
