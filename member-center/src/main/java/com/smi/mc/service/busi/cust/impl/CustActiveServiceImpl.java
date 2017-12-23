package com.smi.mc.service.busi.cust.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.busi.cust.CustActiveService;

@Service("custActiveService")
public class CustActiveServiceImpl
  implements CustActiveService
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private CustAtomicService custAtomicService;

  public Map<String, Object> activeCust(Map<String, Object> param)
    throws BusiServiceException
  {
    Map<String, Object> respMap = new HashMap<String, Object>();

    if ((null == param.get("CUST_ID")) || ("".equals(param.get("CUST_ID")))) {
      RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "必填参数不能为空");
      respMap.put("QRY_RESULTS", resp);
      return respMap;
    }
    Map<String, Object> respQryCustMap;
    Map<String, Object> reqMap = new HashMap<String, Object>();
    reqMap.put("CUST_ID", param.get("CUST_ID"));
    try {
      respQryCustMap = this.custAtomicService.qryCustInfo(reqMap);
    }
    catch (Exception e)
    {
      this.logger.error("会员激活查询会员失败", e);
      throw new BusiServiceException(e);
    }
    List<Map<String, Object>> reqListCustMaps = (List<Map<String, Object>>)respQryCustMap.get("CUST");

    if ((null == respQryCustMap) || (null == reqListCustMaps) || (reqListCustMaps.isEmpty())) {
      RESP resp = RESP.createFailResp("1091", "该会员不存在！");
      respMap.put("QRY_RESULTS", resp);
      return respMap;
    }

    int success = 0;
    try
    {
      reqMap.put("STATUS_CD", "1000");
      success = this.custAtomicService.updCust(reqMap);
    } catch (Exception e) {
      this.logger.error("会员激活会员资料修改失败", e);
      throw new BusiServiceException(e);
    }
    if (success > 0) {
      RESP resp = RESP.createSuccessResp();
      respMap.put("QRY_RESULTS", resp);
    } else {
      RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "会员激活会员资料修改失败！");
      respMap.put("QRY_RESULTS", resp);
    }
    return respMap;
  }
}