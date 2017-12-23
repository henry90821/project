package com.smi.mc.service.busi.cust.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.CompositeTransactionAttributeSource;
import org.springframework.util.StringUtils;

import com.github.pagehelper.StringUtil;
import com.smi.mc.constants.Constants;
import com.smi.mc.dao.cust.CustInfoMapper;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.model.bill.AcctBalance;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.billing.AcctBalanceAtomicService;
import com.smi.mc.service.atomic.cust.CodeOfferAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustBusiAtomicService;
import com.smi.mc.service.atomic.cust.CustCertiAtomicService;
import com.smi.mc.service.atomic.cust.CustHeadInfoAtomicService;
import com.smi.mc.service.atomic.cust.InfoLoginUserAtomicService;
import com.smi.mc.service.atomic.cust.InfoVitAtomicService;
import com.smi.mc.service.atomic.cust.PayAtomicService;
import com.smi.mc.service.busi.cust.CustQryService;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @author smi 会员资料查询
 */
@Service("custQryService")
public class CustQryServiceImpl implements CustQryService {
	private final Logger logger = LoggerUtils.getLogger(this.getClass());

	@Autowired
	protected CustAtomicService custAtomicService;

	@Autowired
	protected PayAtomicService payAtomicService;

	@Autowired
	protected CustCertiAtomicService custCertiAtomicService;

	@Autowired
	protected CustBusiAtomicService custBusiAtomicService;

	@Autowired
	protected InfoVitAtomicService infoVitAtomicService;

	@Autowired
	protected CodeOfferAtomicService codeOfferAtomicService;

	@Autowired
	protected InfoLoginUserAtomicService infoLoginUserAtomicService;
	
	@Autowired
	private CustInfoMapper custInfoMapper;

	// @Autowired
	// protected BalQryExtServ balQryExtServ;

	@Autowired
	protected AcctBalanceAtomicService acctBalanceAtomicService;

	@Autowired
	protected CustHeadInfoAtomicService custHeadInfoAtomicService;

	/**
	 * 会员资料查询
	 */
	public Map<String, Object> qryCust(Map<String, Object> paramMap) throws BusiServiceException {
		this.logger.info("会员资料查询对内接口服务请求报文： " + JSONObject.fromObject(paramMap).toString());
		Map<String, Object> respMap = new HashMap<String, Object>();

		if (StringUtils.isEmpty(paramMap)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "会员资料查询入参不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> paramCustQryMap = (Map<String, Object>) paramMap.get("CUST_QRY");

		@SuppressWarnings("unchecked")
		Map<String, Object> paramCustConditionMap = (Map<String, Object>) paramMap.get("QRY_CONDITION");

		if (!StringUtils.isEmpty(paramCustQryMap)) {
			try {
				respMap = qryCustOuter(paramCustQryMap); // 对外接口的查询
			} catch (BusiServiceException e) {
				this.logger.error("调用会员资料查询对外服务失败！", e);
				throw e;
			}
			return respMap;
		}
		if (!StringUtils.isEmpty(paramCustConditionMap)) {
			try {
				respMap = qryCustInner(paramCustConditionMap);// 内部接口的查询调用
			} catch (BusiServiceException e) {
				this.logger.error("调用会员资料查询业务服务失败！", e);
				throw e;
			}
			return respMap;
		}
		RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "会员资料查询入参不能为空！");
		respMap.put("RESP", resp);
		return respMap;
	}

