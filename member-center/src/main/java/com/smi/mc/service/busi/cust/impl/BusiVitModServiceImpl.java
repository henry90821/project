package com.smi.mc.service.busi.cust.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.InfoVitAtomicService;
import com.smi.mc.service.busi.cust.BusiVitModService;
import com.smi.mc.service.busi.cust.OrderService;

@Service("busiVitModService")
public class BusiVitModServiceImpl implements BusiVitModService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private InfoVitAtomicService infoVitAtomicService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CustAtomicService custAtomicService;

	public Map<String, Object> busiVitMod(Map<String, Object> param) throws BusiServiceException {
		Map<String,Object> respMap = new HashMap<String,Object>();

		String custId = (String) param.get("CUST_ID");
		String channelCode = (String) param.get("CHANNEL_CODE");
		String systemUserId = (String) param.get("SYSTEM_USER_ID");
		String extSystem = (String) param.get("EXT_SYSTEM");
		String vitValue = (String) param.get("VIT_VALUE");
		String servCode = (String) param.get("SERV_CODE");
		if (StringUtils.isEmpty(custId)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数CUST_ID不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(channelCode)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数CHANNEL_CODE不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		param.put("ORG_ID", channelCode);
		if (StringUtils.isEmpty(systemUserId)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数SYSTEM_USER_ID不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(extSystem)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数EXT_SYSTEM不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(vitValue)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数VIT_VALUE不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(servCode)) {
			RESP resp = RESP.createFailResp(Constants.ERROT_INFO_CODE, "必填参数SERV_CODE不能为空");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}
		param.put("STATUS_CD", Integer.valueOf(1000));

		String VIT_ID = null;
		try {
			VIT_ID = this.commonAtomicService.getSequence("SEQ_INFO_VIT");
			param.put("VIT_ID", VIT_ID);
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}

		Map succSumMap = null;
		int succSum = 0;
		try {
			succSumMap = this.infoVitAtomicService.addInfoVit(param);
		} catch (Exception e) {
			this.logger.error("调用活力值新增原子服务失败", e);
			throw new BusiServiceException(e);
		}
		if (succSumMap != null) {
			succSum = ((Integer) succSumMap.get("SUCC_SUM")).intValue();
			if (succSum <= 0) {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "调用活力值新增原子服务失败");
				respMap.put("QRY_RESULTS", resp);
				return respMap;
			}
		} else {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "调用活力值新增原子服务失败！");
			respMap.put("QRY_RESULTS", resp);
			return respMap;
		}

		Map<String,Object> paramQryVitMap = new HashMap<String,Object>();
		Map<String,Object> repCustMap = new HashMap<String,Object>();
		paramQryVitMap.put("CUST_ID", custId);

		Long vitVal = Long.valueOf(0L);
		try {
			Map<String,Object> respQryVitMap = this.infoVitAtomicService.qrySumInfoVit(paramQryVitMap);
			if ((respQryVitMap != null) && (respQryVitMap.size() > 0)) {
				vitVal = (Long) respQryVitMap.get("SUM_VIT");
				repCustMap.put("VIT_VALUE", vitVal);
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员活力值查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String,Object> respQryVitMap;
		Map<String,Object> repVitQry = new HashMap<String,Object>();
		String custVipLevel = "";
		try {
			repVitQry = this.infoVitAtomicService.infoVitQry(repCustMap);
			if ((repVitQry != null) && (repVitQry.size() > 0)) {
				List<Map<String,Object>> listVitQry = (List<Map<String,Object>>) repVitQry.get("CUST");
				if ((listVitQry != null) && (listVitQry.size() > 0))
					custVipLevel = (String) ((Map) listVitQry.get(0)).get("CUST_VIP_LEVEL");
				else
					throw new BusiServiceException("1", Constants.EXCEPTION_CODE_THREE, "根据活力值查询会员等级失败！");
			} else {
				throw new BusiServiceException("1", Constants.EXCEPTION_CODE_THREE, "根据活力值查询会员等级失败！");
			}
		} catch (AtomicServiceException e) {
			this.logger.error("根据活力值查询会员等级失败！", e);
			throw new BusiServiceException(e);
		}

		Map<String,Object> updCustLoginPwdMap1 = new HashMap<String,Object>();
		updCustLoginPwdMap1.put("CUST_ID", custId);
		updCustLoginPwdMap1.put("CUST_VIP_LEVEL", custVipLevel);
		int success = 0;
		try {
			success = this.custAtomicService.updCust(updCustLoginPwdMap1);
		} catch (AtomicServiceException e) {
			this.logger.error("修改会员等级失败！", e);
			throw new BusiServiceException(e);
		}
		if (success > 0) {
			RESP resp = RESP.createSuccessResp();
			respMap.put("QRY_RESULTS", resp);
		} else {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "活力值更新失败！");
			respMap.put("QRY_RESULTS", resp);
		}

		return respMap;
	}
}