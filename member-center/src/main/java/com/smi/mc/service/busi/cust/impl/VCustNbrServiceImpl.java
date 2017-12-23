package com.smi.mc.service.busi.cust.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.busi.cust.VCustNbrService;

@Service("vCustNbrService")
public class VCustNbrServiceImpl implements VCustNbrService{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	CommonAtomicService commonAtomicService;

	/**
	 * 虚拟会员卡号生成
	 */
	public Map<String, Object> addVCustNbr() throws BusiServiceException {
		Map<String, Object> map = new HashMap<String, Object>();

		SimpleDateFormat sdf = new SimpleDateFormat("YYMMdd");
		String sysDate = sdf.format(new Date());
		try {
			String seq = this.commonAtomicService.getSequence("SEQ_V_CUST_NBR");
			String v_cust_nbr = 171 + sysDate + seq;
			map.put("V_CUST_NBR", v_cust_nbr);
		} catch (Exception e) {
			this.logger.error("生成虚拟会员卡号序列失败", e);
			throw new BusiServiceException(e);
		}

		return map;
	}
}