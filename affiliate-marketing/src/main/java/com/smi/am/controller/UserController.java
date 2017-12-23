package com.smi.am.controller;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.IUserservice;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.RedisUtil;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 登录入口
 */
@RestController
@RequestMapping(value = "/user")
@Api(value = "用户登录接口")
public class UserController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private IUserservice userService;
	
	@Autowired
	private RedisUtil redisUtil;

	@ApiOperation(value = "用户登录")
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "用户登录")
	@Cacheable
	public SmiResult<UserVo> login(HttpServletRequest request,
			@ApiParam(name = "username", value = "用户名", required = true) @RequestParam(value = "username", required = true) String username,
			@ApiParam(name = "password", value = "密码", required = true) @RequestParam(value = "password", required = true) String password,
			@ApiParam(name = "verifycode", value = "验证码", required = true) @RequestParam(value = "verifycode", required = true) String verifycode){
		LOGGER.info("UserController sessionId是：" + request.getSession().getId());
		SmiResult<UserVo> smiResult = new SmiResult<UserVo>();
		Date date = new Date();
		String code = (String)SessionManager.getAttribute(request, SmiConstants.SESSION_KEY_SECURITY_CODE);
		LOGGER.info("从当前session中获取到的的验证码值是: {}", code);
		if(!verifycode.equals(code)){
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("登录失败，验证码不正确");
			return smiResult;
		}

		UserVo userVo = userService.findUserByUsernameAndPwd(username, password);
		if (userVo.getuId()!= null ){
			// 更新用户最后登录时间
			userService.updateLastLoginTimeByUserId(date, userVo.getuId(), username);
			userVo.setuLastlogindate(date);

			//登录成功,保存用户对象到session
			SessionManager.setAttribute(request, SmiConstants.USERNAME, userVo);
			SessionManager.setTimeOut(request, 60*30);//30分钟失效

			//根据username去redis缓存获取sessionid信息
			final String userKey = SmiConstants.USERNAME_REDIS_KEY + username;
			String sessionId = (String)redisUtil.get(userKey);
			if(StringUtils.isNotEmpty(sessionId)) {
				final String oldSid=SmiConstants.SESSION_REDIS_KEY + sessionId;
				LOGGER.info("旧的sessionid oldSid:"+oldSid);
				boolean existsSession=redisUtil.exists(oldSid);
				if (existsSession) {
					redisUtil.remove(userKey);
					redisUtil.remove(oldSid);
				}
			}
			//设置key:username，value:sessionId做redis缓存,
			redisUtil.set(userKey, request.getSession().getId());
			
			smiResult.setCode(CodeEnum.SUCCESS);
			smiResult.setMsg("登录成功");
			smiResult.setData(userVo);
		}else{
			//登录失败
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("登录失败，账户或密码不正确");
		}
		return smiResult;
	}

	@ApiOperation(value = "用户登出")
	@RequestMapping(value = "/logout", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "用户登出")
	public BaseValueObject logout(HttpServletRequest request,
			@ApiParam(name = "username", value = "用户名", required = true) @RequestParam(value = "username", required = true) String username) throws Exception {
		SessionManager.logout(request);
		// 移除redis中的缓存
		redisUtil.remove(SmiConstants.USERNAME_REDIS_KEY+username);
		redisUtil.remove(SmiConstants.SESSION_REDIS_KEY + request.getSession().getId());
		
		BaseValueObject baseValueObject = new BaseValueObject();
		baseValueObject.setCode(CodeEnum.SUCCESS);
		baseValueObject.setMsg("登出成功");
		return baseValueObject;
	}

	@ApiOperation(value = "检查当前登录用户是否存在多端登录的情况")
	@RequestMapping(value = "/loginStatus/{userName}", method = { RequestMethod.GET })
	public BaseValueObject userLoginStatus(HttpServletRequest request,
			@ApiParam(value = "userName") @PathVariable(value = "userName") String userName){
		LOGGER.info("检查当前登录用户是否存在多端登录的情况,sessionId:"+request.getSession().getId());
		BaseValueObject result = new BaseValueObject();
		final String userKey = SmiConstants.USERNAME_REDIS_KEY + userName;
		//缓存中获取sessionId信息
		String sessionId = (String)redisUtil.get(userKey);
		if(StringUtils.isNoneEmpty(sessionId) ){
			if(!sessionId.equals(request.getSession().getId())){
				LOGGER.info("检查当前登录用户是否存在多端登录的情况 ,旧的sessionid oldSid:"+sessionId);
				SessionManager.logout(request);
				result.setCode(CodeEnum.NOT_LOGGED_IN);
				result.setMsg("被迫下线，原因：您的账户已在其他地方登陆！");
			}else {
				result.setCode(CodeEnum.SUCCESS);
			}
		}else {
			result.setCode(CodeEnum.SUCCESS);
		}
		return result;
	}


	/**
	 * WebSocket接口,用来检查当前登录用户是否存在多端登录的情况,该接口需要用户登录后方才启用访问
	 *
	 * @param userName
	 *            当前登录用户名
	 * @return

	@MessageMapping(value = "/loginStatus/{userName}")
	@SendTo(value = WebSocketConfig.SOCKET_CLIENT_PREFIX)
	public String userLoginStatus(@DestinationVariable String userName) {
		LOGGER.info("==================当前登录用户是{}==================", userName);
		BaseValueObject result = new BaseValueObject();
//		ServerHttpRequest request=MyWebSocketInterceptor.getHttpRequest();
		// 需要实现判断用户是否已经被提出登录
//		if(ApplicationCache.APPLICATION_MAP.get(userName) != null ){
//			//踢出
//			SessionManager.logout(httpRequest);
//			result.setCode(CodeEnum.NOT_LOGGED_IN);
//			result.setMsg("被迫下线，原因：您的账户已在其他地方登陆！");
//		}else{
//			result.setCode(CodeEnum.SUCCESS);
//		}
		return JsonKit.toJsonString(result);
	}*/

}
