package com.smilife.bcp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smilife.bcp.dto.common.EQryType;
import com.smilife.bcp.dto.request.CustUpdateReq;
import com.smilife.bcp.dto.request.PwdUpdateReq;
import com.smilife.bcp.dto.response.CustDto;
import com.smilife.bcp.dto.response.LoginRepDto;
import com.smilife.bcp.dto.response.MemberInfoResp;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.smilife.bcp.service.common.BusinessHandler;
import com.smilife.bcp.service.common.BusinessHandler2;
import com.tydic.eshop.model.order.CrmNumberInfo;
import com.tydic.eshop.model.order.CrmOrderCommAttr;
import com.tydic.eshop.model.order.CrmOrderCommodity;
import com.tydic.eshop.model.order.CrmOrderCust;
import com.tydic.eshop.model.order.CrmOrderFeeItem;
import com.tydic.eshop.model.order.CrmOrderInst;
import com.tydic.eshop.model.order.CrmOrderOfferInst;
import com.tydic.eshop.model.order.CrmOrderPayment;
import com.tydic.eshop.model.order.CrmOrderProdAttr;
import com.tydic.eshop.model.order.CrmOrderProdInst;
import com.tydic.eshop.model.order.CrmOrderReceiveInfo;
import com.tydic.eshop.model.order.ReqCrmOrder;
import com.tydic.framework.base.exception.ServiceException;
import com.tydic.framework.util.PropertyUtil;
import com.tydic.shop.constant.InterfaceConstant;
import com.tydic.shop.dto.req.ReqMemberInfo;
import com.tydic.shop.dto.req.ReqUserRegister;
import com.tydic.shop.util.CrmInvokeUtil;

/**
 * CRM接口管理实现类
 * 
 * @author liz 
 * 2015-8-25
 */
@Service
public class UserManageConnectorImpl implements UserManageConnector {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private CrmInvokeUtil crmInvokeUtil;
	private BusinessHandler2 businessHandler2 = new BusinessHandler2();
	
	//定义系统常量
	private static final String ERROR = "1";
	private static final String PWD = "PWD"; 
	private static final String RESP = "RESP"; 
	private static final String CUST = "CUST";
	private static final String ACC_NBR = "ACC_NBR";
	private static final String CUST_ID = "CUST_ID";
	private static final String CUST_PAY = "CUST_PAY";
	private static final String CUST_QRY = "CUST_QRY";
	private static final String EXT_SYSTEM = "EXT_SYSTEM";
	private static final String CHANNEL_CODE = "CHANNEL_CODE";
	private static final String SALE_ORDER_ID = "SALE_ORDER_ID";
	private static final String TOKEN = "TOKEN";
	private static final String SOO = "SOO";
	private static final String KEY = "KEY";
	private static final String VALUE = "VALUE";
	private static final String TYPE = "TYPE";
	private static final String CUST_LOGIN = "CUST_LOGIN";
	
	/**
	 * 通过BCP调用CRM进行注册
	 * 
	 * @param account 会员账号
	 * @param password 会员密码
	 * @return 会员ID
	 */
	@Override
	public String register(String account, String password) {
		ReqUserRegister user = null;
		ResultDTO resultDTO = null;
		String custId = null;
		try {
			// 组装请求对象及参数
			user = new ReqUserRegister();
			user.setUserAccount(account);
			user.setPassword(password);
			// 封装报文
			List<Map<String, Object>> mapList = BusinessHandler.handParam(user, null, CUST);
			// 调用服务获得返回结果
			custId = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_MEMBER_ADD, mapList, CUST_ID, String.class);

		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}

