package com.smi.mc.controller.cust;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.mc.po.ReqJsonBody;
import com.smi.mc.service.external.cust.CustAddExtServive;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import net.sf.json.JSONObject;

/**
 * 会员资料新增(满天星接口)<br/>
 */
@RestController
@RequestMapping(value = "/cust/busiAdd")
@Api(value = "会员资料新增(满天星接口)")
public class CustAddForMTXController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private CustAddExtServive custAddExtService;

	@ApiOperation(value = "满天星会员资料新增接口")
	@RequestMapping(value = "/custAddForMTX", method = RequestMethod.POST)
	public Map<String, Object> custAddForMTX(
			@ApiParam(value = "会员资料新增报文") @org.springframework.web.bind.annotation.RequestBody ReqJsonBody reqJsonBody)
			throws Exception {
		LOGGER.info("满天星会员资料新增接口 start...,parma: " + reqJsonBody);
		Map<String, Object> reqMap = JSONObject.fromObject(reqJsonBody);
		JSONObject jasonObject = JSONObject.fromObject(reqMap.get("data"));
		Map<String, Object> dataMap = (Map<String, Object>) jasonObject;
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap = custAddExtService.addCustByMTXExt(dataMap.toString());
		LOGGER.info("满天星会员资料新增接口 end...");
		return resMap;
	}

}
