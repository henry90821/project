package com.smi.am.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.IRoleService;
import com.smi.am.service.IUserservice;
import com.smi.am.service.vo.SystemLogVo;
import com.smi.am.service.vo.UserResVo;
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
 * 角色管理控制器
 */
@RestController
@RequestMapping(value =SmiConstants.ADMIN_PATH + "/roleManger")
@Api(value = "角色管理")
public class RoleMangerController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private IUserservice iUserservice;
	
	@Autowired
	private IRoleService iRoleService;

	/**
	 * 角色列表
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "角色列表")
	@RequestMapping(value = "/rolelist", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "获取角色列表")
	@LoginAuth
	public SmiResult<List<UserResVo>> roleList(
			@ApiParam(name = "roleType", value = "角色类型(1-超级管理员 ;2-运营总监;3-运营专员;4-客服)", required = false) @RequestParam(value = "roleType", required = false) String roleType,
			@ApiParam(name = "roleName", value = "角色姓名", required = false) @RequestParam(value = "roleName", required = false) String roleName,
			@ApiParam(name = "jobNum", value = "工号", required = false) @RequestParam(value = "jobNum", required = false) String jobNum,
			@ApiParam(name = "department", value = "归属部门", required = false) @RequestParam(value = "department", required = false) String department,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,HttpServletRequest request) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roleType", roleType);
		paramMap.put("roleName", roleName);
		paramMap.put("jobNum", jobNum);
		paramMap.put("department", department);
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		this.LOGGER.info("角色列表入参" + paramMap.toString());
		SmiResult<List<UserResVo>> smiresult = this.iUserservice.findUserByParam(paramMap);
		return smiresult;

	}

	/**
	 * 新增管理员
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "新增管理员")
	@RequestMapping(value = "/addManger", method = { RequestMethod.POST })
	@SystemControllerLog(logTitle = "新增管理员")
	@LoginAuth
	public BaseValueObject addManger(
			@Valid @Pattern(regexp = "^[0-9]*$" , message="长度不规则") @ApiParam(name = "uRoletype", value = "角色类型(1-运营总监 ;2-运营专员;3-客服)", required = true) @RequestParam(value = "uRoletype", required = true) String uRoletype,
			@ApiParam(name = "uName", value = "姓名", required = true) @RequestParam(value = "uName", required = true) String uName,
			@ApiParam(name = "uJobnum", value = "工号", required = true) @RequestParam(value = "uJobnum", required = true) String uJobnum,
			@ApiParam(name = "uDepartid", value = "归属部门id(id-1,name-深圳运营中心;id-2,name-北京运营中心;id-3,name-上海运营中心;id-4,name-成都运营中心;id-5,name-客服中心;id-6,name-集团产品运营中心;id-7,name-星美生活;id-8,name-星美票务;id-9,name-会员发展部)", required = true) @RequestParam(value = "uDepartid", required = true) String uDepartid,
			@ApiParam(name = "uUsername", value = "用户账号", required = true) @RequestParam(value = "uUsername", required = true) String uUsername,
			@ApiParam(name = "uPassword", value = "密码", required = true) @RequestParam(value = "uPassword", required = true) String uPassword,HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uRoletype", Integer.valueOf(uRoletype));
		paramMap.put("uName", uName);
		paramMap.put("uJobnum", uJobnum);
		paramMap.put("uDepartid", Integer.valueOf(uDepartid));
		paramMap.put("uUsername", uUsername);
		paramMap.put("uPassword", uPassword);
		this.LOGGER.info("新增管理员入参:" + paramMap.toString());
		BaseValueObject baseValueObject=this.iUserservice.addUserManger(paramMap,requset);
		return baseValueObject;
	}

	/**
	 * 修改密码
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "修改密码")
	@RequestMapping(value = "/editPwd", method = { RequestMethod.POST })
	@LoginAuth
	@SystemControllerLog(logTitle = "新增管理员")
	public BaseValueObject editPwd(
			@ApiParam(name = "userName", value = "用户账号", required = true) @RequestParam(value = "userName", required = true) String userName,
			@ApiParam(name = "oldPassWord", value = "旧密码", required = true) @RequestParam(value = "oldPassWord", required = true) String oldPassWord,
			@ApiParam(name = "passWord", value = "新密码", required = true) @RequestParam(value = "passWord", required = true) String passWord,HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", userName);
		paramMap.put("oldPassWord", oldPassWord);
		paramMap.put("passWord", passWord);
		this.LOGGER.info("新增管理员入参:" + paramMap.toString());
		BaseValueObject base=this.iUserservice.editPwd(paramMap,requset);
		return base;
	}

	/**
	 * 系统日志查询
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "系统日志查询")
	@RequestMapping(value = "/qrySystemlog", method = { RequestMethod.GET })
	@SystemControllerLog(logTitle = "系统日志查询")
	@LoginAuth
	public SmiResult<List<SystemLogVo>> qrySystemLog(
			@ApiParam(name = "userName", value = "用户账号", required = false) @RequestParam(value = "userName", required = false) String userName,
			@ApiParam(name = "roleType", value = "角色类型", required = false) @RequestParam(value = "roleType", required = false) String roleType,
			@ApiParam(name = "queryStartTime", value = "查询的开始时间", required = false) @RequestParam(value = "queryStartTime", required = false) String queryStartTime,
			@ApiParam(name = "queryEndTime", value = "查询的结束时间", required = false) @RequestParam(value = "queryEndTime", required = false) String queryEndTime,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,HttpServletRequest request) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", userName);
		paramMap.put("roleType", roleType);
		paramMap.put("queryStartTime", queryStartTime);
		paramMap.put("queryEndTime", queryEndTime);
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		this.LOGGER.info("系统日志查询入参:" + paramMap.toString());
		SmiResult<List<SystemLogVo>> smiresult=this.iRoleService.findSystemLog(paramMap);
		return smiresult;

	}
	
	/**
	 * 删除角色
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "删除角色")
	@RequestMapping(value = "/removeManger", method = { RequestMethod.POST})
	@LoginAuth
	@SystemControllerLog(logTitle = "删除角色")
	public BaseValueObject removeRole(
			@ApiParam(name = "userName", value = "用户账号", required = true) @RequestParam(value = "userName", required = true) String userName,
			HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", userName);
		this.LOGGER.info("新增管理员入参:" + paramMap.toString());
		BaseValueObject base=this.iUserservice.removeRole(paramMap,requset);
		return base;
	}
	
	
	/**
	 * 重置密码
	 * 
	 * @param userVo
	 * @return
	 */
	@ApiOperation(value = "重置密码")
	@RequestMapping(value = "/resetPwd", method = { RequestMethod.POST})
	@LoginAuth
	@SystemControllerLog(logTitle = "重置密码")
	public BaseValueObject resetPwd(
			@ApiParam(name = "userName", value = "用户账号", required = true) @RequestParam(value = "userName", required = true) String userName,
			@ApiParam(name = "passWord", value = "管理员密码", required = true) @RequestParam(value = "passWord", required = true) String passWord,
			HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uUsername", userName);
		paramMap.put("passWord", passWord);
		this.LOGGER.info("重置密码入参:" + paramMap.toString());
		BaseValueObject base=this.iUserservice.resetRolePwd(paramMap,requset);
		return base;
	}
	
}
