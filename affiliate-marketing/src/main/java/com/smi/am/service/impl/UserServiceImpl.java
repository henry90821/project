package com.smi.am.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmDepartmentMapper;
import com.smi.am.dao.AmRoleMapper;
import com.smi.am.dao.AmUserMapper;
import com.smi.am.dao.model.AmDepartment;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.dao.model.AmRole;
import com.smi.am.dao.model.AmUser;
import com.smi.am.dao.model.AmUserExample;
import com.smi.am.dao.model.AmUserExample.Criteria;
import com.smi.am.dao.model.AmUserRes;
import com.smi.am.service.IUserservice;
import com.smi.am.service.vo.UserResVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.BeanToMapUtil;
import com.smi.am.utils.DateUtils;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.ValidatorUtil;
import com.smi.tools.kits.MD5Kit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 角色用户服务基类
 * @author smi
 *
 */
@Service("userService")
public class UserServiceImpl implements IUserservice {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private AmUserMapper amUserMapper;
	
	@Autowired
	private AmDepartmentMapper amDepartmentMapper;
	
	@Autowired
	private AmRoleMapper amRoleMapper;

	@Override
	public UserVo findUserByUsernameAndPwd(String username, String pwd) {
		UserVo uservo = new UserVo();
		AmUserExample amUserExample = new AmUserExample();
		Criteria createCriteria = amUserExample.createCriteria();
		createCriteria.andUUsernameEqualTo(username);
		createCriteria.andUPasswordEqualTo(pwd);
		createCriteria.andUDeletestateEqualTo(0);
		List<AmUser> selectByExample = amUserMapper.selectByExample(amUserExample);
		if (selectByExample.size() > 0) {
			AmUser amUser = selectByExample.get(0);
			try {
				BeanUtils.copyProperties(uservo, amUser);

				AmDepartment deptVo = amDepartmentMapper.selectByPrimaryKey(uservo.getuDepartid());
				if(deptVo != null){
					uservo.setuDepartName(deptVo.getdDepartmentname());
					uservo.setAreaId(deptVo.getdAreaid());
				}
				
				AmRole role = amRoleMapper.selectByPrimaryKey(uservo.getuRoletype());
				if(role != null)
					uservo.setuRoleName(role.getrRolename());
				
			} catch (IllegalAccessException e) {
				LOGGER.error("service层实体类复制异常", e);
				throw new SmiBusinessException("service层实体类复制异常");
			} catch (InvocationTargetException e) {
				LOGGER.error("service层实体类复制异常", e);
				throw new SmiBusinessException("service层实体类复制异常");
 			}
		}
		return uservo;
	}
	
	@Override
	public int updateLastLoginTimeByUserId(Date lastloginTime, Integer userId,String username) {
		AmUser amUser = new AmUser();
		amUser.setuLastlogindate(lastloginTime);
		amUser.setuId(userId);
		amUser.setuLastmoduser(username);
		amUser.setuLastmoddate(lastloginTime);
		return amUserMapper.updateByPrimaryKeySelective(amUser);
	}

	/**
	 * 查询角色列表
	 */
	@Override
	public SmiResult<List<UserResVo>> findUserByParam(Map<String, Object> param) {
		List<UserResVo> userList = new ArrayList<UserResVo>();
		SmiResult<List<UserResVo>> smiresult = new SmiResult<List<UserResVo>>();
		try {
			AmUser am = new AmUser();
			String roleType = (String) param.get("roleType");
			String department = (String) param.get("department");
			if (!"".equals(roleType) && roleType != null) {
				am.setuRoletype(Integer.valueOf((String) param.get("roleType")));
			}
			if (!"".equals(department) && department != null) {
				am.setuDepartid(Integer.valueOf((String) param.get("department")));
			}
			am.setuName((String) param.get("roleName"));
			am.setuJobnum((String) param.get("jobNum"));

			// 分页查询
			PageHelper.startPage((Integer) param.get("pageNum"), (Integer) param.get("pageSize"));// 执行分页
			List<AmUserRes> auList = this.amUserMapper.selectUserByParam(am);
			PageInfo<AmUserRes> pageInfo = new PageInfo<AmUserRes>(auList);// 取分页值
			if (auList.size() > 0) {
				for (AmUserRes amu : auList) {
					UserResVo uo = new UserResVo();
					uo.setuName(amu.getName());
					uo.setuJobnum(amu.getJobNum());
					uo.setuLastlogindate(amu.getLastLogindate());
					uo.setuRoleName(amu.getRoleName());
					uo.setuDepartName(amu.getDepartmentName());
					uo.setuUsername(amu.getUserName());
					userList.add(uo);
				}
				smiresult.setData(userList);
				smiresult.setPages(pageInfo.getPages());
				smiresult.setTotal(pageInfo.getTotal());
				smiresult.setPageSize(pageInfo.getPageSize());
				smiresult.setCode(CodeEnum.SUCCESS);
				smiresult.setMsg("查询成功");
			} else {
				smiresult.setCode(CodeEnum.PROFILE_MISSING);
				smiresult.setMsg("获取角色列表无数据");
				this.LOGGER.info("获取角色列表为null");
			}
		} catch (Exception e) {
			this.LOGGER.error("获取角色列表异常" + e);
			throw new SmiBusinessException("获取角色列表异常");
		}
		return smiresult;
	}

