package com.smi.mc.controller.cust;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.ReqJsonBody;
import com.smi.mc.service.external.cust.BusiAddExtService;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import net.sf.json.JSONObject;

/**
 * 会员业务受理服务接口实例<br/>
 */
@RestController
@RequestMapping(value = "/cust/busiAdd")
/*@Api(value = "会员业务受理服务实例")*/
@ApiIgnore
public class BusiAddController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private BusiAddExtService busiAddExtService;

	@ApiOperation(value = "会员业务受理")
	@RequestMapping(value = "/addBusi" ,method = RequestMethod.POST)
	public Object addBusi(@ApiParam(value = "业务受理请求json") @org.springframework.web.bind.annotation.RequestBody ReqJsonBody reqJsonBody) throws BusiServiceException {
		LOGGER.info("会员业务受理开始...");
		// 封装报文
		JSONObject reqjson = JSONObject.fromObject(reqJsonBody.getData());
		Map<String, Object> resMap = busiAddExtService.addBusi(reqjson);
		LOGGER.info("会员业务受理结束..."+resMap.toString());
		return resMap;
	}

}