	private Map<String, Object> qryCustInner(Map<String, Object> paramInnerMap) throws BusiServiceException {
		Map<String, Object> repCustMap = new HashMap<String, Object>();
		Map<String, Object> respMap = new HashMap<String, Object>();

		String qryLoginPwd = "";
		String qryPayPwd = "";
		String qryMobile170 = "";

		if (null == paramInnerMap) {
			RESP resp = RESP.createFailResp("1", "会员查询入参不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}

		String reqQryCustId = (String) paramInnerMap.get("CUST_ID");

		if (StringUtils.isEmpty(reqQryCustId)) {
			RESP resp = RESP.createFailResp("1", "会员唯一标识不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}

		Map<String, Object> paramQryCustMap = new HashMap<String, Object>();
		Map<String, Object> respQryCustMap;
		paramQryCustMap.put("CUST_ID", reqQryCustId);
		paramQryCustMap.put("PAGE_INDEX", "1");
		paramQryCustMap.put("PAGE_SIZE", "10");
		try {
			respQryCustMap = this.custAtomicService.qryCustInfo(paramQryCustMap);
		} catch (AtomicServiceException e) {
			this.logger.error("会员基本资料查询失败！", e);
			throw new BusiServiceException(e);
		}
		if (respQryCustMap != null) {
			@SuppressWarnings("unchecked")
			List<Object> reqListCustMaps = (List<Object>) respQryCustMap.get("CUST");
			if ((null != reqListCustMaps) && (reqListCustMaps.size() > 0)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> qryCustInfoMap = (Map<String, Object>) reqListCustMaps.get(0);
				qryLoginPwd = (String) qryCustInfoMap.get("LOGIN_PWD");
				qryPayPwd = (String) qryCustInfoMap.get("PAY_PWD");

				if (StringUtils.isEmpty(qryLoginPwd))
					repCustMap.put("LOGIN_PWD", "");
				else {
					repCustMap.put("LOGIN_PWD", qryLoginPwd);
				}

				if (StringUtils.isEmpty(qryPayPwd))
					repCustMap.put("PAY_PWD", "");
				else {
					repCustMap.put("PAY_PWD", qryPayPwd);
				}

				repCustMap.put("CUST_NBR", qryCustInfoMap.get("CUST_NBR"));
				repCustMap.put("CUST_NAME", qryCustInfoMap.get("CUST_NAME"));
				repCustMap.put("NICK_NAME", qryCustInfoMap.get("NICK_NAME"));
				repCustMap.put("SEX", qryCustInfoMap.get("SEX"));
				repCustMap.put("CONTACT_ADDR", qryCustInfoMap.get("CONTACT_ADDR"));
				repCustMap.put("CUST_ID", qryCustInfoMap.get("CUST_ID"));
				repCustMap.put("CUST_TYPE", qryCustInfoMap.get("CUST_TYPE"));
				repCustMap.put("MOBILE", qryCustInfoMap.get("CONTACT_MOBILE"));
				repCustMap.put("CUST_VIP_LEVEL", qryCustInfoMap.get("CUST_VIP_LEVEL"));
				repCustMap.put("MOBILE_170", qryCustInfoMap.get("MOBILE_170"));

				repCustMap.put("EMAIL", qryCustInfoMap.get("EMAIL"));
				repCustMap.put("BIRTHDATE", qryCustInfoMap.get("BIRTHDATE"));
				repCustMap.put("BLACKLIST", qryCustInfoMap.get("BLACKLIST"));
				repCustMap.put("JOIN_DATE", qryCustInfoMap.get("CRT_DATE"));
				repCustMap.put("CHANNEL_CODE", qryCustInfoMap.get("ORG_ID"));
				repCustMap.put("SMI_SOURCE", qryCustInfoMap.get("SMI_SOURCE"));
			} else {
				RESP resp = RESP.createFailResp("2", "根据会员标识查询不到会员资料！");
				respMap.put("RESP", resp);
				return respMap;
			}
		} else {
			RESP resp = RESP.createFailResp("2", "根据会员标识查询不到会员资料！");
			respMap.put("RESP", resp);
			return respMap;
		}
		Map<String, Object> paramQryPayMap = new HashMap<String, Object>();
		paramQryPayMap.put("cust_id", reqQryCustId);
		paramQryPayMap.put("PAGE_INDEX", "1");
		paramQryPayMap.put("PAGE_SIZE", "10");
		try {
			Map<String, Object> respQryPayMap = this.payAtomicService.qryInfoPay(paramQryPayMap);
			List reqListPayMaps = (List) respQryPayMap.get("PAY");
			if ((!StringUtils.isEmpty(reqListPayMaps)) && (reqListPayMaps.size() > 0)
					&& (!StringUtils.isEmpty(reqListPayMaps.get(0)))) {
				@SuppressWarnings("unchecked")
				Map<String, Object> repInfoPayMap = (Map<String, Object>) reqListPayMaps.get(0);
				repCustMap.put("PAY_ID", repInfoPayMap.get("PAY_ID"));
			} else {
				RESP resp = RESP.createFailResp("2", "账户不存在！");
				respMap.put("RESP", resp);
				return respMap;
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员基本资料查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String, Object> paramQryCertiMap = new HashMap<String, Object>();
		paramQryCertiMap.put("CUST_ID", reqQryCustId);
		paramQryCertiMap.put("STATUS_CD_TWO", "1000,1098");
		paramQryCertiMap.put("PAGE_INDEX", "1");
		paramQryCertiMap.put("PAGE_SIZE", "10");
		try {
			Map<String, Object> respQryCertiMap = this.custCertiAtomicService.qryCertiInfo(paramQryCertiMap);
			List respQryCertiUseMap = (List) respQryCertiMap.get("Cust_Certi");
			if ((!StringUtils.isEmpty(respQryCertiUseMap)) && (respQryCertiUseMap.size() > 0)) {
				repCustMap.put("CERTI_INST", respQryCertiUseMap);
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员证件信息查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String, Object> paramQryCustBusiMap = new HashMap<String, Object>();
		paramQryCustBusiMap.put("CUST_ID", reqQryCustId);
		paramQryCustBusiMap.put("STATUS_CD_TWO", "1000,1098");
		paramQryCustBusiMap.put("PAGE_INDEX", "1");
		paramQryCustBusiMap.put("PAGE_SIZE", "10");
		try {
			Map<String, Object> respQryCustBusiMap = this.custBusiAtomicService.qryCustBusiInfo(paramQryCustBusiMap);
			List respQryCustBusiUseMap = (List) respQryCustBusiMap.get("QRY_RESULTS");
			if ((!StringUtils.isEmpty(respQryCustBusiUseMap)) && (respQryCustBusiUseMap.size() > 0)) {
				repCustMap.put("BUSI_INST", respQryCustBusiUseMap);
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员业务信息查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String, Object> paramQryVitMap = new HashMap<String, Object>();
		paramQryVitMap.put("CUST_ID", reqQryCustId);
		paramQryVitMap.put("PAGE_INDEX", "1");
		paramQryVitMap.put("PAGE_SIZE", "10");

		String vitValue = "";
		try {
			Map<String, Object> respQryVitMap = this.infoVitAtomicService.qrySumInfoVit(paramQryVitMap);
			if (!StringUtils.isEmpty(respQryVitMap)) {
				vitValue = String.valueOf(respQryVitMap.get("SUM_VIT"));
			}
			if ((null != vitValue) && (!"".equals(vitValue)))
				repCustMap.put("VIT_VALUE", vitValue);
		} catch (AtomicServiceException e) {
			this.logger.error("会员活力值查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String, Object> paramQryCustBusiOfferMap = new HashMap<String, Object>();
		paramQryCustBusiOfferMap.put("CUST_ID", reqQryCustId);
		paramQryCustBusiOfferMap.put("OFFER_NAME", "170业务");
		paramQryCustBusiOfferMap.put("STATUS_CD", "1000");
		paramQryCustBusiOfferMap.put("PAGE_INDEX", "1");
		paramQryCustBusiOfferMap.put("PAGE_SIZE", "10");
		try {
			Map<String, Object> respQryCustBusiOfferMap = this.custBusiAtomicService
					.qryCustBusiInfo(paramQryCustBusiOfferMap);
			List respQryCustBusiOfferUseMap = (List) respQryCustBusiOfferMap.get("QRY_RESULTS");
			if ((null != respQryCustBusiOfferUseMap) && (respQryCustBusiOfferUseMap.size() > 0)) {
				qryMobile170 = (String) ((Map) respQryCustBusiOfferUseMap.get(0)).get("BUSI_NBR");
				if ((null != qryMobile170) && (!"".equals(qryMobile170)))
					repCustMap.put("Mobile_170", qryMobile170);
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员业务信息查询失败！", e);
			throw new BusiServiceException(e);
		}
		Map<String, Object> custHeadMap = new HashMap<String, Object>();
		custHeadMap.put("CUST_ID", reqQryCustId);
		custHeadMap.put("STATUS_CD", "1000");
		try {
			Map<String, Object> respCustHeadInfoMap = this.custHeadInfoAtomicService.custHeadInfoQuery(custHeadMap);
			if (((Integer) respCustHeadInfoMap.get("cust_size")).intValue() > 0) {
				List respCustHeadInfoList = (List) respCustHeadInfoMap.get("cust_list");
				if ((null != respCustHeadInfoList) && (respCustHeadInfoList.size() > 0))
					repCustMap.put("CUST_HEAD", ((Map) respCustHeadInfoList.get(0)).get("FILE_PATH"));
			}
		} catch (AtomicServiceException e) {
			this.logger.error("会员头像查询异常！", e);
			throw new BusiServiceException(e);
		}
		RESP resp = RESP.createSuccessResp();
		respMap.put("RESP", resp);
		respMap.put("CUST", repCustMap);
		return respMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> qryCustOuter(Map<String, Object> paramOuterMap) throws BusiServiceException {
		Map<String, Object> respOutMap = new HashMap<String, Object>();
		Map<String, Object> retCustQryMap = new HashMap<String, Object>();

		String qryCustId = "";

		if (StringUtils.isEmpty(paramOuterMap)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "入参不能为空！");
			respOutMap.put("RESP", resp);
			return respOutMap;
		}

		String reqChannelCode = (String) paramOuterMap.get("CHANNEL_CODE");
		String reqExtSystem = (String) paramOuterMap.get("EXT_SYSTEM");
		String reqCustId = (String) paramOuterMap.get("CUST_ID");
		String reqQryType = (String) paramOuterMap.get("QRY_TYPE");
		String reqQryNumber = String.valueOf(paramOuterMap.get("QRY_NUMBER"));

		if (StringUtils.isEmpty(reqChannelCode)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "渠道编码不能为空！");
			respOutMap.put("RESP", resp);
			return respOutMap;
		}
		if (StringUtils.isEmpty(reqExtSystem)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "接入系统标识不能为空！");
			respOutMap.put("RESP", resp);
			return respOutMap;
		}
		if ((StringUtils.isEmpty(reqCustId)) && (StringUtils.isEmpty(reqQryType))
				&& (StringUtils.isEmpty(reqQryNumber))) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "查询值不能同时为空！");
			respOutMap.put("RESP", resp);
			return respOutMap;
		}

		Map<String, Object> paramCustFlagMap1 = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(reqCustId)) {
			reqQryType = "4";
			reqQryNumber = reqCustId;
		} else if ("106".equals(reqExtSystem)) {
			reqQryType = "6";
		}

		paramCustFlagMap1.put("QRY_TYPE", reqQryType);
		paramCustFlagMap1.put("QRY_NUMBER", reqQryNumber);
		paramCustFlagMap1.put("PAGE_INDEX", "1");
		paramCustFlagMap1.put("PAGE_SIZE", "10");
		try {
			Map<String, Object> respCustFlagMap1 = qryCustFlag(paramCustFlagMap1);
			if (!StringUtils.isEmpty(respCustFlagMap1.get("CUST_ID"))) {
				qryCustId = (String) respCustFlagMap1.get("CUST_ID");
			} else {
				RESP resp1 = (RESP) respCustFlagMap1.get("RESP");
				RESP resp = RESP.createFailResp("2", resp1.getMsg());
				respOutMap.put("RESP", resp);
				return respOutMap;
			}
		} catch (BusiServiceException e) {
			this.logger.error("调用会员标识查询业务服务失败！", e);
			throw e;
		}
		Map<String, Object> qryCustRespMap;
		Map<String, Object> paramQryCustOutMap = new HashMap<String, Object>();
		Map<String, Object> paramQryCustInMap = new HashMap<String, Object>();
		paramQryCustInMap.put("CUST_ID", qryCustId);
		paramQryCustOutMap.put("QRY_CONDITION", paramQryCustInMap);
		try {
			qryCustRespMap = qryCust(paramQryCustOutMap);
		} catch (BusiServiceException e) {

			this.logger.error("调用会员资料查询业务服务失败！", e);
			throw e;
		}
		boolean isCertiExistFlag = false;

		if (!StringUtils.isEmpty(qryCustRespMap)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> retQryCustMap = (Map<String, Object>) qryCustRespMap.get("CUST");
			if (!StringUtils.isEmpty(retQryCustMap)) {
				List qryCertiListMap = (List) retQryCustMap.get("CERTI_INST");

				if ((!StringUtils.isEmpty(qryCertiListMap)) && (qryCertiListMap.size() > 0)) {
					for (int i = 0; i < qryCertiListMap.size(); i++) {
						if (("10".equals(((Map<String, Object>) qryCertiListMap.get(i)).get("CERTI_TYPE").toString()))
								&& ("1000".equals(
										((Map<String, Object>) qryCertiListMap.get(i)).get("STATUS_CD").toString()))) {
							isCertiExistFlag = true;

							retCustQryMap.put("CERTI_TYPE", "10");
							retCustQryMap.put("CERTI_NBR",
									((Map<String, Object>) qryCertiListMap.get(i)).get("CERTI_NBR"));
							retCustQryMap.put("CERTI_ADDR",
									((Map<String, Object>) qryCertiListMap.get(i)).get("CERTI_ADDR"));
							retCustQryMap.put("AUTH_FLAG",
									((Map<String, Object>) qryCertiListMap.get(i)).get("AUTH_FLAG"));
							break;
						}
					}
				}

				if ("106".equals(reqExtSystem)) {
					List<Map<String, Object>> qryBusiListMap = (List<Map<String, Object>>) retQryCustMap
							.get("BUSI_INST");
					if ((!StringUtils.isEmpty(qryBusiListMap)) && (qryBusiListMap.size() > 0)) {
						for (int i = 0; i < qryBusiListMap.size(); i++) {
							String busi_name = ((String) ((Map<String, Object>) qryBusiListMap.get(i)).get("BUSI_NAME"))
									.trim();
							if ("百度联名业务".equals(busi_name)) {
								retQryCustMap.put("CUST_VIP_LEVEL", "1999");
							} else if (busi_name.indexOf("随影卡业务") != -1) {
								retQryCustMap.put("CUST_VIP_LEVEL", "1998");
								break;
							}
						}
					}
				}
				if (retQryCustMap.containsKey("BUSI_INST")) {
					Map<String, Object> offerMap = new HashMap<String, Object>();
					List<Map<String, Object>> busiList = (List<Map<String, Object>>) retQryCustMap.get("BUSI_INST");
					// 增加判断查询类型为2和6的时候返回参数
					if (busiList.size() > 0) {
						for (Map<String, Object> busiMap : busiList) {
							if ("2".equals(reqQryType) || "6".equals(reqQryType)) {
								if (reqQryNumber.equals(busiMap.get("CARD_NBR"))) {
									retCustQryMap.put("L_PROD_OFFER_INST", retQryCustMap.get("BUSI_INST")); // 增加了个返回订购业务参数
								} else {
									retCustQryMap.put("L_PROD_OFFER_INST", "");
								}
							} else {
								offerMap.put("OFFER_ID", busiMap.get("OFFER_ID"));// 业务标识
								offerMap.put("BUSI_NAME", busiMap.get("BUSI_NAME"));// 业务名称
								offerMap.put("BUSI_NBR", busiMap.get("BUSI_NBR"));// 业务号码
								offerMap.put("CREATE_DATE", busiMap.get("CRT_DATE"));// 创建时间
								offerMap.put("VALID_DATE", busiMap.get("EFF_DATE"));// 生效时间
								offerMap.put("INVALID_DATE", busiMap.get("EXP_DATE"));// 失效时间
								offerMap.put("CARD_ID", busiMap.get("CARD_ID"));// 会员卡号标识
								offerMap.put("CARD_NAME", busiMap.get("CARD_NAME"));// 会员卡名称
								offerMap.put("CARD_NBR", busiMap.get("CARD_NBR"));// 会员卡号
								retCustQryMap.put("PROD_OFFER_INST", offerMap); // 增加了个返回订购业务参数
							}
						}
					}
					retCustQryMap.put("PROD_OFFER_INST", ""); // 若查询不到返回空
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "输出参数缺少BUSI_INST列表");
					respOutMap.put("RESP", resp);
					return respOutMap;
				}
				retCustQryMap.put("CUST_NBR", retQryCustMap.get("CUST_NBR"));
				retCustQryMap.put("CUST_NAME", retQryCustMap.get("CUST_NAME"));
				retCustQryMap.put("NICK_NAME", retQryCustMap.get("NICK_NAME"));
				retCustQryMap.put("CUST_VIP_LEVEL", retQryCustMap.get("CUST_VIP_LEVEL"));
				retCustQryMap.put("SEX", retQryCustMap.get("SEX"));
				retCustQryMap.put("CONTACT_ADDR", retQryCustMap.get("CONTACT_ADDR"));
				retCustQryMap.put("CUST_ID", retQryCustMap.get("CUST_ID"));
				retCustQryMap.put("CUST_TYPE", retQryCustMap.get("CUST_TYPE"));
				retQryCustMap.put("VIT_VALUE", retQryCustMap.get("VIT_VALUE"));

				if (!isCertiExistFlag) {
					retCustQryMap.put("CERTI_TYPE", "");
					retCustQryMap.put("CERTI_NBR", "");
					retCustQryMap.put("CERTI_ADDR", "");
					retCustQryMap.put("AUTH_FLAG", "");
				}

				retCustQryMap.put("MOBILE", retQryCustMap.get("MOBILE"));
				retCustQryMap.put("MOBILE_170", retQryCustMap.get("MOBILE_170"));

				retCustQryMap.put("EMAIL", retQryCustMap.get("EMAIL"));
				retCustQryMap.put("BIRTHDATE", retQryCustMap.get("BIRTHDATE"));
				retCustQryMap.put("BLACKLIST", retQryCustMap.get("BLACKLIST"));
				retCustQryMap.put("JOIN_DATE", retQryCustMap.get("JOIN_DATE"));
				retCustQryMap.put("CHANNEL_CODE", retQryCustMap.get("CHANNEL_CODE"));

				retCustQryMap.put("PAY_PWD", "".equals(retQryCustMap.get("PAY_PWD")) ? "0" : "1");
				retCustQryMap.put("LOGON_NBR", "".equals(retQryCustMap.get("LOGIN_PWD")) ? "0" : "1");
			} else {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "未查询到相关会员资料信息！");
				respOutMap.put("RESP", resp);
				return respOutMap;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> payMap = (Map<String, Object>) qryCustRespMap.get("CUST");
			Map<String, Object> params = new HashMap<String, Object>();
			if ("120".equals(reqExtSystem)) {
				this.logger.info("调用百度余额查询服务开始！");
				// params.put("CUST_ID", qryCustId);
				params.put("PAY_ID", payMap.get("PAY_ID"));
				params.put("SYSTEM_ID", reqExtSystem);
				Map<String, Object> resultMap;
				try {
					resultMap = this.acctBalanceAtomicService.balanceQuery(params);
				} catch (AtomicServiceException e) {
					this.logger.error("调用帐户渠道支付余额查询原子服务失败", e);
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "调用帐户渠道支付余额查询原子服务失败！");
					respOutMap.put("RESP", resp);
					return respOutMap;
				}
				@SuppressWarnings("unchecked")
				List<AcctBalance> acctBalance = (List<AcctBalance>) resultMap.get("ACCT_BALANCE");
				if ((acctBalance == null) || (acctBalance.isEmpty())) {
					RESP resp = RESP.createSuccessResp(Constants.STATE_CODE_SUCC, "查询成功");
					respOutMap.put("RESP", resp.toString());
					retCustQryMap.put("CASH", String.format("%2.2f", new Object[] { Double.valueOf(0.0D) }));
					respOutMap.put("CUST", retCustQryMap);
					return respOutMap;
				}
				assembleTotal(acctBalance, respOutMap);
				RESP resp = RESP.createSuccessResp();
				respOutMap.put("RESP", resp);
			} else {
				RESP resp = RESP.createSuccessResp();
				respOutMap.put("RESP", resp);
				respOutMap.put("CUST", retCustQryMap);
			}
		} else {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "未查询到相关会员资料信息！");
			respOutMap.put("RESP", resp);
			return respOutMap;
		}
		// if (null != this.balQryExtServ) {
		// String reqJson =
		// "{\"SOO\":[{\"BALANCEQRY_REQ\":{\"BalType\":\"1\",\"MemberId\":\"" +
		// qryCustId
		// + "\",\"QRY_TYPE\":\"1\",\"SystemID\":\"" + reqExtSystem
		// + "\"},\"PUB_REQ\":{\"TYPE\":\"BALANCE_QRY\"}}]}";
		// this.logger.info("余额接口服务请求报文： " +
		// JSONObject.fromObject(reqJson).toString());
		// JSONObject resBalQryMap =
		// this.balQryExtServ.baiduBalQuery(JSONObject.fromObject(reqJson));
		// 请求esb平台调用百度余额查询服务
		return respOutMap;
	}

	/**
	 * 获取会员的总余额余额
	 *
	 * @param acctBalance
	 * @param result
	 */
	public void assembleTotal(List<AcctBalance> acctBalance, Map<String, Object> result) {
		Map<String, Object> resultAssem = new HashMap<String, Object>();
		long cash = 0L;
		for (int i = 0; i < acctBalance.size(); i++) {
			long uniteTypeId = ((AcctBalance) acctBalance.get(i)).getUNITE_TYPE_ID();
			long balanceTypeId = ((AcctBalance) acctBalance.get(i)).getBALANCE_TYPE_ID();
			if ((uniteTypeId == 0L) && (balanceTypeId != 2L)) {
				cash += ((AcctBalance) acctBalance.get(i)).getREAL_BALANCE();
			}

		}
		resultAssem.put("CASH", String.format("%2.2f", new Object[] { Double.valueOf(cash * 0.01D) }) + "");
		result.put("CUST", resultAssem);
	}
	// String resultJson = HttpToEsb.httpToEsb(reqJson, "132",
	// "35a3e06e-2628-463d-9336-b7b72b77ad54",
	// "/ServiceBus/custView/cust/BalanceQry", ConfigConstants.BalanceQryURL);
	// JSONObject resBalQryMap = JSONObject.fromObject(resultJson);
	// this.logger.info("余额接口服务返回报文： " + resBalQryMap.toString());
	// if (!StringUtils.isEmpty(resultJson)) {
	// JSONObject sooObject =
	// JSONObject.fromObject(resBalQryMap.get("SvcCont"));
	// JSONArray sooArray = JSONArray.fromObject(sooObject.get("SOO"));
	// JSONObject sooJSON = JSONObject.fromObject(sooArray.getJSONObject(0));
	// Object rInfo = sooJSON.get("RESP");
	// JSONObject retJson = JSONObject.fromObject(rInfo);
	//
	// String result = retJson.getString("Result");
	// if (Constants.STATE_CODE_SUCC.equals(result)) {
	// Object cashInfo = sooJSON.get("CASH_COIN_BALANCE");
	// JSONObject retCashJson = JSONObject.fromObject(cashInfo);
	// retCustQryMap.put("CASH", retCashJson.getString("CASH"));
	// } else {
	// RESP resp = RESP.createFailResp(retJson.getString("Code"),
	// retJson.getString("Msg"));
	// respOutMap.put("RESP", resp);
	// return respOutMap;
	// }
	// }
	// } else {
	// RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
	// "百度余额查询返回失败或为空");
	// respOutMap.put("RESP", resp);
	// return respOutMap;
	// }
	// this.logger.info("调用百度余额查询接口服务结束！");
	//
	// RESP resp = RESP.createSuccessResp();
	// respOutMap.put("RESP", resp);
	// respOutMap.put("CUST", retCustQryMap);
	// return respOutMap;
	// }

	@SuppressWarnings("null")
	public Map<String, Object> qryFee(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();

		String reqQryOfferId = (String) param.get("OFFER_ID");
		String qryPageIndex = (String) param.get("PAGE_INDEX");
		String qryPageSize = (String) param.get("PAGE_SIZE");
		if ((null == reqQryOfferId) || ("".equals(reqQryOfferId))) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "业务费用查询必填入参不能为空！");

			respMap.put("RESP", resp);
			return respMap;
		}

		Map<String, Object> paramQryOfferMap = new HashMap<String, Object>();
		Map<String, Object> result;
		paramQryOfferMap.put("ORDER_ID", reqQryOfferId);
		paramQryOfferMap.put("PAGE_INDEX", qryPageIndex);
		paramQryOfferMap.put("PAGE_SIZE", qryPageSize);
		try {
			result = this.codeOfferAtomicService.qryCodeOffer(paramQryOfferMap);
		} catch (Exception e) {
			this.logger.error("业务费用查询失败！", e);
			throw new BusiServiceException(e);
		}
		@SuppressWarnings("unchecked")
		List<Object> respQryOfferMap = (List<Object>) result.get("Code_Offer");
		if ((respQryOfferMap == null) && (respQryOfferMap.size() > 0)) {
			RESP resp = RESP.createFailResp("3", "业务参数错误！");
			respMap.put("RESP", resp);
			return respMap;
		}
		double totalFee = 0.0D;
		for (int i = 0; i < respQryOfferMap.size(); i++) {
			totalFee = totalFee
					+ Double.parseDouble(((BigDecimal) ((Map) respQryOfferMap.get(i)).get("OFFER_PRICE")).toString());
		}
		RESP resp = RESP.createSuccessResp();
		respMap.put("RESP", resp);
		respMap.put("SUM", Double.valueOf(totalFee));

		resp = RESP.createSuccessResp();
		respMap.put("RESP", resp);
		respMap.put("ORDER_ID", respQryOfferMap);
		return respMap;
	}

	public Map<String, Object> qryCustFlag(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();

		String qrytype = (String) param.get("QRY_TYPE");
		String qrynum = (String) param.get("QRY_NUMBER");
		String qryPageIndex = (String) param.get("PAGE_INDEX");
		String qryPageSize = (String) param.get("PAGE_SIZE");
		if ((null == qrytype) || ("".equals(qrytype))) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "QRY_TYPE不能为空！");

			respMap.put("RESP", resp);
			return respMap;
		}
		if ((null == qrynum) || ("".equals(qrynum))) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_ONE, "QRY_NUM不能为空！");

			respMap.put("RESP", resp);
			return respMap;
		}
		Map<String, Object> paramQryFlagMap = new HashMap<String, Object>();
		Map<String, Object> respQryCustIdMap;
		paramQryFlagMap.put("PAGE_INDEX", qryPageIndex);
		paramQryFlagMap.put("PAGE_SIZE", qryPageSize);

