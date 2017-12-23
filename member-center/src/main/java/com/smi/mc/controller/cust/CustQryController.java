package com.smi.mc.controller.cust;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.smi.mc.po.ReqJsonBody;
import com.smi.mc.service.external.cust.CustQryExtService;
import com.smilife.core.common.controller.BaseController;
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
@RequestMapping(value = "/cust")
@ApiIgnore
public class CustQryController extends BaseController {

	private final Logger logger = LoggerUtils.getLogger(this.getClass());
	@Autowired
	private CustQryExtService custQryExtService;

	@ApiOperation(value = "会员基本资料查询")
	@RequestMapping(value = "/qryCust", method = RequestMethod.POST)
	public Object custQry(@ApiParam(value = "会员基本资料查询") @org.springframework.web.bind.annotation.RequestBody ReqJsonBody reqJsonBody) {
		this.logger.info("会员资料查询请求报文：" + reqJsonBody.getData().toString());
		JSONObject JSON = JSONObject.fromObject(reqJsonBody.getData());
		Map<String, Object> resMap = this.custQryExtService.custQry(JSON);
		return resMap;

	}

}
