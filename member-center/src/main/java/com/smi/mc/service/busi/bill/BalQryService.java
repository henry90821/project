package com.smi.mc.service.busi.bill;

import java.util.Map;

import com.smilife.core.exception.SmiBusinessException;

public interface BalQryService {
	public Map<String, Object> baiduBalQuery(Map<String, Object> param) throws SmiBusinessException;

}
