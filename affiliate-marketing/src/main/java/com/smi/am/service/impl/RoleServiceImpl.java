package com.smi.am.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.dao.AmRoleMapper;
import com.smi.am.dao.SystemLogMapper;
import com.smi.am.dao.model.AmRole;
import com.smi.am.dao.model.SystemLog;
import com.smi.am.service.IRoleService;
import com.smi.am.service.vo.SystemLogVo;
import com.smi.am.utils.DateUtils;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.ValidatorUtil;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 角色管理服务类
 * 
 * @author smi
 *
 */
@Service
public class RoleServiceImpl implements IRoleService {

	@Autowired
	private AmRoleMapper amRoleMapper;

	@Autowired
	private SystemLogMapper systemLogMapper;

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	/**
	 * 根据角色id查询角色类型
	 */
	@Override
	public AmRole selectByRoleId(Integer rRoleid) {
		AmRole role = this.amRoleMapper.selectByPrimaryKey(rRoleid);
		return role;
	}

	/**
	 * 查询系统日志
	 */
	@Override
	public SmiResult<List<SystemLogVo>> findSystemLog(Map<String, Object> param) {
		List<SystemLogVo> listVo = new ArrayList<SystemLogVo>();
		SmiResult<List<SystemLogVo>> smiresult = new SmiResult<List<SystemLogVo>>();
		Map<String, Object> reqParam = new HashMap<String, Object>();
		try {
			reqParam.put("slUsername", param.get("userName"));
			if (!ValidatorUtil.isEmpty((String)param.get("roleType"))) {
				reqParam.put("slRoleType", Integer.valueOf((String) param.get("roleType")));
			}
			if (!ValidatorUtil.isEmpty((String)param.get("queryStartTime"))) {
				reqParam.put("slStartDate", DateUtils.parse((String) param.get("queryStartTime")));
			}
			if (!ValidatorUtil.isEmpty((String)param.get("queryEndTime"))) {
				reqParam.put("slEndDate", DateUtils.parse((String) param.get("queryEndTime")));
			}
			// 开始分页
			PageHelper.startPage((Integer) param.get("pageNum"), (Integer) param.get("pageSize"));

			List<SystemLog> sysLog = this.systemLogMapper.selectSystemLog(reqParam);
			PageInfo<SystemLog> pageInfo = new PageInfo<SystemLog>(sysLog);
			if (sysLog.size() > 0) {
				for (SystemLog log : sysLog) {
					SystemLogVo vo = new SystemLogVo();
					BeanUtils.copyProperties(vo, log);
					listVo.add(vo);
				}
				smiresult.setTotal(pageInfo.getTotal());
				smiresult.setPages(pageInfo.getPages());
				smiresult.setPageSize(pageInfo.getPageSize());
				smiresult.setData(listVo);
				smiresult.setCode(CodeEnum.SUCCESS);
				smiresult.setMsg("查询成功");
			} else {
				smiresult.setCode(CodeEnum.REQUEST_ERROR);
				smiresult.setMsg("无数据");
			}
		} catch (Exception e) {
			this.LOGGER.error("查询系统日志异常", e);
			throw new SmiBusinessException("查询系统日志异常");
		}
		return smiresult;
	}

}