		return custId;
	}

	/**
	 * 通过BCP调用CRM进行账户校验
	 * 
	 * @param account 会员账号
	 * @return CRM返回对象
	 */
	@Override
	public ResultDTO isExist(String account) {
		ResultDTO resultDTO = null;
		// 组装请求对象及参数
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put(CHANNEL_CODE, PropertyUtil.getProperty(CHANNEL_CODE));
		reqMap.put(EXT_SYSTEM, PropertyUtil.getProperty(EXT_SYSTEM));
		reqMap.put(ACC_NBR, account);
		// 封装报文
		List<Map<String, Object>> mapList = BusinessHandler.handParam(reqMap, null, CUST);
		try {
			// 调用服务获得返回结果
			resultDTO = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_ACCOUNT_VALIDATE, mapList, RESP, ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}
		return resultDTO;
	}

	/**
	 * 通过BCP调用CRM进行会员资料查询
	 * 
	 * @param custId 会员ID
	 * @return 会员资料
	 */
	@Override
	public MemberInfoResp queryMemberInfo(String custId) {
		// 组装请求对象及参数
		ReqMemberInfo reqMemberInfo = new ReqMemberInfo();
		reqMemberInfo.setCustId(custId);
		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(reqMemberInfo, null, CUST_QRY);
		try {
			// 调用服务获得返回结果
			return crmInvokeUtil.invokeList(InterfaceConstant.BIC_QUERY_MEMBER_INFO, params, CUST, MemberInfoResp.class)
					.getResult().get(0);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
		}
		return null;
	}
	

	@Override
	public MemberInfoResp queryMemberInfo(EQryType qryType, String qryValue) {
		// 组装请求对象及参数
		ReqMemberInfo reqMemberInfo = new ReqMemberInfo();
		reqMemberInfo.setQryType(qryType.getQryType());
		reqMemberInfo.setQryNumber(qryValue);
		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(reqMemberInfo, null, CUST_QRY);
		try {
			// 调用服务获得返回结果
			return crmInvokeUtil.invokeList(InterfaceConstant.BIC_QUERY_MEMBER_INFO, params, CUST, MemberInfoResp.class).getResult().get(0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	
	/**
	 * 通过BCP调用CRM进行会员资料修改
	 * 
	 * @param req 会员资料
	 * @return CRM返回对象
	 */
	@Override
	public ResultDTO updateCust(CustUpdateReq req) {
		ResultDTO resultDTO = null;
		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(req, null, CUST);
		try {
			// 调用服务获得返回结果
			resultDTO = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_CUST_UPDATE, params, RESP, ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}
		return resultDTO;
	}

	/**
	 * 通过BCP调用CRM进行密码修改
	 * 
	 * @param custId 会员ID
	 * @param oldPassword 原密码
	 * @param newPassword 新密码
	 * @param resetType 确认密码
	 * @param pwdType 密码类型
	 * @return CRM返回对象
	 */
	@Override
	public ResultDTO updatePwd(String custId, String oldPassword, String newPassword, String resetType, String pwdType) {
		ResultDTO resultDTO = null;
		// 组装请求对象及参数
		PwdUpdateReq pwdUpdateReq = new PwdUpdateReq();
		pwdUpdateReq.setCustId(custId);
		pwdUpdateReq.setOldPwd(oldPassword);
		pwdUpdateReq.setNewPwd(newPassword);
		pwdUpdateReq.setPwdType(pwdType); // 1，登录密码 2，服务密码（支付密码）
		pwdUpdateReq.setResetType(resetType); // 1:修改3:重置 2:找回密码
		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(pwdUpdateReq, null, CUST);

		try {
			resultDTO = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_RESET_PWD, params, RESP, ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}

		return resultDTO;
	}
	
	/**
	 * 通过BCP调用CRM进行支付密码校验
	 * 
	 * @param custId 会员ID
	 * @param payPassword 支付密码
	 * @return CRM返回对象
	 */
	@Override
	public ResultDTO payPwdCheck(String custId, String payPassword) {
		ResultDTO resultDTO = null;
		// 组装请求对象及参数
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put(CHANNEL_CODE, PropertyUtil.getProperty(CHANNEL_CODE));
		reqMap.put(EXT_SYSTEM, PropertyUtil.getProperty(EXT_SYSTEM));
		reqMap.put(CUST_ID, custId);
		reqMap.put(PWD, payPassword);
		// 封装报文
		List<Map<String, Object>> mapList = BusinessHandler.handParam(reqMap, null, CUST_PAY);
		try {
			// 调用服务获得返回结果
			resultDTO = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_PAY_PWD_CHECK, mapList, RESP, ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}
		return resultDTO;
	}
	
	/**
	 * 通过BCP调用CRM进行订单保存
	 * 
	 * @param reqCrmOrder crm订单对象
	 * @return crm订单号
	 */
	@Override
	public String saveCrmOrder(ReqCrmOrder reqCrmOrder) {
		
		String crmOrderId = null;
		List<Map<String, Object>> soo = new ArrayList<Map<String, Object>>();
		List<CrmOrderInst> orderInst = reqCrmOrder.getOrderInst();
		List<CrmOrderCommodity> commodity = reqCrmOrder.getCommodity();
		List<CrmOrderCommAttr> commAttr = reqCrmOrder.getCommAttr();
		List<CrmOrderOfferInst> orderOfferInst = reqCrmOrder.getOrderOfferInst();
		List<CrmOrderFeeItem> feeItem = reqCrmOrder.getFeeItem();
		List<CrmOrderCust> orderCust = reqCrmOrder.getOrderCust();
		List<CrmOrderPayment> payment = reqCrmOrder.getPayment();
		List<CrmOrderProdInst> prodInst = reqCrmOrder.getProdInst();
		List<CrmOrderProdAttr> prodAttr = reqCrmOrder.getProdAttr();
		List<CrmOrderReceiveInfo> receiveInfo = reqCrmOrder.getReceiveInfo();
		List<CrmNumberInfo> numberInfo = reqCrmOrder.getNumberInfo();
		
		if (orderInst != null) {
			soo.add(businessHandler2.handParam(orderInst, CrmOrderInst.TYPE, CrmOrderInst.ATT_KEY_NAME));
		}
		if (commodity != null) {
			soo.add(businessHandler2.handParam(commodity, CrmOrderCommodity.TYPE, CrmOrderCommodity.ATT_KEY_NAME));
		}
		if (commAttr != null) {
			soo.add(businessHandler2.handParam(commAttr, CrmOrderCommAttr.TYPE, CrmOrderCommAttr.ATT_KEY_NAME));
		}
		if (orderOfferInst != null) {
			soo.add(businessHandler2.handParam(orderOfferInst, CrmOrderOfferInst.TYPE, CrmOrderOfferInst.ATT_KEY_NAME));
		}
		if (feeItem != null) {
			soo.add(businessHandler2.handParam(feeItem, CrmOrderFeeItem.TYPE, CrmOrderFeeItem.ATT_KEY_NAME));
		}
		if (orderCust != null) {
			soo.add(businessHandler2.handParam(orderCust, CrmOrderCust.TYPE, CrmOrderCust.ATT_KEY_NAME));
		}
		if (payment != null) {
			soo.add(businessHandler2.handParam(payment, CrmOrderPayment.TYPE, CrmOrderPayment.ATT_KEY_NAME));
		}
		if (prodInst != null) {
			soo.add(businessHandler2.handParam(prodInst, CrmOrderProdInst.TYPE, CrmOrderProdInst.ATT_KEY_NAME));
		}
		if (prodAttr != null) {
			soo.add(businessHandler2.handParam(prodAttr, CrmOrderProdAttr.TYPE, CrmOrderProdAttr.ATT_KEY_NAME));
		}
		if (receiveInfo != null) {
			soo.add(businessHandler2.handParam(receiveInfo, CrmOrderReceiveInfo.TYPE, CrmOrderReceiveInfo.ATT_KEY_NAME));
		}
		if (CollectionUtils.isNotEmpty(numberInfo)) {
			soo.add(businessHandler2.handParam(numberInfo, CrmNumberInfo.TYPE, CrmOrderReceiveInfo.ATT_KEY_NAME));
		}
		try {
			crmOrderId = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_SAVE_ORDER, soo, SALE_ORDER_ID, String.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
		}
		return crmOrderId;
	}

	@Override
	public CustDto getSessionUser(String token) {
		CustDto userDto = null;
		// 组装请求对象及参数
		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(TOKEN, token);
		
		params.add(reqMap);
		try {
			// 调用服务获得返回结果
			userDto = crmInvokeUtil.invokeObj("BIC_SESSION_USER", params, "CUST", CustDto.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
		}
		return userDto;
	}

	@Override
	public ResultDTO saveSessionOrder(String custId, String orderForm) {
		ResultDTO resultDTO = null;
		// 组装请求对象及参数
		List<Map<String, Object>> SOO = new ArrayList<Map<String, Object>>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(TYPE, "1");
		reqMap.put(KEY, custId);
		reqMap.put(VALUE, orderForm);
		
		SOO.add(reqMap);
		try {
			// 调用服务获得返回结果
			resultDTO = crmInvokeUtil.invokeObj("BIC_SESSION_ORDERFORM", SOO, RESP, ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
		}
		return resultDTO;
	}

	@Override
	public String getSessionOrder(String custId) {
		String orderFormDTO = null;
		// 组装请求对象及参数
		List<Map<String, Object>> SOO = new ArrayList<Map<String, Object>>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put(TYPE, "2");
		reqMap.put(KEY, custId);
		
		SOO.add(reqMap);
		try {
			// 调用服务获得返回结果
			orderFormDTO = crmInvokeUtil.invokeObj("BIC_SESSION_ORDERFORM", SOO, VALUE, String.class);
			
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
		}
		return orderFormDTO;
	}

	@Override
	public LoginRepDto login(String loginName, String password) {
		LoginRepDto dto = null;
		// 组装请求对象及参数
		Map<String, String> requestMap = new HashMap<String, String>();
		
		requestMap.put("LOGIN_NBR", loginName);
		requestMap.put("PWD", password);
		// 判断是否为邮箱
		if (!loginName.contains("@")) {
			requestMap.put("LOGIN_TYPE", "1");
		} else {
			requestMap.put("LOGIN_TYPE", "2");
		}
		requestMap.put("EXT_SYSTEM", PropertyUtil.getProperty("EXT_SYSTEM"));
		requestMap.put("CHANNEL_CODE", PropertyUtil.getProperty("CHANNEL_CODE"));
		
		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(requestMap, null, CUST_LOGIN);
		
		// 调用服务获得返回结果
		try {
			dto = crmInvokeUtil.invokeObj(InterfaceConstant.BIC_MEMBER_LOGIN, params, "", LoginRepDto.class);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			dto = new LoginRepDto();
			ResultDTO resultDTO=new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult(ERROR);
			dto.setResp(resultDTO);
		}
		return dto;
	}

	
	
}
