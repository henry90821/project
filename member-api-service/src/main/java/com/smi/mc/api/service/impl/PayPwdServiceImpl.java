package com.smi.mc.api.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.service.IPayPwdService;
import com.smi.mc.api.utils.RedisUtil;
import com.smi.mc.api.valueobject.CustVo;
import com.smi.mc.api.valueobject.enums.PwdTypeEnum;
import com.smi.mc.api.valueobject.enums.RestPwdTypeEnum;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.DataVo;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 支付密码操作基类
 * 
 * @author smi
 *
 */
@Service
public class PayPwdServiceImpl implements IPayPwdService {

	@Autowired
	private MemberCenterConfiguration mc;
	@Autowired
	private RedisUtil redisUtil;

	private static final Logger LOGGER = LoggerUtils.getLogger(PayPwdServiceImpl.class);

	/**
	 * 支付密码的验证
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DataVo<CustVo> verifyPayPwd(Map<String, Object> paramMap) throws SmiBusinessException {
		DataVo<CustVo> result = new DataVo<CustVo>();
		if (!paramMap.isEmpty()) {
			try {
				// 根据token去redis查询custId
				String custId = redisUtil.getInfoCustIdByToken((String) paramMap.get("token"));
				if (paramMap.get("pwdType").equals(PwdTypeEnum.SERVICE_PWD)) {
					paramMap.put("pwdType", PwdTypeEnum.SERVICE_PWD.getCode().toString());
					paramMap.put("custId", custId);
					LOGGER.info("开始请求会员中心支付密码验证服务,请求参数：" + paramMap.toString());
					String jsonStr = HttpKit.post(mc.getCustUrl() + mc.getCustCheckpwdUrl(), paramMap);
					LOGGER.info("请求会员中心支付密码验证服务结束,返回响应：" + jsonStr);
					if (StrKit.isNotBlank(jsonStr)) {
						Map<String, Object> resMap = JsonKit.parseObject(jsonStr);
						Map<String, Object> resp = (Map<String, Object>) resMap.get("RESP");
						String resultCode = (String) resp.get("code");
						String resultMsg = (String) resp.get("msg");
						if ("0".equals(resultCode)) {
							result.setCode(CodeEnum.SUCCESS);
							result.setMsg("成功");
							if (resMap.containsKey("CUST")) {
								Map<String, Object> custMap = (Map<String, Object>) resMap.get("CUST");
								CustVo vo = new CustVo();
								vo.setCustId((String) custMap.get("CUST_ID"));
								vo.setCustName((String) custMap.get("CUST_NAME"));
								result.setData(vo);
							} else {
								result.setData(null);
							}
						} else {
							result.setCode(CodeEnum.REQUEST_ERROR);
							result.setMsg(resultMsg);
						}
					} else {
						result.setCode(CodeEnum.PROFILE_MISSING);
						result.setMsg("请求会员中心支付密码验证服务，返回为null");
					}
				} else {
					result.setCode(CodeEnum.REQUEST_ERROR);
					result.setMsg("请求的验证密码类型非支付密码");
				}
			} catch (Exception e) {
				LOGGER.error("请求会员中心支付验证服务异常", e);
				throw new SmiBusinessException("请求会员中心支付密码验证服务异常");
			}
		}
		return result;
	}

	/**
	 * 判断用户是否设置了支付密码
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BaseValueObject judgeIsSetPayPwd(Map<String, Object> param) throws SmiBusinessException {
		BaseValueObject result = new BaseValueObject();
		try {
			// 根据token去redis查询custId
			String custId = redisUtil.getInfoCustIdByToken((String) param.get("token"));
			param.put("custId", custId);
			LOGGER.info("开始请求会员中心判断用户设置了支付密码服务，请求参数：" + param.toString());
			String jsonStr = HttpKit.post(mc.getCustUrl() + mc.getCustJudgeSetpaypwdUrl(), param);
			LOGGER.info("会员中心判断用户设置了支付密码服务请求结束，返回结果：" + jsonStr);
			if (StrKit.isBlank(jsonStr)) {
				result.setCode(CodeEnum.SERVER_INNER_ERROR);
				result.setMsg("请求会员中心返回为null或者无响应");
				return result;
			}
			Map<String, Object> resMap = JsonKit.parseObject(jsonStr);
			Map<String, Object> respMap = (Map<String, Object>) resMap.get("RESP");
			String resultCode = (String) respMap.get("code");
			String resultMsg = (String) respMap.get("msg");
			if ("1".equals(resultCode)) {
				result.setCode(CodeEnum.SUCCESS);
				result.setMsg(resultMsg);
				return result;
			}
			if ("2".equals(resultCode)) {
				result.setCode(CodeEnum.PROFILE_MISSING);
				result.setMsg(resultMsg);
			} else {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg(resultMsg);
			}
		} catch (Exception e) {
			LOGGER.error("请求会员中心判断用户设置了支付密码服务异常", e);
			throw new SmiBusinessException("请求会员中心判断用户设置了支付密码服务异常");
		}
		return result;
	}

	/**
	 * 设置支付密码
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BaseValueObject settingPayPwd(Map<String, Object> param) throws SmiBusinessException {
		BaseValueObject result = new BaseValueObject();
		try {
			// 根据token去redis查询custId
			String custId = redisUtil.getInfoCustIdByToken((String) param.get("token"));
			param.put("custId", custId);
			if (!PwdTypeEnum.SERVICE_PWD.equals(param.get("pwdType"))) {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg("请求密码不是支付密码");
				return result;
			}
			LOGGER.info("开始请求会员中心设置支付密码服务，请求参数：" + param.toString());
			String jsonStr = HttpKit.post(mc.getCustUrl() + mc.getCustSetpaypwdUrl(), param);
			LOGGER.info("会员中心设置支付密码服务请求结束，返回结果：" + jsonStr);
			if (StrKit.isBlank(jsonStr)) {
				result.setCode(CodeEnum.SERVER_INNER_ERROR);
				result.setMsg("请求会员中心返回为null或者无响应");
				return result;
			}
			Map<String, Object> resMap = JsonKit.parseObject(jsonStr);
			Map<String, Object> respMap = (Map<String, Object>) resMap.get("RESP");
			String resultCode = (String) respMap.get("code");
			String resultMsg = (String) respMap.get("msg");
			if ("0".equals(resultCode)) {
				result.setCode(CodeEnum.SUCCESS);
				result.setMsg("密码设置成功");
				return result;
			}
			if ("1".equals("resultCode")) {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg(resultMsg);
			} else {
				result.setCode(CodeEnum.SERVER_INNER_ERROR);
				result.setMsg(resultMsg);
			}
		} catch (Exception e) {
			LOGGER.error("请求会员中心判断用户设置了支付密码服务异常", e);
			throw new SmiBusinessException("请求会员中心判断用户设置了支付密码服务异常");
		}
		return result;
	}

	/**
	 * 请求会员中心调用修改密码服务
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BaseValueObject updateLoginPwd(Map<String, Object> param) throws SmiBusinessException {
		BaseValueObject result = new BaseValueObject();
		try {
			// 根据token去redis查询custId
			String custId = redisUtil.getInfoCustIdByToken((String) param.get("token"));
			param.put("custId", custId);
			param.put("restType", RestPwdTypeEnum.MODIFY_PWD.getCode());
			LOGGER.info("开始请求会员中心调用修改登录密码服务，请求参数：" + param.toString());
			String url = mc.getCustUrl() + mc.getCustRestLoginpwdUrl();
			String jsonStr = HttpKit.post(url, param);
			LOGGER.info("请求会员中心调用修改登录密码服务服务结束，响应：" + jsonStr);
			if (StrKit.isBlank(jsonStr)) {
				result.setCode(CodeEnum.SERVER_INNER_ERROR);
				result.setMsg("请求会员中心返回为null或者无响应");
				return result;
			}
			Map<String, Object> resMap = JsonKit.parseObject(jsonStr);
			Map<String, Object> respMap = (Map<String, Object>) resMap.get("RESP");
			String resultCode = (String) respMap.get("code");
			String resultMsg = (String) respMap.get("msg");
			if ("0".equals(resultCode)) {
				result.setCode(CodeEnum.SUCCESS);
				result.setMsg("密码更新成功");
			} else {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg(resultMsg);
			}
		} catch (Exception e) {
			LOGGER.error("请求会员中心修改登录密码服务服务异常", e);
			throw new SmiBusinessException("请求会员中心调用修改登录密码服务服务异常");
		}
		return result;
	}

	/**
	 * 请求会员中心的重置登录密码服务
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BaseValueObject restLoginPwd(Map<String, Object> param) throws SmiBusinessException {
		BaseValueObject result = new BaseValueObject();
		try {
			LOGGER.info("开始请求会员中心根据手机号查询custId，请求参数：" + param.toString());
			String jsonRes = HttpKit.post(mc.getCustUrl() + mc.getCustQuerybynumUrl(), param);
			LOGGER.info("请求会员中心根据手机号查询custId服务结束，响应：" + jsonRes);
			JSONObject json=JSONObject.parseObject(jsonRes);
			Map<String ,Object> maps=(Map<String, Object>) json.get("data");
			if(maps==null){
				result.setCode(CodeEnum.PROFILE_MISSING);
				result.setMsg("该用户未注册");
				return result;
			}
	        String custId=(String) maps.get("custId");
	        param.put("custId", custId);
			param.put("restType", RestPwdTypeEnum.RESET_PWD.getCode());
			LOGGER.info("开始请求会员中心调用修改登录密码服务，请求参数：" + param.toString());
			String jsonStr = HttpKit.post(mc.getCustUrl() + mc.getCustRestLoginpwdUrl(), param);
			LOGGER.info("请求会员中心调用重置登录密码服务服务结束，响应：" + jsonStr);
			if (StrKit.isBlank(jsonStr)) {
				result.setCode(CodeEnum.SERVER_INNER_ERROR);
				result.setMsg("请求会员中心返回为null或者无响应");
				return result;
			}
			Map<String, Object> resMap = JsonKit.parseObject(jsonStr);
			Map<String, Object> respMap = (Map<String, Object>) resMap.get("RESP");
			String resultCode = (String) respMap.get("code");
			String resultMsg = (String) respMap.get("msg");
			if ("0".equals(resultCode)) {
				result.setCode(CodeEnum.SUCCESS);
				result.setMsg("登录密码重置成功");
			} else {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg(resultMsg);
			}
		} catch (IOException e) {
			LOGGER.error("请求会员中心重置登录密码服务服务异常", e);
			throw new SmiBusinessException("请求会员中心调用重置登录密码服务服务异常");
		}

		return result;
	}

}
