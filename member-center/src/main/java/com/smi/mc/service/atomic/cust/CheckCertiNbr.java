package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smi.mc.constants.Constants;
import com.smi.mc.po.RESP;
import com.smi.mc.utils.IDCard;

public class CheckCertiNbr 
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public Map<String, Object> checkNbr(String param)
  {
    RESP resp = null;
    String resMsg = IDCard.IDCardValidate(param);

    if ("" != resMsg)
      resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, resMsg);
    else {
      resp = RESP.createSuccessResp();
    }
    Map resMap = new HashMap();
    resMap.put("RESP", resp);
    return resMap;
  }
}