	/**
	 * 新增管理员
	 */
	@Override
	public BaseValueObject addUserManger(Map<String, Object> paramMap, HttpServletRequest requset) {
		BaseValueObject baseValueObject=new BaseValueObject();
		try {
			String uUsername=(String) paramMap.get("uUsername");
			AmUser am=new AmUser();
			am.setuUsername(uUsername);
			int count=this.amUserMapper.selectIsExistUsername(am);
			if(count>0){
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg("账户已存在");
			}else{
			UserVo vo = (UserVo) SessionManager.getUserInfo(requset);
			AmUser amUser = new AmUser();
			amUser = (AmUser) BeanToMapUtil.convertMap(AmUser.class, paramMap);
			amUser.setuPassword(amUser.getuPassword());
			amUser.setuCreatedate(DateUtils.now());
			amUser.setuLastlogindate(DateUtils.now());
			amUser.setuLastmoddate(DateUtils.now());
			amUser.setuCreateuser(vo.getuName());
			amUser.setuLastmoduser(vo.getuName());
			this.amUserMapper.insertManger(amUser);
			baseValueObject.setCode(CodeEnum.SUCCESS);
			baseValueObject.setMsg("新增成功");
			}
		} catch (Exception e) {
			this.LOGGER.error("新增异常", e);
			throw new SmiBusinessException("新增管理员异常");
		}
		return baseValueObject;
	}

	/**
	 * 修改密码
	 */
	@Override
	public BaseValueObject editPwd(Map<String, Object> param, HttpServletRequest requset) {
		BaseValueObject base = new BaseValueObject();
		UserVo vo = (UserVo) SessionManager.getUserInfo(requset);
		try {
			String uUsername = (String) param.get("userName");
			String oldPassWord = (String) param.get("oldPassWord");
			String newPassWord = (String) param.get("passWord");// 新密码
			// String oldPwdMD5 = MD5Kit.getMD5String(oldPassWord);
			String uPasswod = this.amUserMapper.selectPwdByUsername(uUsername);
			if (oldPassWord.equals(uPasswod)) {
				// String newPwdMD5 = MD5Kit.getMD5String(newPassWord);
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("uUsername", uUsername);
				paramMap.put("uPassword", newPassWord);
				paramMap.put("uLastmoddate", DateUtils.now());
				paramMap.put("uLastmoduser", vo.getuName());
				int num = this.amUserMapper.updatePwdByUsername(paramMap);
				this.LOGGER.info("修改密码成功" + num);
				base.setCode(CodeEnum.SUCCESS);
				base.setMsg("修改密码成功");
			} else {
				base.setMsg("输入的密码错误");
				base.setCode(CodeEnum.REQUEST_ERROR);
			}
		} catch (Exception e) {
			this.LOGGER.error("修改密码异常", e);
			throw new SmiBusinessException("修改密码异常");
		}
		return base;
	}

	/**
	 * 删除角色
	 */
	@Override
	public BaseValueObject removeRole(Map<String, Object> param, HttpServletRequest requset) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			UserVo userVo = (UserVo) SessionManager.getUserInfo(requset);
			param.put("uLastmoddate", DateUtils.now());
			param.put("uLastmoduser", userVo.getuName());
			this.amUserMapper.updateDeleteStateByUsername(param);
			baseValueObject.setCode(CodeEnum.SUCCESS);
			baseValueObject.setMsg("删除成功");
			this.LOGGER.info("删除角色是：" + param.get("userName"));
		} catch (Exception e) {
			this.LOGGER.error("删除角色异常", e);
			throw new SmiBusinessException("删除角色异常");
		}
		return baseValueObject;
	}

	/**
	 * 管理员重置用户密码
	 */
	@Transactional
	@Override
	public BaseValueObject resetRolePwd(Map<String, Object> param, HttpServletRequest requset) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			UserVo userVo = (UserVo) SessionManager.getUserInfo(requset);
			String username = userVo.getuUsername();
			String mangerPWD = this.amUserMapper.selectPwdByUsername(username);//根据管理员账号查询密码
			String paramPWD=(String) param.get("passWord");
			if(paramPWD!=null){
			if (paramPWD.equals(mangerPWD)) {
				param.put("uLastmoddate", DateUtils.now());
				param.put("uLastmoduser", userVo.getuName());
				param.put("uPassword", MD5Kit.getMD5String(SmiConstants.DEFAULT_PWD));
				this.amUserMapper.updatePwdByUsername(param);
				baseValueObject.setCode(CodeEnum.SUCCESS);
				baseValueObject.setMsg("重置密码成功");
			}else{
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg("管理员密码输入不匹配");
			}
			}
		} catch (Exception e) {
			this.LOGGER.error("管理员重置用户密码异常", e);
			throw new SmiBusinessException("管理员重置用户密码异常");
		}

		return baseValueObject;
	}

}
