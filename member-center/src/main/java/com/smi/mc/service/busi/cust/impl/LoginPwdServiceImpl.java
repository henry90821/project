package com.smi.mc.service.busi.cust.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smi.mc.common.MemberCenterConfiguration;
import com.smi.mc.constants.BusiConstants;
import com.smi.mc.constants.Constants;
import com.smi.mc.dao.cust.InfoLoginUserMapper;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.model.cust.InfoLoginUser;
import com.smi.mc.model.cust.InfoLoginUserExample;
import com.smi.mc.model.cust.InfoLoginUserExample.Criteria;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;
import com.smi.mc.service.atomic.cust.OrgOpAtomicService;
import com.smi.mc.service.busi.cust.LoginPwdService;
import com.smi.mc.service.busi.cust.OrderService;
import com.smi.mc.utils.Common;
import com.smi.mc.utils.MD5Utils;
import com.smi.tools.kits.StrKit;

@Service("loginPwdService")
public class LoginPwdServiceImpl implements LoginPwdService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustAtomicService custAtomicService;

	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;

	@Autowired
	protected OrderService orderService;

	@Autowired
	private OrgOpAtomicService orgOpAtomicService;

	@Autowired
	private InfoLoginUserMapper infoLoginUserMapper;

	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;

	/**
	 * 密码验证
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> checkLoginPwd(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respQryCustMap = new HashMap<String, Object>();
		Map<String, Object> respMap = new HashMap<String, Object>();
		Map<String, Object> custInfo2 = new HashMap<String, Object>();
		Date startDate = new Date();
		RESP resp = null;
		String reqPwd = null;
		try {
			Map<String, Object> checkmap = Common.checkParams(param,
					new String[] { "CUST_ID", "PWD_TYPE", "PWD", "CHANNEL_CODE"});
			if ("1".equals(checkmap.get("STATUS"))) {
				resp = RESP.createFailResp("1100", (String) checkmap.get("MESSAGE"));
				respMap.put("RESP", resp);
				return respMap;
			}
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("CUST_ID", param.get("CUST_ID"));
				respQryCustMap = this.custAtomicService.qryCustInfo(map);
			} catch (Exception e) {
				this.logger.error("密码验证查询会员异常", e);
				throw new BusiServiceException("密码验证查询会员异常");
			}

			if ((null == respQryCustMap) || (null == respQryCustMap.get("CUST"))
					|| (((List<Map<String, Object>>) respQryCustMap.get("CUST")).isEmpty())) {
				resp = RESP.createFailResp("1091", "根据会员标识查询不到会员资料！");
				respMap.put("RESP", resp);
				return respMap;
			}
			// 根据custId查询账户对应的随机数
			// InfoLoginUserExample infoLoginUserExample = new
			// InfoLoginUserExample();
			// Criteria createCriteria = infoLoginUserExample.createCriteria();
			// createCriteria.andCustIdEqualTo((String) param.get("CUST_ID"));
			// createCriteria.andStatusCdEqualTo("1000");
			// List<InfoLoginUser> selectByExample =
			// this.infoLoginUserMapper.selectByExample(infoLoginUserExample);
			// if (selectByExample.isEmpty() || selectByExample.size() == 0) {
			// resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE,
			// "查询该账户不存在");
			// respMap.put("RESP", resp);
			// return respMap;
			// }
			// InfoLoginUser infoUser = selectByExample.get(0);
			List<Map<String,Object>> listMap=(List<Map<String,Object>>)respQryCustMap.get("CUST");
			Map<String,Object> respQryMap=listMap.get(0);
			if (!respQryMap.containsKey("RANDOM_NUMBER")) {
				resp = RESP.createFailResp("999", "查询该用户的密码随机数为空");
				respMap.put("RESP", resp);
				return respMap;
			}
			String randomNum = (String) respQryMap.get("RANDOM_NUMBER");
			if ("1".equals(param.get("PWD_TYPE"))) {
				Map<String, Object> custInfo = ((List<Map<String, Object>>) respQryCustMap.get("CUST")).get(0);
				if ((null == custInfo.get("LOGIN_PWD")) || ("".equals(custInfo.get("LOGIN_PWD")))) {
					resp = RESP.createFailResp("1194", "登录密码不存在");
					respMap.put("RESP", resp);
					return respMap;
				} 
				String qryLoginPwd = (String) custInfo.get("LOGIN_PWD");
				reqPwd = (String) param.get("PWD");
				// 设置登录密码是否加随机数的逻辑
				if ("true".equals(this.memberCenterConfiguration.getOpenRndnumEncryption())) {
					String pwd = (String) param.get("PWD") + randomNum;
					reqPwd = MD5Utils.MD5(pwd.replaceAll("\r|\n", ""));
				}
				if (!qryLoginPwd.equals(reqPwd)) {
					resp = RESP.createFailResp("1100", "登录密码不正确");
					respMap.put("RESP", resp);
					return respMap;
				}
				resp = RESP.createSuccessResp();
			} else {
				Map<String, Object> custInfo = ((List<Map<String, Object>>) respQryCustMap.get("CUST")).get(0);
				if ((null == custInfo.get("PAY_PWD")) || ("".equals(custInfo.get("PAY_PWD")))) {
					resp = RESP.createFailResp("1100", "支付密码不存在");
					respMap.put("RESP", resp);
					return respMap;
				}
				String qryPayPwd = (String) custInfo.get("PAY_PWD");
				custInfo2.put("CUST_ID", custInfo.get("CUST_ID"));
				custInfo2.put("CUST_NAME", custInfo.get("CUST_NAME"));
				if (!qryPayPwd.equals(param.get("PWD"))) {
					resp = RESP.createFailResp("1193", "支付密码不正确");
					respMap.put("RESP", resp);
					return respMap;
				}
				resp = RESP.createSuccessResp();
				respMap.put("CUST", custInfo2);
			}
		} catch (Exception e) {
			this.logger.error("密码验证异常", e);
			throw new BusiServiceException("密码验证异常");
		} finally {
			Date endDate = new Date();
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("REQUEST", param.toString());
			logMap.put("REQ_DATE", startDate);
			logMap.put("RESPONSE", respMap.toString());
			logMap.put("RES_DATE", endDate);
			logMap.put("REQ_SYSTEM", param.get("EXT_SYSTEM"));
			logMap.put("RES_SYSTEM", "101");
			logMap.put("INTERFACE_CODE", param.get("CHANNEL_CODE"));
			try {
				this.logInterfaceAtomicService.addLogInterface(logMap);
			} catch (Exception e) {
				this.logger.info("调用日志新增服务失败！ " + e.getMessage());
			}
		}
		respMap.put("RESP", resp);
		return respMap;
	}

	/**
	 * 设置支付密码
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> setPayPwd(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		RESP resp = null;
		try {
			reqMap.put("CUST_ID", param.get("custId"));
			// 根据custid查询用户
			Map<String, Object> custInfo = this.custAtomicService.qryCustInfo(reqMap);
			if ((null == custInfo) || (null == custInfo.get("CUST"))
					|| (((List<Map<String, Object>>) custInfo.get("CUST")).isEmpty())) {
				resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "查询该用户不存在");
				respMap.put("RESP", resp);
				return respMap;
			}
			List<Map<String, Object>> custList = (List<Map<String, Object>>) custInfo.get("CUST");
			Map<String, Object> custMap = custList.get(0);
			if (custMap.containsKey("PAY_PWD")) {
				resp = RESP.createSuccessResp(Constants.EXCEPTION_CODE_ONE, "该用户已设置支付密码");
				respMap.put("RESP", resp);
				return respMap;
			}
			reqMap.put("PAY_PWD", param.get("payPwd"));
			// 设置支付密码
			this.custAtomicService.updCust(reqMap);
			resp = RESP.createSuccessResp();
			respMap.put("RESP", resp);
		} catch (Exception e) {
			this.logger.error("设置支付密码异常", e);
			resp = RESP.createFailResp(Constants.EXCEPTION_CODE, "设置支付密码异常");
		}
		return respMap;
	}

	/**
	 * 该账户是否设置了支付密码
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public Map<String, Object> isSetPayPwd(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();
		RESP resp = null;
		try {
			if (!param.isEmpty()) {
				Map<String, Object> custInfo = this.custAtomicService.qryCustInfo(param);
				if ((null == custInfo) || (null == custInfo.get("CUST"))
						|| (((List<Map<String, Object>>) custInfo.get("CUST")).isEmpty())) {
					resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "查询该用户不存在");
					respMap.put("RESP", resp);
					return respMap;
				}
				List<Map<String, Object>> custList = (List<Map<String, Object>>) custInfo.get("CUST");
				Map<String, Object> custMap = custList.get(0);
				if (custMap.containsKey("PAY_PWD")) {
					String payPwd = (String) custMap.get("PAY_PWD");
					if (StrKit.isNotBlank(payPwd)) {
						resp = RESP.createSuccessResp(Constants.EXCEPTION_CODE_ONE, "该用户已设置支付密码");
						respMap.put("RESP", resp);
					} else {
						resp = RESP.createSuccessResp(Constants.EXCEPTION_CODE_TWO, "该用户未设置支付密码");
						respMap.put("RESP", resp);
					}
				} else {
					resp = RESP.createSuccessResp(Constants.EXCEPTION_CODE_TWO, "该用户未设置支付密码");
					respMap.put("RESP", resp);
				}
			}
		} catch (Exception e) {
			this.logger.error("验证是否设置了支付密码异常", e);
			resp.createFailResp(Constants.EXCEPTION_CODE, "验证是否设置了支付密码异常");
			respMap.put("RESP", resp);
		}
		return respMap;
	}

	/**
	 * 修改or重置密码
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public Map<String, Object> restOrUpdatePwd(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> respMap = new HashMap<String, Object>();
		Map<String, Object> loginMap = new HashMap<String, Object>();
		Date startDate = new Date();
		RESP resp = null;
		try {
			this.logger.info("传入的参数：" + param.toString());
			Map<String, Object> checkmap = Common.checkParams(param,
					new String[] { "CUST_ID", "PWD_TYPE", "REST_TYPE", "CHANNEL_CODE" });
			if ("1".equals(checkmap.get("STATUS"))) {
				resp = RESP.createFailResp("1100", (String) checkmap.get("MESSAGE"));
				respMap.put("RESP", resp);
				return respMap;
			}
			String custId = (String) param.get("CUST_ID");
			String restType = (String) param.get("REST_TYPE");
			String pwdType = (String) param.get("PWD_TYPE");
			String channelCode = (String) param.get("CHANNEL_CODE");
			String extSystem = (String) param.get("EXT_SYSTEM");
			String qryCreateOpeId = this.orgOpAtomicService.querySysUId(channelCode);
			if (StrKit.isBlank(qryCreateOpeId)) {
				resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "该渠道号不存在");
				respMap.put("RESP", resp);
				return respMap;
			}
			// 根据custId查询账户对应的随机数
			Map<String, Object> respQryCustMap;
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("CUST_ID", custId);
				respQryCustMap = this.custAtomicService.qryCustInfo(map);
			} catch (Exception e) {
				this.logger.error("密码验证查询会员异常", e);
				throw new BusiServiceException("密码验证查询会员异常");
			}

			if ((null == respQryCustMap) || (null == respQryCustMap.get("CUST"))
					|| (((List<Map<String, Object>>) respQryCustMap.get("CUST")).isEmpty())) {
				resp = RESP.createFailResp("1091", "根据会员标识查询不到会员资料！");
				respMap.put("RESP", resp);
				return respMap;
			}
			List<Map<String,Object>> listMap=(List<Map<String,Object>>)respQryCustMap.get("CUST");
			Map<String,Object> respQryMap=listMap.get(0);
			if (!respQryMap.containsKey("RANDOM_NUMBER")) {
				resp = RESP.createFailResp("999", "查询该用户的密码随机数为空");
				respMap.put("RESP", resp);
				return respMap;
			}
			String randomNum = (String) respQryMap.get("RANDOM_NUMBER");
			if ("1".equals(restType)) {
				String oldPwd = (String) param.get("OLD_PWD");
				if (StrKit.isBlank(oldPwd)) {
					resp = RESP.createFailResp(Constants.EXCEPTION_CODE_THREE, "老密码为空");
					respMap.put("RESP", resp);
					return respMap;
				}
				param.put("PWD", oldPwd);
				Map<String, Object> respCheckMap = this.checkLoginPwd(param);
				RESP resp1 = (RESP) respCheckMap.get("RESP");
				String code = resp1.getCode();
				if (!Constants.STATE_CODE_SUCC.equals(code)) {
					respMap.put("RESP", resp1);
					return respMap;
				}
			}
			String loginpwd = null;
			loginMap.put("CUST_ID", param.get("CUST_ID"));
			String newPwd = (String) param.get("NEW_PWD");
			loginpwd = newPwd;
			if (BusiConstants.CUST_LOGON_PWD.equals(pwdType)) {
				// 设置登录密码是否加随机数的逻辑
				if ("true".equals(this.memberCenterConfiguration.getOpenRndnumEncryption())) {
					// 存入数据库密码=Md5(传入的登录密码+随机数)
					String pwdScrpt = newPwd + randomNum;
					loginpwd = MD5Utils.MD5(pwdScrpt.replaceAll("\r|\n", ""));
				}
				loginMap.put("LOGIN_PWD", loginpwd);
			} else if (BusiConstants.CUST_PAY_PWD.equals(pwdType)) {
				loginMap.put("PAY_PWD", param.get("NEW_PWD"));
			}
			this.custAtomicService.updCust(loginMap);
			resp = RESP.createSuccessResp();
			respMap.put("RESP", resp);
			Map<String, Object> paramOrderAddReqOuterMap = new HashMap<String, Object>();
			Map<String, Object> paramOrderAddReqInnerMap = new HashMap<String, Object>();
			paramOrderAddReqInnerMap.put("CUST_ID", param.get("CUST_ID"));
			paramOrderAddReqInnerMap.put("ORDER_TYPE", BusiConstants.CERIT_EFFECTIVE);
			paramOrderAddReqInnerMap.put("CREATE_OPERATOR_ID", qryCreateOpeId);
			paramOrderAddReqInnerMap.put("CREATE_CHANNEL_ID", channelCode);
			paramOrderAddReqInnerMap.put("EXT_SYSTEM", extSystem);
			paramOrderAddReqInnerMap.put("BUSINESS_TYPE", "13");
			paramOrderAddReqInnerMap.put("SERV_CODE", BusiConstants.SERV_CODE_UPDATE_PWD);

			Map<String, Object> paramAddOrderChildReqMap = new HashMap<String, Object>();
			List<Map<String, Object>> paramAddOrderChildReqListMap = new ArrayList<Map<String, Object>>();
			paramAddOrderChildReqMap.put("OBJ_TYPE", BusiConstants.SERV_CODE_UPDATE_PWD);
			paramAddOrderChildReqMap.put("OBJ_ID", "reqCustId");
			paramAddOrderChildReqListMap.add(paramAddOrderChildReqMap);
			paramOrderAddReqInnerMap.put("ADD_ORDER_CHILD_REQ", paramAddOrderChildReqListMap);
			paramOrderAddReqOuterMap.put("ADD_ORDER_REQ", paramOrderAddReqInnerMap);
			try {
				this.orderService.addOrders(paramOrderAddReqOuterMap);
			} catch (BusiServiceException e) {
				this.logger.error("调用新增日志业务服务失败！", e);
				throw new BusiServiceException("调用新增日志业务服务失败！");
			}
		} catch (Exception e) {
			this.logger.error("修改or重置密码异常" + e);
			throw new BusiServiceException("修改or重置密码异常");
		} finally {
			Date endDate = new Date();
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("REQUEST", param.toString());
			logMap.put("REQ_DATE", startDate);
			logMap.put("RESPONSE", respMap.toString());
			logMap.put("RES_DATE", endDate);
			logMap.put("REQ_SYSTEM", param.get("EXT_SYSTEM"));
			logMap.put("RES_SYSTEM", "101");
			logMap.put("INTERFACE_CODE", param.get("CHANNEL_CODE"));
			try {
				this.logInterfaceAtomicService.addLogInterface(logMap);
			} catch (Exception e) {
				this.logger.info("调用日志新增服务失败！ " + e.getMessage());
			}
		}
		return respMap;
	}

}