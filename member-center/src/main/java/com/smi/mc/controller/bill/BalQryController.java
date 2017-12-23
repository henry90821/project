package com.smi.mc.controller.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smi.mc.po.ReqJsonBody;
import com.smi.mc.service.external.bill.impl.BalQryExtServ;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/billing")
@Api(value = "百度余额查询")
public class BalQryController extends BaseController {
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private BalQryExtServ balQryExtServ;

	@ApiOperation(value = "百度余额查询")
	@RequestMapping(value = "/baiduQryAcct", method = RequestMethod.POST)
	public Object baiduQryAcct(@org.springframework.web.bind.annotation.RequestBody ReqJsonBody reqJsonBody) {
		LOGGER.info("百度余额查询开始", reqJsonBody.toString());
		JSONObject reqjson = JSONObject.fromObject(reqJsonBody.getData());
		JSONObject resJSON = this.balQryExtServ.baiduBalQuery(reqjson);
		LOGGER.info("百度余额查询结束", reqJsonBody.toString());
		return resJSON;

	}

}
