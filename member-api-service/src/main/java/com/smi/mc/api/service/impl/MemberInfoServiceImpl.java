package com.smi.mc.api.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.service.IMemberInfoService;
import com.smi.mc.api.utils.RedisUtil;
import com.smi.mc.api.valueobject.InfoCustVo;
import com.smi.mc.api.valueobject.InfoLoginUserVo;
import com.smi.mc.api.valueobject.MemberInfoVo;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.BeanKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class MemberInfoServiceImpl implements IMemberInfoService{
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private MemberCenterConfiguration mcConfig;
	
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public DataVo<MemberInfoVo> login(String channelCode, String accountNo, String pwd, String verifyCode, String loginType,Long tokenTimeOut) {
		DataVo<MemberInfoVo> dataVo = new DataVo<MemberInfoVo>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("channelCode", channelCode);
			paramMap.put("account", accountNo);
			paramMap.put("password", pwd);
			paramMap.put("verifyCode", verifyCode);
			paramMap.put("loginType", loginType);
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCheckLoginUrl(), paramMap);
			DataVo<InfoCustVo> infoDataVo = (DataVo<InfoCustVo>)JsonKit.parseObject(jsonstr, DataVo.class);
			if (infoDataVo.getCode() != 1) {
				dataVo.setCode(CodeEnum.getCodeEnum(infoDataVo.getCode()));
				dataVo.setMsg(infoDataVo.getMsg());
				return dataVo;
			}
			//登录成功,会员信息放入redis缓存
			if (infoDataVo.getData() != null ) {
				//生成令牌token
				String uuidToken =  UUID.randomUUID().toString();
				String token = Base64Utils.encodeToString(uuidToken.getBytes());
				Map<String, Object> infoCustMap = (Map<String, Object>)infoDataVo.getData();
				InfoCustVo infoCustVo = BeanKit.mapToBean(infoCustMap, InfoCustVo.class);
				// 会员中心返回实体转成MemberInfoVo
				MemberInfoVo memberInfoVo = redisUtil.assembleEntity(infoCustVo);
				memberInfoVo.setSmiToken(token);
				dataVo.setData(memberInfoVo);
				redisUtil.set(mcConfig.getTokenPrefix() + uuidToken, infoCustVo.getCustId(), tokenTimeOut);
			}
			dataVo.setCode(CodeEnum.SUCCESS);
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的老登录接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的老登录接口异常");
		}
		return dataVo;
	}

	
	@Override
	public BaseValueObject logout(String token) {
		BaseValueObject baseValueObject = new BaseValueObject();
		byte[] decodeToken = Base64Utils.decodeFromString(token);
		String tokenStr = StrKit.str(decodeToken, "UTF8");
		redisUtil.remove(mcConfig.getTokenPrefix() + tokenStr);
		baseValueObject.setCode(CodeEnum.SUCCESS);
		return baseValueObject;
	}

	@Override
	public DataVo<MemberInfoVo> custQuery(String token) {
		DataVo<MemberInfoVo> dataVo = new DataVo<MemberInfoVo>();
		try {
			String custId = redisUtil.getInfoCustIdByToken(token);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("custId", custId);
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCustQueryUrl(), paramMap);
			DataVo<InfoCustVo> infoDataVo = (DataVo<InfoCustVo>)JsonKit.parseObject(jsonstr, DataVo.class);
			if (infoDataVo.getCode() != 1) {
				dataVo.setCode(CodeEnum.getCodeEnum(infoDataVo.getCode()));
				dataVo.setMsg(infoDataVo.getMsg());
			}else {
				Map<String, Object> infoCustMap = (Map<String, Object>)infoDataVo.getData();
				InfoCustVo infoCustVo = BeanKit.mapToBean(infoCustMap, InfoCustVo.class);
				// 会员中心返回实体转成MemberInfoVo
				MemberInfoVo memberInfoVo = redisUtil.assembleEntity(infoCustVo);
				memberInfoVo.setSmiToken(token);
				dataVo.setData(memberInfoVo);
				dataVo.setCode(CodeEnum.SUCCESS);
			}
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的新增会员接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的新增会员接口异常");
		}
		return dataVo;
	}


	@Override
	public BaseValueObject custAdd(Map<String, Object> paramMap) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCustAddUrl(), paramMap);
			baseValueObject = JsonKit.parseObject(jsonstr, BaseValueObject.class);
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的新增会员接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的新增会员接口异常");
		}
		return baseValueObject;
	}


	@Override
	public BaseValueObject custUpdate(Map<String, Object> paramMap) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCustUpdateUrl(), paramMap);
			baseValueObject = JsonKit.parseObject(jsonstr, BaseValueObject.class);
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的修改会员资料接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的修改会员资料接口异常");
		}
		return baseValueObject;
	}


	@Override
	public BaseValueObject isSmiMember(String loginUser) {
		BaseValueObject baseValueObject = new BaseValueObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("mobile", loginUser);
		try {
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getCustQuerybynumUrl(), paramMap);
			baseValueObject = JsonKit.parseObject(jsonstr, BaseValueObject.class);
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的查询账户信息接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的查询账户信息接口异常");
		}
		return baseValueObject;
	}


	@Override
	public BaseValueObject getVerifyCode(String mobile,String codeType) {
		BaseValueObject baseValueObject = new BaseValueObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("mobile", mobile);
		paramMap.put("codeType", codeType);
		try {
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + mcConfig.getSmsVerifycodeUrl(), paramMap);
			baseValueObject = JsonKit.parseObject(jsonstr, BaseValueObject.class);
		} catch (IOException e) {
			LOGGER.error("调用新会员中心系统的发送短信验证码接口异常",e);
			throw new SmiBusinessException("调用新会员中心系统的发送短信验证码接口异常");
		}
		return baseValueObject;
	}

}
