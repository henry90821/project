package com.smi.mc.service.cust.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smi.mc.common.MemberCenterConfiguration;
import com.smi.mc.constants.Constants;
import com.smi.mc.dao.cust.InfoCertiMapper;
import com.smi.mc.dao.cust.InfoCustMapper;
import com.smi.mc.dao.cust.InfoLoginUserMapper;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.model.cust.InfoCerti;
import com.smi.mc.model.cust.InfoCertiExample;
import com.smi.mc.model.cust.InfoCust;
import com.smi.mc.model.cust.InfoCustExample;
import com.smi.mc.model.cust.InfoLoginUser;
import com.smi.mc.model.cust.InfoLoginUserExample;
import com.smi.mc.model.cust.InfoLoginUserExample.Criteria;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.busi.cust.CustRegService;
import com.smi.mc.service.cust.IMemberInfoService;
import com.smi.mc.utils.MD5Utils;
import com.smi.mc.utils.MemberCenterLog;
import com.smi.mc.utils.RedisUtil;
import com.smi.mc.vo.cust.InfoCustVo;
import com.smi.mc.vo.cust.InfoLoginUserVo;
import com.smi.tools.kits.DateKit;
import com.smi.tools.kits.RandomKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessCodeException;
import com.smilife.core.sms.service.ISmsService;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class MemberInfoServiceImpl implements IMemberInfoService{
	
	private static final Logger LOGGER = LoggerUtils.getLogger(MemberInfoServiceImpl.class);
	
	@Autowired
	private InfoCustMapper infoCustMapper;
	
	@Autowired
	private InfoLoginUserMapper infoLoginUserMapper;
	
	@Autowired
	private InfoCertiMapper infoCertiMapper;
	
	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private MemberCenterLog memberCenterLog; 
	
	@Autowired
	private CustRegService custRegService;
	
	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;
	
	@Autowired
	private ISmsService smsService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Override
	public DataVo<InfoCustVo> login(String accountNo, String pwd, String verifiyCode, String loginType,String channelCode) throws Exception {
		DataVo<InfoCustVo> dataVo = new DataVo<InfoCustVo>(); 
		
		if("3".equals(loginType)){
			//手机验证码方式登录
			String code = (String)redisUtil.get(Constants.SMS_LOGIN_VERIFYCODE + accountNo);
			if (verifiyCode.equals(code)) {
				//登录成功，查询是否存在该账户，无则注册
				List<String> custIds = infoLoginUserMapper.getCustIdByLoginUser(accountNo);
				if (custIds.size() > 0) {
					//获取用户信息
					String custId = custIds.get(0);
					InfoCust infocust = infoCustMapper.selectByPrimaryKey(custId);
					InfoCustVo infoCustVo = new InfoCustVo();
					if (infocust != null) {
						BeanUtils.copyProperties(infoCustVo, infocust);
					}
					//获取会员证件信息
					InfoCerti infoCerti = infoCertiMapper.getInfoCertiByCustId(custId);
					if (infoCerti != null) {
						BeanUtils.copyProperty(infoCustVo, "certiType", infoCerti.getCertiType());
						BeanUtils.copyProperty(infoCustVo, "certiNbr", infoCerti.getCertiNbr());
						BeanUtils.copyProperty(infoCustVo, "certiAddr", infoCerti.getCertiAddr());
					}
					dataVo.setData(infoCustVo);
					dataVo.setCode(CodeEnum.SUCCESS);
				}else {
					//注册
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("CHANNEL_CODE", channelCode);
					paramMap.put("EXT_SYSTEM", channelCode);
					paramMap.put("SYSTEM_USER_ID", channelCode);
					paramMap.put("LOGIN_USER", accountNo);
					String rndPwd = MD5Utils.MD5(RandomKit.randomNumbers(6));
					paramMap.put("LOGIN_PWD", rndPwd);
					paramMap.put("PAY_PWD", rndPwd);
					paramMap.put("SMI_SOURCE", "0");
					paramMap.put("CUST_NAME", accountNo);
					paramMap.put("LOGIN_USER_TYPE", "1000");
					Map<String, Object> mapResp = this.custRegService.custReg(paramMap);
					if (((String)mapResp.get("RESULT")).equals(Constants.STATE_CODE_SUCC)) {
						//获取注册后所有信息
						String cust_Id = (String)mapResp.get("CUST_ID");
						InfoCust infocust = infoCustMapper.selectByPrimaryKey(cust_Id);
						InfoCustVo infoCustVo = new InfoCustVo();
						if (infocust != null) {
							BeanUtils.copyProperties(infoCustVo, infocust);
						}
						dataVo.setData(infoCustVo);
						dataVo.setCode(CodeEnum.SUCCESS);
					}else {
						dataVo.setCode(CodeEnum.SERVER_INNER_ERROR);
						dataVo.setMsg("登录失败，原因："+(String)mapResp.get("MSG"));
					}
				}
			}else {
				dataVo.setCode(CodeEnum.REQUEST_ERROR);
				dataVo.setMsg("验证码不正确");
			}
		}else {
			//账号密码登录方式
			List<String> custIds = infoLoginUserMapper.getCustIdByLoginUser(accountNo);
			if (custIds.size() > 0) {
				//数据库info_cust存的随机数
				String custId = custIds.get(0);
				String randomNbr = infoCustMapper.getRndNumByCustId(custId);
				
				if (memberCenterConfiguration.getOpenRndnumEncryption().equals("true")) {
					//开启登录密码逻辑:MD5(密文+随机数)
					pwd = MD5Utils.MD5(pwd + randomNbr) ;
				}
				InfoCustExample infoCustExample = new InfoCustExample();
				com.smi.mc.model.cust.InfoCustExample.Criteria infoCustCreateCriteria = infoCustExample.createCriteria();
				infoCustCreateCriteria.andCustIdEqualTo(custId);
				infoCustCreateCriteria.andLoginPwdEqualTo(pwd);
				infoCustCreateCriteria.andStatusCdEqualTo("1000");
				List<InfoCust> infoCusts = infoCustMapper.selectByExample(infoCustExample);
				if (infoCusts == null || infoCusts.size() == 0) {
					dataVo.setCode(CodeEnum.NOT_LOGGED_IN);
					dataVo.setMsg("密码错误！");
				}else {
					InfoCust infoCust = infoCusts.get(0);
					InfoCustVo infoCustVo = new InfoCustVo();
					if (infoCust != null) {
						BeanUtils.copyProperties(infoCustVo, infoCust);
					}
					//获取会员证件信息
					InfoCerti infoCerti = infoCertiMapper.getInfoCertiByCustId(custId);
					if (infoCerti != null) {
						BeanUtils.copyProperty(infoCustVo, "certiType", infoCerti.getCertiType());
						BeanUtils.copyProperty(infoCustVo, "certiNbr", infoCerti.getCertiNbr());
						BeanUtils.copyProperty(infoCustVo, "certiAddr", infoCerti.getCertiAddr());
					}
					dataVo.setData(infoCustVo);
					dataVo.setCode(CodeEnum.SUCCESS);
				}
			}else {
				dataVo.setCode(CodeEnum.NOT_LOGGED_IN);
				dataVo.setMsg("账号不存在！");
			}
		}
		return dataVo;
	}

	
	@Transactional
	@Override
	public BaseValueObject custUpdate(Map<String, Object> paramMap) {
		BaseValueObject baseValueObject = new BaseValueObject();
		Date date = new Date();
		
		String channelCode = (String)paramMap.get("channelCode");
		String extSystem = (String)paramMap.get("extSystem");
		String email = (String)paramMap.get("email");
		String birthdate = (String)paramMap.get("birthdate");
		String mobile = (String)paramMap.get("mobile");
		String sex = (String)paramMap.get("sex");
		String nickName = (String)paramMap.get("nickName");
		String contactAddr = (String)paramMap.get("contactAddr");
		String custName = (String)paramMap.get("custName");
		String certiNbr = (String)paramMap.get("certiNbr");
		String certiAddr = (String)paramMap.get("certiAddr");
		String certiType = (String)paramMap.get("certiType");
		String custId = (String)paramMap.get("custId");
		
		try {
			/**
			 * 判断是否存在该custId的会员信息
			 */
			InfoCust infoCustVo = infoCustMapper.selectByPrimaryKey(custId);
			if (infoCustVo == null) {
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg("找不到该custId的会员资料信息！");
				return baseValueObject;
			}
			
			/**
			 * 更新info_cust表
			 */
			InfoCust infoCust = new InfoCust();
			infoCust.setEmail(email);
			if (StringUtils.isNotEmpty(birthdate)) {
				infoCust.setBirthdate(DateKit.parseDate(birthdate));
			}
			infoCust.setContactMobile(mobile);
			infoCust.setSex(sex);
			infoCust.setNickName(nickName);
			infoCust.setContactAddr(contactAddr);
			infoCust.setCustName(custName);
			infoCust.setCustId(custId);
			infoCust.setModDate(date);
			infoCustMapper.updateByPrimaryKeySelective(infoCust);
			
			/**
			 * 更新info_certi表：先查询如果有身份证信息则update，否则新增
			 */
			if (StringUtils.isNotEmpty(certiNbr)) {
				InfoCertiExample infoCertiExample = new InfoCertiExample();
				com.smi.mc.model.cust.InfoCertiExample.Criteria createCriteriaShow = infoCertiExample.createCriteria();
				createCriteriaShow.andCustIdEqualTo(custId);
				createCriteriaShow.andStatusCdEqualTo("1000");
				int certiCount = infoCertiMapper.countByExample(infoCertiExample);
				if (certiCount > 0) {
					//更新
					InfoCerti infoCerti = new InfoCerti();
					com.smi.mc.model.cust.InfoCertiExample.Criteria infoCertiCriteria = infoCertiExample.createCriteria();
					infoCertiCriteria.andCustIdEqualTo(custId);
					infoCertiCriteria.andStatusCdEqualTo("1000");
					infoCerti.setCustId(custId);
					infoCerti.setCertiAddr(certiAddr);
					infoCerti.setCertiNbr(certiNbr);
					infoCerti.setModDate(date);
					if (StringUtils.isNumeric(certiType)) {
						infoCerti.setCertiType(Short.parseShort(certiType));
					}
					infoCertiMapper.updateByExampleSelective(infoCerti, infoCertiExample);
				}else {
					//新增
					InfoCerti infoCerti = new InfoCerti();
					infoCerti.setCertiId(commonAtomicService.getSequenceByTable("INFO_CERTI"));
					infoCerti.setCustId(custId);
					infoCerti.setCertiAddr(certiAddr);
					infoCerti.setCertiNbr(certiNbr);
					infoCerti.setModDate(date);
					infoCerti.setCrtDate(date);
					infoCerti.setStatusCd("1000");
					infoCerti.setAuthFlag("1");
					infoCerti.setAuthDate(date);
					if (StringUtils.isNumeric(certiType)) {
						infoCerti.setCertiType(Short.parseShort(certiType));
					}
					infoCertiMapper.insertSelective(infoCerti);
				}
			}
		} catch (Exception e) {
			LOGGER.error("调用会员资料修改接口异常", e);
			throw new SmiBusinessCodeException("调用会员资料修改接口异常！");
		}finally{
			/**
			 * 记录日志
			 */
			Date end = new Date();
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("REQUEST", paramMap.toString());
			logMap.put("REQ_DATE", date);
			logMap.put("RESPONSE", baseValueObject.toString());
			logMap.put("RES_DATE", end);
			logMap.put("REQ_SYSTEM", extSystem == null ? "" : extSystem);
			logMap.put("RES_SYSTEM", "101");
			logMap.put("INTERFACE_CODE", "CUST_UPDATE");
			memberCenterLog.addInterfaceLog(logMap);
		}
		
		baseValueObject.setCode(CodeEnum.SUCCESS);
		return baseValueObject;
	}

	@Override
	public DataVo<InfoCustVo> showCustInfoByCustId(String custId) {
		DataVo<InfoCustVo> dataVo = new DataVo<InfoCustVo>();
		InfoCustVo infoCustVo = new InfoCustVo();
		try {
			InfoCust infoCust = infoCustMapper.selectByPrimaryKey(custId);
			if (infoCust != null) {
				BeanUtils.copyProperties(infoCustVo, infoCust);
			}else {
				dataVo.setCode(CodeEnum.REQUEST_ERROR);
				dataVo.setMsg("不存在该custId的会员信息！");
				return dataVo;
			}
			//获取会员证件信息
			InfoCertiExample infoCertiExample = new InfoCertiExample();
			com.smi.mc.model.cust.InfoCertiExample.Criteria certiCriteria = infoCertiExample.createCriteria();
			certiCriteria.andCustIdEqualTo(custId);
			certiCriteria.andStatusCdEqualTo("1000");
			List<InfoCerti> infoCertis = infoCertiMapper.selectByExample(infoCertiExample);
			if (infoCertis != null && infoCertis.size() > 0) {
				InfoCerti infoCerti = infoCertis.get(0);
				BeanUtils.copyProperty(infoCustVo, "certiType", infoCerti.getCertiType());
				BeanUtils.copyProperty(infoCustVo, "certiNbr", infoCerti.getCertiNbr());
				BeanUtils.copyProperty(infoCustVo, "certiAddr", infoCerti.getCertiAddr());
			}
		} catch (Exception e) {
			LOGGER.error("调用会员资料查询接口异常", e);
			throw new SmiBusinessCodeException("调用会员资料查询接口异常！");
		}
		dataVo.setData(infoCustVo);
		dataVo.setCode(CodeEnum.SUCCESS);
		return dataVo;
	}

	@Override
	public BaseValueObject custAdd(Map<String, Object> paramMap) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			Map<String, Object> mapResp = this.custRegService.custReg(paramMap);
			if (((String)mapResp.get("RESULT")).equals(Constants.STATE_CODE_SUCC)) {
				baseValueObject.setCode(CodeEnum.SUCCESS);
			}else {
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg((String)mapResp.get("MSG"));
			}
		} catch (BusiServiceException e) {
			LOGGER.error("调用会员资料新增原子服务异常", e);
			throw new SmiBusinessCodeException("调用会员资料新增原子服务异常！");
		}
		return baseValueObject;
	}


	@Override
	public DataVo<InfoLoginUserVo> findInfoLoginUserByMobile(String mobile) {
		DataVo<InfoLoginUserVo> baseValueObject = new DataVo<InfoLoginUserVo>();
		InfoLoginUserExample example = new InfoLoginUserExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andLoginUserEqualTo(mobile);
		createCriteria.andStatusCdEqualTo("1000");
		List<InfoLoginUser> infoLoginUsers = infoLoginUserMapper.selectByExample(example);
		if (infoLoginUsers.size() > 0) {
			baseValueObject.setCode(CodeEnum.SUCCESS);
			baseValueObject.setMsg("该手机号码已经注册过星美会员");
			InfoLoginUser infoLoginUser = infoLoginUsers.get(0);
			InfoLoginUserVo infoLoginUserVo = new InfoLoginUserVo();
			try {
				BeanUtils.copyProperties(infoLoginUserVo, infoLoginUser);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			baseValueObject.setData(infoLoginUserVo);
		}else {
			baseValueObject.setCode(CodeEnum.SERVER_INNER_ERROR);
			baseValueObject.setMsg("该手机号码未注册过星美会员");
		}
		return baseValueObject;
	}


	@Override
	public BaseValueObject getVerifyCode(String mobile,String verifyCode,String codeType) {
		BaseValueObject baseValueObject = new BaseValueObject();
		if (codeType.equals("1")) {
			//发送登录验证码
			String smsModel = memberCenterConfiguration.getSmsLoginModel();
			smsService.sendSMS(mobile, smsModel.replace("#smsCode#", verifyCode));
			redisUtil.set(Constants.SMS_LOGIN_VERIFYCODE+ mobile, verifyCode, 600L);
		}else if (codeType.equals("2")) {
			//发送注册验证码
			String smsModel = memberCenterConfiguration.getSmsRegisterModel();
			smsService.sendSMS(mobile, smsModel.replace("#smsCode#", verifyCode));
			redisUtil.set(Constants.SMS_REGISTER_VERIFYCODE+ mobile, verifyCode, 600L);
		}
		baseValueObject.setCode(CodeEnum.SUCCESS);
		return baseValueObject;
	}

	
}