		if ("1".equals(qrytype)) {
			try {
				paramQryFlagMap.put("CONTACT_MOBILE", qrynum);
				paramQryFlagMap.put("STATUS_CD_TWO", "1000,1098");
				paramQryFlagMap.put("BondCust", "BondCust");

				respQryCustIdMap = this.custAtomicService.qryCustInfo(paramQryFlagMap);
			} catch (Exception e) {
				this.logger.error("根据手机号码查询CUST_ID失败！", e);
				throw new BusiServiceException(e);
			}
			if (respQryCustIdMap != null) {
				List respQryCustId = (List) respQryCustIdMap.get("CUST");

				if ((respQryCustId != null) && (respQryCustId.size() > 0)) {
					String CUST_ID = (String) ((Map) respQryCustId.get(0)).get("CUST_ID");

					respMap.put("CUST_ID", CUST_ID);
				} else {
					RESP resp = RESP.createFailResp("2", "根据手机号码查询不到CUST_ID！");
					respMap.put("RESP", resp);
					return respMap;
				}
			}
		} else if ("2".equals(qrytype)) {
			try {
				paramQryFlagMap.put("CUST_NBR", qrynum);
				paramQryFlagMap.put("STATUS_CD_TWO",
						Constants.CUST_STATUS_EFFECTIVE + "," + Constants.CUST_STATUS_STAY);
				paramQryFlagMap.put("BondCust", "BondCust");

				respQryCustIdMap = this.custAtomicService.qryCustInfo(paramQryFlagMap);
			} catch (Exception e) {

				this.logger.error("根据会员卡号查询CUST_ID失败！", e);
				throw new BusiServiceException(e);
			}
			if (respQryCustIdMap != null) {
				// String svnCountTotal =
				// respQryCustIdMap.get("COUNT_TOTAL").toString();

				List respQryCustId = (List) respQryCustIdMap.get("CUST");

				if ((respQryCustId != null) && (respQryCustId.size() > 0)) {
					String CUST_ID = (String) ((Map) respQryCustId.get(0)).get("CUST_ID");

					respMap.put("CUST_ID", CUST_ID);
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "根据会员卡号查询不到CUST_ID！");
					respMap.put("RESP", resp);
					return respMap;
				}
			}
		} else if ("3".equals(qrytype)) {
			try {
				paramQryFlagMap.put("LOGIN_USER", qrynum);
				paramQryFlagMap.put("STATUS_CD_TWO", "1000,1098");

				respQryCustIdMap = this.infoLoginUserAtomicService.qryInfoLoginUserForCheck(paramQryFlagMap);
			} catch (Exception e) {
				this.logger.error("根据线上登录账号查询CUST_ID失败！", e);
				throw new BusiServiceException(e);
			}
			if (respQryCustIdMap != null) {
				// String svnCountTotal =
				// respQryCustIdMap.get("COUNT_TOTAL").toString();

				List respQryCustId = (List) respQryCustIdMap.get("CUST");

				if ((respQryCustId != null) && (respQryCustId.size() > 0)) {
					String CUST_ID = (String) ((Map) respQryCustId.get(0)).get("CUST_ID");

					respMap.put("CUST_ID", CUST_ID);
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "根据线上登录账号查询不到CUST_ID！");

					respMap.put("RESP", resp);
					return respMap;
				}
			}
		} else if ("4".equals(qrytype)) {
			try {
				paramQryFlagMap.put("CUST_ID", qrynum);
				paramQryFlagMap.put("STATUS_CD_TWO", "1000,1098");
				paramQryFlagMap.put("BondCust", "BondCust");

				respQryCustIdMap = this.custAtomicService.qryCustInfo(paramQryFlagMap);
			} catch (Exception e) {
				this.logger.error("根据会员编码查询CUST_ID失败！", e);
				throw new BusiServiceException(e);
			}
			if (respQryCustIdMap != null) {
				// String svnCountTotal =
				// respQryCustIdMap.get("COUNT_TOTAL").toString();

				List respQryCustId = (List) respQryCustIdMap.get("CUST");

				if ((respQryCustId != null) && (respQryCustId.size() > 0)) {
					String CUST_ID = (String) ((Map) respQryCustId.get(0)).get("CUST_ID");

					respMap.put("CUST_ID", CUST_ID);
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "根据会员编码查询不到CUST_ID！");

					respMap.put("RESP", resp);
					return respMap;
				}
			}
		} else if ("5".equals(qrytype)) {
			try {
				paramQryFlagMap.put("CERTI_NBR", qrynum);
				paramQryFlagMap.put("STATUS_CD_TWO", "1000,1098");
				paramQryFlagMap.put("CERTI_TYPE", "10");

				respQryCustIdMap = this.custCertiAtomicService.qryCertiInfo(paramQryFlagMap);
			} catch (Exception e) {
				this.logger.error("根据会员证件号码查询CUST_ID失败！", e);
				throw new BusiServiceException(e);
			}
			if (respQryCustIdMap != null) {
				// String svnCountTotal =
				// respQryCustIdMap.get("COUNT_TOTAL").toString();

				List respQryCustId = (List) respQryCustIdMap.get("Cust_Certi");

				if ((respQryCustId != null) && (respQryCustId.size() > 0)) {
					String CUST_ID = (String) ((Map) respQryCustId.get(0)).get("CUST_ID");

					respMap.put("CUST_ID", CUST_ID);
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "根据会员证件号码查询不到CUST_ID！");

					respMap.put("RESP", resp);
					return respMap;
				}
			}
		} else {
			if ("6".equals(qrytype)) {
				try {
					respMap = queryCustInfoByBusiNbr(qrytype, paramQryFlagMap, qrynum);
				} catch (Exception e) {
					this.logger.error("业务号码查询会员标识异常" + e);
					throw new BusiServiceException(e);
				}
				return respMap;
			}
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "QRY_TYPE类型不存在！");

			respMap.put("RESP", resp);
			return respMap;
		}
		RESP resp = RESP.createSuccessResp();
		respMap.put("RESP", resp);
		return respMap;
	}

	private Map<String, Object> queryCustInfoByBusiNbr(String qrytype, Map<String, Object> paramQryFlagMap,
			String qrynum) throws BusiServiceException {
		Map<String, Object> respQryCustIdMap = new HashMap<String, Object>();
		List list = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String custId = null;
		try {
			paramQryFlagMap.put("BUSI_NBR", qrynum);
			paramQryFlagMap.put("STATUS_CD_TWO", Constants.CUST_STATUS_EFFECTIVE + "," + Constants.CUST_STATUS_STAY);
			respQryCustIdMap = this.custBusiAtomicService.qryCustBusiInfo(paramQryFlagMap);

			list = (List) respQryCustIdMap.get("QRY_RESULTS");
			if ((list != null) && (list.size() > 0)) {
				custId = (String) ((Map) list.get(0)).get("CUST_ID");
				if (!StringUtils.isEmpty(custId)) {
					resultMap.put("CUST_ID", custId);
					RESP resp = RESP.createSuccessResp();
					resultMap.put("RESP", resp);
					return resultMap;
				}

			}

			paramQryFlagMap.remove("BUSI_NBR");
			paramQryFlagMap.put("CUST_NBR", qrynum);
			paramQryFlagMap.put("STATUS_CD_TWO", Constants.CUST_STATUS_EFFECTIVE + "," + Constants.CUST_STATUS_STAY);

			respQryCustIdMap = this.custAtomicService.qryCustInfo(paramQryFlagMap);

			list = (List) respQryCustIdMap.get("CUST");
			if ((list != null) && (list.size() > 0)) {
				custId = (String) ((Map) list.get(0)).get("CUST_ID");
				if (!StringUtils.isEmpty(custId)) {
					resultMap.put("CUST_ID", custId);
					RESP resp = RESP.createSuccessResp();
					resultMap.put("RESP", resp);
					return resultMap;
				}

			}

			paramQryFlagMap.remove("CUST_NBR");
			paramQryFlagMap.put("LOGIN_USER", qrynum);
			paramQryFlagMap.put("STATUS_CD_TWO", Constants.CUST_STATUS_EFFECTIVE + "," + Constants.CUST_STATUS_STAY);

			respQryCustIdMap = this.infoLoginUserAtomicService.qryInfoLoginUserForCheck(paramQryFlagMap);

			list = (List) respQryCustIdMap.get("CUST");
			if ((list != null) && (list.size() > 0)) {
				custId = (String) ((Map) list.get(0)).get("CUST_ID");
				if (!StringUtils.isEmpty(custId)) {
					resultMap.put("CUST_ID", custId);
					RESP resp = RESP.createSuccessResp();
					resultMap.put("RESP", resp);
					return resultMap;
				}
			}
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "查询值不存在！");

			resultMap.put("RESP", resp);
			return resultMap;
		} catch (Exception e) {
			this.logger.error("会员卡号+业务号码+线上登录账号查询异常" + e);
			throw new BusiServiceException(e);
		}
	}

	public Map<String, Object> qryCustBusiInfo(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();

		String qryCustId = (String) param.get("CUST_ID");

		String qryPageIndex = (String) param.get("PAGE_INDEX");
		String qryPageSize = (String) param.get("PAGE_SIZE");
		if ((null == qryCustId) || ("".equals(qryCustId))) {
			RESP resp = RESP.createFailResp("1", "会员标识查询必填入参不能为空！");

			respMap.put("RESP", resp);
			return respMap;
		}
		Map<String, Object> paramQryCustBusiMap = new HashMap<String, Object>();
		paramQryCustBusiMap.put("CUST_ID", qryCustId);

		paramQryCustBusiMap.put("PAGE_INDEX", qryPageIndex);
		paramQryCustBusiMap.put("PAGE_SIZE", qryPageSize);
		List<Object> respQryCustBusiUseMap;
		Map<String, Object> respQryCustBusiMap;
		try {
			respQryCustBusiMap = this.custBusiAtomicService.qryCustBusiInfo(paramQryCustBusiMap);
			respQryCustBusiUseMap = (List) respQryCustBusiMap.get("QRY_RESULTS");
		} catch (AtomicServiceException e) {
			this.logger.error("会员业务信息查询失败！", e);
			throw new BusiServiceException(e);
		}

		if ((!StringUtils.isEmpty(respQryCustBusiUseMap)) && (respQryCustBusiUseMap.size() > 0)) {
			respMap.put("BUSI_INST", respQryCustBusiUseMap);
			respMap.put("svnCountTotal", respQryCustBusiMap.get("COUNT_TOTAL"));
		} else {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_TWO, "业务信息不存在");
			respMap.put("RESP", resp);
			return respMap;
		}

		RESP resp = RESP.createSuccessResp();
		respMap.put("RESP", resp);
		return respMap;
	}

	/**
	 * 查询会员基本资料
	 */
	@Override
	public Map<String, Object> qryCustInfo(Map<String, Object> paramMap) throws SmiBusinessException {
		Map<String, Object> reusltMap = null;
		Map<String, Object> reusltMaps = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try {
			reusltMap = this.custAtomicService.qryCustInfo(paramMap);
			if (!reusltMap.isEmpty()) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> custList = (List<Map<String, Object>>) reusltMap.get("CUST");
				if (custList.size() > 0) {
					for (Map<String, Object> custMap : custList) {
					Map<String, Object> returnMap = new HashMap<String, Object>();
						checkParam(custMap, returnMap, "CUST_ID", "CUST_NAME", "CUST_NBR", "ORG_ID", "CUST_VIP_LEVEL",
								"CONTACT_MOBILE", "SEX", "BIRTHDATE", "CRT_DATE");
						returnList.add(returnMap);
					}
				} 
				reusltMaps.put("PAGE_SIZE", reusltMap.get("PAGE_SIZE"));
				reusltMaps.put("TOTAL", reusltMap.get("TOTAL"));
				reusltMaps.put("TOTAL_PAGE", reusltMap.get("TOTAL_PAGE"));
				reusltMaps.put("CUST_INFO_LIST", returnList);
			}
		} catch (AtomicServiceException e) {
			this.logger.error("调用会员基本资料原子服务异常", e);
			throw new SmiBusinessException("999", "调用会员基本资料原子服务异常", e.getMessage());
		}
		return reusltMaps;
	}

	/**
	 * 参数校验
	 * 
	 * @param paramMap
	 * @param resultMap
	 * @param returnMap
	 * @param args
	 * @return
	 */
	private void checkParam(Map<String, Object> resultMap, Map<String, Object> returnMap, String... args) {
		Set<String> set = (Set<String>) resultMap.keySet();
		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < set.size(); j++) {
				if (set.contains(args[i])) {
					returnMap.put(args[i], resultMap.get(args[i]));
				} else {
					returnMap.put(args[i], "");
				}

			}
		}
	}

	@Override
	public List<Map<String, Object>> getCustIdByloginMobile(String loginMobiles) {
		return custInfoMapper.getCustIdByloginMobile(loginMobiles.split(","));
	}
}
