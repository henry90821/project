package com.smi.am.service;


import java.util.Map;

import com.smi.am.utils.SmiResult;
import com.smilife.core.exception.SmiBusinessException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface ICustCardPackQryService {
	/**
	 * 会员卡包查询
	 * @param param
	 * @return
	 */
  public SmiResult<Object> qryCustCardPack(Map<String,Object> param) throws SmiBusinessException;
	

}
