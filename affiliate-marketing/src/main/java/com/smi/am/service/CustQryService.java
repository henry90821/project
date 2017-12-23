package com.smi.am.service;

import java.util.List;
import java.util.Map;

import com.smi.am.service.vo.CustVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.exception.SmiBusinessException;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface CustQryService {
	/**
	 * 会员列表查询
	 * @param paramMap
	 * @return
	 * @throws SmiBusinessException
	 */
	public SmiResult<List<CustVo>> qryCustList(Map<String,Object> paramMap)throws SmiBusinessException;

	
	public String qryCustCardPack(Map<String,Object> paramMap)throws SmiBusinessException;
}
