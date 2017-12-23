/*package com.smi.mc.service.busi.cust.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smi.mc.common.AtomicServiceException;
import com.smi.mc.common.BusiServiceException;
import com.smi.mc.constants.BusiConstants;
import com.smi.mc.constants.Constants;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.billing.SmsAtomicService;
import com.smi.mc.service.atomic.cust.CodeOfferAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustBusiAtomicService;
import com.smi.mc.service.atomic.cust.OrgOpAtomicService;
import com.smi.mc.service.busi.cust.CustPwdUpdService;
import com.smi.mc.service.busi.cust.LoginPwdService;
import com.smi.mc.service.busi.cust.OrderService;
import com.smi.mc.utils.Common;
import com.smi.mc.utils.MD5Utils;
import com.tydic.unicom.crm.busi.po.CUST;
import com.tydic.unicom.crm.busi.po.PubOutputDto;
import com.tydic.unicom.crm.busi.service.interfaces.CustPwdServ;

@Service("CustPwdUpdService")
public class CustPwdUpdServiceImpl implements CustPwdUpdService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CustAtomicService custAtomicService;

	@Autowired
	protected LoginPwdService loginPwdService;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected OrgOpAtomicService orgOpAtomicService;

	@Autowired
	SmsAtomicService smsAtomicService;

	@Autowired
	CodeOfferAtomicService codeOfferAtomicService;

	@Autowired
	CustBusiAtomicService custBusiAtomicService;

	@Autowired
	CustPwdServ custPwdServ;

	public Map<String, Object> modifyAndResetCustPwd(Map<String, Object> param) throws BusiServiceException {
		this.logger.info("密码修改、重置对内接口服务入参报文： " + param);
		Map<String, Object> respMap = new HashMap<String, Object>();
		String qryCreateOpeId = "";

		if (null == param) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"会员查询入参不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}

		Map<String,Object> paramCustMap = (Map<String,Object>) param.get("CUST");

		String reqChannelCode = (String) paramCustMap.get("CHANNEL_CODE");
		String reqExtSystem = (String) paramCustMap.get("EXT_SYSTEM");
		String reqCustId = (String) paramCustMap.get("CUST_ID");
		String reqOldPwds = (String) paramCustMap.get("OLD_PWD");
		String reqNewPwds = (String) paramCustMap.get("NEW_PWD");
		String reqPwdType = (String) paramCustMap.get("PWD_TYPE");
		String reqResetType = (String) paramCustMap.get("RESET_TYPE");

		//增加的代码
		String reqOldPwd=reqOldPwds;
		String reqNewPwd=reqNewPwds;
		if (StringUtils.isEmpty(reqChannelCode)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"入参渠道编码不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(reqExtSystem)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"入参接入系统标识不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(reqCustId)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"入参用户唯一标识不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(reqPwdType)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"入参密码类型不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}
		if (StringUtils.isEmpty(reqResetType)) {
			RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
					"入参更新类型不能为空！");
			respMap.put("RESP", resp);
			return respMap;
		}

		Map<String, Object> praCustMap = new HashMap<String, Object>();
		praCustMap.put("CUST_ID", reqCustId);
		praCustMap.put("PAGE_INDEX", "1");
		praCustMap.put("PAGE_SIZE", "10");

		String contactMobile = "";
		try {
			Map<String, Object> qryCustMap = this.custAtomicService.qryCustInfo(praCustMap);
			if ((!StringUtils.isEmpty(qryCustMap)) && (!StringUtils.isEmpty(qryCustMap.get("CUST")))) {
				List<Map<String, Object>> qryCustList = (List<Map<String, Object>>) qryCustMap.get("CUST");
				if (qryCustList.size() > 0) {
					contactMobile = (String) ((Map) qryCustList.get(0)).get("CONTACT_MOBILE");
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
							"会员资料不存在！");
					respMap.put("RESP", resp);
					return respMap;
				}
			} else {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"会员资料不存在！");
				respMap.put("RESP", resp);
				return respMap;
			}
		} catch (Exception e) {
			this.logger.error("会员基本资料查询失败！", e);
			throw new BusiServiceException(e);
		}
		try {
			qryCreateOpeId = this.orgOpAtomicService.querySysUId(reqChannelCode);
		} catch (Exception e) {
			this.logger.error("员工号原子服务查询失败！", e);
			throw new BusiServiceException(e);
		}

		boolean is170 = false;

		Map<String, Object> mapInput = new HashMap<String, Object>();
		Map<String, Object> mapResp = new HashMap<String, Object>();
		try {
			mapInput.put("OFFER_NAME", "170业务");
			mapResp = this.codeOfferAtomicService.qryCodeOfferByOffername(mapInput);
		} catch (Exception e) {
			this.logger.error("调用会员业务原子服务失败", e);
			throw new BusiServiceException("1", "999", "系统内部错误");
		}

		List offerlists = (List) mapResp.get("CODE_OFFER");

		mapInput.clear();
		mapResp.clear();

		if ((offerlists != null) && (offerlists.size() > 0) && (((Map) offerlists.get(0)).get("OFFER_ID") != null)
				&& (!"".equals(((Map) offerlists.get(0)).get("OFFER_ID")))) {
			mapInput.put("CUST_ID", reqCustId);
			mapInput.put("OFFER_ID", ((Map) offerlists.get(0)).get("OFFER_ID"));
			try {
				mapResp = this.custBusiAtomicService.qryCustBusiInfo(mapInput);
			} catch (Exception e) {
				this.logger.error("调用会员业务查询原子服务失败", e);
				throw new BusiServiceException("1", "999", "业务查询失败");
			}

			List results = (List) mapResp.get("QRY_RESULTS");

			if ((results != null) && (results.size() > 0)) {
				is170 = true;
			}

		}

		if ("1".equals(reqResetType)) {
			if (StringUtils.isEmpty(reqOldPwd)) {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"老密码不能为空！");
				respMap.put("RESP", resp);
				return respMap;
			}
			if (StringUtils.isEmpty(reqNewPwd)) {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"新密码不能为空！");
				respMap.put("RESP", resp);
				return respMap;
			}

			Map<String, Object> paramCheckLoginMap = new HashMap<String, Object>();
			Map<String, Object> respCheckPwdMap;
			paramCheckLoginMap.put("CUST_ID", reqCustId);
			paramCheckLoginMap.put("PWD_TYPE", reqPwdType);
			paramCheckLoginMap.put("PWD", reqOldPwd);
			try {
				respCheckPwdMap = this.loginPwdService.checkLoginPwd(paramCheckLoginMap);
			} catch (BusiServiceException e) {
				this.logger.error("调用密码验证业务服务失败！", e);
				throw e;
			}
			RESP respResp = (RESP) respCheckPwdMap.get("RESP");
			String respResult = respResp.getResult();

			if (Constants.STATE_CODE_SUCC.equals(respResult)) {
				if (BusiConstants.CUST_LOGON_PWD.equals(reqPwdType)) {
					Map<String, Object> updCustLoginPwdMap1 = new HashMap<String, Object>();
					updCustLoginPwdMap1.put("CUST_ID", reqCustId);
					updCustLoginPwdMap1.put("LOGIN_PWD", reqNewPwd);
					try {
						this.custAtomicService.updCust(updCustLoginPwdMap1);
					} catch (Exception e) {
						this.logger.error("登录密码更新失败！", e);
						throw new BusiServiceException(e);
					}

					Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
					Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
					paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
					paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
					paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
					paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
					paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
					paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
					paramOrderAddReqInnerMap.put("SERV_CODE", BusiConstants.SERV_CODE_UPDATE_PWD);

					Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
					List paramAddOrderChildReqListMap = new ArrayList();
					paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
					paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
					paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
					paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

					paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
					try {
						this.orderService.addOrders(paramOrderAddReqOuterMap);
					} catch (BusiServiceException e) {
						this.logger.error("调用新增日志业务服务失败！", e);
						throw e;
					}
				} else if ("2".equals(reqPwdType)) {
					Map updCustPayPwdMap1 = new HashMap();
					updCustPayPwdMap1.put("CUST_ID", reqCustId);
					updCustPayPwdMap1.put("PAY_PWD", reqNewPwd);
					try {
						this.custAtomicService.updCust(updCustPayPwdMap1);
					} catch (Exception e) {
						this.logger.error("支付密码更新失败！", e);
						throw new BusiServiceException(e);
					}
					if (is170) {
						CUST cust = new CUST();
						cust.setCUST_ID(reqCustId);
						cust.setOLD_PWD(reqOldPwd);
						cust.setNEW_PWD(reqNewPwd);
						cust.setPWD_TYPE("2");
						cust.setRESET_TYPE("1");
						cust.setCHANNEL_CODE((String) paramCustMap.get("CHANNEL_CODE"));
						cust.setEXT_SYSTEM((String) paramCustMap.get("EXT_SYSTEM"));
						com.tydic.unicom.crm.busi.po.RESP res = new com.tydic.unicom.crm.busi.po.RESP();
						try {
							res = this.custPwdServ.updateCustPwd(cust);
						} catch (PubOutputDto e1) {
							this.logger.error("调用crm修改服务密码失败", e1);
							throw new BusiServiceException("1", "999", "调用crm修改服务密码失败");
						}
						if (!"0".equals(res.getRESULT())) {
							this.logger.error("调用crm修改服务密码失败,返回结果："+res.getMSG());
							throw new BusiServiceException("1", "999", "调用crm修改服务密码失败");
						}

					}

					Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
					Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
					paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
					paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
					paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
					paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
					paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
					paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
					paramOrderAddReqInnerMap.put("SERV_CODE", "104");

					Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
					List paramAddOrderChildReqListMap = new ArrayList();
					paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
					paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
					paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
					paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

					paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
					try {
						this.orderService.addOrders(paramOrderAddReqOuterMap);
					} catch (BusiServiceException e) {
						this.logger.error("调用新增日志业务服务失败！", e);
						throw e;
					}
				} else {
					RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
							"无效操作，密码类型有误！");
					respMap.put("RESP", resp);
					return respMap;
				}
			} else {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"老密码不正确！");
				respMap.put("RESP", resp);
				return respMap;
			}
			RESP resp = RESP.createSuccessResp();
			respMap.put("RESP", resp);
			return respMap;
		}
		if ("2".equals(reqResetType)) {
			if (StringUtils.isEmpty(reqNewPwd)) {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"新密码不能为空！");
				respMap.put("RESP", resp);
				return respMap;
			}

			if ("1".equals(reqPwdType)) {
				Map<String, Object> updCustLoginPwdMap2 = new HashMap<String, Object>();
				updCustLoginPwdMap2.put("CUST_ID", reqCustId);
				updCustLoginPwdMap2.put("LOGIN_PWD", reqNewPwd);
				try {
					this.custAtomicService.updCust(updCustLoginPwdMap2);
				} catch (Exception e) {
					this.logger.error("登录密码更新失败！", e);
					throw new BusiServiceException(e);
				}

				Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
				Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
				paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
				paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
				paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
				paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
				paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
				paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
				paramOrderAddReqInnerMap.put("SERV_CODE", "104");

				Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
				List paramAddOrderChildReqListMap = new ArrayList();
				paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
				paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
				paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
				paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

				paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
				try {
					this.orderService.addOrders(paramOrderAddReqOuterMap);
				} catch (BusiServiceException e) {
					this.logger.error("调用新增日志业务服务失败！", e);
					throw e;
				}
			} else if ("2".equals(reqPwdType)) {
				Map<String, Object> updCustPayPwdMap2 = new HashMap<String, Object>();
				updCustPayPwdMap2.put("CUST_ID", reqCustId);
				updCustPayPwdMap2.put("PAY_PWD", reqNewPwd);
				try {
					this.custAtomicService.updCust(updCustPayPwdMap2);
				} catch (Exception e) {
					this.logger.error("支付密码更新失败！", e);
					throw new BusiServiceException(e);
				}

				if (is170) {
					CUST cust = new CUST();
					cust.setCUST_ID(reqCustId);
					cust.setOLD_PWD(reqOldPwd);
					cust.setNEW_PWD(reqNewPwd);
					cust.setPWD_TYPE("2");
					cust.setRESET_TYPE("3");
					cust.setCHANNEL_CODE((String) paramCustMap.get("CHANNEL_CODE"));
					cust.setEXT_SYSTEM((String) paramCustMap.get("EXT_SYSTEM"));
					com.tydic.unicom.crm.busi.po.RESP res = new com.tydic.unicom.crm.busi.po.RESP();
					try {
						res = this.custPwdServ.updateCustPwd(cust);
					} catch (PubOutputDto e1) {
						this.logger.error("调用crm重置服务密码失败");
						throw new BusiServiceException("1", "999", "调用crm重置服务密码失败");
					}
					if (!"0".equals(res.getRESULT())) {
						this.logger.error("调用crm重置服务密码失败");
						throw new BusiServiceException("1", "999", "调用crm重置服务密码失败");
					}

				}

				Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
				Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
				paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
				paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
				paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
				paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
				paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
				paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
				paramOrderAddReqInnerMap.put("SERV_CODE", "104");

				Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
				List paramAddOrderChildReqListMap = new ArrayList();
				paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
				paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
				paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
				paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

				paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
				try {
					this.orderService.addOrders(paramOrderAddReqOuterMap);
				} catch (BusiServiceException e) {
					this.logger.error("调用新增日志业务服务失败！", e);
					throw e;
				}
			} else {
				RESP resp = RESP.createFailResp("3", "无效操作，密码类型有误！");
				respMap.put("RESP", resp);
				return respMap;
			}
			RESP resp = RESP.createSuccessResp();
			respMap.put("RESP", resp);
			return respMap;
		}
		if ("3".equals(reqResetType)) {
			String randomPwd = Common.genRandomNum(6, "NUM");

			reqNewPwd = MD5Utils.MD5(randomPwd);

			if ("1".equals(reqPwdType)) {
				Map<String, Object> smsParamMap1 = new HashMap<String, Object>();
				smsParamMap1.put("MSISDN_SEND", "10023");
				smsParamMap1.put("MSISDN_RECEIVE", contactMobile);
				smsParamMap1.put("PRIORITY", "123");
				smsParamMap1.put("MESSAGE_TEXT", "|dic.crm.passwordchange|" + randomPwd);
				try {
					smsAtomicService.smsSendInsert(smsParamMap1);
				} catch (AtomicServiceException e) {
					this.logger.error("调用计费短信下发原子服务失败！", e);
					throw new BusiServiceException(e);
				}

				Map<String, Object> updCustLoginPwdMap3 = new HashMap<String, Object>();
				updCustLoginPwdMap3.put("CUST_ID", reqCustId);
				updCustLoginPwdMap3.put("LOGIN_PWD", reqNewPwd);
				try {
					this.custAtomicService.updCust(updCustLoginPwdMap3);
				} catch (Exception e) {
					this.logger.error("登录密码更新失败！", e);
					throw new BusiServiceException(e);
				}

				Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
				Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
				paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
				paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
				paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
				paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
				paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
				paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
				paramOrderAddReqInnerMap.put("SERV_CODE", "104");

				Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
				List paramAddOrderChildReqListMap = new ArrayList();
				paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
				paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
				paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
				paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

				paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
				try {
					this.orderService.addOrders(paramOrderAddReqOuterMap);
				} catch (BusiServiceException e) {
					this.logger.error("调用新增日志业务服务失败！", e);
					throw e;
				}
			} else if ("2".equals(reqPwdType)) {
				Map<String, Object> smsParamMap2 = new HashMap<String, Object>();
				smsParamMap2.put("MSISDN_SEND", "10023");
				smsParamMap2.put("MSISDN_RECEIVE", contactMobile);
				smsParamMap2.put("PRIORITY", "123");
				smsParamMap2.put("MESSAGE_TEXT", "|dic.crm.passwordchange|" + randomPwd);
				try {
					smsAtomicService.smsSendInsert(smsParamMap2);
				} catch (AtomicServiceException e) {
					this.logger.error("调用计费短信下发原子服务失败！", e);
					throw new BusiServiceException(e);
				}

				Map<String, Object> updCustPayPwdMap3 = new HashMap<String, Object>();
				updCustPayPwdMap3.put("CUST_ID", reqCustId);
				updCustPayPwdMap3.put("PAY_PWD", reqNewPwd);
				try {
					this.custAtomicService.updCust(updCustPayPwdMap3);
				} catch (Exception e) {
					this.logger.error("支付密码更新失败！", e);
					throw new BusiServiceException(e);
				}
				if (is170) {
					CUST cust = new CUST();
					cust.setCUST_ID(reqCustId);
					cust.setOLD_PWD(reqOldPwd);
					cust.setNEW_PWD(reqNewPwd);
					cust.setPWD_TYPE("2");
					cust.setRESET_TYPE("2");
					cust.setCHANNEL_CODE((String) paramCustMap.get("CHANNEL_CODE"));
					cust.setEXT_SYSTEM((String) paramCustMap.get("EXT_SYSTEM"));
					com.tydic.unicom.crm.busi.po.RESP res = new com.tydic.unicom.crm.busi.po.RESP();
					try {
						res = this.custPwdServ.updateCustPwd(cust);
					} catch (PubOutputDto e1) {
						this.logger.error("调用crm找回服务密码失败");
						throw new BusiServiceException("1", "999", "调用crm找回服务密码失败");
					}
					if (!"0".equals(res.getRESULT())) {
						this.logger.error("调用crm找回服务密码失败");
						throw new BusiServiceException("1", "999", "调用crm找回服务密码失败");
					}

				}

				Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
				Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
				paramOrderAddReqInnerMap.put("CUST_ID", reqCustId);
				paramOrderAddReqInnerMap.put("ORDER_TYPE", "1000");
				paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
				paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", reqChannelCode);
				paramOrderAddReqInnerMap.put("EXT_SYSTEM", reqExtSystem);
				paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
				paramOrderAddReqInnerMap.put("SERV_CODE", "104");

				Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
				List paramAddOrderChildReqListMap = new ArrayList();
				paramAddOrderChildReqMap.put("OBJ_TYPE", "104");
				paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
				paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
				paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);

				paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
				try {
					this.orderService.addOrders(paramOrderAddReqOuterMap);
				} catch (BusiServiceException e) {
					this.logger.error("调用新增日志业务服务失败！", e);
					throw e;
				}
			} else {
				RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
						"无效操作，密码类型有误！");
				respMap.put("RESP", resp);
				return respMap;
			}
			RESP resp = RESP.createSuccessResp();
			respMap.put("RESP", resp);
			return respMap;
		}
		
		RESP resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
				"无效操作，更新类型有误！");
		respMap.put("RESP", resp);
		return respMap;
	}
}*/