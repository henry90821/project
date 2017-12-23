package com.smi.am.dao;

import com.smi.am.dao.model.AmUser;
import com.smi.am.dao.model.AmUserExample;
import com.smi.am.dao.model.AmUserRes;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AmUserMapper {
    int countByExample(AmUserExample example);

    int deleteByExample(AmUserExample example);

    int deleteByPrimaryKey(Integer uId);

    int insert(AmUser record);

    int insertSelective(AmUser record);

    List<AmUser> selectByExample(AmUserExample example);

    AmUser selectByPrimaryKey(Integer uId);

    int updateByExampleSelective(@Param("record") AmUser record, @Param("example") AmUserExample example);

    int updateByExample(@Param("record") AmUser record, @Param("example") AmUserExample example);

    int updateByPrimaryKeySelective(AmUser record);

	int updateByPrimaryKey(AmUser record);
	
	/**
	 * 查询账户是否存在
	 * @param userName
	 * @return
	 */
	int selectIsExistUsername(AmUser user);
	/**
	 * 逻辑删除角色
	 * @param param
	 * @return
	 */
	int updateDeleteStateByUsername(Map<String,Object> param);

	/**
	 * 新增管理员
	 * 
	 * @param amUser
	 * @return
	 */
	int insertManger(AmUser amUser);

	/**
	 * 根据参数查询角色列表
	 * 
	 * @param AmUser
	 * @return
	 */
	List<AmUserRes> selectUserByParam(AmUser AmUser);

	/**
	 * 根据账号找密码
	 * 
	 * @param username
	 * @return
	 */
	String selectPwdByUsername(String username);

	/**
	 * 根据账号更新密码
	 * 
	 * @param param
	 * @return
	 */
	int updatePwdByUsername(Map<String, Object> param);
}