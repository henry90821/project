package com.smi.am.aspect;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.dao.model.AmRole;
import com.smi.am.dao.model.SystemLog;
import com.smi.am.service.ICommonService;
import com.smi.am.service.IRoleService;
import com.smi.am.service.ISystemLogService;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.DateUtils;
import com.smi.am.utils.SessionManager;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 系统日志切点类
 * 
 * @author smi
 *
 */
@Aspect
@Component
public class SystemLogAspect {

	private final Logger logger = LoggerUtils.getLogger(this.getClass());
	@Autowired
	private ISystemLogService iSystemLogService;
	@Autowired
	private IRoleService iRoleService;
	@Autowired
	private ICommonService commService;

	// Controller层切点
	@Pointcut("execution (* com.smi.am.controller..*.*(..))")
	public void controllerAspect() {
	}

	/**
	 * 前置通知 用于拦截Controller层记录用户的操作
	 * 
	 * @param joinPoint
	 *            切点
	 */
	@Before("controllerAspect()&&@annotation(systemControllerLog)")
	public void doBefore(JoinPoint joinPoint, SystemControllerLog systemControllerLog) {
		logger.info("==========执行controller前置通知===============");
	}

	/**
	 * 配置controller环绕通知,使用在方法aspect()上注册的切入点
	 * 
	 * @param joinPoint
	 */
	// TODO 改方法会导致Controller层的返回值无法返回到页面
	// @Around("controllerAspect()&&@annotation(systemControllerLog)")
	public void around(JoinPoint joinPoint, SystemControllerLog systemControllerLog) {
		logger.info("==========开始执行controller环绕通知===============");
		long start = System.currentTimeMillis();
		try {
			((ProceedingJoinPoint) joinPoint).proceed();
			long end = System.currentTimeMillis();
			logger.info("==========结束执行controller环绕通知===============执行时间：" + (end - start));
		} catch (Throwable e) {
			long end = System.currentTimeMillis();
			logger.error(
					"around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());
		}
	}

	/**
	 * 后置通知 用于拦截Controller层记录用户的操作
	 * 
	 * @param joinPoint
	 *            切点
	 */
	@After("controllerAspect()&&@annotation(systemControllerLog)")
	public void after(JoinPoint joinPoint, SystemControllerLog systemControllerLog) {
		logger.info("==========开始执行controller后置通知===============");
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			UserVo uservo = (UserVo) SessionManager.getUserInfo(request);
			int areaId = commService.getAreaIdByDeptId(uservo.getuDepartid());
			
			String targetName = joinPoint.getTarget().getClass().getName();
			String methodName = joinPoint.getSignature().getName();
			Object[] arguments = joinPoint.getArgs();
			Class<?> targetClass = Class.forName(targetName);
			Method[] methods = targetClass.getMethods();
			String logTitle = null;
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					@SuppressWarnings("rawtypes")
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						// logTitle =
						// method.getAnnotation(SystemControllerLog.class).logTitle();
						logTitle = systemControllerLog.logTitle();
						break;
					}
				}
			}
			SystemLog log = new SystemLog();
			logger.info("======根据角色id查询角色类型表的角色名称==" + uservo.getuRoletype());
			AmRole aRole = this.iRoleService.selectByRoleId(uservo.getuRoletype());
			log.setSlLoginIp(SessionManager.getRemoteAddr(request));
			log.setSlLogTitle(logTitle);
			log.setSlOperateDate(new Date());
			String logDetail=assembleLogDetail(uservo.getuName(),logTitle);
			log.setSlLogDetail(logDetail);
			log.setSlUsername(uservo.getuUsername());
			log.setSlName(uservo.getuName());
			log.setSlRemark(null);
			log.setSlRoleName(aRole.getrRolename());
			log.setSlRoleType(uservo.getuRoletype());
			log.setAreaId(areaId);
			this.iSystemLogService.addSystemLog(log);
			logger.info("=========系统日志入库结束========");
		} catch (Exception e) {
			logger.error("后置通知切点异常"+e.getMessage());
		}
	}
	
	/**
	 * 组装日志详情
	 * @param name
	 * @param operateStr
	 * @return
	 */
	private static String assembleLogDetail(String name,String operateStr){
		StringBuffer sb=new StringBuffer();
		sb.append(name+"于");
		sb.append(DateUtils.format(DateUtils.now()));
		sb.append("执行"+operateStr+"操作");
		return sb.toString();
	}
}
