package com.smi.am.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.smi.am.service.vo.UserResVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface IUserservice {

	/**
	 * 登录验证
	 * 
	 * @param username
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	UserVo findUserByUsernameAndPwd(String username, String pwd);
	
	/**
	 * 更新用户最后登录时间
	 * @param lastloginTime
	 * @param userId
	 * @return
	 */
	int updateLastLoginTimeByUserId(Date lastloginTime,Integer userId,String username);

	/**
	 * 根据参数查询角色列表
	 * 
	 * @param AmUser
	 * @return
	 */
	SmiResult<List<UserResVo>> findUserByParam(Map<String, Object> param);

	/**
	 * 新增管理员
	 * 
	 * @param paramMap
	 * @return
	 */
	BaseValueObject addUserManger(Map<String, Object> paramMap, HttpServletRequest requset);

	/**修改密码
	 * @param param
	 * @return
	 */
	BaseValueObject editPwd(Map<String,Object> param,HttpServletRequest requset);
	
	/**
	 * 删除角色
	 * @param param
	 * @param requset
	 * @return
	 */
	BaseValueObject removeRole(Map<String,Object> param,HttpServletRequest requset);
	
	/**
	 * 重置密码
	 * @param param
	 * @param requset
	 * @return
	 */
	BaseValueObject resetRolePwd(Map<String,Object> param,HttpServletRequest requset);
	
}
